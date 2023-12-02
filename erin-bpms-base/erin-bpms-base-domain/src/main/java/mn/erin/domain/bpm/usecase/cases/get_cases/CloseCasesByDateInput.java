package mn.erin.domain.bpm.usecase.cases.get_cases;

import java.time.LocalDate;

/**
 * @author Lkhagvadorj.A
 **/

public class CloseCasesByDateInput
{
  private LocalDate endDate;
  private long sleepTime;
  private int sleepPerProcess;
  private boolean threadStopper;

  public CloseCasesByDateInput(LocalDate endDate, long sleepTime, int sleepPerProcess, boolean threadStopper)
  {
    this.endDate = endDate;
    this.sleepTime = sleepTime;
    this.sleepPerProcess = sleepPerProcess;
    this.threadStopper = threadStopper;
  }

  public CloseCasesByDateInput()
  {

  }

  public LocalDate getEndDate()
  {
    return endDate;
  }

  public void setEndDate(LocalDate endDate)
  {
    this.endDate = endDate;
  }

  public long getSleepTime()
  {
    return sleepTime;
  }

  public void setSleepTime(long sleepTime)
  {
    this.sleepTime = sleepTime;
  }

  public int getSleepPerProcess()
  {
    return sleepPerProcess;
  }

  public void setSleepPerProcess(int sleepPerProcess)
  {
    this.sleepPerProcess = sleepPerProcess;
  }

  public boolean isThreadStopper()
  {
    return threadStopper;
  }

  public void setThreadStopper(boolean threadStopper)
  {
    this.threadStopper = threadStopper;
  }
}
