package mn.erin.domain.bpm.usecase;

import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.BpmModulePermission;
import mn.erin.domain.bpm.model.process.ProcessRequestId;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;

/**
 * @author Lkhagvadorj.A
 **/

public class DeleteProcessRequest extends AuthorizedUseCase<String, Void>
{
  private static final Permission permission = new BpmModulePermission("DeleteProcess");
  private final ProcessRequestRepository processRequestRepository;

  public DeleteProcessRequest(AuthenticationService authenticationService, AuthorizationService authorizationService,
      ProcessRequestRepository processRequestRepository)
  {
    super(authenticationService, authorizationService);
    this.processRequestRepository = processRequestRepository;
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected Void executeImpl(String input) throws UseCaseException
  {
    processRequestRepository.deleteProcessRequest(ProcessRequestId.valueOf(input));
    return null;
  }
}
