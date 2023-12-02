package mn.erin.domain.bpm.usecase.branch_banking.billing;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BranchBankingService;
import mn.erin.domain.bpm.usecase.branch_banking.GetAccountInfo;
import mn.erin.domain.bpm.usecase.branch_banking.GetAccountInfoInput;

/**
 * @author Lkhagvadorj.A
 **/

public class GetAccountInfoTest
{
  private BranchBankingService branchBankingService;
  private GetAccountInfo useCase;

  @Before
  public void setUp()
  {
    branchBankingService = Mockito.mock(BranchBankingService.class);
    useCase = new GetAccountInfo(branchBankingService);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_null() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test
  public void when_return_success() throws BpmServiceException, UseCaseException, ParseException
  {
    Mockito.when(branchBankingService.getAccountInfo("123", "test", true)).thenReturn(getTransactionMap());
    Map<String, Object> actualMap = useCase.execute(new GetAccountInfoInput("123", "test", true));

    Assert.assertEquals(actualMap.size(), 2);
    Assert.assertEquals(actualMap.get("k1"), "v1");
    Assert.assertEquals(actualMap.get("k2"), "v2");
  }

  private Map<String, Object> getTransactionMap()
  {
    Map<String, Object> transactionMap = new HashMap<>();

    transactionMap.put("k1", "v1");
    transactionMap.put("k2", "v2");

    return transactionMap;
  }
}
