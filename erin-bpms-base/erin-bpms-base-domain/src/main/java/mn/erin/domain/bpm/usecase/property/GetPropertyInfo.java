/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.domain.bpm.usecase.property;

import java.util.Objects;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.property.PropertyInfo;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CustomerService;

import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;

/**
 * @author Zorig
 */
public class GetPropertyInfo extends AbstractUseCase<GetPropertyInfoInput, PropertyInfo>
{
  private final CustomerService customerService;

  public GetPropertyInfo(CustomerService customerService)
  {
    this.customerService = Objects.requireNonNull(customerService, "Customer Service is required!");
  }

  @Override
  public PropertyInfo execute(GetPropertyInfoInput input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
    }

    try
    {
      return customerService.getPropertyInfo(input.getOperatorInfo(), input.getCitizenInfo(), input.getPropertyId());
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
  }
}
