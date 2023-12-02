package mn.erin.domain.bpm.usecase.cases.get_cases;

import java.time.LocalDate;

/**
 * @author Lkhagvadorj.A
 **/

public class GetCasesByDateInput
{
  private LocalDate startDate;
  private LocalDate endDate;

  public GetCasesByDateInput(LocalDate startDate, LocalDate endDate)
  {
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public LocalDate getStartDate()
  {
    return startDate;
  }

  public void setStartDate(LocalDate startDate)
  {
    this.startDate = startDate;
  }

  public LocalDate getEndDate()
  {
    return endDate;
  }

  public void setEndDate(LocalDate endDate)
  {
    this.endDate = endDate;
  }
}
