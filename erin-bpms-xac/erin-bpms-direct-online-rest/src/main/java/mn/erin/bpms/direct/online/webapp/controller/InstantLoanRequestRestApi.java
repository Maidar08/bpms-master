package mn.erin.bpms.direct.online.webapp.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.bpms.direct.online.webapp.DirectOnlineLoanConstants;
import mn.erin.bpms.direct.online.webapp.model.InstantLoanRequestBody;
import mn.erin.bpms.direct.online.webapp.model.RestDanEntity;
import mn.erin.domain.aim.provider.ExtSessionInfoCache;
import mn.erin.domain.aim.service.AimServiceRegistry;
import mn.erin.domain.aim.service.EncryptionService;
import mn.erin.domain.aim.usecase.user.LoginUser;
import mn.erin.domain.aim.usecase.user.LoginUserInput;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.model.process.ProcessType;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.repository.directOnline.DefaultParameterRepository;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;
import mn.erin.domain.bpm.service.RuntimeService;
import mn.erin.domain.bpm.usecase.GetGeneralInfo;
import mn.erin.domain.bpm.usecase.GetGeneralInfoInput;
import mn.erin.domain.bpm.usecase.direct_online.DownloadOrganizationInfo;
import mn.erin.domain.bpm.usecase.direct_online.DownloadOrganizationInfoInput;
import mn.erin.domain.bpm.usecase.direct_online.GetLatestRequestByCif;
import mn.erin.domain.bpm.usecase.instant_loan.GetInstantLoanRequestStatus;
import mn.erin.domain.bpm.usecase.process.CreateProcessRequest;
import mn.erin.domain.bpm.usecase.process.CreateProcessRequestInput;
import mn.erin.domain.bpm.usecase.process.CreateProcessRequestOutput;
import mn.erin.domain.bpm.usecase.process.GetProcessTypesByCategory;
import mn.erin.domain.bpm.usecase.process.GetProcessTypesByCategoryOutput;
import mn.erin.domain.bpm.usecase.process.StartProcess;
import mn.erin.domain.bpm.usecase.process.StartProcessInput;
import mn.erin.domain.bpm.usecase.process.StartProcessOutput;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParameters;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParametersInput;
import mn.erin.domain.bpm.usecase.process.UpdateRequestParameters;
import mn.erin.domain.bpm.usecase.process.UpdateRequestParametersInput;
import mn.erin.domain.bpm.usecase.process.UpdateRequestState;
import mn.erin.domain.bpm.usecase.process.UpdateRequestStateInput;
import mn.erin.infrastucture.rest.common.response.RestResponse;

import static mn.erin.bpms.direct.online.webapp.DirectOnlineLoanConstants.RESPONSE;
import static mn.erin.bpms.direct.online.webapp.utils.DirectOnlineBnplRestUtil.deletePreviousProcess;
import static mn.erin.bpms.direct.online.webapp.utils.DirectOnlineBnplRestUtil.extractDanInfo;
import static mn.erin.bpms.direct.online.webapp.utils.DirectOnlineBnplRestUtil.getProcessingStateResponse;
import static mn.erin.bpms.direct.online.webapp.utils.DirectOnlineBnplRestUtil.handleGeneralException;
import static mn.erin.bpms.direct.online.webapp.utils.DirectOnlineBnplRestUtil.returnError;
import static mn.erin.domain.bpm.BpmActivityIdConstants.TASK_DEF_INSTANT_LOAN_ACCEPT;
import static mn.erin.domain.bpm.BpmActivityIdConstants.TASK_DEF_KEY_DIRECT_ONLINE_SET_CONSTANT_VARIABLES;
import static mn.erin.domain.bpm.BpmMessagesConstants.COULD_NOT_FIND_COMPLETED_PROCESS_BY_CIF_IN_LAST_7_DAYS_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.COULD_NOT_FIND_CONFIRMED_PROCESS_BY_CIF_IN_LAST_7_DAYS_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INSTANT_LOAN_LOG;
import static mn.erin.domain.bpm.BpmMessagesConstants.REQUEST_BODY_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.REQUEST_BODY_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.ACTION_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.ADDRESS;
import static mn.erin.domain.bpm.BpmModuleConstants.AREA;
import static mn.erin.domain.bpm.BpmModuleConstants.BRANCH_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CHARGE;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CURRENT_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.DAN_INFO;
import static mn.erin.domain.bpm.BpmModuleConstants.DAN_REGISTER;
import static mn.erin.domain.bpm.BpmModuleConstants.DEBT_INCOME_INSURANCE_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.DEFAULT_PARAM_ENTITY_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.EMAIL;
import static mn.erin.domain.bpm.BpmModuleConstants.EMPTY_VALUE;
import static mn.erin.domain.bpm.BpmModuleConstants.EXTEND;
import static mn.erin.domain.bpm.BpmModuleConstants.FIRST_PAYMENT_DATE;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ACCEPTED_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ACCEPTED_LOAN_AMOUNT_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.FULL_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.GRANT_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.GRANT_LOAN_AMOUNT_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_CONFIRM_TIME_RANGE;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_PROCESS_TYPE_CATEGORY;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.INTEREST_RATE;
import static mn.erin.domain.bpm.BpmModuleConstants.IS_SALARY_ORGANIZATION;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_CLASS_RANK;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PRODUCT;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PRODUCT_DESCRIPTION;
import static mn.erin.domain.bpm.BpmModuleConstants.LOCALE;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PRODUCT_CATEGORY;
import static mn.erin.domain.bpm.BpmModuleConstants.RETRY_ATTEMPT;
import static mn.erin.domain.bpm.BpmModuleConstants.START;
import static mn.erin.domain.bpm.BpmModuleConstants.STATE;
import static mn.erin.domain.bpm.BpmModuleConstants.TOPUP;
import static mn.erin.domain.bpm.model.process.ParameterEntityType.INSTANT_LOAN;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.COMPLETED;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.CONFIRMED;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.DISBURSED;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.PROCESSING;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.SYSTEM_FAILED;
import static mn.erin.domain.bpm.usecase.process.ProcessUtils.submitProcessByDefKeyAndProcessId;
import static mn.erin.domain.bpm.util.process.BpmNumberUtils.getThousandSeparatedString;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;
import static mn.erin.domain.bpm.util.process.DigitalLoanUtils.getProductDescription;

