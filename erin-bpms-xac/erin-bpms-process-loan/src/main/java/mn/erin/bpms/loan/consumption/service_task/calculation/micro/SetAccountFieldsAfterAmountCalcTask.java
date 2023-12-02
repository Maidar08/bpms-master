package mn.erin.bpms.loan.consumption.service_task.calculation.micro;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.bpms.loan.consumption.utils.NumberUtils;
import mn.erin.bpms.process.base.ProcessTaskException;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.account.XacAccount;
import mn.erin.domain.bpm.service.NewCoreBankingService;
import mn.erin.domain.bpm.usecase.loan.GetAccountsList;

import static mn.erin.bpms.loan.consumption.constant.CamundaTaskDefinitionKeyConstants.TASK_DEF_KEY_MICRO_CREATE_LOAN_ACCOUNT;
import static mn.erin.bpms.loan.consumption.utils.DelegationExecutionUtils.setVariablesOnAllActiveTasks;
import static mn.erin.domain.bpm.BpmModuleConstants.ACCEPTED_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.ACCOUNT_BRANCH_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_PROVIDED_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.CURRENT_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CURRENT_ASSETS_RATIO;
import static mn.erin.domain.bpm.BpmModuleConstants.DAY_OF_PAYMENT;
import static mn.erin.domain.bpm.BpmModuleConstants.DEBT_TO_INCOME_ASSET;
import static mn.erin.domain.bpm.BpmModuleConstants.DEBT_TO_INCOME_RATIO;
import static mn.erin.domain.bpm.BpmModuleConstants.DEBT_TO_SOLVENCY_RATIO;
import static mn.erin.domain.bpm.BpmModuleConstants.DEPOSIT_INTEREST_RATE;
import static mn.erin.domain.bpm.BpmModuleConstants.DEPOSIT_INTEREST_RATE_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.FIRST_PAYMENT_DATE;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ACCEPTED_LOAN_AMOUNT_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.INTEREST_RATE;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_TERM;
import static mn.erin.domain.bpm.BpmModuleConstants.NUMBER_OF_PAYMENTS;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.YEARLY_INTEREST_RATE;
import static mn.erin.domain.bpm.BpmModuleConstants.YEARLY_INTEREST_RATE_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.YEARLY_INTEREST_RATE_STRING_PERCENTAGE;

/**
 * @author Tamir
 */
public class SetAccountFieldsAfterAmountCalcTask implements JavaDelegate
{
  private final NewCoreBankingService newCoreBankingService;
  private final AuthenticationService authenticationService;

  private static final Logger LOGGER = LoggerFactory.getLogger(SetAccountFieldsAfterAmountCalcTask.class);

