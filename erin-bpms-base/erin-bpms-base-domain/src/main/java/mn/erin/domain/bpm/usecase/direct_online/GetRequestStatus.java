package mn.erin.domain.bpm.usecase.direct_online;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.model.process.ProcessInstanceId;
import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.model.process.ProcessType;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.repository.directOnline.DefaultParameterRepository;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.usecase.process.GetProcessTypesByCategory;
import mn.erin.domain.bpm.usecase.process.GetProcessTypesByCategoryOutput;

import static mn.erin.domain.bpm.BpmMessagesConstants.ONLINE_SALARY_LOG_HASH;
import static mn.erin.domain.bpm.BpmModuleConstants.BRANCH_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CHARGE_UC;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.DAY_OF_PAYMENT;
import static mn.erin.domain.bpm.BpmModuleConstants.DIRECT_ONLINE_SALARY_CONFIRM_TIME_RANGE;
import static mn.erin.domain.bpm.BpmModuleConstants.DIRECT_ONLINE_SALARY_DELETE_TIME_RANGE;
import static mn.erin.domain.bpm.BpmModuleConstants.DIRECT_ONLINE_PROCESS_TYPE_CATEGORY;
import static mn.erin.domain.bpm.BpmModuleConstants.GRANT_LOAN_AMOUNT_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.INTEREST_RATE;
import static mn.erin.domain.bpm.BpmModuleConstants.MAX_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_SALARY_PROCESS_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PRODUCT_CODE;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.TERM_UC;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.AMOUNT_REJECTED;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.DISBURSE_FAILED;

/**
 * @author Oyungerel Chuluunsukh
 **/
