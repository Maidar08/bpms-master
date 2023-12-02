/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.rest.controller;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import javax.inject.Inject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.Validate;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.bpm.rest.model.RestActivated;
import mn.erin.bpm.rest.model.RestCaseNode;
import mn.erin.bpm.rest.model.RestDeleteProcess;
import mn.erin.bpm.rest.model.RestManualActivation;
import mn.erin.bpm.rest.model.RestNote;
import mn.erin.bpm.rest.model.RestNotes;
import mn.erin.bpm.rest.model.RestProcessInstance;
import mn.erin.bpm.rest.model.RestProcessRequestModel;
import mn.erin.bpm.rest.model.RestStartProcess;
import mn.erin.bpm.rest.model.RestVariable;
import mn.erin.bpm.rest.util.BpmRestUtils;
import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmMessagesConstants;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.exception.BpmInvalidArgumentException;
import mn.erin.domain.bpm.model.cases.Case;
import mn.erin.domain.bpm.model.cases.Execution;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.model.process.Process;
import mn.erin.domain.bpm.model.task.Task;
import mn.erin.domain.bpm.model.variable.Variable;
import mn.erin.domain.bpm.model.variable.VariableId;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.repository.LoanContractRequestRepository;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.repository.ProcessTypeRepository;
import mn.erin.domain.bpm.repository.VariableRepository;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.service.ExecutionService;
import mn.erin.domain.bpm.service.NewCoreBankingService;
import mn.erin.domain.bpm.service.ProcessTypeService;
import mn.erin.domain.bpm.service.TaskService;
import mn.erin.domain.bpm.usecase.cases.get_cases.CloseCasesByDate;
import mn.erin.domain.bpm.usecase.cases.get_cases.CloseCasesByDateInput;
import mn.erin.domain.bpm.usecase.cases.get_cases.GetCases;
import mn.erin.domain.bpm.usecase.cases.get_cases.GetCasesOutput;
import mn.erin.domain.bpm.usecase.cases.start_case.StartCase;
import mn.erin.domain.bpm.usecase.cases.start_case.StartCaseInput;
import mn.erin.domain.bpm.usecase.cases.start_case.StartCaseOutput;
import mn.erin.domain.bpm.usecase.contract.CreateLoanContractRequest;
import mn.erin.domain.bpm.usecase.contract.GetLoanContractRequest;
import mn.erin.domain.bpm.usecase.contract.GetLoanContractRequestById;
import mn.erin.domain.bpm.usecase.execution.FilterExecutionByRoleAndType;
import mn.erin.domain.bpm.usecase.execution.FilterExecutionByRoleAndTypeInput;
import mn.erin.domain.bpm.usecase.execution.GetActiveExecutions;
import mn.erin.domain.bpm.usecase.execution.GetActiveExecutionsOutput;
import mn.erin.domain.bpm.usecase.execution.GetEnabledExecutions;
import mn.erin.domain.bpm.usecase.execution.GetEnabledExecutionsInput;
import mn.erin.domain.bpm.usecase.execution.GetEnabledExecutionsOutput;
import mn.erin.domain.bpm.usecase.execution.GroupEnableExecutionsByParentIdInput;
import mn.erin.domain.bpm.usecase.execution.GroupEnabledExecutionsByParentId;
import mn.erin.domain.bpm.usecase.process.CreateProcess;
import mn.erin.domain.bpm.usecase.process.CreateProcessInput;
import mn.erin.domain.bpm.usecase.process.CreateProcessRequest;
import mn.erin.domain.bpm.usecase.process.CreateProcessRequestInput;
import mn.erin.domain.bpm.usecase.process.CreateProcessRequestOutput;
import mn.erin.domain.bpm.usecase.process.GetProcess;
import mn.erin.domain.bpm.usecase.process.GetProcessEntityInput;
import mn.erin.domain.bpm.usecase.process.GetProcessInput;
import mn.erin.domain.bpm.usecase.process.GetProcessOutput;
import mn.erin.domain.bpm.usecase.process.GetProcessParameters;
import mn.erin.domain.bpm.usecase.process.RemoveProcess;
import mn.erin.domain.bpm.usecase.process.RemoveProcessInput;
import mn.erin.domain.bpm.usecase.process.RemoveProcessOutput;
import mn.erin.domain.bpm.usecase.process.StartProcess;
import mn.erin.domain.bpm.usecase.process.StartProcessInput;
import mn.erin.domain.bpm.usecase.process.StartProcessOutput;
import mn.erin.domain.bpm.usecase.process.UpdateAssignedUser;
import mn.erin.domain.bpm.usecase.process.UpdateAssignedUserInput;
import mn.erin.domain.bpm.usecase.process.get_variables.GetVariablesById;
import mn.erin.domain.bpm.usecase.process.get_variables.GetVariablesByIdOutput;
import mn.erin.domain.bpm.usecase.process.manual_activate.ManualActivate;
import mn.erin.domain.bpm.usecase.process.manual_activate.ManualActivateInput;
import mn.erin.domain.bpm.usecase.task.GetCompletedTasks;
import mn.erin.domain.bpm.usecase.task.GetCompletedTasksOutput;
import mn.erin.domain.dms.repository.FolderRepository;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;

import static mn.erin.bpm.rest.constant.BpmsControllerConstants.INTERNAL_SERVER_ERROR;
import static mn.erin.bpm.rest.util.BpmRestUtils.toDateStringWithHour;
import static mn.erin.bpm.rest.util.BpmRestUtils.toRestCase;
import static mn.erin.bpm.rest.util.BpmRestUtils.toRestCaseNode;
import static mn.erin.bpm.rest.util.BpmRestUtils.toRestExecutions;
import static mn.erin.bpm.rest.util.BpmRestUtils.toRestExecutionsFromTasks;
import static mn.erin.bpm.rest.util.BpmRestUtils.toRestVariables;
import static mn.erin.bpm.rest.util.BpmRestUtils.toVariables;
import static mn.erin.domain.bpm.BpmActivityIdConstants.ACTIVITY_ID_GENERATE_LOAN_DECISION;
import static mn.erin.domain.bpm.BpmActivityIdConstants.ACTIVITY_ID_MICRO_LOAN_DECISION;
import static mn.erin.domain.bpm.BpmActivityIdConstants.ACTIVITY_ID_MORTGAGE_LOAN_DECISION;
import static mn.erin.domain.bpm.BpmActivityIdConstants.ACTIVITY_ID_SEND_LOAN_DECISION;
import static mn.erin.domain.bpm.BpmModuleConstants.ACTIVE_REQUEST;
import static mn.erin.domain.bpm.BpmModuleConstants.ADMIN_1;
import static mn.erin.domain.bpm.BpmModuleConstants.ALL_LOAN_REQUEST;
import static mn.erin.domain.bpm.BpmModuleConstants.CONTEXT_ACCOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.CONTEXT_ACCOUNT_UDF;
import static mn.erin.domain.bpm.BpmModuleConstants.CONTEXT_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.CONTEXT_CUSTOMER;
import static mn.erin.domain.bpm.BpmModuleConstants.CONTEXT_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmModuleConstants.INDEX_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.ISO_DATE_FORMAT;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_REQUEST;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.SCORING;
import static mn.erin.domain.bpm.BpmModuleConstants.STAGE;
import static mn.erin.domain.bpm.BpmUserRoleConstants.ADMIN_ROLES;
import static mn.erin.domain.bpm.BpmUserRoleConstants.DIRECTOR_ROLES;
import static mn.erin.domain.bpm.BpmUserRoleConstants.HR_SPECIALIST;
import static mn.erin.domain.bpm.BpmUserRoleConstants.SPECIALIST_ROLES;
import static mn.erin.domain.bpm.util.process.BpmUtils.stringToDateByFormat;

