package mn.erin.domain.bpm.usecase.process;

import mn.erin.domain.bpm.model.process.Process;

/**
 * @author Zorig
 */
public class GetProcessOutput
{
  private final Process returnedProcess;

  public GetProcessOutput(Process returnedProcess)
  {
    this.returnedProcess = returnedProcess;
  }

  public Process getReturnedProcess()
  {
    return returnedProcess;
  }
}
