/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.execution;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.cases.Execution;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.ExecutionService;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;

/**
 * @author Tamir
 */
public class GetEnabledExecutions implements UseCase<GetEnabledExecutionsInput, GetEnabledExecutionsOutput>
{
  private final ExecutionService executionService;

  public GetEnabledExecutions(ExecutionService executionService)
  {
    this.executionService = executionService;
  }

  @Override
  public GetEnabledExecutionsOutput execute(GetEnabledExecutionsInput input) throws UseCaseException
  {
    if (null == input)
    {
      throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
    }

    String instanceId = input.getInstanceId();

    try
    {
      List<Execution> enabledExecutions = executionService.getEnabledByInstanceId(instanceId);

      return new GetEnabledExecutionsOutput(getUniqueExecutions(enabledExecutions));
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage(), e);
    }
  }

  public List<Execution> getUniqueExecutions(List<Execution> enabledExecutions)
  {
    return enabledExecutions.stream()
        .collect(collectingAndThen(toCollection(() -> new TreeSet<>(comparing(Execution::getActivityName))),
            ArrayList::new));
  }
}
