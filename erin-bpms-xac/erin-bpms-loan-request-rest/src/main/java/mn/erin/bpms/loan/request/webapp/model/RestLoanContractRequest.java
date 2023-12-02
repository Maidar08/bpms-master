package mn.erin.bpms.loan.request.webapp.model;

import java.time.LocalDateTime;

public class RestLoanContractRequest
{
  private String requestNumber;
  private String cifNumber;
  private String account;
  private String productDescription;
  private String amount;
  private LocalDateTime date;
  private String userId;
  private String processInstanceId;

  public RestLoanContractRequest(String requestNumber, String cifNumber, String account, String productDescription, String amount, LocalDateTime date,
      String userId, String processInstanceId)
  {
    this.requestNumber = requestNumber;
    this.cifNumber = cifNumber;
    this.account = account;
    this.productDescription = productDescription;
    this.amount = amount;
    this.date = date;
    this.userId = userId;
    this.processInstanceId = processInstanceId;
  }

  public String getRequestNumber()
  {
    return requestNumber;
  }

  public void setRequestNumber(String requestNumber)
  {
    this.requestNumber = requestNumber;
  }

  public String getCifNumber()
  {
    return cifNumber;
  }

  public void setCifNumber(String cifNumber)
  {
    this.cifNumber = cifNumber;
  }

  public String getAccount()
  {
    return account;
  }

  public void setAccount(String account)
  {
    this.account = account;
  }

  public String getProductDescription()
  {
    return productDescription;
  }

  public void setProductDescription(String productDescription)
  {
    this.productDescription = productDescription;
  }

  public String getAmount()
  {
    return amount;
  }

  public void setAmount(String amount)
  {
    this.amount = amount;
  }

  public LocalDateTime getDate()
  {
    return date;
  }

  public void setDate(LocalDateTime date)
  {
    this.date = date;
  }

  public String getUserId()
  {
    return userId;
  }

  public void setUserId(String userId)
  {
    this.userId = userId;
  }

  public String getProcessInstanceId()
  {
    return processInstanceId;
  }

  public void setProcessInstanceId(String processInstanceId)
  {
    this.processInstanceId = processInstanceId;
  }
}
