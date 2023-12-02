package mn.erin.bpm.repository.jdbc;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.inject.Inject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;

import mn.erin.bpm.repository.jdbc.interfaces.JdbcLoanContractParameterRepository;
import mn.erin.bpm.repository.jdbc.model.JdbcLoanContractParameter;
import mn.erin.domain.base.model.EntityId;
import mn.erin.domain.bpm.model.contract.LoanContractParameter;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.model.process.ProcessInstanceId;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.LoanContractParameterRepository;

import static mn.erin.domain.bpm.BpmMessagesConstants.CLOB_CONVERSION_TO_STRING_FAIL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CLOB_CONVERSION_TO_STRING_FAIL_MESSAGE;
import static mn.erin.domain.bpm.util.process.BpmUtils.convertClobToString;
import static mn.erin.domain.bpm.util.process.BpmUtils.getParameterDataType;

/**
 * @author Temuulen Naranbold
 */
@Repository
public class DefaultJdbcLoanContractParameterRepository implements LoanContractParameterRepository
{
  private final JdbcLoanContractParameterRepository jdbcLoanContractParameterRepository;

  @Inject
  public DefaultJdbcLoanContractParameterRepository(JdbcLoanContractParameterRepository jdbcLoanContractParameterRepository)
  {
    this.jdbcLoanContractParameterRepository = Objects.requireNonNull(jdbcLoanContractParameterRepository,
        "Jdbc loan contract parameter repository is required!");
  }

  @Override
  public void update(ProcessInstanceId instanceId, String defKey, Map<String, Object> parameterValue, String parameterEntityType)
      throws BpmRepositoryException
  {
    if (jdbcLoanContractParameterRepository.findByProcessInstanceIdAndDefKeyAndEntity(instanceId.getId(), defKey, parameterEntityType).isPresent())
    {
      jdbcLoanContractParameterRepository.update(instanceId.getId(), defKey, mapToJson(parameterValue), parameterEntityType);
    }
    else
    {
      jdbcLoanContractParameterRepository.insert(instanceId.getId(), defKey, mapToJson(parameterValue), getParameterDataType(parameterValue.toString()),
          parameterEntityType);
    }
  }

  @Override
  public LoanContractParameter getByInstanceIdAndDefKey(String instanceId, String defKey) throws BpmRepositoryException
  {
    List<JdbcLoanContractParameter> processParamList = jdbcLoanContractParameterRepository.findByProcessInstanceIdAndDefKey(instanceId, defKey);
    return mapToLoanContractParameter(processParamList);
    //    return jdbcLoanContractParameterRepository.findByProcessInstanceIdAndDefKey(instanceId, defKey).map(this::mapToLoanContractParameter).orElse(null);
  }

  @Override
  public List<LoanContractParameter> getByInstanceId(String processInstanceId)
  {
    final List<JdbcLoanContractParameter> jdbcLoanContractParameters = jdbcLoanContractParameterRepository.findByProcessInstanceId2(processInstanceId);
    if (null == jdbcLoanContractParameters || jdbcLoanContractParameters.isEmpty())
    {
      return null;
    }
    return jdbcLoanContractParameters.stream().map(jdbcParameters -> mapToLoanContractParameter(jdbcParameters))
        .collect(Collectors.toList());
  }

  @Override
  public LoanContractParameter findById(EntityId entityId)
  {
    return jdbcLoanContractParameterRepository.findById(entityId.getId()).map(this::mapToLoanContractParameter).orElse(null);
  }

  @Override
  public Collection<LoanContractParameter> findAll()
  {
    return jdbcLoanContractParameterRepository.findAll().stream().map(this::mapToLoanContractParameter).collect(Collectors.toList());
  }

  private LoanContractParameter mapToLoanContractParameter(JdbcLoanContractParameter jdbcLoanContractParameter)
  {
    Map<String, Object> object;
    try
    {
      object = new JSONObject(convertClobToString(jdbcLoanContractParameter.getParameterValue())).toMap();
    }
    catch (SQLException e)
    {
      object = new HashMap<>();
    }
    return new LoanContractParameter(
        ProcessInstanceId.valueOf(jdbcLoanContractParameter.getProcessInstanceId()),
        jdbcLoanContractParameter.getDefKey(),
        object,
        jdbcLoanContractParameter.getParameterValueType(),
        ParameterEntityType.fromStringToEnum(jdbcLoanContractParameter.getParameterEntityType())
    );
  }

  private LoanContractParameter mapToLoanContractParameter(List<JdbcLoanContractParameter> jdbcLoanContractParameterList) throws BpmRepositoryException
  {
    if (jdbcLoanContractParameterList.isEmpty())
    {
      return null;
    }
    Map<String, Object> object = new HashMap<>();
    Map<String, Object> tableObject = new HashMap<>();
    String instanceId = null;
    String defKey = null;
    String parameterEntityType = null;
    String parameterValueType = null;
    for (JdbcLoanContractParameter jdbcLoanContractParameter : jdbcLoanContractParameterList)
    {
      instanceId = jdbcLoanContractParameter.getProcessInstanceId();
      defKey = jdbcLoanContractParameter.getDefKey();
      if (null != jdbcLoanContractParameter.getParameterEntityType() && jdbcLoanContractParameter.getParameterEntityType().equals("FORM"))
      {
        parameterEntityType = jdbcLoanContractParameter.getParameterEntityType();
      }
      parameterValueType = jdbcLoanContractParameter.getParameterValueType();
      try
      {
        if (jdbcLoanContractParameter.getParameterEntityType().equals("FORM"))
        {
          object = new JSONObject(convertClobToString(jdbcLoanContractParameter.getParameterValue())).toMap();
        } else
        {
          tableObject = new JSONObject(convertClobToString(jdbcLoanContractParameter.getParameterValue())).toMap();
        }
      }
      catch (SQLException e)
      {
        throw new BpmRepositoryException(e.getMessage());
      }
    }

    final LoanContractParameter loanContractParameter = new LoanContractParameter(
        ProcessInstanceId.valueOf(instanceId),
        defKey,
        object,
        parameterValueType,
        ParameterEntityType.fromStringToEnum(parameterEntityType)
    );
    loanContractParameter.setTableData(tableObject);

    return loanContractParameter;
  }

  private String mapToJson(Map<String, Object> parameterValue) throws BpmRepositoryException
  {
    ObjectMapper mapper = new ObjectMapper();
    try
    {
      return mapper.writeValueAsString(parameterValue);
    }
    catch (JsonProcessingException e)
    {
      throw new BpmRepositoryException(CLOB_CONVERSION_TO_STRING_FAIL_CODE + CLOB_CONVERSION_TO_STRING_FAIL_MESSAGE + e.getMessage());
    }
  }
}
