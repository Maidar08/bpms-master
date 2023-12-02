package mn.erin.bpms.loan.consumption.service_task.bpms.co_borrower;

import java.util.Arrays;
import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_DOWNLOAD_CO_BORROWER_MONGOL_BANK;
import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_DOWNLOAD_CO_BORROWER_XYP;
import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_MICRO_DOWNLOAD_CO_BORROWER_MONGOL_BANK;
import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_MICRO_DOWNLOAD_FROM_KHUR_CO_BORROWER;
import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_MORTGAGE_DOWNLOAD_CO_BORROWER_MONGOL_BANK;
import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_MORTGAGE_DOWNLOAD_CO_BORROWER_XYP;
import static mn.erin.bpms.loan.consumption.constant.CamundaTaskDefinitionKeyConstants.TASK_DEF_KEY_CONSUMPTION_CO_BORROWER_MONGOL_BANK;
import static mn.erin.bpms.loan.consumption.constant.CamundaTaskDefinitionKeyConstants.TASK_DEF_KEY_CO_BORROWER_XYP_ENQUIRE;
import static mn.erin.bpms.loan.consumption.constant.CamundaTaskDefinitionKeyConstants.TASK_DEF_KEY_MICRO_CO_BORROWER_KHUR_ENQUIRE;
import static mn.erin.bpms.loan.consumption.constant.CamundaTaskDefinitionKeyConstants.TASK_DEF_KEY_MICRO_CO_BORROWER_MONGOL_BANK;
import static mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils.suspendActiveProcessInstancesByDefKey;
import static mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils.terminateActiveExecutions;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;

public class TerminateCoBoActiveEnquireTask implements JavaDelegate {
    private static final Logger LOG = LoggerFactory.getLogger(TerminateCoBoActiveEnquireTask.class);

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String caseInstanceId = (String) execution.getVariable(CASE_INSTANCE_ID);
        String requestId = (String) execution.getVariable(PROCESS_REQUEST_ID);

        List<String> activityIds = Arrays.asList(
                ACTIVITY_ID_DOWNLOAD_CO_BORROWER_XYP,
                ACTIVITY_ID_DOWNLOAD_CO_BORROWER_MONGOL_BANK,
                ACTIVITY_ID_MICRO_DOWNLOAD_CO_BORROWER_MONGOL_BANK,
                ACTIVITY_ID_MICRO_DOWNLOAD_FROM_KHUR_CO_BORROWER,
                ACTIVITY_ID_MORTGAGE_DOWNLOAD_CO_BORROWER_XYP,
                ACTIVITY_ID_MORTGAGE_DOWNLOAD_CO_BORROWER_MONGOL_BANK);
        List<String> suspendFormKeys = Arrays.asList(TASK_DEF_KEY_CO_BORROWER_XYP_ENQUIRE,
                TASK_DEF_KEY_CONSUMPTION_CO_BORROWER_MONGOL_BANK,
                TASK_DEF_KEY_MICRO_CO_BORROWER_MONGOL_BANK,
                TASK_DEF_KEY_MICRO_CO_BORROWER_KHUR_ENQUIRE);

        try {
            terminateActiveExecutions(caseInstanceId, execution, activityIds);

            suspendActiveProcessInstancesByDefKey(caseInstanceId, execution, suspendFormKeys, true);
        } catch (Exception e) {
            LOG.error("COULD NOT TERMINATE CO-BORROWER ENQUIRE TASKS WHEN"
                    + "ALL CO-BORROWER REMOVED WITH REQUEST ID = [{}], REASON = [{}]", requestId, e.getMessage());
        }
    }
}
