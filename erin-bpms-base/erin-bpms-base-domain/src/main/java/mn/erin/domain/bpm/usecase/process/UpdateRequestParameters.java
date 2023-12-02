package mn.erin.domain.bpm.usecase.process;

import java.io.Serializable;
import java.util.Map;
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
import mn.erin.domain.bpm.repository.ProcessRequestRepository;


/**
 * @author Zorig
 */
public class UpdateRequestParameters extends AuthorizedUseCase<UpdateRequestParametersInput, UpdateRequestParametersOutput>
{
  private static final Permission permission = new BpmModulePermission("UpdateRequestParameter");
  private final ProcessRequestRepository processRequestRepository;

  public UpdateRequestParameters(ProcessRequestRepository processRequestRepository)
  {
    super();
    this.processRequestRepository = processRequestRepository;
  }

  public UpdateRequestParameters(AuthenticationService authenticationService,
      AuthorizationService authorizationService, ProcessRequestRepository processRequestRepository)
  {
    super(authenticationService, authorizationService);
    this.processRequestRepository = Objects.requireNonNull(processRequestRepository, "Process request repository is required!");
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected UpdateRequestParametersOutput executeImpl(UpdateRequestParametersInput input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException(BpmMessagesConstants.INPUT_NULL_CODE,BpmMessagesConstants.INPUT_NULL_MESSAGE);
    }

    validateParameters(input.getParameters());

    try
    {
      boolean isUpdated = processRequestRepository.updateParameters(input.getProcessRequestId(), input.getParameters());
      UpdateRequestParametersOutput output = new UpdateRequestParametersOutput(isUpdated);
      return output;
    }
    catch (BpmRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }

  private void validateParameters(Map<String, Serializable> parameters) throws UseCaseException
  {
    if (parameters == null)
    {
      throw new UseCaseException(BpmMessagesConstants.PARAMETER_NULL_CODE, BpmMessagesConstants.PARAMETER_NULL_MESSAGE);
    }

    if (parameters.isEmpty())
    {
      throw new UseCaseException(BpmMessagesConstants.PARAMETER_NULL_CODE, BpmMessagesConstants.PARAMETER_NULL_MESSAGE);
    }

    for (Map.Entry<String, Serializable> parameter : parameters.entrySet())
    {
      if (parameter.getKey() == null || StringUtils.isBlank(parameter.getKey()))
      {
        String errorCode = "CBS006";
        throw new UseCaseException(errorCode, "Name for parameter must not be null or blank!");
      }
      if (parameter.getValue() instanceof String)
      {
        String parameterValueString = parameter.getValue().toString();
        if (StringUtils.isBlank(parameterValueString))
        {
          String errorCode = "CBS007";
          throw new UseCaseException(errorCode, "Value for parameter [" + parameter.getKey() +"] must not be blank");
        }
      }
    }
  }
}
