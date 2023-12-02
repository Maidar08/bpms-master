package mn.erin.domain.bpm.usecase.branch_banking;

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
import mn.erin.domain.bpm.usecase.branch_banking.ussd.GetUserInfoFromUSSD;
import mn.erin.domain.bpm.usecase.branch_banking.ussd.GetUserInfoFromUSSDInput;

public class GetUserFromUSSDTest
{
  private BranchBankingService branchBankingService;
  private GetUserInfoFromUSSD usecase;

  @Before
  public void setup()
  {
    branchBankingService = Mockito.mock(BranchBankingService.class);
    usecase = new GetUserInfoFromUSSD(branchBankingService);
  }

  @Test(expected = Exception.class)
  public void when_input_is_null() throws UseCaseException
  {
    usecase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_cif_phone_are_both_null() throws UseCaseException
  {
    usecase.execute(new GetUserInfoFromUSSDInput(null, null, null, null));
  }

  @Test(expected = UseCaseException.class)
  public void when_instance_id_is_null() throws UseCaseException
  {
    usecase.execute(new GetUserInfoFromUSSDInput("randomString", null, null, null));
  }

  @Test(expected = UseCaseException.class)
  public void when_bpms_service_exception() throws UseCaseException, BpmServiceException, ParseException
  {
    Mockito.when(branchBankingService.getUserInfoFromUSSD("cif", "", null, "instanceId")).thenThrow(new BpmServiceException("error"));
    usecase.execute(new GetUserInfoFromUSSDInput("cif", null, null, "instanceId"));
  }

  @Test
  public void successful_working() throws UseCaseException, BpmServiceException, ParseException
  {
    HashMap<String, Object> serverResponse = new HashMap<>();
    serverResponse.put("key", "value");
    Mockito.when(branchBankingService.getUserInfoFromUSSD("cif", "", null, "instanceId")).thenReturn(serverResponse);
    Map<String, Object> result = usecase.execute(new GetUserInfoFromUSSDInput("cif", null, null, "instanceId"));
    Assert.assertEquals(serverResponse, result);
  }
}