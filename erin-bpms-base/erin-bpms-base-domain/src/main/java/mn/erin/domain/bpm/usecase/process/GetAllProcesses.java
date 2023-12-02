package mn.erin.domain.bpm.usecase.process;

import java.util.Collection;
import java.util.Objects;

import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.BpmModulePermission;
import mn.erin.domain.bpm.model.process.Process;
import mn.erin.domain.bpm.repository.ProcessRepository;

/**
 * @author Zorig
 */
public class GetAllProcesses extends AuthorizedUseCase<Void, GetAllProcessesOutput>
{
  private static final Permission permission = new BpmModulePermission("GetAllProcesses");
  private final ProcessRepository processRepository;

  public GetAllProcesses()
  {
    super();
    this.processRepository = null;
  }

  public GetAllProcesses(AuthenticationService authenticationService,
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
  protected GetAllProcessesOutput executeImpl(Void input) throws UseCaseException
  {
    try
    {
      Collection<Process> allProcesses = processRepository.findAll();
      return new GetAllProcessesOutput(allProcesses);
    }
    catch (NullPointerException | IllegalArgumentException e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }

}
