package mn.erin.bpm.rest.model;

public class RestServiceTask
{
  private String taskName;
  private String taskState;

  public String getTaskName()
  {
    return taskName;
  }

  public void setTaskName(String taskName)
  {
    this.taskName = taskName;
  }

  public String getTaskState()
  {
    return taskState;
  }

  public void setTaskState(String taskState)
  {
    this.taskState = taskState;
  }
}
