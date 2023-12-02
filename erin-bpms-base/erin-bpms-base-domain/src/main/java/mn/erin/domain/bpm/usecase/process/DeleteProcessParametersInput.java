package mn.erin.domain.bpm.usecase.process;

import java.util.List;
import java.util.Map;

import mn.erin.domain.bpm.model.process.ParameterEntityType;

/**
 * @author Tamir
 */
public class DeleteProcessParametersInput
{
  private final String processInstanceId;
  private final Map<ParameterEntityType, List<String>> deleteParams;

  public DeleteProcessParametersInput(String processInstanceId,
      Map<ParameterEntityType, List<String>> deleteParams)
  {
    this.processInstanceId = processInstanceId;
    this.deleteParams = deleteParams;
  }

  public String getProcessInstanceId()
  {
    return processInstanceId;
  }

  public Map<ParameterEntityType, List<String>> getDeleteParameters()
  {
    return deleteParams;
  }
}
