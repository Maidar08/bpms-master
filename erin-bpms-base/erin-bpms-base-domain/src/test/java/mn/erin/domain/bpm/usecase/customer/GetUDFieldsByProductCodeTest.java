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
import mn.erin.domain.bpm.service.NewCoreBankingService;

import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;

/**
 * @author Bilguunbor
 */
public class GetUDFieldsByProductCodeTest
{
  private static final String PROD_CODE = "productCode";

  private NewCoreBankingService newCoreBankingService;
  private GetUDFieldsByProductCode useCase;

  @Before
  public void setUp()
  {
    newCoreBankingService = Mockito.mock(NewCoreBankingService.class);
    useCase = new GetUDFieldsByProductCode(newCoreBankingService);
  }

  @Test(expected = NullPointerException.class)
  public void when_service_is_null()
  {
    new GetUDFieldsByProductCode(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_is_blank() throws UseCaseException
  {
    useCase.execute(new HashMap<>());
  }

  @Test(expected = UseCaseException.class)
  public void when_input_is_null() throws UseCaseException
  {
    useCase.execute(new HashMap<>());
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_service_exception() throws BpmServiceException, UseCaseException
  {
    Map<String, String> input = new HashMap<>();
    input.put(PROCESS_TYPE_ID, ONLINE_LEASING_PROCESS_TYPE_ID);
    input.put(PHONE_NUMBER, "phoneNumber");
    input.put(PROD_CODE, PROD_CODE);
    Mockito.when(newCoreBankingService.getUDFields(input)).thenThrow(BpmServiceException.class);
    useCase.execute(input);
  }


  @Test
  public void when_ud_field_found() throws BpmServiceException, UseCaseException
  {
    Map<String, UDField> udFieldMap = generateMap();

    GetUDFieldsByProductCodeOutput output = new GetUDFieldsByProductCodeOutput(udFieldMap, "SUBTYPE");
    Map<String, String> input = new HashMap<>();
    input.put(PROCESS_TYPE_ID, ONLINE_LEASING_PROCESS_TYPE_ID);
    input.put(PHONE_NUMBER, "phoneNumber");
    input.put(PROD_CODE, PROD_CODE);
    Mockito.when(newCoreBankingService.getUDFields(input)).thenReturn(generateCoreUdfMap());
    GetUDFieldsByProductCodeOutput output1 = useCase.execute(input);

//    Assert.assertEquals(output.getSubTypeName(), output1.getSubTypeName());
    Assert.assertEquals(output.getUdFieldsMap(), output1.getUdFieldsMap());
  }

  private Map<String, UDField> generateCoreUdfMap()
  {
    Map<String, UDField> returnMap = new HashMap<>();
    returnMap.put("Account Free Code 2", null);
    returnMap.put("Account Free Code 3", null);
    returnMap.put("Type of Advance", null);
    returnMap.put("Borrower category Code", null);
    returnMap.put("FREE_CODE_4", null);
    returnMap.put("FREE_CODE_5", null);
    returnMap.put("FREE_CODE_6", null);
    returnMap.put("FREE_CODE_7", null);
    returnMap.put("FREE_CODE_8", null);
    returnMap.put("FREE_CODE_9", null);
    returnMap.put("FREE_CODE_10", null);
    returnMap.put("Nature of Advance", null);
    returnMap.put("Customer Industry Type", null);
    returnMap.put("Purpose of Advance", null);
    returnMap.put("Account Free Code 1", null);
    returnMap.put("FREE_TEXT_1", null);
    returnMap.put("Mode of Advance", null);

    return returnMap;
  }

  private Map<String, UDField> generateMap()
  {
    Map<String, UDField> returnMap = new HashMap<>();
    returnMap.put("AccountFreeCode2", null);
    returnMap.put("AccountFreeCode3", null);
    returnMap.put("TypeOfAdvance", null);
    returnMap.put("BorrowerCategoryCode", null);
    returnMap.put("FREE_CODE_4", null);
    returnMap.put("FREE_CODE_5", null);
    returnMap.put("FREE_CODE_6", null);
    returnMap.put("FREE_CODE_7", null);
    returnMap.put("FREE_CODE_8", null);
    returnMap.put("FREE_CODE_9", null);
    returnMap.put("FREE_CODE_10", null);
    returnMap.put("NatureOfAdvance", null);
    returnMap.put("CustomerIndustryType", null);
    returnMap.put("PurposeOfAdvance", null);
    returnMap.put("AccountFreeCode1", null);
    returnMap.put("FREE_TEXT_1", null);
    returnMap.put("modeOfAdvance", null);

    return returnMap;
  }
}

