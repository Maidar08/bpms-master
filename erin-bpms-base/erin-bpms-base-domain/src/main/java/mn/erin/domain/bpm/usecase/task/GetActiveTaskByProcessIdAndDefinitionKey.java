package mn.erin.domain.bpm.usecase.task;

import java.util.List;
import java.util.Objects;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.task.Task;
import mn.erin.domain.bpm.service.TaskService;

public class GetActiveTaskByProcessIdAndDefinitionKey extends AbstractUseCase<GetActiveTaskByProcessIdAndDefinitionKeyInput, List<Task>>
{
  private final TaskService taskService;

  public GetActiveTaskByProcessIdAndDefinitionKey(TaskService taskService)
  {
    this.taskService = Objects.requireNonNull(taskService, "Task service is required!");
  }

  @Override
  public List<Task> execute(GetActiveTaskByProcessIdAndDefinitionKeyInput input) throws UseCaseException
  {
    validateNotBlank(input.getProcessId(), "Process Id should not be null!");
    validateNotBlank(input.getDefinitionKey(), "Definition key should not be null!");

    return taskService.getActiveTaskByProcessIdAndDefinitionKey(input.getProcessId(), input.getDefinitionKey());
  }
}
