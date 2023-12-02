package mn.erin.domain.bpm.usecase.branch_banking.billing;

/**
 * @author Lkhagvadorj.A
 **/

public class GetBillingTinsInput
{
  private String instanceId;
  private String registerNumber;

  public GetBillingTinsInput(String instanceId, String registerNumber)
  {
    this.registerNumber = registerNumber;
    this.instanceId = instanceId;
  }

  public String getRegisterNumber()
  {
    return registerNumber;
  }

  public void setRegisterNumber(String registerNumber)
  {
    this.registerNumber = registerNumber;
  }

  public String getInstanceId()
  {
    return instanceId;
  }

  public void setInstanceId(String instanceId)
  {
    this.instanceId = instanceId;
  }
}
