package mn.erin.domain.bpm.usecase.loan_contract;
public class GetPurchaseSaleContractTypeInput
{
  private String instanceId;
  private String documentType;
  private String accountNumber;

  public GetPurchaseSaleContractTypeInput(String instanceId, String documentType, String accountNumber)
  {
    this.instanceId = instanceId;
    this.documentType = documentType;
    this.accountNumber = accountNumber;
  }

  public String getInstanceId()
  {
    return instanceId;
  }

  public void setInstanceId(String instanceId)
  {
    this.instanceId = instanceId;
  }

  public void setAccountNumber(String accountNumber) {
    this.accountNumber = accountNumber;
  }

  public String getDocumentType()
  {
    return documentType;
  }

  public void setDocumentType(String documentType)
  {
    this.documentType = documentType;
  }

  public String getAccountNumber() {
    return accountNumber;
  }
}
