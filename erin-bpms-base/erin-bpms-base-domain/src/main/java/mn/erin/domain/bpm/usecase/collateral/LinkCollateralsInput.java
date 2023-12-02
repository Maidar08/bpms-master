package mn.erin.domain.bpm.usecase.collateral;

import java.util.Map;

/**
 * @author Tamir
 */
public class LinkCollateralsInput
{
  private final String accountNumber;
  private final String linkageType;
  private final Map<String, Object> collaterals;

  public LinkCollateralsInput(String accountNumber, String linkageType, Map<String, Object> collaterals)
  {
    this.accountNumber = accountNumber;
    this.linkageType = linkageType;
    this.collaterals = collaterals;
  }

  public String getAccountNumber()
  {
    return accountNumber;
  }

  public String getLinkageType()
  {
    return linkageType;
  }

  public Map<String, Object> getCollaterals()
  {
    return collaterals;
  }
}
