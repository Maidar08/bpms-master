package mn.erin.domain.bpm.usecase.process;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.json.JSONObject;

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

/**
 * @author Zorig
 */
public class GetProcessEntity extends AuthorizedUseCase<GetProcessEntityInput, Map<String, Serializable>>
{
  private static final Permission permission = new BpmModulePermission("GetProcessEntity");
  private final ProcessRepository processRepository;

  public GetProcessEntity()
  {
    super();
    this.processRepository = null;
  }

  public GetProcessEntity(AuthenticationService authenticationService,
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
  protected Map<String, Serializable> executeImpl(GetProcessEntityInput input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException(BpmMessagesConstants.INPUT_NULL_CODE, BpmMessagesConstants.INPUT_NULL_MESSAGE);
    }

    try
    {
      Process returnedProcess = processRepository.filterByInstanceIdAndEntityType(input.getProcessInstanceId(), input.getParameterEntityType());

      if (returnedProcess == null)
      {
        return Collections.emptyMap();
      }

      Map<String, Serializable> parameters = returnedProcess.getProcessParameters().get(input.getParameterEntityType());
      Map<String, Serializable> mapToReturn = new HashMap<>();

      for (Map.Entry<String, Serializable> entityAndValue : parameters.entrySet())
      {
        String parameterName = entityAndValue.getKey();
        if (checkIfJson(entityAndValue.getValue()))
        {
          addJsonParameters(mapToReturn, entityAndValue.getValue());
        }
        else
        {
          mapToReturn.put(parameterName, entityAndValue.getValue());
        }
      }

      return mapToReturn;
    }
    catch (BpmRepositoryException e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }

  private void addJsonParameters(Map<String, Serializable> map, Serializable jsonString)
  {
    JSONObject parameters = new JSONObject(jsonString.toString());

    for (Map.Entry<String, Object> parameter : parameters.toMap().entrySet())
    {
      if (parameter.getValue() instanceof Double)
      {
        map.put(parameter.getKey(), new BigDecimal((Double) parameter.getValue()).setScale(3, RoundingMode.FLOOR));
      }
      else
      {
        map.put(parameter.getKey(), (Serializable)parameter.getValue());
      }
    }
  }

  private boolean checkIfJson(Serializable parameter)
  {
    if (parameter instanceof String)
    {
      try
      {
        new JSONObject(parameter.toString());
        return true;
      }
      catch (Exception e)
      {
        return false;
      }
    }
    else
    {
      return false;
    }
  }

}
