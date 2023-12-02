/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.execution;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.cases.Execution;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.ExecutionService;

/**
 * @author Tamir
 */
public class GetCompletedExecutions implements UseCase<String, GetCompletedExecutionsOutput>
{
  private final ExecutionService executionService;

  public GetCompletedExecutions(ExecutionService executionService)
  {
    this.executionService = Objects.requireNonNull(executionService, "Execution service is required!");
  }

  @Override
  public GetCompletedExecutionsOutput execute(String instanceId) throws UseCaseException
  {
    if (null == instanceId)
    {
      String errorCode = "CamundaExecutionSrvice001";
      throw new UseCaseException(errorCode, "Instance id is cannot be null!");
    }

    try
    {
      List<Execution> enabledExecutions = executionService.getCompletedByInstanceId(instanceId);
      List<Execution> filteredExecutions = filterFromDuplicated(enabledExecutions);

      return new GetCompletedExecutionsOutput(filteredExecutions);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage(), e);
    }
  }

  private List<Execution> filterFromDuplicated(List<Execution> enabledExecutions)
  {
    List<Execution> filteredExecutions = new ArrayList<>();
    List<String> activityNameList = new ArrayList<>();

    for (Execution enabledExecution : enabledExecutions)
    {
      String activityName = enabledExecution.getActivityName();

      if (!activityNameList.contains(activityName))
      {
        activityNameList.add(activityName);
        filteredExecutions.add(enabledExecution);
      }
    }
    return filteredExecutions;
  }
}
