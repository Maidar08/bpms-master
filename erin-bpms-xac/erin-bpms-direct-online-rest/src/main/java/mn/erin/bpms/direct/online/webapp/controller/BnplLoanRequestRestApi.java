package mn.erin.bpms.direct.online.webapp.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.bpms.direct.online.webapp.model.BnplLoanRequestBody;
import mn.erin.bpms.direct.online.webapp.model.RestDanEntity;
import mn.erin.bpms.direct.online.webapp.model.RestTask;
import mn.erin.domain.aim.provider.ExtSessionInfoCache;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.service.AimServiceRegistry;
import mn.erin.domain.aim.service.EncryptionService;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.aim.usecase.user.LoginUser;
import mn.erin.domain.aim.usecase.user.LoginUserInput;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.form.TaskForm;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.model.process.ProcessRequestId;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.model.process.ProcessType;
import mn.erin.domain.bpm.model.task.Task;
import mn.erin.domain.bpm.model.variable.Variable;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.repository.ProcessTypeRepository;
import mn.erin.domain.bpm.repository.directOnline.DefaultParameterRepository;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;
import mn.erin.domain.bpm.service.NewCoreBankingService;
import mn.erin.domain.bpm.service.RuntimeService;
import mn.erin.domain.bpm.service.TaskFormService;
import mn.erin.domain.bpm.service.TaskService;
import mn.erin.domain.bpm.usecase.GetGeneralInfo;
import mn.erin.domain.bpm.usecase.GetGeneralInfoInput;
import mn.erin.domain.bpm.usecase.bnpl.GetBnplInvoiceInfo;
import mn.erin.domain.bpm.usecase.bnpl.GetBnplRequestStatus;
import mn.erin.domain.bpm.usecase.bnpl.SetBnplInvoiceState;
import mn.erin.domain.bpm.usecase.bnpl.SetInvoiceStateInput;
import mn.erin.domain.bpm.usecase.direct_online.GetLatestRequestByCif;
import mn.erin.domain.bpm.usecase.process.CreateProcessRequest;
import mn.erin.domain.bpm.usecase.process.CreateProcessRequestInput;
import mn.erin.domain.bpm.usecase.process.CreateProcessRequestOutput;
import mn.erin.domain.bpm.usecase.process.GetProcessTypesByCategory;
import mn.erin.domain.bpm.usecase.process.GetProcessTypesByCategoryOutput;
import mn.erin.domain.bpm.usecase.process.StartProcess;
import mn.erin.domain.bpm.usecase.process.StartProcessInput;
import mn.erin.domain.bpm.usecase.process.StartProcessOutput;
import mn.erin.domain.bpm.usecase.process.UpdateRequestState;
import mn.erin.domain.bpm.usecase.process.UpdateRequestStateInput;
import mn.erin.domain.bpm.usecase.task.GetActiveTaskByProcessIdAndDefinitionKey;
import mn.erin.domain.bpm.usecase.task.GetActiveTaskByProcessIdAndDefinitionKeyInput;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;

