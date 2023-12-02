package mn.erin.domain.bpm.usecase.process;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.json.JSONObject;

import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmMessagesConstants;
import mn.erin.domain.bpm.model.BpmModulePermission;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.model.salary.CalculatedSalaryInfo;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProcessRepository;

/**
 * @author Zorig
 */
public class SaveSalaries extends AuthorizedUseCase<SaveSalariesInput, SaveSalariesOutput>
{
  private static final Permission permission = new BpmModulePermission("SaveSalaries");
  private final ProcessRepository processRepository;

  public SaveSalaries(AuthenticationService authenticationService,
      AuthorizationService authorizationService, ProcessRepository processRepository)
  {
    super(authenticationService, authorizationService);
    this.processRepository = Objects.requireNonNull(processRepository, "ProcessRepository is required!");
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected SaveSalariesOutput executeImpl(SaveSalariesInput input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException(BpmMessagesConstants.INPUT_NULL_CODE, BpmMessagesConstants.INPUT_NULL_MESSAGE);
    }
    BigDecimal zero = new BigDecimal(0);

    if (input.getAverageSalaryAfterTax() == null || input.getAverageSalaryAfterTax().compareTo(zero) == -1)
    {
      String errorCode = "BPMS031";
      throw new UseCaseException(errorCode, "Average Salary must not be negative or null!");
    }

    if (input.getAverageSalaryBeforeTax() == null || input.getAverageSalaryBeforeTax().compareTo(zero) == -1)
    {
      String errorCode = "BPMS031";
      throw new UseCaseException(errorCode, "Average Salary must not be negative or null!");
    }

    String processInstanceId = input.getProcessInstanceId();
    BigDecimal averageBeforeTax = input.getAverageSalaryBeforeTax();
    BigDecimal averageAfterTax = input.getAverageSalaryAfterTax();
    String hasMortgage = input.getHasMortgage();
    String NDSH = input.getNdsh();
    String EMD = input.getEmd();
    Map<Date, CalculatedSalaryInfo> salariesInfo = input.getSalariesInfo();

    validateSalariesInfo(salariesInfo);

    Map<String, Serializable> updateValuesMap = new HashMap<>();

    try
    {
      processRepository.deleteEntity(processInstanceId, ParameterEntityType.SALARY);

      for (Map.Entry<Date, CalculatedSalaryInfo> dateMapEntry : salariesInfo.entrySet())
      {
        Date currentDateToInsert = dateMapEntry.getKey();
        JSONObject salariesJson = new JSONObject();
        CalculatedSalaryInfo salaryInfo = dateMapEntry.getValue();

        salariesJson.put("checked", salaryInfo.getChecked());
        salariesJson.put("ndsh", salaryInfo.getNdsh());
        salariesJson.put("hhoat", salaryInfo.getHhoat());
        salariesJson.put("monthlySalaryBeforeTax", salaryInfo.getSalaryBeforeTax());
        salariesJson.put("monthlySalaryAfterTax", salaryInfo.getSalaryAfterTax());
        salariesJson.put("salaryFromXyp", salaryInfo.getSalaryFromXyp());

        updateValuesMap.put(currentDateToInsert.toString(), salariesJson.toString());
      }

      updateValuesMap.put("averageBeforeTax", averageBeforeTax);
      updateValuesMap.put("averageAfterTax", averageAfterTax);
      updateValuesMap.put("hasMortgage", hasMortgage);
      updateValuesMap.put("NDSH", NDSH);
      updateValuesMap.put("EMD", EMD);
      int numberUpdated = processRepository.updateParameters(processInstanceId,
          Collections.singletonMap(ParameterEntityType.SALARY, updateValuesMap));

      return new SaveSalariesOutput(numberUpdated > 0);
    }
    catch (BpmRepositoryException e)
    {
      throw new UseCaseException(e.getMessage());
    }

  }

  private void validateSalariesInfo(Map<Date, CalculatedSalaryInfo> salariesInfo) throws UseCaseException
  {
    if (salariesInfo == null)
    {
      String errorCode = "BPMS032";
      throw new UseCaseException(errorCode, "Salaries Info must not be null!");
    }

    if (salariesInfo.isEmpty())
    {
      String errorCode = "BPMS032";
      throw new UseCaseException(errorCode, "Empty salaries info not allowed!");
    }

    BigDecimal zero = new BigDecimal(0);
    for (Map.Entry<Date, CalculatedSalaryInfo> salaryInfoEntry : salariesInfo.entrySet())
    {
      CalculatedSalaryInfo currentCalculatedSalaryInfo = salaryInfoEntry.getValue();
      if (salaryInfoEntry.getKey() == null)
      {
        String errorCode = "BPMS033";
        throw new UseCaseException(errorCode, "Date is null!");
      }
      if (currentCalculatedSalaryInfo == null)
      {
        String errorCode = "BPMS034";
        throw new UseCaseException(errorCode, "Invalid Salary Input!");
      }
      if (currentCalculatedSalaryInfo.getHhoat() == null || currentCalculatedSalaryInfo.getHhoat().compareTo(zero) == -1)
      {
        String errorCode = "BPMS035";
        throw new UseCaseException(errorCode, "Salary field must not be null or negative!");
      }
      if (currentCalculatedSalaryInfo.getNdsh() == null || currentCalculatedSalaryInfo.getNdsh().compareTo(zero) == -1)
      {
        String errorCode = "BPMS035";
        throw new UseCaseException(errorCode, "Salary field must not be null or negative!");
      }
      if (currentCalculatedSalaryInfo.getSalaryBeforeTax() == null || currentCalculatedSalaryInfo.getSalaryBeforeTax().compareTo(zero) == -1)
      {
        String errorCode = "BPMS035";
        throw new UseCaseException(errorCode, "Salary field must not be null or negative!");
      }
      if (currentCalculatedSalaryInfo.getSalaryAfterTax() == null || currentCalculatedSalaryInfo.getSalaryAfterTax().compareTo(zero) == -1)
      {
        String errorCode = "BPMS035";
        throw new UseCaseException(errorCode, "Salary field must not be null or negative!");
      }
    }
  }
}
