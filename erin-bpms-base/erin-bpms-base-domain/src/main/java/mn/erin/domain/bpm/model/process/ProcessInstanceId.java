package mn.erin.domain.bpm.model.process;

import mn.erin.domain.base.model.EntityId;

/**
 * @author Zorig
 */
public class ProcessInstanceId extends EntityId
{
  public ProcessInstanceId(String id)
  {
    super(id);
  }

  public static ProcessInstanceId valueOf(String id)
  {
    return new ProcessInstanceId(id);
  }
}