/**
 * Represents Case Rest Api.
 *
 * @author Tamir
 */
@Api
@RestController
@RequestMapping(value = "bpm/cases", name = "Provides BPM case APIs.")
public class CaseRestApi extends BaseBpmsRestApi
{
  private static final Logger LOGGER = LoggerFactory.getLogger(CaseRestApi.class);
  private static final String ERR_INSTANCE_ID_NULL_MSG = "Instance id is required!";
  private static final String ERR_INSTANCE_ID_NULL_CODE = "BPMS015";

  public static final String USER_NAME = "username";
  public static final String NOTE = "note";
  public static final String NOTE_DATE = "noteDate";
  public static final String IS_REASON = "isReason";

  protected final AuthenticationService authenticationService;
  protected final AuthorizationService authorizationService;

  protected final CaseService caseService;
  protected final ExecutionService executionService;
  protected final TaskService taskService;
  protected final ProcessTypeService processTypeService;

  protected final ProcessRepository processRepository;
  protected final ProcessRequestRepository processRequestRepository;

  protected final VariableRepository variableRepository;
  protected final MembershipRepository membershipRepository;

  protected final TenantIdProvider tenantIdProvider;
  protected final FolderRepository folderRepository;
  protected final LoanContractRequestRepository loanContractRequestRepository;
  protected final NewCoreBankingService newCoreBankingService;

  private final ProcessTypeRepository processTypeRepository;
  private final GroupRepository groupRepository;
  private final CloseCasesByDateInput closeCasesByDateInput;
  private final Environment environment;

  @Inject
  public CaseRestApi(BpmsServiceRegistry bpmsServiceRegistry, BpmsRepositoryRegistry bpmsRepositoryRegistry,
      AuthenticationService authenticationService,
      AuthorizationService authorizationService,
      TaskService taskService, MembershipRepository membershipRepository,
      TenantIdProvider tenantIdProvider,
      FolderRepository folderRepository,
      LoanContractRequestRepository loanContractRequestRepository, NewCoreBankingService newCoreBankingService,
      ProcessTypeRepository processTypeRepository, GroupRepository groupRepository,
      CloseCasesByDateInput closeCasesByDateInput, Environment environment)
  {
    super(bpmsServiceRegistry, bpmsRepositoryRegistry);
    this.caseService = Objects.requireNonNull(bpmsServiceRegistry.getCaseService(), "Case service is required!");
    this.executionService = Objects.requireNonNull(bpmsServiceRegistry.getExecutionService(), "Execution service is required!");

    this.processRequestRepository = Objects.requireNonNull(bpmsRepositoryRegistry.getProcessRequestRepository(), "Process request repository is required!");
    this.variableRepository = Objects.requireNonNull(bpmsRepositoryRegistry.getVariableRepository(), "Variable repository is required!");

    this.processTypeService = Objects.requireNonNull(bpmsServiceRegistry.getProcessTypeService(), "Process type service is required!");
    this.processRepository = Objects.requireNonNull(bpmsRepositoryRegistry.getProcessRepository(), "Process repository is required!");

    this.authenticationService = Objects.requireNonNull(authenticationService, "Authentication service is required!");
    this.authorizationService = Objects.requireNonNull(authorizationService, "AuthorizationService service is required!");
    this.taskService = Objects.requireNonNull(taskService);

    this.membershipRepository = Objects.requireNonNull(membershipRepository, "Membership Repository is required!");
    this.tenantIdProvider = Objects.requireNonNull(tenantIdProvider, "Tenant Id Provider is required!");
    this.folderRepository = Objects.requireNonNull(folderRepository, "Folder repository is required!");
    this.loanContractRequestRepository = Objects.requireNonNull(loanContractRequestRepository, "Loan contract repository is required!");
    this.newCoreBankingService = Objects.requireNonNull(newCoreBankingService, "New core banking service is required!");
    this.processTypeRepository = processTypeRepository;
    this.groupRepository = groupRepository;
    this.closeCasesByDateInput = closeCasesByDateInput;
    this.environment = environment;
  }

  @ApiOperation("Closes and terminates cases by date range")
  @PostMapping("/close/{dateString}/{sleepTime}/{sleepPerProcess}")
  public ResponseEntity<RestResult> closeCasesByDate(@PathVariable String dateString, @PathVariable long sleepTime, @PathVariable int sleepPerProcess) throws UseCaseException
  {
    if (org.apache.commons.lang3.StringUtils.isBlank(dateString))
    {
      RestResponse.internalError("Invalid date!");
    }
    asyncCloseCases(dateString, sleepTime, sleepPerProcess);

    return RestResponse.success(null);
  }

  @PostMapping("/stop")
  public ResponseEntity<RestResult> stopCloseCasesThread()
  {
      closeCasesByDateInput.setThreadStopper(true);
    return RestResponse.success(null);
  }

  @ApiOperation("Starts case by case definition key.")
  @PostMapping("/start/caseDefinitionKey/{caseDefinitionKey}")
  public ResponseEntity<RestResult> startCaseByDefKey(@PathVariable String caseDefinitionKey) throws UseCaseException
  {
    if (StringUtils.isEmpty(caseDefinitionKey))
    {
      return RestResponse.badRequest("Case definition key is required!");
    }

    StartCase startCase = new StartCase(authenticationService, authorizationService, caseService);
    StartCaseOutput output = startCase.execute(new StartCaseInput(caseDefinitionKey));

    Case startedCase = output.getStartedCase();
    String caseInstanceId = startedCase.getId().getId();

    CreateProcessInput createProcessInput = new CreateProcessInput(caseInstanceId, LocalDateTime.now(), Collections.emptyMap());
    CreateProcess createProcess = new CreateProcess(authenticationService, authorizationService, processRepository);

    createProcess.execute(createProcessInput);

    return RestResponse.success(toRestCase(startedCase));
  }