@RestController
@RequestMapping(value = "/instant-loan", name = "Provides BPMS instant loan request API")
public class InstantLoanRequestRestApi
{
  private static final Logger LOGGER = LoggerFactory.getLogger(InstantLoanRequestRestApi.class);

  private final AimServiceRegistry aimServiceRegistry;
  private final BpmsServiceRegistry bpmsServiceRegistry;
  private final BpmsRepositoryRegistry bpmsRepositoryRegistry;
  private final DefaultParameterRepository defaultParameterRepository;
  private final EncryptionService encryptionService;
  private final ExtSessionInfoCache extSessionInfoCache;
  private final Environment environment;

  @Inject
  public InstantLoanRequestRestApi(AimServiceRegistry aimServiceRegistry, BpmsServiceRegistry bpmsServiceRegistry,
      BpmsRepositoryRegistry bpmsRepositoryRegistry, DefaultParameterRepository defaultParameterRepository, EncryptionService encryptionService,
      ExtSessionInfoCache extSessionInfoCache, Environment environment)
  {
    this.aimServiceRegistry = aimServiceRegistry;
    this.bpmsServiceRegistry = bpmsServiceRegistry;
    this.bpmsRepositoryRegistry = bpmsRepositoryRegistry;
    this.defaultParameterRepository = defaultParameterRepository;
    this.encryptionService = encryptionService;
    this.extSessionInfoCache = extSessionInfoCache;
    this.environment = environment;
  }

  @ApiOperation("Creates Instant Loan request.")
  @PostMapping(value = "loan-requests/create")
  public ResponseEntity<Map<String, Object>> createInstantLoanRequest(@NotNull @RequestBody InstantLoanRequestBody requestBody)
  {
    if (requestBody.getDanInfo() == null)
    {
      returnError(null, "DAN Info is null!");
    }

    try
    {
      checkUserAndLogin();

      Map<String, Serializable> parameters = new HashMap<>();
      CreateProcessRequestOutput output = createInstantLoanProcess(requestBody, environment.getProperty("directOnline.user.id"), parameters);
      String instanceId = startInstantLoanProcess(output, requestBody, parameters);
      LOGGER.info(INSTANT_LOAN_LOG + " Successfully created and started process for user with cif = [{}] and process instance id = [{}]",
          requestBody.getCifNumber(), instanceId);
      Map<String, Object> responseMap = new HashMap<>();
      Map<String, Object> result = new HashMap<>();
      result.put("processInstanceId", instanceId);
      responseMap.put(STATE, "SUCCESS");
      responseMap.put("RESPONSE", result);

      return ResponseEntity.ok(responseMap);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return returnError(e.getCode(), e.getMessage());
    }
  }

