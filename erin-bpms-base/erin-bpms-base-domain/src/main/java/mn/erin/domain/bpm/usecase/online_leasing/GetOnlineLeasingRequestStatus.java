package mn.erin.domain.bpm.usecase.online_leasing;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.account.XacAccount;
import mn.erin.domain.bpm.model.process.ProcessInstanceId;
import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.model.process.ProcessType;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;
import mn.erin.domain.bpm.service.RuntimeService;
import mn.erin.domain.bpm.usecase.process.GetProcessTypesByCategory;
import mn.erin.domain.bpm.usecase.process.GetProcessTypesByCategoryOutput;
import mn.erin.domain.bpm.util.process.DigitalLoanUtils;

import static mn.erin.domain.bpm.BpmMessagesConstants.ONLINE_LEASING_LOG;
import static mn.erin.domain.bpm.BpmModuleConstants.CHARGE_UC;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.EXPIRY;
import static mn.erin.domain.bpm.BpmModuleConstants.GRANT_LOAN_AMOUNT_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.INTEREST;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.MAX_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.MIN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_CONFIRM_TIME_RANGE;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_DELETE_TIME_RANGE;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_CATEGORY;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PRODUCT_CATEGORY;
import static mn.erin.domain.bpm.BpmModuleConstants.PRODUCT_CODE;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.TERM_UC;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.AMOUNT_REJECTED;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.COMPLETED;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.ORG_REJECTED;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.SCORING_REJECTED;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.SYSTEM_FAILED;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.fromEnumToString;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

