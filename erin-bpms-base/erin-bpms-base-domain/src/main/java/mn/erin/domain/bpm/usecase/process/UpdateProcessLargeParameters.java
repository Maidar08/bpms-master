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
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProcessRepository;

public class UpdateProcessLargeParameters extends AuthorizedUseCase<UpdateProcessParametersInput, UpdateProcessParametersOutput>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateProcessParameters.class);
  private static final Permission permission = new BpmModulePermission("UpdateProcessParameters");
  private final ProcessRepository processRepository;

  public UpdateProcessLargeParameters(AuthenticationService authenticationService,
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
  protected UpdateProcessParametersOutput executeImpl(UpdateProcessParametersInput input) throws UseCaseException
  {
    if (input == null || StringUtils.isBlank(input.getProcessInstanceId()))
    {
      LOGGER.error("Invalid input (null or blank)!");
      throw new UseCaseException(BpmMessagesConstants.INVALID_INPUT_CODE, BpmMessagesConstants.INVALID_INPUT_MESSAGE);
    }

    try
    {
      ProcessUtils.validateProcessParameters(input.getParameters());

      int numUpdated = processRepository.updateLargeParameters(input.getProcessInstanceId(), input.getParameters());
      return new UpdateProcessParametersOutput(numUpdated);
    }
    catch (BpmRepositoryException e)
    {
      LOGGER.error(e.getMessage());
      throw new UseCaseException(e.getMessage());
    }
  }
}
