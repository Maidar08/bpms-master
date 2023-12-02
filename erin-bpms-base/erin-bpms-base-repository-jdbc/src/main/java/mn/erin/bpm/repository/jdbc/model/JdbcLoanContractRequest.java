package mn.erin.bpm.repository.jdbc.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author Temuulen Naranbold
 */
@Table("LOAN_CONTRACT_PROCESS_REQUEST")
public class JdbcLoanContractRequest
{
  @Id
  String loanContractId;
  String processInstanceId;
  String loanContractType;
  String loanAccount;
  BigDecimal loanAmount;
  LocalDateTime createdDate;
  String assignedUserId;
  String groupId;
  String tenantId;
  String state;
  String cifNumber;
  String productDescription;

  public String getLoanContractId()
  {
    return loanContractId;
  }

  public void setLoanContractId(String loanContractId)
  {
    this.loanContractId = loanContractId;
  }

  public String getProcessInstanceId()
  {
    return processInstanceId;
  }

  public void setProcessInstanceId(String processInstanceId)
  {
    this.processInstanceId = processInstanceId;
  }

  public String getLoanContractType()
  {
    return loanContractType;
  }

  public void setLoanContractType(String loanContractType)
  {
    this.loanContractType = loanContractType;
  }

  public String getLoanAccount()
  {
    return loanAccount;
  }

  public void setLoanAccount(String loanAccount)
  {
    this.loanAccount = loanAccount;
  }

  public BigDecimal getLoanAmount()
  {
    return loanAmount;
  }

  public void setLoanAmount(BigDecimal loanAmount)
  {
    this.loanAmount = loanAmount;
  }

  public LocalDateTime getCreatedDate()
  {
    return createdDate;
  }

  public void setCreatedDate(LocalDateTime createdDate)
  {
    this.createdDate = createdDate;
  }

  public String getAssignedUserId()
  {
    return assignedUserId;
  }

  public void setAssignedUserId(String assignedUserId)
  {
    this.assignedUserId = assignedUserId;
  }

  public String getGroupId()
  {
    return groupId;
  }

  public void setGroupId(String groupId)
  {
    this.groupId = groupId;
  }

  public String getTenantId()
  {
    return tenantId;
  }

  public void setTenantId(String tenantId)
  {
    this.tenantId = tenantId;
  }

  public String getState()
  {
    return state;
  }

  public void setState(String state)
  {
    this.state = state;
  }

  public String getCifNumber()
  {
    return cifNumber;
  }

  public void setCifNumber(String cifNumber)
  {
    this.cifNumber = cifNumber;
  }

  public String getProductDescription()
  {
    return productDescription;
  }

  public void setProductDescription(String productDescription)
  {
    this.productDescription = productDescription;
  }
}
