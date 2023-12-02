package mn.erin.domain.bpm.usecase.branch_banking;

import java.text.ParseException;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BranchBankingService;

import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;

public class GetAccountInfo extends AbstractUseCase<GetAccountInfoInput, Map<String, Object>>
{
  private final BranchBankingService branchBankingService;

  public GetAccountInfo(BranchBankingService branchBankingService)
  {
    this.branchBankingService = Objects.requireNonNull(branchBankingService);
  }

  @Override
  public Map<String, Object> execute(GetAccountInfoInput input) throws UseCaseException
  {
    validateInput(input);

    try
    {
      return branchBankingService.getAccountInfo(input.getInstanceId(), input.getAccountId(), input.isHasAccountValidation());
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
    catch (ParseException e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }

  private void validateInput(GetAccountInfoInput input) throws UseCaseException
  {
    if (null == input)
    {
      throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
    }

    if (StringUtils.isBlank(input.getInstanceId()))
    {
      throw new UseCaseException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
    }
  }
}