  @ApiOperation("Get instant loan general info")
  @GetMapping("/get-general-info/{processType}/{entity}")
  public ResponseEntity getGeneralInfo(@PathVariable String processType, @PathVariable String entity) throws UseCaseException
  {
    Map<String, Object> responseMap = new HashMap<>();
    try
    {
      GetGeneralInfo getGeneralInfo = new GetGeneralInfo(defaultParameterRepository);
      GetGeneralInfoInput input = new GetGeneralInfoInput(processType, entity);
      Map<String, Object> defaultParameters = getGeneralInfo.execute(input);
      responseMap.put(RESPONSE, defaultParameters);
      return ResponseEntity.ok(responseMap);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return returnError(e.getCode(), e.getMessage());
    }
    catch (Exception e)
    {
      return handleGeneralException(e);
    }
  }

  @GetMapping(value = "/get-request-status/{cifNumber}")
  public ResponseEntity getInstantLoanRequestStatus(@PathVariable String cifNumber)
  {
    try
    {
      checkUserAndLogin();
      GetInstantLoanRequestStatus getInstantLoanRequestStatus = new GetInstantLoanRequestStatus(bpmsServiceRegistry, bpmsRepositoryRegistry, environment);
      return ResponseEntity.ok(getInstantLoanRequestStatus.execute(cifNumber));
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return returnError(e.getCode(), e.getMessage());
    }
  }

