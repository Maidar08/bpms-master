package mn.erin.domain.bpm.usecase.loan;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.LoanService;

public class GetCustomerRelatedInfoTest
{
  private LoanService loanService;
  private GetCustomerRelatedInfo useCase;
  private GetCustomerRelatedInfoInput input;

  @Before
  public void setUp()
  {
    loanService = Mockito.mock(LoanService.class);
    useCase = new GetCustomerRelatedInfo(loanService);
    input = new GetCustomerRelatedInfoInput("123", "123");
  }

  @Test(expected = NullPointerException.class)
  public void when_loan_service_is_null()
  {
    new GetCustomerRelatedInfo(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_use_case_exception() throws UseCaseException, BpmServiceException
  {
    useCase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_relation_is_blank() throws UseCaseException
  {
    input.setRelation("");
    useCase.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void when_customer_cid_is_blank() throws UseCaseException
  {
    input.setCustomerCid("");
    useCase.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_service_exception() throws BpmServiceException, UseCaseException
  {
    Mockito.when(loanService.getCustomerRelatedInfo(input.getCustomerCid(), input.getRelation())).thenThrow(BpmServiceException.class);
    useCase.execute(input);
  }

  @Test
  public void when_works_correctly() throws UseCaseException, BpmServiceException
  {
    Map<String, String> output = new HashMap<>();
    output.put("key", "value");
    Mockito.when(loanService.getCustomerRelatedInfo(input.getCustomerCid(), input.getRelation())).thenReturn(output);
    Assert.assertEquals(useCase.execute(input), output);
  }
}
