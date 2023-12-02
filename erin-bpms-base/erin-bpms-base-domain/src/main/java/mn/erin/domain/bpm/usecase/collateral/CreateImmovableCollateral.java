package mn.erin.domain.bpm.usecase.collateral;

import java.util.Map;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.NewCoreBankingService;

/**
 * @author Tamir
 */
public class CreateImmovableCollateral extends AbstractUseCase<CreateCollateralInput, String>
{
  private final NewCoreBankingService newCoreBankingService;

  public CreateImmovableCollateral(NewCoreBankingService newCoreBankingService)
  {
    this.newCoreBankingService = newCoreBankingService;
  }

  @Override
  public String execute(CreateCollateralInput input) throws UseCaseException
  {
    Map<String, Object> genericInfo = input.getGenericInfo();
    Map<String, Object> collateralInfo = input.getCollateralInfo();

    Map<String, Object> inspectionInfo = input.getInspectionInfo();
    Map<String, Object> ownershipInfo = input.getOwnershipInfo();

    try
    {
      return newCoreBankingService.createImmovableCollateral(genericInfo, collateralInfo, inspectionInfo, ownershipInfo);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
  }
}
