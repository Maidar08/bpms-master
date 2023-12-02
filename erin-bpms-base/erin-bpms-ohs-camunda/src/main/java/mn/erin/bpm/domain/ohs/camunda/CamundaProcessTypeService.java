package mn.erin.bpm.domain.ohs.camunda;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.CaseInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.model.process.ProcessDefinitionType;
import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.model.process.ProcessRequestId;
import mn.erin.domain.bpm.model.process.ProcessType;
import mn.erin.domain.bpm.model.process.ProcessTypeId;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.repository.ProcessTypeRepository;
import mn.erin.domain.bpm.service.ProcessTypeService;

/**
 * @author EBazarragchaa
 */
public class CamundaProcessTypeService implements ProcessTypeService
{
  private static final Logger LOG = LoggerFactory.getLogger(CamundaProcessTypeService.class);
  private final ProcessEngineProvider processEngineProvider;
  private final ProcessTypeRepository processTypeRepository;
  private final ProcessRequestRepository processRequestRepository;

  public CamundaProcessTypeService(ProcessEngineProvider processEngineProvider, ProcessTypeRepository processTypeRepository,
      ProcessRequestRepository processRequestRepository)
  {
    this.processEngineProvider = processEngineProvider;
    this.processTypeRepository = processTypeRepository;
    this.processRequestRepository = processRequestRepository;
  }

  @Override
  public String startProcess(String processTypeId, String processRequestId)
  {
    final ProcessType processType = getProcessType(processTypeId);
    ProcessRequest processRequest = processRequestRepository.findById(ProcessRequestId.valueOf(processRequestId));

    if (null == processRequest)
    {
      throw new IllegalArgumentException("Process request with id [" + processRequestId + "] not found!");
    }

    String definitionKey = processType.getDefinitionKey();
    String processId = processRequest.getId().getId();

    Map<String, Serializable> parameters = processRequest.getParameters();

    Map<String, Object> variables = new HashMap<>();

    variables.put(BpmModuleConstants.PROCESS_REQUEST_ID, processId);
    variables.put(BpmModuleConstants.STARTED_DATE, new Date());

    variables.put(BpmModuleConstants.PROCESS_TYPE_ID, processTypeId);
    variables.put(BpmModuleConstants.PROCESS_TYPE_NAME, processType.getName());

    variables.putAll(parameters);

    String processInstanceId = null;

    if (ProcessDefinitionType.CASE.equals(processType.getProcessDefinitionType()))
    {
      processInstanceId = startCamundaCase(definitionKey, variables);
    }
    else
    {
      processInstanceId = startCamundaProcess(definitionKey, variables);
    }

    return processInstanceId;
  }

  @Override
  public String startProcess(String processTypeId)
  {
    final ProcessType processType = getProcessType(processTypeId);
    String definitionKey = processType.getDefinitionKey();

    Map<String, Object> variables = new HashMap<>();
    variables.put(BpmModuleConstants.STARTED_DATE, new Date());

    variables.put(BpmModuleConstants.PROCESS_TYPE_ID, processTypeId);
    variables.put(BpmModuleConstants.PROCESS_TYPE_NAME, processType.getName());

    String processInstanceId = null;

    if (ProcessDefinitionType.CASE.equals(processType.getProcessDefinitionType()))
    {
      processInstanceId = startCamundaCase(definitionKey, variables);
    }
    else
    {
      processInstanceId = startCamundaProcess(definitionKey, variables);
    }

    return processInstanceId;
  }

  @Override
  public String startProcess(String processTypeId, Map<String, Object> variables)
  {
    final ProcessType processType = getProcessType(processTypeId);

    String definitionKey = processType.getDefinitionKey();

    variables.put(BpmModuleConstants.STARTED_DATE, new Date());

    variables.put(BpmModuleConstants.PROCESS_TYPE_ID, processTypeId);
    variables.put(BpmModuleConstants.PROCESS_TYPE_NAME, processType.getName());

    String processInstanceId = null;

    if (ProcessDefinitionType.CASE.equals(processType.getProcessDefinitionType()))
    {
      processInstanceId = startCamundaCase(definitionKey, variables);
    }
    else
    {
      processInstanceId = startCamundaProcess(definitionKey, variables);
    }

    return processInstanceId;
  }

  @Override
  public void terminateProcess(String executionId)
  {
    if (null != executionId)
    {
      processEngine().getCaseService().terminateCaseExecution(executionId);
    }
  }

  @Override
  public String startProcessWithVariables(String processTypeId, String processRequestId, Map<String, Object> params)
  {
    ProcessType processType = processTypeRepository.findById(ProcessTypeId.valueOf(processTypeId));
    ProcessRequest processRequest = processRequestRepository.findById(ProcessRequestId.valueOf(processRequestId));

    if (null == processType)
    {
      throw new IllegalArgumentException("Process type with id [" + processTypeId + "] not found!");
    }

    if (null == processRequest)
    {
      throw new IllegalArgumentException("Process request with id [" + processRequestId + "] not found!");
    }

    String definitionKey = processType.getDefinitionKey();
    String processId = processRequest.getId().getId();

    Map<String, Serializable> parameters = processRequest.getParameters();

    Map<String, Object> variables = new HashMap<>();

    variables.put(BpmModuleConstants.PROCESS_REQUEST_ID, processId);
    variables.put(BpmModuleConstants.STARTED_DATE, new Date());

    variables.put(BpmModuleConstants.PROCESS_TYPE_ID, processTypeId);
    variables.put(BpmModuleConstants.PROCESS_TYPE_NAME, processType.getName());

    variables.putAll(parameters);
    variables.putAll(params);

    String processInstanceId = null;

    if (ProcessDefinitionType.CASE.equals(processType.getProcessDefinitionType()))
    {
      processInstanceId = startCamundaCase(definitionKey, variables);
    }
    else
    {
      processInstanceId = startCamundaProcess(definitionKey, variables);
    }

    return processInstanceId;
  }

  private String startCamundaProcess(String processDefinitionKey, Map<String, Object> parameters)
  {
    ProcessInstance processInstance = processEngine().getRuntimeService().startProcessInstanceByKey(processDefinitionKey, parameters);
    if (null == processInstance)
    {
      throw new IllegalArgumentException("Process with key [" + processDefinitionKey + "] could not be started!");
    }

    return processInstance.getProcessInstanceId();
  }

  private String startCamundaCase(String caseDefinitionKey, Map<String, Object> parameters)
  {
    CaseInstance caseInstance = processEngine().getCaseService().
        createCaseInstanceByKey(caseDefinitionKey, parameters);

    if (null == caseInstance)
    {
      throw new IllegalArgumentException("Case with key [" + caseDefinitionKey + "] could not be started!");
    }
    return caseInstance.getCaseInstanceId();
  }

  private ProcessEngine processEngine()
  {
    return processEngineProvider.getProcessEngine();
  }

  private ProcessType getProcessType(String processTypeId)
  {
    ProcessType processType = processTypeRepository.findById(ProcessTypeId.valueOf(processTypeId));

    if (null == processType)
    {
      throw new IllegalArgumentException("Process type with id [" + processTypeId + "] not found!");
    }

    return processType;
  }
}
