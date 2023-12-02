package mn.erin.domain.bpm.usecase.product;

/**
 * @author Lkhagvadorj
 */
public class GetFilteredCollateralProductInput
{
  String id;
  String type;
  String subType;
  String description;
  String moreInfo;
  public GetFilteredCollateralProductInput(String type, String subType, String description)
  {
    this.type = type;
    this.subType = subType;
    this.description = description;
  }

  public String getType()
  {
    return type;
  }

  public void setType(String type)
  {
    this.type = type;
  }

  public String getSubType()
  {
    return subType;
  }

  public void setSubType(String subType)
  {
    this.subType = subType;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public String getMoreInfo()
  {
    return moreInfo;
  }

  public void setMoreInfo(String moreInfo)
  {
    this.moreInfo = moreInfo;
  }
}
