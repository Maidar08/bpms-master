/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.model.variable;

import java.io.Serializable;

import mn.erin.domain.base.model.Entity;

/**
 * @author Tamir
 */
public class Variable implements Entity<Variable>
{
  private VariableId id;
  private Serializable value;

  private String label;
  private String type;
  private String context;
  private boolean isLocalVariable;

  public Variable(VariableId id)
  {
    this.id = id;
  }

  public Variable(VariableId id, Serializable value)
  {
    this.id = id;
    this.value = value;
  }

  public Variable(VariableId id, String type, String label, String context, boolean isLocalVariable)
  {
    this.id = id;
    this.type = type;
    this.label = label;
    this.context = context;
    this.isLocalVariable = isLocalVariable;
  }

  public Variable(VariableId id, Serializable value, String type, String label, String context, boolean isLocalVariable)
  {
    this.id = id;
    this.value = value;

    this.type = type;
    this.label = label;

    this.context = context;
    this.isLocalVariable = isLocalVariable;
  }

  public VariableId getId()
  {
    return id;
  }

  public String getLabel()
  {
    return label;
  }

  public void setLabel(String label)
  {
    this.label = label;
  }

  public String getType()
  {
    return type;
  }

  public void setType(String type)
  {
    this.type = type;
  }

  public Serializable getValue()
  {
    return value;
  }

  public void setValue(Serializable value)
  {
    this.value = value;
  }

  public boolean isLocalVariable()
  {
    return isLocalVariable;
  }

  public void setLocalVariable(boolean localVariable)
  {
    this.isLocalVariable = localVariable;
  }

  public String getContext()
  {
    return context;
  }

  public void setContext(String context)
  {
    this.context = context;
  }

  @Override
  public boolean sameIdentityAs(Variable other)
  {
    return other != null && other.id.sameValueAs(this.id);
  }
}
