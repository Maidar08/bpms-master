/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.bpms.loan.consumption.service_task;

import java.io.Serializable;
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
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.bpms.loan.consumption.loan_amount_calculation.LoanCyclePlan;
import mn.erin.bpms.loan.consumption.loan_amount_calculation.MonthlyLoanCycle;
import mn.erin.bpms.process.base.ProcessTaskException;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.CreateCollateralLoanAccount;
import mn.erin.domain.bpm.CreateCollateralLoanAccountInput;
import mn.erin.domain.bpm.model.account.UDField;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.model.process.Process;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.service.CoreBankingService;
import mn.erin.domain.bpm.service.NewCoreBankingService;
import mn.erin.domain.bpm.usecase.customer.GetCustomerAccountCreationInfo;
import mn.erin.domain.bpm.usecase.customer.GetCustomerAccountCreationInfoOutput;
import mn.erin.domain.bpm.usecase.customer.GetUDFieldsByProductCode;
import mn.erin.domain.bpm.usecase.customer.GetUDFieldsByProductCodeOutput;
import mn.erin.domain.bpm.usecase.process.collateral.GetCollateralById;
import mn.erin.domain.bpm.usecase.process.collateral.GetCollateralByIdInput;

import static mn.erin.bpms.loan.consumption.service_task.bpms.co_borrower.CoBorrowerUtils.INDEX_CO_BORROWER;
import static mn.erin.bpms.loan.consumption.utils.CollateralUtils.getCollInfoFromVariable;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_BASIC_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_DESCRIPTION;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_SUB_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.DEDUCTION_RATE;
import static mn.erin.domain.bpm.BpmModuleConstants.FULL_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.FULL_NAME_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.HAS_INSURANCE;
import static mn.erin.domain.bpm.BpmModuleConstants.INSURANCE_C_UDF_EXECUTION_VAR_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PRODUCT;
import static mn.erin.domain.bpm.BpmModuleConstants.PRODUCT_CODE;
import static mn.erin.domain.bpm.BpmModuleConstants.TOTAL_COLLATERAL_AMOUNT_UDF_VAR_ID;

/**
 * @author Zorig
 */
