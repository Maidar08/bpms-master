package mn.erin.domain.bpm.usecase.directOnline;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;

import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.model.process.ProcessRequestId;
import mn.erin.domain.bpm.model.process.ProcessTypeId;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.usecase.direct_online.GetLatestRequestByCif;

import static mn.erin.domain.bpm.model.process.ProcessRequestState.STARTED;

/**
 * @author Lkhagvadorj.A
 **/

public class GetLatestRequestByCifTest
{
  private GetLatestRequestByCif useCase;
  private ProcessRequestRepository processRequestRepository;
  private BpmsRepositoryRegistry bpmsRepositoryRegistry;
  private Environment environment;

  @Before
  public void setUp()
  {
    processRequestRepository = Mockito.mock(ProcessRequestRepository.class);
    useCase = new GetLatestRequestByCif(processRequestRepository, bpmsRepositoryRegistry, environment);
  }

  @Test(expected = UseCaseException.class)
  public void when_cif_blank() throws UseCaseException
  {
    useCase.execute(null);
  }


  @Test(expected = UseCaseException.class)
  public void when_BpmRepositoryException_throw() throws BpmRepositoryException, UseCaseException
  {
    Mockito.when(processRequestRepository.getDirectOnlineProcessRequests("123", LocalDateTime.now(), LocalDateTime.now(), "EB71"))
        .thenThrow(BpmRepositoryException.class);
    useCase.execute(null);
  }

  private Collection<ProcessRequest> getMockProcessRequests()
  {
    Collection<ProcessRequest> processRequests = new ArrayList<>();
    LocalDateTime date1 = LocalDateTime.of(2021, 01, 10, 10, 10);
    LocalDateTime date2 = LocalDateTime.of(2021, 02, 10, 10, 10);
    ProcessRequest request1 = new ProcessRequest(ProcessRequestId.valueOf("id1"), ProcessTypeId.valueOf("id1"),
        GroupId.valueOf("id1"), "id1", date1, STARTED,
        Collections.EMPTY_MAP);

    ProcessRequest request2 = new ProcessRequest(ProcessRequestId.valueOf("id2"), ProcessTypeId.valueOf("id2"),
        GroupId.valueOf("id2"), "id2", date1, STARTED,
        Collections.EMPTY_MAP);

    processRequests.add(request1);
    processRequests.add(request2);

    return processRequests;
  }
}
