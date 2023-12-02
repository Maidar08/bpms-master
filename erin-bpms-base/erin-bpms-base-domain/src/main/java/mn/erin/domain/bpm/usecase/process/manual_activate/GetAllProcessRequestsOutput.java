package mn.erin.domain.bpm.usecase.process.manual_activate;

import java.util.Collection;

import mn.erin.domain.bpm.model.process.ProcessRequest;

/**
 * @author Zorig
 */
public class GetAllProcessRequestsOutput
{
  private final Collection<ProcessRequest> allProcessRequests;

  public GetAllProcessRequestsOutput(Collection<ProcessRequest> allProcessRequests)
  {
    this.allProcessRequests = allProcessRequests;
  }

  public Collection<ProcessRequest> getAllProcessRequests()
  {
    return allProcessRequests;
  }
}
