package mn.erin.domain.bpm.usecase.loan;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.LoanService;

import static mn.erin.domain.bpm.BpmMessagesConstants.CUSTOMER_CID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CUSTOMER_CID_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;

/**
 * @author Lkhagvadorj.A
 **/

public class GetCustomerDetail extends AbstractUseCase<GetCustomerRelatedInfoInput, Map<String, String>>
{
    private final LoanService loanService;

    public GetCustomerDetail(LoanService loanService)
    {
        this.loanService = Objects.requireNonNull(loanService, "Loan service is required!");
    }

    @Override
    public Map<String, String> execute(GetCustomerRelatedInfoInput input) throws UseCaseException
    {
        if (null  == input)
        {
            throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
        }

        if (StringUtils.isBlank(input.getCustomerCid()))
        {
            throw new UseCaseException(CUSTOMER_CID_NULL_CODE, CUSTOMER_CID_NULL_MESSAGE);
        }

        try
        {
            return loanService.getCustomerDetail(input.getCustomerCid());
        }
        catch (BpmServiceException e)
        {
            throw new UseCaseException(e.getCode(), e.getMessage(), e);
        }
    }
}
