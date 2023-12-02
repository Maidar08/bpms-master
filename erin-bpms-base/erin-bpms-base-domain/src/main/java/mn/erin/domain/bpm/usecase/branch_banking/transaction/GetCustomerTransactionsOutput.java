package mn.erin.domain.bpm.usecase.branch_banking.transaction;

import java.util.Collection;

import mn.erin.domain.bpm.model.branch_banking.transaction.CustomerTransaction;

/**
 * @author Tamir
 */
public class GetCustomerTransactionsOutput
{
  private Collection<CustomerTransaction> transactions;

  public GetCustomerTransactionsOutput(Collection<CustomerTransaction> transactions)
  {
    this.transactions = transactions;
  }

  public Collection<CustomerTransaction> getTransactions()
  {
    return transactions;
  }

  public void setTransactions(Collection<CustomerTransaction> transactions)
  {
    this.transactions = transactions;
  }

}
