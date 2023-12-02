package mn.erin.domain.bpm.model.form;

import mn.erin.domain.base.model.EntityId;

/**
 * @author Tamir
 */
public class TaskDefinitionId extends EntityId
{
  public TaskDefinitionId(String id)
  {
    super(id);
  }

  public static TaskDefinitionId valueOf(String id)
  {
    return new TaskDefinitionId(id);
  }
}
