package mn.erin.bpms.direct.online.webapp.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.bpms.direct.online.webapp.DirectOnlineLoanConstants;
import mn.erin.bpms.direct.online.webapp.model.OnlineLeasingRequestBody;
import mn.erin.bpms.direct.online.webapp.model.RestDanEntity;
import mn.erin.bpms.direct.online.webapp.model.RestTask;
import mn.erin.bpms.direct.online.webapp.model.UnionField;
import mn.erin.domain.aim.service.AimServiceRegistry;
import mn.erin.domain.aim.usecase.user.LoginUser;
import mn.erin.domain.aim.usecase.user.LoginUserInput;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.model.task.Task;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.repository.UnionFieldsRepository;
import mn.erin.domain.bpm.repository.directOnline.DefaultParameterRepository;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;
import mn.erin.domain.bpm.service.RuntimeService;
import mn.erin.domain.bpm.usecase.direct_online.DownloadOrganizationInfo;
import mn.erin.domain.bpm.usecase.direct_online.GetLatestRequestByCif;
import mn.erin.domain.bpm.usecase.online_leasing.GetOnlineLeasingRequestStatus;
import mn.erin.domain.bpm.usecase.process.CreateProcessRequest;
import mn.erin.domain.bpm.usecase.process.CreateProcessRequestInput;
import mn.erin.domain.bpm.usecase.process.CreateProcessRequestOutput;
import mn.erin.domain.bpm.usecase.process.DeleteProcess;
import mn.erin.domain.bpm.usecase.process.DeleteProcessInput;
import mn.erin.domain.bpm.usecase.process.StartProcess;
import mn.erin.domain.bpm.usecase.process.StartProcessInput;
import mn.erin.domain.bpm.usecase.process.StartProcessOutput;
import mn.erin.domain.bpm.usecase.process.UpdateRequestState;
import mn.erin.domain.bpm.usecase.process.UpdateRequestStateInput;
import mn.erin.domain.bpm.util.process.BpmUtils;
import mn.erin.infrastucture.rest.common.response.RestResponse;

import static mn.erin.bpms.direct.online.webapp.utils.DirectOnlineBnplRestUtil.extractDanInfo;
import static mn.erin.bpms.direct.online.webapp.utils.DirectOnlineBnplRestUtil.getProcessingStateResponse;
import static mn.erin.bpms.direct.online.webapp.utils.DirectOnlineBnplRestUtil.handleGeneralException;
import static mn.erin.bpms.direct.online.webapp.utils.DirectOnlineBnplRestUtil.returnError;
import static mn.erin.domain.bpm.BpmActivityIdConstants.TASK_DEF_KEY_DIRECT_ONLINE_SET_CONSTANT_VARIABLES;
import static mn.erin.domain.bpm.BpmActivityIdConstants.TASK_DEF_ONLINE_LEASING_ACCEPT;
import static mn.erin.domain.bpm.BpmMessagesConstants.COULD_NOT_CREATE_ORGANIZATION_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.COULD_NOT_FIND_COMPLETED_PROCESS_BY_CIF_IN_LAST_24_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.ONLINE_LEASING_LOG;
import static mn.erin.domain.bpm.BpmMessagesConstants.REQUEST_BODY_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.REQUEST_BODY_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.ADDRESS;
import static mn.erin.domain.bpm.BpmModuleConstants.AREA;
import static mn.erin.domain.bpm.BpmModuleConstants.BRANCH_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CHANNEL;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CURRENT_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.DAN_INFO;
import static mn.erin.domain.bpm.BpmModuleConstants.DAN_REGISTER;
import static mn.erin.domain.bpm.BpmModuleConstants.DEFAULT_PARAM_ENTITY_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.EMAIL;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ACCEPTED_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.IS_SALARY_ORGANIZATION;
import static mn.erin.domain.bpm.BpmModuleConstants.KEY_FIELD_1;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_CLASS_RANK;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PRODUCT;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PRODUCT_DESCRIPTION;
import static mn.erin.domain.bpm.BpmModuleConstants.LOCALE;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_DEFAULT_PARAM_PROCESS_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PRODUCT_CATEGORY;
import static mn.erin.domain.bpm.BpmModuleConstants.RETRY_ATTEMPT;
import static mn.erin.domain.bpm.BpmModuleConstants.START;
import static mn.erin.domain.bpm.BpmModuleConstants.STATE;
import static mn.erin.domain.bpm.BpmModuleConstants.TRACK_NUMBER;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.CIB_FAILED;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.CONFIRMED;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.DISBURSE_FAILED;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.PROCESSING;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.SYSTEM_FAILED;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.fromEnumToString;
import static mn.erin.domain.bpm.usecase.process.ProcessUtils.submitProcessByDefKeyAndProcessId;
import static mn.erin.domain.bpm.util.process.BpmUtils.getDefaultBranch;
import static mn.erin.domain.bpm.util.process.BpmUtils.getDefaultStringParameter;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;
import static mn.erin.domain.bpm.util.process.DigitalLoanUtils.changeChannelAndBranch;
import static mn.erin.domain.bpm.util.process.DigitalLoanUtils.getProductDescription;
import static mn.erin.domain.bpm.util.process.DigitalLoanUtils.updateRequestState;

