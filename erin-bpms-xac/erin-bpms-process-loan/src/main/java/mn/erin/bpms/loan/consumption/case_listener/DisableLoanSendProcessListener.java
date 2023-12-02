package mn.erin.bpms.loan.consumption.case_listener;

import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.delegate.CaseExecutionListener;
import org.camunda.bpm.engine.delegate.DelegateCaseExecution;
import org.camunda.bpm.engine.runtime.CaseExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils;

import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_SEND_LOAN_REQUEST_DECISION;
import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_SEND_LOAN_REQUEST_DECISION_AFTER_RETURN;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;

public class DisableLoanSendProcessListener implements CaseExecutionListener
{

    private static final Logger LOG = LoggerFactory.getLogger(DisableLoanSendProcessListener.class);
    @Override
    public void notify(DelegateCaseExecution caseExecution) throws Exception {
        String requestId = (String) caseExecution.getVariable(PROCESS_REQUEST_ID);
        String caseInstanceId = (String) caseExecution.getVariable(CASE_INSTANCE_ID);

        ProcessEngine processEngine = caseExecution.getProcessEngine();
        CaseService caseService = processEngine.getCaseService();

        try
        {
            List<CaseExecution> enabledExecutions = CaseExecutionUtils.getEnabledExecutions(caseInstanceId, processEngine);
            Map<String, Object> caseVariables = caseService.getVariables(caseInstanceId);
            if (!enabledExecutions.isEmpty())
            {
                for (CaseExecution enabledExecution : enabledExecutions)
                {
                    if (ACTIVITY_ID_SEND_LOAN_REQUEST_DECISION.equals(enabledExecution.getActivityId())
                            || ACTIVITY_ID_SEND_LOAN_REQUEST_DECISION_AFTER_RETURN.equals(enabledExecution.getActivityId()))
                    {
                        LOG.info("############## DISABLES LOAN REQUEST DECISION AFTER RETURN WITH REQUEST ID = [{}] ##########", requestId);
                        caseService.disableCaseExecution(enabledExecution.getId(), caseVariables);
                    }
                }
            }
        }
        catch (Exception e)
        {
            LOG.error("########## COULD NOT DISABLE AND LOAN REQUEST DECISION AFTER RETURN WITH REQUEST ID = [{}] ##########", requestId);
            LOG.error(e.getMessage(), e);
        }
    }
}
