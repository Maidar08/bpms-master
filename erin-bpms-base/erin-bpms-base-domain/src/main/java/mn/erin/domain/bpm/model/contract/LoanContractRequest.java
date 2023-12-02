package mn.erin.domain.bpm.model.contract;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.base.model.Entity;
import mn.erin.domain.bpm.model.process.ProcessRequestId;

/**
 * @author Temuulen Naranbold
 */
public class LoanContractRequest implements Entity<LoanContractRequest>, Serializable
{
  private ProcessRequestId id;
  private String processInstanceId;
  private String type;
  private String account;
  private BigDecimal amount;
  private LocalDateTime createdDate;
  private String userId;
  private String groupId;
  private TenantId tenantId;
  private String state;
  private String cifNumber;
  private String productDescription;
  private Map<String, Object> variables;

  public LoanContractRequest(ProcessRequestId id, String processInstanceId, String type, String account,
      BigDecimal amount, LocalDateTime createdDate, String userId, String groupId, TenantId tenantId, String state, String cifNumber, String productDescription)
  {
    this.id = id;
    this.processInstanceId = processInstanceId;
    this.type = type;
    this.account = account;
    this.amount = amount;
    this.createdDate = createdDate;
    this.userId = userId;
    this.groupId = groupId;
    this.tenantId = tenantId;
    this.state = state;
    this.cifNumber = cifNumber;
    this.productDescription = productDescription;
  }

  public LoanContractRequest()
  {

  }

  public ProcessRequestId getId()
  {
    return id;
  }

  public void setId(ProcessRequestId id)
  {
    this.id = id;
  }

  public String getProcessInstanceId()
  {
    return processInstanceId;
  }

  public void setProcessInstanceId(String processInstanceId)
  {
    this.processInstanceId = processInstanceId;
  }

  public String getType()
  {
    return type;
  }

  public void setType(String type)
  {
    this.type = type;
  }

  public String getAccount()
  {
    return account;
  }

  public void setAccount(String account)
  {
    this.account = account;
  }

  public BigDecimal getAmount()
  {
    return amount;
  }

  public void setAmount(BigDecimal amount)
  {
    this.amount = amount;
  }

  public LocalDateTime getCreatedDate()
  {
    return createdDate;
  }

  public void setCreatedDate(LocalDateTime createdDate)
  {
    this.createdDate = createdDate;
  }

  public String getUserId()
  {
    return userId;
  }

  public void setUserId(String userId)
  {
    this.userId = userId;
  }

  public String getGroupId()
  {
    return groupId;
  }

  public void setGroupId(String groupId)
  {
    this.groupId = groupId;
  }

  public TenantId getTenantId()
  {
    return tenantId;
  }

  public void setTenantId(TenantId tenantId)
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

  public Map<String, Object> getVariables()
  {
    return variables;
  }

  public void setVariables(Map<String, Object> variables)
  {
    this.variables = variables;
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

  @Override
  public boolean sameIdentityAs(LoanContractRequest other)
  {
    return other != null && other.id.equals(this.id);
  }
}
