package mn.erin.domain.bpm.usecase.form.case_variable;

import java.util.Objects;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmMessagesConstants;
import mn.erin.domain.bpm.service.CaseService;

/**
 * @author Tamir
 */
public class SetCaseVariableById extends AbstractUseCase<SetCaseVariableByIdInput, Void>
{
  private final CaseService caseService;

  public SetCaseVariableById(CaseService caseService)
  {
    this.caseService = Objects.requireNonNull(caseService, "Case service is required!");
  }

  @Override
  public Void execute(SetCaseVariableByIdInput input) throws UseCaseException
  {
    if (null == input)
    {
      throw new UseCaseException(BpmMessagesConstants.SET_VARIABLE_INPUT_VALUE_CODE, BpmMessagesConstants.SET_VARIABLE_INPUT_VALUE_MESSAGE);
    }

    String caseInstanceId = input.getCaseInstanceId();
    String variableId = input.getVariableId();
    Object variableValue = input.getVariableValue();

    try
    {
      caseService.setCaseVariableById(caseInstanceId, variableId, variableValue);
      return null;
    }
    catch (Exception e)
    {
      String message = String.format(BpmMessagesConstants.COULD_NOT_SET_VARIABLE_VALUE_MESSAGE, e.getMessage());
      throw new UseCaseException(BpmMessagesConstants.COULD_NOT_SET_VARIABLE_VALUE_CODE, message);
    }
  }
}
