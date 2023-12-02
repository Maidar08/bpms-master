package mn.erin.bpms.loan.consumption.service_task;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Objects;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.service.AuthenticationService;

import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;

/**
 * @author Zorig
 */
public class GetResourceToLoanAmountRatioTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GetResourceToLoanAmountRatioTask.class);
  private static final String RESOURCE_TO_LOAN_AMOUNT_RATIO = "resource_loan_amount_ratio";

  private final AuthenticationService authenticationService;

  public GetResourceToLoanAmountRatioTask(AuthenticationService authenticationService)
  {
    this.authenticationService = Objects.requireNonNull(authenticationService, "Authentication service is required!");
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String registrationNumber = (String) execution.getVariable("registerNumber");
    String requestId = (String )execution.getVariable(PROCESS_REQUEST_ID);
    String userId = authenticationService.getCurrentUserId();
    LOGGER.info("#########  Getting Resource To Loan Amount Ratio Task.. Register Number: " + registrationNumber + ", Request ID: " + requestId + " , User ID: " + userId);

    Map<String, Object> variables = execution.getVariables();

    //validation make sure it is there/null check
    BigDecimal resource = (BigDecimal) variables.get("resource");
    BigDecimal requestedLoanAmount = null;

    Object amount = variables.get("amount");

    if (amount instanceof Integer)
    {
      requestedLoanAmount = BigDecimal.valueOf((int)amount);
    }
    else if (amount instanceof BigDecimal)
    {
      requestedLoanAmount = (BigDecimal)amount;
    }
    else if (variables.get("amount") instanceof Long)
    {
      requestedLoanAmount = BigDecimal.valueOf((long) amount);
    }

    //set scale and rounding mode when dividing
    BigDecimal resourceToLoanAmountRatio = null;

    if (requestedLoanAmount.compareTo(BigDecimal.ZERO) == 0)
    {
      resourceToLoanAmountRatio = BigDecimal.ZERO;
    }
    else
    {
      resourceToLoanAmountRatio = resource.divide(requestedLoanAmount.setScale(2, RoundingMode.FLOOR), RoundingMode.FLOOR).setScale(2, RoundingMode.FLOOR);
    }
    Double resourceToLoanAmountRatioDouble = resourceToLoanAmountRatio.doubleValue();

    if (variables.containsKey(RESOURCE_TO_LOAN_AMOUNT_RATIO))
    {
      execution.removeVariable(RESOURCE_TO_LOAN_AMOUNT_RATIO);
    }

    if (execution.getVariable("loanClassName") == null)
    {
      execution.setVariable("loanClassName", "Хэвийн");
    }

    execution.setVariable(RESOURCE_TO_LOAN_AMOUNT_RATIO, resourceToLoanAmountRatioDouble);

    LOGGER.info("*********** Successfully calculated Resource to Loan Amount Ratio");
  }
}
