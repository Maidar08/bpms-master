package mn.erin.domain.bpm.usecase.form.relation;

import mn.erin.domain.bpm.model.form.TaskFormRelation;

/**
 * @author Tamir
 */
public class GetFormRelationOutput
{
  private TaskFormRelation taskFormRelation;

  public GetFormRelationOutput(TaskFormRelation taskFormRelation)
  {
    this.taskFormRelation = taskFormRelation;
  }

  public TaskFormRelation getTaskFormRelation()
  {
    return taskFormRelation;
  }

  public void setTaskFormRelation(TaskFormRelation taskFormRelation)
  {
    this.taskFormRelation = taskFormRelation;
  }
}
