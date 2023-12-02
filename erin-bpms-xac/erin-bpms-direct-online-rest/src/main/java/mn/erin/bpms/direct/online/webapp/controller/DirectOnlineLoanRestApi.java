package mn.erin.bpms.direct.online.webapp.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.inject.Inject;

import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.bpms.direct.online.webapp.model.RestDanEntity;
import mn.erin.bpms.direct.online.webapp.model.RestDirectOnlineSalaryLoanRequest;
import mn.erin.domain.aim.provider.ExtSessionInfoCache;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.service.AimServiceRegistry;
import mn.erin.domain.aim.service.EncryptionService;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.aim.usecase.user.LoginUser;
import mn.erin.domain.aim.usecase.user.LoginUserInput;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.cases.Execution;
import mn.erin.domain.bpm.model.cases.ExecutionId;
import mn.erin.domain.bpm.model.directOnline.DanInfo;
import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.model.process.ProcessType;
import mn.erin.domain.bpm.model.salary.CalculatedSalaryInfo;
import mn.erin.domain.bpm.model.variable.Variable;
import mn.erin.domain.bpm.model.variable.VariableId;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.repository.ProcessTypeRepository;
import mn.erin.domain.bpm.repository.directOnline.DefaultParameterRepository;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.usecase.GetGeneralInfo;
import mn.erin.domain.bpm.usecase.GetGeneralInfoInput;
import mn.erin.domain.bpm.usecase.direct_online.DownloadOrganizationInfo;
import mn.erin.domain.bpm.usecase.direct_online.GetLatestRequestByCif;
import mn.erin.domain.bpm.usecase.direct_online.GetRequestStatus;
import mn.erin.domain.bpm.usecase.execution.GetEnabledExecutions;
import mn.erin.domain.bpm.usecase.execution.GetEnabledExecutionsInput;
import mn.erin.domain.bpm.usecase.execution.GetEnabledExecutionsOutput;
import mn.erin.domain.bpm.usecase.process.CreateProcessRequest;
import mn.erin.domain.bpm.usecase.process.CreateProcessRequestInput;
import mn.erin.domain.bpm.usecase.process.CreateProcessRequestOutput;
import mn.erin.domain.bpm.usecase.process.DeleteProcess;
import mn.erin.domain.bpm.usecase.process.DeleteProcessInput;
import mn.erin.domain.bpm.usecase.process.GetProcessTypesByCategory;
import mn.erin.domain.bpm.usecase.process.GetProcessTypesByCategoryOutput;
import mn.erin.domain.bpm.usecase.process.SaveSalaries;
import mn.erin.domain.bpm.usecase.process.SaveSalariesInput;
import mn.erin.domain.bpm.usecase.process.StartProcess;
import mn.erin.domain.bpm.usecase.process.StartProcessInput;
import mn.erin.domain.bpm.usecase.process.StartProcessOutput;
import mn.erin.domain.bpm.usecase.process.UpdateRequestState;
import mn.erin.domain.bpm.usecase.process.UpdateRequestStateInput;
import mn.erin.domain.bpm.usecase.process.manual_activate.ManualActivate;
import mn.erin.domain.bpm.usecase.process.manual_activate.ManualActivateInput;
import mn.erin.domain.bpm.usecase.task.ChangeProcessTasksStateById;
import mn.erin.domain.bpm.usecase.task.ChangeProcessTasksStateByIdInput;
import mn.erin.infrastucture.rest.common.response.RestResponse;

