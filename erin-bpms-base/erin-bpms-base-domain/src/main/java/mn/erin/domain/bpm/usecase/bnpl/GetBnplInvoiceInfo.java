package mn.erin.domain.bpm.usecase.bnpl;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BnplCoreBankingService;
import mn.erin.domain.bpm.service.BpmServiceException;

import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;

public class GetBnplInvoiceInfo extends AbstractUseCase<String, Map<String, Object>>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GetBnplInvoiceInfo.class);
  private final BnplCoreBankingService bnplCoreBankingService;

  public GetBnplInvoiceInfo(BnplCoreBankingService bnplCoreBankingService)
  {
    this.bnplCoreBankingService = Objects.requireNonNull(bnplCoreBankingService);
  }

  @Override
  public Map<String, Object> execute(String input) throws UseCaseException
  {
    if (StringUtils.isBlank(input))
    {
      throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
    }
    Map<String, Object> bnplInvoiceInfo;

    try{
      bnplInvoiceInfo = bnplCoreBankingService.getBnplInvoiceInfo(input);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
    return bnplInvoiceInfo;
  }
}
