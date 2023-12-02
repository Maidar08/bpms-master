package mn.erin.domain.bpm.usecase.execution;

import java.util.Objects;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CaseService;

public class CompleteExecutionByActivityId implements UseCase<CompleteExecutionByActivityIdInput, Void>
{
  private final CaseService caseService;

  public CompleteExecutionByActivityId(CaseService caseService)
  {
    this.caseService = Objects.requireNonNull(caseService, "Case service is required!");
  }

  @Override
  public Void execute(CompleteExecutionByActivityIdInput input) throws UseCaseException
  {
    String caseInstanceId = input.getCaseInstanceId();
    String activityId = input.getActivityId();

    if (null != caseInstanceId && null != activityId)
    {
      try
      {
        caseService.completeByActivityId(caseInstanceId, activityId);
      }
      catch (BpmServiceException e)
      {
        throw new UseCaseException(e.getCode(), e.getMessage());
      }
    }
    return null;
  }
}
