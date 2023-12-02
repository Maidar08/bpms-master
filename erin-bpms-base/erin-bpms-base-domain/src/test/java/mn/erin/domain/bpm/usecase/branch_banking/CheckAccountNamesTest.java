package mn.erin.domain.bpm.usecase.branch_banking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.branch_banking.AccountInfo;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BranchBankingService;

public class CheckAccountNamesTest
{
  private static final String INSTANCE_ID = "instance";
  private static final String ACCOUNT_ID = "123";

  private BranchBankingService branchBankingService;
  private CheckAccountNames useCase;
  private CheckAccountNamesInput input;

  @Before
  public void setUp() throws BpmServiceException
  {
    branchBankingService = Mockito.mock(BranchBankingService.class);

    AccountInfo accountInfo = new AccountInfo();
    accountInfo.setAccountId(ACCOUNT_ID);
    accountInfo.setAccountCcy("MNT");
    accountInfo.setCustomerName("Suhee");
    List<AccountInfo> customerInfos = new ArrayList<>();
    customerInfos.add(accountInfo);
    input = new CheckAccountNamesInput(customerInfos, INSTANCE_ID);

    Map<String, AccountInfo> accountsFromService = new HashMap<>();
    accountsFromService.put("123", accountInfo);

    Mockito.when(branchBankingService.getAccountNames(Mockito.any(), Mockito.any())).thenReturn(accountsFromService);
    useCase = new CheckAccountNames(branchBankingService);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_null() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_blank() throws UseCaseException
  {
    input.setInstanceId("");
    useCase.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void when_cutomerInfo_is_null() throws UseCaseException
  {
    input.setCustomerInfos(null);
    useCase.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_is_empty() throws UseCaseException
  {
    input.setCustomerInfos(new ArrayList<>());
    useCase.execute(input);
  }

  @Test
  public void when_return_success() throws UseCaseException
  {
    Map<String, Object> map = useCase.execute(input);
    List<AccountInfo> accountInfos = (List<AccountInfo>) map.get("accountInfo");
    String id = accountInfos.get(0).getAccountId();
    Assert.assertEquals(id, ACCOUNT_ID);
  }

  @Test
  public void when_return_unsuccessfull() throws UseCaseException
  {
    AccountInfo accountInfo = new AccountInfo();
    accountInfo.setAccountId("ACCOUNT_ID");
    accountInfo.setAccountCcy("MNT");
    accountInfo.setCustomerName("sukhee");
    List<AccountInfo> customerInfos = new ArrayList<>();
    customerInfos.add(accountInfo);
    input.setCustomerInfos(customerInfos);
    Map<String, Object> map = useCase.execute(input);
    List<String> accountInfos = (List<String>) map.get("invalidAccounts");
    String id = accountInfos.get(0);
    Assert.assertEquals(id, "ACCOUNT_ID");
  }
}