import static mn.erin.bpm.rest.util.BpmRestUtils.toRestTaskForm;
import static mn.erin.bpms.direct.online.webapp.utils.DirectOnlineBnplRestUtil.deletePreviousProcess;
import static mn.erin.bpms.direct.online.webapp.utils.DirectOnlineBnplRestUtil.extractDanInfo;
import static mn.erin.bpms.direct.online.webapp.utils.DirectOnlineBnplRestUtil.getProcessingStateResponse;
import static mn.erin.bpms.direct.online.webapp.utils.DirectOnlineBnplRestUtil.handleGeneralException;
import static mn.erin.bpms.direct.online.webapp.utils.DirectOnlineBnplRestUtil.returnError;
import static mn.erin.domain.bpm.BpmActivityIdConstants.TASK_DEF_BNPL_ACCEPT;
import static mn.erin.domain.bpm.BpmActivityIdConstants.TASK_DEF_KEY_DIRECT_ONLINE_SET_CONSTANT_VARIABLES;
import static mn.erin.domain.bpm.BpmMessagesConstants.BNPL_LOG;
import static mn.erin.domain.bpm.BpmMessagesConstants.COULD_NOT_FIND_COMPLETED_PROCESS_BY_CIF_IN_LAST_48_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INVOICE_AMOUNT_IS_GREATER_THAN_LOAN_AMOUNT_ERROR_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INVOICE_AMOUNT_IS_GREATER_THAN_LOAN_AMOUNT_ERROR_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INVOICE_NOT_FOUND_ERROR_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PROCESS_REQUEST_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PROCESS_REQUEST_ID_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.REQUEST_BODY_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.REQUEST_BODY_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.TASK_DEF_KEY_NULL_ERROR_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.TASK_DEF_KEY_NULL_ERROR_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.ADDRESS;
import static mn.erin.domain.bpm.BpmModuleConstants.AREA;
import static mn.erin.domain.bpm.BpmModuleConstants.BNPL;
import static mn.erin.domain.bpm.BpmModuleConstants.BNPL_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.DEFAULT_PARAM_ENTITY_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.BNPL_LOAN_DEFAULT_PARAM_PROCESS_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.BRANCH_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CHANNEL;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CURRENT_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.DAN_INFO;
import static mn.erin.domain.bpm.BpmModuleConstants.DAN_REGISTER;
import static mn.erin.domain.bpm.BpmModuleConstants.EMAIL;
import static mn.erin.domain.bpm.BpmModuleConstants.INVOICE_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.INVOICE_AMOUNT_75;
import static mn.erin.domain.bpm.BpmModuleConstants.INVOICE_NUM;
import static mn.erin.domain.bpm.BpmModuleConstants.IS_SALARY_ORGANIZATION;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_CLASS_RANK;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PRODUCT;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PRODUCT_DESCRIPTION;
import static mn.erin.domain.bpm.BpmModuleConstants.LOCALE;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.RETRY_ATTEMPT;
import static mn.erin.domain.bpm.BpmModuleConstants.START;
import static mn.erin.domain.bpm.BpmModuleConstants.STATE;
import static mn.erin.domain.bpm.BpmModuleConstants.TERMINAL_ID;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.CONFIRMED;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.PROCESSING;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.SYSTEM_FAILED;
import static mn.erin.domain.bpm.usecase.process.ProcessUtils.getTaskFormByDefKeyAndProcessId;
import static mn.erin.domain.bpm.usecase.process.ProcessUtils.submitProcessByDefKeyAndProcessId;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;
import static mn.erin.domain.bpm.util.process.DigitalLoanUtils.getProductDescription;
import static mn.erin.domain.bpm.util.process.DigitalLoanUtils.updateProcessParameters;
import static mn.erin.domain.bpm.util.process.DigitalLoanUtils.updateRequestParameters;
import static mn.erin.domain.bpm.util.process.DigitalLoanUtils.updateRequestState;

@RestController
@RequestMapping(value = "/bnpl", name = "Provides BNPL request API")
public class BnplLoanRequestRestApi
{
  private static final Logger LOGGER = LoggerFactory.getLogger(BnplLoanRequestRestApi.class);

  private final AimServiceRegistry aimServiceRegistry;
  private final BpmsServiceRegistry bpmsServiceRegistry;
  private final BpmsRepositoryRegistry bpmsRepositoryRegistry;
  private final ProcessRequestRepository processRequestRepository;
  private final ProcessRepository processRepository;
  private final GroupRepository groupRepository;
  private final TenantIdProvider tenantIdProvider;
  private final ProcessTypeRepository processTypeRepository;
  private final DefaultParameterRepository defaultParameterRepository;
  private final EncryptionService encryptionService;
  private final Environment environment;
  private final ExtSessionInfoCache extSessionInfoCache;
  private final RuntimeService runtimeService;
  private final NewCoreBankingService newCoreBankingService;
  private final TaskFormService formService;

