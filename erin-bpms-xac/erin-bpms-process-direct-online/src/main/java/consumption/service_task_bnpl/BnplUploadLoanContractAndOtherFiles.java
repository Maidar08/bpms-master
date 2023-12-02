package consumption.service_task_bnpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import mn.erin.domain.bpm.usecase.document.UploadDocuments;
import mn.erin.domain.bpm.usecase.document.UploadDocumentsInput;
import mn.erin.domain.bpm.usecase.document.UploadFile;
import mn.erin.domain.bpm.usecase.process.GetProcessLargeParameter;
import mn.erin.domain.bpm.usecase.process.GetProcessParameterInput;
import mn.erin.domain.bpm.usecase.process.GetProcessParameterOutput;

import static consumption.util.CamundaUtils.updateTaskStatus;
import static consumption.util.CustomerInfoUtils.getParametersOfCustomerToLdms;
import static mn.erin.domain.bpm.BpmModuleConstants.ACTION_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.BNPL_CONTRACT_BASE64;
import static mn.erin.domain.bpm.BpmModuleConstants.BNPL_CONTRACT_NAME_PDF;
import static mn.erin.domain.bpm.BpmModuleConstants.BNPL_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.BNPL_REPAYMENT_BASE64;
import static mn.erin.domain.bpm.BpmModuleConstants.BNPL_REPORT_BASE64;
import static mn.erin.domain.bpm.BpmModuleConstants.BNPL_REPORT_NAME_PDF;
import static mn.erin.domain.bpm.BpmModuleConstants.EMPTY_VALUE;
import static mn.erin.domain.bpm.BpmModuleConstants.ERROR_CAUSE;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_CONTRACT_BASE64;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_CONTRACT_NAME_PDF;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_REPAYMENT_BASE64;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_REPORT_BASE64;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_REPORT_NAME_PDF;
import static mn.erin.domain.bpm.BpmModuleConstants.LDMS_CATEGORY;
import static mn.erin.domain.bpm.BpmModuleConstants.LDMS_SUB_CATEGORY;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PAYMENT_SCHEDULE_NAME_PDF;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_CONTRACT_BASE64;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_CONTRACT_NAME_PDF;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_REPAYMENT_BASE64;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_REPORT_BASE64;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_REPORT_NAME_PDF;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.SOURCE_LDMS;
import static mn.erin.domain.bpm.model.process.ParameterEntityType.BNPL;
import static mn.erin.domain.bpm.model.process.ParameterEntityType.INSTANT_LOAN;
import static mn.erin.domain.bpm.model.process.ParameterEntityType.ONLINE_LEASING;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;
import static mn.erin.domain.bpm.util.process.DigitalLoanUtils.getLogPrefix;

