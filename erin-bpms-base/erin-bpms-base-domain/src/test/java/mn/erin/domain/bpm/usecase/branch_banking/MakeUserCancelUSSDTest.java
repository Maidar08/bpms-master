package mn.erin.domain.bpm.usecase.branch_banking;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BranchBankingService;
import mn.erin.domain.bpm.usecase.branch_banking.ussd.MakeUserCancelOnUSSD;

import static mn.erin.domain.bpm.BpmModuleConstants.BRANCH_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANCE_ID;

public class MakeUserCancelUSSDTest
{
  private final String dataJson = "{\n"
      + "        \"Code\": \"0\",\n"
      + "        \"Desc\": \"SUCCESS\"\n"
      + "    }";
  private BranchBankingService branchBankingService;
  private MakeUserCancelOnUSSD makeUserCancelOnUSSD;

  @Before
  public void setup()
  {
    this.branchBankingService = Mockito.mock(BranchBankingService.class);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_is_null() throws UseCaseException
  {
    this.makeUserCancelOnUSSD = new MakeUserCancelOnUSSD(branchBankingService);
    this.makeUserCancelOnUSSD.execute(null);
  }
  
  @Test(expected = UseCaseException.class)
  public void instance_id_is_null() throws UseCaseException
  {
    this.makeUserCancelOnUSSD = new MakeUserCancelOnUSSD(branchBankingService);
    Map<String, String> input = new HashMap<>();
    input.put("id", "123");
    input.put(BRANCH_NUMBER, "123");
    this.makeUserCancelOnUSSD.execute(input);
  }

  @Test
  public void when_working_successful() throws UseCaseException
  {
    this.makeUserCancelOnUSSD = new MakeUserCancelOnUSSD(branchBankingService);
    Mockito.when(this.makeUserCancelOnUSSD.execute(returnInput())).thenReturn(true);
    Assert.assertEquals(this.makeUserCancelOnUSSD.execute(returnInput()), true);
  }

  private Map<String, String> returnInput(){
    Map<String, String> input = new HashMap<>();
    input.put("id", "123");
    input.put(INSTANCE_ID, "123");
    input.put(BRANCH_NUMBER, "123");
    return input;
  }
}