  @ApiOperation("Gets all variable by case instance id.")
  @GetMapping("/variables/all/{instanceId}")
  public ResponseEntity<RestResult> getAllVariable(@PathVariable String instanceId) throws UseCaseException, BpmInvalidArgumentException
  {
    if (StringUtils.isEmpty(instanceId))
    {
      throw new BpmInvalidArgumentException(ERR_INSTANCE_ID_NULL_CODE, ERR_INSTANCE_ID_NULL_MSG);
    }

    GetVariablesById getVariablesById = new GetVariablesById(authenticationService, authorizationService, caseService);
    GetVariablesByIdOutput output = getVariablesById.execute(instanceId);
    List<Variable> processVariables = output.getVariables();

    return RestResponse.success(toRestVariables(processVariables));
  }

  @ApiOperation("Gets variables by case execution id.")
  @GetMapping("/variables/loan-info/{instanceId}")
  public ResponseEntity<RestResult> getResearchInfoByInstanceId(@PathVariable String instanceId) throws UseCaseException, BpmInvalidArgumentException
  {

    if (StringUtils.isEmpty(instanceId))
    {
      throw new BpmInvalidArgumentException(ERR_INSTANCE_ID_NULL_CODE, ERR_INSTANCE_ID_NULL_MSG);
    }

    GetVariablesById getVariablesById = new GetVariablesById(authenticationService, authorizationService, caseService);
    GetVariablesByIdOutput output = getVariablesById.execute(instanceId);
    List<Variable> processVariables = output.getVariables();

    Collection<Variable> loanInfoVariables = new ArrayList<>();

    putResultVariables(LOAN_REQUEST, processVariables, loanInfoVariables);
    putResultVariables(SCORING, processVariables, loanInfoVariables);

    return RestResponse.success(toRestVariables(loanInfoVariables));
  }

  @ApiOperation("Gets variables by case execution id.")
  @GetMapping("/variables/main-info/{instanceId}")
  public ResponseEntity<RestResult> getMainInfoByInstanceId(@PathVariable String instanceId) throws UseCaseException, BpmInvalidArgumentException
  {
    if (StringUtils.isEmpty(instanceId))
    {
      throw new BpmInvalidArgumentException(ERR_INSTANCE_ID_NULL_CODE, ERR_INSTANCE_ID_NULL_MSG);
    }
    GetVariablesById getVariablesById = new GetVariablesById(authenticationService, authorizationService, caseService);
    GetVariablesByIdOutput output = getVariablesById.execute(instanceId);

    List<Variable> allVariable = output.getVariables();
    Collection<Variable> mainInfos = new ArrayList<>();

    Integer indexCoBorrower = getIndexCoBorrower(allVariable);

    addCustomerInfos(allVariable, mainInfos, CONTEXT_CUSTOMER);
    addCoBorrowerInfos(allVariable, mainInfos, indexCoBorrower);

    return RestResponse.success(toRestVariables(mainInfos));
  }

  @ApiOperation("Gets variables by case execution id. ")
  @GetMapping("/variables/notes-info/{parameterEntityType}/{caseInstanceId}")
  public ResponseEntity<RestResult> getNotesInfoByInstanceId(@PathVariable String caseInstanceId, @PathVariable String parameterEntityType)
      throws UseCaseException, BpmInvalidArgumentException
  {
    if (StringUtils.isEmpty(caseInstanceId))
    {
      throw new BpmInvalidArgumentException(ERR_INSTANCE_ID_NULL_CODE, ERR_INSTANCE_ID_NULL_MSG);
    }

    GetProcessEntityInput input = new GetProcessEntityInput(caseInstanceId, ParameterEntityType.fromStringToEnum(parameterEntityType));
    GetProcessParameters getProcessEntity = new GetProcessParameters(processRepository);

    Map<String, Serializable> noteEntityValues = getProcessEntity.execute(input);
    Set<Map.Entry<String, Serializable>> noteEntries = noteEntityValues.entrySet();

    List<RestNote> noteList = new ArrayList<>();

    for (Map.Entry<String, Serializable> noteEntry : noteEntries)
    {
      String noteEntryString = (String) noteEntry.getValue();
      JSONObject jsonObject = new JSONObject(noteEntryString);

      String name = jsonObject.getString(USER_NAME);
      String noteText = jsonObject.getString(NOTE);
      String stringDate = jsonObject.getString(NOTE_DATE);
      LocalDateTime noteDate = LocalDateTime.parse(stringDate);
      boolean isReason = jsonObject.getBoolean(IS_REASON);

      RestNote restNote = new RestNote();
      restNote.setDate(noteDate);
      restNote.setFormattedDate(toDateStringWithHour(noteDate));
      restNote.setNoteText(noteText);
      restNote.setReason(isReason);
      restNote.setUsername(name);
      noteList.add(restNote);
    }
    Collections.sort(noteList);
    RestNotes restNotes = new RestNotes(caseInstanceId, noteList);
    return RestResponse.success(restNotes);
  }

  @ApiOperation("Gets variables by case execution id.")
  @GetMapping("/variables/account-info/{instanceId}")
  public ResponseEntity<RestResult> getAccountInfoByInstanceId(@PathVariable String instanceId) throws UseCaseException, BpmInvalidArgumentException
  {
    if (StringUtils.isEmpty(instanceId))
    {
      throw new BpmInvalidArgumentException(ERR_INSTANCE_ID_NULL_CODE, ERR_INSTANCE_ID_NULL_MSG);
    }

    GetVariablesById getVariablesById = new GetVariablesById(authenticationService, authorizationService, caseService);
    GetVariablesByIdOutput output = getVariablesById.execute(instanceId);
    List<Variable> processVariables = output.getVariables();
    Collection<Variable> loanInfoVariables = new ArrayList<>();

    putResultVariables(CONTEXT_ACCOUNT, processVariables, loanInfoVariables);
    putResultVariables(CONTEXT_ACCOUNT_UDF, processVariables, loanInfoVariables);

    return RestResponse.success(toRestVariables(loanInfoVariables));
  }

