package mn.erin.domain.bpm.usecase.branch_banking.transaction;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.branch_banking.transaction.ETransaction;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BranchBankingService;

import static mn.erin.domain.bpm.BpmMessagesConstants.BB_TRANSACTION_ACCOUNT_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_TRANSACTION_ACCOUNT_ID_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_TRANSACTION_CHANNEL_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_TRANSACTION_CHANNEL_ID_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_TRANSACTION_END_DT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_TRANSACTION_END_DT_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_TRANSACTION_START_DT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_TRANSACTION_START_DT_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_MESSAGE;

/**
 * @author Bilguunbor
 **/

public class GetTransactionEbankList extends AbstractUseCase<GetTransactionEbankListInput, List<ETransaction>>
{
  private final BranchBankingService branchBankingService;

  public GetTransactionEbankList(BranchBankingService branchBankingService)
  {
    this.branchBankingService = Objects.requireNonNull(branchBankingService);
  }

  @Override
  public List<ETransaction> execute(GetTransactionEbankListInput input) throws UseCaseException
  {
    validateInput(input);

    String accountId = input.getAccountId();
    String channelId = input.getChannelId();
    String channel = input.getChannel();
    String startDt = input.getStartDt();
    String endDt = input.getEndDt();

    String instanceId = input.getInstanceId();

    try
    {
      return branchBankingService.getTransactionEBankList(accountId, channelId, channel, startDt, endDt, instanceId);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
  }

  private void validateInput(GetTransactionEbankListInput input) throws UseCaseException
  {
    if (StringUtils.isEmpty(input.getAccountId()))
    {
      throw new UseCaseException(BB_TRANSACTION_ACCOUNT_ID_NULL_CODE, BB_TRANSACTION_ACCOUNT_ID_NULL_MESSAGE);
    }

    if (StringUtils.isEmpty(input.getChannelId()))
    {
      throw new UseCaseException(BB_TRANSACTION_CHANNEL_ID_NULL_CODE, BB_TRANSACTION_CHANNEL_ID_NULL_MESSAGE);
    }

    if (StringUtils.isEmpty(input.getStartDt()))
    {
      throw new UseCaseException(BB_TRANSACTION_START_DT_NULL_CODE, BB_TRANSACTION_START_DT_NULL_MESSAGE);
    }

    if (StringUtils.isEmpty(input.getEndDt()))
    {
      throw new UseCaseException(BB_TRANSACTION_END_DT_NULL_CODE, BB_TRANSACTION_END_DT_NULL_MESSAGE);
    }

    if (StringUtils.isBlank(input.getInstanceId()))
    {
      throw new UseCaseException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
    }
  }
}
