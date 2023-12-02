/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.domain.bpm;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CoreBankingService;
import mn.erin.domain.bpm.usecase.loan.CreateLoanAccountOutput;

/**
 * @author Zorig
 */
public class CreateCollateralLoanAccount extends AbstractUseCase<CreateCollateralLoanAccountInput, CreateLoanAccountOutput>
{
  private final CoreBankingService coreBankingService;

  public CreateCollateralLoanAccount(CoreBankingService coreBankingService)
  {
    this.coreBankingService = coreBankingService;
  }

  @Override
  public CreateLoanAccountOutput execute(CreateCollateralLoanAccountInput input) throws UseCaseException
  {
    //validate input more
    if (input == null)
    {
      throw new UseCaseException(BpmMessagesConstants.INVALID_INPUT_CODE, BpmMessagesConstants.INVALID_INPUT_MESSAGE);
    }

    try
    {
      Map<String, Object> accountCreationInformation = input.getAccountCreationInformation();
      Map<String, String> validAccountCreationInformation = validateAccountCreationInformation(accountCreationInformation);

      int accountNumber = coreBankingService
          .createCollateralLoanAccount(input.getProductCode(), validAccountCreationInformation, input.getCoBorrowers(), input.getCollaterals());
      return new CreateLoanAccountOutput(accountNumber);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage(), e);
    }
  }

  private Map<String, String> validateAccountCreationInformation(Map<String, Object> accountCreationInformation) throws UseCaseException
  {
    Map<String, String> validAccountCreationInformation = new HashMap<>();

    for (Map.Entry<String, Object> entry : accountCreationInformation.entrySet())
    {
      String key = entry.getKey();
      Object value = entry.getValue();

      if (value instanceof Date)
      {
        String date = dateToProperString((Date) value);
        validAccountCreationInformation.put(key, date);
      }
      else if (value instanceof String)
      {
        validAccountCreationInformation.put(key, (String) value);
      }
      else if (value instanceof Long)
      {
        validAccountCreationInformation.put(key, String.valueOf(value));
      }
      else if (value == null)
      {
        continue;
      }
      else
      {
        String errorCode = "CBS017";
        throw new UseCaseException(errorCode, "Invalid type for account creation information!" + "  : " + key + " : " + key.getClass());
      }
    }
    return validAccountCreationInformation;
  }

  private String dateToProperString(Date date)
  {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    return format.format(date);
  }
}
