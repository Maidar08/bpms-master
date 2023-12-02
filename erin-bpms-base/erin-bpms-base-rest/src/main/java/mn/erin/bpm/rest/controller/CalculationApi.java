package mn.erin.bpm.rest.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.inject.Inject;

import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.bpm.rest.model.RestMicroBalanceCalculate;
import mn.erin.bpm.rest.model.RestSalariesCalculationInfo;
import mn.erin.bpm.rest.model.RestSaveSalaries;
import mn.erin.bpm.rest.model.RestTaskFormSubmit;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.exception.BpmInvalidArgumentException;
import mn.erin.domain.bpm.model.cases.Execution;
import mn.erin.domain.bpm.model.form.FormFieldValue;
import mn.erin.domain.bpm.model.form.TaskForm;
import mn.erin.domain.bpm.model.form.TaskFormField;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.model.task.Task;
import mn.erin.domain.bpm.model.variable.Variable;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.repository.directOnline.DefaultParameterRepository;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.service.ExecutionService;
import mn.erin.domain.bpm.service.RuntimeService;
import mn.erin.domain.bpm.service.TaskFormService;
import mn.erin.domain.bpm.service.TaskService;
import mn.erin.domain.bpm.usecase.calculations.CalculateMicroBalance;
import mn.erin.domain.bpm.usecase.calculations.CalculateMicroBalanceInput;
import mn.erin.domain.bpm.usecase.calculations.CalculateMicroBalanceOutput;
import mn.erin.domain.bpm.usecase.calculations.GetAverageSalary;
import mn.erin.domain.bpm.usecase.calculations.GetAverageSalaryInput;
import mn.erin.domain.bpm.usecase.calculations.GetAverageSalaryOutput;
import mn.erin.domain.bpm.usecase.execution.GetEnabledExecutions;
import mn.erin.domain.bpm.usecase.execution.GetEnabledExecutionsInput;
import mn.erin.domain.bpm.usecase.execution.GetEnabledExecutionsOutput;
import mn.erin.domain.bpm.usecase.form.case_variable.SetCaseVariables;
import mn.erin.domain.bpm.usecase.form.get_form_by_task_id.GetFormByTaskId;
import mn.erin.domain.bpm.usecase.form.get_form_by_task_id.GetFormByTaskIdInput;
import mn.erin.domain.bpm.usecase.form.get_form_by_task_id.GetFormByTaskIdOutput;
import mn.erin.domain.bpm.usecase.form.submit_form.SubmitForm;
import mn.erin.domain.bpm.usecase.form.submit_form.SubmitFormInput;
import mn.erin.domain.bpm.usecase.process.GetBalanceFromProcessParameter;
import mn.erin.domain.bpm.usecase.process.GetSalaries;
import mn.erin.domain.bpm.usecase.process.GetSalariesOutput;
import mn.erin.domain.bpm.usecase.process.GetXypSalaries;
import mn.erin.domain.bpm.usecase.process.GetXypSalariesOutput;
import mn.erin.domain.bpm.usecase.process.SaveMicroBalance;
import mn.erin.domain.bpm.usecase.process.SaveSalaries;
import mn.erin.domain.bpm.usecase.process.SaveSalariesInput;
import mn.erin.domain.bpm.usecase.process.SaveSalariesOutput;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParameters;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParametersInput;
import mn.erin.domain.bpm.usecase.process.collateral.calculate_collateral.CalculateCollateralAmount;
import mn.erin.domain.bpm.usecase.process.get_variables.GetVariablesById;
import mn.erin.domain.bpm.usecase.process.get_variables.GetVariablesByIdOutput;
import mn.erin.domain.bpm.usecase.process.manual_activate.ManualActivate;
import mn.erin.domain.bpm.usecase.process.manual_activate.ManualActivateInput;
import mn.erin.domain.bpm.usecase.task.GetActiveTaskByInstanceId;
import mn.erin.domain.bpm.usecase.task.GetActiveTaskByInstanceIdOutput;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;

