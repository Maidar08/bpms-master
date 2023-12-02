package mn.erin.domain.bpm.usecase.customer;

import mn.erin.domain.bpm.model.account.UDField;

/**
 * @author Zorig
 */
public class GetCustomerAccountCreationInfoOutput
{
  private final UDField lateReasonAttention;
  private final UDField firstAccountNumber;
  private final UDField restructuredNumber;
  private final UDField attentiveLoan;
  private final UDField firstDisbursedLoanDate;
  private final UDField firstSupplier;
  private final UDField loanPurpose;
  private final UDField businessTypeReason;
  private final UDField sanctionedBy;
  private final UDField subType;
  private final UDField insuranceCompanyInfo;
  private final UDField secondSupplier;
  private final UDField thirdSupplier;
  private final UDField lateReason;
  private final UDField loanCycle;
  private final UDField worker;

  private UDField schoolNameAndInstitution;
  private UDField schoolName;

  public GetCustomerAccountCreationInfoOutput(UDField lateReasonAttention, UDField firstAccountNumber,
      UDField restructuredNumber, UDField attentiveLoan, UDField firstDisbursedLoanDate, UDField firstSupplier,
      UDField loanPurpose, UDField businessTypeReason, UDField sanctionedBy, UDField subType, UDField insuranceCompanyInfo,
      UDField secondSupplier, UDField thirdSupplier, UDField lateReason, UDField loanCycle, UDField worker)
  {
    this.lateReasonAttention = lateReasonAttention;
    this.firstAccountNumber = firstAccountNumber;
    this.restructuredNumber = restructuredNumber;
    this.attentiveLoan = attentiveLoan;
    this.firstDisbursedLoanDate = firstDisbursedLoanDate;
    this.firstSupplier = firstSupplier;
    this.loanPurpose = loanPurpose;
    this.businessTypeReason = businessTypeReason;
    this.sanctionedBy = sanctionedBy;
    this.subType = subType;
    this.insuranceCompanyInfo = insuranceCompanyInfo;
    this.secondSupplier = secondSupplier;
    this.thirdSupplier = thirdSupplier;
    this.lateReason = lateReason;
    this.loanCycle = loanCycle;
    this.worker = worker;
  }

  public UDField getLateReasonAttention()
  {
    return lateReasonAttention;
  }

  public UDField getFirstAccountNumber()
  {
    return firstAccountNumber;
  }

  public UDField getRestructuredNumber()
  {
    return restructuredNumber;
  }

  public UDField getAttentiveLoan()
  {
    return attentiveLoan;
  }

  public UDField getFirstDisbursedLoanDate()
  {
    return firstDisbursedLoanDate;
  }

  public UDField getFirstSupplier()
  {
    return firstSupplier;
  }

  public UDField getLoanPurpose()
  {
    return loanPurpose;
  }

  public UDField getBusinessTypeReason()
  {
    return businessTypeReason;
  }

  public UDField getSanctionedBy()
  {
    return sanctionedBy;
  }

  public UDField getSubType()
  {
    return subType;
  }

  public UDField getInsuranceCompanyInfo()
  {
    return insuranceCompanyInfo;
  }

  public UDField getSecondSupplier()
  {
    return secondSupplier;
  }

  public UDField getThirdSupplier()
  {
    return thirdSupplier;
  }

  public UDField getLateReason()
  {
    return lateReason;
  }

  public UDField getLoanCycle()
  {
    return loanCycle;
  }

  public UDField getWorker()
  {
    return worker;
  }

  public UDField getSchoolNameAndInstitution()
  {
    return schoolNameAndInstitution;
  }

  public void setSchoolNameAndInstitution(UDField schoolNameAndInstitution)
  {
    this.schoolNameAndInstitution = schoolNameAndInstitution;
  }

  public UDField getSchoolName()
  {
    return schoolName;
  }

  public void setSchoolName(UDField schoolName)
  {
    this.schoolName = schoolName;
  }
}
