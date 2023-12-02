/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.model.cases;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

import mn.erin.domain.base.model.Entity;

/**
 * @author Tamir
 */
public class Case implements Entity<Case>
{
  private final CaseInstanceId id;

  private String name;
  private String definitionKey;
  private Map<String, Serializable> variables;

  public Case(CaseInstanceId id, String name)
  {
    this.id = Objects.requireNonNull(id, "Case id is required!");
    this.name = Objects.requireNonNull(name, "Case name is required!");
  }

  public CaseInstanceId getId()
  {
    return id;
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

  @Override
  public boolean sameIdentityAs(Case other)
  {
    return other != null && (this.id.equals(other.id));
  }
}
