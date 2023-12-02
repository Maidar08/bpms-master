package mn.erin.bpms.loan.consumption.listener;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;

import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;

public class StartContractProcessTask implements JavaDelegate
{
  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String caseInstanceId = ((ExecutionEntity) execution).getCaseInstanceId();
    execution.getProcessEngine().getCaseService().setVariable(caseInstanceId, CASE_INSTANCE_ID, 1080000041);
    execution.setVariable(CASE_INSTANCE_ID, caseInstanceId);
  }
}
