/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.form.get_form_by_task_id;

import java.util.Objects;

/**
 * @author Tamir
 */
public class GetFormByTaskIdInput
{
  private final String caseInstanceId;
  private final String taskId;

  public GetFormByTaskIdInput(String caseInstanceId, String taskId)
  {
    this.caseInstanceId = Objects.requireNonNull(caseInstanceId, "Case instance id is required!");
    this.taskId = Objects.requireNonNull(taskId, "Input task id is required!");
  }

  public String getTaskId()
  {
    return taskId;
  }

  public String getCaseInstanceId()
  {
    return caseInstanceId;
  }
}
