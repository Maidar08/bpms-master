/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.rest.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import javax.inject.Inject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
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

import mn.erin.bpm.rest.model.RestCompletedForm;
import mn.erin.bpm.rest.model.RestDeleteCamundaVariable;
import mn.erin.bpm.rest.model.RestServiceTask;
import mn.erin.bpm.rest.model.RestSetExecutionVariables;
import mn.erin.bpm.rest.model.RestTaskFormSubmit;
import mn.erin.bpm.rest.model.RestXacAccount;
import mn.erin.bpm.rest.util.BpmRestUtils;
import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.User;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.provider.ExtSessionInfoCache;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.repository.UserRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.aim.usecase.group.GetGroupUsersByRole;
import mn.erin.domain.aim.usecase.group.GetUsersByRoleInput;
import mn.erin.domain.aim.usecase.group.GetUsersByRoleOutput;
import mn.erin.domain.aim.usecase.membership.GetMembershipOutput;
import mn.erin.domain.aim.usecase.membership.GetUserMembership;
import mn.erin.domain.aim.usecase.membership.GetUserMembershipsInput;
import mn.erin.domain.aim.usecase.user.GetParentGroupUsersByRole;
import mn.erin.domain.aim.usecase.user.GetParentGroupUsersByRoleInput;
import mn.erin.domain.aim.usecase.user.GetParentGroupUsersByRoleOutput;
import mn.erin.domain.aim.usecase.user.GetUsersByRoleAndGivenGroup;
import mn.erin.domain.aim.usecase.user.GetUsersByRoleAndGivenGroupInput;
import mn.erin.domain.aim.usecase.user.GetUsersByRoleAndGivenGroupOutput;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmMessagesConstants;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.exception.BpmInvalidArgumentException;
import mn.erin.domain.bpm.model.account.UDField;
import mn.erin.domain.bpm.model.account.UDFieldValue;
import mn.erin.domain.bpm.model.account.XacAccount;
import mn.erin.domain.bpm.model.contract.LoanContractParameter;
import mn.erin.domain.bpm.model.form.FieldProperty;
import mn.erin.domain.bpm.model.form.FieldValidation;
import mn.erin.domain.bpm.model.form.FormFieldId;
import mn.erin.domain.bpm.model.form.FormFieldValue;
import mn.erin.domain.bpm.model.form.TaskForm;
import mn.erin.domain.bpm.model.form.TaskFormField;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.model.process.Process;
import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.model.variable.Variable;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.repository.LoanContractParameterRepository;
import mn.erin.domain.bpm.repository.LoanContractRequestRepository;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.repository.ProductRepository;
import mn.erin.domain.bpm.repository.TaskFormRepository;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.service.CoreBankingService;
import mn.erin.domain.bpm.service.NewCoreBankingService;
import mn.erin.domain.bpm.service.RuntimeService;
import mn.erin.domain.bpm.service.TaskFormService;
import mn.erin.domain.bpm.service.TaskService;
import mn.erin.domain.bpm.usecase.collateral.GetCollReferenceCodes;
import mn.erin.domain.bpm.usecase.collateral.GetCollReferenceCodesInput;
import mn.erin.domain.bpm.usecase.collateral.GetCollReferenceCodesOutput;
import mn.erin.domain.bpm.usecase.collateral.GetCollateralCodes;
import mn.erin.domain.bpm.usecase.collateral.GetCollateralCodesOutput;
import mn.erin.domain.bpm.usecase.contract.GetLoanContractForm;
import mn.erin.domain.bpm.usecase.contract.GetLoanContractFormById;
import mn.erin.domain.bpm.usecase.contract.LoanContractParameterInput;
import mn.erin.domain.bpm.usecase.contract.UpdateLoanContractParameter;
import mn.erin.domain.bpm.usecase.contract.UpdateLoanContractRequest;
import mn.erin.domain.bpm.usecase.contract.UpdateLoanContractRequestInput;
import mn.erin.domain.bpm.usecase.customer.GetCustomerAccountCreationInfoOutput;
import mn.erin.domain.bpm.usecase.customer.GetUDFieldsByFn;
import mn.erin.domain.bpm.usecase.customer.GetUDFieldsByFnOutput;
import mn.erin.domain.bpm.usecase.customer.GetUDFieldsByProductCode;
import mn.erin.domain.bpm.usecase.customer.GetUDFieldsByProductCodeOutput;
import mn.erin.domain.bpm.usecase.form.case_variable.SetCaseVariables;
import mn.erin.domain.bpm.usecase.form.common.TaskListOutput;
import mn.erin.domain.bpm.usecase.form.get_form_by_form_id.GetFormByFormId;
import mn.erin.domain.bpm.usecase.form.get_form_by_form_id.GetFormByFormIdInput;
import mn.erin.domain.bpm.usecase.form.get_form_by_form_id.GetFormByFormIdOutput;
import mn.erin.domain.bpm.usecase.form.get_form_by_task_id.GetFormByTaskId;
import mn.erin.domain.bpm.usecase.form.get_form_by_task_id.GetFormByTaskIdInput;
import mn.erin.domain.bpm.usecase.form.get_form_by_task_id.GetFormByTaskIdOutput;
import mn.erin.domain.bpm.usecase.form.get_forms_by_case_instance.GetFormsByCaseInstanceId;
import mn.erin.domain.bpm.usecase.form.get_forms_by_case_instance.GetFormsByCaseInstanceIdInput;
import mn.erin.domain.bpm.usecase.form.get_forms_by_definition_id.GetFormsByDefinitionId;
import mn.erin.domain.bpm.usecase.form.get_forms_by_definition_id.GetFormsByDefinitionIdInput;
import mn.erin.domain.bpm.usecase.form.get_forms_by_definition_key.GetFormsByDefinitionKey;
import mn.erin.domain.bpm.usecase.form.get_forms_by_definition_key.GetFormsByDefinitionKeyInput;
import mn.erin.domain.bpm.usecase.form.get_forms_by_process_instance_id.GetFormsByProcessInstanceId;
import mn.erin.domain.bpm.usecase.form.get_forms_by_process_instance_id.GetFormsByProcessInstanceIdInput;
import mn.erin.domain.bpm.usecase.form.runtime_variable.SetVariablesOnActiveTasks;
import mn.erin.domain.bpm.usecase.form.submit_form.SubmitForm;
import mn.erin.domain.bpm.usecase.form.submit_form.SubmitFormInput;
import mn.erin.domain.bpm.usecase.form.submit_form.SubmitFormOutput;
import mn.erin.domain.bpm.usecase.loan.GetAccountsList;
import mn.erin.domain.bpm.usecase.loan.GetAccountsListOutput;
import mn.erin.domain.bpm.usecase.process.DeleteVariableInput;
import mn.erin.domain.bpm.usecase.process.DeleteVariables;
import mn.erin.domain.bpm.usecase.process.GetProcess;
import mn.erin.domain.bpm.usecase.process.GetProcessInput;
import mn.erin.domain.bpm.usecase.process.GetProcessLargeParameter;
import mn.erin.domain.bpm.usecase.process.GetProcessOutput;
import mn.erin.domain.bpm.usecase.process.GetProcessParameterInput;
import mn.erin.domain.bpm.usecase.process.GetProcessParameterOutput;
import mn.erin.domain.bpm.usecase.process.GetProcessRequestByProcessInstanceId;
import mn.erin.domain.bpm.usecase.process.UpdateProcessLargeParameters;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParameters;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParametersInput;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParametersOutput;
import mn.erin.domain.bpm.usecase.process.UpdateRequestParameters;
import mn.erin.domain.bpm.usecase.process.UpdateRequestParametersInput;
import mn.erin.domain.bpm.usecase.process.UpdateRequestParametersOutput;
import mn.erin.domain.bpm.usecase.process.get_variables.GetVariablesById;
import mn.erin.domain.bpm.usecase.process.get_variables.GetVariablesByIdOutput;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;

