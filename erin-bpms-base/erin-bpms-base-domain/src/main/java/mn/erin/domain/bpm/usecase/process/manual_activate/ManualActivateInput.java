/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.process.manual_activate;

import java.util.List;
import java.util.Objects;

import mn.erin.domain.bpm.model.variable.Variable;

/**
 * @author Tamir
 */
public class ManualActivateInput
{
  private String executionId;
  private List<Variable> variables;
  private List<Variable> deletions;

  public ManualActivateInput(String executionId, List<Variable> variables, List<Variable> deletions)
  {
    this.executionId = Objects.requireNonNull(executionId, "Execution id is required");
    this.variables = variables;
    this.deletions = deletions;
  }

  public String getExecutionId()
  {
    return executionId;
  }

  public void setExecutionId(String executionId)
  {
    this.executionId = executionId;
  }

  public List<Variable> getVariables()
  {
    return variables;
  }

  public void setVariables(List<Variable> variables)
  {
    this.variables = variables;
  }

  public List<Variable> getDeletions()
  {
    return deletions;
  }

  public void setDeletions(List<Variable> deletions)
  {
    this.deletions = deletions;
  }
}
