/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.execution;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.cases.Execution;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.ExecutionService;

import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_MESSAGE;

/**
 * @author Tamir
 */
public class GetActiveExecutions implements UseCase<String, GetActiveExecutionsOutput>
{
  private final ExecutionService executionService;

  public GetActiveExecutions(ExecutionService executionService)
  {
    this.executionService = Objects.requireNonNull(executionService, "Execution service is required!");
  }

  @Override
  public GetActiveExecutionsOutput execute(String instanceId) throws UseCaseException
  {
    if (StringUtils.isBlank(instanceId))
    {
      throw new UseCaseException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
    }

    try
    {
      List<Execution> executions = executionService.getActiveByInstanceId(instanceId);
      return new GetActiveExecutionsOutput(executions);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage(), e);
    }
  }
}
