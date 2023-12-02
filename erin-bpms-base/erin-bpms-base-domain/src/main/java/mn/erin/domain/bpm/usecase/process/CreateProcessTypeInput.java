/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.process;

/**
 * @author Tamir
 */
public class CreateProcessTypeInput
{
  private String processTypeId;
  private String definitionKey;

  private String name;
  private String version;

  private String processDefinitionType;

  public CreateProcessTypeInput(String processTypeId, String definitionKey, String name, String version, String processDefinitionType)
  {
    this.processTypeId = processTypeId;
    this.definitionKey = definitionKey;
    this.name = name;
    this.version = version;
    this.processDefinitionType = processDefinitionType;
  }

  public String getProcessTypeId()
  {
    return processTypeId;
  }

  public void setProcessTypeId(String processTypeId)
  {
    this.processTypeId = processTypeId;
  }

  public String getDefinitionKey()
  {
    return definitionKey;
  }

  public void setDefinitionKey(String definitionKey)
  {
    this.definitionKey = definitionKey;
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
}
