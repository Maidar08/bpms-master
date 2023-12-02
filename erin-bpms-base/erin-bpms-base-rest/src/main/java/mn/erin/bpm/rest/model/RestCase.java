/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.rest.model;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Tamir
 */
public class RestCase
{
  private String id;
  private String name;
  private String definitionKey;
  private Map<String, Serializable> variables;

  public RestCase(String id, String name, String definitionKey)
  {
    this.id = id;
    this.name = name;
    this.definitionKey = definitionKey;
  }

  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getDefinitionKey()
  {
    return definitionKey;
  }

  public void setDefinitionKey(String definitionKey)
  {
    this.definitionKey = definitionKey;
  }

  public Map<String, Serializable> getVariables()
  {
    return variables;
  }

  public void setVariables(Map<String, Serializable> variables)
  {
    this.variables = variables;
  }
}