import static mn.erin.bpm.rest.constant.BpmsControllerConstants.INTERNAL_SERVER_ERROR;
import static mn.erin.bpm.rest.util.BpmRestUtils.jsonToObject;
import static mn.erin.bpm.rest.util.BpmRestUtils.setCollateralCode;
import static mn.erin.bpm.rest.util.BpmRestUtils.setCreateCollateralFieldValue;
import static mn.erin.bpm.rest.util.BpmRestUtils.setFieldByReferenceCodes;
import static mn.erin.bpm.rest.util.BpmRestUtils.toCompletedFormJson;
import static mn.erin.bpm.rest.util.BpmRestUtils.toRestTaskForm;
import static mn.erin.bpm.rest.util.BpmRestUtils.toRestTaskForms;
import static mn.erin.bpm.rest.util.BpmRestUtils.validateCompletedFormInput;
import static mn.erin.domain.bpm.BpmActivityIdConstants.TASK_DEF_KEY_CREATE_IMMOVABLE_COLL;
import static mn.erin.domain.bpm.BpmActivityIdConstants.TASK_DEF_KEY_CREATE_MACHINERY_COLL;
import static mn.erin.domain.bpm.BpmActivityIdConstants.TASK_DEF_KEY_CREATE_OTHER_COLL;
import static mn.erin.domain.bpm.BpmActivityIdConstants.TASK_DEF_KEY_CREATE_VEHICLE_COLL;
import static mn.erin.domain.bpm.BpmActivityIdConstants.TASK_DEF_KEY_CUSTOMER_TRANSACTION_FORM;
import static mn.erin.domain.bpm.BpmActivityIdConstants.TASK_DEF_KEY_DIRECT_ONLINE_CLOSE_ACCOUNT_AND_DISBURSE;
import static mn.erin.domain.bpm.BpmActivityIdConstants.TASK_DEF_KEY_E_TRANSACTION_FORM;
import static mn.erin.domain.bpm.BpmActivityIdConstants.TASK_DEF_KEY_MORTGAGE_GENERATE_LOAN_DECISION;
import static mn.erin.domain.bpm.BpmActivityIdConstants.TASK_DEF_KEY_MORTGAGE_LOAN_SEND;
import static mn.erin.domain.bpm.BpmMessagesConstants.CAMUNDA_TASK_FORM_DOES_NOT_EXIST_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CAMUNDA_TASK_FORM_DOES_NOT_EXIST_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CAMUNDA_TASK_FORM_NOT_EXIST_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.COMPLETED_FORM_NOT_FOUND_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.REST_COMPLETED_FORM_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.REST_COMPLETED_FORM_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.TASK_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.TASK_ID_NULL_ERROR_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.TASK_ID_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.TASK_ID_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.BUSINESS_LOAN_CALCULATION_FIELDS;
import static mn.erin.domain.bpm.BpmModuleConstants.CALCULATE_LOAN_AMOUNT_ACTIVITY_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CITY_FORM_FIELD_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_CODE_FORM_FIELD_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.COLL_TYPE_IMMOVABLE;
import static mn.erin.domain.bpm.BpmModuleConstants.COLL_TYPE_MACHINERY;
import static mn.erin.domain.bpm.BpmModuleConstants.COLL_TYPE_OTHERS;
import static mn.erin.domain.bpm.BpmModuleConstants.COLL_TYPE_VEHICLE;
import static mn.erin.domain.bpm.BpmModuleConstants.CONSUMPTION_LOAN_CALCULATION_FIELDS;
import static mn.erin.domain.bpm.BpmModuleConstants.CO_BORROWER_CONTEXT;
import static mn.erin.domain.bpm.BpmModuleConstants.EB_50_PRODUCT_CODE;
import static mn.erin.domain.bpm.BpmModuleConstants.EB_50_PRODUCT_DESCRIPTION;
import static mn.erin.domain.bpm.BpmModuleConstants.EB_51_PRODUCT_CODE;
import static mn.erin.domain.bpm.BpmModuleConstants.EB_51_PRODUCT_DESCRIPTION;
import static mn.erin.domain.bpm.BpmModuleConstants.EF_50_PRODUCT_CODE;
import static mn.erin.domain.bpm.BpmModuleConstants.EF_50_PRODUCT_DESCRIPTION;
import static mn.erin.domain.bpm.BpmModuleConstants.ELEMENTARY_CRITERIA_ACTIVITY_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.FINANCIAL_LEASING_SUPPLIER;
import static mn.erin.domain.bpm.BpmModuleConstants.FINGER_PRINT_LOWER_CASE;
import static mn.erin.domain.bpm.BpmModuleConstants.FULL_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_CONTRACT_CAMEL_CASE;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PRODUCT;
import static mn.erin.domain.bpm.BpmModuleConstants.MORTGAGE_LOAN_CALCULATION_FIELDS;
import static mn.erin.domain.bpm.BpmModuleConstants.NO_MN_VALUE;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PRODUCT_CODE;
import static mn.erin.domain.bpm.BpmModuleConstants.PURPOSE_OF_USAGE_FIELD_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.RECEIVED_USER;
import static mn.erin.domain.bpm.BpmModuleConstants.RECEIVERS;
import static mn.erin.domain.bpm.BpmModuleConstants.REFERENCE_CODE_COLL_CITY;
import static mn.erin.domain.bpm.BpmModuleConstants.REFERENCE_CODE_COLL_INSPTYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.REFERENCE_CODE_COLL_LEASING_SUPPLIER;
import static mn.erin.domain.bpm.BpmModuleConstants.REFERENCE_CODE_COLL_OWNERTYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.REFERENCE_CODE_COLL_PURPOSE;
import static mn.erin.domain.bpm.BpmModuleConstants.REFERENCE_CODE_COLL_VEHICLE_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.RELATED_USER_TASK_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.REMOVE_CO_BORROWER_ACTIVITY_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.SENT_USER;
import static mn.erin.domain.bpm.BpmModuleConstants.TASK_DEF_CREATE_COLLATERAL;
import static mn.erin.domain.bpm.BpmModuleDataTypeConstants.BIG_DECIMAL;
import static mn.erin.domain.bpm.BpmModuleDataTypeConstants.DOUBLE;
import static mn.erin.domain.bpm.BpmModuleDataTypeConstants.LONG;
import static mn.erin.domain.bpm.BpmModuleDataTypeConstants.STRING_TYPE;
import static mn.erin.domain.bpm.BpmModuleLabelConstants.LABEL_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleLabelConstants.LABEL_LOAN_CATEGORY;
import static mn.erin.domain.bpm.BpmModuleLabelConstants.LABEL_RISKY_CUSTOMER;
import static mn.erin.domain.bpm.BpmUserRoleConstants.BRANCH_DIRECTOR;
import static mn.erin.domain.bpm.BpmUserRoleConstants.BRANCH_SPECIALIST;
import static mn.erin.domain.bpm.BpmUserRoleConstants.HR_SPECIALIST;
import static mn.erin.domain.bpm.BpmUserRoleConstants.HUB_DIRECTOR;
import static mn.erin.domain.bpm.BpmUserRoleConstants.RC_SPECIALIST;
import static mn.erin.domain.bpm.BpmUserRoleConstants.R_ANALYST;
import static mn.erin.domain.bpm.BpmUserRoleConstants.SPECIALIST_ROLES_WITHOUT_HR_SPECIALIST;
import static mn.erin.domain.bpm.model.process.ParameterEntityType.COMPLETED_FORM;
import static mn.erin.domain.bpm.model.process.ParameterEntityType.FORM;
import static mn.erin.domain.bpm.model.process.ParameterEntityType.enumToString;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

/**
 * BPM User task form rest api for managing only CASE related task forms.
 *
 * @author Tamir
 */
@Api
@RestController
@RequestMapping(value = "bpm/task-forms", name = "Provides case task form APIs.")
public class CaseTaskFormRestApi extends BaseBpmsRestApi
{
  private static final Logger LOGGER = LoggerFactory.getLogger(CaseTaskFormRestApi.class);
  public static final String CONTRACT_DATE = "contractDate";

  private final TaskFormService taskFormService;
  private final CaseService caseService;
  private final RuntimeService runtimeService;
  private final TaskService taskService;
  private final CoreBankingService coreBankingService;
  private final NewCoreBankingService newCoreBankingService;

  private final ProcessRequestRepository processRequestRepository;
  private final ProcessRepository processRepository;
  private final TaskFormRepository taskFormRepository;
  private final ProductRepository productRepository;

  private final AuthenticationService authenticationService;
  private final AuthorizationService authorizationService;

  private final MembershipRepository membershipRepository;
  private final UserRepository userRepository;

  private final GroupRepository groupRepository;
  private final TenantIdProvider tenantIdProvider;
  private final LoanContractParameterRepository loanContractParameterRepository;
  private final LoanContractRequestRepository loanContractRequestRepository;
  private final ExtSessionInfoCache extSessionInfoCache;
  private final Environment environment;

  @Inject
  public CaseTaskFormRestApi(BpmsServiceRegistry bpmsServiceRegistry, BpmsRepositoryRegistry bpmsRepositoryRegistry,
      AuthenticationService authenticationService, AuthorizationService authorizationService,
      MembershipRepository membershipRepository, UserRepository userRepository, GroupRepository groupRepository,
      TenantIdProvider tenantIdProvider, ExtSessionInfoCache extSessionInfoCache,
      LoanContractParameterRepository loanContractParameterRepository,
      LoanContractRequestRepository loanContractRequestRepository, Environment environment)
  {
    super(bpmsServiceRegistry, bpmsRepositoryRegistry);

    this.taskFormService = Objects.requireNonNull(bpmsServiceRegistry.getTaskFormService(), "Task form service is required!");
    this.caseService = Objects.requireNonNull(bpmsServiceRegistry.getCaseService(), "Case service is required!");
    this.runtimeService = Objects.requireNonNull(bpmsServiceRegistry.getRuntimeService(), "Runtime service is required!");
    this.taskService = Objects.requireNonNull(bpmsServiceRegistry.getTaskService(), "Task service is required!");
    this.coreBankingService = Objects.requireNonNull(bpmsServiceRegistry.getCoreBankingService(), "Core banking service is required!");
    this.newCoreBankingService = Objects.requireNonNull(bpmsServiceRegistry.getNewCoreBankingService(), "New core banking service is required!");

    this.processRequestRepository = Objects.requireNonNull(bpmsRepositoryRegistry.getProcessRequestRepository(), "Process request repository is required!");
    this.processRepository = Objects.requireNonNull(bpmsRepositoryRegistry.getProcessRepository(), "Process repository is required!");
    this.taskFormRepository = Objects.requireNonNull(bpmsRepositoryRegistry.getTaskFormRepository(), "Task form repository is required!");

    this.productRepository = Objects.requireNonNull(bpmsRepositoryRegistry.getProductRepository(), "Product repository is required!");
    this.authenticationService = Objects.requireNonNull(authenticationService, "Authentication service is required!");
    this.authorizationService = Objects.requireNonNull(authorizationService, "Authorization service is required!");

    this.membershipRepository = Objects.requireNonNull(membershipRepository, "Membership repository is required!");
    this.userRepository = Objects.requireNonNull(userRepository, "User repository is required!");

    this.groupRepository = Objects.requireNonNull(groupRepository, "Group repository is required!");
    this.tenantIdProvider = Objects.requireNonNull(tenantIdProvider, "Tenant id provider is required!");
    this.extSessionInfoCache = extSessionInfoCache;
    this.loanContractParameterRepository = Objects.requireNonNull(loanContractParameterRepository, "Loan contract parameter repository is required!");
    this.loanContractRequestRepository = Objects.requireNonNull(loanContractRequestRepository, "Loan contract request repository is required!");
    this.environment = environment;
  }

  @PostMapping("/save/execution-variables")
  public ResponseEntity<RestResult> setVariables(@RequestBody RestSetExecutionVariables restSetExecutionVariables)
      throws UseCaseException, BpmInvalidArgumentException
  {
    String caseInstanceId = restSetExecutionVariables.getCaseInstanceId();
    String taskId = restSetExecutionVariables.getTaskId();
    Map<String, Object> properties = restSetExecutionVariables.getProperties();

    if (StringUtils.isBlank(caseInstanceId))
    {
      throw new BpmInvalidArgumentException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
    }

    if (StringUtils.isBlank(taskId))
    {
      throw new BpmInvalidArgumentException(TASK_ID_NULL_ERROR_CODE, TASK_ID_NULL_MESSAGE);
    }

    try
    {
      SetCaseVariables setCaseVariables = new SetCaseVariables(caseService);

      SubmitFormInput submitFormInput = new SubmitFormInput(taskId, caseInstanceId, properties);

      boolean isSet = setCaseVariables.execute(submitFormInput);

      return RestResponse.success(isSet);
    }
    catch (RuntimeException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(INTERNAL_SERVER_ERROR + e.getMessage());
    }
  }

  @GetMapping("/formId/{id}")
  public ResponseEntity<RestResult> getByFormId(@PathVariable String id) throws UseCaseException, BpmInvalidArgumentException
  {
    if (StringUtils.isBlank(id))
    {
      String errorCode = "BPMS051";
      throw new BpmInvalidArgumentException(errorCode, "Form id is required, cannot be null or empty!");
    }

    try
    {
      GetFormByFormIdInput input = new GetFormByFormIdInput(id);
      GetFormByFormId getFormByFormId = new GetFormByFormId(taskFormRepository, productRepository);

      GetFormByFormIdOutput output = getFormByFormId.execute(input);
      TaskForm taskForm = output.getTaskForm();

      return RestResponse.success(toRestTaskForm(taskForm));
    }
    catch (RuntimeException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(INTERNAL_SERVER_ERROR + e.getMessage());
    }
  }

  @ApiOperation("Saves completed form as process parameter.")
  @PostMapping("/save/completed-form")
  public ResponseEntity<RestResult> saveCompletedForm(@RequestBody RestCompletedForm restCompletedForm)
      throws UseCaseException, BpmInvalidArgumentException
  {
    if (null == restCompletedForm)
    {
      throw new BpmInvalidArgumentException(REST_COMPLETED_FORM_NULL_CODE, REST_COMPLETED_FORM_NULL_MESSAGE);
    }

    String caseInstanceId = restCompletedForm.getCaseInstanceId();
    String taskId = restCompletedForm.getTaskId();
    String formId = restCompletedForm.getFormId();

    validateCompletedFormInput(caseInstanceId, taskId, formId);

    JSONObject completedFormJson = toCompletedFormJson(restCompletedForm);
    String completedFormAsString = completedFormJson.toString();

    Map<String, Serializable> serializableParams = new HashMap<>();
    serializableParams.put(taskId, completedFormAsString);

    UpdateProcessParametersInput input = new UpdateProcessParametersInput(caseInstanceId,
        Collections.singletonMap(COMPLETED_FORM, serializableParams));

    UpdateProcessLargeParameters updateProcessLargeParameters = new UpdateProcessLargeParameters(authenticationService, authorizationService,
        processRepository);
    UpdateProcessParametersOutput output = updateProcessLargeParameters.execute(input);

    return RestResponse.success(output);
  }

