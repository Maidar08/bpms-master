/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.service;

import java.util.List;
import java.util.Map;

import mn.erin.domain.bpm.model.cases.Case;
import mn.erin.domain.bpm.model.variable.Variable;

/**
 * @author Tamir
 */
public interface CaseService
{
  /**
   * Starts case by definition key.
   * @param caseDefinitionKey given definition key.
   *
   * @return created case.
   */
  Case startCase(String caseDefinitionKey) throws BpmServiceException;
  /**
   * Completes by activity id.
   *
   * @param instanceId given instanceId id.
   * @param activityId given activity id.
   */
  void completeByActivityId(String instanceId, String activityId) throws BpmServiceException;

  /**
   * Get variables by execution id.
   *
   * @param instanceId Unique instance id.
   * @return found variables.
   * @throws BpmServiceException when this service is not reachable or usable
   */
  List<Variable> getVariables(String instanceId) throws BpmServiceException;

  /**
   * Get variable by execution id and variable name.
   * @param instanceId Unique instance id.
   * @param name variable name.
   * @return found variable
   */
  Object getVariableById(String instanceId, String name) throws BpmServiceException;

  /**
   * Gets all cases of current user.
   * Depending on current user and user groups.
   *
   * @return found {@link Case} list.
   */
  List<Case> getUserCases();

  /**
   * Gets case by task id.
   *
   * @param taskId Unique task id.
   * @return {@link Case}.
   */
  Case findByTaskId(String taskId) throws BpmServiceException;

  /**
   * Delete variables by instance id and variable name
   *
   * @param instanceId    variable's instance id and name
   * @param documentNames variable's  name
   * @throws BpmServiceException if failed to delete variable
   */
  void deleteVariablesByInstanceIdAndVariableName(String instanceId, List<String> documentNames) throws BpmServiceException;

  /**
   * Set variables by new value
   *
   * @param caseInstanceId instance id
   * @param variables      vairable's name and value map
   */
  void setCaseVariables(String caseInstanceId, Map<String, Object> variables);

  /**
   * Sets case variable by id;
   * @param caseInstanceId Unique case instance id.
   * @param variableId Unique variable id.
   * @param variableValue value.
   */
  void setCaseVariableById(String caseInstanceId, String variableId, Object variableValue);

  boolean terminateCase(String caseExecutionId);

  boolean closeCases(String caseExecutionId);

  void disable(String id);
}