public class BnplUploadLoanContractAndOtherFiles implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(BnplUploadLoanContractAndOtherFiles.class);
  private final BpmsRepositoryRegistry bpmsRepositoryRegistry;
  private final AimServiceRegistry aimServiceRegistry;
  private final BpmsServiceRegistry bpmsServiceRegistry;

  public BnplUploadLoanContractAndOtherFiles(BpmsRepositoryRegistry bpmsRepositoryRegistry, AimServiceRegistry aimServiceRegistry,
      BpmsServiceRegistry bpmsServiceRegistry)
  {
    this.bpmsRepositoryRegistry = bpmsRepositoryRegistry;
    this.aimServiceRegistry = aimServiceRegistry;
    this.bpmsServiceRegistry = bpmsServiceRegistry;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    boolean isInstantLoan = false;
    boolean isOnlineLeasing = false;
    String processTypeId = getValidString(execution.getVariable(PROCESS_TYPE_ID));
    String logHash = getLogPrefix(processTypeId);
    try
    {
      if (processTypeId.equals(INSTANT_LOAN_PROCESS_TYPE_ID))
      {
        isInstantLoan = true;
      }
      if (processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
      {
        isOnlineLeasing = true;
      }
      String processInstanceId = (String) execution.getVariable(PROCESS_INSTANCE_ID);
      String processType = String.valueOf(execution.getVariable(PROCESS_TYPE_ID));
      String repaymentFileBase64;
      String contractFileBase64;
      String reportFileBase64;

      if (processType.equals(BNPL_PROCESS_TYPE_ID))
      {
        repaymentFileBase64 = getFileFromProcessLargeParameter(processInstanceId, BNPL_REPAYMENT_BASE64, BNPL);
        contractFileBase64 = getFileFromProcessLargeParameter(processInstanceId, BNPL_CONTRACT_BASE64, BNPL);
        reportFileBase64 = getFileFromProcessLargeParameter(processInstanceId, BNPL_REPORT_BASE64, BNPL);
      }
      else if (processType.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
      {
        repaymentFileBase64 = getFileFromProcessLargeParameter(processInstanceId, ONLINE_LEASING_REPAYMENT_BASE64, ONLINE_LEASING);
        contractFileBase64 = getFileFromProcessLargeParameter(processInstanceId, ONLINE_LEASING_CONTRACT_BASE64, ONLINE_LEASING);
        reportFileBase64 = getFileFromProcessLargeParameter(processInstanceId, ONLINE_LEASING_REPORT_BASE64, ONLINE_LEASING);
      }
      else
      {
        repaymentFileBase64 = getFileFromProcessLargeParameter(processInstanceId, INSTANT_LOAN_REPAYMENT_BASE64, INSTANT_LOAN);
        contractFileBase64 = getFileFromProcessLargeParameter(processInstanceId, INSTANT_LOAN_CONTRACT_BASE64, INSTANT_LOAN);
        reportFileBase64 = getFileFromProcessLargeParameter(processInstanceId, INSTANT_LOAN_REPORT_BASE64, INSTANT_LOAN);
      }

      Map<String, String> categories = new HashMap<>();

      sendLoanRepaymentScheduleTask(execution, repaymentFileBase64, categories, isInstantLoan, logHash);
      sendLoanContractTask(execution, contractFileBase64, categories, isInstantLoan, logHash, isOnlineLeasing);
      sendLoanReportTask(execution, reportFileBase64, categories, isInstantLoan, logHash, isOnlineLeasing);

      updateTaskStatus(execution, "Ldms upload", "Success");
    }
    catch (UseCaseException e)
    {
      LOGGER.error("{} LDMS UPLOAD ERROR = [{} - {}]  to upload file. {}", logHash, e.getCode(), e.getMessage(),
          (isInstantLoan ? " ActionType :" + execution.getVariable(ACTION_TYPE) + "." : ""));
      e.printStackTrace();
      if (processTypeId.equals(INSTANT_LOAN_PROCESS_TYPE_ID) || processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
        updateTaskStatus(execution, "Ldms upload", "Failed");
      execution.setVariable(ERROR_CAUSE, e.getMessage());
      throw new BpmnError("File Upload", e.getMessage());
    }
  }

  private void sendLoanReportTask(DelegateExecution execution, String reportFileBase64, Map<String, String> categories, boolean isInstantLoan, String logHash, boolean isOnlineLeasing)
  {
    categories.put(LDMS_CATEGORY, getValidString(execution.getVariable("categoryContract")));
    categories.put(LDMS_SUB_CATEGORY, getValidString(execution.getVariable("subCategoryOtherContract")));

    if (isOnlineLeasing)
    {
      uploadToLdms(execution, ONLINE_LEASING_REPORT_NAME_PDF, reportFileBase64, categories, false, logHash);
    }
    else
    {
      uploadToLdms(execution, isInstantLoan ? INSTANT_LOAN_REPORT_NAME_PDF : BNPL_REPORT_NAME_PDF, reportFileBase64, categories, isInstantLoan, logHash);
    }
  }

  private void sendLoanContractTask(DelegateExecution execution, String contractFileBase64, Map<String, String> categories, boolean isInstantLoan, String logHash, boolean isOnlineLeasing)
  {
    categories.put(LDMS_CATEGORY, getValidString(execution.getVariable("categoryContract")));
    categories.put(LDMS_SUB_CATEGORY, getValidString(execution.getVariable("subCategoryBasicContract")));

    if (isOnlineLeasing)
    {
      uploadToLdms(execution, ONLINE_LEASING_CONTRACT_NAME_PDF, contractFileBase64, categories, false,logHash);
    }
    else {
      uploadToLdms(execution, isInstantLoan ? INSTANT_LOAN_CONTRACT_NAME_PDF : BNPL_CONTRACT_NAME_PDF, contractFileBase64, categories, isInstantLoan,logHash);
    }
  }

  private void sendLoanRepaymentScheduleTask(DelegateExecution execution, String repaymentFileBase64, Map<String, String> categories, boolean isInstantLoan, String logHash)
  {
    categories.put(LDMS_CATEGORY, getValidString(execution.getVariable("categoryContract")));
    categories.put(LDMS_SUB_CATEGORY, getValidString(execution.getVariable("subCategoryOtherContract")));
    uploadToLdms(execution, LOAN_PAYMENT_SCHEDULE_NAME_PDF, repaymentFileBase64, categories, isInstantLoan, logHash);
  }

  private String getFileFromProcessLargeParameter(String caseInstanceId, String parameterName, ParameterEntityType entityType) throws UseCaseException
  {
    GetProcessLargeParameter getProcessLargeParameter = new GetProcessLargeParameter(bpmsRepositoryRegistry.getProcessRepository());
    GetProcessParameterInput input1 = new GetProcessParameterInput(caseInstanceId, parameterName, entityType);
    final GetProcessParameterOutput execute = getProcessLargeParameter.execute(input1);
    return String.valueOf(execute.getParameterValue());
  }

  private void uploadToLdms(DelegateExecution execution, String documentName, String base64, Map<String, String> fileCategories, boolean isInstantLoan, String logHash)
  {
    String instanceId = String.valueOf(execution.getVariable(PROCESS_INSTANCE_ID));
    String processRequestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    String processType = String.valueOf(execution.getVariable(PROCESS_TYPE_ID));
    Boolean isUploadedFile;
    try
    {
      List<UploadFile> uploadFiles = new ArrayList<>();
      uploadFiles.add(new UploadFile(documentName, base64));

      String category = fileCategories.get(LDMS_CATEGORY);
      String subCategory = fileCategories.get(LDMS_SUB_CATEGORY);

      Map<String, String> parameterValues = getParametersOfCustomerToLdms(execution);
      UploadDocumentsInput input = new UploadDocumentsInput(instanceId, category, subCategory, SOURCE_LDMS, parameterValues, uploadFiles);
      UploadDocuments useCase = new UploadDocuments(aimServiceRegistry.getAuthenticationService(), aimServiceRegistry.getAuthorizationService(),
          bpmsServiceRegistry.getDocumentService(), aimServiceRegistry.getMembershipRepository(), bpmsRepositoryRegistry.getDocumentInfoRepository(),
          bpmsRepositoryRegistry.getDocumentRepository());
      isUploadedFile = useCase.execute(input);
    }
    catch (UseCaseException e)
    {
      LOGGER.error("{} LDMS UPLOAD ERROR = [{} - {}]  to upload {}. {}", logHash, e.getCode(), e.getMessage(), documentName,
          (isInstantLoan ? " ActionType :" + execution.getVariable(ACTION_TYPE) + "." : ""));
      e.printStackTrace();
      if (processType.equals(INSTANT_LOAN_PROCESS_TYPE_ID) || processType.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
        updateTaskStatus(execution, "Ldms upload", "Failed");
      execution.setVariable(ERROR_CAUSE, e.getMessage());
      throw new BpmnError("File Upload", e.getMessage());
    }
    if (Boolean.TRUE.equals(isUploadedFile))
    {
      String fileName = EMPTY_VALUE;
      if(processType.equals(BNPL_PROCESS_TYPE_ID)){
        setFileNameToLog(documentName);
      }
      if(processType.equals(INSTANT_LOAN_PROCESS_TYPE_ID)){
        setInstantLoanFileNameToLog(documentName);
      }
      if(processType.equals(ONLINE_LEASING_PROCESS_TYPE_ID)){
        setOnlineLeasingFileNameToLog(documentName);
      }
      LOGGER.info("{} {} TO LDMS WITH REQUEST ID [{}]. {}", logHash, fileName, processRequestId,
          (isInstantLoan ? " ActionType :" + execution.getVariable(ACTION_TYPE) + "." : ""));
    }
  }

  private String setFileNameToLog(String documentName)
  {
    switch (documentName)
    {
    case LOAN_PAYMENT_SCHEDULE_NAME_PDF:
      return "SENT BNPL REPAYMENT FILE";
    case BNPL_CONTRACT_NAME_PDF:
      return "SENT BNPL CONTRACT FILE";
    case BNPL_REPORT_NAME_PDF:
      return "SENT BNPL REPORT FILE";
    default:
      return EMPTY_VALUE;
    }
  }

  private String setInstantLoanFileNameToLog(String documentName)
  {
    switch (documentName)
    {
    case LOAN_PAYMENT_SCHEDULE_NAME_PDF:
      return "SENT INSTANT LOAN REPAYMENT FILE";
    case INSTANT_LOAN_CONTRACT_NAME_PDF:
      return "SENT INSTANT LOAN CONTRACT FILE";
    case INSTANT_LOAN_REPORT_NAME_PDF:
      return "SENT INSTANT LOAN REPORT FILE";
    default:
      return EMPTY_VALUE;
    }
  }
  private String setOnlineLeasingFileNameToLog(String documentName)
  {
    switch (documentName)
    {
    case LOAN_PAYMENT_SCHEDULE_NAME_PDF:
      return "SENT ONLINE LEASING REPAYMENT FILE";
    case ONLINE_LEASING_CONTRACT_NAME_PDF:
      return "SENT ONLINE LEASING CONTRACT FILE";
    case ONLINE_LEASING_REPORT_NAME_PDF:
      return "SENT ONLINE LEASING REPORT FILE";
    default:
      return EMPTY_VALUE;
    }
  }
}
