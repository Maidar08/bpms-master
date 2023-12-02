package mn.erin.domain.bpm.usecase.branch_banking.billing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.branch_banking.CustomInvoice;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BranchBankingService;

import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static org.hamcrest.Matchers.hasSize;

public class GetCustomInfoListTest

{
  private BranchBankingService branchBankingService;
  private GetCustomInfoList useCase;

  @Before
  public void setUp()
  {
    branchBankingService = Mockito.mock(BranchBankingService.class);
    useCase = new GetCustomInfoList(branchBankingService);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_null() throws UseCaseException
  {
    useCase.execute(null);
  }


  @Test(expected = UseCaseException.class)
  public void when_get_custom_info() throws BpmServiceException, UseCaseException
  {
    Mockito.when(branchBankingService.getCustomInfoList("invoiceNumber", "02214101418I0001601", "123")).thenThrow(BpmServiceException.class);
    Map<String, Object> input = new HashMap<String, Object>()
    {{
      put("type", "invoiceNumber");
      put("value", "02214101418I0001601");
      put(CASE_INSTANCE_ID, "123");
    }};
    List<CustomInvoice> customInvoices = useCase.execute(input);
    Assert.assertThat(customInvoices, hasSize(1));
  }

}
