package mn.erin.domain.bpm.usecase.contract;

import java.util.Objects;

/**
 * @author Tamir
 */
public class DownloadLoanContractAsBase64Input
{
  private String caseInstanceId;

  public DownloadLoanContractAsBase64Input(String caseInstanceId)
  {
    this.caseInstanceId = Objects.requireNonNull(caseInstanceId, "Case instance id is required!");
  }

  public String getCaseInstanceId()
  {
    return caseInstanceId;
  }

  public void setCaseInstanceId(String caseInstanceId)
  {
    this.caseInstanceId = caseInstanceId;
  }
}
