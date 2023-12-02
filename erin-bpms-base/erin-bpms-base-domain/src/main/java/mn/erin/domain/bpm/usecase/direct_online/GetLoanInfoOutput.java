package mn.erin.domain.bpm.usecase.direct_online;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author Lkhagvadorj.A
 **/

public class GetLoanInfoOutput
{
  private boolean hasActiveLoanAccount;
  private Map<String, Map<String, Object>> mappedAccount;
  private BigDecimal totalClosingAmount;
  private BigDecimal totalBalance;

  public GetLoanInfoOutput(boolean hasActiveLoanAccount, Map<String, Map<String, Object>> mappedAccount, BigDecimal totalClosingAmount,
      BigDecimal totalBalance)
  {
    this.hasActiveLoanAccount = hasActiveLoanAccount;
    this.mappedAccount = mappedAccount;
    this.totalClosingAmount = totalClosingAmount;
    this.totalBalance = totalBalance;
  }

  public boolean isHasActiveLoanAccount()
  {
    return hasActiveLoanAccount;
  }

  public Map<String, Map<String, Object>> getMappedAccount()
  {
    return mappedAccount;
  }

  public BigDecimal getTotalClosingAmount()
  {
    return totalClosingAmount;
  }

  public BigDecimal getTotalBalance()
  {
    return totalBalance;
  }
}