public class GetOnlineLeasingRequestStatus extends AbstractUseCase<Map<String, Object>, Map<String, Object>>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GetOnlineLeasingRequestStatus.class);

  public static final String STATUS = "Status";

  private final BpmsServiceRegistry bpmsServiceRegistry;
  private final BpmsRepositoryRegistry bpmsRepositoryRegistry;
  private final Environment environment;
  private String cifNumber;
  private String productCategory;

  public GetOnlineLeasingRequestStatus(BpmsServiceRegistry bpmsServiceRegistry,
      BpmsRepositoryRegistry bpmsRepositoryRegistry, Environment environment)
  {
    this.bpmsRepositoryRegistry = bpmsRepositoryRegistry;
    this.bpmsServiceRegistry = bpmsServiceRegistry;
    this.environment = environment;
  }

  @Override
  public Map<String, Object> execute(Map<String, Object> input) throws UseCaseException
  {
    boolean isOverdue;
    this.cifNumber = String.valueOf(input.get(CIF_NUMBER));
    this.productCategory = ((Map<String, String>) input.get("unionFields")).get(PRODUCT_CATEGORY);
    Map<String, Object> output = new HashMap<>();
    try
    {
      ProcessRequestRepository processRequestRepository = bpmsRepositoryRegistry.getProcessRequestRepository();

      LocalDateTime endDate = LocalDateTime.now(ZoneId.of("UTC+8"));
      LocalDateTime startDate = endDate.minusHours(Long.parseLong(environment.getProperty(ONLINE_LEASING_CONFIRM_TIME_RANGE)));
      update24HoursPassedProcessRequests(processRequestRepository, bpmsRepositoryRegistry.getProcessRepository(), bpmsServiceRegistry.getRuntimeService(),
          startDate, endDate);

      Collection<ProcessRequest> processRequests = processRequestRepository.getProcessRequestsOnlineLeasing(cifNumber, startDate, endDate, getProcessType(),
          productCategory);

      Map<String, String> inputParam = new HashMap<>();
      if (!processRequests.isEmpty())
      {
        ProcessRequest lastProcessRequest = Collections.max(processRequests, Comparator.comparing(ProcessRequest::getCreatedTime));
        inputParam.put(PHONE_NUMBER, bpmsRepositoryRegistry.getProcessRequestRepository().getParameterByName(lastProcessRequest.getId().getId(), PHONE_NUMBER));
      }
      if (StringUtils.isBlank(inputParam.get(PHONE_NUMBER)) || inputParam.get(PHONE_NUMBER) == null)
      {
        Map<String, String> unionFields = (Map<String, String>) input.get("unionFields");
        inputParam.put(PHONE_NUMBER, unionFields.get("keyField1"));
      }
      inputParam.put(PROCESS_TYPE_ID, ONLINE_LEASING_PROCESS_TYPE_ID);
      inputParam.put(CIF_NUMBER, cifNumber);
      Map<String, Object> customerMap = bpmsServiceRegistry.getNewCoreBankingService().findCustomerByCifNumber(inputParam);
      inputParam.put(REGISTER_NUMBER, (String) customerMap.get(REGISTER_NUMBER));
      isOverdue = checkAccount(output, inputParam);
      if (isOverdue)
      {
        output.put(STATUS, ProcessRequestState.OVERDUE);
      }
      else
      {
        assignProcessTypeAndStatus(processRequests, output);
      }

      output.put("UnionFields", input.get("unionFields"));

      LOGGER.info("{} SUCCESSFULLY GETS THE OUTPUT = [{}], WITH CIF NUMBER = [{}]", ONLINE_LEASING_LOG, output, cifNumber);
    }
    catch (BpmRepositoryException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
    catch (BpmServiceException e)
    {
      throw new RuntimeException(e);
    }
    return output;
  }

  private void update24HoursPassedProcessRequests(ProcessRequestRepository processRequestRepository, ProcessRepository processRepository,
      RuntimeService runtimeService, LocalDateTime startDate, LocalDateTime endDate) throws UseCaseException, BpmRepositoryException
  {
    String processType = getProcessType();
    Collection<ProcessRequest> getTimePassedRequests = processRequestRepository.getBnplGivenTimePassedProcessRequests(processType, cifNumber, startDate,
        endDate);

    for (ProcessRequest processRequest : getTimePassedRequests)
    {
      String processInstanceId = processRequest.getProcessInstanceId();
      String processRequestState = processRequest.getState().toString();

      processRepository.deleteProcess(ProcessInstanceId.valueOf(processInstanceId));

      if (!StringUtils.equals(processRequestState, fromEnumToString(AMOUNT_REJECTED)) && !StringUtils.equals(processRequestState,
          fromEnumToString(ORG_REJECTED))
          && !StringUtils.equals(processRequestState, fromEnumToString(SCORING_REJECTED)) && !StringUtils.equals(processRequestState,
          fromEnumToString(SYSTEM_FAILED)) && !StringUtils.equals(processRequestState, fromEnumToString(COMPLETED)))
      {
        runtimeService.closeProcess(processInstanceId);
      }
    }

    LocalDateTime startDate2 = endDate.minusHours(Long.parseLong(environment.getProperty(ONLINE_LEASING_DELETE_TIME_RANGE)));
    int updatedRequestCount24h = processRequestRepository.update24hPassedRequestStateExcludingConfirmed(cifNumber, startDate2, endDate, processType,
        ProcessRequestState.DELETED.name());
    LOGGER.info("Updated state of [{}] process requests that are passed 24 hours to DELETED with cif number: [{}].", updatedRequestCount24h, cifNumber);

    int updatedRequestCount = processRequestRepository.update24hPassedProcessState(cifNumber, startDate, endDate, processType,
        ProcessRequestState.DELETED.name());
    LOGGER.info("Updated state of [{}] process requests that are passed 168 hours to DELETED with cif number: [{}].", updatedRequestCount, cifNumber);
  }

  private void assignProcessTypeAndStatus(Collection<ProcessRequest> processRequests, Map<String, Object> output) throws UseCaseException
  {
    final List<ProcessRequest> sortedProcessRequests = processRequests.stream().sorted(Comparator.comparing(ProcessRequest::getCreatedTime).reversed())
        .collect(Collectors.toList());
    for (ProcessRequest processRequest : sortedProcessRequests)
    {
      final String processInstanceId = processRequest.getProcessInstanceId();
      Map<String, Object> terms = bpmsRepositoryRegistry.getProcessRepository().getProcessParametersByInstanceId(processInstanceId);

      switch (processRequest.getState())
      {
      case NEW:
      case COMPLETED:
        assignAccounts(output, terms);
        setTypeAndStatusToMap(output, ProcessRequestState.COMPLETED);
        return;
      case DELETED:
        setTypeAndStatusToMap(output, ProcessRequestState.DELETED);
        return;
      case STARTED:
        setTypeAndStatusToMap(output, ProcessRequestState.STARTED);
        return;
      case CONFIRMED:
        assignTerms(output, terms);
        assignAccounts(output, terms);
        setTypeAndStatusToMap(output, ProcessRequestState.CONFIRMED);
        output.put(EXPIRY, changeDateTimeFormat(processRequest.getCreatedTime().plusHours(
            Long.parseLong(Objects.requireNonNull(environment.getProperty(ONLINE_LEASING_CONFIRM_TIME_RANGE))))));
        return;
      case DISBURSED:
        assignTerms(output, terms);
        assignAccounts(output, terms);
        setTypeAndStatusToMap(output, ProcessRequestState.DISBURSED);
        return;
      case PROCESSING:
        assignTerms(output, terms);
        assignAccounts(output, terms);
        setTypeAndStatusToMap(output, ProcessRequestState.PROCESSING);
        return;
      case REJECTED:
        setTypeAndStatusToMap(output, ProcessRequestState.REJECTED);
        return;
      case ORG_REJECTED:
        setTypeAndStatusToMap(output, ProcessRequestState.ORG_REJECTED);
        return;
      case SCORING_REJECTED:
        setTypeAndStatusToMap(output, ProcessRequestState.SCORING_REJECTED);
        return;
      case CIB_FAILED:
        setTypeAndStatusToMap(output, ProcessRequestState.CIB_FAILED);
        return;
      case AMOUNT_REJECTED:
        setTypeAndStatusToMap(output, ProcessRequestState.AMOUNT_REJECTED);
        return;
      case SYSTEM_FAILED:
        final Object systemFailCause = bpmsServiceRegistry.getRuntimeService().getVariableById(processInstanceId, "systemFailCause");
        output.put("systemFailCause", systemFailCause);
        setTypeAndStatusToMap(output, ProcessRequestState.SYSTEM_FAILED);
        return;
      case DISBURSE_FAILED:
        assignAccounts(output, terms);
        setTypeAndStatusToMap(output, ProcessRequestState.DISBURSE_FAILED);
        return;
      case TRANSACTION_FAILED:
        setTypeAndStatusToMap(output, ProcessRequestState.TRANSACTION_FAILED);
        return;
      case LOAN_ACCOUNT_FAILED:
        setTypeAndStatusToMap(output, ProcessRequestState.LOAN_ACCOUNT_FAILED);
        return;
      case FILE_UPLOAD_FAILED:
        assignAccounts(output, terms);
        setTypeAndStatusToMap(output, ProcessRequestState.FILE_UPLOAD_FAILED);
        return;
      case AMOUNT_BLOCKED:
        assignAccounts(output, terms);
        setTypeAndStatusToMap(output, ProcessRequestState.AMOUNT_BLOCKED);
        return;
      case AMOUNT_BLOCKED_FAILED:
        assignAccounts(output, terms);
        setTypeAndStatusToMap(output, ProcessRequestState.AMOUNT_BLOCKED_FAILED);
        return;
      default:
        output.put(STATUS, fromEnumToString(ProcessRequestState.NEW));
        return;
      }
    }

    output.put(STATUS, fromEnumToString(ProcessRequestState.NEW));
    LOGGER.info("{} PROCESS TYPE AND STATUS'S ARE ASSIGNED WITH PROCESS REQUESTS = [{}], OUTPUT = [{}]", ONLINE_LEASING_LOG, processRequests, output);
  }

  @SuppressWarnings("unchecked")
  private void assignTerms(Map<String, Object> output, Map<String, Object> terms)
  {
    output.put(MAX_AMOUNT, terms.get(GRANT_LOAN_AMOUNT_STRING));
    output.put(MIN_AMOUNT, terms.get("grantMinimumAmount"));
    output.put(TERM_UC, terms.get("term"));
    output.put(CHARGE_UC, terms.get("charge"));
    output.put(INTEREST, terms.get("interestRate"));
    LOGGER.info("{} ASSIGNING TERMS", ONLINE_LEASING_LOG);
  }

  @SuppressWarnings("unchecked")
  private void assignAccounts(Map<String, Object> output, Map<String, Object> terms) throws UseCaseException
  {
    Map<String, String> input = new HashMap<>();
    input.put(PROCESS_TYPE_ID, ONLINE_LEASING_PROCESS_TYPE_ID);
    input.put(PRODUCT_CODE, String.valueOf(terms.get(PRODUCT_CODE)));
    input.put(CIF_NUMBER, String.valueOf(terms.get(CIF_NUMBER)));
    input.put(REGISTER_NUMBER, String.valueOf(terms.get(REGISTER_NUMBER)));
    input.put(PHONE_NUMBER, String.valueOf(terms.get(PHONE_NUMBER)));
    input.put(PRODUCT_CATEGORY, productCategory);
    ArrayList<Map<String, Object>> loanAccounts = DigitalLoanUtils.getStatusLoanAccounts(bpmsServiceRegistry.getNewCoreBankingService(),
        bpmsServiceRegistry.getDirectOnlineCoreBankingService(),
        bpmsRepositoryRegistry.getProductRepository(), input);
    output.put("PreviousLoanRequests", loanAccounts);

    String accountNumber = getValidString(terms.get(LOAN_ACCOUNT_NUMBER));
    String finalAccountNumber = accountNumber;
    if (StringUtils.isBlank(finalAccountNumber) || loanAccounts.stream().noneMatch(m1 -> m1.containsValue(finalAccountNumber)))
    {
      accountNumber = "";
    }
    output.put("createdLoanAccountID", accountNumber);
    LOGGER.info("{} ASSIGNING TERMS", ONLINE_LEASING_LOG);
  }

  private void setTypeAndStatusToMap(Map<String, Object> output, ProcessRequestState state)
  {
    LOGGER.info("{} STATE = [{}]", ONLINE_LEASING_LOG, state);
    output.put(PROCESS_TYPE, ONLINE_LEASING_PROCESS_TYPE_ID);
    output.put(STATUS, fromEnumToString(state));
  }

  private String getProcessType() throws UseCaseException
  {
    GetProcessTypesByCategory getProcessTypesByCategory = new GetProcessTypesByCategory(bpmsRepositoryRegistry.getProcessTypeRepository());
    GetProcessTypesByCategoryOutput output = getProcessTypesByCategory.execute(ONLINE_LEASING_PROCESS_TYPE_CATEGORY);
    List<ProcessType> processTypes = output.getProcessTypes();
    return String.valueOf(processTypes.get(0).getId().getId());
  }

  private boolean checkAccount(Map<String, Object> output, Map<String, String> input) throws BpmServiceException
  {
    List<XacAccount> accountsList = bpmsServiceRegistry.getNewCoreBankingService().getAccountsList(input);
    accountsList = accountsList.stream().filter(a -> a.getSchemaType().equals(environment.getProperty("laa.type"))).collect(Collectors.toList());
    if (accountsList.stream().anyMatch(a -> !a.getClassification().equals(environment.getProperty("norm.classification"))))
    {
      attachAccounts(output, accountsList);
      return true;
    }
    return false;
  }

  private void attachAccounts(Map<String, Object> output, List<XacAccount> accountsList)
  {
    List<Object> accounts = new ArrayList<>();
    for (XacAccount account : accountsList)
    {
      Map<String, String> account1 = new HashMap<>();
      account1.put("accountId", account.getId().getId());
      account1.put("balance", account.getBalance());
      account1.put("classification", account.getClassification());
      accounts.add(account1);
    }
    output.put("accounts", accounts);
    LOGGER.info(ONLINE_LEASING_LOG + "ASSIGNING ACCOUNTS");
  }

  private String changeDateTimeFormat(LocalDateTime plusHours)
  {
    return plusHours.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
  }
}
