package mn.erin.domain.bpm.usecase.execution;

import java.util.List;

import mn.erin.domain.bpm.model.cases.Execution;

/**
 * @author Lkhagvadorj.A
 **/

public class FilterExecutionByRoleAndTypeInput
{
  private String role;
  private String type;
  private List<Execution> executions;

  public FilterExecutionByRoleAndTypeInput(String role, String type, List<Execution> executions)
  {
    this.role = role;
    this.type = type;
    this.executions = executions;
  }

  public String getRole()
  {
    return role;
  }

  public void setRole(String role)
  {
    this.role = role;
  }

  public String getType()
  {
    return type;
  }

  public void setType(String type)
  {
    this.type = type;
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