  @PostMapping(value = "/loan-requests/confirm")
  public ResponseEntity confirmLoanRequest(@RequestBody Map<String, Object> requestBody)
  {
    if (null == requestBody)
    {
      return returnError(REQUEST_BODY_NULL_CODE, REQUEST_BODY_NULL_MESSAGE);
    }
    try
    {
      String cifNumber = String.valueOf(requestBody.get(CIF_NUMBER));
      String action = String.valueOf(requestBody.get("action"));
      int days = (int) requestBody.get("days");
      String currentAccountNumber = String.valueOf(requestBody.get(CURRENT_ACCOUNT_NUMBER));
      checkUserAndLogin();
      GetLatestRequestByCif getLatestRequestByCif = new GetLatestRequestByCif(bpmsRepositoryRegistry.getProcessRequestRepository(), bpmsRepositoryRegistry,
          environment);
      Map<String, String> input = new HashMap<>();
      input.put(CIF_NUMBER, cifNumber);
      input.put(PROCESS_TYPE_ID, getProcessType());
      input.put("duration", environment.getProperty(INSTANT_LOAN_CONFIRM_TIME_RANGE));

      ProcessRequest processRequest = getLatestRequestByCif.execute(input);
      if (null != processRequest && StringUtils.isNotBlank(processRequest.getProcessInstanceId()) && processRequest.getState() == PROCESSING)
      {
        Map<String, Object> responseMap = getProcessingStateResponse(processRequest.getProcessInstanceId(), bpmsServiceRegistry.getRuntimeService());
        return ResponseEntity.ok(responseMap);
      }
      if ((null == processRequest || StringUtils.isBlank(processRequest.getProcessInstanceId()) || (processRequest.getState() != CONFIRMED))
          && action.equalsIgnoreCase("new"))
      {
        return returnError("", COULD_NOT_FIND_CONFIRMED_PROCESS_BY_CIF_IN_LAST_7_DAYS_MESSAGE + cifNumber);
      }

      if ((null == processRequest || StringUtils.isBlank(processRequest.getProcessInstanceId()) || (processRequest.getState() != COMPLETED
          && processRequest.getState() != DISBURSED) && processRequest.getState() != CONFIRMED) && (action.equalsIgnoreCase(TOPUP) || action.equalsIgnoreCase(
          EXTEND)))
      {
        return returnError("", COULD_NOT_FIND_COMPLETED_PROCESS_BY_CIF_IN_LAST_7_DAYS_MESSAGE + cifNumber);
      }
      RuntimeService runtimeService = bpmsServiceRegistry.getRuntimeService();
      String processInstanceId = processRequest.getProcessInstanceId();
      if(requestBody.get(FIXED_ACCEPTED_LOAN_AMOUNT) != null){
       BigDecimal fixedAcceptedLoanAmount = new BigDecimal(getValidString(requestBody.get(FIXED_ACCEPTED_LOAN_AMOUNT)));
       runtimeService.setVariable(processInstanceId, FIXED_ACCEPTED_LOAN_AMOUNT, fixedAcceptedLoanAmount);
       runtimeService.setVariable(processInstanceId, FIXED_ACCEPTED_LOAN_AMOUNT_STRING, getThousandSeparatedString(fixedAcceptedLoanAmount.doubleValue()));
      }
      runtimeService.setVariable(processInstanceId, "days", days);
      runtimeService.setVariable(processInstanceId, CURRENT_ACCOUNT_NUMBER, currentAccountNumber);
      runtimeService.setVariable(processInstanceId, "calculateAmount", false);
      runtimeService.setVariable(processInstanceId, ACTION_TYPE, action);
      runtimeService.setVariable(processInstanceId, FIRST_PAYMENT_DATE, DateUtils.addDays(new Date(), days));

      String requestId = String.valueOf(runtimeService.getVariableById(processInstanceId, PROCESS_REQUEST_ID));
      Map<String, Object> responseMap = new HashMap<>();

      if (processRequest.getState() == CONFIRMED && action.equalsIgnoreCase("new"))
      {
        updateProcessState(requestId, PROCESSING);
        callAsyncSubmit(processRequest.getProcessInstanceId(), "confirm", action);
        responseMap = getInstantLoanProcessingStateResponse(processInstanceId, runtimeService);

        updateNewRequestProcessParameter(days, processInstanceId);
      }
      else if ((processRequest.getState() == COMPLETED || processRequest.getState() == DISBURSED || processRequest.getState() == CONFIRMED) && (
          action.equalsIgnoreCase(TOPUP) || action.equalsIgnoreCase(EXTEND)))
      {
        String instanceId = processRequest.getProcessInstanceId();
        String userId = environment.getProperty("directOnline.user.id");
        Map<String, Object> variables = runtimeService.getRuntimeVariables(instanceId);
        variables.put("term", days);

        Map<String, Serializable> parameters = bpmsRepositoryRegistry.getProcessRequestRepository().getRequestParametersByRequestId(requestId);
        CreateProcessRequest createProcessRequest = new CreateProcessRequest(aimServiceRegistry.getAuthenticationService(),
            aimServiceRegistry.getAuthorizationService(), aimServiceRegistry.getTenantIdProvider(), bpmsRepositoryRegistry.getProcessRequestRepository(),
            aimServiceRegistry.getGroupRepository(), bpmsRepositoryRegistry.getProcessTypeRepository());

        CreateProcessRequestInput createProcessRequestInput = new CreateProcessRequestInput(getValidString(variables.get(BRANCH_NUMBER)), userId,
            getValidString(variables.get(PROCESS_TYPE_ID)));
        createProcessRequestInput.setParameters(parameters);

        CreateProcessRequestOutput output = createProcessRequest.execute(createProcessRequestInput);
        String newRequestId = output.getProcessRequestId();
        variables.put(PROCESS_REQUEST_ID, newRequestId);
        variables.put(ACTION_TYPE, action);

        variables.remove("completedTasks");
        variables.remove("instantLoanTaskIndex");

        String newInstanceId = startProcessToTopup(variables, newRequestId);
        runtimeService.setVariable(newInstanceId, PROCESS_REQUEST_ID, newRequestId);
        runtimeService.setVariable(newInstanceId, PROCESS_INSTANCE_ID, newInstanceId);

        updateProcessState(newRequestId, PROCESSING);

        updateTopupRequestParameters(variables, newInstanceId, newRequestId);

        callAsyncSubmit(newInstanceId, "confirm", action);
        responseMap = getInstantLoanProcessingStateResponse(newInstanceId, runtimeService);
      }

      return ResponseEntity.ok(responseMap);
    }
    catch (UseCaseException | BpmServiceException e)
    {
      LOGGER.error(e.getMessage(), e);
      return returnError(e.getCode(), e.getMessage());
    }
    catch (Exception e)
    {
      return handleGeneralException(e);
    }
  }

  private void updateNewRequestProcessParameter(Integer days, String processInstanceId) throws UseCaseException
  {
    UpdateProcessParameters updateProcessParameters = new UpdateProcessParameters(aimServiceRegistry.getAuthenticationService(),
        aimServiceRegistry.getAuthorizationService(), bpmsRepositoryRegistry.getProcessRepository());

    Map<String, Serializable> variables = new HashMap<>();
    variables.put("term", days);

    Map<ParameterEntityType, Map<String, Serializable>> updateVariables = new HashMap<>();
    updateVariables.put(INSTANT_LOAN, variables);

    UpdateProcessParametersInput updateProcessParametersInput = new UpdateProcessParametersInput(processInstanceId, updateVariables);
    updateProcessParameters.execute(updateProcessParametersInput);
  }

