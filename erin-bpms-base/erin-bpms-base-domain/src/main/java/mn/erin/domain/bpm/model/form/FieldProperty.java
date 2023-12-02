/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.model.form;

import java.io.Serializable;

/**
 * @author Tamir
 */
public class FieldProperty implements Serializable
{
  private static final long serialVersionUID = 2139543427187273702L;

  private String id;
  private String value;

  public FieldProperty()
  {
  }

  public FieldProperty(String id, String value)
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