import static mn.erin.bpm.rest.constant.BpmsControllerConstants.INTERNAL_SERVER_ERROR;
import static mn.erin.bpm.rest.util.BpmRestUtils.toRestTaskForm;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INVALID_INPUT_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INVALID_INPUT_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PROCESS_INSTANCE_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PROCESS_INSTANCE_ID_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.REPORT_COVERAGE_PERIOD_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.REPORT_COVERAGE_PERIOD_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.REST_FORM_FIELDS_EMPTY_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.REST_FORM_FIELDS_EMPTY_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.BIG_DECIMAL_FORM_FIELD_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.CALCULATE_INTEREST_RATE_ACTIVITY_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.CALCULATE_LOAN_AMOUNT_ACTIVITY_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.CALCULATE_LOAN_AMOUNT_MICRO_ACTIVITY_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_ACCOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_LIST;
import static mn.erin.domain.bpm.BpmModuleConstants.CREATE_LOAN_ACCOUNT_ACTIVITY_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.CREATE_LOAN_ACCOUNT_ACTIVITY_NAME_PREVIOUS;
import static mn.erin.domain.bpm.BpmModuleConstants.CREATE_LOAN_ACCOUNT_MICRO_WITH_COLLATERAL_ACTIVITY_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.CREATE_LOAN_ACCOUNT_WITH_COLLATERAL_ACTIVITY_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.DOUBLE_FORM_FIELD_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.LONG_FORM_FIELD_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.MONGOL_BANK_ACTIVITY_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.MORTGAGE_SCORING_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.SALARY_CALCULATION_ACTIVITY_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.SCORING_ACTIVITY_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.SCORING_MICRO_ACTIVITY_NAME;
import static mn.erin.domain.bpm.model.process.ParameterEntityType.FORM;

/**
 * @author Zorig
 */
@Api
@RestController
@RequestMapping(value = "bpm", name = "Calculations regarding salary.")
public class CalculationApi extends BaseBpmsRestApi
{
  private static final Logger LOGGER = LoggerFactory.getLogger(CalculationApi.class);

  private final AuthenticationService authenticationService;
  private final AuthorizationService authorizationService;

  private final ProcessRepository processRepository;
  private final ExecutionService executionService;

  private final CaseService caseService;
  private final TaskService taskService;
  private final RuntimeService runtimeService;
  private final TaskFormService taskFormService;

  private final DefaultParameterRepository defaultParameterRepository;

  @Inject
  public CalculationApi(BpmsServiceRegistry bpmsServiceRegistry, BpmsRepositoryRegistry bpmsRepositoryRegistry, AuthenticationService authenticationService,
      AuthorizationService authorizationService, RuntimeService runtimeService,
      DefaultParameterRepository defaultParameterRepository)
  {
    super(bpmsServiceRegistry, bpmsRepositoryRegistry);
    this.caseService = Objects.requireNonNull(bpmsServiceRegistry.getCaseService(), "Case service is required!");
    this.taskFormService = Objects.requireNonNull(bpmsServiceRegistry.getTaskFormService(), "Task form service is required!");

    this.taskService = Objects.requireNonNull(bpmsServiceRegistry.getTaskService(), "Task service is required!");
    this.executionService = Objects.requireNonNull(bpmsServiceRegistry.getExecutionService(), "Execution service is required!");

    this.processRepository = Objects.requireNonNull(bpmsRepositoryRegistry.getProcessRepository(), "Process repository is required!");

    this.authenticationService = Objects.requireNonNull(authenticationService, "Authentication service is required!");
    this.authorizationService = Objects.requireNonNull(authorizationService, "Authorization service is required!");
    this.runtimeService = runtimeService;
    this.defaultParameterRepository = defaultParameterRepository;
  }

