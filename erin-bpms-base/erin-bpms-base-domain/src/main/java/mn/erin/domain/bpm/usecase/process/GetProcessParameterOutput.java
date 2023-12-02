package mn.erin.domain.bpm.usecase.process;

import java.io.Serializable;

/**
 * @author Tamir
 */
public class GetProcessParameterOutput
{
  private Serializable parameterValue;

  public GetProcessParameterOutput(Serializable parameterValue)
  {
    this.parameterValue = parameterValue;
  }

  public Serializable getParameterValue()
  {
    return parameterValue;
  }

  public void setParameterValue(Serializable parameterValue)
  {
    this.parameterValue = parameterValue;
  }
}
