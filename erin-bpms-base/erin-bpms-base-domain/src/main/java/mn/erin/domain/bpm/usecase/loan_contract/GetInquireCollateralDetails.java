package mn.erin.domain.bpm.usecase.loan_contract;

import java.util.List;
import java.util.Objects;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.loan_contract.LinkageCollateralInfo;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.NewCoreBankingService;

public class GetInquireCollateralDetails extends AbstractUseCase<GetInquireCollateralDetailsInput, List<LinkageCollateralInfo>>
{
  private final NewCoreBankingService newCoreBankingService;

  public GetInquireCollateralDetails(NewCoreBankingService newCoreBankingService)
  {
    this.newCoreBankingService = Objects.requireNonNull(newCoreBankingService, "Core banking service is required!");
  }

  @Override
  public List<LinkageCollateralInfo> execute(GetInquireCollateralDetailsInput input) throws UseCaseException
  {
    validateNotNull(input, "a");
    validateNotBlank(input.getEntityId(), "a");
    validateNotBlank(input.getEntityType(), "a");

    try
    {
      return newCoreBankingService.getInquireCollateralDetails(input.getEntityId(), input.getEntityType());
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
  }
}
