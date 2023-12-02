package mn.erin.domain.bpm.usecase;

public class GetGeneralInfoInput
{

  private String processType;
  private String entity;

  public GetGeneralInfoInput(String processType, String entity)
  {
    this.processType = processType;
    this.entity = entity;
  }

  public String getProcessType()
  {
    return processType;
  }

  public void setProcessType(String processType)
  {
    this.processType = processType;
  }

  public String getEntity()
  {
    return entity;
  }

  public void setEntity(String entity)
  {
    this.entity = entity;
  }
}