@RestController
@RequestMapping(
    value = { "/onlineLeasing" }, name = "Provides BPMS online leasing request API"
)

public class OnlineLeasingRestApi
{
  private static final Logger LOGGER = LoggerFactory.getLogger(OnlineLeasingRestApi.class);
  private final AimServiceRegistry aimServiceRegistry;
  private final BpmsServiceRegistry bpmsServiceRegistry;
  private final BpmsRepositoryRegistry bpmsRepositoryRegistry;
  private final Environment environment;
  private final UnionFieldsRepository unionFieldsRepository;

  public OnlineLeasingRestApi(AimServiceRegistry aimServiceRegistry, BpmsServiceRegistry bpmsServiceRegistry, BpmsRepositoryRegistry bpmsRepositoryRegistry,
      Environment environment, UnionFieldsRepository unionFieldsRepository)
  {
    this.aimServiceRegistry = aimServiceRegistry;
    this.bpmsServiceRegistry = bpmsServiceRegistry;
    this.bpmsRepositoryRegistry = bpmsRepositoryRegistry;
    this.environment = environment;
    this.unionFieldsRepository = unionFieldsRepository;
  }

  @ApiOperation("Create Online Leasing Request.")
  @PostMapping("/create")
  public ResponseEntity<Map<String, Object>> createOnlineLeasing(@NotNull @RequestBody OnlineLeasingRequestBody requestBody)
  {
    if (requestBody.getDanInfo() == null)
    {
      returnError(null, "DAN Info is null!");
    }
    try
    {
      checkUserAndLogin();
      Map<String, Serializable> parameters = new HashMap<>();
      CreateProcessRequestOutput output = createOnlineLeasingRequest(requestBody, environment.getProperty("directOnline.user.id"), parameters);
      String instanceId = startOnlineLeasingProcess(output, requestBody, parameters);

      LOGGER.info("##### Online Leasing Request:  Successfully created and started process for user with cif = [{}] and process instance id = [{}]",
          requestBody.getCifNumber(), instanceId);
      LOGGER.info("##### Online Leasing: Create request trackNumber = [{}] and requestNumber = [{}]", requestBody.getUnionFields().getTrackNumber(), output.getProcessRequestId());

      String processRequestId = output.getProcessRequestId();
      UnionField unionFields = requestBody.getUnionFields();
      addUnionFields(unionFields, processRequestId);

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

  @PostMapping (value = "/get-request-status/{cifNumber}")
  public ResponseEntity getOnlineLeasingRequestStatus(@PathVariable String cifNumber, @RequestBody Map<String, Map<String, String>> requestBody)
  {
    try
    {
      String trackNumber = String.valueOf(requestBody.get("unionFields").get("trackNumber"));
      checkUserAndLogin();
      GetOnlineLeasingRequestStatus getOnlineLeasingRequestStatus = new GetOnlineLeasingRequestStatus(bpmsServiceRegistry, bpmsRepositoryRegistry, environment);
      Map<String, String> unionFields = requestBody.get("unionFields");
      addMappedUnionFields(unionFields, "getRequestStatus", null);
      LOGGER.info("{} STATUS CHECK TRACKNUMBER = [{}], WITH CIF NUMBER = [{}]", ONLINE_LEASING_LOG, trackNumber, cifNumber);
      Map<String, Object> input = new HashMap<>();
      input.put(CIF_NUMBER, cifNumber);
      input.put("unionFields", requestBody.get("unionFields"));
      return ResponseEntity.ok(getOnlineLeasingRequestStatus.execute(input));
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return returnError(e.getCode(), e.getMessage());
    }
  }

  @ApiOperation("Confirm Online leasing request.")
  @PostMapping("/confirm")
  public ResponseEntity<Map<String, Object>> confirmOnlineLeasing(@NotNull @RequestBody Map<String, Object> requestBody)
  {
    if (null == requestBody)
    {
      return returnError(REQUEST_BODY_NULL_CODE, REQUEST_BODY_NULL_MESSAGE);
    }
    try
    {
      checkUserAndLogin();
      RuntimeService runtimeService = bpmsServiceRegistry.getRuntimeService();
      String cifNumber = String.valueOf(requestBody.get(CIF_NUMBER));
      Map<String, String> req= (Map<String, String>) requestBody.get("unionFields");
      String trackNumber = String.valueOf(req.get("trackNumber"));
      BigDecimal fixedAcceptedLoanAmount = new BigDecimal(getValidString(requestBody.get(FIXED_ACCEPTED_LOAN_AMOUNT)));
      String currentAccountNumber = String.valueOf(requestBody.get(CURRENT_ACCOUNT_NUMBER));

      GetLatestRequestByCif getLatestRequestByCif = new GetLatestRequestByCif(bpmsRepositoryRegistry.getProcessRequestRepository(), bpmsRepositoryRegistry,
          environment);
      Map<String, String> input = new HashMap<>();
      input.put(CIF_NUMBER, cifNumber);
      input.put(PROCESS_TYPE_ID, "onlineLeasing");
      //todo: get this duration from properties after getRequestStatus service merged
      input.put("duration", "24");
      ProcessRequest processRequest = getLatestRequestByCif.execute(input);
      if (null != processRequest && !StringUtils.isBlank(processRequest.getProcessInstanceId()) && processRequest.getState() == PROCESSING)
      {
        Map<String, Object> responseMap = getProcessingStateResponse(processRequest.getProcessInstanceId(), runtimeService);
        return ResponseEntity.ok(responseMap);
      }
      if (null == processRequest || StringUtils.isBlank(processRequest.getProcessInstanceId()) || processRequest.getState() != CONFIRMED)
      {
        return returnError("", COULD_NOT_FIND_COMPLETED_PROCESS_BY_CIF_IN_LAST_24_MESSAGE + getValidString(requestBody.get(CIF_NUMBER)));
      }
      String processInstanceId = processRequest.getProcessInstanceId();
      runtimeService.setVariable(processInstanceId, "calculateAmount", false);
      runtimeService.setVariable(processInstanceId, FIXED_ACCEPTED_LOAN_AMOUNT, fixedAcceptedLoanAmount);
      runtimeService.setVariable(processInstanceId, CURRENT_ACCOUNT_NUMBER, currentAccountNumber);

      String requestId = String.valueOf(runtimeService.getVariableById(processInstanceId, PROCESS_REQUEST_ID));
      updateRequestState(bpmsRepositoryRegistry.getProcessRequestRepository(), requestId, PROCESSING);
      callAsyncSubmit(processRequest.getProcessInstanceId(), "confirm");

      Map<String, Object> responseMap = getProcessingStateResponse(processInstanceId, runtimeService);
      LOGGER.info("##### Online Leasing: Confirm request trackNumber = [{}] and requestNumber = [{}]", trackNumber, requestId);

      Map<String, String> unionFields = (Map<String, String>) requestBody.get("unionFields");
      addMappedUnionFields(unionFields, "confirmRequest", requestId);

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

  private CreateProcessRequestOutput createOnlineLeasingRequest(OnlineLeasingRequestBody request, String userId, Map<String, Serializable> parameters)
      throws UseCaseException
  {
    DefaultParameterRepository defaultParameterRepository = bpmsRepositoryRegistry.getDefaultParameterRepository();
    RestDanEntity danInfo = extractDanInfo(request.getDanInfo());

    DownloadOrganizationInfo downloadOrganizationInfo = new DownloadOrganizationInfo(this.bpmsServiceRegistry.getDirectOnlineCoreBankingService(),
        this.bpmsRepositoryRegistry.getDefaultParameterRepository());
    Map<String, String> inputParam = new HashMap<>();
    inputParam.put(PROCESS_TYPE_ID, ONLINE_LEASING_PROCESS_TYPE_ID);
    inputParam.put(DirectOnlineLoanConstants.PHONE_NUMBER, String.valueOf(request.getPhoneNumber()));
    inputParam.put(DAN_REGISTER, danInfo.getDanRegister());
    inputParam.put("district", danInfo.getDistrict());
    inputParam.put(PRODUCT_CATEGORY, request.getUnionFields().getProductCategory());
    Map<String, Object> orgInfo = downloadOrganizationInfo.execute(inputParam);

    String choBranch = environment.getProperty("cho.branch.number");

    String branchNumber = (orgInfo.get(BRANCH_NUMBER).equals(choBranch) || orgInfo.get(BRANCH_NUMBER).equals("CHO")) ?
        getValidString(orgInfo.get(BRANCH_NUMBER)) :
        getDefaultBranch(bpmsRepositoryRegistry.getDefaultParameterRepository(), ONLINE_LEASING_DEFAULT_PARAM_PROCESS_TYPE);

    String productCategory = request.getUnionFields().getProductCategory();
    String loanProduct = getDefaultStringParameter(defaultParameterRepository, productCategory, DEFAULT_PARAM_ENTITY_NAME, "defaultProduct");
    CreateProcessRequest createProcessRequest = new CreateProcessRequest(aimServiceRegistry.getAuthenticationService(),
        aimServiceRegistry.getAuthorizationService(), aimServiceRegistry.getTenantIdProvider(), bpmsRepositoryRegistry.getProcessRequestRepository(),
        aimServiceRegistry.getGroupRepository(), bpmsRepositoryRegistry.getProcessTypeRepository());
    CreateProcessRequestInput input = new CreateProcessRequestInput(branchNumber, userId, request.getProcessType());

    parameters.put(BRANCH_NUMBER, branchNumber);
    parameters.put(CHANNEL, request.getChannel());
    parameters.put("jobless_members_string", request.getJoblessMembers());
    parameters.put("workspan_string", request.getWorkspan());
    parameters.put("sector", request.getBusinessSector());
    parameters.put(ADDRESS, request.getAddress());

    //Union fields
    parameters.put(TRACK_NUMBER, request.getUnionFields().getTrackNumber());
    parameters.put(KEY_FIELD_1, request.getUnionFields().getKeyField1());
    parameters.put(PRODUCT_CATEGORY, productCategory);

    //Loan amount calculation
    parameters.put(LOAN_PRODUCT, loanProduct);
    parameters.put(LOAN_PRODUCT_DESCRIPTION, getProductDescription(bpmsRepositoryRegistry.getProductRepository(), loanProduct));
    parameters.put(CIF_NUMBER, request.getCifNumber());
    parameters.put(PROCESS_TYPE_ID, request.getProcessType());

    // Messaging
    parameters.put(PHONE_NUMBER, request.getPhoneNumber());
    parameters.put(LOCALE, request.getLocale());
    parameters.put(EMAIL, request.getEmail());
    parameters.put(DAN_REGISTER, danInfo.getDanRegister());

    input.setParameters(parameters);

    return createProcessRequest.execute(input);
  }

  private String startOnlineLeasingProcess(CreateProcessRequestOutput createProcessRequestOutput, OnlineLeasingRequestBody request,
      Map<String, Serializable> createProcessParameters)
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
    parameters.put(TRACK_NUMBER, request.getUnionFields().getTrackNumber());
    parameters.put(PRODUCT_CATEGORY, request.getUnionFields().getProductCategory());
    parameters.put(KEY_FIELD_1, request.getUnionFields().getKeyField1());
    return startProcess(parameters, createProcessRequestOutput.getProcessRequestId());
  }

  private String startProcess(Map<String, Object> parameters, String processRequestId) throws UseCaseException
  {
    StartProcess startProcess = new StartProcess(aimServiceRegistry.getAuthenticationService(), aimServiceRegistry.getAuthorizationService(),
        bpmsRepositoryRegistry.getProcessRequestRepository(), bpmsServiceRegistry.getProcessTypeService(), bpmsRepositoryRegistry.getProcessRepository(),
        bpmsServiceRegistry.getCaseService());
    StartProcessInput startProcessInput = new StartProcessInput(processRequestId);
    startProcessInput.setParameters(parameters);
    StartProcessOutput startProcessOutput = startProcess.execute(startProcessInput);
    String instanceId = startProcessOutput.getProcessInstanceId();

    UpdateRequestState updateRequestState = new UpdateRequestState(bpmsRepositoryRegistry.getProcessRequestRepository());
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

    String decryptedPassword = aimServiceRegistry.getEncryptionService().decrypt(password);
    LoginUser loginUser = new LoginUser(aimServiceRegistry.getAuthenticationService(), aimServiceRegistry.getMembershipRepository(),
        aimServiceRegistry.getRoleRepository(), aimServiceRegistry.getEncryptionService(), aimServiceRegistry.getExtSessionInfoCache());
    LoginUserInput input = new LoginUserInput(tenantId, userId, decryptedPassword);
    input.setKillPreviousSession(false);
    loginUser.execute(input);
  }

  private void addMappedUnionFields(Map<String, String> unionFields, String requestType, String processRequestId) throws UseCaseException
  {
    String customerNumber = String.valueOf(unionFields.get("custNo"));
    String registerId = String.valueOf(unionFields.get("registerID"));
    String keyField = String.valueOf(unionFields.get("keyField1"));
    String trackNumber = String.valueOf(unionFields.get("trackNumber"));
    String productCategory = String.valueOf(unionFields.get("productCategory"));

    try
    {
      {
        unionFieldsRepository.create(customerNumber, registerId, keyField, trackNumber, productCategory, processRequestId, "onlineLeasing", requestType,
            LocalDateTime.now());
      }
    }
    catch (BpmRepositoryException e)
    {
      throw new UseCaseException(COULD_NOT_CREATE_ORGANIZATION_CODE,
          e.getErrorMessage());
    }
  }

  private void addUnionFields(UnionField unionFields, String processRequestId)  throws UseCaseException
  {
    String customerNumber = String.valueOf(unionFields.getCustNo());
    String registerId = String.valueOf(unionFields.getRegisterID());
    String keyField = String.valueOf(unionFields.getKeyField1());
    String trackNumber = String.valueOf(unionFields.getTrackNumber());
    String productCategory = String.valueOf(unionFields.getProductCategory());

    try
    {
      {
        unionFieldsRepository.create(customerNumber, registerId, keyField, trackNumber, productCategory, processRequestId, "onlineLeasing", "createRequest",
            LocalDateTime.now());
      }
    }
    catch (BpmRepositoryException e)
    {
      throw new UseCaseException(COULD_NOT_CREATE_ORGANIZATION_CODE,
          e.getErrorMessage());
    }
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
          taskDefinition = TASK_DEF_ONLINE_LEASING_ACCEPT;
        }
        else
        {
          taskDefinition = TASK_DEF_KEY_DIRECT_ONLINE_SET_CONSTANT_VARIABLES;
        }
        submitProcessByDefKeyAndProcessId(instanceId, taskDefinition, bpmsServiceRegistry.getTaskService(),
            bpmsServiceRegistry.getTaskFormService(), bpmsServiceRegistry.getRuntimeService());
      }
      catch (Exception e)
      {
        LOGGER.error(e.getMessage());
        e.printStackTrace();
        RuntimeService runtimeService = bpmsServiceRegistry.getRuntimeService();
        runtimeService.setVariable(instanceId, "systemFailCause", e.getMessage());
        Map<String, Object> parameters = bpmsRepositoryRegistry.getProcessRepository().getProcessParametersByInstanceId(instanceId);
        String accountNumber = getValidString(parameters.get(LOAN_ACCOUNT_NUMBER));
        String accountNumberRun = getValidString(runtimeService.getVariableById(instanceId, LOAN_ACCOUNT_NUMBER));
        if(StringUtils.isBlank(accountNumber) && StringUtils.isBlank(accountNumberRun)){
          retryProcess(instanceId, e.getMessage());
        }
        else
        {
          String requestId = String.valueOf(runtimeService.getVariableById(instanceId, PROCESS_REQUEST_ID));
          String branchNumber = String.valueOf(runtimeService.getVariableById(instanceId, BRANCH_NUMBER));
          runtimeService.setVariable(instanceId, STATE, fromEnumToString(DISBURSE_FAILED));
          try
          {
            String defaultBranch = BpmUtils.getDefaultBranchExceptCho(bpmsRepositoryRegistry.getDefaultParameterRepository(), environment, ONLINE_LEASING_PROCESS_TYPE_ID, branchNumber);
            changeChannelAndBranch(bpmsRepositoryRegistry, aimServiceRegistry, environment, requestId, instanceId, ONLINE_LEASING_PROCESS_TYPE_ID, defaultBranch, "Internet bank");
            updateRequestState(bpmsRepositoryRegistry.getProcessRequestRepository(), requestId, DISBURSE_FAILED);
          }
          catch (UseCaseException ex)
          {
            throw new RuntimeException(ex);
          }
        }
      }
    }
  }

