package mn.erin.domain.bpm.usecase.process;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Zorig
 */
public class UpdateRequestParametersInput
{
  private final String processRequestId;
  private final Map<String, Serializable> parameters;

  public UpdateRequestParametersInput(String processRequestId, Map<String, Serializable> parameters)
  {
    this.processRequestId = processRequestId;
    this.parameters = parameters;
  }

  public String getProcessRequestId()
  {
    return processRequestId;
  }

  public Map<String, Serializable> getParameters()
  {
    return parameters;
  }
}
