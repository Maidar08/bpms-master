package mn.erin.domain.bpm.usecase.branch_banking;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BranchBankingService;

import static mn.erin.domain.bpm.BpmMessagesConstants.BB_LOAN_ACCOUNT_ID_IS_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_LOAN_ACCOUNT_ID_IS_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_MESSAGE;

public class GetLoanAccountInfo extends AbstractUseCase<String, Map<String, Object>>
{
    private final BranchBankingService branchBankingService;
    private final String instanceId;

    public GetLoanAccountInfo(BranchBankingService branchBankingService, String instanceId) throws UseCaseException
    {
        this.branchBankingService = Objects.requireNonNull(branchBankingService, "Branch banking service cannot be null!");
        if (StringUtils.isBlank(instanceId))
        {
            throw new UseCaseException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
        }
        this.instanceId = instanceId;
    }

    @Override
    public Map<String, Object> execute(String accountId) throws UseCaseException
    {
        if (StringUtils.isBlank(accountId))
        {
            throw new UseCaseException(BB_LOAN_ACCOUNT_ID_IS_NULL_CODE, BB_LOAN_ACCOUNT_ID_IS_NULL_MESSAGE);
        }

        try
        {
            return branchBankingService.getLoanAccountInfo(accountId, instanceId);
        }
        catch (BpmServiceException e)
        {
            throw new UseCaseException(e.getCode(), e.getMessage());
        }
    }
}
