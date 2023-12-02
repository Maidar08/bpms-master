package mn.erin.bpm.rest.model;

import java.util.Collection;

public class RestNotes
{
  private String instanceId;
  private Collection<RestNote> notes;

  public RestNotes(String instanceId, Collection<RestNote> notes)
  {
    this.instanceId = instanceId;
    this.notes = notes;
  }

  public String getInstanceId()
  {
    return instanceId;
  }

  public void setInstanceId(String instanceId)
  {
    this.instanceId = instanceId;
  }

  public Collection<RestNote> getNotes()
  {
    return notes;
  }

  public void setNotes(Collection<RestNote> notes)
  {
    this.notes = notes;
  }
}
