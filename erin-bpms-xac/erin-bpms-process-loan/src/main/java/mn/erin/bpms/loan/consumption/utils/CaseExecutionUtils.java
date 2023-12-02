package mn.erin.bpms.loan.consumption.utils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.delegate.DelegateCaseExecution;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.runtime.CaseExecution;
import org.camunda.bpm.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.usecase.execution.CompleteExecutionByActivityId;
import mn.erin.domain.bpm.usecase.execution.CompleteExecutionByActivityIdInput;

import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;

/**
 * @author Tamir
 */
public final class CaseExecutionUtils
{
  private static final Logger LOG = LoggerFactory.getLogger(CaseExecutionUtils.class);
  private static final String PROCESS_TASK_ACTIVITY_TYPE = "processTask";
  private static final String USER_TASK_ACTIVITY_TYPE = "userTask";

  private static final String CASE_INSTANCE_ID_NULL_LOG = "####### Terminate task rejected! CASE INSTANCE ID is null!";
  private static final String SUSPEND_PROCESS_INSTANCE_LOG = "######## Suspends process instance of task definition key = {}";

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
   * Completes execution by activity id.
   *
   * @param caseService    case service.
   * @param caseInstanceId unique case instance id.
   * @throws UseCaseException
   */
  public static void completeExecutionByActivityId(mn.erin.domain.bpm.service.CaseService caseService, String caseInstanceId, String activityId)
      throws UseCaseException
  {
    CompleteExecutionByActivityIdInput input = new CompleteExecutionByActivityIdInput(caseInstanceId, activityId);

    CompleteExecutionByActivityId useCase = new CompleteExecutionByActivityId(caseService);

    useCase.execute(input);
  }

  /**
   * If execution state disabled it re-enables then manually start execution.
   *
   * @param caseInstanceId  Unique case instance id.
   * @param givenActivityId activity id.
   * @param processEngine   camunda process engine.
   * @param variables       given variables.
   */
  public static void manuallyStartExecution(String caseInstanceId, String givenActivityId, ProcessEngine processEngine, Map<String, Object> variables)
  {
    CaseService caseService = processEngine.getCaseService();
    List<CaseExecution> activeExecutions = CaseExecutionUtils.getActiveExecutions(caseInstanceId, processEngine);

    boolean isActiveExecution = false;

    for (CaseExecution activeExecution : activeExecutions)
    {
      String activityId = activeExecution.getActivityId();

      if (givenActivityId.equals(activityId))
      {
        isActiveExecution = true;
      }
    }

    if (!isActiveExecution)
    {
      List<CaseExecution> enabledExecutions = CaseExecutionUtils.getEnabledExecutions(caseInstanceId, processEngine);

      for (CaseExecution enabledExecution : enabledExecutions)
      {
        String activityId = enabledExecution.getActivityId();

        try
        {
          if (givenActivityId.equals(activityId))
          {
            caseService.manuallyStartCaseExecution(enabledExecution.getId(), variables);
          }
        }
        catch (Exception e)
        {
          String message = String.format("COULD NOT MANUAL START EXECUTION WITH ACTIVITY ID = [%s], REASON = [{}], CASE INSTANCE ID = [%s]",
              activityId, caseInstanceId);
          LOG.error(message, e.getMessage());
        }
      }
    }
  }

  /**
   * Terminates active executions by case instance id and given activity ids.
   *
   * @param caseInstanceId       Unique case instance id.
   * @param execution            {@link DelegateExecution}.
   * @param terminateActivityIds terminate activity ids.
   */
  public static void terminateActiveExecutions(String caseInstanceId, DelegateExecution execution, List<String> terminateActivityIds)
  {
    CaseService caseService = execution.getProcessEngine().getCaseService();

    if (null == caseInstanceId)
    {
      LOG.warn("####### Terminate executions rejected! CASE INSTANCE ID is null!");
      return;
    }

    List<CaseExecution> activeExecutions = caseService.createCaseExecutionQuery()
        .active()
        .caseInstanceId(caseInstanceId)
        .list();

    for (CaseExecution activeExecution : activeExecutions)
    {
      String activityId = activeExecution.getActivityId();
      String activityType = activeExecution.getActivityType();

      if (terminateActivityIds.contains(activityId) && isTerminableExecution(activityType))
      {
        try
        {
          caseService.terminateCaseExecution(activeExecution.getId(), execution.getVariables());
        }
        catch (Exception e)
        {
          LOG.error("######## Error occurred when terminate active execution = {}", e.getMessage());
        }
      }
    }
  }

