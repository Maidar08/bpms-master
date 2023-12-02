package consumption.service_task_bnpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.alfresco.connector.service.download.DownloadServiceException;
import mn.erin.domain.aim.service.AimServiceRegistry;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;
import mn.erin.domain.bpm.usecase.document.UploadDocuments;
import mn.erin.domain.bpm.usecase.document.UploadDocumentsInput;
import mn.erin.domain.bpm.usecase.document.UploadFile;

import static consumption.constant.CamundaMongolBankConstants.LOAN_ENQUIRE_PDF;
import static consumption.constant.CamundaVariableConstants.FAILED_STATUS;
import static consumption.constant.CamundaVariableConstants.SUCCESS_STATUS;
import static consumption.constant.CamundaVariableConstants.UPLOAD_FILES_TO_LDMS_SERVICE_TASK;
import static consumption.util.CamundaUtils.updateTaskStatus;
import static consumption.util.CustomerInfoUtils.getParametersOfCustomerToLdms;
import static mn.erin.domain.bpm.BpmMessagesConstants.INSTANT_LOAN_LOG;
import static mn.erin.domain.bpm.BpmModuleConstants.BNPL_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.DOCUMENT_NAME_MONGOL_BANK_ENQUIRE_PDF;
import static mn.erin.domain.bpm.BpmModuleConstants.EMPTY_VALUE;
import static mn.erin.domain.bpm.BpmModuleConstants.ERROR_CAUSE;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_APPLICATION_NAME_PDF;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_DECISION_NAME_PDF;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.SALARY_ENQUIRE_NAME_PDF;
import static mn.erin.domain.bpm.BpmModuleConstants.SOURCE_LDMS;
import static mn.erin.domain.bpm.BpmModuleConstants.TRACK_NUMBER;
import static mn.erin.domain.bpm.util.process.BpmUtils.convertByteArrayToBase64;
import static mn.erin.domain.bpm.util.process.BpmUtils.getPdfBase64;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;
import static mn.erin.domain.bpm.util.process.DigitalLoanUtils.getLogPrefix;

