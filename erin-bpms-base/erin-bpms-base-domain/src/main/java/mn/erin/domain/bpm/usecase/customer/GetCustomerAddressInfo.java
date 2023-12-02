/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.customer;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.model.person.AddressInfo;
import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CustomerService;

/**
 * @author Tamir
 */
public class GetCustomerAddressInfo extends AbstractUseCase<GetCustomerIDCardInfoInput, AddressInfo>
{
  private final CustomerService customerService;

  private static final String REGISTER_REG_EXP = "[ӨөҮүЁёА-Яа-я]{2}[0-9]{8}";

  public GetCustomerAddressInfo(CustomerService customerService)
  {
    this.customerService = Objects.requireNonNull(customerService, "Customer service is required!");
  }

  @Override
  public AddressInfo execute(GetCustomerIDCardInfoInput input) throws UseCaseException
  {
    if (null == input)
    {
      String errorCode = "BPMS020";
      throw new UseCaseException(errorCode, "Input cannot be null!");
    }

    String regNumber = input.getRegNumber();
    String employeeRegNumber = input.getEmployeeRegNumber();

    checkRegisterNumber(regNumber);
    checkRegisterNumber(employeeRegNumber);

    try
    {
      return customerService.getCustomerAddress(regNumber, employeeRegNumber);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage(), e);
    }
  }
  private void checkRegisterNumber(String registerNumber) throws UseCaseException
  {
    String errorCode = "XYP001";
    String errorDesc = "Регистерийн дугаар буруу байна!";
    if (StringUtils.isBlank(registerNumber) || !registerNumber.matches(REGISTER_REG_EXP))
    {
      throw new UseCaseException(errorCode, errorDesc);
    }
  }
}
