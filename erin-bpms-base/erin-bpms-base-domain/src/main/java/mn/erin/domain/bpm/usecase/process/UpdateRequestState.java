/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.process;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.model.process.ProcessRequestId;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;

/**
 * @author Tamir
 */
public class UpdateRequestState extends AbstractUseCase<UpdateRequestStateInput, UpdateRequestStateOutput>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateRequestState.class);

  private final ProcessRequestRepository processRequestRepository;

  public UpdateRequestState(ProcessRequestRepository processRequestRepository)
  {
    this.processRequestRepository = Objects.requireNonNull(processRequestRepository, "Process request repository is required!");
  }

  @Override
  public UpdateRequestStateOutput execute(UpdateRequestStateInput input) throws UseCaseException
  {
    if (null == input)
    {
      String errorCode = "BPMS072";
      throw new UseCaseException(errorCode, "Update request state input cannot be null!");
    }

    try
    {
      String processRequestId = input.getProcessRequestId();
      ProcessRequestState state = input.getState();

      LOGGER.info("############ Updates process requests state.");
      ProcessRequest processRequest = processRequestRepository.findById(ProcessRequestId.valueOf(processRequestId));

      if (null == processRequest)
      {
        throw new UseCaseException("Process request does not exist with id: " + processRequestId);
      }

      boolean isUpdated = processRequestRepository.updateState(processRequestId, state);

      if(isUpdated)
      {
        LOGGER.info("############## Successful updated PROCESS REQUEST STATE to {} with REQUEST ID = [{}]", ProcessRequestState.fromEnumToString(state), processRequestId);
      }
      else {
        LOGGER.warn("############## Unsuccessful updated PROCESS REQUEST STATE with REQUEST ID = [{}]", processRequestId);
      }
      return new UpdateRequestStateOutput(isUpdated);
    }
    catch (BpmRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}
