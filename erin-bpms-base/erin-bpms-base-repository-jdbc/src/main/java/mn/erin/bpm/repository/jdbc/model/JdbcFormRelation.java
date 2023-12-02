package mn.erin.bpm.repository.jdbc.model;

import java.sql.Clob;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author Lkhagvadorj
 */
@Table("TASK_FORM_RELATION")
public class JdbcFormRelation
{
  @Id
  private String taskDefinitionId;
  private Clob  formValue;
  private String entity;

  public String getTaskDefinitionId()
  {
    return taskDefinitionId;
  }

  public void setTaskDefinitionId(String taskDefinitionId)
  {
    this.taskDefinitionId = taskDefinitionId;
  }

  public Clob getFormValue()
  {
    return formValue;
  }

  public void setFormValue(Clob formValue)
  {
    this.formValue = formValue;
  }

  public String getEntity()
  {
    return entity;
  }

  public void setEntity(String entity)
  {
    this.entity = entity;
  }
}
