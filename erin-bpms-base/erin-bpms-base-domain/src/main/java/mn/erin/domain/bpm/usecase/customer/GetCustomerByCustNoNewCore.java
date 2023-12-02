package mn.erin.domain.bpm.usecase.customer;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.customer.Customer;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.NewCoreBankingService;

import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CUSTOMER;
import static mn.erin.domain.bpm.BpmNewCoreMessagesConstants.CUSTOMER_CIF_IS_NULL_ERROR_CODE;
import static mn.erin.domain.bpm.BpmNewCoreMessagesConstants.CUSTOMER_CIF_IS_NULL_ERROR_MESSAGE;
import static mn.erin.domain.bpm.BpmNewCoreMessagesConstants.CUSTOMER_NOT_FOUND_ERROR_CODE;

public class GetCustomerByCustNoNewCore extends AbstractUseCase<Map<String, String>, Customer>
{

  private final NewCoreBankingService coreBankingService;

  public GetCustomerByCustNoNewCore(NewCoreBankingService coreBankingService)
  {
    this.coreBankingService = Objects.requireNonNull(coreBankingService, "Core banking service is required!");
  }

  @Override
  public Customer execute(Map<String, String> input) throws UseCaseException
  {
    if (StringUtils.isBlank(input.get(CIF_NUMBER)))
    {
      throw new UseCaseException(CUSTOMER_CIF_IS_NULL_ERROR_CODE, CUSTOMER_CIF_IS_NULL_ERROR_MESSAGE);
    }

    Customer customer;
    try
    {
      Map<String, Object> customerMap = coreBankingService.findCustomerByCifNumber(input);
      customer = (Customer) customerMap.get(CUSTOMER);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage(), e);
    }

    if (null == customer)
    {
      throw new UseCaseException(CUSTOMER_NOT_FOUND_ERROR_CODE, "Customer with customer number [" + input.get(CIF_NUMBER) + "] not found!");
    }
    return customer;
  }
}