import static mn.erin.bpms.direct.online.webapp.DirectOnlineLoanConstants.BUSINESS_SECTOR;
import static mn.erin.bpms.direct.online.webapp.DirectOnlineLoanConstants.CHANNEL;
import static mn.erin.bpms.direct.online.webapp.DirectOnlineLoanConstants.CIF_NUMBER;
import static mn.erin.bpms.direct.online.webapp.DirectOnlineLoanConstants.DAN_INFO;
import static mn.erin.bpms.direct.online.webapp.DirectOnlineLoanConstants.FIRST_PAYMENT_DATE;
import static mn.erin.bpms.direct.online.webapp.DirectOnlineLoanConstants.HAS_MORTGAGE;
import static mn.erin.bpms.direct.online.webapp.DirectOnlineLoanConstants.JOBLESS_MEMBERS_STRING;
import static mn.erin.bpms.direct.online.webapp.DirectOnlineLoanConstants.PHONE_NUMBER;
import static mn.erin.bpms.direct.online.webapp.DirectOnlineLoanConstants.RESPONSE;
import static mn.erin.bpms.direct.online.webapp.DirectOnlineLoanConstants.SALARY;
import static mn.erin.bpms.direct.online.webapp.DirectOnlineLoanConstants.WORK_SPAN_STRING;
import static mn.erin.domain.bpm.BpmActivityIdConstants.ACTIVITY_ID_AMOUNT_CALCULATION;
import static mn.erin.domain.bpm.BpmActivityIdConstants.ACTIVITY_ID_CALCULATION_STAGE;
import static mn.erin.domain.bpm.BpmActivityIdConstants.ACTIVITY_ID_MONGOL_BANK;
import static mn.erin.domain.bpm.BpmActivityIdConstants.ACTIVITY_ID_ONLINE_SALARY_ACCEPT_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmActivityIdConstants.ACTIVITY_ID_SALARY_CALCULATION;
import static mn.erin.domain.bpm.BpmActivityIdConstants.ACTIVITY_ID_SCORING;
import static mn.erin.domain.bpm.BpmActivityIdConstants.ACTIVITY_ID_XYP;
import static mn.erin.domain.bpm.BpmActivityIdConstants.TASK_DEF_KEY_DIRECT_ONLINE_SET_CONSTANT_VARIABLES;
import static mn.erin.domain.bpm.BpmActivityIdConstants.TASK_ID_DOWNLOAD_CUSTOMER_INFO;
import static mn.erin.domain.bpm.BpmMessagesConstants.COULD_NOT_FIND_COMPLETED_PROCESS_BY_CIF_IN_LAST_24_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.ONLINE_SALARY_LOG_HASH;
import static mn.erin.domain.bpm.BpmMessagesConstants.REQUEST_BODY_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.REQUEST_BODY_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.ADDRESS;
import static mn.erin.domain.bpm.BpmModuleConstants.AREA;
import static mn.erin.domain.bpm.BpmModuleConstants.AVERAGE_SALARY_AFTER_TAX;
import static mn.erin.domain.bpm.BpmModuleConstants.AVERAGE_SALARY_BEFORE_TAX;
import static mn.erin.domain.bpm.BpmModuleConstants.BRANCH_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CURRENT_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.DAN_REGISTER;
import static mn.erin.domain.bpm.BpmModuleConstants.DAY_OF_PAYMENT;
import static mn.erin.domain.bpm.BpmModuleConstants.DIRECT_ONLINE_PROCESS_TYPE_CATEGORY;
import static mn.erin.domain.bpm.BpmModuleConstants.DIRECT_ONLINE_TENANT_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.DIRECT_ONLINE_USER_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.DIRECT_ONLINE_USER_PASSWORD;
import static mn.erin.domain.bpm.BpmModuleConstants.EMAIL;
import static mn.erin.domain.bpm.BpmModuleConstants.ERATE;
import static mn.erin.domain.bpm.BpmModuleConstants.ERATE_MAX;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ACCEPTED_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.IS_EXCLUDED_HEALTH_INSURANCE;
import static mn.erin.domain.bpm.BpmModuleConstants.IS_EXCLUDED_NIIGMIIN_DAATGAL;
import static mn.erin.domain.bpm.BpmModuleConstants.IS_SALARY_ORGANIZATION;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_CLASS_RANK;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PRODUCT;
import static mn.erin.domain.bpm.BpmModuleConstants.LOCALE;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_SALARY_PROCESS_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PRODUCT_CATEGORY;
import static mn.erin.domain.bpm.BpmModuleConstants.RETRY_ATTEMPT;
import static mn.erin.domain.bpm.BpmModuleConstants.SALARY_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.SALARY_INFO;
import static mn.erin.domain.bpm.BpmModuleConstants.STATE;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.CONFIRMED;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.PROCESSING;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.SYSTEM_FAILED;
import static mn.erin.domain.bpm.usecase.process.ProcessUtils.submitProcessByDefKey;
import static mn.erin.domain.bpm.util.process.BpmUtils.getMnBooleanValue;

/**
 * @author Lkhagvadorj.A
 **/

