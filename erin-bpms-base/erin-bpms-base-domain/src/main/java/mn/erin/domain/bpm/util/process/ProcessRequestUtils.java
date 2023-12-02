package mn.erin.domain.bpm.util.process;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.model.process.ProcessRequestState;

/**
 * @author Lkhagvadorj
 */
public class ProcessRequestUtils
{
  private ProcessRequestUtils()
  {

  }

  private static final String  DELETED = "DELETED";

  public static Collection<ProcessRequest> filterProcessRequestByState(Collection<ProcessRequest> processRequests)
  {
    return processRequests.stream().filter(processRequest -> {
      ProcessRequestState processRequestState = processRequest.getState();
      return !ProcessRequestState.fromEnumToString(processRequestState).equals(DELETED);
    }).collect(Collectors.toList());
  }

  public static Collection<ProcessRequest> findBySearchKey(Collection<ProcessRequest> processRequests, String searchKey)
  {
    searchKey = searchKey.replaceAll("^\\s+", "");
    List<ProcessRequest> filteredRequests = new ArrayList<>();
    for (ProcessRequest processRequest : processRequests)
    {

      if (matchRequest(processRequest, searchKey)) {
        filteredRequests.add(processRequest);
      }
    }

    return filteredRequests;
  }

  private static boolean matchRequest(ProcessRequest processRequest, String searchKey)
  {
    if (null != processRequest.getId() && processRequest.getId().getId().equals(searchKey)) {
      return true;
    }

    if (null != processRequest.getProcessTypeId())
    {
      String processType = BpmModuleConstants.PROCESS_TYPES.get(processRequest.getProcessTypeId().getId());

      if (null != processType && processType.equals(searchKey))
      {
        return true;
      }
    }

    if (null != processRequest.getGroupId() && processRequest.getGroupId().getId().equals(searchKey))
    {
      return true;
    }

    if (null != processRequest.getRequestedUserId() && processRequest.getRequestedUserId().equals(searchKey))
    {
      return true;
    }

    if (null != processRequest.getCreatedTime())
    {
      String processCreatedDate = String.valueOf(processRequest.getCreatedTime().toLocalDate());
      if (processCreatedDate.equals(searchKey))
      {
        return true;
      }
    }

    if (null != processRequest.getAssignedUserId() && processRequest.getAssignedUserId().getId().equals(searchKey))
    {
      return true;
    }

    if (null != processRequest.getProcessInstanceId() && processRequest.getProcessInstanceId().equals(searchKey))
    {
      return true;
    }

    if (null != processRequest.getAssignedTime())
    {
      String assignedTime = String.valueOf(processRequest.getAssignedTime().toLocalDate());
      if (assignedTime.equals(searchKey))
      {
        return true;
      }
    }

    if (null != processRequest.getState())
    {
      String state = ProcessRequestState.fromEnumToString(processRequest.getState());
      String processState = BpmModuleConstants.PROCESS_REQUEST_STATE_MAP.get(state);
      if (!StringUtils.isBlank(processState) && processState.equals(searchKey))
      {
        return true;
      }
    }

    return matchSearchKeyInParameter(processRequest.getParameters(), searchKey);
  }

  private static boolean matchSearchKeyInParameter(Map<String, Serializable> parameters, String searchKey)
  {
    for (Map.Entry<String, Serializable> param : parameters.entrySet())
    {
      String value = String.valueOf(param.getValue());
      if (value.equalsIgnoreCase(searchKey))
      {
        return true;
      }
    }

    return false;
  }

}
