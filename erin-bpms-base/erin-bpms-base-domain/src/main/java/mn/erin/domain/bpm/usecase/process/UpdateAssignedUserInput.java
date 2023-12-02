package mn.erin.domain.bpm.usecase.process;

/**
 * @author Zorig
 */
public class UpdateAssignedUserInput
{
  private final String processRequestId;
  private final String assignedUser;

  public UpdateAssignedUserInput(String processRequestId, String assignedUser)
  {
    this.processRequestId = processRequestId;
    this.assignedUser = assignedUser;
  }

  public String getProcessRequestId()
  {
    return processRequestId;
  }

  public String getAssignedUser()
  {
    return assignedUser;
  }
}
