package consumption.service_task_bnpl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.service.AimServiceRegistry;
import mn.erin.domain.bpm.model.document.Document;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;
import mn.erin.domain.bpm.usecase.contract.CreateOnlineSalaryLoanContractDocument;
import mn.erin.domain.bpm.usecase.loan.GetLoanContractsByType;
import mn.erin.domain.bpm.usecase.loan.GetLoanContractsByTypeInput;
import mn.erin.domain.bpm.usecase.process.UpdateProcessLargeParameters;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParametersInput;

import static consumption.util.CamundaUtils.updateTaskStatus;
import static mn.erin.domain.bpm.BpmDocumentConstant.BNPL_REPORT;
import static mn.erin.domain.bpm.BpmDocumentConstant.INSTANT_LOAN_REPORT;
import static mn.erin.domain.bpm.BpmDocumentConstant.ONLINE_LEASING_REPORT;
import static mn.erin.domain.bpm.BpmModuleConstants.ACTION_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.BNPL_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.BNPL_REPORT_BASE64;
import static mn.erin.domain.bpm.BpmModuleConstants.ERROR_CAUSE;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_REPORT_BASE64;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.NULL_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_REPORT_BASE64;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.REQUEST_ID;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.P_ACC;
import static mn.erin.domain.bpm.model.process.ParameterEntityType.BNPL;
import static mn.erin.domain.bpm.model.process.ParameterEntityType.INSTANT_LOAN;
import static mn.erin.domain.bpm.model.process.ParameterEntityType.ONLINE_LEASING;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

