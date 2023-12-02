package mn.erin.domain.bpm.usecase.bnpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
import mn.erin.domain.bpm.BpmModuleConstants;
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
import mn.erin.domain.bpm.service.NewCoreBankingService;
import mn.erin.domain.bpm.service.RuntimeService;
import mn.erin.domain.bpm.usecase.process.GetProcessTypesByCategory;
import mn.erin.domain.bpm.usecase.process.GetProcessTypesByCategoryOutput;

import static mn.erin.domain.bpm.BpmMessagesConstants.BNPL_LOG;
import static mn.erin.domain.bpm.BpmModuleConstants.BNPL;
import static mn.erin.domain.bpm.BpmModuleConstants.BNPL_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.BNPL_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.BRANCH_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.EXPIRE_DATE;
import static mn.erin.domain.bpm.BpmModuleConstants.INVOICE_AMOUNT_75;
import static mn.erin.domain.bpm.BpmModuleConstants.ISO_DATE_FORMAT;
import static mn.erin.domain.bpm.BpmModuleConstants.MAX_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.AMOUNT_REJECTED;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.ORG_REJECTED;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.SCORING_REJECTED;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.SYSTEM_FAILED;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.fromEnumToString;
import static mn.erin.domain.bpm.util.process.BpmUtils.convertDateToDateString;

