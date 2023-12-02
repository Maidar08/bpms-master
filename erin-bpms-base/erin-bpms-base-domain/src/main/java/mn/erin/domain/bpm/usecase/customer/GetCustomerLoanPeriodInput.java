package mn.erin.domain.bpm.usecase.customer;

/**
 * @author Zorig
 */
public class GetCustomerLoanPeriodInput
{
  private final String customerCif;

  public GetCustomerLoanPeriodInput(String customerCif)
  {
    this.customerCif = customerCif;
  }

  public String getCustomerCif()
  {
    return customerCif;
  }
}