  public BnplLoanRequestRestApi(AimServiceRegistry aimServiceRegistry, BpmsServiceRegistry bpmsServiceRegistry,
      BpmsRepositoryRegistry bpmsRepositoryRegistry, ProcessRequestRepository processRequestRepository,
      ProcessRepository processRepository, GroupRepository groupRepository, TenantIdProvider tenantIdProvider,
      ProcessTypeRepository processTypeRepository, DefaultParameterRepository defaultParameterRepository,
      EncryptionService encryptionService, Environment environment, ExtSessionInfoCache extSessionInfoCache,
      RuntimeService runtimeService, NewCoreBankingService newCoreBankingService, TaskFormService formService)
  {
    this.aimServiceRegistry = aimServiceRegistry;
    this.bpmsServiceRegistry = bpmsServiceRegistry;
    this.bpmsRepositoryRegistry = bpmsRepositoryRegistry;
    this.processRequestRepository = processRequestRepository;
    this.processRepository = processRepository;
    this.groupRepository = groupRepository;
    this.tenantIdProvider = tenantIdProvider;
    this.processTypeRepository = processTypeRepository;
    this.defaultParameterRepository = defaultParameterRepository;
    this.encryptionService = encryptionService;
    this.environment = environment;
    this.extSessionInfoCache = extSessionInfoCache;
    this.runtimeService = runtimeService;
    this.newCoreBankingService = newCoreBankingService;
    this.formService = formService;
  }

  @ApiOperation("Creates BNPL loan request.")
  @PostMapping(value = "loanRequest/create")
  public ResponseEntity<Map<String, Object>> createLoanRequest(@NotNull @RequestBody BnplLoanRequestBody requestBody)
  {
    if (requestBody.getDanInfo() == null)
    {
      returnError(null, "DAN Info is null!");
    }

    try
    {
      checkUserAndLogin();

      Map<String, Serializable> parameters = new HashMap<>();
      CreateProcessRequestOutput output = createBNPLLoanProcess(requestBody, environment.getProperty("directOnline.user.id"), parameters);
      String instanceId = startBNPLLoanProcess(output, requestBody, parameters);

      LOGGER.info("##### BNPL Loan Request" + " Successfully created and started process for user with cif = [{}] and process instance id = [{}]",
          requestBody.getCifNumber(), instanceId);
      Map<String, Object> responseMap = new HashMap<>();
      Map<String, Object> result = new HashMap<>();
      result.put("processInstanceId", instanceId);
      responseMap.put(STATE, "SUCCESS");
      responseMap.put("RESPONSE", result);

      return ResponseEntity.ok(responseMap);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return returnError(e.getCode(), e.getMessage());
    }
  }

  @GetMapping(value = "/getRequestStatus/{cifNumber}")
  public ResponseEntity getBnplRequestStatus(@PathVariable String cifNumber)
  {
    try
    {
      checkUserAndLogin();
      GetBnplRequestStatus getBnplRequestStatus = new GetBnplRequestStatus(bpmsServiceRegistry, bpmsRepositoryRegistry,
          environment, newCoreBankingService);
      return ResponseEntity.ok(getBnplRequestStatus.execute(cifNumber));
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return returnError(e.getCode(), e.getMessage());
    }
  }

  @ApiOperation("Gets active tasks by process request id.")
  @PostMapping(value = "/getActiveTasks")
  public ResponseEntity<RestResult> getActiveTasks(@RequestBody Map<String, String> requestBody) throws UseCaseException
  {

    String processRequestId = requestBody.get("requestId");
    String definitionKey = requestBody.get("definitionKey");

    if (processRequestId == null || StringUtils.isBlank(processRequestId))
    {
      throw new UseCaseException(PROCESS_REQUEST_ID_NULL_CODE, PROCESS_REQUEST_ID_NULL_MESSAGE);
    }
    if (definitionKey == null || StringUtils.isBlank(definitionKey))
    {
      throw new UseCaseException(TASK_DEF_KEY_NULL_ERROR_CODE, TASK_DEF_KEY_NULL_ERROR_MESSAGE);
    }

    ProcessRequest processRequest = processRequestRepository.findById(ProcessRequestId.valueOf(processRequestId));

    String processInstanceId = processRequest.getProcessInstanceId();
    TaskService taskService = bpmsServiceRegistry.getTaskService();

    GetActiveTaskByProcessIdAndDefinitionKeyInput input = new GetActiveTaskByProcessIdAndDefinitionKeyInput(processInstanceId, definitionKey);
    GetActiveTaskByProcessIdAndDefinitionKey getActiveTaskByProcessIdAndDefinitionKey = new GetActiveTaskByProcessIdAndDefinitionKey(taskService);

    List<Task> outputActiveTask = getActiveTaskByProcessIdAndDefinitionKey.execute(input);

    return RestResponse.success(toRestTasks(outputActiveTask));
  }

