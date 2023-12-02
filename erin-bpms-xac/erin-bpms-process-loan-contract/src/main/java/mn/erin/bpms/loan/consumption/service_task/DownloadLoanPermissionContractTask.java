package mn.erin.bpms.loan.consumption.service_task;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.codec.binary.StringUtils;
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
import static mn.erin.domain.bpm.BpmDocumentConstant.DISBURSEMENT_PERMISSION;
import static mn.erin.domain.bpm.BpmDocumentConstant.DOCUMENT_PARAMETER;
import static mn.erin.domain.bpm.BpmDocumentConstant.DOCUMENT_TYPE;
import static mn.erin.domain.bpm.BpmDocumentConstant.LOAN_PERMISSION;
import static mn.erin.domain.bpm.BpmLoanContractConstants.DISBURSEMENT_PERMISSION_MN;
import static mn.erin.domain.bpm.BpmLoanContractConstants.LOAN_PERMISSION_MN;
import static mn.erin.domain.bpm.BpmLoanContractConstants.PERMISSION_CONTRACT_MN;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_CONTRACT_AS_BASE_64;
import static mn.erin.domain.bpm.BpmModuleConstants.MAIN_LOAN_CONTRACT_DOC_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.SOURCE_PUBLISHER;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

public class DownloadLoanPermissionContractTask implements JavaDelegate
{
  private final DocumentRepository documentRepository;

  private static final Logger LOG = LoggerFactory.getLogger(DownloadLoanPermissionContractTask.class);

  public DownloadLoanPermissionContractTask(DocumentRepository documentRepository)
  {
    this.documentRepository = documentRepository;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    final String processRequestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    final String type = String.valueOf(execution.getVariable("preparationType"));
    final String accountNumber = String.valueOf(execution.getVariable("accountNumber"));

    String attachmentType = "";
    String instanceId = getInstanceId(execution);

    if (StringUtils.equals(type, DISBURSEMENT_PERMISSION_MN))
    {
      attachmentType = DISBURSEMENT_PERMISSION;
    }
    else if (StringUtils.equals(type, LOAN_PERMISSION_MN))
    {
      attachmentType = LOAN_PERMISSION;
    }

    String documentId = (System.currentTimeMillis()) + "-" + attachmentType;

    final Collection<Document> documents = documentRepository.findByProcessInstanceId(instanceId);
    String finalFileName = PERMISSION_CONTRACT_MN;

    final Optional<Document> previousDocument = documents.stream().filter(doc -> doc.getName().equals(finalFileName)).findFirst();
    if (previousDocument.isPresent())
    {
      documentRepository.removeBy(instanceId, "10.3 шийдвэр", "02.ЗОЗ", finalFileName);
    }
    try
    {
      documentRepository.create(documentId, MAIN_LOAN_CONTRACT_DOC_ID, instanceId, finalFileName, "10.3 шийдвэр",
          "02.ЗОЗ", LOAN_CONTRACT_AS_BASE_64, SOURCE_PUBLISHER);
      LOG.info("####### LOAN PERMISSION CONTRACT HAS CREATED. INSTANCE ID = {}", instanceId);

      final CaseService caseService = execution.getProcessEngine().getCaseService();

      Map<String, Object> documentParameter = new HashMap<>();
      documentParameter.put(PROCESS_REQUEST_ID, processRequestId);
      documentParameter.put("preparationType", type);
      documentParameter.put("accountNumber", accountNumber);
      documentParameter.put("loanContractTotalAmount", getValidString(execution.getVariable("loanContractTotalAmount")));
      documentParameter.put("confirmedTotalAmount", getValidString(execution.getVariable("confirmedTotalAmount")));
      documentParameter.put("currentTotalAmount", getValidString(execution.getVariable("currentTotalAmount")));
      documentParameter.put("crossTrade", getValidString(execution.getVariable("crossTrade")));
      documentParameter.put("closingAccountNumber", getValidString(execution.getVariable("closingAccountNumber")));
      documentParameter.put("currencyValue", getValidString(execution.getVariable("currencyValue")));
      documentParameter.put("checkboxPoll", getValidString(execution.getVariable("checkboxPoll")));
      documentParameter.put("loanRepaymentFee", getValidString(execution.getVariable("loanRepaymentFee")));
      documentParameter.put("printCollateralList", getValidString(execution.getVariable("printCollateralList")));
      documentParameter.put("buNe", getValidString(execution.getVariable("buNe")));
      documentParameter.put("loanAccountFirstAmount", getValidString(execution.getVariable("loanAccountFirstAmount")));
      documentParameter.put("accountNumberTrans", getValidString(execution.getVariable("accountNumberTrans")));
      documentParameter.put("accountName", getValidString(execution.getVariable("accountName")));
      documentParameter.put("amount", getValidString(execution.getVariable("amount")));

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
