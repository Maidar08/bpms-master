package mn.erin.domain.bpm.usecase.collateral;

import java.util.List;
import java.util.Objects;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.collateral.Collateral;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.NewCoreBankingService;

public class GetCollateralsByCustNumber extends AbstractUseCase<String, GetCollateralsByCustNumberOutput>
{
  private final NewCoreBankingService newCoreBankingService;

  public GetCollateralsByCustNumber(NewCoreBankingService newCoreBankingService)
  {
    this.newCoreBankingService = Objects.requireNonNull(newCoreBankingService, "New core banking service is required!");
  }

  @Override
  public GetCollateralsByCustNumberOutput execute(String cifNumber) throws UseCaseException
  {
    try
    {
      List<Collateral> collateralsTest = newCoreBankingService.getCollateralsByCifNumber(cifNumber);
      return new GetCollateralsByCustNumberOutput(collateralsTest);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
  }
}
