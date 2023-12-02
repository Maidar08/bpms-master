package mn.erin.bpms.loan.request.webapp.controller;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

/**
 * @author EBazarragchaa
 */
public class RestLoanRequest
{
  private String id;
  private String state;
  private String instanceId;

  private String channel;
  private String userId;

  private String createdDate;
  private String registerNumber;

  private String cifNumber;
  private String phoneNumber;

  private String email;
  private String branchNumber;

  private String productCategory;
  private String incomeType;

  private BigDecimal incomeBeforeTax;
  private String amount;

  private Integer term;
  private BigDecimal monthlyRepayment;

  private String repaymentType;
  private String purpose;

  private String firstPaymentDate;
  private BigDecimal annualInterestRate;

  private Boolean hasMortgage;
  private String loanProduct;

  private String loanProductDescription;
  private String fullName;

  private String borrowerType;
  private String processTypeId;

  public RestLoanRequest()
  {
    // no needed to do something
  }

  public String getCreatedDate()
  {
    return createdDate;
  }

  public void setCreatedDate(String createdDate)
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

  public String getState()
  {
    return state;
  }

  public void setState(String state)
  {
    this.state = state;
  }

  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public String getRegisterNumber()
  {
    return StringUtils.upperCase(registerNumber);
  }

  public void setRegisterNumber(String registerNumber)
  {
    this.registerNumber = registerNumber;
  }

  public String getCifNumber()
  {
    return cifNumber;
  }

  public void setCifNumber(String cifNumber)
  {
    this.cifNumber = cifNumber;
  }

  public String getPhoneNumber()
  {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber)
  {
    this.phoneNumber = phoneNumber;
  }

  public String getEmail()
  {
    return email;
  }

  public void setEmail(String email)
  {
    this.email = email;
  }

  public String getProductCategory()
  {
    return productCategory;
  }

  public void setProductCategory(String productCategory)
  {
    this.productCategory = productCategory;
  }

  public String getAmount()
  {
    return amount;
  }

  public void setAmount(String amount)
  {
    this.amount = amount;
  }

  public String getIncomeType()
  {
    return incomeType;
  }

  public void setIncomeType(String incomeType)
  {
    this.incomeType = incomeType;
  }

  public String getBranchNumber()
  {
    return branchNumber;
  }

  public void setBranchNumber(String branchNumber)
  {
    this.branchNumber = branchNumber;
  }

  public String getChannel()
  {
    return channel;
  }

  public void setChannel(String channel)
  {
    this.channel = channel;
  }

  public BigDecimal getIncomeBeforeTax()
  {
    return incomeBeforeTax;
  }

  public void setIncomeBeforeTax(BigDecimal incomeBeforeTax)
  {
    this.incomeBeforeTax = incomeBeforeTax;
  }

  public Integer getTerm()
  {
    return term;
  }

  public void setTerm(Integer term)
  {
    this.term = term;
  }

  public BigDecimal getMonthlyRepayment()
  {
    return monthlyRepayment;
  }

  public void setMonthlyRepayment(BigDecimal monthlyRepayment)
  {
    this.monthlyRepayment = monthlyRepayment;
  }

  public String getRepaymentType()
  {
    return repaymentType;
  }

  public void setRepaymentType(String repaymentType)
  {
    this.repaymentType = repaymentType;
  }

  public String getPurpose()
  {
    return purpose;
  }

  public void setPurpose(String purpose)
  {
    this.purpose = purpose;
  }

  public String getFirstPaymentDate()
  {
    return firstPaymentDate;
  }

  public void setFirstPaymentDate(String firstPaymentDate)
  {
    this.firstPaymentDate = firstPaymentDate;
  }

  public BigDecimal getAnnualInterestRate()
  {
    return annualInterestRate;
  }

  public void setAnnualInterestRate(BigDecimal annualInterestRate)
  {
    this.annualInterestRate = annualInterestRate;
  }

  public Boolean getHasMortgage()
  {
    return hasMortgage;
  }

  public void setHasMortgage(Boolean hasMortgage)
  {
    this.hasMortgage = hasMortgage;
  }

  public String getInstanceId()
  {
    return instanceId;
  }

  public void setInstanceId(String instanceId)
  {
    this.instanceId = instanceId;
  }

  public String getFullName()
  {
    return fullName;
  }

  public void setFullName(String fullName)
  {
    this.fullName = fullName;
  }

  public String getLoanProduct()
  {
    return loanProduct;
  }

  public void setLoanProduct(String loanProduct)
  {
    this.loanProduct = loanProduct;
  }

  public String getLoanProductDescription()
  {
    return loanProductDescription;
  }

  public void setLoanProductDescription(String loanProductDescription)
  {
    this.loanProductDescription = loanProductDescription;
  }

  public String getBorrowerType()
  {
    return borrowerType;
  }

  public void setBorrowerType(String borrowerType)
  {
    this.borrowerType = borrowerType;
  }

  public String getProcessTypeId()
  {
    return processTypeId;
  }

  public void setProcessTypeId(String processTypeId)
  {
    this.processTypeId = processTypeId;
  }
}
