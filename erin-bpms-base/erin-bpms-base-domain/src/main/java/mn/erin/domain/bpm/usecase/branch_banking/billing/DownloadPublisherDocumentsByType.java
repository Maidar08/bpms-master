package mn.erin.domain.bpm.usecase.branch_banking.billing;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.document.Document;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BranchBankingDocumentService;
import mn.erin.domain.bpm.service.CaseService;

import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_ID;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_ID_ENTER;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_REFERENCE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_REFERENCE_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ADD_CASH_TRANSACTION_RECEIPT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CASHIER_INCOME_EXPENCE_RECEIPT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CASHIER_INCOME_EXPENSE_DOC_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CASH_TRANSACTION_RECEIPT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CHILD_ACCOUNT_CHILD_MONEY_CONTRACT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CHILD_ACCOUNT_CHILD_MONEY_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CHILD_FUTURE_MILLIONARE_CONTRACT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CHILD_FUTURE_MILLIONARE_CONTRACT_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CHILD_FUTURE_MILLIONARE_ENDOW_CONTRACT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CHILD_FUTURE_MILLIONARE_ENDOW_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CHILD_FUTURE_MILLIONARE_THIRD_PARTY_CONDITION_CONTRACT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CHILD_FUTURE_MILLIONARE_THIRD_PARTY_CONTRACT_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CITIZEN_MASTER_CONTRACT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CITIZEN_MASTER_CONTRACT_EXTENSION;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CITIZEN_MASTER_CONTRACT_EXTENSION_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CITIZEN_MASTER_CONTRACT_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CURRENCY_EXCHANGE_RECEIPT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CURRENCY_EXCHANGE_RECEIPT_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CUSTOM_PAY;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CUSTOM_PAY_RECEIPT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CUSTOM_TRANSACTION_DOC_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CUSTOM_TRANSACTION_RECEIPT_DOC_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ENTITY_ACCOUNT_DEPOSIT_CONTRACT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ENTITY_ACCOUNT_SIMPLE_SPECIAL_CONTRACT_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ENTITY_PREPAID_INTEREST_DEPOSIT_CONTRACT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ENTITY_PREPAID_INTEREST_DEPOSIT_SIMPLE_SPECIAL_CONTRACT_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ENTITY_TIME_DEPOSIT_CONTRACT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ENTITY_TIME_DEPOSIT_SIMPLE_SPECIAL_CONTRACT_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.EXPENSE_TRANSACTION_DOC_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.EXPENSE_TRANSACTION_RECEIPT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.E_CUSTOM_PAY_DOC_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.E_CUSTOM_PAY_RECEIPT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.E_TRANSACTION_DOC_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.E_TRANSACTION_RECEIPT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.INCOME_TRANSACTION_DOC_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.INCOME_TRANSACTION_RECEIPT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.MEMORIAL_ORDER_DOC_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.MEMORIAL_ORDER_RECEIPT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.NON_CASH_TRANSACTION_RECEIPT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.OFF_BALANCE_DOC_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.OFF_BALANCE_RECEIPT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.PAYMENT_FORM;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.PAYMENT_FORM_DOC_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.SALARY_PACKAGE_TRANSACTION;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.SALARY_PACKAGE_TRANSACTION_NAME_MN;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TAX_PAY;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TAX_TRANSACTION_DOC_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_ID;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_NO;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_NUMBER;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_DOCUMENT_TYPE_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_DOCUMENT_TYPE_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PARAMETER_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PARAMETER_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.STRING_TO_DATE_EXCEPTION_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.STRING_TO_DATE_EXCEPTION_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.EMPTY_VALUE;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.P_DATE;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.P_TRAN;
import static mn.erin.domain.bpm.util.branch_banking.BranchBankingDocumentUtil.getDocumentParameters;
import static mn.erin.domain.bpm.util.branch_banking.BranchBankingDocumentUtil.getFormattedDateForBIP;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

/**
 * @author Lkhagvadorj.A
 **/

public class DownloadPublisherDocumentsByType extends AbstractUseCase<DownloadPublisherDocumentsByTypeInput, List<Document>>
{
  private final CaseService caseService;
  private final BranchBankingDocumentService branchBankingDocumentService;
  private final AuthenticationService authenticationService;

  public DownloadPublisherDocumentsByType(CaseService caseService, BranchBankingDocumentService branchBankingDocumentService,
      AuthenticationService authenticationService)
  {
    this.branchBankingDocumentService = Objects.requireNonNull(branchBankingDocumentService, "Branch banking document service required!");
    this.caseService = Objects.requireNonNull(caseService, "Case service is required!");
    this.authenticationService = Objects.requireNonNull(authenticationService, "Authentication service is required!");
  }

