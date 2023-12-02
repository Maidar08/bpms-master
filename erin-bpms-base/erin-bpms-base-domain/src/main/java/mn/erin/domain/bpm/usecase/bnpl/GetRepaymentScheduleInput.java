package mn.erin.domain.bpm.usecase.bnpl;

import mn.erin.domain.base.usecase.UseCaseException;

public class GetRepaymentScheduleInput
{
  private final String acid;
  private final String project;

  public GetRepaymentScheduleInput(String acid, String project) throws UseCaseException
  {
    this.acid = acid;
    this.project = project;
  }

  public String getAcId()
  {
    return acid;
  }

  public String getProject()
  {
    return project;
  }
}