  @ApiOperation("Gets variables by case execution id.")
  @GetMapping("/variables/loan-contract/{instanceId}")
  public ResponseEntity<RestResult> getContractInfoByInstanceId(@PathVariable String instanceId) throws UseCaseException, BpmInvalidArgumentException
  {
    if (StringUtils.isEmpty(instanceId))
    {
      throw new BpmInvalidArgumentException(ERR_INSTANCE_ID_NULL_CODE, ERR_INSTANCE_ID_NULL_MSG);
    }
    GetVariablesById getVariablesById = new GetVariablesById(authenticationService, authorizationService, caseService);
    GetVariablesByIdOutput output = getVariablesById.execute(instanceId);
    List<Variable> processVariables = output.getVariables();

    Collection<Variable> contractInfoVariables = new ArrayList<>();

    GetProcessInput input = new GetProcessInput(instanceId);
    GetProcess getProcess = new GetProcess(authenticationService, authorizationService, processRepository);
    GetProcessOutput outputProcess = getProcess.execute(input);

    Process returnedProcess = outputProcess.getReturnedProcess();
    Map<ParameterEntityType, Map<String, Serializable>> processParameters = returnedProcess.getProcessParameters();

    Map<String, Serializable> parameters = processParameters.get(ParameterEntityType.LOAN_CONTRACT);

    putResultVariables(CONTEXT_LOAN_CONTRACT, processVariables, contractInfoVariables);

    for (Variable contractInfoVariable : contractInfoVariables)
    {
      Serializable value = contractInfoVariable.getValue();

      if (null == value && null != contractInfoVariable.getId())
      {
        String contractVariableId = contractInfoVariable.getId().getId();
        if (null != parameters && parameters.containsKey(contractVariableId))
        {
          Serializable parameterValue = parameters.get(contractVariableId);

          if (null != parameterValue)
          {
            contractInfoVariable.setValue(parameterValue);
          }
        }
      }
    }

    return RestResponse.success(toRestVariables(contractInfoVariables));
  }

  @ApiOperation("Gets variables by case execution id.")
  @GetMapping("/variables/active-request-main-info/{instanceId}")
  public ResponseEntity<RestResult> getActiveRequestMainInfo(@PathVariable String instanceId) throws UseCaseException, BpmInvalidArgumentException
  {
    if (StringUtils.isEmpty(instanceId))
    {
      throw new BpmInvalidArgumentException(ERR_INSTANCE_ID_NULL_CODE, ERR_INSTANCE_ID_NULL_MSG);
    }

    GetVariablesById getVariablesById = new GetVariablesById(authenticationService, authorizationService, caseService);
    GetVariablesByIdOutput output = getVariablesById.execute(instanceId);

    List<Variable> allVariable = output.getVariables();
    Collection<Variable> mainInfos = new ArrayList<>();

    addCustomerInfos(allVariable, mainInfos, ACTIVE_REQUEST);

    return RestResponse.success(toRestVariables(mainInfos));
  }

  @ApiOperation("Manually activate enabled execution otherwise ENABLED state to ACTIVE state.")
  @PostMapping("/executions/manual-activate")
  public ResponseEntity<RestResult> manualActivate(@RequestBody RestManualActivation restManualActivation) throws UseCaseException, BpmInvalidArgumentException
  {

    if (null == restManualActivation)
    {
      String errorCode = "BPMS038";
      throw new BpmInvalidArgumentException(errorCode, "RestManualStart body cannot be null!");
    }

    String executionId = restManualActivation.getExecutionId();

    if (StringUtils.isEmpty(executionId))
    {
      throw new BpmInvalidArgumentException(BpmMessagesConstants.EXECUTION_ID_NULL_CODE, BpmMessagesConstants.EXECUTION_ID_NULL_MESSAGE);
    }

    List<RestVariable> restVariables = restManualActivation.getVariables();
    List<RestVariable> restDeleteVariables = restManualActivation.getDeletions();

    List<Variable> variables = toVariables(restVariables);
    List<Variable> deletions = toVariables(restDeleteVariables);

    ManualActivateInput input = new ManualActivateInput(executionId, variables, deletions);

    ManualActivate manualActivate = new ManualActivate(authenticationService, authorizationService, executionService);

    boolean isManualActivated = manualActivate.execute(input);

    return RestResponse.success(new RestActivated(isManualActivated));
  }

  @ApiOperation("Gets completed executions")
  @GetMapping(value = "/executions/completed/{instanceId}/{requestType}")
  public ResponseEntity<RestResult> getCompleted(@PathVariable String instanceId, @PathVariable String requestType)
      throws UseCaseException, BpmInvalidArgumentException
  {
    if (StringUtils.isEmpty(instanceId))
    {
      throw new BpmInvalidArgumentException(ERR_INSTANCE_ID_NULL_CODE, "Case instance id is required!");
    }

    GetCompletedTasks getCompletedTasks = new GetCompletedTasks(bpmsServiceRegistry);
    GetCompletedTasksOutput completedTasksOutput = getCompletedTasks.execute(instanceId);

    Collection<Task> completedTasks = completedTasksOutput.getCompletedTasks();

    return RestResponse.success(toRestExecutionsFromTasks(completedTasks));
  }

  @ApiOperation("Gets active executions")
  @GetMapping(value = "/executions/active/{instanceId}")
  public ResponseEntity<RestResult> getActive(@PathVariable String instanceId) throws UseCaseException, BpmInvalidArgumentException
  {
    if (StringUtils.isEmpty(instanceId))
    {
      throw new BpmInvalidArgumentException(ERR_INSTANCE_ID_NULL_CODE, "Case instance id is required!");
    }

    GetActiveExecutions getActiveExecutions = new GetActiveExecutions(executionService);
    GetActiveExecutionsOutput output = getActiveExecutions.execute(instanceId);
    List<Execution> executions = output.getExecutions();
    return RestResponse.success(toRestExecutions(executions));
  }

  @ApiOperation("Creates a process request")
  @PostMapping("/start")
  public ResponseEntity<RestResult> start(@RequestBody RestStartProcess restStartProcess) throws UseCaseException, BpmInvalidArgumentException
  {
    String processRequestId = restStartProcess.getProcessRequestId();

    if (StringUtils.isEmpty(processRequestId))
    {
      throw new BpmInvalidArgumentException(BpmMessagesConstants.PROCESS_REQUEST_ID_NULL_CODE, BpmMessagesConstants.PROCESS_REQUEST_ID_NULL_MESSAGE);
    }

    StartProcessInput input = new StartProcessInput(processRequestId);
    if (null != restStartProcess.getProcessCategory() && !StringUtils.isEmpty(restStartProcess.getProcessCategory()))
    {
      input.setProcessCategory(restStartProcess.getProcessCategory());
    }

    if (null != restStartProcess.getProcessType() && !StringUtils.isEmpty(restStartProcess.getProcessType()))
    {
      input.setProcessType(restStartProcess.getProcessType());
    }

    if(null != restStartProcess.getCreatedUser() && !StringUtils.isEmpty(restStartProcess.getCreatedUser())){
      input.setCreatedUser(restStartProcess.getCreatedUser());
    }

     StartProcess startProcess = new StartProcess(authenticationService, authorizationService,
        processRequestRepository, processTypeService, processRepository, caseService);

    StartProcessOutput output = startProcess.execute(input);
    String instanceId = output.getProcessInstanceId();

    return RestResponse.success(new RestProcessInstance(instanceId));
  }

