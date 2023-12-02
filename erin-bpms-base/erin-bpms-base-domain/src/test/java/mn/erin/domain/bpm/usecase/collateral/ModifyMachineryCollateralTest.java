package mn.erin.domain.bpm.usecase.collateral;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.service.NewCoreBankingService;

public class ModifyMachineryCollateralTest
{
  private NewCoreBankingService newCoreBankingService;
  private CaseService caseService;
  private ModifyMachineryCollateral useCase;
  private ModifyCollateralInput input;

  @Before
  public void setUp()
  {
    newCoreBankingService = Mockito.mock(NewCoreBankingService.class);
    caseService = Mockito.mock(CaseService.class);
    useCase = new ModifyMachineryCollateral(newCoreBankingService, caseService);
    input = new ModifyCollateralInput("123", "123", generateMap());
  }

  @Test(expected = NullPointerException.class)
  public void when_new_core_banking_service_is_null()
  {
    new ModifyMachineryCollateral(null, caseService);
  }

  @Test(expected = NullPointerException.class)
  public void when_case_service_is_null()
  {
    new ModifyMachineryCollateral(newCoreBankingService, null);
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_use_case_exception() throws UseCaseException
  {
    useCase.execute(null);
  }

  //  @Test(expected = UseCaseException.class)
  //  public void when_throws_service_exception() throws BpmServiceException, UseCaseException
  //  {
  //    CreateCollateralInput collateralInput = new CreateCollateralInput(generateMap(),generateMap(),generateMap(),generateMap());
  //    Mockito.when(newCoreBankingService.modifyMachineryCollateral(input.getCollateralId(),input.getProperties(),generateMap(),generateMap(),generateMap())).thenReturn(
  //        String.valueOf(collateralInput));
  //      useCase.execute(input);
  //    Assert.assertEquals("123","123","123");
  //  }

  private static Map<String, Object> generateMap()
  {
    Map<String, Object> map = new HashMap<>();
    Object object = new Object();
    map.put("A", object);
    map.put("A", object);
    map.put("A", object);
    map.put("A", object);

    return map;
  }
}
