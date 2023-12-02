package mn.erin.bpms.loan.consumption.service_task.bpms.collateral;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils;
import mn.erin.bpms.process.base.ProcessTaskException;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.collateral.Collateral;
import mn.erin.domain.bpm.model.collateral.CollateralId;
import mn.erin.domain.bpm.model.collateral.CollateralInfo;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.model.process.Process;
import mn.erin.domain.bpm.model.product.CollateralProduct;
import mn.erin.domain.bpm.repository.CollateralProductRepository;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParameters;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParametersInput;
import mn.erin.domain.bpm.usecase.process.collateral.GetGeneratedCollateralId;
import mn.erin.domain.bpm.usecase.process.process_parameter.ProcessParameterByNameAndEntityInput;
import mn.erin.domain.bpm.usecase.product.GetFilteredCollateralProduct;
import mn.erin.domain.bpm.usecase.product.GetFilteredCollateralProductInput;

import static mn.erin.domain.bpm.BpmMessagesConstants.COLLATERAL_PRODUCT_NOT_FOUND_WO_COLLID_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.COLLATERAL_PRODUCT_NOT_FOUND_WO_COLLID_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.AMOUNT_OF_COLLATERAL;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_ASSESSMENT;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_BASIC_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_DESCRIPTION;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_SUB_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_VALUE;
import static mn.erin.domain.bpm.BpmModuleConstants.DEDUCTION_RATE;
import static mn.erin.domain.bpm.BpmModuleConstants.FORM_OF_OWNERSHIP;
import static mn.erin.domain.bpm.BpmModuleConstants.LOCATION_OF_COLLATERAL;
import static mn.erin.domain.bpm.BpmModuleConstants.PRODUCT;
import static mn.erin.domain.bpm.BpmModuleConstants.START_DATE;
import static mn.erin.domain.bpm.BpmModuleConstants.STATE_REGISTRATION_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.TYPE_OF_ASSESSMENT;
import static mn.erin.domain.bpm.util.process.BpmUtils.convertJsonStringToMap;

/**
 * @author Lkhagvadorj.A
 */

