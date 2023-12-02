package mn.erin.domain.bpm.usecase.organization;

import java.util.Collection;

import mn.erin.domain.bpm.model.organization.OrganizationRequestExcel;

public class GetAllOrganizationRequestsOutputExcel
{
  private Collection<OrganizationRequestExcel> organizationRequests;

  public GetAllOrganizationRequestsOutputExcel(Collection<OrganizationRequestExcel> organizationRequests)
  {
    this.organizationRequests = organizationRequests;
  }

  public Collection<OrganizationRequestExcel> getOrganizationRequests()
  {
    return organizationRequests;
  }

  public void setOrganizationRequests(Collection<OrganizationRequestExcel> organizationRequests)
  {
    this.organizationRequests = organizationRequests;
  }
}
