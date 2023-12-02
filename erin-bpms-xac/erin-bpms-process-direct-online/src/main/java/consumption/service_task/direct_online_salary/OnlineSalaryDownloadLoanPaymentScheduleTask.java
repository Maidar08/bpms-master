package consumption.service_task.direct_online_salary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.service.AimServiceRegistry;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;
import mn.erin.domain.bpm.usecase.contract.DownloadLoanPaymentScheduleAsBase64;
import mn.erin.domain.bpm.usecase.document.UploadDocuments;
import mn.erin.domain.bpm.usecase.document.UploadDocumentsInput;
import mn.erin.domain.bpm.usecase.document.UploadFile;
import mn.erin.domain.bpm.usecase.process.UpdateProcessLargeParameters;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParametersInput;

import static consumption.util.CamundaUtils.updateTaskStatus;
import static mn.erin.domain.bpm.BpmModuleConstants.BNPL_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.BNPL_REPAYMENT_BASE64;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.ERROR_CAUSE;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_REPAYMENT_BASE64;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PAYMENT_SCHEDULE_NAME_PDF;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_REPAYMENT_BASE64;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_SALARY_LOAN_REPAYMENT_BASE64;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_SALARY_PROCESS_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.REPAYMENT_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.SOURCE_LDMS;
import static mn.erin.domain.bpm.model.process.ParameterEntityType.BNPL;
import static mn.erin.domain.bpm.model.process.ParameterEntityType.INSTANT_LOAN;
import static mn.erin.domain.bpm.model.process.ParameterEntityType.ONLINE_LEASING;
import static mn.erin.domain.bpm.model.process.ParameterEntityType.ONLINE_SALARY;

public class OnlineSalaryDownloadLoanPaymentScheduleTask implements JavaDelegate
{
  private static final Logger LOG = LoggerFactory.getLogger(OnlineSalaryDownloadLoanPaymentScheduleTask.class);

  private final BpmsServiceRegistry bpmsServiceRegistry;
  private final BpmsRepositoryRegistry bpmsRepositoryRegistry;
  private final AimServiceRegistry aimServiceRegistry;

