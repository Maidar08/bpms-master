package mn.erin.domain.bpm.model.organization;

import java.util.Date;

public class FormDataOrganizationLeasing
{
  private final String contractId;
  private String contractPeriod;
  private Long contractFee;
  private String partnerType;
  private String partnerDirection;
  private String productSuppliedType;
  private String productSuppliedDescription;
  private String contactName;
  private String contactPhoneNumber;
  private String contactEmail;
  private String contactDescription;
  private String feePaymentType;
  private String feePercentAmount;
  private String feeLoanAmount;
  private String feeSupplierDueDate;
  private String feeSupplierAmountPercent;
  private String feeAccountNumber;
  private String feeType;
  private Double feeAmountPercent;
  private Long feeLoanOriginationFee;
  private String feeOnlineBnpl;
  private String feeTerminalId;
  private String contractExtensionYear;
  private String contractHasExtension;
  private Date contractExtensionNewDate;

  private Date contractDate;
  private Date contractEndDate;
  private String contractOldNumber;
  private String partnerCif;
  private String partnerRegistryId;
  private Date partnerEstablishedDate;
  private String partnerAddress;
  private String partnerPhoneNumber;
  private String partnerEmail;
  private Date createdDate;

  public FormDataOrganizationLeasing(String contractId)
  {
    this.contractId = contractId;
  }

  public String getContractId()
  {
    return contractId;
  }

  public String getContractPeriod()
  {
    return contractPeriod;
  }

  public void setContractPeriod(String contractPeriod)
  {
    this.contractPeriod = contractPeriod;
  }

  public Long getContractFee()
  {
    return contractFee;
  }

  public void setContractFee(Long contractFee)
  {
    this.contractFee = contractFee;
  }

  public String getFeePaymentType()
  {
    return feePaymentType;
  }

  public void setFeePaymentType(String feePaymentType)
  {
    this.feePaymentType = feePaymentType;
  }

  public String getFeePercentAmount()
  {
    return feePercentAmount;
  }

  public void setFeePercentAmount(String feePercentAmount)
  {
    this.feePercentAmount = feePercentAmount;
  }

  public String getFeeLoanAmount()
  {
    return feeLoanAmount;
  }

  public void setFeeLoanAmount(String feeLoanAmount)
  {
    this.feeLoanAmount = feeLoanAmount;
  }

  public String getPartnerType()
  {
    return partnerType;
  }

  public void setPartnerType(String partnerType)
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

  public String getProductSuppliedType()
  {
    return productSuppliedType;
  }

  public void setProductSuppliedType(String productSuppliedType)
  {
    this.productSuppliedType = productSuppliedType;
  }

  public String getProductSuppliedDescription()
  {
    return productSuppliedDescription;
  }

  public void setProductSuppliedDescription(String productSuppliedDescription)
  {
    this.productSuppliedDescription = productSuppliedDescription;
  }

  public String getContactName()
  {
    return contactName;
  }

  public void setContactName(String contactName)
  {
    this.contactName = contactName;
  }

  public String getContactPhoneNumber()
  {
    return contactPhoneNumber;
  }

  public void setContactPhoneNumber(String contactPhoneNumber)
  {
    this.contactPhoneNumber = contactPhoneNumber;
  }

  public String getContactEmail()
  {
    return contactEmail;
  }

  public void setContactEmail(String contactEmail)
  {
    this.contactEmail = contactEmail;
  }

  public String getContactDescription()
  {
    return contactDescription;
  }

  public void setContactDescription(String contactDescription)
  {
    this.contactDescription = contactDescription;
  }

  public String getFeeSupplierDueDate()
  {
    return feeSupplierDueDate;
  }

  public void setFeeSupplierDueDate(String feeSupplierDueDate)
  {
    this.feeSupplierDueDate = feeSupplierDueDate;
  }

  public String getFeeSupplierAmountPercent()
  {
    return feeSupplierAmountPercent;
  }

  public void setFeeSupplierAmountPercent(String feeSupplierAmountPercent)
  {
    this.feeSupplierAmountPercent = feeSupplierAmountPercent;
  }

  public String getFeeAccountNumber()
  {
    return feeAccountNumber;
  }

  public void setFeeAccountNumber(String feeAccountNumber)
  {
    this.feeAccountNumber = feeAccountNumber;
  }

  public String getFeeType()
  {
    return feeType;
  }

  public void setFeeType(String feeType)
  {
    this.feeType = feeType;
  }

  public Double getFeeAmountPercent()
  {
    return feeAmountPercent;
  }

  public void setFeeAmountPercent(Double feeAmountPercent)
  {
    this.feeAmountPercent = feeAmountPercent;
  }

  public Long getFeeLoanOriginationFee()
  {
    return feeLoanOriginationFee;
  }

  public void setFeeLoanOriginationFee(Long feeLoanOriginationFee)
  {
    this.feeLoanOriginationFee = feeLoanOriginationFee;
  }

  public String getFeeOnlineBnpl()
  {
    return feeOnlineBnpl;
  }

  public void setFeeOnlineBnpl(String feeOnlineBnpl)
  {
    this.feeOnlineBnpl = feeOnlineBnpl;
  }

  public String getFeeTerminalId()
  {
    return feeTerminalId;
  }

  public void setFeeTerminalId(String feeTerminalId)
  {
    this.feeTerminalId = feeTerminalId;
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

  public Date getContractDate()
  {
    return contractDate;
  }

  public void setContractDate(Date contractDate)
  {
    this.contractDate = contractDate;
  }

  public Date getContractEndDate()
  {
    return contractEndDate;
  }

  public void setContractEndDate(Date contractEndDate)
  {
    this.contractEndDate = contractEndDate;
  }

  public String getContractOldNumber()
  {
    return contractOldNumber;
  }

  public void setContractOldNumber(String contractOldNumber)
  {
    this.contractOldNumber = contractOldNumber;
  }

  public String getPartnerCif()
  {
    return partnerCif;
  }

  public void setPartnerCif(String partnerCif)
  {
    this.partnerCif = partnerCif;
  }

  public String getPartnerRegistryId()
  {
    return partnerRegistryId;
  }

  public void setPartnerRegistryId(String partnerRegistryId)
  {
    this.partnerRegistryId = partnerRegistryId;
  }

  public Date getPartnerEstablishedDate()
  {
    return partnerEstablishedDate;
  }

  public void setPartnerEstablishedDate(Date partnerEstablishedDate)
  {
    this.partnerEstablishedDate = partnerEstablishedDate;
  }

  public String getPartnerAddress()
  {
    return partnerAddress;
  }

  public void setPartnerAddress(String partnerAddress)
  {
    this.partnerAddress = partnerAddress;
  }

  public String getPartnerPhoneNumber()
  {
    return partnerPhoneNumber;
  }

  public void setPartnerPhoneNumber(String partnerPhoneNumber)
  {
    this.partnerPhoneNumber = partnerPhoneNumber;
  }

  public String getPartnerEmail()
  {
    return partnerEmail;
  }

  public void setPartnerEmail(String partnerEmail)
  {
    this.partnerEmail = partnerEmail;
  }

  public Date getCreatedDate() { return createdDate; }

  public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
}
