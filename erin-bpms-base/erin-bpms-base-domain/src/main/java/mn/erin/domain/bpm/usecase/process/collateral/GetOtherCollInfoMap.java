package mn.erin.domain.bpm.usecase.process.collateral;

import java.util.Map;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.NewCoreBankingService;

/**
 * @author Tamir
 */
public class GetOtherCollInfoMap extends AbstractUseCase<String, Map<String, Object>>
{
  private final NewCoreBankingService newCoreBankingService;

  public GetOtherCollInfoMap(NewCoreBankingService newCoreBankingService)
  {
    this.newCoreBankingService = newCoreBankingService;
  }

  @Override
  public Map<String, Object> execute(String input) throws UseCaseException
  {
    try
    {
      return newCoreBankingService.getOtherCollateral(input);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
  }
}
