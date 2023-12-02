/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.domain.bpm.usecase.customer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmMessagesConstants;
import mn.erin.domain.bpm.model.account.UDField;
import mn.erin.domain.bpm.model.account.UDFieldValue;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.NewCoreBankingService;

/**
 * @author Zorig
 */
public class GetUDFieldsByProductCode extends AbstractUseCase<Map<String, String>, GetUDFieldsByProductCodeOutput>
{
  private final NewCoreBankingService newCoreBankingService;

  public GetUDFieldsByProductCode(NewCoreBankingService newCoreBankingService)
  {
    this.newCoreBankingService = Objects.requireNonNull(newCoreBankingService, "New Core banking service is required!");
  }

  @Override
  public GetUDFieldsByProductCodeOutput execute(Map<String, String> input) throws UseCaseException
  {
    if (input.isEmpty())
    {
      throw new UseCaseException(BpmMessagesConstants.INVALID_INPUT_CODE, BpmMessagesConstants.INVALID_INPUT_MESSAGE);
    }

    try
    {
      Map<String, UDField> udFieldsMap = newCoreBankingService.getUDFields(input);
      return new GetUDFieldsByProductCodeOutput(formatUdfMap(udFieldsMap), StringUtils.EMPTY);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage(), e);
    }
  }

  private Map<String, UDField> formatUdfMap(Map<String, UDField> udFieldMap)
  {
    Map<String, UDField> returnMap = new HashMap<>();
    returnMap.put("AccountFreeCode2", udFieldMap.get("Account Free Code 2"));
    returnMap.put("AccountFreeCode3", udFieldMap.get("Account Free Code 3"));
    returnMap.put("TypeOfAdvance", udFieldMap.get("Type of Advance"));
    returnMap.put("BorrowerCategoryCode", udFieldMap.get("Borrower category Code"));
    returnMap.put("FREE_CODE_4", udFieldMap.get("FREE_CODE_4"));
    returnMap.put("FREE_CODE_5", udFieldMap.get("FREE_CODE_5"));
    returnMap.put("FREE_CODE_6", udFieldMap.get("FREE_CODE_6"));
    returnMap.put("FREE_CODE_7", udFieldMap.get("FREE_CODE_7"));
    returnMap.put("FREE_CODE_8", udFieldMap.get("FREE_CODE_8"));
    returnMap.put("FREE_CODE_9", udFieldMap.get("FREE_CODE_9"));
    returnMap.put("FREE_CODE_10", udFieldMap.get("FREE_CODE_10"));
    returnMap.put("NatureOfAdvance", udFieldMap.get("Nature of Advance"));
    returnMap.put("CustomerIndustryType", udFieldMap.get("Customer Industry Type"));
    returnMap.put("PurposeOfAdvance", udFieldMap.get("Purpose of Advance"));
    returnMap.put("AccountFreeCode1", udFieldMap.get("Account Free Code 1"));
    returnMap.put("FREE_TEXT_1", udFieldMap.get("sanctionedBy"));
    returnMap.put("modeOfAdvance", updateModeOfAdvanceUdField(udFieldMap.get("Mode of Advance")));
    return returnMap;
  }

  private String getSubTypeFieldName(Map<String, UDField> udFieldMap, String productCode) throws UseCaseException
  {
    for (Map.Entry<String, UDField> udFieldEntry : udFieldMap.entrySet())
    {
      String udFieldEntryKey = udFieldEntry.getKey();
      UDField udField = udFieldEntry.getValue();
      if (udFieldEntryKey.contains("SUBTYPE") && udField.getFieldType().equals("T"))
      {
        return udFieldEntryKey;
      }
    }

    throw new UseCaseException("SUBTYPE could not be found for " + productCode + " from UDF Fields.");
  }

  private UDField updateModeOfAdvanceUdField(UDField udField)
  {
    if (udField != null)
    {
      List<UDFieldValue> values = udField.getValues();
      List<UDFieldValue> newValues = new ArrayList<>();

      for (UDFieldValue udFieldValue : values)
      {
        String itemId = udFieldValue.getItemId();
        String itemDescription = udFieldValue.getItemDescription();
        boolean isDefaultValue = udFieldValue.isDefault();

        UDFieldValue newUdFieldValue = new UDFieldValue(itemId, itemId + "-" + itemDescription, isDefaultValue);
        newValues.add(newUdFieldValue);
      }

      udField.setValues(newValues);

      return udField;
    }

    return null;
  }
}
