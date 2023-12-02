package mn.erin.domain.bpm.usecase.process;

import java.util.Objects;

import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.BpmModulePermission;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProcessRepository;

/**
 * @author Zorig
 */
public class UpdateProcessFinishedDate extends AuthorizedUseCase<UpdateProcessFinishedDateInput, UpdateProcessFinishedDateOutput>
{
  private static final Permission permission = new BpmModulePermission("UpdateProcessFinishedDate");
  private final ProcessRepository processRepository;

  public UpdateProcessFinishedDate()
  {
    super();
    this.processRepository = null;
  }

  public UpdateProcessFinishedDate(AuthenticationService authenticationService,
      AuthorizationService authorizationService, ProcessRepository processRepository)
  {
    super(authenticationService, authorizationService);
    this.processRepository = Objects.requireNonNull(processRepository, "ProcessRepository is required!");
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected UpdateProcessFinishedDateOutput executeImpl(UpdateProcessFinishedDateInput input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException("Input must not be null!");
    }

    try
    {
      boolean isUpdated = processRepository.updateFinishedDate(input.getProcessInstanceId(), input.getFinishedDate());
      return new UpdateProcessFinishedDateOutput(isUpdated);
    }
    catch (BpmRepositoryException e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }
}
