package mn.erin.domain.bpm.usecase.process;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmMessagesConstants;
import mn.erin.domain.bpm.model.BpmModulePermission;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;

/**
 * @author Lkhagvadorj
 **/
public class RemoveProcess extends AuthorizedUseCase<RemoveProcessInput, RemoveProcessOutput>
{
  private static final Permission permission = new BpmModulePermission("DeleteProcess");
  private static final Logger LOGGER = LoggerFactory.getLogger(RemoveProcess.class);

  private final BpmsRepositoryRegistry bpmsRepositoryRegistry;

  public RemoveProcess(BpmsRepositoryRegistry bpmsRepositoryRegistry,
      AuthenticationService authenticationService,
      AuthorizationService authorizationService)
  {
    super(authenticationService, authorizationService);
    this.bpmsRepositoryRegistry = Objects.requireNonNull(bpmsRepositoryRegistry, "Repository registry is required!");
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected RemoveProcessOutput executeImpl(RemoveProcessInput input) throws UseCaseException
  {
    String currentUserId = authenticationService.getCurrentUserId();

    if (input == null)
    {
      throw new UseCaseException(BpmMessagesConstants.INPUT_NULL_CODE, BpmMessagesConstants.INPUT_NULL_MESSAGE);
    }

    String state = input.getProcessRequestState();

    String processInstanceId = input.getProcessInstanceId();
    String processRequestId = input.getProcessRequestId();

    deleteProcessFromProcessRequestRepository(processRequestId);
    deleteDocumentFromDocumentRepository(processInstanceId);

    LOGGER.info("########## Successfully deleted request with id = [{}], DELETED BY USER = [{}], STATE = [{}]",
        input.getProcessRequestId(), currentUserId, state);
    return new RemoveProcessOutput(true);
  }

  private void deleteProcessFromProcessRequestRepository(String processRequestId) throws UseCaseException
  {
    if (!StringUtils.isBlank(processRequestId))
    {
      UpdateRequestState updateRequestState = new UpdateRequestState(bpmsRepositoryRegistry.getProcessRequestRepository());
      UpdateRequestStateInput updateRequestStateInput = new UpdateRequestStateInput(processRequestId, ProcessRequestState.DELETED);
      updateRequestState.execute(updateRequestStateInput);
    }
  }

  private void deleteDocumentFromDocumentRepository(String processInstanceId)
  {
    if (!StringUtils.isBlank(processInstanceId))
    {
      bpmsRepositoryRegistry.getDocumentRepository().delete(processInstanceId);
    }
  }
}
