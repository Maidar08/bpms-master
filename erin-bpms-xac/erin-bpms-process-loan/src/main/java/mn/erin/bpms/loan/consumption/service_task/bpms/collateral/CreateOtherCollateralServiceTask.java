package mn.erin.bpms.loan.consumption.service_task.bpms.collateral;

import java.util.Map;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.bpm.service.NewCoreBankingService;
import mn.erin.domain.bpm.usecase.collateral.CreateOtherCollateral;

import static mn.erin.bpms.loan.consumption.utils.CreateCollateralUtils.removeFormValues;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.util.process.CollateralUtils.toOtherCollateralInfoMap;

public class CreateOtherCollateralServiceTask implements JavaDelegate
{
  private static final Logger LOG = LoggerFactory.getLogger(CreateOtherCollateralServiceTask.class);

  private final String[] formVariableNames = new String[] {
      "collateralCode", "formNumber", "collateralCifNumber", "receivedDate", "reviewDate", "remarks",
      "inspectionType", "inspectionAmountValue", "amountOfCollateral", "deductionRate", "collateralAssessment", "inspectorId", "inspectionDate",
      "ownershipType", "ownerCifNumber", "ownerName"
  };

  public final NewCoreBankingService newCoreBankingService;

  public CreateOtherCollateralServiceTask(NewCoreBankingService newCoreBankingService)
  {
    this.newCoreBankingService = newCoreBankingService;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String requestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    String caseInstanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));
    LOG.info("###### CREATING OTHER COLLATERAL  with REQUEST ID = [{}]", requestId);

    Map<String, Object> variables = execution.getVariables();

    CaseService caseService = execution.getProcessEngine().getCaseService();
    Map<String, Object> caseVariables = caseService.getVariables(caseInstanceId);

    CreateOtherCollateral createOtherCollateral = new CreateOtherCollateral(newCoreBankingService);
    String collateralId = createOtherCollateral.execute(toOtherCollateralInfoMap(variables, caseVariables));
    LOG.info("###### SUCCESSFUL CREATED OTHER COLLATERAL = [{}] with REQUEST ID = [{}]", collateralId, requestId);
    removeFormValues(caseInstanceId, execution, formVariableNames);
  }
}
