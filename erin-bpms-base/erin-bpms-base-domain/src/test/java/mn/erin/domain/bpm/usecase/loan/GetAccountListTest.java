package mn.erin.domain.bpm.usecase.loan;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.NewCoreBankingService;

import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CUSTOMER_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;

public class GetAccountListTest
{
  private NewCoreBankingService newCoreBankingService;
  private GetAccountsList useCase;
  private GetAccountsListInput input;

  @Before
  public void setUp()
  {
    newCoreBankingService = Mockito.mock(NewCoreBankingService.class);
    useCase = new GetAccountsList(newCoreBankingService);
    input = new GetAccountsListInput("123", "123");
  }

  @Test(expected = NullPointerException.class)
  public void when_new_core_banking_service_is_null()
  {
    new GetAccountsList(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_use_case_exceotion() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Ignore
  @Test(expected = UseCaseException.class)
  public void when_reg_number_is_blank() throws UseCaseException
  {
    input.getRegNumber();
    Map<String, String> input1 = new HashMap<>();
    input1.put(PROCESS_TYPE_ID, ONLINE_LEASING_PROCESS_TYPE_ID);
    input1.put(PHONE_NUMBER, "phoneNumber");
    input1.put(REGISTER_NUMBER, input.getRegNumber());
    useCase.execute(input1);
  }

  @Ignore
  @Test(expected = UseCaseException.class)
  public void when_customer_number_is_blank() throws UseCaseException
  {
    Map<String, String> input1 = new HashMap<>();
    input1.put(PROCESS_TYPE_ID, ONLINE_LEASING_PROCESS_TYPE_ID);
    input1.put(PHONE_NUMBER, "phoneNumber");
    input1.put(CUSTOMER_ID, input.getCustomerNumber());
    useCase.execute(input1);
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_service_exception() throws BpmServiceException, UseCaseException
  {
    Map<String, String> input1 = new HashMap<>();
    input1.put(PROCESS_TYPE_ID, ONLINE_LEASING_PROCESS_TYPE_ID);
    input1.put(PHONE_NUMBER, "phoneNumber");
    input1.put(REGISTER_NUMBER, REGISTER_NUMBER);
    input1.put(CIF_NUMBER, CIF_NUMBER);
    Mockito.when(newCoreBankingService.getAccountsList(input1)).thenThrow(BpmServiceException.class);
    useCase.execute(input1);
  }

  //  @Test
  //  public void when_works_correctly() throws BpmServiceException, UseCaseException
  //  {
  //    Mockito.when(newCoreBankingService.getAccountsList("123","123")).thenReturn(generateVariableList());
  //    useCase.execute(input);
  //  }

  //  private static List<XacAccount> generateVariableList()
  //  {
  //    List<XacAccount> variableList = new ArrayList<>();
  //
  //    LoanClass loanClass = new LoanClass(1, "test");
  //    Loan loan = new Loan(LoanId.valueOf("123"), loanClass);
  //
  //    AccountId id = new AccountId("123");
  //
  //    variableList.add();
  //    return variableList;
  //  }
}
