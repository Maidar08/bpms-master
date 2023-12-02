package mn.erin.domain.bpm.usecase.task;

import mn.erin.domain.bpm.model.task.Task;

/**
 * @author Tamir
 */
public class GetCompletedTaskByDefKeyOutput
{
  private Task completedTask;

  public GetCompletedTaskByDefKeyOutput(Task completedTask)
  {
    this.completedTask = completedTask;
  }

  public Task getCompletedTask()
  {
    return completedTask;
  }

  public void setCompletedTask(Task completedTask)
  {
    this.completedTask = completedTask;
  }
}
