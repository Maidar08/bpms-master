package mn.erin.domain.bpm.usecase.task;

import java.util.Collection;

import mn.erin.domain.bpm.model.task.Task;

/**
 * @author Tamir
 */
public class GetCompletedTasksOutput
{
  private Collection<Task> completedTasks;

  public GetCompletedTasksOutput(Collection<Task> completedTasks)
  {
    this.completedTasks = completedTasks;
  }

  public Collection<Task> getCompletedTasks()
  {
    return completedTasks;
  }

  public void setCompletedTasks(Collection<Task> completedTasks)
  {
    this.completedTasks = completedTasks;
  }
}