  @ApiOperation("Gets Form values.")
  @PostMapping(value = "/getFormVariables")
  public ResponseEntity<RestResult> getFormVariables(@RequestBody Map<String, String> reqBody) throws UseCaseException
  {
    TaskForm taskForm = getTaskFormByDefKeyAndProcessId(reqBody.get("processInstanceId"), formService, reqBody.get("taskDefinitionKey"));
    if (taskForm == null)
    {
      return RestResponse.success(null);
    }
    else
    {
      return RestResponse.success(toRestTaskForm(taskForm));
    }
  }

  @ApiOperation("Submit form to runtime service")
  @PostMapping(value = "/submitForm")
  public ResponseEntity<RestResult> submitBpmnForm(@RequestBody Map<String, Object> reqBody) throws UseCaseException
  {
    String taskDefinition = String.valueOf(reqBody.get("taskDefinitionKey"));
    String instanceId = String.valueOf(reqBody.get(PROCESS_INSTANCE_ID));

    submitProcessByDefKeyAndProcessId(instanceId, taskDefinition, bpmsServiceRegistry.getTaskService(),
        bpmsServiceRegistry.getTaskFormService(), runtimeService);
    return RestResponse.success(true);
  }

  @ApiOperation("Continue process by cif")
  @PostMapping(value = "/loanRequest/confirm")
  public ResponseEntity continueProcessByCif(@RequestBody Map<String, Object> request)
  {
    if (null == request)
    {
      return returnError(REQUEST_BODY_NULL_CODE, REQUEST_BODY_NULL_MESSAGE);
    }
    try
    {
      checkUserAndLogin();
      GetLatestRequestByCif getLatestRequestByCif = new GetLatestRequestByCif(processRequestRepository, bpmsRepositoryRegistry, environment);
      Map<String, String> input = new HashMap<>();

      input.put(CIF_NUMBER, getValidString(request.get(CIF_NUMBER)));
      input.put(PROCESS_TYPE_ID, getProcessType());
      input.put("duration", "48");

      ProcessRequest processRequest = getLatestRequestByCif.execute(input);

      if (null != processRequest && !StringUtils.isBlank(processRequest.getProcessInstanceId()) && processRequest.getState() == PROCESSING)
      {
        Map<String, Object> responseMap = getProcessingStateResponse(processRequest.getProcessInstanceId(), runtimeService);
        return ResponseEntity.ok(responseMap);
      }

      if (null == processRequest || StringUtils.isBlank(processRequest.getProcessInstanceId()) || processRequest.getState() != CONFIRMED)
      {
        return returnError("", COULD_NOT_FIND_COMPLETED_PROCESS_BY_CIF_IN_LAST_48_MESSAGE + getValidString(request.get(CIF_NUMBER)));
      }

      String processInstanceId = processRequest.getProcessInstanceId();
      String requestId = String.valueOf(runtimeService.getVariableById(processInstanceId, PROCESS_REQUEST_ID));
      String invoiceNumber = getValidString(request.get("invoiceNum"));

      Map<String, Object> invoiceInfo = getInvoiceInfo(invoiceNumber);

      Map<String, Object> processParameters = processRepository.getProcessParametersByInstanceId(processInstanceId);

      BigDecimal loanAmount = processParameters.containsKey(BNPL_LOAN_AMOUNT) ?
          new BigDecimal(getValidString(processParameters.get(BNPL_LOAN_AMOUNT))) :
          BigDecimal.valueOf(0);

      // getting invoice info
      String invoiceAmountString = getValidString(invoiceInfo.get("sumPrice"));
      String invoiceState = getValidString(invoiceInfo.get("State"));
      BigDecimal invoiceAmount = !StringUtils.isBlank(invoiceAmountString) ? new BigDecimal(invoiceAmountString) : new BigDecimal("0");
      long invoicePercent = (long) runtimeService.getVariableById(processInstanceId, "invoice75Constant");
      BigDecimal invoiceAmount75 = invoiceAmount.multiply(BigDecimal.valueOf(invoicePercent).divide(BigDecimal.valueOf(100)));
      String terminalId = getValidString(invoiceInfo.get("Terminal_ID"));

      // saving invoice amount into process parameter

      runtimeService.setVariable(processInstanceId, INVOICE_NUM, request.get(INVOICE_NUM));
      runtimeService.setVariable(processInstanceId, CURRENT_ACCOUNT_NUMBER, request.get(CURRENT_ACCOUNT_NUMBER));
      runtimeService.setVariable(processInstanceId, INVOICE_AMOUNT_75, invoiceAmount75);
      runtimeService.setVariable(processInstanceId, INVOICE_AMOUNT, invoiceAmount);
      runtimeService.setVariable(processInstanceId, TERMINAL_ID, terminalId);

      saveProcessParameter(requestId, processInstanceId, INVOICE_AMOUNT_75, invoiceAmount75);
      saveProcessParameter(requestId, processInstanceId, INVOICE_NUM, request.get(INVOICE_NUM));
      // validating invoice
      if (invoiceAmount75.compareTo(loanAmount) > 0)
      {
        return returnError(INVOICE_AMOUNT_IS_GREATER_THAN_LOAN_AMOUNT_ERROR_CODE, INVOICE_AMOUNT_IS_GREATER_THAN_LOAN_AMOUNT_ERROR_MESSAGE);
      }
      else
      {
        if (invoiceState.equalsIgnoreCase("CREATED"))
        {
          updateInvoiceState(invoiceNumber);
        }
        else
        {
          return returnError(INVOICE_NOT_FOUND_ERROR_CODE, "Invoice not found with invoice number = " + invoiceNumber + "and CREATED state");
        }
      }

      updateRequestState(processRequestRepository,requestId, PROCESSING);
      callAsyncSubmit(processRequest.getProcessInstanceId(), "confirm");

      Map<String, Object> responseMap = getProcessingStateResponse(processInstanceId, runtimeService);

      return ResponseEntity.ok(responseMap);
    }
    catch (UseCaseException | BpmServiceException e)
    {
      LOGGER.error(e.getMessage(), e);
      return returnError(e.getCode(), e.getMessage());
    }
    catch (Exception e)
    {
      return handleGeneralException(e);
    }
  }

