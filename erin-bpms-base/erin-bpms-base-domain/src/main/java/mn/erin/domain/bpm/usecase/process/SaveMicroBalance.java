package mn.erin.domain.bpm.usecase.process;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmMessagesConstants;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.usecase.calculations.CalculateMicroBalanceInput;

import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.ASSET;
import static mn.erin.domain.bpm.BpmModuleConstants.BALANCE_TOTAL_INCOME_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.BALANCE_TOTAL_INCOME_PERCENT;
import static mn.erin.domain.bpm.BpmModuleConstants.DEBT;
import static mn.erin.domain.bpm.BpmModuleConstants.OPERATION;
import static mn.erin.domain.bpm.BpmModuleConstants.REPORT_PERIOD;
import static mn.erin.domain.bpm.BpmModuleConstants.SALE;
import static mn.erin.domain.bpm.util.process.BpmUtils.convertMapToJsonString;

/**
 * @author Lkhagvadorj.A
 **/

public class SaveMicroBalance extends AbstractUseCase<CalculateMicroBalanceInput, Boolean>
{
  private final ProcessRepository processRepository;

  public SaveMicroBalance(ProcessRepository processRepository)
  {
    this.processRepository = Objects.requireNonNull(processRepository, "ProcessRepository is required!");
  }

  @Override
  public Boolean execute(CalculateMicroBalanceInput input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException(BpmMessagesConstants.INPUT_NULL_CODE, BpmMessagesConstants.INPUT_NULL_MESSAGE);
    }

    if (StringUtils.isBlank(input.getInstanceId()))
    {
      throw new UseCaseException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
    }

    Map<String, Serializable> values = new HashMap<>();
    try
    {
      setParameter(values, input.getSale(), SALE);
      setParameter(values, input.getOperation(), OPERATION);
      setParameter(values, input.getAsset(), ASSET);
      setParameter(values, input.getDebt(), DEBT);
      if (null != input.getColumnHeader() && !input.getColumnHeader().isEmpty())
      {
        setHeaderParameter(values, input.getColumnHeader().get(SALE), "saleHeader");
        setHeaderParameter(values, input.getColumnHeader().get(OPERATION), "operationHeader");
        setHeaderParameter(values, input.getColumnHeader().get(ASSET), "assetHeader");
        setHeaderParameter(values, input.getColumnHeader().get(DEBT), "debtHeader");
      }
    }
    catch (JsonProcessingException exception)
    {
      throw new UseCaseException(exception.getMessage());
    }

    values.put(BALANCE_TOTAL_INCOME_AMOUNT, input.getBalanceTotalIncomeAmount());
    values.put(BALANCE_TOTAL_INCOME_PERCENT, input.getBalanceTotalIncomePercent());
    values.put(REPORT_PERIOD, input.getReportPeriod());

    EnumMap<ParameterEntityType, Map<String, Serializable>> parameters = new EnumMap<>(ParameterEntityType.class);
    parameters.put(ParameterEntityType.BALANCE, values);
    try
    {
      deleteParameters(input.getInstanceId());
      updateParameters(input.getInstanceId(), parameters);
      return true;
    }
    catch (BpmRepositoryException e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }

  private void deleteParameters(String instanceId) throws UseCaseException
  {
        DeleteProcessParameterByInstanceIdAndEntityInput deleteParameterInput = new DeleteProcessParameterByInstanceIdAndEntityInput(instanceId, ParameterEntityType.BALANCE);
        DeleteProcessParameterByInstanceIdAndEntity deleteParameter = new DeleteProcessParameterByInstanceIdAndEntity(processRepository);
        deleteParameter.execute(deleteParameterInput);
  }

  private void updateParameters(String instanceId, Map<ParameterEntityType, Map<String, Serializable>> parameters) throws BpmRepositoryException
  {
    processRepository.updateParameters(instanceId, parameters);
  }

  private void setParameter(Map<String, Serializable> values, Map<Integer, Map<String, Object>> inputMap, String key) throws JsonProcessingException
  {
    if (null != inputMap)
    {
      for (Map.Entry<Integer, Map<String, Object>> row : inputMap.entrySet())
      {
        Map<String, Object> rowMap = row.getValue();
        values.put(key + row.getKey(), convertMapToJsonString(rowMap));
      }
    }
  }

  private void setHeaderParameter(Map<String, Serializable> values, Map<String, Object> inputMap, String key) throws JsonProcessingException
  {
    values.put(key, convertMapToJsonString(inputMap));
  }
}
