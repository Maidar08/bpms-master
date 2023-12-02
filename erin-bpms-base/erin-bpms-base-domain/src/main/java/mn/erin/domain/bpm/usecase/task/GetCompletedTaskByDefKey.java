package mn.erin.domain.bpm.usecase.task;

import java.util.Collection;
import java.util.Objects;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.task.Task;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;

/**
 * @author Tamir
 */
public class GetCompletedTaskByDefKey implements UseCase<GetCompletedTaskByDefKeyInput, GetCompletedTaskByDefKeyOutput>
{
  private final BpmsServiceRegistry bpmsServiceRegistry;

  public GetCompletedTaskByDefKey(BpmsServiceRegistry bpmsServiceRegistry)
  {
    this.bpmsServiceRegistry = Objects.requireNonNull(bpmsServiceRegistry, "BPM service registry is required!");
  }

  @Override
  public GetCompletedTaskByDefKeyOutput execute(GetCompletedTaskByDefKeyInput input) throws UseCaseException
  {
    if (null == input)
    {
      return null;
    }

    String caseInstanceId = input.getCaseInstanceId();
    String taskDefinitionKey = input.getTaskDefinitionKey();

    GetCompletedTasks getCompletedTasks = new GetCompletedTasks(bpmsServiceRegistry);
    GetCompletedTasksOutput completedTasksOutput = getCompletedTasks.execute(caseInstanceId);

    Collection<Task> completedTasks = completedTasksOutput.getCompletedTasks();

    for (Task completedTask : completedTasks)
    {
      String definitionKey = completedTask.getDefinitionKey();

      if (taskDefinitionKey.equals(definitionKey))
      {
        return new GetCompletedTaskByDefKeyOutput(completedTask);
      }
    }

    return null;
  }
}
