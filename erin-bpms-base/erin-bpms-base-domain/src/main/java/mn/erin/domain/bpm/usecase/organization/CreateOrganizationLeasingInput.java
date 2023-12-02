package mn.erin.domain.bpm.usecase.organization;

import java.time.LocalDate;
import java.util.Date;

/**
 * @author Bilguunbor
 */
public class CreateOrganizationLeasingInput
{
  private String organizationType;
  private String cif;
  private String organizationName;
  private String registerNumber;
  private LocalDate establishedDate;
  private String branchNumber;
  private String assignee;
  private String processInstanceId;
  private Date createdDate;

  public CreateOrganizationLeasingInput(String organizationType, String cif, String organizationName, String registerNumber, LocalDate establishedDate,
      String branchNumber, String assignee, String processInstanceId, Date createdDate)
  {
    this.organizationType = organizationType;
    this.cif = cif;
    this.organizationName = organizationName;
    this.registerNumber = registerNumber;
    this.establishedDate = establishedDate;
    this.branchNumber = branchNumber;
    this.assignee = assignee;
    this.processInstanceId = processInstanceId;
    this.createdDate = createdDate;
  }

  public String getOrganizationType()
  {
    return organizationType;
  }

  public void setOrganizationType(String organizationType)
  {
    this.organizationType = organizationType;
  }

  public String getCif()
  {
    return cif;
  }

  public void setCif(String cif)
  {
    this.cif = cif;
  }

  public String getRegisterNumber()
  {
    return registerNumber;
  }

  public void setRegisterNumber(String registerNumber)
  {
    this.registerNumber = registerNumber;
  }

  public String getBranchNumber()
  {
    return branchNumber;
  }

  public void setBranchNumber(String branchNumber)
  {
    this.branchNumber = branchNumber;
  }

  public LocalDate getEstablishedDate()
  {
    return establishedDate;
  }

  public void setEstablishedDate(LocalDate establishedDate)
  {
    this.establishedDate = establishedDate;
  }

  public String getOrganizationName()
  {
    return organizationName;
  }

  public void setOrganizationName(String organizationName)
  {
    this.organizationName = organizationName;
  }

  public String getAssignee()
  {
    return assignee;
  }

  public void setAssignee(String assignee)
  {
    this.assignee = assignee;
  }

  public String getProcessInstanceId()
  {
    return processInstanceId;
  }

  public void setProcessInstanceId(String processInstanceId)
  {
    this.processInstanceId = processInstanceId;
  }

  public Date getCreatedDate() { return createdDate; }

  public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
}
