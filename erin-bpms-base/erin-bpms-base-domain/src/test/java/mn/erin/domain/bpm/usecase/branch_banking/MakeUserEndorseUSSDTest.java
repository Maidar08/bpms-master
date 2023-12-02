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
import mn.erin.domain.bpm.usecase.branch_banking.ussd.MakeUserEndorseOnUSSD;
import mn.erin.domain.bpm.usecase.branch_banking.ussd.MakeUserEndorseOnUSSD;
import mn.erin.domain.bpm.usecase.branch_banking.ussd.UpdateUserUSSDInput;

import static mn.erin.domain.bpm.BpmModuleConstants.BRANCH_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANCE_ID;

public class MakeUserEndorseUSSDTest
{
  private final String dataJson = "{\n"
      + "        \"Code\": \"0\",\n"
      + "        \"Desc\": \"SUCCESS\"\n"
      + "    }";
  private BranchBankingService branchBankingService;
  private MakeUserEndorseOnUSSD makeUserEndorseOnUSSD;

  @Before
  public void setup()
  {
    this.branchBankingService = Mockito.mock(BranchBankingService.class);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_is_null() throws UseCaseException
  {
    this.makeUserEndorseOnUSSD = new MakeUserEndorseOnUSSD(branchBankingService);
    this.makeUserEndorseOnUSSD.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void instance_id_is_null() throws UseCaseException
  {
    this.makeUserEndorseOnUSSD = new MakeUserEndorseOnUSSD(branchBankingService);
    Map<String, String> input = new HashMap<>();
    input.put("id", "123");
    input.put(BRANCH_NUMBER, "123");
    this.makeUserEndorseOnUSSD.execute(input);
  }

  @Test
  public void when_working_successful() throws UseCaseException
  {
    this.makeUserEndorseOnUSSD = new MakeUserEndorseOnUSSD(branchBankingService);
    Mockito.when(this.makeUserEndorseOnUSSD.execute(returnInput())).thenReturn(true);
    Assert.assertEquals(this.makeUserEndorseOnUSSD.execute(returnInput()), true);
  }

  private Map<String, String> returnInput(){
    Map<String, String> input = new HashMap<>();
    input.put("id", "123");
    input.put(INSTANCE_ID, "123");
    input.put(BRANCH_NUMBER, "123");
    return input;
  }
}
