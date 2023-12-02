/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.model.process;

import java.util.Objects;

import mn.erin.domain.base.model.Entity;

/**
 * @author EBazarragchaa
 */
public class ProcessType implements Entity<ProcessType>
{
  private final ProcessTypeId id;
  private final String definitionKey;
  private String version;
  private String name;
  private final ProcessDefinitionType processDefinitionType;
  private String processTypeCategory;

  public ProcessType(ProcessTypeId id, String definitionKey, ProcessDefinitionType processDefinitionType)
  {
    this.id = Objects.requireNonNull(id, "Process type id is required!");
    this.definitionKey = Objects.requireNonNull(definitionKey, "Process definition key is required!");
    this.processDefinitionType = Objects.requireNonNull(processDefinitionType, "Process definition type id is required!");
  }

  public ProcessTypeId getId()
  {
    return id;
  }

  public ProcessDefinitionType getProcessDefinitionType()
  {
    return processDefinitionType;
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

  public String getProcessTypeCategory() {
    return processTypeCategory;
  }

  public void setProcessTypeCategory(String processTypeCategory) {
    this.processTypeCategory = processTypeCategory;
  }
  @Override
  public boolean sameIdentityAs(ProcessType other)
  {
    return other != null && (this.id.equals(other.id));
  }
}