  /******** Creating nultiple cases as async api is beginning here */

  @ApiOperation("Creates a process request")
  @PostMapping("/startCases/{requestCount}")
  public ResponseEntity<RestResult> startCases(@PathVariable int requestCount, @RequestBody RestStartProcess restStartProcess)

  {

    LOGGER.info("################ STARTS PROCESS #############");

    callAsyncCaseStartService(requestCount, restStartProcess);
    return RestResponse.success(null);
  }

  private void callAsyncCaseStartService(int requestCount, RestStartProcess restStartProcess)
  {

    ExecutorService executor = Executors.newFixedThreadPool(10);
    CaseRestApi.ProcessRunnable processRunnable = new CaseRestApi.ProcessRunnable(requestCount, restStartProcess);
    executor.execute(processRunnable);
    executor.shutdown();
  }

  private void asyncCloseCases(String dateString, long sleepTime, int sleepPerProcess)
  {
    ExecutorService executor = Executors.newFixedThreadPool(10);
    CaseRunnable caseRunnable = new CaseRunnable(dateString, sleepTime, sleepPerProcess);
    executor.execute(caseRunnable);
    executor.shutdown();
  }

  public class CaseRunnable implements Runnable
  {
    String dateString;
    long sleepTime;
    int sleepPerProcess;
    CaseRunnable(String dateString, long sleepTime, int sleepPerProcess)
    {
      this.dateString = dateString;
      this.sleepTime = sleepTime;
      this.sleepPerProcess = sleepPerProcess;
    }
    @Override
    public void run()
    {
        try
        {
          LocalDate endDate = stringToDateByFormat(ISO_DATE_FORMAT, dateString);
          closeCasesByDateInput.setEndDate(endDate);
          closeCasesByDateInput.setSleepTime(sleepTime);
          closeCasesByDateInput.setSleepPerProcess(sleepPerProcess);
          closeCasesByDateInput.setThreadStopper(false);
          CloseCasesByDate useCase = new CloseCasesByDate(processRepository, caseService);
          useCase.execute(closeCasesByDateInput);
        }
        catch (ParseException | UseCaseException e)
        {
          e.printStackTrace();
        }
    }
  }

  public class ProcessRunnable implements Runnable
  {
    int requestCount;
    RestStartProcess restStartProcess;
    public ProcessRunnable(int requestCount, RestStartProcess restStartProcess)
    {
      this.requestCount = requestCount;
      this.restStartProcess = restStartProcess;
    }

    public void run()
    {
      int counter = 0;
      try
      {
        while (counter < requestCount)
        {
          CreateProcessRequest createProcessRequest = new CreateProcessRequest(authenticationService, authorizationService, tenantIdProvider,
              processRequestRepository, groupRepository, processTypeRepository);
          CreateProcessRequestInput input2 = new CreateProcessRequestInput("108", "branchSpecialist108", "consumptionLoan");
          Map<String, Serializable> parameters = new HashMap<>();
          parameters.put("registerNumber", "ИМ96073111");
//          parameters.put("registerNumber", "ТА97123184");
          parameters.put("loanProductDescription", "EA50-365-Өрхийн зээл - Иргэн");
          parameters.put("cifNumber", null);
          parameters.put("amount", "1");
          parameters.put("monthlyRepayment", null);
          parameters.put("incomeType", "Цалингийн орлого");
          parameters.put("purpose", null);
          parameters.put("branchNumber", "108");
          parameters.put("channel", "BPMS APP");
          parameters.put("firstPaymentDate", null);
          parameters.put("loanProduct", "EA50");
          input2.setParameters(parameters);
          final CreateProcessRequestOutput output = createProcessRequest.execute(input2);
          final String requestId = output.getProcessRequestId();

          StartProcessInput input = new StartProcessInput(requestId);
          if (null != restStartProcess.getProcessCategory() && !StringUtils.isEmpty(restStartProcess.getProcessCategory()))
          {
            input.setProcessCategory(restStartProcess.getProcessCategory());
          }

          if (null != restStartProcess.getProcessType() && !StringUtils.isEmpty(restStartProcess.getProcessType()))
          {
            input.setProcessType(restStartProcess.getProcessType());
          }

          if (null != restStartProcess.getCreatedUser() && !StringUtils.isEmpty(restStartProcess.getCreatedUser()))
          {
            input.setCreatedUser(restStartProcess.getCreatedUser());
          }
          StartProcess startProcess = new StartProcess(authenticationService, authorizationService,
              processRequestRepository, processTypeService, processRepository, caseService);

          startProcess.execute(input);
          UpdateAssignedUserInput updateAssignedUserInput = new UpdateAssignedUserInput(output.getProcessRequestId(), authenticationService.getCurrentUserId());
          UpdateAssignedUser updateAssignedUser = new UpdateAssignedUser(authenticationService, authorizationService, processRequestRepository);
          updateAssignedUser.execute(updateAssignedUserInput);
          counter++;
        }
        LOGGER.info("########### CREATED REQUEST COUNT={}", counter);
      }
      catch (Exception var6)
      {
        var6.printStackTrace();
      }
    }
  }

  /******** Create number of cases as async for test ends here */

  @ApiOperation("Gets the all loan contract requests")
  @GetMapping("/loan-contract/get-requests")
  public ResponseEntity<RestResult> getAllContractRequest() throws UseCaseException
  {
    GetLoanContractRequest getLoanContractRequest = new GetLoanContractRequest(loanContractRequestRepository, authenticationService, authorizationService);
    return RestResponse.success(getLoanContractRequest.execute(null));
  }

  @ApiOperation("Creates the loan contract request")
  @PostMapping("/loan-contract/create")
  public ResponseEntity<RestResult> createLoanContractRequest(@RequestBody RestProcessRequestModel request) throws UseCaseException
  {
    Validate.notNull(request);

    CreateLoanContractRequest createLoanContractRequest = new CreateLoanContractRequest(loanContractRequestRepository,
        processTypeService, tenantIdProvider, authenticationService, authorizationService, newCoreBankingService, caseService);

    CreateProcessRequestInput input = new CreateProcessRequestInput(request.getGroupNumber(), request.getCreatedUserId(), request.getProcessTypeId());
    input.setObjectParameters(request.getParameters());

    return RestResponse.success(createLoanContractRequest.execute(input));
  }

