package mn.erin.domain.bpm.model.salary;

import java.math.BigDecimal;

/**
 * @author Zorig
 */
public class CalculatedSalaryInfo
{
  private boolean checked;
  private BigDecimal ndsh;
  private BigDecimal hhoat;
  private BigDecimal salaryBeforeTax;
  private BigDecimal salaryAfterTax;
  private boolean salaryFromXyp;

  public CalculatedSalaryInfo()
  {
  }

  public BigDecimal getNdsh()
  {
    return ndsh;
  }

  public void setNdsh(BigDecimal ndsh)
  {
    this.ndsh = ndsh;
  }

  public BigDecimal getHhoat()
  {
    return hhoat;
  }

  public void setHhoat(BigDecimal hhoat)
  {
    this.hhoat = hhoat;
  }

  public BigDecimal getSalaryBeforeTax()
  {
    return salaryBeforeTax;
  }

  public void setSalaryBeforeTax(BigDecimal salaryBeforeTax)
  {
    this.salaryBeforeTax = salaryBeforeTax;
  }

  public BigDecimal getSalaryAfterTax()
  {
    return salaryAfterTax;
  }

  public void setSalaryAfterTax(BigDecimal salaryAfterTax)
  {
    this.salaryAfterTax = salaryAfterTax;
  }

  public void setSalaryFromXyp(boolean salaryFromXyp) {
    this.salaryFromXyp = salaryFromXyp;
  }

  public boolean getSalaryFromXyp() {
    return salaryFromXyp;
  }

  public boolean getChecked() { return checked; }

  public void setChecked(boolean checked) { this.checked = checked; }
}