  @ApiOperation("Gets completed form by task id and case instance id.")
  @GetMapping("/completed-form/taskId/{taskId}/caseInstanceId/{caseInstanceId}")
  public ResponseEntity<RestResult> getCompletedForm(@PathVariable String taskId, @PathVariable String caseInstanceId) throws BpmInvalidArgumentException
  {
    if (StringUtils.isBlank(caseInstanceId))
    {
      throw new BpmInvalidArgumentException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
    }

    if (StringUtils.isBlank(taskId))
    {
      throw new BpmInvalidArgumentException(TASK_ID_NULL_CODE, TASK_ID_NULL_MESSAGE);
    }

    GetProcessParameterInput input = new GetProcessParameterInput(caseInstanceId, taskId, COMPLETED_FORM);
    GetProcessLargeParameter getProcessLargeParameter = new GetProcessLargeParameter(processRepository);

    try
    {
      GetProcessParameterOutput output = getProcessLargeParameter.execute(input);
      Serializable parameterValue = output.getParameterValue();

      if (null == parameterValue)
      {
        String messageNotFound = String.format(COMPLETED_FORM_NOT_FOUND_MESSAGE, taskId, caseInstanceId);
        return RestResponse.notFound(messageNotFound);
      }

      return getCompletedFormResponse((String) parameterValue);
    }
    catch (RuntimeException | UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(INTERNAL_SERVER_ERROR + e.getMessage());
    }
  }

  @GetMapping("/taskId/{taskId}/caseInstanceId/{caseInstanceId}/defKey/{defKey}")
  public ResponseEntity<RestResult> getByTaskId(@PathVariable String taskId, @PathVariable String caseInstanceId, @PathVariable String defKey)
      throws UseCaseException, BpmInvalidArgumentException
  {
    if (StringUtils.isBlank(taskId))
    {
      throw new BpmInvalidArgumentException(TASK_ID_NULL_CODE, TASK_ID_NULL_MESSAGE);
    }

    LOGGER.info("########### CASE INSTANCE ID  = [{}]", caseInstanceId);

    GetFormByTaskId useCase = new GetFormByTaskId(taskFormService);
    try
    {
      GetFormByTaskIdInput input = new GetFormByTaskIdInput(caseInstanceId, taskId);
      GetFormByTaskIdOutput output = useCase.execute(input);

      TaskForm taskForm = output.getTaskForm();
      Map<String, Object> tableData = new HashMap<>();

      if (null == taskForm)
      {
        throw new BpmInvalidArgumentException(CAMUNDA_TASK_FORM_NOT_EXIST_CODE, "Task form does not exist with task id: " + taskId);
      }

      Collection<TaskFormField> taskFormFields = taskForm.getTaskFormFields();

      GetProcess getProcess = new GetProcess(authenticationService, authorizationService, processRepository);

      GetProcessOutput getProcessOutput = getProcess.execute(new GetProcessInput(caseInstanceId));
      Process process = getProcessOutput.getReturnedProcess();
      if (null != process)
      {
        getTaskDataFromProcess(taskForm, process, taskFormFields, caseInstanceId);
      }
      else if (!StringUtils.isBlank(defKey))
      {
        GetLoanContractForm getLoanContractForm = new GetLoanContractForm(loanContractParameterRepository, authenticationService, authorizationService);
        LoanContractParameterInput parameterInput = new LoanContractParameterInput(caseInstanceId, defKey);
        LoanContractParameter loanContractParameter = getLoanContractForm.execute(parameterInput);
        if (loanContractParameter != null)
        {
          getTaskDataFromLoanContractForm(taskForm, tableData, loanContractParameter);
        }
        else
        {
          boolean isBranchBankingTransactionForm = taskForm.getTaskDefinitionKey().equalsIgnoreCase(TASK_DEF_KEY_CUSTOMER_TRANSACTION_FORM);
          boolean isBranchBankingETransactionForm = taskForm.getTaskDefinitionKey().equalsIgnoreCase(TASK_DEF_KEY_E_TRANSACTION_FORM);
          for (TaskFormField taskFormField : taskFormFields)
          {
            String fieldId = taskFormField.getId().getId();
            String type = taskFormField.getType();

            if (type.equalsIgnoreCase(DOUBLE) || type.equalsIgnoreCase(LONG))
            {
              taskFormField.setType(BIG_DECIMAL);
            }

            // branch banking task form

            if (isBranchBankingTransactionForm || isBranchBankingETransactionForm)
            {
              setTransactionTaskFormValue(taskFormField, fieldId);
              continue;
            }

            // loan contract task form

            setLoanContractDefaultDate(taskFormField, fieldId);
          }
        }
      }
      if (null == tableData || tableData.isEmpty())
      {
        return RestResponse.success(toRestTaskForm(taskForm));
      }

      return RestResponse.success(toRestTaskForm(taskForm, tableData));
    }
    catch (RuntimeException | BpmServiceException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(INTERNAL_SERVER_ERROR + e.getMessage());
    }
  }

  @GetMapping("/caseInstanceId/{id}")
  public ResponseEntity<RestResult> getByCaseInstanceId(@PathVariable String id) throws UseCaseException, BpmInvalidArgumentException
  {
    if (StringUtils.isBlank(id))
    {
      throw new BpmInvalidArgumentException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
    }

    GetFormsByCaseInstanceId useCase = new GetFormsByCaseInstanceId(taskFormService);
    GetFormsByCaseInstanceIdInput input = new GetFormsByCaseInstanceIdInput(id);

    try
    {
      TaskListOutput output = useCase.execute(input);

      List<TaskForm> taskFormList = output.getTaskFormList();

      if (taskFormList.isEmpty())
      {
        throw new BpmInvalidArgumentException(CAMUNDA_TASK_FORM_DOES_NOT_EXIST_CODE, CAMUNDA_TASK_FORM_DOES_NOT_EXIST_MESSAGE + id);
      }
      TaskForm taskForm = taskFormList.get(0);

      if (null == taskForm)
      {
        throw new BpmInvalidArgumentException(CAMUNDA_TASK_FORM_DOES_NOT_EXIST_CODE, CAMUNDA_TASK_FORM_DOES_NOT_EXIST_MESSAGE);
      }

      String taskFormId = taskForm.getTaskFormId().getId();
      boolean isElementaryCriteriaForm = taskFormId.equalsIgnoreCase(ELEMENTARY_CRITERIA_ACTIVITY_NAME);
      boolean isLoanAmountCalculation = taskFormId.equalsIgnoreCase(CALCULATE_LOAN_AMOUNT_ACTIVITY_NAME);

      if (isElementaryCriteriaForm)
      {
        setCoBorrowerFormFields(id, taskForm.getTaskFormFields());
      }

      TaskForm filledTaskForm = fillFormFieldValues(id, taskForm);

      GetProcess getProcess = new GetProcess(authenticationService, authorizationService, processRepository);
      GetProcessOutput getProcessOutput = getProcess.execute(new GetProcessInput(id));
      Process process = getProcessOutput.getReturnedProcess();
      Map<ParameterEntityType, Map<String, Serializable>> parameters = process.getProcessParameters();

      Collection<TaskFormField> taskFormFields = filledTaskForm.getTaskFormFields();
      for (TaskFormField taskFormField : taskFormFields)
      {
        String fieldId = taskFormField.getId().getId();
        String type = taskFormField.getType();

        if (type.equalsIgnoreCase(DOUBLE) || type.equalsIgnoreCase(LONG))
        {
          taskFormField.setType(BIG_DECIMAL);
        }

        setValueByProcessParameters(fieldId, taskFormField, parameters);
      }
      filledTaskForm.setTaskFormFields(taskFormFields);

      if (isLoanAmountCalculation)
      {
        // sets form fields values from variable value.
        setLoanAmountFieldsFromVariable(id, filledTaskForm.getTaskFormFields());
      }

      return RestResponse.success(toRestTaskForm(filledTaskForm));
    }
    catch (RuntimeException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(INTERNAL_SERVER_ERROR + e.getMessage());
    }
  }

  @GetMapping("/processInstanceId/{id}")
  public ResponseEntity<RestResult> getByProcessInstanceId(@PathVariable String id) throws UseCaseException, BpmInvalidArgumentException
  {
    if (StringUtils.isBlank(id))
    {
      String errorCode = "BPMS015";
      throw new BpmInvalidArgumentException(errorCode, "Case instance id is required, cannot be null or empty!");
    }

    GetFormsByProcessInstanceId useCase = new GetFormsByProcessInstanceId(taskFormService);
    GetFormsByProcessInstanceIdInput input = new GetFormsByProcessInstanceIdInput(id);
    TaskListOutput output = useCase.execute(input);
    List<TaskForm> taskFormList = output.getTaskFormList();

    if (taskFormList.isEmpty())
    {
      String errorCode = "CamundaTasKFormService002";
      throw new BpmInvalidArgumentException(errorCode, "Task forms does not exist with process instance id: " + id);
    }

    return RestResponse.success(toRestTaskForms(taskFormList));
  }

  @GetMapping("/definitionId/{id}")
  public ResponseEntity<RestResult> getByDefinitionId(@PathVariable String id) throws UseCaseException, BpmInvalidArgumentException
  {
    if (StringUtils.isBlank(id))
    {
      String errorCode = "BPMS053";
      throw new BpmInvalidArgumentException(errorCode, "Definition id is required, cannot be null or empty!");
    }

    GetFormsByDefinitionId useCase = new GetFormsByDefinitionId(taskFormService);
    GetFormsByDefinitionIdInput input = new GetFormsByDefinitionIdInput(id);

    try
    {
      TaskListOutput output = useCase.execute(input);

      List<TaskForm> taskFormList = output.getTaskFormList();

      if (taskFormList.isEmpty())
      {
        String errorCode = "CamundaTasKFormService002";
        throw new BpmInvalidArgumentException(errorCode, "Task forms does not exist with definition id: " + id);
      }

      return RestResponse.success(toRestTaskForms(taskFormList));
    }
    catch (RuntimeException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(INTERNAL_SERVER_ERROR + e.getMessage());
    }
  }

  @GetMapping("/definitionKey/{key}")
  public ResponseEntity<RestResult> getByDefinitionKey(@PathVariable String key) throws UseCaseException, BpmInvalidArgumentException
  {
    if (StringUtils.isBlank(key))
    {
      String errorCode = "BPMS053";
      throw new BpmInvalidArgumentException(errorCode, "Definition key is required, cannot be null or empty!");
    }

    GetFormsByDefinitionKey useCase = new GetFormsByDefinitionKey(taskFormService);

    GetFormsByDefinitionKeyInput input = new GetFormsByDefinitionKeyInput(key);

    try
    {
      TaskListOutput output = useCase.execute(input);

      List<TaskForm> taskFormList = output.getTaskFormList();

      if (taskFormList.isEmpty())
      {
        String errorCode = "CamundaCaseService002";
        throw new BpmInvalidArgumentException(errorCode, "Task forms does not exist with definition key: " + key);
      }

      return RestResponse.success(toRestTaskForms(taskFormList));
    }
    catch (RuntimeException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(INTERNAL_SERVER_ERROR + e.getMessage());
    }
  }

