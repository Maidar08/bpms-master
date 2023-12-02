package mn.erin.bpms.loan.consumption.service_task;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.bpms.process.base.ProcessTaskException;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.account.XacAccount;
import mn.erin.domain.bpm.service.NewCoreBankingService;
import mn.erin.domain.bpm.usecase.loan.GetAccountsList;

import static mn.erin.domain.bpm.BpmModuleConstants.ACTION_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.BNPL_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CURRENT_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.ERROR_CAUSE;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.NUMBER_OF_PAYMENTS;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.TRACK_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

/**
 * @author Zorig
 */
public class SetAccountCreationFieldsTask implements JavaDelegate
{
  private final AuthenticationService authenticationService;
  private final NewCoreBankingService newCoreBankingService;

  private static final Logger LOGGER = LoggerFactory.getLogger(SetAccountCreationFieldsTask.class);

  public SetAccountCreationFieldsTask(AuthenticationService authenticationService, NewCoreBankingService newCoreBankingService)
  {
    this.authenticationService = authenticationService;
    this.newCoreBankingService = newCoreBankingService;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String processType = String.valueOf(execution.getVariable(PROCESS_TYPE_ID));
    try
    {
      String registrationNumber = (String) execution.getVariable("registerNumber");
      String requestId = (String) execution.getVariable(PROCESS_REQUEST_ID);
      String userId = authenticationService.getCurrentUserId();
      String trackNumber = String.valueOf(execution.getVariable(TRACK_NUMBER));
      if (processType.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
      {
        LOGGER
            .info("#########  Setting Account Creation Fields.. Register Number: {}, Request ID: {}, Tracknumber: {}, User ID: {}. {}", registrationNumber, requestId, trackNumber, userId,
                (StringUtils.isBlank(getValidString(execution.getVariable(ACTION_TYPE))) ? "." : ", ActionType :" + execution.getVariable(ACTION_TYPE) + "."));
      }
      else
      {
        LOGGER
            .info("#########  Setting Account Creation Fields.. Register Number: {}, Request ID: {}, User ID: {}. {}", registrationNumber, requestId, userId,
                (StringUtils.isBlank(getValidString(execution.getVariable(ACTION_TYPE))) ? "." : ", ActionType :" + execution.getVariable(ACTION_TYPE) + "."));
      }

      BigDecimal yearlyInterestRate = null;
      BigDecimal depositInterestRate = null;

      if (execution.getVariable("yearlyInterestRate") == null)
      {
        Double interestRate = Double.parseDouble(String.valueOf(execution.getVariable("interest_rate")));

        yearlyInterestRate = BigDecimal.valueOf(interestRate).multiply(BigDecimal.valueOf(12));
        yearlyInterestRate = yearlyInterestRate.setScale(2, RoundingMode.HALF_UP);
      }
      else
      {
        yearlyInterestRate = getInterestRate(execution);
        yearlyInterestRate = yearlyInterestRate.setScale(2, RoundingMode.HALF_UP);
      }

      depositInterestRate = yearlyInterestRate.multiply(BigDecimal.valueOf(.20));
      depositInterestRate = depositInterestRate.setScale(2, RoundingMode.HALF_UP);

      execution.setVariable("yearlyInterestRateString", yearlyInterestRate.toString());
      execution.setVariable("yearlyInterestRate", yearlyInterestRate);
      execution.setVariable("yearlyInterestRateStringPercentage", yearlyInterestRate.toString() + "%");

      execution.setVariable("depositInterestRateString", depositInterestRate.toString() + "%");
      execution.setVariable("depositInterestRate", depositInterestRate);

      //---------------------------------------------------------------------------------

      if (execution.getVariable("currentAccountNumber") != null)
      {
        //set account Branch Number

        String currentAccountNumber = (String) execution.getVariable("currentAccountNumber");

        String regNo = (String) execution.getVariable("registerNumber");
        String customerNumber = (String) execution.getVariable("cifNumber");
        Map<String, String> input = new HashMap<>();
        input.put(REGISTER_NUMBER, regNo);
        input.put(CIF_NUMBER, customerNumber);
        input.put(PROCESS_TYPE_ID, String.valueOf(execution.getVariable(PROCESS_TYPE_ID)));
        input.put(PHONE_NUMBER, String.valueOf(execution.getVariable(PHONE_NUMBER)));
        input.put(CURRENT_ACCOUNT_NUMBER, currentAccountNumber);
        String accountBranchNumber = getAccountBranchNumber(input);
        execution.setVariable("accountBranchNumber", accountBranchNumber);
        //and dayOfPayment

        Date firstPaymentDate = (Date) execution.getVariable("firstPaymentDate");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(firstPaymentDate);
        Integer dayOfPayment = calendar.get(Calendar.DAY_OF_MONTH);

        execution.setVariable("dayOfPayment", dayOfPayment.toString());
      }

      Object term = execution.getVariable("term");
      if (!processType.equals(BNPL_PROCESS_TYPE_ID) && !processType.equals(INSTANT_LOAN_PROCESS_TYPE_ID))
      {
        if (null == term)
        {
          execution.setVariable(NUMBER_OF_PAYMENTS, String.valueOf(0));
        }
        else if (term instanceof Long)
        {
          long consumptionLoanTerm = (long) term;
          execution.setVariable(NUMBER_OF_PAYMENTS, String.valueOf(consumptionLoanTerm));
        }
        else if (term instanceof Integer)
        {
          Integer consumptionLoanTerm = (Integer) term;
          execution.setVariable(NUMBER_OF_PAYMENTS, String.valueOf(consumptionLoanTerm));
        }
        else if (term instanceof Double)
        {
          double consumptionLoanTerm = (Double) term;
          execution.setVariable(NUMBER_OF_PAYMENTS, String.valueOf(consumptionLoanTerm));
        }
      }
      if (processType.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
      {
        LOGGER.info("######### Finished Setting Account Creation Fields with REQUEST ID = [{}] WITH TRACKNUMBER = [{}]. {}", requestId, trackNumber,
            (StringUtils.isBlank(getValidString(execution.getVariable(ACTION_TYPE))) ? "" : " ActionType :" + execution.getVariable(ACTION_TYPE) + "."));
      }
      else
      {
        LOGGER.info("######### Finished Setting Account Creation Fields with REQUEST ID = [{}]. {}", requestId,
            (StringUtils.isBlank(getValidString(execution.getVariable(ACTION_TYPE))) ? "" : " ActionType :" + execution.getVariable(ACTION_TYPE) + "."));
      }
    }
    catch (Exception e)
    {
      if (processType.equals(BNPL_PROCESS_TYPE_ID) || processType.equals(INSTANT_LOAN_PROCESS_TYPE_ID))
      {
        e.printStackTrace();
        if (!execution.hasVariable(ERROR_CAUSE))
        {
          execution.setVariable(ERROR_CAUSE, e.getMessage());
        }
        throw new BpmnError("Account Creation", e.getMessage());
      }
      throw new ProcessTaskException(e.getMessage());
    }
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

  private BigDecimal getInterestRate(DelegateExecution execution) throws ProcessTaskException
  {
    String interestRateString = (String) execution.getVariable("yearlyInterestRateString");

    try
    {
      return new BigDecimal(interestRateString);
    }
    catch (Exception e)
    {
      String errorCode = "BPMS076";
      throw new ProcessTaskException(errorCode, "Invalid percentage!");
    }
  }
}
