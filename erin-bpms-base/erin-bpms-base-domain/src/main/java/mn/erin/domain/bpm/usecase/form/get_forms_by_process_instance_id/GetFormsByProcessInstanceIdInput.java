/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.form.get_forms_by_process_instance_id;

import java.util.Objects;

/**
 * @author Tamir
 */
public class GetFormsByProcessInstanceIdInput
{
  private final String processInstanceId;

  public GetFormsByProcessInstanceIdInput(String processInstanceId)
  {
    this.processInstanceId = Objects.requireNonNull(processInstanceId, "Input process instance id is required!");
  }

  public String getProcessInstanceId()
  {
    return processInstanceId;
  }
}
