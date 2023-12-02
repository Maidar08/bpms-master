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
public class GetCustomerByPersonId extends AbstractUseCase<String, Customer>
{
  private final CoreBankingService coreBankingService;

  public GetCustomerByPersonId(CoreBankingService coreBankingService)
  {
    this.coreBankingService = Objects.requireNonNull(coreBankingService, "Core banking service is required!");
  }

  @Override
  public Customer execute(String registerNumber) throws UseCaseException
  {
    if (StringUtils.isBlank(registerNumber))
    {
      String errorCode = "CBS004";
      throw new UseCaseException(errorCode, "Customer register number is missing!");
    }

    Customer customer = null;
    try
    {
      customer = coreBankingService.findCustomerByPersonId(registerNumber);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode() ,e.getMessage(), e);
    }

    if (null == customer)
    {
      String errorCode = "CBS005";
      throw new UseCaseException(errorCode, "Customer with register number [" + registerNumber + "] not found!");
    }

    return customer;
  }
}
