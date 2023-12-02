package mn.erin.domain.bpm.model.branch_banking.account_reference;

public class AccountJointHolder
{
  public String checkJointHolder;
  public String jointHolderName;
  public String jointHolderId;

  public AccountJointHolder(String checkJointHolder, String jointHolderName, String jointHolderId)
  {
    this.checkJointHolder = checkJointHolder;
    this.jointHolderName = jointHolderName;
    this.jointHolderId = jointHolderId;
  }

  public String getJointHolderName()
  {
    return jointHolderName;
  }

  public void setJointHolderName(String jointHolderName)
  {
    this.jointHolderName = jointHolderName;
  }

  public String getJointHolderId()
  {
    return jointHolderId;
  }

  public void setJointHolderId(String jointHolderId)
  {
    this.jointHolderId = jointHolderId;
  }

}
