package mn.erin.bpms.loan.consumption.util;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;

import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;

/**
 * @author Lkhagvadorj.A
 **/

public final class CamundaContractUtils
{
  private CamundaContractUtils()
  {

  }

  public static String getInstanceId(DelegateExecution execution)
  {
    if (execution.hasVariable(CASE_INSTANCE_ID))
    {
      return String.valueOf(execution.getVariable(CASE_INSTANCE_ID));
    }
    return ((ExecutionEntity) execution).getCaseInstanceId();
  }
}