  @ApiOperation("Calculates monthly salaries after tax and average salary after tax")
  @PostMapping("/salary/calculate-tax-average")
  public ResponseEntity<RestResult> calculateSalaries(@RequestBody RestSalariesCalculationInfo restSalariesCalculationInfo) throws UseCaseException
  {
    GetAverageSalary getAverageSalary = new GetAverageSalary(defaultParameterRepository);

    try
    {
      GetAverageSalaryInput input = new GetAverageSalaryInput(restSalariesCalculationInfo.getDateAndSalaries(),
          restSalariesCalculationInfo.isNiigmiinDaatgalExcluded(), restSalariesCalculationInfo.isHealthInsuranceExcluded());
      GetAverageSalaryOutput output = getAverageSalary.execute(input);

      return RestResponse.success(output);
    }
    catch (IllegalArgumentException | NullPointerException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(INTERNAL_SERVER_ERROR + e.getMessage());
    }
  }

  @ApiOperation("Saves salaries")
  @PostMapping("/salary/save/{processInstanceId}")
  public ResponseEntity<RestResult> saveSalaries(@PathVariable String processInstanceId, @RequestBody RestSaveSalaries restSaveSalaries)
      throws UseCaseException, BpmInvalidArgumentException
  {
    if (StringUtils.isBlank(processInstanceId))
    {
      throw new BpmInvalidArgumentException(PROCESS_INSTANCE_ID_NULL_CODE, PROCESS_INSTANCE_ID_NULL_MESSAGE);
    }

    SaveSalaries saveSalaries = new SaveSalaries(authenticationService, authorizationService, processRepository);
    SaveSalariesInput input = new SaveSalariesInput(processInstanceId, restSaveSalaries.getSalariesInfo(), restSaveSalaries.getAverageBeforeTax(),
        restSaveSalaries.getAverageAfterTax(), restSaveSalaries.getHasMortgage(), restSaveSalaries.getNdsh(), restSaveSalaries.getEmd());
    SaveSalariesOutput output = saveSalaries.execute(input);

    return RestResponse.success(output);
  }

  @ApiOperation("Get salaries")
  @GetMapping("/salary/{processInstanceId}")
  public ResponseEntity<RestResult> getSalaries(@PathVariable String processInstanceId) throws UseCaseException, BpmInvalidArgumentException
  {

    if (StringUtils.isBlank(processInstanceId))
    {
      String errorCode = "BPMS030";
      throw new BpmInvalidArgumentException(errorCode, "Process Instance Id is invalid");
    }

    GetSalaries getSalaries = new GetSalaries(authenticationService, authorizationService, processRepository);
    GetSalariesOutput output = getSalaries.execute(processInstanceId);

    return RestResponse.success(output);
  }

  @ApiOperation("Get salaries")
  @GetMapping("/xyp-salary/{processInstanceId}")
  public ResponseEntity<RestResult> getXypSalaries(@PathVariable String processInstanceId) throws UseCaseException, BpmInvalidArgumentException
  {

    if (StringUtils.isBlank(processInstanceId))
    {
      String errorCode = "BPMS030";
      throw new BpmInvalidArgumentException(errorCode, "Process Instance Id is invalid");
    }

    GetXypSalaries getXypSalaries = new GetXypSalaries(authenticationService, authorizationService, processRepository);
    GetXypSalariesOutput getXypSalariesOutput = getXypSalaries.execute(processInstanceId);

    return RestResponse.success(getXypSalariesOutput.getXypSalary());
  }

  @ApiOperation("Calculate collateral amount from collateral list")
  @PostMapping("/collateral/calculate-collateral-amount")
  public ResponseEntity<RestResult> calculateCollateralAmount(@RequestBody List<Map<String, Object>> restCollaterals) throws UseCaseException
  {
    if (restCollaterals.isEmpty())
    {
      RestResponse.internalError(INTERNAL_SERVER_ERROR + " collateral list is null!");
    }

    CalculateCollateralAmount calculateCollateralAmount = new CalculateCollateralAmount();
    calculateCollateralAmount.execute(restCollaterals);
    return RestResponse.success(restCollaterals);
  }


