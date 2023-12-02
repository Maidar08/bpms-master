package mn.erin.domain.bpm.usecase.bnpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.DirectOnlineCoreBankingService;

import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;

public class GetAccLienTest
{
  private GetAccLien useCase;
  private DirectOnlineCoreBankingService directOnlineCoreBankingService;

  @Before
  public void setUp()
  {
    directOnlineCoreBankingService = Mockito.mock(DirectOnlineCoreBankingService.class);
    useCase = new GetAccLien(directOnlineCoreBankingService);
  }

  @Test(expected = NullPointerException.class)
  public void when_service_null()
  {
    new GetAccLien(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_is_null_throws_exception() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test
  public void when_response_is_empty() throws BpmServiceException, UseCaseException
  {
    Map<String, String> input = new HashMap<>();
    input.put(PROCESS_TYPE_ID, ONLINE_LEASING_PROCESS_TYPE_ID);
    input.put(PHONE_NUMBER, "phoneNumber");
    input.put("AcctId", "1234");
    input.put("ModuleType", "ULIEN");
    Mockito.when(directOnlineCoreBankingService.getAccLien(Mockito.anyMap())).thenReturn(Collections.emptyMap());
    Map<String, Object> output = useCase.execute(input);
    Assert.assertEquals(output, Collections.emptyMap());
  }

  @Test
  public void when_response_is_successful() throws BpmServiceException, UseCaseException
  {
    Map<String, String> input = new HashMap<>();
    input.put(PROCESS_TYPE_ID, ONLINE_LEASING_PROCESS_TYPE_ID);
    input.put(PHONE_NUMBER, "phoneNumber");
    input.put("AcctId", "1234");
    input.put("ModuleType", "ULIEN");
    Mockito.when(directOnlineCoreBankingService.getAccLien(Mockito.anyMap())).thenReturn(getDummyResData());
    Map<String, Object> output = useCase.execute(input);
    Assert.assertNotNull(output.get("acctId"));
    Assert.assertEquals(output.get("acctCurr"), "MNT");
    Assert.assertEquals(output.get("moduleType"), "ULIEN");
  }
  private Map<String, Object> getDummyResData(){
    Map<String, Object> result = new HashMap<>();
    result.put("acctId", "1234");
    result.put("acctCurr", "MNT");
    result.put("schmCode", "");
    result.put("schmType", "");
    result.put("schmCodeDesc", "");
    result.put("branchId", "null");
    result.put("branchName", "null");
    result.put("moduleType", "ULIEN");

    List<Map<String, Object>> lienDtlsInfoList = new ArrayList<>();
    result.put("lienDtls", lienDtlsInfoList);

    return result;
  }
}