  private void checkUserAndLogin() throws UseCaseException
  {
    final String userId = environment.getProperty("directOnline.user.id");
    final String tenantId = environment.getProperty("directOnline.tenant.id");
    final String password = environment.getProperty("directOnline.user.password");
    if (StringUtils.isBlank(userId))
    {
      RestResponse.internalError("User id is missing!");
      return;
    }

    if (StringUtils.isBlank(tenantId))
    {
      RestResponse.internalError("Tenant id is missing!");
      return;
    }

    if (StringUtils.isBlank(password))
    {
      RestResponse.internalError("password is missing!");
      return;
    }

    String decryptedPassword = encryptionService.decrypt(password);

    LoginUser loginUser = new LoginUser(aimServiceRegistry.getAuthenticationService(), aimServiceRegistry.getMembershipRepository(),
        aimServiceRegistry.getRoleRepository(), encryptionService, extSessionInfoCache);
    LoginUserInput input = new LoginUserInput(tenantId, userId, decryptedPassword);
    input.setKillPreviousSession(false);
    loginUser.execute(input);
  }

  private String startBNPLLoanProcess(CreateProcessRequestOutput createProcessRequestOutput,
      BnplLoanRequestBody request, Map<String, Serializable> createProcessParameters)
      throws UseCaseException
  {
    RestDanEntity danInfo = extractDanInfo(request.getDanInfo());
    Map<String, Object> parameters = new HashMap<>();
    parameters.put(CIF_NUMBER, request.getCifNumber());
    parameters.put("salary", danInfo.getSalary());
    parameters.put(EMAIL, request.getEmail());
    parameters.put(PHONE_NUMBER, request.getPhoneNumber());
    parameters.put(BRANCH_NUMBER, createProcessParameters.get(BRANCH_NUMBER));
    parameters.put(AREA, createProcessParameters.get(BRANCH_NUMBER));
    parameters.put(ADDRESS, request.getAddress());
    parameters.put(IS_SALARY_ORGANIZATION, createProcessParameters.get(IS_SALARY_ORGANIZATION));
    parameters.put(DAN_INFO, request.getDanInfo());

    return startProcess(parameters, createProcessRequestOutput.getProcessRequestId());
  }

