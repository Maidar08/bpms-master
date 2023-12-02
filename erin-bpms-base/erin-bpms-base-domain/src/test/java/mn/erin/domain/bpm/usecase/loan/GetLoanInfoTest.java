package mn.erin.domain.bpm.usecase.loan;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.loan.BorrowerId;
import mn.erin.domain.bpm.model.loan.Loan;
import mn.erin.domain.bpm.model.loan.LoanClass;
import mn.erin.domain.bpm.model.loan.LoanId;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.LoanService;

public class GetLoanInfoTest
{
  private LoanService loanService;
  private GetLoanInfo useCase;
  private GetLoanInfoInput input;

  @Before
  public void setUp()
  {
    loanService = Mockito.mock(LoanService.class);
    useCase = new GetLoanInfo(loanService);
    input = new GetLoanInfoInput("123", "123");
  }

  @Test(expected = NullPointerException.class)
  public void when_loan_service_is_null()
  {
    new GetLoanInfo(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_borrowed_id_is_blank() throws UseCaseException
  {
    input.setBorrowerId("");
    useCase.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void when_customer_id_is_blank() throws UseCaseException
  {
    input.setCustomerCID("");
    useCase.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_use_case_exception() throws UseCaseException, BpmServiceException
  {
    useCase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_service_exception() throws BpmServiceException, UseCaseException
  {
    Mockito.when(loanService.getLoanList("123", BorrowerId.valueOf("123"))).thenThrow(BpmServiceException.class);
    useCase.execute(input);
  }

  @Test
  public void when_works_correctly() throws BpmServiceException, UseCaseException
  {
    Mockito.when(loanService.getLoanList("123", BorrowerId.valueOf("123"))).thenReturn(generateVariableList());
    useCase.execute(input);
  }

  private static List<Loan> generateVariableList()
  {
    List<Loan> variableList = new ArrayList<>();

    LoanClass loanClass = new LoanClass(1, "test");
    Loan loan = new Loan(LoanId.valueOf("123"), loanClass);

    variableList.add(loan);
    return variableList;
  }
}
