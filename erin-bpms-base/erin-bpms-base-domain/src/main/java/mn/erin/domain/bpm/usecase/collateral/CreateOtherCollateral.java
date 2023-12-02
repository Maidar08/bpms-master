package mn.erin.domain.bpm.usecase.collateral;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.NewCoreBankingService;

import static mn.erin.domain.bpm.BpmMessagesConstants.INVALID_INPUT_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INVALID_INPUT_MESSAGE;

public class CreateOtherCollateral extends AbstractUseCase<CreateCollateralInput, String>
{
  public final NewCoreBankingService newCoreBankingService;

  public CreateOtherCollateral(NewCoreBankingService newCoreBankingService)
  {
    this.newCoreBankingService = newCoreBankingService;
  }

  @Override
  public String execute(CreateCollateralInput input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException(INVALID_INPUT_CODE, INVALID_INPUT_MESSAGE);
    }
    try
    {
      return newCoreBankingService.createOtherCollateral(input.getGenericInfo(), input.getCollateralInfo(), input.getInspectionInfo(), input.getOwnershipInfo());
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
  }
}
