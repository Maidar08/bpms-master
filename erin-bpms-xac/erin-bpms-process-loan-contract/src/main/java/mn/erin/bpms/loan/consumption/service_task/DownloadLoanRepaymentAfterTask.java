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

import static mn.erin.bpms.loan.consumption.util.CamundaContractUtils.getInstanceId;
import static mn.erin.domain.bpm.BpmDocumentConstant.DOCUMENT_PARAMETER;
import static mn.erin.domain.bpm.BpmDocumentConstant.DOCUMENT_TYPE;
import static mn.erin.domain.bpm.BpmDocumentConstant.LOAN_REPAYMENT_AFTER;
import static mn.erin.domain.bpm.BpmLoanContractConstants.ZAT_NEXT_SCHEDULE_MN;
import static mn.erin.domain.bpm.BpmModuleConstants.CATEGORY_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_CONTRACT_AS_BASE_64;
import static mn.erin.domain.bpm.BpmModuleConstants.MAIN_LOAN_CONTRACT_DOC_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.SOURCE_PUBLISHER;
import static mn.erin.domain.bpm.BpmModuleConstants.SUB_CATEGORY_LOAN_REPORT;

public class DownloadLoanRepaymentAfterTask implements JavaDelegate
{
  private static final Logger LOG = LoggerFactory.getLogger(DownloadMainContractTask.class);
  private final DocumentRepository documentRepository;

  public DownloadLoanRepaymentAfterTask(DocumentRepository documentRepository)
  {
    this.documentRepository = documentRepository;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    CaseService caseService = execution.getProcessEngine().getCaseService();
    String instanceId = getInstanceId(execution);
    final String processRequestId = String.valueOf(caseService.getVariable(instanceId, PROCESS_REQUEST_ID));

    String documentId = (System.currentTimeMillis()) + "-" + LOAN_REPAYMENT_AFTER;

    final Collection<Document> documents = documentRepository.findByProcessInstanceId(instanceId);
    final Optional<Document> previousDocument = documents.stream()
        .filter(doc -> doc.getSubCategory().equals(SUB_CATEGORY_LOAN_REPORT) && doc.getName().equals(ZAT_NEXT_SCHEDULE_MN)).findFirst();

    if (previousDocument.isPresent())
    {
      documentRepository.removeBy(instanceId, CATEGORY_LOAN_CONTRACT, SUB_CATEGORY_LOAN_REPORT, ZAT_NEXT_SCHEDULE_MN);
    }

    try
    {
      documentRepository.create(documentId, MAIN_LOAN_CONTRACT_DOC_ID, instanceId, ZAT_NEXT_SCHEDULE_MN, CATEGORY_LOAN_CONTRACT,
          SUB_CATEGORY_LOAN_REPORT, LOAN_CONTRACT_AS_BASE_64, SOURCE_PUBLISHER);

      LOG.info("####### LOAN REPAYMENT SCHEDULE(AFTER) HAS CREATED. INSTANCE ID = {}", instanceId);

    }
    catch (BpmRepositoryException e)
    {
      LOG.error("####### ERROR OCCURRED DURING CREATE LOAN CONTRACT TO DATABASE : " + e.getMessage(), e);
      throw new UseCaseException(e.getMessage(), e);
    }

    Map<String, Object> documentParameter = new HashMap<>();
    documentParameter.put(PROCESS_REQUEST_ID, processRequestId);
    execution.setVariable(DOCUMENT_TYPE, LOAN_REPAYMENT_AFTER);
    caseService.setVariable(instanceId, DOCUMENT_TYPE, LOAN_REPAYMENT_AFTER);
    execution.setVariable(DOCUMENT_PARAMETER, documentParameter);
    caseService.setVariable(instanceId, DOCUMENT_PARAMETER, documentParameter);
  }
}
