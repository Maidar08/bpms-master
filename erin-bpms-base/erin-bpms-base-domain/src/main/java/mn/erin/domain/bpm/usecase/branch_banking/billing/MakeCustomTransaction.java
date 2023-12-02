package mn.erin.domain.bpm.usecase.branch_banking.billing;

import java.util.Map;

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
 * @author Odgavaa
 **/

public class MakeCustomTransaction extends AbstractUseCase<Map<String, Object>, Map<String, String>>
{
  private final BranchBankingService branchBankingService;

  public MakeCustomTransaction(BranchBankingService branchBankingService)
  {
    this.branchBankingService = branchBankingService;
  }

  @Override
  public Map<String, String> execute(Map<String, Object> input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
    }
    try
    {
      String instanceId = String.valueOf(input.get(CASE_INSTANCE_ID));
      if (StringUtils.isBlank(instanceId))
      {
        throw new UseCaseException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
      }
      return branchBankingService.makeCustomTransaction(input, instanceId);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
  }
}
