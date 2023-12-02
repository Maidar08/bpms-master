package consumption.service_task.direct_online_salary;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.repository.directOnline.DefaultParameterRepository;
import mn.erin.domain.bpm.usecase.calculations.GetAverageSalary;
import mn.erin.domain.bpm.usecase.calculations.GetAverageSalaryInput;
import mn.erin.domain.bpm.usecase.calculations.GetAverageSalaryOutput;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParameters;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParametersInput;

import static consumption.constant.CamundaVariableConstants.HAS_MORTGAGE;
import static consumption.constant.CamundaVariableConstants.SALARY;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.FAMILY_INCOME;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_SALARY_PROCESS_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.TRACK_NUMBER;

public class SalaryCalculationTask implements JavaDelegate
{
  private final DefaultParameterRepository defaultParameterRepository;
  private final AuthorizationService authorizationService;
  private final AuthenticationService authenticationService;
  private final ProcessRepository processRepository;
  private static final Logger LOGGER = LoggerFactory.getLogger(SalaryCalculationTask.class);

  public SalaryCalculationTask(DefaultParameterRepository defaultParameterRepository, AuthorizationService authorizationService,
      AuthenticationService authenticationService, ProcessRepository processRepository)
  {
    this.defaultParameterRepository = defaultParameterRepository;
    this.authorizationService = authorizationService;
    this.authenticationService = authenticationService;
    this.processRepository = processRepository;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    Map<String, BigDecimal> salary = (Map<String, BigDecimal>) execution.getVariable(SALARY);
    Map<Date, BigDecimal> dateAndSalariesMap = new HashMap<>();
    for (Map.Entry<String, BigDecimal> entry : salary.entrySet())
    {
      SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy");
      dateAndSalariesMap.put((formatter.parse(entry.getKey())), entry.getValue());
    }

    boolean isExcludedNiigmiinDaatgal = (boolean) execution.getVariable("isExcludedNiigmiinDaatgal");
    boolean isExcludedHealthInsurance = (boolean) execution.getVariable("isExcludedHealthInsurance");
    execution.setVariable("salaryInfo", dateAndSalariesMap);

    GetAverageSalary getAverageSalary = new GetAverageSalary(defaultParameterRepository);

    GetAverageSalaryOutput result = getAverageSalary.execute(
        new GetAverageSalaryInput(dateAndSalariesMap.size() > 12 ? getSortedSalaryMap(dateAndSalariesMap, execution) : dateAndSalariesMap, isExcludedNiigmiinDaatgal,
            isExcludedHealthInsurance));
    execution.setVariable("averageSalaryBeforeTax", result.getAverageSalaryBeforeTax());
    execution.setVariable("averageSalaryAfterTax", result.getAverageSalaryAfterTax());
    execution.setVariable("afterTaxSalaries", result.getAfterTaxSalaries());

    String processTypeId = String.valueOf(execution.getVariable(PROCESS_TYPE_ID));
    String trackNumber = String.valueOf(execution.getVariable(TRACK_NUMBER));
    
    if (!execution.hasVariable(HAS_MORTGAGE))
    {
      execution.setVariable(FAMILY_INCOME, 0);
      if (processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
      {
        LOGGER.info("####### FAMILY INCOME =  [{}], WITH TRACKNUMBER = [{}]",0, trackNumber);
      }
      else
      {
        LOGGER.info("####### FAMILY INCOME =  [{}]",0);
      }
    }
    else
    {
      boolean hasMortgage = (boolean) execution.getVariable(HAS_MORTGAGE);
      BigDecimal familyIncome = hasMortgage ? result.getAverageSalaryBeforeTax() : result.getAverageSalaryAfterTax();
      execution.setVariable(FAMILY_INCOME, familyIncome.longValue());
      if (processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
      {
        LOGGER.info("####### FAMILY INCOME =  [{}], WITH TRACKNUMBER = [{}]", familyIncome, trackNumber);
      }
      else
      {
        LOGGER.info("####### FAMILY INCOME =  [{}]", familyIncome);
      }
    }

    String caseInstanceId =String.valueOf(execution.getVariable(CASE_INSTANCE_ID));
    Map<String, Serializable> parameters = new HashMap<>();
    parameters.put("averageSalaryBeforeTax", result.getAverageSalaryAfterTax());

    Map<ParameterEntityType, Map<String, Serializable>> processParams = new HashMap<>();
    String processType = String.valueOf(execution.getVariable(PROCESS_TYPE_ID));

    ParameterEntityType parameterEntityType = processType.equals(ONLINE_SALARY_PROCESS_TYPE) ? ParameterEntityType.ONLINE_SALARY : ParameterEntityType.BNPL;
    processParams.put(parameterEntityType, parameters);

    UpdateProcessParametersInput input = new UpdateProcessParametersInput(caseInstanceId, processParams);
    UpdateProcessParameters updateProcessParameters = new UpdateProcessParameters(authenticationService, authorizationService, processRepository);
    updateProcessParameters.execute(input);

    if (processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
    {
      LOGGER.info("####### AVERAGE SALARY CALCULATED WITH TRACKNUMBER = [{}] .... After Tax = [{}], Before tax = [{}]", trackNumber, result.getAverageSalaryAfterTax(),
          result.getAverageSalaryBeforeTax());
    }
    else
    {
      LOGGER.info("####### AVERAGE SALARY CALCULATED.... After Tax = [{}], Before tax = [{}]", result.getAverageSalaryAfterTax(),
          result.getAverageSalaryBeforeTax());
    }

  }

  private Map<Date, BigDecimal> getSortedSalaryMap(Map<Date, BigDecimal> dateAndSalariesMap, DelegateExecution execution)
  {
    Map<Date, BigDecimal> sortedMap = new HashMap<>();
    final TreeSet<Date> dates = new TreeSet<>(dateAndSalariesMap.keySet());
    final int size = dates.size();
    int index = 1;
    for (Date date : dates)
    {
      if (size - 12 < index)
      {
        sortedMap.put(date, dateAndSalariesMap.get(date));
      }
      else
      {
        dateAndSalariesMap.remove(date);
      }
      index++;
    }
    execution.setVariable("salaryInfo", dateAndSalariesMap);
    return sortedMap;
  }
}
