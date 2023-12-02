package mn.erin.domain.bpm.usecase.bnpl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.DirectOnlineCoreBankingService;

public class AddAccLienTest
{
  private AddAccLien useCase;
  private DirectOnlineCoreBankingService directOnlineCoreBankingService;

  @Before
  public void setUp()
  {
    directOnlineCoreBankingService = Mockito.mock(DirectOnlineCoreBankingService.class);
    useCase = new AddAccLien(directOnlineCoreBankingService);
  }

  @Test(expected = NullPointerException.class)
  public void when_service_null()
  {
    new AddAccLien(null);
  }

  @Test
  public void when_response_is_empty() throws BpmServiceException, UseCaseException
  {
    Mockito.when(directOnlineCoreBankingService.addAccLien(Mockito.anyMap())).thenReturn(Collections.emptyMap());
    Map<String, Object> output = useCase.execute(getDummyRequestBody());
    Assert.assertEquals(output, Collections.emptyMap());
  }

  @Test
  public void when_response_is_successful() throws BpmServiceException, UseCaseException
  {
    Mockito.when(directOnlineCoreBankingService.addAccLien(Mockito.anyMap())).thenReturn(getDummyResData());
    Map<String, Object> output = useCase.execute(getDummyRequestBody());
    Assert.assertNotNull(output.get("AcctId"));
  }

  private Map<String, Object> getDummyRequestBody(){
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("AcctId", "1234");
    requestBody.put("ModuleType", "ULIEN");
    requestBody.put("NewLienAmt", "10000");
    requestBody.put("NewLienAmtCurrencyCode", "MNT");
    requestBody.put("ReasonCode", "002");
    requestBody.put("Rmks", "");
    Map<String, Object> request = new HashMap<>();
    request.put("Request", requestBody);
    return requestBody;
  }
  private Map<String, Object> getDummyResData()
  {
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("AcctId", "1234");
    requestBody.put("SchmCode", "");
    requestBody.put("SchmType", "");
    requestBody.put("SchmCodeDesc", "");
    requestBody.put("BranchId", "null");
    requestBody.put("BranchName", "null");
    requestBody.put("LienId", "XB17353");
    return requestBody;
  }
}
