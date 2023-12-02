package mn.erin.domain.bpm.usecase.task;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.task.Task;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;
import mn.erin.domain.bpm.service.TaskService;

import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_MESSAGE;
import static mn.erin.domain.bpm.util.process.TaskUtils.filterByTaskName;
import static mn.erin.domain.bpm.util.process.TaskUtils.getLastFinishedTasks;

/**
 * @author Tamir
 */
public class GetCompletedTasks implements UseCase<String, GetCompletedTasksOutput>
{
  private final BpmsServiceRegistry bpmsServiceRegistry;

  public GetCompletedTasks(BpmsServiceRegistry bpmsServiceRegistry)
  {
    this.bpmsServiceRegistry = Objects.requireNonNull(bpmsServiceRegistry, "Bpms service registry is required!");
  }

  @Override
  public GetCompletedTasksOutput execute(String caseInstanceId) throws UseCaseException
  {

    if (StringUtils.isBlank(caseInstanceId))
    {
      throw new UseCaseException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
    }

    TaskService taskService = bpmsServiceRegistry.getTaskService();
    List<Task> completedTasks = taskService.getCompletedByCaseInstanceId(caseInstanceId);

    if (completedTasks.isEmpty())
    {
      return new GetCompletedTasksOutput(Collections.emptyList());
    }

    List<Task> lastFinishedTasks = getLastFinishedTasks(completedTasks);
    List<Task> filteredTasks = filterByTaskName(lastFinishedTasks);

    return new GetCompletedTasksOutput(filteredTasks);
  }
}
