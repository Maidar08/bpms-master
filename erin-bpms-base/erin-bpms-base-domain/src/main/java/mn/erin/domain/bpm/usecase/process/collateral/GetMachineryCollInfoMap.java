package mn.erin.domain.bpm.usecase.process.collateral;

import java.util.Map;
import java.util.Objects;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.NewCoreBankingService;

/**
 * @author Tamir
 */
public class GetMachineryCollInfoMap extends AbstractUseCase<String, Map<String, Object>>
{
  private final NewCoreBankingService newCoreBankingService;

  public GetMachineryCollInfoMap(NewCoreBankingService newCoreBankingService)
  {
    this.newCoreBankingService = Objects.requireNonNull(newCoreBankingService, "New Core Banking Service is required!");
  }

  @Override
  public Map<String, Object> execute(String input) throws UseCaseException
  {
    try
    {
      return newCoreBankingService.getMachineryCollateral(input);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
  }
}
