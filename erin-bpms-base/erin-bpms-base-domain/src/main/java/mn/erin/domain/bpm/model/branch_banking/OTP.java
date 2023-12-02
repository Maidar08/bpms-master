package mn.erin.domain.bpm.model.branch_banking;

import java.time.Instant;

/**
 * @author Bilguunbor
 **/

public class OTP
{
  private String generatedOTP;
  private Instant instantTime;

  public OTP(String generatedOTP, Instant instantTime)
  {
    this.generatedOTP = generatedOTP;
    this.instantTime = instantTime;
  }

  public String getGeneratedOTP()
  {
    return generatedOTP;
  }

  public void setGeneratedOTP(String generatedOTP)
  {
    this.generatedOTP = generatedOTP;
  }

  public Instant getInstantTime()
  {
    return instantTime;
  }

  public void setInstantTime(Instant instantTime)
  {
    this.instantTime = instantTime;
  }
}
