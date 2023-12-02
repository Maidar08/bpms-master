package mn.erin.bpms.loan.consumption.service_task.bpms.co_borrower;

import java.util.List;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.runtime.CaseExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tamir
 */
public final class CoBorrowerUtils
{
  private static final Logger LOG = LoggerFactory.getLogger(CoBorrowerUtils.class);

  private CoBorrowerUtils()
  {

  }

  public static final String IS_LOAN_ACCOUNT_CREATE = "isLoanAccountCreate";
  public static final String INDEX_CO_BORROWER = "indexCoBorrower";

  public static void setIndexedVariable(DelegateExecution execution, String key, Object value)
  {
    Integer index = (Integer) execution.getVariable(INDEX_CO_BORROWER);
    String indexedKey = key + "-" + index;
    execution.setVariable(indexedKey, value);
  }

  public static void setIndexedCoBorrowerVariable(DelegateExecution execution, String key, String caseInstanceId)
  {
    Object variableValue = execution.getVariable(key);

    execution.setVariable(key, null);

    CaseService caseService = execution.getProcessEngine().getCaseService();

    Integer index = (Integer) execution.getVariable(INDEX_CO_BORROWER);

    caseService.setVariable(caseInstanceId, key, null);

    execution.setVariable(key + "-" + index, variableValue);
  }

  public static void disableCoBorrowerProcessByActivityIds(List<CaseExecution> enabledExecutions, CaseService caseService, String requestId,
      List<String> activityIds)
  {
    if (null == enabledExecutions)
    {
      return;
    }

    for (CaseExecution enabledExecution : enabledExecutions)
    {
      String activityId = enabledExecution.getActivityId();

      for (String givenActivityId : activityIds)
      {
        if (activityId.equalsIgnoreCase(givenActivityId))
        {
          LOG.info("########## DISABLES ENABLED CO-BORROWER PROCESS = [{}], WITH REQUEST ID = [{}],"
              + " OTHERWISE CO-BORROWER SUCCESSFUL ADDED.", activityId, requestId);
          caseService.disableCaseExecution(enabledExecution.getId());
          LOG.info("########## SUCCESSFUL DISABLED CO-BORROWER PROCESS = [{}], WITH REQUEST ID = [{}]", activityId, requestId);
        }
      }
    }
  }
}
