package mn.erin.domain.bpm.usecase.organization;

import java.util.Collection;

import mn.erin.domain.bpm.model.organization.OrganizationRequest;

/**
 * @author Tamir
 */
public class GetAllOrganizationRequestsOutput
{
  private Collection<OrganizationRequest> organizationRequests;

  public GetAllOrganizationRequestsOutput(Collection<OrganizationRequest> organizationRequests)
  {
    this.organizationRequests = organizationRequests;
  }

  public Collection<OrganizationRequest> getOrganizationRequests()
  {
    return organizationRequests;
  }

  public void setOrganizationRequests(Collection<OrganizationRequest> organizationRequests)
  {
    this.organizationRequests = organizationRequests;
  }
}
