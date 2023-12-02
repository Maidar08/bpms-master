package mn.erin.bpms.loan.consumption.service_task;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
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
import static mn.erin.domain.bpm.BpmLoanContractConstants.ATTACHMENT_NUMBER_CO_OWNER;
import static mn.erin.domain.bpm.BpmLoanContractConstants.COLLATERAL_CO_OWNER_CONSENT;
import static mn.erin.domain.bpm.BpmLoanContractConstants.COLLATERAL_CO_OWNER_CONSENT_MN;
import static mn.erin.domain.bpm.BpmLoanContractConstants.CONTRACT_NUMBER;
import static mn.erin.domain.bpm.BpmLoanContractConstants.CO_OWNER_ATTACHMENT_NUMBER_AFTER;
import static mn.erin.domain.bpm.BpmLoanContractConstants.CO_OWNER_CONSENT_MN;
import static mn.erin.domain.bpm.BpmLoanContractConstants.CO_OWNER_LOAN_ATTACHMENT_NUMBER;
import static mn.erin.domain.bpm.BpmLoanContractConstants.NEXT_COLLATERAL_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmLoanContractConstants.NEXT_COLLATERAL_LOAN_CONTRACT_MN;
import static mn.erin.domain.bpm.BpmLoanContractConstants.NEXT_COLLATERAL_MN;
import static mn.erin.domain.bpm.BpmModuleConstants.CATEGORY_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_CONTRACT_AS_BASE_64;
import static mn.erin.domain.bpm.BpmModuleConstants.MAIN_LOAN_CONTRACT_DOC_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.SOURCE_PUBLISHER;
import static mn.erin.domain.bpm.BpmModuleConstants.SUB_CATEGORY_LOAN_REPORT;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

public class DownloadCoOwnerContractTask implements JavaDelegate
{
  private static final Logger LOG = LoggerFactory.getLogger(DownloadCoOwnerContractTask.class);
  private final LoanContractRequestRepository loanContractRequestRepository;
  private final DocumentRepository documentRepository;

  public DownloadCoOwnerContractTask(LoanContractRequestRepository loanContractRequestRepository, DocumentRepository documentRepository)
  {
    this.loanContractRequestRepository = loanContractRequestRepository;
    this.documentRepository = documentRepository;
  }

  @Override
  public void execute(DelegateExecution execution) throws UseCaseException
  {
    final String processRequestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    final String contractNumber = String.valueOf(execution.getVariable(CONTRACT_NUMBER));
    final String loanAttachmentNumber = String.valueOf(execution.getVariable(CO_OWNER_LOAN_ATTACHMENT_NUMBER));
    String typeOfLoanContract = "";
    String loanContractType = "";
    String instanceId = getInstanceId(execution);

    String attachmentNumber = getValidString(execution.getVariable(CO_OWNER_ATTACHMENT_NUMBER_AFTER));
    String attachmentCoOwner = getValidString(execution.getVariable(ATTACHMENT_NUMBER_CO_OWNER));

    switch (contractNumber)
    {
    case COLLATERAL_CO_OWNER_CONSENT_MN:
      typeOfLoanContract = CO_OWNER_CONSENT_MN + " " + loanAttachmentNumber;
      loanContractType = COLLATERAL_CO_OWNER_CONSENT;
      break;
    case NEXT_COLLATERAL_LOAN_CONTRACT_MN:
      typeOfLoanContract = NEXT_COLLATERAL_MN + " " + loanAttachmentNumber;
      loanContractType = NEXT_COLLATERAL_LOAN_CONTRACT;
      break;
    default:
      break;
    }

    String documentId = (System.currentTimeMillis()) + "-" + loanContractType;
    final Collection<Document> documents = documentRepository.findByProcessInstanceId(instanceId);
    String finalTypeOfLoanContract = typeOfLoanContract;
    final Optional<Document> previousDocument = documents.stream().filter(doc -> doc.getName().equals(finalTypeOfLoanContract)).findFirst();

    if (previousDocument.isPresent())
    {
      documentRepository.removeBy(instanceId, CATEGORY_LOAN_CONTRACT, SUB_CATEGORY_LOAN_REPORT, finalTypeOfLoanContract);
    }
    try
    {
      if (toCreateCoOwnerAndColContract(attachmentNumber, contractNumber, attachmentCoOwner))
      {
        documentRepository
            .create(System.currentTimeMillis() + "-" + COLLATERAL_CO_OWNER_CONSENT, MAIN_LOAN_CONTRACT_DOC_ID, instanceId,
                CO_OWNER_CONSENT_MN + " " + loanAttachmentNumber, CATEGORY_LOAN_CONTRACT,
                SUB_CATEGORY_LOAN_REPORT, LOAN_CONTRACT_AS_BASE_64, SOURCE_PUBLISHER);
        LOG.info("####### CO-OWNER CONTRACT COLLATERAL HAS CREATED. INSTANCE ID = {}", instanceId);
        documentRepository
            .create(System.currentTimeMillis() + "-" + NEXT_COLLATERAL_LOAN_CONTRACT, MAIN_LOAN_CONTRACT_DOC_ID, instanceId,
                NEXT_COLLATERAL_MN + " " + loanAttachmentNumber, CATEGORY_LOAN_CONTRACT,
                SUB_CATEGORY_LOAN_REPORT, LOAN_CONTRACT_AS_BASE_64, SOURCE_PUBLISHER);
        LOG.info("####### CO-OWNER CONTRACT NEXT COLLATERAL HAS CREATED. INSTANCE ID = {}", instanceId);
      }
      else
      {
        documentRepository
            .create(documentId, MAIN_LOAN_CONTRACT_DOC_ID, instanceId, finalTypeOfLoanContract, CATEGORY_LOAN_CONTRACT,
                SUB_CATEGORY_LOAN_REPORT, LOAN_CONTRACT_AS_BASE_64, SOURCE_PUBLISHER);
        LOG.info("####### CO-OWNER CONTRACT HAS CREATED. INSTANCE ID = {}", instanceId);
      }
    }
    catch (BpmRepositoryException e)
    {
      LOG.error("####### ERROR OCCURRED DURING CREATE CO-OWNER CONTRACT TO DATABASE : " + e.getMessage(), e);
      throw new UseCaseException(e.getMessage(), e);
    }

    final CaseService caseService = execution.getProcessEngine().getCaseService();

    Map<String, Object> documentParameter = new HashMap<>();
    documentParameter.put(PROCESS_REQUEST_ID, processRequestId);
    execution.setVariable(DOCUMENT_TYPE, loanContractType);
    caseService.setVariable(instanceId, DOCUMENT_TYPE, loanContractType);
    execution.setVariable(DOCUMENT_PARAMETER, documentParameter);
    caseService.setVariable(instanceId, DOCUMENT_PARAMETER, documentParameter);
  }

  private boolean toCreateCoOwnerAndColContract(String attachmentNumber, String contractNumber, String attachmentCoOwner)
  {
    return (!StringUtils.isBlank(attachmentNumber) && contractNumber.equals(COLLATERAL_CO_OWNER_CONSENT_MN) && StringUtils.isBlank(attachmentCoOwner)) ||
        (!StringUtils.isBlank(attachmentCoOwner) && contractNumber.equals(NEXT_COLLATERAL_LOAN_CONTRACT_MN) && StringUtils.isBlank(attachmentNumber) ||
            (!StringUtils.isBlank(attachmentCoOwner) && !StringUtils.isBlank(attachmentNumber)));
  }
}


