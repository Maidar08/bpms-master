package mn.erin.bpms.loan.consumption.service_task.sequence;

import java.util.Arrays;
import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_DOWNLOAD_CO_BORROWER_MONGOL_BANK;
import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_DOWNLOAD_CO_BORROWER_XYP;
import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_DOWNLOAD_MICRO_LOAN_DOWNLOAD_ХУР;
import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_DOWNLOAD_MONGOL_BANK;
import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_DOWNLOAD_XYP;
import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_MICRO_DOWNLOAD_CO_BORROWER_MONGOL_BANK;
import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_MICRO_DOWNLOAD_FROM_KHUR_CO_BORROWER;
import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_MICRO_MONGOL_BANK_ENQUIRE;
import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_MICRO_MONGOL_BANK_ENQUIRE_EXTENDED;
import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_MICRO_MONGOL_BANK_NEW_CORE;
import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_MORTGAGE_DOWNLOAD_CO_BORROWER_MONGOL_BANK;
import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_MORTGAGE_DOWNLOAD_CO_BORROWER_XYP;
import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_MORTGAGE_LOAN_DOWNLOAD_FROM_MONGOLBANK;
import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_MORTGAGE_LOAN_DOWNLOAD_KHUR;
import static mn.erin.bpms.loan.consumption.constant.CamundaTaskDefinitionKeyConstants.TASK_DEF_KEY_CONSUMPTION_CO_BORROWER_MONGOL_BANK;
import static mn.erin.bpms.loan.consumption.constant.CamundaTaskDefinitionKeyConstants.TASK_DEF_KEY_CONSUMPTION_MONGOL_BANK;
import static mn.erin.bpms.loan.consumption.constant.CamundaTaskDefinitionKeyConstants.TASK_DEF_KEY_CO_BORROWER_XYP_ENQUIRE;
import static mn.erin.bpms.loan.consumption.constant.CamundaTaskDefinitionKeyConstants.TASK_DEF_KEY_MICRO_CO_BORROWER_KHUR_ENQUIRE;
import static mn.erin.bpms.loan.consumption.constant.CamundaTaskDefinitionKeyConstants.TASK_DEF_KEY_MICRO_CO_BORROWER_MONGOL_BANK;
import static mn.erin.bpms.loan.consumption.constant.CamundaTaskDefinitionKeyConstants.TASK_DEF_KEY_MICRO_MONGOL_BANK;
import static mn.erin.bpms.loan.consumption.constant.CamundaTaskDefinitionKeyConstants.TASK_DEF_KEY_MICRO_MONGOL_BANK_EXTENDED;
import static mn.erin.bpms.loan.consumption.constant.CamundaTaskDefinitionKeyConstants.TASK_DEF_KEY_MICRO_MONGOL_NEW_CORE;
import static mn.erin.bpms.loan.consumption.constant.CamundaTaskDefinitionKeyConstants.TASK_DEF_KEY_XYP_ENQUIRE;
import static mn.erin.bpms.loan.consumption.constant.CamundaTaskDefinitionKeyConstants.TASK_DEF_MICRO_KHUR_ENQUIRE;
import static mn.erin.bpms.loan.consumption.constant.CamundaVariableConstants.IS_COMPLETED_ELEMENTARY_CRITERIA;
import static mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils.disableExecutionsByActivityId;
import static mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils.suspendActiveProcessInstancesByDefKey;
import static mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils.terminateActiveExecutions;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;

/**
 * @author Tamir
 */
public class DisableXypMongolBankTaskListener implements TaskListener
{
  private static final Logger LOG = LoggerFactory.getLogger(DisableXypMongolBankTaskListener.class);

  @Override
  public void notify(DelegateTask delegateTask)
  {
    LOG.info("###### Disables Xyp, Mongol tasks");

    String caseInstanceId = delegateTask.getCaseInstanceId();
    DelegateExecution execution = delegateTask.getExecution();

    execution.setVariable(IS_COMPLETED_ELEMENTARY_CRITERIA, true);

    if (null == caseInstanceId)
    {
      String requestId = (String) execution.getVariable(PROCESS_REQUEST_ID);
      LOG.warn("####### REJECTED disable Xyp, MONGOL bank execution : CASE INSTANCE ID is null with REQUEST_ID ={}", requestId);
      return;
    }

    List<String> activityIds = Arrays.asList(
        ACTIVITY_ID_DOWNLOAD_XYP,
        ACTIVITY_ID_DOWNLOAD_MICRO_LOAN_DOWNLOAD_ХУР,
        ACTIVITY_ID_DOWNLOAD_MONGOL_BANK,
        ACTIVITY_ID_DOWNLOAD_CO_BORROWER_XYP,
        ACTIVITY_ID_DOWNLOAD_CO_BORROWER_MONGOL_BANK,
        ACTIVITY_ID_MICRO_DOWNLOAD_CO_BORROWER_MONGOL_BANK,
        ACTIVITY_ID_MICRO_DOWNLOAD_FROM_KHUR_CO_BORROWER,
        ACTIVITY_ID_MORTGAGE_LOAN_DOWNLOAD_FROM_MONGOLBANK,
        ACTIVITY_ID_MORTGAGE_LOAN_DOWNLOAD_KHUR,
        ACTIVITY_ID_MORTGAGE_DOWNLOAD_CO_BORROWER_XYP,
        ACTIVITY_ID_MORTGAGE_DOWNLOAD_CO_BORROWER_MONGOL_BANK,
        ACTIVITY_ID_MICRO_MONGOL_BANK_ENQUIRE_EXTENDED,
        ACTIVITY_ID_MICRO_MONGOL_BANK_ENQUIRE,
        ACTIVITY_ID_MICRO_MONGOL_BANK_NEW_CORE
    );

    disableExecutionsByActivityId(caseInstanceId, execution, activityIds);
    terminateActiveExecutions(caseInstanceId, execution, activityIds);

    List<String> suspendFormKeys = Arrays.asList(
        TASK_DEF_KEY_XYP_ENQUIRE,
        TASK_DEF_KEY_CO_BORROWER_XYP_ENQUIRE,
        TASK_DEF_MICRO_KHUR_ENQUIRE,
        TASK_DEF_KEY_MICRO_CO_BORROWER_KHUR_ENQUIRE,
        TASK_DEF_KEY_CONSUMPTION_MONGOL_BANK,
        TASK_DEF_KEY_CONSUMPTION_CO_BORROWER_MONGOL_BANK,
        TASK_DEF_KEY_MICRO_CO_BORROWER_MONGOL_BANK,
        TASK_DEF_KEY_MICRO_MONGOL_BANK_EXTENDED,
        TASK_DEF_KEY_MICRO_MONGOL_BANK,
        TASK_DEF_KEY_MICRO_MONGOL_NEW_CORE
    );

    suspendActiveProcessInstancesByDefKey(caseInstanceId, execution, suspendFormKeys, true);
    LOG.info("################  Successful disabled Xyp, Mongol tasks");
  }
}