  private CreateProcessRequestOutput createBNPLLoanProcess(BnplLoanRequestBody request, String userId,
      Map<String, Serializable> parameters) throws UseCaseException
  {
    /* Extracting DAN Info */
    String branchNumber = getDefaultParameter(BNPL_LOAN_DEFAULT_PARAM_PROCESS_TYPE, DEFAULT_PARAM_ENTITY_NAME, "defaultBranch");
    RestDanEntity danInfo = extractDanInfo(request.getDanInfo());
    CreateProcessRequest createProcessRequest = new CreateProcessRequest(aimServiceRegistry.getAuthenticationService(),
        aimServiceRegistry.getAuthorizationService(), tenantIdProvider, processRequestRepository, groupRepository, processTypeRepository);
    CreateProcessRequestInput input = new CreateProcessRequestInput(branchNumber, userId, request.getProductCategory());
    parameters.put(BRANCH_NUMBER, branchNumber);
    parameters.put(CHANNEL, request.getChannel());
    parameters.put("jobless_members_string", request.getJoblessMembers());
    parameters.put("workspan_string", request.getworkspan());
    parameters.put("sector", request.getBusinessSector());
    parameters.put(ADDRESS, request.getAddress());

    //Loan amount calculation
    parameters.put(LOAN_PRODUCT, getDefaultParameter(BNPL_LOAN_DEFAULT_PARAM_PROCESS_TYPE, DEFAULT_PARAM_ENTITY_NAME, "defaultProduct"));
    parameters.put(LOAN_PRODUCT_DESCRIPTION,
        getProductDescription(bpmsRepositoryRegistry.getProductRepository(), getDefaultParameter(BNPL_LOAN_DEFAULT_PARAM_PROCESS_TYPE, DEFAULT_PARAM_ENTITY_NAME, "defaultProduct")));
    parameters.put(CIF_NUMBER, request.getCifNumber());
    parameters.put(PROCESS_TYPE_ID, request.getProductCategory());

    // Messaging
    parameters.put(PHONE_NUMBER, request.getPhoneNumber());
    parameters.put(LOCALE, request.getLocale());
    parameters.put(EMAIL, request.getEmail());
    parameters.put(DAN_REGISTER, danInfo.getDanRegister());

    input.setParameters(parameters);

    return createProcessRequest.execute(input);
  }

