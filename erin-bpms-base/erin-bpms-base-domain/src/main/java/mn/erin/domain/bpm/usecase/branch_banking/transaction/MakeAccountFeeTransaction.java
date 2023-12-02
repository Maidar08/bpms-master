package mn.erin.domain.bpm.usecase.branch_banking.transaction;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.branch_banking.MakeAccountFeeTransactionInput;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BranchBankingService;

import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;

public class MakeAccountFeeTransaction  extends AbstractUseCase<MakeAccountFeeTransactionInput, Map<String, String>>
{

    private final BranchBankingService branchBankingService;

    public MakeAccountFeeTransaction(BranchBankingService branchBankingService)
    {
        this.branchBankingService = Objects.requireNonNull(branchBankingService, "Branch banking service is required!");
    }

    @Override
    public Map<String, String> execute(MakeAccountFeeTransactionInput input) throws UseCaseException {
        if (input == null)
        {
            throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
        }
        String amount = input.getAmount();
        String currency = input.getCurrency();
        String transactionSubType = input.getTransactionSubType();
        List<Map<String, Object>> transactionsParameters = input.getTransactionsParameters();
        String instanceId = input.getInstanceId(CASE_INSTANCE_ID);

        if (StringUtils.isBlank(instanceId))
        {
            throw new UseCaseException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
        }

        try
        {
            return branchBankingService.makeAccountFeeTransactionTask(transactionsParameters, amount, currency, transactionSubType, instanceId);
        }
        catch (BpmServiceException e)
        {
            throw new UseCaseException(e.getCode(), e.getMessage());
        }
    }
}
