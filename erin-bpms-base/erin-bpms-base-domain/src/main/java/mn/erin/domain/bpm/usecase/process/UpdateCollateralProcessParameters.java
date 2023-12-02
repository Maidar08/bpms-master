/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.domain.bpm.usecase.process;

import java.io.Serializable;
import java.util.Collections;
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
import mn.erin.domain.bpm.repository.ProcessRepository;

/**
 * @author Zorig
 */
public class UpdateCollateralProcessParameters extends AbstractUseCase<UpdateCollateralProcessParametersInput, UpdateProcessParametersOutput>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateCollateralProcessParameters.class);

  private final ProcessRepository processRepository;

  public UpdateCollateralProcessParameters(ProcessRepository processRepository)
  {
    this.processRepository = Objects.requireNonNull(processRepository, "ProcessRepository is required!");
  }

  @Override
  public UpdateProcessParametersOutput execute(UpdateCollateralProcessParametersInput input) throws UseCaseException
  {
    if (input == null || StringUtils.isBlank(input.getProcessInstanceId()) || StringUtils.isBlank(input.getCollateralId()))
    {
      LOGGER.error("Invalid input (null or blank)!");
      throw new UseCaseException(BpmMessagesConstants.INVALID_INPUT_CODE, BpmMessagesConstants.INVALID_INPUT_MESSAGE);
    }

    ProcessUtils.validateProcessParameters(input.getParameters());

    String processInstanceId = input.getProcessInstanceId();
    String collateralId = input.getCollateralId();
    Map<String, Serializable> udfFields = input.getParameters().get(ParameterEntityType.UD_FIELD_COLLATERAL);

    JSONObject udfFieldsJSON = new JSONObject();

    for (Map.Entry<String, Serializable> udfField : udfFields.entrySet())
    {
      String udFieldKey = udfField.getKey();
      String udFieldValue = (String) udfField.getValue();

      udfFieldsJSON.put(udFieldKey, udFieldValue);
    }

    String udFieldsJSONString = udfFieldsJSON.toString();

    Map<String, Serializable> updateParametersMap = Collections.singletonMap(collateralId, udFieldsJSONString);

    UpdateProcessParametersInput updateProcessParametersInput = new UpdateProcessParametersInput(processInstanceId, Collections.singletonMap(ParameterEntityType.UD_FIELD_COLLATERAL, updateParametersMap));

    UpdateProcessParametersByName updateProcessParametersByName = new UpdateProcessParametersByName(processRepository);

    return updateProcessParametersByName.execute(updateProcessParametersInput);
  }
}
