package mn.erin.domain.bpm.usecase.file;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.file.ExcelHeader;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.ExcelService;

/**
 * @author Bilguunbor
 **/
public class ReadFromExcelTest
{
  private static final List<ExcelHeader> EXCEL_HEADER_LIST = Collections.unmodifiableList(
      Arrays.asList(new ExcelHeader("no", "Дугаар"), new ExcelHeader("customerName", "Харилцагчийн нэр"), new ExcelHeader("accountId", "Дансны дугаар"),
          new ExcelHeader("transactionCcy", "Гүйлгээний валют"), new ExcelHeader("amount", "Мөнгөн дүн")));
  private static final String CONTENT_AS_BASE64 = "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";
  private static final String RESPONSE_ARRAY = "[\n"
      + "            {\n"
      + "                \"transactionCcy\": \"MNT\",\n"
      + "                \"no\": 1,\n"
      + "                \"accountId\": 5000002037,\n"
      + "                \"amount\": 50000,\n"
      + "                \"customerName\": \"АВИРМЭД БАЛДАН\"\n"
      + "            },\n"
      + "            {\n"
      + "                \"transactionCcy\": \"MNT\",\n"
      + "                \"no\": 2,\n"
      + "                \"accountId\": 5000001302,\n"
      + "                \"amount\": 60000,\n"
      + "                \"customerName\": \"АНУДАРЬ АЛТАНСҮХ\"\n"
      + "            },\n"
      + "            {\n"
      + "                \"transactionCcy\": \"MNT\",\n"
      + "                \"no\": 3,\n"
      + "                \"accountId\": 5000007310,\n"
      + "                \"amount\": 70000,\n"
      + "                \"customerName\": \"БААТАР УРИАНХАЙ БАЯНБААТАР\"\n"
      + "            },\n"
      + "            {\n"
      + "                \"transactionCcy\": \"MNT\",\n"
      + "                \"no\": 4,\n"
      + "                \"accountId\": 5000001296,\n"
      + "                \"amount\": 80000,\n"
      + "                \"customerName\": \"БАТАА ЖАВЗМАА\"\n"
      + "            },\n"
      + "            {\n"
      + "                \"transactionCcy\": \"MNT\",\n"
      + "                \"no\": 5,\n"
      + "                \"accountId\": 5000002106,\n"
      + "                \"amount\": 90000,\n"
      + "                \"customerName\": \"БАТХИШИГ ЯЛАЛТ\"\n"
      + "            },\n"
      + "            {\n"
      + "                \"transactionCcy\": \"MNT\",\n"
      + "                \"no\": 6,\n"
      + "                \"accountId\": 5000002010,\n"
      + "                \"amount\": 100000,\n"
      + "                \"customerName\": \"БАЯР ТАВИТ\"\n"
      + "            },\n"
      + "            {\n"
      + "                \"transactionCcy\": \"MNT\",\n"
      + "                \"no\": 7,\n"
      + "                \"accountId\": 5000001381,\n"
      + "                \"amount\": 110000,\n"
      + "                \"customerName\": \"МАНДУХАЙ САРАНХҮҮ\"\n"
      + "            },\n"
      + "            {\n"
      + "                \"transactionCcy\": \"MNT\",\n"
      + "                \"no\": 8,\n"
      + "                \"accountId\": 5000001216,\n"
      + "                \"amount\": 120000,\n"
      + "                \"customerName\": \"МЯГМАРЖАВ ЛХАГВАДУЛАМ\"\n"
      + "            },\n"
      + "            {\n"
      + "                \"transactionCcy\": \"MNT\",\n"
      + "                \"no\": 9,\n"
      + "                \"accountId\": 5000000762,\n"
      + "                \"amount\": 130000,\n"
      + "                \"customerName\": \"ТАМЖИД БАЛДОРЖ\"\n"
      + "            },\n"
      + "            {\n"
      + "                \"transactionCcy\": \"MNT\",\n"
      + "                \"no\": 10,\n"
      + "                \"accountId\": 5000002040,\n"
      + "                \"amount\": 140000,\n"
      + "                \"customerName\": \"УУГАНБАЯР ДАВАА\"\n"
      + "            },\n"
      + "            {\n"
      + "                \"transactionCcy\": \"MNT\",\n"
      + "                \"no\": 11,\n"
      + "                \"accountId\": 5000001998,\n"
      + "                \"amount\": 150000,\n"
      + "                \"customerName\": \"УЯНГА ЭНХ-АМГАЛАН\"\n"
      + "            },\n"
      + "            {\n"
      + "                \"transactionCcy\": \"MNT\",\n"
      + "                \"no\": 12,\n"
      + "                \"accountId\": 5000004837,\n"
      + "                \"amount\": 160000,\n"
      + "                \"customerName\": \"ХИШИГЖАРГАЛ ОЮУН\"\n"
      + "            },\n"
      + "            {\n"
      + "                \"transactionCcy\": \"MNT\",\n"
      + "                \"no\": 13,\n"
      + "                \"accountId\": 5000007571,\n"
      + "                \"amount\": 170000,\n"
      + "                \"customerName\": \"ЭГШИГЛЭН ГАНБАТ\"\n"
      + "            },\n"
      + "            {\n"
      + "                \"transactionCcy\": \"MNT\",\n"
      + "                \"no\": 14,\n"
      + "                \"accountId\": 5000002095,\n"
      + "                \"amount\": 180000,\n"
      + "                \"customerName\": \"ЭНХМӨНХ САРАНЦЭЦЭГ\"\n"
      + "            }\n"
      + "        ]";

  private ExcelService excelService;
  private Environment environment;
  private ReadFromExcelInput input;
  private ReadFromExcel useCase;

  @Before
  public void setUp()
  {
    excelService = Mockito.mock(ExcelService.class);
    environment = Mockito.mock(Environment.class);
    input = new ReadFromExcelInput(CONTENT_AS_BASE64, EXCEL_HEADER_LIST);
    useCase = new ReadFromExcel(excelService, environment);
  }

  @Test(expected = UseCaseException.class)
  public void when_returns_empty_json_array() throws BpmServiceException, UseCaseException
  {
    Mockito.when(excelService.readFromExcel(Mockito.anyString(), Mockito.any())).thenReturn(null);
    Mockito.when(environment.getProperty("xacTransactionMax")).thenReturn("1000");
    useCase.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_service_exception() throws BpmServiceException, UseCaseException
  {
    Mockito.when(excelService.readFromExcel(Mockito.anyString(), Mockito.any())).thenThrow(BpmServiceException.class);
    useCase.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void when_content_as_base64_is_null() throws UseCaseException
  {
    useCase.execute(new ReadFromExcelInput("", EXCEL_HEADER_LIST));
  }

  @Test(expected = UseCaseException.class)
  public void when_excel_header_list_is_null() throws UseCaseException
  {
    useCase.execute(new ReadFromExcelInput(CONTENT_AS_BASE64, Collections.emptyList()));
  }

  @Test
  public void when_works_correctly() throws UseCaseException, BpmServiceException
  {
    JSONArray responseArray = new JSONArray(RESPONSE_ARRAY);
    Mockito.when(excelService.readFromExcel(Mockito.anyString(), Mockito.any())).thenReturn(responseArray);
    Mockito.when(environment.getProperty("xacTransactionMax")).thenReturn("1000");

    JSONObject output = useCase.execute(input);
    Assert.assertEquals(3, output.length());
  }
}
