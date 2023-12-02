package mn.erin.domain.bpm.usecase.calculations;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.Validate;

/**
 * @author Zorig
 */
public class GetAverageSalaryInput
{
  private final Map<Date, BigDecimal> dateToSalariesMap;
  private final boolean isExcludedNiigmiinDaatgal;
  private final boolean isExcludedHealthInsurance;

  public GetAverageSalaryInput(Map<Date, BigDecimal> dateToSalariesMap, boolean isExcludedNiigmiinDaatgal, boolean isExcludedHealthInsurance)
  {
    this.dateToSalariesMap = Validate.notNull(dateToSalariesMap, "Salary List must be included!");
    this.isExcludedNiigmiinDaatgal = Validate.notNull(isExcludedNiigmiinDaatgal, "Niigmiin Daatgal exclusion has to be included!");
    this.isExcludedHealthInsurance = Validate.notNull(isExcludedHealthInsurance, "Health Insurance exclusion has to be included!");
  }

  public Map<Date, BigDecimal> getYearToSalariesMap()
  {
    return Collections.unmodifiableMap(dateToSalariesMap);
  }

  public boolean isExcludedNiigmiinDaatgal()
  {
    return isExcludedNiigmiinDaatgal;
  }

  public boolean isExcludedHealthInsurance()
  {
    return isExcludedHealthInsurance;
  }
}
