package mn.erin.bpm.repository.jdbc.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author Zorig
 */
@Table("PROCESS")
public class JdbcProcess
{
  @Id
  String processInstanceId;
  LocalDateTime startedDate;
  LocalDateTime finishedDate;
  String createdUser;
  String processTypeCategory;



  public String getProcessInstanceId()
  {
    return processInstanceId;
  }

  public void setProcessInstanceId(String processInstanceId)
  {
    this.processInstanceId = processInstanceId;
  }

  public LocalDateTime getStartedDate()
  {
    return startedDate;
  }

  public void setStartedDate(LocalDateTime startedDate)
  {
    this.startedDate = startedDate;
  }

  public LocalDateTime getFinishedDate()
  {
    return finishedDate;
  }

  public void setFinishedDate(LocalDateTime finishedDate)
  {
    this.finishedDate = finishedDate;
  }

  public String getCreatedUser() { return createdUser; }

  public void setCreatedUser(String createdUser) { this.createdUser = createdUser; }

  public String getProcessTypeCategory() { return processTypeCategory; }

  public void setProcessTypeCategory(String processTypeCategory) { this.processTypeCategory = processTypeCategory; }

}
