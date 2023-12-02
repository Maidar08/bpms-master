package mn.erin.bpms.loan.consumption.service_task.sequence;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static mn.erin.bpms.loan.consumption.constant.CamundaVariableConstants.CALCULATION_STAGE_REPEATABLE;
import static mn.erin.bpms.loan.consumption.constant.CamundaVariableConstants.ENABLE_LOAN_AMOUNT;
import static mn.erin.bpms.loan.consumption.constant.CamundaVariableConstants.ENABLE_SALARY_CALCULATION;
import static mn.erin.bpms.loan.consumption.constant.CamundaVariableConstants.IS_CALCULATE_INTEREST_RATE;
import static mn.erin.bpms.loan.consumption.constant.CamundaVariableConstants.IS_CALCULATE_LOAN_AMOUNT;
import static mn.erin.bpms.loan.consumption.constant.CamundaVariableConstants.IS_COMPLETED_ELEMENTARY_CRITERIA;
import static mn.erin.bpms.loan.consumption.constant.CamundaVariableConstants.IS_COMPLETED_SCORING;
import static mn.erin.bpms.loan.consumption.constant.CamundaVariableConstants.IS_LOAN_ACCOUNT_CREATE;
import static mn.erin.bpms.loan.consumption.constant.CamundaVariableConstants.IS_STARTED_COLLATERAL_LIST_EXECUTION;
import static mn.erin.bpms.loan.consumption.constant.CamundaVariableConstants.IS_STARTED_COLL_ACCOUNT_STAGE;
import static mn.erin.bpms.loan.consumption.constant.CamundaVariableConstants.LOAN_DECISION;
import static mn.erin.bpms.loan.consumption.constant.CamundaVariableConstants.SALARY_CALCULATION;
import static mn.erin.bpms.loan.consumption.constant.CamundaVariableConstants.SCORING_CALCULATION;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_SALARY_PROCESS_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;

/**
 * @author Tamir
 */
public class SetInitialVariablesTask implements JavaDelegate
{
  private static final Logger LOG = LoggerFactory.getLogger(SetInitialVariablesTask.class);

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    LOG.info("####### Sets CONSUMPTION_LOAN case INITIAL IMPORTANT variables... ");

    LOG.info("###### 1.Sets IS_COMPLETED_ELEMENTARY_CRITERIA = [{}]", false);
    execution.setVariable(IS_COMPLETED_ELEMENTARY_CRITERIA, false);

    LOG.info("###### 2. Sets ENABLE_SALARY_CALCULATION = [{}]", true);
    execution.setVariable(ENABLE_SALARY_CALCULATION, true);

    LOG.info("###### 3. Sets IS_COMPLETED_SCORING = [{}]", true);
    execution.setVariable(IS_COMPLETED_SCORING, false);

    LOG.info("###### 4. Sets IS_CALCULATE_INTEREST_RATE = [{}]", true);
    execution.setVariable(IS_CALCULATE_INTEREST_RATE, true);

    LOG.info("###### 5. Sets IS_CALCULATE_LOAN_AMOUNT = [{}]", true);
    execution.setVariable(IS_CALCULATE_LOAN_AMOUNT, true);

    LOG.info("###### 6. Sets LOAN_DECISION VARIABLE EMPTY STRING");
    execution.setVariable(LOAN_DECISION, "");

    if (!String.valueOf(execution.getVariable(PROCESS_TYPE_ID)).equals(ONLINE_SALARY_PROCESS_TYPE))
    {
      LOG.info("###### 7. Sets IS_LOAN_ACCOUNT_CREATED = [{}]", false);
      execution.setVariable(IS_LOAN_ACCOUNT_CREATE, false);
    }

    LOG.info("###### 8. Sets STAGE_CALCULATION = [{}]", true);
    execution.setVariable(CALCULATION_STAGE_REPEATABLE, true);

    LOG.info("###### 9. Sets IS_STARTED_COLL_ACCOUNT_STAGE = [{}]", false);
    execution.setVariable(IS_STARTED_COLL_ACCOUNT_STAGE, false);

    LOG.info("###### 10. Sets ENABLE_LOAN_AMOUNT = [{}]", false);
    execution.setVariable(ENABLE_LOAN_AMOUNT, false);

    LOG.info("###### 11. Sets IS_STARTED_COLLATERAL_LIST_EXECUTION = [{}]", false);
    execution.setVariable(IS_STARTED_COLLATERAL_LIST_EXECUTION, false);

    LOG.info("###### 12. Sets SALARY_CALCULATION = [{}]", true);
    execution.setVariable(SALARY_CALCULATION, true);

    LOG.info("###### 13. Sets SCORING_CALCULATION = [{}]", false);
    execution.setVariable(SCORING_CALCULATION, false);
  }
}
