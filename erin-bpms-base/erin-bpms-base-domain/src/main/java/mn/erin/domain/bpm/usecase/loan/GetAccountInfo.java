package mn.erin.domain.bpm.usecase.loan;

import java.util.Map;
import java.util.Objects;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.NewCoreBankingService;

import static mn.erin.domain.bpm.BpmLoanContractConstants.ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmMessagesConstants.ACCOUNT_NUMBER_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.ACCOUNT_NUMBER_NULL_MESSAGE;

public class GetAccountInfo extends AbstractUseCase<Map<String, String>, Map<String, Object>>
{
  private final NewCoreBankingService newCoreBankingService;

  public GetAccountInfo(NewCoreBankingService newCoreBankingService)
  {
    this.newCoreBankingService = Objects.requireNonNull(newCoreBankingService, "New core banking service is required!");
  }

  @Override
  public Map<String, Object> execute(Map<String, String> input) throws UseCaseException
  {
    validateNotBlank(input.get(ACCOUNT_NUMBER), ACCOUNT_NUMBER_NULL_CODE + ACCOUNT_NUMBER_NULL_MESSAGE);

    try
    {
      return newCoreBankingService.getAccountInfo(input);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
  }
}
