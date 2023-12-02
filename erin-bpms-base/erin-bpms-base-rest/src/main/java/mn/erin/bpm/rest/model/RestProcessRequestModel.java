package mn.erin.bpm.rest.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Lkhagvadorj.A
 **/

/**
 * This rest model is identical to RestProcessRequest except parameters.
 * RestProcessRequest's parameters were serializable and there is no need to be serializable.
 */
public class RestProcessRequestModel
{
  private String processTypeId;
  private String groupNumber;
  private String createdUserId;
  private Map<String, Object> parameters = new HashMap<>();

  public String getProcessTypeId()
  {
    return processTypeId;
  }

  public void setProcessTypeId(String processTypeId)
  {
    this.processTypeId = processTypeId;
  }

  public String getGroupNumber()
  {
    return groupNumber;
  }

  public void setGroupNumber(String groupNumber)
  {
    this.groupNumber = groupNumber;
  }

  public String getCreatedUserId()
  {
    return createdUserId;
  }

  public void setCreatedUserId(String createdUserId)
  {
    this.createdUserId = createdUserId;
  }

  public Map<String, Object> getParameters()
  {
    return parameters;
  }

  public void setParameters(Map<String, Object> parameters)
  {
    this.parameters = parameters;
  }
}