  @ApiOperation("Download info from mongol bank")
  @PostMapping("/mongol-bank/{caseInstanceId}")
  public ResponseEntity<RestResult> downloadMongolBank(@PathVariable String caseInstanceId, @RequestBody RestTaskFormSubmit restTaskFormSubmit)
      throws UseCaseException, BpmInvalidArgumentException
  {
    List<String> activityNames = new ArrayList<>();

    activityNames.add(MONGOL_BANK_ACTIVITY_NAME);

    return calculateRepeatableTask(caseInstanceId, restTaskFormSubmit, activityNames, activityNames);
  }

  @ApiOperation("Calculate Scoring")
  @PostMapping("/scoring/{caseInstanceId}")
  public ResponseEntity<RestResult> calculateScoring(@PathVariable String caseInstanceId, @RequestBody RestTaskFormSubmit restTaskFormSubmit)
      throws UseCaseException, BpmInvalidArgumentException
  {
    List<String> scoringActivityNames = new ArrayList<>();

    scoringActivityNames.add(SCORING_ACTIVITY_NAME);
    scoringActivityNames.add(MORTGAGE_SCORING_NAME);

    return calculateRepeatableTask(caseInstanceId, restTaskFormSubmit, scoringActivityNames, scoringActivityNames);
  }

  @ApiOperation("Calculate Scoring")
  @PostMapping("/scoring-micro/{caseInstanceId}")
  public ResponseEntity calculateScoringMicro(@PathVariable String caseInstanceId, @RequestBody RestTaskFormSubmit restTaskFormSubmit)
      throws UseCaseException, BpmInvalidArgumentException
  {
    List<String> scoringActivityNames = new ArrayList<>();

    scoringActivityNames.add(SCORING_MICRO_ACTIVITY_NAME);
    scoringActivityNames.add(MORTGAGE_SCORING_NAME);

    return calculateRepeatableTask(caseInstanceId, restTaskFormSubmit, scoringActivityNames, scoringActivityNames);
  }

  @ApiOperation("Calculate Interest Rate")
  @PostMapping("/interest-rate/{caseInstanceId}")
  public ResponseEntity<RestResult> calculateInterestRate(@PathVariable String caseInstanceId, @RequestBody RestTaskFormSubmit restTaskFormSubmit)
      throws UseCaseException, BpmInvalidArgumentException
  {
    return calculateRepeatableTask(caseInstanceId, restTaskFormSubmit, Collections.singletonList(CALCULATE_INTEREST_RATE_ACTIVITY_NAME),
        Collections.singletonList(CALCULATE_INTEREST_RATE_ACTIVITY_NAME));
  }

  @ApiOperation("Calculate loan amount")
  @PostMapping("/loan-amount/calculate/{caseInstanceId}")
  public ResponseEntity<RestResult> calculateLoanAmount(@PathVariable String caseInstanceId, @RequestBody RestTaskFormSubmit restTaskFormSubmit)
      throws UseCaseException, BpmInvalidArgumentException
  {
    return calculateRepeatableTask(caseInstanceId, restTaskFormSubmit, Collections.singletonList(CALCULATE_LOAN_AMOUNT_ACTIVITY_NAME),
        Collections.singletonList(CALCULATE_LOAN_AMOUNT_ACTIVITY_NAME));
  }

  @ApiOperation("Calculate loan amount")
  @PostMapping("/micro-loan-amount/calculate/{caseInstanceId}")
  public ResponseEntity<RestResult> calculateLoanAmountMicro(@PathVariable String caseInstanceId, @RequestBody RestTaskFormSubmit restTaskFormSubmit)
      throws UseCaseException, BpmInvalidArgumentException
  {
    return calculateRepeatableTask(caseInstanceId, restTaskFormSubmit, Collections.singletonList(CALCULATE_LOAN_AMOUNT_MICRO_ACTIVITY_NAME),
        Collections.singletonList(CALCULATE_LOAN_AMOUNT_MICRO_ACTIVITY_NAME));
  }

