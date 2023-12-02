package mn.erin.bpm.base.service.memory;

import java.util.List;

import org.springframework.stereotype.Service;

import mn.erin.domain.bpm.model.task.Task;
import mn.erin.domain.bpm.service.TaskService;

@Service
public class InMemoryTaskService implements TaskService
{
  @Override
  public List<Task> getActiveByCaseInstanceId(String caseInstanceId)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Task> getCompletedByCaseInstanceId(String caseInstanceId)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Task> getActiveTaskByProcessIdAndDefinitionKey(String processId, String definitionKey)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Task> getActiveTaskByProcessInstanceId(String processInstanceId)
  {
    throw new UnsupportedOperationException();
  }
}