  private String startProcess(Map<String, Object> parameters, String processRequestId) throws UseCaseException
  {
    StartProcess startProcess = new StartProcess(aimServiceRegistry.getAuthenticationService(), aimServiceRegistry.getAuthorizationService(),
        processRequestRepository, bpmsServiceRegistry.getProcessTypeService(), processRepository, bpmsServiceRegistry.getCaseService());
    StartProcessInput startProcessInput = new StartProcessInput(processRequestId);
    startProcessInput.setParameters(parameters);
    StartProcessOutput startProcessOutput = startProcess.execute(startProcessInput);
    String instanceId = startProcessOutput.getProcessInstanceId();

    UpdateRequestState updateRequestState = new UpdateRequestState(processRequestRepository);
    UpdateRequestStateInput input = new UpdateRequestStateInput(processRequestId, ProcessRequestState.STARTED);
    updateRequestState.execute(input);

    callAsyncSubmit(instanceId, START);
    return instanceId;
  }

  private void callAsyncSubmit(String instanceId, String type)
  {
    ExecutorService executor = Executors.newFixedThreadPool(10);
    ProcessRunnable processRunnable = new ProcessRunnable(instanceId, type);
    executor.execute(processRunnable);
    executor.shutdown();
  }

  private Map<String, Object> getInvoiceInfo(String invoiceNum) throws UseCaseException
  {
    GetBnplInvoiceInfo getBnplInvoiceInfo = new GetBnplInvoiceInfo(bpmsServiceRegistry.getBnplCoreBankingService());
    return getBnplInvoiceInfo.execute(invoiceNum);
  }

  private void updateInvoiceState(String invoiceNum) throws UseCaseException
  {
    SetInvoiceStateInput setInvoiceStateInput = new SetInvoiceStateInput(invoiceNum, "NEW");
    SetBnplInvoiceState setBnplInvoiceState = new SetBnplInvoiceState(bpmsServiceRegistry.getBnplCoreBankingService());
    setBnplInvoiceState.execute(setInvoiceStateInput);
  }

  private String getProcessType() throws UseCaseException
  {
    GetProcessTypesByCategory getProcessTypesByCategory = new GetProcessTypesByCategory(bpmsRepositoryRegistry.getProcessTypeRepository());
    GetProcessTypesByCategoryOutput output = getProcessTypesByCategory.execute(BNPL);
    List<ProcessType> processTypes = output.getProcessTypes();
    return String.valueOf(processTypes.get(0).getId().getId());
  }

  private String getDefaultParameter(String processType, String entityType, String parameterName) throws UseCaseException
  {
    GetGeneralInfoInput input = new GetGeneralInfoInput(processType, entityType);
    GetGeneralInfo getGeneralInfo = new GetGeneralInfo(bpmsRepositoryRegistry.getDefaultParameterRepository());
    Map<String, Object> defaultParams = getGeneralInfo.execute(input);

    Map<String, Object> defaultParam = (Map<String, Object>) defaultParams.get(entityType);
    return String.valueOf(defaultParam.get(parameterName));
  }

  private void saveProcessParameter(String requestId, String processInstanceId, String valueName, Object value) throws UseCaseException
  {
    Map<String, Serializable> parameters = new HashMap<>();
    parameters.put(valueName, String.valueOf(value));

    if(!valueName.equals(INVOICE_NUM)){
      updateRequestParameters(aimServiceRegistry, bpmsRepositoryRegistry.getProcessRequestRepository(), requestId, parameters);
    }
    Map<ParameterEntityType, Map<String, Serializable>> processParams = new HashMap<>();
    processParams.put(ParameterEntityType.BNPL, parameters);

    updateProcessParameters(aimServiceRegistry, bpmsRepositoryRegistry.getProcessRepository(), processInstanceId, processParams);
  }

  public class ProcessRunnable implements Runnable
  {
    String instanceId;
    String type;

    public ProcessRunnable(String instanceId, String type)
    {
      this.instanceId = Objects.requireNonNull(instanceId, "Instance Id is null!");
      this.type = type;
    }

