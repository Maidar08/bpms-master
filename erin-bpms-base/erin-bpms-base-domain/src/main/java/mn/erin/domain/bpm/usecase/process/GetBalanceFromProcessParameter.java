package mn.erin.domain.bpm.usecase.process;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.usecase.calculations.CalculateMicroBalanceOutput;

import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.ASSET;
import static mn.erin.domain.bpm.BpmModuleConstants.BALANCE_TOTAL_INCOME_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.BALANCE_TOTAL_INCOME_PERCENT;
import static mn.erin.domain.bpm.BpmModuleConstants.DEBT;
import static mn.erin.domain.bpm.BpmModuleConstants.OPERATION;
import static mn.erin.domain.bpm.BpmModuleConstants.REPORT_PERIOD;
import static mn.erin.domain.bpm.BpmModuleConstants.SALE;
import static mn.erin.domain.bpm.util.process.BpmUtils.convertJsonStringToMap;

/**
 * @author Lkhagvadorj.A
 **/

public class GetBalanceFromProcessParameter extends AbstractUseCase<String, CalculateMicroBalanceOutput>
{
  private final ProcessRepository processRepository;

  public GetBalanceFromProcessParameter(ProcessRepository processRepository)
  {
    this.processRepository = Objects.requireNonNull(processRepository, "ProcessRepository is required!");
  }

  @Override
  public CalculateMicroBalanceOutput execute(String input) throws UseCaseException
  {
    if (StringUtils.isBlank(input))
    {
      throw new UseCaseException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
    }

    GetProcessParameters getProcessParameters = new GetProcessParameters(processRepository);
    GetProcessEntityInput getProcessEntityInput = new GetProcessEntityInput(input, ParameterEntityType.BALANCE);
    Map<String, Serializable> parameters = getProcessParameters.execute(getProcessEntityInput);
    return convertParameterToMap(parameters);
  }

  private CalculateMicroBalanceOutput convertParameterToMap(Map<String, Serializable> parameters)
  {
    Map<Integer, Map<String, Object>> sale = new HashMap<>();
    Map<Integer, Map<String, Object>> operation = new HashMap<>();
    Map<Integer, Map<String, Object>> asset = new HashMap<>();
    Map<Integer, Map<String, Object>> debt = new HashMap<>();
    boolean contains = true;
    int index = 0;
    while (contains)
    {
      boolean hasSale = false;
      boolean hasOp = false;
      boolean hasAsset = false;
      boolean hasDebt = false;
      if (parameters.containsKey(SALE + index))
      {
        sale.put(index, convertToMap(parameters, SALE, index));
        hasSale = true;
      }

      if (parameters.containsKey(OPERATION + index))
      {
        operation.put(index, convertToMap(parameters, OPERATION, index));
        hasOp = true;
      }

      if (parameters.containsKey(ASSET + index))
      {
        asset.put(index, convertToMap(parameters, ASSET, index));
        hasAsset = true;
      }

      if (parameters.containsKey(DEBT + index))
      {
        debt.put(index, convertToMap(parameters, DEBT, index));
        hasDebt = true;
      }
      contains = hasSale || hasOp || hasAsset || hasDebt;
      index++;
    }

    Map<String, Map<String, Object>> columnHeader = getColumnHeader(parameters);
    int reportPeriod = 0;
    if (parameters.containsKey(REPORT_PERIOD))
    {
      reportPeriod = Integer.parseInt(String.valueOf(parameters.get(REPORT_PERIOD)));
    }
    double totalIncomeAmount = 0;
    if (parameters.containsKey(BALANCE_TOTAL_INCOME_AMOUNT))
    {
      totalIncomeAmount = new BigDecimal(String.valueOf(parameters.get(BALANCE_TOTAL_INCOME_AMOUNT))).doubleValue();
    }
    double totalIncomePercent = 0;
    if (parameters.containsKey(BALANCE_TOTAL_INCOME_PERCENT))
    {
      totalIncomePercent = new BigDecimal(String.valueOf(parameters.get(BALANCE_TOTAL_INCOME_PERCENT))).doubleValue();
    }
    return new CalculateMicroBalanceOutput(reportPeriod, sale, operation, asset, debt, columnHeader,
        totalIncomeAmount, totalIncomePercent);
  }

  private Map<String, Map<String, Object>> getColumnHeader(Map<String, Serializable> parameters)
  {
    Map<String, Map<String, Object>> columnHeader = new HashMap<>();
    columnHeader.put(SALE, convertToMap(parameters, "saleHeader"));
    columnHeader.put(OPERATION, convertToMap(parameters, "operationHeader"));
    columnHeader.put(ASSET, convertToMap(parameters, "assetHeader"));
    columnHeader.put(DEBT, convertToMap(parameters, "debtHeader"));

    return columnHeader;
  }

  private Map<String, Object> convertToMap(Map<String, Serializable> parameters, String key, int index)
  {
    String value = String.valueOf(parameters.get(key + index));
    return convertJsonStringToMap(value);
  }

  private Map<String, Object> convertToMap(Map<String, Serializable> parameters, String key)
  {
    String value = String.valueOf(parameters.get(key));
    return convertJsonStringToMap(value);
  }
}
