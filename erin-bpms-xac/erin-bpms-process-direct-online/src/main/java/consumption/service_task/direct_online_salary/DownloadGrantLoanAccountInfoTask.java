package consumption.service_task.direct_online_salary;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import mn.erin.domain.bpm.model.loan.Loan;
import mn.erin.domain.bpm.service.DirectOnlineCoreBankingService;
import mn.erin.domain.bpm.service.NewCoreBankingService;

import static mn.erin.domain.bpm.BpmLoanContractConstants.ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_INFO;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.TRACK_NUMBER;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;
import static mn.erin.domain.bpm.util.process.DigitalLoanUtils.getLogPrefix;

public class DownloadGrantLoanAccountInfoTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DownloadGrantLoanAccountInfoTask.class);
  private final DirectOnlineCoreBankingService directOnlineCoreBankingService;
  private final NewCoreBankingService newCoreBankingService;
  private final Environment environment;

  public DownloadGrantLoanAccountInfoTask(DirectOnlineCoreBankingService directOnlineCoreBankingService,
      NewCoreBankingService newCoreBankingService, Environment environment)
  {
    this.directOnlineCoreBankingService = directOnlineCoreBankingService;
    this.newCoreBankingService = newCoreBankingService;
    this.environment = environment;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    GetLoanAccounts getLoanAccounts = new GetLoanAccounts(newCoreBankingService, environment);
    Map<String, String> input = new HashMap<>();
    input.put(REGISTER_NUMBER, (String) execution.getVariable(REGISTER_NUMBER));
    input.put(CIF_NUMBER, (String) execution.getVariable(CIF_NUMBER));
    input.put(PROCESS_TYPE_ID, String.valueOf(execution.getVariable(PROCESS_TYPE_ID)));
    input.put(PHONE_NUMBER, String.valueOf(execution.getVariable(PHONE_NUMBER)));
    Map<String, String> accounts = getLoanAccounts.execute(input);
    Map<String, Loan> loanInfo = new HashMap<>();
    String trackNumber = String.valueOf(execution.getVariable(TRACK_NUMBER));
    String processTypeId = String.valueOf(execution.getVariable(PROCESS_TYPE_ID));
    for (Map.Entry<String, String> account : accounts.entrySet())
    {
      input.put(ACCOUNT_NUMBER, account.getKey());
      input.put(PHONE_NUMBER, String.valueOf(execution.getVariable(PHONE_NUMBER)));

      Loan loan = directOnlineCoreBankingService.getDbsrList(input);
      loan.setType(account.getValue());
      loanInfo.put(loan.getId().getId(), loan);
    }

    execution.setVariable(LOAN_INFO, loanInfo);
    for (Map.Entry<String, Loan> entry : loanInfo.entrySet())
    {
      Loan value = entry.getValue();
      String log = getLogPrefix(getValidString(execution.getVariable(PROCESS_TYPE_ID)));
      if (processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
      {
        LOGGER.info(
            "{} LOAN ACCOUNT DISBURSEMENT DETAIL SUCCESSFULLY PERSISTED WITH ACCOUNTS = [{}], DISBURSED AMOUNT = [{}], DISBURSED DATE = [{}], MATURITY DATE = [{}], TRACKNUMBER = [{}]",
            log, accounts, value.getAmount(), value.getStartDate(), value.getExpireDate(), trackNumber);
      }
      else
      {
        LOGGER.info(
            "{} LOAN ACCOUNT DISBURSEMENT DETAIL SUCCESSFULLY PERSISTED WITH ACCOUNTS = [{}], DISBURSED AMOUNT = [{}], DISBURSED DATE = [{}], MATURITY DATE = [{}]",
            log, accounts, value.getAmount(), value.getStartDate(), value.getExpireDate());
      }
    }
  }
}