  private void retryProcess(String instanceId, String message)
  {
    try
    {
      RuntimeService runtimeService = bpmsServiceRegistry.getRuntimeService();
      String cifNumber = String.valueOf(runtimeService.getVariableById(instanceId, CIF_NUMBER));
      Object retryNumber = runtimeService.getVariableById(instanceId, RETRY_ATTEMPT);
      String previousRequestId = String.valueOf(runtimeService.getVariableById(instanceId, PROCESS_REQUEST_ID));

      if (null != retryNumber && Integer.parseInt(String.valueOf(retryNumber)) >= 3)
      {
        ProcessRequestState processRequestState = SYSTEM_FAILED;
        if (message.contains(ProcessRequestState.fromEnumToString(CIB_FAILED))){
          processRequestState = CIB_FAILED;
        }
        updateRequestState(bpmsRepositoryRegistry.getProcessRequestRepository(), previousRequestId, processRequestState);
        LOGGER.error("{} {} FOR CIF = [{}]. PROCESS RETRY ATTEMPT = [{}]", ONLINE_LEASING_LOG, processRequestState, cifNumber, retryNumber);
        return;
      }

      // Set variables to new process
      Map<String,Object> variables = this.bpmsServiceRegistry.getRuntimeService().getRuntimeVariables(instanceId);

      LOGGER.error(ONLINE_LEASING_LOG + " EXCEPTION OCCURRED FOR CIF = [{}]. PROCESS RETRY ATTEMPT = [{}]", cifNumber,
          retryNumber == null ? 1 : retryNumber);

      // 1.Start new process
      String newRequestId = previousRequestId;

      variables.put(PROCESS_REQUEST_ID, newRequestId);
      variables.remove(PROCESS_INSTANCE_ID);
      variables.remove(LOAN_CLASS_RANK);
      final String newInstanceId = startProcess(variables, newRequestId);

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
      deletePreviousProcess(instanceId);
      //4. terminate and close previous process on camunda
      //      runtimeService.(instanceId);
      //      caseService.closeCases(instanceId);
    }
    catch (Exception e)
    {
      LOGGER.error(ONLINE_LEASING_LOG + "ERROR OCCURRED TO RETRY FAILED PROCESS");
      e.printStackTrace();

      try
      {
        String previousRequestId = String.valueOf(bpmsServiceRegistry.getRuntimeService().getVariableById(instanceId, PROCESS_REQUEST_ID));
        updateRequestState(bpmsRepositoryRegistry.getProcessRequestRepository(), previousRequestId, SYSTEM_FAILED);
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
  private void deletePreviousProcess(String instanceId) throws UseCaseException
  {
    DeleteProcess deleteProcess = new DeleteProcess(aimServiceRegistry.getAuthenticationService(),
        aimServiceRegistry.getAuthorizationService(), bpmsRepositoryRegistry.getProcessRepository());
    deleteProcess.execute(new DeleteProcessInput(instanceId));
  }
}
