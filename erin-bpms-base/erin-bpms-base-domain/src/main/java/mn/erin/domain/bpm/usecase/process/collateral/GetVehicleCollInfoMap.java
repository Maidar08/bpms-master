package mn.erin.domain.bpm.usecase.process.collateral;

import java.util.Map;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.NewCoreBankingService;

import static mn.erin.domain.bpm.BpmMessagesConstants.INVALID_INPUT_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INVALID_INPUT_MESSAGE;

/**
 * @author Tamir
 */
public class GetVehicleCollInfoMap extends AbstractUseCase<String, Map<String, Object>>
{
  private final NewCoreBankingService newCoreBankingService;

  public GetVehicleCollInfoMap(NewCoreBankingService newCoreBankingService)
  {
    this.newCoreBankingService = newCoreBankingService;
  }

  @Override
  public Map<String, Object> execute(String input) throws UseCaseException
  {
    if (null == input)
    {
      throw new UseCaseException(INVALID_INPUT_CODE, INVALID_INPUT_MESSAGE);
    }
    try
    {
      return newCoreBankingService.getVehicleCollInfo(input);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
  }
}
