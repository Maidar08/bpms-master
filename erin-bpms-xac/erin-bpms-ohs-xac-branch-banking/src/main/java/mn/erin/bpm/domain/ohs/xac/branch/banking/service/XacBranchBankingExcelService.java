package mn.erin.bpm.domain.ohs.xac.branch.banking.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.commons.codec.binary.Base64;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.json.JSONArray;
import org.json.JSONObject;

import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.ExcelService;

import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.CANNOT_CREATE_FILE_INPUT_STREAM_CODE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.CANNOT_CREATE_FILE_INPUT_STREAM_MESSAGE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.CONTENT_AS_BASE64_NULL_CODE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.CONTENT_AS_BASE64_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.HEADER_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.KEY;

/**
 * @author Bilguunbor
 **/

public class XacBranchBankingExcelService implements ExcelService
{
  @Override
  public JSONArray readFromExcel(String base64, JSONArray headerValues) throws BpmServiceException
  {
    if (base64.isEmpty())
    {
      throw new BpmServiceException(CONTENT_AS_BASE64_NULL_CODE, CONTENT_AS_BASE64_NULL_MESSAGE);
    }

    byte[] bytes = Base64.decodeBase64(base64);

    try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes))
    {
      Workbook workbook = WorkbookFactory.create(byteArrayInputStream);
      return extractFromWorkbook(headerValues, workbook);
    }
    catch (IOException | InvalidFormatException e)
    {
      throw new BpmServiceException(CANNOT_CREATE_FILE_INPUT_STREAM_CODE, CANNOT_CREATE_FILE_INPUT_STREAM_MESSAGE);
    }
  }

  private JSONArray extractFromWorkbook(JSONArray headerValues, Workbook workbook)
  {
    Sheet sheet = workbook.getSheetAt(0);
    return extractFromSheet(sheet, workbook, headerValues);
  }

  private static JSONArray extractFromSheet(Sheet sheet, Workbook workbook, JSONArray headerValues)
  {
    FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

    JSONArray responseArray = new JSONArray();

    return extractTable(responseArray, sheet, formulaEvaluator, headerValues);
  }

  private static JSONArray extractTable(JSONArray responseArray, Sheet sheet, FormulaEvaluator formulaEvaluator, JSONArray headerValues)
  {
    int[] table = findTable(sheet, formulaEvaluator, headerValues);

    int startColumn = table[0];
    int endColumn = table[2];
    int rowNumber = table[1];

    return extractTableContent(responseArray, headerValues, sheet, formulaEvaluator, rowNumber, startColumn, endColumn);
  }

  private static int[] findTable(Sheet sheet, FormulaEvaluator formulaEvaluator, JSONArray headerValues)
  {
    JSONObject headerValue = headerValues.getJSONObject(0);
    for (Row row : sheet)
    {
      for (Cell cell : row)
      {
        if (matchWithHeader(headerValue, formulaEvaluator, cell))
        {
          int startColumn = cell.getColumnIndex();
          return new int[] { startColumn, cell.getRowIndex(), startColumn + headerValues.length() - 1 };
        }
      }
    }
    return new int[0];
  }

  private static boolean isValidHeaderType(FormulaEvaluator formulaEvaluator, Cell cell)
  {
    return formulaEvaluator.evaluateInCell(cell).getCellTypeEnum() == CellType.STRING
        || formulaEvaluator.evaluateInCell(cell).getCellTypeEnum() == CellType.NUMERIC;
  }

  private static String getHeaderCellValue(FormulaEvaluator formulaEvaluator, Cell cell)
  {
    if (formulaEvaluator.evaluateInCell(cell).getCellTypeEnum() == CellType.NUMERIC)
    {
      return String.valueOf(Math.round(cell.getNumericCellValue())).trim();
    }
    return cell.getStringCellValue().trim();
  }

  private static boolean matchWithHeader(JSONObject headerValue, FormulaEvaluator formulaEvaluator, Cell cell)
  {
    if (isValidHeaderType(formulaEvaluator, cell))
    {
      String cellValue = getHeaderCellValue(formulaEvaluator, cell);

      return headerValue.getString(HEADER_NAME).equals(cellValue);
    }
    return false;
  }

  private static JSONArray extractTableContent(JSONArray responseArray, JSONArray headerValues, Sheet sheet, FormulaEvaluator formulaEvaluator, int rowNumber,
      int startColumn, int endColumn)
  {
    for (int rowIndex = rowNumber + 1; rowIndex <= sheet.getLastRowNum(); rowIndex++)
    {
      JSONObject rowObject = extractRowContent(sheet.getRow(rowIndex), formulaEvaluator, headerValues, startColumn, endColumn);

      if (null == rowObject || rowObject.isEmpty())
      {
        return responseArray;
      }

      if (rowObject.length() == headerValues.length())
      {
        responseArray.put(rowObject);
      }
    }

    return responseArray;
  }

  private static JSONObject extractRowContent(Row row, FormulaEvaluator formulaEvaluator, JSONArray headerValues, int startColumn, int endColumn)
  {
    if (null == row)
    {
      return null;
    }
    int index = 0;
    JSONObject rowObject = new JSONObject();

    for (Cell cell : row)
    {
      extractCellContent(startColumn, endColumn, index, cell, formulaEvaluator, headerValues, rowObject);
      index++;
    }

    return rowObject;
  }

  private static void extractCellContent(int startColumn, int endColumn, int index, Cell cell, FormulaEvaluator formulaEvaluator, JSONArray headerValues,
      JSONObject rowObject)
  {
    if (startColumn <= cell.getColumnIndex() && cell.getColumnIndex() <= endColumn)
    {
      JSONObject header = headerValues.getJSONObject(index);

      switch (formulaEvaluator.evaluateInCell(cell).getCellTypeEnum())
      {
      case NUMERIC:
        if (headerValues.length() != 60)
        {
          double numericCellValue = cell.getNumericCellValue();
          rowObject.put(header.getString(KEY), numericCellValue);
        }
        else
        {
          String numericStringValue = NumberToTextConverter.toText(cell.getNumericCellValue());
          rowObject.put(header.getString(KEY), numericStringValue);
        }
        break;
      case STRING:
        String stringCellValue = cell.getStringCellValue();
        rowObject.put(header.getString(KEY), stringCellValue);
        break;
      case BLANK:
        rowObject.put(header.getString(KEY), "");
        break;
      default:
        break;
      }
    }
  }
}
