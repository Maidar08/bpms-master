package mn.erin.domain.bpm.usecase.branch_banking.transaction;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.branch_banking.MakeNoCashAccountFeeTransactionInput;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BranchBankingService;

import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;

public class MakeNoCashAccountFeeTransaction extends AbstractUseCase<MakeNoCashAccountFeeTransactionInput, Map<String, String>>
{
    private final BranchBankingService branchBankingService;

    public MakeNoCashAccountFeeTransaction(BranchBankingService branchBankingService)
    {
        this.branchBankingService = Objects.requireNonNull(branchBankingService, "Branch banking service is required!");
    }
    @Override
    public Map<String, String> execute(MakeNoCashAccountFeeTransactionInput input) throws UseCaseException {
        if (input == null)
        {
            throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
        }
        List<Map<String, Object>> transactionParameters = input.getTransactionParameters();
        String transactionType = input.getTransactionType();
        String transactionSubType = input.getTransactionSubType();
        String userTransactionCode = input.getUserTransactionCode();
        String remarks = input.getRemarks();
        String transactionParticulars = input.getTransactionParticulars();
        String valueDate = input.getValueDate();
        String dtlsOnResponse = input.getDtlsOnResponse();
        String transactionRefNumber = input.getTransactionRefNumber();
        String instanceId = input.getInstanceId(CASE_INSTANCE_ID);

        if (StringUtils.isBlank(instanceId))
        {
            throw new UseCaseException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
        }
        try
        {
            return branchBankingService.makeNoCashAccountFeeTransactionTask(transactionParameters, transactionType, transactionSubType, userTransactionCode, remarks, transactionParticulars, valueDate, dtlsOnResponse, transactionRefNumber, instanceId);
        }
        catch (BpmServiceException e)
        {
            throw new UseCaseException(e.getCode(), e.getMessage());
        }
    }

}
