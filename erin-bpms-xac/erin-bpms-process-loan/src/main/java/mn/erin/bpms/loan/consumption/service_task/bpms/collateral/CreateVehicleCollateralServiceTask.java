package mn.erin.bpms.loan.consumption.service_task.bpms.collateral;

import java.util.Map;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.bpm.service.NewCoreBankingService;
import mn.erin.domain.bpm.usecase.collateral.CreateVehicleCollateral;

import static mn.erin.bpms.loan.consumption.utils.CreateCollateralUtils.removeFormValues;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.util.process.CollateralUtils.toVehicleCollateralMap;

/**
 * @author Odgavaa
 */
public class CreateVehicleCollateralServiceTask implements JavaDelegate
{
  private static final Logger LOG = LoggerFactory.getLogger(CreateVehicleCollateralServiceTask.class);
  private final String[] formVariableNames = new String[] {
      "vehicleRegisterNumber", "collateralCode", "reviewDate", "chassisNumber", "engineNumber", "manufactureYear", "vehicleModel",
      "financialLeasingSupplier", "purposeOfUsage", "formNumber", "address1", "address2", "address3", "remarks", "inspectionType",
      "customerCif", "inspectionAmountValue", "amountOfCollateral", "deductionRate", "collateralAssessment", "employeeId",
      "inspectionDate", "ownershipType", "ownerCifNumber", "ownerName"
  };

  private final NewCoreBankingService newCoreBankingService;

  public CreateVehicleCollateralServiceTask(NewCoreBankingService newCoreBankingService)
  {
    this.newCoreBankingService = newCoreBankingService;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String caseInstanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));
    String requestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    LOG.info("###### CREATING VEHICLE COLLATERAL with REQUEST ID = [{}]", requestId);
    Map<String, Object> variables = execution.getVariables();

    CaseService caseService = execution.getProcessEngine().getCaseService();
    Map<String, Object> caseVariables = caseService.getVariables(caseInstanceId);
    CreateVehicleCollateral createVehicleCollateral = new CreateVehicleCollateral(newCoreBankingService);
    String createdCollateralCode = createVehicleCollateral.execute(toVehicleCollateralMap(variables, caseVariables));

    LOG.info("###### SUCCESSFUL CREATED VEHICLE COLLATERAL = [{}] with REQUEST ID = [{}]", createdCollateralCode, requestId);
    removeFormValues(caseInstanceId, execution, formVariableNames);
  }
}