  @Override
  public List<Document> execute(DownloadPublisherDocumentsByTypeInput input) throws UseCaseException
  {
    validateNotNull(input, INPUT_NULL_CODE + ": " + INPUT_NULL_MESSAGE);
    validateNotNull(input.getParameter(), PARAMETER_NULL_CODE + ": " + PARAMETER_NULL_MESSAGE);

    validateNotBlank(input.getInstanceId(), CASE_INSTANCE_ID_NULL_CODE + ": " + CASE_INSTANCE_ID_NULL_MESSAGE);
    validateNotBlank(input.getDocumentType(), BB_DOCUMENT_TYPE_NULL_CODE + ": " + BB_DOCUMENT_TYPE_NULL_MESSAGE);

    try
    {
      List<Document> documents = new ArrayList<>();
      String instanceId = input.getInstanceId();
      String documentType = input.getDocumentType();
      Map<String, Object> parameter = input.getParameter();

      String tranId = (StringUtils.isBlank(getValidString(parameter.get(TRANSACTION_NO)))) ?
          getValidString(parameter.get(TRANSACTION_ID)) : String.valueOf(parameter.get(TRANSACTION_NO));

      String documentId = getDocumentIdByType(instanceId, documentType, parameter, tranId);

      String loanRepaymentTask = getValidString(parameter.get("loanRepaymentTask"));
      String transactionIdScheduled = getValidString(caseService.getVariableById(instanceId, "transactionIdScheduled"));
      String transactionIdUnscheduled = getValidString(caseService.getVariableById(instanceId, "transactionIdUnscheduled"));

      // TODO: improve this logic later
      if (loanRepaymentTask.equals("loanRepayment") && transactionIdUnscheduled.contains("XB") && transactionIdScheduled.contains("XB"))
      {
        List<String> transactionsId = Arrays.asList(transactionIdScheduled, transactionIdUnscheduled);
        String transactionDate = getValidString(caseService.getVariableById(instanceId, "transactionDate1"));
        for (String transactionId : transactionsId)
        {
          String base64File = branchBankingDocumentService
              .downloadDocumentByType(getTransactionParams(transactionId, transactionDate), documentType, instanceId);
          documents.add(new Document(null, null, null, getDocumentNameByType(documentType, transactionId), null, null, null, base64File));
        }
      }
      else
      {
        Map<String, String> documentParameter = getDocumentParameters(caseService, authenticationService, instanceId, documentType, parameter, tranId);
        String base64 = branchBankingDocumentService.downloadDocumentByType(documentParameter, documentType, instanceId);
        documents.add(new Document(null, null, null, getDocumentNameByType(documentType, documentId), null, null, null, base64));
      }
      return documents;
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
    catch (ParseException e)
    {
      throw new UseCaseException(STRING_TO_DATE_EXCEPTION_CODE, STRING_TO_DATE_EXCEPTION_MESSAGE);
    }
  }

  private static Map<String, String> getTransactionParams(String transactionId, String transactionDate) throws ParseException
  {
    Map<String, String> parameters = new HashMap<>();
    String date = getFormattedDateForBIP(transactionDate);
    parameters.put(P_TRAN, transactionId);
    parameters.put(P_DATE, date);
    return parameters;
  }

  private String getDocumentIdByType(String instanceId, String documentType, Map<String, Object> parameters, String transactionId) throws BpmServiceException
  {
    switch (documentType)
    {
    case TAX_PAY:
    case CUSTOM_PAY:
    case ADD_CASH_TRANSACTION_RECEIPT:
    case NON_CASH_TRANSACTION_RECEIPT:
    case SALARY_PACKAGE_TRANSACTION:
    case CASH_TRANSACTION_RECEIPT:
      String transactionNumber = getValidString(caseService.getVariableById(instanceId, TRANSACTION_NUMBER));
      String scheduledTrnNumber = getValidString(caseService.getVariableById(instanceId, "transactionIdScheduled"));
      String unscheduledTrnNumber = getValidString(caseService.getVariableById(instanceId, "transactionIdUnscheduled"));
      if (!transactionNumber.equals(EMPTY_VALUE))
      {
        return transactionNumber;
      }
      else
      {
        return scheduledTrnNumber.equals(EMPTY_VALUE) ? unscheduledTrnNumber : scheduledTrnNumber;
      }
    case INCOME_TRANSACTION_RECEIPT:
    case EXPENSE_TRANSACTION_RECEIPT:
    case MEMORIAL_ORDER_RECEIPT:
    case PAYMENT_FORM:
    case CURRENCY_EXCHANGE_RECEIPT:
    case CASHIER_INCOME_EXPENCE_RECEIPT:
    case CUSTOM_PAY_RECEIPT:
    case E_TRANSACTION_RECEIPT:
    case E_CUSTOM_PAY_RECEIPT:
      return transactionId;
    case CHILD_FUTURE_MILLIONARE_CONTRACT:
    case CHILD_FUTURE_MILLIONARE_THIRD_PARTY_CONDITION_CONTRACT:
    case CHILD_FUTURE_MILLIONARE_ENDOW_CONTRACT:
    case CHILD_ACCOUNT_CHILD_MONEY_CONTRACT:
    case CITIZEN_MASTER_CONTRACT_EXTENSION:
    case ENTITY_TIME_DEPOSIT_CONTRACT:
    case ENTITY_PREPAID_INTEREST_DEPOSIT_CONTRACT:
    case ENTITY_ACCOUNT_DEPOSIT_CONTRACT:
      return String.valueOf(parameters.get(ACCOUNT_ID));
    case CITIZEN_MASTER_CONTRACT:
      return String.valueOf(parameters.get(CIF_NUMBER));
    case ACCOUNT_REFERENCE:
      return String.valueOf(parameters.get(ACCOUNT_ID_ENTER));
    default:
      return EMPTY_VALUE;
    }
  }

  private String getDocumentNameByType(String documentType, String documentId)
  {
    switch (documentType)
    {
    case TAX_PAY:
      return TAX_TRANSACTION_DOC_NAME + documentId;
    case CUSTOM_PAY:
      return CUSTOM_TRANSACTION_DOC_NAME + documentId;
    case CUSTOM_PAY_RECEIPT:
      return CUSTOM_TRANSACTION_RECEIPT_DOC_NAME + documentId;
    case CHILD_FUTURE_MILLIONARE_CONTRACT:
      return CHILD_FUTURE_MILLIONARE_CONTRACT_NAME + documentId;
    case CHILD_FUTURE_MILLIONARE_THIRD_PARTY_CONDITION_CONTRACT:
      return CHILD_FUTURE_MILLIONARE_THIRD_PARTY_CONTRACT_NAME + documentId;
    case CHILD_FUTURE_MILLIONARE_ENDOW_CONTRACT:
      return CHILD_FUTURE_MILLIONARE_ENDOW_NAME + documentId;
    case CHILD_ACCOUNT_CHILD_MONEY_CONTRACT:
      return CHILD_ACCOUNT_CHILD_MONEY_NAME + documentId;
    case CITIZEN_MASTER_CONTRACT:
      return CITIZEN_MASTER_CONTRACT_NAME + documentId;
    case CITIZEN_MASTER_CONTRACT_EXTENSION:
      return CITIZEN_MASTER_CONTRACT_EXTENSION_NAME + documentId;
    case INCOME_TRANSACTION_RECEIPT:
    case CASH_TRANSACTION_RECEIPT:
      return INCOME_TRANSACTION_DOC_NAME + documentId;
    case EXPENSE_TRANSACTION_RECEIPT:
      return EXPENSE_TRANSACTION_DOC_NAME + documentId;
    case MEMORIAL_ORDER_RECEIPT:
      return MEMORIAL_ORDER_DOC_NAME + documentId;
    case CASHIER_INCOME_EXPENCE_RECEIPT:
    case ADD_CASH_TRANSACTION_RECEIPT:
      return CASHIER_INCOME_EXPENSE_DOC_NAME + documentId;
    case OFF_BALANCE_RECEIPT:
      return OFF_BALANCE_DOC_NAME + documentId;
    case PAYMENT_FORM:
    case NON_CASH_TRANSACTION_RECEIPT:
      return PAYMENT_FORM_DOC_NAME + documentId;
    case CURRENCY_EXCHANGE_RECEIPT:
      return CURRENCY_EXCHANGE_RECEIPT_NAME + documentId;
    case E_CUSTOM_PAY_RECEIPT:
      return E_CUSTOM_PAY_DOC_NAME + documentId;
    case E_TRANSACTION_RECEIPT:
      return E_TRANSACTION_DOC_NAME + documentId;
    case ENTITY_TIME_DEPOSIT_CONTRACT:
      return ENTITY_TIME_DEPOSIT_SIMPLE_SPECIAL_CONTRACT_NAME + documentId;
    case ENTITY_PREPAID_INTEREST_DEPOSIT_CONTRACT:
      return ENTITY_PREPAID_INTEREST_DEPOSIT_SIMPLE_SPECIAL_CONTRACT_NAME + documentId;
    case ENTITY_ACCOUNT_DEPOSIT_CONTRACT:
      return ENTITY_ACCOUNT_SIMPLE_SPECIAL_CONTRACT_NAME + documentId;
    case ACCOUNT_REFERENCE:
      return ACCOUNT_REFERENCE_NAME + documentId;
    case SALARY_PACKAGE_TRANSACTION:
      return SALARY_PACKAGE_TRANSACTION_NAME_MN + documentId;
    default:
      return EMPTY_VALUE;
    }
  }
}
