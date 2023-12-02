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
import static mn.erin.domain.bpm.BpmDocumentConstant.DOCUMENT_TYPE;
import static mn.erin.domain.bpm.BpmDocumentConstant.LOAN_REPAYMENT_BEFORE;
import static mn.erin.domain.bpm.BpmDocumentConstant.ZET_DOCUMENT_PARAMETER;
import static mn.erin.domain.bpm.BpmLoanContractConstants.ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmLoanContractConstants.ZET_PREVIOUS_SCHEDULE_MN;
import static mn.erin.domain.bpm.BpmModuleConstants.CATEGORY_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_CONTRACT_AS_BASE_64;
import static mn.erin.domain.bpm.BpmModuleConstants.MAIN_LOAN_CONTRACT_DOC_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.SOURCE_PUBLISHER;
import static mn.erin.domain.bpm.BpmModuleConstants.SUB_CATEGORY_LOAN_REPORT;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

public class DownloadLoanRepaymentBeforeTask implements JavaDelegate
{
  private static final Logger LOG = LoggerFactory.getLogger(DownloadMainContractTask.class);
  private final DocumentRepository documentRepository;

  public DownloadLoanRepaymentBeforeTask(DocumentRepository documentRepository)
  {
    this.documentRepository = documentRepository;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    CaseService caseService = execution.getProcessEngine().getCaseService();
    String instanceId = getInstanceId(execution);

    final String accountNumber = getValidString(caseService.getVariable(instanceId, ACCOUNT_NUMBER));

    String documentId = (System.currentTimeMillis()) + "-" + LOAN_REPAYMENT_BEFORE;

    final Collection<Document> documents = documentRepository.findByProcessInstanceId(instanceId);
    final Optional<Document> previousDocument = documents.stream()
        .filter(doc -> doc.getSubCategory().equals(SUB_CATEGORY_LOAN_REPORT) && doc.getName().equals(ZET_PREVIOUS_SCHEDULE_MN)).findFirst();

    if (previousDocument.isPresent())
    {
      documentRepository.removeBy(instanceId, CATEGORY_LOAN_CONTRACT, SUB_CATEGORY_LOAN_REPORT, ZET_PREVIOUS_SCHEDULE_MN);
    }

    try
    {
      documentRepository.create(documentId, MAIN_LOAN_CONTRACT_DOC_ID, instanceId, ZET_PREVIOUS_SCHEDULE_MN, CATEGORY_LOAN_CONTRACT,
          SUB_CATEGORY_LOAN_REPORT, LOAN_CONTRACT_AS_BASE_64, SOURCE_PUBLISHER);

      LOG.info("####### LOAN REPAYMENT SCHEDULE(BEFORE) HAS CREATED. INSTANCE ID = {}", instanceId);

    }
    catch (BpmRepositoryException e)
    {
      LOG.error("####### ERROR OCCURRED DURING CREATE LOAN CONTRACT TO DATABASE : " + e.getMessage(), e);
      throw new UseCaseException(e.getMessage(), e);
    }

    Map<String, Object> documentParameter = new HashMap<>();
    documentParameter.put(ACCOUNT_NUMBER, accountNumber);

    execution.setVariable(DOCUMENT_TYPE, LOAN_REPAYMENT_BEFORE);
    caseService.setVariable(instanceId, DOCUMENT_TYPE, LOAN_REPAYMENT_BEFORE);

    execution.setVariable(ZET_DOCUMENT_PARAMETER, documentParameter);
    caseService.setVariable(instanceId, ZET_DOCUMENT_PARAMETER, documentParameter);
  }
}