  public void updateTopupRequestParameters(Map<String, Object> variables, String instanceId, String requestId) throws UseCaseException
  {

    Map<String, Serializable> parameters = new HashMap<>();
    String grantLoanAmount = String.valueOf(variables.get(GRANT_LOAN_AMOUNT));
    String interestRate = String.valueOf(variables.get(INTEREST_RATE));
    String grantMinimumAmount = String.valueOf(variables.get("grantMinimumAmount"));

    parameters.put(GRANT_LOAN_AMOUNT, grantLoanAmount);
    parameters.put(GRANT_LOAN_AMOUNT_STRING, grantLoanAmount);
    parameters.put("grantMinimumAmount", grantMinimumAmount);
    parameters.put("Interest", String.valueOf(variables.get("Interest")));
    parameters.put(INTEREST_RATE, interestRate);
    parameters.put("loanClassName", String.valueOf(variables.get("loanClassName")));
    parameters.put(FIRST_PAYMENT_DATE, String.valueOf(variables.get(FIRST_PAYMENT_DATE)));
    parameters.put(LOAN_PRODUCT_DESCRIPTION, String.valueOf(variables.get(LOAN_PRODUCT_DESCRIPTION)));
    parameters.put(FULL_NAME, String.valueOf(variables.get(FULL_NAME)));
    parameters.put(FIXED_ACCEPTED_LOAN_AMOUNT_STRING, String.valueOf(variables.get(FIXED_ACCEPTED_LOAN_AMOUNT_STRING)));
    parameters.put(DEBT_INCOME_INSURANCE_STRING, String.valueOf(variables.get(DEBT_INCOME_INSURANCE_STRING)));
    parameters.put("term", (Integer) variables.get("term"));

    Map<ParameterEntityType, Map<String, Serializable>> processParams = new HashMap<>();
    processParams.put(INSTANT_LOAN, parameters);

    UpdateProcessParameters updateProcessParameters = new UpdateProcessParameters(aimServiceRegistry.getAuthenticationService(),
        aimServiceRegistry.getAuthorizationService(), bpmsRepositoryRegistry.getProcessRepository());
    UpdateProcessParametersInput parametersInput = new UpdateProcessParametersInput(instanceId, processParams);
    updateProcessParameters.execute(parametersInput);

    UpdateRequestParameters updateRequestParameters = new UpdateRequestParameters(aimServiceRegistry.getAuthenticationService(),
        aimServiceRegistry.getAuthorizationService(), bpmsRepositoryRegistry.getProcessRequestRepository());
    UpdateRequestParametersInput input = new UpdateRequestParametersInput(requestId, parameters);
    updateRequestParameters.execute(input);
  }

  public static Map<String, Object> getInstantLoanProcessingStateResponse(String instanceId, RuntimeService runtimeService)
  {
    Map<String, Object> responseMap = new HashMap<>();
    Map<String, Object> result = new HashMap<>();

    String requestId = String.valueOf(runtimeService.getVariableById(instanceId, PROCESS_REQUEST_ID));
    String cifNumber = String.valueOf(runtimeService.getVariableById(instanceId, DirectOnlineLoanConstants.CIF_NUMBER));
    String branchNumber = String.valueOf(runtimeService.getVariableById(instanceId, BRANCH_NUMBER));
    String disbursementAmountString = getValidString((runtimeService.getVariableById(instanceId, FIXED_ACCEPTED_LOAN_AMOUNT)));
    BigDecimal disbursementAmount = disbursementAmountString.equals(EMPTY_VALUE) ? new BigDecimal("0") : new BigDecimal(disbursementAmountString);
    String interestRateString = getValidString(runtimeService.getVariableById(instanceId, INTEREST_RATE));
    double interestRate = interestRateString.equals(EMPTY_VALUE) ? 0 : Double.parseDouble(interestRateString);
    String charge = getValidString(runtimeService.getVariableById(instanceId, CHARGE));

    result.put(DirectOnlineLoanConstants.CIF_NUMBER, cifNumber);
    result.put(PROCESS_REQUEST_ID, requestId);
    result.put(BRANCH_NUMBER, branchNumber);
    result.put(STATE, ProcessRequestState.fromEnumToString(PROCESSING));
    result.put(CHARGE, charge);
    result.put("disbursementAmount", disbursementAmount);
    result.put("loanAmount", disbursementAmount);
    result.put(INTEREST_RATE, interestRate);
    responseMap.put(STATE, "SUCCESS");
    responseMap.put(RESPONSE, result);

    return responseMap;
  }

