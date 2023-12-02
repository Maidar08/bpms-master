package mn.erin.bpm.domain.ohs.camunda;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.ProcessEngine;

import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.RuntimeService;

/**
 * @author Lkhagvadorj.A
 **/

public class CamundaRunTimeService implements RuntimeService
{
  private final ProcessEngineProvider processEngineProvider;
  private static final String EXECUTION_ID_IS_NULL_CODE = "CamundaRuntimeService001";
  private static final String EXECUTION_ID_IS_NULL_MESSAGE = "Execution id cannot be null!";
  public CamundaRunTimeService(ProcessEngineProvider processEngineProvider)
  {
    this.processEngineProvider = processEngineProvider;
  }

  @Override
  public void suspendProcess(String processInstanceId)
  {
    if (!StringUtils.isBlank(processInstanceId))
    {
      org.camunda.bpm.engine.RuntimeService runtimeService = processEngine().getRuntimeService();
      runtimeService.suspendProcessInstanceById(processInstanceId);
    }
  }

  @Override
  public void closeProcess(String processInstanceId)
  {
    if (!StringUtils.isBlank(processInstanceId))
    {
      org.camunda.bpm.engine.RuntimeService runtimeService = processEngine().getRuntimeService();

      runtimeService.deleteProcessInstanceIfExists(processInstanceId, "Already past 48 hours.",false, false, false, false);
    }
  }

  @Override
  public void setVariable(String processInstanceId, String variableName, Object value)
  {
    if (!StringUtils.isBlank(processInstanceId) && !StringUtils.isBlank(variableName))
    {
      org.camunda.bpm.engine.RuntimeService runtimeService = processEngine().getRuntimeService();
      runtimeService.setVariable(processInstanceId, variableName, value);
    }
  }

  @Override
  public void setVariables(String executionId, Map<String, Object> variables)
  {
    org.camunda.bpm.engine.RuntimeService runtimeService = processEngine().getRuntimeService();
    runtimeService.setVariables(executionId, variables);
  }

  @Override
  public Object getVariableById(String executionId, String variableId)
  {
    org.camunda.bpm.engine.RuntimeService runtimeService = processEngine().getRuntimeService();
    return runtimeService.getVariable(executionId, variableId);
  }

  @Override
  public Map<String, Object> getRuntimeVariables(String executionId) throws BpmServiceException
  {
    if (StringUtils.isBlank(executionId))
    {
      throw new BpmServiceException(EXECUTION_ID_IS_NULL_CODE, EXECUTION_ID_IS_NULL_MESSAGE);
    }
    org.camunda.bpm.engine.RuntimeService runtimeService  = processEngine().getRuntimeService();
    return runtimeService.getVariables(executionId);
  }


  private ProcessEngine processEngine()
  {
    return processEngineProvider.getProcessEngine();
  }
}
