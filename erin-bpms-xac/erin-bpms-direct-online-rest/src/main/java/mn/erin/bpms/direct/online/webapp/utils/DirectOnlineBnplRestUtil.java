package mn.erin.bpms.direct.online.webapp.utils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import mn.erin.bpms.direct.online.webapp.model.RestDanEntity;
import mn.erin.domain.aim.service.AimServiceRegistry;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.directOnline.DanInfo;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.model.salary.CalculatedSalaryInfo;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;
import mn.erin.domain.bpm.service.RuntimeService;
import mn.erin.domain.bpm.usecase.process.DeleteProcess;
import mn.erin.domain.bpm.usecase.process.DeleteProcessInput;
import mn.erin.domain.bpm.usecase.process.SaveSalaries;
import mn.erin.domain.bpm.usecase.process.SaveSalariesInput;

import static mn.erin.bpms.direct.online.webapp.DirectOnlineLoanConstants.CIF_NUMBER;
import static mn.erin.bpms.direct.online.webapp.DirectOnlineLoanConstants.RESPONSE;
import static mn.erin.domain.bpm.BpmModuleConstants.BRANCH_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.SALARY_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.STATE;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.PROCESSING;

public class DirectOnlineBnplRestUtil
{
  private static AimServiceRegistry aimServiceRegistry;
  private static BpmsServiceRegistry bpmsServiceRegistry;
  private static BpmsRepositoryRegistry bpmsRepositoryRegistry;

  private static final Logger LOGGER = LoggerFactory.getLogger(DirectOnlineBnplRestUtil.class);

  public DirectOnlineBnplRestUtil(AimServiceRegistry aimServiceRegistry, BpmsServiceRegistry bpmsServiceRegistry,
      BpmsRepositoryRegistry bpmsRepositoryRegistry)
  {
    /* Private constructor */
    this.aimServiceRegistry = aimServiceRegistry;
    this.bpmsServiceRegistry = bpmsServiceRegistry;
    this.bpmsRepositoryRegistry = bpmsRepositoryRegistry;
  }

  public static ResponseEntity returnError(String errorCode, String errorMessage)
  {
    Map<String, Object> responseMap = new HashMap<>();
    responseMap.put(STATE, "FAILURE");
    Map<String, String> error = new HashMap<>();
    if (null != errorCode && !StringUtils.isBlank(errorCode))
    {
      error.put("errorCode", errorCode);
    }
    error.put("errorMessage", errorMessage);
    responseMap.put(RESPONSE, error);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
  }

  private static void saveSalaryToProcessParameter(String processInstanceId, Map<Date, CalculatedSalaryInfo> salariesInfo, BigDecimal averageBeforeTax,
      BigDecimal averageAfterTax, String hasMortgage, String ndsh, String emd)
      throws UseCaseException
  {
    SaveSalaries saveSalaries = new SaveSalaries(aimServiceRegistry.getAuthenticationService(), aimServiceRegistry.getAuthorizationService(),
        bpmsRepositoryRegistry.getProcessRepository());
    SaveSalariesInput input = new SaveSalariesInput(processInstanceId, salariesInfo, averageBeforeTax,
        averageAfterTax, hasMortgage, ndsh, emd);
    saveSalaries.execute(input);
  }

  public static Map<Date, CalculatedSalaryInfo> mapToSalaryInfo(Map<Date, Map<String, BigDecimal>> salaryInfo,
      Map<Date, BigDecimal> salInfo)
  {
    Map<Date, CalculatedSalaryInfo> salariesInfo = new HashMap<>();
    for (Map.Entry<Date, Map<String, BigDecimal>> entry : salaryInfo.entrySet())
    {
      BigDecimal salaryBeforeTax = salInfo.get(entry.getKey());
      CalculatedSalaryInfo calculatedSalaryInfo = new CalculatedSalaryInfo();
      calculatedSalaryInfo.setChecked(true);
      Map<String, BigDecimal> mapValue = entry.getValue();
      calculatedSalaryInfo.setSalaryBeforeTax(salaryBeforeTax);
      calculatedSalaryInfo.setNdsh(mapValue.get("Ndsh"));
      calculatedSalaryInfo.setHhoat(mapValue.get("Hhoat"));
      calculatedSalaryInfo.setSalaryAfterTax(mapValue.get("MonthSalaryAfterTax"));
      salariesInfo.put(entry.getKey(), calculatedSalaryInfo);
    }

    return salariesInfo;
  }