  @ApiOperation("Calculate loan amount")
  @PostMapping("/mortgage-loan-amount/calculate/{caseInstanceId}")
  public ResponseEntity<RestResult> calculateLoanAmountMortgage(@PathVariable String caseInstanceId, @RequestBody RestTaskFormSubmit restTaskFormSubmit)
      throws UseCaseException, BpmInvalidArgumentException
  {
    return calculateRepeatableTask(caseInstanceId, restTaskFormSubmit, Collections.singletonList(CALCULATE_LOAN_AMOUNT_MICRO_ACTIVITY_NAME),
        Collections.singletonList(CALCULATE_LOAN_AMOUNT_MICRO_ACTIVITY_NAME));
  }

  @ApiOperation("Calculate collateral attachment percentage")
  @PostMapping("loan-account/calculate/collateral/{caseInstanceId}")
  public ResponseEntity<RestResult> calculateCollateralPercent(@PathVariable String caseInstanceId, @RequestBody RestTaskFormSubmit restTaskFormSubmit)
      throws BpmInvalidArgumentException
  {
    if (null == restTaskFormSubmit)
    {
      throw new BpmInvalidArgumentException(REST_FORM_FIELDS_EMPTY_CODE, REST_FORM_FIELDS_EMPTY_MESSAGE);
    }
    return null;
  }

  @ApiOperation("Create loan account")
  @PostMapping("/loan-account/create/{caseInstanceId}")
  public ResponseEntity<RestResult> createLoanAccount(@PathVariable String caseInstanceId, @RequestBody RestTaskFormSubmit restTaskFormSubmit)
      throws UseCaseException, BpmInvalidArgumentException
  {

    List<String> accountActivityNames = new ArrayList<>();

    accountActivityNames.add(CREATE_LOAN_ACCOUNT_ACTIVITY_NAME);
    accountActivityNames.add(CREATE_LOAN_ACCOUNT_ACTIVITY_NAME_PREVIOUS);
    accountActivityNames.add(CREATE_LOAN_ACCOUNT_WITH_COLLATERAL_ACTIVITY_NAME);
    accountActivityNames.add(CREATE_LOAN_ACCOUNT_MICRO_WITH_COLLATERAL_ACTIVITY_NAME);

    // (Update/Save) collateral variables
    if (null != restTaskFormSubmit.getProperties().get(COLLATERAL_LIST))
    {
      String param = new Gson().toJson(restTaskFormSubmit.getProperties().get(COLLATERAL_LIST));
      Map<ParameterEntityType, Map<String, Serializable>> parameters = new HashMap<>();
      parameters.put(FORM, new HashMap<>());
      parameters.get(FORM).put(COLLATERAL_ACCOUNT, param);
      UpdateProcessParametersInput input = new UpdateProcessParametersInput(caseInstanceId, parameters);
      UpdateProcessParameters updateProcessParameters = new UpdateProcessParameters(authenticationService, authorizationService, processRepository);
      updateProcessParameters.execute(input);
    }

    return calculateRepeatableTask(caseInstanceId, restTaskFormSubmit, accountActivityNames, accountActivityNames);
  }

  // Micro
  @ApiOperation("Calculate micro simple calculation")
  @PostMapping("micro-calculation/simple-calculation/{caseInstanceId}")
  public ResponseEntity<RestResult> calculateMicroCalculation(@PathVariable String caseInstanceId, @RequestBody RestTaskFormSubmit restTaskFormSubmit)
      throws UseCaseException, BpmInvalidArgumentException
  {
    return calculateRepeatableTask(caseInstanceId, restTaskFormSubmit, Collections.singletonList(SALARY_CALCULATION_ACTIVITY_NAME),
        Collections.singletonList(SALARY_CALCULATION_ACTIVITY_NAME));
  }

