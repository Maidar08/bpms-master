package mn.erin.domain.bpm.usecase.cases.startcases;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.cases.Case;
import mn.erin.domain.bpm.model.cases.CaseInstanceId;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.usecase.cases.start_case.StartCase;
import mn.erin.domain.bpm.usecase.cases.start_case.StartCaseInput;
import mn.erin.domain.bpm.usecase.cases.start_case.StartCaseOutput;

public class StartCaseTest
{
  private AuthenticationService authenticationService;
  private AuthorizationService authorizationService;
  private CaseService caseService;
  private StartCaseInput input;
  private StartCase useCase;

  @Before
  public void setUp()
  {
    caseService = Mockito.mock(CaseService.class);
    authenticationService = Mockito.mock(AuthenticationService.class);
    authorizationService = Mockito.mock(AuthorizationService.class);
    input = new StartCaseInput("123");
    useCase = new StartCase(authenticationService, authorizationService, caseService);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_is_null() throws UseCaseException
  {
    Mockito.when(authenticationService.getCurrentUserId()).thenReturn("branchSpecialist108");
    useCase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_service_exception() throws BpmServiceException, UseCaseException
  {
    Mockito.when(caseService.startCase("123")).thenThrow(BpmServiceException.class);
    Mockito.when(authenticationService.getCurrentUserId()).thenReturn("branchSpecialist108");

    useCase.execute(input);
  }

  @Test
  public void when_works_correctly() throws BpmServiceException, UseCaseException
  {
    Mockito.when(caseService.startCase("123")).thenReturn(new Case(CaseInstanceId.valueOf("123"), "testCase"));
    Mockito.when(authenticationService.getCurrentUserId()).thenReturn("branchSpecialist108");

    StartCaseOutput output = useCase.execute(input);

    Case startedCase = output.getStartedCase();

    Assert.assertEquals(CaseInstanceId.valueOf("123"), startedCase.getId());
  }
}