public class SaveCollateralTask implements JavaDelegate
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SaveCollateralTask.class);

    private final ProcessRepository processRepository;
    private final AuthenticationService authenticationService;
    private final AuthorizationService authorizationService;
    private final CollateralProductRepository collateralProductRepository;

    private final String[] variableNames = new String[]{
            COLLATERAL_ID, COLLATERAL_DESCRIPTION, COLLATERAL_VALUE, COLLATERAL_BASIC_TYPE, FORM_OF_OWNERSHIP, AMOUNT_OF_COLLATERAL, COLLATERAL_SUB_TYPE, STATE_REGISTRATION_NUMBER,
            DEDUCTION_RATE, PRODUCT, START_DATE, COLLATERAL_ASSESSMENT, LOCATION_OF_COLLATERAL, TYPE_OF_ASSESSMENT
    };

    public SaveCollateralTask(ProcessRepository processRepository, AuthenticationService authenticationService,
        AuthorizationService authorizationService, CollateralProductRepository collateralProductRepository)
    {
        this.processRepository = Objects.requireNonNull(processRepository, "ProcessRepository is required!");
        this.authenticationService = authenticationService;
        this.authorizationService = authorizationService;
        this.collateralProductRepository = collateralProductRepository;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception
    {
        validateCollateralTypes(execution);

        String cifNumber = (String) execution.getVariable(CIF_NUMBER);
        ParameterEntityType entityType = ParameterEntityType.COLLATERAL;
        String collateralId = getCollateralId(cifNumber, entityType);
        String processInstanceId = (String) execution.getVariable(CASE_INSTANCE_ID);

        Map<ParameterEntityType, Map<String, Serializable>> parameters = new HashMap<>();
        setCollateralParameters(cifNumber, collateralId, processInstanceId, parameters, execution);
        saveCollateralParameters(processInstanceId, parameters);
        removeValues(execution);
    }

    private void validateCollateralTypes(DelegateExecution execution) throws ProcessTaskException, UseCaseException
    {
        String collateralBasicType = (String) execution.getVariable("collateralBasicType");
        String collateralSubType = (String) execution.getVariable("collateralSubType");
        String description = (String) execution.getVariable("product");

        GetFilteredCollateralProduct getFilteredCollateralProduct = new GetFilteredCollateralProduct(collateralProductRepository);
        CollateralProduct product = getFilteredCollateralProduct.execute(new GetFilteredCollateralProductInput(collateralBasicType, collateralSubType, description));
        String logMessage;
        if (product == null)
        {
            logMessage = String.format(COLLATERAL_PRODUCT_NOT_FOUND_WO_COLLID_MESSAGE, collateralBasicType, collateralSubType, description);
            LOGGER.error(logMessage);
            throw new ProcessTaskException(COLLATERAL_PRODUCT_NOT_FOUND_WO_COLLID_CODE, logMessage);
        }

    }

    private void removeValues(DelegateExecution execution)
    {
        CaseService caseService = execution.getProcessEngineServices().getCaseService();
        String caseInstanceId = (String) execution.getVariable(CASE_INSTANCE_ID);

        execution.removeVariables(Arrays.asList(variableNames));
        caseService.removeVariable(caseInstanceId, String.valueOf(variableNames));
        caseService.removeVariable(caseInstanceId, COLLATERAL_ASSESSMENT);
    }

    private void setCollateralParameters(String cifNumber, String collateralId, String processInstanceId, Map<ParameterEntityType, Map<String, Serializable>> parameters, DelegateExecution execution)
        throws JsonProcessingException, UseCaseException
    {
        parameters.put( ParameterEntityType.COLLATERAL, new HashMap<>() );
        String collateralValueAsString = new ObjectMapper().writeValueAsString( getCollateralFields(collateralId, processInstanceId, execution) );
        String parameterKey = convertJsonParameterIntoString( new String[]{CIF_NUMBER, COLLATERAL_ID}, new String[]{cifNumber, collateralId} );
        parameters.get(ParameterEntityType.COLLATERAL).put( parameterKey, collateralValueAsString );
    }

    private String convertJsonParameterIntoString(String[] key, String[] value)
    {
        if (key.length != value.length)
        {
            return null;
        }

        JSONObject collateralJson = new JSONObject();
        for (int i = 0 ; i < key.length; i ++)
        {
            collateralJson.put( key[i], value[i] );
        }
        return collateralJson.toString();
    }

    private Map<String, String> getCollateralFields(String collateralId, String processInstanceId, DelegateExecution execution) throws UseCaseException
    {
        Map<String, String> collateralParameters = new HashMap<>();
        long collateralValue = (long) execution.getVariable(COLLATERAL_VALUE);
        long amountOfCollateral = (long) execution.getVariable(AMOUNT_OF_COLLATERAL);
        long assessment = getCollateralAssessment(processInstanceId, execution);
        long deductionRate = (long) execution.getVariable(DEDUCTION_RATE);
        Object dateObject = execution.getVariable(START_DATE);
        String startDate = null == dateObject ? "" : dateObject.toString();
        String stateRegistrationNumber = (String) execution.getVariable(STATE_REGISTRATION_NUMBER);

        collateralParameters.put(COLLATERAL_ID, collateralId);
        collateralParameters.put(COLLATERAL_DESCRIPTION, (String) execution.getVariable(COLLATERAL_DESCRIPTION));
        collateralParameters.put(COLLATERAL_VALUE, Float.toString(collateralValue));
        collateralParameters.put(COLLATERAL_BASIC_TYPE, (String) execution.getVariable(COLLATERAL_BASIC_TYPE));
        collateralParameters.put(FORM_OF_OWNERSHIP, (String) execution.getVariable(FORM_OF_OWNERSHIP));
        collateralParameters.put(AMOUNT_OF_COLLATERAL, String.valueOf(amountOfCollateral));
        collateralParameters.put(COLLATERAL_SUB_TYPE, (String) execution.getVariable(COLLATERAL_SUB_TYPE));
        collateralParameters.put(STATE_REGISTRATION_NUMBER, stateRegistrationNumber);
        collateralParameters.put(DEDUCTION_RATE, String.valueOf(deductionRate));
        collateralParameters.put(PRODUCT, (String) execution.getVariable(PRODUCT));
        collateralParameters.put(START_DATE, startDate);
        collateralParameters.put(COLLATERAL_ASSESSMENT, String.valueOf(assessment));
        collateralParameters.put(LOCATION_OF_COLLATERAL, (String) execution.getVariable(LOCATION_OF_COLLATERAL));
        collateralParameters.put(TYPE_OF_ASSESSMENT, (String) execution.getVariable(TYPE_OF_ASSESSMENT));

        saveCollateralOnExecution(execution, processInstanceId, collateralId, stateRegistrationNumber, assessment, amountOfCollateral);

        return collateralParameters;
    }

    private void saveCollateralOnExecution(DelegateExecution execution, String processInstanceId, String collateralId, String stateRegistrationNumber, long assessment, long amountOfCollateral)
    {
        Collateral collateral = new Collateral(CollateralId.valueOf(collateralId), null, null, null);
        collateral.setAmountOfAssessment(BigDecimal.valueOf(amountOfCollateral));
        CollateralInfo collateralInfo = new CollateralInfo(null, null, null, BigDecimal.valueOf(assessment));
        collateralInfo.setStateRegistrationNumber(stateRegistrationNumber);
        collateral.setCollateralInfo(collateralInfo);

        Process process = processRepository.filterByParameterNameOnly(collateralId);
        if (null != process)
        {
            Map<ParameterEntityType, Map<String, Serializable>> processParameters = process.getProcessParameters();
            Serializable jsonString = processParameters.get(ParameterEntityType.UD_FIELD_COLLATERAL).get(collateralId);
            @SuppressWarnings("unchecked")
            Map<String, Serializable > colUdfMap = convertJsonStringToMap(String.valueOf(jsonString));
            String ownerNames = String.valueOf(colUdfMap.get("BARITSAA HURUNGU UMCHLUGSDIIN NERS"));
            collateral.setOwnerNames(Collections.singletonList(ownerNames));
        }

        execution.setVariable(collateralId, collateral);
        execution.getProcessEngine().getCaseService().setVariable(processInstanceId, collateralId, collateral);
    }

    private String getCollateralId(String cifNumber, ParameterEntityType entityType) throws UseCaseException
    {
        ProcessParameterByNameAndEntityInput processParameterInput = new ProcessParameterByNameAndEntityInput(CIF_NUMBER, cifNumber, entityType);
        GetGeneratedCollateralId getGeneratedCollateralId = new GetGeneratedCollateralId(processRepository);
        return getGeneratedCollateralId.execute(processParameterInput);
    }

    private void saveCollateralParameters(String processInstanceId, Map<ParameterEntityType, Map<String, Serializable>> parameters)throws UseCaseException
    {
        UpdateProcessParametersInput updateProcessParametersInput = new UpdateProcessParametersInput(processInstanceId, parameters);
        UpdateProcessParameters updateProcessParameters = new UpdateProcessParameters(authenticationService, authorizationService, processRepository);
        updateProcessParameters.execute(updateProcessParametersInput);
    }

    private long getCollateralAssessment(String processInstanceId, DelegateExecution execution)
    {
        return  (int) CaseExecutionUtils.getCaseVariables( processInstanceId, execution.getProcessEngine() ).get( COLLATERAL_ASSESSMENT );
    }
}
