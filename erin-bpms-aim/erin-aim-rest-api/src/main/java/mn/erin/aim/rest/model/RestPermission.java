package mn.erin.aim.rest.model;

import java.util.Collections;
import java.util.Map;

public class RestPermission
{
  private final String id;
  private Map<String, Object> properties;

  public RestPermission(String id)
  {
    this(id, Collections.emptyMap());
  }

  public RestPermission(String id, Map<String, Object> properties)
  {
    this.id = id;
    this.properties = properties;
  }

  public String getId()
  {
    return id;
  }

  public Map<String, Object> getProperties()
  {
    return properties;
  }

  public void setProperties(Map<String, Object> properties)
  {
    this.properties = properties;
  }
}
