package mn.erin.domain.bpm.usecase.organization;

import java.util.List;

import mn.erin.domain.bpm.model.organization.OrganizationRequestExcel;

/**
 * @author Bilguunbor
 */
public class CreateOrganizationRequestsExcelInput
{
  private String topHeader;
  private String searchKey;
  private String groupId;
  private String stringDate;
  private List<OrganizationRequestExcel> organizationRequests;

  public CreateOrganizationRequestsExcelInput(String topHeader, String searchKey, String groupId, String stringDate,
      List<OrganizationRequestExcel> organizationRequests)
  {
    this.topHeader = topHeader;
    this.searchKey = searchKey;
    this.groupId = groupId;
    this.stringDate = stringDate;
    this.organizationRequests = organizationRequests;
  }

  public String getTopHeader()
  {
    return topHeader;
  }

  public void setTopHeader(String topHeader)
  {
    this.topHeader = topHeader;
  }

  public String getSearchKey()
  {
    return searchKey;
  }

  public void setSearchKey(String searchKey)
  {
    this.searchKey = searchKey;
  }

  public String getGroupId()
  {
    return groupId;
  }

  public void setGroupId(String groupId)
  {
    this.groupId = groupId;
  }

  public String getStringDate()
  {
    return stringDate;
  }

  public void setStringDate(String stringDate)
  {
    this.stringDate = stringDate;
  }

  public List<OrganizationRequestExcel> getOrganizationRequests()
  {
    return organizationRequests;
  }

  public void setOrganizationRequests(List<OrganizationRequestExcel> organizationRequests)
  {
    this.organizationRequests = organizationRequests;
  }
}
