package mn.erin.bpms.loan.consumption.service_task;

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
import mn.erin.domain.bpm.repository.LoanContractRequestRepository;

import static mn.erin.bpms.loan.consumption.util.CamundaContractUtils.getInstanceId;
import static mn.erin.domain.bpm.BpmDocumentConstant.DOCUMENT_PARAMETER;
import static mn.erin.domain.bpm.BpmDocumentConstant.DOCUMENT_TYPE;
import static mn.erin.domain.bpm.BpmLoanContractConstants.ATTACHMENT_COLLATERAL_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmLoanContractConstants.ATTACHMENT_COLLATERAL_LOAN_CONTRACT_MN;
import static mn.erin.domain.bpm.BpmLoanContractConstants.ATTACHMENT_FIDUC_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmLoanContractConstants.ATTACHMENT_FIDUC_LOAN_CONTRACT_MN;
import static mn.erin.domain.bpm.BpmLoanContractConstants.ATTACHMENT_MORTGAGE_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmLoanContractConstants.ATTACHMENT_MORTGAGE_LOAN_CONTRACT_MN;
import static mn.erin.domain.bpm.BpmLoanContractConstants.ATTACHMENT_WARRANTY_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmLoanContractConstants.ATTACHMENT_WARRANTY_LOAN_CONTRACT_MN;
import static mn.erin.domain.bpm.BpmLoanContractConstants.COLLATERAL_MN;
import static mn.erin.domain.bpm.BpmLoanContractConstants.CONTRACT_ID;
import static mn.erin.domain.bpm.BpmLoanContractConstants.FIDUC_MN;
import static mn.erin.domain.bpm.BpmLoanContractConstants.LOAN_CONTRACT_SUB_CATEGORY;
import static mn.erin.domain.bpm.BpmLoanContractConstants.MORTGAGE_MN;
import static mn.erin.domain.bpm.BpmLoanContractConstants.WARRANT_MN;
import static mn.erin.domain.bpm.BpmModuleConstants.CATEGORY_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_CONTRACT_AS_BASE_64;
import static mn.erin.domain.bpm.BpmModuleConstants.MAIN_LOAN_CONTRACT_DOC_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.SOURCE_PUBLISHER;

public class DownloadAttachmentLoanContractTask implements JavaDelegate
{
  private static final Logger LOG = LoggerFactory.getLogger(DownloadCoOwnerContractTask.class);
  private final LoanContractRequestRepository loanContractRequestRepository;
  private final DocumentRepository documentRepository;

  public DownloadAttachmentLoanContractTask(LoanContractRequestRepository loanContractRequestRepository, DocumentRepository documentRepository)
  {
    this.loanContractRequestRepository = loanContractRequestRepository;
    this.documentRepository = documentRepository;
  }

  @Override
  public void execute(DelegateExecution execution) throws UseCaseException
  {
    final String processRequestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    final String contractId = String.valueOf(execution.getVariable(CONTRACT_ID));
    final String typeOfAttachment = String.valueOf(execution.getVariable("typeOfAttachment"));
    String attachmentType = "";
    String typeOfLoanContract = "";
    String instanceId = getInstanceId(execution);

    switch (typeOfAttachment)
    {
    case ATTACHMENT_COLLATERAL_LOAN_CONTRACT_MN:
      typeOfLoanContract = COLLATERAL_MN + " "+contractId;
      attachmentType = ATTACHMENT_COLLATERAL_LOAN_CONTRACT;
      break;
    case ATTACHMENT_MORTGAGE_LOAN_CONTRACT_MN:
      typeOfLoanContract = MORTGAGE_MN + " "+contractId;
      attachmentType = ATTACHMENT_MORTGAGE_LOAN_CONTRACT;
      break;
    case ATTACHMENT_WARRANTY_LOAN_CONTRACT_MN:
      typeOfLoanContract = WARRANT_MN + " "+contractId;
      attachmentType = ATTACHMENT_WARRANTY_LOAN_CONTRACT;
      break;
    case ATTACHMENT_FIDUC_LOAN_CONTRACT_MN:
      typeOfLoanContract = FIDUC_MN +" "+contractId;
      attachmentType = ATTACHMENT_FIDUC_LOAN_CONTRACT;
      break;
    default:
      break;
    }
    String documentId = (System.currentTimeMillis()) + "-" + attachmentType;

    final Collection<Document> documents = documentRepository.findByProcessInstanceId(instanceId);
    String finalTypeOfLoanContract = typeOfLoanContract;
    final Optional<Document> previousDocument = documents.stream().filter(doc -> doc.getName().equals(finalTypeOfLoanContract)).findFirst();
    if (previousDocument.isPresent())
    {
      documentRepository.removeBy(instanceId, CATEGORY_LOAN_CONTRACT, LOAN_CONTRACT_SUB_CATEGORY, finalTypeOfLoanContract);
    }
    try
    {
      documentRepository.create(documentId, MAIN_LOAN_CONTRACT_DOC_ID, instanceId, finalTypeOfLoanContract, CATEGORY_LOAN_CONTRACT,
          LOAN_CONTRACT_SUB_CATEGORY, LOAN_CONTRACT_AS_BASE_64, SOURCE_PUBLISHER);
      LOG.info("####### LOAN ATTACHMENT CONTRACT HAS CREATED. INSTANCE ID = {}", instanceId);

      final CaseService caseService = execution.getProcessEngine().getCaseService();

      Map<String, Object> documentParameter = new HashMap<>();
      documentParameter.put(PROCESS_REQUEST_ID, processRequestId);

      execution.setVariable(DOCUMENT_TYPE, attachmentType);
      caseService.setVariable(instanceId, DOCUMENT_TYPE, attachmentType);
      execution.setVariable(DOCUMENT_PARAMETER, documentParameter);
      caseService.setVariable(instanceId, DOCUMENT_PARAMETER, documentParameter);
    }
    catch (BpmRepositoryException e)
    {
      LOG.error("####### ERROR OCCURRED DURING CREATE ATTACHMENT LOAN CONTRACT TO DATABASE : " + e.getMessage(), e);
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}
