package mn.erin.domain.bpm.usecase.loan;

import java.util.Collections;
import java.util.List;

import mn.erin.domain.bpm.model.account.XacAccount;

/**
 * @author Zorig
 */
public class GetAccountsListOutput
{
  private final List<XacAccount> accountList;

  public GetAccountsListOutput(List<XacAccount> accountList)
  {
    this.accountList = accountList;
  }

  public List<XacAccount> getAccountList()
  {
    return Collections.unmodifiableList(accountList);
  }
}
