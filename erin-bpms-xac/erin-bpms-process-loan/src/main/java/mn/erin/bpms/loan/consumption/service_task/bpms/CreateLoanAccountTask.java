package mn.erin.bpms.loan.consumption.service_task.bpms;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.runtime.CaseExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants;
import mn.erin.bpms.loan.consumption.loan_amount_calculation.LoanCyclePlan;
import mn.erin.bpms.loan.consumption.loan_amount_calculation.MonthlyLoanCycle;
import mn.erin.bpms.process.base.ProcessTaskException;
import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.account.UDField;
import mn.erin.domain.bpm.service.CoreBankingService;
import mn.erin.domain.bpm.service.NewCoreBankingService;
import mn.erin.domain.bpm.usecase.customer.GetCustomerAccountCreationInfo;
import mn.erin.domain.bpm.usecase.customer.GetCustomerAccountCreationInfoOutput;
import mn.erin.domain.bpm.usecase.customer.GetUDFieldsByProductCode;
import mn.erin.domain.bpm.usecase.customer.GetUDFieldsByProductCodeOutput;
import mn.erin.domain.bpm.usecase.loan.CreateLoanAccount;
import mn.erin.domain.bpm.usecase.loan.CreateLoanAccountInput;

import static mn.erin.bpms.loan.consumption.service_task.bpms.co_borrower.CoBorrowerUtils.INDEX_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ACCEPTED_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.FULL_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.FULL_NAME_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PRODUCT_CODE;

/**
 * @author Zorig
 */
