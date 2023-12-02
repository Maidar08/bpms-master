package mn.erin.domain.bpm.usecase.process;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import mn.erin.domain.bpm.model.salary.CalculatedSalaryInfo;

/**
 * @author Zorig
 */
public class SaveSalariesInput
{
  private final String processInstanceId;
  private final Map<Date, CalculatedSalaryInfo> salariesInfo;
  private final BigDecimal averageSalaryBeforeTax;
  private final BigDecimal averageSalaryAfterTax;
  private final String hasMortgage;
  private final String emd;
  private final String ndsh;

  public SaveSalariesInput(String processInstanceId, Map<Date, CalculatedSalaryInfo> salariesInfo, BigDecimal averageSalaryBeforeTax,
      BigDecimal averageSalaryAfterTax, String hasMortgage, String ndsh, String emd)
  {
    this.processInstanceId = processInstanceId;
    this.salariesInfo = salariesInfo;
    this.averageSalaryBeforeTax = averageSalaryBeforeTax;
    this.averageSalaryAfterTax = averageSalaryAfterTax;
    this.hasMortgage = hasMortgage;
    this.ndsh = ndsh;
    this.emd = emd;
  }

  public String getProcessInstanceId()
  {
    return processInstanceId;
  }

  public Map<Date, CalculatedSalaryInfo> getSalariesInfo()
  {
    return salariesInfo;
  }

  public BigDecimal getAverageSalaryBeforeTax()
  {
    return averageSalaryBeforeTax;
  }

  public BigDecimal getAverageSalaryAfterTax()
  {
    return averageSalaryAfterTax;
  }

  public String getHasMortgage() { return hasMortgage; }

  public String getNdsh() { return ndsh; }

  public String getEmd() { return emd; }
}
