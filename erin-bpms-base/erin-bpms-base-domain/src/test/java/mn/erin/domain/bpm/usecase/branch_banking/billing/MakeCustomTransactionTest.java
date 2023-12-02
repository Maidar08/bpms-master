package mn.erin.domain.bpm.usecase.branch_banking.billing;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BranchBankingService;

import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;

public class MakeCustomTransactionTest
{
  private BranchBankingService branchBankingService;
  private MakeCustomTransaction useCase;

  @Before
  public void setUp()
  {
    branchBankingService = Mockito.mock(BranchBankingService.class);
    useCase = new MakeCustomTransaction(branchBankingService);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_null() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test
  public void when_return_success() throws BpmServiceException, UseCaseException
  {
    Map<String, Object> input = getTransactionMap();
    input.put(CASE_INSTANCE_ID, "123");
    Mockito.when(branchBankingService.makeCustomTransaction(input, "123")).thenReturn(new HashMap<String, String>()
    {{
      put("k1", "v1");
      put("k2", "v2");
    }});
    Map<String, String> actualMap = useCase.execute(input);

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
