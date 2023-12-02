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
import mn.erin.domain.bpm.model.process.Process;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProcessRepository;

/**
 * @author Zorig
 */
public class GetProcessByNameAndEntityType extends AbstractUseCase<GetProcessByNameAndEntityTypeInput, Process>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateCollateralProcessParameters.class);

  private final ProcessRepository processRepository;

  public GetProcessByNameAndEntityType(ProcessRepository processRepository)
  {
    this.processRepository = Objects.requireNonNull(processRepository, "ProcessRepository is required!");
  }

  @Override
  public Process execute(GetProcessByNameAndEntityTypeInput input) throws UseCaseException
  {
    if (input == null || StringUtils.isBlank(input.getParameterName()))
    {
      LOGGER.error("Invalid input (null or blank)!");
      throw new UseCaseException(BpmMessagesConstants.INVALID_INPUT_CODE, BpmMessagesConstants.INVALID_INPUT_MESSAGE);
    }

    try
    {

      return processRepository.filterByParameterNameAndEntityType(input.getParameterName(), input.getParameterEntityType());
    }
    catch (BpmRepositoryException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
  }
}
