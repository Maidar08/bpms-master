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
public class FieldValidation implements Serializable
{
  private static final long serialVersionUID = 7135914313277038178L;

  private String name;
  private String configuration;

  public FieldValidation()
  {
  }

  public FieldValidation(String name, String configuration)
  {
    this.name = name;
    this.configuration = configuration;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getConfiguration()
  {
    return configuration;
  }

  public void setConfiguration(String configuration)
  {
    this.configuration = configuration;
  }
}