  @ApiOperation("Gets the loan contract request by ID")
  @GetMapping("/loan-contract/get-by-id/{id}")
  public ResponseEntity<RestResult> getLoanContract(@PathVariable String id) throws UseCaseException
  {
    GetLoanContractRequestById getLoanContractRequestById = new GetLoanContractRequestById(loanContractRequestRepository, authenticationService,
        authorizationService);
    return RestResponse.success(getLoanContractRequestById.execute(id));
  }

  @ApiOperation("Remove process request")
  @PostMapping("/delete")
  public ResponseEntity<RestResult> delete(@RequestBody RestDeleteProcess restDeleteProcess) throws UseCaseException, BpmInvalidArgumentException
  {
    String processRequestId = restDeleteProcess.getProcessRequestId();
    String processInstanceId = restDeleteProcess.getProcessInstanceId();
    String processRequestState = restDeleteProcess.getProcessRequestState();

    if (StringUtils.isEmpty(processRequestId))
    {
      throw new BpmInvalidArgumentException(BpmMessagesConstants.PROCESS_REQUEST_ID_NULL_CODE, BpmMessagesConstants.PROCESS_REQUEST_ID_NULL_MESSAGE);
    }

    if (StringUtils.isEmpty(processRequestState))
    {
      String errorCode = "BPMS072";
      throw new BpmInvalidArgumentException(errorCode, "Process request state is required!");
    }

    RemoveProcessInput removeProcessInput = new RemoveProcessInput(processRequestId, processInstanceId, processRequestState);
    RemoveProcess removeProcess = new RemoveProcess(bpmsRepositoryRegistry, authenticationService, authorizationService);
    RemoveProcessOutput removeProcessOutput = removeProcess.execute(removeProcessInput);

    return RestResponse.success(removeProcessOutput);
  }

  @ApiOperation("Gets enabled executions by candidate group.")
  @GetMapping(value = "/executions/enabled/candidate-group/{instanceId}")
  public ResponseEntity<RestResult> getEnabledExecutionsByUserId(@PathVariable String instanceId)
      throws UseCaseException, BpmInvalidArgumentException
  {

    if (StringUtils.isEmpty(instanceId))
    {
      throw new BpmInvalidArgumentException(ERR_INSTANCE_ID_NULL_CODE, ERR_INSTANCE_ID_NULL_MSG);
    }
    GetEnabledExecutionsInput input = new GetEnabledExecutionsInput(instanceId);
    GetEnabledExecutions useCase = new GetEnabledExecutions(bpmsServiceRegistry.getExecutionService());

    try
    {
      GetEnabledExecutionsOutput output = useCase.execute(input);
      List<Execution> executions = output.getExecutions();

      // TODO : replace candidate group implementation
      String role = getRole();

      if (role.equals("hrManager"))
      {
        List<Execution> filteredExecutions = filterDirectorExecution(executions, false);
        return RestResponse.success(toRestExecutions(filteredExecutions));
      }
      else if (role.equals("director"))
      {
        List<Execution> filteredExecutions = filterDirectorExecution(executions, true);
        return RestResponse.success(toRestExecutions(filteredExecutions));
      }
      else if (role.equals(ADMIN_1))
      {
        return RestResponse.success(toRestExecutions(Collections.emptyList()));
      }

      return RestResponse.success(toRestExecutions(executions));
    }
    catch (RuntimeException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(INTERNAL_SERVER_ERROR + e.getMessage());
    }
  }

  @ApiOperation("Gets enabled executions")
  @GetMapping(value = "/executions/enabled/{instanceId}/{requestType}")
  public ResponseEntity<RestResult> getEnabledExecutions(@PathVariable String instanceId, @PathVariable String requestType)
      throws UseCaseException, BpmInvalidArgumentException
  {
    if (StringUtils.isEmpty(instanceId))
    {
      throw new BpmInvalidArgumentException(ERR_INSTANCE_ID_NULL_CODE, ERR_INSTANCE_ID_NULL_MSG);
    }

    GetEnabledExecutionsInput input = new GetEnabledExecutionsInput(instanceId);
    GetEnabledExecutions useCase = new GetEnabledExecutions(executionService);

    try
    {
      GetEnabledExecutionsOutput output = useCase.execute(input);
      List<Execution> executions = output.getExecutions();

      String role = getRole();

      if (role.equals(HR_SPECIALIST) && requestType.equalsIgnoreCase(ALL_LOAN_REQUEST))
      {
        return RestResponse.success(toRestExecutions(Collections.emptyList()));
      }
      else
      {
        List<String> adminRoles = Arrays.asList(Objects.requireNonNull(environment.getProperty(ADMIN_ROLES)).split(","));
        List<String> directorRoles = Arrays.asList(Objects.requireNonNull(environment.getProperty(DIRECTOR_ROLES)).split(","));
        List<String> specialistRoles = Arrays.asList(Objects.requireNonNull(environment.getProperty(SPECIALIST_ROLES)).split(","));

        if (adminRoles.stream().anyMatch(role::equals))
        {
          return RestResponse.success(toRestExecutions(Collections.emptyList()));
        }
        else if (specialistRoles.stream().anyMatch(role::equals))
        {
          return RestResponse.success(toRestExecutions(filterDecisionEnabledExecution(executions)));
        }
        else if (directorRoles.stream().anyMatch(role::equals))
        {
          return RestResponse.success(toRestExecutions(filterEveryExecutionExceptSend(executions)));
        }
        else
        {
          return RestResponse.success(toRestExecutions(filterExecutionsByActivityId(executions)));
        }
      }
    }
    catch (RuntimeException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(INTERNAL_SERVER_ERROR + e.getMessage());
    }
  }

