package mn.erin.domain.bpm.usecase.bnpl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BnplCoreBankingService;
import mn.erin.domain.bpm.service.BpmServiceException;

public class GetBnplInvoiceInfoTest
{
  private GetBnplInvoiceInfo useCase;
  private BnplCoreBankingService bnplCoreBankingService;

  @Before
  public void setUp()
  {
    bnplCoreBankingService = Mockito.mock(BnplCoreBankingService.class);
    useCase = new GetBnplInvoiceInfo(bnplCoreBankingService);
  }

  @Test(expected = NullPointerException.class)
  public void when_service_null()
  {
    new GetBnplInvoiceInfo(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_is_null_throws_exception() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test
  public void when_response_is_empty() throws BpmServiceException, UseCaseException
  {
    Mockito.when(bnplCoreBankingService.getBnplInvoiceInfo(Mockito.anyString())).thenReturn(Collections.emptyMap());
    Map<String, Object> output = useCase.execute("1234");
    Assert.assertEquals(output, Collections.emptyMap());
  }

  @Test
  public void when_response_is_successful() throws BpmServiceException, UseCaseException
  {
    Mockito.when(bnplCoreBankingService.getBnplInvoiceInfo(Mockito.anyString())).thenReturn(getDummyResData());
    Map<String, Object> output = useCase.execute("1234");
    Assert.assertNotNull(output.get("invoiceNo"));
    Assert.assertNotNull(output.get("Terminal_ID"));
  }

  private Map<String, Object> getDummyResData()
  {
    Map<String, Object> result = new HashMap<>();
    result.put("invoiceNo", "1234");
    result.put("sumPrice", "sumPrice");
    result.put("prepaymentAmt", "prepaymentAmt");
    result.put("Terminal_ID", "Terminal_ID");
    result.put("CreatedOn", "CreatedOn");
    result.put("State", "State");
    result.put("reqUserID", "reqUserID");

    return result;
  }
}