  private String startProcessToTopup(Map<String, Object> parameters, String processRequestId) throws UseCaseException
  {
    StartProcess startProcess = new StartProcess(aimServiceRegistry.getAuthenticationService(), aimServiceRegistry.getAuthorizationService(),
        bpmsRepositoryRegistry.getProcessRequestRepository(), bpmsServiceRegistry.getProcessTypeService(), bpmsRepositoryRegistry.getProcessRepository(),
        bpmsServiceRegistry.getCaseService());
    StartProcessInput startProcessInput = new StartProcessInput(processRequestId);
    startProcessInput.setParameters(parameters);
    StartProcessOutput startProcessOutput = startProcess.execute(startProcessInput);
    String newInstanceId = startProcessOutput.getProcessInstanceId();

    UpdateRequestState updateRequestState = new UpdateRequestState(bpmsRepositoryRegistry.getProcessRequestRepository());
    UpdateRequestStateInput input = new UpdateRequestStateInput(processRequestId, CONFIRMED);
    updateRequestState.execute(input);

    return newInstanceId;
  }

  private String getProcessType() throws UseCaseException
  {
    GetProcessTypesByCategory getProcessTypesByCategory = new GetProcessTypesByCategory(bpmsRepositoryRegistry.getProcessTypeRepository());
    GetProcessTypesByCategoryOutput output = getProcessTypesByCategory.execute(INSTANT_LOAN_PROCESS_TYPE_CATEGORY);
    List<ProcessType> processTypes = output.getProcessTypes();
    return String.valueOf(processTypes.get(0).getId().getId());
  }

  private String startInstantLoanProcess(CreateProcessRequestOutput createProcessRequestOutput, InstantLoanRequestBody request,
      Map<String, Serializable> createProcessParameters) throws UseCaseException
  {
    RestDanEntity danInfo = extractDanInfo(request.getDanInfo());
    Map<String, Object> parameters = new HashMap<>();
    parameters.put(CIF_NUMBER, request.getCifNumber());
    parameters.put("salary", danInfo.getSalary());
    parameters.put(EMAIL, request.getEmail());
    parameters.put(PHONE_NUMBER, request.getPhoneNumber());
    parameters.put(BRANCH_NUMBER, createProcessParameters.get(BRANCH_NUMBER));
    parameters.put(AREA, createProcessParameters.get(BRANCH_NUMBER));
    parameters.put(ADDRESS, request.getAddress());
    parameters.put(IS_SALARY_ORGANIZATION, createProcessParameters.get(IS_SALARY_ORGANIZATION));
    parameters.put(DAN_INFO, request.getDanInfo());
    parameters.put(ACTION_TYPE, START);

    return startProcess(parameters, createProcessRequestOutput.getProcessRequestId());
  }

  private String startProcess(Map<String, Object> parameters, String processRequestId) throws UseCaseException
  {
    StartProcess startProcess = new StartProcess(aimServiceRegistry.getAuthenticationService(), aimServiceRegistry.getAuthorizationService(),
        bpmsRepositoryRegistry.getProcessRequestRepository(), bpmsServiceRegistry.getProcessTypeService(), bpmsRepositoryRegistry.getProcessRepository(),
        bpmsServiceRegistry.getCaseService());
    StartProcessInput startProcessInput = new StartProcessInput(processRequestId);
    startProcessInput.setParameters(parameters);
    StartProcessOutput startProcessOutput = startProcess.execute(startProcessInput);
    String instanceId = startProcessOutput.getProcessInstanceId();

    UpdateRequestState updateRequestState = new UpdateRequestState(bpmsRepositoryRegistry.getProcessRequestRepository());
    UpdateRequestStateInput input = new UpdateRequestStateInput(processRequestId, ProcessRequestState.STARTED);
    updateRequestState.execute(input);

    callAsyncSubmit(instanceId, START, START);
    return instanceId;
  }

  private void callAsyncSubmit(String instanceId, String type, String action)
  {
    ExecutorService executor = Executors.newFixedThreadPool(10);
    ProcessRunnable processRunnable = new ProcessRunnable(instanceId, type, action);
    executor.execute(processRunnable);
    executor.shutdown();
  }

  private void updateProcessState(String previousRequestId, ProcessRequestState state) throws UseCaseException
  {
    UpdateRequestState updateRequestState = new UpdateRequestState(bpmsRepositoryRegistry.getProcessRequestRepository());
    UpdateRequestStateInput input = new UpdateRequestStateInput(previousRequestId, state);
    updateRequestState.execute(input);
  }

