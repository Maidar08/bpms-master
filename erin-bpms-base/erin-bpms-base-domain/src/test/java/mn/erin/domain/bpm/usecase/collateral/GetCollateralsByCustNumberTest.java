package mn.erin.domain.bpm.usecase.collateral;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.NewCoreBankingService;

public class GetCollateralsByCustNumberTest
{
  private NewCoreBankingService newCoreBankingService;
  private static final String CIF_NUMBER = "C12345";
  private GetCollateralsByCustNumber useCase;

  @Before
  public void setUp()
  {
    newCoreBankingService = Mockito.mock(NewCoreBankingService.class);

    useCase = new GetCollateralsByCustNumber(newCoreBankingService);
  }

  @Test(expected = UseCaseException.class)
  public void whenUseCaseException() throws UseCaseException, BpmServiceException
  {
    Mockito.when(newCoreBankingService.getCollateralsByCifNumber(CIF_NUMBER)).thenThrow(BpmServiceException.class);
    useCase.execute(CIF_NUMBER);
  }

  @Test
  public void whenSuccessfulGetCollaterals() throws UseCaseException, BpmServiceException
  {
    Mockito.when(newCoreBankingService.getCollateralsByCifNumber(CIF_NUMBER)).thenReturn(new ArrayList<>());
    GetCollateralsByCustNumberOutput output = useCase.execute(CIF_NUMBER);

    Assert.assertNotNull(output.getCollaterals());
  }
}