    public void run()
    {
      try
      {
        String taskDefinition;
        if (type.equals("confirm"))
        {
          taskDefinition = TASK_DEF_BNPL_ACCEPT;
        }
        else
        {
          taskDefinition = TASK_DEF_KEY_DIRECT_ONLINE_SET_CONSTANT_VARIABLES;
        }
        submitProcessByDefKeyAndProcessId(instanceId, taskDefinition, bpmsServiceRegistry.getTaskService(),
            bpmsServiceRegistry.getTaskFormService(), runtimeService);
      }
      catch (Exception e)
      {
        LOGGER.error(e.getMessage());
        e.printStackTrace();
        runtimeService.setVariable(instanceId, "systemFailCause", e.getMessage());
        retryProcess(instanceId);
      }
    }
  }

  private void retryProcess(String instanceId)
  {
    try
    {
      String cifNumber = String.valueOf(runtimeService.getVariableById(instanceId, CIF_NUMBER));
      Object retryNumber = runtimeService.getVariableById(instanceId, RETRY_ATTEMPT);
      String previousRequestId = String.valueOf(runtimeService.getVariableById(instanceId, PROCESS_REQUEST_ID));

      if (null != retryNumber && Integer.parseInt(String.valueOf(retryNumber)) >= 3)
      {
        updateRequestState(processRequestRepository, previousRequestId, SYSTEM_FAILED);
        LOGGER.error("{} {} FOR CIF = [{}]. PROCESS RETRY ATTEMPT = [{}]", BNPL_LOG, SYSTEM_FAILED, cifNumber, retryNumber);
        return;
      }

      // Set variables to new process
      final List<Variable> variables = this.bpmsServiceRegistry.getCaseService().getVariables(instanceId);
      Map<String, Serializable> serializableParameters = new HashMap<>();
      Map<String, Object> objectParameters = new HashMap<>();
      variables.forEach(variable ->
      {
        serializableParameters.put(variable.getId().getId(), variable.getValue());
        objectParameters.put(variable.getId().getId(), variable.getValue());
      });

      LOGGER.error(BNPL_LOG + " EXCEPTION OCCURRED FOR CIF = [{}]. PROCESS RETRY ATTEMPT = [{}]", cifNumber,
          retryNumber == null ? 1 : retryNumber);

      // 1.Start new process
      String newRequestId = previousRequestId;

      objectParameters.put(PROCESS_REQUEST_ID, newRequestId);
      objectParameters.remove(PROCESS_INSTANCE_ID);
      objectParameters.remove(LOAN_CLASS_RANK);
      final String newInstanceId = startProcess(objectParameters, newRequestId);

      // 2.Set retry number
      if (null == retryNumber)
      {
        retryNumber = 2;
      }
      else
      {
        retryNumber = Integer.parseInt(String.valueOf(retryNumber)) + 1;
      }
      runtimeService.setVariable(newInstanceId, RETRY_ATTEMPT, retryNumber);

      // 3. delete previous process
      deletePreviousProcess(instanceId, previousRequestId);
      //4. terminate and close previous process on camunda
      //      runtimeService.(instanceId);
      //      caseService.closeCases(instanceId);
    }
    catch (Exception e)
    {
      LOGGER.error(BNPL_LOG + "ERROR OCCURRED TO RETRY FAILED PROCESS");
      e.printStackTrace();

      try
      {
        String previousRequestId = String.valueOf(runtimeService.getVariableById(instanceId, PROCESS_REQUEST_ID));
        updateRequestState(processRequestRepository, previousRequestId, SYSTEM_FAILED);
      }
      catch (Exception exception)
      {
        exception.printStackTrace();
      }
    }
  }

  public static List<RestTask> toRestTasks(List<Task> tasks)
  {
    List<RestTask> restTasks = new ArrayList<>();

    for (Task task : tasks)
    {
      if (null != task)
      {
        String id = task.getId().getId();
        String name = task.getName();
        String status = task.getStatus();

        String executionId = task.getExecutionId().getId();
        String instanceId = task.getCaseInstanceId().getId();
        String type = task.getType();
        String parentTaskId = task.getParentTaskId();
        String definitionKey = task.getDefinitionKey();

        restTasks.add(new RestTask(id, executionId, instanceId, name, status, type, parentTaskId, definitionKey));
      }
    }
    return restTasks;
  }
}