  @GetMapping("/getTaskState/processInstanceId/{processInstanceId}")
  public ResponseEntity<RestResult> getTaskState(@PathVariable String processInstanceId)
  {
    List<Map<String, String>> completedTasks = (List<Map<String, String>>) runtimeService.getVariableById(processInstanceId, "completedTasks");
    List<RestServiceTask> taskList = new ArrayList<>();
    for (int i = 0; i < completedTasks.size(); i++)
    {
      RestServiceTask restServiceTask = new RestServiceTask();
      for (HashMap.Entry<String, String> entry : completedTasks.get(i).entrySet())
      {
        restServiceTask.setTaskName(entry.getKey());
        restServiceTask.setTaskState(entry.getValue());
      }
      taskList.add(restServiceTask);
    }
    return RestResponse.success(taskList);
  }

  @PostMapping("/submit")
  public ResponseEntity<RestResult> submitForm(@RequestBody RestTaskFormSubmit taskFormSubmit)
      throws UseCaseException, BpmInvalidArgumentException, BpmServiceException
  {
    if (null == taskFormSubmit)
    {
      String errorCode = "BPMS023";
      throw new BpmInvalidArgumentException(errorCode, "Submit task form rest entity is required!");
    }

    String taskId = taskFormSubmit.getTaskId();
    String caseInstanceId = taskFormSubmit.getCaseInstanceId();

    String processRequestId = taskFormSubmit.getProcessRequestId();
    Map<String, Object> properties = taskFormSubmit.getProperties();

    String processTypeId = String.valueOf(caseService.getVariableById(caseInstanceId, "processTypeId"));

    if (processTypeId.contains(LOAN_CONTRACT_CAMEL_CASE))
    {
      final SubmitFormOutput output = submitLoanContract(caseInstanceId, taskId, properties, taskFormSubmit.getDefKey());
      return RestResponse.success(output);
    }

    if (StringUtils.isBlank(processRequestId))
    {
      GetProcessRequestByProcessInstanceId getProcessRequestByProcessInstanceId = new GetProcessRequestByProcessInstanceId(
          authenticationService, authorizationService, processRequestRepository, loanContractRequestRepository);
      ProcessRequest processRequest = getProcessRequestByProcessInstanceId.execute(caseInstanceId);
      updateProcessRequestParams(processRequest.getId().getId(), properties);
    }
    else
    {
      updateProcessRequestParams(processRequestId, properties);
    }

    Map<String, Object> filteredProperties = filterFromFingerPrint(properties);

    // Updates BPMS Process parameters.
    Map<ParameterEntityType, Map<String, Serializable>> formParameters = new HashMap<>();
    Map<String, Serializable> serializableMap = BpmRestUtils.objectMapToSerializableMap(filteredProperties);

    SubmitForm useCase = new SubmitForm(taskFormService, caseService);
    SubmitFormInput submitInput = new SubmitFormInput(taskId, caseInstanceId, properties);
    properties.put("reCalculated", false);

    SubmitFormInput setVariablesInput = new SubmitFormInput(taskId, caseInstanceId, filteredProperties);

    SetVariablesOnActiveTasks setVariablesOnActiveTasks = new SetVariablesOnActiveTasks(taskService, runtimeService);
    setVariablesOnActiveTasks.execute(setVariablesInput);

    SubmitFormOutput output = useCase.execute(submitInput);

    LOGGER.info("######### UPDATES FORM PARAMETERS TO PROCESS PARAMETER TABLE WHEN SUBMIT TASK, REQUEST ID = [{}]", processRequestId);
    formParameters.put(FORM, serializableMap);

    updateProcessParameter(caseInstanceId, formParameters);

    String relatedUserTaskId = checkRelatedUserTaskId(caseInstanceId);
    if (null != relatedUserTaskId)
    {
      output.setRelatedUserTaskId(relatedUserTaskId);
    }

    return RestResponse.success(output);
  }

  @PostMapping("/submit/call/user-task")
  public ResponseEntity<RestResult> submitThenCallUSerTask(@RequestBody RestTaskFormSubmit taskFormSubmit)
      throws UseCaseException, BpmInvalidArgumentException
  {
    if (null == taskFormSubmit)
    {
      String errorCode = "BPMS023";
      throw new BpmInvalidArgumentException(errorCode, "Submit task form rest entity is required!");
    }

    String taskId = taskFormSubmit.getTaskId();
    String caseInstanceId = taskFormSubmit.getCaseInstanceId();

    Map<String, Object> properties = taskFormSubmit.getProperties();

    SubmitForm useCase = new SubmitForm(taskFormService, caseService);
    SubmitFormInput input = new SubmitFormInput(taskId, caseInstanceId, properties);
    SubmitFormOutput output = useCase.execute(input);

    return RestResponse.success(output);
  }

  @PostMapping("/delete-variables/{caseInstanceId}/{contextType}")
  public ResponseEntity<RestResult> deleteVariables(@PathVariable String caseInstanceId, @PathVariable String contextType,
      @RequestBody RestDeleteCamundaVariable restDeleteCamundaVariable)
      throws UseCaseException, BpmInvalidArgumentException
  {
    if (StringUtils.isBlank(caseInstanceId))
    {
      String errorCode = "BPMS013";
      throw new BpmInvalidArgumentException(errorCode, "Process instance id is required!!");
    }

    if (null == restDeleteCamundaVariable)
    {
      String errorCode = "BPMS047";
      throw new BpmInvalidArgumentException(errorCode, "Request body is null!");
    }

    if (contextType.equals(CO_BORROWER_CONTEXT))
    {
      DeleteVariableInput deleteVariableInput = new DeleteVariableInput(caseInstanceId, contextType, restDeleteCamundaVariable.getIds());
      DeleteVariables deleteVariables = new DeleteVariables(authenticationService, authorizationService, caseService);
      deleteVariables.execute(deleteVariableInput);
    }

    return RestResponse.success();
  }

  @GetMapping("/accounts-list/{caseInstanceId}")
  public ResponseEntity<RestResult> getAccountsList(@PathVariable String caseInstanceId) throws UseCaseException
  {
    GetAccountsList getAccountsList = new GetAccountsList(newCoreBankingService);
    Map<String, String> accountCreationInformationVariables = getAccountCreationInformationVariables(caseInstanceId);
    Map<String, String> input = new HashMap<>();
    input.put(REGISTER_NUMBER, accountCreationInformationVariables.get("regNo"));
    input.put(CIF_NUMBER, accountCreationInformationVariables.get("CIF"));
    input.put(PROCESS_TYPE_ID, accountCreationInformationVariables.get(PROCESS_TYPE_ID));
    input.put(PHONE_NUMBER, accountCreationInformationVariables.get(PHONE_NUMBER));
    GetAccountsListOutput output = getAccountsList.execute(input);

    List<XacAccount> xacAccounts = output.getAccountList();

    List<RestXacAccount> restXacAccounts = new ArrayList<>();

    for (XacAccount xacAccount : xacAccounts)
    {
      restXacAccounts.add(RestXacAccount.of(xacAccount));
    }

    return RestResponse.success(restXacAccounts);
  }

  @GetMapping("/ud-fields-by-function/{function}")
  public ResponseEntity<RestResult> getUDFormFieldsByFunction(@PathVariable String function) throws UseCaseException, BpmInvalidArgumentException
  {
    if (StringUtils.isBlank(function))
    {
      throw new BpmInvalidArgumentException(BpmMessagesConstants.UD_FIELD_BY_FN_USECASE_EMPTY_FUNCTION_ERROR_CODE,
          BpmMessagesConstants.UD_FIELD_BY_FN_USECASE_EMPTY_FUNCTION_ERROR_MESSAGE);
    }

    GetUDFieldsByFn getUdfFieldsByFn = new GetUDFieldsByFn(coreBankingService);
    GetUDFieldsByFnOutput getUdfFieldsByFnOutput = getUdfFieldsByFn.execute(function);
    Map<String, UDField> udFieldMap = getUdfFieldsByFnOutput.getUdFieldMap();

    return RestResponse.success(udFieldMap);
  }

  @GetMapping("/loan-contract/get-form-by-id/{id}")
  public ResponseEntity<RestResult> getById(@PathVariable String id)
  {
    try
    {
      GetLoanContractFormById getLoanContractFormById = new GetLoanContractFormById(loanContractParameterRepository,
          authenticationService, authorizationService);
      return RestResponse.success(getLoanContractFormById.execute(id));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(INTERNAL_SERVER_ERROR + e.getMessage());
    }
  }

  private void getTaskDataFromLoanContractForm(TaskForm taskForm, Map<String, Object> tableData,
      LoanContractParameter loanContractParameter)
  {
    Collection<TaskFormField> taskFormFields = taskForm.getTaskFormFields();
    Map<String, Object> parameterValue = loanContractParameter.getParameterValue();
    final Map<String, Object> tableParam = loanContractParameter.getTableData();
    if (null != tableParam && !tableParam.isEmpty())
    {
      tableData.putAll(tableParam);
    }

    for (TaskFormField formField : taskFormFields)
    {
      String id = formField.getId().getId();
      if (parameterValue.containsKey(id))
      {
        formField.getFormFieldValue().setDefaultValue(parameterValue.get(id));
        continue;
      }

      setLoanContractDefaultDate(formField, id);
    }
  }