public class CreateCollateralLoanAccountTaskMicro implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(CreateCollateralLoanAccountTaskMicro.class);
  private final CoreBankingService coreBankingService;
  private final NewCoreBankingService newCoreBankingService;
  private final AuthenticationService authenticationService;
  private final MembershipRepository membershipRepository;
  private final ProcessRepository processRepository;

  private static final String SCORING_SCORE = "score";

  public CreateCollateralLoanAccountTaskMicro(NewCoreBankingService newCoreBankingService, CoreBankingService coreBankingService, AuthenticationService authenticationService,
      MembershipRepository membershipRepository, ProcessRepository processRepository)
  {
    this.newCoreBankingService = newCoreBankingService;
    this.coreBankingService = coreBankingService;
    this.authenticationService = authenticationService;
    this.membershipRepository = membershipRepository;
    this.processRepository = processRepository;
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
    LOGGER.info(
        "#########  Creating Micro Loan Account with collateral(s).. Register Number: " + registrationNumber + ", Request ID: " + requestId + " , User ID: "
            + userId);

    String productCode = (String) execution.getVariable("loanProduct");

    if (null != productCode)
    {
      productCode = productCode.substring(0, 4);
    }

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

    if (execution.getVariable("acceptedLoanAmount") instanceof Integer)
    {
      grantLoanAmountBD = BigDecimal.valueOf((Integer) execution.getVariable("acceptedLoanAmount"));
      grantLoanAmount = grantLoanAmountBD.toString();
    }
    else
    {
      grantLoanAmountBD = BigDecimal.valueOf((long) execution.getVariable("acceptedLoanAmount"));
      grantLoanAmount = grantLoanAmountBD.toString();
    }

    String numberOfPayments = (String) execution.getVariable("numberOfPayments");

    BigDecimal yearlyInterestRate = (BigDecimal) execution.getVariable("yearlyInterestRate");

    BigDecimal interestRateBD = yearlyInterestRate.divide(BigDecimal.valueOf(12), 16, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
    double interestRate = interestRateBD.doubleValue();

    String accountBranchNumber = (String) execution.getVariable("accountBranchNumber");
    String currentAccountNumber = (String) execution.getVariable("currentAccountNumber");
    String frequency = (String) execution.getVariable("frequency");
    BigDecimal depositInterestRate = (BigDecimal) execution.getVariable("depositInterestRate");
    String dayOfPayment = (String) execution.getVariable("dayOfPayment");
    String fees = (String) execution.getVariable("fees");
    Date firstPaymentDate = (Date) execution.getVariable("firstPaymentDate");//camunda gets this
    String loanPurpose = (String) execution.getVariable("loanPurpose");
    String businessTypeReason = (String) execution.getVariable("businessTypeReason");
    String worker = (String) execution.getVariable("worker");
    String sanctionedBy = (String) execution.getVariable("sanctionedBy");
    String subType = (String) execution.getVariable("subType");
    String insuranceCompanyInfo = (String) execution.getVariable("insuranceCompanyInfo");
    String lateReason = (String) execution.getVariable("lateReason");
    String loanCycle = (String) execution.getVariable("loanCycle");

    String branch = (String) execution.getVariable("branch");

    String typeOfFee = (String) execution.getVariable("TypeOfFee");
    String amountOfFee = (String) execution.getVariable("amountOfFee");
    String numberOfCurrentJobs = (String) execution.getVariable("numberOfCurrentJobs");
    String numberNewCreatedApartments = (String) execution.getVariable("numberNewCreatedApartments");
    String discountRate = (String) execution.getVariable("discountedInterestFromMinistry");

    long boney = 0;
    if (null != execution.getVariable("boneu"))
    {
      boney = (long) execution.getVariable("boneu");
    }

    long purchasePrice = 0;
    if (null != execution.getVariable("sellAndPurchasePrice"))
    {
      purchasePrice = (long) execution.getVariable("sellAndPurchasePrice");
    }

    String currentHeater = (String) execution.getVariable("currentHeater");
    String schoolNameAndInstitution = (String) execution.getVariable("schoolNameAndInstitution");

    boolean hasInsurance = false;
    if (null != execution.getVariable(HAS_INSURANCE))
    {
      hasInsurance = (boolean) execution.getVariable(HAS_INSURANCE);
    }

    long insuranceCUDF = getInsuranceUDF(execution);
    long totalCollateralAmountUDF = getCollateralAmountUDF(execution);

    //TODO : set Missing loanGuaranteeRate

    String controlOfficer = (String) execution.getVariable("controlOfficer");
    String exceptionalLoan = (String) execution.getVariable("exceptionalLoan");
    Date loanReviewDate = (Date) execution.getVariable("loanReviewDate");
    Date restructureDate = (Date) execution.getVariable("dateOfRestructure");
    Date dateOfAttention = (Date) execution.getVariable("dateOfAttention");

    String facilityRating = getFacilityRating(execution);
    String borrowerRating = getBorrowerRating(execution);

    String score = getScore(execution);

    String scoreLevel = (String) execution.getVariable("scoring_level_risk");

    String cif = (String) execution.getVariable("cifNumber");

    Map<String, Object> accountCreationInformation = new HashMap<>();

    GetCustomerAccountCreationInfoOutput output = getAccountCreationInformation(productCode);
    Map<String, String> input = new HashMap<>();
    input.put(PROCESS_TYPE_ID, String.valueOf(execution.getVariable(PROCESS_TYPE_ID)));
    input.put(PHONE_NUMBER, String.valueOf(execution.getVariable(PHONE_NUMBER)));
    input.put(PRODUCT_CODE, String.valueOf(execution.getVariable(PRODUCT_CODE)));
    GetUDFieldsByProductCodeOutput getUDFieldsByProductCodeOutput = getUDFieldByProductCode(input);

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

    accountCreationInformation.put("HURAAMJIIN_TURUL", getUDFieldValueId("HURAAMJIIN_TURUL", typeOfFee, getUDFieldsByProductCodeOutput));
    accountCreationInformation.put("HURAAMJIIN_DUN", getUDFieldValueId("HURAAMJIIN_DUN", amountOfFee, getUDFieldsByProductCodeOutput));
    accountCreationInformation.put("NUMBER_OF_CURRENT_POSITION", numberOfCurrentJobs);
    accountCreationInformation.put("NUMBER_OF_NEW_POSITIONS", numberNewCreatedApartments);
    accountCreationInformation.put("DISCOUNT_RATE", discountRate);
    accountCreationInformation.put("CREDIT_CONTROL_OFFICER", getUDFieldValueId("CREDIT_CONTROL_OFFICER", controlOfficer, getUDFieldsByProductCodeOutput));
    accountCreationInformation.put("EXCEPTIONAL_LOAN", getUDFieldValueId("EXCEPTIONAL_LOAN", exceptionalLoan, getUDFieldsByProductCodeOutput));
    accountCreationInformation.put("CL_CONTROL_DATE", loanReviewDate);
    accountCreationInformation.put("RESTRUCTURED_DATE", restructureDate);
    accountCreationInformation.put("ANHAARAL_TATSAN_DATE", dateOfAttention);
    accountCreationInformation.put("FACILITY_RATING", facilityRating);
    accountCreationInformation.put("BORROWER_RATING", borrowerRating);

    insertDefaultUDFAccountCreationInformation(accountCreationInformation, getUDFieldsByProductCodeOutput);

    accountCreationInformation.put("BONEY", String.valueOf(boney));
    accountCreationInformation
        .put("SURGUULIIN NER/BAIGUULLAGA", getUDFieldValueId("SURGUULIIN NER/BAIGUULLAGA", schoolNameAndInstitution, getUDFieldsByProductCodeOutput));
    accountCreationInformation.put("PURCHASE PRICE", String.valueOf(purchasePrice));
    accountCreationInformation
        .put("ODOOGIIN HALAALTIIN HEREGSEL", getUDFieldValueId("ODOOGIIN HALAALTIIN HEREGSEL", currentHeater, getUDFieldsByProductCodeOutput));

    if (hasInsurance)
    {
      accountCreationInformation.put("INSURANCE_C", insuranceCUDF);
      accountCreationInformation.put("COLLATERAL_AMOUNT", totalCollateralAmountUDF);
    }

    // TODO: set guaranteedLoanRate, it is missing.

    validateDayOfPayment(dayOfPayment);
    accountCreationInformation.put("DUEDATESON", dayOfPayment);

    validateFees(fees);
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

    Map<String, Map<String, Object>> collateralMap = (Map) execution.getVariable("collateralList");

    for (Map.Entry<String, Map<String, Object>> collateral : collateralMap.entrySet())
    {
      String collateralId = collateral.getKey();
      Map<String, Serializable> collateralInfoFromExecutionVariableOrProcess = getCollateral(collateralId, execution);
      Map<String, Object> collateralInfo = collateral.getValue();

      String haircut = String.valueOf(collateralInfoFromExecutionVariableOrProcess.get(DEDUCTION_RATE));

      collateralInfo.put("description", collateralInfoFromExecutionVariableOrProcess.get(COLLATERAL_DESCRIPTION));
      collateralInfo.put("haircut", haircut);
    }

    int accountNumber = createLoanAccount(productCode, accountCreationInformation, coBorrowers, collateralMap);

    LOGGER.info("######### CREATED ACCOUNT NUMBER = [{}], for request number = [{}]", accountNumber, requestId);
    execution.setVariable(BpmModuleConstants.LOAN_ACCOUNT_NUMBER, String.valueOf(accountNumber));

    CaseService caseService = execution.getProcessEngine().getCaseService();

    String caseInstanceId = (String) execution.getVariable(CASE_INSTANCE_ID);
    LOGGER.info("############ SETS ACCOUNT NUMBER TO CASE VARIABLE = [{}]", accountNumber);
    caseService.setVariable(caseInstanceId, BpmModuleConstants.LOAN_ACCOUNT_NUMBER, String.valueOf(accountNumber));

    //  disables needless enabled processes.
    if (null != accountBranchNumber)
    {
      disableCoBorrowerExecutions(execution);

      disableCollateralExecutions(execution, caseInstanceId);
    }

    LOGGER.info("######### Finished Creating Micro Loan Account...");
  }

  private List<Map<String, String>> getCoBorrowers(DelegateExecution execution, Map<String, Object> accountCreationInformation, BigDecimal grantLoanAmountBD) throws
    ProcessTaskException
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

  private void disableCollateralExecutions(DelegateExecution execution, String caseInstanceId)
  {
    String requestId = (String) execution.getVariable(PROCESS_REQUEST_ID);
    CaseService caseService = execution.getProcessEngine().getCaseService();

    List<CaseExecution> enabledExecutions = caseService.createCaseExecutionQuery()
        .caseInstanceId(caseInstanceId)
        .enabled()
        .list();

    for (CaseExecution enabledExecution : enabledExecutions)
    {
      String enabledActId = enabledExecution.getActivityId();

      if (enabledActId.equals(BpmModuleConstants.ACTIVITY_ID_COLLATERAL_LIST)
          || enabledActId.equals(BpmModuleConstants.ACTIVITY_ID_CALCULATE_LOAN_AMOUNT_AFTER_ACCOUNT_CREATION)
          || enabledActId.equals(BpmModuleConstants.ACTIVITY_ID_CREATE_COLLATERAL))
      {
        try
        {
          LOGGER.info("################ DISABLES ENABLED EXECUTION WITH ACTIVITY ID = [{}], with REQUEST ID = [{}]",
              enabledActId, requestId);
          caseService.disableCaseExecution(enabledExecution.getId());
        }
        catch (Exception e)
        {
          LOGGER.error("##### COULD NOT DISABLE EXECUTION = [{}] with  REQUEST ID = [{}], REASON = [{}]", enabledActId, requestId, e.getMessage());
        }
      }
    }
  }

  private void insertDefaultUDFAccountCreationInformation(Map<String, Object> accountCreationInformation, GetUDFieldsByProductCodeOutput output)
  {
    //if udFields don't come, getting UDFieldValueId, no ud field, will return and set null, when creating dynamic request body, these fields won't be used.
    //if udFields do come, getting UDFieldValueId, yes ud field, description match, return id, when creating dynamic request body, field will be used.
    //if udFields do come, getting UDFieldValueId, yes ud field, no description match, return null, when creating dynamic request body, field will be set as a null placeholder
    //because the value is set as null in accountCreationInformation
    accountCreationInformation.put("BAISHINGAA SUULIIN 1 JIL DULAALSAN", getUDFieldValueId("BAISHINGAA SUULIIN 1 JIL DULAALSAN", "Тийм", output));
    accountCreationInformation.put("HOLIMOG ZEEL ESEH", getUDFieldValueId("HOLIMOG ZEEL ESEH", "Тийм", output));
    accountCreationInformation.put("ODOOGIIN ORON BAIRNII MEDEELEL", getUDFieldValueId("ODOOGIIN ORON BAIRNII MEDEELEL", "Байшин", output));
    accountCreationInformation.put("BOAJYA_TATAAS", getUDFieldValueId("BOAJYA_TATAAS", "Шаардлагагүй", output));
  }

  private Map<String, Serializable> getCollateral(String collateralId, DelegateExecution execution) throws UseCaseException, ProcessTaskException
  {
    GetCollateralById getCollateralById = new GetCollateralById(processRepository);
    GetCollateralByIdInput getCollateralByIdInput = new GetCollateralByIdInput(collateralId);

    Process process = getCollateralById.execute(getCollateralByIdInput);

    if (process == null)
    {
      return getCollInfoFromVariable(collateralId, execution);
    }

    Map<ParameterEntityType, Map<String, Serializable>> processParameters = process.getProcessParameters();
    Map<String, Serializable> collateralEntityParameters = processParameters.get(ParameterEntityType.COLLATERAL);

    for (Map.Entry<String, Serializable> collateralParameter : collateralEntityParameters.entrySet())
    {
      String collateralsJSONString = String.valueOf(collateralParameter.getValue());
      JSONObject collateralJSON = new JSONObject(collateralsJSONString);

      Map<String, Serializable> collateralMapToReturn = new HashMap<>();

      collateralMapToReturn.put(COLLATERAL_BASIC_TYPE,
          JSONObject.NULL.equals(collateralJSON.get(COLLATERAL_BASIC_TYPE)) ? "" : (String) collateralJSON.get(COLLATERAL_BASIC_TYPE));
      collateralMapToReturn
          .put(COLLATERAL_SUB_TYPE, JSONObject.NULL.equals(collateralJSON.get(COLLATERAL_SUB_TYPE)) ? "" : (String) collateralJSON.get(COLLATERAL_SUB_TYPE));
      collateralMapToReturn.put(PRODUCT, JSONObject.NULL.equals(collateralJSON.get(PRODUCT)) ? "" : (String) collateralJSON.get(PRODUCT));
      collateralMapToReturn.put(COLLATERAL_DESCRIPTION,
          JSONObject.NULL.equals(collateralJSON.get(COLLATERAL_DESCRIPTION)) ? "" : String.valueOf(collateralJSON.get(COLLATERAL_DESCRIPTION)));
      collateralMapToReturn
          .put(DEDUCTION_RATE, JSONObject.NULL.equals(collateralJSON.get(DEDUCTION_RATE)) ? "" : String.valueOf(collateralJSON.get(DEDUCTION_RATE)));

      return collateralMapToReturn;
    }

    return process.getProcessParameters().get(ParameterEntityType.COLLATERAL);
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
            || activityId.equalsIgnoreCase("PlanItem_1ouvsp3"))
        {
          caseService.disableCaseExecution(enabledExecution.getId());
        }
      }
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

    return null;
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

  private int createLoanAccount(String productCode, Map<String, Object> accountCreationInformation, List<Map<String, String>> coBorrowers,
      Map<String, Map<String, Object>> collateralMap)
      throws UseCaseException
  {
    CreateCollateralLoanAccount createCollateralLoanAccount = new CreateCollateralLoanAccount(coreBankingService);
    CreateCollateralLoanAccountInput input = new CreateCollateralLoanAccountInput(productCode, accountCreationInformation, coBorrowers, collateralMap);

    return createCollateralLoanAccount.execute(input).getAccountNumber();
  }

  private GetCustomerAccountCreationInfoOutput getAccountCreationInformation(String productCode) throws UseCaseException
  {
    GetCustomerAccountCreationInfo getCustomerAccountCreationInfo = new GetCustomerAccountCreationInfo(coreBankingService);

    return getCustomerAccountCreationInfo.execute(productCode);
  }

  private GetUDFieldsByProductCodeOutput getUDFieldByProductCode(Map<String, String> input) throws UseCaseException
  {
    GetUDFieldsByProductCode getUDFieldsByProductCode = new GetUDFieldsByProductCode(newCoreBankingService);
    return getUDFieldsByProductCode.execute(input);
  }

  private long getInsuranceUDF(DelegateExecution execution)
  {
    long insuranceCUDF = 0;
    if (null != execution.getVariable(INSURANCE_C_UDF_EXECUTION_VAR_ID))
    {
      Object insuranceUDFVariable = execution.getVariable(INSURANCE_C_UDF_EXECUTION_VAR_ID);

      if (insuranceUDFVariable instanceof Long)
      {
        insuranceCUDF = (long) execution.getVariable(INSURANCE_C_UDF_EXECUTION_VAR_ID);
      }
      else if (insuranceUDFVariable instanceof String)
      {
        insuranceCUDF = (long) Double.parseDouble(String.valueOf(insuranceUDFVariable));
      }
    }
    return insuranceCUDF;
  }

  private long getCollateralAmountUDF(DelegateExecution execution)
  {
    long totalCollateralAmountUDF = 0;
    if (null != execution.getVariable(TOTAL_COLLATERAL_AMOUNT_UDF_VAR_ID))
    {
      Object variableTotalColUDF = execution.getVariable(TOTAL_COLLATERAL_AMOUNT_UDF_VAR_ID);

      if (variableTotalColUDF instanceof Long)
      {
        totalCollateralAmountUDF = (long) variableTotalColUDF;
      }
      else if (variableTotalColUDF instanceof String)
      {
        totalCollateralAmountUDF = Long.parseLong(String.valueOf(variableTotalColUDF));
      }
    }
    return totalCollateralAmountUDF;
  }

  private String getFacilityRating(DelegateExecution execution) throws ProcessTaskException
  {
    String facilityRating = "1";

    Object facilityRatingObj = execution.getVariable("facilityRating");

    if (facilityRatingObj != null)
    {
      if (facilityRatingObj instanceof String)
      {
        facilityRating = (String) facilityRatingObj;
      }
      else if (facilityRatingObj instanceof Integer)
      {
        facilityRating = String.valueOf(facilityRatingObj);
      }
      else if (facilityRatingObj instanceof Long)
      {
        facilityRating = String.valueOf((long) facilityRatingObj);
      }
      else
      {
        Double facilityRatingDouble = (double) facilityRatingObj;
        facilityRating = facilityRatingDouble.toString();
      }

      try
      {
        new BigDecimal(facilityRating);
      }
      catch (NumberFormatException e)
      {
        throw new ProcessTaskException("Wrong number format for Facility Rating!");
      }
    }

    return facilityRating;
  }

  private String getBorrowerRating(DelegateExecution execution) throws ProcessTaskException
  {
    String borrowerRating = "1";

    Object borrowerRatingObj = execution.getVariable("borrowerRating");

    if (borrowerRatingObj != null)
    {
      if (borrowerRatingObj instanceof String)
      {
        borrowerRating = (String) borrowerRatingObj;
      }
      else if (borrowerRatingObj instanceof Integer)
      {
        borrowerRating = String.valueOf(borrowerRatingObj);
      }
      else if (borrowerRatingObj instanceof Long)
      {
        borrowerRating = String.valueOf((long) borrowerRatingObj);
      }
      else
      {
        Double borrrowerRatingDouble = (double) borrowerRatingObj;
        borrowerRating = borrrowerRatingDouble.toString();
      }

      try
      {
        new BigDecimal(borrowerRating);
      }
      catch (NumberFormatException e)
      {
        throw new ProcessTaskException("Wrong number format for Borrower Rating!");
      }
    }

    return borrowerRating;
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
