package mn.erin.domain.bpm.usecase.process.collateral.updateCollateral;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.form.CompletedFormField;
import mn.erin.domain.bpm.model.form.FieldProperty;
import mn.erin.domain.bpm.model.form.FieldValidation;
import mn.erin.domain.bpm.model.form.FormFieldValue;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.model.process.Process;
import mn.erin.domain.bpm.model.product.CollateralProduct;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.CollateralProductRepository;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.usecase.process.UpdateProcessLargeParameters;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParameters;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParametersInput;
import mn.erin.domain.bpm.usecase.product.GetFilteredCollateralProduct;
import mn.erin.domain.bpm.usecase.product.GetFilteredCollateralProductInput;

import static mn.erin.domain.bpm.BpmMessagesConstants.CLOB_CONVERSION_TO_STRING_FAIL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CLOB_CONVERSION_TO_STRING_FAIL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.COLLATERAL_PRODUCT_NOT_FOUND_WO_COLLID_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.COLLATERAL_PRODUCT_NOT_FOUND_WO_COLLID_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CREATE_COLLATERAL_TASK_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.DISABLED;
import static mn.erin.domain.bpm.BpmModuleConstants.FORM_FIELDS;
import static mn.erin.domain.bpm.BpmModuleConstants.FORM_FIELD_VALUE;
import static mn.erin.domain.bpm.BpmModuleConstants.FORM_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.ID;
import static mn.erin.domain.bpm.BpmModuleConstants.LABEL;
import static mn.erin.domain.bpm.BpmModuleConstants.OPTIONS;
import static mn.erin.domain.bpm.BpmModuleConstants.SPECIAL_FORMS;
import static mn.erin.domain.bpm.BpmModuleConstants.TASK_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.VALIDATIONS;

/**
 * @author Lkhagvadorj
 */
