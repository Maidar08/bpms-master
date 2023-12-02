package mn.erin.bpms.loan.consumption.service_task;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Objects;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.bpms.process.base.ProcessTaskException;
import mn.erin.domain.aim.service.AuthenticationService;

import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;

/**
 * @author Zorig
 */
public class CalculateInterestRateTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(CalculateInterestRateTask.class);

  private final AuthenticationService authenticationService;

  public CalculateInterestRateTask(AuthenticationService authenticationService)
  {
    this.authenticationService = Objects.requireNonNull(authenticationService, "Authentication service is required!");
  }

  private static final String KEY_WORKER = "key_worker";

  private static final String ORGANIZATION_LEVEL_SCORES = "organization_level_scores";
  private static final String ORGANIZATION_LEVEL_SCORE_HIGH = "organization_level_score_high";
  private static final String ORGANIZATION_LEVEL_SCORE_LOW = "organization_level_score_low";
  private static final String ORGANIZATION_LEVEL_SCORE_RANGE = "organization_level_score_range";
  private static final String ORGANIZATION_KEY_WORKER_SCORE = "organization_key_worker_score";

  private static final String IMPORTANCE_LEVEL = "importance_level";
  private static final String LOAN_HISTORY_IMPORTANCE = "loan_history_importance";
  private static final String LOAN_PERIOD_IMPORTANCE = "loan_period_importance";
  private static final String RESOURCE_IMPORTANCE = "resource_importance";
  private static final String TOTAL_YEARS_WORKED_IMPORTANCE = "total_years_worked_importance";
  private static final String WORKSPAN_IMPORTANCE = "workspan_importance";

  private static final String LOAN_PERIOD_SCORE = "loan_period_score";
  private static final String LOAN_HISTORY_SCORE = "loan_history_score";
  private static final String RESOURCE_SCORE = "resource_score";
  private static final String TOTAL_YEARS_WORKED_SCORE = "total_years_worked_score";
  private static final String WORKSPAN_SCORE = "workspan_score";

  private static final String MAX_SCORES = "max_scores";
  private static final String LOAN_PERIOD_MAX_SCORE = "loan_period_max_score";
  private static final String LOAN_HISTORY_MAX_SCORE = "loan_history_max_score";
  private static final String RESOURCE_MAX_SCORE = "resource_max_score";
  private static final String TOTAL_YEARS_WORKED_MAX_SCORE = "total_years_worked_max_score";
  private static final String WORKSPAN_MAX_SCORE = "workspan_max_score";

  private static final String INTEREST_RATE = "interest_rate";
  private static final String INTEREST_RATE_STRING = "interest_rate_string";

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String registrationNumber = (String) execution.getVariable("registerNumber");
    String requestId = (String )execution.getVariable(PROCESS_REQUEST_ID);
    String userId = authenticationService.getCurrentUserId();
    LOGGER.info("#########  Calculate Interest Rate Task.. Register Number: " + registrationNumber + ", Request ID: " + requestId + " , User ID: " + userId);

    boolean calculateInterestRate = (boolean) execution.getVariable("calculateInterestRate");
    if (!calculateInterestRate)
    {
      if (execution.getVariable(INTEREST_RATE) == null)
      {
        String errorCode = "BPMS075";
        throw new ProcessTaskException(errorCode, "Must Calculate Interest Rate!");
      }
      return;
    }

    Map<String, Object> variables = execution.getVariables();

    String keyWorker  = (String)variables.get(KEY_WORKER);

    Map<String, Object> organizationLevelScores = (Map<String, Object>) variables.get(ORGANIZATION_LEVEL_SCORES);
    Double interestRateHigh = (Double)organizationLevelScores.get(ORGANIZATION_LEVEL_SCORE_HIGH);
    Double interestRateLow = (Double) organizationLevelScores.get(ORGANIZATION_LEVEL_SCORE_LOW);
    Double interestRateRange = (Double)organizationLevelScores.get(ORGANIZATION_LEVEL_SCORE_RANGE);
    Double keyWorkerScoreInterestRate = (Double)organizationLevelScores.get(ORGANIZATION_KEY_WORKER_SCORE);

    Map<String, Object> importanceLevel = (Map<String, Object>) variables.get(IMPORTANCE_LEVEL);
    Double loanHistoryImportance = (Double)importanceLevel.get(LOAN_HISTORY_IMPORTANCE);
    Double loanPeriodImportance = (Double)importanceLevel.get(LOAN_PERIOD_IMPORTANCE);
    Double resourceImportance = (Double)importanceLevel.get(RESOURCE_IMPORTANCE);
    Double totalYearsWorkedImportance = (Double)importanceLevel.get(TOTAL_YEARS_WORKED_IMPORTANCE);
    Double workspanImportance = (Double)importanceLevel.get(WORKSPAN_IMPORTANCE);

    int loanPeriodScore = (int)variables.get(LOAN_PERIOD_SCORE);
    int loanHistoryScore = (int)variables.get(LOAN_HISTORY_SCORE);
    int resourceScore = (int)variables.get(RESOURCE_SCORE);
    int totalYearsWorkedScore = (int)variables.get(TOTAL_YEARS_WORKED_SCORE);
    int workspanScore = (int)variables.get(WORKSPAN_SCORE);

    Map<String, Object> maxScores = (Map<String, Object>) variables.get(MAX_SCORES);
    int loanPeriodMaxScore = (int)maxScores.get(LOAN_PERIOD_MAX_SCORE);
    int loanHistoryMaxScore = (int)maxScores.get(LOAN_HISTORY_MAX_SCORE);
    int resourceMaxScore = (int)maxScores.get(RESOURCE_MAX_SCORE);
    int totalYearsWorkedMaxScore = (int)maxScores.get(TOTAL_YEARS_WORKED_MAX_SCORE);
    int workspanMaxScore = (int)maxScores.get(WORKSPAN_MAX_SCORE);

    BigDecimal loanPeriodDiscountPercentageFactor = new BigDecimal(loanPeriodScore).divide(new BigDecimal(loanPeriodMaxScore)).multiply(new BigDecimal(loanPeriodImportance));
    BigDecimal loanHistoryDiscountPercentageFactor = new BigDecimal(loanHistoryScore).divide(new BigDecimal(loanHistoryMaxScore)).multiply(new BigDecimal(loanHistoryImportance));
    BigDecimal resourceDiscountPercentageFactor = new BigDecimal(resourceScore).divide(new BigDecimal(resourceMaxScore)).multiply(new BigDecimal(resourceImportance));
    BigDecimal totalYearsWorkedDiscountPercentageFactor = new BigDecimal(totalYearsWorkedScore).divide(new BigDecimal(totalYearsWorkedMaxScore)).multiply(new BigDecimal(totalYearsWorkedImportance));
    BigDecimal workspanDiscountPercentageFactor = new BigDecimal(workspanScore).divide(new BigDecimal(workspanMaxScore)).multiply(new BigDecimal(workspanImportance));
    BigDecimal discountPercentageFactor = loanPeriodDiscountPercentageFactor.add(loanHistoryDiscountPercentageFactor).add(resourceDiscountPercentageFactor).add(totalYearsWorkedDiscountPercentageFactor).add(workspanDiscountPercentageFactor);

    BigDecimal discountPercentage = discountPercentageFactor.multiply(new BigDecimal(interestRateRange));

    BigDecimal interestRateBigDecimal = new BigDecimal(interestRateHigh).subtract(discountPercentage).setScale(2, RoundingMode.HALF_UP);
    Double interestRate = interestRateBigDecimal.doubleValue();
    String interestRateString = interestRate.toString() + "%";
    String keyWorkerScoreInterestRateString = keyWorkerScoreInterestRate.toString() + "%";

    if (keyWorker.equals("Тийм"))
    {
      execution.removeVariable(INTEREST_RATE_STRING);
      execution.setVariable(INTEREST_RATE_STRING, keyWorkerScoreInterestRateString);
      execution.removeVariable(INTEREST_RATE);
      execution.setVariable(INTEREST_RATE, keyWorkerScoreInterestRate);
    }
    else
    {
      execution.removeVariable(INTEREST_RATE_STRING);
      execution.setVariable(INTEREST_RATE_STRING, interestRateString);
      execution.removeVariable(INTEREST_RATE);
      execution.setVariable(INTEREST_RATE, interestRate);

    }

    LOGGER.info("*********** Successfully Calculated Interest Rate...");
  }
}
