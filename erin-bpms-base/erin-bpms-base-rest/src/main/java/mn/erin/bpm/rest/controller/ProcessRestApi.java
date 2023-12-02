/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.rest.controller;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.inject.Inject;

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

import mn.erin.bpm.rest.model.RestCompletedFormField;
import mn.erin.bpm.rest.model.RestProcess;
import mn.erin.bpm.rest.model.RestProcessEntityParameters;
import mn.erin.bpm.rest.util.BpmRestUtils;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.exception.BpmInvalidArgumentException;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.model.process.Process;
import mn.erin.domain.bpm.model.salary.SalaryInfo;
import mn.erin.domain.bpm.model.variable.Variable;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.repository.LoanContractParameterRepository;
import mn.erin.domain.bpm.repository.LoanContractRequestRepository;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.usecase.contract.LoanContractParameterInput;
import mn.erin.domain.bpm.usecase.contract.UpdateLoanContractParameter;
import mn.erin.domain.bpm.usecase.contract.UpdateLoanContractRequest;
import mn.erin.domain.bpm.usecase.contract.UpdateLoanContractRequestInput;
import mn.erin.domain.bpm.usecase.form.case_variable.SetCaseVariableById;
import mn.erin.domain.bpm.usecase.form.case_variable.SetCaseVariableByIdInput;
import mn.erin.domain.bpm.usecase.form.case_variable.SetCaseVariables;
import mn.erin.domain.bpm.usecase.form.submit_form.SubmitFormInput;
import mn.erin.domain.bpm.usecase.process.GetProcess;
import mn.erin.domain.bpm.usecase.process.GetProcessEntity;
import mn.erin.domain.bpm.usecase.process.GetProcessEntityInput;
import mn.erin.domain.bpm.usecase.process.GetProcessInput;
import mn.erin.domain.bpm.usecase.process.GetProcessOutput;
import mn.erin.domain.bpm.usecase.process.GetProcessParameterInput;
import mn.erin.domain.bpm.usecase.process.UpdateCollateralProcessParameters;
import mn.erin.domain.bpm.usecase.process.UpdateCollateralProcessParametersInput;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParameters;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParametersInput;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParametersOutput;
import mn.erin.domain.bpm.usecase.process.collateral.GetSavedCollateralUDFields;
import mn.erin.domain.bpm.usecase.process.collateral.GetSavedCollateralUDFieldsInput;
import mn.erin.domain.bpm.usecase.process.collateral.GetSavedCollateralUDFieldsOutput;
import mn.erin.domain.bpm.usecase.process.get_variables.GetVariablesById;
import mn.erin.domain.bpm.usecase.process.get_variables.GetVariablesByIdOutput;
import mn.erin.domain.bpm.usecase.process.process_parameter.GetProcessParameterByName;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;

import static mn.erin.bpm.rest.util.BpmRestUtils.mapLoanContractForm;
import static mn.erin.bpm.rest.util.BpmRestUtils.objectMapToSerializableMap;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.COLLATERAL_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.COLLATERAL_ID_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PARAMETERS_EMPTY_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PARAMETERS_EMPTY_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PARAMETER_NAME_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PARAMETER_NAME_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PROCESS_INSTANCE_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PROCESS_INSTANCE_ID_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.STRING_TO_DATE_EXCEPTION_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.STRING_TO_DATE_EXCEPTION_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.DATE_FORMAT2;
import static mn.erin.domain.bpm.BpmModuleConstants.DATE_PREFIX;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_CONTRACT_CAMEL_CASE;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.SALARY_INFO;
import static mn.erin.domain.bpm.model.process.ParameterEntityType.LOAN_CONTRACT;
import static mn.erin.domain.bpm.util.process.BpmUtils.convertStringToDate;

/**
 * Represents a process rest api.
 *
 * @author Tamir
 */
@Api
@RestController
@RequestMapping(value = "bpm/process", name = "Provides BPM case APIs.")
public class ProcessRestApi extends BaseBpmsRestApi
{
  private final AuthenticationService authenticationService;
  private final AuthorizationService authorizationService;

  private final CaseService caseService;
  private final ProcessRepository processRepository;
  private final LoanContractParameterRepository loanContractParameterRepository;
  private final LoanContractRequestRepository loanContractRequestRepository;

  private static final Logger LOGGER = LoggerFactory.getLogger(ProcessRestApi.class);

