package consumption.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.DelegateCaseExecution;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.service.AimServiceRegistry;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.base.model.person.ContactInfo;
import mn.erin.domain.base.usecase.MessageUtil;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.customer.Customer;
import mn.erin.domain.bpm.model.document.Document;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.repository.DocumentRepository;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;
import mn.erin.domain.bpm.usecase.process.CreateProcessRequest;
import mn.erin.domain.bpm.usecase.process.CreateProcessRequestInput;
import mn.erin.domain.bpm.usecase.process.CreateProcessRequestOutput;
import mn.erin.domain.bpm.usecase.process.StartProcess;
import mn.erin.domain.bpm.usecase.process.StartProcessInput;
import mn.erin.domain.bpm.usecase.process.StartProcessOutput;

import static consumption.constant.CamundaVariableConstants.AMOUNT;
import static consumption.constant.CamundaVariableConstants.DISBURSE_AMOUNT;
import static consumption.constant.CamundaVariableConstants.DISBURSE_CURRENCY;
import static consumption.constant.CamundaVariableConstants.GENDER;
import static consumption.constant.CamundaVariableConstants.GENDER_INPUT;
import static consumption.constant.CamundaVariableConstants.GRANT_MINIMUM_AMOUNT;
import static consumption.constant.CamundaVariableConstants.JOBLESS_MEMBERS;
import static consumption.constant.CamundaVariableConstants.LOAN_ACCOUNT_ID;
import static consumption.constant.CamundaVariableConstants.SOL_ID;
import static consumption.constant.CamundaVariableConstants.VALUE_DATE;
import static mn.erin.domain.bpm.BpmMessagesConstants.ONLINE_SALARY_LOG_HASH;
import static mn.erin.domain.bpm.BpmModuleConstants.BNPL_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.BRANCH_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CHANNEL;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CONSUMPTION_LOAN;
import static mn.erin.domain.bpm.BpmModuleConstants.CUSTOMER;
import static mn.erin.domain.bpm.BpmModuleConstants.EMAIL;
import static mn.erin.domain.bpm.BpmModuleConstants.FAMILY_INCOME;
import static mn.erin.domain.bpm.BpmModuleConstants.FAMILY_INCOME_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.FEMALE_MN_VALUE;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.ISO_DATE_FORMATTER_2;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PRODUCT;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PRODUCT_DESCRIPTION;
import static mn.erin.domain.bpm.BpmModuleConstants.MALE_MN_VALUE;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PREVIUOS_EBANK_USER_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.WORK_SPAN;
import static mn.erin.domain.bpm.util.process.BpmUtils.convertDateToDateString;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;
import static mn.erin.domain.bpm.util.process.BpmUtils.removeCommaAndGetBigDecimal;
import static mn.erin.domain.bpm.util.process.DigitalLoanUtils.getProductDescription;

/**
 * @author Lkhagvadorj.A
 **/

public class CamundaUtils
{
  private CamundaUtils()
  {
    FAMILY_INCOME_MAP.put(0, "0 - 488,940");
    FAMILY_INCOME_MAP.put(1, "488,941 - 627,812");
    FAMILY_INCOME_MAP.put(2, "627,813 - 948,608");
    FAMILY_INCOME_MAP.put(3, "948,608+");
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(CamundaUtils.class);
  private static final String PART_TRANSACTION = "PartTransactions";
  private static final String CHARGES = "Charges";
  private static final String CREDIT_ACCOUNT = "creditAccount";
  private static final String ACC_CURRENCY = "AccCurrency";
  private static final String LA_AMOUNT = "laAmount";
  private static final String LA_CURRENCY = "laCurrency";
  private static final String DESCRIPTION = " тоот зээл олгов";
  private static final Map<Integer, String> FAMILY_INCOME_MAP = new HashMap<>();

  public static Map<String, String> toConsumptionLoanProcess(AimServiceRegistry aimServiceRegistry, TenantIdProvider tenantIdProvider,
      BpmsRepositoryRegistry bpmsRepositoryRegistry, BpmsServiceRegistry bpmsServiceRegistry, GroupRepository groupRepository,
      DelegateCaseExecution execution) throws UseCaseException, BpmRepositoryException
  {
    String requestId = createBpmsRequest(aimServiceRegistry, tenantIdProvider, bpmsRepositoryRegistry, groupRepository, execution);
    String instanceId = startProcess(aimServiceRegistry, bpmsServiceRegistry, bpmsRepositoryRegistry, execution, requestId);
    final String onlineSalaryInstanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));
    moveDocuments(onlineSalaryInstanceId, instanceId, bpmsRepositoryRegistry.getDocumentRepository());

