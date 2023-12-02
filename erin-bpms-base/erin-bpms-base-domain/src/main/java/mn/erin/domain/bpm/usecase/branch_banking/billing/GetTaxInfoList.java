package mn.erin.domain.bpm.usecase.branch_banking.billing;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.branch_banking.TaxInvoice;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BranchBankingService;

import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;

/**
 * @author Lkhagvadorj.A
 **/

public class GetTaxInfoList extends AbstractUseCase<Map<String, Object>, List<TaxInvoice>>
{
  private final BranchBankingService branchBankingService;

  public GetTaxInfoList(BranchBankingService branchBankingService)
  {
    this.branchBankingService = Objects.requireNonNull(branchBankingService, "Branch banking service is required!");
  }

  @Override
  public List<TaxInvoice> execute(Map<String, Object> input) throws UseCaseException
  {
    if (null == input || input.isEmpty())
    {
      throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
    }

    String type = String.valueOf(input.get("type"));
    String value = String.valueOf(input.get("value"));
    String instanceId = String.valueOf(input.get(CASE_INSTANCE_ID));

    try
    {
      return branchBankingService.getTaxInfoList(type, value, instanceId);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
  }
}
