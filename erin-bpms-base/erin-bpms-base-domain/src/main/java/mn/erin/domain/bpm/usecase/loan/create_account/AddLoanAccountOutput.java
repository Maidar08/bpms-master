package mn.erin.domain.bpm.usecase.loan.create_account;

/**
 * @author Tamir
 */
public class AddLoanAccountOutput
{
  private final String createdAccountNumber;

  public AddLoanAccountOutput(String createdAccountNumber)
  {
    this.createdAccountNumber = createdAccountNumber;
  }

  public String getCreatedAccountNumber()
  {
    return createdAccountNumber;
  }
}
