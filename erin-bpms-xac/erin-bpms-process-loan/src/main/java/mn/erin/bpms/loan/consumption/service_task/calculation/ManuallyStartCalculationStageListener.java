package mn.erin.bpms.loan.consumption.service_task.calculation;

import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.runtime.CaseExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_STAGE_CALCULATION;
import static mn.erin.bpms.loan.consumption.constant.CamundaVariableConstants.ENABLE_SALARY_CALCULATION;
import static mn.erin.bpms.loan.consumption.constant.CamundaVariableConstants.GRANT_LOAN_AMOUNT;
import static mn.erin.bpms.loan.consumption.constant.CamundaVariableConstants.GRANT_LOAN_AMOUNT_STRING;
import static mn.erin.bpms.loan.consumption.constant.CamundaVariableConstants.HAS_MORTGAGE;
import static mn.erin.bpms.loan.consumption.constant.CamundaVariableConstants.IS_CALCULATE_LOAN_AMOUNT;
import static mn.erin.bpms.loan.consumption.constant.CamundaVariableConstants.IS_CALCULATE_SCORING;
import static mn.erin.bpms.loan.consumption.constant.CamundaVariableConstants.TOTAL_INCOME_AMOUNT;
import static mn.erin.bpms.loan.consumption.constant.CamundaVariableConstants.TOTAL_INCOME_AMOUNT_STRING;
import static mn.erin.bpms.loan.consumption.utils.CalculationUtils.setVariablesHasMortgage;
import static mn.erin.bpms.loan.consumption.utils.CalculationUtils.setVariablesWhenNoMortgage;
import static mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils.getCaseVariables;
import static mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils.getEnabledExecutions;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;

/**
 * @author Tamir
 */
public class ManuallyStartCalculationStageListener implements TaskListener
{
  private static final Logger LOGGER = LoggerFactory.getLogger(ManuallyStartCalculationStageListener.class);

  private static final String ZERO_STRING = "0";
  private static final String TRUE_STRING = "true";

  @Override
  public void notify(DelegateTask delegateTask)
  {
    DelegateExecution execution = delegateTask.getExecution();
    String requestId = (String) execution.getVariable(PROCESS_REQUEST_ID);

    LOGGER.info("######################## SETS SALARY CALCULATION REPETITION RULE, ENABLE_SALARY_CALCULATION = [{}].", true);
    execution.setVariable(ENABLE_SALARY_CALCULATION, true);

    if (execution.getVariable(HAS_MORTGAGE) instanceof Boolean)
    {
      boolean hasMortgage = (boolean) execution.getVariable(HAS_MORTGAGE);

      if (hasMortgage)
      {
        setVariablesHasMortgage(execution);
      }
      else
      {
        setVariablesWhenNoMortgage(execution);
      }
    }

    else if (execution.getVariable(HAS_MORTGAGE) instanceof String)
    {
      String hasMortgageString = (String) execution.getVariable(HAS_MORTGAGE);

      if (hasMortgageString.equals(TRUE_STRING))
      {
        setVariablesHasMortgage(execution);
      }
      else
      {
        setVariablesWhenNoMortgage(execution);
      }
    }

    if (execution.getVariable(GRANT_LOAN_AMOUNT) == null)
    {
      execution.setVariable(GRANT_LOAN_AMOUNT, 0);
      execution.setVariable(GRANT_LOAN_AMOUNT_STRING, ZERO_STRING);
    }

    if (execution.getVariable(TOTAL_INCOME_AMOUNT) == null)
    {
      execution.setVariable(TOTAL_INCOME_AMOUNT, 0);
      execution.setVariable(TOTAL_INCOME_AMOUNT_STRING, ZERO_STRING);
    }

    execution.setVariable(IS_CALCULATE_LOAN_AMOUNT, true);
    execution.setVariable(IS_CALCULATE_SCORING, true);

    String caseInstanceId = (String) execution.getVariable(CASE_INSTANCE_ID);
    ProcessEngine processEngine = execution.getProcessEngine();

    List<CaseExecution> enabledExecutions = getEnabledExecutions(caseInstanceId, processEngine);

    CaseService caseService = execution.getProcessEngine().getCaseService();
    Map<String, Object> executionVariables = getCaseVariables(caseInstanceId, processEngine);

    for (CaseExecution enabledExecution : enabledExecutions)
    {
      String activityId = enabledExecution.getActivityId();
      String executionId = enabledExecution.getId();

      if (activityId.equalsIgnoreCase(ACTIVITY_ID_STAGE_CALCULATION))
      {
        LOGGER.info("#################### MANUALLY STARTS CALCULATION STAGE WITH REQUEST ID = [{}] ################", requestId);
        caseService.manuallyStartCaseExecution(executionId, executionVariables);
      }
    }

    LOGGER.info("####################### SUCCESSFUL SET SALARY CALCULATION VARIABLES TO CASE VARIABLE. #############");
  }
}
