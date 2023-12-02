package mn.erin.domain.bpm.usecase.instant_loan;

import static mn.erin.domain.bpm.BpmMessagesConstants.INSTANT_LOAN_LOG;
import static mn.erin.domain.bpm.BpmModuleConstants.BRANCH_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.GRANT_LOAN_AMOUNT_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_CONFIRM_TIME_RANGE;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_DELETE_TIME_RANGE;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_PROCESS_TYPE_CATEGORY;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.INTEREST_RATE;
import static mn.erin.domain.bpm.BpmModuleConstants.MAX_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PRODUCT_CODE;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.AMOUNT_REJECTED;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.ORG_REJECTED;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.SCORING_REJECTED;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.SYSTEM_FAILED;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.fromEnumToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.ProcessInstanceId;
import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.model.process.ProcessType;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;
import mn.erin.domain.bpm.service.RuntimeService;
import mn.erin.domain.bpm.usecase.process.GetProcessTypesByCategory;
import mn.erin.domain.bpm.usecase.process.GetProcessTypesByCategoryOutput;
import mn.erin.domain.bpm.util.process.DigitalLoanUtils;

public class GetInstantLoanRequestStatus extends AbstractUseCase<String, Map<String, Object>>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GetInstantLoanRequestStatus.class);

  public static final String STATUS = "Status";

  private final BpmsServiceRegistry bpmsServiceRegistry;
  private final BpmsRepositoryRegistry bpmsRepositoryRegistry;
  private final Environment environment;
  private ProcessRequestState requestState;
  private String instanceId;
  private String cifNumber;

  public GetInstantLoanRequestStatus(BpmsServiceRegistry bpmsServiceRegistry,
      BpmsRepositoryRegistry bpmsRepositoryRegistry, Environment environment)
  {
    this.bpmsRepositoryRegistry = bpmsRepositoryRegistry;
    this.bpmsServiceRegistry = bpmsServiceRegistry;
    this.environment = environment;
  }

  @Override
  public Map<String, Object> execute(String cifNumber) throws UseCaseException
  {
    this.cifNumber = cifNumber;
    Map<String, Object> output = new HashMap<>();
    try
    {
      ProcessRequestRepository processRequestRepository = bpmsRepositoryRegistry.getProcessRequestRepository();

      LocalDateTime endDate = LocalDateTime.now(ZoneId.of("UTC+8"));
      LocalDateTime startDate = endDate.minusHours(Long.parseLong(environment.getProperty(INSTANT_LOAN_CONFIRM_TIME_RANGE)));
      update168HoursPassedProcessRequests(processRequestRepository, bpmsRepositoryRegistry.getProcessRepository(), bpmsServiceRegistry.getRuntimeService(),
          cifNumber, startDate, endDate);

      Collection<ProcessRequest> processRequests = processRequestRepository.getDirectOnlineProcessRequests(cifNumber, startDate, endDate, getProcessType());
      assignProcessTypeAndStatus(processRequests, output);

      LOGGER.info(INSTANT_LOAN_LOG + "SUCCESSFULLY GETS THE OUTPUT = [{}], WITH CIF NUMBER = [{}]", output, cifNumber);
    }
    catch (BpmRepositoryException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
    return output;
  }

  private void update168HoursPassedProcessRequests(ProcessRequestRepository processRequestRepository, ProcessRepository processRepository,
      RuntimeService runtimeService,
      String cifNumber, LocalDateTime startDate, LocalDateTime endDate) throws UseCaseException, BpmRepositoryException
  {
    String processType = getProcessType();
    Collection<ProcessRequest> get168hPassedRequests = processRequestRepository.getBnplGivenTimePassedProcessRequests(processType, cifNumber, startDate,
        endDate);

    for (ProcessRequest processRequest : get168hPassedRequests)
    {
      String processInstanceId = processRequest.getProcessInstanceId();
      String processRequestState = processRequest.getState().toString();

      processRepository.deleteProcess(ProcessInstanceId.valueOf(processInstanceId));

      if (!StringUtils.equals(processRequestState, fromEnumToString(AMOUNT_REJECTED)) && !StringUtils.equals(processRequestState,
          fromEnumToString(ORG_REJECTED))
          && !StringUtils.equals(processRequestState, fromEnumToString(SCORING_REJECTED)) && !StringUtils.equals(processRequestState,
          fromEnumToString(SYSTEM_FAILED)))
      {
        runtimeService.closeProcess(processInstanceId);
      }
    }

    LocalDateTime startDate2 = endDate.minusHours(Long.parseLong(environment.getProperty(INSTANT_LOAN_DELETE_TIME_RANGE)));
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
      this.instanceId = processInstanceId;
      Map<String, Object> terms = bpmsRepositoryRegistry.getProcessRepository().getProcessParametersByInstanceId(processInstanceId);

      switch (processRequest.getState())
      {
      case NEW:
      case DELETED:
        assignPreviousLoanRequests(output, terms);
        setTypeAndStatusToMap(output, ProcessRequestState.NEW);
        return;
      case STARTED:
        setTypeAndStatusToMap(output, ProcessRequestState.STARTED, terms);
        return;
      case CONFIRMED:
        assignTerms(output, terms);
        setTypeAndStatusToMap(output, ProcessRequestState.CONFIRMED);
        return;
      case DISBURSED:
        assignTerms(output, terms);
        setTypeAndStatusToMap(output, ProcessRequestState.DISBURSED);
        return;
      case PROCESSING:
        assignTerms(output, terms);
        setTypeAndStatusToMap(output, ProcessRequestState.PROCESSING);
        return;
      case REJECTED:
        setTypeAndStatusToMap(output, ProcessRequestState.REJECTED, terms);
        return;
      case ORG_REJECTED:
        setTypeAndStatusToMap(output, ProcessRequestState.ORG_REJECTED, terms);
        return;
      case SCORING_REJECTED:
        setTypeAndStatusToMap(output, ProcessRequestState.SCORING_REJECTED, terms);
        return;
      case AMOUNT_REJECTED:
        setTypeAndStatusToMap(output, ProcessRequestState.AMOUNT_REJECTED, terms);
        return;
      case SYSTEM_FAILED:
        final Object systemFailCause = bpmsServiceRegistry.getRuntimeService().getVariableById(processInstanceId, "systemFailCause");
        output.put("systemFailCause", systemFailCause);
        setTypeAndStatusToMap(output, ProcessRequestState.SYSTEM_FAILED);
        return;
      case DISBURSE_FAILED:
        setTypeAndStatusToMap(output, ProcessRequestState.DISBURSE_FAILED);
        return;
      case TRANSACTION_FAILED:
        setTypeAndStatusToMap(output, ProcessRequestState.TRANSACTION_FAILED);
        return;
      case LOAN_ACCOUNT_FAILED:
        setTypeAndStatusToMap(output, ProcessRequestState.LOAN_ACCOUNT_FAILED);
        return;
      case FILE_UPLOAD_FAILED:
        setTypeAndStatusToMap(output, ProcessRequestState.FILE_UPLOAD_FAILED);
        return;
      case AMOUNT_BLOCKED:
        setTypeAndStatusToMap(output, ProcessRequestState.AMOUNT_BLOCKED);
        return;
      case AMOUNT_BLOCKED_FAILED:
        setTypeAndStatusToMap(output, ProcessRequestState.AMOUNT_BLOCKED_FAILED);
        return;
      case COMPLETED:
        assignCompletedTerms(output, terms);
        setTypeAndStatusToMap(output, ProcessRequestState.COMPLETED);
        return;
      default:
        assignPreviousLoanRequests(output, terms);
        output.put(STATUS, fromEnumToString(ProcessRequestState.NEW));
        return;
      }
    }
    output.put(STATUS, fromEnumToString(ProcessRequestState.NEW));
    LOGGER.info(INSTANT_LOAN_LOG + " PROCESS TYPE AND STATUS'S ARE ASSIGNED WITH PROCESS REQUESTS = [{}], OUTPUT = [{}]", processRequests, output);
  }

  @SuppressWarnings("unchecked")
  private void assignTerms(Map<String, Object> output, Map<String, Object> terms) throws UseCaseException
  {
    String interestRateString = String.valueOf(terms.get(INTEREST_RATE));
    String interestString = String.valueOf(terms.get("Interest"));
    BigDecimal interestRate = new BigDecimal(interestRateString);
    BigDecimal interest = new BigDecimal(interestString);
    BigDecimal offInterest = interest.subtract(interestRate);

    assignPreviousLoanRequests(output, terms);
    output.put(MAX_AMOUNT, terms.get(GRANT_LOAN_AMOUNT_STRING));
    output.put("offInterest", offInterest);
    output.put("grantMinimumAmount", terms.get("grantMinimumAmount"));

    LOGGER.info(INSTANT_LOAN_LOG + "ASSIGNING TERMS");
  }

  private void assignPreviousLoanRequests(Map<String, Object> output, Map<String, Object> terms) throws UseCaseException
  {
    Map<String, String> input = new HashMap<>();
    input.put(PROCESS_TYPE_ID, INSTANT_LOAN_PROCESS_TYPE_ID);
    input.put(PRODUCT_CODE, String.valueOf(terms.get(PRODUCT_CODE)));
    input.put(CIF_NUMBER, this.cifNumber);
    input.put(REGISTER_NUMBER, String.valueOf(terms.get(REGISTER_NUMBER)));
    input.put(PHONE_NUMBER, String.valueOf(terms.get(PHONE_NUMBER)));
    output.put("PreviousLoanRequests", DigitalLoanUtils.getStatusLoanAccounts(bpmsServiceRegistry.getNewCoreBankingService(), bpmsServiceRegistry.getDirectOnlineCoreBankingService(), bpmsRepositoryRegistry.getProductRepository(), input));
  }

  @SuppressWarnings("unchecked")
  private void assignCompletedTerms(Map<String, Object> output, Map<String, Object> terms) throws UseCaseException
  {
    String interestRateString = String.valueOf(terms.get(INTEREST_RATE));
    String interestString = String.valueOf(terms.get("Interest"));
    BigDecimal interestRate = new BigDecimal(interestRateString);
    BigDecimal interest = new BigDecimal(interestString);
    BigDecimal offInterest = interest.subtract(interestRate);

    assignPreviousLoanRequests(output, terms);
    output.put(MAX_AMOUNT, terms.get(GRANT_LOAN_AMOUNT_STRING));
    output.put("offInterest", offInterest);
    output.put("grantMinimumAmount", terms.get("grantMinimumAmount"));

    LOGGER.info(INSTANT_LOAN_LOG + "ASSIGNING TERMS");
  }

  private void setTypeAndStatusToMap(Map<String, Object> output, ProcessRequestState state)
  {
    LOGGER.info(INSTANT_LOAN_LOG + "STATE = [{}]", state);
    output.put(PROCESS_TYPE_ID, INSTANT_LOAN_PROCESS_TYPE_ID);
    output.put(STATUS, fromEnumToString(state));
    requestState = state;
  }

  private void setTypeAndStatusToMap(Map<String, Object> output, ProcessRequestState state, Map<String, Object> terms)
  {
    output.put(PROCESS_TYPE_ID, INSTANT_LOAN_PROCESS_TYPE_ID);
    output.put(BRANCH_NUMBER, getBranchNumber(terms));
    output.put(STATUS, fromEnumToString(state));
    requestState = state;
  }

  private Object getBranchNumber(Map<String, Object> terms)
  {
    if (terms.containsKey(BRANCH_NUMBER))
    {
      return terms.get(BRANCH_NUMBER);
    }
    return bpmsServiceRegistry.getRuntimeService().getVariableById(instanceId, BRANCH_NUMBER);
  }

  private String getProcessType() throws UseCaseException
  {
    GetProcessTypesByCategory getProcessTypesByCategory = new GetProcessTypesByCategory(bpmsRepositoryRegistry.getProcessTypeRepository());
    GetProcessTypesByCategoryOutput output = getProcessTypesByCategory.execute(INSTANT_LOAN_PROCESS_TYPE_CATEGORY);
    List<ProcessType> processTypes = output.getProcessTypes();
    return String.valueOf(processTypes.get(0).getId().getId());
  }
}
