package mn.erin.domain.bpm.usecase.branch_banking.transaction;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.branch_banking.transaction.ETransaction;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BranchBankingService;

public class GetTransactionEbankListTest
{
  private BranchBankingService branchBankingService;
  private GetTransactionEbankList useCase;
  private GetTransactionEbankListInput input;

  @Before
  public void setUp()
  {
    branchBankingService = Mockito.mock(BranchBankingService.class);
    useCase = new GetTransactionEbankList(branchBankingService);
    input = new GetTransactionEbankListInput("123", "123", "123", "123", "123", "123");
  }

  @Test(expected = NullPointerException.class)
  public void when_service_is_null() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_account_id_is_null() throws UseCaseException
  {
    useCase.execute(new GetTransactionEbankListInput(null, "123", "123", "123", "123", "123"));
  }

  @Test(expected = UseCaseException.class)
  public void when_channel_id_is_null() throws UseCaseException
  {
    useCase.execute(new GetTransactionEbankListInput("123", null, "123", "123", "123", "123"));
  }

  @Test(expected = UseCaseException.class)
  public void when_start_date_is_null() throws UseCaseException
  {
    useCase.execute(new GetTransactionEbankListInput("123", "123", "123", null, "123", "123"));
  }

  @Test(expected = UseCaseException.class)
  public void when_end_date_is_null() throws UseCaseException
  {
    useCase.execute(new GetTransactionEbankListInput("123", "123", "123", "123", null, "123"));
  }

  @Test(expected = UseCaseException.class)
  public void when_instance_id_is_null() throws UseCaseException
  {
    useCase.execute(new GetTransactionEbankListInput("123", "123", "123", "123", "123", null));
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_service_exception() throws BpmServiceException, UseCaseException
  {
    Mockito.when(branchBankingService.getTransactionEBankList("123", "123", "123", "123", "123", "123")).thenThrow(BpmServiceException.class);
    useCase.execute(input);
  }

  @Test
  public void when_works_correctly() throws UseCaseException, BpmServiceException
  {
    Mockito.when(branchBankingService.getTransactionEBankList("123", "123", "123", "123", "123", "123")).thenReturn(generateList());
    List<ETransaction> output = useCase.execute(input);

    Assert.assertFalse(output.isEmpty());
  }

  private static List<ETransaction> generateList()
  {
    List<ETransaction> eTransactions = new ArrayList<>();
    ETransaction eTransaction = new ETransaction("123", "123", "123", "123", "123", "123", "123", "123", "123", "123", "123", "123", "123", "123", "123", "123",
        "123", "123");

    eTransactions.add(eTransaction);

    return eTransactions;
  }
}
