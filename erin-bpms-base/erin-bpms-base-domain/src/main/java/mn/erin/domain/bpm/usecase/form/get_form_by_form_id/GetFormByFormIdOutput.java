/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.form.get_form_by_form_id;

import java.util.Objects;

import mn.erin.domain.bpm.model.form.TaskForm;

/**
 * @author Tamir
 */
public class GetFormByFormIdOutput
{
  private TaskForm taskForm;

  public GetFormByFormIdOutput(TaskForm taskForm)
  {
    this.taskForm = Objects.requireNonNull(taskForm, "Output task form cannot be null!");
  }

  public TaskForm getTaskForm()
  {
    return taskForm;
  }
}
