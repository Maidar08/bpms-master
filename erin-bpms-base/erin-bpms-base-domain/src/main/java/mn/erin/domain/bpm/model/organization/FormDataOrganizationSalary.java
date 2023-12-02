package mn.erin.domain.bpm.model.organization;

import java.util.Date;

public class FormDataOrganizationSalary
{
  private final String contractId;
  private String contractNumber;
  private String branchId;
  private Date contractDate;
  private String contractPeriod;
  private Date contractEndDate;
  private Integer partnerType;
  private String partnerDirection;
  private Integer hrCount;
  private String partnerRegistryId;
  private String partnerCodeND;
  private String partnerNDSubordinate;
  private String partnerAccountId;
  private String partnerContactEmployee;
  private String partnerPhoneNumber;
  private Integer feeHasLoan;
  private String feeHasOnline;
  private String feeType;
  private String feeOrganizationRating;
  private Double feeAmountPercent;
  private Double feeKeyWorker;
  private String feeBankFraud;
  private String contractExtensionYear;
  private String contractHasExtension;
  private Date contractExtensionNewDate;
  private Long feeSalaryTransaction;
  private String feeSalaryDays;
  private String feeSalaryDaysFirst;
  private String feeSalaryDaysSecond;
  private String contractSpecialRemark;
  private Date partnerEstablishedDate;
  private String partnerCif;
  private Date createdDate;
  private String contractOldNumber;

  public FormDataOrganizationSalary(String contractId)
  {
    this.contractId = contractId;
  }

  public String getContractNumber()
  {
    return contractNumber;
  }

  public void setContractNumber(String contractNumber)
  {
    this.contractNumber = contractNumber;
  }

  public String getBranchId()
  {
    return branchId;
  }

  public void setBranchId(String branchId)
  {
    this.branchId = branchId;
  }

  public Date getContractDate()
  {
    return contractDate;
  }

  public void setContractDate(Date contractDate)
  {
    this.contractDate = contractDate;
  }

  public String getContractPeriod()
  {
    return contractPeriod;
  }

  public void setContractPeriod(String contractPeriod)
  {
    this.contractPeriod = contractPeriod;
  }

  public Date getContractEndDate()
  {
    return contractEndDate;
  }

  public void setContractEndDate(Date contractEndDate)
  {
    this.contractEndDate = contractEndDate;
  }

  public Integer getPartnerType()
  {
    return partnerType;
  }

  public void setPartnerType(Integer partnerType)
  {
    this.partnerType = partnerType;
  }

  public String getPartnerDirection()
  {
    return partnerDirection;
  }

  public void setPartnerDirection(String partnerDirection)
  {
    this.partnerDirection = partnerDirection;
  }

  public Integer getHrCount()
  {
    return hrCount;
  }

  public void setHrCount(Integer hrCount)
  {
    this.hrCount = hrCount;
  }

  public String getPartnerRegistryId()
  {
    return partnerRegistryId;
  }

  public void setPartnerRegistryId(String partnerRegistryId)
  {
    this.partnerRegistryId = partnerRegistryId;
  }

  public String getPartnerCodeND()
  {
    return partnerCodeND;
  }

  public void setPartnerCodeND(String partnerCodeND)
  {
    this.partnerCodeND = partnerCodeND;
  }

  public String getPartnerNDSubordinate()
  {
    return partnerNDSubordinate;
  }

  public void setPartnerNDSubordinate(String partnerNDSubordinate)
  {
    this.partnerNDSubordinate = partnerNDSubordinate;
  }

  public String getPartnerAccountId()
  {
    return partnerAccountId;
  }

  public void setPartnerAccountId(String partnerAccountId)
  {
    this.partnerAccountId = partnerAccountId;
  }

  public String getPartnerContactEmployee()
  {
    return partnerContactEmployee;
  }

  public void setPartnerContactEmployee(String partnerContactEmployee)
  {
    this.partnerContactEmployee = partnerContactEmployee;
  }

  public Integer getFeeHasLoan()
  {
    return feeHasLoan;
  }

  public void setFeeHasLoan(Integer feeHasLoan)
  {
    this.feeHasLoan = feeHasLoan;
  }

