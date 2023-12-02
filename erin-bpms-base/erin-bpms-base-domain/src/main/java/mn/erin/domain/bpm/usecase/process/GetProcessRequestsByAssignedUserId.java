package mn.erin.domain.bpm.usecase.process;

import java.util.Collection;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.BpmModulePermission;
import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;

import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;

/**
 * @author Zorig
 */
public class GetProcessRequestsByAssignedUserId extends AuthorizedUseCase<GetProcessRequestsByAssignedUserIdInput, GetProcessRequestsOutput>
{
  private static final Permission permission = new BpmModulePermission("GetProcessRequestsByAssignedUserId");
  private final ProcessRequestRepository processRequestRepository;

  public GetProcessRequestsByAssignedUserId(AuthenticationService authenticationService,
      AuthorizationService authorizationService, ProcessRequestRepository processRequestRepository)
  {
    super(authenticationService, authorizationService);
    this.processRequestRepository = Objects.requireNonNull(processRequestRepository, "Process Request Repository is required!");
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected GetProcessRequestsOutput executeImpl(GetProcessRequestsByAssignedUserIdInput input) throws UseCaseException
  {
    if (input == null || StringUtils.isBlank(input.getAssignedUserId()))
    {
      throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
    }

    try
    {
      Collection<ProcessRequest> processRequests = processRequestRepository.getRequestsByAssignedUserId(input.getAssignedUserId(), input.getStartDate(), input.getEndDate());
      return new GetProcessRequestsOutput(processRequests);
    }
    catch (NullPointerException | IllegalArgumentException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}
