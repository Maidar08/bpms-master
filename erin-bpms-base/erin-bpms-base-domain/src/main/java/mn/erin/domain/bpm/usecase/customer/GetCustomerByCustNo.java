package mn.erin.domain.bpm.usecase.customer;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.customer.Customer;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CoreBankingService;

/**
 * @author EBazarragchaa
 */
public class GetCustomerByCustNo extends AbstractUseCase<String, Customer>
{
  private final CoreBankingService coreBankingService;

  public GetCustomerByCustNo(CoreBankingService coreBankingService)
  {
    this.coreBankingService = Objects.requireNonNull(coreBankingService, "Core banking service is required!");
  }

  @Override
  public Customer execute(String customerNumber) throws UseCaseException
  {
    if (StringUtils.isBlank(customerNumber))
    {
      String errorCode = "CBS008";
      throw new UseCaseException(errorCode, "Customer number is missing!");
    }

    Customer customer = null;
    try
    {
      customer = coreBankingService.findCustomerByCifNumber(customerNumber);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage(), e);
    }

    if (null == customer)
    {
      String errorCode = "CBS009";
      throw new UseCaseException(errorCode, "Customer with customer number [" + customerNumber + "] not found!");
    }

    return customer;
  }
}
