package mn.erin.bpms.loan.consumption.service_task.bpms.collateral;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.service.NewCoreBankingService;
import mn.erin.domain.bpm.usecase.collateral.CreateMachineryCollateral;
import mn.erin.domain.bpm.usecase.process.DeleteProcessParameters;
import mn.erin.domain.bpm.usecase.process.DeleteProcessParametersInput;

import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.AMOUNT_OF_COLLATERAL;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.COLLATERAL_ASSESSMENT;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.COLLATERAL_CODE;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.CUSTOMER_ID;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.DEDUCTION_RATE;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.FORM_NUMBER;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.INSPECTION_AMOUNT_VALUE;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.INSPECTION_DATE;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.INSPECTION_TYPE;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.INSPECTOR_ID;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.MACHINE_MODEL;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.MACHINE_NUMBER;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.MANUFACTURER;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.MANUFACTURER_YEAR;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.OWNERSHIP_TYPE;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.OWNER_CIF_NUMBER;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.OWNER_NAME;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.REMARKS;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.REVIEW_DATE;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.XAC_SERVICE_DATE_FORMATTER_2;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.util.process.CollateralUtils.toMachineryCollateralInfoMap;

/**
 * @author Lkhagvadorj.A
 **/

public class CreateMachineryCollateralTask implements JavaDelegate
{
  private final NewCoreBankingService newCoreBankingService;
  private final AuthenticationService authenticationService;
  private final AuthorizationService authorizationService;
  private final ProcessRepository processRepository;

  private static final Logger LOG = LoggerFactory.getLogger(CreateMachineryCollateralTask.class);
  private final SimpleDateFormat dateFormatter = new SimpleDateFormat(XAC_SERVICE_DATE_FORMATTER_2);
  private final String[] formVariableNames = new String[] {
      COLLATERAL_CODE, FORM_NUMBER, MANUFACTURER, MACHINE_NUMBER, MACHINE_MODEL, MANUFACTURER_YEAR, CUSTOMER_ID, REMARKS,
      REVIEW_DATE,
      INSPECTION_TYPE, INSPECTION_AMOUNT_VALUE, AMOUNT_OF_COLLATERAL, DEDUCTION_RATE, COLLATERAL_ASSESSMENT, INSPECTOR_ID, INSPECTION_DATE, OWNERSHIP_TYPE, OWNER_CIF_NUMBER,
      OWNER_NAME
  };

  public CreateMachineryCollateralTask(AuthenticationService authenticationService, AuthorizationService authorizationService,
      ProcessRepository processRepository, NewCoreBankingService newCoreBankingService)
  {
    this.authenticationService = authenticationService;
    this.authorizationService = authorizationService;
    this.processRepository = processRepository;
    this.newCoreBankingService = newCoreBankingService;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    Map<String, Object> variables = execution.getVariables();
    String caseInstanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));
    CaseService caseService = execution.getProcessEngine().getCaseService();
    Map<String, Object> caseVariables = caseService.getVariables(caseInstanceId);
    CreateMachineryCollateral useCase = new CreateMachineryCollateral(newCoreBankingService);
    String createdCollId = useCase.execute(toMachineryCollateralInfoMap(variables, caseVariables));
    String requestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));

    LOG.info("###### SUCCESSFUL CREATED MACHINERY COLLATERAL = [{}] with REQUEST ID = [{}]", createdCollId, requestId);
    removeFormValues(caseInstanceId, execution);
    removeProcessParameters(caseInstanceId, Arrays.asList(formVariableNames));
  }

  private void removeFormValues(String caseInstanceId, DelegateExecution execution)
  {
    CaseService caseService = execution.getProcessEngineServices().getCaseService();

    execution.removeVariables(Arrays.asList(formVariableNames));
    caseService.removeVariables(caseInstanceId, Arrays.asList(formVariableNames));
  }

  private void removeProcessParameters(String caseInstanceId, List<String> parameterNames) throws UseCaseException
  {
    DeleteProcessParameters useCase = new DeleteProcessParameters(authenticationService, authorizationService, processRepository);

    Map<ParameterEntityType, List<String>> deleteParams = new HashMap<>();
    deleteParams.put(ParameterEntityType.FORM, parameterNames);

    useCase.execute(new DeleteProcessParametersInput(caseInstanceId, deleteParams));
  }
}
