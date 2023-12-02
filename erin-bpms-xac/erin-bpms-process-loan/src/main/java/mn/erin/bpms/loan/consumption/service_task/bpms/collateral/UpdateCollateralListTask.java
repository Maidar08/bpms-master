package mn.erin.bpms.loan.consumption.service_task.bpms.collateral;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.bpm.model.collateral.Collateral;
import mn.erin.domain.bpm.model.collateral.CollateralId;
import mn.erin.domain.bpm.model.collateral.CollateralInfo;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.model.process.Process;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParameters;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParametersInput;

import static mn.erin.domain.bpm.BpmModuleConstants.AMOUNT_OF_COLLATERAL;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_ASSESSMENT;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_LIST;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_LIST_FORM_XAC_BANK;
import static mn.erin.domain.bpm.BpmModuleConstants.STATE_REGISTRATION_NUMBER;
import static mn.erin.domain.bpm.util.process.BpmUtils.convertJsonStringToMap;
import static mn.erin.domain.bpm.util.process.BpmUtils.convertMapToJsonString;

/**
 * @author Lkhagvadorj
 */
public class UpdateCollateralListTask implements JavaDelegate
{
  private final ProcessRepository processRepository;
  private final AuthenticationService authenticationService;
  private final AuthorizationService authorizationService;

  public UpdateCollateralListTask(ProcessRepository processRepository, AuthenticationService authenticationService, AuthorizationService authorizationService)
  {
    this.processRepository = Objects.requireNonNull(processRepository, "ProcessRepository is required!");
    this.authenticationService = authenticationService;
    this.authorizationService = authorizationService;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    if (null != execution.getVariable(COLLATERAL_LIST))
    {
      String processInstanceId = (String) execution.getVariable(CASE_INSTANCE_ID);
      Map<String, Serializable> collateralList = new HashMap<>();
      collateralList.put(COLLATERAL_LIST, new Gson().toJson(execution.getVariable(COLLATERAL_LIST)));
      Map<ParameterEntityType, Map<String, Serializable>> updateList = new HashMap<>();
      updateList.put(ParameterEntityType.COLLATERAL_LIST, collateralList);

      UpdateProcessParametersInput input = new UpdateProcessParametersInput(processInstanceId, updateList);
      UpdateProcessParameters updateProcessParameters = new UpdateProcessParameters(authenticationService, authorizationService, processRepository);
      updateProcessParameters.execute(input);

      setCollateralOnExecution(execution);
    }
  }

  @SuppressWarnings("unchecked")
  private void setCollateralOnExecution(DelegateExecution execution) throws JsonProcessingException
  {
    List<Map<String, Serializable>> colList = (List<Map<String, Serializable>>) execution.getVariable(COLLATERAL_LIST);
    Map<String, Serializable> collateralList = colList.get(0);
    String cif = String.valueOf(execution.getVariable(CIF_NUMBER));
    if (!collateralList.isEmpty())
    {
      for (Map.Entry<String, Serializable> entry : collateralList.entrySet())
      {
        Map<String, Boolean> checked = (Map<String, Boolean>) entry.getValue();
        if (checked.get("checked") && !execution.hasVariable(entry.getKey()))
        {
          Map<String, Object> colKey = new HashMap<>();
          colKey.put(CIF_NUMBER, cif);
          colKey.put(COLLATERAL_ID, entry.getKey());
          String colKeyString = convertMapToJsonString(colKey);
          Process process = processRepository.filterByParameterNameOnly(colKeyString);
          Collateral collateral = getCollateral(process, entry, colKeyString);
          if (null != collateral)
          {
            execution.setVariable(entry.getKey(), collateral);
          }
        }
      }
    }
    setXacColListOnExecution(execution, collateralList);
  }

  @SuppressWarnings("unchecked")
  private Collateral getCollateral(Process process, Map.Entry<String, Serializable> entry, String colKey)
  {
    if (null != process)
    {
      Map<ParameterEntityType, Map<String, Serializable>> processParameters = process.getProcessParameters();
      Map<String, Serializable> collateralMap = processParameters.get(ParameterEntityType.COLLATERAL);
      Map<String, Serializable> collaterals = (Map<String, Serializable>) convertJsonStringToMap(String.valueOf(collateralMap.get(colKey)));
      BigDecimal assessment = new BigDecimal(String.valueOf(collaterals.get(COLLATERAL_ASSESSMENT)));
      BigDecimal amountOfCollateral = new BigDecimal(String.valueOf(collaterals.get(AMOUNT_OF_COLLATERAL)));
      String stateRegistrationNumber = String.valueOf(collaterals.get(STATE_REGISTRATION_NUMBER));

      String collateralId = entry.getKey();
      Collateral collateral = new Collateral(CollateralId.valueOf(collateralId), null, null, null);
      collateral.setAmountOfAssessment(amountOfCollateral);
      CollateralInfo collateralInfo = new CollateralInfo(null, null, null, assessment);
      collateralInfo.setStateRegistrationNumber(stateRegistrationNumber);
      collateral.setCollateralInfo(collateralInfo);

      Process processUdf = processRepository.filterByParameterNameOnly(collateralId);
      if (null != processUdf)
      {
        Map<ParameterEntityType, Map<String, Serializable>> parameters = processUdf.getProcessParameters();
        Serializable jsonString = parameters.get(ParameterEntityType.UD_FIELD_COLLATERAL).get(collateralId);
        Map<String, Serializable> colUdfMap = convertJsonStringToMap(String.valueOf(jsonString));
        String ownerNames = String.valueOf(colUdfMap.get("BARITSAA HURUNGU UMCHLUGSDIIN NERS"));
        collateral.setOwnerNames(Collections.singletonList(ownerNames));
      }
      return collateral;
    }
    return null;
  }

  private void setXacColListOnExecution(DelegateExecution execution, Map<String, Serializable> collateralList)
  {
    String instanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));
    CaseService caseService = execution.getProcessEngine().getCaseService();

    List<Collateral> xacCollaterals = new ArrayList<>();
    for (Map.Entry<String, Serializable> entry : collateralList.entrySet())
    {
      Object executionCol = execution.getVariable(entry.getKey());
      Object caseExecCol = caseService.getVariable(instanceId, entry.getKey());
      if (caseExecCol instanceof Collateral)
      {
        Collateral collateral = (Collateral) caseService.getVariable(instanceId, entry.getKey());
        xacCollaterals.add(collateral);
        execution.setVariable(entry.getKey(), collateral);
      }
      else if (executionCol instanceof Collateral)
      {
        xacCollaterals.add((Collateral) execution.getVariable(entry.getKey()));
      }
    }

    if (!xacCollaterals.isEmpty())
    {
      execution.setVariable(COLLATERAL_LIST_FORM_XAC_BANK, xacCollaterals);
      caseService.setVariable(instanceId, COLLATERAL_LIST_FORM_XAC_BANK, xacCollaterals);
    }
  }
}
