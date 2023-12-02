package mn.erin.domain.bpm.usecase.cases;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.cases.Case;
import mn.erin.domain.bpm.model.cases.CaseInstanceId;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.usecase.cases.get_cases.GetCaseByTaskId;

public class GetCaseByTaskIdTest
{
  private CaseService caseService;
  private GetCaseByTaskId useCase;

  @Before
  public void setUp()
  {
    caseService = Mockito.mock(CaseService.class);
    useCase = new GetCaseByTaskId(caseService);
  }

  @Test(expected = NullPointerException.class)
  public void when_service_is_null()
  {
    new GetCaseByTaskId(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_service_exception() throws BpmServiceException, UseCaseException
  {
    Mockito.when(caseService.findByTaskId("taskId")).thenThrow(BpmServiceException.class);
    useCase.execute("taskId");
  }

  @Test
  public void when_works_correctly() throws BpmServiceException, UseCaseException
  {
    Mockito.when(caseService.findByTaskId("taskId")).thenReturn(new Case(CaseInstanceId.valueOf("123"), "case"));
    Case testCase = useCase.execute("taskId");

    Assert.assertNotNull(testCase.getId());
  }
}
