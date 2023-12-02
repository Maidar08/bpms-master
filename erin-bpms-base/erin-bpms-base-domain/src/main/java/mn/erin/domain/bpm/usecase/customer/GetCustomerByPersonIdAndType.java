package mn.erin.domain.bpm.usecase.customer;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.customer.Customer;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.NewCoreBankingService;

import static mn.erin.domain.bpm.BpmNewCoreMessagesConstants.CUSTOMER_NOT_FOUND_ERROR_CODE;
import static mn.erin.domain.bpm.BpmNewCoreMessagesConstants.CUSTOMER_NOT_FOUND_ERROR_MESSAGE;
import static mn.erin.domain.bpm.BpmNewCoreMessagesConstants.CUSTOMER_REGISTER_NUMBER_IS_NULL_ERROR_CODE;
import static mn.erin.domain.bpm.BpmNewCoreMessagesConstants.CUSTOMER_REGISTER_NUMBER_IS_NULL_ERROR_MESSAGE;
import static mn.erin.domain.bpm.BpmNewCoreMessagesConstants.CUSTOMER_TYPE_IS_NULL_ERROR_CODE;
import static mn.erin.domain.bpm.BpmNewCoreMessagesConstants.CUSTOMER_TYPE_IS_NULL_ERROR_MESSAGE;

public class GetCustomerByPersonIdAndType extends AbstractUseCase<GetCustomerByPersonIdAndTypeInput, Customer>
{
  private final NewCoreBankingService newCoreBankingService;

  public GetCustomerByPersonIdAndType(NewCoreBankingService newCoreBankingService)
  {
    this.newCoreBankingService = Objects.requireNonNull(newCoreBankingService, "New core banking service must not be null");
  }

  @Override
  public Customer execute(GetCustomerByPersonIdAndTypeInput input) throws UseCaseException
  {
    validateInput(input);

    Customer customer;

    try
    {
      customer = newCoreBankingService.findCustomerByPersonIdAndType(input.getRegNumber(), input.getCustType());
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage(), e);
    }

    if (null == customer)
    {
      throw new UseCaseException(CUSTOMER_NOT_FOUND_ERROR_CODE, CUSTOMER_NOT_FOUND_ERROR_MESSAGE);
    }

    return customer;
  }

  private void validateInput(GetCustomerByPersonIdAndTypeInput input) throws UseCaseException
  {
    if (StringUtils.isBlank(input.getCustType()))
    {
      throw new UseCaseException(CUSTOMER_TYPE_IS_NULL_ERROR_CODE, CUSTOMER_TYPE_IS_NULL_ERROR_MESSAGE);
    }
    else if (StringUtils.isBlank(input.getRegNumber()))
    {
      throw new UseCaseException(CUSTOMER_REGISTER_NUMBER_IS_NULL_ERROR_CODE, CUSTOMER_REGISTER_NUMBER_IS_NULL_ERROR_MESSAGE);
    }
  }
}