  public SetAccountFieldsAfterAmountCalcTask(NewCoreBankingService newCoreBankingService,
      AuthenticationService authenticationService)
  {
    this.newCoreBankingService = newCoreBankingService;
    this.authenticationService = authenticationService;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    ProcessEngine processEngine = execution.getProcessEngine();
    RuntimeService runtimeService = processEngine.getRuntimeService();
    CaseService caseService = processEngine.getCaseService();

    String caseInstanceId = (String) execution.getVariable(CASE_INSTANCE_ID);

    String microAccountProcessId = getActiveMicroAccountProcessId(execution, processEngine);


    String registerNumber = (String) execution.getVariable(REGISTER_NUMBER);
    String requestId = (String) execution.getVariable(PROCESS_REQUEST_ID);
    String userId = authenticationService.getCurrentUserId();

    LOGGER.info("################ AFTER MICRO LOAN AMOUNT TASK, MICRO CREATE LOAN ACCOUNT ACTIVE PROCESS ID = [{}], REQUEST ID = [{}]", microAccountProcessId, requestId);
    LOGGER.info("#########  AFTER MICRO LOAN AMOUNT TASK, Setting Account Creation Fields after loan amount calculation : "
        + " Register Number = [{}], Request ID = [{}], User ID: = [{}]", registerNumber, requestId, userId);

    BigDecimal yearlyInterestRate = null;
    BigDecimal depositInterestRate = null;

    if (execution.getVariable(INTEREST_RATE) != null)
    {
      yearlyInterestRate = getInterestRate(execution, INTEREST_RATE);
      yearlyInterestRate = yearlyInterestRate.setScale(2, RoundingMode.HALF_UP);
    }
    else if (execution.getVariable(YEARLY_INTEREST_RATE) == null)
    {
      yearlyInterestRate = BigDecimal.ZERO;
    }
    else if (execution.getVariable(YEARLY_INTEREST_RATE_STRING) != null)
    {
      yearlyInterestRate = getInterestRate(execution, YEARLY_INTEREST_RATE_STRING);
      yearlyInterestRate = yearlyInterestRate.setScale(2, RoundingMode.HALF_UP);
    }

    depositInterestRate = yearlyInterestRate.multiply(BigDecimal.valueOf(.20));
    depositInterestRate = depositInterestRate.setScale(2, RoundingMode.HALF_UP);

    execution.setVariable(YEARLY_INTEREST_RATE_STRING, yearlyInterestRate.toString());
    execution.setVariable(YEARLY_INTEREST_RATE, yearlyInterestRate);
    execution.setVariable(YEARLY_INTEREST_RATE_STRING_PERCENTAGE, yearlyInterestRate.toString() + "%");

    execution.setVariable(DEPOSIT_INTEREST_RATE_STRING, depositInterestRate.toString() + "%");
    execution.setVariable(DEPOSIT_INTEREST_RATE, depositInterestRate);

    // SETs variable to account creation execution.
    if (null != microAccountProcessId)
    {
      LOGGER.info("######### MICRO LOAN ACCOUNT TASK IS ACTIVE,"
          + " SETS ACCOUNT VARIABLES TO RUN TIME SERVICE WHEN CALCULATE LOAN AMOUNT,"
          + " REQUEST ID = [{}]", requestId);

      runtimeService.setVariable(microAccountProcessId, YEARLY_INTEREST_RATE_STRING, yearlyInterestRate.toString());
      runtimeService.setVariable(microAccountProcessId, YEARLY_INTEREST_RATE, yearlyInterestRate);
      runtimeService.setVariable(microAccountProcessId, YEARLY_INTEREST_RATE_STRING_PERCENTAGE, yearlyInterestRate.toString() + "%");

      runtimeService.setVariable(microAccountProcessId, DEPOSIT_INTEREST_RATE_STRING, depositInterestRate.toString() + "%");
      runtimeService.setVariable(microAccountProcessId, DEPOSIT_INTEREST_RATE, depositInterestRate);

      caseService.setVariable(caseInstanceId, YEARLY_INTEREST_RATE_STRING, yearlyInterestRate.toString());
      caseService.setVariable(caseInstanceId, YEARLY_INTEREST_RATE, yearlyInterestRate);
      caseService.setVariable(caseInstanceId, YEARLY_INTEREST_RATE_STRING_PERCENTAGE, yearlyInterestRate.toString() + "%");

      caseService.setVariable(caseInstanceId, DEPOSIT_INTEREST_RATE_STRING, depositInterestRate.toString() + "%");
      caseService.setVariable(caseInstanceId, DEPOSIT_INTEREST_RATE, depositInterestRate);
    }

    Object acceptedLoanAmountObj = execution.getVariable(ACCEPTED_LOAN_AMOUNT);
    Double acceptedLoanAmount = (double) 0;

    if (acceptedLoanAmountObj instanceof Integer)
    {
      Integer acceptedLoanAmountInt = (int) acceptedLoanAmountObj;
      acceptedLoanAmount = acceptedLoanAmountInt.doubleValue();
    }
    else if (acceptedLoanAmountObj instanceof Double)
    {
      acceptedLoanAmount = (double) acceptedLoanAmountObj;
    }
    else if (acceptedLoanAmountObj instanceof Long)
    {
      Long acceptedLoanAmountInt = (long) acceptedLoanAmountObj;
      acceptedLoanAmount = acceptedLoanAmountInt.doubleValue();
    }
    String acceptedLoanAmountStr = NumberUtils.getThousandSeparatedString(acceptedLoanAmount.doubleValue());

    execution.setVariable(FIXED_ACCEPTED_LOAN_AMOUNT_STRING, acceptedLoanAmountStr);



    // set accepted loan amount variables on all active task's runtime service
    Map<String, Object> runtimeVariables = new HashMap<>();
    runtimeVariables.put(ACCEPTED_LOAN_AMOUNT, acceptedLoanAmountObj);
    runtimeVariables.put(FIXED_ACCEPTED_LOAN_AMOUNT_STRING, acceptedLoanAmountStr);
    runtimeVariables.put(DEBT_TO_SOLVENCY_RATIO, execution.getVariable(DEBT_TO_SOLVENCY_RATIO));
    runtimeVariables.put(DEBT_TO_INCOME_RATIO, execution.getVariable(DEBT_TO_INCOME_RATIO));
    runtimeVariables.put(CURRENT_ASSETS_RATIO, execution.getVariable(CURRENT_ASSETS_RATIO));
    runtimeVariables.put(DEBT_TO_INCOME_ASSET, execution.getVariable(DEBT_TO_INCOME_ASSET));
    runtimeVariables.put(COLLATERAL_PROVIDED_AMOUNT, execution.getVariable(COLLATERAL_PROVIDED_AMOUNT));

    runtimeVariables.put(YEARLY_INTEREST_RATE_STRING, yearlyInterestRate.toString());
    runtimeVariables.put(YEARLY_INTEREST_RATE, yearlyInterestRate);
    runtimeVariables.put(YEARLY_INTEREST_RATE_STRING_PERCENTAGE, yearlyInterestRate.toString() + "%");
    runtimeVariables.put(DEPOSIT_INTEREST_RATE_STRING, depositInterestRate.toString() + "%");
    runtimeVariables.put(DEPOSIT_INTEREST_RATE, depositInterestRate);
    setVariablesOnAllActiveTasks(execution, runtimeVariables);

    if (null != microAccountProcessId)
    {
      caseService.setVariable(caseInstanceId, FIXED_ACCEPTED_LOAN_AMOUNT_STRING, acceptedLoanAmountStr);
      runtimeService.setVariable(microAccountProcessId, FIXED_ACCEPTED_LOAN_AMOUNT_STRING, acceptedLoanAmountStr);
    }

    if (execution.getVariable(CURRENT_ACCOUNT_NUMBER) != null)
    {

      String currentAccountNumber = (String) execution.getVariable(CURRENT_ACCOUNT_NUMBER);

      String regNo = (String) execution.getVariable(REGISTER_NUMBER);
      String customerNumber = (String) execution.getVariable(CIF_NUMBER);
      Map<String, String> input = new HashMap<>();
      input.put(REGISTER_NUMBER, regNo);
      input.put(CIF_NUMBER, customerNumber);
      input.put(PROCESS_TYPE_ID, String.valueOf(execution.getVariable(PROCESS_TYPE_ID)));
      input.put(PHONE_NUMBER, String.valueOf(execution.getVariable(PHONE_NUMBER)));
      input.put(CURRENT_ACCOUNT_NUMBER, currentAccountNumber);

      String accountBranchNumber = getAccountBranchNumber(input);
      execution.setVariable(ACCOUNT_BRANCH_NUMBER, accountBranchNumber);

      if (null != microAccountProcessId)
      {
        caseService.setVariable(caseInstanceId, ACCOUNT_BRANCH_NUMBER, accountBranchNumber);
        runtimeService.setVariable(microAccountProcessId, ACCOUNT_BRANCH_NUMBER, accountBranchNumber);
      }

      Date firstPaymentDate = (Date) execution.getVariable(FIRST_PAYMENT_DATE);

      if (null != firstPaymentDate)
      {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(firstPaymentDate);
        Integer dayOfPayment = calendar.get(Calendar.DAY_OF_MONTH);

        execution.setVariable(DAY_OF_PAYMENT, dayOfPayment.toString());
        if (null != microAccountProcessId)
        {
          caseService.setVariable(caseInstanceId, DAY_OF_PAYMENT, dayOfPayment.toString());
          runtimeService.setVariable(microAccountProcessId, DAY_OF_PAYMENT, dayOfPayment.toString());
        }
      }
    }

    Object loanTerm = execution.getVariable(LOAN_TERM);

    if (null == loanTerm)
    {
      execution.setVariable(NUMBER_OF_PAYMENTS, String.valueOf(0));

      if (null != microAccountProcessId)
      {
        caseService.setVariable(caseInstanceId, NUMBER_OF_PAYMENTS, String.valueOf(0));
        runtimeService.setVariable(microAccountProcessId, NUMBER_OF_PAYMENTS, String.valueOf(0));
      }
    }
    else if (loanTerm instanceof Long)
    {
      ///
      long microLoanTerm = (long) loanTerm;
      execution.setVariable(NUMBER_OF_PAYMENTS, String.valueOf(microLoanTerm));

      if (null != microAccountProcessId)
      {
        caseService.setVariable(caseInstanceId, NUMBER_OF_PAYMENTS, String.valueOf(microLoanTerm));
        runtimeService.setVariable(microAccountProcessId, NUMBER_OF_PAYMENTS, String.valueOf(microLoanTerm));
      }
    }
    else if (loanTerm instanceof Integer)
    {
      Integer microLoanTerm = (Integer) loanTerm;
      ///
      execution.setVariable(NUMBER_OF_PAYMENTS, String.valueOf(microLoanTerm));

      if (null != microAccountProcessId)
      {
        caseService.setVariable(caseInstanceId, NUMBER_OF_PAYMENTS, String.valueOf(microLoanTerm));
        runtimeService.setVariable(microAccountProcessId, NUMBER_OF_PAYMENTS, String.valueOf(microLoanTerm));
      }
    }
    else if (loanTerm instanceof Double)
    {
      double microLoanTerm = (Double) loanTerm;
      ///
      execution.setVariable(NUMBER_OF_PAYMENTS, String.valueOf(microLoanTerm));

      if (null != microAccountProcessId)
      {
        caseService.setVariable(caseInstanceId, NUMBER_OF_PAYMENTS, String.valueOf(microLoanTerm));
        runtimeService.setVariable(microAccountProcessId, NUMBER_OF_PAYMENTS, String.valueOf(microLoanTerm));
      }
    }
    else if (loanTerm instanceof String)
    {
      //
      execution.setVariable(NUMBER_OF_PAYMENTS, loanTerm);

      if (null != microAccountProcessId)
      {
        caseService.setVariable(caseInstanceId, NUMBER_OF_PAYMENTS, loanTerm);
        runtimeService.setVariable(microAccountProcessId, NUMBER_OF_PAYMENTS, loanTerm);
      }
    }

    LOGGER.info("######### Finished Setting Account Creation Fields for MICRO with REQUEST ID = [{}]", requestId);
  }

