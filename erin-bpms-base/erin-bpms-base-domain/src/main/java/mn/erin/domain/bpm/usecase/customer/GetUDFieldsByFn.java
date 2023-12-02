/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties. 
 */

package mn.erin.domain.bpm.usecase.customer;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmMessagesConstants;
import mn.erin.domain.bpm.model.account.UDField;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CoreBankingService;

/**
 * @author Zorig
 */
public class GetUDFieldsByFn extends AbstractUseCase<String, GetUDFieldsByFnOutput>
{
  private final CoreBankingService coreBankingService;

  public GetUDFieldsByFn(CoreBankingService coreBankingService)
  {
    this.coreBankingService = Objects.requireNonNull(coreBankingService, "Core banking service is required!");
  }

  @Override
  public GetUDFieldsByFnOutput execute(String function) throws UseCaseException
  {
    if (StringUtils.isBlank(function))
    {
      throw new UseCaseException(BpmMessagesConstants.UD_FIELD_BY_FN_USECASE_EMPTY_FUNCTION_ERROR_CODE, BpmMessagesConstants.UD_FIELD_BY_FN_USECASE_EMPTY_FUNCTION_ERROR_MESSAGE);
    }

    try
    {
      Map<String, UDField> udFieldMap = coreBankingService.getUDFieldsByFunction(function);

      return new GetUDFieldsByFnOutput(udFieldMap);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
  }
}
