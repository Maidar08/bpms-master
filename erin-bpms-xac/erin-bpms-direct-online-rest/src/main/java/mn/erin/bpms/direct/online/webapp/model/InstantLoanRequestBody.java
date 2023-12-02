package mn.erin.bpms.direct.online.webapp.model;

import java.util.List;

import mn.erin.domain.bpm.model.directOnline.DanInfo;

public class InstantLoanRequestBody
{
  private String channel;
  private String cifNumber;
  private String productCategory;
  private String address;
  private String locale;
  private String email;
  private Long phoneNumber;
  private List<DanInfo> danInfo;

  public InstantLoanRequestBody()
  {
  }

  public String getChannel()
  {
    return channel;
  }

  public void setChannel(String channel)
  {
    this.channel = channel;
  }

  public String getCifNumber()
  {
    return cifNumber;
  }

  public void setCifNumber(String cifNumber)
  {
    this.cifNumber = cifNumber;
  }

  public String getProductCategory()
  {
    return productCategory;
  }

  public void setProductCategory(String productCategory)
  {
    this.productCategory = productCategory;
  }

  public String getAddress()
  {
    return address;
  }

  public void setAddress(String address)
  {
    this.address = address;
  }

  public String getLocale()
  {
    return locale;
  }

  public void setLocale(String locale)
  {
    this.locale = locale;
  }

  public String getEmail()
  {
    return email;
  }

  public void setEmail(String email)
  {
    this.email = email;
  }

  public Long getPhoneNumber()
  {
    return phoneNumber;
  }

  public void setPhoneNumber(Long phoneNumber)
  {
    this.phoneNumber = phoneNumber;
  }

  public List<DanInfo> getDanInfo()
  {
    return danInfo;
  }

  public void setDanInfo(List<DanInfo> danInfo)
  {
    this.danInfo = danInfo;
  }
}
