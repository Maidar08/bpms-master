package mn.erin.domain.bpm.usecase.collateral;

import java.util.List;

/**
 * @author Tamir
 */
public class GetCollateralCodesOutput
{
  private List<String> collateralCodes;

  public GetCollateralCodesOutput(List<String> collateralCodes)
  {
    this.collateralCodes = collateralCodes;
  }

  public List<String> getCollateralCodes()
  {
    return collateralCodes;
  }

  public void setCollateralCodes(List<String> collateralCodes)
  {
    this.collateralCodes = collateralCodes;
  }
}
