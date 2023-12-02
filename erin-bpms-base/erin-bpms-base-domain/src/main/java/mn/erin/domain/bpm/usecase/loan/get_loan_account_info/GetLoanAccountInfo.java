package mn.erin.domain.bpm.usecase.loan.get_loan_account_info;

import java.util.Map;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.NewCoreBankingService;

/**
 * @author Oyungerel Chuluunsukh
 **/
public class GetLoanAccountInfo extends AbstractUseCase<Map<String, String>, Map<String, Object>>
{
  private final NewCoreBankingService newCoreBankingService;

  public GetLoanAccountInfo(NewCoreBankingService newCoreBankingService)
  {
    this.newCoreBankingService = newCoreBankingService;
  }

  @Override
  public Map<String, Object> execute(Map<String, String> input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException("Get loan account info input cannot be null!");
    }
    try
    {
      return newCoreBankingService.getLoanAccountInfo(input);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
  }
}