  @Inject
  public ProcessRestApi(
      BpmsServiceRegistry bpmsServiceRegistry,
      BpmsRepositoryRegistry bpmsRepositoryRegistry,
      AuthenticationService authenticationService,
      AuthorizationService authorizationService,
      LoanContractParameterRepository loanContractParameterRepository,
      LoanContractRequestRepository loanContractRequestRepository)
  {
    super(bpmsServiceRegistry, bpmsRepositoryRegistry);

    this.authenticationService = Objects.requireNonNull(authenticationService, "Authentication service is required!");
    this.authorizationService = Objects.requireNonNull(authorizationService, "Authorization service is required!");

    this.caseService = Objects.requireNonNull(bpmsServiceRegistry.getCaseService(), "Case service is required!");
    this.processRepository = Objects.requireNonNull(bpmsRepositoryRegistry.getProcessRepository(), "Process repository is required!");
    this.loanContractParameterRepository = Objects.requireNonNull(loanContractParameterRepository, "Loan contract parameter repository is required!");
    this.loanContractRequestRepository = loanContractRequestRepository;
  }

  // todo : replace process repository after persisting data to our database.
  @ApiOperation("Gets parameters info by case instance id.")
  @GetMapping("/parameters/salary-info/{caseInstanceId}")
  public ResponseEntity<RestResult> getParameterSalaryInfo(@PathVariable String caseInstanceId) throws UseCaseException, BpmInvalidArgumentException
  {
    List<SalaryInfo> salaryInfos = new ArrayList<>();

    if (StringUtils.isBlank(caseInstanceId))
    {
      throw new BpmInvalidArgumentException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
    }

    GetVariablesById getVariablesById = new GetVariablesById(authenticationService, authorizationService, caseService);
    GetVariablesByIdOutput output = getVariablesById.execute(caseInstanceId);
    List<Variable> variables = output.getVariables();

    for (Variable variable : variables)
    {
      String id = variable.getId().getId();

      if (id.equalsIgnoreCase(SALARY_INFO))
      {
        salaryInfos = (List<SalaryInfo>) variable.getValue();
        return RestResponse.success(BpmRestUtils.toRestSalaryInfo(salaryInfos));
      }
    }

    return RestResponse.success(salaryInfos);
  }

  @ApiOperation("Saves parameters of entity")
  @PostMapping("/parameters/save/{processInstanceId}")
  public ResponseEntity<RestResult> saveEntityParameters(@PathVariable String processInstanceId,
      @RequestBody RestProcessEntityParameters restProcessEntityParameters)
      throws UseCaseException, BpmInvalidArgumentException, BpmServiceException
  {
    String processTypeId = String.valueOf(caseService.getVariableById(processInstanceId, "processTypeId"));
    if (StringUtils.isBlank(processInstanceId))
    {
      throw new UseCaseException(PROCESS_INSTANCE_ID_NULL_CODE, PROCESS_INSTANCE_ID_NULL_MESSAGE);
    }

    if (processTypeId.contains(LOAN_CONTRACT_CAMEL_CASE))
    {
      return saveLoanContractParameter(processInstanceId, restProcessEntityParameters.getParameters(), restProcessEntityParameters.getParameterEntityType(),
          restProcessEntityParameters.getDefKey());
    }

    if (restProcessEntityParameters.getParameters().isEmpty() || restProcessEntityParameters.getParameters() == null)
    {
      throw new BpmInvalidArgumentException(PARAMETERS_EMPTY_CODE, PARAMETERS_EMPTY_MESSAGE);
    }

    try
    {
      Map<String, Object> parameters;

      String entityType = ParameterEntityType.enumToString(LOAN_CONTRACT);
      if (restProcessEntityParameters.getParameterEntityType().equals(entityType))
      {
        parameters = mapLoanContractForm(restProcessEntityParameters.getParameters());
      }
      else
      {
        parameters = restProcessEntityParameters.getParameters();
      }
      Map<String, Serializable> serializableParametersMap = objectMapToSerializableMap(parameters);

      // Used to update process parameter table
      UpdateProcessParametersInput input = new UpdateProcessParametersInput(processInstanceId,
          Collections.singletonMap(ParameterEntityType.fromStringToEnum(restProcessEntityParameters.getParameterEntityType()), serializableParametersMap));
      UpdateProcessParameters updateProcessParameters = new UpdateProcessParameters(authenticationService, authorizationService, processRepository);
      UpdateProcessParametersOutput output = updateProcessParameters.execute(input);

      // Used to update case variable
      if (!restProcessEntityParameters.getRestFormFields().isEmpty())
      {
        saveParametersToCaseVariable(restProcessEntityParameters.getRestFormFields(), processInstanceId);
      }

      return RestResponse.success(output);
    }
    catch (IllegalArgumentException e)
    {
      return RestResponse.badRequest(e.getMessage());
    }
  }

