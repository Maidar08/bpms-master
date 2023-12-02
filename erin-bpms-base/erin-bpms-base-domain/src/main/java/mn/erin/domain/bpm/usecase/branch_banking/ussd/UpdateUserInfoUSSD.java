package mn.erin.domain.bpm.usecase.branch_banking.ussd;

import java.text.ParseException;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BranchBankingService;

import static mn.erin.domain.bpm.BpmMessagesConstants.BB_CUSTOMER_INFO_IS_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_CUSTOMER_INFO_IS_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_MAIN_ACCOUNT_IS_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_MAIN_ACCOUNT_IS_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

public class UpdateUserInfoUSSD extends AbstractUseCase<UpdateUserUSSDInput, Map<String, Object>>
{
  private final BranchBankingService branchBankingService;

  public UpdateUserInfoUSSD(BranchBankingService branchBankingService)
  {
    this.branchBankingService = Objects.requireNonNull(branchBankingService, "Branch banking service cannot be null!");
  }

  @Override
  public Map<String, Object> execute(UpdateUserUSSDInput input) throws UseCaseException
  {
    validateInput(input);
    String languageId = input.getLanguageId();
    String instanceId = input.getInstanceId();

    if (languageId == null)
    {
      languageId = "MN";
    }
    try
    {
      return branchBankingService.updateUserUSSD(input.getUserInfo(), languageId, instanceId);
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

  private void validateInput(UpdateUserUSSDInput input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
    }
    if (StringUtils.isBlank(input.getInstanceId()))
    {
      throw new UseCaseException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
    }

    if (input.getUserInfo() == null || input.getUserInfo().isEmpty())
    {
      throw new UseCaseException(BB_CUSTOMER_INFO_IS_NULL_CODE, BB_CUSTOMER_INFO_IS_NULL_MESSAGE);
    }

    if (StringUtils.isBlank(getValidString(input.getUserInfo().get("mainAccount"))))
    {
      throw new UseCaseException(BB_MAIN_ACCOUNT_IS_NULL_CODE, BB_MAIN_ACCOUNT_IS_NULL_MESSAGE);
    }
  }
}
