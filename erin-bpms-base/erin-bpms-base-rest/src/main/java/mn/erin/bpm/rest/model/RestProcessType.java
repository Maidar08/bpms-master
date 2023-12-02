/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.rest.model;

/**
 * @author Tamir
 */
public class RestProcessType
{
  private String id;
  private String definitionKey;

  private String name;
  private String version;

  private String processDefinitionType;
 private String processTypeCategory;
  public RestProcessType()
  {

  }

  public RestProcessType(String id, String definitionKey, String name, String version, String processDefinitionType, String processTypeCategory)
  {
    this.id = id;
    this.definitionKey = definitionKey;
    this.name = name;
    this.version = version;
    this.processDefinitionType = processDefinitionType;
    this.processTypeCategory = processTypeCategory;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public void setDefinitionKey(String definitionKey)
  {
    this.definitionKey = definitionKey;
  }

  public String getId()
  {
    return id;
  }

  public String getDefinitionKey()
  {
    return definitionKey;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getVersion()
  {
    return version;
  }

  public void setVersion(String version)
  {
    this.version = version;
  }

  public String getProcessDefinitionType()
  {
    return processDefinitionType;
  }

  public void setProcessDefinitionType(String processDefinitionType)
  {
    this.processDefinitionType = processDefinitionType;
  }
  public String getProcessTypeCategory() {
    return processTypeCategory;
  }

  public void setProcessTypeCategory(String processTypeCategory) {
    this.processTypeCategory = processTypeCategory;
  }

}