  private CreateProcessRequestOutput createInstantLoanProcess(InstantLoanRequestBody request, String userId, Map<String, Serializable> parameters)
      throws UseCaseException
  {
    //TODO: implementations -> Elementary Criteria

    /* Extracting DAN Info */
    RestDanEntity danInfo = extractDanInfo(request.getDanInfo());

    DownloadOrganizationInfo downloadOrganizationInfo = new DownloadOrganizationInfo(bpmsServiceRegistry.getDirectOnlineCoreBankingService(),
        defaultParameterRepository);
    Map<String, String> inputParam = new HashMap<>();
    inputParam.put(PROCESS_TYPE_ID, request.getProductCategory());
    inputParam.put(DirectOnlineLoanConstants.PHONE_NUMBER, String.valueOf(request.getPhoneNumber()));
    inputParam.put(DAN_REGISTER, danInfo.getDanRegister());
    inputParam.put("district", danInfo.getDistrict());
    inputParam.put(PRODUCT_CATEGORY, request.getProductCategory());
    Map<String, Object> orgInfo = downloadOrganizationInfo.execute(inputParam);

    CreateProcessRequest createProcessRequest = new CreateProcessRequest(aimServiceRegistry.getAuthenticationService(),
        aimServiceRegistry.getAuthorizationService(), aimServiceRegistry.getTenantIdProvider(), bpmsRepositoryRegistry.getProcessRequestRepository(),
        aimServiceRegistry.getGroupRepository(), bpmsRepositoryRegistry.getProcessTypeRepository());

    CreateProcessRequestInput input = new CreateProcessRequestInput(String.valueOf(orgInfo.get(BRANCH_NUMBER)), userId, request.getProductCategory());
    LOGGER.info(INSTANT_LOAN_LOG + "BRANCH NUMBER = [{}] for cif number = [{}}", orgInfo.get(BRANCH_NUMBER), request.getCifNumber());

    parameters.put(BRANCH_NUMBER, String.valueOf(orgInfo.get(BRANCH_NUMBER)));
    parameters.put(DirectOnlineLoanConstants.CHANNEL, request.getChannel());
    parameters.put(PROCESS_TYPE_ID, request.getProductCategory());
    parameters.put(CIF_NUMBER, request.getCifNumber());

    parameters.put(LOAN_PRODUCT, getDefaultParameter(INSTANT_LOAN_PROCESS_TYPE_ID, DEFAULT_PARAM_ENTITY_NAME, "defaultProduct"));
    parameters.put(LOAN_PRODUCT_DESCRIPTION,
        getProductDescription(bpmsRepositoryRegistry.getProductRepository(), getDefaultParameter(INSTANT_LOAN_PROCESS_TYPE_ID, DEFAULT_PARAM_ENTITY_NAME, "defaultProduct")));

    parameters.put(PHONE_NUMBER, request.getPhoneNumber());
    parameters.put(LOCALE, request.getLocale());
    parameters.put(EMAIL, request.getEmail());
    parameters.put(DAN_REGISTER, danInfo.getDanRegister());

    input.setParameters(parameters);

    return createProcessRequest.execute(input);
  }

  private void checkUserAndLogin() throws UseCaseException
  {
    final String userId = environment.getProperty("directOnline.user.id");
    final String tenantId = environment.getProperty("directOnline.tenant.id");
    final String password = environment.getProperty("directOnline.user.password");
    if (StringUtils.isBlank(userId))
    {
      RestResponse.internalError("User id is missing!");
      return;
    }

    if (StringUtils.isBlank(tenantId))
    {
      RestResponse.internalError("Tenant id is missing!");
      return;
    }

    if (StringUtils.isBlank(password))
    {
      RestResponse.internalError("password is missing!");
      return;
    }

    String decryptedPassword = encryptionService.decrypt(password);

    LoginUser loginUser = new LoginUser(aimServiceRegistry.getAuthenticationService(), aimServiceRegistry.getMembershipRepository(),
        aimServiceRegistry.getRoleRepository(), encryptionService, extSessionInfoCache);
    LoginUserInput input = new LoginUserInput(tenantId, userId, decryptedPassword);
    input.setKillPreviousSession(false);
    loginUser.execute(input);
  }

  public class ProcessRunnable implements Runnable
  {
    String instanceId;
    String type;
    String action;

    public ProcessRunnable(String instanceId, String type, String action)
    {
      this.instanceId = Objects.requireNonNull(instanceId, "Instance Id is null!");
      this.type = type;
      this.action = action;
    }