  public static RestDanEntity extractDanInfo(List<DanInfo> danList)
  {
    RestDanEntity restDanEntity = new RestDanEntity();
    Map<String, BigDecimal> salary = new HashMap<>();
    int index = 0;
    int year = 0;
    for (DanInfo danInfo : danList)
    {
      setDanSalaryInfo(danInfo, salary);
      int month = danInfo.getMonth();
      int danInfoYear = danInfo.getYear();
      if (danInfoYear > year)
      {
        year = danInfoYear;
        setDanInfoByMonth(danInfo, restDanEntity);
        continue;
      }
      if (month >= index)
      {
        setDanInfoByMonth(danInfo, restDanEntity);
      }
    }
    restDanEntity.setSalary(salary);
    return restDanEntity;
  }

  public static void setDanInfoByMonth(DanInfo danInfo, RestDanEntity restDanEntity)
  {
    restDanEntity.setDanRegister(danInfo.getOrgSiID());
    restDanEntity.setDistrict(danInfo.getDomName());
  }

  public static void setDanSalaryInfo(DanInfo danInfo, Map<String, BigDecimal> salary)
  {
    final String dateString = getDanDateString(danInfo);
    final BigDecimal salaryAmount = danInfo.getSalaryAmount();
    if (salary.containsKey(dateString))
    {
      final BigDecimal amount1 = salary.get(dateString);
      final BigDecimal amount2 = amount1.add(salaryAmount);
      salary.put(dateString, amount2);
    }
    else
    {
      salary.put(dateString, salaryAmount);
    }
  }

  public static String getDanDateString(DanInfo danInfo)
  {
    int year = danInfo.getYear();
    int month = danInfo.getMonth();
    final LocalDate localDate = LocalDate.of(year, month, 1);
    Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    return date.toString();
  }

  public static BigDecimal getDanSalaryAmount(Map<String, Object> danMap)
  {
    double salaryDouble = Double.parseDouble(String.valueOf(danMap.get(SALARY_AMOUNT)));
    return BigDecimal.valueOf(salaryDouble);
  }

  public static Map<String, Object> getProcessingStateResponse(String instanceId, RuntimeService runtimeService) throws BpmServiceException
  {
    Map<String, Object> responseMap = new HashMap<>();
    Map<String, Object> result = new HashMap<>();

    String requestId = String.valueOf(runtimeService.getVariableById(instanceId, PROCESS_REQUEST_ID));
    String cifNumber = String.valueOf(runtimeService.getVariableById(instanceId, CIF_NUMBER));
    String branchNumber = String.valueOf(runtimeService.getVariableById(instanceId, BRANCH_NUMBER));

    result.put(CIF_NUMBER, cifNumber);
    result.put(PROCESS_REQUEST_ID, requestId);
    result.put(BRANCH_NUMBER, branchNumber);
    result.put(STATE, ProcessRequestState.fromEnumToString(PROCESSING));
    responseMap.put(STATE, "SUCCESS");
    responseMap.put(RESPONSE, result);

    return responseMap;
  }

  public static ResponseEntity handleGeneralException(Exception e)
  {
    LOGGER.error(e.getMessage(), e);
    return returnError(null, e.getMessage());
  }

  public static void deletePreviousProcess(String instanceId, String previousRequestId) throws UseCaseException
  {
    DeleteProcess deleteProcess = new DeleteProcess(aimServiceRegistry.getAuthenticationService(),
        aimServiceRegistry.getAuthorizationService(), bpmsRepositoryRegistry.getProcessRepository());
    deleteProcess.execute(new DeleteProcessInput(instanceId));
  }
}
