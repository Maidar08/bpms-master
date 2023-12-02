package mn.erin.domain.bpm.usecase.process;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmMessagesConstants;
import mn.erin.domain.bpm.model.BpmModulePermission;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.model.process.Process;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProcessRepository;

public class GetXypSalaries extends AuthorizedUseCase<String, GetXypSalariesOutput>
{
  private static final Permission permission = new BpmModulePermission("GetSalaries");
  private final ProcessRepository processRepository;

  public GetXypSalaries(AuthenticationService authenticationService, AuthorizationService authorizationService, ProcessRepository processRepository)
  {
    super(authenticationService, authorizationService);
    this.processRepository = processRepository;
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected GetXypSalariesOutput executeImpl(String input) throws UseCaseException
  {
    if (StringUtils.isBlank(input))
    {
      throw new UseCaseException(BpmMessagesConstants.INVALID_INPUT_CODE, BpmMessagesConstants.INVALID_INPUT_MESSAGE);
    }
    try
    {
      Process returnedProcess = processRepository.filterByInstanceIdAndEntityType(input, ParameterEntityType.XYP_SALARY);

      if (returnedProcess == null)
      {
        return new GetXypSalariesOutput(null);
      }

      Map<String, Serializable> parameters = returnedProcess.getProcessParameters().get(ParameterEntityType.XYP_SALARY);
      Map<String, Map<String, Serializable>> mapToReturn = new HashMap<>();

      for (Map.Entry<String, Serializable> parameter : parameters.entrySet())
      {
        String key = parameter.getKey();
        Map<String, Serializable> salariesMapToInsert = new HashMap<>();

        if (checkIfJson(parameter.getValue()))
        {
          addJsonParameters(salariesMapToInsert, parameter.getValue());
          mapToReturn.put(key, salariesMapToInsert);
        }
      }
      return new GetXypSalariesOutput(mapToReturn);
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
        map.put(parameter.getKey(), (Serializable) parameter.getValue());
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