    public void run()
    {
      try
      {
        String taskDefinition;
        if (type.equals("confirm"))
        {
          taskDefinition = TASK_DEF_INSTANT_LOAN_ACCEPT;
        }
        else
        {
          taskDefinition = TASK_DEF_KEY_DIRECT_ONLINE_SET_CONSTANT_VARIABLES;
        }
        submitProcessByDefKeyAndProcessId(instanceId, taskDefinition, bpmsServiceRegistry.getTaskService(), bpmsServiceRegistry.getTaskFormService(),
            bpmsServiceRegistry.getRuntimeService());
      }
      catch (Exception e)
      {
        LOGGER.error(e.getMessage());
        e.printStackTrace();
        bpmsServiceRegistry.getRuntimeService().setVariable(instanceId, "systemFailCause", e.getMessage());
        retryProcess(instanceId, type, action);
      }
    }
  }

  private String getDefaultParameter(String processType, String entityType, String parameterName) throws UseCaseException
  {
    GetGeneralInfoInput input = new GetGeneralInfoInput(processType, entityType);
    GetGeneralInfo getGeneralInfo = new GetGeneralInfo(bpmsRepositoryRegistry.getDefaultParameterRepository());
    Map<String, Object> defaultParams = getGeneralInfo.execute(input);

    Map<String, Object> defaultParam = (Map<String, Object>) defaultParams.get(entityType);
    return String.valueOf(defaultParam.get(parameterName));
  }

  private void retryProcess(String instanceId, String type, String action)
  {
    try
    {
      String cifNumber = String.valueOf(bpmsServiceRegistry.getRuntimeService().getVariableById(instanceId, CIF_NUMBER));
      Object retryNumber = bpmsServiceRegistry.getRuntimeService().getVariableById(instanceId, RETRY_ATTEMPT);
      String previousRequestId = String.valueOf(bpmsServiceRegistry.getRuntimeService().getVariableById(instanceId, PROCESS_REQUEST_ID));

      if (null != retryNumber && Integer.parseInt(String.valueOf(retryNumber)) >= 3)
      {
        updateProcessState(previousRequestId, SYSTEM_FAILED);
        LOGGER.error(INSTANT_LOAN_LOG + SYSTEM_FAILED + " FOR CIF = [{}]. PROCESS RETRY ATTEMPT = [{}]", cifNumber, retryNumber);
        return;
      }

      // Set variables to new process

      Map<String, Object> objectParameters = bpmsServiceRegistry.getRuntimeService().getRuntimeVariables(instanceId);
      objectParameters.put(ACTION_TYPE, START);
      LOGGER.error(INSTANT_LOAN_LOG + " EXCEPTION OCCURRED FOR CIF = [{}]. PROCESS RETRY ATTEMPT = [{}]", cifNumber, retryNumber == null ? 1 : retryNumber);

      // 2.Set retry number
      if (null == retryNumber)
      {
        retryNumber = 2;
      }
      else
      {
        retryNumber = Integer.parseInt(String.valueOf(retryNumber)) + 1;
      }

      if (type.equals("confirm"))
      {
        if (action.equals(EXTEND))
          throw new UseCaseException("EXTEND action is not supported for retry process");
        bpmsServiceRegistry.getRuntimeService().setVariable(instanceId, RETRY_ATTEMPT, retryNumber);
        callAsyncSubmit(instanceId, "confirm", action);
      }
      else
      {
        // 1.Start new process
        String newRequestId = previousRequestId;

        objectParameters.put(PROCESS_REQUEST_ID, newRequestId);
        objectParameters.remove(PROCESS_INSTANCE_ID);
        objectParameters.remove(LOAN_CLASS_RANK);
        final String newInstanceId = startProcess(objectParameters, newRequestId);

        bpmsServiceRegistry.getRuntimeService().setVariable(newInstanceId, RETRY_ATTEMPT, retryNumber);

        // 3. delete previous process
        deletePreviousProcess(instanceId, previousRequestId);
        //4. terminate and close previous process on camunda
        //      runtimeService.(instanceId);
        //      caseService.closeCases(instanceId);
      }
    }
    catch (Exception e)
    {
      LOGGER.error(INSTANT_LOAN_LOG + "ERROR OCCURRED TO RETRY FAILED PROCESS");
      e.printStackTrace();

      try
      {
        String previousRequestId = String.valueOf(bpmsServiceRegistry.getRuntimeService().getVariableById(instanceId, PROCESS_REQUEST_ID));
        updateProcessState(previousRequestId, SYSTEM_FAILED);
      }
      catch (Exception exception)
      {
        exception.printStackTrace();
      }
    }
  }
}
