package mn.erin.bpms.loan.consumption.utils;

import java.util.Collections;
import java.util.List;

import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.delegate.DelegateCaseExecution;
import org.camunda.bpm.engine.task.Task;

public final class TaskUtils
{
  private TaskUtils()
  {

  }

  public static List<Task> getActiveTasks(DelegateCaseExecution caseExecution, String caseInstanceId)
  {
    if (null == caseInstanceId)
    {
      return Collections.emptyList();
    }

    TaskService taskService = caseExecution.getProcessEngine().getTaskService();

    return taskService.createTaskQuery()
        .caseInstanceId(caseInstanceId)
        .active()
        .list();
  }
}
