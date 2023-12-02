package mn.erin.bpms.direct.online.webapp.model;

import java.math.BigDecimal;
import java.util.List;

import mn.erin.domain.bpm.model.directOnline.DanInfo;

/**
 * @author Lkhagvadorj.A
 **/

public class RestDirectOnlineSalaryLoanRequest
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

  private Integer dayOfPayment;

  private Boolean hasMortgage;
  private String loanProduct;

  private String loanProductDescription;
  private String fullName;

  private String borrowerType;
  private String locale;

  // direct online scoring param
  private String genderMn;
  private String xacspanDate;
  private String familyIncomeString;
  private String businessSector;
  private String xacspan;
  private String workspan;
  private String familyIncome;
  private String joblessMembers;
  private String address;
  private String area;

  // direct online loan amount calculation param
  private Double averageSalaryAfterTax;
  private Double averageSalaryBeforeTax;
  private Double monthPaymentActiveLoan;
  private String interestRate;
  private String loanGrantDate;

  private Double fixedAcceptedLoanAmount;
  private String currentAccountNumber;
  private List<DanInfo> danInfo;

  public RestDirectOnlineSalaryLoanRequest()
  {

  }

  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public String getState()
  {
    return state;
  }

  public void setState(String state)
  {
    this.state = state;
  }

  public String getInstanceId()
  {
    return instanceId;
  }

  public void setInstanceId(String instanceId)
  {
    this.instanceId = instanceId;
  }

  public String getChannel()
  {
    return channel;
  }

  public void setChannel(String channel)
  {
    this.channel = channel;
  }

  public String getUserId()
  {
    return userId;
  }

  public void setUserId(String userId)
  {
    this.userId = userId;
  }

  public String getCreatedDate()
  {
    return createdDate;
  }

  public void setCreatedDate(String createdDate)
  {
    this.createdDate = createdDate;
  }

  public String getRegisterNumber()
  {
    return registerNumber;
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

  public String getBranchNumber()
  {
    return branchNumber;
  }

  public void setBranchNumber(String branchNumber)
  {
    this.branchNumber = branchNumber;
  }

  public String getProductCategory()
  {
    return productCategory;
  }

  public void setProductCategory(String productCategory)
  {
    this.productCategory = productCategory;
  }

  public String getIncomeType()
  {
    return incomeType;
  }

  public void setIncomeType(String incomeType)
  {
    this.incomeType = incomeType;
  }

  public BigDecimal getIncomeBeforeTax()
  {
    return incomeBeforeTax;
  }

  public void setIncomeBeforeTax(BigDecimal incomeBeforeTax)
  {
    this.incomeBeforeTax = incomeBeforeTax;
  }

  public String getAmount()
  {
    return amount;
  }

  public void setAmount(String amount)
  {
    this.amount = amount;
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

  public String getFullName()
  {
    return fullName;
  }

  public void setFullName(String fullName)
  {
    this.fullName = fullName;
  }

  public String getBorrowerType()
  {
    return borrowerType;
  }

  public void setBorrowerType(String borrowerType)
  {
    this.borrowerType = borrowerType;
  }

  public String getGenderMn()
  {
    return genderMn;
  }

  public void setGenderMn(String genderMn)
  {
    this.genderMn = genderMn;
  }

  public String getXacspanDate()
  {
    return xacspanDate;
  }

  public void setXacspanDate(String xacspanDate)
  {
    this.xacspanDate = xacspanDate;
  }

  public String getFamilyIncomeString()
  {
    return familyIncomeString;
  }

  public void setFamilyIncomeString(String familyIncomeString)
  {
    this.familyIncomeString = familyIncomeString;
  }

  public String getBusinessSector()
  {
    return businessSector;
  }

  public void setBusinessSector(String businessSector)
  {
    this.businessSector = businessSector;
  }

  public String getXacspan()
  {
    return xacspan;
  }

  public void setXacspan(String xacspan)
  {
    this.xacspan = xacspan;
  }

  public String getWorkspan()
  {
    return workspan;
  }

  public void setWorkspan(String workspan)
  {
    this.workspan = workspan;
  }

  public String getFamilyIncome()
  {
    return familyIncome;
  }

  public void setFamilyIncome(String family_income)
  {
    this.familyIncome = family_income;
  }

  public String getJoblessMembers()
  {
    return joblessMembers;
  }

  public void setJoblessMembers(String jobless_members)
  {
    this.joblessMembers = jobless_members;
  }

  public String getAddress()
  {
    return address;
  }

  public void setAddress(String address)
  {
    this.address = address;
  }

  public String getArea()
  {
    return area;
  }

  public void setArea(String area)
  {
    this.area = area;
  }

  public Double getAverageSalaryAfterTax()
  {
    return averageSalaryAfterTax;
  }

  public void setAverageSalaryAfterTax(Double averageSalaryAfterTax)
  {
    this.averageSalaryAfterTax = averageSalaryAfterTax;
  }

  public Double getAverageSalaryBeforeTax()
  {
    return averageSalaryBeforeTax;
  }

  public void setAverageSalaryBeforeTax(Double averageSalaryBeforeTax)
  {
    this.averageSalaryBeforeTax = averageSalaryBeforeTax;
  }

  public Double getMonthPaymentActiveLoan()
  {
    return monthPaymentActiveLoan;
  }

  public void setMonthPaymentActiveLoan(Double monthPaymentActiveLoan)
  {
    this.monthPaymentActiveLoan = monthPaymentActiveLoan;
  }

  public String getInterestRate()
  {
    return interestRate;
  }

  public void setInterestRate(String interestRate)
  {
    this.interestRate = interestRate;
  }

  public String getLoanGrantDate()
  {
    return loanGrantDate;
  }

  public void setLoanGrantDate(String loanGrantDate)
  {
    this.loanGrantDate = loanGrantDate;
  }

  public Double getFixedAcceptedLoanAmount()
  {
    return fixedAcceptedLoanAmount;
  }

  public void setFixedAcceptedLoanAmount(Double fixedAcceptedLoanAmount)
  {
    this.fixedAcceptedLoanAmount = fixedAcceptedLoanAmount;
  }

  public String getLocale()
  {
    return locale;
  }

  public void setLocale(String locale)
  {
    this.locale = locale;
  }

  public Integer getDayOfPayment()
  {
    return dayOfPayment;
  }

  public void setDayOfPayment(Integer dayOfPayment)
  {
    this.dayOfPayment = dayOfPayment;
  }

  public String getCurrentAccountNumber()
  {
    return currentAccountNumber;
  }

  public void setCurrentAccountNumber(String currentAccountNumber)
  {
    this.currentAccountNumber = currentAccountNumber;
  }

  public List<DanInfo> getDanInfo()
  {
    return danInfo;
  }

  public void setDanInfo(List<DanInfo> danInfo)
  {
    this.danInfo = danInfo;
  }
}
