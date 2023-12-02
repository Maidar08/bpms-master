/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.domain.bpm.usecase.process;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmMessagesConstants;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProcessRepository;

/**
 * @author Zorig
 */
public class UpdateProcessParametersByName extends AbstractUseCase<UpdateProcessParametersInput, UpdateProcessParametersOutput>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateProcessParametersByName.class);
  private final ProcessRepository processRepository;

  public UpdateProcessParametersByName(ProcessRepository processRepository)
  {
    this.processRepository = Objects.requireNonNull(processRepository, "ProcessRepository is required!");
  }

  @Override
  public UpdateProcessParametersOutput execute(UpdateProcessParametersInput input) throws UseCaseException
  {
    if (input == null || StringUtils.isBlank(input.getProcessInstanceId()))
    {
      LOGGER.error("Invalid input (null or blank)!");
      throw new UseCaseException(BpmMessagesConstants.INVALID_INPUT_CODE, BpmMessagesConstants.INVALID_INPUT_MESSAGE);
    }

    try
    {
      ProcessUtils.validateProcessParameters(input.getParameters());

      int numUpdated = processRepository.updateParametersByName(input.getProcessInstanceId(), input.getParameters());
      return new UpdateProcessParametersOutput(numUpdated);
    }
    catch (BpmRepositoryException e)
    {
      LOGGER.error(e.getMessage());
      throw new UseCaseException(e.getMessage());
    }
  }
}
