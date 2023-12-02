package mn.erin.bpms.loan.consumption.service_task;

import java.util.Map;
import java.util.Objects;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.CoreBankingService;
import mn.erin.domain.bpm.usecase.customer.GetCustomerLoanPeriod;
import mn.erin.domain.bpm.usecase.customer.GetCustomerLoanPeriodInput;

import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;

/**
 * @author Zorig
 */
public class GetLoanPeriodFromXacTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GetLoanPeriodFromXacTask.class);
  private final CoreBankingService coreBankingService;
  private final AuthenticationService authenticationService;

  public GetLoanPeriodFromXacTask(CoreBankingService coreBankingService, AuthenticationService authenticationService)
  {
    this.authenticationService = Objects.requireNonNull(authenticationService, "Authentication service is required!");
    this.coreBankingService = Objects.requireNonNull(coreBankingService, "Core banking service is required!");
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String registrationNumber = (String) execution.getVariable("registerNumber");
    String requestId = (String )execution.getVariable(PROCESS_REQUEST_ID);
    String userId = authenticationService.getCurrentUserId();
    LOGGER.info("#########  Retrieving Loan Period From Xac Task.. Register Number: " + registrationNumber + ", Request ID: " + requestId + " , User ID: " + userId);

    Map<String, Object> variables = execution.getVariables();

    //validation make sure it is there/null check

    //Make sure customer cif is existent/null
    String customerCif = (String)variables.get(CIF_NUMBER);
    int loanPeriod = getCustomerLoanPeriod(customerCif);
    String loanPeriodString = String.valueOf(loanPeriod);

    if (variables.containsKey("loan_period"))
    {
      execution.removeVariable("loan_period");
    }

    if (variables.containsKey("loan_period_string"))
    {
      execution.removeVariable("loan_period_string");
    }

    execution.setVariable("loan_period", loanPeriod);
    execution.setVariable("loan_period_string", loanPeriodString);

    LOGGER.info("*********** Successfully Retrieved Loan Period from Xac");
  }

  private int getCustomerLoanPeriod(String customerCif) throws UseCaseException
  {
    GetCustomerLoanPeriodInput input = new GetCustomerLoanPeriodInput(customerCif);
    GetCustomerLoanPeriod getCustomerLoanPeriod = new GetCustomerLoanPeriod(coreBankingService);
    return getCustomerLoanPeriod.execute(input);
  }
}