  /**
   * Completes execution of all active task.
   *
   * @param caseInstanceId Unique instance id.
   * @param execution      {@link DelegateExecution}
   */
  public static void suspendAllActiveProcesses(String caseInstanceId, DelegateExecution execution)
  {
    TaskService taskService = execution.getProcessEngine().getTaskService();

    String requestId = (String) execution.getVariable(PROCESS_REQUEST_ID);

    if (null == caseInstanceId)
    {
      LOG.warn(CASE_INSTANCE_ID_NULL_LOG);
      return;
    }

    List<Task> taskList = taskService.createTaskQuery()
        .caseInstanceId(caseInstanceId)
        .active()
        .list();

    RuntimeService runtimeService = execution.getProcessEngine().getRuntimeService();

    String processInstanceId = null;
    for (Task task : taskList)
    {
      try
      {
        processInstanceId = task.getProcessInstanceId();
        String taskDefinitionKey = task.getTaskDefinitionKey();
        LOG.info(SUSPEND_PROCESS_INSTANCE_LOG, taskDefinitionKey);
        runtimeService.suspendProcessInstanceById(processInstanceId);
      }
      catch (Exception e)
      {
        LOG.error("######### ERROR : could not suspend process instance with ID={}, REQUEST_ID={}, REASON = {}",
            processInstanceId, requestId, e.getMessage());
      }
    }
  }

  /**
   * Suspends active process instance by task definition key.
   *
   * @param caseInstanceId     Unique instance id.
   * @param execution          {@link DelegateExecution}
   * @param suspendTaskDefKeys task definition keys to suspend.
   */
  public static void suspendActiveProcessInstancesByDefKey(String caseInstanceId, DelegateExecution execution, List<String> suspendTaskDefKeys,
      boolean isContainDefKeys)
  {
    TaskService taskService = execution.getProcessEngine().getTaskService();

    String requestId = (String) execution.getVariable(PROCESS_REQUEST_ID);

    if (null == caseInstanceId)
    {
      LOG.warn("####### Suspend active process action rejected! CASE INSTANCE ID is null!");
      return;
    }

    List<Task> taskList = taskService.createTaskQuery()
        .caseInstanceId(caseInstanceId)
        .active()
        .list();

    RuntimeService runtimeService = execution.getProcessEngine().getRuntimeService();

    String processInstanceId = null;
    for (Task task : taskList)
    {
      try
      {
        processInstanceId = task.getProcessInstanceId();
        String taskDefinitionKey = task.getTaskDefinitionKey();

        if (isContainDefKeys)
        {
          if (suspendTaskDefKeys.contains(taskDefinitionKey))
          {
            LOG.info(SUSPEND_PROCESS_INSTANCE_LOG, taskDefinitionKey);
            runtimeService.suspendProcessInstanceById(processInstanceId);
          }
        }
        else
        {
          if (!suspendTaskDefKeys.contains(taskDefinitionKey))
          {
            LOG.info(SUSPEND_PROCESS_INSTANCE_LOG, taskDefinitionKey);
            runtimeService.suspendProcessInstanceById(processInstanceId);
          }
        }
      }
      catch (Exception e)
      {
        LOG.error("######### ERROR : could not suspend process instance with INSTANCE ID ={}, REQUEST_ID={}, REASON ={}", processInstanceId, requestId,
            e.getMessage());
      }
    }
  }

