package mn.erin.bpms.loan.consumption.service_task;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.RELATED_USER_TASK_ID;

/**
 * @author Lkhagvadorj.A
 **/

public class SetCreateCollateralRelatedUserTaskIdTask implements JavaDelegate
{
    @Override
    public void execute(DelegateExecution execution) throws Exception
    {
        CaseService caseService = execution.getProcessEngine().getCaseService();
        String instanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));
        String collateralType  = String.valueOf(execution.getVariable("collateralType"));
        switch (collateralType)
        {
        case "immovableCollateral":
            execution.setVariable(RELATED_USER_TASK_ID, "user_task_new_core_create_immovable_collateral");
            caseService.setVariable(instanceId, RELATED_USER_TASK_ID, "user_task_new_core_create_immovable_collateral");
            break;
        case "vehicleCollateral":
            execution.setVariable(RELATED_USER_TASK_ID, "user_task_new_core_create_vehicle_collateral");
            caseService.setVariable(instanceId, RELATED_USER_TASK_ID, "user_task_new_core_create_vehicle_collateral");
            break;
        case "machineryCollateral":
            execution.setVariable(RELATED_USER_TASK_ID, "user_task_new_core_create_machinery_collateral");
            caseService.setVariable(instanceId, RELATED_USER_TASK_ID, "user_task_new_core_create_machinery_collateral");
            break;
        case "otherCollateral":
            execution.setVariable(RELATED_USER_TASK_ID, "user_task_new_core_create_other_collateral");
            caseService.setVariable(instanceId, RELATED_USER_TASK_ID, "user_task_new_core_create_other_collateral");
            break;
        default:
            break;
        }
    }
}
