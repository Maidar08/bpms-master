package mn.erin.domain.bpm.usecase.process;

import java.util.Objects;

import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmMessagesConstants;
import mn.erin.domain.bpm.model.BpmModulePermission;
import mn.erin.domain.bpm.model.process.Process;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProcessRepository;

import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_CATEGORY_BRANCH_BANKING;

/**
 * @author Zorig
 */
public class CreateProcess extends AuthorizedUseCase<CreateProcessInput, CreateProcessOutput>
{
  private static final Permission permission = new BpmModulePermission("CreateProcess");
  private final ProcessRepository processRepository;

  public CreateProcess(AuthenticationService authenticationService,
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
  protected CreateProcessOutput executeImpl(CreateProcessInput input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException(BpmMessagesConstants.INPUT_NULL_CODE, BpmMessagesConstants.INPUT_NULL_MESSAGE);
    }

    try
    {
      Process createdProcess;
      if (input.getProcessTypeCategory() != null && input.getProcessTypeCategory().equals(PROCESS_CATEGORY_BRANCH_BANKING))
      {
        createdProcess = createBranchBankingProcess(input);
      }
      else
      {
        ProcessUtils.validateProcessParameters(input.getParameters());
        createdProcess = processRepository
            .createProcess(input.getProcessInstanceId(), input.getStartedDate(), input.getFinishedDate(), input.getParameters());
      }
      return new CreateProcessOutput(createdProcess);
    }
    catch (BpmRepositoryException e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }

  private Process createBranchBankingProcess(CreateProcessInput input) throws BpmRepositoryException
  {
    return processRepository
        .createProcess(input.getProcessInstanceId(), input.getStartedDate(), input.getFinishedDate(), input.getCreatedUser(), PROCESS_CATEGORY_BRANCH_BANKING,
            input.getParameters());
  }
}
