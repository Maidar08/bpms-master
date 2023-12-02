package mn.erin.bpms.loan.consumption.case_listener;

import java.util.Arrays;
import java.util.List;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.delegate.CaseExecutionListener;
import org.camunda.bpm.engine.delegate.DelegateCaseExecution;
import org.camunda.bpm.engine.runtime.CaseExecution;

import mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils;

import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_DOWNLOAD_CO_BORROWER_MONGOL_BANK;
import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_DOWNLOAD_CO_BORROWER_XYP;
import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_MICRO_DOWNLOAD_CO_BORROWER_MONGOL_BANK;
import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_MICRO_DOWNLOAD_FROM_KHUR_CO_BORROWER;
import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_MICRO_REMOVE_CO_BORROWER;
import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_REMOVE_CO_BORROWER;
import static mn.erin.bpms.loan.consumption.service_task.bpms.co_borrower.CoBorrowerUtils.disableCoBorrowerProcessByActivityIds;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;

public class DisableCoBorrowerProcessesListener implements CaseExecutionListener
{
  @Override
  public void notify(DelegateCaseExecution caseExecution) throws Exception
  {

    String caseInstanceId = caseExecution.getCaseInstanceId();
    ProcessEngine processEngine = caseExecution.getProcessEngine();

    CaseService caseService = processEngine.getCaseService();

    String requestId = (String) caseExecution.getVariable(PROCESS_REQUEST_ID);
    List<CaseExecution> enabledExecutions = CaseExecutionUtils.getEnabledExecutions(caseInstanceId, processEngine);

    List<String> activityIds = Arrays
        .asList(
            ACTIVITY_ID_REMOVE_CO_BORROWER,
            ACTIVITY_ID_DOWNLOAD_CO_BORROWER_XYP,
            ACTIVITY_ID_DOWNLOAD_CO_BORROWER_MONGOL_BANK,
            ACTIVITY_ID_MICRO_REMOVE_CO_BORROWER,
            ACTIVITY_ID_MICRO_DOWNLOAD_CO_BORROWER_MONGOL_BANK,
            ACTIVITY_ID_MICRO_DOWNLOAD_FROM_KHUR_CO_BORROWER
        );

    disableCoBorrowerProcessByActivityIds(enabledExecutions, caseService, requestId, activityIds);
  }
}
