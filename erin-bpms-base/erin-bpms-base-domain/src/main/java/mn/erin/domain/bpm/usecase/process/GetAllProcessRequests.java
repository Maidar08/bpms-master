package mn.erin.domain.bpm.usecase.process;

import java.util.Collection;
import java.util.Objects;

import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.BpmModulePermission;
import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.usecase.process.manual_activate.GetAllProcessRequestsOutput;

/**
 * @author Zorig
 */
public class GetAllProcessRequests extends AuthorizedUseCase<Void, GetAllProcessRequestsOutput>
{
  private static final Permission permission = new BpmModulePermission("GetAllProcessRequests");
  private final ProcessRequestRepository processRequestRepository;

  public GetAllProcessRequests()
  {
    super();
    this.processRequestRepository = null;
  }

  public GetAllProcessRequests(AuthenticationService authenticationService,
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
  protected GetAllProcessRequestsOutput executeImpl(Void input) throws UseCaseException
  {
    try
    {
      Collection<ProcessRequest> returnedProcessRequests = processRequestRepository.findAll();
      return new GetAllProcessRequestsOutput(returnedProcessRequests);
    }
    catch (Exception e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}