public class UpdateCollateral extends AbstractUseCase<UpdateCollateralInput, Boolean>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateCollateral.class);

  private final AuthenticationService authenticationService;
  private final AuthorizationService authorizationService;
  private final ProcessRepository processRepository;
  private final CollateralProductRepository collateralProductRepository;

  // Collateral can be from other processes with other instances
  private String updateInstanceId;

  public UpdateCollateral(AuthenticationService authenticationService, AuthorizationService authorizationService,
      ProcessRepository processRepository, CollateralProductRepository collateralProductRepository)
  {
    this.authenticationService = Objects.requireNonNull(authenticationService, "Authentication Service is required!");
    this.authorizationService = Objects.requireNonNull(authorizationService, "Authorization service is required!");
    this.processRepository = Objects.requireNonNull(processRepository, "Process repository is required!");
    this.collateralProductRepository = Objects.requireNonNull(collateralProductRepository, "Collateral Product repository is required!");
  }

  @Override
  public Boolean execute(UpdateCollateralInput input) throws UseCaseException
  {
    if (null == input)
    {
      return false;
    }

    final String instanceId = input.getInstanceId();
    final String collateralId = input.getCollateralId();
    final ParameterEntityType entityType = input.getEntityType();

    if (null == instanceId || null == collateralId || null == entityType)
    {
      return false;
    }

    int numUpdated;
    try
    {
      numUpdated = updateProcessLargeParameter(collateralId, input.getTaskFormFields());
      if (numUpdated < 0)
      {
        return false;
      }
    }
    catch (BpmRepositoryException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }

    numUpdated = updateProcessParameter(collateralId, entityType, input.getTaskFormFields());

    return numUpdated >= 0;
  }

  private int updateProcessParameter(String collateralId, ParameterEntityType entityType,
      Collection<CompletedFormField> taskFormFields) throws UseCaseException
  {
    final String cifNUmber = collateralId.substring(4, collateralId.length() - 3);

    JSONObject paramNameJson = new JSONObject();
    paramNameJson.put(CIF_NUMBER, cifNUmber);
    paramNameJson.put(COLLATERAL_ID, collateralId);

    Map<ParameterEntityType, Map<String, Serializable>> parameters = new HashMap<>();
    Map<String, Serializable> forms = new HashMap<>();
    String formFieldsString = mapTaskFormFields(taskFormFields);
    forms.put( paramNameJson.toString(), formFieldsString);
    parameters.put(entityType, forms );

    UpdateProcessParametersInput updateParameterInput = new UpdateProcessParametersInput(updateInstanceId, parameters);
    UpdateProcessParameters updateProcessParameters = new UpdateProcessParameters(authenticationService, authorizationService, processRepository);
    return updateProcessParameters.execute(updateParameterInput).getNumUpdated();
  }

  private int updateProcessLargeParameter(String collateralId, Collection<CompletedFormField> taskFormFields)
      throws UseCaseException, BpmRepositoryException
  {
    ParameterEntityType entity = ParameterEntityType.COMPLETED_FORM;
    final List<String> params = getParameterNameAndFormId(collateralId, entity);

    if (params.size() != 2)
    {
      return -1;
    }

    Map<ParameterEntityType, Map<String, Serializable>> parameters = getFinalUpdateParameters(entity, taskFormFields, params);

    if (parameters.isEmpty())
    {
      return -1;
    }

    UpdateProcessParametersInput updateParameterInput = new UpdateProcessParametersInput(updateInstanceId, parameters);
    UpdateProcessLargeParameters updateProcessLargeParameters = new UpdateProcessLargeParameters(authenticationService, authorizationService, processRepository);
    return updateProcessLargeParameters.execute(updateParameterInput).getNumUpdated();
  }

  private String mapTaskFormFields(Collection<CompletedFormField> taskFormFields) throws UseCaseException
  {
    JSONObject jsonObject = new JSONObject();

    String basicType = "";
    String subType = "";
    String description = "";

    for (CompletedFormField formField : taskFormFields)
    {
      putToJSON( jsonObject, formField.getId(), formField.getFormFieldValue().getDefaultValue() );
      if (formField.getId().equals("collateralSubType") && formField.getFormFieldValue().getDefaultValue() != null)
      {
        subType = formField.getFormFieldValue().getDefaultValue().toString();
      }
      if (formField.getId().equals("collateralBasicType") && formField.getFormFieldValue().getDefaultValue() != null)
      {
        basicType = formField.getFormFieldValue().getDefaultValue().toString();
      }
      if (formField.getId().equals("product") && formField.getFormFieldValue().getDefaultValue() != null)
      {
        description = formField.getFormFieldValue().getDefaultValue().toString();
      }
    }

    validateCollateralTypes(basicType, subType, description);

    return jsonObject.toString();
  }

  private void validateCollateralTypes(String basicType, String subType, String productDesc) throws UseCaseException
  {
    GetFilteredCollateralProduct getFilteredCollateralProduct = new GetFilteredCollateralProduct(collateralProductRepository);
    CollateralProduct product = getFilteredCollateralProduct.execute(new GetFilteredCollateralProductInput(basicType, subType, productDesc));
    String logMessage;
    if (product == null)
    {
      logMessage = String.format(COLLATERAL_PRODUCT_NOT_FOUND_WO_COLLID_MESSAGE, basicType, subType, productDesc);
      LOGGER.error(logMessage);
      throw new UseCaseException(COLLATERAL_PRODUCT_NOT_FOUND_WO_COLLID_CODE, logMessage);
    }

  }

  private Map<ParameterEntityType, Map<String, Serializable>> getFinalUpdateParameters(ParameterEntityType entity, Collection<CompletedFormField> taskFormFields, List<String> params)
  {
    final String parameterName = params.get(0);
    JSONObject paramJSON = new JSONObject( params.get(1) );
    final Collection<Map<String, Object>> jsonFormField = convertFormToString(taskFormFields);
    final String formId = (String) paramJSON.get(FORM_ID);
    updateInstanceId = (String) paramJSON.get(CASE_INSTANCE_ID);

    final String finalParams = getProcessLargeParameterValueString(formId, parameterName, updateInstanceId, jsonFormField);

    Map<ParameterEntityType, Map<String, Serializable>> parameters = new HashMap<>();
    Map<String, Serializable> forms = new HashMap<>();
    forms.put(parameterName, finalParams);
    parameters.put(entity, forms );
    return parameters;
  }

  private List<String> getParameterNameAndFormId(String collateralId, ParameterEntityType entity)
      throws BpmRepositoryException, UseCaseException
  {
    Process process = null;
    try
    {
      process = processRepository.filterLargeParamBySearchKeyFromValue(collateralId, CREATE_COLLATERAL_TASK_NAME, entity);
    }
    catch (SQLException exception)
    {
      throw new UseCaseException(CLOB_CONVERSION_TO_STRING_FAIL_CODE, CLOB_CONVERSION_TO_STRING_FAIL_MESSAGE);
    }
    if (null == process)
    {
      return Collections.emptyList();
    }
    final Map<String, Serializable> form = process.getProcessParameters().get(entity);

    for (Map.Entry<String, Serializable> entry: form.entrySet() )
    {
      if (entry.getValue().toString().contains(collateralId))
      {
        return Arrays.asList(entry.getKey(), entry.getValue().toString());
      }
    }
    return Collections.emptyList();
  }

  private Collection<Map<String, Object>> convertFormToString(Collection<CompletedFormField> taskFormFields)
  {
    Collection<Map<String, Object>> params = new ArrayList<>();
    JSONObject param = new JSONObject();
    taskFormFields.forEach( formField ->
    {
      final String id = formField.getId();
      final FormFieldValue formFieldValue = formField.getFormFieldValue();
      final String label = formField.getLabel();
      final String type = formField.getType();
      final Collection<FieldProperty> fieldProperties = formField.getOptions();
      final Collection<FieldValidation> fieldValidations = formField.getValidations();

      final boolean disabled = formField.getDisabled();

      putToJSON( param, ID, id );
      putToJSON( param, FORM_FIELD_VALUE, formFieldValue );
      putToJSON( param, LABEL, label );
      putToJSON( param, TYPE, type );
      putToJSON( param, VALIDATIONS, fieldValidations );
      putToJSON( param, OPTIONS, fieldProperties );
      putToJSON( param, DISABLED, disabled );

      params.add(param.toMap());
    } );

    if (params.isEmpty())
    {
      return Collections.emptyList();
    }

    return params;
  }

  private String getProcessLargeParameterValueString( String formId, String taskId, String caseInstanceId, Collection<Map<String, Object>> formFields)
  {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put(FORM_ID, formId);
    jsonObject.put(TASK_ID, taskId);
    jsonObject.put(CASE_INSTANCE_ID, caseInstanceId);
    jsonObject.put(FORM_FIELDS, formFields);
    jsonObject.put(SPECIAL_FORMS, new HashMap<>());

    return jsonObject.toString();
  }

  private void putToJSON(JSONObject updateMap, String key, Object value)
  {
    if (value == null)
    {
      updateMap.put(key, "");
    }
    else
    {
      updateMap.put(key, value);
    }
  }
}