public class CreateLoanAccountTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(CreateLoanAccountTask.class);
  private final CoreBankingService coreBankingService;
  private final NewCoreBankingService newCoreBankingService;
  private final AuthenticationService authenticationService;
  private final MembershipRepository membershipRepository;

  private static final String SCORING_SCORE = "score";

  public CreateLoanAccountTask(NewCoreBankingService newCoreBankingService, CoreBankingService coreBankingService,
      AuthenticationService authenticationService,
      MembershipRepository membershipRepository)
  {
    this.newCoreBankingService = newCoreBankingService;
    this.coreBankingService = coreBankingService;
    this.authenticationService = authenticationService;
    this.membershipRepository = membershipRepository;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    if (execution.getVariable("isLoanAccountCreate") == null)
    {
      return;
    }
    boolean isLoanAccountCreate = (boolean) execution.getVariable("isLoanAccountCreate");
    if (!isLoanAccountCreate)
    {
      if (execution.getVariable("loanAccountNumber") == null)
      {
        String errorCode = "CBS014";
        throw new ProcessTaskException(errorCode, "Unable to continue without creating account!");
      }

      // manually start generate loan decision process.
      String caseInstanceId = (String) execution.getVariable(CASE_INSTANCE_ID);

      CaseService caseService = execution.getProcessEngine().getCaseService();
      List<CaseExecution> enabledExecutions = caseService.createCaseExecutionQuery()
          .caseInstanceId(caseInstanceId)
          .enabled()
          .list();

      for (CaseExecution enabledExecution : enabledExecutions)
      {
        String activityId = enabledExecution.getActivityId();

        // elementary criteria execution activity id.
        if (CamundaActivityIdConstants.ACTIVITY_ID_GENERATE_LOAN_DECISION.equalsIgnoreCase(activityId))
        {
          caseService.manuallyStartCaseExecution(enabledExecution.getId());
        }
      }

      return;
    }

    if (execution.getVariable("loanAccountNumber") != null)
    {
      String errorCode = "CBS015";
      throw new ProcessTaskException(errorCode, "Loan Account has already been created!");
    }

    String registrationNumber = (String) execution.getVariable("registerNumber");
    String requestId = (String) execution.getVariable(PROCESS_REQUEST_ID);
    String userId = authenticationService.getCurrentUserId();
    LOGGER.info("#########  Setting Account Creation Fields.. Register Number: " + registrationNumber + ", Request ID: " + requestId + " , User ID: " + userId);

    String loanProduct = (String) execution.getVariable("loanProduct");
    String productCode = getProductCode(loanProduct);

    String attentiveLoan = (String) execution.getVariable("attentiveLoan");
    String firstAccountNumber = (String) execution.getVariable("firstAccountNumber");
    String supplier1 = (String) execution.getVariable("supplier1");
    Date firstDisbursedDate = (Date) execution.getVariable("firstDisbursedDate");
    String lateReasonAttention = (String) execution.getVariable("lateReasonAttention");
    String restructuredNumber = (String) execution.getVariable("restructuredNumber");

    String supplier2 = (String) execution.getVariable("supplier2");
    String supplier3 = (String) execution.getVariable("supplier3");

    String grantLoanAmount = null;
    BigDecimal grantLoanAmountBD = null;

    if (execution.getVariable(FIXED_ACCEPTED_LOAN_AMOUNT) instanceof Integer)
    {
      grantLoanAmountBD = BigDecimal.valueOf((Integer) execution.getVariable(FIXED_ACCEPTED_LOAN_AMOUNT));
      grantLoanAmount = grantLoanAmountBD.toString();
    }
    else
    {
      grantLoanAmountBD = BigDecimal.valueOf((long) execution.getVariable(FIXED_ACCEPTED_LOAN_AMOUNT));
      grantLoanAmount = grantLoanAmountBD.toString();
    }

    String numberOfPayments = (String) execution.getVariable("numberOfPayments");
    double interestRate = (double) execution.getVariable("interest_rate");
    BigDecimal yearlyInterestRate = (BigDecimal) execution.getVariable("yearlyInterestRate");

    String accountBranchNumber = (String) execution.getVariable("accountBranchNumber");
    String currentAccountNumber = (String) execution.getVariable("currentAccountNumber");
    String frequency = (String) execution.getVariable("frequency");
    BigDecimal depositInterestRate = (BigDecimal) execution.getVariable("depositInterestRate");
    String dayOfPayment = (String) execution.getVariable("dayOfPayment");
    String fees = String.valueOf(execution.getVariable("fees"));
    Date firstPaymentDate = (Date) execution.getVariable("firstPaymentDate");//camunda gets this
    String loanPurpose = (String) execution.getVariable("loanPurpose");
    String businessTypeReason = (String) execution.getVariable("businessTypeReason");
    String worker = (String) execution.getVariable("worker");
    String sanctionedBy = (String) execution.getVariable("sanctionedBy");
    String subType = (String) execution.getVariable("subType");
    String insuranceCompanyInfo = (String) execution.getVariable("insuranceCompanyInfo");
    String lateReason = (String) execution.getVariable("lateReason");
    String loanCycle = (String) execution.getVariable("loanCycle");
    String schoolNameAndInstitution = (String) execution.getVariable("schoolNameAndInstitution");
    String schoolName = (String) execution.getVariable("schoolName");

    String branch = getBranch();
    execution.setVariable("branch", branch);

    String score = getScore(execution);

    String scoreLevel = (String) execution.getVariable("scoring_level_risk");

    String cif = (String) execution.getVariable("cifNumber");

    String surguuli = (String) execution.getVariable("surguuli");

    Map<String, Object> accountCreationInformation = new HashMap<>();

    GetCustomerAccountCreationInfoOutput output = getAccountCreationInformation(productCode);
    Map<String, String> input = new HashMap<>();
    input.put(PROCESS_TYPE_ID, String.valueOf(execution.getVariable(PROCESS_TYPE_ID)));
    input.put(PHONE_NUMBER, String.valueOf(execution.getVariable(PHONE_NUMBER)));
    input.put(PRODUCT_CODE, String.valueOf(execution.getVariable(PRODUCT_CODE)));
    GetUDFieldsByProductCodeOutput getUDFieldsByProductCodeOutput = getUDFieldByProductCode(input);

    LOGGER.info("############ GETS ACCOUNT CREATION INFO (UD Fields ... ) by PRODUCT CODE = [{}] with REQUEST ID = [{}]", productCode, requestId);

    accountCreationInformation
        .put("SURGUULI", getUDFieldValueId("SURGUULI", surguuli, getUDFieldsByProductCodeOutput));

    accountCreationInformation.put("SURGUULIIN NER/BAIGUULLAGA", getUDFieldId("schoolNameAndInstitution", schoolNameAndInstitution, output));
    accountCreationInformation.put("SURGUULI", getUDFieldId("schoolName", schoolName, output));
    accountCreationInformation.put("LOAN_PURPOSE", getUDFieldId("loanPurpose", loanPurpose, output));
    accountCreationInformation.put("REASON_OVERDUE_ANHAARAL_TAT", getUDFieldId("lateReasonAttention", lateReasonAttention, output));
    accountCreationInformation.put("FIRST_ACCOUNT_NUMBER", firstAccountNumber);
    accountCreationInformation.put("RESTRUCTURED_NUMBER", getUDFieldId("restructuredNumber", restructuredNumber, output));
    accountCreationInformation.put("ANHAARAL_TATSAN_ZEEL", getUDFieldId("attentiveLoan", attentiveLoan, output));

    accountCreationInformation.put("FIRST_DISBURSED_DATE", firstDisbursedDate);
    accountCreationInformation.put("SUPPLIERS_FINANCE_LEASING", getUDFieldId("firstSupplier", supplier1, output));
    accountCreationInformation.put("SANCTIONED_BY", sanctionedBy);
    accountCreationInformation.put("LOAN_PURPOSE1", getUDFieldId("businessTypeReason", businessTypeReason, output));
    accountCreationInformation.put(output.getSubType().getId().getId(), getUDFieldId("subType", subType, output));

    accountCreationInformation.put("DETAILINFO_INSURANCE_COMPANY", getUDFieldId("insuranceCompanyInfo", insuranceCompanyInfo, output));
    accountCreationInformation.put("SUPPLIERS_FINANCE_LEASING1", getUDFieldId("secondSupplier", supplier2, output));
    accountCreationInformation.put("SUPPLIERS_FINANCE_LEASING2", getUDFieldId("thirdSupplier", supplier3, output));
    accountCreationInformation.put("REASON_OVERDUE", getUDFieldId("lateReason", lateReason, output));
    accountCreationInformation.put("LOAN_CYCLE", getUDFieldId("loanCycle", loanCycle, output));
    accountCreationInformation.put("INSSTDT", firstPaymentDate);

    accountCreationInformation.put("CREDIT_OFFICER", getUDFieldIdWorker(worker));
    accountCreationInformation.put("CURRENT_ACCOUNT_NUMBER", currentAccountNumber);

    accountCreationInformation.put("CREDIT_SCORING_RATING", score);
    accountCreationInformation.put("CREDIT_SCORING_ID", scoreLevel);

    accountCreationInformation.put("INTEREST_RATE", yearlyInterestRate.toString());

    validateDayOfPayment(dayOfPayment);
    accountCreationInformation.put("DUEDATESON", dayOfPayment);

    try
    {
      BigDecimal feesBD = new BigDecimal(fees);
      if (feesBD.compareTo(BigDecimal.ZERO) == -1 || feesBD.compareTo(BigDecimal.valueOf(100)) == 1)
      {
        throw new Exception();
      }

      execution.setVariable("feesPercentage", fees + "%");
    }
    catch (Exception e)
    {
      String errorCode = "BPMS077";
      throw new ProcessTaskException(errorCode, "Invalid fees percentage!");
    }

    validateFees(fees);
    execution.setVariable("feesPercentage", fees + "%");

    accountCreationInformation.put("PRO_CHARGE_RATE", fees);
    accountCreationInformation.put("PENALTY_RATE", depositInterestRate.toString());

    accountCreationInformation.put("AMTFINANCED", grantLoanAmount);
    accountCreationInformation.put("BRN", branch);
    accountCreationInformation.put("CUSTID", cif);

    Date dateNow = new Date();
    accountCreationInformation.put("VALDT", dateNow);
    accountCreationInformation.put("CCY", "MNT");
    accountCreationInformation.put("MATTYP", "F");
    accountCreationInformation.put("NOOFINS", numberOfPayments);
    accountCreationInformation.put("FREQ", frequency);
    accountCreationInformation.put("FREQUNIT", "M");

    accountCreationInformation.put("CRACCBRN", accountBranchNumber);
    accountCreationInformation.put("CRPRODACC", currentAccountNumber);
    accountCreationInformation.put("DRACCBRN", accountBranchNumber);
    accountCreationInformation.put("DRPRODACC", currentAccountNumber);

    Date finishedDate = getLastPaymentDate(firstPaymentDate, dateNow, grantLoanAmountBD, Integer.valueOf(numberOfPayments),
        BigDecimal.valueOf(interestRate).multiply(BigDecimal.valueOf(12)));

    accountCreationInformation.put("MATDT", finishedDate);

    List<Map<String, String>> coBorrowers = getCoBorrowers(execution, accountCreationInformation, grantLoanAmountBD);

    int accountNumber = createLoanAccount(productCode, accountCreationInformation, coBorrowers);

    LOGGER.info("CREATED ACCOUNT NUMBER: " + accountNumber);

    execution.setVariable("loanAccountNumber", String.valueOf(accountNumber));

    //  disables all enabled processes.
    if (null != accountBranchNumber)
    {
      disableCoBorrowerExecutions(execution);
    }

    LOGGER.info("######### Finished Creating Loan Account...");
  }

  private List<Map<String, String>> getCoBorrowers(DelegateExecution execution, Map<String, Object> accountCreationInformation, BigDecimal grantLoanAmountBD) throws ProcessTaskException
  {
    Integer indexCoBorrower = (Integer) execution.getVariable(INDEX_CO_BORROWER);

    List<Map<String, String>> coBorrowers = new ArrayList<>();

    if (null != indexCoBorrower)
    {
      for (int index = 1; index <= indexCoBorrower; index++)
      {
        Map<String, String> currentCoBorrower = new HashMap<>();
        String cifCB = (String) execution.getVariable(CIF_NUMBER_CO_BORROWER + "-" + index);
        String fullNameCB = null;

        if (execution.getVariable(FULL_NAME_CO_BORROWER + "-" + index) == null)
        {
          fullNameCB = "";
        }
        else
        {
          fullNameCB = (String) execution.getVariable(FULL_NAME_CO_BORROWER + "-" + index);
        }

        currentCoBorrower.put("cifCB", cifCB);
        currentCoBorrower.put("fullNameCB", fullNameCB);

        LOGGER.info("Coborrower " + index + " CIF: " + cifCB);
        LOGGER.info("Coborrower " + index + " Name: " + fullNameCB);

        coBorrowers.add(currentCoBorrower);
      }

      String customerName = (String) execution.getVariable(FULL_NAME);
      accountCreationInformation.put("customerName", customerName);

      String liability = getLiability(indexCoBorrower);
      BigDecimal liabilityAmount = grantLoanAmountBD.divide(BigDecimal.valueOf(indexCoBorrower + 1), 2, RoundingMode.HALF_UP).setScale(2);

      accountCreationInformation.put("LIABILITY", liability);
      accountCreationInformation.put("LIABILITYAMT", liabilityAmount.toString());
    }

    return coBorrowers;
  }

  private GetUDFieldsByProductCodeOutput getUDFieldByProductCode(Map<String, String> input) throws UseCaseException
  {
    GetUDFieldsByProductCode getUDFieldsByProductCode = new GetUDFieldsByProductCode(newCoreBankingService);
    return getUDFieldsByProductCode.execute(input);
  }

  private String getUDFieldValueId(String udFieldId, String udfieldValueDescription, GetUDFieldsByProductCodeOutput output)
  {
    Map<String, UDField> udFieldMap = output.getUdFieldsMap();
    UDField udField = udFieldMap.get(udFieldId);

    if (udField == null)
    {
      return null;
    }

    return udField.getFieldValueIdByDescription(udfieldValueDescription);
  }

  private String getLiability(int indexCoBorrower) throws ProcessTaskException
  {
    int totalBorrowers = indexCoBorrower + 1;

    if (totalBorrowers > 10)
    {
      throw new ProcessTaskException("Total borrowers over 10 not allowed!");
    }

    if (totalBorrowers == 3)
    {
      return "33.(3)";
    }
    else if (totalBorrowers == 6)
    {
      return "16.(6)";
    }
    else if (totalBorrowers == 7)
    {
      return "14.(285714)";
    }
    else if (totalBorrowers == 9)
    {
      return "11.(1)";
    }
    else
    {
      BigDecimal liability = BigDecimal.valueOf(100).divide(BigDecimal.valueOf(totalBorrowers), 2, RoundingMode.HALF_UP).setScale(2);
      return liability.toString();
    }
  }

  private void disableCoBorrowerExecutions(DelegateExecution execution)
  {
    ProcessEngine processEngine = execution.getProcessEngine();

    CaseService caseService = processEngine.getCaseService();

    String caseInstanceId = (String) execution.getVariable(CASE_INSTANCE_ID);

    if (null != caseInstanceId)
    {
      List<CaseExecution> enabledExecutions = caseService.createCaseExecutionQuery()
          .caseInstanceId(caseInstanceId)
          .enabled()
          .list();

      for (CaseExecution enabledExecution : enabledExecutions)
      {
        String activityId = enabledExecution.getActivityId();

        // co-borrower processes.
        if (activityId.equalsIgnoreCase("PlanItem_1voieh9")
            || activityId.equalsIgnoreCase("PlanItem_0xpnjj9")
            || activityId.equalsIgnoreCase("PlanItem_1ouvsp3")
            || activityId.equalsIgnoreCase("process_task_mortgage_download_khur_coborrower")
            || activityId.equalsIgnoreCase("process_task_mortgage_mongol_bank_coborrower"))
        {
          caseService.disableCaseExecution(enabledExecution.getId());
        }
      }
    }
  }

  private String getBranch() throws UseCaseException
  {
    try
    {
      String currentUserId = authenticationService.getCurrentUserId();
      List<Membership> membershipList = membershipRepository.listAllByUserId(TenantId.valueOf("xac"), UserId.valueOf(currentUserId));
      Membership membership = membershipList.get(0);
      String branch = membership.getGroupId().getId();

      return branch;
    }
    catch (AimRepositoryException e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }

  private Date getLastPaymentDate(Date firstPaymentDate, Date loanStartDate, BigDecimal requestedLoanAmount, int loanLength, BigDecimal yearlyInterestRate)
  {
    LoanCyclePlan loanCyclePlan = new LoanCyclePlan(firstPaymentDate, loanStartDate, requestedLoanAmount, loanLength, yearlyInterestRate);
    loanCyclePlan.setUpLoanCyclePlanEqualTotalPayment();
    List<MonthlyLoanCycle> loanCycles = loanCyclePlan.getMonthlyLoanCycles();
    int lastCycleIndex = loanCycles.size() - 1;

    MonthlyLoanCycle monthlyLoanCycle = loanCycles.get(lastCycleIndex);

    return monthlyLoanCycle.getScheduledDay();
  }

  private String getUDFieldIdWorker(String udFieldDescription)
  {
    if (udFieldDescription == null)
    {
      return null;
    }
    int splitIndex = udFieldDescription.indexOf(" ");

    return udFieldDescription.substring(0, splitIndex);
  }

  private String getUDFieldId(String udFieldName, String udFieldDescription, GetCustomerAccountCreationInfoOutput output)
  {
    if (null == output)
    {
      return null;
    }

    if (udFieldName.equals("restructuredNumber"))
    {
      if (null == output.getRestructuredNumber())
      {
        return null;
      }
      return output.getRestructuredNumber().getFieldValueIdByDescription(udFieldDescription);
    }
    else if (udFieldName.equals("lateReasonAttention"))
    {
      if (null == output.getLateReasonAttention())
      {
        return null;
      }
      return output.getLateReasonAttention().getFieldValueIdByDescription(udFieldDescription);
    }
    else if (udFieldName.equals("firstAccountNumber"))
    {
      if (null == output.getFirstAccountNumber())
      {
        return null;
      }
      return output.getFirstAccountNumber().getFieldValueIdByDescription(udFieldDescription);
    }
    else if (udFieldName.equals("attentiveLoan"))
    {
      if (null == output.getAttentiveLoan())
      {
        return null;
      }
      return output.getAttentiveLoan().getFieldValueIdByDescription(udFieldDescription);
    }
    else if (udFieldName.equals("firstDisbursedLoanDate"))
    {
      if (null == output.getFirstDisbursedLoanDate())
      {
        return null;
      }
      return output.getFirstDisbursedLoanDate().getFieldValueIdByDescription(udFieldDescription);
    }
    else if (udFieldName.equals("firstSupplier"))
    {
      if (null == output.getFirstSupplier())
      {
        return null;
      }
      return output.getFirstSupplier().getFieldValueIdByDescription(udFieldDescription);
    }
    else if (udFieldName.equals("loanPurpose"))
    {
      if (null == output.getLoanPurpose())
      {
        return null;
      }
      return output.getLoanPurpose().getFieldValueIdByDescription(udFieldDescription);
    }
    else if (udFieldName.equals("businessTypeReason"))
    {
      if (null == output.getBusinessTypeReason())
      {
        return null;
      }
      return output.getBusinessTypeReason().getFieldValueIdByDescription(udFieldDescription);
    }
    else if (udFieldName.equals("sanctionedBy"))
    {
      if (null == output.getSanctionedBy())
      {
        return null;
      }
      return output.getSanctionedBy().getFieldValueIdByDescription(udFieldDescription);
    }
    else if (udFieldName.equals("subType"))
    {
      if (null == output.getSubType())
      {
        return null;
      }
      return output.getSubType().getFieldValueIdByDescription(udFieldDescription);
    }
    else if (udFieldName.equals("insuranceCompanyInfo"))
    {
      if (null == output.getInsuranceCompanyInfo())
      {
        return null;
      }
      return output.getInsuranceCompanyInfo().getFieldValueIdByDescription(udFieldDescription);
    }
    else if (udFieldName.equals("secondSupplier"))
    {
      if (null == output.getSecondSupplier())
      {
        return null;
      }
      return output.getSecondSupplier().getFieldValueIdByDescription(udFieldDescription);
    }
    else if (udFieldName.equals("thirdSupplier"))
    {
      if (null == output.getThirdSupplier())
      {
        return null;
      }
      return output.getThirdSupplier().getFieldValueIdByDescription(udFieldDescription);
    }
    else if (udFieldName.equals("lateReason"))
    {
      if (null == output.getLateReason())
      {
        return null;
      }
      return output.getLateReason().getFieldValueIdByDescription(udFieldDescription);
    }
    else if (udFieldName.equals("loanCycle"))
    {
      if (null == output.getLoanCycle())
      {
        return null;
      }
      return output.getLoanCycle().getFieldValueIdByDescription(udFieldDescription);
    }
    else if (udFieldName.equals("schoolNameAndInstitution"))
    {
      if (null == output.getSchoolNameAndInstitution())
      {
        return null;
      }
      return output.getSchoolNameAndInstitution().getFieldValueIdByDescription(udFieldDescription);
    }
    return null;
  }

  private int createLoanAccount(String productCode, Map<String, Object> accountCreationInformation, List<Map<String, String>> coBorrowers)
      throws UseCaseException
  {
    CreateLoanAccount createLoanAccount = new CreateLoanAccount(coreBankingService);
    CreateLoanAccountInput input = new CreateLoanAccountInput(productCode, accountCreationInformation, coBorrowers);
    return createLoanAccount.execute(input).getAccountNumber();
  }

  private GetCustomerAccountCreationInfoOutput getAccountCreationInformation(String productCode) throws UseCaseException
  {
    GetCustomerAccountCreationInfo getCustomerAccountCreationInfo = new GetCustomerAccountCreationInfo(coreBankingService);

    GetCustomerAccountCreationInfoOutput output = getCustomerAccountCreationInfo.execute(productCode);

    return output;
  }

  private String getProductCode(String loanProduct)
  {
    String productCode = null;
    if (loanProduct.equals("EB50-365-Цалингийн зээл-Иргэн") || loanProduct.equals("EB50"))
    {
      productCode = "EB50";
    }
    else if (loanProduct.equals("EF50-365-Ажиллагсадын хэрэглээний зээл") || loanProduct.equals("EF50"))
    {
      productCode = "EF50";
    }
    else if (loanProduct.equals("EB51-365-Цалингийн зээл-Иргэн-EMI") || loanProduct.equals("EB51"))
    {
      productCode = "EB51";
    }

    if (null == productCode)
    {
      productCode = loanProduct.substring(0, 4);
    }

    return productCode;
  }

  private String getScore(DelegateExecution execution)
  {
    String score = null;
    if (execution.getVariable(SCORING_SCORE) instanceof String)
    {
      score = (String) execution.getVariable(SCORING_SCORE);
    }
    else if (execution.getVariable(SCORING_SCORE) instanceof Integer)
    {
      score = String.valueOf(execution.getVariable(SCORING_SCORE));
    }
    else
    {
      Double scoreDouble = (double) execution.getVariable(SCORING_SCORE);
      score = scoreDouble.toString();
    }

    return score;
  }

  private void validateDayOfPayment(String dayOfPayment) throws ProcessTaskException
  {
    try
    {
      Integer.valueOf(dayOfPayment);
    }
    catch (Exception e)
    {
      String errorCode = "BPMS078";
      throw new ProcessTaskException(errorCode, "Invalid payment day!" + " Day Of Payment: " + dayOfPayment.getClass() + "  :  " + dayOfPayment);
    }
  }

  private void validateFees(String fees) throws ProcessTaskException
  {
    try
    {
      BigDecimal feesBD = new BigDecimal(fees);
      if (feesBD.compareTo(BigDecimal.ZERO) == -1 || feesBD.compareTo(BigDecimal.valueOf(100)) == 1)
      {
        throw new Exception();
      }

    }
    catch (Exception e)
    {
      String errorCode = "BPMS077";
      throw new ProcessTaskException(errorCode, "Invalid fees percentage!");
    }
  }
}
