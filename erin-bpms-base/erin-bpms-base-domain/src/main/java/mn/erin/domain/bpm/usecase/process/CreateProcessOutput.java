package mn.erin.domain.bpm.usecase.process;

import mn.erin.domain.bpm.model.process.Process;

/**
 * @author Zorig
 */
public class CreateProcessOutput
{
  private final Process process;

  public CreateProcessOutput(Process process)
  {
    this.process = process;
  }

  public Process getProcess()
  {
    return process;
  }
}
