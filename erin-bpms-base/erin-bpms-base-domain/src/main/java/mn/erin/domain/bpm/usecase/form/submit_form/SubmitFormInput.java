/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.form.submit_form;

import java.util.Map;
import java.util.Objects;

/**
 * @author Tamir
 */
public class SubmitFormInput
{
  private String taskId;
  private String caseInstanceId;
  private Map<String, Object> properties;

  public SubmitFormInput(String taskId, String caseInstanceId, Map<String, Object> properties)
  {
    this.taskId = Objects.requireNonNull(taskId, "Submit form input task is required!");
    this.caseInstanceId = caseInstanceId;
    this.properties = Objects.requireNonNull(properties, "Submit form input properties is required!");
  }

  public String getTaskId()
  {
    return taskId;
  }

  public Map<String, Object> getProperties()
  {
    return properties;
  }

  public String getCaseInstanceId()
  {
    return caseInstanceId;
  }
}
