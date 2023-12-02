package mn.erin.domain.bpm.usecase.process;

import java.time.LocalDateTime;

/**
 * @author Zorig
 */
public class UpdateProcessFinishedDateInput
{
  private final String processInstanceId;
  private final LocalDateTime finishedDate;

  public UpdateProcessFinishedDateInput(String processInstanceId, LocalDateTime finishedDate)
  {
    this.processInstanceId = processInstanceId;
    this.finishedDate = finishedDate;
  }

  public String getProcessInstanceId()
  {
    return processInstanceId;
  }

  public LocalDateTime getFinishedDate()
  {
    return finishedDate;
  }
}
