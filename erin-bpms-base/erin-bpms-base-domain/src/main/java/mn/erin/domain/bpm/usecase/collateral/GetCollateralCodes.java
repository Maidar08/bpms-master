package mn.erin.domain.bpm.usecase.collateral;

import java.util.List;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.NewCoreBankingService;

import static mn.erin.domain.bpm.BpmMessagesConstants.COLLATERAL_TYPE_INVALID_ERROR_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.COLLATERAL_TYPE_INVALID_ERROR_MESSAGE;

/**
 * @author Tamir
 */
public class GetCollateralCodes extends AbstractUseCase<String, GetCollateralCodesOutput>
{
  private final NewCoreBankingService newCoreBankingService;

  public GetCollateralCodes(NewCoreBankingService newCoreBankingService)
  {
    this.newCoreBankingService = newCoreBankingService;
  }

  @Override
  public GetCollateralCodesOutput execute(String collType) throws UseCaseException
  {
    if (collType.isEmpty())
    {
      throw new UseCaseException(COLLATERAL_TYPE_INVALID_ERROR_CODE, COLLATERAL_TYPE_INVALID_ERROR_MESSAGE);
    }

    try
    {
      List<String> collateralCodes = newCoreBankingService.getCollateralCode(collType);

      return new GetCollateralCodesOutput(collateralCodes);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
  }
}
