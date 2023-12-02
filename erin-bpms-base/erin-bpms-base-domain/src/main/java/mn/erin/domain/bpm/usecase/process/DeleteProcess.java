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
public class DeleteProcess extends AuthorizedUseCase<DeleteProcessInput, DeleteProcessOutput>
{
  private static final Permission permission = new BpmModulePermission("DeleteProcess");
  private final ProcessRepository processRepository;

  public DeleteProcess()
  {
    super();
    this.processRepository = null;
  }

  public DeleteProcess(AuthenticationService authenticationService,
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
  protected DeleteProcessOutput executeImpl(DeleteProcessInput input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException("Input must not be null!");
    }

    try
    {
      boolean isDeleted = processRepository.deleteProcess(input.getProcessInstanceId());
      return new DeleteProcessOutput(isDeleted);
    }
    catch (BpmRepositoryException e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }

}
