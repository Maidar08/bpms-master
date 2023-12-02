package mn.erin.domain.bpm.usecase.directOnline;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.usecase.direct_online.Delete14daysPassedRequests;

public class Delete14daysPassedRequestsTest
{
  public static final String PRODUCT = "EB71";
  public static final String CHANNEL = "internet-bank";
  public static final String PROCESS_TYPE_ID = "onlineSalary";

  private BpmsRepositoryRegistry bpmsRepositoryRegistry;
  private CaseService caseService;
  private Environment environment;
  private Delete14daysPassedRequests useCase;


  @Before
  public void setUp()
  {
    bpmsRepositoryRegistry = Mockito.mock(BpmsRepositoryRegistry.class);
    caseService = Mockito.mock(CaseService.class);
    useCase = new Delete14daysPassedRequests(bpmsRepositoryRegistry, caseService, environment);
  }

  @Test(expected = NullPointerException.class)
  public void when_process_repository_is_missing()
  {
    new Delete14daysPassedRequests(null, caseService, environment);
  }

  @Test(expected = NullPointerException.class)
  public void when_case_service_is_missing()
  {
    new Delete14daysPassedRequests(bpmsRepositoryRegistry, null, environment);
  }

  @Ignore
  @Test
  public void when_throws_repository_exception() throws UseCaseException
  {
    //    Collection<ProcessRequest> mockCollection = new ArrayList<>();
    //    mockCollection.add(new ProcessRequest(ProcessRequestId.valueOf("123"), null, null, null, null, null, null));
    //    Mockito.when(processRequestRepository.get14daysPassedRequestsAndDelete(ONLINE_SALARY_PROCESS_TYPE, Mockito.any(),
    //        Mockito.any(), PRODUCT, PROCESS_TYPE_ID)).thenReturn(mockCollection);
    Assert.assertNull(useCase.execute("input"));
  }
}
