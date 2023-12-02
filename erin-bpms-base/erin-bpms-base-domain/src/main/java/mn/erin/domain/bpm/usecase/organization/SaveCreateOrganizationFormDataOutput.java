package mn.erin.domain.bpm.usecase.organization;

public class SaveCreateOrganizationFormDataOutput
{
  private Integer numberOfColsUpdated;

  public SaveCreateOrganizationFormDataOutput(Integer numberOfColsUpdated)
  {
    this.numberOfColsUpdated = numberOfColsUpdated;
  }

  public Integer getNumberOfColsUpdated()
  {
    return numberOfColsUpdated;
  }

  public void setNumberOfColsUpdated(Integer numberOfColsUpdated)
  {
    this.numberOfColsUpdated = numberOfColsUpdated;
  }
}
