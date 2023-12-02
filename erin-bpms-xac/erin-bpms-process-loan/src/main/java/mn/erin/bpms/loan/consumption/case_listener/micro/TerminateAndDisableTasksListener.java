package mn.erin.bpms.loan.consumption.case_listener.micro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.camunda.bpm.engine.delegate.CaseExecutionListener;
import org.camunda.bpm.engine.delegate.DelegateCaseExecution;

import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_ELEMENTARY_CRITERIA;
import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_MICRO_MONGOL_BANK_ENQUIRE;
import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_MICRO_MONGOL_BANK_NEW_CORE;
import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_MORTGAGE_LOAN_DOWNLOAD_FROM_MONGOLBANK;
import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_MORTGAGE_LOAN_DOWNLOAD_KHUR;
import static mn.erin.bpms.loan.consumption.constant.CamundaTaskDefinitionKeyConstants.TASK_DEF_KEY_CONSUMPTION_MONGOL_BANK;
import static mn.erin.bpms.loan.consumption.constant.CamundaTaskDefinitionKeyConstants.TASK_DEF_KEY_ELEMENTARY_CRITERIA;
import static mn.erin.bpms.loan.consumption.constant.CamundaTaskDefinitionKeyConstants.TASK_DEF_KEY_MICRO_ELEMENTARY_CRITERIA;
import static mn.erin.bpms.loan.consumption.constant.CamundaTaskDefinitionKeyConstants.TASK_DEF_KEY_MICRO_MONGOL_BANK;
import static mn.erin.bpms.loan.consumption.constant.CamundaTaskDefinitionKeyConstants.TASK_DEF_KEY_MICRO_MONGOL_BANK_EXTENDED;
import static mn.erin.bpms.loan.consumption.constant.CamundaTaskDefinitionKeyConstants.TASK_DEF_KEY_MICRO_MONGOL_NEW_CORE;
import static mn.erin.bpms.loan.consumption.constant.CamundaTaskDefinitionKeyConstants.TASK_DEF_KEY_MICRO_XYP;
import static mn.erin.bpms.loan.consumption.constant.CamundaVariableConstants.MICRO_LOAN_CASE_ID;
import static mn.erin.bpms.loan.consumption.constant.CamundaVariableConstants.MORTGAGE_LOAN_CASE_ID;
import static mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils.disableExecutionsByActivityId;
import static mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils.suspendActiveProcessInstancesByDefKey;
import static mn.erin.domain.bpm.BpmActivityIdConstants.ACTIVITY_ID_MICRO_ELEMENTARY_CRITERIA;
import static mn.erin.domain.bpm.BpmActivityIdConstants.ACTIVITY_ID_MICRO_MONGOL_BANK;
import static mn.erin.domain.bpm.BpmActivityIdConstants.ACTIVITY_ID_MICRO_XYP;

/**
 * @author Lkhagvadorj.A
 **/

public class TerminateAndDisableTasksListener implements CaseExecutionListener {
    @Override
    public void notify(DelegateCaseExecution delegateCaseExecution) throws Exception {
        String caseInstanceId = delegateCaseExecution.getCaseInstanceId();
        String caseDefinitionId = delegateCaseExecution.getCaseDefinitionId();

        List<String> taskDefKeys = new ArrayList<>();
        List<String> processTaskDefKeys = new ArrayList<>();

        if (caseDefinitionId != null) {
            if (caseDefinitionId.contains(MICRO_LOAN_CASE_ID)) {
                taskDefKeys.addAll(Arrays.asList(TASK_DEF_KEY_MICRO_MONGOL_BANK, TASK_DEF_KEY_MICRO_MONGOL_BANK_EXTENDED, TASK_DEF_KEY_MICRO_XYP, TASK_DEF_KEY_MICRO_ELEMENTARY_CRITERIA, TASK_DEF_KEY_MICRO_MONGOL_NEW_CORE));
                processTaskDefKeys.addAll(Arrays.asList(ACTIVITY_ID_MICRO_MONGOL_BANK, ACTIVITY_ID_MICRO_XYP, ACTIVITY_ID_MICRO_ELEMENTARY_CRITERIA, ACTIVITY_ID_MICRO_MONGOL_BANK_NEW_CORE, ACTIVITY_ID_MICRO_MONGOL_BANK_ENQUIRE));
            } else if (caseDefinitionId.contains(MORTGAGE_LOAN_CASE_ID)) {
                taskDefKeys.addAll(Arrays.asList(TASK_DEF_KEY_CONSUMPTION_MONGOL_BANK, TASK_DEF_KEY_MICRO_XYP,  TASK_DEF_KEY_ELEMENTARY_CRITERIA));
                processTaskDefKeys.addAll(Arrays.asList(ACTIVITY_ID_MORTGAGE_LOAN_DOWNLOAD_FROM_MONGOLBANK, ACTIVITY_ID_MORTGAGE_LOAN_DOWNLOAD_KHUR, ACTIVITY_ID_ELEMENTARY_CRITERIA));

            }
        }

        // suspend
        suspendActiveProcessInstancesByDefKey(caseInstanceId, delegateCaseExecution, taskDefKeys, true);

        // disable
        disableExecutionsByActivityId(caseInstanceId, delegateCaseExecution, processTaskDefKeys);
    }
}
