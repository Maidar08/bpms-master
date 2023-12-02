package mn.erin.domain.bpm.util.process;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.core.env.Environment;

import mn.erin.domain.aim.service.AimServiceRegistry;
import mn.erin.domain.base.usecase.ErinException;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.model.product.Product;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.repository.ProductRepository;
import mn.erin.domain.bpm.service.DirectOnlineCoreBankingService;
import mn.erin.domain.bpm.service.NewCoreBankingService;
import mn.erin.domain.bpm.usecase.direct_online.GetLoanInfo;
import mn.erin.domain.bpm.usecase.direct_online.GetLoanInfoOutput;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParameters;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParametersInput;
import mn.erin.domain.bpm.usecase.process.UpdateRequestParameters;
import mn.erin.domain.bpm.usecase.process.UpdateRequestParametersInput;
import mn.erin.domain.bpm.usecase.process.UpdateRequestState;
import mn.erin.domain.bpm.usecase.process.UpdateRequestStateInput;
import mn.erin.domain.bpm.usecase.process.UpdateRequestStateOutput;
import mn.erin.domain.bpm.usecase.product.GetProductsById;

import static mn.erin.domain.bpm.BpmMessagesConstants.BNPL_LOG;
import static mn.erin.domain.bpm.BpmMessagesConstants.INSTANT_LOAN_LOG;
import static mn.erin.domain.bpm.BpmMessagesConstants.ONLINE_LEASING_LOG;
import static mn.erin.domain.bpm.BpmMessagesConstants.ONLINE_SALARY_LOG_HASH;
import static mn.erin.domain.bpm.BpmModuleConstants.BNPL_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.BRANCH_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CHANNEL;
import static mn.erin.domain.bpm.BpmModuleConstants.DIRECT_ONLINE_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.NO_CHANGE_BRANCH_PROCESS_TYPES;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.model.process.ParameterEntityType.getTypeByProcessType;


public class DigitalLoanUtils
{
  private DigitalLoanUtils()
  {
  }

  public static boolean updateRequestState(ProcessRequestRepository processRequestRepository, String requestId, ProcessRequestState processRequestState) throws UseCaseException
  {
    UpdateRequestState updateRequestState = new UpdateRequestState(processRequestRepository);
    UpdateRequestStateInput input = new UpdateRequestStateInput(requestId, processRequestState);
    UpdateRequestStateOutput output = updateRequestState.execute(input);
    return output.isUpdated();
  }

  public static void changeChannelAndBranch(BpmsRepositoryRegistry bpmsRepositoryRegistry, AimServiceRegistry aimServiceRegistry, Environment environment,
      String requestId, String processInstanceId, String processType, String branchNumber, String channel) throws UseCaseException
  {
    Map<String, Serializable> parameters = new HashMap<>();
    List<String> processTypes = Arrays.asList(Objects.requireNonNull(environment.getProperty(NO_CHANGE_BRANCH_PROCESS_TYPES)).split(","));
    if(!processTypes.contains(processType)){
      bpmsRepositoryRegistry.getProcessRequestRepository().updateGroup(requestId, branchNumber);
      parameters.put(BRANCH_NUMBER, branchNumber);
    }
    parameters.put(CHANNEL, channel);
    updateRequestParameters(aimServiceRegistry, bpmsRepositoryRegistry.getProcessRequestRepository(), requestId, parameters);

    Map<ParameterEntityType, Map<String, Serializable>> processParameters = new HashMap<>();
    processParameters.put(getTypeByProcessType(processType), parameters);
    updateProcessParameters(aimServiceRegistry, bpmsRepositoryRegistry.getProcessRepository(), processInstanceId, processParameters);
  }

  public static void updateRequestParameters(AimServiceRegistry aimServiceRegistry, ProcessRequestRepository processRequestRepository,
      String requestId, Map<String, Serializable> parameters) throws UseCaseException
  {
    UpdateRequestParametersInput updateRequestParametersInput = new UpdateRequestParametersInput(requestId, parameters);
    UpdateRequestParameters useCase = new UpdateRequestParameters(aimServiceRegistry.getAuthenticationService(), aimServiceRegistry.getAuthorizationService(),
        processRequestRepository);
    useCase.execute(updateRequestParametersInput);
  }

  public static void updateProcessParameters(AimServiceRegistry aimServiceRegistry, ProcessRepository processRepository,
      String processInstanceId, Map<ParameterEntityType, Map<String, Serializable>> processParameters)
      throws UseCaseException
  {
    UpdateProcessParameters updateProcessParameters = new UpdateProcessParameters(aimServiceRegistry.getAuthenticationService(),
        aimServiceRegistry.getAuthorizationService(), processRepository);
    UpdateProcessParametersInput updateProcessParametersInput = new UpdateProcessParametersInput(processInstanceId, processParameters);
    updateProcessParameters.execute(updateProcessParametersInput);
  }

  public static String getLogPrefix(String processTypeId){
    switch (processTypeId)
    {
    case BNPL_PROCESS_TYPE_ID:
      return BNPL_LOG;
    case INSTANT_LOAN_PROCESS_TYPE_ID:
      return INSTANT_LOAN_LOG;
    case ONLINE_LEASING_PROCESS_TYPE_ID:
      return ONLINE_LEASING_LOG;
    case DIRECT_ONLINE_PROCESS_TYPE_ID:
      return ONLINE_SALARY_LOG_HASH;
    default:
      return  ONLINE_SALARY_LOG_HASH;
    }
  }
  public static String getProductDescription(ProductRepository productRepository, String productId) throws UseCaseException
  {
    GetProductsById getProductsById = new GetProductsById(productRepository);
    List<Product> productsList = getProductsById.execute(productId).getProductsList();
    if (null == productsList || productsList.isEmpty())
    {
      return null;
    }
    Product product = productsList.get(0);
    return product.getProductDescription();
  }
  
  public static ArrayList<Map<String, Object>> getStatusLoanAccounts(NewCoreBankingService newCoreBankingService, DirectOnlineCoreBankingService directOnlineCoreBankingService, ProductRepository productRepository, Map<String, String> input) throws UseCaseException
  {
    try
    {
      GetLoanInfo getLoanInfo = new GetLoanInfo(newCoreBankingService, directOnlineCoreBankingService, productRepository);
      GetLoanInfoOutput getLoanInfoOutput = getLoanInfo.execute(input);

      Map<String, Map<String, Object>> mappedAccount = getLoanInfoOutput.getMappedAccount();

      return getFormattedAccounts(productRepository, mappedAccount);
    }
    catch (ErinException exception)
    {
      throw new UseCaseException(exception.getCode(), exception.getMessage());
    }
  }

  private static ArrayList<Map<String, Object>> getFormattedAccounts(ProductRepository productRepository, Map<String, Map<String, Object>> inputMap)
  {
    ArrayList<Map<String, Object>> formatedAccounts = new ArrayList<>();

    if (!inputMap.isEmpty())
    {
      inputMap.forEach((k, v) -> {
        Map<String, Object> tempAccount = new HashMap<>();
        v.forEach((k1, v1) -> {
          if (k1.equals("AccountID") || k1.equals("Balance") || k1.equals("ClosingLoanAmount"))
          {
            tempAccount.put(k1, v1);
            try
            {
              tempAccount.put("ProductName", getProductDescription(productRepository, (String) v.get("ProductID")));
            }
            catch (UseCaseException e)
            {
              throw new RuntimeException(e);
            }
          }
        });
        formatedAccounts.add(tempAccount);
      });
    }
    return formatedAccounts;
  }
}