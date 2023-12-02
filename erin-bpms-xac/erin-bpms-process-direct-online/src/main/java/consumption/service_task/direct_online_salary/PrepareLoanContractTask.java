package consumption.service_task.direct_online_salary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
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
import mn.erin.domain.bpm.model.document.Document;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;
import mn.erin.domain.bpm.usecase.contract.CreateOnlineSalaryLoanContractDocument;
import mn.erin.domain.bpm.usecase.document.UploadDocuments;
import mn.erin.domain.bpm.usecase.document.UploadDocumentsInput;
import mn.erin.domain.bpm.usecase.document.UploadFile;
import mn.erin.domain.bpm.usecase.loan.GetLoanContractsByType;
import mn.erin.domain.bpm.usecase.loan.GetLoanContractsByTypeInput;
import mn.erin.domain.bpm.usecase.process.UpdateProcessLargeParameters;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParametersInput;

import static consumption.util.CamundaUtils.updateTaskStatus;
import static mn.erin.domain.bpm.BpmDocumentConstant.BNPL_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.INSTANT_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.ONLINE_LEASING_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.ONLINE_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmMessagesConstants.ONLINE_SALARY_LOG_HASH;
import static mn.erin.domain.bpm.BpmModuleConstants.ACTION_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.BNPL_CONTRACT_BASE64;
import static mn.erin.domain.bpm.BpmModuleConstants.BNPL_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.ERROR_CAUSE;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_CONTRACT_BASE64;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_CONTRACT_NAME_PDF;
import static mn.erin.domain.bpm.BpmModuleConstants.NULL_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_CONTRACT_BASE64;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_SALARY_LOAN_CONTRACT_BASE64;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_SALARY_PROCESS_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.SOURCE_LDMS;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.P_ACC;
import static mn.erin.domain.bpm.model.process.ParameterEntityType.BNPL;
import static mn.erin.domain.bpm.model.process.ParameterEntityType.INSTANT_LOAN;
import static mn.erin.domain.bpm.model.process.ParameterEntityType.ONLINE_LEASING;
import static mn.erin.domain.bpm.model.process.ParameterEntityType.ONLINE_SALARY;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

