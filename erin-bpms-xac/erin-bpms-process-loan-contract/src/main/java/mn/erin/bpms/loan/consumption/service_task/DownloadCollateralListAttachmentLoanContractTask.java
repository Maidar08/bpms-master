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
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ATTACHMENT_NUMBER;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CUSTOMER_NAME;
import static mn.erin.domain.bpm.BpmDocumentConstant.DOCUMENT_PARAMETER;
import static mn.erin.domain.bpm.BpmDocumentConstant.DOCUMENT_TYPE;
import static mn.erin.domain.bpm.BpmLoanContractConstants.COLLATERAL_ASSETS_LIST_EQUIPMENT_MN;
import static mn.erin.domain.bpm.BpmLoanContractConstants.COLLATERAL_ASSETS_LIST_FIDUCIARY_MN;
import static mn.erin.domain.bpm.BpmLoanContractConstants.COLLATERAL_ASSETS_LIST_MORTGAGE;
import static mn.erin.domain.bpm.BpmLoanContractConstants.COLLATERAL_ASSETS_LIST_MORTGAGE_MN;
import static mn.erin.domain.bpm.BpmLoanContractConstants.COLLATERAL_CUSTOMER_NAME;
import static mn.erin.domain.bpm.BpmLoanContractConstants.EQUIPMENT_ASSETS;
import static mn.erin.domain.bpm.BpmLoanContractConstants.EQUIPMENT_ASSETS_MN;
import static mn.erin.domain.bpm.BpmLoanContractConstants.LOAN_ATTACHMENT_NUMBER;
import static mn.erin.domain.bpm.BpmLoanContractConstants.MOVABLE_ASSETS_FIDUCIARY;
import static mn.erin.domain.bpm.BpmLoanContractConstants.MOVABLE_ASSETS_MN;
import static mn.erin.domain.bpm.BpmLoanContractConstants.REAL_ESTAT_MN;
import static mn.erin.domain.bpm.BpmLoanContractConstants.TYPE_OF_COLLATERAL;
import static mn.erin.domain.bpm.BpmModuleConstants.CATEGORY_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_CONTRACT_AS_BASE_64;
import static mn.erin.domain.bpm.BpmModuleConstants.MAIN_LOAN_CONTRACT_DOC_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.SOURCE_PUBLISHER;
import static mn.erin.domain.bpm.BpmModuleConstants.SUB_CATEGORY_LOAN_REPORT;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

public class DownloadCollateralListAttachmentLoanContractTask implements JavaDelegate
{
  private static final Logger LOG = LoggerFactory.getLogger(DownloadCoOwnerContractTask.class);
  private final LoanContractRequestRepository loanContractRequestRepository;
  private final DocumentRepository documentRepository;

  public DownloadCollateralListAttachmentLoanContractTask(LoanContractRequestRepository loanContractRequestRepository, DocumentRepository documentRepository)
  {
    this.loanContractRequestRepository = loanContractRequestRepository;
    this.documentRepository = documentRepository;
  }

  @Override
  public void execute(DelegateExecution execution) throws UseCaseException
  {
    final String processRequestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    final String typeOfCollateral = String.valueOf(execution.getVariable(TYPE_OF_COLLATERAL));
    final String attachmentNumber = getValidString(execution.getVariable(ATTACHMENT_NUMBER));
    final String loanAttachmentNumber = getValidString(execution.getVariable(LOAN_ATTACHMENT_NUMBER));
    String collateralType = "";
    String typeOfLoanContract = "";
    String instanceId = getInstanceId(execution);

    switch (typeOfCollateral)
    {
    case REAL_ESTAT_MN:
      typeOfLoanContract = COLLATERAL_ASSETS_LIST_MORTGAGE_MN+" " + loanAttachmentNumber +" "+ attachmentNumber;
      collateralType = COLLATERAL_ASSETS_LIST_MORTGAGE;
      break;
    case MOVABLE_ASSETS_MN:
      typeOfLoanContract = COLLATERAL_ASSETS_LIST_FIDUCIARY_MN + " " + loanAttachmentNumber +" "+ attachmentNumber;
      collateralType = MOVABLE_ASSETS_FIDUCIARY;
      break;
    case EQUIPMENT_ASSETS_MN:
      typeOfLoanContract = COLLATERAL_ASSETS_LIST_EQUIPMENT_MN + " " + loanAttachmentNumber +" "+ attachmentNumber;
      collateralType = EQUIPMENT_ASSETS;
      break;
    default:
      break;
    }
    String documentId = (System.currentTimeMillis()) + "-" + collateralType;
    final Collection<Document> documents = documentRepository.findByProcessInstanceId(instanceId);
    String finalTypeOfLoanContract = typeOfLoanContract;
    final Optional<Document> previousDocument = documents.stream().filter(doc -> doc.getName().equals(finalTypeOfLoanContract)).findFirst();
    if (previousDocument.isPresent())
    {
      documentRepository.removeBy(instanceId, CATEGORY_LOAN_CONTRACT, SUB_CATEGORY_LOAN_REPORT, finalTypeOfLoanContract);
    }
    try
    {
      documentRepository.create(documentId, MAIN_LOAN_CONTRACT_DOC_ID, instanceId, finalTypeOfLoanContract, CATEGORY_LOAN_CONTRACT,
          SUB_CATEGORY_LOAN_REPORT, LOAN_CONTRACT_AS_BASE_64, SOURCE_PUBLISHER);
      LOG.info("####### COLLATERAL LIST ATTACHMENT LOAN CONTRACT HAS CREATED. INSTANCE ID = {}", instanceId);

      final CaseService caseService = execution.getProcessEngine().getCaseService();

      Map<String, Object> documentParameter = new HashMap<>();
      documentParameter.put(PROCESS_REQUEST_ID, processRequestId);
      documentParameter.put(ATTACHMENT_NUMBER, getValidString(execution.getVariable(ATTACHMENT_NUMBER)));
      documentParameter.put(LOAN_ATTACHMENT_NUMBER, getValidString(execution.getVariable(LOAN_ATTACHMENT_NUMBER)));
      documentParameter.put(COLLATERAL_CUSTOMER_NAME, getValidString(execution.getVariable(CUSTOMER_NAME)));

      execution.setVariable(DOCUMENT_TYPE, collateralType);
      caseService.setVariable(instanceId, DOCUMENT_TYPE, collateralType);
      execution.setVariable(DOCUMENT_PARAMETER, documentParameter);
      caseService.setVariable(instanceId, DOCUMENT_PARAMETER, documentParameter);
    }
    catch (BpmRepositoryException e)
    {
      LOG.error("####### ERROR OCCURRED DURING CREATE COLLATERAL LIST ATTACHMENT LOAN CONTRACT TO DATABASE : " + e.getMessage(), e);
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}
