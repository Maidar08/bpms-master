/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.execution;

import java.util.List;

import mn.erin.domain.bpm.model.cases.Execution;

/**
 * @author Tamir
 */
public class GetEnabledExecutionsOutput
{
  private List<Execution> executions;

  public GetEnabledExecutionsOutput(List<Execution> executions)
  {
    this.executions = executions;
  }

  public List<Execution> getExecutions()
  {
    return executions;
  }

  public void setExecutions(List<Execution> executions)
  {
    this.executions = executions;
  }
}
