package mn.erin.domain.bpm.usecase.process;

import java.util.Objects;

import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.BpmModulePermission;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;

/**
 * @author Zorig
 */
public class UpdateAssignedUser extends AuthorizedUseCase<UpdateAssignedUserInput, UpdateAssignedUserOutput>
{
  private static final Permission permission = new BpmModulePermission("UpdateAssignedUser");
  private final ProcessRequestRepository processRequestRepository;

  public UpdateAssignedUser()
  {
    super();
    this.processRequestRepository = null;
  }

  public UpdateAssignedUser(AuthenticationService authenticationService, AuthorizationService authorizationService,
      ProcessRequestRepository processRequestRepository)
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
  protected UpdateAssignedUserOutput executeImpl(UpdateAssignedUserInput input) throws UseCaseException
  {
    if (input == null)
    {
      String errorCode = "BPMS020";
      throw new UseCaseException(errorCode, "Input must not be null");
    }

    try
    {
      boolean isUpdated = processRequestRepository.updateAssignedUser(input.getProcessRequestId(), new UserId(input.getAssignedUser()));
      return new UpdateAssignedUserOutput(isUpdated);
    }
    catch (NullPointerException | IllegalArgumentException | BpmRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}
