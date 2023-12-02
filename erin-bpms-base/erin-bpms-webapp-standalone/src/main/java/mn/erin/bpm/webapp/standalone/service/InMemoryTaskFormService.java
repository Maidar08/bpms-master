package mn.erin.bpm.webapp.standalone.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import mn.erin.domain.bpm.model.form.TaskForm;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.TaskFormService;

@Service
public class InMemoryTaskFormService implements TaskFormService
{
  @Override
  public void submitForm(String taskId, Map<String, Object> properties) throws BpmServiceException
  {
     throw new UnsupportedOperationException();
  }

  @Override
  public TaskForm getFormByTaskId(String caseInstanceId, String taskId) throws BpmServiceException
  {
    return null;
  }

  @Override
  public TaskForm getFormByTaskIdBeforeCreationOfTask(String taskId) throws BpmServiceException
  {
    return null;
  }

  @Override
  public List<TaskForm> getFormsByDefinitionId(String definitionId) throws BpmServiceException
  {
    return null;
  }

  @Override
  public List<TaskForm> getFormsByDefinitionKey(String definitionKey) throws BpmServiceException
  {
    return null;
  }

  @Override
  public List<TaskForm> getFormsByProcessInstanceId(String processInstanceId) throws BpmServiceException
  {
    return null;
  }

  @Override
  public List<TaskForm> getFormsByCaseInstanceId(String caseInstanceId) throws BpmServiceException
  {
    return null;
  }

  @Override
  public TaskForm getFormByTaskId(String taskId)
  {
    return null;
  }
}