  @ApiOperation("Gets enabled executions")
  @GetMapping(value = "/executions/enabled/{instanceId}/filtered")
  public ResponseEntity<RestResult> getFilteredEnabledExecutionsByAssigneeAndCandidategroup(@PathVariable String instanceId)
      throws UseCaseException, BpmInvalidArgumentException, BpmServiceException
  {
    if (StringUtils.isEmpty(instanceId))
    {
      throw new BpmInvalidArgumentException(ERR_INSTANCE_ID_NULL_CODE, ERR_INSTANCE_ID_NULL_MSG);
    }

    GetEnabledExecutionsInput input = new GetEnabledExecutionsInput(instanceId);
    GetEnabledExecutions useCase = new GetEnabledExecutions(executionService);

    GetActiveExecutions getActiveExecutions = new GetActiveExecutions(executionService);

    try
    {
      GetEnabledExecutionsOutput output = useCase.execute(input);
      List<Execution> enableExecutionList = output.getExecutions();

      GetActiveExecutionsOutput activeListOutput = getActiveExecutions.execute(instanceId);
      List<Execution> activeExecutionList = activeListOutput.getExecutions();

      String role = getRole();
      if (role.equals(ADMIN_1))
      {
        return RestResponse.success(toRestExecutions(Collections.emptyList()));
      }

      String type = String.valueOf(caseService.getVariableById(instanceId, PROCESS_TYPE_ID));

      FilterExecutionByRoleAndType filterExecutionUseCase = new FilterExecutionByRoleAndType();
      FilterExecutionByRoleAndTypeInput filterExecutionInput = new FilterExecutionByRoleAndTypeInput(role, type, enableExecutionList);

      enableExecutionList = filterExecutionUseCase.execute(filterExecutionInput);
      filterExecutionInput.setExecutions(activeExecutionList);
      activeExecutionList = filterExecutionUseCase.execute(filterExecutionInput);

      GroupEnabledExecutionsByParentId groupExecution = new GroupEnabledExecutionsByParentId();
      GroupEnableExecutionsByParentIdInput groupExecutionInput = new GroupEnableExecutionsByParentIdInput(activeExecutionList, enableExecutionList);
      Map<String, List<Execution>> groupedExecution = groupExecution.execute(groupExecutionInput);

      List<RestCaseNode> restCaseNodes = toRestCaseNode(groupedExecution);

      return RestResponse.success(restCaseNodes);
    }
    catch (RuntimeException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(INTERNAL_SERVER_ERROR + e.getMessage());
    }
  }

  @GetMapping
  public ResponseEntity<RestResult> getCases()
  {
    GetCases useCase = new GetCases(caseService);
    try
    {
      GetCasesOutput output = useCase.execute(null);
      List<Case> cases = output.getCases();
      return RestResponse.success(BpmRestUtils.toRestCases(cases));
    }
    catch (UseCaseException | RuntimeException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(e.getMessage());
    }
  }

  @GetMapping(value = "/category/{category}")
  public ResponseEntity<RestResult> getCasesByCategory(@PathVariable String category)
  {
    if (StringUtils.isEmpty(category))
    {
      RestResponse.badRequest("Case category is required!");
    }
    List<RestCaseNode> caseList = new ArrayList<>();

    List<RestCaseNode> transactionStages = new ArrayList<>();
    List<RestCaseNode> accountManagementStages = new ArrayList<>();
    List<RestCaseNode> customerManagementStages = new ArrayList<>();

    List<RestCaseNode> customerTransactionProcesses = new ArrayList<>();
    List<RestCaseNode> billingTransactionProcesses = new ArrayList<>();

    caseList.add(new RestCaseNode("caseId", "Гүйлгээ", transactionStages));
    caseList.add(new RestCaseNode("caseId", "Данс", accountManagementStages));
    caseList.add(new RestCaseNode("caseId", "Харилцагч", customerManagementStages));

    transactionStages.add(new RestCaseNode("processId1", "Зээл урьдчилан төлөх", Collections.emptyList()));
    transactionStages.add(new RestCaseNode("processId2", "Билингийн үйлчилгээ", billingTransactionProcesses));
    transactionStages.add(new RestCaseNode("processId3", "Цалин ЗЭТ", Collections.emptyList()));
    transactionStages.add(new RestCaseNode("processId4", "Гүйлгээний баримт хэвлэх", customerTransactionProcesses));

    billingTransactionProcesses.add(new RestCaseNode("taxPaying", "Татвар төлөх", Collections.emptyList()));
    billingTransactionProcesses.add(new RestCaseNode("customsPayment", "Гаалийн төлбөр төлөх", Collections.emptyList()));

    customerTransactionProcesses.add(new RestCaseNode("processId5", "Харилцагчийн гүйлгээний маягт", Collections.emptyList()));
    customerTransactionProcesses.add(new RestCaseNode("processId6", "Мемориал", Collections.emptyList()));
    customerTransactionProcesses.add(new RestCaseNode("processId7", "Цахим гүйлгээ", Collections.emptyList()));

    accountManagementStages.add(new RestCaseNode("processId9", "Дансны гэрээ хэвлэх", Collections.emptyList()));
    accountManagementStages.add(new RestCaseNode("processId10", "Дансны тодорхойлолт", Collections.emptyList()));
    accountManagementStages.add(new RestCaseNode("processId11", "Хүүхдийн мөнгө бүртгэх, засах", Collections.emptyList()));

    customerManagementStages.add(new RestCaseNode("processId12", "USSD үйлчилгээнд бүртгэх", Collections.emptyList()));

    return RestResponse.success(caseList);
  }

  @ApiOperation("Gets variables by case instance id and context.")
  @GetMapping("/variables/{instanceId}/{context}")
  public ResponseEntity<RestResult> getVariablesBy(@PathVariable String instanceId, @PathVariable String context) throws UseCaseException
  {
    GetVariablesById getVariablesById = new GetVariablesById(authenticationService, authorizationService, caseService);
    GetVariablesByIdOutput output = getVariablesById.execute(instanceId);
    List<Variable> processVariables = output.getVariables();

    for (Variable variable : processVariables)
    {
      if (variable.getId().getId().toLowerCase().contains("date"))
      {
        Date birthDate = (Date) variable.getValue();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getDefault());

        variable.setValue(sdf.format(birthDate));
      }
    }

    Collection<Variable> loanInfoVariables = new ArrayList<>();
    putMemoryResultVariables(context, processVariables, loanInfoVariables);

