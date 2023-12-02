package mn.erin.domain.bpm.usecase.process;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

/**
 * @author Zorig
 */
public class GetSalaries extends AuthorizedUseCase<String, GetSalariesOutput>//Map<String, Map<String, Serializable>>>
{
  private static final Permission permission = new BpmModulePermission("GetSalaries");
  private final ProcessRepository processRepository;

  public GetSalaries()
  {
    super();
    this.processRepository = null;
  }

  public GetSalaries(AuthenticationService authenticationService, AuthorizationService authorizationService,
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
  protected GetSalariesOutput executeImpl(String input) throws UseCaseException
  {
    if (StringUtils.isBlank(input))
    {
      throw new UseCaseException(BpmMessagesConstants.INVALID_INPUT_CODE, BpmMessagesConstants.INVALID_INPUT_MESSAGE);
    }

    try
    {
      Process returnedProcess = processRepository.filterByInstanceIdAndEntityType(input, ParameterEntityType.SALARY);

      if (returnedProcess == null)
      {
        return new GetSalariesOutput(Collections.emptyMap(), 0, 0, "", "", "");
      }

      Map<String, Serializable> parameters = returnedProcess.getProcessParameters().get(ParameterEntityType.SALARY);
      Map<String, Map<String, Serializable>> mapToReturn = new HashMap<>();

      for (Map.Entry<String, Serializable> parameter : parameters.entrySet())
      {
        String date = parameter.getKey();
        Map<String, Serializable> salariesMapToInsert = new HashMap<>();

        if (checkIfJson(parameter.getValue()))
        {
          addJsonParameters(salariesMapToInsert, parameter.getValue());
          mapToReturn.put(date, salariesMapToInsert);
        }
      }

      return new GetSalariesOutput(mapToReturn, parameters.get("averageBeforeTax"), parameters.get("averageAfterTax"),
          parameters.get("hasMortgage"), parameters.get("NDSH"), parameters.get("EMD"));
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
