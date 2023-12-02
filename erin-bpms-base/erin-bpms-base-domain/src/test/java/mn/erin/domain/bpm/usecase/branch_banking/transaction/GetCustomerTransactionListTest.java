package mn.erin.domain.bpm.usecase.branch_banking.transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.branch_banking.transaction.CustomerTransaction;
import mn.erin.domain.bpm.model.branch_banking.transaction.TransactionId;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BranchBankingService;

import static org.hamcrest.Matchers.hasSize;

public class GetCustomerTransactionListTest
{
  private BranchBankingService branchBankingService;
  private GetCustomerTransactions usecase;

  @Before
  public void setUp()
  {
    branchBankingService = Mockito.mock(BranchBankingService.class);
    usecase = new GetCustomerTransactions(branchBankingService);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_null() throws UseCaseException
  {
    usecase.execute(null);
  }

  @Test()
  public void when_return_success() throws BpmServiceException, UseCaseException
  {
    Mockito.when(branchBankingService.findByUserIdAndDate("user", "2021-02-06", "123")).thenReturn(getCustomerTransactionList());
    Map<String, Object> input = new HashMap<>();

    input.put("userId", "user");
    input.put("transactionDate", "2021-02-06");

    GetCustomerTransactionsOutput transactionList = usecase.execute(new GetCustomerTransactionsInput("user", "2021-02-06", "123"));
    List<CustomerTransaction> expectedTrasactinList = getCustomerTransactionList();
    Assert.assertThat(expectedTrasactinList, hasSize(2));
  }

  private List<CustomerTransaction> getCustomerTransactionList()
  {
    List<CustomerTransaction> customerTransactions = new ArrayList<>();
    customerTransactions.add(new CustomerTransaction(new TransactionId("XB1111111"), "2021-02-06", "150000", "MNT", "108",
        "5959787878", "user", "type", "subType", "status", "none"));
    customerTransactions.add(new CustomerTransaction(new TransactionId("XB2222222"), "2021-02-06", "150000", "MNT", "108",
        "5959787878", "user", "type", "subType", "status", "none"));
    return customerTransactions;
  }
}

