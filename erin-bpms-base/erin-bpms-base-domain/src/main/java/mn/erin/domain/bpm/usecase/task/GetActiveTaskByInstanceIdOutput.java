/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.task;

import java.util.List;

import mn.erin.domain.bpm.model.task.Task;

/**
 * @author Tamir
 */
public class GetActiveTaskByInstanceIdOutput
{
  private List<Task> activeTasks;

  public GetActiveTaskByInstanceIdOutput(List<Task> activeTasks)
  {
    this.activeTasks = activeTasks;

  }

  public List<Task> getActiveTasks()
  {
    return activeTasks;
  }
}
