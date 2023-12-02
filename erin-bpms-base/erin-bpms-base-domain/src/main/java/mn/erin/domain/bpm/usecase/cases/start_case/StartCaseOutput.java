package mn.erin.domain.bpm.usecase.cases.start_case;

import mn.erin.domain.bpm.model.cases.Case;

/**
 * @author Tamir
 */
public class StartCaseOutput
{
  private Case startedCase;

  public StartCaseOutput(Case startedCase)
  {
    this.startedCase = startedCase;
  }

  public Case getStartedCase()
  {
    return startedCase;
  }

  public void setStartedCase(Case startedCase)
  {
    this.startedCase = startedCase;
  }
}
