package mn.erin.bpm.repository.jdbc;

import static mn.erin.domain.bpm.BpmModuleDataTypeConstants.BIG_DECIMAL;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.stereotype.Repository;

import mn.erin.bpm.repository.jdbc.interfaces.JdbcProcessRequestParameterRepository;
import mn.erin.bpm.repository.jdbc.interfaces.JdbcProcessRequestRepository;
import mn.erin.bpm.repository.jdbc.model.JdbcProcessRequest;
import mn.erin.bpm.repository.jdbc.model.JdbcProcessRequestJoined;
import mn.erin.bpm.repository.jdbc.model.JdbcProcessRequestParameter;
import mn.erin.common.utils.NumberUtils;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.base.model.EntityId;
import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.model.process.ProcessRequestId;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.model.process.ProcessTypeId;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;

/**
 * @author EBazarragchaa
 */
@Repository
public class DefaultJdbcProcessRequestRepository implements ProcessRequestRepository
{
  private final JdbcProcessRequestRepository jdbcProcessRequestRepository;
  private final JdbcProcessRequestParameterRepository jdbcProcessRequestParameterRepository;
  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultJdbcProcessRequestRepository.class);
  private static final String USER_ID_REQUIRED_MESSAGE = "User Id is required!";

  @Inject
  public DefaultJdbcProcessRequestRepository(JdbcProcessRequestRepository jdbcProcessRequestRepository,
      JdbcProcessRequestParameterRepository jdbcProcessRequestParameterRepository)
  {
    this.jdbcProcessRequestRepository = jdbcProcessRequestRepository;
    this.jdbcProcessRequestParameterRepository = jdbcProcessRequestParameterRepository;
  }

  @Override
  public ProcessRequest createProcessRequest(ProcessTypeId processTypeId, GroupId groupId, String userId, Map<String, Serializable> parameters)
      throws BpmRepositoryException
  {
    try
    {
      Validate.notNull(processTypeId, "Process Type Id should not be null!");
      Validate.notNull(groupId, "Group Id should not be null!");
      Validate.notBlank(userId, "User Id should not be blank!");
      Validate.notNull(parameters, "Parameters should not be null!");

      LocalDateTime createdTime = LocalDateTime.now(ZoneId.of("UTC+8"));

      String incrementedId = StringUtils.leftPad(jdbcProcessRequestRepository.getCurrentIncrementId(), 8, "0");
      String processRequestId = groupId.getId() + incrementedId;

      jdbcProcessRequestRepository.insert(processRequestId, processTypeId.getId(), groupId.getId(), userId, ProcessRequestState.NEW.toString(), createdTime);

      for (Map.Entry<String, Serializable> entry : parameters.entrySet())
      {
        String parameterType = getParameterType(entry.getValue());

        if (null == parameterType)
        {
          continue;
        }
        if (BIG_DECIMAL.equals(parameterType))
        {
          BigDecimal bigDecimal = getBigDecimalParameterValue(entry.getValue());
          String value = NumberUtils.bigDecimalToString(bigDecimal);
          jdbcProcessRequestParameterRepository.insert(processRequestId, entry.getKey(), value, parameterType);
        }
        else
        {
          jdbcProcessRequestParameterRepository.insert(processRequestId, entry.getKey(), entry.getValue(), parameterType);
        }
      }

      return new ProcessRequest(new ProcessRequestId(processRequestId), processTypeId, groupId, userId, createdTime,
          ProcessRequestState.NEW, parameters);
    }
    catch (Exception e)
    {
      throw new BpmRepositoryException(e.getMessage(), e);
    }
  }

  @Override
  public void deleteProcessRequest(ProcessRequestId processRequestId)
  {
    Validate.notNull(processRequestId, "Process Request Id should not be null!");
    jdbcProcessRequestRepository.deleteByProcessRequestId(processRequestId.getId());
    jdbcProcessRequestParameterRepository.deleteByProcessRequestId(processRequestId.getId());
  }

  @Override
  public Collection<ProcessRequest> findAllByAssignedUserId(UserId userId) throws BpmRepositoryException
  {
    try
    {
      Validate.notNull(userId, USER_ID_REQUIRED_MESSAGE);

      List<ProcessRequest> processRequestListToReturn = new ArrayList<>();

      Iterator<JdbcProcessRequest> allJdbcProcessRequestsIterator = jdbcProcessRequestRepository.getJdbcProcessRequestsByAssignedUserId(userId.getId())
          .iterator();
      while (allJdbcProcessRequestsIterator.hasNext())
      {
        JdbcProcessRequest currentJdbcProcessRequest = allJdbcProcessRequestsIterator.next();
        String currentProcessRequestId = currentJdbcProcessRequest.getProcessRequestId();
        List<JdbcProcessRequestJoined> jdbcProcessRequestJoinedList = jdbcProcessRequestRepository
            .getJoinedProcessRequestsByProcessRequestId(currentProcessRequestId);
        if (jdbcProcessRequestJoinedList.isEmpty())
        {
          processRequestListToReturn.add(convertToProcessRequestFromJdbcProcessRequest(currentJdbcProcessRequest));
        }
        else
        {
          processRequestListToReturn.add(convertToProcessRequestFromJdbcProcessRequestJoined(jdbcProcessRequestJoinedList));
        }
      }

      return processRequestListToReturn;
    }
    catch (Exception e)
    {
      throw new BpmRepositoryException(e.getMessage(), e);
    }
  }

  @Override
  public Collection<ProcessRequest> findAllBySearchKey(String searchKey) throws BpmRepositoryException
  {
    try
    {
      Validate.notNull(searchKey, USER_ID_REQUIRED_MESSAGE);

      List<ProcessRequest> processRequestListToReturn = new ArrayList<>();

      for (JdbcProcessRequest currentJdbcProcessRequest : jdbcProcessRequestRepository.getJdbcProcessRequestsBySearchKey(searchKey))
      {
        String currentProcessRequestId = currentJdbcProcessRequest.getProcessRequestId();
        List<JdbcProcessRequestJoined> jdbcProcessRequestJoinedList = jdbcProcessRequestRepository
            .getJoinedProcessRequestsByProcessRequestId(currentProcessRequestId);
        if (jdbcProcessRequestJoinedList.isEmpty())
        {
          processRequestListToReturn.add(convertToProcessRequestFromJdbcProcessRequest(currentJdbcProcessRequest));
        }
        else
        {
          processRequestListToReturn.add(convertToProcessRequestFromJdbcProcessRequestJoined(jdbcProcessRequestJoinedList));
        }
      }

      return processRequestListToReturn;
    }
    catch (Exception e)
    {
      throw new BpmRepositoryException(e.getMessage(), e);
    }
  }

  @Override
  public Collection<ProcessRequest> findAllByUserGroupId(GroupId groupId) throws BpmRepositoryException
  {
    try
    {
      Validate.notNull(groupId, "Group Id is required!");

      List<ProcessRequest> processRequestListToReturn = new ArrayList<>();

      Iterator<JdbcProcessRequest> allJdbcProcessRequestsIterator = jdbcProcessRequestRepository.getJdbcProcessRequestsByGroupId(groupId.getId()).iterator();
      while (allJdbcProcessRequestsIterator.hasNext())
      {
        JdbcProcessRequest currentJdbcProcessRequest = allJdbcProcessRequestsIterator.next();
        String currentProcessRequestId = currentJdbcProcessRequest.getProcessRequestId();
        // TODO Fix Me!!!! Not a Good Solution
        //Thread.sleep(100);
        List<JdbcProcessRequestJoined> jdbcProcessRequestJoinedList = jdbcProcessRequestRepository
            .getJoinedProcessRequestsByProcessRequestId(currentProcessRequestId);
        if (jdbcProcessRequestJoinedList.isEmpty())
        {
          processRequestListToReturn.add(convertToProcessRequestFromJdbcProcessRequest(currentJdbcProcessRequest));
        }
        else
        {
          processRequestListToReturn.add(convertToProcessRequestFromJdbcProcessRequestJoined(jdbcProcessRequestJoinedList));
        }
      }

      return processRequestListToReturn;
    }
    catch (Exception e)
    {
      throw new BpmRepositoryException(e.getMessage(), e);
    }
  }

  @Override
  public Collection<ProcessRequest> findAllByUserGroupIdDate(GroupId groupId, Date startDate, Date endDate) throws BpmRepositoryException
  {
    try
    {
      Validate.notNull(groupId, "Group Id is required!");

      List<ProcessRequest> processRequestListToReturn = new ArrayList<>();

      Iterator<JdbcProcessRequest> allJdbcProcessRequestsIterator = jdbcProcessRequestRepository.getJdbcProcessRequestsByGroupIdAndDate(groupId.getId(),
          startDate, endDate).iterator();
      while (allJdbcProcessRequestsIterator.hasNext())
      {
        JdbcProcessRequest currentJdbcProcessRequest = allJdbcProcessRequestsIterator.next();
        String currentProcessRequestId = currentJdbcProcessRequest.getProcessRequestId();
        List<JdbcProcessRequestJoined> jdbcProcessRequestJoinedList = jdbcProcessRequestRepository
            .getJoinedProcessRequestsByProcessRequestId(currentProcessRequestId);
        if (jdbcProcessRequestJoinedList.isEmpty())
        {
          processRequestListToReturn.add(convertToProcessRequestFromJdbcProcessRequest(currentJdbcProcessRequest));
        }
        else
        {
          processRequestListToReturn.add(convertToProcessRequestFromJdbcProcessRequestJoined(jdbcProcessRequestJoinedList));
        }
      }

      return processRequestListToReturn;
    }
    catch (Exception e)
    {
      throw new BpmRepositoryException(e.getMessage(), e);
    }
  }

  @Override
  public Collection<ProcessRequest> findAllByGroupIdAndProcessType(GroupId groupId, String processType) throws BpmRepositoryException
  {
    try
    {
      Validate.notNull(groupId, "Group Id is required!");

      List<ProcessRequest> processRequestList = new ArrayList<>();

      Iterator<JdbcProcessRequest> allJdbcProcessRequestsIterator =
          jdbcProcessRequestRepository
              .getJdbcProcessRequestByGroupIdAndProcessType(groupId.getId(), "%" + processType + "%").iterator();
      while (allJdbcProcessRequestsIterator.hasNext())
      {
        JdbcProcessRequest currentJdbcProcessRequest = allJdbcProcessRequestsIterator.next();
        String currentProcessRequestId = currentJdbcProcessRequest.getProcessRequestId();

        List<JdbcProcessRequestJoined> jdbcProcessRequestJoinedList = jdbcProcessRequestRepository.getJoinedProcessRequestsByProcessRequestId(
            currentProcessRequestId);

        if (jdbcProcessRequestJoinedList.isEmpty())
        {
          processRequestList.add(convertToProcessRequestFromJdbcProcessRequest(currentJdbcProcessRequest));
        }
        else
        {
          processRequestList.add(convertToProcessRequestFromJdbcProcessRequestJoined(jdbcProcessRequestJoinedList));
        }
      }
      return processRequestList;
    }
    catch (Exception e)
    {
      throw new BpmRepositoryException(e.getMessage(), e);
    }
  }

  @Override
  public Collection<ProcessRequest> findAllByProcessType(String processType, TenantId tenantId) throws BpmRepositoryException
  {
    try
    {
      Validate.notNull(processType, "Process Type Id is required!");

      List<ProcessRequest> processRequestListToReturn = new ArrayList<>();

      Iterator<JdbcProcessRequest> allJdbcProcessRequestsIterator = jdbcProcessRequestRepository.getJdbcProcessRequestsByProcessTypeId(processType).iterator();
      while (allJdbcProcessRequestsIterator.hasNext())
      {
        JdbcProcessRequest currentJdbcProcessRequest = allJdbcProcessRequestsIterator.next();
        String currentProcessRequestId = currentJdbcProcessRequest.getProcessRequestId();
        List<JdbcProcessRequestJoined> jdbcProcessRequestJoinedList = jdbcProcessRequestRepository
            .getJoinedProcessRequestsByProcessRequestId(currentProcessRequestId);
        if (jdbcProcessRequestJoinedList.isEmpty())
        {
          processRequestListToReturn.add(convertToProcessRequestFromJdbcProcessRequest(currentJdbcProcessRequest));
        }
        else
        {
          processRequestListToReturn.add(convertToProcessRequestFromJdbcProcessRequestJoined(jdbcProcessRequestJoinedList));
        }
      }

      return processRequestListToReturn;
    }
    catch (Exception e)
    {
      throw new BpmRepositoryException(e.getMessage(), e);
    }
  }

  @Override
  public boolean updateAssignedUser(String processRequestId, UserId userId) throws BpmRepositoryException
  {
    try
    {
      Validate.notBlank(processRequestId, "Process Request Id can't be null!");
      Validate.notNull(userId, "User Id can't be null");

      LocalDateTime assignedTime = LocalDateTime.now(ZoneId.of("UTC+8"));

      return 1 == jdbcProcessRequestRepository.updateAssignedUserId(processRequestId, userId.getId(), assignedTime);
    }
    catch (Exception e)
    {
      throw new BpmRepositoryException(e.getMessage(), e);
    }
  }

  @Override
  public ProcessRequest getByProcessInstanceId(String processInstanceId) throws BpmRepositoryException
  {
    try
    {
      Validate.notBlank(processInstanceId);

      JdbcProcessRequest jdbcProcessRequest = jdbcProcessRequestRepository.getJdbcProcessRequestByProcessInstanceId(processInstanceId);

      if (jdbcProcessRequest == null)
      {
        return null;
      }

      List<JdbcProcessRequestJoined> jdbcProcessRequestJoinedList = jdbcProcessRequestRepository.getJoinedProcessRequestsByProcessRequestId(
          jdbcProcessRequest.getProcessRequestId());
      if (jdbcProcessRequestJoinedList.isEmpty())
      {
        return convertToProcessRequestFromJdbcProcessRequest(jdbcProcessRequest);
      }
      else
      {
        return convertToProcessRequestFromJdbcProcessRequestJoined(jdbcProcessRequestJoinedList);
      }
    }
    catch (NullPointerException | IllegalArgumentException | DbActionExecutionException e)
    {
      throw new BpmRepositoryException(e.getMessage(), e);
    }
  }

  @Override
  public boolean updateState(String processRequestId, ProcessRequestState processRequestState) throws BpmRepositoryException
  {
    try
    {
      Validate.notBlank(processRequestId, "Process Request Id can't be null!");
      Validate.notNull(processRequestState, "Process Request State can't be null");
      return 1 == jdbcProcessRequestRepository.updateState(processRequestId, ProcessRequestState.fromEnumToString(processRequestState));
    }
    catch (Exception e)
    {
      throw new BpmRepositoryException(e.getMessage(), e);
    }
  }

  @Override
  public int updateGroup(String processRequestId, String groupId)
  {
    Validate.notBlank(processRequestId, "Process Request Id can't be null!");
    Validate.notBlank(processRequestId, "Group Id can't be null!");
    return jdbcProcessRequestRepository.updateGroup(processRequestId, groupId);
  }

  @Override
  public boolean updateProcessInstanceId(String processRequestId, String processInstanceId) throws BpmRepositoryException
  {
    try
    {
      Validate.notBlank(processRequestId, "Process Request Id can't be blank!");
      Validate.notBlank(processInstanceId, "Process Instance Id can't be blank");
      return 1 == jdbcProcessRequestRepository.updateProcessInstanceId(processRequestId, processInstanceId);
    }
    catch (Exception e)
    {
      throw new BpmRepositoryException(e.getMessage(), e);
    }
  }

  @Override
  public Collection<ProcessRequest> findAllByCreatedDateInterval(String parameterValue, Date startCreatedDate, Date endCreatedDate)
      throws BpmRepositoryException
  {
    try
    {
      Collection<ProcessRequest> foundRequests = new ArrayList<>();

      List<JdbcProcessRequestJoined> jdbcProcessRequests = jdbcProcessRequestRepository
          .getJdbcProcessRequestsByCreatedDate(startCreatedDate, endCreatedDate, parameterValue);

      for (JdbcProcessRequestJoined joinedRequest : jdbcProcessRequests)
      {
        if (null != joinedRequest)
        {
          String requestId = joinedRequest.getProcessRequestId();

          List<JdbcProcessRequestJoined> joinedList = jdbcProcessRequestRepository
              .getJoinedProcessRequestsByProcessRequestId(requestId);

          if (!joinedList.isEmpty())
          {
            foundRequests.add(convertToProcessRequestFromJdbcProcessRequestJoined(joinedList));
          }
        }
      }
      return foundRequests;
    }
    catch (Exception e)
    {
      throw new BpmRepositoryException(e.getMessage(), e);
    }
  }

  @Override
  public Collection<ProcessRequest> findAllUnassignedProcessRequestsByChannelType(String channelType) throws BpmRepositoryException
  {
    if (StringUtils.isBlank(channelType))
    {
      throw new BpmRepositoryException("Invalid Channel Type!");
    }

    try
    {
      List<ProcessRequest> processRequestListToReturn = new ArrayList<>();

      List<JdbcProcessRequestJoined> jdbcProcessRequestJoinedList = jdbcProcessRequestRepository.getUnassignedProcessRequestsByChannel(channelType);

      for (JdbcProcessRequestJoined joinedRequest : jdbcProcessRequestJoinedList)
      {
        if (null != joinedRequest)
        {
          String requestId = joinedRequest.getProcessRequestId();

          List<JdbcProcessRequestJoined> joinedList = jdbcProcessRequestRepository
              .getJoinedProcessRequestsByProcessRequestId(requestId);

          if (!joinedList.isEmpty())
          {
            processRequestListToReturn.add(convertToProcessRequestFromJdbcProcessRequestJoined(joinedList));
          }
        }
      }

      return processRequestListToReturn;
    }
    catch (Exception e)
    {
      throw new BpmRepositoryException(e.getMessage());
    }
  }

  @Override
  public Collection<ProcessRequest> findAllUnassignedProcessRequestsByChannelType(String channelType, Date startDate, Date endDate)
      throws BpmRepositoryException
  {
    if (StringUtils.isBlank(channelType))
    {
      throw new BpmRepositoryException("Invalid Channel Type!");
    }

    try
    {
      List<ProcessRequest> processRequestListToReturn = new ArrayList<>();

      List<JdbcProcessRequestJoined> jdbcProcessRequestJoinedList = jdbcProcessRequestRepository.getUnassignedProcessRequestsByChannel(channelType, startDate,
          endDate);

      for (JdbcProcessRequestJoined joinedRequest : jdbcProcessRequestJoinedList)
      {
        if (null != joinedRequest)
        {
          String requestId = joinedRequest.getProcessRequestId();

          List<JdbcProcessRequestJoined> joinedList = jdbcProcessRequestRepository
              .getJoinedProcessRequestsByProcessRequestId(requestId);

          if (!joinedList.isEmpty())
          {
            processRequestListToReturn.add(convertToProcessRequestFromJdbcProcessRequestJoined(joinedList));
          }
        }
      }

      return processRequestListToReturn;
    }
    catch (Exception e)
    {
      throw new BpmRepositoryException(e.getMessage());
    }
  }

  @Override
  public Collection<ProcessRequest> findAllRequests()
  {
    List<JdbcProcessRequestJoined> allProcessRequests = jdbcProcessRequestRepository.getAllProcessRequests();
    if (allProcessRequests.isEmpty())
    {
      return Collections.emptyList();
    }

    return this.mapJdbcProcessRequestParameters(allProcessRequests);
  }

  @Override
  public Collection<ProcessRequest> findAllRequestsByDate(Date start, Date end)
  {
    List<JdbcProcessRequestJoined> allProcessRequests = jdbcProcessRequestRepository.getAllProcessRequestsByDate(start, end);
    if (allProcessRequests.isEmpty())
    {
      return Collections.emptyList();
    }

    return mapJdbcProcessRequestParameters(allProcessRequests);
  }

  @Override
  public Collection<ProcessRequest> getRequestsByAssignedUserId(String userId, Date startDate, Date endDate)
  {
    Validate.notNull(userId, USER_ID_REQUIRED_MESSAGE);

    List<JdbcProcessRequestJoined> processRequests = jdbcProcessRequestRepository.getProcessRequestsByAssignedUserId(userId, startDate, endDate);
    if (processRequests.isEmpty())
    {
      return Collections.emptyList();
    }

    return mapJdbcProcessRequestParameters(processRequests);
  }

  @Override
  public boolean updateParameters(String processRequestId, Map<String, Serializable> parameters) throws BpmRepositoryException
  {
    try
    {
      Validate.notBlank(processRequestId, "Process Request Id is required!");
      Validate.notNull(parameters);

      for (Map.Entry<String, Serializable> parameter : parameters.entrySet())
      {
        String parameterName = parameter.getKey();
        Serializable parameterValue = parameter.getValue();
        String parameterType = getParameterType(parameter.getValue());

        if (null == parameterType)
        {
          continue;
        }
        if (BIG_DECIMAL.equals(parameterType))
        {
          BigDecimal bigDecimal = getBigDecimalParameterValue(parameter.getValue());
          parameterValue = NumberUtils.bigDecimalToString(bigDecimal);
        }

        if (jdbcProcessRequestParameterRepository.getByParameterName(processRequestId, parameterName) != null)
        {
          jdbcProcessRequestParameterRepository.updateParameter(processRequestId, parameterName, parameterValue);
        }
        else
        {
          jdbcProcessRequestParameterRepository.insert(processRequestId, parameterName, parameterValue, parameterType);
        }

        //TODO return false after checking if it exists
      }

      return true;
    }
    catch (NullPointerException | IllegalArgumentException | DbActionExecutionException e)
    {
      throw new BpmRepositoryException(e.getMessage());
    }
  }

  @Override
  public ProcessRequest findById(EntityId entityId)
  {
    Validate.notNull(entityId, "Entity Id is required!");

    Optional jdbcProcessRequest = jdbcProcessRequestRepository.findById(entityId.getId());

    if (!jdbcProcessRequest.isPresent())
    {
      return null;
    }

    List<JdbcProcessRequestJoined> jdbcProcessRequestJoinedList = jdbcProcessRequestRepository.getJoinedProcessRequestsByProcessRequestId(entityId.getId());

    if (jdbcProcessRequestJoinedList.isEmpty())
    {
      return convertToProcessRequestFromJdbcProcessRequest((JdbcProcessRequest) jdbcProcessRequest.get());
    }

    return convertToProcessRequestFromJdbcProcessRequestJoined(jdbcProcessRequestJoinedList);
  }

  @Override
  public Collection<ProcessRequest> findAll()
  {
    List<ProcessRequest> processRequestListToReturn = new ArrayList<>();

    Iterator<JdbcProcessRequest> allJdbcProcessRequestsIterator = jdbcProcessRequestRepository.findAll().iterator();
    while (allJdbcProcessRequestsIterator.hasNext())
    {
      JdbcProcessRequest currentJdbcProcessRequest = allJdbcProcessRequestsIterator.next();
      String currentProcessRequestId = currentJdbcProcessRequest.getProcessRequestId();
      List<JdbcProcessRequestJoined> jdbcProcessRequestJoinedList = jdbcProcessRequestRepository
          .getJoinedProcessRequestsByProcessRequestId(currentProcessRequestId);
      if (jdbcProcessRequestJoinedList.isEmpty())
      {
        processRequestListToReturn.add(convertToProcessRequestFromJdbcProcessRequest(currentJdbcProcessRequest));
      }
      else
      {
        processRequestListToReturn.add(convertToProcessRequestFromJdbcProcessRequestJoined(jdbcProcessRequestJoinedList));
      }
    }

    return processRequestListToReturn;
  }

  @Override
  public int update14daysPassedProcessState(String processTypeId, LocalDateTime startDate, LocalDateTime endDate, String product, String state)
      throws BpmRepositoryException
  {
    try
    {
      return jdbcProcessRequestRepository.updateProcessState14days(processTypeId, startDate, endDate, product, state);
    }
    catch (DbActionExecutionException e)
    {
      throw new BpmRepositoryException(e.getMessage());
    }
  }

  @Override
  public Collection<ProcessRequest> get14daysPassedRequestsAndDelete(String processType, LocalDateTime startDate,
      LocalDateTime endDate, String processTypeId)
  {
    List<ProcessRequest> processRequestListToReturn = new ArrayList<>();

    List<JdbcProcessRequest> processRequests = jdbcProcessRequestRepository.get14daysPassedRequests(processType, startDate, endDate, processTypeId);
    for (JdbcProcessRequest request : processRequests)
    {
      String processRequestId = request.getProcessRequestId();
      jdbcProcessRequestRepository.deleteByProcessRequestId(processRequestId);
      jdbcProcessRequestParameterRepository.deleteByProcessRequestId(processRequestId);
      processRequestListToReturn.add(convertToProcessRequestFromJdbcProcessRequest(request));
    }
    return processRequestListToReturn;
  }

  @Override
  public int update24hPassedProcessState(String cifNumber, LocalDateTime startDate, LocalDateTime endDate, String processType, String state)
      throws BpmRepositoryException
  {
    try
    {
      if (StringUtils.equals(processType, "onlineLeasing"))
      {
        return jdbcProcessRequestRepository.updateOnlineLeasingProcessState(cifNumber, startDate, endDate, processType, state);
      }
      if (!StringUtils.equals(processType, "bnplLoan"))
      {
        return jdbcProcessRequestRepository.updateProcessState(cifNumber, startDate, endDate, processType, state);
      }
      else
      {
        return jdbcProcessRequestRepository.updateBnplProcessState(cifNumber, startDate, endDate, processType, state);
      }
    }
    catch (DbActionExecutionException e)
    {
      throw new BpmRepositoryException(e.getMessage());
    }
  }

  @Override
  public int update24hPassedRequestStateExcludingConfirmed(String cifNumber, LocalDateTime startDate, LocalDateTime endDate, String processType, String state)
      throws BpmRepositoryException
  {
    try
    {
      return jdbcProcessRequestRepository.update24hPassedRequestStateExcludingConfirmed(cifNumber, startDate, endDate, processType, state);
    }
    catch (DbActionExecutionException e)
    {
      throw new BpmRepositoryException(e.getMessage());
    }
  }

  @Override
  public List<ProcessRequest> getGivenTimePassedProcessRequests(String processType, String cifNumber, LocalDateTime startDate, LocalDateTime endDate)
  {
    List<ProcessRequest> processRequestListToReturn = new ArrayList<>();

    List<JdbcProcessRequest> processRequests = jdbcProcessRequestRepository.getGivenTimePassedRequests(processType, cifNumber, startDate, endDate);
    for (JdbcProcessRequest request : processRequests)
    {
      processRequestListToReturn.add(convertToProcessRequestFromJdbcProcessRequest(request));
    }
    return processRequestListToReturn;
  }

  @Override
  public Collection<ProcessRequest> getBnplGivenTimePassedProcessRequests(String processType, String cifNumber, LocalDateTime startDate, LocalDateTime endDate)
  {
    List<ProcessRequest> outputProcessRequests = new ArrayList<>();
    List<JdbcProcessRequest> processRequests = jdbcProcessRequestRepository.getBnplGivenTimePassedRequests(processType, cifNumber, startDate, endDate);

    for (JdbcProcessRequest request : processRequests)
    {
      outputProcessRequests.add(convertToProcessRequestFromJdbcProcessRequest(request));
    }
    return outputProcessRequests;
  }

  @Override
  public Map<String, Serializable> getRequestParametersByRequestId(String requestId)
  {
    List<JdbcProcessRequestParameter> parameters = jdbcProcessRequestRepository.getProcessParameters(requestId);
    return convert(parameters);
  }

  public Map<String, Serializable> convert(List<JdbcProcessRequestParameter> parameters)
  {
    Map<String, Serializable> parametersMap = new HashMap<>();
    for (JdbcProcessRequestParameter parameter : parameters)
    {
      parametersMap.put(parameter.getParameterName(), parameter.getParameterValue());
    }
    return parametersMap;
  }

  @Override
  public List<ProcessRequest> getInstantLoanProcessRequestsByCifNumber(String cifNumber)
  {
    List<ProcessRequest> outputProcessRequests = new ArrayList<>();
    List<JdbcProcessRequest> processRequests = jdbcProcessRequestRepository.getInstantLoanRequestsByCifNumber(cifNumber, "instantLoan");

    for (JdbcProcessRequest request : processRequests)
    {
      outputProcessRequests.add(convertToProcessRequestFromJdbcProcessRequest(request));
    }
    return outputProcessRequests;
  }

  @Override
  public String getParameterByName(String requestId, String parameterName)
  {
    JdbcProcessRequestParameter result = this.jdbcProcessRequestParameterRepository.getByParameterName(requestId, parameterName);
    return null == result ? null : result.getParameterValue();
  }

  @Override
  public Collection<ProcessRequest> getDirectOnlineProcessRequests(String cifNumber, LocalDateTime startDate, LocalDateTime endDate, String processType)
      throws BpmRepositoryException
  {
    try
    {
      Validate.notBlank(cifNumber, "Cif Number is required!");
      List<JdbcProcessRequestJoined> processRequests = jdbcProcessRequestRepository.getProcessRequestsOnlineDirect(cifNumber, startDate, endDate,
          processType);
      if (processRequests.isEmpty())
      {
        return Collections.emptyList();
      }

      return mapJdbcProcessRequestParameters(processRequests);
    }
    catch (NullPointerException | IllegalArgumentException | DbActionExecutionException e)
    {
      throw new BpmRepositoryException(e.getMessage());
    }
  }

  @Override
  public Collection<ProcessRequest> getProcessRequestsOnlineLeasing(String cifNumber, LocalDateTime startDate, LocalDateTime endDate, String processType,
      String productCategory)
      throws BpmRepositoryException
  {
    try
    {
      Validate.notBlank(cifNumber, "Cif Number is required!");
      List<JdbcProcessRequestJoined> processRequests = jdbcProcessRequestRepository.getProcessRequestsOnlineLeasing(cifNumber, startDate, endDate,
          processType, productCategory);
      if (processRequests.isEmpty())
      {
        return Collections.emptyList();
      }

      return mapJdbcProcessRequestParameters(processRequests);
    }
    catch (NullPointerException | IllegalArgumentException | DbActionExecutionException e)
    {
      throw new BpmRepositoryException(e.getMessage());
    }
  }

  private ProcessRequest convertToProcessRequestFromJdbcProcessRequestJoined(List<JdbcProcessRequestJoined> jdbcProcessRequestJoinedList)
  {
    Map<String, Serializable> parameters = new HashMap<>();
    Iterator<JdbcProcessRequestJoined> jdbcProcessRequestJoinedIterator = jdbcProcessRequestJoinedList.iterator();
    while (jdbcProcessRequestJoinedIterator.hasNext())
    {
      JdbcProcessRequestJoined current = jdbcProcessRequestJoinedIterator.next();
      Serializable parameterValue = saveAsRightType(current.getParameterValue(), current.getParameterType());
      parameters.put(current.getParameterName(), parameterValue);
    }

    JdbcProcessRequestJoined firstJdbcProcessRequestJoined = jdbcProcessRequestJoinedList.get(0);
    ProcessRequestId processRequestId = new ProcessRequestId(firstJdbcProcessRequestJoined.getProcessRequestId());
    ProcessTypeId processTypeId = new ProcessTypeId(firstJdbcProcessRequestJoined.getProcessTypeId());
    GroupId groupId = new GroupId(firstJdbcProcessRequestJoined.getGroupId());
    String requestedUserId = firstJdbcProcessRequestJoined.getRequestedUserId();
    String userId = requestedUserId;
    ProcessRequestState state = ProcessRequestState.fromStringToEnum(firstJdbcProcessRequestJoined.getProcessRequestState());
    LocalDateTime createdTime = firstJdbcProcessRequestJoined.getCreatedTime();

    LocalDateTime assignedTime = firstJdbcProcessRequestJoined.getAssignedTime();
    String processInstanceId = firstJdbcProcessRequestJoined.getProcessInstanceId();
    UserId assignedUserId = null;

    if (firstJdbcProcessRequestJoined.getAssignedUserId() != null)
    {
      assignedUserId = new UserId(firstJdbcProcessRequestJoined.getAssignedUserId());
      userId = assignedUserId.getId();
    }

    ProcessRequest processRequestToReturn = new ProcessRequest(processRequestId, processTypeId, groupId, userId, createdTime, state, parameters);
    processRequestToReturn.setAssignedUserId(assignedUserId);
    processRequestToReturn.setProcessInstanceId(processInstanceId);
    processRequestToReturn.setAssignedTime(assignedTime);
    return processRequestToReturn;
  }

  private ProcessRequest convertToProcessRequestFromJdbcProcessRequest(JdbcProcessRequest jdbcProcessRequest)
  {
    ProcessRequestId processRequestId = new ProcessRequestId(jdbcProcessRequest.getProcessRequestId());
    ProcessTypeId processTypeId = new ProcessTypeId(jdbcProcessRequest.getProcessTypeId());
    GroupId groupId = new GroupId(jdbcProcessRequest.getGroupId());
    String requestedUserId = jdbcProcessRequest.getRequestedUserId();
    ProcessRequestState state = ProcessRequestState.fromStringToEnum(jdbcProcessRequest.getProcessRequestState());
    LocalDateTime createdTime = jdbcProcessRequest.getCreatedTime();
    Map<String, Serializable> parameters = new HashMap<>();

    LocalDateTime assignedTime = jdbcProcessRequest.getAssignedTime();
    String processInstanceId = jdbcProcessRequest.getProcessInstanceId();
    UserId assignedUserId = null;
    if (jdbcProcessRequest.getAssignedUserId() != null)
    {
      assignedUserId = new UserId(jdbcProcessRequest.getAssignedUserId());
    }

    ProcessRequest processRequestToReturn = new ProcessRequest(processRequestId, processTypeId, groupId, requestedUserId, createdTime, state, parameters);
    processRequestToReturn.setAssignedUserId(assignedUserId);
    processRequestToReturn.setProcessInstanceId(processInstanceId);
    processRequestToReturn.setAssignedTime(assignedTime);

    return processRequestToReturn;
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

  private String getParameterType(Serializable parameter)
  {
    if (parameter instanceof String)
    {
      return "String";
    }
    else if (parameter instanceof Integer)
    {
      return "Integer";
    }
    else if (parameter instanceof BigDecimal)
    {
      return BIG_DECIMAL;
    }
    else if (parameter instanceof Double)
    {
      return BIG_DECIMAL;
    }
    else if (parameter instanceof Long)
    {
      return "Long";
    }
    else if (parameter instanceof Boolean)
    {
      return "Boolean";
    }
    else if (null == parameter)
    {
      return null;
    }
    throw new IllegalArgumentException("Not supported parameter type!");
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
        LOGGER.error(e.getMessage(), e);
        return null;
      }
    case "Boolean":
      return Boolean.valueOf(value);
    default:
      return value;
    }
  }

  private Collection<ProcessRequest> mapJdbcProcessRequestParameters(List<JdbcProcessRequestJoined> allProcessRequests)
  {
    Map<String, ProcessRequest> processRequestMap = new HashMap<>();
    for (JdbcProcessRequestJoined processRequest : allProcessRequests)
    {
      if (processRequestMap.containsKey(processRequest.getProcessRequestId()))
      {
        setRequestParamToMap(processRequestMap, processRequest);
      }
      else
      {
        setNewRequestToMap(processRequestMap, processRequest);
      }
    }

    return processRequestMap.values();
  }

  private void setNewRequestToMap(Map<String, ProcessRequest> processRequestMap, JdbcProcessRequestJoined processRequest)
  {
    ProcessRequest request = new ProcessRequest(
        ProcessRequestId.valueOf(processRequest.getProcessRequestId()), ProcessTypeId.valueOf(processRequest.getProcessTypeId()),
        GroupId.valueOf(processRequest.getGroupId()), processRequest.getRequestedUserId(), processRequest.getCreatedTime(),
        ProcessRequestState.fromStringToEnum(processRequest.getProcessRequestState()), new HashMap<>()
    );
    request.setProcessInstanceId(processRequest.getProcessInstanceId());
    if (null != processRequest.getAssignedTime())
    {
      request.setAssignedTime(processRequest.getAssignedTime());
    }
    if (!StringUtils.isBlank(processRequest.getAssignedUserId()))
    {
      request.setAssignedUserId(UserId.valueOf(processRequest.getAssignedUserId()));
    }

    /* Fixed logic where loan description left from the parameters upon adding a column*/
    if (StringUtils.equals("loanProductDescription", processRequest.getParameterName()))
    {
      request.addParameterToMap("loanProductDescription", processRequest.getParameterValue());
    }
    if (StringUtils.equals("amount", processRequest.getParameterName()))
    {
      request.addParameterToMap("amount", processRequest.getParameterValue());
    }

    processRequestMap.put(processRequest.getProcessRequestId(), request);
  }

  private void setRequestParamToMap(Map<String, ProcessRequest> processRequestMap, JdbcProcessRequestJoined request)
  {
    ProcessRequest processRequest = processRequestMap.get(request.getProcessRequestId());
    processRequest.addParameterToMap(request.getParameterName(), request.getParameterValue());
  }
}
