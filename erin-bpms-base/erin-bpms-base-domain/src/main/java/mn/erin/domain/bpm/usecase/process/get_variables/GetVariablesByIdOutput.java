/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.process.get_variables;

import java.util.List;

import mn.erin.domain.bpm.model.variable.Variable;

/**
 * @author Tamir
 */
public class GetVariablesByIdOutput
{
  private List<Variable> variables;

  public GetVariablesByIdOutput(List<Variable> variables)
  {
    this.variables = variables;
  }

  public List<Variable> getVariables()
  {
    return variables;
  }

  public void setVariables(List<Variable> variables)
  {
    this.variables = variables;
  }
}
