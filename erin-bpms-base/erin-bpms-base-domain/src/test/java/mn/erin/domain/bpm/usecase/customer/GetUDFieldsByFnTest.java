package mn.erin.domain.bpm.usecase.customer;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.account.UDField;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CoreBankingService;

/**
 * @author Bilguunbor
 */
public class GetUDFieldsByFnTest
{
  private static final String function = "bodyFunction";

  private CoreBankingService coreBankingService;
  private GetUDFieldsByFn useCase;
  public UDField udField;

  @Before
  public void setUp()
  {
    coreBankingService = Mockito.mock(CoreBankingService.class);
    useCase = new GetUDFieldsByFn(coreBankingService);
  }

  @Test(expected = NullPointerException.class)
  public void when_service_is_null()
  {
    new GetUDFieldsByFn(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_function_is_blank() throws UseCaseException
  {
    useCase.execute("");
  }

  @Test(expected = UseCaseException.class)
  public void when_function_is_null() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_service_exception() throws BpmServiceException, UseCaseException
  {
    Mockito.when(coreBankingService.getUDFieldsByFunction(function)).thenThrow(BpmServiceException.class);
    useCase.execute(function);
  }

  @Test
  public void when_return_ud_field_map() throws BpmServiceException, UseCaseException
  {
    Map<String, UDField> udFieldMap = new HashMap<>();
    udFieldMap.put("key", udField);

    Map<String, UDField> udFieldMap2 = new HashMap<>();
    udFieldMap2.put("key", udField);

    Mockito.when(coreBankingService.getUDFieldsByFunction(function)).thenReturn(udFieldMap);
    GetUDFieldsByFnOutput output = useCase.execute(function);

    Map<String, UDField> outputMap = output.getUdFieldMap();

    Assert.assertEquals(udFieldMap2, outputMap);
  }
}
