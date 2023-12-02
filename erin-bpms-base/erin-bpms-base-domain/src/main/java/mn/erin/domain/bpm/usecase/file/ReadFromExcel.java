package mn.erin.domain.bpm.usecase.file;

import java.util.List;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.core.env.Environment;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.file.ExcelHeader;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.ExcelService;

import static mn.erin.domain.bpm.BpmBranchBankingConstants.AMOUNT;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_BASE64_IS_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_BASE64_IS_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_MISSING_TABLE_HEADER_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_MISSING_TABLE_HEADER_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_RESPONSE_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_RESPONSE_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_TRANSACTION_LIMIT_EXCEPTION_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_TRANSACTION_LIMIT_EXCEPTION_MESSAGE;
import static mn.erin.domain.bpm.util.process.BpmNumberUtils.getThousandSepStrWithDigit;

/**
 * @author Bilguunbor
 **/

public class ReadFromExcel extends AbstractUseCase<ReadFromExcelInput, JSONObject>
{
  private final ExcelService excelService;
  private final Environment environment;
  private final static String XAC_TRANSACTION_MAX = "xacTransactionMax";

  public ReadFromExcel(ExcelService excelService, Environment environment)
  {
    this.excelService = Objects.requireNonNull(excelService, "BPMS Excel service is required!");
    this.environment = environment;
  }

  @Override
  public JSONObject execute(ReadFromExcelInput input) throws UseCaseException
  {
    validateNotNull(input, "Read from excel input is null!");
    validateInput(input);

    JSONArray headerValues = entityListToJsonArray(input.getHeaderValues());
    try
    {
      JSONArray responseArray = excelService.readFromExcel(input.getContentAsBase64(), headerValues);
      if (responseArray == null)
      {
        throw new UseCaseException(BB_RESPONSE_NULL_CODE, BB_RESPONSE_NULL_MESSAGE);
      }

      checkExcelSize(responseArray);
      return calculateTransactionSum(responseArray);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
  }

  private static void validateInput(ReadFromExcelInput input) throws UseCaseException
  {
    if (input.getContentAsBase64().isEmpty())
    {
      throw new UseCaseException(BB_BASE64_IS_NULL_CODE, BB_BASE64_IS_NULL_MESSAGE);
    }
    else if (input.getHeaderValues().isEmpty())
    {
      throw new UseCaseException(BB_MISSING_TABLE_HEADER_CODE, BB_MISSING_TABLE_HEADER_MESSAGE);
    }
  }

  private void checkExcelSize(JSONArray responseArray) throws UseCaseException
  {
    int maxSize = getTransactionMaxSize();
    if (maxSize < responseArray.length())
    {
      throw new UseCaseException(BB_TRANSACTION_LIMIT_EXCEPTION_CODE, BB_TRANSACTION_LIMIT_EXCEPTION_MESSAGE + maxSize);
    }
  }

  private static JSONObject calculateTransactionSum(JSONArray responseArray)
  {
    int transactionsNumber = responseArray.length();
    double transactionsSum = 0;
    for (int index = 0; index < responseArray.length(); index++)
    {
      JSONObject tableData = responseArray.getJSONObject(index);
      double amount = tableData.getDouble(AMOUNT);
      transactionsSum += amount;
      String amountString = getThousandSepStrWithDigit(amount, 3);
      tableData.put(AMOUNT, amountString);
      responseArray.put(index, tableData);
    }

    JSONObject formInfo = new JSONObject();

    formInfo.put("transactionCount", transactionsNumber);
    formInfo.put("transactionTotalAmount", getThousandSepStrWithDigit(transactionsSum, 3));
    formInfo.put("tableData", responseArray);
    return formInfo;
  }

  private static JSONArray entityListToJsonArray(List<ExcelHeader> headerList)
  {
    JSONArray inputArray = new JSONArray();

    for (ExcelHeader excelHeader : headerList)
    {
      JSONObject inputObject = new JSONObject();
      inputObject.put("key", excelHeader.getKey());
      inputObject.put("headerName", excelHeader.getHeaderValue());

      inputArray.put(inputObject);
    }

    return inputArray;
  }

  private int getTransactionMaxSize()
  {
    return Integer.parseInt(environment.getProperty(XAC_TRANSACTION_MAX));
  }
}