  @ApiOperation("Get saved micro balance")
  @GetMapping("balance/{caseInstanceId}")
  public ResponseEntity<RestResult> getBalanceByInstanceId(@PathVariable String caseInstanceId) throws UseCaseException
  {
    if (StringUtils.isBlank(caseInstanceId))
    {
      throw new UseCaseException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
    }

    GetBalanceFromProcessParameter getBalanceFromProcessParameter = new GetBalanceFromProcessParameter(processRepository);
    CalculateMicroBalanceOutput output = getBalanceFromProcessParameter.execute(caseInstanceId);

    return RestResponse.success(output);
  }

  @ApiOperation("Calculate micro simple calculation")
  @PostMapping("micro-calculation/balance-calculation/{caseInstanceId}")
  public ResponseEntity<RestResult> calculateBalanceCalculation(@PathVariable String caseInstanceId,
      @RequestBody RestMicroBalanceCalculate restMicroBalanceCalculate)
      throws UseCaseException
  {
    if (StringUtils.isBlank(caseInstanceId))
    {
      throw new UseCaseException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
    }

    if (null == restMicroBalanceCalculate)
    {
      throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
    }

    if (restMicroBalanceCalculate.getReportPeriod() == 0)
    {
      throw new UseCaseException(REPORT_COVERAGE_PERIOD_NULL_CODE, REPORT_COVERAGE_PERIOD_NULL_MESSAGE);
    }

    CalculateMicroBalanceInput input = new CalculateMicroBalanceInput(caseInstanceId, restMicroBalanceCalculate.getReportPeriod(),
        restMicroBalanceCalculate.getSale(),
        restMicroBalanceCalculate.getOperation(), restMicroBalanceCalculate.getAsset(), restMicroBalanceCalculate.getDebt());
    CalculateMicroBalance calculateMicroBalance = new CalculateMicroBalance(caseService, runtimeService, taskService);
    CalculateMicroBalanceOutput microBalanceOutput = calculateMicroBalance.execute(input);
    return RestResponse.success(microBalanceOutput);
  }

  @ApiOperation("Save Micro Balance")
  @PostMapping("save/balance/{caseInstanceId}")
  public ResponseEntity<RestResult> saveBalance(@PathVariable String caseInstanceId,
      @RequestBody RestMicroBalanceCalculate restMicroBalanceCalculate) throws UseCaseException
  {
    if (StringUtils.isBlank(caseInstanceId))
    {
      throw new UseCaseException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
    }

    if (null == restMicroBalanceCalculate)
    {
      throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
    }

    SaveMicroBalance saveMicroBalance = new SaveMicroBalance(processRepository);
    CalculateMicroBalanceInput input = new CalculateMicroBalanceInput(caseInstanceId, restMicroBalanceCalculate.getReportPeriod(),
        restMicroBalanceCalculate.getSale(), restMicroBalanceCalculate.getOperation(), restMicroBalanceCalculate.getAsset(),
        restMicroBalanceCalculate.getDebt(), restMicroBalanceCalculate.getColumnHeader(), restMicroBalanceCalculate.getTotalIncomeAmount(),
        restMicroBalanceCalculate.getTotalIncomePercent());
    return RestResponse.success(saveMicroBalance.execute(input));
  }

  // Mortgage
    @ApiOperation("Calculate micro simple calculation")
    @PostMapping("mortgage-calculation/business-calculation/{caseInstanceId}")
    public ResponseEntity<RestResult> calculateMortgageBusiness(@PathVariable String caseInstanceId, @RequestBody RestTaskFormSubmit restTaskFormSubmit)
        throws UseCaseException, BpmInvalidArgumentException
    {
      return calculateRepeatableTask(caseInstanceId, restTaskFormSubmit, Collections.singletonList(SALARY_CALCULATION_ACTIVITY_NAME),
          Collections.singletonList(SALARY_CALCULATION_ACTIVITY_NAME));
    }

