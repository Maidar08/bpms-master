package mn.erin.domain.bpm.usecase.loan;

/**
 * @author Zorig
 */
public class CreateLoanAccountOutput
{
  private final int accountNumber;

  public CreateLoanAccountOutput(int accountNumber)
  {
    this.accountNumber = accountNumber;
  }

  public int getAccountNumber()
  {
    return accountNumber;
  }
}
