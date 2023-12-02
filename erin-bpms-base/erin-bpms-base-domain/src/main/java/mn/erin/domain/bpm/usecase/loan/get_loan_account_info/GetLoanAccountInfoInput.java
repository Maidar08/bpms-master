package mn.erin.domain.bpm.usecase.loan.get_loan_account_info;

/**
 * @author Oyungerel Chuluunsukh
 **/
public class GetLoanAccountInfoInput
{
  String accountNumber;
  String taskDefinitionKey;

  public GetLoanAccountInfoInput(String accountNumber, String taskDefinitionKey)
  {
    this.accountNumber = accountNumber;
    this.taskDefinitionKey = taskDefinitionKey;
  }

  public String getAccountNumber()
  {
    return accountNumber;
  }

  public void setAccountNumber(String accountNumber)
  {
    this.accountNumber = accountNumber;
  }

  public String getTaskDefinitionKey()
  {
    return taskDefinitionKey;
  }

  public void setTaskDefinitionKey(String taskDefinitionKey)
  {
    this.taskDefinitionKey = taskDefinitionKey;
  }
}
