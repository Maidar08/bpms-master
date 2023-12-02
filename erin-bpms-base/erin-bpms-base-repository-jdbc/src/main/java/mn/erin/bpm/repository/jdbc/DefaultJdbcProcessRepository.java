package mn.erin.bpm.repository.jdbc;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.stereotype.Repository;

import mn.erin.bpm.repository.jdbc.interfaces.JdbcProcessLargeParameterRepository;
import mn.erin.bpm.repository.jdbc.interfaces.JdbcProcessParameterRepository;
import mn.erin.bpm.repository.jdbc.interfaces.JdbcProcessRepository;
import mn.erin.bpm.repository.jdbc.model.JdbcProcess;
import mn.erin.bpm.repository.jdbc.model.JdbcProcessJoined;
import mn.erin.bpm.repository.jdbc.model.JdbcProcessJoinedForLarge;
import mn.erin.bpm.repository.jdbc.model.JdbcProcessLargeParameter;
import mn.erin.bpm.repository.jdbc.model.JdbcProcessParameter;
import mn.erin.common.utils.NumberUtils;
import mn.erin.domain.base.model.EntityId;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.model.process.Process;
import mn.erin.domain.bpm.model.process.ProcessInstanceId;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProcessRepository;

import static mn.erin.domain.bpm.BpmMessagesConstants.CLOB_CONVERSION_TO_STRING_FAIL_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleDataTypeConstants.BIG_DECIMAL;
import static mn.erin.domain.bpm.util.process.BpmUtils.getParameterDataType;

/**
 * @author Zorig
 */
