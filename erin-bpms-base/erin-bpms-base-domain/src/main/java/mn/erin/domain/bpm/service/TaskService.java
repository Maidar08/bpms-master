/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.service;

import java.util.List;

import mn.erin.domain.bpm.model.task.Task;

/**
 * @author Tamir
 */
public interface TaskService
{
  /**
   * Gets active tasks by case instance id.
   *
   * @param caseInstanceId Unique task id.
   * @return Found active tasks with given case instance id.
   */
  List<Task> getActiveByCaseInstanceId(String caseInstanceId);

  /**
   * Gets completed tasks by case instance id.
   *
   * @param caseInstanceId Unique task id.
   * @return Found completed tasks with given case instance id.
   */
  List<Task> getCompletedByCaseInstanceId(String caseInstanceId);

  /**
   *  Gets active task by process id and task definition key.
   *
   * @param processId Unique process id.
   * @param definitionKey Task process definition key.
   * @return Found active tasks with given input.
   */
  List<Task> getActiveTaskByProcessIdAndDefinitionKey(String processId, String definitionKey);

  /**
   *  Get active tasks by process instance id.
   *
   * @param processInstanceId process instance id
   * @return Returns active tasks found by process instance id.
   */
  List<Task> getActiveTaskByProcessInstanceId(String processInstanceId);
}
