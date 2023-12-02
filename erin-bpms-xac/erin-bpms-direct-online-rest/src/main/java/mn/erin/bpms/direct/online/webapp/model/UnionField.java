package mn.erin.bpms.direct.online.webapp.model;

public class UnionField
{
  private String custNo;
  private String registerID;
  private String keyField1;
  private int trackNumber;
  private String productCategory;

  public String getCustNo()
  {
    return custNo;
  }

  public void setCustNo(String custNo)
  {
    this.custNo = custNo;
  }

  public String getRegisterID()
  {
    return registerID;
  }

  public void setRegisterID(String registerID)
  {
    this.registerID = registerID;
  }

  public String getKeyField1()
  {
    return keyField1;
  }

  public void setKeyField1(String keyField1)
  {
    this.keyField1 = keyField1;
  }

  public int getTrackNumber()
  {
    return trackNumber;
  }

  public void setTrackNumber(int trackNumber)
  {
    this.trackNumber = trackNumber;
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
