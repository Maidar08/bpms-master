package mn.erin.domain.bpm.usecase.branch_banking.ussd;

public class GetUserInfoFromUSSDInput
{
  private String cif;
  private String phone;
  private String branch;
  private String instanceId;

  public GetUserInfoFromUSSDInput(String cif, String phone, String branch, String instanceId)
  {
    this.cif = cif;
    this.phone = phone;
    this.branch = branch;
    this.instanceId = instanceId;
  }

  public String getCif()
  {
    return cif;
  }

  public void setCif(String cif)
  {
    this.cif = cif;
  }

  public String getPhone()
  {
    return phone;
  }

  public void setPhone(String phone)
  {
    this.phone = phone;
  }

  public String getBranch()
  {
    return branch;
  }

  public void setBranch(String branch)
  {
    this.branch = branch;
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
