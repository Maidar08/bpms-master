package mn.erin.domain.bpm.usecase.collateral;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.NewCoreBankingService;

public class LinkCollateralsTest
{
  private NewCoreBankingService newCoreBankingService;
  private LinkCollaterals useCase;
  private LinkCollateralsInput input;

  @Before
  public void setUp()
  {
    newCoreBankingService = Mockito.mock(NewCoreBankingService.class);
    useCase = new LinkCollaterals(newCoreBankingService);
    input = new LinkCollateralsInput("123", "123", generateMap());
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_service_exception() throws BpmServiceException, UseCaseException
  {
    Mockito.when(newCoreBankingService.linkCollaterals(input.getAccountNumber(), input.getLinkageType(), input.getCollaterals()))
        .thenThrow(BpmServiceException.class);
    useCase.execute(input);
  }

  @Test
  public void when_works_correctly() throws BpmServiceException, UseCaseException
  {
    Mockito.when(newCoreBankingService.linkCollaterals(input.getAccountNumber(), input.getLinkageType(), input.getCollaterals())).thenReturn(true);
    LinkCollateralsOutput output = useCase.execute(input);
    Assert.assertTrue(String.valueOf(output), true);
  }

  private static Map<String, Object> generateMap()
  {
    Map<String, Object> map = new HashMap<>();
    Object object = new Object();
    map.put("A", object);

    return map;
  }
}
