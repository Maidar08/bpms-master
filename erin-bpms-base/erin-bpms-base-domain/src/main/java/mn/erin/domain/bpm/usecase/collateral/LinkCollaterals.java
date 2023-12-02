package mn.erin.domain.bpm.usecase.collateral;

import java.util.Map;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.NewCoreBankingService;

/**
 * @author Tamir
 */
public class LinkCollaterals extends AbstractUseCase<LinkCollateralsInput, LinkCollateralsOutput>
{
  private final NewCoreBankingService newCoreBankingService;

  public LinkCollaterals(NewCoreBankingService newCoreBankingService)
  {
    this.newCoreBankingService = newCoreBankingService;
  }

  @Override
  public LinkCollateralsOutput execute(LinkCollateralsInput input) throws UseCaseException
  {
    String accountNumber = input.getAccountNumber();
    String linkageType = input.getLinkageType();

    Map<String, Object> collaterals = input.getCollaterals();

    try
    {
      boolean isLinked = newCoreBankingService.linkCollaterals(accountNumber, linkageType, collaterals);

      return new LinkCollateralsOutput(isLinked);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage(), e.getCause());
    }
  }
}
