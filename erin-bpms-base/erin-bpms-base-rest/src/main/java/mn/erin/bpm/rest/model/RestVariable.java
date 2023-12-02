/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.rest.model;

import java.io.Serializable;

/**
 * @author Tamir
 */
public class RestVariable
{
  private String id;
  private Serializable value;

  private String type;
  private String label;

  private String context;
  private boolean isLocalVariable;

  public RestVariable()
  {

  }

  public RestVariable(String id, Serializable value, String type, String label, String context, boolean isLocalVariable)
  {
    this.id = id;
    this.value = value;

    this.type = type;
    this.label = label;

    this.context = context;
    this.isLocalVariable = isLocalVariable;
  }

  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = id;
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

  public String getLabel()
  {
    return label;
  }

  public void setLabel(String label)
  {
    this.label = label;
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
}
