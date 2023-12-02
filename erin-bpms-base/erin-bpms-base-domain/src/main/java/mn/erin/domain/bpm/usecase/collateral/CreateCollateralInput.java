package mn.erin.domain.bpm.usecase.collateral;

import java.util.Map;

/**
 * @author Tamir
 */
public class CreateCollateralInput
{
  private Map<String, Object> genericInfo;
  private Map<String, Object> collateralInfo;
  private Map<String, Object> inspectionInfo;
  private Map<String, Object> ownershipInfo;

  public CreateCollateralInput(Map<String, Object> genericInfo, Map<String, Object> collateralInfo,
      Map<String, Object> inspectionInfo, Map<String, Object> ownershipInfo)
  {
    this.genericInfo = genericInfo;
    this.collateralInfo = collateralInfo;
    this.inspectionInfo = inspectionInfo;
    this.ownershipInfo = ownershipInfo;
  }

  public Map<String, Object> getGenericInfo()
  {
    return genericInfo;
  }

  public void setGenericInfo(Map<String, Object> genericInfo)
  {
    this.genericInfo = genericInfo;
  }

  public Map<String, Object> getCollateralInfo()
  {
    return collateralInfo;
  }

  public void setCollateralInfo(Map<String, Object> collateralInfo)
  {
    this.collateralInfo = collateralInfo;
  }

  public Map<String, Object> getInspectionInfo()
  {
    return inspectionInfo;
  }

  public void setInspectionInfo(Map<String, Object> inspectionInfo)
  {
    this.inspectionInfo = inspectionInfo;
  }

  public Map<String, Object> getOwnershipInfo()
  {
    return ownershipInfo;
  }

  public void setOwnershipInfo(Map<String, Object> ownershipInfo)
  {
    this.ownershipInfo = ownershipInfo;
  }
}
