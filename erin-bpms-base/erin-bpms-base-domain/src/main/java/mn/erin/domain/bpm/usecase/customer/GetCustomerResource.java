package mn.erin.domain.bpm.usecase.customer;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CoreBankingService;

/**
 * @author Zorig
 */
public class GetCustomerResource extends AbstractUseCase<String, Double>
{
  private final CoreBankingService coreBankingService;

  public GetCustomerResource(CoreBankingService coreBankingService)
  {
    this.coreBankingService = Objects.requireNonNull(coreBankingService, "Core banking service is required!");
  }

  @Override
  public Double execute(String input) throws UseCaseException
  {
    if (StringUtils.isBlank(input))
    {
      String errorCode = "CBS011";
      throw new UseCaseException(errorCode, "Invalid Customer CIF Input!");
    }

    try
    {
      String resource = coreBankingService.getCustomerResource(input);
      return Double.valueOf(resource);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage(), e);
    }
  }
}
