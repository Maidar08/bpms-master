package mn.erin.domain.bpm.usecase.process;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

import mn.erin.domain.bpm.model.process.ParameterEntityType;

/**
 * @author Zorig
 */
public class CreateProcessInput
{
  private final String processInstanceId;
  private final LocalDateTime startedDate;
  private LocalDateTime finishedDate;
  private String createdUser;
  private String processTypeCategory;
  private final Map<ParameterEntityType, Map<String, Serializable>> parameters;

  public CreateProcessInput(String processInstanceId, LocalDateTime startedDate,
       Map<ParameterEntityType, Map<String, Serializable>> parameters)
  {
    this.processInstanceId = processInstanceId;
    this.startedDate = startedDate;
    this.parameters = parameters;
  }

  public CreateProcessInput(String processInstanceId, LocalDateTime startedDate, String createdUser,
      String processTypeCategory, Map<ParameterEntityType, Map<String, Serializable>> parameters)
  {
    this.processInstanceId = processInstanceId;
    this.startedDate = startedDate;
    this.createdUser = createdUser;
    this.processTypeCategory = processTypeCategory;
    this.parameters = parameters;
  }

  public String getProcessInstanceId()
  {
    return processInstanceId;
  }

  public LocalDateTime getStartedDate()
  {
    return startedDate;
  }

  public LocalDateTime getFinishedDate()
  {
    return finishedDate;
  }

  public void setFinishedDate(LocalDateTime finishedDate)
  {
    this.finishedDate = finishedDate;
  }

  public Map<ParameterEntityType, Map<String, Serializable>> getParameters()
  {
    return parameters;
  }

  public String getCreatedUser() { return createdUser; }

  public String getProcessTypeCategory() { return processTypeCategory; }

  public void setProcessTypeCategory(String processTypeCategory) { this.processTypeCategory = processTypeCategory; }
}
