package mn.erin.bpms.loan.consumption.service_task.calculation;

import org.camunda.bpm.engine.delegate.CaseExecutionListener;
import org.camunda.bpm.engine.delegate.DelegateCaseExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static mn.erin.bpms.loan.consumption.constant.CamundaVariableConstants.ENABLE_SALARY_CALCULATION;

/**
 * @author Tamir
 */
public class DeactivateSalaryCalculation implements CaseExecutionListener
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DeactivateSalaryCalculation.class);

  @Override
  public void notify(DelegateCaseExecution caseExecution) throws Exception
  {
    LOGGER.info("########## Deactivates salary calculation process task.");

    caseExecution.setVariable(ENABLE_SALARY_CALCULATION, false);

    LOGGER.info("########## Successful deactivated salary calculation process task.");
  }
}
