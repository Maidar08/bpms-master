/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.service;

import java.util.List;
import java.util.Map;

import mn.erin.domain.bpm.model.form.TaskForm;

/**
 * @author Tamir
 */
public interface TaskFormService
{
  /**
   * Submits task form and returns result of submitted.
   *
   * @param taskId     Unique id of task.
   * @param properties form field properties.
   * @throws BpmServiceException if task id or properties empty.
   */
  void submitForm(String taskId, Map<String, Object> properties) throws BpmServiceException;


  /**
   * Gets task form by form id.
   *
   * @param caseInstanceId Unique id of case instance.
   * @param taskId Unique id of task.
   * @return {@link TaskForm}.
   * @throws BpmServiceException if task id empty.
   */
  TaskForm getFormByTaskId(String caseInstanceId, String taskId) throws BpmServiceException;
  /**
   * Gets task form by task id.
   *
   * @param taskId Unique id of task.
   * @return {@link TaskForm}.
   * @throws BpmServiceException if task id empty.
   */
  TaskForm getFormByTaskIdBeforeCreationOfTask(String taskId) throws BpmServiceException;

  /**
   * Gets task form by form id.
   *
   * @param definitionId Unique definition id of process.
   * @return found {@link TaskForm}s.
   * @throws BpmServiceException if definition id empty.
   */
  List<TaskForm> getFormsByDefinitionId(String definitionId) throws BpmServiceException;

  /**
   * Gets task form by form id.
   *
   * @param definitionKey Unique definition id of process.
   * @return found {@link TaskForm}s.
   * @throws BpmServiceException if definition key empty.
   */
  List<TaskForm> getFormsByDefinitionKey(String definitionKey) throws BpmServiceException;

  /**
   * Gets task form by case instance id.
   *
   * @param processInstanceId Unique process instance id.
   * @return found {@link TaskForm}s.
   * @throws BpmServiceException if process instance id empty.
   */
  List<TaskForm> getFormsByProcessInstanceId(String processInstanceId) throws BpmServiceException;

  /**
   * Gets task form by case instance id.
   *
   * @param caseInstanceId Uniques case instance id.
   * @return found {@link TaskForm}s.
   * @throws BpmServiceException if case instance id empty.
   */
  List<TaskForm> getFormsByCaseInstanceId(String caseInstanceId) throws BpmServiceException;

  TaskForm getFormByTaskId(String taskId);
}
