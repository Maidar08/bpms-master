package mn.erin.domain.bpm.model.form;

import java.util.Collection;
import java.util.Map;

import mn.erin.domain.base.model.Entity;

/**
 * @author Tamir
 */
public class TaskFormRelation implements Entity<TaskFormRelation>
{
  private TaskDefinitionId taskDefinitionId;
  private Map<String, Collection<FormFieldRelation>> fieldRelations;

  public TaskFormRelation(TaskDefinitionId taskDefinitionId,
      Map<String, Collection<FormFieldRelation>> fieldRelations)
  {
    this.taskDefinitionId = taskDefinitionId;
    this.fieldRelations = fieldRelations;
  }

  public TaskDefinitionId getTaskDefinitionId()
  {
    return taskDefinitionId;
  }

  public void setTaskDefinitionId(TaskDefinitionId taskDefinitionId)
  {
    this.taskDefinitionId = taskDefinitionId;
  }

  public Map<String, Collection<FormFieldRelation>> getFieldRelations()
  {
    return fieldRelations;
  }

  public void setFieldRelations(
      Map<String, Collection<FormFieldRelation>> fieldRelations)
  {
    this.fieldRelations = fieldRelations;
  }

  @Override
  public boolean sameIdentityAs(TaskFormRelation other)
  {
    return null != other.taskDefinitionId && this.taskDefinitionId.equals(other.getTaskDefinitionId());
  }
}
