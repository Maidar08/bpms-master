package mn.erin.domain.bpm.model.organization;

import java.time.LocalDateTime;

import mn.erin.domain.base.model.Entity;
import mn.erin.domain.bpm.model.process.ProcessInstanceId;

/**
 * @author Tamir
 */
public class OrganizationRequest implements Entity<OrganizationRequest>
{
  private OrganizationRequestId id;
  private ProcessInstanceId instanceId;
  private String branchId;

  private String organizationName;
  private String organizationNumber;

  private String cifNumber;
  private String state;

  private LocalDateTime contractDate;
  private String assignee;
  private String confirmedUser;

  public OrganizationRequest(OrganizationRequestId id)
  {
    this.id = id;
  }

  public OrganizationRequest(OrganizationRequestId id, String assignee, String organizationName, String organizationNumber,
      LocalDateTime contractDate, String cifNumber, String branchId, String state, String confirmedUser)
  {
    this.id = id;
    this.assignee = assignee;
    this.organizationNumber = organizationNumber;
    this.organizationName = organizationName;
    this.contractDate = contractDate;
    this.cifNumber = cifNumber;
    this.branchId = branchId;
    this.state = state;
    this.confirmedUser = confirmedUser;
  }

  public OrganizationRequest(OrganizationRequestId id, ProcessInstanceId instanceId, String assignee, String organizationName, String organizationNumber,
      LocalDateTime contractDate, String cifNumber, String branchId, String state, String confirmedUser)
  {
    this.id = id;
    this.instanceId = instanceId;
    this.assignee = assignee;
    this.organizationNumber = organizationNumber;
    this.organizationName = organizationName;
    this.contractDate = contractDate;
    this.cifNumber = cifNumber;
    this.branchId = branchId;
    this.state = state;
    this.confirmedUser = confirmedUser;
  }

  public OrganizationRequestId getId()
  {
    return id;
  }

  public void setId(OrganizationRequestId id)
  {
    this.id = id;
  }

  public String getAssignee()
  {
    return assignee;
  }

  public void setAssignee(String assignee)
  {
    this.assignee = assignee;
  }

  public String getOrganizationNumber()
  {
    return organizationNumber;
  }

  public void setOrganizationNumber(String organizationNumber)
  {
    this.organizationNumber = organizationNumber;
  }

  public String getOrganizationName()
  {
    return organizationName;
  }

  public void setOrganizationName(String organizationName)
  {
    this.organizationName = organizationName;
  }

  public LocalDateTime getContractDate()
  {
    return contractDate;
  }

  public void setContractDate(LocalDateTime contractDate)
  {
    this.contractDate = contractDate;
  }

  public String getCifNumber()
  {
    return cifNumber;
  }

  public void setCifNumber(String cifNumber)
  {
    this.cifNumber = cifNumber;
  }

  public String getBranchId()
  {
    return branchId;
  }

  public void setBranchId(String branchId)
  {
    this.branchId = branchId;
  }

  public String getState()
  {
    return state;
  }

  public void setState(String state)
  {
    this.state = state;
  }

  public String getConfirmedUser()
  {
    return confirmedUser;
  }

  public void setConfirmedUser(String confirmedUser)
  {
    this.confirmedUser = confirmedUser;
  }

  public ProcessInstanceId getInstanceId() { return instanceId; }

  public void setInstanceId(ProcessInstanceId instanceId)
  {
    this.instanceId = instanceId;
  }

  @Override
  public boolean sameIdentityAs(OrganizationRequest other)
  {
    return other != null && (this.id.equals(other.id));
  }
}
