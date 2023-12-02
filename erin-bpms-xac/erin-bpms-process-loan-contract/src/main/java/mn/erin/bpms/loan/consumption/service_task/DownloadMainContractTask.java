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

import static mn.erin.bpms.loan.consumption.util.CamundaContractUtils.getInstanceId;
import static mn.erin.domain.bpm.BpmDocumentConstant.CONSUMPTION_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.CREDIT_LINE_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.DIRECT_PRINTING_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.DOCUMENT_PARAMETER;
import static mn.erin.domain.bpm.BpmDocumentConstant.DOCUMENT_TYPE;
import static mn.erin.domain.bpm.BpmDocumentConstant.EMPLOYEE_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.EMPLOYEE_MORTGAGE_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.MORTGAGE_TYPE_GOVERNMENT;
import static mn.erin.domain.bpm.BpmDocumentConstant.MORTGAGE_TYPE_LOAN;
import static mn.erin.domain.bpm.BpmDocumentConstant.P_CON_TYPE;
import static mn.erin.domain.bpm.BpmDocumentConstant.SMALL_AND_MEDIUM_ENTERPRISE_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.SME_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmLoanContractConstants.ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmLoanContractConstants.LOAN_CONTRACT_SUB_CATEGORY;
import static mn.erin.domain.bpm.BpmModuleConstants.CATEGORY_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmModuleConstants.EQUAL_PRINCIPLE_PAYMENT_VALUE;
import static mn.erin.domain.bpm.BpmModuleConstants.EQUATED_MONTHLY_INSTALLMENT_VALUE;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_CONTRACT_AS_BASE_64;
import static mn.erin.domain.bpm.BpmModuleConstants.MAIN_LOAN_CONTRACT_DOC_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.REPAYMENT_EQUAL_PRINCIPLE_PAYMENT;
import static mn.erin.domain.bpm.BpmModuleConstants.REPAYMENT_EQUATED_MONTHLY_INSTALLMENT;
import static mn.erin.domain.bpm.BpmModuleConstants.REPAYMENT_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.SOURCE_PUBLISHER;

public class DownloadMainContractTask implements JavaDelegate
{
  private static final Logger LOG = LoggerFactory.getLogger(DownloadMainContractTask.class);
  public static final String HASH = "###";
  private final DocumentRepository documentRepository;

  public DownloadMainContractTask(DocumentRepository documentRepository)
  {
    this.documentRepository = documentRepository;
  }

  @Override
  public void execute(DelegateExecution execution) throws UseCaseException
  {
    String paymentType = String.valueOf(execution.getVariable("dialog"));
    String additionalType = String.valueOf(execution.getVariable("checkbox"));
    String instanceId = getInstanceId(execution);

    String smeType = "";
    String mainType = "";

    final CaseService caseService = execution.getProcessEngine().getCaseService();
    final String processRequestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    final String accountNumber = String.valueOf(execution.getVariable(ACCOUNT_NUMBER));
    final String processTypeId = String.valueOf(execution.getVariable(PROCESS_TYPE_ID));

    switch (paymentType)
    {
    case SMALL_AND_MEDIUM_ENTERPRISE_LOAN_CONTRACT:
      smeType = SMALL_AND_MEDIUM_ENTERPRISE_LOAN_CONTRACT;
      break;
    case SME_LOAN_CONTRACT:
      smeType = SME_LOAN_CONTRACT;
      break;
    case MORTGAGE_TYPE_GOVERNMENT:
      smeType = MORTGAGE_TYPE_GOVERNMENT;
      break;
    case MORTGAGE_TYPE_LOAN:
      smeType = MORTGAGE_TYPE_LOAN;
      break;
    case REPAYMENT_EQUATED_MONTHLY_INSTALLMENT:
      paymentType = EQUATED_MONTHLY_INSTALLMENT_VALUE;
      break;
    case REPAYMENT_EQUAL_PRINCIPLE_PAYMENT:
      paymentType = EQUAL_PRINCIPLE_PAYMENT_VALUE;
      break;
    default:
      break;
    }

    switch (processTypeId)
    {
    case CONSUMPTION_LOAN_CONTRACT:
      mainType = CONSUMPTION_LOAN_CONTRACT;
      break;
    case CREDIT_LINE_LOAN_CONTRACT:
      mainType = CREDIT_LINE_LOAN_CONTRACT;
      break;
    case EMPLOYEE_LOAN_CONTRACT:
      mainType = EMPLOYEE_LOAN_CONTRACT;
      break;
    case DIRECT_PRINTING_CONTRACT:
      mainType = DIRECT_PRINTING_CONTRACT;
      break;
    case EMPLOYEE_MORTGAGE_LOAN_CONTRACT:
      mainType = EMPLOYEE_MORTGAGE_LOAN_CONTRACT;
      caseService.setVariable(instanceId, P_CON_TYPE, paymentType);
      break;
    default:
      mainType = smeType;
      break;
    }

    String documentId = (System.currentTimeMillis()) + "-" + mainType;

    final Collection<Document> documents = documentRepository.findByProcessInstanceId(instanceId);
    final Optional<Document> previousDocument = documents.stream()
        .filter(doc -> doc.getCategory().equals(CATEGORY_LOAN_CONTRACT) && doc.getName().equals(accountNumber)).findFirst();
    if (previousDocument.isPresent())
    {
      documentRepository.removeBy(instanceId, CATEGORY_LOAN_CONTRACT, LOAN_CONTRACT_SUB_CATEGORY, accountNumber);
    }
    try
    {
      documentRepository.create(documentId, MAIN_LOAN_CONTRACT_DOC_ID, instanceId, accountNumber, CATEGORY_LOAN_CONTRACT,
          LOAN_CONTRACT_SUB_CATEGORY, LOAN_CONTRACT_AS_BASE_64, SOURCE_PUBLISHER);
      LOG.info("####### LOAN CONTRACT HAS CREATED. INSTANCE ID = {}", instanceId);
    }
    catch (BpmRepositoryException e)
    {
      LOG.error("####### ERROR OCCURRED DURING CREATE LOAN CONTRACT TO DATABASE : " + e.getMessage(), e);
      throw new UseCaseException(e.getMessage(), e);
    }


    Map<String, Object> documentParameter = new HashMap<>();
    documentParameter.put(PROCESS_REQUEST_ID, processRequestId);

    if (!StringUtils.equals(additionalType, "null"))
    {
      String[] split = additionalType.split(HASH);

      for (String option : split)
      {
        switch (option)
        {
        case "buyPublicHouse":
          documentParameter.put("buyPublicHouse", "1");
          break;
        case "buyFencedHouseAoc":
          documentParameter.put("buyFencedHouseAoc", "2");
          break;
        case "buyCarGarageParkingLot":
          documentParameter.put("buyCarGarageParkingLot", "3");
          break;
        case "fixBuyingPublicHouse":
          documentParameter.put("fixBuyingPublicHouse", "4");
          break;
        case "buildAocFencedHouse":
          documentParameter.put("buildAocFencedHouse", "5");
          break;
        default:
          break;
        }
      }
    }

    execution.setVariable(DOCUMENT_TYPE, mainType);
    caseService.setVariable(instanceId, DOCUMENT_TYPE, mainType);

    execution.setVariable(REPAYMENT_TYPE_ID, paymentType);
    caseService.setVariable(instanceId, REPAYMENT_TYPE_ID, paymentType);

    execution.setVariable(DOCUMENT_PARAMETER, documentParameter);
    caseService.setVariable(instanceId, DOCUMENT_PARAMETER, documentParameter);
  }
}
