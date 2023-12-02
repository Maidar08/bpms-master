package mn.erin.domain.bpm.usecase.branch_banking.ussd;

import java.text.ParseException;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BranchBankingService;

import static mn.erin.domain.bpm.BpmMessagesConstants.BB_ONE_OF_CIF_PHONE_IS_REQUIRED_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_ONE_OF_CIF_PHONE_IS_REQUIRED_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

public class GetUserInfoFromUSSD extends AbstractUseCase<GetUserInfoFromUSSDInput, Object>
{

  private final BranchBankingService branchBankingService;

  public GetUserInfoFromUSSD(BranchBankingService branchBankingService)
  {
    this.branchBankingService = Objects.requireNonNull(branchBankingService, "Branch banking service cannot be null!");
  }

  @Override
  public Map<String, Object> execute(GetUserInfoFromUSSDInput input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
    }

    String cif = getValidString(input.getCif());
    String phone = getValidString(input.getPhone());
    if (StringUtils.isBlank(cif) && StringUtils.isBlank(phone))
    {
      throw new UseCaseException(BB_ONE_OF_CIF_PHONE_IS_REQUIRED_CODE, BB_ONE_OF_CIF_PHONE_IS_REQUIRED_MESSAGE);
    }
    if (StringUtils.isBlank(input.getInstanceId()))
    {
      throw new UseCaseException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
    }

    try
    {
      return this.branchBankingService.getUserInfoFromUSSD(cif, phone, input.getBranch(), input.getInstanceId());
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
    catch (ParseException parseException)
    {
      throw new UseCaseException(parseException.getMessage());
    }
  }
}