public class UploadFilesToLdmsTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(UploadFilesToLdmsTask.class);
  private final AimServiceRegistry aimServiceRegistry;
  private final BpmsServiceRegistry bpmsServiceRegistry;
  private final BpmsRepositoryRegistry bpmsRepositoryRegistry;

  public UploadFilesToLdmsTask(AimServiceRegistry aimServiceRegistry, BpmsServiceRegistry bpmsServiceRegistry, BpmsRepositoryRegistry bpmsRepositoryRegistry)
  {
    this.aimServiceRegistry = aimServiceRegistry;
    this.bpmsServiceRegistry = bpmsServiceRegistry;
    this.bpmsRepositoryRegistry = bpmsRepositoryRegistry;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String processTypeId = getValidString(execution.getVariable(PROCESS_TYPE_ID));
    String logHash = getLogPrefix(processTypeId);
    boolean isInstantLoan = false;
    if (StringUtils.equals(INSTANT_LOAN_PROCESS_TYPE_ID, getValidString(execution.getVariable(PROCESS_TYPE_ID))))
    {
      isInstantLoan = true;
      logHash = INSTANT_LOAN_LOG;
    }
    try
    {
      sendNdshFileToLdms(execution, isInstantLoan, logHash);
      sendMongolBankFile(execution, logHash);
      sendLoanAppicationFileToLdms(execution, isInstantLoan, logHash);
      sendLoanDecisionFileToLdms(execution, isInstantLoan, processTypeId, logHash);
    }
    catch (Exception e)
    {
      LOGGER.error(logHash + " UploadFilesToLdmsTask error: ", e);
      updateTaskStatus(execution, UPLOAD_FILES_TO_LDMS_SERVICE_TASK, FAILED_STATUS);
      throw new BpmnError("File upload", "UploadFilesToLdmsTask error: " + e.getMessage());
    }
    updateTaskStatus(execution, UPLOAD_FILES_TO_LDMS_SERVICE_TASK, SUCCESS_STATUS);
  }

  private void sendMongolBankFile(DelegateExecution execution, String logHash)
  {
    byte[] mbEnquireFile = (byte[]) execution.getVariable(LOAN_ENQUIRE_PDF);
    final String base64 = convertByteArrayToBase64(mbEnquireFile);
    uploadToLdms(execution, DOCUMENT_NAME_MONGOL_BANK_ENQUIRE_PDF, base64, getEnquireCategories(execution), logHash);
  }

  private void sendNdshFileToLdms(DelegateExecution execution, boolean isInstantLoan, String logHash) throws DownloadServiceException
  {
    List<String> documentIdList = (List<String>) execution.getVariable("ndshDocumentList");
    List<String> returnValue = getPdfBase64(documentIdList);

    if (null == returnValue || returnValue.isEmpty() || returnValue.size() != 2)
    {
      if (isInstantLoan || getValidString(execution.getVariable(PROCESS_TYPE_ID)).equals(ONLINE_LEASING_PROCESS_TYPE_ID))
      {
        updateTaskStatus(execution, UPLOAD_FILES_TO_LDMS_SERVICE_TASK, FAILED_STATUS);
      }
      throw new BpmnError("File upload", "File not found when upload ndsh enquire file to LDMS");
    }

    String documentBase64 = returnValue.get(1);
    uploadToLdms(execution, SALARY_ENQUIRE_NAME_PDF, documentBase64, getEnquireCategories(execution), logHash);
  }

  private void sendLoanAppicationFileToLdms(DelegateExecution execution, boolean isInstantLoan, String logHash) throws DownloadServiceException
  {
    List<String> documentIdList = (List<String>) execution.getVariable("loanApplicationDocList");
    List<String> returnValue = getPdfBase64(documentIdList);

    if (null == returnValue || returnValue.isEmpty() || returnValue.size() != 2)
    {
      if (isInstantLoan || getValidString(execution.getVariable(PROCESS_TYPE_ID)).equals(ONLINE_LEASING_PROCESS_TYPE_ID))
      {
        updateTaskStatus(execution, UPLOAD_FILES_TO_LDMS_SERVICE_TASK, FAILED_STATUS);
      }
      throw new BpmnError("File upload", "File not found when upload loan application  file to LDMS");
    }

    String documentBase64 = returnValue.get(1);
    Map<String, String> categories = new HashMap<>();
    categories.put("category", getValidString(execution.getVariable("categoryLoanApplication")));
    categories.put("subCategory", getValidString(execution.getVariable("subCategoryLoanApplication")));

    uploadToLdms(execution, LOAN_APPLICATION_NAME_PDF, documentBase64, categories, logHash);
  }

  private void sendLoanDecisionFileToLdms(DelegateExecution execution, boolean isInstantLoan,String processTypeId, String logHash) throws DownloadServiceException
  {
    String documentBase64;
    List<String> documentIdList = (List<String>) (processTypeId.equals(BNPL_PROCESS_TYPE_ID) ? execution.getVariable("bnplLoanDecisionDocList") : execution.getVariable("instantLoanDecisionDocId"));
    List<String> returnValue = getPdfBase64(documentIdList);
    if (null == returnValue || returnValue.size() != 2)
    {
      throw new BpmnError("File upload", "File not found when upload loan decision file to LDMS");
    }
    documentBase64 = returnValue.get(1);
    if (StringUtils.isBlank(documentBase64))
    {
      if (isInstantLoan || getValidString(execution.getVariable(PROCESS_TYPE_ID)).equals(ONLINE_LEASING_PROCESS_TYPE_ID))
      {
        updateTaskStatus(execution, UPLOAD_FILES_TO_LDMS_SERVICE_TASK, FAILED_STATUS);
      }
      throw new BpmnError("File upload", "File not found when upload loan decision file to LDMS");
    }
    Map<String, String> categories = new HashMap<>();
    categories.put("category", getValidString(execution.getVariable("categoryLoanDecision")));
    categories.put("subCategory", getValidString(execution.getVariable("subCategoryLoanDecision")));

    uploadToLdms(execution, LOAN_DECISION_NAME_PDF, documentBase64, categories, logHash);
  }

  private Map<String, String> getEnquireCategories(DelegateExecution execution)
  {
    String categoryEnquire = String.valueOf(execution.getVariable("categoryEnquire"));
    String subCategoryEnquire = String.valueOf(execution.getVariable("subCategoryEnquire"));

    Map<String, String> categories = new HashMap<>();
    categories.put("category", categoryEnquire);
    categories.put("subCategory", subCategoryEnquire);
    return categories;
  }

  private void uploadToLdms(DelegateExecution execution, String documentName, String base64, Map<String, String> fileCategories, String logHash)
  {
    String instanceId = String.valueOf(execution.getVariable(PROCESS_INSTANCE_ID));
    String processRequestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    String processTypeId = String.valueOf(execution.getVariable(PROCESS_TYPE_ID));
    String trackNumber = String.valueOf(execution.getVariable(TRACK_NUMBER));

    List<UploadFile> uploadFiles = new ArrayList<>();
    uploadFiles.add(new UploadFile(documentName, base64));

    String category = fileCategories.get("category");
    String subCategory = fileCategories.get("subCategory");

    Map<String, String> parameterValues = getParametersOfCustomerToLdms(execution);
    UploadDocumentsInput input = new UploadDocumentsInput(instanceId, category, subCategory, SOURCE_LDMS, parameterValues, uploadFiles);
    UploadDocuments useCase = new UploadDocuments(aimServiceRegistry.getAuthenticationService(), aimServiceRegistry.getAuthorizationService(),
        bpmsServiceRegistry.getDocumentService(), aimServiceRegistry.getMembershipRepository(), bpmsRepositoryRegistry.getDocumentInfoRepository(),
        bpmsRepositoryRegistry.getDocumentRepository());
    Boolean isUploadedFile;
    try
    {
      isUploadedFile = useCase.execute(input);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(logHash + "LDMS UPLOAD ERROR = [{} - {}]  to upload {}", e.getCode(), e.getMessage(), documentName);
      e.printStackTrace();
      execution.setVariable(ERROR_CAUSE, e.getMessage());
      throw new BpmnError("File Upload", e.getMessage());
    }
    if (Boolean.TRUE.equals(isUploadedFile))
    {
      String fileName = setFileNameToLog(documentName);

      if (processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
      {
        LOGGER.info("{} {} TO LDMS WITH REQUEST ID [{}], TRACKNUMBER = [{}]", logHash, fileName, processRequestId, trackNumber);
      }
      else
      {
        LOGGER.info("{} {} TO LDMS WITH REQUEST ID [{}]", logHash, fileName, processRequestId);
      }
    }
  }

  private String setFileNameToLog(String documentName)
  {
    switch (documentName)
    {
    case SALARY_ENQUIRE_NAME_PDF:
      return "SENT NDSH ENQUIRE FILE";
    case DOCUMENT_NAME_MONGOL_BANK_ENQUIRE_PDF:
      return "SENT MONGOL BANK ENQUIRE FILE";
    case LOAN_APPLICATION_NAME_PDF:
      return "SENT LOAN APPLICATION FILE";
    case LOAN_DECISION_NAME_PDF:
      return "SENT LOAN DECISION FILE";
    default:
      return EMPTY_VALUE;
    }
  }
}
