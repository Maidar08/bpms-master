package mn.erin.bpm.repository.jdbc;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import mn.erin.bpm.repository.jdbc.interfaces.JdbcDefaultParameterRepository;
import mn.erin.bpm.repository.jdbc.model.JdbcDefaultParameter;
import mn.erin.domain.base.model.Entity;
import mn.erin.domain.base.model.EntityId;
import mn.erin.domain.bpm.repository.BpmRepositoryException;

import static mn.erin.domain.bpm.BpmMessagesConstants.PARAMETER_ENTITY_TYPE_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PARAMETER_ENTITY_TYPE_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PROCESS_TYPE_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PROCESS_TYPE_ID_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.util.process.BpmUtils.convertClobToString;
import static mn.erin.domain.bpm.util.process.BpmUtils.convertJsonStringToMap;

/**
 * @author Odgavaa
 **/

@Repository
public class DefaultJdbcGeneralInfoRepository implements mn.erin.domain.bpm.repository.directOnline.DefaultParameterRepository
{
  private final JdbcDefaultParameterRepository jdbcDefaultParameterRepository;

  public DefaultJdbcGeneralInfoRepository(JdbcDefaultParameterRepository jdbcDefaultParameterRepository)
  {
    this.jdbcDefaultParameterRepository = Objects.requireNonNull(jdbcDefaultParameterRepository, "Default Parameter repository is required!");
  }

  @Override
  public Map<String, Object> getDefaultParametersByProcessType(String processType, String entity) throws BpmRepositoryException
  {
    if (StringUtils.isBlank(processType))
    {
      throw new BpmRepositoryException(PROCESS_TYPE_ID_NULL_CODE, PROCESS_TYPE_ID_NULL_MESSAGE);
    }
    if (StringUtils.isBlank(entity))
    {
      throw new BpmRepositoryException(PARAMETER_ENTITY_TYPE_NULL_CODE, PARAMETER_ENTITY_TYPE_NULL_MESSAGE);
    }
    List<JdbcDefaultParameter> defaultParametersList;
    if (processType.equals(INSTANT_LOAN_PROCESS_TYPE_ID) && (entity.equals("Screen") || entity.equals("LoanTerms")))
    {
      defaultParametersList = jdbcDefaultParameterRepository.getDefaultParametersByProcessType(processType, "LoanTerms");
    }
    else
    {
      defaultParametersList = jdbcDefaultParameterRepository.getDefaultParametersByProcessType(processType, entity);
    }


    try
    {
      if (defaultParametersList.isEmpty())
      {
        return Collections.emptyMap();
      }
      else if (entity.equals("Screen"))
      {
        return convertToParametersMapScreen(defaultParametersList, entity);
      }
      return convertToParametersMap(defaultParametersList, entity);
    }
    catch (Exception e)
    {
      throw new BpmRepositoryException(e.getMessage());
    }
  }

  @Override
  public Entity findById(EntityId entityId)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Collection findAll()
  {
    throw new UnsupportedOperationException();
  }

  private Map<String, Object> convertToParametersMap(List<JdbcDefaultParameter> defaultParameterList, String entity) throws SQLException
  {
    Map<String, Object> parametersMap = new HashMap<>();
    Map<String, Object> resultParameters = new HashMap<>();

    for (JdbcDefaultParameter defaultParameter : defaultParameterList)
    {
      String parameterValue = convertClobToString(defaultParameter.getParameterValue());
      if (defaultParameter.getParameterDataType().equals("LIST"))
      {
        String[] resultValue = parameterValue.split("; ");
        List<String> parameterList = new ArrayList<>(Arrays.asList(resultValue));
        if (defaultParameter.getParameterName().equals("Term") && defaultParameter.getProcessType().equals(INSTANT_LOAN_PROCESS_TYPE_ID))
        {
          Map<String, Object> instantLoanMap = new HashMap<>();
          instantLoanMap.put(defaultParameter.getParameterName(), parameterList);
          resultParameters.put("Screen", instantLoanMap);
        }
        else
        {
          parametersMap.put(defaultParameter.getParameterName(), parameterList);
        }
      }
      else if (defaultParameter.getParameterDataType().equals("JSON"))
      {
        Map<String, Object> paramValueMap = convertJsonStringToMap(parameterValue);
        parametersMap.put(defaultParameter.getParameterName(), paramValueMap);
      }
      else
      {
        parametersMap.put(defaultParameter.getParameterName(), parameterValue);
      }
    }
    resultParameters.put(entity, parametersMap);
    return resultParameters;
  }
  private Map<String, Object> convertToParametersMapScreen(List<JdbcDefaultParameter> defaultParameterList, String entity) throws SQLException
  {
    Map<String, Object> parametersMap = new HashMap<>();
    Map<String, Object> resultParameters = new HashMap<>();

    for (JdbcDefaultParameter defaultParameter : defaultParameterList)
    {
      String parameterValue = convertClobToString(defaultParameter.getParameterValue());
      if (defaultParameter.getParameterDataType().equals("LIST"))
      {
        String[] resultValue = parameterValue.split("; ");
        List<String> parameterList = new ArrayList<>(Arrays.asList(resultValue));
        parametersMap.put(defaultParameter.getParameterName(), parameterList);
      }
    }
    resultParameters.put(entity, parametersMap);
    return resultParameters;
  }
}