    return RestResponse.success(toRestVariables(loanInfoVariables));
  }

  protected String getRole() throws UseCaseException
  {
    String userId = authenticationService.getCurrentUserId();
    try
    {
      Membership membership = membershipRepository.listAllByUserId(TenantId.valueOf(tenantIdProvider.getCurrentUserTenantId()), UserId.valueOf(userId)).get(0);
      return membership.getRoleId().getId();
    }
    catch (AimRepositoryException e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }

  private List<Execution> filterEveryExecutionExceptSend(List<Execution> executions)
  {
    List<Execution> filteredExecutions = new ArrayList<>();

    for (Execution execution : executions)
    {
      String activityId = execution.getActivityId().getId();
      if (activityId.equals(ACTIVITY_ID_MICRO_LOAN_DECISION)
          || activityId.equals(ACTIVITY_ID_SEND_LOAN_DECISION)
          || activityId.equals(ACTIVITY_ID_MORTGAGE_LOAN_DECISION))
      {
        filteredExecutions.add(execution);
      }
    }

    return filteredExecutions;
  }

  private List<Execution> filterDecisionEnabledExecution(List<Execution> executions)
  {
    List<Execution> filteredExecutions = new ArrayList<>();

    for (Execution execution : executions)
    {
      String activityId = execution.getActivityId().getId();
      String activityType = execution.getActivityType();

      // loan decision, decision generatation, elementary criteria.
      if (!activityId.equals(ACTIVITY_ID_SEND_LOAN_DECISION)
          && !activityId.equals(ACTIVITY_ID_GENERATE_LOAN_DECISION)
          && !activityType.equalsIgnoreCase(STAGE)
          && !activityId.equals(ACTIVITY_ID_MICRO_LOAN_DECISION)
          && !activityId.equals(ACTIVITY_ID_MORTGAGE_LOAN_DECISION)
      )
      {
        filteredExecutions.add(execution);
      }
    }

    return filteredExecutions;
  }

  private Integer getIndexCoBorrower(Collection<Variable> allVariable)
  {
    for (Variable variable : allVariable)
    {
      String variableIdStr = variable.getId().getId();

      if (variableIdStr.equalsIgnoreCase(INDEX_CO_BORROWER))
      {
        return (Integer) variable.getValue();
      }
    }
    return 0;
  }

  private void addCustomerInfos(Collection<Variable> allVariable, Collection<Variable> mainInfos, String context)
  {
    Collection<Variable> customerVariables = variableRepository.findByContext(context);

    for (Variable customerVariable : customerVariables)
    {
      for (Variable variable : allVariable)
      {
        if (variable.sameIdentityAs(customerVariable))
        {
          String variableIdStr = variable.getId().getId();

          addMainInfo(mainInfos, variableIdStr, variable.getValue(),
              customerVariable.getType(), customerVariable.getLabel(), customerVariable.getContext());
        }
      }
    }
  }

  private void addCoBorrowerInfos(Collection<Variable> allVariable, Collection<Variable> mainInfos, Integer coBorrowerIndex)
  {
    Collection<Variable> coBorrowerVariables = variableRepository.findByContext(CONTEXT_CO_BORROWER);

    for (Variable coBorrowerVariable : coBorrowerVariables)
    {
      if (coBorrowerIndex == 0)
      {
        break;
      }

      for (Variable variable : allVariable)
      {
        String borrowerVariableId = coBorrowerVariable.getId().getId();

        VariableId variableId = variable.getId();
        String variableIdStr = variableId.getId();

        for (int index = coBorrowerIndex; index > 0; index--)
        {
          String id = borrowerVariableId + "-" + index;

          if (variableIdStr.equalsIgnoreCase(id))
          {
            addMainInfo(mainInfos, variableIdStr, variable.getValue(),
                coBorrowerVariable.getType(), coBorrowerVariable.getLabel(), coBorrowerVariable.getContext() + "-" + index);
          }
        }
      }
    }
  }

  private void addMainInfo(Collection<Variable> variables, String variableIdStr, Serializable value, String type, String label, String context)
  {
    Variable mainInfoVariable = new Variable(VariableId.valueOf(variableIdStr), value);

    mainInfoVariable.setType(type);
    mainInfoVariable.setLabel(label);

    mainInfoVariable.setContext(context);
    mainInfoVariable.setLocalVariable(false);

    variables.add(mainInfoVariable);
  }

  private void putResultVariables(String context, Collection<Variable> processVariables, Collection<Variable> resultVariables)
  {
    Collection<Variable> definedVariables = variableRepository.findByContext(context);

    for (Variable definedVariable : definedVariables)
    {
      VariableId variableId = VariableId.valueOf(definedVariable.getId().getId());
      Variable variable = new Variable(variableId);

      for (Variable processVariable : processVariables)
      {
        if (definedVariable.sameIdentityAs(processVariable))
        {
          if (processVariable.getValue() instanceof Boolean)
          {
            setBooleanMNValue(processVariable, variable);
          }
          else
          {
            variable.setValue(processVariable.getValue());
          }
        }
      }

      variable.setType(definedVariable.getType());
      variable.setLabel(definedVariable.getLabel());

      variable.setContext(definedVariable.getContext());
      variable.setLocalVariable(definedVariable.isLocalVariable());

      resultVariables.add(variable);
    }
  }

  private void putMemoryResultVariables(String context, Collection<Variable> processVariables, Collection<Variable> resultVariables)
  {
    Collection<Variable> definedVariables = variableRepository.findByContext(context);

    for (Variable definedVariable : definedVariables)
    {
      VariableId variableId = VariableId.valueOf(definedVariable.getId().getId());
      Variable variable = new Variable(variableId);

      for (Variable processVariable : processVariables)
      {
        if (definedVariable.sameIdentityAs(processVariable))
        {
          variable.setValue(processVariable.getValue());
          variable.setLabel(processVariable.getLabel());
        }
      }

      if (null == variable.getLabel())
      {
        variable.setLabel(definedVariable.getLabel());
      }

      variable.setType(definedVariable.getType());

      variable.setContext(definedVariable.getContext());
      variable.setLocalVariable(definedVariable.isLocalVariable());

      resultVariables.add(variable);
    }
  }

  private void setBooleanMNValue(Variable processVariable, Variable resultVariable)
  {
    boolean booleanValue = (boolean) processVariable.getValue();
    if (booleanValue)
    {
      resultVariable.setValue(BpmModuleConstants.YES_MN_VALUE);
    }
    else
    {
      resultVariable.setValue(BpmModuleConstants.NO_MN_VALUE);
    }
  }

  // TODO : remove this method (Dedicated to CMS demo)
  private List<Execution> filterDirectorExecution(List<Execution> executions, boolean isDirector)
  {
    String decideApplicationForm = "decide_application_form";

    List<Execution> filteredExecutions = new ArrayList<>();

    for (Execution execution : executions)
    {
      String activityId = execution.getActivityId().getId();
      if (isDirector)
      {
        if (execution.getActivityId().getId().equals(decideApplicationForm))
        {
          filteredExecutions.add(execution);
        }
      }
      else
      {
        if (!activityId.equals(decideApplicationForm)
            && !activityId.equalsIgnoreCase(STAGE))
        {
          filteredExecutions.add(execution);
        }
      }
    }
    return filteredExecutions;
  }

  private List<Execution> filterExecutionsByActivityId(List<Execution> executions)
  {
    List<Execution> filteredExecutions;
    for (Execution execution : executions)
    {
      String activityId = execution.getActivityId().getId();

      if (activityId.equalsIgnoreCase("PlanItem_033xob0")
          || activityId.equalsIgnoreCase("PlanItem_0g81lne")
          || activityId.equalsIgnoreCase(ACTIVITY_ID_MICRO_LOAN_DECISION)
          || activityId.equalsIgnoreCase(ACTIVITY_ID_MORTGAGE_LOAN_DECISION))
      {
        return Collections.emptyList();
      }
    }

    filteredExecutions = executions.stream()
        .filter(execution -> !execution.getActivityId().getId().equals("PlanItem_0kctick"))
        .collect(Collectors.toList());
    return filteredExecutions;
  }
}
