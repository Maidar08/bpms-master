/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.cases.get_cases;

import java.util.Objects;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.cases.Case;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CaseService;

/**
 * @author Tamir
 */
public class GetCaseByTaskId implements UseCase<String, Case>
{
  private final CaseService caseService;

  public GetCaseByTaskId(CaseService caseService)
  {
    this.caseService = Objects.requireNonNull(caseService,"Case service is null!");
  }

  @Override
  public Case execute(String taskId) throws UseCaseException
  {
    try
    {
      return caseService.findByTaskId(taskId);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage(), e);
    }
  }
}