  @ApiOperation("Saves parameters of entity type collateral")
  @PostMapping("/parameters/save/{processInstanceId}/collateral/{collateralId}")
  public ResponseEntity<RestResult> saveCollateralEntityParameters(@PathVariable String processInstanceId, @PathVariable String collateralId,
      @RequestBody RestProcessEntityParameters restProcessEntityParameters)
      throws UseCaseException, BpmInvalidArgumentException
  {
    if (StringUtils.isBlank(processInstanceId))
    {
      throw new UseCaseException(PROCESS_INSTANCE_ID_NULL_CODE, PROCESS_INSTANCE_ID_NULL_MESSAGE);
    }

    if (StringUtils.isBlank(collateralId))
    {
      throw new UseCaseException(COLLATERAL_ID_NULL_CODE, COLLATERAL_ID_NULL_MESSAGE);
    }

    if (restProcessEntityParameters.getParameters().isEmpty() || restProcessEntityParameters.getParameters() == null)
    {
      throw new BpmInvalidArgumentException(PARAMETERS_EMPTY_CODE, PARAMETERS_EMPTY_MESSAGE);
    }

    try
    {
      Map<String, Object> parameters = restProcessEntityParameters.getParameters();
      Map<String, Serializable> serializableParametersMap = objectMapToSerializableMap(parameters);

      UpdateCollateralProcessParameters updateCollateralProcessParameters = new UpdateCollateralProcessParameters(processRepository);
      UpdateCollateralProcessParametersInput input = new UpdateCollateralProcessParametersInput(processInstanceId, collateralId,
          Collections.singletonMap(ParameterEntityType.fromStringToEnum(restProcessEntityParameters.getParameterEntityType()), serializableParametersMap));

      UpdateProcessParametersOutput output = updateCollateralProcessParameters.execute(input);

      return RestResponse.success(output);
    }
    catch (IllegalArgumentException e)
    {
      return RestResponse.badRequest(e.getMessage());
    }
  }

  @ApiOperation("Gets parameters of entity type")
  @GetMapping("/parameters/{parameterEntityType}/{processInstanceId}")
  public ResponseEntity<RestResult> getEntityParameters(@PathVariable String processInstanceId, @PathVariable String parameterEntityType)
      throws UseCaseException
  {
    if (StringUtils.isBlank(processInstanceId))
    {
      throw new UseCaseException(PROCESS_INSTANCE_ID_NULL_CODE, PROCESS_INSTANCE_ID_NULL_MESSAGE);
    }

    try
    {
      GetProcessEntityInput input = new GetProcessEntityInput(processInstanceId, ParameterEntityType.fromStringToEnum(parameterEntityType));
      GetProcessEntity getProcessEntity = new GetProcessEntity(authenticationService, authorizationService, processRepository);
      Map<String, Serializable> entityValues = getProcessEntity.execute(input);

      return RestResponse.success(entityValues);
    }
    catch (IllegalArgumentException e)
    {
      return RestResponse.badRequest(e.getMessage());
    }
  }

  @ApiOperation("Gets parameters using entity type and collateral id.")
  @GetMapping("/parameters/{processInstanceId}/collateral/{collateralId}")
  public ResponseEntity<RestResult> getEntityParametersOfCollateral(@PathVariable String processInstanceId, @PathVariable String collateralId)
      throws UseCaseException
  {
    if (StringUtils.isBlank(processInstanceId))
    {
      throw new UseCaseException(PROCESS_INSTANCE_ID_NULL_CODE, PROCESS_INSTANCE_ID_NULL_MESSAGE);
    }

    if (StringUtils.isBlank(collateralId))
    {
      throw new UseCaseException(COLLATERAL_ID_NULL_CODE, COLLATERAL_ID_NULL_MESSAGE);
    }

    GetSavedCollateralUDFields getSavedCollateralUDFields = new GetSavedCollateralUDFields(processRepository);
    GetSavedCollateralUDFieldsInput getSavedCollateralUDFieldsInput = new GetSavedCollateralUDFieldsInput(collateralId);
    GetSavedCollateralUDFieldsOutput output = getSavedCollateralUDFields.execute(getSavedCollateralUDFieldsInput);

    return RestResponse.success(output.getUdFields());
  }

  @ApiOperation("Get Process Parameter by parameter name")
  @GetMapping("/instanceId/{instanceId}/parameterName/{parameterName}")
  public ResponseEntity<RestResult> getProcessParameterByName(@PathVariable String instanceId, @PathVariable String parameterName) throws UseCaseException
  {
    if (StringUtils.isBlank(instanceId))
    {
      throw new UseCaseException(PROCESS_INSTANCE_ID_NULL_CODE, PROCESS_INSTANCE_ID_NULL_MESSAGE);
    }

    if (StringUtils.isBlank(parameterName))
    {
      throw new UseCaseException(PARAMETER_NAME_NULL_CODE, PARAMETER_NAME_NULL_MESSAGE);
    }

    GetProcessParameterInput input = new GetProcessParameterInput(instanceId, parameterName, null);
    GetProcessParameterByName useCase = new GetProcessParameterByName(processRepository);
    final Process process = useCase.execute(input);

    if (null == process)
    {
      return RestResponse.success(null);
    }

    return RestResponse.success(toRestProcess(process));
  }

