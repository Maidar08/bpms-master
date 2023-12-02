package mn.erin.domain.bpm.usecase.branch_banking;

import java.util.List;

import mn.erin.domain.bpm.model.branch_banking.AccountInfo;

public class CheckAccountNamesInput
{
  private List<AccountInfo> customerInfos;
  private String instanceId;

  public CheckAccountNamesInput(List<AccountInfo> customerInfos, String instanceId)
  {
    this.customerInfos = customerInfos;
    this.instanceId = instanceId;
  }

  public List<AccountInfo> getCustomerInfos()
  {
    return customerInfos;
  }

  public void setCustomerInfos(List<AccountInfo> customerInfos)
  {
    this.customerInfos = customerInfos;
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

