package mn.erin.bpm.rest.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import mn.erin.domain.bpm.model.salary.CalculatedSalaryInfo;

/**
 * @author Zorig
 */
public class RestSaveSalaries
{
  public RestSaveSalaries()
  {
    // no needed to do something

  }

  private Map<Date, CalculatedSalaryInfo> salariesInfo;

  private BigDecimal averageBeforeTax;

  private BigDecimal averageAfterTax;

  private String  hasMortgage;

  private String emd;

  private String ndsh;


  public BigDecimal getAverageBeforeTax()
  {
    return averageBeforeTax;
  }

  public void setAverageBeforeTax(BigDecimal averageBeforeTax)
  {
    this.averageBeforeTax = averageBeforeTax;
  }

  public BigDecimal getAverageAfterTax()
  {
    return averageAfterTax;
  }

  public void setAverageAfterTax(BigDecimal averageAfterTax)
  {
    this.averageAfterTax = averageAfterTax;
  }

  public Map<Date, CalculatedSalaryInfo> getSalariesInfo()
  {
    return salariesInfo;
  }

  public void setSalariesInfo(Map<Date, CalculatedSalaryInfo> salariesInfo)
  {
    this.salariesInfo = salariesInfo;
  }

  public String getHasMortgage() { return hasMortgage; }

  private void setHasMortgage(String  hasMortgage) { this.hasMortgage = hasMortgage; }

  public String getNdsh() { return ndsh; }

  public void setNdsh(String ndsh) { this.ndsh = ndsh; }

  public String getEmd() { return emd; }

  public void setEmd(String emd) { this.emd = emd; }
}
