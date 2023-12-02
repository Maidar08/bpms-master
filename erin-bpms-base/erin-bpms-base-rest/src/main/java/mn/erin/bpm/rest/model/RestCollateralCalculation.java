package mn.erin.bpm.rest.model;

/**
 * @author Lkhagvadorj
 */
public class RestCollateralCalculation
{
  private String collateralId;
  private String amountOfAssessment;
  private String collateralConnectionRate;
  private String loanAmount;
  private String numbering;

  public RestCollateralCalculation()
  {

  }

  public RestCollateralCalculation(String collateralId, String amountOfAssessment, String collateralConnectionRate, String loanAmount, String numbering)
  {
    this.collateralId = collateralId;
    this.amountOfAssessment = amountOfAssessment;
    this.collateralConnectionRate = collateralConnectionRate;
    this.loanAmount = amountOfAssessment;
    this.numbering = numbering;
  }

  public String getCollateralId()
  {
    return collateralId;
  }

  public void setCollateralId(String collateralId)
  {
    this.collateralId = collateralId;
  }

  public String getCollateralConnectionRate()
  {
    return collateralConnectionRate;
  }

  public void setCollateralConnectionRate(String collateralConnectionRate)
  {
    this.collateralConnectionRate = collateralConnectionRate;
  }

  public String getAmountOfAssessment()
  {
    return amountOfAssessment;
  }

  public void setAmountOfAssessment(String amountOfAssessment)
  {
    this.amountOfAssessment = amountOfAssessment;
  }

  public String getLoanAmount()
  {
    return loanAmount;
  }

  public void setLoanAmount(String loanAmount)
  {
    this.loanAmount = loanAmount;
  }

  public String getNumbering()
  {
    return numbering;
  }

  public void setNumbering(String numbering)
  {
    this.numbering = numbering;
  }
}
