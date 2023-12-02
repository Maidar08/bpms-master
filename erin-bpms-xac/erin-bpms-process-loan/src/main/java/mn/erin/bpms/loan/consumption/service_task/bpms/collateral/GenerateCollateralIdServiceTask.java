package mn.erin.bpms.loan.consumption.service_task.bpms.collateral;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.usecase.process.collateral.GetGeneratedCollateralId;
import mn.erin.domain.bpm.usecase.process.process_parameter.ProcessParameterByNameAndEntityInput;

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

/**
 * @author Lkhagvadorj.A
 */
public class GenerateCollateralIdServiceTask implements JavaDelegate
{
  private final ProcessRepository processRepository;
  private final String[] variableNames = new String[] {
      COLLATERAL_DESCRIPTION, COLLATERAL_VALUE, COLLATERAL_BASIC_TYPE, FORM_OF_OWNERSHIP, AMOUNT_OF_COLLATERAL, COLLATERAL_SUB_TYPE, STATE_REGISTRATION_NUMBER,
      DEDUCTION_RATE, PRODUCT, START_DATE, COLLATERAL_ASSESSMENT, LOCATION_OF_COLLATERAL, TYPE_OF_ASSESSMENT
  };

  public GenerateCollateralIdServiceTask(ProcessRepository processRepository)
  {
    this.processRepository = Objects.requireNonNull(processRepository, "ProcessRepository is required!");
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String cifNumber = (String) execution.getVariable(CIF_NUMBER);
    ParameterEntityType entityType = ParameterEntityType.COLLATERAL;

    ProcessParameterByNameAndEntityInput input = new ProcessParameterByNameAndEntityInput(CIF_NUMBER, cifNumber, entityType);
    GetGeneratedCollateralId getGeneratedCollateralId = new GetGeneratedCollateralId(processRepository);
    String newCollateralId = getGeneratedCollateralId.execute(input);

    setVariable(execution, newCollateralId);
  }

  private void setVariable(DelegateExecution execution, String newCollateralId)
  {
    Map<String, Object> variables = new HashMap<>();
    Arrays.stream(variableNames).forEach(varName -> variables.put(varName, null));
    execution.setVariable(COLLATERAL_ID, newCollateralId);
    execution.setVariables(variables);
    setCaseServiceVariable(execution, newCollateralId, variables);
  }

  private void setCaseServiceVariable(DelegateExecution execution, String newCollateralId, Map<String, Object> variables)
  {
    CaseService caseService = execution.getProcessEngineServices().getCaseService();
    String caseInstanceId = (String) execution.getVariable(CASE_INSTANCE_ID);
    caseService.setVariable(caseInstanceId, COLLATERAL_ID, newCollateralId);
    caseService.setVariables(caseInstanceId, variables);
  }
}
