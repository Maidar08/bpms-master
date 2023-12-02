package mn.erin.bpms.loan.consumption.service_task.bpms.co_borrower;

import java.util.Arrays;
import java.util.List;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.runtime.CaseExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils;

import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_DOWNLOAD_CO_BORROWER_MONGOL_BANK;
import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_DOWNLOAD_CO_BORROWER_XYP;
import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_MICRO_DOWNLOAD_CO_BORROWER_MONGOL_BANK;
import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_MICRO_DOWNLOAD_FROM_KHUR_CO_BORROWER;
import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_MORTGAGE_DOWNLOAD_CO_BORROWER_MONGOL_BANK;
import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_MORTGAGE_DOWNLOAD_CO_BORROWER_XYP;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.INDEX_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;

public class VerifyCoBoRemovedServiceTask implements JavaDelegate
{
  private static final Logger LOG = LoggerFactory.getLogger(VerifyCoBoRemovedServiceTask.class);

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    ProcessEngine processEngine = execution.getProcessEngine();
    CaseService caseService = processEngine.getCaseService();

    String caseInstanceId = (String) execution.getVariable(CASE_INSTANCE_ID);

    String requestId = (String) execution.getVariable(PROCESS_REQUEST_ID);
    Object indexCoBorrower = execution.getVariable(INDEX_CO_BORROWER);

    if (null == indexCoBorrower)
    {
      return;
    }

    Integer index = (Integer) indexCoBorrower;

    if (index == 0)
    {
      LOG.info("############ DISABLED CO-BORROWER XYP, MONGOL BANK PROCESSES "
          + "CAUSE ALL CO-BORROWER REMOVED WITH REQUEST ID = [{}]", requestId);

      List<CaseExecution> enabledExecutions = CaseExecutionUtils.getEnabledExecutions(caseInstanceId, processEngine);
      List<String> activityIds = Arrays.asList(ACTIVITY_ID_DOWNLOAD_CO_BORROWER_XYP, ACTIVITY_ID_DOWNLOAD_CO_BORROWER_MONGOL_BANK, ACTIVITY_ID_MICRO_DOWNLOAD_CO_BORROWER_MONGOL_BANK, ACTIVITY_ID_MICRO_DOWNLOAD_FROM_KHUR_CO_BORROWER, ACTIVITY_ID_MORTGAGE_DOWNLOAD_CO_BORROWER_XYP, ACTIVITY_ID_MORTGAGE_DOWNLOAD_CO_BORROWER_MONGOL_BANK);

      CoBorrowerUtils.disableCoBorrowerProcessByActivityIds(enabledExecutions, caseService, requestId, activityIds);
    }
  }
}
