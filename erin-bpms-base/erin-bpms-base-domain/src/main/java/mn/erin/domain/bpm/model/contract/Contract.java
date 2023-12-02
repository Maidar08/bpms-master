package mn.erin.domain.bpm.model.contract;

/**
 * @author Tamir
 */
public class Contract
{
  private ContractId contractId;
  private byte[] contractAsFile;

  public Contract(ContractId contractId, byte[] contractAsFile)
  {
    this.contractId = contractId;
    this.contractAsFile = contractAsFile;
  }

  public ContractId getContractId()
  {
    return contractId;
  }

  public void setContractId(ContractId contractId)
  {
    this.contractId = contractId;
  }

  public byte[] getContractAsFile()
  {
    return contractAsFile;
  }

  public void setContractAsFile(byte[] contractAsFile)
  {
    this.contractAsFile = contractAsFile;
  }
}
