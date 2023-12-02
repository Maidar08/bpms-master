package mn.erin.bpms.loan.consumption.service_task.bpms.elementary_criteria;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;

/**
 * @author Tamir
 */
public class CleanCriteriaPassedFieldListener implements TaskListener
{
  public static final String ELEMENTARY_CRITERIAN_PASSED = "elementaryCriterianPassed";

  @Override
  public void notify(DelegateTask delegateTask)
  {
    DelegateExecution execution = delegateTask.getExecution();

    if (execution.hasVariable(ELEMENTARY_CRITERIAN_PASSED))
    {
      String value = (String) execution.getVariable(ELEMENTARY_CRITERIAN_PASSED);

      if (!StringUtils.isBlank(value))
      {
        execution.setVariable(ELEMENTARY_CRITERIAN_PASSED, "");
      }
    }
  }
}