  private void getTaskDataFromProcess(TaskForm taskForm, Process process, Collection<TaskFormField> taskFormFields, String caseInstanceId)
      throws BpmServiceException, UseCaseException
  {
    Map<ParameterEntityType, Map<String, Serializable>> parameters = process.getProcessParameters();

    String taskFormId = taskForm.getTaskFormId().getId();
    boolean isDecisionForm = taskFormId.contains("ZSH") || taskFormId.contains("Зээл шийдвэрлэх") || taskFormId.contains("ZSI");
    boolean isAccountCreationFormCollateral = isLoanAccountCreationCollateralTask(taskFormId);

    boolean isElementaryCriteriaForm = taskFormId.equalsIgnoreCase(ELEMENTARY_CRITERIA_ACTIVITY_NAME);
    boolean isRemoveCoBorrowerForm = taskFormId.equalsIgnoreCase(REMOVE_CO_BORROWER_ACTIVITY_NAME);

    boolean isLoanAmountCalculation = taskFormId.equalsIgnoreCase(CALCULATE_LOAN_AMOUNT_ACTIVITY_NAME);
    boolean isGenerateLoanDecision = taskForm.getTaskDefinitionKey().equalsIgnoreCase(TASK_DEF_KEY_MORTGAGE_GENERATE_LOAN_DECISION);

    boolean isCreateImmovableCollateral = taskForm.getTaskDefinitionKey().equalsIgnoreCase(TASK_DEF_KEY_CREATE_IMMOVABLE_COLL);
    boolean isCreateVehicleCollateral = taskForm.getTaskDefinitionKey().equalsIgnoreCase(TASK_DEF_KEY_CREATE_VEHICLE_COLL);
    boolean isCreateMachineryCollateral = taskForm.getTaskDefinitionKey().equalsIgnoreCase(TASK_DEF_KEY_CREATE_MACHINERY_COLL);
    boolean isCreateOtherCollateral = taskForm.getTaskDefinitionKey().equalsIgnoreCase(TASK_DEF_KEY_CREATE_OTHER_COLL);
    // Handle Loan closure and disbursement error task
    boolean isCloseAndDisbursement = taskForm.getTaskDefinitionKey().equalsIgnoreCase(TASK_DEF_KEY_DIRECT_ONLINE_CLOSE_ACCOUNT_AND_DISBURSE);
    if (isElementaryCriteriaForm || isRemoveCoBorrowerForm)
    {
      setCoBorrowerFormFields(caseInstanceId, taskFormFields);
    }

    String role = null;
    if (isDecisionForm)
    {
      role = getRole();
    }

    GetAccountsListOutput getAccountsListOutput = null;
    GetUDFieldsByProductCodeOutput getUDFieldsByProductCodeOutput = null;
    if (isAccountCreationFormCollateral)
    {
      Map<String, String> accountCreationInformationVariables = getAccountCreationInformationVariables(caseInstanceId);
      Map<String, String> input = new HashMap<>();
      input.put(REGISTER_NUMBER, accountCreationInformationVariables.get("regNo"));
      input.put(CIF_NUMBER, accountCreationInformationVariables.get("CIF"));
      input.put(PROCESS_TYPE_ID, accountCreationInformationVariables.get(PROCESS_TYPE_ID));
      input.put(PHONE_NUMBER, accountCreationInformationVariables.get(PHONE_NUMBER));
      input.put(PRODUCT_CODE, accountCreationInformationVariables.get(PRODUCT_CODE));
      getUDFieldsByProductCodeOutput = getUDFieldsByProductCode(input);

      GetAccountsList getAccountsList = new GetAccountsList(newCoreBankingService);
      getAccountsListOutput = getAccountsList.execute(input);
    }

    // It relates to create collateral task
    boolean haveValueInProcessParameter =
        !taskForm.getTaskDefinitionKey().equals(TASK_DEF_CREATE_COLLATERAL) || hasCollateralParameters(taskFormFields, parameters);

    if (taskForm.getTaskDefinitionKey().equals(TASK_DEF_KEY_CREATE_IMMOVABLE_COLL) ||
        taskForm.getTaskDefinitionKey().equals(TASK_DEF_KEY_CREATE_VEHICLE_COLL) || isCreateMachineryCollateral || isCreateOtherCollateral)
    {
      haveValueInProcessParameter = false;
    }

    for (TaskFormField taskFormField : taskFormFields)
    {
      String fieldId = taskFormField.getId().getId();
      String type = taskFormField.getType();
      if (isCloseAndDisbursement)
      {
        setTextAreaField(taskFormField);
      }
      if (isCreateImmovableCollateral)
      {
        setImmovableCollFormInfos(taskFormField);
      }

      if (isCreateMachineryCollateral)
      {
        setMachineryCollFormInfos(taskFormField);
      }

      if (isCreateVehicleCollateral)
      {
        setVehicleCollateralInfos(taskFormField);
      }
      if (isCreateOtherCollateral)
      {
        setOtherCollateralInfos(taskFormField);
      }

      if (isDecisionForm)
      {
        setLoanDecisionReceivers(taskFormField, role, taskForm.getTaskDefinitionKey());
        setLoanDecisionReceivedUser(taskForm, taskFormField, role);

        setTextAreaField(taskFormField);
      }

      if (isGenerateLoanDecision)
      {
        setTextAreaField(taskFormField);
      }

      if (isAccountCreationFormCollateral)
      {
        setLoanAccountCreationProperties(taskFormField, getUDFieldsByProductCodeOutput);
        setAccountNumbers(taskFormField, getAccountsListOutput);
        setTextAreaField(taskFormField);
      }

      if (type.equalsIgnoreCase(DOUBLE) || type.equalsIgnoreCase(LONG))
      {
        taskFormField.setType(BIG_DECIMAL);
      }

      // check it so that new generated collateral won't get previous collateral's info
      if (haveValueInProcessParameter)
      {
        setValueByProcessParameters(fieldId, taskFormField, parameters);
      }

      String countString = getValidString(caseService.getVariableById(caseInstanceId, "salaryCalculationCount"));
      String amountCalculationCount = getValidString(caseService.getVariableById(caseInstanceId, "amountCalculationCount"));
      String businessCalculationCount = getValidString(caseService.getVariableById(caseInstanceId, "businessCalculationCount"));
      String mortgageCalculationCount = getValidString(caseService.getVariableById(caseInstanceId, "mortgageCalculationCount"));

      Object reCalculated = caseService.getVariableById(caseInstanceId, "reCalculated");

      String[] list = new String[0];
      if (reCalculated != null && Boolean.parseBoolean(getValidString(reCalculated)))
      {
        if (StringUtils.isNotBlank(countString) && Integer.parseInt(countString) > 1)
        {
          list = CONSUMPTION_LOAN_CALCULATION_FIELDS;
        }
        else if (StringUtils.isNotBlank(amountCalculationCount) && Integer.parseInt(amountCalculationCount) > 1)
        {
          list = new String[] { "collateralAmount", "LTVField", "loanApprovalAmount", "lackedAmountField" };
        }
        else if (StringUtils.isNotBlank(businessCalculationCount) && Integer.parseInt(businessCalculationCount) > 1)
        {
          list = BUSINESS_LOAN_CALCULATION_FIELDS;
        }
        else if (StringUtils.isNotBlank(mortgageCalculationCount))
        {
          list = MORTGAGE_LOAN_CALCULATION_FIELDS;
        }
      }
      if (Arrays.asList(list).contains(taskFormField.getId().getId()))
      {
        taskFormField.setFormFieldValue(new FormFieldValue(null));
      }
    }
    taskForm.setTaskFormFields(taskFormFields);

    if (isLoanAmountCalculation)
    {
      // sets form fields values from variable value.
      setLoanAmountFieldsFromVariable(caseInstanceId, taskFormFields);
    }
  }

  private boolean isLoanAccountTask(String taskFormId)
  {
    return taskFormId.contains(BpmModuleConstants.TASK_NUMBER_CREATE_LOAN_ACCOUNT) || taskFormId.contains(BpmModuleConstants.ZDC);
  }

  private boolean isLoanAccountCreationCollateralTask(String taskFormId)
  {
    return taskFormId.contains(BpmModuleConstants.TASK_NUMBER_CREATE_LOAN_ACCOUNT) || taskFormId.contains(BpmModuleConstants.ZDC) || taskFormId
        .contains(BpmModuleConstants.CREATE_LOAN_ACCOUNT_ACTIVITY_NAME_NO_NUMBER);
  }

  private void setLoanAmountFieldsFromVariable(String caseInstanceId, Collection<TaskFormField> taskFormFields) throws UseCaseException
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

        if (formFieldId.equalsIgnoreCase("salaryAmountString") && variableId.equalsIgnoreCase("salaryAmountString"))
        {
          taskFormField.setFormFieldValue(new FormFieldValue(value));
        }

