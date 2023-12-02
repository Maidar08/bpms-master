package mn.erin.domain.bpm.usecase.collateral;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.NewCoreBankingService;

import static mn.erin.domain.bpm.BpmMessagesConstants.INVALID_INPUT_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INVALID_INPUT_MESSAGE;

/**
 * @author Odgavaa
 */
public class CreateVehicleCollateral extends AbstractUseCase<CreateCollateralInput, String>
{
  private NewCoreBankingService coreBankingService;

  public CreateVehicleCollateral(NewCoreBankingService coreBankingService)
  {
    this.coreBankingService = coreBankingService;
  }

  @Override
  public String execute(CreateCollateralInput input) throws UseCaseException
  {

    if (null == input)
    {
      throw new UseCaseException(INVALID_INPUT_CODE, INVALID_INPUT_MESSAGE);
    }
    try
    {
      return coreBankingService.createVehicleCollateral(input.getGenericInfo(), input.getCollateralInfo(), input.getInspectionInfo(),
          input.getOwnershipInfo());
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage(), e);
    }
  }
}
