package mn.erin.domain.bpm.usecase.collateral;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.service.NewCoreBankingService;

public class ModifyOtherCollateralTest
{
  private NewCoreBankingService newCoreBankingService;
  private CaseService caseService;
  private ModifyOtherCollateral useCase;
  private ModifyCollateralInput input;

  @Before
  public void setUp()
  {
    newCoreBankingService = Mockito.mock(NewCoreBankingService.class);
    caseService = Mockito.mock(CaseService.class);
    useCase = new ModifyOtherCollateral(newCoreBankingService, caseService);
    input = new ModifyCollateralInput("123", "123", generateMap());
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_use_case_exception() throws UseCaseException
  {
    useCase.execute(null);
  }
  //
  //  @Test(expected = UseCaseException.class)
  //  public void when_throws_service_exception() throws BpmServiceException, UseCaseException
  //  {
  //    Mockito.when(newCoreBankingService.modifyOtherCollateral(input.getCollateralId(),generateMap(),generateMap(),generateMap(),generateMap())).thenThrow(UseCaseException.class);
  //    useCase.execute(input);
  //  }

  private static Map<String, Object> generateMap()
  {
    Map<String, Object> map = new HashMap<>();
    Object object = new Object();
    map.put("genericInfo", object);
    map.put("collateralInfo", object);
    map.put("inspectionInfo", object);
    map.put("ownershipInfo", object);

    return map;
  }
}
