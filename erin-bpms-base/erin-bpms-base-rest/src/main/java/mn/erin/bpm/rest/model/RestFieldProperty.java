package mn.erin.bpm.rest.model;

/**
 * @author Tamir
 */
public class RestFieldProperty
{
  private String id;
  private String value;

  public RestFieldProperty()
  {
  }

  public RestFieldProperty(String id, String value)
  {
    this.id = id;
    this.value = value;
  }

  public String getId()
  {
    return id;
  }

  public String getValue()
  {
    return value;
  }
}
