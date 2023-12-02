/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.rest.model;

import java.util.List;

/**
 * @author Tamir
 */
public class RestManualActivation
{
  private String executionId;

  private List<RestVariable> variables;
  private List<RestVariable> deletions;

  public RestManualActivation()
  {

  }

  public RestManualActivation(String executionId, List<RestVariable> variables)
  {
    this.executionId = executionId;
    this.variables = variables;
  }

  public String getExecutionId()
  {
    return executionId;
  }

  public void setExecutionId(String executionId)
  {
    this.executionId = executionId;
  }

  public List<RestVariable> getVariables()
  {
    return variables;
  }

  public void setVariables(List<RestVariable> variables)
  {
    this.variables = variables;
  }

  public List<RestVariable> getDeletions()
  {
    return deletions;
  }

  public void setDeletions(List<RestVariable> deletions)
  {
    this.deletions = deletions;
  }
}
