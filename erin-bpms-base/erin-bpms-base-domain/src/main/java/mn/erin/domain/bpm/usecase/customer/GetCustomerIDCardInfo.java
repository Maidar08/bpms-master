/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.customer;

import java.util.Objects;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.customer.Customer;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CustomerService;

import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;

/**
 * @author Tamir
 */
public class GetCustomerIDCardInfo extends AbstractUseCase<GetCustomerIDCardInfoInput, Customer>
{
  private final CustomerService customerService;

  public GetCustomerIDCardInfo(CustomerService customerService)
  {
    this.customerService = Objects.requireNonNull(customerService, "Customer service is required!");
  }

  @Override
  public Customer execute(GetCustomerIDCardInfoInput input) throws UseCaseException
  {
    if (null == input)
    {
      throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
    }

    String regNumber = input.getRegNumber();
    String employeeRegNumber = input.getEmployeeRegNumber();

    try
    {
      return customerService.getCustomerInfo(regNumber, employeeRegNumber);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage(), e);
    }
  }
}
