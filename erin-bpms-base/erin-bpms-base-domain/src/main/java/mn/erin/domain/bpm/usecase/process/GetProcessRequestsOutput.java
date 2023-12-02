package mn.erin.domain.bpm.usecase.process;

import java.util.Collection;

import mn.erin.domain.bpm.model.process.ProcessRequest;

/**
 * @author Zorig
 */
public class GetProcessRequestsOutput
{
  private final Collection<ProcessRequest> processRequests;

  public GetProcessRequestsOutput(Collection<ProcessRequest> processRequests)
  {
    this.processRequests = processRequests;
  }

  public Collection<ProcessRequest> getProcessRequests()
  {
    return processRequests;
  }
}
