package mn.erin.bpms.loan.consumption.service_task;

import static mn.erin.bpms.loan.consumption.util.CamundaContractUtils.getInstanceId;
import static mn.erin.domain.bpm.BpmDocumentConstant.DOCUMENT_PARAMETER;
import static mn.erin.domain.bpm.BpmDocumentConstant.DOCUMENT_TYPE;
import static mn.erin.domain.bpm.BpmDocumentConstant.GENERAL_TRADE_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.REPURCHASE_TRADE_CONTRACT;
import static mn.erin.domain.bpm.BpmLoanContractConstants.ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmLoanContractConstants.LOAN_CONTRACT_SUB_CATEGORY;
import static mn.erin.domain.bpm.BpmLoanContractConstants.PURCHASE_TRADE_CONTRACT;
import static mn.erin.domain.bpm.BpmModuleConstants.CATEGORY_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmModuleConstants.SUB_CATEGORY_LOAN_REPORT;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_CONTRACT_AS_BASE_64;
import static mn.erin.domain.bpm.BpmModuleConstants.MAIN_LOAN_CONTRACT_DOC_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PRODUCT_CODE;
import static mn.erin.domain.bpm.BpmModuleConstants.SOURCE_PUBLISHER;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.P_ACC;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.document.Document;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.DocumentRepository;
import mn.erin.domain.bpm.service.DocumentService;

public class DownloadPurchaseTradeContractTask implements JavaDelegate {
    
  private static final Logger LOG = LoggerFactory.getLogger(DownloadCoOwnerContractTask.class);
  private final DocumentRepository documentRepository;
  private final DocumentService documentService;
  
  public DownloadPurchaseTradeContractTask(DocumentRepository documentRepository, DocumentService documentService ){
    this.documentRepository = documentRepository;
    this.documentService = documentService;
  }

@Override
public void execute(DelegateExecution execution) throws Exception {
    final CaseService caseService = execution.getProcessEngine().getCaseService();

    String accountNumber = String.valueOf(execution.getVariable("accountNumber"));
    String contractId= String.valueOf(execution.getVariable("contractNumber"));
    String instanceId = getInstanceId(execution);
    String documentTypeMn = String.valueOf(execution.getVariable("attachmentType"));
    String documentType;
    String attachmentType = ""; 
    String typeOfLoanContract = "";
    String productCode = "";
    
    if (documentTypeMn.equals("Энгийн худалдах худалдан авах гэрээ")) {
      documentType = GENERAL_TRADE_CONTRACT;
    } 
    else 
    {
      documentType = REPURCHASE_TRADE_CONTRACT;
    }

    switch (documentType) {
        case GENERAL_TRADE_CONTRACT:
            attachmentType = GENERAL_TRADE_CONTRACT;
            typeOfLoanContract = PURCHASE_TRADE_CONTRACT+"-"+contractId;
            productCode = "EW10";
            break;
        case REPURCHASE_TRADE_CONTRACT:
            attachmentType = REPURCHASE_TRADE_CONTRACT;
            typeOfLoanContract = PURCHASE_TRADE_CONTRACT+"-"+contractId;
            productCode = "EW20";     
        default:
            break;
    }

    String documentId = (System.currentTimeMillis()) + "-" + attachmentType;
    String finalTypeOfLoanContract = typeOfLoanContract;
    final Collection<Document> documents = documentRepository.findByProcessInstanceId(instanceId);
    final Optional<Document> previousDocument = documents.stream().filter(doc -> doc.getName().equals(accountNumber)).findFirst();
  
    if (previousDocument.isPresent())
    {
      documentRepository.removeBy(instanceId, CATEGORY_LOAN_CONTRACT, LOAN_CONTRACT_SUB_CATEGORY, accountNumber);
    }
    try
    {
      Map<String, String> documentParameter = new HashMap<>();
      documentParameter.put(P_ACC, accountNumber);
      documentParameter.put(PRODUCT_CODE, productCode);

      LOG.info("#### DOWNLOADED BASE64 CONTRACT DOCUMENT");
      
      documentRepository.create(documentId, MAIN_LOAN_CONTRACT_DOC_ID, instanceId, finalTypeOfLoanContract, CATEGORY_LOAN_CONTRACT,
      SUB_CATEGORY_LOAN_REPORT, LOAN_CONTRACT_AS_BASE_64, SOURCE_PUBLISHER);
      LOG.info("####### TRADE CONTRACT HAS CREATED. INSTANCE ID = {}", instanceId);
    
      execution.setVariable(DOCUMENT_TYPE, attachmentType);
      caseService.setVariable(instanceId, DOCUMENT_TYPE, attachmentType);
      execution.setVariable(DOCUMENT_PARAMETER, documentParameter);
      caseService.setVariable(instanceId, DOCUMENT_PARAMETER, documentParameter);
      execution.setVariable(ACCOUNT_NUMBER, accountNumber);
      caseService.setVariable(instanceId, ACCOUNT_NUMBER, accountNumber);
    }
    catch (BpmRepositoryException e)
    {
      LOG.error("####### ERROR OCCURRED DURING CREATE ATTACHMENT LOAN CONTRACT TO DATABASE : " + e.getMessage(), e);
      throw new UseCaseException(e.getMessage(), e);
    }
}
}