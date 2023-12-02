package mn.erin.domain.bpm.usecase.customer;

public class GetCustomerByPersonIdAndTypeInput
{
  private String custId;
  private String custType;
  private String regNumber;

  public GetCustomerByPersonIdAndTypeInput(String custId, String custType, String regNumber)
  {
    this.custId = custId;
    this.custType = custType;
    this.regNumber = regNumber;
  }

  public String getRegNumber()
  {
    return regNumber;
  }

  public void setRegNumber(String regNumber)
  {
    this.regNumber = regNumber;
  }

  public String getCustType()
  {
    return custType;
  }

  public void setCustType(String custType)
  {
    this.custType = custType;
  }

  public String getCustId()
  {
    return custId;
  }

  public void setCustId(String custId)
  {
    this.custId = custId;
  }
}
