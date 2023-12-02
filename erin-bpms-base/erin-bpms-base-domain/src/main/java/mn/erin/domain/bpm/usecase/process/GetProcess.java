package mn.erin.domain.bpm.usecase.process;

import java.util.Objects;

import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.BpmModulePermission;
import mn.erin.domain.bpm.model.process.Process;
import mn.erin.domain.bpm.model.process.ProcessInstanceId;
import mn.erin.domain.bpm.repository.ProcessRepository;

/**
 * @author Zorig
 */
public class GetProcess extends AuthorizedUseCase<GetProcessInput, GetProcessOutput>
{
  private static final Permission permission = new BpmModulePermission("GetProcess");
  private final ProcessRepository processRepository;

  public GetProcess()
  {
    super();
    this.processRepository = null;
  }

  public GetProcess(AuthenticationService authenticationService, AuthorizationService authorizationService,
      ProcessRepository processRepository)
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
  protected GetProcessOutput executeImpl(GetProcessInput input) throws UseCaseException
  {
    if (input == null)
    {
      String errorCode = "BPMS020";
      throw new UseCaseException(errorCode, "Input must not be null!");
    }
    try
    {
      Process returnedProcess = processRepository.findById(new ProcessInstanceId(input.getProcessInstanceId()));
      return new GetProcessOutput(returnedProcess);
    }
    catch (NullPointerException | IllegalArgumentException e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }

}
