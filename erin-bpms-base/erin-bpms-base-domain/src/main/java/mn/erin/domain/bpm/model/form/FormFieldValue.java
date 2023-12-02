/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.model.form;

import java.util.Map;

/**
 * @author Tamir
 */
public class FormFieldValue
{
  private Object defaultValue;
  private Map<String, Object> valueInfo;

  public FormFieldValue()
  {

  }

  public FormFieldValue(Object defaultValue)
  {
    this.defaultValue = defaultValue;
  }

  public FormFieldValue(Object defaultValue, Map<String, Object> valueInfo)
  {
    this.defaultValue = defaultValue;
    this.valueInfo = valueInfo;
  }

  public Object getDefaultValue()
  {
    return defaultValue;
  }

  public void setDefaultValue(Object defaultValue)
  {
    this.defaultValue = defaultValue;
  }

  public Map<String, Object> getValueInfo()
  {
    return valueInfo;
  }

  public void setValueInfo(Map<String, Object> valueInfo)
  {
    this.valueInfo = valueInfo;
  }
}

