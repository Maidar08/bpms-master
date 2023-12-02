package mn.erin.domain.bpm.model;

/**
 * @author Zorig
 */
public class OrganizationInfo
{
  private String organizationLevel;

  public OrganizationInfo(String organizationLevel)
  {
    this.organizationLevel = organizationLevel;
  }

  public String getOrganizationLevel()
  {
    return organizationLevel;
  }

  public void setOrganizationLevel(String organizationLevel)
  {
    this.organizationLevel = organizationLevel;
  }
}
