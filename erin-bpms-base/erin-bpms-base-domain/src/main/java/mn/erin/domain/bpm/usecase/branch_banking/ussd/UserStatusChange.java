package mn.erin.domain.bpm.usecase.branch_banking.ussd;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BranchBankingService;

import static mn.erin.domain.bpm.BpmMessagesConstants.BB_MOBILE_NUMBER_IS_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_MOBILE_NUMBER_IS_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_STATUS_IS_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_STATUS_IS_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;

public class UserStatusChange extends AbstractUseCase<UserStatusChangeInput, Map<String, Object>>
{
    private final BranchBankingService branchBankingService;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserStatusChange.class);
    public UserStatusChange(BranchBankingService branchBankingService)
    {
        this.branchBankingService = Objects.requireNonNull(branchBankingService, "Branch banking service cannot be null!");
    }

    @Override
    public Map<String, Object> execute(UserStatusChangeInput input) throws UseCaseException
    {
        validateInput(input);
        try
        {
            LOGGER.info("##### USSSD USER STATUS CHANGE USE CASE, INSTANCE ID = [{}]", input.getInstanceId());
            Map<String, Object> output = branchBankingService.userStatusChange(input.getMobileNumber(), input.getType(), input.getInstanceId());
            LOGGER.info("##### USSSD USER STATUS CHANGE USE CASE, OUTPUT = [{}], \nINSTANCE ID = [{}]", output, input.getInstanceId());
            return  output;
        } catch (BpmServiceException e) {
            throw new UseCaseException(e.getCode(), e.getMessage());
        }
    }

    public void validateInput(UserStatusChangeInput input) throws UseCaseException {
        if(input == null) {
            throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
        }

        String mobileNumber = input.getMobileNumber();
        String instanceId = input.getInstanceId();
        String status = input.getType();

        if(StringUtils.isBlank(instanceId))
        {
            throw new UseCaseException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
        }

        if(StringUtils.isBlank(mobileNumber))
        {
            throw new UseCaseException(BB_MOBILE_NUMBER_IS_NULL_CODE, BB_MOBILE_NUMBER_IS_NULL_MESSAGE);
        }

        if(StringUtils.isBlank(status))
        {
            throw new UseCaseException(BB_STATUS_IS_NULL_CODE, BB_STATUS_IS_NULL_MESSAGE);
        }
    }
}
