package mn.erin.domain.bpm.usecase.execution;

public class CompleteExecutionByActivityIdInput
{
  private String caseInstanceId;
  private String activityId;

  public CompleteExecutionByActivityIdInput(String caseInstanceId, String activityId)
  {
    this.caseInstanceId = caseInstanceId;
    this.activityId = activityId;
  }

  public String getCaseInstanceId()
  {
    return caseInstanceId;
  }

  public void setCaseInstanceId(String caseInstanceId)
  {
    this.caseInstanceId = caseInstanceId;
  }

  public String getActivityId()
  {
    return activityId;
  }

  public void setActivityId(String activityId)
  {
    this.activityId = activityId;
  }
}
