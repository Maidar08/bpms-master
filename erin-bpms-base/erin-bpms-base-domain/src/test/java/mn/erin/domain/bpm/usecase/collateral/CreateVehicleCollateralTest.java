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

/**
 * @author Sukhbat
 */

public class CreateVehicleCollateralTest
{
  private NewCoreBankingService coreBankingService;
  private CreateVehicleCollateral useCase;
  private CreateCollateralInput input;

  @Before
  public void setUp()
  {
    coreBankingService = Mockito.mock(NewCoreBankingService.class);
    useCase = new CreateVehicleCollateral(coreBankingService);
    input = new CreateCollateralInput(generateMap(), generateMap(), generateMap(), generateMap());
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_use_case_exception() throws UseCaseException, BpmServiceException
  {
    useCase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_service_exception() throws BpmServiceException, UseCaseException
  {
    Mockito.when(
            coreBankingService.createVehicleCollateral(input.getGenericInfo(), input.getCollateralInfo(), input.getInspectionInfo(), input.getOwnershipInfo()))
        .thenThrow(BpmServiceException.class);
    useCase.execute(input);
  }

  @Test
  public void when_works_correctly() throws BpmServiceException, UseCaseException
  {
    Mockito.when(
            coreBankingService.createVehicleCollateral(input.getGenericInfo(), input.getCollateralInfo(), input.getInspectionInfo(), input.getOwnershipInfo()))
        .thenReturn(("123"));
    String output = useCase.execute(input);
    Assert.assertEquals(output, "123");
  }

  private static Map<String, Object> generateMap()
  {
    Map<String, Object> map = new HashMap<>();
    Object object = new Object();

    map.put("a", object);
    map.put("d", object);
    map.put("c", object);
    map.put("s", object);
    return null;
  }
}
