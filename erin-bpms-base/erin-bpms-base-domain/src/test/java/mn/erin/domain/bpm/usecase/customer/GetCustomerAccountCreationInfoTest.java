package mn.erin.domain.bpm.usecase.customer;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.account.UDField;
import mn.erin.domain.bpm.model.account.UDFieldId;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CoreBankingService;

/**
 * @author Bilguunbor
 */
public class GetCustomerAccountCreationInfoTest
{
  private CoreBankingService coreBankingService;
  private GetCustomerAccountCreationInfo useCase;

  @Before
  public void setUp()
  {
    coreBankingService = Mockito.mock(CoreBankingService.class);
    useCase = new GetCustomerAccountCreationInfo(coreBankingService);
  }

  @Test(expected = NullPointerException.class)
  public void when_service_is_null()
  {
    new GetCustomerAccountCreationInfo(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_is_null() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_service_exception() throws BpmServiceException, UseCaseException
  {
    Mockito.when(coreBankingService.getUDFields("input")).thenThrow(BpmServiceException.class);
    useCase.execute("input");
  }

  @Test
  public void when_work_correctly() throws UseCaseException, BpmServiceException
  {
    Map<String, UDField> udFieldMap = generateUdFieldMap();
    Mockito.when(coreBankingService.getUDFields("input")).thenReturn(udFieldMap);

    GetCustomerAccountCreationInfoOutput output = useCase.execute("input");
    Assert.assertEquals(udFieldMap.get("SUBTYPE"), output.getSubType());
    Assert.assertEquals(udFieldMap.get("REASON_OVERDUE_ANHAARAL_TAT"), output.getLateReasonAttention());
    Assert.assertEquals(udFieldMap.get("FIRST_ACCOUNT_NUMBER").getDefaultValue(), output.getFirstAccountNumber().getDefaultValue());
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_use_case_exception_on_private_method() throws BpmServiceException, UseCaseException
  {
    Map<String, UDField> udFieldMap = generateInvalidUdFieldMap();
    Mockito.when(coreBankingService.getUDFields("input")).thenReturn(udFieldMap);

    useCase.execute("input");
  }

  private Map<String, UDField> generateUdFieldMap()
  {
    Map<String, UDField> udFieldMap = new HashMap<>();
    UDField udField = new UDField(UDFieldId.valueOf("123"), "123", "T", "1", true, "123", "defaultValue", true, true);

    udFieldMap.put("SUBTYPE", udField);
    udFieldMap.put("REASON_OVERDUE_ANHAARAL_TAT", udField);
    udFieldMap.put("FIRST_ACCOUNT_NUMBER", udField);

    return udFieldMap;
  }

  private Map<String, UDField> generateInvalidUdFieldMap()
  {
    Map<String, UDField> udFieldMap = new HashMap<>();
    UDField udField = new UDField(UDFieldId.valueOf("123"), "123", "T", "1", true, "123", "defaultValue", true, true);

    udFieldMap.put("1", udField);
    udFieldMap.put("2", udField);
    udFieldMap.put("3", udField);

    return udFieldMap;
  }
}
