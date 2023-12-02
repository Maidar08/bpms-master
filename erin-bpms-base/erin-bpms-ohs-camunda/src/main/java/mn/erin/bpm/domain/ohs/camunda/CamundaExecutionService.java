/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.domain.ohs.camunda;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricCaseActivityInstance;
import org.camunda.bpm.engine.runtime.CaseExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import mn.erin.domain.bpm.BpmMessagesConstants;
import mn.erin.domain.bpm.model.cases.Execution;
import mn.erin.domain.bpm.model.variable.Variable;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.ExecutionService;

import static mn.erin.bpm.domain.ohs.camunda.util.CamundaEntityUtil.getExecutionsFrom;
import static mn.erin.bpm.domain.ohs.camunda.util.CamundaEntityUtil.toExecutions;
import static mn.erin.bpm.domain.ohs.camunda.util.CamundaEntityUtil.toVariableMap;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.MANUAL_ACTIVATE_ERROR_CODE;

/**
 * @author Tamir
 */
public class CamundaExecutionService implements ExecutionService
{
  private static final Logger LOGGER = LoggerFactory.getLogger(CamundaExecutionService.class);
  private static final String CASE_PLAN_MODEL = "casePlanModel";

  private final ProcessEngineProvider processEngineProvider;

  public CamundaExecutionService(ProcessEngineProvider processEngineProvider)
  {
    this.processEngineProvider = Objects.requireNonNull(processEngineProvider, "Camunda process engine provider is required!");
  }

  @Override
  public List<Execution> getCompletedByInstanceId(String instanceId) throws BpmServiceException
  {
    if (StringUtils.isEmpty(instanceId))
    {
      String errorCode = "CamundaExecutionSrvice001";
      throw new BpmServiceException(errorCode, "Instance id cannot be null!");
    }

    List<HistoricCaseActivityInstance> activityInstances = processEngine().getHistoryService().createHistoricCaseActivityInstanceQuery()
        .caseInstanceId(instanceId)
        .completed()
        .list();

    return getExecutionsFrom(activityInstances);
  }

  @Override
  public List<Execution> getEnabledByInstanceId(String instanceId) throws BpmServiceException
  {
    if (StringUtils.isEmpty(instanceId))
    {
      String errorCode = "CamundaExecutionSrvice001";
      throw new BpmServiceException(errorCode, "Instance id cannot be null!");
    }

    List<CaseExecution> caseExecutions = processEngine().getCaseService().createCaseExecutionQuery()
        .caseInstanceId(instanceId)
        .enabled()
        .list();

    return toExecutions(processEngine(), caseExecutions);
  }

  @Override
  public List<Execution> getActiveByInstanceId(String instanceId) throws BpmServiceException
  {
    if (StringUtils.isEmpty(instanceId))
    {
      throw new BpmServiceException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
    }

    List<CaseExecution> caseExecutions = processEngine().getCaseService().createCaseExecutionQuery()
        .caseInstanceId(instanceId)
        .active()
        .list();

    List<CaseExecution> filteredExecutions = new ArrayList<>();
    for (CaseExecution caseExecution : caseExecutions)
    {
      String activityType = caseExecution.getActivityType();

      if (!activityType.equalsIgnoreCase(CASE_PLAN_MODEL))
      {
        filteredExecutions.add(caseExecution);
      }
    }

    return toExecutions(processEngine(), filteredExecutions);
  }

  @Override
  public void manualActivate(String executionId) throws BpmServiceException
  {
    if (StringUtils.isEmpty(executionId))
    {
      String errorCode = "CamundaExecutionSrvice002";
      throw new BpmServiceException(errorCode, "Execution id is empty!");
    }

    CaseService caseService = processEngine().getCaseService();
    try
    {
      caseService.manuallyStartCaseExecution(executionId);
    }
    catch (Exception e)
    {
      throw new BpmServiceException(MANUAL_ACTIVATE_ERROR_CODE, e.getMessage());
    }
  }

  @Override
  public void manualActivate(String executionId, List<Variable> variables) throws BpmServiceException
  {
    if (StringUtils.isEmpty(executionId) || variables.isEmpty())
    {
      String errorCode = "CamundaExecutionSrvice002";
      throw new BpmServiceException(errorCode, "Execution id or variables empty!");
    }

    CaseService caseService = processEngine().getCaseService();

    Map<String, Serializable> variablesMap = toVariableMap(variables);
    Map<String, Object> camundaVariables = new HashMap<>(variablesMap);
    try
    {
      caseService.manuallyStartCaseExecution(executionId, camundaVariables);
    }
    catch (Exception e)
    {
      throw new BpmServiceException(MANUAL_ACTIVATE_ERROR_CODE, e.getMessage());
    }
  }

  @Override
  public void manualActivate(String executionId, List<Variable> variables, List<Variable> deletions) throws BpmServiceException
  {
    if (StringUtils.isEmpty(executionId))
    {
      throw new BpmServiceException(BpmMessagesConstants.EXECUTION_ID_NULL_CODE, BpmMessagesConstants.EXECUTION_ID_NULL_MESSAGE);
    }

    if (variables.isEmpty() || deletions.isEmpty())
    {
      String errorCode = "CamundaExecutionSrvice003";
      throw new BpmServiceException(errorCode, "Variables or delete variables is empty!");
    }

    CaseService caseService = processEngine().getCaseService();

    Map<String, Serializable> variablesMap = toVariableMap(variables);
    Map<String, Object> variablesCamunda = new HashMap<>(variablesMap);

    List<String> variableIds = new ArrayList<>();

    for (Variable deleteVariable : deletions)
    {
      variableIds.add(deleteVariable.getId().getId());
    }
    caseService.removeVariables(executionId, variableIds);

    try
    {
      caseService.manuallyStartCaseExecution(executionId, variablesCamunda);
    }
    catch (Exception e)
    {
      throw new BpmServiceException(MANUAL_ACTIVATE_ERROR_CODE, e.getMessage());
    }
  }

  private ProcessEngine processEngine()
  {
    return processEngineProvider.getProcessEngine();
  }
}
