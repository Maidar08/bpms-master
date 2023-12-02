package mn.erin.domain.bpm.usecase.collateral;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.NewCoreBankingService;

public class GetCollateralCodesTest
{
  private NewCoreBankingService newCoreBankingService;
  private GetCollateralCodes useCase;
  private static final String Colltype = "abcabc";

  @Before
  public void setUp()
  {
    newCoreBankingService = Mockito.mock(NewCoreBankingService.class);

    useCase = new GetCollateralCodes(newCoreBankingService);
  }

  @Test(expected = UseCaseException.class)
  public void whenUseCaseException() throws UseCaseException, BpmServiceException
  {
    Mockito.when(newCoreBankingService.getCollateralCode(Colltype)).thenThrow(BpmServiceException.class);
    useCase.execute(Colltype);
  }

  @Test
  public void whenSuccessfulGetCollateralCode() throws UseCaseException, BpmServiceException
  {
    Mockito.when(newCoreBankingService.getCollateralCode(Colltype)).thenReturn(new ArrayList<>());
    GetCollateralCodesOutput output = useCase.execute(Colltype);

    Assert.assertNotNull(output.getCollateralCodes());
  }
}
