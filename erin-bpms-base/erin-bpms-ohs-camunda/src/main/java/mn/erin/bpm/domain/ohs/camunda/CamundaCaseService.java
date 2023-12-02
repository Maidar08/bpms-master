/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.domain.ohs.camunda;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.CaseExecution;
import org.camunda.bpm.engine.runtime.CaseInstance;
import org.camunda.bpm.engine.task.Task;

import mn.erin.domain.bpm.model.cases.Case;
import mn.erin.domain.bpm.model.cases.CaseInstanceId;
import mn.erin.domain.bpm.model.variable.Variable;
import mn.erin.domain.bpm.model.variable.VariableId;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CaseService;

import static mn.erin.domain.bpm.BpmMessagesConstants.START_CASE_ERROR_CODE;

/**
 * @author Tamir
 */
public class CamundaCaseService implements CaseService
{
  private final ProcessEngineProvider processEngineProvider;
  private static final String TASK_ID_IS_NULL_CODE = "CamundaCaseService001";
  private static final String TASK_ID_IS_NULL_MESSAGE = "Task id cannot be null!";
  private static final String EXECUTION_ID_IS_NULL_CODE = "CamundaCaseService003";
  private static final String EXECUTION_ID_IS_NULL_MESSAGE = "Execution id cannot be null!";
  
  public CamundaCaseService(ProcessEngineProvider processEngineProvider)
  {
    this.processEngineProvider = Objects.requireNonNull(processEngineProvider, "ProcessEngineProvider is required!");
  }

  @Override
  public Case findByTaskId(String taskId) throws BpmServiceException
  {
    if (StringUtils.isBlank(taskId))
    {
      throw new BpmServiceException(TASK_ID_IS_NULL_CODE, TASK_ID_IS_NULL_MESSAGE);
    }

    Task task = processEngine().getTaskService().createTaskQuery()
        .taskId(taskId)
        .singleResult();

    if (null != task)
    {
      String processInstanceId = task.getProcessInstanceId();

      CaseInstance caseInstance = processEngine().getCaseService().createCaseInstanceQuery()
          .active()
          .subProcessInstanceId(processInstanceId)
          .singleResult();

      if (null == caseInstance)
      {
        String message = String
            .format("Case instance does not exist with process instance id of current task name = [%s], definitionKey = [%s]", task.getName(),
                task.getTaskDefinitionKey());
        throw new BpmServiceException("CamundaCaseService002", message);
      }

      return new Case(new CaseInstanceId(caseInstance.getCaseInstanceId()), "DEFAULT");
    }
    return null;
  }

  @Override
  public void deleteVariablesByInstanceIdAndVariableName(String instanceId, List<String> documentNames) throws BpmServiceException
  {
    if (StringUtils.isBlank(instanceId))
    {
      throw new BpmServiceException(EXECUTION_ID_IS_NULL_CODE, EXECUTION_ID_IS_NULL_MESSAGE);
    }

    if (null == documentNames)
    {
      String errorCode = "CamundaCaseService004";
      throw new BpmServiceException(errorCode, "Variable name cannot be null!");
    }

    documentNames.forEach(documentName -> processEngine().getCaseService().removeVariable(instanceId, documentName));
  }

  @Override
  public void setCaseVariables(String caseInstanceId, Map<String, Object> variables)
  {
    processEngine().getCaseService().setVariables(caseInstanceId, variables);
  }

  @Override
  public void setCaseVariableById(String caseInstanceId, String variableId, Object variableValue)
  {
    processEngine().getCaseService().setVariable(caseInstanceId, variableId, variableValue);
  }

  @Override
  public boolean terminateCase(String caseExecutionId)
  {
    if (!StringUtils.isBlank(caseExecutionId) && null != getCaseInstance(caseExecutionId))
    {
      processEngine().getCaseService().terminateCaseExecution(caseExecutionId);
      return true;
    }
    return false;
  }