public class GetRequestStatus extends AbstractUseCase<String, Map<String, Object>>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GetRequestStatus.class);

  public static final String STATUS = "Status";
  public static final String PREVIOUS_LOAN_REQUESTS = "PreviousLoanRequests";
  private static final String INTEREST = "Interest";
  public static final String LOAN_TERMS = "LoanTerms";
  public static final String CHANNEL = "internet-bank";
  public static final String ONLINE_SALARY = "Online salary";
  public static final String PROCESS_TYPE_ID = "ProcessTypeId";

  private final DefaultParameterRepository defaultParameterRepository;
  private final BpmsServiceRegistry bpmsServiceRegistry;
  private final BpmsRepositoryRegistry bpmsRepositoryRegistry;
  private final Environment environment;

  private ProcessRequestState requestState;
  private String instanceId;
  private String registerNumber;

  public GetRequestStatus(DefaultParameterRepository defaultParameterRepository, BpmsServiceRegistry bpmsServiceRegistry,
      BpmsRepositoryRegistry bpmsRepositoryRegistry, Environment environment)
  {
    this.bpmsRepositoryRegistry = bpmsRepositoryRegistry;
    this.defaultParameterRepository = Objects.requireNonNull(defaultParameterRepository, "Default parameter repository cannot be null!");
    this.bpmsServiceRegistry = bpmsServiceRegistry;
    this.environment = environment;
  }

  @Override
  public Map<String, Object> execute(String cifNumber) throws UseCaseException
  {
    Map<String, Object> output = new HashMap<>();
    try
    {
      ProcessRequestRepository processRequestRepository = bpmsRepositoryRegistry.getProcessRequestRepository();
      LocalDateTime endDate = LocalDateTime.now(ZoneId.of("UTC+8"));
      LocalDateTime startDate = endDate.minusHours(Long.parseLong(environment.getProperty(DIRECT_ONLINE_SALARY_CONFIRM_TIME_RANGE)));
      update24hPassedProcessRequests(processRequestRepository, bpmsRepositoryRegistry.getProcessRepository(), bpmsServiceRegistry.getCaseService(), cifNumber,
          startDate, endDate);
      Collection<ProcessRequest> processRequests = processRequestRepository.getDirectOnlineProcessRequests(cifNumber, startDate, endDate, getProcessType());
      assignProcessTypeAndStatus(processRequests, output);
      LOGGER.info(ONLINE_SALARY_LOG_HASH + " PROCESS TYPE AND STATUS'S ARE ASSIGNED WITH PROCESS REQUESTS = [{}], OUTPUT = [{}]", processRequests, output);
      if (requestState == ProcessRequestState.CONFIRMED && registerNumber != null)
      {
        Map<String, String> input = new HashMap<>();
        input.put(BpmModuleConstants.PROCESS_TYPE_ID, ONLINE_SALARY_PROCESS_TYPE);
        input.put(PRODUCT_CODE, ONLINE_SALARY_PROCESS_TYPE);
        input.put(CIF_NUMBER, cifNumber);
        input.put(REGISTER_NUMBER, registerNumber);
        if (!processRequests.isEmpty()){
          ProcessRequest lastProcessRequest = Collections.max(processRequests, Comparator.comparing(ProcessRequest::getCreatedTime));
          input.put(PHONE_NUMBER, bpmsRepositoryRegistry.getProcessRequestRepository().getParameterByName(lastProcessRequest.getId().getId(), PHONE_NUMBER));
        }
        assignAccounts(output, input);
      }
    }
    catch (BpmRepositoryException | BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
    LOGGER.info(ONLINE_SALARY_LOG_HASH + "SUCCESSFULLY GETS THE OUTPUT = [{}], WITH CIF NUMBER = [{}]", output, cifNumber);
    return output;
  }

  private void update24hPassedProcessRequests(ProcessRequestRepository processRequestRepository, ProcessRepository processRepository, CaseService caseService,
      String cifNumber, LocalDateTime startDate, LocalDateTime endDate)
      throws BpmRepositoryException, UseCaseException
  {
    String processType = getProcessType();

    Collection<ProcessRequest> get24hPassedRequests = processRequestRepository.getGivenTimePassedProcessRequests(processType, cifNumber, startDate, endDate);
    for (ProcessRequest request : get24hPassedRequests)
    {
      String processInstanceId = request.getProcessInstanceId();
      processRepository.deleteProcess(ProcessInstanceId.valueOf(processInstanceId));
      caseService.closeCases(processInstanceId);
      if (!request.getState().equals(AMOUNT_REJECTED))
      {
        caseService.terminateCase(processInstanceId);
      }
    }
    LocalDateTime startDate2 = endDate.minusHours(Long.parseLong(environment.getProperty(DIRECT_ONLINE_SALARY_DELETE_TIME_RANGE)));
    int updatedRequestCount = processRequestRepository.update24hPassedProcessState(cifNumber, startDate2, endDate, processType,
        ProcessRequestState.DELETED.name());
    LOGGER.info("[{}] process requests passed 24 hour and updated state to DELETED with cif number: [{}].", updatedRequestCount, cifNumber);
  }

  private String getProcessType() throws UseCaseException
  {
    GetProcessTypesByCategory getProcessTypesByCategory = new GetProcessTypesByCategory(bpmsRepositoryRegistry.getProcessTypeRepository());
    GetProcessTypesByCategoryOutput output = getProcessTypesByCategory.execute(DIRECT_ONLINE_PROCESS_TYPE_CATEGORY);
    List<ProcessType> processTypes = output.getProcessTypes();
    return String.valueOf(processTypes.get(0).getId().getId());
  }

  private void assignAccounts(Map<String, Object> output, Map<String, String> input) throws UseCaseException
  {
    BigDecimal totalBalance = getTotalBalance(input);
    output.put(PREVIOUS_LOAN_REQUESTS, totalBalance);
    LOGGER.info(ONLINE_SALARY_LOG_HASH + "WHEN STATE IS CONFIRMED WITH TOTAL BALANCE = [{}]", totalBalance);
  }

  private void assignProcessTypeAndStatus(Collection<ProcessRequest> processRequests, Map<String, Object> output) throws UseCaseException, BpmServiceException
  {
    final List<ProcessRequest> sortedProcessRequests = processRequests.stream().sorted(Comparator.comparing(ProcessRequest::getCreatedTime).reversed())
        .collect(Collectors.toList());
    for (ProcessRequest processRequest : sortedProcessRequests)
    {
      registerNumber = bpmsRepositoryRegistry.getProcessRequestRepository().getParameterByName(processRequest.getId().getId(), REGISTER_NUMBER);
      final String processInstanceId = processRequest.getProcessInstanceId();
      this.instanceId = processInstanceId;
      Map<String, Object> terms = bpmsRepositoryRegistry.getProcessRepository().getProcessParametersByInstanceId(processInstanceId);
      switch (processRequest.getState())
      {
      case NEW:
      case RETURNED:
      case DISBURSED:
      case DELETED:
        setTypeAndStatusToMap(output, ProcessRequestState.NEW);
        return;
      case STARTED:
        setTypeAndStatusToMap(output, ProcessRequestState.STARTED, terms);
        return;
      case CONFIRMED:
        assignTerms(output, terms);
        setTypeAndStatusToMap(output, ProcessRequestState.CONFIRMED);
        return;
      case PROCESSING:
        assignTerms(output, terms);
        setTypeAndStatusToMap(output, ProcessRequestState.PROCESSING);
        return;
      case REJECTED:
        setTypeAndStatusToMap(output, ProcessRequestState.REJECTED, terms);
        return;
      case CUST_REJECTED:
        setTypeAndStatusToMap(output, ProcessRequestState.CUST_REJECTED, terms);
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
      case MB_SESSION_EXPIRED:
        setTypeAndStatusToMap(output, ProcessRequestState.MB_SESSION_EXPIRED, terms);
        return;
      case SYSTEM_FAILED:
        final Object systemFailCause = bpmsServiceRegistry.getCaseService().getVariableById(processInstanceId, "systemFailCause");
        output.put("systemFailCause", systemFailCause);
        setTypeAndStatusToMap(output, ProcessRequestState.SYSTEM_FAILED);

        return;
      case DISBURSE_FAILED:
        setTypeAndStatusToMap(output, DISBURSE_FAILED);
        return;
      default:
        return;
      }
    }

    output.put(STATUS, ProcessRequestState.fromEnumToString(ProcessRequestState.NEW));
  }

  @SuppressWarnings("unchecked")
  private void assignTerms(Map<String, Object> output, Map<String, Object> terms) throws UseCaseException, BpmServiceException
  {
    try
    {
      Map<String, Object> staticTerms = (Map<String, Object>) this.defaultParameterRepository.getDefaultParametersByProcessType("OnlineSalary", LOAN_TERMS)
          .get(LOAN_TERMS);
      output.put(CHARGE_UC, staticTerms.get(CHARGE_UC));
      output.put(TERM_UC, staticTerms.get(TERM_UC));
    }
    catch (BpmRepositoryException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
    output.put(MAX_AMOUNT, terms.get(GRANT_LOAN_AMOUNT_STRING));
    output.put(INTEREST, terms.get(INTEREST_RATE));
    output.put(DAY_OF_PAYMENT, terms.get(DAY_OF_PAYMENT));
    LOGGER.info(ONLINE_SALARY_LOG_HASH
            + "ASSIGNING TERMS WITH CHARGE = [{}], TERM = [{}], MAX AMOUNT = [{}], INTEREST RATE = [{}], DAY OF PAYMENT = [{}] , BRANCH NUMBER = [{}]",
        terms.get(CHARGE_UC), terms.get(TERM_UC), terms.get(GRANT_LOAN_AMOUNT_STRING), terms.get(INTEREST_RATE), terms.get(DAY_OF_PAYMENT), getBranchNumber(terms));
    output.put(BRANCH_NUMBER, getBranchNumber(terms));
  }

  private void setTypeAndStatusToMap(Map<String, Object> output, ProcessRequestState state)
  {
    LOGGER.info(ONLINE_SALARY_LOG_HASH + "STATE = [{}]", state);
    output.put(PROCESS_TYPE_ID, ONLINE_SALARY);
    output.put(STATUS, ProcessRequestState.fromEnumToString(state));
    this.requestState = state;
  }

  private void setTypeAndStatusToMap(Map<String, Object> output, ProcessRequestState state, Map<String, Object> terms) throws BpmServiceException
  {
    LOGGER.info(ONLINE_SALARY_LOG_HASH + "STATE = [{}], TERM = [{}]", state, getBranchNumber(terms));
    output.put(PROCESS_TYPE_ID, ONLINE_SALARY);
    output.put(BRANCH_NUMBER, getBranchNumber(terms));
    output.put(STATUS, ProcessRequestState.fromEnumToString(state));
    this.requestState = state;
  }

  private Object getBranchNumber(Map<String, Object> terms) throws BpmServiceException
  {
    if (terms.containsKey(BRANCH_NUMBER))
    {
      return terms.get(BRANCH_NUMBER);
    }
    return bpmsServiceRegistry.getCaseService().getVariableById(instanceId, BRANCH_NUMBER);
  }

  private BigDecimal getTotalBalance(Map<String, String> input) throws UseCaseException
  {
    GetLoanInfo getLoanInfo = new GetLoanInfo(bpmsServiceRegistry.getNewCoreBankingService(), bpmsServiceRegistry.getDirectOnlineCoreBankingService(),
        bpmsRepositoryRegistry.getProductRepository());
    final GetLoanInfoOutput output = getLoanInfo.execute(input);
    if (output == null)
    {
      throw new UseCaseException("Failed to download loan account info for cif = " + input.get(CIF_NUMBER));
    }

    return output.getTotalBalance();
  }
}
