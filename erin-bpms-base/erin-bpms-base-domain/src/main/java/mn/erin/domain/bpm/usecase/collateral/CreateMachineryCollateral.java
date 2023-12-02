package mn.erin.domain.bpm.usecase.collateral;

import java.util.Map;
import java.util.Objects;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.NewCoreBankingService;

import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;

/**
 * @author Lkhagvadorj.A
 **/

public class CreateMachineryCollateral extends AbstractUseCase<CreateCollateralInput, String>
{
  private final NewCoreBankingService newCoreBankingService;

  public CreateMachineryCollateral(NewCoreBankingService newCoreBankingService)
  {
    this.newCoreBankingService = Objects.requireNonNull(newCoreBankingService, "New core banking service is required!");
  }

  @Override
  public String execute(CreateCollateralInput input) throws UseCaseException
  {
    if (null == input)
    {
      throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
    }

    Map<String, Object> genericInfo = input.getGenericInfo();
    Map<String, Object> collateralInfo = input.getCollateralInfo();
    Map<String, Object> inspectionInfo = input.getInspectionInfo();
    Map<String, Object> ownershipInfo = input.getOwnershipInfo();

    try
    {
      return newCoreBankingService.createMachineryCollateral(genericInfo, collateralInfo, inspectionInfo, ownershipInfo);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
  }
}
