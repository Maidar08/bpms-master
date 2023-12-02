package mn.erin.domain.bpm.usecase.branch_banking;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BranchBankingService;
import mn.erin.domain.bpm.usecase.branch_banking.ussd.UpdateUserInfoUSSD;
import mn.erin.domain.bpm.usecase.branch_banking.ussd.UpdateUserUSSDInput;

public class UpdateUserInfoUSSDTest
{
  private final String dataJson = "{\n"
      + "  \"ussdPhoneNumber\": \"85452008\",\n"
      + "  \"registeredBranch\": \"CHO\",\n"
      + "  \"customerId\": \"R00000050\",\n"
      + "  \"mainAccount\": \"5000000140\",\n"
      + "  \"mainAccounts\": [\n"
      + "    {\n"
      + "      \"productCode\": \"AA180\",\n"
      + "      \"isPrimary\": true,\n"
      + "      \"accountType\": \"CAA\",\n"
      + "      \"accountNumber\": \"5000000140\",\n"
      + "      \"currencyCode\": \"MNT\"\n"
      + "    }\n"
      + "  ],\n"
      + "  \"allAccounts\": [\n"
      + "    {\n"
      + "      \"productCode\": \"AA180\",\n"
      + "      \"isPrimary\": true,\n"
      + "      \"accountType\": \"CAA\",\n"
      + "      \"accountNumber\": \"5000000140\",\n"
      + "      \"currencyCode\": \"MNT\"\n"
      + "    },\n"
      + "    {\n"
      + "      \"productCode\": \"BC150\",\n"
      + "      \"isPrimary\": false,\n"
      + "      \"accountType\": \"TUA\",\n"
      + "      \"accountNumber\": \"5000000110\",\n"
      + "      \"currencyCode\": \"MNT\"\n"
      + "    }\n"
      + "  ]\n"
      + "}";
  private BranchBankingService branchBankingService;
  private UpdateUserInfoUSSD updateUserInfoUSSD;

  @Before
  public void setup()
  {
    this.branchBankingService = Mockito.mock(BranchBankingService.class);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_is_null() throws UseCaseException
  {
    this.updateUserInfoUSSD = new UpdateUserInfoUSSD(branchBankingService);
    this.updateUserInfoUSSD.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_user_information_is_null() throws UseCaseException
  {
    this.updateUserInfoUSSD = new UpdateUserInfoUSSD(branchBankingService);
    this.updateUserInfoUSSD.execute(new UpdateUserUSSDInput(null, "languageID", "userID"));
  }

  @Test(expected = UseCaseException.class)
  public void instance_id_is_null() throws UseCaseException
  {
    this.updateUserInfoUSSD = new UpdateUserInfoUSSD(branchBankingService);
    this.updateUserInfoUSSD.execute(new UpdateUserUSSDInput(new HashMap<>(), "language", null));
  }

  @Test(expected = UseCaseException.class)
  public void when_service_exception_is_throwed() throws UseCaseException, BpmServiceException, ParseException
  {
    JSONObject data = new JSONObject(dataJson);
    Map<String, Object> customerInfo = data.toMap();
    this.updateUserInfoUSSD = new UpdateUserInfoUSSD(branchBankingService);
    Mockito.when(branchBankingService.updateUserUSSD(customerInfo, "MN", "Test1")).thenThrow(new BpmServiceException("Service Exception"));
    this.updateUserInfoUSSD.execute(new UpdateUserUSSDInput(customerInfo, "MN", "Test1"));
  }

  @Test
  public void when_working_successful() throws UseCaseException, BpmServiceException, ParseException
  {
    JSONObject data = new JSONObject(dataJson);
    Map<String, Object> customerInfo = data.toMap();
    this.updateUserInfoUSSD = new UpdateUserInfoUSSD(branchBankingService);
    Mockito.when(branchBankingService.updateUserUSSD(customerInfo, "MN", "Test1")).thenReturn(new HashMap<>());
    Assert.assertEquals(this.updateUserInfoUSSD.execute(new UpdateUserUSSDInput(data.toMap(), "MN", "Test1")), new HashMap<>());
  }
}
