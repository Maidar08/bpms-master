package mn.erin.domain.bpm.usecase.loan;

/**
 * @author Zorig
 */
public class GetAccountsListInput
{
  private final String regNumber;
  private final String customerNumber;

  public GetAccountsListInput(String regNumber, String customerNumber)
  {
    this.regNumber = regNumber;
    this.customerNumber = customerNumber;
  }

  public String getRegNumber()
  {
    return regNumber;
  }

  public String getCustomerNumber()
  {
    return customerNumber;
  }
}
