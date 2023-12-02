package mn.erin.domain.bpm.usecase.form.submit_form;

import java.util.Map;

public class SubmitProcessFormInput
{
  private String executionId;
  private String taskId;
  private Map<String, Object> properties;

  public SubmitProcessFormInput(String executionId, String taskId, Map<String, Object> properties)
  {
    this.executionId = executionId;
    this.taskId = taskId;
    this.properties = properties;
  }

  public String getExecutionId()
  {
    return executionId;
  }

  public void setExecutionId(String executionId)
  {
    this.executionId = executionId;
  }

  public Map<String, Object> getProperties()
  {
    return properties;
  }

  public void setProperties(Map<String, Object> properties)
  {
    this.properties = properties;
  }

  public String getTaskId()
  {
    return taskId;
  }

  public void setTaskId(String taskId)
  {
    this.taskId = taskId;
  }
}
