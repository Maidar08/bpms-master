package mn.erin.domain.bpm.usecase.process;

import java.util.Collection;

import mn.erin.domain.bpm.model.process.Process;

/**
 * @author Zorig
 */
public class GetAllProcessesOutput
{
  private final Collection<Process> returnedProcesses;

  public GetAllProcessesOutput(Collection<Process> returnedProcesses)
  {
    this.returnedProcesses = returnedProcesses;
  }

  public Collection<Process> getReturnedProcesses()
  {
    return returnedProcesses;
  }
}
