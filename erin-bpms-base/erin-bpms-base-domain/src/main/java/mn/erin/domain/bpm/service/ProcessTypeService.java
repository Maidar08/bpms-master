/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.service;

import java.util.Map;

/**
 * Represents a process type specific services which shall be implemented by external BPM systems
 *
 * @author EBazarragchaa
 */
public interface ProcessTypeService
{
  /**
   * Starts or creates new process instance with a given process type.
   *
   * @param processTypeId the process type including process definition key, not blank
   * @param processRequestId Unique id of process request.
   * @return process instance id or null
   */
  String startProcess(String processTypeId, String processRequestId);

  /**
   * Starts or create new process instance with given process type. This process has not saved in BPM database.
   *
   * @param processTypeId the process type including process definition key, not blank
   * @return process instance id or null
   */
  String startProcess(String processTypeId);

  // processTypeService
  /**
   * Starts or creates new process instance with a given process type with parameters.
   * @param processTypeId the process type including process definition key, not blank
   * @param processRequestId Unique id of process request.
   * @return process instance id or null
   */
  String startProcessWithVariables(String processTypeId, String processRequestId, Map<String, Object> parameters);

  /**
   *
   * @param processTypeId the process type including process definition key, not blank
   * @param variables related to the process
   * @return process instance id or null
   */
  String startProcess(String processTypeId, Map<String, Object> variables);

  /**
   *
   * @param caseInstanceId case instance id
   */
  void terminateProcess(String caseInstanceId);
}