public class GetBnplRequestStatus extends AbstractUseCase<String, Map<String, Object>>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GetBnplRequestStatus.class);

  public static final String STATUS = "Status";

  private final BpmsServiceRegistry bpmsServiceRegistry;
  private final BpmsRepositoryRegistry bpmsRepositoryRegistry;
  private final Environment environment;
  private final NewCoreBankingService newCoreBankingService;
  private ProcessRequestState requestState;
  private String instanceId;
  public static final String PROCESS_TYPE_ID = "ProcessTypeId";

  public GetBnplRequestStatus(BpmsServiceRegistry bpmsServiceRegistry,
      BpmsRepositoryRegistry bpmsRepositoryRegistry, Environment environment, NewCoreBankingService newCoreBankingService)
  {
    this.bpmsRepositoryRegistry = bpmsRepositoryRegistry;
    this.bpmsServiceRegistry = bpmsServiceRegistry;
    this.environment = environment;
    this.newCoreBankingService = newCoreBankingService;
  }

  @Override
  public Map<String, Object> execute(String cifNumber) throws UseCaseException
  {
    Map<String, Object> output = new HashMap<>();
    boolean isOverdue;
    try
    {
      ProcessRequestRepository processRequestRepository = bpmsRepositoryRegistry.getProcessRequestRepository();

      LocalDateTime endDate = LocalDateTime.now(ZoneId.of("UTC+8"));
      LocalDateTime startDate = endDate.minusHours(48);
      update48HoursPassedProcessRequests(processRequestRepository, bpmsRepositoryRegistry.getProcessRepository(), bpmsServiceRegistry.getRuntimeService(),
          cifNumber, startDate, endDate);

      Collection<ProcessRequest> processRequests = processRequestRepository.getDirectOnlineProcessRequests(cifNumber, startDate, endDate, getProcessType());

      Map<String, String> input = new HashMap<>();
      if (!processRequests.isEmpty()){
        ProcessRequest lastProcessRequest = Collections.max(processRequests, Comparator.comparing(ProcessRequest::getCreatedTime));
        input.put(PHONE_NUMBER, bpmsRepositoryRegistry.getProcessRequestRepository().getParameterByName(lastProcessRequest.getId().getId(), PHONE_NUMBER));
      }
      input.put(BpmModuleConstants.PROCESS_TYPE_ID, BNPL_PROCESS_TYPE_ID);
      input.put(CIF_NUMBER, cifNumber);

      isOverdue = checkAccount(output, input);
      if (isOverdue)
      {
        output.put(STATUS, ProcessRequestState.OVERDUE);
      }
      else
      {
        assignProcessTypeAndStatus(processRequests, output);
      }
      LOGGER.info(BNPL_LOG + "SUCCESSFULLY GETS THE OUTPUT = [{}], WITH CIF NUMBER = [{}]", output, cifNumber);
    }
    catch (BpmRepositoryException | BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
    return output;
  }

  private void update48HoursPassedProcessRequests(ProcessRequestRepository processRequestRepository, ProcessRepository processRepository,
      RuntimeService runtimeService,
      String cifNumber, LocalDateTime startDate, LocalDateTime endDate) throws UseCaseException, BpmRepositoryException
  {
    String processType = getProcessType();
    Collection<ProcessRequest> get48hPassedRequests = processRequestRepository.getBnplGivenTimePassedProcessRequests(processType, cifNumber, startDate,
        endDate);

    for (ProcessRequest processRequest : get48hPassedRequests)
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

    int updatedRequestCount = processRequestRepository.update24hPassedProcessState(cifNumber, startDate, endDate, processType,
        ProcessRequestState.DELETED.name());
    LOGGER.info("Updated state of [{}] process requests that are passed 48 hours to DELETED with cif number: [{}].", updatedRequestCount, cifNumber);
  }

  private void assignProcessTypeAndStatus(Collection<ProcessRequest> processRequests, Map<String, Object> output)
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
        setTypeAndStatusToMap(output, ProcessRequestState.NEW);
        return;
      case DELETED:
        setTypeAndStatusToMap(output, ProcessRequestState.DELETED);
        return;
      case STARTED:
        setTypeAndStatusToMap(output, ProcessRequestState.STARTED, terms);
        return;
      case CONFIRMED:
        assignTerms(output, terms);
        setTypeAndStatusToMap(output, ProcessRequestState.CONFIRMED);
        output.put(MAX_AMOUNT, terms.get(BNPL_LOAN_AMOUNT));
        output.put(EXPIRE_DATE, changeDateTimeFormat(processRequest.getCreatedTime().plusHours(48)));
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
      default:
        output.put(STATUS, fromEnumToString(ProcessRequestState.NEW));
        return;
      }
    }

    output.put(STATUS, fromEnumToString(ProcessRequestState.NEW));
    LOGGER.info(BNPL_LOG + " PROCESS TYPE AND STATUS'S ARE ASSIGNED WITH PROCESS REQUESTS = [{}], OUTPUT = [{}]", processRequests, output);
  }

  private String changeDateTimeFormat(LocalDateTime plusHours)
  {
    return plusHours.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
  }

  @SuppressWarnings("unchecked")
  private void assignTerms(Map<String, Object> output, Map<String, Object> terms)
  {
    LOGGER.info(BNPL_LOG
            + "ASSIGNING TERMS WITH MAX AMOUNT = [{}], BRANCH NUMBER = [{}]",
        terms.get(BNPL_LOAN_AMOUNT), getBranchNumber(terms));
    output.put(BRANCH_NUMBER, getBranchNumber(terms));
  }

  private void setTypeAndStatusToMap(Map<String, Object> output, ProcessRequestState state)
  {
    Date date = new Date();
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.DATE, 45);

    String loanMaturityDate = convertDateToDateString(calendar.getTime(), ISO_DATE_FORMAT);

    LOGGER.info(BNPL_LOG + "STATE = [{}]", state);
    output.put(PROCESS_TYPE_ID, BNPL);
    output.put(STATUS, fromEnumToString(state));
    if (state.equals(ProcessRequestState.PROCESSING))
    {
      BigDecimal invoiceAmount75 = new BigDecimal(String.valueOf(bpmsServiceRegistry.getRuntimeService().getVariableById(instanceId, INVOICE_AMOUNT_75)));
      output.put("loanAmount", invoiceAmount75);
      output.put("loanMaturity", loanMaturityDate);
    }
    requestState = state;
  }

  private void setTypeAndStatusToMap(Map<String, Object> output, ProcessRequestState state, Map<String, Object> terms)
  {
    output.put(PROCESS_TYPE_ID, BNPL);
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

  private boolean checkAccount(Map<String, Object> output, Map<String, String> input) throws BpmServiceException
  {
    Map<String, Object> customerMap = newCoreBankingService.findCustomerByCifNumber(input);
    input.put(REGISTER_NUMBER, (String) customerMap.get(REGISTER_NUMBER));
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
    LOGGER.info(BNPL_LOG + "ASSIGNING ACCOUNTS");
  }

  private String getProcessType() throws UseCaseException
  {
    GetProcessTypesByCategory getProcessTypesByCategory = new GetProcessTypesByCategory(bpmsRepositoryRegistry.getProcessTypeRepository());
    GetProcessTypesByCategoryOutput output = getProcessTypesByCategory.execute(BNPL);
    List<ProcessType> processTypes = output.getProcessTypes();
    return String.valueOf(processTypes.get(0).getId().getId());
  }
}
