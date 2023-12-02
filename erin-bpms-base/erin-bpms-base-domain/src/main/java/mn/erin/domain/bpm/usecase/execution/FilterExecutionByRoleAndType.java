package mn.erin.domain.bpm.usecase.execution;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.cases.Execution;

import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;

/**
 * @author Lkhagvadorj.A
 **/

public class FilterExecutionByRoleAndType extends AbstractUseCase<FilterExecutionByRoleAndTypeInput, List<Execution>>
{
  @Override
  public List<Execution> execute(FilterExecutionByRoleAndTypeInput input) throws UseCaseException
  {
    if (null == input)
    {
      throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
    }
    List<Execution> executions = input.getExecutions();
    executions.removeIf(x -> checkExecution(input.getRole(), input.getType(), x));
    return executions;
  }

  private boolean checkExecution(String role, String type, Execution execution)
  {
    String validation = role + "_" + type;
    String activityDescription = execution.getActivityDescription();
    if (StringUtils.isBlank(activityDescription))
    {
      return false;
    }
    String[] validations = activityDescription.split(",");

    for (String val : validations)
    {
      if (val.equals(validation))
      {
        return false;
      }
    }

    return true;
  }
}