        if (formFieldId.equalsIgnoreCase("debtIncomeBalanceString") && variableId.equalsIgnoreCase("debtIncomeBalanceString"))
        {
          taskFormField.setFormFieldValue(new FormFieldValue(value));
        }
      }
    }
  }

  private void checkUdfFields(TaskForm taskForm, Map<String, UDField> udFieldsMap, String hasCollateral) throws BpmServiceException
  {
    Collection<TaskFormField> taskFormFields = taskForm.getTaskFormFields();
    if (!StringUtils.isBlank(hasCollateral) && hasCollateral.equals(NO_MN_VALUE))
    {
      removeEmptyUdfFormField(udFieldsMap, taskFormFields);
    }
    else
    {
      boolean emptySchoolNameUdf = !udFieldsMap.containsKey("SURGUULIIN NER/BAIGUULLAGA") || null == udFieldsMap.get("SURGUULIIN NER/BAIGUULLAGA");
      boolean hasSchoolUdf = udFieldsMap.containsKey("SURGUULI") && null != udFieldsMap.get("SURGUULI");

      if (emptySchoolNameUdf && hasSchoolUdf)
      {
        setSchoolUdfToSchoolNameUdf(udFieldsMap, taskFormFields);
      }
    }
  }

  private void removeEmptyUdfFormField(Map<String, UDField> udFieldsMap, Collection<TaskFormField> taskFormFields)
  {
    if (!udFieldsMap.containsKey("SURGUULIIN NER/BAIGUULLAGA") || null == udFieldsMap.get("SURGUULIIN NER/BAIGUULLAGA"))
    {
      taskFormFields.removeIf(field -> field.getId().getId().equals("schoolNameAndInstitution"));
    }
    if (!udFieldsMap.containsKey("SURGUULI") || null == udFieldsMap.get("SURGUULI"))
    {
      taskFormFields.removeIf(field -> field.getId().getId().equals("schoolName"));
    }
  }

  private void setSchoolUdfToSchoolNameUdf(Map<String, UDField> udFieldsMap, Collection<TaskFormField> taskFormFields)
  {
    for (TaskFormField taskFormField : taskFormFields)
    {
      if (taskFormField.getId().getId().equals("schoolNameAndInstitution"))
      {
        udFieldsMap.get("SURGUULI").getValues().add(0, new UDFieldValue(" ", "     ", false));
        taskFormField.setFieldValidations(Collections.singletonList(new FieldValidation("required", null)));
        setTaskFormFieldDropDown(udFieldsMap.get("SURGUULI"), taskFormField);
      }
    }
  }

  private void updateProcessParameter(String caseInstanceId, Map<ParameterEntityType, Map<String, Serializable>> parameters) throws UseCaseException
  {
    UpdateProcessParametersInput input = new UpdateProcessParametersInput(caseInstanceId, parameters);
    UpdateProcessParameters updateProcessParameters = new UpdateProcessParameters(authenticationService, authorizationService, processRepository);
    updateProcessParameters.execute(input);
  }

  private void setCoBorrowerFormFields(String caseInstanceId, Collection<TaskFormField> taskFormFields) throws UseCaseException
  {
    // todo : migrate to use case impl.
    GetVariablesById getVariablesById = new GetVariablesById(authenticationService, authorizationService, caseService);

    GetVariablesByIdOutput outputVariables = getVariablesById.execute(caseInstanceId);

    List<Variable> variables = outputVariables.getVariables();

    for (Variable coBorrowerVariable : variables)
    {
      String variableId = coBorrowerVariable.getId().getId();

      if (variableId.contains("fullNameCoBorrower-"))
      {
        String[] splitId = variableId.split("-");
        String indexString = splitId[splitId.length - 1];

        String label = LABEL_CO_BORROWER + indexString;

        addStringValueToFormFields(taskFormFields, coBorrowerVariable, variableId, label, true);
      }

      if (variableId.contains("riskyCustomerValue") && variableId.contains("-"))
      {
        addStringValueToFormFields(taskFormFields, coBorrowerVariable, variableId, LABEL_RISKY_CUSTOMER, true);
      }

      if (variableId.contains("loanClassName") && variableId.contains("-"))
      {
        addStringValueToFormFields(taskFormFields, coBorrowerVariable, variableId, LABEL_LOAN_CATEGORY, true);
      }
    }
  }

  private boolean hasCollateralParameters(Collection<TaskFormField> taskFormFields, Map<ParameterEntityType, Map<String, Serializable>> parameters)
  {
    AtomicBoolean haveValueInProcessParameter = new AtomicBoolean(false);
    Optional<TaskFormField> collateralField = taskFormFields.stream().filter(formField -> formField.getId().getId().equals(COLLATERAL_ID)).findFirst();
    if (collateralField.isPresent() && null != collateralField.get().getFormFieldValue())
    {
      String collateralId = (String) collateralField.get().getFormFieldValue().getDefaultValue();
      if (!StringUtils.isBlank(collateralId))
      {
        parameters.forEach((key, value) -> {
          if (value.containsKey(COLLATERAL_ID) && value.get(COLLATERAL_ID).equals(collateralId))
          {
            haveValueInProcessParameter.set(true);
          }
        });
      }
    }

    return haveValueInProcessParameter.get();
  }

  private void setValueByProcessParameters(String fieldId, TaskFormField taskFormField, Map<ParameterEntityType, Map<String, Serializable>> parameters)
  {

    for (Map.Entry<ParameterEntityType, Map<String, Serializable>> parameterEntityTypeMapEntry : parameters.entrySet())
    {
      Map<String, Serializable> values = parameterEntityTypeMapEntry.getValue();
      if (values.containsKey(fieldId))
      {
        FormFieldValue formFieldValue = taskFormField.getFormFieldValue();
        if ((null == formFieldValue || null == formFieldValue.getDefaultValue()) && !"empty".equals(values.get(fieldId)))
        {
          taskFormField.setFormFieldValue(new FormFieldValue(values.get(fieldId)));
        }
      }
    }
  }

  private void addStringValueToFormFields(Collection<TaskFormField> taskFormFields, Variable variable, String variableId, String label, boolean readOnly)
  {
    String value = (String) variable.getValue();

    FormFieldValue formFieldValue = new FormFieldValue();
    formFieldValue.setDefaultValue(value);

    TaskFormField taskFormField = new TaskFormField(FormFieldId.valueOf(variableId), formFieldValue, label, STRING_TYPE);

    taskFormField.setFieldProperties(Collections.emptyList());

    if (readOnly)
    {
      List<FieldValidation> fieldValidations = new ArrayList<>();
      fieldValidations.add(new FieldValidation("readonly", null));
      taskFormField.setFieldValidations(fieldValidations);
    }
    else
    {
      taskFormField.setFieldValidations(Collections.emptyList());
    }

    taskFormFields.add(taskFormField);
  }

  private void setLoanDecisionReceivers(TaskFormField taskFormField, String role, String taskDefKey) throws UseCaseException
  {
    String fieldId = taskFormField.getId().getId();

    GetParentGroupUsersByRole getParentGroupUsersByRole = new GetParentGroupUsersByRole(authenticationService, authorizationService,
        membershipRepository, userRepository, tenantIdProvider, groupRepository);

    GetGroupUsersByRole getGroupUsersByRole = new GetGroupUsersByRole(authenticationService, authorizationService,
        membershipRepository, userRepository, tenantIdProvider);

    if (fieldId.equals(RECEIVERS))
    {
      setDecisionSendUserProperties(role, taskDefKey, taskFormField, getParentGroupUsersByRole, getGroupUsersByRole);
    }
  }

  private void setLoanDecisionReceivedUser(TaskForm taskForm, TaskFormField taskFormField, String roleId) throws UseCaseException
  {
    String currentFormFieldId = taskFormField.getId().getId();

    GetGroupUsersByRole getGroupUsersByRole = new GetGroupUsersByRole(authenticationService, authorizationService,
        membershipRepository, userRepository, tenantIdProvider);

    if (currentFormFieldId.equals(RECEIVED_USER))
    {
      String sentUserId = null;
      Collection<TaskFormField> taskFormFields = taskForm.getTaskFormFields();

      for (TaskFormField formField : taskFormFields)
      {
        String formFieldId = formField.getId().getId();

        if (formFieldId.equalsIgnoreCase(SENT_USER))
        {
          FormFieldValue formFieldValue = formField.getFormFieldValue();
          sentUserId = String.valueOf(formFieldValue.getDefaultValue());
        }
      }

      setDecisionMakeUserProperties(roleId, sentUserId, taskFormField, getGroupUsersByRole);
    }
  }

  private void setDecisionSendUserProperties(String role, String taskDefKey, TaskFormField taskFormField, GetParentGroupUsersByRole getParentGroupUsersByRole,
      GetGroupUsersByRole getGroupUsersByRole)
      throws UseCaseException
  {
    List<String> roles = Arrays.asList(Objects.requireNonNull(environment.getProperty(SPECIALIST_ROLES_WITHOUT_HR_SPECIALIST)).split(","));
    if (roles.stream().anyMatch(role::equals))
    {
      long start = System.currentTimeMillis();
      List<User> parentGroupUsers;
      if (taskDefKey.equals(TASK_DEF_KEY_MORTGAGE_LOAN_SEND))
      {
        parentGroupUsers = getUsersByRoleList(getParentGroupUsersByRole, GetParentGroupUsersByRoleInput.class, Arrays.asList(HUB_DIRECTOR, RC_SPECIALIST));
      }
      else
      {
        parentGroupUsers = getUsersByRoleList(getParentGroupUsersByRole, GetParentGroupUsersByRoleInput.class, Collections.singletonList(HUB_DIRECTOR));
      }

      GetUsersByRoleOutput output = getGroupUsersByRole.execute(new GetUsersByRoleInput(BRANCH_DIRECTOR));
      Collection<User> sameGroupUsers = output.getUsersByRole();

      List<User> userSetNoDuplicates = removeDuplicates(parentGroupUsers, sameGroupUsers);

      fillFormPropertiesWithUsers(taskFormField, userSetNoDuplicates);
      LOGGER.info("Duration for getting users for branch_specialist: [{}]", System.currentTimeMillis() - start);
    }

    if (role.equals(HR_SPECIALIST))
    {
      Collection<User> sameGroupUsers;
      if (taskDefKey.equals(TASK_DEF_KEY_MORTGAGE_LOAN_SEND))
      {
        sameGroupUsers = getUsersByRoleList(getGroupUsersByRole, GetUsersByRoleInput.class, Arrays.asList(R_ANALYST, RC_SPECIALIST));
      }
      else
      {
        sameGroupUsers = getUsersByRoleList(getGroupUsersByRole, GetUsersByRoleInput.class, Collections.singletonList(R_ANALYST));
      }

      fillFormPropertiesWithUsers(taskFormField, sameGroupUsers);
    }
  }

  private void setDecisionMakeUserProperties(String roleId, String sentUserId, TaskFormField taskFormField, GetGroupUsersByRole getGroupUsersByRole)
      throws UseCaseException
  {
    if (roleId.equals(HUB_DIRECTOR))
    {
      fillReceivedUsersBySentUser(sentUserId, taskFormField);
    }
    if (roleId.equals(R_ANALYST))
    {
      //GetSameGroupUsersByRole //hr specialist
      GetUsersByRoleOutput output = getGroupUsersByRole.execute(new GetUsersByRoleInput(HR_SPECIALIST));
      Collection<User> sameGroupUsers = output.getUsersByRole();

      fillFormPropertiesWithUsers(taskFormField, sameGroupUsers);
    }
    if (roleId.equals(BRANCH_DIRECTOR))
    {
      //GetSameGroupUsersByRole //branch specialist

      final GetUsersByRoleInput input = new GetUsersByRoleInput(BRANCH_SPECIALIST);
      input.setRoleIdList(Arrays.asList(Objects.requireNonNull(environment.getProperty(SPECIALIST_ROLES_WITHOUT_HR_SPECIALIST)).split(",")));
      GetUsersByRoleOutput output = getGroupUsersByRole.execute(input);
      Collection<User> sameGroupUsers = output.getUsersByRole();

      fillFormPropertiesWithUsers(taskFormField, sameGroupUsers);
    }
    if (roleId.equals(RC_SPECIALIST))
    {
      fillReceivedUsersBySentUser(sentUserId, taskFormField);
    }
  }

  private void fillReceivedUsersBySentUser(String sentUserId, TaskFormField taskFormField) throws UseCaseException
  {
    GetUserMembership getUserMembership = new GetUserMembership(authenticationService, authorizationService, membershipRepository);
    GetMembershipOutput membershipOutput = getUserMembership.execute(new GetUserMembershipsInput(sentUserId));

    String sentUserGroupId = membershipOutput.getGroupId();

    GetUsersByRoleAndGivenGroupInput input = new GetUsersByRoleAndGivenGroupInput(BRANCH_SPECIALIST, sentUserGroupId);
    GetUsersByRoleAndGivenGroup useCase = new GetUsersByRoleAndGivenGroup(authenticationService, authorizationService,
        membershipRepository, userRepository, tenantIdProvider);
    input.setRoleIdList(Arrays.asList(Objects.requireNonNull(environment.getProperty(SPECIALIST_ROLES_WITHOUT_HR_SPECIALIST)).split(",")));
    GetUsersByRoleAndGivenGroupOutput output = useCase.execute(input);

    fillFormPropertiesWithUsers(taskFormField, output.getUsers());
  }

  private <T> List<User> getUsersByRoleList(AuthorizedUseCase useCase, Class<T> classType, List<String> roleList) throws UseCaseException
  {
    List<User> users = new ArrayList<>();
    for (String roleId : roleList)
    {
      if (classType == GetParentGroupUsersByRoleInput.class)
      {
        GetParentGroupUsersByRoleOutput getParentGroupUsersByRoleOutput = (GetParentGroupUsersByRoleOutput) useCase
            .execute(new GetParentGroupUsersByRoleInput(roleId));
        users.addAll(getParentGroupUsersByRoleOutput.getParentGroupUsersByRole());
      }
      else if (classType == GetUsersByRoleInput.class)
      {
        GetUsersByRoleOutput output = (GetUsersByRoleOutput) useCase.execute(new GetUsersByRoleInput(roleId));
        users.addAll(output.getUsersByRole());
      }
    }
    return users;
  }

  private ResponseEntity<RestResult> getCompletedFormResponse(String jsonString) throws BpmInvalidArgumentException
  {
    try
    {
      RestCompletedForm restCompletedForm = jsonToObject(jsonString, RestCompletedForm.class);
      return RestResponse.success(restCompletedForm);
    }
    catch (JSONException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(INTERNAL_SERVER_ERROR + e.getMessage() + " REASON : COULD NOT CREATE JSON FROM SERIALIZABLE VALUE.");
    }
  }

  private void setLoanAccountCreationProperties(TaskFormField taskFormField, GetCustomerAccountCreationInfoOutput output,
      GetUDFieldsByProductCodeOutput getUDFieldsByProductCodeOutput)
  {
    String fieldId = taskFormField.getId().getId();

    if (fieldId.equals("attentiveLoan"))
    {
      if (output.getAttentiveLoan() == null)
      {
        return;
      }
      output.getAttentiveLoan().getValues().add(0, new UDFieldValue(" ", "     ", false));
      setTaskFormFieldDropDown(output.getAttentiveLoan(), taskFormField);
    }
    if (fieldId.equals("surguuli"))
    {
      if (getUDFieldsByProductCodeOutput.getUdFieldsMap().get("SURGUULI") == null)
      {
        return;
      }
      setTaskFormFieldDropDown(getUDFieldsByProductCodeOutput.getUdFieldsMap().get("SURGUULI"), taskFormField);
    }
    if (fieldId.equals("firstAccountNumber"))
    {
      if (output.getFirstAccountNumber() == null)
      {
        return;
      }
      setTaskFormFieldDropDown(output.getFirstAccountNumber(), taskFormField);
    }
    if (fieldId.equals("supplier1"))
    {
      if (output.getFirstSupplier() == null)
      {
        return;
      }
      output.getFirstSupplier().getValues().add(0, new UDFieldValue(" ", "     ", false));
      setTaskFormFieldDropDown(output.getFirstSupplier(), taskFormField);
    }
    if (fieldId.equals("firstDisbursedDate"))
    {
      if (output.getFirstDisbursedLoanDate() == null)
      {
        return;
      }
      setTaskFormFieldDropDown(output.getFirstDisbursedLoanDate(), taskFormField);
    }
    if (fieldId.equals("lateReasonAttention"))
    {
      if (output.getLateReasonAttention() == null)
      {
        return;
      }
      output.getLateReasonAttention().getValues().add(0, new UDFieldValue(" ", "     ", false));
      setTaskFormFieldDropDown(output.getLateReasonAttention(), taskFormField);
    }
    if (fieldId.equals("restructuredNumber"))
    {
      if (output.getRestructuredNumber() == null)
      {
        return;
      }
      output.getRestructuredNumber().getValues().add(0, new UDFieldValue(" ", "     ", false));
      setTaskFormFieldDropDown(output.getRestructuredNumber(), taskFormField);
    }
    if (fieldId.equals("loanPurpose"))
    {
      if (output.getLoanPurpose() == null)
      {
        return;
      }
      setTaskFormFieldDropDown(output.getLoanPurpose(), taskFormField);
    }
    if (fieldId.equals("businessTypeReason"))
    {
      if (output.getBusinessTypeReason() == null)
      {
        return;
      }
      setTaskFormFieldDropDown(output.getBusinessTypeReason(), taskFormField);
    }
    if (fieldId.equals("sanctionedBy"))
    {
      if (output.getSanctionedBy() == null)
      {
        return;
      }
      setTaskFormFieldDropDown(output.getSanctionedBy(), taskFormField);
    }
    if (fieldId.equals("subType"))
    {
      if (output.getSubType() == null)
      {
        return;
      }
      setTaskFormFieldDropDown(output.getSubType(), taskFormField);
    }
    if (fieldId.equals("insuranceCompanyInfo"))
    {
      if (output.getInsuranceCompanyInfo() == null)
      {
        return;
      }
      setTaskFormFieldDropDown(output.getInsuranceCompanyInfo(), taskFormField);
    }
    if (fieldId.equals("supplier2"))
    {
      if (output.getSecondSupplier() == null)
      {
        return;
      }
      output.getSecondSupplier().getValues().add(0, new UDFieldValue(" ", "     ", false));
      setTaskFormFieldDropDown(output.getSecondSupplier(), taskFormField);
    }
    if (fieldId.equals("supplier3"))
    {
      if (output.getThirdSupplier() == null)
      {
        return;
      }
      output.getThirdSupplier().getValues().add(0, new UDFieldValue(" ", "     ", false));
      setTaskFormFieldDropDown(output.getThirdSupplier(), taskFormField);
    }
    if (fieldId.equals("lateReason"))
    {
      if (output.getLateReason() == null)
      {
        return;
      }
      output.getLateReason().getValues().add(0, new UDFieldValue(" ", "     ", false));
      setTaskFormFieldDropDown(output.getLateReason(), taskFormField);
    }
    if (fieldId.equals("loanCycle"))
    {
      if (output.getLoanCycle() == null)
      {
        return;
      }
      setTaskFormFieldDropDown(output.getLoanCycle(), taskFormField);
    }
    if (fieldId.equals("worker"))
    {
      if (output.getWorker() == null)
      {
        return;
      }
      UDField udField = output.getWorker();
      if (!udField.getValues().isEmpty())
      {
        List<FieldProperty> fieldProperties = new ArrayList<>();
        for (UDFieldValue value : udField.getValues())
        {
          StringBuilder workerId = new StringBuilder();
          workerId.append(value.getItemId());
          workerId.append(" - ");
          workerId.append(value.getItemDescription());
          fieldProperties.add(new FieldProperty(value.getItemId(), workerId.toString()));
        }

        taskFormField.setFieldProperties(fieldProperties);
      }
      else
      {
        taskFormField.setFormFieldValue(new FormFieldValue(udField.getDefaultValue()));
      }
    }
  }

  private void setLoanAccountCreationProperties(TaskFormField taskFormField, GetUDFieldsByProductCodeOutput output)
  {
    String fieldId = taskFormField.getId().getId();
    Map<String, UDField> udFieldsMap = output.getUdFieldsMap();
    if (udFieldsMap.containsKey(fieldId) && null != udFieldsMap.get(fieldId))
    {
      if (!StringUtils.equals(fieldId, "FREE_CODE_8") && !StringUtils.equals(fieldId, "modeOfAdvance"))
      {
        udFieldsMap.get(fieldId).getValues().add(0, new UDFieldValue(" ", "     ", false));
      }
      setTaskFormFieldDropDown(udFieldsMap.get(fieldId), taskFormField);
    }
  }

  private GetUDFieldsByProductCodeOutput getUDFieldsByProductCode(Map<String, String> input) throws UseCaseException
  {
    GetUDFieldsByProductCode getUDFieldsByProductCode = new GetUDFieldsByProductCode(newCoreBankingService);

    return getUDFieldsByProductCode.execute(input);
  }

  private void setTaskFormFieldDropDown(UDField udField, TaskFormField taskFormField)
  {
    if (null != udField)
    {
      if (!udField.getValues().isEmpty())
      {
        List<FieldProperty> fieldProperties = new ArrayList<>();
        for (UDFieldValue value : udField.getValues())
        {
          fieldProperties.add(new FieldProperty(value.getItemId(), value.getItemDescription()));
        }

        taskFormField.setFieldProperties(fieldProperties);
      }
      else
      {
        if (udField.getDefaultValue() != null)
        {
          taskFormField.setFormFieldValue(new FormFieldValue(udField.getDefaultValue()));
        }
      }
    }
  }

  private void setTextAreaField(TaskFormField taskFormField)
  {
    if (null != taskFormField && taskFormField.getId().getId().equals("additionalSpecialCondition"))
    {
      taskFormField.setType("textArea");
    }

    if (null != taskFormField && taskFormField.getId().getId().equals("loanCommentExplanation"))
    {
      taskFormField.setType("textArea");
    }

    if (null != taskFormField && taskFormField.getId().getId().equals("loanDecisionReason"))
    {
      taskFormField.setType("textArea");
    }
    if (null != taskFormField && taskFormField.getId().getId().equals("errorCause"))
    {
      taskFormField.setType("textArea");
    }
  }

  private Map<String, Object> filterFromFingerPrint(Map<String, Object> properties)
  {
    return properties.entrySet()
        .stream()
        .filter(property -> !property.getKey().toLowerCase().contains(FINGER_PRINT_LOWER_CASE))
        .filter(property -> property.getValue() != null)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  private void setAccountNumbers(TaskFormField taskFormField, GetAccountsListOutput getAccountsListOutput)
  {
    String fieldId = taskFormField.getId().getId();
    if (fieldId.equals("currentAccountNumber"))
    {
      List<FieldProperty> fieldProperties = new ArrayList<>();
      Map<String, Integer> nonDuplicateXacAccounts = new HashMap<>();

      for (XacAccount xacAccount : getAccountsListOutput.getAccountList())
      {
        String accountId = xacAccount.getId().getId();
        if (!nonDuplicateXacAccounts.containsKey(accountId))
        {
          fieldProperties.add(new FieldProperty(accountId, accountId));
          nonDuplicateXacAccounts.put(accountId, 1);
        }
      }
      taskFormField.setFieldProperties(fieldProperties);
    }
  }

  private Map<String, String> getAccountCreationInformationVariables(String caseInstanceId) throws UseCaseException
  {
    Map<String, String> accountCreationInformationVariables = new HashMap<>();

    GetVariablesById getVariablesById = new GetVariablesById(authenticationService, authorizationService, caseService);
    GetVariablesByIdOutput getVariablesByIdOutput = getVariablesById.execute(caseInstanceId);
    for (Variable variable : getVariablesByIdOutput.getVariables())
    {
      String id = variable.getId().getId();

      if (id.equalsIgnoreCase(BpmModuleConstants.REGISTER_NUMBER))
      {
        accountCreationInformationVariables.put("regNo", (String) variable.getValue());
      }
      else if (id.equalsIgnoreCase(CIF_NUMBER))
      {
        accountCreationInformationVariables.put("CIF", (String) variable.getValue());
      }
      else if (id.equalsIgnoreCase(PHONE_NUMBER))
      {
        accountCreationInformationVariables.put(PHONE_NUMBER, (String) variable.getValue());
      }
      else if (id.equalsIgnoreCase(PROCESS_TYPE_ID))
      {
        accountCreationInformationVariables.put(PROCESS_TYPE_ID, (String) variable.getValue());
      }
      else if (id.equalsIgnoreCase(LOAN_PRODUCT))
      {
        String loanProduct = String.valueOf(variable.getValue());

        if (null != loanProduct)
        {
          String productCode = loanProduct.substring(0, 4);
          LOGGER.info("############ SETS PRODUCT CODE = [{}] TO ACCOUNT CREATION INFORMATION VARIABLES WHEN LOAN ACCOUNT FORM RENDERING...", productCode);

          accountCreationInformationVariables.put(PRODUCT_CODE, productCode);
          setProductCodeFromConsumptionLoan(productCode, accountCreationInformationVariables);
        }
      }
    }
    return accountCreationInformationVariables;
  }

  /**
   * Sets correct product code from consumption loan product variable.
   * Following method responsible for consumption loan process variables otherwise affects v2.0 requests.
   *
   * @param loanProduct              loan product.
   * @param accountCreationVariables account creation variables.
   */
  private void setProductCodeFromConsumptionLoan(String loanProduct, Map<String, String> accountCreationVariables)
  {
    if (null == loanProduct)
    {
      return;
    }

    switch (loanProduct)
    {
    case EB_50_PRODUCT_DESCRIPTION:
      accountCreationVariables.put(PRODUCT_CODE, EB_50_PRODUCT_CODE);
      break;

    case EF_50_PRODUCT_DESCRIPTION:
      accountCreationVariables.put(PRODUCT_CODE, EF_50_PRODUCT_CODE);
      break;

    case EB_51_PRODUCT_DESCRIPTION:
      accountCreationVariables.put(PRODUCT_CODE, EB_51_PRODUCT_CODE);
      break;

    default:
      break;
    }
  }

  private void fillFormPropertiesWithUsers(TaskFormField taskFormField, Collection<User> users)
  {
    List<FieldProperty> fieldProperties = new ArrayList<>();
    users = removeSelfUserId(users, authenticationService.getCurrentUserId());
    for (User user : users)
    {
      fieldProperties.add(new FieldProperty(user.getUserId().getId(), user.getUserInfo().getUserName()));
    }
    taskFormField.setFieldProperties(fieldProperties);
  }

  private Collection<User> removeSelfUserId(Collection<User> users, String userId)
  {
    for (User user : users)
    {
      if (null != user.getUserId() && user.getUserId().getId().equals(userId))
      {
        users.remove(user);
      }
    }
    return users;
  }

  private List<User> removeDuplicates(List<User> userList1, Collection<User> userList2)
  {
    Map<String, User> users = new HashMap<>();

    for (User user : userList1)
    {
      users.put(user.getUserId().getId(), user);
    }

    for (User user : userList2)
    {
      users.put(user.getUserId().getId(), user);
    }

    List<User> usersList = new ArrayList<>();

    for (Map.Entry<String, User> entry : users.entrySet())
    {
      usersList.add(entry.getValue());
    }

    return usersList;
  }

  private String getRole() throws UseCaseException
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

  private boolean updateProcessRequestParams(String processRequestId, Map<String, Object> properties) throws UseCaseException
  {
    Map<String, Serializable> requestParameters = new HashMap<>();

    if (properties.containsKey(FULL_NAME))
    {
      String fullName = (String) properties.get(FULL_NAME);
      requestParameters.put(FULL_NAME, fullName);
    }

    if (properties.containsKey(CIF_NUMBER))
    {
      String cifNumber = (String) properties.get(CIF_NUMBER);
      requestParameters.put(CIF_NUMBER, cifNumber);
    }

    if (requestParameters.isEmpty())
    {
      return true;
    }

    UpdateRequestParametersInput input = new UpdateRequestParametersInput(processRequestId, requestParameters);
    UpdateRequestParameters updateRequestParameters = new UpdateRequestParameters(authenticationService, authorizationService, processRequestRepository);

    UpdateRequestParametersOutput output = updateRequestParameters.execute(input);
    return output.isUpdated();
  }

  private TaskForm fillFormFieldValues(String caseInstanceId, TaskForm taskForm) throws UseCaseException
  {
    GetVariablesById getVariablesById = new GetVariablesById(authenticationService, authorizationService, caseService);
    GetVariablesByIdOutput variablesOutput = getVariablesById.execute(caseInstanceId);

    List<Variable> variables = variablesOutput.getVariables();
    Collection<TaskFormField> filledFormFields = getFilledFormFields(taskForm, variables);

    taskForm.setTaskFormFields(filledFormFields);
    return taskForm;
  }

  private Collection<TaskFormField> getFilledFormFields(TaskForm taskForm, List<Variable> variables)
  {
    Collection<TaskFormField> taskFormFields = taskForm.getTaskFormFields();

    for (TaskFormField taskFormField : taskFormFields)
    {
      FormFieldId formFieldId = taskFormField.getId();

      if (null != formFieldId)
      {
        String id = formFieldId.getId();

        for (Variable variable : variables)
        {
          String variableId = variable.getId().getId();
          Serializable value = variable.getValue();

          if (variableId.equalsIgnoreCase(id) && null != value)
          {
            taskFormField.setFormFieldValue(new FormFieldValue(value));
          }
        }
      }
    }
    return taskFormFields;
  }

  private void setImmovableCollFormInfos(TaskFormField taskFormField) throws UseCaseException
  {
    Map<String, List<String>> referenceCodeResponses = getReferenceCodes(Arrays
        .asList(REFERENCE_CODE_COLL_OWNERTYPE,
            REFERENCE_CODE_COLL_INSPTYPE,
            REFERENCE_CODE_COLL_CITY,
            REFERENCE_CODE_COLL_PURPOSE));

    String fieldId = taskFormField.getId().getId();

    if (fieldId.equalsIgnoreCase(COLLATERAL_CODE_FORM_FIELD_ID))
    {
      List<String> collCodes = getCollCodes(COLL_TYPE_IMMOVABLE);
      setCollateralCode(taskFormField, collCodes);
    }
    else if (fieldId.equalsIgnoreCase(CITY_FORM_FIELD_ID))
    {
      setFieldByReferenceCodes(REFERENCE_CODE_COLL_CITY, taskFormField, referenceCodeResponses);
    }
    else if (fieldId.equalsIgnoreCase(PURPOSE_OF_USAGE_FIELD_ID))
    {
      setFieldByReferenceCodes(REFERENCE_CODE_COLL_PURPOSE, taskFormField, referenceCodeResponses);
    }
    else
      setCreateCollateralFieldValue(taskFormField, referenceCodeResponses, fieldId);
  }

  private void setMachineryCollFormInfos(TaskFormField taskFormField) throws UseCaseException
  {
    Map<String, List<String>> referenceCodeResponses = getReferenceCodes(Arrays
        .asList(REFERENCE_CODE_COLL_OWNERTYPE,
            REFERENCE_CODE_COLL_INSPTYPE,
            REFERENCE_CODE_COLL_CITY,
            REFERENCE_CODE_COLL_PURPOSE));

    String fieldId = taskFormField.getId().getId();

    // set collateral code field options
    if (fieldId.equalsIgnoreCase(COLLATERAL_CODE_FORM_FIELD_ID))
    {
      List<String> collCodes = getCollCodes(COLL_TYPE_MACHINERY);
      setCollateralCode(taskFormField, collCodes);
    }
    else if (fieldId.equalsIgnoreCase(CITY_FORM_FIELD_ID))
    {
      setFieldByReferenceCodes(REFERENCE_CODE_COLL_CITY, taskFormField, referenceCodeResponses);
    }
    else if (fieldId.equalsIgnoreCase(PURPOSE_OF_USAGE_FIELD_ID))
    {
      setFieldByReferenceCodes(REFERENCE_CODE_COLL_PURPOSE, taskFormField, referenceCodeResponses);
    }
    else
      setCreateCollateralFieldValue(taskFormField, referenceCodeResponses, fieldId);
  }

  private void setVehicleCollateralInfos(TaskFormField taskFormField) throws UseCaseException
  {
    Map<String, List<String>> referenceCodes = getReferenceCodes(Arrays.asList(
        REFERENCE_CODE_COLL_OWNERTYPE,
        REFERENCE_CODE_COLL_INSPTYPE,
        REFERENCE_CODE_COLL_LEASING_SUPPLIER,
        REFERENCE_CODE_COLL_VEHICLE_TYPE));

    String fieldId = taskFormField.getId().getId();
    if (fieldId.equalsIgnoreCase(COLLATERAL_CODE_FORM_FIELD_ID))
    {
      List<String> collCodes = getCollCodes(COLL_TYPE_VEHICLE);

      setCollateralCode(taskFormField, collCodes);
    }

    else if (fieldId.equalsIgnoreCase(FINANCIAL_LEASING_SUPPLIER))
    {
      setFieldByReferenceCodes(REFERENCE_CODE_COLL_LEASING_SUPPLIER, taskFormField, referenceCodes);
    }
    else if (fieldId.equalsIgnoreCase(PURPOSE_OF_USAGE_FIELD_ID))
    {
      setFieldByReferenceCodes(REFERENCE_CODE_COLL_VEHICLE_TYPE, taskFormField, referenceCodes);
    }
    else
      setCreateCollateralFieldValue(taskFormField, referenceCodes, fieldId);
  }

  private void setOtherCollateralInfos(TaskFormField taskFormField) throws UseCaseException
  {
    Map<String, List<String>> referenceCodes = getReferenceCodes(Arrays.asList(REFERENCE_CODE_COLL_OWNERTYPE, REFERENCE_CODE_COLL_INSPTYPE));
    String fieldId = taskFormField.getId().getId();

    if (fieldId.equalsIgnoreCase(COLLATERAL_CODE_FORM_FIELD_ID))
    {
      List<String> collCodes = getCollCodes(COLL_TYPE_OTHERS);
      setCollateralCode(taskFormField, collCodes);
    }
    else
      setCreateCollateralFieldValue(taskFormField, referenceCodes, fieldId);
  }

  private List<String> getCollCodes(String collType) throws UseCaseException
  {
    GetCollateralCodes getCollateralCodes = new GetCollateralCodes(bpmsServiceRegistry.getNewCoreBankingService());
    GetCollateralCodesOutput codesOutput = getCollateralCodes.execute(collType);
    return codesOutput.getCollateralCodes();
  }

  private Map<String, List<String>> getReferenceCodes(List<String> types) throws UseCaseException
  {
    GetCollReferenceCodes getCollReferenceCodes = new GetCollReferenceCodes(bpmsServiceRegistry.getNewCoreBankingService());
    GetCollReferenceCodesInput input = new GetCollReferenceCodesInput(types);

    GetCollReferenceCodesOutput output = getCollReferenceCodes.execute(input);

    return output.getReferenceCodes();
  }

  private String checkRelatedUserTaskId(String caseInstanceId) throws BpmServiceException
  {
    Object relatedUSerTaskId = caseService.getVariableById(caseInstanceId, RELATED_USER_TASK_ID);
    if (null == relatedUSerTaskId || StringUtils.isBlank(String.valueOf(relatedUSerTaskId)))
    {
      return null;
    }

    return String.valueOf(relatedUSerTaskId);
  }

  private void setTransactionTaskFormValue(TaskFormField taskFormField, String fieldId)
  {
    if ((fieldId.equalsIgnoreCase("transactionDate")
        || fieldId.equalsIgnoreCase("transactionStartDate")
        || fieldId.equalsIgnoreCase("transactionEndDate"))
        && taskFormField.getFormFieldValue().getDefaultValue() == null)
    {
      taskFormField.setFormFieldValue(new FormFieldValue(new Date()));
    }
  }

  private void setLoanContractDefaultDate(TaskFormField taskFormField, String fieldId)
  {
    if (fieldId.equalsIgnoreCase(CONTRACT_DATE))
    {
      if (taskFormField.getFormFieldValue().getDefaultValue() == null)
      {
        taskFormField.setFormFieldValue(new FormFieldValue(new Date()));
      }
    }
  }

  private SubmitFormOutput submitLoanContract(String caseInstanceId, String taskId, Map<String, Object> properties, String defKey)
      throws UseCaseException, BpmServiceException
  {
    Map<String, Object> tableMap = new HashMap<>();
    String user = null;
    if (properties.containsKey("table"))
    {
      tableMap.put("table", properties.get("table"));
      properties.remove("table");
    }

    if (properties.containsKey("user"))
    {
      user = String.valueOf(properties.get("user"));
      properties.remove("user");
    }

    SubmitForm useCase = new SubmitForm(taskFormService, caseService);
    SubmitFormInput submitInput = new SubmitFormInput(taskId, caseInstanceId, properties);

    SubmitFormInput setVariablesInput = new SubmitFormInput(taskId, caseInstanceId, properties);

    SetVariablesOnActiveTasks setVariablesOnActiveTasks = new SetVariablesOnActiveTasks(taskService, runtimeService);
    setVariablesOnActiveTasks.execute(setVariablesInput);

    SubmitFormOutput output = useCase.execute(submitInput);
    LOGGER.info("######### SUBMITTED CONTRACT FORM TO CAMUNDA");
    UpdateLoanContractParameter updateLoanContractParameter = new UpdateLoanContractParameter(loanContractParameterRepository, authenticationService,
        authorizationService);
    LoanContractParameterInput input = new LoanContractParameterInput(caseInstanceId, properties, enumToString(FORM), defKey);
    if (!tableMap.isEmpty())
    {
      input.setTableMap(tableMap);
    }

    updateLoanContractParameter.execute(input);
    LOGGER.info("######### UPDATES FORM PARAMETERS TO CONTRACT PROCESS PARAMETER TABLE WHEN SUBMIT TASK, PROCESS INSTANCE ID ID = [{}]", caseInstanceId);
    if (!StringUtils.isBlank(user))
    {
      UpdateLoanContractRequestInput updateLoanContractRequestInput = new UpdateLoanContractRequestInput(caseInstanceId, user);
      UpdateLoanContractRequest updateLoanContractRequest = new UpdateLoanContractRequest(loanContractRequestRepository);
      updateLoanContractRequest.execute(updateLoanContractRequestInput);
      final Object requestId = caseService.getVariableById(caseInstanceId, PROCESS_REQUEST_ID);
      LOGGER.info("######### ASSIGNED PROCESS REQUEST = {} TO USER {}", requestId, user);
    }
    return output;
  }
}
