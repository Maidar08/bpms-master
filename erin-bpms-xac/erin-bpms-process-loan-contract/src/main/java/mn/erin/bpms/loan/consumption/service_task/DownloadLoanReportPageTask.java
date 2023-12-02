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
import static mn.erin.domain.bpm.BpmLoanContractConstants.LOAN_REPORT_PAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.CATEGORY_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_CONTRACT_AS_BASE_64;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_REPORT_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.MAIN_LOAN_CONTRACT_DOC_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.SOURCE_PUBLISHER;
import static mn.erin.domain.bpm.BpmModuleConstants.SUB_CATEGORY_LOAN_REPORT;

public class DownloadLoanReportPageTask implements JavaDelegate
{
  private static final Logger LOG = LoggerFactory.getLogger(DownloadMainContractTask.class);
  private final LoanContractRequestRepository loanContractRequestRepository;
  private final DocumentRepository documentRepository;

  public DownloadLoanReportPageTask(LoanContractRequestRepository loanContractRequestRepository, DocumentRepository documentRepository)
  {
    this.loanContractRequestRepository = loanContractRequestRepository;
    this.documentRepository = documentRepository;
  }

  @Override
  public void execute(DelegateExecution execution) throws UseCaseException
  {
    final String processRequestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    String typeOfLoanContract = LOAN_REPORT_NAME;
    String instanceId = getInstanceId(execution);

    String documentId = (System.currentTimeMillis()) + "-" + LOAN_REPORT_PAGE;

    final Collection<Document> documents = documentRepository.findByProcessInstanceId(instanceId);
    String finalTypeOfLoanContract = typeOfLoanContract;
    final Optional<Document> previousDocument = documents.stream()
        .filter(doc -> doc.getCategory().equals(CATEGORY_LOAN_CONTRACT) && doc.getName().equals(finalTypeOfLoanContract)).findFirst();
    if (previousDocument.isPresent())
    {
      documentRepository.removeBy(instanceId, CATEGORY_LOAN_CONTRACT, SUB_CATEGORY_LOAN_REPORT, finalTypeOfLoanContract);
    }

    try
    {
      documentRepository.create(documentId, MAIN_LOAN_CONTRACT_DOC_ID, instanceId, typeOfLoanContract, CATEGORY_LOAN_CONTRACT,
          SUB_CATEGORY_LOAN_REPORT, LOAN_CONTRACT_AS_BASE_64, SOURCE_PUBLISHER);
      LOG.info("####### LOAN REPORT PAGE HAS CREATED. INSTANCE ID = {}", instanceId);
    }
    catch (BpmRepositoryException e)
    {
      LOG.error("####### ERROR OCCURRED DURING CREATE LOAN REPORT PAGE TO DATABASE : " + e.getMessage(), e);
      throw new UseCaseException(e.getMessage(), e);
    }

    final CaseService caseService = execution.getProcessEngine().getCaseService();
    Map<String, Object> documentParameter = new HashMap<>();

    Map<String, Object> previousParams = (Map<String, Object>) execution.getVariable(DOCUMENT_PARAMETER);

    documentParameter.put(PROCESS_REQUEST_ID, processRequestId);
    documentParameter.putAll(previousParams);
    execution.setVariable(DOCUMENT_TYPE, LOAN_REPORT_PAGE);
    caseService.setVariable(instanceId, DOCUMENT_TYPE, LOAN_REPORT_PAGE);
    execution.setVariable(DOCUMENT_PARAMETER, documentParameter);
    caseService.setVariable(instanceId, DOCUMENT_PARAMETER, documentParameter);
  }
}
