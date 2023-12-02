package mn.erin.domain.bpm.usecase.branch_banking.ussd;

import java.text.ParseException;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BranchBankingService;

import static mn.erin.domain.bpm.BpmMessagesConstants.BB_ID_IS_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_ID_IS_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.BRANCH_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.ID;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANCE_ID;

public class MakeUserEndorseOnUSSD extends AbstractUseCase<Map<String, String>, Object>
{

  private final BranchBankingService branchBankingService;

  public MakeUserEndorseOnUSSD(BranchBankingService branchBankingService)
  {
    this.branchBankingService = Objects.requireNonNull(branchBankingService, "Branch banking service cannot be null!");
  }

  @Override
  public Boolean execute(Map<String, String> input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
    }

    if (StringUtils.isBlank(input.get(ID)))
    {
      throw new UseCaseException(BB_ID_IS_NULL_CODE, BB_ID_IS_NULL_MESSAGE);
    }
    if (StringUtils.isBlank(input.get(INSTANCE_ID)))
    {
      throw new UseCaseException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
    }

    try
    {
      return this.branchBankingService.makeUserEndorse(input.get(ID), input.get(BRANCH_NUMBER), input.get(INSTANCE_ID));
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