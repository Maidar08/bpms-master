package mn.erin.bpm.repository.jdbc.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author EBazarragchaa
 */
@Table("PROCESS_REQUEST_PARAMETER")
public class JdbcProcessRequestParameter
{
  @Id
  private String processRequestId;
  private String parameterName;
  private String parameterValue;

  /**
   * String, Integer, BigDecimal etc.
   */
  private String parameterType;

  public String getProcessRequestId()
  {
    return processRequestId;
  }

  public void setProcessRequestId(String processRequestId)
  {
    this.processRequestId = processRequestId;
  }

  public String getParameterName()
  {
    return parameterName;
  }

  public void setParameterName(String parameterName)
  {
    this.parameterName = parameterName;
  }

  public String getParameterValue()
  {
    return parameterValue;
  }

  public void setParameterValue(String parameterValue)
  {
    this.parameterValue = parameterValue;
  }

  public String getParameterType()
  {
    return parameterType;
  }

  public void setParameterType(String parameterType)
  {
    this.parameterType = parameterType;
  }
}