@Repository
public class DefaultJdbcProcessRepository implements ProcessRepository
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultJdbcProcessRepository.class);

  private final JdbcProcessRepository jdbcProcessRepository;
  private final JdbcProcessParameterRepository jdbcProcessParameterRepository;
  private final JdbcProcessLargeParameterRepository jdbcProcessLargeParameterRepository;

  @Inject
  public DefaultJdbcProcessRepository(JdbcProcessRepository jdbcProcessRepository,
      JdbcProcessParameterRepository jdbcProcessParameterRepository,
      JdbcProcessLargeParameterRepository jdbcProcessLargeParameterRepository)
  {
    this.jdbcProcessRepository = Objects.requireNonNull(jdbcProcessRepository, "Process repository is required!");
    this.jdbcProcessParameterRepository = Objects.requireNonNull(jdbcProcessParameterRepository, "Process parameter repository is required!");
    this.jdbcProcessLargeParameterRepository = Objects.requireNonNull(jdbcProcessLargeParameterRepository, "Process large parameter repository is required!");
  }

  @Override
  public Process createProcess(String processInstanceId, LocalDateTime startedDate, LocalDateTime finishedDate,
      Map<ParameterEntityType, Map<String, Serializable>> parameters) throws BpmRepositoryException
  {
    try
    {
      Validate.notBlank(processInstanceId);
      Validate.notNull(startedDate);
      //parameters should've been validated in usecase

      jdbcProcessRepository.insert(processInstanceId, startedDate, finishedDate);

      for (Map.Entry<ParameterEntityType, Map<String, Serializable>> parameter : parameters.entrySet())
      {
        String parameterEntityType = parameter.getKey().toString();

        for (Map.Entry<String, Serializable> valueAndEntityType : parameter.getValue().entrySet())
        {
          Serializable parameterValue = valueAndEntityType.getValue();
          String parameterDataType = getParameterDataType(parameterValue);
          String parameterName = valueAndEntityType.getKey();

          if (null == parameterDataType)
          {
            continue;
          }
          if (BIG_DECIMAL.equals(parameterDataType))
          {
            BigDecimal bigDecimal = getBigDecimalParameterValue(parameterValue);
            String value = NumberUtils.bigDecimalToString(bigDecimal);
            jdbcProcessParameterRepository.insert(processInstanceId, parameterName, value, parameterDataType, parameterEntityType);
          }
          else
          {
            jdbcProcessParameterRepository.insert(processInstanceId, parameterName, parameterValue, parameterDataType, parameterEntityType);
          }
        }
      }

      return new Process(new ProcessInstanceId(processInstanceId), startedDate, finishedDate, null, null, parameters);
    }
    catch (Exception e)
    {
      throw new BpmRepositoryException(e.getMessage(), e);
    }
  }

  @Override
  public Process createProcess(String processInstanceId, LocalDateTime startedDate, LocalDateTime finishedDate, String createdUser,
      String processTypeCategory, Map<ParameterEntityType, Map<String, Serializable>> parameters) throws BpmRepositoryException
  {

    Validate.notBlank(processInstanceId);
    Validate.notNull(startedDate);

    jdbcProcessRepository.insert(processInstanceId, startedDate, finishedDate, createdUser, processTypeCategory);
    return new Process(new ProcessInstanceId(processInstanceId), startedDate, finishedDate, createdUser,  processTypeCategory, parameters);
  }

  @Override
  public void deleteProcess(ProcessInstanceId processInstanceId)
  {
    Validate.notNull(processInstanceId, "Process Request Id should not be null!");
    jdbcProcessRepository.deleteByProcessInstanceId(processInstanceId.getId());
    jdbcProcessParameterRepository.deleteAllByProcessInstanceId(processInstanceId.getId());
  }

  @Override
  public boolean deleteEntity(String processInstanceId, ParameterEntityType parameterEntityType) throws BpmRepositoryException
  {
    try
    {
      Validate.notBlank(processInstanceId);
      Validate.notNull(parameterEntityType);

      int numUpdated = jdbcProcessParameterRepository.deleteByEntityType(processInstanceId, parameterEntityType.toString());
      return numUpdated > 0;
    }
    catch (Exception e)
    {
      throw new BpmRepositoryException(e.getMessage());
    }
  }

  @Override
  public List<Process> filterByParameterNameAndNameValueAndEntityType(ParameterEntityType entityType, String parameterName, String parameterNameValue)
  {
    Validate.notBlank(entityType.toString());

    List<JdbcProcessParameter> processParameters = jdbcProcessParameterRepository.getByParameterEntityType(entityType.toString());
    if (processParameters.isEmpty())
    {
      return Collections.emptyList();
    }

    return convertToProcessFromJdbcProcessParameter(processParameters, parameterName, parameterNameValue, entityType);
  }

  @Override
  public Process findProcessByUserAndType(String createdUser, String processTypeCategory)
  {
    Validate.notBlank(createdUser);
    Validate.notBlank(processTypeCategory);

    JdbcProcess jdbcProcess = null;
    List<JdbcProcess> jdbcProcesses = jdbcProcessRepository.findByCreatedUser(createdUser, processTypeCategory);
    if(!jdbcProcesses.isEmpty())
    {
      jdbcProcesses.sort((process1, process2) -> process2.getStartedDate().compareTo(process1.getStartedDate()));
      jdbcProcess = jdbcProcesses.get(0);
    }
    return convertToProcess(jdbcProcess);
  }

  @Override
  public List<Process> findProcessesByDate(LocalDate startDate, LocalDate endDate)
  {
    final List<JdbcProcess> processes = jdbcProcessRepository.findProcessesByDate(startDate, endDate);
    return processes.stream().map(this::convertToProcess).collect(Collectors.toList());
  }

  @Override
  public void deleteProcesses(String createdUser, String processTypeCategory)
  {
    jdbcProcessRepository.deleteProcessesByTypeAndUser(createdUser, processTypeCategory);
  }

  @Override
  public int updateParameters(String processInstanceId, Map<ParameterEntityType, Map<String, Serializable>> parameters) throws BpmRepositoryException
  {
    try
    {
      Validate.notBlank(processInstanceId, "Process Instance Id is required!");
      Validate.notNull(parameters);

      if (!jdbcProcessRepository.existsById(processInstanceId))
      {
        return 0;
      }

      if (parameters.isEmpty())
      {
        return 0;
      }

      int numUpdated = 0;

      for (Map.Entry<ParameterEntityType, Map<String, Serializable>> parameter : parameters.entrySet())
      {
        String parameterEntityType = parameter.getKey().toString();

        for (Map.Entry<String, Serializable> valueAndEntityType : parameter.getValue().entrySet())
        {
          Serializable parameterValue = valueAndEntityType.getValue();
          Serializable validParameterValue = validateParameterValue(parameterValue);

          String parameterDataType = getParameterDataType(validParameterValue);
          String parameterName = valueAndEntityType.getKey();

          if (null == parameterDataType)
          {
            continue;
          }
          if (BIG_DECIMAL.equals(parameterDataType))
          {
            BigDecimal bigDecimal = getBigDecimalParameterValue(validParameterValue);
            validParameterValue = NumberUtils.bigDecimalToString(bigDecimal);
          }

          if (jdbcProcessParameterRepository.getByParameterNameAndEntityType(processInstanceId, parameterName, parameterEntityType) != null)
          {
            jdbcProcessParameterRepository.updateParameter(processInstanceId, parameterName, validParameterValue, parameterEntityType, parameterDataType);
            numUpdated++;
          }
          else
          {
            jdbcProcessParameterRepository.insert(processInstanceId, parameterName, validParameterValue, parameterDataType, parameterEntityType);
            numUpdated++;
          }
        }
      }

      return numUpdated;
    }
    catch (NullPointerException | IllegalArgumentException | DbActionExecutionException e)
    {
      throw new BpmRepositoryException(e.getMessage());
    }
  }

  @Override
  public int deleteProcessParameters(String processInstanceId, Map<ParameterEntityType, List<String>> deleteParams) throws BpmRepositoryException
  {
    if (!jdbcProcessRepository.existsById(processInstanceId))
    {
      return 0;
    }

    if (deleteParams.isEmpty())
    {
      return 0;
    }

    int numDeleted = 0;
    try
    {
      for (Map.Entry<ParameterEntityType, List<String>> parameter : deleteParams.entrySet())
      {
        String entityType = parameter.getKey().toString();

        List<String> parameterNames = parameter.getValue();

        for (String parameterName : parameterNames)
        {

          if (jdbcProcessParameterRepository.getByParameterName(processInstanceId, parameterName) != null)
          {
            jdbcProcessParameterRepository.deleteParameter(processInstanceId, parameterName, entityType);
            numDeleted++;
          }
        }
      }

      return numDeleted;
    }
    catch (NullPointerException | IllegalArgumentException | DbActionExecutionException e)
    {
      throw new BpmRepositoryException("JDBC_DELETE_PROCESS_PARAM_ERROR_CODE", e.getMessage());
    }
  }

  @Override
  public int updateParametersByName(String processInstanceId, Map<ParameterEntityType, Map<String, Serializable>> parameters) throws BpmRepositoryException
  {
    try
    {
      Validate.notBlank(processInstanceId, "Process Instance Id is required!");
      Validate.notNull(parameters);

      if (!jdbcProcessRepository.existsById(processInstanceId))
      {
        return 0;
      }

      if (parameters.isEmpty())
      {
        return 0;
      }

      int numUpdated = 0;

      for (Map.Entry<ParameterEntityType, Map<String, Serializable>> parameter : parameters.entrySet())
      {
        String parameterEntityType = parameter.getKey().toString();

        for (Map.Entry<String, Serializable> valueAndEntityType : parameter.getValue().entrySet())
        {
          Serializable parameterValue = valueAndEntityType.getValue();
          String parameterDataType = getParameterDataType(parameterValue);
          String parameterName = valueAndEntityType.getKey();

          if (null == parameterDataType)
          {
            continue;
          }
          if (BIG_DECIMAL.equals(parameterDataType))
          {
            BigDecimal bigDecimal = getBigDecimalParameterValue(parameterValue);
            parameterValue = NumberUtils.bigDecimalToString(bigDecimal);
          }

          if (jdbcProcessParameterRepository.getByParameterNameOnly(parameterName) != null)
          {
            jdbcProcessParameterRepository.updateParameterValueByName(parameterName, parameterValue);
            numUpdated++;
          }
          else
          {
            jdbcProcessParameterRepository.insert(processInstanceId, parameterName, parameterValue, parameterDataType, parameterEntityType);
            numUpdated++;
          }
        }
      }

      return numUpdated;
    }
    catch (NullPointerException | IllegalArgumentException | DbActionExecutionException e)
    {
      throw new BpmRepositoryException(e.getMessage());
    }
  }

  @Override
  public int updateLargeParameters(String processInstanceId, Map<ParameterEntityType, Map<String, Serializable>> parameters) throws BpmRepositoryException
  {
    try
    {
      Validate.notBlank(processInstanceId, "Process Instance Id is required!");
      Validate.notNull(parameters);

      if (!jdbcProcessRepository.existsById(processInstanceId))
      {
        return 0;
      }

      if (parameters.isEmpty())
      {
        return 0;
      }

      int numUpdated = 0;

      for (Map.Entry<ParameterEntityType, Map<String, Serializable>> parameter : parameters.entrySet())
      {
        String parameterEntityType = parameter.getKey().toString();

        for (Map.Entry<String, Serializable> valueAndEntityType : parameter.getValue().entrySet())
        {
          Serializable parameterValue = valueAndEntityType.getValue();
          String parameterDataType = getParameterDataType(parameterValue);
          String parameterName = valueAndEntityType.getKey();

          if (null == parameterDataType)
          {
            continue;
          }
          if (BIG_DECIMAL.equals(parameterDataType))
          {
            BigDecimal bigDecimal = getBigDecimalParameterValue(parameterValue);
            parameterValue = NumberUtils.bigDecimalToString(bigDecimal);
          }

          if (jdbcProcessLargeParameterRepository.getByParameterName(processInstanceId, parameterName) != null)
          {
            jdbcProcessLargeParameterRepository.updateParameter(processInstanceId, parameterName, parameterValue, parameterEntityType, parameterDataType);
            numUpdated++;
          }
          else
          {
            jdbcProcessLargeParameterRepository.insert(processInstanceId, parameterName, parameterValue, parameterDataType, parameterEntityType);
            numUpdated++;
          }
        }
      }

      return numUpdated;
    }
    catch (NullPointerException | IllegalArgumentException | DbActionExecutionException e)
    {
      throw new BpmRepositoryException(e.getMessage());
    }
  }

  @Override
  public Process filterLargeParamBySearchKeyFromValue(String searchKey, String additionalSearchKey, ParameterEntityType entity) throws SQLException
  {
    if (StringUtils.isBlank(searchKey) || null == entity)
    {
      return null;
    }

    final List<JdbcProcessLargeParameter> largeParameters = jdbcProcessLargeParameterRepository
        .getByEntityAndSearchFromValueByKey(ParameterEntityType.enumToString(entity));
    if (largeParameters.isEmpty())
    {
      return null;
    }

    for (JdbcProcessLargeParameter param : largeParameters)
    {
      final String valueString = convertToString(param.getParameterValue());
      if (valueString.contains(searchKey))
      {
        if (!StringUtils.isBlank(additionalSearchKey) && valueString.contains(additionalSearchKey) || StringUtils.isBlank(additionalSearchKey))
        {
          Map<String, Serializable> valueParam = new HashMap<>();
          Map<ParameterEntityType, Map<String, Serializable>> processParameters = new HashMap<>();

          valueParam.put(param.getParameterName(), valueString);
          processParameters.put(entity, valueParam);

          return new Process(ProcessInstanceId.valueOf(param.getProcessInstanceId()), null, null, null, null,  processParameters);
        }
      }
    }
    return null;
  }

  @Override
  public boolean deleteProcess(String processInstanceId) throws BpmRepositoryException
  {
    try
    {
      Validate.notBlank(processInstanceId);

      jdbcProcessRepository.deleteById(processInstanceId);
      jdbcProcessParameterRepository.deleteAllByProcessInstanceId(processInstanceId);

      return !jdbcProcessRepository.existsById(processInstanceId);
    }
    catch (NullPointerException | IllegalArgumentException | DbActionExecutionException e)
    {
      throw new BpmRepositoryException(e.getMessage());
    }
  }

  @Override
  public Process filterByInstanceIdAndEntityType(String processInstanceId, ParameterEntityType parameterEntityType) throws BpmRepositoryException
  {
    try
    {
      //validation
      Validate.notBlank(processInstanceId);
      Validate.notNull(parameterEntityType);

      List<JdbcProcessJoined> jdbcProcessJoinedList = jdbcProcessRepository
          .getJoinedProcessByProcessInstanceIdAndEntityType(processInstanceId, parameterEntityType.toString());
      if (jdbcProcessJoinedList.isEmpty())
      {
        return null;
      }
      else
      {
        return convertToProcessFromJdbcProcessJoined(jdbcProcessJoinedList);
      }
    }
    catch (Exception e)
    {
      throw new BpmRepositoryException(e.getMessage());
    }
  }

  @Override
  public Process filterByParameterNameAndEntityType(String parameterName, ParameterEntityType parameterEntityType) throws BpmRepositoryException
  {
    try
    {
      //validation
      Validate.notBlank(parameterName);
      Validate.notNull(parameterEntityType);

      List<JdbcProcessJoined> jdbcProcessJoinedList = jdbcProcessRepository
          .getJoinedProcessByNameAndEntityType(parameterName, parameterEntityType.toString());
      if (jdbcProcessJoinedList.isEmpty())
      {
        return null;
      }
      else
      {
        return convertToProcessFromJdbcProcessJoined(jdbcProcessJoinedList);
      }
    }
    catch (Exception e)
    {
      throw new BpmRepositoryException(e.getMessage());
    }
  }

  @Override
  public Process filterByParameterName(String instanceId, String parameterName)
  {
    Validate.notBlank(instanceId);
    Validate.notBlank(parameterName);

    final JdbcProcessParameter processParameters = jdbcProcessParameterRepository.getByParameterName(instanceId, parameterName);

    if (null == processParameters)
    {
      return null;
    }

    return toProcess(processParameters);
  }

  @Override
  public Process filterByParameterNameOnly(String parameterName)
  {
    Validate.notBlank(parameterName);

    final JdbcProcessParameter processParameters = jdbcProcessParameterRepository.getByParameterNameOnly(parameterName);

    if (null == processParameters)
    {
      return null;
    }

    return toProcess(processParameters);
  }

  @Override
  public Process filterLargeParametersByEntityType(String processInstanceId, ParameterEntityType parameterEntityType) throws BpmRepositoryException
  {
    try
    {
      //validation
      Validate.notBlank(processInstanceId);
      Validate.notNull(parameterEntityType);

      List<JdbcProcessJoinedForLarge> jdbcLargeProcessJoinedList = jdbcProcessRepository
          .getJoinedLargeProcessByProcessInstanceIdAndEntityType(processInstanceId, parameterEntityType.toString());
      if (jdbcLargeProcessJoinedList.isEmpty())
      {
        return null;
      }
      else
      {
        List<JdbcProcessJoined> jdbcProcessJoinedList = new ArrayList<>();
        jdbcLargeProcessJoinedList.forEach(joinedProcess -> {

          JdbcProcessJoined jdbcProcessJoined = new JdbcProcessJoined();
          jdbcProcessJoined.setStartedDate(joinedProcess.getStartedDate());
          jdbcProcessJoined.setProcessInstanceId(joinedProcess.getProcessInstanceId());
          String parameterValueString = "";
          try
          {
            parameterValueString = convertToString(joinedProcess.getParameterValue());
          }
          catch (SQLException e)
          {
            LOGGER.error("##################### get from process large parameter " + CLOB_CONVERSION_TO_STRING_FAIL_MESSAGE);
          }
          jdbcProcessJoined.setParameterValue(parameterValueString);
          jdbcProcessJoined.setParameterName(joinedProcess.getParameterName());
          jdbcProcessJoined.setParameterEntityType(joinedProcess.getParameterEntityType());
          jdbcProcessJoined.setParameterDataType(joinedProcess.getParameterDataType());
          jdbcProcessJoined.setFinishedDate(joinedProcess.getFinishedDate());
          jdbcProcessJoinedList.add(jdbcProcessJoined);
        });
        return convertToProcessFromJdbcProcessJoined(jdbcProcessJoinedList);
      }
    }
    catch (Exception e)
    {
      throw new BpmRepositoryException(e.getMessage());
    }
  }

  private String convertToString(Clob parameterValue) throws SQLException
  {
    return parameterValue.getSubString(1, (int) parameterValue.length());
  }

  @Override
  public boolean updateFinishedDate(String processInstanceId, LocalDateTime finishedDate) throws BpmRepositoryException
  {
    try
    {
      Validate.notBlank(processInstanceId);
      Validate.notNull(finishedDate);
      int numRowsAffected = jdbcProcessRepository.updateFinishedDate(processInstanceId, finishedDate);
      return numRowsAffected == 1;
    }
    catch (IllegalArgumentException | NullPointerException | DbActionExecutionException e)
    {
      throw new BpmRepositoryException(e.getMessage(), e);
    }
  }

  @Override
  public Process findById(EntityId entityId)
  {
    Validate.notNull(entityId, "Entity Id is required!");

    Optional jdbcProcess = jdbcProcessRepository.findById(entityId.getId());

    if (!jdbcProcess.isPresent())
    {
      return null;
    }

    List<JdbcProcessJoined> jdbcProcessJoinedList = jdbcProcessRepository.getJoinedProcessByProcessInstanceId(entityId.getId());

    if (jdbcProcessJoinedList.isEmpty())
    {
      return convertToProcessFromJdbcProcess((JdbcProcess) jdbcProcess.get());
    }

    return convertToProcessFromJdbcProcessJoined(jdbcProcessJoinedList);
  }

  @Override
  public Collection<Process> findAll()
  {
    List<Process> processListToReturn = new ArrayList<>();

    Iterator<JdbcProcess> allJdbcProcessesIterator = jdbcProcessRepository.findAll().iterator();
    while (allJdbcProcessesIterator.hasNext())
    {
      JdbcProcess currentJdbcProcessRequest = allJdbcProcessesIterator.next();
      String currentProcessInstanceId = currentJdbcProcessRequest.getProcessInstanceId();
      List<JdbcProcessJoined> jdbcProcessRequestJoinedList = jdbcProcessRepository
          .getJoinedProcessByProcessInstanceId(currentProcessInstanceId);
      if (jdbcProcessRequestJoinedList.isEmpty())
      {
        processListToReturn.add(convertToProcessFromJdbcProcess(currentJdbcProcessRequest));
      }
      else
      {
        processListToReturn.add(convertToProcessFromJdbcProcessJoined(jdbcProcessRequestJoinedList));
      }
    }

    return processListToReturn;
  }

  @Override
  public Map<String, Object> getProcessParametersByInstanceId(String instanceId)
  {
    Validate.notBlank(instanceId);
    Map<String , Object> result= new HashMap<>();
    List<JdbcProcessParameter> processParameters = jdbcProcessParameterRepository.getByProcessInstanceId(instanceId);

    for (JdbcProcessParameter jdbcProcessParameter : processParameters){
      result.put(jdbcProcessParameter.getParameterName(), jdbcProcessParameter.getParameterValue());
    }
    return result;
  }

  private BigDecimal getBigDecimalParameterValue(Serializable parameter)
  {
    if (parameter instanceof BigDecimal)
    {
      return (BigDecimal) parameter;
    }
    else if (parameter instanceof Double)
    {
      Double tmp = (Double) parameter;
      return BigDecimal.valueOf(tmp);
    }
    else
    {
      return null;
    }
  }

  private List<Process> convertToProcessFromJdbcProcessParameter(List<JdbcProcessParameter> processes, String parameterName, String parameterNameValue,
      ParameterEntityType entityType)
  {
    List<Process> returnProcesses = new ArrayList<>();
    for (JdbcProcessParameter jdbcProcessParameter : processes)
    {
      Process process = toProcess(jdbcProcessParameter);

      getFilteredProcess(process, parameterName, parameterNameValue, entityType, returnProcesses);
    }

    return returnProcesses;
  }

  private Process toProcess(JdbcProcessParameter processParameter)
  {
    Map<ParameterEntityType, Map<String, Serializable>> parameters = new HashMap<>();
    String entity = processParameter.getParameterEntityType();
    String processParameterName = processParameter.getParameterName();
    parameters.put(ParameterEntityType.fromStringToEnum(entity), new HashMap<>());
    if (processParameter.getParameterDataType().equals("JSON"))
    {
      String parameterValue = processParameter.getParameterValue();

      try
      {
        JSONObject jsonObject = new JSONObject(parameterValue);
        parameters.get(ParameterEntityType.fromStringToEnum(entity)).put(processParameterName, jsonObject.toString());
      }
      catch (JSONException e)
      {
        JSONArray jsonArray = new JSONArray(parameterValue);
        parameters.get(ParameterEntityType.fromStringToEnum(entity)).put(processParameterName, jsonArray.toString());
      }
    }
    else
    {
      parameters.get(ParameterEntityType.fromStringToEnum(entity)).put(processParameterName, processParameter.getParameterValue());
    }
    return new Process(ProcessInstanceId.valueOf(processParameter.getProcessInstanceId()), null, null, null, null,  parameters);
  }

  private void getFilteredProcess(Process process, String parameterName, String parameterNameValue, ParameterEntityType entityType,
      List<Process> returnProcessList)
  {
    Map<ParameterEntityType, Map<String, Serializable>> parameters = process.getProcessParameters();
    Map<String, Serializable> parameter = parameters.get(entityType);
    parameter.forEach((key, value) ->
    {
      String parameterNameDataType = getParameterDataType(key);
      if (parameterNameDataType.equals("JSON"))
      {
        String searchKey = (String) new JSONObject(key).get(parameterName);
        if (searchKey.equals(parameterNameValue))
        {
          returnProcessList.add(process);
        }
      }
      else
      {
        if (key.equals(parameterNameValue))
        {
          returnProcessList.add(process);
        }
      }
    });
  }

  private Process convertToProcessFromJdbcProcessJoined(List<JdbcProcessJoined> jdbcProcessJoinedList)
  {
    Map<ParameterEntityType, Map<String, Serializable>> parameters = new HashMap<>();
    Iterator<JdbcProcessJoined> jdbcProcessJoinedIterator = jdbcProcessJoinedList.iterator();
    while (jdbcProcessJoinedIterator.hasNext())
    {
      JdbcProcessJoined current = jdbcProcessJoinedIterator.next();
      Serializable parameterValue = saveAsRightType(current.getParameterValue(), current.getParameterDataType());
      ParameterEntityType parameterEntityType = ParameterEntityType.fromStringToEnum(current.getParameterEntityType());

      if (!parameters.containsKey(ParameterEntityType.fromStringToEnum(current.getParameterEntityType())))
      {
        parameters.put(parameterEntityType, new HashMap<>());
        parameters.get(parameterEntityType).put(current.getParameterName(), parameterValue);
      }
      else
      {
        parameters.get(parameterEntityType).put(current.getParameterName(), parameterValue);
      }
    }

    JdbcProcessJoined firstJdbcProcessJoined = jdbcProcessJoinedList.get(0);
    ProcessInstanceId processInstanceId = new ProcessInstanceId(firstJdbcProcessJoined.getProcessInstanceId());
    LocalDateTime startedDate = firstJdbcProcessJoined.getStartedDate();
    LocalDateTime finishedDate = firstJdbcProcessJoined.getFinishedDate();

    return new Process(processInstanceId, startedDate, finishedDate, null, null,  parameters);
  }

  private Process convertToProcessFromJdbcProcess(JdbcProcess jdbcProcess)
  {
    ProcessInstanceId processInstanceId = new ProcessInstanceId(jdbcProcess.getProcessInstanceId());
    LocalDateTime startedDate = jdbcProcess.getStartedDate();
    LocalDateTime finishedDate = jdbcProcess.getFinishedDate();
    String createdUser = jdbcProcess.getCreatedUser();
    String processTypeCategory = jdbcProcess.getProcessTypeCategory();

    return new Process(processInstanceId, startedDate, finishedDate, createdUser, processTypeCategory, Collections.emptyMap());
  }

  private Serializable saveAsRightType(String value, String type)
  {
    switch (type)
    {
    case "Integer":
      return Integer.valueOf(value);
    case "Long":
      return Long.valueOf(value);
    case BIG_DECIMAL:
      Locale locale = Locale.US;
      NumberFormat format = NumberFormat.getInstance(locale);
      try
      {
        return new BigDecimal(format.parse(value).toString());
      }
      catch (ParseException e)
      {
        e.printStackTrace();
        return null;
      }
    case "Boolean":
      return Boolean.valueOf(value);
    default:
      return value;
    }
  }

  private Serializable validateParameterValue(Serializable parameterValue)
  {
    if (null == parameterValue)
    {
      return null;
    }

    if (parameterValue instanceof JSONObject)
    {
      return parameterValue;
    }
    else if (parameterValue instanceof JSONArray)
    {
      return parameterValue;
    }
    else if (parameterValue instanceof String)
    {
      return parameterValue;
    }
    else if (parameterValue instanceof Integer)
    {
      return parameterValue;
    }
    else if (parameterValue instanceof BigDecimal)
    {
      return parameterValue;
    }
    else if (parameterValue instanceof Double)
    {
      return parameterValue;
    }
    else if (parameterValue instanceof Long)
    {
      return parameterValue;
    }
    else if (parameterValue instanceof Boolean)
    {
      return parameterValue;
    }
    else if (parameterValue instanceof byte[])
    {
      return parameterValue;
    }
    else if (parameterValue instanceof Date)
    {
      return parameterValue;
    }
    else
    {
      return null;
    }
  }

  private Process convertToProcess(JdbcProcess jdbcProcess)
  {
    if(jdbcProcess != null){
      return new Process(ProcessInstanceId.valueOf(jdbcProcess.getProcessInstanceId()), jdbcProcess.getStartedDate(),jdbcProcess.getFinishedDate(), jdbcProcess.getCreatedUser(),
          jdbcProcess.getProcessTypeCategory(), Collections.emptyMap());
    }
    return null;
  }
}
