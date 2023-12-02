package mn.erin.domain.bpm.usecase.cases.start_case;

import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.BpmModulePermission;
import mn.erin.domain.bpm.model.cases.Case;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CaseService;

import static mn.erin.domain.bpm.BpmMessagesConstants.START_CASE_INPUT_ERROR_CODE;

/**
 * @author Tamir
 */
public class StartCase extends AuthorizedUseCase<StartCaseInput, StartCaseOutput>
{
  private static final Permission permission = new BpmModulePermission("StartCase");

  private final CaseService caseService;

  public StartCase(AuthenticationService authenticationService, AuthorizationService authorizationService,
      CaseService caseService)
  {
    super(authenticationService, authorizationService);
    this.caseService = caseService;
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected StartCaseOutput executeImpl(StartCaseInput input) throws UseCaseException
  {
    if (null == input)
    {
      throw new UseCaseException(START_CASE_INPUT_ERROR_CODE, "Start case input is required!");
    }

    String definitionKey = input.getDefinitionKey();

    try
    {
      Case startedCase = caseService.startCase(definitionKey);

      return new StartCaseOutput(startedCase);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
  }
}