  private String getActiveMicroAccountProcessId(DelegateExecution execution, ProcessEngine processEngine)
  {
    String caseInstanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));

    TaskService taskService = processEngine.getTaskService();

    List<Task> activeTasks = taskService.createTaskQuery()
        .caseInstanceId(caseInstanceId)
        .active()
        .list();

    for (Task activeTask : activeTasks)
    {
      String taskDefinitionKey = activeTask.getTaskDefinitionKey();

      if (taskDefinitionKey.equalsIgnoreCase(TASK_DEF_KEY_MICRO_CREATE_LOAN_ACCOUNT))
      {
        return activeTask.getProcessInstanceId();
      }
    }

    return null;
  }

  private String getAccountBranchNumber(Map<String, String> input) throws UseCaseException
  {
    GetAccountsList getAccountsList = new GetAccountsList(newCoreBankingService);

    List<XacAccount> xacAccounts = getAccountsList.execute(input).getAccountList();

    for (XacAccount xacAccount : xacAccounts)
    {
      if (xacAccount.getId().getId().equalsIgnoreCase(input.get(CURRENT_ACCOUNT_NUMBER)))
      {
        return xacAccount.getBranchId();
      }
    }
    return null;
  }

  private BigDecimal getInterestRate(DelegateExecution execution, String id) throws ProcessTaskException
  {
    String interestRateString = (String) execution.getVariable(id);

    try
    {
      return new BigDecimal(interestRateString);
    }
    catch (Exception e)
    {
      throw new ProcessTaskException("BPMS076", "Invalid percentage!");
    }
  }
}