  private ResponseEntity<RestResult> calculateRepeatableTask(String caseInstanceId, RestTaskFormSubmit restTaskFormSubmit, List<String> activityNames,
      List<String> taskNames)
      throws UseCaseException, BpmInvalidArgumentException
  {

    if (StringUtils.isBlank(caseInstanceId) || StringUtils.isBlank(restTaskFormSubmit.getTaskId()) || restTaskFormSubmit.getProperties().isEmpty())
    {
      throw new BpmInvalidArgumentException(INVALID_INPUT_CODE, INTERNAL_SERVER_ERROR + " " + INVALID_INPUT_MESSAGE);
    }

    String taskId = restTaskFormSubmit.getTaskId();
    Map<String, Object> properties = restTaskFormSubmit.getProperties();

    submitForm(taskId, caseInstanceId, properties);

    List<Execution> enabledExecutions = getEnabledExecutions(caseInstanceId);
    String executionId = getExecutionIdByActivityName(enabledExecutions, activityNames);

    List<Variable> allVariable = getVariablesByCaseInstanceId(caseInstanceId);

    // Execution id is null, It means current task still active.
    if (null == executionId)
    {
      Map<String, Object> variableMap = convertVariableMap(allVariable);
      setCaseVariables(taskId, caseInstanceId, variableMap);

      List<Task> activeTasks = getActiveTasks(caseInstanceId);
      TaskForm taskForm = getTaskFormByTaskName(caseInstanceId, activeTasks, taskNames);

      if (null == taskForm)
      {
        return RestResponse.success();
      }

      return RestResponse.success(toRestTaskForm(taskForm));
    }

    // 1 step : manual activate
    Boolean isActivated = manualActivate(executionId, allVariable);

    // 2 step : get task form data by taskId.
    if (null != isActivated && isActivated)
    {
      List<Task> activeTasks = getActiveTasks(caseInstanceId);
      TaskForm taskForm = getTaskFormByTaskName(caseInstanceId, activeTasks, taskNames);

      if (null == taskForm)
      {
        // throw exception.
        return RestResponse.success();
      }

      return RestResponse.success(toRestTaskForm(taskForm));
    }

    return RestResponse.success();
  }

  private void submitForm(String taskId, String caseInstanceId, Map<String, Object> properties) throws UseCaseException
  {
    SubmitFormInput submitFormInput = new SubmitFormInput(taskId, caseInstanceId, properties);
    SubmitForm submitForm = new SubmitForm(taskFormService, caseService);

    submitForm.execute(submitFormInput);
  }

  private List<Execution> getEnabledExecutions(String caseInstanceId) throws UseCaseException
  {
    GetEnabledExecutionsInput input = new GetEnabledExecutionsInput(caseInstanceId);
    GetEnabledExecutions getEnabledExecutions = new GetEnabledExecutions(executionService);

    GetEnabledExecutionsOutput output = getEnabledExecutions.execute(input);
    return output.getExecutions();
  }

  private void setCaseVariables(String taskId, String caseInstanceId, Map<String, Object> variableMap) throws UseCaseException
  {
    SubmitFormInput input = new SubmitFormInput(taskId, caseInstanceId, variableMap);
    SetCaseVariables setCaseVariables = new SetCaseVariables(caseService);

    setCaseVariables.execute(input);
  }

  private Boolean manualActivate(String executionId, List<Variable> allVariable) throws UseCaseException
  {
    ManualActivateInput manualActivateInput = new ManualActivateInput(executionId, allVariable, new ArrayList<>());
    ManualActivate manualActivate = new ManualActivate(authenticationService, authorizationService, executionService);

    return manualActivate.execute(manualActivateInput);
  }

