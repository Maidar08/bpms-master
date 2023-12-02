package mn.erin.domain.bpm.usecase.branch_banking.billing;

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
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;

/**
 * @author Lkhagvadorj.A
 **/

public class MakeTaxTransaction extends AbstractUseCase<Map<String, String>, Map<String, String>>
{
  private final BranchBankingService branchBankingService;

  public MakeTaxTransaction(BranchBankingService branchBankingService)
  {
    this.branchBankingService = Objects.requireNonNull(branchBankingService, "Branch banking service is required!");
  }

  @Override
  public Map<String, String> execute(Map<String, String> input) throws UseCaseException
  {
    if (null == input || input.isEmpty())
    {
      throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
    }

    try
    {
      String instanceId = input.get(CASE_INSTANCE_ID);
      if (StringUtils.isBlank(instanceId))
      {
        throw new UseCaseException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
      }
      return branchBankingService.makeTaxTransaction(input, instanceId);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
  }
}
