package mn.erin.bpm.rest.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * @author Zorig
 */
public class RestSalariesCalculationInfo
{
  private boolean niigmiinDaatgalExcluded;
  private boolean healthInsuranceExcluded;
  Map<Date, BigDecimal> dateAndSalaries;

  public RestSalariesCalculationInfo()
  {
    // no needed to do something

  }

  public boolean isNiigmiinDaatgalExcluded()
  {
    return niigmiinDaatgalExcluded;
  }

  public void setNiigmiinDaatgalExcluded(boolean niigmiinDaatgalExcluded)
  {
    this.niigmiinDaatgalExcluded = niigmiinDaatgalExcluded;
  }

  public boolean isHealthInsuranceExcluded()
  {
    return healthInsuranceExcluded;
  }

  public void setHealthInsuranceExcluded(boolean healthInsuranceExcluded)
  {
    this.healthInsuranceExcluded = healthInsuranceExcluded;
  }

  public Map<Date, BigDecimal> getDateAndSalaries()
  {
    return dateAndSalaries;
  }

  public void setDateAndSalaries(Map<Date, BigDecimal> dateAndSalaries)
  {
    this.dateAndSalaries = dateAndSalaries;
  }
}
