package mn.erin.domain.bpm.usecase.bnpl;

import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.DirectOnlineCoreBankingService;

public class ModifyAccLien extends AbstractUseCase<Map<String, Object>, Map<String, Object>>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(ModifyAccLien.class);
  private final DirectOnlineCoreBankingService directOnlineCoreBankingService;

  public ModifyAccLien(DirectOnlineCoreBankingService directOnlineCoreBankingService)
  {
    this.directOnlineCoreBankingService = Objects.requireNonNull(directOnlineCoreBankingService);
  }

  @Override
  public Map<String, Object> execute(Map<String, Object> input) throws UseCaseException
  {
    Map<String, Object> addAccLien;
    try{
      addAccLien = directOnlineCoreBankingService.modifyAccLien(input);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
    return addAccLien;
  }
}