  @ApiOperation("Gets Process with all information")
  @GetMapping("/{processInstanceId}")
  public ResponseEntity<RestResult> getProcess(@PathVariable String processInstanceId) throws UseCaseException
  {
    if (StringUtils.isBlank(processInstanceId))
    {
      throw new UseCaseException(PROCESS_INSTANCE_ID_NULL_CODE, PROCESS_INSTANCE_ID_NULL_MESSAGE);
    }

    GetProcessInput getProcessInput = new GetProcessInput(processInstanceId);
    GetProcess getProcess = new GetProcess(authenticationService, authorizationService, processRepository);
    GetProcessOutput output = getProcess.execute(getProcessInput);

    if (output.getReturnedProcess() == null)
    {
      return RestResponse.success("Process does not exist!");
    }

    RestProcess restProcess = toRestProcess(output.getReturnedProcess());

    return RestResponse.success(restProcess);
  }

  private ResponseEntity<RestResult> saveLoanContractParameter(String processInstanceId, Map<String, Object> parameters, String entityType,
      String defKey) throws UseCaseException, BpmServiceException
  {
    List<String> nullFields = new ArrayList<>();
    Map<String, Object> tableMap = new HashMap<>();
    String user = String.valueOf(parameters.get("user"));
    nullFields.add("user");
    for (Map.Entry<String, Object> entry : parameters.entrySet())
    {
      if (entry.getValue() == null)
      {
        nullFields.add(entry.getKey());
      }
      if (entry.getKey().equals("table") || entry.getKey().equals("filterKey"))
      {
        nullFields.add(entry.getKey());
        tableMap.put(entry.getKey(), entry.getValue());
      }
    }
    for (String key : nullFields)
    {
      parameters.remove(key);
    }

    UpdateLoanContractParameter updateLoanContractParameter = new UpdateLoanContractParameter(loanContractParameterRepository, authenticationService,
        authorizationService);
    LoanContractParameterInput input = new LoanContractParameterInput(processInstanceId, parameters, entityType, defKey);
    input.setTableMap(tableMap);
    updateLoanContractParameter.execute(input);

    UpdateLoanContractRequestInput updateLoanContractRequestInput = new UpdateLoanContractRequestInput(processInstanceId, user);
    UpdateLoanContractRequest updateLoanContractRequest = new UpdateLoanContractRequest(loanContractRequestRepository);
    final Object requestId = caseService.getVariableById(processInstanceId, PROCESS_REQUEST_ID);
    updateLoanContractRequest.execute(updateLoanContractRequestInput);
    LOGGER.info("######### ASSIGNED PROCESS REQUEST = {} TO USER {}", requestId, user);
    return RestResponse.success(true);
  }

  private RestProcess toRestProcess(Process process)
  {
    RestProcess restProcess = new RestProcess();
    restProcess.setProcessInstanceId(process.getId().getId());
    restProcess.setProcessParameters(process.getProcessParameters());
    if (null != process.getStartedTime())
    {
      restProcess.setStartedTime(process.getStartedTime().toString());
    }
    if (process.getFinishedTime() != null)
    {
      restProcess.setFinishedTime(process.getFinishedTime().toString());
    }
    return restProcess;
  }

  private void saveParametersToCaseVariable(List<RestCompletedFormField> restCompletedForms, String caseInstanceId) throws UseCaseException
  {
    Map<String, Object> parameters = new HashMap<>();
    for (RestCompletedFormField completedForm : restCompletedForms)
    {
      Object defaultValue = completedForm.getFormFieldValue().getDefaultValue();
      if (completedForm.getType().equals(DATE_PREFIX) && null != defaultValue)
      {
        try
        {
          Date dateValue = convertStringToDate(DATE_FORMAT2, (String) defaultValue);
          SetCaseVariableById setCaseVariableById = new SetCaseVariableById(caseService);
          setCaseVariableById.execute(new SetCaseVariableByIdInput(caseInstanceId, completedForm.getId(), dateValue));
        }
        catch (ParseException e)
        {
          throw new UseCaseException(STRING_TO_DATE_EXCEPTION_CODE, STRING_TO_DATE_EXCEPTION_MESSAGE);
        }
      }
      else
      {
        parameters.put(completedForm.getId(), completedForm.getFormFieldValue().getDefaultValue());
      }
    }

    SubmitFormInput setCaseVariablesInput = new SubmitFormInput("", caseInstanceId, parameters);
    new SetCaseVariables(caseService).execute(setCaseVariablesInput);
  }
}
