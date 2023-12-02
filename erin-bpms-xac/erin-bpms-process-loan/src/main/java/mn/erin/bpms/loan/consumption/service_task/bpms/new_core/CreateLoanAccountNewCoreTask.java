package mn.erin.bpms.loan.consumption.service_task.bpms.new_core;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import mn.erin.bpms.loan.consumption.utils.BranchUtils;
import mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils;
import mn.erin.bpms.loan.consumption.utils.CustomDateUtils;
import mn.erin.bpms.process.base.ProcessTaskException;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.AimServiceRegistry;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.bpm.model.account.UDField;
import mn.erin.domain.bpm.model.account.UDFieldValue;
import mn.erin.domain.bpm.model.collateral.Collateral;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.repository.OrganizationLeasingRepository;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.NewCoreBankingService;
import mn.erin.domain.bpm.usecase.loan.create_account.AddLoanAccount;
import mn.erin.domain.bpm.usecase.loan.create_account.AddLoanAccountInput;
import mn.erin.domain.bpm.usecase.loan.create_account.AddLoanAccountOutput;

import static mn.erin.bpm.domain.ohs.xac.XacConstants.CHO_BRANCH_NUMBER;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.PROPERTY_KEY_ACCOUNT_DEFAULT_CURRENCY;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.PROPERTY_KEY_ACCOUNT_FREQUENCY_CALL;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.PROPERTY_KEY_ACCOUNT_FREQUENCY_HOLIDAY_STATUS;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.PROPERTY_KEY_ACCOUNT_FREQUENCY_TYPE;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.PROPERTY_KEY_ACCOUNT_REPAYMENT_METHOD;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.PROPERTY_KEY_ACCOUNT_REPRICING_PLAN;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.PROPERTY_KEY_TEST_ACCOUNT_DATE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.FREE_TEXT_1;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.NATURE_OF_ADVN;
import static mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountConstants.ACCOUNT_FREE_CODE_1;
import static mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountConstants.ACCOUNT_FREE_CODE_2;
import static mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountConstants.ACCOUNT_FREE_CODE_3;
import static mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountConstants.ADDITIONAL_SPECIAL_CONDITION;
import static mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountConstants.BORROWER_CATEGORY_CODE;
import static mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountConstants.BRANCH_ID;
import static mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountConstants.CAL;
import static mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountConstants.CODE;
import static mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountConstants.CURRENCY;
import static mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountConstants.CUSTOMER_INDUSTRY_TYPE;
import static mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountConstants.EMPTY_STRING_VALUE;
import static mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountConstants.EQ_INSTALL_FLG;
import static mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountConstants.FREE_CODE_10;
import static mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountConstants.FREE_CODE_4;
import static mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountConstants.FREE_CODE_5;
import static mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountConstants.FREE_CODE_6;
import static mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountConstants.FREE_CODE_7;
import static mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountConstants.FREE_CODE_8;
import static mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountConstants.FREE_CODE_9;
import static mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountConstants.HOL_STAT;
import static mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountConstants.INSTALL_FREQ;
import static mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountConstants.INSTALL_START_DT;
import static mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountConstants.INTEREST_TABLE_CODE;
import static mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountConstants.INT_FREQ;
import static mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountConstants.INT_START_DT;
import static mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountConstants.LOAN_AMOUNT;
import static mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountConstants.LOAN_PERIOD_DAYS;
import static mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountConstants.LOAN_PERIOD_MONTHS;
import static mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountConstants.NATURE_OF_ADVANCE;
import static mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountConstants.NO;
import static mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountConstants.NO_OF_INSTALL;
import static mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountConstants.OPER_ACCT_ID;
import static mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountConstants.PRODUCT_CODE;
import static mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountConstants.PURPOSE_OF_ADVANCE;
import static mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountConstants.REPAYMENT_METHOD;
import static mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountConstants.REPRICING_PLAN;
import static mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountConstants.REQUEST_ID;
import static mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountConstants.SANCTIONED_BY;
import static mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountConstants.START_DT;
import static mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountConstants.TYPE;
import static mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountConstants.TYPE_OF_ADVANCE;
import static mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountConstants.WEEK_DAY;
import static mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountConstants.WEEK_NUM;
import static mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountConstants.YES;
import static mn.erin.bpms.loan.consumption.utils.AccountUtils.getProductCode;
import static mn.erin.domain.bpm.BpmModuleConstants.ACCEPTED_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.ACTION_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.AMOUNT_OF_ASSESSMENT;
import static mn.erin.domain.bpm.BpmModuleConstants.BNPL_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.BRANCH_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CHO_BRANCH;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_LIST;
import static mn.erin.domain.bpm.BpmModuleConstants.CO_BORROWER_CUST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CO_BORROWER_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.CURRENT_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.DAY_OF_PAYMENT;
import static mn.erin.domain.bpm.BpmModuleConstants.EQUAL_PRINCIPLE_PAYMENT;
import static mn.erin.domain.bpm.BpmModuleConstants.EQUATED_MONTHLY_INSTALLMENT;
import static mn.erin.domain.bpm.BpmModuleConstants.ERROR_CAUSE;
import static mn.erin.domain.bpm.BpmModuleConstants.FIRST_PAYMENT_DATE;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ACCEPTED_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ACCEPTED_LOAN_AMOUNT_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.HAS_ACTIVE_LOAN_ACCOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.INDEX_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.INVOICE_AMOUNT_75;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PRODUCT;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_TERM;
import static mn.erin.domain.bpm.BpmModuleConstants.NUMBER_OF_PAYMENTS;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_SALARY_PROCESS_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.REPAYMENT_EQUAL_PRINCIPLE_PAYMENT;
import static mn.erin.domain.bpm.BpmModuleConstants.REPAYMENT_EQUATED_MONTHLY_INSTALLMENT;
import static mn.erin.domain.bpm.BpmModuleConstants.REPAYMENT_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.TENANT_ID_XAC;
import static mn.erin.domain.bpm.BpmModuleConstants.TERM;
import static mn.erin.domain.bpm.BpmModuleConstants.TERMINAL_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.TOTAL_CLOSING_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.TRACK_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.YEARLY_INTEREST_RATE_STRING;
import static mn.erin.domain.bpm.model.process.ParameterEntityType.getTypeByProcessType;
import static mn.erin.domain.bpm.util.process.BpmUtils.getStringValue;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;
import static mn.erin.domain.bpm.util.process.BpmUtils.toBigDecimal;
import static mn.erin.domain.bpm.util.process.DigitalLoanUtils.updateProcessParameters;