  public String getFeeHasOnline()
  {
    return feeHasOnline;
  }

  public void setFeeHasOnline(String feeHasOnline)
  {
    this.feeHasOnline = feeHasOnline;
  }

  public String getFeeType()
  {
    return feeType;
  }

  public void setFeeType(String feeType)
  {
    this.feeType = feeType;
  }

  public String getFeeOrganizationRating()
  {
    return feeOrganizationRating;
  }

  public void setFeeOrganizationRating(String feeOrganizationRating)
  {
    this.feeOrganizationRating = feeOrganizationRating;
  }

  public Double getFeeAmountPercent()
  {
    return feeAmountPercent;
  }

  public void setFeeAmountPercent(Double feeAmountPercent)
  {
    this.feeAmountPercent = feeAmountPercent;
  }

  public Double getFeeKeyWorker()
  {
    return feeKeyWorker;
  }

  public void setFeeKeyWorker(Double feeKeyWorker)
  {
    this.feeKeyWorker = feeKeyWorker;
  }

  public String getFeeBankFraud()
  {
    return feeBankFraud;
  }

  public void setFeeBankFraud(String feeBankFraud)
  {
    this.feeBankFraud = feeBankFraud;
  }

  public String getContractExtensionYear()
  {
    return contractExtensionYear;
  }

  public void setContractExtensionYear(String contractExtensionYear)
  {
    this.contractExtensionYear = contractExtensionYear;
  }

  public String getContractHasExtension()
  {
    return contractHasExtension;
  }

  public void setContractHasExtension(String contractHasExtension)
  {
    this.contractHasExtension = contractHasExtension;
  }

  public Date getContractExtensionNewDate()
  {
    return contractExtensionNewDate;
  }

  public void setContractExtensionNewDate(Date contractExtensionNewDate)
  {
    this.contractExtensionNewDate = contractExtensionNewDate;
  }

  public Long getFeeSalaryTransaction()
  {
    return feeSalaryTransaction;
  }

  public void setFeeSalaryTransaction(Long feeSalaryTransaction)
  {
    this.feeSalaryTransaction = feeSalaryTransaction;
  }

  public String getFeeSalaryDays()
  {
    return feeSalaryDays;
  }

  public void setFeeSalaryDays(String feeSalaryDays)
  {
    this.feeSalaryDays = feeSalaryDays;
  }

  public String getFeeSalaryDaysFirst()
  {
    return feeSalaryDaysFirst;
  }

  public void setFeeSalaryDaysFirst(String feeSalaryDaysFirst)
  {
    this.feeSalaryDaysFirst = feeSalaryDaysFirst;
  }

  public String getFeeSalaryDaysSecond()
  {
    return feeSalaryDaysSecond;
  }

  public void setFeeSalaryDaysSecond(String feeSalaryDaysSecond)
  {
    this.feeSalaryDaysSecond = feeSalaryDaysSecond;
  }

  public String getContractSpecialRemark()
  {
    return contractSpecialRemark;
  }

  public void setContractSpecialRemark(String contractSpecialRemark)
  {
    this.contractSpecialRemark = contractSpecialRemark;
  }

  public String getContractId()
  {
    return contractId;
  }

  public String getPartnerPhoneNumber()
  {
    return partnerPhoneNumber;
  }

  public void setPartnerPhoneNumber(String partnerPhoneNumber)
  {
    this.partnerPhoneNumber = partnerPhoneNumber;
  }

  public Date getPartnerEstablishedDate()
  {
    return partnerEstablishedDate;
  }

  public void setPartnerEstablishedDate(Date partnerEstablishedDate)
  {
    this.partnerEstablishedDate = partnerEstablishedDate;
  }

  public String getPartnerCif()
  {
    return partnerCif;
  }

  public void setPartnerCif(String partnerCif)
  {
    this.partnerCif = partnerCif;
  }

  public Date getCreatedDate() { return createdDate; }

  public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }

  public String getContractOldNumber()
  {
    return contractOldNumber;
  }

  public void setContractOldNumber(String contractOldNumber)
  {
    this.contractOldNumber = contractOldNumber;
  }
}
