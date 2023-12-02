/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.form.common;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import mn.erin.domain.bpm.model.form.TaskForm;

/**
 * @author Tamir
 */
public class TaskListOutput
{
  private List<TaskForm> taskFormList;

  public TaskListOutput(List<TaskForm> taskFormList)
  {
    this.taskFormList = Objects.requireNonNull(taskFormList, "Output task list cannot be null!");
  }

  public List<TaskForm> getTaskFormList()
  {
    return Collections.unmodifiableList(taskFormList);
  }
}
