package mn.erin.domain.bpm.usecase.cases.startcases;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.util.Assert;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.cases.Case;
import mn.erin.domain.bpm.model.cases.CaseInstanceId;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.usecase.cases.get_cases.GetCases;
import mn.erin.domain.bpm.usecase.cases.get_cases.GetCasesOutput;

public class GetCasesTest
{
  private CaseService caseService;
  private GetCases useCase;

  @Before
  public void setUp()
  {
    caseService = Mockito.mock(CaseService.class);
    useCase = new GetCases(caseService);
  }

  @Test(expected = NullPointerException.class)
  public void when_service_is_null()
  {
    new GetCases(null);
  }

  //Testing use case exception throw
  @Test(expected = UseCaseException.class)
  public void when_throws_service_exception() throws BpmServiceException, UseCaseException
  {
    Mockito.when(caseService.getUserCases()).thenReturn(null);
    useCase.execute(null);
  }

  @Test
  public void when_works_correctly() throws UseCaseException
  {
    Mockito.when(caseService.getUserCases()).thenReturn(generateList());
    GetCasesOutput output = useCase.execute(null);

    Assert.notNull(output.getCases().get(0));
  }

  private static List<Case> generateList()
  {
    List<Case> caseList = new ArrayList<>();
    Case caseTest = new Case(CaseInstanceId.valueOf("123"), "123");

    caseList.add(caseTest);

    return caseList;
  }
}