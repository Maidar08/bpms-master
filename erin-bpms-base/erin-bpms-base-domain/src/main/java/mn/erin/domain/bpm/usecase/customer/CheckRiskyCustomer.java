package mn.erin.domain.bpm.usecase.customer;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.NewCoreBankingService;

import static mn.erin.domain.bpm.BpmNewCoreMessagesConstants.CUSTOMER_REGISTER_NUMBER_IS_NULL_ERROR_CODE;
import static mn.erin.domain.bpm.BpmNewCoreMessagesConstants.CUSTOMER_REGISTER_NUMBER_IS_NULL_ERROR_MESSAGE;

/**
 * @author EBazarragchaa
 */
public class CheckRiskyCustomer extends AbstractUseCase<String, Boolean>
{
  private final NewCoreBankingService coreBankingService;

  public CheckRiskyCustomer(NewCoreBankingService coreBankingService)
  {
    this.coreBankingService = Objects.requireNonNull(coreBankingService, "Core banking service is required!");
  }

  @Override
  public Boolean execute(String personId) throws UseCaseException
  {
    if (StringUtils.isBlank(personId))
    {
      throw new UseCaseException( CUSTOMER_REGISTER_NUMBER_IS_NULL_ERROR_CODE,  CUSTOMER_REGISTER_NUMBER_IS_NULL_ERROR_MESSAGE);
    }

    try
    {
      return coreBankingService.checkRiskyCustomer(personId);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage(), e);
    }
  }
}