  public OnlineSalaryDownloadLoanPaymentScheduleTask(
      BpmsServiceRegistry bpmsServiceRegistry,
      BpmsRepositoryRegistry bpmsRepositoryRegistry, AimServiceRegistry aimServiceRegistry)
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
      CaseService caseService = execution.getProcessEngine().getCaseService();
      String caseInstanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));
      String processInstanceId = String.valueOf(execution.getVariable(PROCESS_INSTANCE_ID));
      boolean isBnpl = execution.getVariable(PROCESS_TYPE_ID).equals(BNPL_PROCESS_TYPE_ID);
      boolean isInstant = execution.getVariable(PROCESS_TYPE_ID).equals(INSTANT_LOAN_PROCESS_TYPE_ID);
      boolean isOnlineLeasing = execution.getVariable(PROCESS_TYPE_ID).equals(ONLINE_LEASING_PROCESS_TYPE_ID);

      String instanceId = execution.getVariable(PROCESS_TYPE_ID).equals(ONLINE_SALARY_PROCESS_TYPE) ? caseInstanceId : processInstanceId;
      String loanAccountNumber = String.valueOf(execution.getVariable("loanAccountNumber"));
      DownloadLoanPaymentScheduleAsBase64 downloadLoanPaymentScheduleAsBase64 = new DownloadLoanPaymentScheduleAsBase64(
          bpmsServiceRegistry.getDocumentService(),
          bpmsServiceRegistry.getCaseService(),
          aimServiceRegistry.getAuthenticationService(), aimServiceRegistry.getMembershipRepository());
      Map<String, String> downloadLoanPaymentParameter = new HashMap<>();
      downloadLoanPaymentParameter.put(PROCESS_TYPE_ID, (String) execution.getVariable(PROCESS_TYPE_ID));
      downloadLoanPaymentParameter.put(INSTANCE_ID, instanceId);
      downloadLoanPaymentParameter.put(LOAN_ACCOUNT_NUMBER, (String) execution.getVariable(LOAN_ACCOUNT_NUMBER));
      downloadLoanPaymentParameter.put(REPAYMENT_TYPE_ID, (String) execution.getVariable(REPAYMENT_TYPE_ID));
      String paymentFileAsBase64 = downloadLoanPaymentScheduleAsBase64.execute(downloadLoanPaymentParameter);

      Map<ParameterEntityType, Map<String, Serializable>> processParam = new HashMap<>();
      Map<String, Serializable> param = new HashMap<>();
      if (isBnpl)
      {
        param.put(BNPL_REPAYMENT_BASE64, paymentFileAsBase64);
        processParam.put(BNPL, param);
      }
      else if (isInstant)
      {
        param.put(INSTANT_LOAN_REPAYMENT_BASE64, paymentFileAsBase64);
        processParam.put(INSTANT_LOAN, param);
      }
      else if (isOnlineLeasing)
      {
        param.put(ONLINE_LEASING_REPAYMENT_BASE64, paymentFileAsBase64);
        processParam.put(ONLINE_LEASING, param);
      }
      else
      {
        caseService.setVariable(instanceId, LOAN_ACCOUNT_NUMBER, loanAccountNumber);
        sendFileToLDMS(execution, instanceId, paymentFileAsBase64);
        param.put(ONLINE_SALARY_LOAN_REPAYMENT_BASE64, paymentFileAsBase64);
        processParam.put(ONLINE_SALARY, param);
      }
      UpdateProcessLargeParameters updateProcessLargeParameters = new UpdateProcessLargeParameters(aimServiceRegistry.getAuthenticationService(),
          aimServiceRegistry.getAuthorizationService(), bpmsRepositoryRegistry.getProcessRepository());
      UpdateProcessParametersInput input = new UpdateProcessParametersInput(instanceId, processParam);
      updateProcessLargeParameters.execute(input);

      if (execution.getVariable(PROCESS_TYPE_ID).equals(INSTANT_LOAN_PROCESS_TYPE_ID))
        updateTaskStatus(execution, "Download loan payment schedule task", "Success");
      if (execution.getVariable(PROCESS_TYPE_ID).equals(ONLINE_LEASING_PROCESS_TYPE_ID))
        updateTaskStatus(execution, "Download online leasing payment schedule task", "Success");
    }
    catch (Exception e)
    {
      e.printStackTrace();
      if (execution.getVariable(PROCESS_TYPE_ID).equals(INSTANT_LOAN_PROCESS_TYPE_ID))
        updateTaskStatus(execution, "Download loan payment schedule task", "Failed");
      execution.setVariable(ERROR_CAUSE, e.getMessage());
      if (execution.getVariable(PROCESS_TYPE_ID).equals(ONLINE_LEASING_PROCESS_TYPE_ID))
        updateTaskStatus(execution, "Download online leasing payment schedule task", "Failed");
      execution.setVariable(ERROR_CAUSE, e.getMessage());
      throw new BpmnError("Download Loan Payment Schedule", e.getMessage());
    }
  }

  private void sendFileToLDMS(DelegateExecution execution, String instanceId, String paymentFileAsBase64)
      throws UseCaseException
  {
    String processRequestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    String categoryContract = String.valueOf(execution.getVariable("categoryContract"));
    String subCategoryOtherContract = String.valueOf(execution.getVariable("subCategoryOtherContract"));
    List<UploadFile> uploadFiles = new ArrayList<>();

    uploadFiles.add(new UploadFile(LOAN_PAYMENT_SCHEDULE_NAME_PDF, paymentFileAsBase64));
    UploadDocumentsInput input = new UploadDocumentsInput(instanceId, categoryContract, subCategoryOtherContract, SOURCE_LDMS,
        Collections.emptyMap(), uploadFiles);
    UploadDocuments useCase = new UploadDocuments(aimServiceRegistry.getAuthenticationService(), aimServiceRegistry.getAuthorizationService(),
        bpmsServiceRegistry.getDocumentService(), aimServiceRegistry.getMembershipRepository(),
        bpmsRepositoryRegistry.getDocumentInfoRepository(),
        bpmsRepositoryRegistry.getDocumentRepository());
    Boolean isUploadedFile = useCase.execute(input);
    if (Boolean.TRUE.equals(isUploadedFile))
    {
      LOG.info("######## SENT LOAN PAYMENT CONTRACT TO LDMS WITH REQUEST ID [{}]", processRequestId);
    }
    else
    {
      LOG.error("########## COULD NOT SENT LOAN PAYMENT CONTRACT TO LDMS WITH REQUEST ID = [{}] ##########", processRequestId);
    }
  }
}