/**
 * @author Lkhagvadorj.A
 **/

public class CreateLoanAccountNewCoreTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(CreateLoanAccountNewCoreTask.class);

  private final NewCoreBankingService newCoreBankingService;
  private final AuthenticationService authenticationService;

  private final MembershipRepository membershipRepository;
  private final Environment environment;
  private final OrganizationLeasingRepository organizationLeasingRepository;

  private final AimServiceRegistry aimServiceRegistry;
  private final ProcessRepository processRepository;

  public CreateLoanAccountNewCoreTask(NewCoreBankingService newCoreBankingService,
      AuthenticationService authenticationService,
      MembershipRepository membershipRepository,
      Environment environment, OrganizationLeasingRepository organizationLeasingRepository, AimServiceRegistry aimServiceRegistry,
      ProcessRepository processRepository)
  {
    this.newCoreBankingService = newCoreBankingService;
    this.authenticationService = authenticationService;

    this.membershipRepository = membershipRepository;
    this.environment = environment;
    this.organizationLeasingRepository = organizationLeasingRepository;
    this.aimServiceRegistry = aimServiceRegistry;
    this.processRepository = processRepository;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String processType = String.valueOf(execution.getVariable(PROCESS_TYPE_ID));
    try
    {
      validateLinkedColAmount(execution);
      String instanceId = (processType.equals(BNPL_PROCESS_TYPE_ID) || processType.equals(INSTANT_LOAN_PROCESS_TYPE_ID) || processType.equals(ONLINE_LEASING_PROCESS_TYPE_ID)) ?
          String.valueOf(execution.getVariable(PROCESS_INSTANCE_ID)) :
          (String) execution.getVariable(CASE_INSTANCE_ID);
      updateCollateralOnExecution(execution, instanceId);

      String requestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));

      String product = String.valueOf(execution.getVariable(LOAN_PRODUCT));
      String productCode = getProductCode(product);

      String cifNumber = String.valueOf(execution.getVariable(CIF_NUMBER));
      String branchId;
      String loanTermMonth;
      String days = String.valueOf(execution.getVariable("days"));
      String phoneNumber = String.valueOf(execution.getVariable(PHONE_NUMBER));

      if (processType.equals(ONLINE_SALARY_PROCESS_TYPE) || processType.equals(BNPL_PROCESS_TYPE_ID) || processType.equals(INSTANT_LOAN_PROCESS_TYPE_ID)
          || processType.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
      {
        branchId = String.valueOf(execution.getVariable(BRANCH_NUMBER));
        loanTermMonth = String.valueOf(execution.getVariable(TERM));
      }
      else
      {
        branchId = BranchUtils.getBranchId(TENANT_ID_XAC, authenticationService, membershipRepository);
        loanTermMonth = execution.hasVariable(LOAN_TERM) ? String.valueOf(execution.getVariable(LOAN_TERM)) : String.valueOf(execution.getVariable(TERM));
      }

      if (branchId.equals(CHO_BRANCH))
      {
        branchId = Objects.requireNonNull(environment.getProperty(CHO_BRANCH_NUMBER), "Could not get CHO branch number from config file!");
      }
      execution.setVariable("branch", branchId);

      String currentAccountNumber = String.valueOf(execution.getVariable(CURRENT_ACCOUNT_NUMBER));
      String yearlyInterestRate = String.valueOf(execution.getVariable(YEARLY_INTEREST_RATE_STRING));

      BigDecimal invoiceAmount75 = (BigDecimal) execution.getVariable(INVOICE_AMOUNT_75);
      BigDecimal acceptedLoanAmount = processType.equals(BNPL_PROCESS_TYPE_ID) ? invoiceAmount75 : getAcceptedLoanAmount(execution);

      Object dayOfPayment = execution.getVariable(DAY_OF_PAYMENT);
      String dayPaymentString;
      if (null == dayOfPayment)
      {
        Map<String, Object> caseVariables = CaseExecutionUtils.getCaseVariables(instanceId, execution.getProcessEngine());
        dayPaymentString = (String) caseVariables.get(DAY_OF_PAYMENT);
      }
      else
      {
        dayPaymentString = String.valueOf(dayOfPayment);
      }

      Map<String, Object> genericInfo = new HashMap<>();
      Map<String, Object> additionalInfos = new HashMap<>();

      genericInfo.put(REQUEST_ID, requestId);
      genericInfo.put(PRODUCT_CODE, productCode);

      genericInfo.put(BRANCH_ID, branchId);

      genericInfo.put(LOAN_AMOUNT, String.valueOf(acceptedLoanAmount));
      genericInfo.put(OPER_ACCT_ID, currentAccountNumber);

      genericInfo.put(REPRICING_PLAN, environment.getProperty(PROPERTY_KEY_ACCOUNT_REPRICING_PLAN));
      genericInfo.put(INTEREST_TABLE_CODE, yearlyInterestRate);

      if (processType.equals(BNPL_PROCESS_TYPE_ID))
      {
        genericInfo.put(LOAN_PERIOD_MONTHS, "0");
        genericInfo.put(LOAN_PERIOD_DAYS, loanTermMonth);
      }
      else if (processType.equals(INSTANT_LOAN_PROCESS_TYPE_ID))
      {
        genericInfo.put(LOAN_PERIOD_MONTHS, "0");
        genericInfo.put(LOAN_PERIOD_DAYS, days);
      }
      else
      {
        genericInfo.put(LOAN_PERIOD_MONTHS, loanTermMonth);
        genericInfo.put(LOAN_PERIOD_DAYS, EMPTY_STRING_VALUE);
      }
      genericInfo.put(CIF_NUMBER, cifNumber);

      genericInfo.put(CURRENCY, environment.getProperty(PROPERTY_KEY_ACCOUNT_DEFAULT_CURRENCY));
      genericInfo.put(REPAYMENT_METHOD, environment.getProperty(PROPERTY_KEY_ACCOUNT_REPAYMENT_METHOD));

      String eqInstallFlg = getEqFlag(execution);

      genericInfo.put(EQ_INSTALL_FLG, eqInstallFlg);
      genericInfo.put(CODE, EMPTY_STRING_VALUE);

      String firstPaymentDateStr = EMPTY_STRING_VALUE;

      Date firstPaymentDate = (Date) execution.getVariable(FIRST_PAYMENT_DATE);

      if (null != firstPaymentDate)
      {
        firstPaymentDateStr = CustomDateUtils.dateFormatter.format(firstPaymentDate);
      }

      // TODO :  set correct date regarding to business logic
      if (null == environment.getProperty(PROPERTY_KEY_TEST_ACCOUNT_DATE))
      {
        genericInfo.put(INSTALL_START_DT, firstPaymentDateStr);
      }
      else
      {
        // gets test date from config (Dedicated XAC TEST Environment)
        genericInfo.put(INSTALL_START_DT, environment.getProperty(PROPERTY_KEY_TEST_ACCOUNT_DATE));
      }

      genericInfo.put(INSTALL_FREQ, getFrequency(dayPaymentString, environment));
      genericInfo.put(INT_FREQ, getFrequency(dayPaymentString, environment));
      genericInfo.put(NO_OF_INSTALL, String.valueOf(execution.getVariable(NUMBER_OF_PAYMENTS)));
      genericInfo.put(PROCESS_TYPE_ID, processType);
      genericInfo.put(PHONE_NUMBER, phoneNumber);

      // TODO : set correct date regarding to business logic
      if (null == environment.getProperty(PROPERTY_KEY_TEST_ACCOUNT_DATE))
      {
        genericInfo.put(INT_START_DT, firstPaymentDateStr);
      }
      else
      {
        genericInfo.put(INT_START_DT, environment.getProperty(PROPERTY_KEY_TEST_ACCOUNT_DATE));
      }
      String terminalId = (String) execution.getVariable(TERMINAL_ID);
      if (!StringUtils.isBlank(terminalId) && !StringUtils.isBlank(productCode))
      {
        Map<String, String> input = new HashMap<>();
        input.put(PROCESS_TYPE_ID, processType);
        input.put(PHONE_NUMBER, phoneNumber);
        input.put(TERMINAL_ID, terminalId);
        input.put(PRODUCT_CODE, productCode);
        genericInfo.put(NATURE_OF_ADVN, getNatureOfAdvn(input));
      }

      // Get UD fields
      additionalInfos.put("freeCode1", getStringValue(execution.getVariable(ACCOUNT_FREE_CODE_1)));
      additionalInfos.put("freeCode2", getStringValue(execution.getVariable(ACCOUNT_FREE_CODE_2)));

      additionalInfos.put("freeCode3", getStringValue(execution.getVariable(ACCOUNT_FREE_CODE_3)));
      additionalInfos.put("freeCode4", getStringValue(execution.getVariable(FREE_CODE_4)));

      additionalInfos.put("freeCode5", getStringValue(execution.getVariable(FREE_CODE_5)));
      additionalInfos.put("freeCode6", getStringValue(execution.getVariable(FREE_CODE_6)));

      additionalInfos.put("freeCode7", getStringValue(execution.getVariable(FREE_CODE_7)));

      additionalInfos.put("freeCode8", getStringValue(execution.getVariable(FREE_CODE_8)));

      additionalInfos.put("freeCode9", getStringValue(execution.getVariable(FREE_CODE_9)));
      additionalInfos.put("freeCode10", getStringValue(execution.getVariable(FREE_CODE_10)));

      additionalInfos.put("freeText15", getStringValue(execution.getVariable("FREE_TEXT_15")));
      additionalInfos.put("modeOfAdvance", getStringValue(execution.getVariable("modeOfAdvance")));

      additionalInfos.put(TYPE_OF_ADVANCE, getStringValue(execution.getVariable(TYPE_OF_ADVANCE)));
      additionalInfos.put(BORROWER_CATEGORY_CODE, getStringValue(execution.getVariable(BORROWER_CATEGORY_CODE)));
      additionalInfos.put(PURPOSE_OF_ADVANCE, getStringValue(execution.getVariable(PURPOSE_OF_ADVANCE)));

      additionalInfos.put(NATURE_OF_ADVANCE, getStringValue(execution.getVariable(NATURE_OF_ADVANCE)));
      additionalInfos.put(CUSTOMER_INDUSTRY_TYPE, getStringValue(execution.getVariable(CUSTOMER_INDUSTRY_TYPE)));
      additionalInfos.put(FREE_TEXT_1, getStringValue(execution.getVariable(SANCTIONED_BY)));
      additionalInfos.put(ADDITIONAL_SPECIAL_CONDITION, getStringValue(execution.getVariable(ADDITIONAL_SPECIAL_CONDITION)));

      String trackNumber = String.valueOf(execution.getVariable(TRACK_NUMBER));

      if (processType.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
      {
        LOGGER.info("######## CREATES LOAN ACCOUNT TO CBS WITH PRODUCT CODE = [{}], REQUEST ID = [{}], TRACKNUMBER = [{}]. {}", productCode, requestId, trackNumber,
            (StringUtils.isBlank(getValidString(execution.getVariable(ACTION_TYPE))) ? "" : " ActionType :" + execution.getVariable(ACTION_TYPE) + "."));
      }
      else
      {
        LOGGER.info("######## CREATES LOAN ACCOUNT TO CBS WITH PRODUCT CODE = [{}], REQUEST ID = [{}]. {}", productCode, requestId,
            (StringUtils.isBlank(getValidString(execution.getVariable(ACTION_TYPE))) ? "" : " ActionType :" + execution.getVariable(ACTION_TYPE) + "."));
      }


      List<Map<String, Object>> coBorrowerInfo = getCoBorrowerInfo(execution);

      AddLoanAccountInput input = new AddLoanAccountInput(genericInfo, additionalInfos, coBorrowerInfo);
      AddLoanAccount addLoanAccount = new AddLoanAccount(newCoreBankingService);

      AddLoanAccountOutput output = addLoanAccount.execute(input);
      String accountNumber = output.getCreatedAccountNumber();

      execution.setVariable(LOAN_ACCOUNT_NUMBER, String.valueOf(accountNumber));

      LOGGER.info("######### Loan account number {} generated.", accountNumber);

      if (processType.equals(INSTANT_LOAN_PROCESS_TYPE_ID) || processType.equals(ONLINE_SALARY_PROCESS_TYPE) || processType.equals(BNPL_PROCESS_TYPE_ID) || processType.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
      {
        Map<String, Serializable> parameters = new HashMap<>();
        parameters.put(LOAN_ACCOUNT_NUMBER, accountNumber);
        parameters.put(FIXED_ACCEPTED_LOAN_AMOUNT_STRING, String.valueOf(execution.getVariable(FIXED_ACCEPTED_LOAN_AMOUNT_STRING)));
        parameters.put(FIXED_ACCEPTED_LOAN_AMOUNT, String.valueOf(execution.getVariable(FIXED_ACCEPTED_LOAN_AMOUNT)));
        parameters.put(ACCEPTED_LOAN_AMOUNT, acceptedLoanAmount);
        Map<ParameterEntityType, Map<String, Serializable>> processParameters = new HashMap<>();
        processParameters.put(getTypeByProcessType(processType), parameters);
        updateProcessParameters(aimServiceRegistry, processRepository, instanceId, processParameters);
        LOGGER.info("######### Loan account number {} saved in Process Parameter table.", execution.getVariable(LOAN_ACCOUNT_NUMBER));
      }
      if (processType.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
      {
        LOGGER.info("######## SUCCESSFUL CREATED LOAN ACCOUNT NUMBER = [{}] WITH PRODUCT CODE = [{}], REQUEST ID = [{}], TRACKNUMBER = [{}]. {}",
            accountNumber, productCode, requestId, trackNumber,
            (StringUtils.isBlank(getValidString(execution.getVariable(ACTION_TYPE))) ? "" : " ActionType :" + execution.getVariable(ACTION_TYPE) + "."));
      }
      else
      {
        LOGGER.info("######## SUCCESSFUL CREATED LOAN ACCOUNT NUMBER = [{}] WITH PRODUCT CODE = [{}], REQUEST ID = [{}]. {}",
          accountNumber, productCode, requestId,
          (StringUtils.isBlank(getValidString(execution.getVariable(ACTION_TYPE))) ? "" : " ActionType :" + execution.getVariable(ACTION_TYPE) + "."));
      }

      updateTaskStatus(execution, "Create Loan Account Task", "Success");
    }
    catch (Exception e)
    {
      if (processType.equals(BNPL_PROCESS_TYPE_ID) || processType.equals(INSTANT_LOAN_PROCESS_TYPE_ID) || processType.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
      {
        e.printStackTrace();
        if (!execution.hasVariable(ERROR_CAUSE))
        {
          updateTaskStatus(execution, "Create Loan Account Task", "Failed");
          execution.setVariable(ERROR_CAUSE, e.getMessage());
        }
        throw new BpmnError("Account Creation", e.getMessage());
      }
      throw new ProcessTaskException(e.getMessage());
    }
  }

  private String getNatureOfAdvn(Map<String, String> input) throws BpmServiceException
  {
    String orgLeasingName = organizationLeasingRepository.getNameByTerminalId(input.get(TERMINAL_ID));
    if (!StringUtils.isBlank(orgLeasingName))
    {
      Map<String, UDField> udFieldsMap = newCoreBankingService.getUDFields(input);
      List<UDFieldValue> natureOfAdvance = udFieldsMap.get("Nature of Advance").getValues();
      for (UDFieldValue fieldValue : natureOfAdvance)
      {
        if (fieldValue.getItemDescription().equals(orgLeasingName) && !StringUtils.isBlank(fieldValue.getItemId()))
        {
          return fieldValue.getItemId();
        }
      }
    }
    return EMPTY_STRING_VALUE;
  }

  private Map<String, Object> getFrequency(String dayPaymentString, Environment environment)
  {
    Map<String, Object> frequency = new HashMap<>();

    frequency.put(START_DT, dayPaymentString);
    frequency.put(CAL, environment.getProperty(PROPERTY_KEY_ACCOUNT_FREQUENCY_CALL));
    frequency.put(TYPE, environment.getProperty(PROPERTY_KEY_ACCOUNT_FREQUENCY_TYPE));
    frequency.put(HOL_STAT, environment.getProperty(PROPERTY_KEY_ACCOUNT_FREQUENCY_HOLIDAY_STATUS));

    frequency.put(WEEK_DAY, EMPTY_STRING_VALUE);
    frequency.put(WEEK_NUM, EMPTY_STRING_VALUE);

    return frequency;
  }

  private String getEqFlag(DelegateExecution execution)
  {
    String repaymentType = String.valueOf(execution.getVariable(REPAYMENT_TYPE));

    if (repaymentType.equalsIgnoreCase(REPAYMENT_EQUAL_PRINCIPLE_PAYMENT) || repaymentType.equalsIgnoreCase(EQUAL_PRINCIPLE_PAYMENT))
    {
      return NO;
    }

    else if (repaymentType.equalsIgnoreCase(REPAYMENT_EQUATED_MONTHLY_INSTALLMENT) || repaymentType.equalsIgnoreCase(EQUATED_MONTHLY_INSTALLMENT))
    {
      return YES;
    }
    else
    {
      return "";
    }
  }

  private List<Map<String, Object>> getCoBorrowerInfo(DelegateExecution execution)
  {
    if (!execution.hasVariable(INDEX_CO_BORROWER))
    {
      return Collections.emptyList();
    }
    int indexCoBorrower = (int) execution.getVariable(INDEX_CO_BORROWER);

    if (indexCoBorrower == 0)
    {
      return Collections.emptyList();
    }
    List<Map<String, Object>> coBorrowerInfo = new ArrayList<>();
    for (int i = 1; i <= indexCoBorrower; i++)
    {
      String coBorrowerCif = String.valueOf(execution.getVariable(CIF_NUMBER_CO_BORROWER + "-" + i));
      String coBorrowerType = String.valueOf(execution.getVariable(CO_BORROWER_TYPE + "-" + i));
      Map<String, Object> coBorrower = new HashMap<>();
      coBorrower.put(CO_BORROWER_CUST_ID, coBorrowerCif);
      coBorrower.put(CO_BORROWER_TYPE, coBorrowerType);
      coBorrowerInfo.add(coBorrower);
    }

    return coBorrowerInfo;
  }

  @SuppressWarnings("unchecked")
  private void updateCollateralOnExecution(DelegateExecution execution, String instanceId)
  {
    CaseService caseService = execution.getProcessEngine().getCaseService();
    Map<String, Serializable> collaterals = new HashMap<>();
    if (execution.getVariable(COLLATERAL_LIST) instanceof List)
    {
      collaterals = ((List<Map<String, Serializable>>) execution.getVariable(COLLATERAL_LIST)).get(0);
    }
    else if (execution.getVariable(COLLATERAL_LIST) instanceof Map)
    {
      collaterals = (Map<String, Serializable>) execution.getVariable(COLLATERAL_LIST);
    }

    for (Map.Entry<String, Serializable> entry : collaterals.entrySet())
    {
      Collateral collateral = (Collateral) caseService.getVariable(instanceId, entry.getKey());
      execution.setVariable(entry.getKey(), collateral);
    }
  }

  private void validateLinkedColAmount(DelegateExecution execution) throws BpmServiceException
  {
    if (!execution.hasVariable(COLLATERAL_LIST))
    {
      return;
    }
    BigDecimal fixedAcceptedAmount = new BigDecimal(String.valueOf(execution.getVariable(FIXED_ACCEPTED_LOAN_AMOUNT)));
    BigDecimal totalColAmount = new BigDecimal(0);
    LinkedHashMap<String, Map<String, Object>> colList = (LinkedHashMap<String, Map<String, Object>>) execution.getVariable(COLLATERAL_LIST);

    for (Map.Entry<String, Map<String, Object>> colEntry : colList.entrySet())
    {
      BigDecimal colAmount = new BigDecimal(String.valueOf(colEntry.getValue().get(LOAN_AMOUNT)));
      BigDecimal amountOfAssessment = new BigDecimal(String.valueOf(colEntry.getValue().get(AMOUNT_OF_ASSESSMENT)));
      if (colAmount.compareTo(amountOfAssessment) > 0)
      {
        throw new BpmServiceException("Linked collateral amount cannot be greater than amount of assessment!");
      }
      totalColAmount = totalColAmount.add(colAmount);
    }
    if (totalColAmount.compareTo(fixedAcceptedAmount) > 0)
    {
      throw new BpmServiceException("Linked collateral amount cannot be greater than loan accepted amount!");
    }
  }

  private BigDecimal getAcceptedLoanAmount(DelegateExecution execution) throws BpmServiceException
  {
    BigDecimal acceptedLoanAmount;
    BigDecimal requestedLoanAmount= (BigDecimal) execution.getVariable("requestedLoanAmount");
    Object fixedAcceptedLoanAmount = getValidString(execution.getVariable(PROCESS_TYPE_ID)).equals(ONLINE_SALARY_PROCESS_TYPE) && requestedLoanAmount != null ? requestedLoanAmount
        : execution.getVariable(FIXED_ACCEPTED_LOAN_AMOUNT);

    if (fixedAcceptedLoanAmount instanceof Integer)
    {
      acceptedLoanAmount = BigDecimal.valueOf((Integer) fixedAcceptedLoanAmount);
    }
    else if (fixedAcceptedLoanAmount instanceof Long)
    {
      acceptedLoanAmount = BigDecimal.valueOf((long) fixedAcceptedLoanAmount);
    }
    else
    {
      acceptedLoanAmount = toBigDecimal(fixedAcceptedLoanAmount);
    }
    boolean hasActiveLoanAccount = execution.getVariable(HAS_ACTIVE_LOAN_ACCOUNT) != null && (boolean) execution.getVariable(HAS_ACTIVE_LOAN_ACCOUNT);
    String processType = String.valueOf(execution.getVariable(PROCESS_TYPE_ID));
    if (hasActiveLoanAccount && (!processType.equals(INSTANT_LOAN_PROCESS_TYPE_ID) && !processType.equals(ONLINE_LEASING_PROCESS_TYPE_ID)))
    {
      BigDecimal totalClosingAmount = (BigDecimal) execution.getVariable(TOTAL_CLOSING_AMOUNT);
      return totalClosingAmount.add(acceptedLoanAmount);
    }
    return acceptedLoanAmount;
  }

  private void updateTaskStatus(DelegateExecution execution, String serviceTask, String state)
  {
    Map<String, String> completedTasksMap = new HashMap<>();
    List<Map<String, String>> completedTasksList;

    int index;

    if (null != execution.getVariable("instantLoanTaskIndex"))
    {
      index = Integer.parseInt(getValidString(execution.getVariable("instantLoanTaskIndex")));
      completedTasksList = (List<Map<String, String>>) execution.getVariable("completedTasks");
    }
    else
    {
      index = -1;
      completedTasksList = new ArrayList<>();
    }
    completedTasksMap.put(serviceTask, state);
    completedTasksList.add(index + 1, completedTasksMap);

    execution.setVariable("completedTasks", completedTasksList);
    execution.setVariable("instantLoanTaskIndex", index);
  }
}