  /**
   * Suspends active process instance by task definition key.
   *
   * @param caseInstanceId     Unique instance id.
   * @param execution          {@link DelegateCaseExecution}
   * @param suspendTaskDefKeys task definition keys to suspend.
   */
  public static void suspendActiveProcessInstancesByDefKey(String caseInstanceId, DelegateCaseExecution execution, List<String> suspendTaskDefKeys,
      boolean isContainDefKeys)
  {
    if (null == caseInstanceId)
    {
      LOG.warn("####### Suspend active process action rejected! CASE INSTANCE ID is null!");
      return;
    }
    String requestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));

    TaskService taskService = execution.getProcessEngine().getTaskService();
    RuntimeService runtimeService = execution.getProcessEngine().getRuntimeService();
    List<Task> taskList = taskService.createTaskQuery()
        .caseInstanceId(caseInstanceId)
        .active()
        .list();

    String processInstanceId = null;
    for (Task task : taskList)
    {
      try
      {
        processInstanceId = task.getProcessInstanceId();
        String taskDefinitionKey = task.getTaskDefinitionKey();

        if (isContainDefKeys)
        {
          if (suspendTaskDefKeys.contains(taskDefinitionKey))
          {
            LOG.info(SUSPEND_PROCESS_INSTANCE_LOG, taskDefinitionKey);
            runtimeService.suspendProcessInstanceById(processInstanceId);
          }
        }
        else
        {
          if (!suspendTaskDefKeys.contains(taskDefinitionKey))
          {
            LOG.info(SUSPEND_PROCESS_INSTANCE_LOG, taskDefinitionKey);
            runtimeService.suspendProcessInstanceById(processInstanceId);
          }
        }
      }
      catch (Exception e)
      {
        LOG.error("######### ERROR : could not suspend process instance with INSTANCE ID ={}, REQUEST_ID={}, REASON ={}", processInstanceId, requestId,
            e.getMessage());
      }
    }
  }

  /**
   * Completes executions of all active task with given case instance id.
   *
   * @param caseInstanceId Unique case instance id.
   * @param execution      {@link DelegateExecution}.
   */
  public static void completeActiveTaskExecutions(String caseInstanceId, DelegateExecution execution)
  {
    TaskService taskService = execution.getProcessEngine().getTaskService();

    Map<String, Object> variables = execution.getVariables();
    String requestId = (String) execution.getVariable(PROCESS_REQUEST_ID);

    if (null == caseInstanceId)
    {
      LOG.warn(CASE_INSTANCE_ID_NULL_LOG);
      return;
    }

    List<Task> taskList = taskService.createTaskQuery()
        .caseInstanceId(caseInstanceId)
        .active()
        .list();

    CaseService caseService = execution.getProcessEngine().getCaseService();

    String caseExecutionId = null;
    for (Task task : taskList)
    {
      try
      {
        caseExecutionId = task.getCaseExecutionId();

        if (null == caseExecutionId)
        {
          LOG.warn("######### Case execution id is null! during complete ACTIVE task executions!");
          return;
        }

        CaseExecution caseExecution = caseService.createCaseExecutionQuery()
            .caseExecutionId(caseExecutionId)
            .singleResult();

        if (null == caseExecution)
        {
          LOG.warn("######### Case execution is null with id={} and REQUEST_ID={}, during complete ACTIVE task executions!", caseExecutionId, requestId);
          return;
        }

        if (caseExecution.isActive())
        {
          caseService.completeCaseExecution(caseExecutionId, variables);
        }
        LOG.info("###### Completes case execution with ID ={} and REQUEST_ID={}", caseExecutionId, requestId);
      }
      catch (Exception e)
      {
        LOG.error("########## ERROR : " + e.getMessage());
        LOG.error("######### ERROR : could not complete case execution with ID={}, REQUEST_ID={}", caseExecutionId, requestId);
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

  public static Map<String, Object> getCaseVariables(String caseInstanceId, ProcessEngine processEngine)
  {
    CaseService caseService = processEngine.getCaseService();

    if (null == caseInstanceId)
    {
      return Collections.emptyMap();
    }

    return caseService.getVariables(caseInstanceId);
  }

  /**
   * Gets all disabled executions by case instance id.
   *
   * @param caseInstanceId given case instance id.
   * @param execution      {@link DelegateExecution}.
   * @return
   */
  public static List<CaseExecution> getDisabledExecutions(String caseInstanceId, DelegateExecution execution)
  {
    CaseService caseService = execution.getProcessEngine().getCaseService();

    return caseService.createCaseExecutionQuery()
        .caseInstanceId(caseInstanceId)
        .disabled()
        .list();
  }

  /**
   * Gets all disabled executions by case instance id.
   *
   * @param caseInstanceId given case instance id.
   * @param caseService    {@link CaseService}.
   * @return
   */
  public static List<CaseExecution> getDisabledExecutions(String caseInstanceId, CaseService caseService)
  {
    return caseService.createCaseExecutionQuery()
        .caseInstanceId(caseInstanceId)
        .disabled()
        .list();
  }

  /**
   * Disables all executions.
   *
   * @param execution {@link DelegateExecution}.
   */
  public static void disableAllExecutions(String caseInstanceId, DelegateExecution execution)
  {
    CaseService caseService = execution.getProcessEngine().getCaseService();
    if (null == caseInstanceId)
    {
      LOG.warn(CASE_INSTANCE_ID_NULL_LOG);
      return;
    }
    List<CaseExecution> enabledExecutions = caseService.createCaseExecutionQuery()
        .enabled()
        .caseInstanceId(caseInstanceId)
        .list();

    enabledExecutions.forEach(enabledExecution ->
        caseService.disableCaseExecution(enabledExecution.getId(), execution.getVariables()));
  }

  /**
   * Disables executions by given activity ids.
   *
   * @param execution            {@link DelegateExecution}.
   * @param toDisableActivityIds to disable activity ids.
   */
  public static void disableExecutionsByActivityId(String caseInstanceId, DelegateExecution execution, List<String> toDisableActivityIds)
  {
    CaseService caseService = execution.getProcessEngine().getCaseService();

    if (null != caseInstanceId)
    {
      List<CaseExecution> enabledExecutions = caseService.createCaseExecutionQuery()
          .enabled()
          .caseInstanceId(caseInstanceId)
          .list();

      for (CaseExecution enabledExecution : enabledExecutions)
      {
        String activityId = enabledExecution.getActivityId();

        if (toDisableActivityIds.contains(activityId) && !enabledExecution.isDisabled())
        {
          caseService.disableCaseExecution(enabledExecution.getId(), execution.getVariables());
        }
      }
    }
  }

  /**
   * Disables executions by given activity ids.
   *
   * @param execution            {@link DelegateCaseExecution}.
   * @param toDisableActivityIds to disable activity ids.
   */
  public static void disableExecutionsByActivityId(String caseInstanceId, DelegateCaseExecution execution, List<String> toDisableActivityIds)
  {
    CaseService caseService = execution.getProcessEngine().getCaseService();

    if (null != caseInstanceId)
    {
      List<CaseExecution> enabledExecutions = caseService.createCaseExecutionQuery()
          .enabled()
          .caseInstanceId(caseInstanceId)
          .list();

      for (CaseExecution enabledExecution : enabledExecutions)
      {
        String activityId = enabledExecution.getActivityId();

        if (!enabledExecution.isDisabled() && toDisableActivityIds.contains(activityId))
        {
          caseService.disableCaseExecution(enabledExecution.getId(), execution.getVariables());
        }
      }
    }
  }

  /**
   * Disables other enabled executions from given activity id.
   *
   * @param execution       {@link DelegateExecution}.
   * @param skipActivityIds skip activity ids otherwise do not disable activity ids.
   */
  public static void disableExecutions(String caseInstanceId, DelegateExecution execution, List<String> skipActivityIds)
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

        if (!skipActivityIds.contains(activityId))
        {
          caseService.disableCaseExecution(enabledExecution.getId(), variables);
        }
      }
    }
  }

  /**
   * Find active task process instance id by Task definition key
   *
   * @param taskDefKey     task definition key
   * @param caseInstanceId case instance id
   * @param execution      {@link DelegateExecution}.
   * @return process instance id
   */
  public static String getActiveTaskProcessInstanceIdByTaskDefKey(String taskDefKey, String caseInstanceId, DelegateExecution execution)
  {
    if (StringUtils.isBlank(taskDefKey) || StringUtils.isBlank(caseInstanceId) || null == execution)
    {
      return null;
    }
    TaskService taskService = execution.getProcessEngine().getTaskService();
    List<Task> activeTasks = taskService.createTaskQuery()
        .caseInstanceId(caseInstanceId)
        .list();
    for (Task activeTask : activeTasks)
    {
      String taskDefinitionKey = activeTask.getTaskDefinitionKey();

      if (taskDefinitionKey.equalsIgnoreCase(taskDefKey))
      {
        return activeTask.getProcessInstanceId();
      }
    }

    return null;
  }

  private static boolean isTerminableExecution(String activityType)
  {
    return (activityType.equalsIgnoreCase(PROCESS_TASK_ACTIVITY_TYPE) || activityType.equalsIgnoreCase(USER_TASK_ACTIVITY_TYPE));
  }
}
