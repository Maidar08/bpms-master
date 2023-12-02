package consumption.util;

import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.delegate.DelegateCaseExecution;
import org.camunda.bpm.engine.impl.cmmn.entity.runtime.CaseExecutionEntity;
import org.camunda.bpm.engine.runtime.CaseExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tamir
 */
public final class CaseExecutionUtils
{
  private static final Logger LOG = LoggerFactory.getLogger(CaseExecutionUtils.class);
  private CaseExecutionUtils()
  {

  }

  /**
   * Terminates case execution by given activity id, if not active.
   *
   * @param caseInstanceId  Unique case instance id.
   * @param processEngine   Camunda process engine.
   * @param givenActivityId Given activity id.
   * @param variables       to set variables.
   */
  public static void terminateExecutionByActivityId(String caseInstanceId, ProcessEngine processEngine, String givenActivityId, Map<String, Object> variables)
  {
    CaseService caseService = processEngine.getCaseService();
    List<CaseExecution> activeExecutions = CaseExecutionUtils.getActiveExecutions(caseInstanceId, processEngine);

    for (CaseExecution activeExecution : activeExecutions)
    {
      String activityId = activeExecution.getActivityId();

      if (givenActivityId.equals(activityId))
      {
        try
        {
          caseService.terminateCaseExecution(activeExecution.getId(), variables);
        }
        catch (Exception e)
        {
          LOG.error(e.getMessage());
        }
      }
    }
  }

  /**
   * Gets active executions with given case instance id.
   *
   * @param caseInstanceId given case instance id.
   * @param processEngine  {@link ProcessEngine}.
   * @return Enabled executions.
   */
  public static List<CaseExecution> getActiveExecutions(String caseInstanceId, ProcessEngine processEngine)
  {
    CaseService caseService = processEngine.getCaseService();

    return caseService.createCaseExecutionQuery()
        .caseInstanceId(caseInstanceId)
        .active()
        .list();
  }

  public static void disableExecutions(String caseInstanceId, DelegateCaseExecution execution, List<String> skipActivityIds)
  {
    CaseService caseService = execution.getProcessEngine().getCaseService();
    Map<String, Object> variables = execution.getVariables();

    if (null != caseInstanceId)
    {
      List<CaseExecution> enabledExecutions = caseService.createCaseExecutionQuery()
          .enabled()
          .caseInstanceId(caseInstanceId)
          .list();

      for (CaseExecution enabledExecution : enabledExecutions)
      {
        String activityId = enabledExecution.getActivityId();
        if (!skipActivityIds.contains(activityId) && !((CaseExecutionEntity) enabledExecution).isCompleted() && !enabledExecution.isDisabled())
        {
            caseService.disableCaseExecution(enabledExecution.getId(), variables);
        }
      }
    }
  }

  /**
   * Gets enabled executions with given case instance id.
   *
   * @param caseInstanceId given case instance id.
   * @param processEngine  {@link ProcessEngine}.
   * @return Enabled executions.
   */
  public static List<CaseExecution> getEnabledExecutions(String caseInstanceId, ProcessEngine processEngine)
  {
    CaseService caseService = processEngine.getCaseService();

    return caseService.createCaseExecutionQuery()
        .caseInstanceId(caseInstanceId)
        .enabled()
        .list();
  }
}
