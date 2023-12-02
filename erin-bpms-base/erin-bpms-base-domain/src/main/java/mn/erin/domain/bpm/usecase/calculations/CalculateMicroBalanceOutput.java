package mn.erin.domain.bpm.usecase.calculations;

import java.util.Map;

/**
 * @author Lkhagvadorj.A
 **/

public class CalculateMicroBalanceOutput
{
  private int reportPeriod;
  private Map<Integer, Map<String, Object>> sale;
  private Map<Integer, Map<String, Object>> operation;
  private Map<Integer, Map<String, Object>> asset;
  private Map<Integer, Map<String, Object>> debt;
  private Map<String, Map<String, Object>> columnHeader;
  private double totalIncomeAmount;
  private double totalIncomePercent;

  public CalculateMicroBalanceOutput(int reportPeriod, Map<Integer, Map<String, Object>> sale,
      Map<Integer, Map<String, Object>> operation, Map<Integer, Map<String, Object>> asset,
      Map<Integer, Map<String, Object>> debt, Map<String, Map<String, Object>> columnHeader, double totalIncomeAmount, double totalIncomePercent)
  {
    this.reportPeriod = reportPeriod;
    this.sale = sale;
    this.operation = operation;
    this.asset = asset;
    this.debt = debt;
    this.columnHeader = columnHeader;
    this.totalIncomeAmount = totalIncomeAmount;
    this.totalIncomePercent = totalIncomePercent;
  }

  public int getReportPeriod()
  {
    return reportPeriod;
  }

  public void setReportPeriod(int reportPeriod)
  {
    this.reportPeriod = reportPeriod;
  }

  public Map<Integer, Map<String, Object>> getSale()
  {
    return sale;
  }

  public void setSale(Map<Integer, Map<String, Object>> sale)
  {
    this.sale = sale;
  }

  public Map<Integer, Map<String, Object>> getOperation()
  {
    return operation;
  }

  public void setOperation(Map<Integer, Map<String, Object>> operation)
  {
    this.operation = operation;
  }

  public Map<Integer, Map<String, Object>> getAsset()
  {
    return asset;
  }

  public void setAsset(Map<Integer, Map<String, Object>> asset)
  {
    this.asset = asset;
  }

  public Map<Integer, Map<String, Object>> getDebt()
  {
    return debt;
  }

  public void setDebt(Map<Integer, Map<String, Object>> debt)
  {
    this.debt = debt;
  }

  public Map<String, Map<String, Object>> getColumnHeader()
  {
    return columnHeader;
  }

  public void setColumnHeader(Map<String, Map<String, Object>> columnHeader)
  {
    this.columnHeader = columnHeader;
  }

  public double getTotalIncomeAmount()
  {
    return totalIncomeAmount;
  }

  public void setTotalIncomeAmount(double totalIncomeAmount)
  {
    this.totalIncomeAmount = totalIncomeAmount;
  }

  public double getTotalIncomePercent()
  {
    return totalIncomePercent;
  }

  public void setTotalIncomePercent(double totalIncomePercent)
  {
    this.totalIncomePercent = totalIncomePercent;
  }
}
