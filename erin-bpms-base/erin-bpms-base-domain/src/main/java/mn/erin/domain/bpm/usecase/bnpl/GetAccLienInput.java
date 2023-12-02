package mn.erin.domain.bpm.usecase.bnpl;
import mn.erin.domain.base.usecase.UseCaseException;

public class GetAccLienInput
{
  private final String acctId;
  private final String moduleType;
  public GetAccLienInput(String acctId, String moduleType) throws UseCaseException
  {
    this.acctId = acctId;
    this.moduleType = moduleType;
  }
  public String getAcctId()
  {
    return acctId;
  }
  public String getModuleType()
  {
    return moduleType;
  }

}
