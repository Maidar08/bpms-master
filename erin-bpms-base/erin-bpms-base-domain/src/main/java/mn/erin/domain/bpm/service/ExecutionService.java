/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.service;

import java.util.List;

import mn.erin.domain.bpm.model.cases.Execution;
import mn.erin.domain.bpm.model.variable.Variable;

/**
 * @author Tamir
 */
public interface ExecutionService
{
  /**
   * Gets completed case executions by unique case instance id.
   *
   * @param instanceId Uniques case instance id;
   * @return found executions.
   * @throws BpmServiceException when this service is not reachable or usable
   */
  List<Execution> getCompletedByInstanceId(String instanceId) throws BpmServiceException;
  /**
   * Gets enabled case executions by unique case instance id.
   *
   * @param instanceId Uniques case instance id;
   * @return found executions.
   * @throws BpmServiceException when this service is not reachable or usable
   */
  List<Execution> getEnabledByInstanceId(String instanceId) throws BpmServiceException;

  /**
   * Gets active case active executions by unique case instance id.
   *
   * @param instanceId Uniques case instance id;
   * @return found executions.
   * @throws BpmServiceException when this service is not reachable or usable
   */
  List<Execution> getActiveByInstanceId(String instanceId) throws BpmServiceException;

  /**
   * Manual activates case execution with variables.
   *
   * @param executionId Uniques execution id;
   * @throws BpmServiceException when this service is not reachable or usable
   */
  void manualActivate(String executionId) throws BpmServiceException;

  /**
   * Manual activates case execution with variables.
   *
   * @param executionId Uniques execution id;
   * @param variables   variables.
   * @throws BpmServiceException when this service is not reachable or usable
   */
  void manualActivate(String executionId, List<Variable> variables) throws BpmServiceException;

  /**
   * Manual activates case execution with variables, delete variables.
   *
   * @param executionId Uniques execution id;
   * @param variables   variables.
   * @param deletions   delete variables.
   * @throws BpmServiceException when this service is not reachable or usable
   */
  void manualActivate(String executionId, List<Variable> variables, List<Variable> deletions) throws BpmServiceException;
}