  @Override
  public boolean closeCases(String caseExecutionId)
  {
    if (!StringUtils.isBlank(caseExecutionId) && null != getCaseInstance(caseExecutionId))
    {
      processEngine().getCaseService().closeCaseInstance(caseExecutionId);
      return true;
    }
    return false;
  }

  @Override
  public Case startCase(String caseDefinitionKey) throws BpmServiceException
  {
    try
    {

      CaseInstance caseInstance = processEngine().getCaseService().createCaseInstanceByKey(caseDefinitionKey);
      String caseInstanceId = caseInstance.getCaseInstanceId();
      String activityName = caseInstance.getActivityName();

      return new Case(CaseInstanceId.valueOf(caseInstanceId), activityName);
    }
    catch (Exception e)
    {
      throw new BpmServiceException(START_CASE_ERROR_CODE, e.getMessage());
    }
  }

  @Override
  public void completeByActivityId(String caseInstanceId, String activityId)
  {
    List<CaseExecution> activeExecutions = processEngine().getCaseService().createCaseExecutionQuery()
        .caseInstanceId(caseInstanceId)
        .active()
        .list();

    if (null != activityId && null != activeExecutions)
    {
      for (CaseExecution activeExecution : activeExecutions)
      {
        String id = activeExecution.getId();
        String activeActivityId = activeExecution.getActivityId();

        if (activityId.equals(activeActivityId))
        {
          processEngine().getCaseService().completeCaseExecution(id);
        }
      }
    }
  }

  @Override
  public List<Variable> getVariables(String instanceId) throws BpmServiceException
  {
    if (StringUtils.isBlank(instanceId))
    {
      throw new BpmServiceException(TASK_ID_IS_NULL_CODE, EXECUTION_ID_IS_NULL_MESSAGE);
    }

    CaseInstance caseInstance = processEngine().getCaseService()
        .createCaseInstanceQuery()
        .caseInstanceId(instanceId)
        .singleResult();

    if (null == caseInstance)
    {
      return Collections.emptyList();
    }
    else
    {
      Map<String, Object> variables = processEngine().getCaseService().getVariables(caseInstance.getId());
      return getVariableList(variables);
    }
  }

  @Override
  public Object getVariableById(String instanceId, String name) throws BpmServiceException
  {
    if (StringUtils.isBlank(instanceId))
    {
      throw new BpmServiceException(TASK_ID_IS_NULL_CODE, EXECUTION_ID_IS_NULL_MESSAGE
      );
    }
    CaseInstance caseInstance = getCaseInstance(instanceId);
    return processEngineProvider.getProcessEngine().getCaseService().getVariable(caseInstance.getId(), name);
  }

  @Override
  public List<Case> getUserCases()
  {
    List<CaseInstance> caseInstances = processEngine().getCaseService().createCaseInstanceQuery()
        .active()
        .list();

    List<Case> userCases = new ArrayList<>();

    for (CaseInstance caseInstance : caseInstances)
    {
      String id = caseInstance.getId();
      String name = caseInstance.getActivityName();

      userCases.add(new Case(new CaseInstanceId(id), name));
    }

    return userCases;
  }

  @Override
  public void disable(String id)
  {
    if (!StringUtils.isEmpty(id))
    {
      org.camunda.bpm.engine.CaseService caseService = processEngine().getCaseService();
      caseService.disableCaseExecution(id);
    }
  }

  private List<Variable> getVariableList(Map<String, Object> variables)
  {
    List<Variable> variableList = new ArrayList<>();
    Set<Map.Entry<String, Object>> entries = variables.entrySet();

    for (Map.Entry<String, Object> entry : entries)
    {
      String key = entry.getKey();
      Object value = entry.getValue();

      variableList.add(new Variable(VariableId.valueOf(key), (Serializable) value));
    }
    return variableList;
  }

  private CaseInstance getCaseInstance(String instanceId)
  {
    return processEngine().getCaseService()
        .createCaseInstanceQuery()
        .caseInstanceId(instanceId)
        .singleResult();
  }

  private ProcessEngine processEngine()
  {
    return processEngineProvider.getProcessEngine();
  }
}