@RestController
@RequestMapping(value = "/directOnline", name = "Provides BPMS Direct Online loan request API")
public class DirectOnlineLoanRestApi
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DirectOnlineLoanRestApi.class);
  private static final String DEFAULT_PRODUCT = "EB71";

  private static String COULD_NOT_FIND_BY_CIF_MESSAGE = "Could not find process by cif = ";
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

  @Inject
  public DirectOnlineLoanRestApi(AimServiceRegistry aimServiceRegistry, BpmsServiceRegistry bpmsServiceRegistry,
      BpmsRepositoryRegistry bpmsRepositoryRegistry, ProcessRequestRepository processRequestRepository,
      ProcessRepository processRepository, GroupRepository groupRepository,
      TenantIdProvider tenantIdProvider, ProcessTypeRepository processTypeRepository,
      DefaultParameterRepository defaultParameterRepository, EncryptionService encryptionService, Environment environment,
      ExtSessionInfoCache extSessionInfoCache)
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
  }

  @ApiOperation("Creates Direct Online Salary Request")
  @PostMapping(value = "/loanRequests/create")
  public ResponseEntity createDirectOnlineRequest(@RequestBody RestDirectOnlineSalaryLoanRequest request)
  {
    if (null == request)
    {
      return returnError(REQUEST_BODY_NULL_CODE, REQUEST_BODY_NULL_MESSAGE);
    }

    if (null == request.getDanInfo())
    {
      return returnError(null, "DAN Info is empty!");
    }

    try
    {
      checkUserAndLogin();

      Map<String, Serializable> parameters = new HashMap<>();
      CreateProcessRequestOutput output = createDirectOnlineSalaryLoanProcess(request, environment.getProperty(DIRECT_ONLINE_USER_ID), parameters);
      String instanceId = startDirectOnlineSalaryLoanProcess(output, request, parameters);

      LOGGER.info(ONLINE_SALARY_LOG_HASH + " Successfully created and started process for user with cif = [{}] and process instance id = [{}]",
          request.getCifNumber(), instanceId);
      Map<String, Object> responseMap = new HashMap<>();
      Map<String, Object> result = new HashMap<>();
      result.put("processInstanceId", instanceId);
      responseMap.put(STATE, "SUCCESS");
      responseMap.put(RESPONSE, result);

      return ResponseEntity.ok(responseMap);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return returnError(e.getCode(), e.getMessage());
    }
    catch (Exception e)
    {
      return handleGeneralException(e);
    }
  }

  @ApiOperation("Continue process by cif")
  @PostMapping(value = "/loanRequests/confirm")
  public ResponseEntity continueProcessByCif(@RequestBody RestDirectOnlineSalaryLoanRequest request)
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
      input.put(CIF_NUMBER, request.getCifNumber());
      input.put(PROCESS_TYPE_ID, getProcessType());
      input.put("duration", "24");

      ProcessRequest processRequest = getLatestRequestByCif.execute(input);

      if (null != processRequest && !StringUtils.isBlank(processRequest.getProcessInstanceId()) && processRequest.getState() == PROCESSING)
      {
        Map<String, Object> responseMap = getProcessingStateResponse(processRequest.getProcessInstanceId());
        return ResponseEntity.ok(responseMap);
      }

      if (null == processRequest || StringUtils.isBlank(processRequest.getProcessInstanceId()) || processRequest.getState() != CONFIRMED)
      {
        return returnError("", COULD_NOT_FIND_COMPLETED_PROCESS_BY_CIF_IN_LAST_24_MESSAGE + request.getCifNumber());
      }

      boolean isActivated = getEnableProcessAnActivate(processRequest, getConfirmAmountProcessVariables(request), request.getCifNumber(),
          ACTIVITY_ID_ONLINE_SALARY_ACCEPT_LOAN_AMOUNT, true);

      if (!isActivated)
      {
        return returnError("", COULD_NOT_FIND_BY_CIF_MESSAGE + request.getCifNumber());
      }

      String processInstanceId = processRequest.getProcessInstanceId();
      CaseService caseService = bpmsServiceRegistry.getCaseService();
      String requestId = String.valueOf(caseService.getVariableById(processInstanceId, PROCESS_REQUEST_ID));
      updateProcessState(requestId, PROCESSING);

      Map<String, Object> responseMap = getProcessingStateResponse(processInstanceId);

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

  @GetMapping(value = "/getRequestStatus/{cifNumber}")
  public ResponseEntity getRequestStatus(@PathVariable String cifNumber)
  {
    try
    {
      checkUserAndLogin();
      GetRequestStatus getRequestStatus = new GetRequestStatus(defaultParameterRepository, bpmsServiceRegistry, bpmsRepositoryRegistry, environment);
      return ResponseEntity.ok(getRequestStatus.execute(cifNumber));
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return returnError(e.getCode(), e.getMessage());
    }
  }

  @ApiOperation("Get online salary general info")
  @GetMapping("/getGeneralInfo/{processType}/{entity}")
  public ResponseEntity getGeneralInfo(@PathVariable String processType, @PathVariable String entity) throws UseCaseException
  {
    Map<String, Object> responseMap = new HashMap<>();
    try
    {
      GetGeneralInfo getGeneralInfo = new GetGeneralInfo(defaultParameterRepository);
      GetGeneralInfoInput input = new GetGeneralInfoInput(processType, entity);
      Map<String, Object> defaultParameters = getGeneralInfo.execute(input);
      responseMap.put(RESPONSE, defaultParameters);
      return ResponseEntity.ok(responseMap);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return returnError(e.getCode(), e.getMessage());
    }
    catch (Exception e)
    {
      return handleGeneralException(e);
    }
  }

  //  @ApiOperation("Continue process from MB enquire task")
  //  @PostMapping("/loanRequests/startFromMb")
  //  public  ResponseEntity continueProcessFromMb(@RequestBody RestDirectOnlineSalaryLoanRequest request)
  //  {
  //    if (null == request)
  //    {
  //      return returnError(REQUEST_BODY_NULL_CODE, REQUEST_BODY_NULL_MESSAGE);
  //    }
  //
  //    try
  //    {
  //      checkUserAndLogin();
  //      GetLatestRequestByCif getLatestRequestByCif = new GetLatestRequestByCif(processRequestRepository);
  //      ProcessRequest processRequest = getLatestRequestByCif.execute(request.getCifNumber());
  //
  //      if (null == processRequest || StringUtils.isBlank(processRequest.getProcessInstanceId()))
  //      {
  //        return returnError("", "Could not find process by cif = " + request.getCifNumber());
  //      }
  //      LOGGER.info(ONLINE_SALARY_LOG_HASH + " Starting process again from MONGOL BANK task");
  //
  //      List<Variable> variables = new ArrayList<>();
  //      boolean isActivated = getEnableProcessAnActivate(processRequest, variables, request.getCifNumber(), ACTIVITY_ID_ONLINE_SALARY_MONGOL_BANK, true);
  //
  //      if (!isActivated)
  //      {
  //        return returnError("", COULD_NOT_FIND_BY_CIF_MESSAGE + request.getCifNumber());
  //      }
  //
  //      Map<String, Object> responseMap = new HashMap<>();
  //      Map<String, Object> result = new HashMap<>();
  //
  //      result.put("isActivated", isActivated);
  //      result.put("processInstanceId", processRequest.getProcessInstanceId());
  //      responseMap.put(STATE, "SUCCESS");
  //      responseMap.put(RESPONSE, result);
  //      return  ResponseEntity.ok(responseMap);
  //    }
  //    catch (UseCaseException | BpmServiceException e)
  //    {
  //      return  handleGeneralException(e);
  //    }
  //  }
  //
  private void checkUserAndLogin() throws UseCaseException
  {
    final String userId = environment.getProperty(DIRECT_ONLINE_USER_ID);
    final String tenantId = environment.getProperty(DIRECT_ONLINE_TENANT_ID);
    final String password = environment.getProperty(DIRECT_ONLINE_USER_PASSWORD);
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

  private CreateProcessRequestOutput createDirectOnlineSalaryLoanProcess(RestDirectOnlineSalaryLoanRequest request, String userId,
      Map<String, Serializable> parameters) throws UseCaseException
  {
    RestDanEntity danInfo = extractDanInfo(request.getDanInfo());

    DownloadOrganizationInfo downloadOrganizationInfo = new DownloadOrganizationInfo(bpmsServiceRegistry.getDirectOnlineCoreBankingService(),
        defaultParameterRepository);
    Map<String, String> inputParam = new HashMap<>();
    inputParam.put(PROCESS_TYPE_ID, request.getProductCategory());
    inputParam.put(PHONE_NUMBER, request.getPhoneNumber());
    inputParam.put(DAN_REGISTER, danInfo.getDanRegister());
    inputParam.put("district", danInfo.getDistrict());
    inputParam.put(PRODUCT_CATEGORY, request.getProductCategory());
    Map<String, Object> orgInfo = downloadOrganizationInfo.execute(inputParam);
    CreateProcessRequest createProcessRequest = new CreateProcessRequest(aimServiceRegistry.getAuthenticationService(),
        aimServiceRegistry.getAuthorizationService(), tenantIdProvider, processRequestRepository, groupRepository, processTypeRepository);

    CreateProcessRequestInput input = new CreateProcessRequestInput(String.valueOf(orgInfo.get(BRANCH_NUMBER)), userId, request.getProductCategory());
    LOGGER.info(ONLINE_SALARY_LOG_HASH + "BRANCH NUMBER = [{}] for cif number = [{}}", orgInfo.get(BRANCH_NUMBER), request.getCifNumber());
    parameters.put(BRANCH_NUMBER, String.valueOf(orgInfo.get(BRANCH_NUMBER)));
    parameters.put(CHANNEL, request.getChannel());
    parameters.put(BUSINESS_SECTOR, request.getBusinessSector());
    parameters.put(WORK_SPAN_STRING, request.getWorkspan());
    parameters.put(JOBLESS_MEMBERS_STRING, request.getJoblessMembers());
    parameters.put(ADDRESS, request.getAddress());

    // Loan Amount Calculation
    parameters.put(FIRST_PAYMENT_DATE, request.getFirstPaymentDate());
    parameters.put(DAY_OF_PAYMENT, request.getDayOfPayment());
    parameters.put(LOAN_PRODUCT, DEFAULT_PRODUCT);
    parameters.put(CIF_NUMBER, request.getCifNumber());

    parameters.put(PROCESS_TYPE_ID, request.getProductCategory());
    // Messaging
    parameters.put(PHONE_NUMBER, request.getPhoneNumber());
    parameters.put(LOCALE, request.getLocale());
    parameters.put(CURRENT_ACCOUNT_NUMBER, request.getCurrentAccountNumber());
    parameters.put(EMAIL, request.getEmail());
    parameters.put(DAN_REGISTER, danInfo.getDanRegister());

    boolean isSalaryOrganization = (boolean) orgInfo.get(IS_SALARY_ORGANIZATION);
    parameters.put(IS_SALARY_ORGANIZATION, isSalaryOrganization);
    if (isSalaryOrganization)
    {
      parameters.put(ERATE, String.valueOf(orgInfo.get(ERATE)));
      parameters.put(ERATE_MAX, String.valueOf(orgInfo.get(ERATE_MAX)));
    }

    input.setParameters(parameters);

    return createProcessRequest.execute(input);
  }

  private CreateProcessRequestOutput createDirectOnlineSalaryLoanProcess(Map<String, Serializable> variables) throws UseCaseException
  {
    Map<String, Serializable> parameters = new HashMap<>();
    final String userId = aimServiceRegistry.getAuthenticationService().getCurrentUserId();
    CreateProcessRequest createProcessRequest = new CreateProcessRequest(aimServiceRegistry.getAuthenticationService(),
        aimServiceRegistry.getAuthorizationService(), tenantIdProvider, processRequestRepository, groupRepository, processTypeRepository);

    CreateProcessRequestInput input = new CreateProcessRequestInput(String.valueOf(variables.get(BRANCH_NUMBER)), userId, ONLINE_SALARY_PROCESS_TYPE);

    parameters.put(BRANCH_NUMBER, variables.get(BRANCH_NUMBER));
    parameters.put(CHANNEL, variables.get(CHANNEL));
    parameters.put(BUSINESS_SECTOR, variables.get(BUSINESS_SECTOR));
    parameters.put(WORK_SPAN_STRING, variables.get(WORK_SPAN_STRING));
    parameters.put(JOBLESS_MEMBERS_STRING, variables.get(JOBLESS_MEMBERS_STRING));
    parameters.put(ADDRESS, variables.get(ADDRESS));

    // Loan Amount Calculation
    parameters.put(FIRST_PAYMENT_DATE, variables.get(FIRST_PAYMENT_DATE));
    parameters.put(DAY_OF_PAYMENT, variables.get(DAY_OF_PAYMENT));
    parameters.put(LOAN_PRODUCT, DEFAULT_PRODUCT);
    parameters.put(CIF_NUMBER, variables.get(CIF_NUMBER));

    parameters.put(PROCESS_TYPE_ID, variables.get(PROCESS_TYPE_ID));
    // Messaging
    parameters.put(PHONE_NUMBER, variables.get(PHONE_NUMBER));
    parameters.put(LOCALE, variables.get(LOCALE));
    parameters.put(CURRENT_ACCOUNT_NUMBER, variables.get(CURRENT_ACCOUNT_NUMBER));

    boolean isSalaryOrganization = (boolean) variables.get(IS_SALARY_ORGANIZATION);
    parameters.put(IS_SALARY_ORGANIZATION, isSalaryOrganization);
    if (isSalaryOrganization)
    {
      parameters.put(ERATE, variables.get(ERATE));
      parameters.put(ERATE_MAX, variables.get(ERATE_MAX));
    }

    if (null != RETRY_ATTEMPT)
    {
      parameters.put(RETRY_ATTEMPT, variables.get(RETRY_ATTEMPT));
    }

    input.setParameters(parameters);

    return createProcessRequest.execute(input);
  }

  public String startDirectOnlineSalaryLoanProcess(CreateProcessRequestOutput createProcessRequestOutput,
      RestDirectOnlineSalaryLoanRequest request, Map<String, Serializable> createProcessParameters)
      throws UseCaseException
  {
    RestDanEntity danInfo = extractDanInfo(request.getDanInfo());
    Map<String, Object> parameters = new HashMap<>();
    parameters.put(CIF_NUMBER, request.getCifNumber());
    parameters.put(SALARY, danInfo.getSalary());
    parameters.put(DAY_OF_PAYMENT, request.getDayOfPayment());
    parameters.put(EMAIL, request.getEmail());
    parameters.put(PHONE_NUMBER, request.getPhoneNumber());
    parameters.put(BRANCH_NUMBER, createProcessParameters.get(BRANCH_NUMBER));
    parameters.put(AREA, createProcessParameters.get(BRANCH_NUMBER));
    parameters.put(ADDRESS, request.getAddress());
    parameters.put(IS_SALARY_ORGANIZATION, createProcessParameters.get(IS_SALARY_ORGANIZATION));
    parameters.put(DAN_INFO, request.getDanInfo());

    if ((boolean) createProcessParameters.get(IS_SALARY_ORGANIZATION))
    {
      parameters.put(ERATE, createProcessParameters.get(ERATE));
      parameters.put(ERATE_MAX, createProcessParameters.get(ERATE_MAX));
    }

    return startProcess(parameters, createProcessRequestOutput.getProcessRequestId());
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

    callAsyncSubmit(instanceId);
    return instanceId;
  }

  private void retryProcess(String instanceId)
  {
    final CaseService caseService = this.bpmsServiceRegistry.getCaseService();
    try
    {
      String cifNumber = String.valueOf(caseService.getVariableById(instanceId, CIF_NUMBER));
      Object retryNumber = caseService.getVariableById(instanceId, RETRY_ATTEMPT);
      String previousRequestId = String.valueOf(caseService.getVariableById(instanceId, PROCESS_REQUEST_ID));

      if (null != retryNumber && Integer.parseInt(String.valueOf(retryNumber)) >= 3)
      {
        updateProcessState(previousRequestId, SYSTEM_FAILED);
        LOGGER.error(ONLINE_SALARY_LOG_HASH + SYSTEM_FAILED + " FOR CIF = [{}]. PROCESS RETRY ATTEMPT = [{}]", cifNumber, retryNumber);
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

      LOGGER.error(ONLINE_SALARY_LOG_HASH + " EXCEPTION OCCURRED FOR CIF = [{}]. PROCESS RETRY ATTEMPT = [{}]", cifNumber,
          retryNumber == null ? 1 : retryNumber);

      // 1.Start new process
      String newRequestId = previousRequestId;

      objectParameters.put(PROCESS_REQUEST_ID, newRequestId);
      objectParameters.remove(CASE_INSTANCE_ID);
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
      caseService.setCaseVariableById(newInstanceId, RETRY_ATTEMPT, retryNumber);

      // 3. delete previous process
      deletePreviousProcess(instanceId);
      //4. terminate and close previous process on camunda
      caseService.terminateCase(instanceId);
      caseService.closeCases(instanceId);
    }
    catch (Exception e)
    {
      LOGGER.error(ONLINE_SALARY_LOG_HASH + "ERROR OCCURRED TO RETRY FAILED PROCESS");
      e.printStackTrace();

      try
      {
        String previousRequestId = String.valueOf(caseService.getVariableById(instanceId, PROCESS_REQUEST_ID));
        updateProcessState(previousRequestId, SYSTEM_FAILED);
      }
      catch (Exception exception)
      {
        exception.printStackTrace();
      }
    }
  }

  private void deletePreviousProcess(String instanceId) throws UseCaseException
  {
    DeleteProcess deleteProcess = new DeleteProcess(aimServiceRegistry.getAuthenticationService(),
        aimServiceRegistry.getAuthorizationService(), processRepository);
    deleteProcess.execute(new DeleteProcessInput(instanceId));
  }

  private void updateProcessState(String previousRequestId, ProcessRequestState state) throws UseCaseException
  {
    UpdateRequestState updateRequestState = new UpdateRequestState(processRequestRepository);
    UpdateRequestStateInput input = new UpdateRequestStateInput(previousRequestId, state);
    updateRequestState.execute(input);
  }

  private void callAsyncSubmit(String instanceId)
  {
    ExecutorService executor = Executors.newFixedThreadPool(10);
    ProcessRunnable processRunnable = new ProcessRunnable(instanceId);
    executor.execute(processRunnable);
    executor.shutdown();
  }

  private void callAsyncManualActivate(ManualActivate manualActivate, ManualActivateInput manualActivateInput, String processInstanceId)
  {
    ExecutorService executor = Executors.newFixedThreadPool(10);
    ManualActivateRunnable runnable = new ManualActivateRunnable(manualActivate, manualActivateInput, processInstanceId);
    executor.execute(runnable);
    executor.shutdown();
  }

  public class ProcessRunnable implements Runnable
  {
    String instanceId;

    public ProcessRunnable(String instanceId)
    {
      this.instanceId = Objects.requireNonNull(instanceId, "Instance Id is null!");
    }

    public void run()
    {
      try
      {
        submitProcessByDefKey(instanceId, TASK_DEF_KEY_DIRECT_ONLINE_SET_CONSTANT_VARIABLES, bpmsServiceRegistry.getTaskService(),
            bpmsServiceRegistry.getRuntimeService(), bpmsServiceRegistry.getExecutionService(), bpmsServiceRegistry.getTaskFormService(),
            bpmsServiceRegistry.getCaseService());

        CaseService caseService = bpmsServiceRegistry.getCaseService();
        String state = String.valueOf(caseService.getVariableById(instanceId, "pApprove"));
        Object mbApproveObject = caseService.getVariableById(instanceId, "mbApprove");

        if (state.equals("REJECTED"))
        {
          completeRejectedConsumptionLoanTasks(instanceId);
        }
        else if (mbApproveObject != null && !(boolean) mbApproveObject)
        {
          String consumptionLoanInstanceId = String.valueOf(caseService.getVariableById(instanceId, "consumptionLoanInstanceId"));
          String consumptionLoanRequestId = String.valueOf(caseService.getVariableById(instanceId, "consumptionLoanRequestId"));
          caseService.setCaseVariableById(String.valueOf(consumptionLoanInstanceId), PROCESS_REQUEST_ID, consumptionLoanRequestId);
          setSalaryInfoToRejectedProcess(instanceId);
        }
      }
      catch (Exception e)
      {
        LOGGER.error(e.getMessage());
        e.printStackTrace();
        final CaseService caseService = bpmsServiceRegistry.getCaseService();
        caseService.setCaseVariableById(instanceId, "systemFailCause", e.getMessage());
        retryProcess(instanceId);
      }
    }
  }

  public class ManualActivateRunnable implements Runnable
  {
    ManualActivate manualActivate;
    ManualActivateInput manualActivateInput;
    String instanceId;

    public ManualActivateRunnable(ManualActivate manualActivate, ManualActivateInput manualActivateInput, String processInstanceId)
    {
      this.manualActivate = manualActivate;
      this.manualActivateInput = manualActivateInput;
      this.instanceId = processInstanceId;
    }

    public void run()
    {
      try
      {
        manualActivate.execute(manualActivateInput);
        final CaseService caseService = bpmsServiceRegistry.getCaseService();
        String state = String.valueOf(caseService.getVariableById(instanceId, "state"));
        if (state.equals("REJECTED"))
        {
          completeRejectedConsumptionLoanTasks(instanceId);
        }
      }
      catch (Exception e)
      {
        LOGGER.error(e.getMessage());
        e.printStackTrace();
        final CaseService caseService = bpmsServiceRegistry.getCaseService();
        caseService.setCaseVariableById(instanceId, "systemFailCause", e.getMessage());
        retryProcess(instanceId);
      }
    }
  }

  private ResponseEntity returnError(String errorCode, String errorMessage)
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

  private boolean getEnableProcessAnActivate(ProcessRequest processRequest, List<Variable> variables, String cifNumber, String activityId, boolean isASync)
      throws UseCaseException, BpmServiceException
  {
    GetEnabledExecutions getEnabledExecutions = new GetEnabledExecutions(bpmsServiceRegistry.getExecutionService());
    GetEnabledExecutionsInput input = new GetEnabledExecutionsInput(processRequest.getProcessInstanceId());
    GetEnabledExecutionsOutput executionsOutput = getEnabledExecutions.execute(input);

    if (null == executionsOutput || executionsOutput.getExecutions().isEmpty())
    {
      throw new BpmServiceException("Could not find process by cif = " + cifNumber);
    }

    List<Execution> executions = executionsOutput.getExecutions();
    String id = null;
    for (Execution execution : executions)
    {
      if (execution.getActivityId().getId().equals(activityId))
      {
        ExecutionId executionId = execution.getId();
        id = executionId.getId();
      }
    }
    ManualActivate manualActivate = new ManualActivate(aimServiceRegistry.getAuthenticationService(), aimServiceRegistry.getAuthorizationService(),
        bpmsServiceRegistry.getExecutionService());
    ManualActivateInput manualActivateInput = new ManualActivateInput(id, Collections.emptyList(), Collections.emptyList());

    if (!variables.isEmpty())
    {
      manualActivateInput.setVariables(variables);
    }

    if (isASync)
    {
      callAsyncManualActivate(manualActivate, manualActivateInput, processRequest.getProcessInstanceId());
      return true;
    }

    return manualActivate.execute(manualActivateInput);
  }

  private List<Variable> getConfirmAmountProcessVariables(RestDirectOnlineSalaryLoanRequest request)
  {
    List<Variable> variables = new ArrayList<>();
    variables.add(new Variable(VariableId.valueOf("calculateAmount"), false));
    variables.add(new Variable(VariableId.valueOf(FIXED_ACCEPTED_LOAN_AMOUNT), request.getFixedAcceptedLoanAmount()));
    variables.add(new Variable(VariableId.valueOf(CURRENT_ACCOUNT_NUMBER), request.getCurrentAccountNumber()));
    return variables;
  }

  private ResponseEntity handleGeneralException(Exception e)
  {
    LOGGER.error(e.getMessage(), e);
    return returnError(null, e.getMessage());
  }

  private void completeRejectedConsumptionLoanTasks(String processInstanceId) throws BpmServiceException, UseCaseException
  {
    LOGGER.info(ONLINE_SALARY_LOG_HASH + "SETTING [ORG_REJECTED, SCORING_REJECTED, AMOUNT_REJECTED, REJECTED] CONSUMPTION TASKS .....");
    setSalaryInfoToRejectedProcess(processInstanceId);
    CaseService caseService = bpmsServiceRegistry.getCaseService();
    String instanceId = String.valueOf(caseService.getVariableById(processInstanceId, "consumptionLoanInstanceId"));

    List<String> taskIds = new ArrayList<>();
    taskIds.add(TASK_ID_DOWNLOAD_CUSTOMER_INFO);

    List<String> enableProcessIds = new ArrayList<>();
    enableProcessIds.add(ACTIVITY_ID_SALARY_CALCULATION);
    enableProcessIds.add(ACTIVITY_ID_CALCULATION_STAGE);
    enableProcessIds.add(ACTIVITY_ID_SCORING);
    enableProcessIds.add(ACTIVITY_ID_AMOUNT_CALCULATION);

    List<String> disableEnableProcessIds = new ArrayList<>();
    disableEnableProcessIds.add(ACTIVITY_ID_MONGOL_BANK);
    disableEnableProcessIds.add(ACTIVITY_ID_XYP);

    ChangeProcessTasksStateById changeProcessTasksStateById = new ChangeProcessTasksStateById(aimServiceRegistry, bpmsServiceRegistry);
    ChangeProcessTasksStateByIdInput input = new ChangeProcessTasksStateByIdInput(taskIds, enableProcessIds, disableEnableProcessIds,
        String.valueOf(instanceId));
    changeProcessTasksStateById.execute(input);
    LOGGER.info(ONLINE_SALARY_LOG_HASH + "FINISHED TO SET CONSUMPTION TASKS FROM ONLINE SALARY PROCESS");
  }

  private void setSalaryInfoToRejectedProcess(String processInstanceId) throws BpmServiceException, UseCaseException
  {
    CaseService caseService = bpmsServiceRegistry.getCaseService();
    String instanceId = String.valueOf(caseService.getVariableById(processInstanceId, "consumptionLoanInstanceId"));

    caseService.setCaseVariableById(String.valueOf(instanceId), "calculateStageRepeatable", true);
    Map<Date, BigDecimal> salaryInfo = (Map<Date, BigDecimal>) caseService.getVariableById(instanceId, SALARY_INFO);
    BigDecimal averageSalaryBeforeTax;
    if (caseService.getVariableById(instanceId, AVERAGE_SALARY_BEFORE_TAX) instanceof BigDecimal)
    {
      averageSalaryBeforeTax = (BigDecimal) caseService.getVariableById(instanceId, AVERAGE_SALARY_BEFORE_TAX);
    }
    else
    {
      averageSalaryBeforeTax = BigDecimal.valueOf((Double) caseService.getVariableById(instanceId, AVERAGE_SALARY_BEFORE_TAX));
    }
    BigDecimal averageSalaryAfterTax;
    if (caseService.getVariableById(instanceId, AVERAGE_SALARY_AFTER_TAX) instanceof BigDecimal)
    {
      averageSalaryAfterTax = (BigDecimal) caseService.getVariableById(instanceId, AVERAGE_SALARY_AFTER_TAX);
    }
    else
    {
      averageSalaryAfterTax = BigDecimal.valueOf((Double) caseService.getVariableById(instanceId, AVERAGE_SALARY_AFTER_TAX));
    }
    String hasMortgage = String.valueOf(caseService.getVariableById(instanceId, HAS_MORTGAGE));
    String ndsh = String.valueOf(caseService.getVariableById(instanceId, IS_EXCLUDED_NIIGMIIN_DAATGAL));
    String emd = String.valueOf(caseService.getVariableById(instanceId, IS_EXCLUDED_HEALTH_INSURANCE));
    Map<Date, Map<String, BigDecimal>> afterTaxSalaries = (Map<Date, Map<String, BigDecimal>>) caseService.getVariableById(instanceId, "afterTaxSalaries");
    saveSalaryToProcessParameter(instanceId, mapToSalaryInfo(afterTaxSalaries, salaryInfo), averageSalaryBeforeTax, averageSalaryAfterTax,
        getMnBooleanValue(hasMortgage), getMnBooleanValue(ndsh), getMnBooleanValue(emd));
    LOGGER.info(ONLINE_SALARY_LOG_HASH + "FINISHED TO SET CONSUMPTION SALARY INFO FROM ONLINE SALARY PROCESS");
  }

  private void saveSalaryToProcessParameter(String processInstanceId, Map<Date, CalculatedSalaryInfo> salariesInfo, BigDecimal averageBeforeTax,
      BigDecimal averageAfterTax, String hasMortgage, String ndsh, String emd)
      throws UseCaseException
  {
    SaveSalaries saveSalaries = new SaveSalaries(aimServiceRegistry.getAuthenticationService(), aimServiceRegistry.getAuthorizationService(),
        processRepository);
    SaveSalariesInput input = new SaveSalariesInput(processInstanceId, salariesInfo, averageBeforeTax,
        averageAfterTax, hasMortgage, ndsh, emd);
    saveSalaries.execute(input);
  }

  private Map<Date, CalculatedSalaryInfo> mapToSalaryInfo(Map<Date, Map<String, BigDecimal>> salaryInfo,
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

  private RestDanEntity extractDanInfo(List<DanInfo> danList)
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

  private void setDanInfoByMonth(DanInfo danInfo, RestDanEntity restDanEntity)
  {
    restDanEntity.setDanRegister(danInfo.getOrgSiID());
    restDanEntity.setDistrict(danInfo.getDomName());
  }

  private void setDanSalaryInfo(DanInfo danInfo, Map<String, BigDecimal> salary)
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

  private String getDanDateString(DanInfo danInfo)
  {
    int year = danInfo.getYear();
    int month = danInfo.getMonth();
    final LocalDate localDate = LocalDate.of(year, month, 1);
    Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    return date.toString();
  }

  private BigDecimal getDanSalaryAmount(Map<String, Object> danMap)
  {
    double salaryDouble = Double.parseDouble(String.valueOf(danMap.get(SALARY_AMOUNT)));
    return BigDecimal.valueOf(salaryDouble);
  }

  private Map<String, Object> getProcessingStateResponse(String instanceId) throws BpmServiceException
  {
    Map<String, Object> responseMap = new HashMap<>();
    Map<String, Object> result = new HashMap<>();

    CaseService caseService = bpmsServiceRegistry.getCaseService();
    String requestId = String.valueOf(caseService.getVariableById(instanceId, PROCESS_REQUEST_ID));
    String cifNumber = String.valueOf(caseService.getVariableById(instanceId, CIF_NUMBER));
    String branchNumber = String.valueOf(caseService.getVariableById(instanceId, BRANCH_NUMBER));

    result.put(CIF_NUMBER, cifNumber);
    result.put(PROCESS_REQUEST_ID, requestId);
    result.put(BRANCH_NUMBER, branchNumber);
    result.put(STATE, ProcessRequestState.fromEnumToString(PROCESSING));
    responseMap.put(STATE, "SUCCESS");
    responseMap.put(RESPONSE, result);

    return responseMap;
  }
  private String getProcessType() throws UseCaseException
  {
    GetProcessTypesByCategory getProcessTypesByCategory = new GetProcessTypesByCategory(bpmsRepositoryRegistry.getProcessTypeRepository());
    GetProcessTypesByCategoryOutput output = getProcessTypesByCategory.execute(DIRECT_ONLINE_PROCESS_TYPE_CATEGORY);
    List<ProcessType> processTypes = output.getProcessTypes();
    return String.valueOf(processTypes.get(0).getId().getId());
  }
}
