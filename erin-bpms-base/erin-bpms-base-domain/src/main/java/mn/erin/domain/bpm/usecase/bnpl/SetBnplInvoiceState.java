package mn.erin.domain.bpm.usecase.bnpl;

import java.util.HashMap;
import java.util.Map;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BnplCoreBankingService;
import mn.erin.domain.bpm.service.BpmServiceException;

import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.INVOICE_NUM;
import static mn.erin.domain.bpm.BpmModuleConstants.INVOICE_STATE;

public class SetBnplInvoiceState extends AbstractUseCase<SetInvoiceStateInput, Void>
{
  private final BnplCoreBankingService bnplCoreBankingService;

  public SetBnplInvoiceState(BnplCoreBankingService bnplCoreBankingService)
  {
    this.bnplCoreBankingService = bnplCoreBankingService;
  }

  @Override
  public Void execute(SetInvoiceStateInput input) throws UseCaseException
  {
    if (null == input)
    {
      throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
    }

    Map<String, String> inputMap = new HashMap<>();
    inputMap.put(INVOICE_NUM, input.getInvoiceNum());
    inputMap.put(INVOICE_STATE, input.getInvoiceUpdateState());

    try
    {
      bnplCoreBankingService.setBnplInvoiceState(inputMap);
      return null;
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
  }
}
