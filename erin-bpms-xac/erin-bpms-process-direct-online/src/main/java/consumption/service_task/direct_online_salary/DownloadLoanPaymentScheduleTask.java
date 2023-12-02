package consumption.service_task.direct_online_salary;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.service.AimServiceRegistry;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;
import mn.erin.domain.bpm.usecase.contract.DownloadLoanPaymentScheduleAsBase64;
import mn.erin.domain.bpm.usecase.document.UploadDocuments;
import mn.erin.domain.bpm.usecase.document.UploadDocumentsInput;
import mn.erin.domain.bpm.usecase.document.UploadFile;

import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PAYMENT_SCHEDULE_NAME_PDF;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_SALARY_LOAN_REPAYMENT_BASE64;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.REPAYMENT_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.SOURCE_LDMS;

public class DownloadLoanPaymentScheduleTask implements JavaDelegate
{
  private static final Logger LOG = LoggerFactory.getLogger(DownloadLoanPaymentScheduleTask.class);

  private final BpmsServiceRegistry bpmsServiceRegistry;
  private final BpmsRepositoryRegistry bpmsRepositoryRegistry;
  private final AimServiceRegistry aimServiceRegistry;

  public DownloadLoanPaymentScheduleTask(
      BpmsServiceRegistry bpmsServiceRegistry,
      BpmsRepositoryRegistry bpmsRepositoryRegistry, AimServiceRegistry aimServiceRegistry)
  {
    this.bpmsServiceRegistry = bpmsServiceRegistry;
    this.bpmsRepositoryRegistry = bpmsRepositoryRegistry;
    this.aimServiceRegistry = aimServiceRegistry;
  }

  @Override
  public void execute(DelegateExecution execution) throws UseCaseException, UnsupportedEncodingException
  {
    CaseService caseService = execution.getProcessEngine().getCaseService();
    String caseInstanceId = (String) execution.getVariable(CASE_INSTANCE_ID);
    String loanAccountNumber = String.valueOf(execution.getVariable("loanAccountNumber"));
    String repaymentType = "Үндсэн төлбөр тэнцүү";
    caseService.setVariable(caseInstanceId, LOAN_ACCOUNT_NUMBER, loanAccountNumber);
    caseService.setVariable(caseInstanceId, REPAYMENT_TYPE, repaymentType);
    DownloadLoanPaymentScheduleAsBase64 downloadLoanPaymentScheduleAsBase64 = new DownloadLoanPaymentScheduleAsBase64(bpmsServiceRegistry.getDocumentService(),
        bpmsServiceRegistry.getCaseService(),
        aimServiceRegistry.getAuthenticationService(), aimServiceRegistry.getMembershipRepository());
    Map<String, String> input = new HashMap<>();
    input.put(CASE_INSTANCE_ID, caseInstanceId);
    String paymentFileAsBase64 = downloadLoanPaymentScheduleAsBase64.execute(input);

    execution.setVariable(ONLINE_SALARY_LOAN_REPAYMENT_BASE64, paymentFileAsBase64);
    sendFileToLDMS(execution, caseInstanceId, paymentFileAsBase64);
  }

  private void sendFileToLDMS(DelegateExecution execution, String caseInstanceId, String paymentFileAsBase64)
      throws UseCaseException
  {
    String processRequestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    String categoryContract = String.valueOf(execution.getVariable("categoryContract"));
    String subCategoryOtherContract = String.valueOf(execution.getVariable("subCategoryOtherContract"));
    List<UploadFile> uploadFiles = new ArrayList<>();

    uploadFiles.add(new UploadFile(LOAN_PAYMENT_SCHEDULE_NAME_PDF, paymentFileAsBase64));
    UploadDocumentsInput input = new UploadDocumentsInput(caseInstanceId, categoryContract, subCategoryOtherContract, SOURCE_LDMS,
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
