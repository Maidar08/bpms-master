package mn.erin.domain.bpm.usecase.organization;

public class GenerateOrgRequestIdInput
{
  private String branchId;
  private String regNumber;

  public GenerateOrgRequestIdInput(String branchId, String regNumber)
  {
    this.branchId = branchId;
    this.regNumber = regNumber;
  }

  public String getBranchId()
  {
    return branchId;
  }

  public void setBranchId(String branchId)
  {
    this.branchId = branchId;
  }

  public String getRegNumber()
  {
    return regNumber;
  }

  public void setRegNumber(String regNumber)
  {
    this.regNumber = regNumber;
  }
}