  private String getExecutionIdByActivityName(List<Execution> executions, List<String> activityNames)
  {
    for (Execution execution : executions)
    {
      String executionActivityName = execution.getActivityName();

      for (String activityName : activityNames)
      {
        if (executionActivityName.equals(activityName))
        {
          return execution.getId().getId();
        }
      }
    }
    return null;
  }

  private Map<String, Object> convertVariableMap(List<Variable> variables)
  {
    Map<String, Object> variableMap = new HashMap<>();

    for (Variable variable : variables)
    {
      String id = variable.getId().getId();
      Serializable value = variable.getValue();

      variableMap.put(id, value);
    }
    return variableMap;
  }

  private List<Variable> getVariablesByCaseInstanceId(String caseInstanceId) throws UseCaseException
  {
    GetVariablesById getVariablesById = new GetVariablesById(authenticationService, authorizationService, caseService);
    GetVariablesByIdOutput getVariablesByIdOutput = getVariablesById.execute(caseInstanceId);

    return getVariablesByIdOutput.getVariables();
  }

  private TaskForm getTaskFormByTaskName(String caseInstanceId, List<Task> activeTasks, List<String> taskNames) throws UseCaseException
  {
    for (Task activeTask : activeTasks)
    {
      for (String taskName : taskNames)
      {
        if (activeTask.getName().equals(taskName))
        {
          String activeTaskId = activeTask.getId().getId();
          GetFormByTaskIdOutput taskFormsOutput = getFormByTaskId(caseInstanceId, activeTaskId);

          TaskForm taskForm = taskFormsOutput.getTaskForm();
          Collection<TaskFormField> taskFormFields = taskForm.getTaskFormFields();

          setBigDecimalType(taskFormFields);
          setValueByVariable(caseInstanceId, taskFormFields);

          return taskForm;
        }
      }
    }
    return null;
  }

  private GetFormByTaskIdOutput getFormByTaskId(String caseInstanceId, String taskId) throws UseCaseException
  {
    GetFormByTaskIdInput taskIdInput = new GetFormByTaskIdInput(caseInstanceId, taskId);
    GetFormByTaskId getFormByTaskId = new GetFormByTaskId(taskFormService);

    return getFormByTaskId.execute(taskIdInput);
  }

  private void setBigDecimalType(Collection<TaskFormField> taskFormFields)
  {
    for (TaskFormField taskFormField : taskFormFields)
    {
      String type = taskFormField.getType();
      if (type.equalsIgnoreCase(DOUBLE_FORM_FIELD_TYPE) || type.equalsIgnoreCase(LONG_FORM_FIELD_TYPE))
      {
        taskFormField.setType(BIG_DECIMAL_FORM_FIELD_TYPE);
      }
    }
  }

  private List<Task> getActiveTasks(String caseInstanceId) throws UseCaseException
  {
    GetActiveTaskByInstanceId getActiveTaskByInstanceId = new GetActiveTaskByInstanceId(taskService, runtimeService, executionService);
    GetActiveTaskByInstanceIdOutput activeTasksOutput = getActiveTaskByInstanceId.execute(caseInstanceId);

    return activeTasksOutput.getActiveTasks();
  }

  private void setValueByVariable(String caseInstanceId, Collection<TaskFormField> taskFormFields) throws UseCaseException
  {
    GetVariablesById getVariablesById = new GetVariablesById(authenticationService, authorizationService, caseService);
    GetVariablesByIdOutput outputVariables = getVariablesById.execute(caseInstanceId);

    List<Variable> variables = outputVariables.getVariables();

    for (TaskFormField taskFormField : taskFormFields)
    {
      for (Variable variable : variables)
      {
        String formFieldId = taskFormField.getId().getId();
        String variableId = variable.getId().getId();

        Serializable value = variable.getValue();

        if (formFieldId.equalsIgnoreCase(variableId))
        {
          taskFormField.setFormFieldValue(new FormFieldValue(value));
        }
      }
    }
  }
}


