package mn.erin.bpm.rest.model;

import java.io.Serializable;
import java.util.Map;

import mn.erin.domain.bpm.model.process.ParameterEntityType;

/**
 * @author Zorig
 */
public class RestProcess
{
  private String processInstanceId;
  private String startedTime;
  private String finishedTime;
  private Map<ParameterEntityType, Map<String, Serializable>> processParameters;

  public RestProcess()
  {
    // no needed to do something
  }

  public String getProcessInstanceId()
  {
    return processInstanceId;
  }

  public void setProcessInstanceId(String processInstanceId)
  {
    this.processInstanceId = processInstanceId;
  }

  public String getStartedTime()
  {
    return startedTime;
  }

  public void setStartedTime(String startedTime)
  {
    this.startedTime = startedTime;
  }

  public String getFinishedTime()
  {
    return finishedTime;
  }

  public void setFinishedTime(String finishedTime)
  {
    this.finishedTime = finishedTime;
  }

  public Map<ParameterEntityType, Map<String, Serializable>> getProcessParameters()
  {
    return processParameters;
  }

  public void setProcessParameters(
      Map<ParameterEntityType, Map<String, Serializable>> processParameters)
  {
    this.processParameters = processParameters;
  }
}
