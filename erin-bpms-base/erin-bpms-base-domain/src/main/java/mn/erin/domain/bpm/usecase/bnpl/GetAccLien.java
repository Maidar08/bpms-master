package mn.erin.domain.bpm.usecase.bnpl;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.DirectOnlineCoreBankingService;

import static mn.erin.domain.bpm.BpmMessagesConstants.ACCOUNT_NUMBER_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.ACCOUNT_NUMBER_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.MODULE_TYPE_IS_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.MODULE_TYPE_IS_NULL_MESSAGE;

public class GetAccLien extends AbstractUseCase<Map<String, String>, Map<String, Object>>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GetAccLien.class);
  private final DirectOnlineCoreBankingService directOnlineCoreBankingService;

  public GetAccLien(DirectOnlineCoreBankingService directOnlineCoreBankingService)
  {
    this.directOnlineCoreBankingService = Objects.requireNonNull(directOnlineCoreBankingService);
  }

  @Override
  public Map<String, Object> execute(Map<String, String> input) throws UseCaseException
  {
    if (null == input)
    {
      throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
    }
    String acctId = input.get("AcctId");
    String moduleType = input.get("ModuleType");

    Map<String, Object> accLien;

    try{
      validate(acctId, moduleType);
      accLien = directOnlineCoreBankingService.getAccLien(input);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
    return accLien;
  }
  private void validate(String acctId, String moduleType) throws UseCaseException
  {
    if (StringUtils.isBlank(acctId))
    {
      throw new UseCaseException(ACCOUNT_NUMBER_NULL_CODE, ACCOUNT_NUMBER_NULL_MESSAGE);
    }

    if (StringUtils.isBlank(moduleType))
    {
      throw new UseCaseException(MODULE_TYPE_IS_NULL_CODE, MODULE_TYPE_IS_NULL_MESSAGE);
    }
  }
}