    Map<String, String> result = new HashMap<>();
    result.put(PROCESS_REQUEST_ID, requestId);
    result.put(CASE_INSTANCE_ID, instanceId);
    return result;
  }

  public static BigDecimal calculateDisbursementCharge(String settleAmount, String fees)
  {

    return removeCommaAndGetBigDecimal(settleAmount).multiply(new BigDecimal(fees)).divide(BigDecimal.valueOf(100));
  }

  public static String getWsoValueDate()
  {
    return convertDateToDateString(new Date(), ISO_DATE_FORMATTER_2);
  }

  public static Map<String, Object> toLoanDisbursement(String account, String branch, BigDecimal disburseAmount, String disburseCurrency,
      Map<String, Object> partTransactions)
  {
    Map<String, Object> requestBody = new HashMap<>();
    // Олголт хийх зээлийн дансны дугаар
    requestBody.put(LOAN_ACCOUNT_ID, account);
    // Салбарын дугаар
    requestBody.put(SOL_ID, branch);
    // Системийн огноо
    requestBody.put(VALUE_DATE, getWsoValueDate());
    // Олголт хийх дүн
    requestBody.put(DISBURSE_AMOUNT, disburseAmount);
    // Олголт хийх дүнгийн валют
    requestBody.put(DISBURSE_CURRENCY, disburseCurrency);

    requestBody.put(PART_TRANSACTION, Collections.singletonList(partTransactions));
    requestBody.put(CHARGES, Collections.emptyList());
    return requestBody;
  }

  public static Map<String, Object> getDisburseTransactionParam(String creditAccount, String accountCurrency, BigDecimal disburseAmount)
  {
    Map<String, Object> partTransactions = new HashMap<>();
    // Олголт хийгдэх дансны дугаар
    partTransactions.put(CREDIT_ACCOUNT, creditAccount);
    // Олголт хийх дансны валют
    partTransactions.put(ACC_CURRENCY, accountCurrency);
    // Олголтын гүйлгээний дүн
    partTransactions.put(LA_AMOUNT, disburseAmount);
    // Олголтын гүйлгээний дүнгийн валют
    partTransactions.put(LA_CURRENCY, accountCurrency);

    return partTransactions;
  }

  public static Map<String, Object> toTransaction(DelegateExecution execution, String accountId, String accountId2, BigDecimal amount, BigDecimal amount2,
      String transactionType)
  {
    Map<String, Object> variables = execution.getVariables();
    final String loanAccountNumber = String.valueOf(execution.getVariable(LOAN_ACCOUNT_NUMBER));
    String currency = String.valueOf(variables.get("disbursementSttlCcy"));
    String accCurrency = String.valueOf(variables.get("accCurrency"));
    String processType = String.valueOf(execution.getVariable(PROCESS_TYPE_ID));

    List<Map<String, Object>> transactions = new ArrayList<>();
    transactions.add(mapTransaction(accountId2, "D", amount, currency, accCurrency));
    transactions.add(mapTransaction(accountId, "C", amount2, currency, accCurrency));

    Map<String, Object> requestBody = new HashMap<>();

    // Гүйлгээний төрөл
    requestBody.put("TrnType", "T");
    // Гүйлгээний дэд төрөл
    requestBody.put("TrnSubType", "CI");
    // Гүйлгээний утга
    String description = loanAccountNumber + DESCRIPTION;
    if (processType.equals(BNPL_PROCESS_TYPE_ID))
    {
      String messageKey = transactionType.equals("advanceTransaction") ? "bnpl.xac.transaction.description" : "tran.desc.addTransaction.refund";
      description = MessageUtil.getMessageByLocale(messageKey, "mn").getText();
    }
    if (processType.equals(INSTANT_LOAN_PROCESS_TYPE_ID))
    {
      description = MessageUtil.getMessageByLocale("instantLoan.xac.transaction.description", "mn").getText();
    }
    if(processType.equals(ONLINE_LEASING_PROCESS_TYPE_ID)){
      String phoneNumber = String.valueOf(execution.getVariable(PHONE_NUMBER));
      String registerNumber = String.valueOf(execution.getVariable(REGISTER_NUMBER));
      description = loanAccountNumber + "-" + phoneNumber + "-" + registerNumber;
    }
    requestBody.put("TrnParticulars", description);
    requestBody.put("Transactions", transactions);
    requestBody.put(PROCESS_TYPE_ID, processType);

    return requestBody;
  }

  public static void setScoringField(DelegateCaseExecution execution)
  {
    if (execution.getVariable(WORK_SPAN) instanceof Double)
    {
      final long workspan = ((Double) execution.getVariable(WORK_SPAN)).longValue();
      execution.setVariable(WORK_SPAN, workspan);
    }

    String familyIncomeStringValue = String.valueOf(execution.getVariable(FAMILY_INCOME));
    long familyIncome = Long.parseLong(familyIncomeStringValue);

    String familyIncomeString;
    if (familyIncome <= 488940)
    {
      familyIncomeString = FAMILY_INCOME_MAP.get(0);
    }
    else if (familyIncome <= 627812)
    {
      familyIncomeString = FAMILY_INCOME_MAP.get(1);
    }
    else if (familyIncome <= 948608)
    {
      familyIncomeString = FAMILY_INCOME_MAP.get(2);
    }
    else
    {
      familyIncomeString = FAMILY_INCOME_MAP.get(3);
    }

    execution.setVariable(FAMILY_INCOME_STRING, familyIncomeString);

    String genderInput = String.valueOf(execution.getVariable(GENDER)).equals("M") ? MALE_MN_VALUE : FEMALE_MN_VALUE;
    execution.setVariable(GENDER_INPUT, genderInput);

    if (!execution.hasVariable(JOBLESS_MEMBERS))
    {
      execution.setVariable(JOBLESS_MEMBERS, execution.getVariable("jobless_members_string"));
      if (String.valueOf(execution.getVariable(JOBLESS_MEMBERS)).equals("2-с их"))
      {
        execution.removeVariable(JOBLESS_MEMBERS);
        execution.setVariable(JOBLESS_MEMBERS, 3);
      }
    }

    if (!execution.hasVariable("sector"))
    {
      execution.setVariable("sector", execution.getVariable("businessSector"));
    }

    if (!execution.hasVariable(WORK_SPAN))
    {
      execution.setVariable(WORK_SPAN, execution.getVariable("workspan_string"));
      if (String.valueOf(execution.getVariable(WORK_SPAN)).equals("10-с их"))
      {
        execution.removeVariable(WORK_SPAN);
        execution.setVariable(WORK_SPAN, 11);
      }
    }
  }

  private static String createBpmsRequest(AimServiceRegistry aimServiceRegistry, TenantIdProvider tenantIdProvider,
      BpmsRepositoryRegistry bpmsRepositoryRegistry, GroupRepository groupRepository, DelegateCaseExecution execution) throws UseCaseException
  {
    Map<String, Object> variables = execution.getVariables();
    String branchNumber = String.valueOf(variables.get(BRANCH_NUMBER));

    CreateProcessRequestInput input = new CreateProcessRequestInput(branchNumber, PREVIUOS_EBANK_USER_NAME, CONSUMPTION_LOAN);
    CreateProcessRequest createProcessRequest = new CreateProcessRequest(aimServiceRegistry.getAuthenticationService(),
        aimServiceRegistry.getAuthorizationService(), tenantIdProvider, bpmsRepositoryRegistry.getProcessRequestRepository(), groupRepository,
        bpmsRepositoryRegistry.getProcessTypeRepository());
    input.setParameters(getCreateProcessParameter(bpmsRepositoryRegistry, variables));
    CreateProcessRequestOutput output = createProcessRequest.execute(input);
    String requestId = output.getProcessRequestId();
    String onlineSalaryLoanRequestId = String.valueOf(variables.get(PROCESS_REQUEST_ID));
    String cif = String.valueOf(execution.getVariable(CIF_NUMBER));
    LOGGER.info(ONLINE_SALARY_LOG_HASH + " process with request id = [{}] is moved to consumption loan process with request id = [{}]. CIF NUMBER = [{}]",
        onlineSalaryLoanRequestId, requestId, cif);

    return requestId;
  }

  private static String startProcess(AimServiceRegistry aimServiceRegistry, BpmsServiceRegistry bpmsServiceRegistry,
      BpmsRepositoryRegistry bpmsRepositoryRegistry, DelegateCaseExecution execution, String requestId) throws UseCaseException
  {
    Map<String, Object> variables = execution.getVariables();
    variables.put(PROCESS_TYPE_ID, CONSUMPTION_LOAN);
    variables.put(PROCESS_REQUEST_ID, requestId);
    StartProcess startProcess = new StartProcess(aimServiceRegistry.getAuthenticationService(), aimServiceRegistry.getAuthorizationService(),
        bpmsRepositoryRegistry.getProcessRequestRepository(), bpmsServiceRegistry.getProcessTypeService(), bpmsRepositoryRegistry.getProcessRepository(),
        bpmsServiceRegistry.getCaseService());

    StartProcessInput input = new StartProcessInput(requestId);
    input.setParameters(variables);
    StartProcessOutput output = startProcess.execute(input);
    return output.getProcessInstanceId();
  }

  private static void moveDocuments(String onlineSalaryInstanceId, String consumptionInstanceId,
      DocumentRepository documentRepository) throws BpmRepositoryException
  {
    final Collection<Document> documents = documentRepository.findByProcessInstanceId(onlineSalaryInstanceId);
    documentRepository.delete(onlineSalaryInstanceId);
    for (Document document : documents)
    {
      documentRepository.create(document.getDocumentId().getId(), document.getDocumentInfoId().getId(), consumptionInstanceId, document.getName(),
          document.getCategory(), document.getSubCategory(), document.getReference(), document.getSource());
    }
  }

  private static Map<String, Serializable> getCreateProcessParameter(BpmsRepositoryRegistry bpmsRepositoryRegistry,
      Map<String, Object> variables) throws UseCaseException
  {
    Map<String, Serializable> parameters = new HashMap<>();

    String productId = String.valueOf(variables.get(LOAN_PRODUCT));
    String productDescription = getProductDescription(bpmsRepositoryRegistry.getProductRepository(), productId);

    if (null != productDescription)
    {
      parameters.put(LOAN_PRODUCT_DESCRIPTION, productDescription);
    }
    parameters.put(LOAN_PRODUCT, productId);
    parameters.put(BRANCH_NUMBER, (Serializable) variables.get(BRANCH_NUMBER));
    parameters.put(REGISTER_NUMBER, (Serializable) variables.get(REGISTER_NUMBER));
    parameters.put(CIF_NUMBER, (Serializable) variables.get(CIF_NUMBER));
    parameters.put(CHANNEL, "Internet bank");
    parameters.put(AMOUNT, (Serializable) variables.get(GRANT_MINIMUM_AMOUNT));

    if (variables.containsKey(CUSTOMER))
    {
      Map<String, String> contactInfo = getContactInfo((Customer) variables.get(CUSTOMER));
      if (!contactInfo.isEmpty())
      {
        parameters.put(PHONE_NUMBER, contactInfo.get(PHONE_NUMBER));
        parameters.put(EMAIL, contactInfo.get(EMAIL));
      }
    }

    return parameters;
  }

  private static Map<String, String> getContactInfo(Customer customer)
  {
    List<ContactInfo> contactInfoList = customer.getContactInfoList();
    if (null == contactInfoList || contactInfoList.isEmpty())
    {
      return Collections.emptyMap();
    }
    StringBuilder phoneNumber = new StringBuilder();
    StringBuilder email = new StringBuilder();
    for (ContactInfo contactInfo : contactInfoList)
    {
      if (!StringUtils.isBlank(contactInfo.getPhone()))
      {
        phoneNumber.append(contactInfo.getPhone());
      }

      if (!StringUtils.isBlank(contactInfo.getEmail()))
      {
        email.append(contactInfo.getEmail());
      }
    }

    Map<String, String> parameter = new HashMap<>();
    parameter.put(PHONE_NUMBER, phoneNumber.toString());
    parameter.put(EMAIL, email.toString());
    return parameter;
  }

  private static Map<String, Object> mapTransaction(String accountId, String crDr, BigDecimal amount, String currency, String accCurrency)
  {
    Map<String, Object> map = new HashMap<>();
    // Дансны дугаар
    map.put("AcctId", accountId);
    // Гүйлгээний бичилтийн төрөл:
    map.put("CrDr", crDr);
    // Гүйлгээний дүн
    map.put("Amount", amount.setScale(2, RoundingMode.HALF_UP));
    // Гүйлгээний вальют
    map.put("Currency", currency);
    // Дансны вальют
    map.put(ACC_CURRENCY, accCurrency);
    map.put("Eventid", "");

    return map;
  }

  public static Date calculatedFirstPaymentDate(int dayOfPayment)
  {

    LocalDate systemDate = LocalDate.now();
    LocalDate paymentDate = setPaymentDate(systemDate, 1, dayOfPayment);
    int daysBetween = (int) ChronoUnit.DAYS.between(systemDate, paymentDate);

    LocalDate firstPaymentLocalDate = daysBetween > 15 ? paymentDate : setPaymentDate(systemDate, 2, dayOfPayment);
    Date firstPaymentDate = Date.from(firstPaymentLocalDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

    LOGGER.info(ONLINE_SALARY_LOG_HASH + "CALCULATED FIRST PAYMENT DATE: [{}]", firstPaymentDate);
    return firstPaymentDate;
  }

  private static LocalDate setPaymentDate(LocalDate systemDate, int plusMonths, int dayOfPayment)
  {
    if (systemDate.getMonthValue() == 12 || (systemDate.getMonthValue() == 11 && plusMonths == 2))
    {
      return LocalDate.of(systemDate.plusYears(1).getYear(), systemDate.getMonth().plus(plusMonths), dayOfPayment);
    }

    return LocalDate.of(systemDate.getYear(), systemDate.getMonth().plus(plusMonths), dayOfPayment);
  }

  public static Date getFirstPaymentDate()
  {
    Date today = getLoanGrantDate();
    int month = today.getMonth() + 1;
    today.setMonth(month);
    return today;
  }

  public static void updateTaskStatus(DelegateExecution execution, String taskName, String status)
  {
    Map<String, String> completedTasksMap = new HashMap<>();
    List<Map<String, String>> completedTasksList;
    int index;

    if (null != execution.getVariable("instantLoanTaskIndex"))
    {
      index = Integer.parseInt(getValidString(execution.getVariable("instantLoanTaskIndex")));
      completedTasksList = (List<Map<String, String>>) execution.getVariable("completedTasks");
    }
    else {
      index = -1;
      completedTasksList = new ArrayList<>();
    }

    completedTasksMap.put(taskName, status);
    completedTasksList.add(index + 1, completedTasksMap);

    execution.setVariable("completedTasks", completedTasksList);
    execution.setVariable("instantLoanTaskIndex", index + 1);
  }

  private static Date getLoanGrantDate()
  {
    Date today = new Date();
    today.setHours(0);
    return today;
  }
}
