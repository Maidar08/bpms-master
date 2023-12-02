package mn.erin.domain.bpm.usecase.contract;

public class UpdateLoanContractRequestInput
{
  private String processInstanceId;
  private String processRequestId;
  private String parameterValue;

  public UpdateLoanContractRequestInput(String processInstanceId, String parameterValue)
  {
    this.processInstanceId = processInstanceId;
    this.parameterValue = parameterValue;
  }

  public UpdateLoanContractRequestInput(String processInstanceId, String processRequestId, String parameterValue)
  {
    this.processInstanceId = processInstanceId;
    this.processRequestId = processRequestId;
    this.parameterValue = parameterValue;
  }

  public void setProcessInstanceId(String processInstanceId)
  {
    this.processInstanceId = processInstanceId;
  }

  public void setProcessRequestId(String processRequestId)
  {
    this.processRequestId = processRequestId;
  }

  public void setParameterValue(String parameterValue)
  {
    this.parameterValue = parameterValue;
  }

  public String getProcessInstanceId()
  {
    return processInstanceId;
  }

  public String getProcessRequestId()
  {
    return processRequestId;
  }

  public String getParameterValue()
  {
    return parameterValue;
  }
}
