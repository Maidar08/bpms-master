package mn.erin.domain.bpm.usecase.direct_online;

/**
 * @author Lkhagvadorj.A
 **/

public class DownloadOrganizationInfoInput
{
  private String danRegister;
  private String district;
  private String productCategory;

  public DownloadOrganizationInfoInput(String danRegister, String district)
  {
    this.danRegister = danRegister;
    this.district = district;
  }

  public DownloadOrganizationInfoInput(String danRegister, String district, String productCategory)
  {
    this.danRegister = danRegister;
    this.district = district;
    this.productCategory = productCategory;
  }

  public String getDanRegister()
  {
    return danRegister;
  }

  public void setDanRegister(String danRegister)
  {
    this.danRegister = danRegister;
  }

  public String getDistrict()
  {
    return district;
  }

  public void setDistrict(String district)
  {
    this.district = district;
  }

  public String getProductCategory()
  {
    return productCategory;
  }

  public void setProductCategory(String productCategory)
  {
    this.productCategory = productCategory;
  }
}
