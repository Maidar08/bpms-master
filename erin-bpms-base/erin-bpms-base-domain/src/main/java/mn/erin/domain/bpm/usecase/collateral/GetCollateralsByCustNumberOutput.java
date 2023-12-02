package mn.erin.domain.bpm.usecase.collateral;

import java.util.List;

import mn.erin.domain.bpm.model.collateral.Collateral;

public class GetCollateralsByCustNumberOutput
{
  private List<Collateral> collaterals;

  public GetCollateralsByCustNumberOutput(List<Collateral> collaterals)
  {
    this.collaterals = collaterals;
  }

  public List<Collateral> getCollaterals()
  {
    return collaterals;
  }

  public void setCollaterals(List<Collateral> collaterals)
  {
    this.collaterals = collaterals;
  }
}
