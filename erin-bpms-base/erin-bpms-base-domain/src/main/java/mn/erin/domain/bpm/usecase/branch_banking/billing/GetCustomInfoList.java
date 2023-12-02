package mn.erin.domain.bpm.usecase.branch_banking.billing;

import java.util.List;
import java.util.Map;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.branch_banking.CustomInvoice;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BranchBankingService;

import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;

public class GetCustomInfoList extends AbstractUseCase<Map<String, Object>, List<CustomInvoice>>

{
  public final BranchBankingService branchBankingService;

  public GetCustomInfoList(BranchBankingService branchBankingService)
  {
    this.branchBankingService = branchBankingService;
  }

  @Override
  public List<CustomInvoice> execute(Map<String, Object> input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
    }

    String type = String.valueOf(input.get("type"));
    String searchValue = String.valueOf(input.get("value"));
    String instanceId = String.valueOf(input.get(CASE_INSTANCE_ID));

    try
    {
      return branchBankingService.getCustomInfoList(type, searchValue, instanceId);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
  }
}
