/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.domain.bpm.usecase.process.collateral;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmMessagesConstants;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.model.process.Process;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.usecase.process.GetProcessByNameAndEntityType;
import mn.erin.domain.bpm.usecase.process.GetProcessByNameAndEntityTypeInput;

/**
 * @author Zorig
 */
public class GetSavedCollateralUDFields extends AbstractUseCase<GetSavedCollateralUDFieldsInput, GetSavedCollateralUDFieldsOutput>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GetSavedCollateralUDFields.class);

  private final ProcessRepository processRepository;

  public GetSavedCollateralUDFields(ProcessRepository processRepository)
  {
    this.processRepository = Objects.requireNonNull(processRepository, "ProcessRepository is required!");
  }

  @Override
  public GetSavedCollateralUDFieldsOutput execute(GetSavedCollateralUDFieldsInput input) throws UseCaseException
  {
    if (input == null || StringUtils.isBlank(input.getCollateralId()))
    {
      LOGGER.error("Invalid input (null or blank)!");
      throw new UseCaseException(BpmMessagesConstants.INVALID_INPUT_CODE, BpmMessagesConstants.INVALID_INPUT_MESSAGE);
    }
    Map<String, Serializable> udFieldsToReturn = new HashMap<>();

    String collateralId = input.getCollateralId();

    GetProcessByNameAndEntityType getProcessByNameAndEntityType = new GetProcessByNameAndEntityType(processRepository);
    GetProcessByNameAndEntityTypeInput getProcessByNameAndEntityTypeInput = new GetProcessByNameAndEntityTypeInput(collateralId, ParameterEntityType.UD_FIELD_COLLATERAL);
    Process filteredProcess = getProcessByNameAndEntityType.execute(getProcessByNameAndEntityTypeInput);

    if (filteredProcess == null)
    {
      return new GetSavedCollateralUDFieldsOutput(Collections.emptyMap());
    }

    Map<String, Serializable> parameters = filteredProcess.getProcessParameters().get(ParameterEntityType.UD_FIELD_COLLATERAL);

    if (parameters == null)
    {
      return new GetSavedCollateralUDFieldsOutput(Collections.emptyMap());
    }

    if (parameters.get(collateralId) == null)
    {
      return new GetSavedCollateralUDFieldsOutput(Collections.emptyMap());
    }

    String udFieldsJSONString = (String) parameters.get(collateralId);

    JSONObject udFieldsJSON = new JSONObject(udFieldsJSONString);

    Map<String, Object> udFieldsMap = udFieldsJSON.toMap();

    for (Map.Entry<String, Object> udField : udFieldsMap.entrySet())
    {
      String udFieldKey = udField.getKey();
      String udFieldValue = (String) udField.getValue();

      udFieldsToReturn.put(udFieldKey, udFieldValue);
    }

    return new GetSavedCollateralUDFieldsOutput(udFieldsToReturn);
  }
}
