package mn.erin.bpms.direct.online.webapp.model;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author Lkhagvadorj.A
 **/

public class RestDanEntity
{
  private String danRegister;
  private String district;
  private Map<String, BigDecimal> salary;

  public RestDanEntity()
  {

  }

  public String getDanRegister()
  {
    return danRegister;
  }

  public void setDanRegister(String danRegister)
  {
    this.danRegister = danRegister;
  }

  public Map<String, BigDecimal> getSalary()
  {
    return salary;
  }

  public void setSalary(Map<String, BigDecimal> salary)
  {
    this.salary = salary;
  }

  public String getDistrict()
  {
    return district;
  }

  public void setDistrict(String district)
  {
    this.district = district;
  }
}
