package mn.erin.domain.bpm.usecase.form.case_variable;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.usecase.form.submit_form.SubmitFormInput;

/**
 * @author Lkhagvadorj
 */
public class SetCaseVariables extends AbstractUseCase<SubmitFormInput, Boolean>
{
  private final CaseService caseService;

  public SetCaseVariables(CaseService caseService)
  {
    this.caseService = caseService;
  }

  @Override
  public Boolean execute(SubmitFormInput input) throws UseCaseException
  {
    caseService.setCaseVariables(input.getCaseInstanceId(), input.getProperties());

    return true;
  }
}