public class PrepareLoanReportTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(PrepareLoanReportTask.class);
  private final BpmsServiceRegistry bpmsServiceRegistry;
  private final BpmsRepositoryRegistry bpmsRepositoryRegistry;
  private final AimServiceRegistry aimServiceRegistry;
  private String reportType;

  public PrepareLoanReportTask(BpmsServiceRegistry bpmsServiceRegistry, BpmsRepositoryRegistry bpmsRepositoryRegistry,
      AimServiceRegistry aimServiceRegistry)
  {
    this.bpmsServiceRegistry = bpmsServiceRegistry;
    this.bpmsRepositoryRegistry = bpmsRepositoryRegistry;
    this.aimServiceRegistry = aimServiceRegistry;
  }

  @Override
  public void execute(DelegateExecution execution)
  {
    try
    {
      String processInstanceId = String.valueOf(execution.getVariable(PROCESS_INSTANCE_ID));
      String requestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));

      String loanAccountNumber = String.valueOf(execution.getVariable(LOAN_ACCOUNT_NUMBER));
      boolean isBnpl = execution.getVariable(PROCESS_TYPE_ID).equals(BNPL_PROCESS_TYPE_ID);
      boolean isOnlineLeasing = execution.getVariable(PROCESS_TYPE_ID).equals(ONLINE_LEASING_PROCESS_TYPE_ID);
      Object processType = execution.getVariable(PROCESS_TYPE_ID);
      boolean hasNoAction = StringUtils.isBlank(getValidString(execution.getVariable(ACTION_TYPE)));

      checkProcessType(String.valueOf(processType));

      Map<String, Object> documentParam = new HashMap<>();
      documentParam.put(P_ACC, loanAccountNumber);
      documentParam.put(REQUEST_ID, requestId);
      GetLoanContractsByType getLoanContractsByType = new GetLoanContractsByType(bpmsServiceRegistry.getDocumentService(), bpmsServiceRegistry.getCaseService(),
          aimServiceRegistry.getAuthenticationService());
      GetLoanContractsByTypeInput bipInput = new GetLoanContractsByTypeInput(processInstanceId, reportType, documentParam);
      final List<Document> documentList = getLoanContractsByType.execute(bipInput);
      String base64 = null;
      if (!documentList.isEmpty())
      {
        base64 = documentList.get(0).getSource();
      }

      LOGGER.info("##### LOAN REPORT persisting to database. {}", hasNoAction ? "" : " ActionType :" + execution.getVariable(ACTION_TYPE) + ".");
      if (null == processInstanceId)
      {
        LOGGER.error("######### CASE INSTANCE ID is null during download loan report with REQUEST_ID =[{}]. {}", requestId,
            (hasNoAction ? "" : " ActionType :" + execution.getVariable(ACTION_TYPE) + "."));
        throw new BpmnError("Create Loan Report", "###### CASE INSTANCE ID is null! with REQUEST ID =" + requestId);
      }

      CreateOnlineSalaryLoanContractDocument createOnlineSalaryLoanContractDocument = new CreateOnlineSalaryLoanContractDocument(
          aimServiceRegistry.getAuthenticationService(), aimServiceRegistry.getAuthorizationService(),
          bpmsServiceRegistry.getCaseService(), bpmsRepositoryRegistry.getDocumentRepository());

      Map<String, String> inputMap = new HashMap<>();

      inputMap.put("instanceId", processInstanceId);
      inputMap.put(PROCESS_REQUEST_ID, requestId);
      inputMap.put(PROCESS_TYPE_ID, getValidString(execution.getVariable(PROCESS_TYPE_ID)));

      Boolean isCreated = createOnlineSalaryLoanContractDocument.execute(inputMap);

      if (null != isCreated && isCreated)
      {
        LOGGER.info("########## Successful create loan report document with REQUEST ID =[{}]. {}", requestId,
            (hasNoAction ? "" : " ActionType :" + execution.getVariable(ACTION_TYPE) + "."));
      }
      else
      {
        LOGGER.error("###### Could not create loan report document with REQUEST ID =[{}]. {}", requestId,
            (hasNoAction ? "" : " ActionType :" + execution.getVariable(ACTION_TYPE) + "."));
        throw new BpmnError("Create Loan Report", "###### Could not create loan report  document with REQUEST ID =" + requestId);
      }

      Map<ParameterEntityType, Map<String, Serializable>> processParam = new HashMap<>();
      Map<String, Serializable> param = new HashMap<>();
      if (isBnpl)
      {
        param.put(BNPL_REPORT_BASE64, base64);
        processParam.put(BNPL, param);
      }
      else if (isOnlineLeasing)
      {
        param.put(ONLINE_LEASING_REPORT_BASE64, base64);
        processParam.put(ONLINE_LEASING, param);
      }
      else
      {
        param.put(INSTANT_LOAN_REPORT_BASE64, base64);
        processParam.put(INSTANT_LOAN, param);
      }

      UpdateProcessLargeParameters updateProcessLargeParameters = new UpdateProcessLargeParameters(aimServiceRegistry.getAuthenticationService(),
          aimServiceRegistry.getAuthorizationService(), bpmsRepositoryRegistry.getProcessRepository());
      UpdateProcessParametersInput input = new UpdateProcessParametersInput(processInstanceId, processParam);
      updateProcessLargeParameters.execute(input);

      if (execution.getVariable(PROCESS_TYPE_ID).equals(INSTANT_LOAN_PROCESS_TYPE_ID))
      {
        updateTaskStatus(execution, "Create loan report document task", "Success");
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      String errorMessage = (e.getMessage() == null || e.getMessage().equals(NULL_STRING)) ? String.valueOf(e) : e.getMessage();
      if (execution.getVariable(PROCESS_TYPE_ID).equals(INSTANT_LOAN_PROCESS_TYPE_ID))
      {
        updateTaskStatus(execution, "Could not create loan report document task", "Failed");
      }
      execution.setVariable(ERROR_CAUSE, errorMessage);
      throw new BpmnError("Prepare Loan Report", errorMessage);
    }
  }

  private void checkProcessType(String processType)
  {
    switch (processType) {
    case BNPL_PROCESS_TYPE_ID:
      reportType = BNPL_REPORT;
      break;
    case INSTANT_LOAN_PROCESS_TYPE_ID:
      reportType = INSTANT_LOAN_REPORT;
      break;
    case ONLINE_LEASING_PROCESS_TYPE_ID:
      reportType = ONLINE_LEASING_REPORT;
      break;
    }
  }
}
