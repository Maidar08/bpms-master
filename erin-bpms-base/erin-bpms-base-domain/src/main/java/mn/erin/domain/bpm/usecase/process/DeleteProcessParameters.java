package mn.erin.domain.bpm.usecase.process;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmMessagesConstants;
import mn.erin.domain.bpm.model.BpmModulePermission;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProcessRepository;

/**
 * @author Tamir
 */
public class DeleteProcessParameters extends AuthorizedUseCase<DeleteProcessParametersInput, DeleteProcessParametersOutput>
{
  private static final Permission permission = new BpmModulePermission("DeleteProcessParameters");
  private final ProcessRepository processRepository;

  public DeleteProcessParameters(AuthenticationService authenticationService,
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
  protected DeleteProcessParametersOutput executeImpl(DeleteProcessParametersInput input) throws UseCaseException
  {
    if (input == null || StringUtils.isBlank(input.getProcessInstanceId()))
    {
      throw new UseCaseException(BpmMessagesConstants.INVALID_INPUT_CODE, BpmMessagesConstants.INVALID_INPUT_MESSAGE);
    }

    try
    {
      int numDeleted = processRepository.deleteProcessParameters(input.getProcessInstanceId(), input.getDeleteParameters());
      return new DeleteProcessParametersOutput(numDeleted);
    }
    catch (BpmRepositoryException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
  }
}