public class PrepareLoanContractTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(PrepareLoanContractTask.class);

  private static final Logger LOG = LoggerFactory.getLogger(PrepareLoanContractTask.class);
  private final BpmsServiceRegistry bpmsServiceRegistry;
  private final BpmsRepositoryRegistry bpmsRepositoryRegistry;
  private final AimServiceRegistry aimServiceRegistry;

  public PrepareLoanContractTask(BpmsServiceRegistry bpmsServiceRegistry, BpmsRepositoryRegistry bpmsRepositoryRegistry,
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
      String caseInstanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));
      String processInstanceId = String.valueOf(execution.getVariable(PROCESS_INSTANCE_ID));
      boolean isBnpl = execution.getVariable(PROCESS_TYPE_ID).equals(BNPL_PROCESS_TYPE_ID);
      boolean isInstantLoan = execution.getVariable(PROCESS_TYPE_ID).equals(INSTANT_LOAN_PROCESS_TYPE_ID);
      boolean isOnlineLeasing = execution.getVariable(PROCESS_TYPE_ID).equals(ONLINE_LEASING_PROCESS_TYPE_ID);
      String instanceId = execution.getVariable(PROCESS_TYPE_ID).equals(ONLINE_SALARY_PROCESS_TYPE) ? caseInstanceId : processInstanceId;
      String requestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));

      String loanAccountNumber = String.valueOf(execution.getVariable(LOAN_ACCOUNT_NUMBER));
      Map<String, Object> documentParam = new HashMap<>();
      documentParam.put(P_ACC, loanAccountNumber);
      documentParam.put(REQUEST_ID, requestId);
      GetLoanContractsByType getLoanContractsByType = new GetLoanContractsByType(bpmsServiceRegistry.getDocumentService(), bpmsServiceRegistry.getCaseService(),
          aimServiceRegistry.getAuthenticationService());
      String documentType;
      if (isBnpl)
      {
        documentType = BNPL_CONTRACT;
      }
      else if (isInstantLoan)
      {
        documentType = INSTANT_LOAN_CONTRACT;
      }
      else if (isOnlineLeasing)
      {
        documentType = ONLINE_LEASING_CONTRACT;
      }
      else
      {
        documentType = ONLINE_LOAN_CONTRACT;
      }
      GetLoanContractsByTypeInput bipInput = new GetLoanContractsByTypeInput(instanceId, documentType, documentParam);
      final List<Document> documentList = getLoanContractsByType.execute(bipInput);
      String base64 = null;
      if (!documentList.isEmpty())
      {
        base64 = documentList.get(0).getSource();
      }

      LOGGER.info("##### LOAN CONTRACT persisting to database. {}", (isInstantLoan ? " ActionType :" + execution.getVariable(ACTION_TYPE) + "." : ""));
      if (null == instanceId)
      {
        LOGGER.error("######### CASE INSTANCE ID is null during download loan contract with REQUEST_ID =[{}]. {}", requestId,
            (isInstantLoan ? " ActionType :" + execution.getVariable(ACTION_TYPE) + "." : ""));
        throw new BpmnError("Create Loan Contract", "###### CASE INSTANCE ID is null! with REQUEST ID =" + requestId);
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
        LOGGER.info("########## Successful create loan contract document with REQUEST ID =[{}]. {}", requestId,
            (isInstantLoan ? " ActionType :" + execution.getVariable(ACTION_TYPE) + "." : ""));
      }
      else
      {
        LOGGER.error("###### Could not create loan contract document with REQUEST ID =[{}]. {}", requestId,
            (isInstantLoan ? " ActionType :" + execution.getVariable(ACTION_TYPE) + "." : ""));
        throw new BpmnError("Create Loan Contract", "###### Could not create loan contract  document with REQUEST ID =" + requestId);
      }

      Map<ParameterEntityType, Map<String, Serializable>> processParam = new HashMap<>();
      Map<String, Serializable> param = new HashMap<>();
      if (isBnpl)
      {
        param.put(BNPL_CONTRACT_BASE64, base64);
        processParam.put(BNPL, param);
      }
      else if (isInstantLoan)
      {
        param.put(INSTANT_LOAN_CONTRACT_BASE64, base64);
        processParam.put(INSTANT_LOAN, param);
      }
      else if (isOnlineLeasing)
      {
        param.put(ONLINE_LEASING_CONTRACT_BASE64, base64);
        processParam.put(ONLINE_LEASING, param);
      }
      else
      {
        sendFileToLDMS(execution, instanceId, base64);
        param.put(ONLINE_SALARY_LOAN_CONTRACT_BASE64, base64);
        processParam.put(ONLINE_SALARY, param);
      }
      UpdateProcessLargeParameters updateProcessLargeParameters = new UpdateProcessLargeParameters(aimServiceRegistry.getAuthenticationService(),
          aimServiceRegistry.getAuthorizationService(), bpmsRepositoryRegistry.getProcessRepository());
      UpdateProcessParametersInput input = new UpdateProcessParametersInput(instanceId, processParam);
      updateProcessLargeParameters.execute(input);

      if (execution.getVariable(PROCESS_TYPE_ID).equals(INSTANT_LOAN_PROCESS_TYPE_ID))
      {
        updateTaskStatus(execution, "Create loan contract document task", "Success");
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      String errorMessage = (e.getMessage() == null || e.getMessage().equals(NULL_STRING)) ? String.valueOf(e) : e.getMessage();
      if (execution.getVariable(PROCESS_TYPE_ID).equals(INSTANT_LOAN_PROCESS_TYPE_ID))
      {
        updateTaskStatus(execution, "Could not create loan contract document task", "Failed");
      }
      execution.setVariable(ERROR_CAUSE, errorMessage);
      throw new BpmnError("Prepare Loan Contract", errorMessage);
    }
  }

  private void sendFileToLDMS(DelegateExecution execution, String instanceId, String base64File) throws UseCaseException
  {
    String processRequestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));

    String categoryContract = String.valueOf(execution.getVariable("categoryContract"));
    String subCategoryBasicContract = String.valueOf(execution.getVariable("subCategoryBasicContract"));
    List<UploadFile> uploadFiles = new ArrayList<>();

    uploadFiles.add(new UploadFile(LOAN_CONTRACT_NAME_PDF, base64File));

    UploadDocumentsInput input = new UploadDocumentsInput(instanceId, categoryContract, subCategoryBasicContract, SOURCE_LDMS, Collections.emptyMap(),
        uploadFiles);
    UploadDocuments useCase = new UploadDocuments(aimServiceRegistry.getAuthenticationService(), aimServiceRegistry.getAuthorizationService(),
        bpmsServiceRegistry.getDocumentService(), aimServiceRegistry.getMembershipRepository(), bpmsRepositoryRegistry.getDocumentInfoRepository(),
        bpmsRepositoryRegistry.getDocumentRepository());
    Boolean isUploadedFile = useCase.execute(input);
    if (Boolean.TRUE.equals(isUploadedFile))
    {
      LOG.info(ONLINE_SALARY_LOG_HASH + "SENT LOAN CONTRACT FILE TO LDMS WITH REQUEST ID [{}]", processRequestId);
    }
    else
    {
      LOG.error(ONLINE_SALARY_LOG_HASH + "FAILED TO UPLOAD FILE TO LDMS, REQUEST ID [{}]", processRequestId);
    }
  }
}
