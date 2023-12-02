package mn.erin.domain.bpm.usecase.branch_banking.transaction;


import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.branch_banking.transaction.CustomerTransaction;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BranchBankingService;

import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;

/**
 * @author Tamir
 */
public class GetCustomerTransactions extends AbstractUseCase<GetCustomerTransactionsInput, GetCustomerTransactionsOutput>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GetCustomerTransactions.class);

  private final BranchBankingService branchBankingService;

  public GetCustomerTransactions(BranchBankingService branchBankingService)
  {
    this.branchBankingService = branchBankingService;
  }

  @Override
  public GetCustomerTransactionsOutput execute(GetCustomerTransactionsInput input) throws UseCaseException
  {
    if (null == input)
    {
      throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
    }
    String userId = input.getUserId();
    String transactionDate = input.getTransactionDate();

    try
    {
      Collection<CustomerTransaction> transactions = branchBankingService.findByUserIdAndDate(userId, transactionDate, input.getInstanceId());

      return new GetCustomerTransactionsOutput(transactions);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
  }
}
