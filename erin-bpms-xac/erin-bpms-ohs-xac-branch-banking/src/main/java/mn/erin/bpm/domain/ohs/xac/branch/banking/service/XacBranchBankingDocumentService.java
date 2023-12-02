package mn.erin.bpm.domain.ohs.xac.branch.banking.service;

import java.util.Map;
import java.util.Objects;

import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import mn.erin.bpm.domain.ohs.xac.XacHttpClient;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BranchBankingDocumentService;

import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_PUBLISHER_ENDPOINT;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_PUBLISHER_FUNCTION;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_PUBLISHER_PASSWORD;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_PUBLISHER_TRANSACTION_CONTRACTS;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_PUBLISHER_TRANSACTION_SLIPS;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_PUBLISHER_USER_ID;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_PUBLISHER_XBR10HA;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_PUBLISHER_XBR12SB;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_PUBLISHER_XBR13IS;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_PUBLISHER_XBR14SN;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_PUBLISHER_XBR19MC;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_PUBLISHER_XBR7HAA;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_PUBLISHER_XBR9UAA;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_PUBLISHER_XBRACR;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_PUBLISHER_XBRCHM;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_PUBLISHER_XBRCUP;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_PUBLISHER_XBRFXTX;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_PUBLISHER_XBRGALI;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_PUBLISHER_XBRINTR;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_PUBLISHER_XBRMISC;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_PUBLISHER_XBRNETS;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_PUBLISHER_XBROFFB;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_PUBLISHER_XBROUTR;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_PUBLISHER_XBROZTR;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_PUBLISHER_XBRPAYT;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.PUBLISHER_SOAP_ACTION_DOWNLOAD_CONTRACT;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.CONTRACT;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.TRANSACTION;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.util.DocumentServiceUtil.getDocumentRequestBody;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_REFERENCE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ADD_CASH_TRANSACTION_RECEIPT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CASHIER_INCOME_EXPENCE_RECEIPT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CASH_TRANSACTION_RECEIPT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CHILD_ACCOUNT_CHILD_MONEY_CONTRACT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CHILD_FUTURE_MILLIONARE_CONTRACT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CHILD_FUTURE_MILLIONARE_ENDOW_CONTRACT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CHILD_FUTURE_MILLIONARE_THIRD_PARTY_CONDITION_CONTRACT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CITIZEN_MASTER_CONTRACT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CITIZEN_MASTER_CONTRACT_EXTENSION;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CURRENCY_EXCHANGE_RECEIPT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CUSTOM_PAY;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CUSTOM_PAY_RECEIPT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ENTITY_ACCOUNT_DEPOSIT_CONTRACT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ENTITY_PREPAID_INTEREST_DEPOSIT_CONTRACT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ENTITY_TIME_DEPOSIT_CONTRACT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.EXPENSE_TRANSACTION_RECEIPT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.E_CUSTOM_PAY_RECEIPT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.E_TRANSACTION_RECEIPT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.INCOME_TRANSACTION_RECEIPT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.MEMORIAL_ORDER_RECEIPT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.NON_CASH_TRANSACTION_RECEIPT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.OFF_BALANCE_RECEIPT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.PAYMENT_FORM;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.SALARY_PACKAGE_TRANSACTION;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TAX_PAY;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_ACCOUNT_MONEY_CHILD_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_ACCOUNT_MONEY_CHILD_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_ACCOUNT_REFERENCE_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_ACCOUNT_REFERENCE_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_CASHIER_INCOME_EXPENCE_TRANSACTION_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_CASHIER_INCOME_EXPENCE_TRANSACTION_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_CITIZEN_MASTER_CONTRACT_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_CITIZEN_MASTER_CONTRACT_EXTENSION_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_CITIZEN_MASTER_CONTRACT_EXTENSION_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_CITIZEN_MASTER_CONTRACT_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_CURRENCY_EXCHANGE_TRANSACTION_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_CURRENCY_EXCHANGE_TRANSACTION_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_CUSTOM_TRANSACTION_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_CUSTOM_TRANSACTION_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_ENTITY_ACCOUNT_SIMPLE_SPECIAL_CONTRACT_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_ENTITY_ACCOUNT_SIMPLE_SPECIAL_CONTRACT_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_ENTITY_PREPAID_INTEREST_DEPOSIT_SIMPLE_SPECIAL_CONTRACT_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_ENTITY_PREPAID_INTEREST_DEPOSIT_SIMPLE_SPECIAL_CONTRACT_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_ENTITY_TIME_DEPOSIT_SIMPLE_SPECIAL_CONTRACT_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_ENTITY_TIME_DEPOSIT_SIMPLE_SPECIAL_CONTRACT_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_EXPENSE_TRANSACTION_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_EXPENSE_TRANSACTION_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_E_CUSTOM_PAY_TRANSACTION_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_E_CUSTOM_PAY_TRANSACTION_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_E_TRANSACTION_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_E_TRANSACTION_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_FUTURE_MILLIONARE_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_FUTURE_MILLIONARE_ENDOW_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_FUTURE_MILLIONARE_ENDOW_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_FUTURE_MILLIONARE_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_FUTURE_MILLIONARE_THIRD_PARTY_CONDIITON_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_FUTURE_MILLIONARE_THIRD_PARTY_CONDITION_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_INCOME_TRANSACTION_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_INCOME_TRANSACTION_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_MEMORIAL_ORDER_RECEIPT_TRANSACTION_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_MEMORIAL_ORDER_RECEIPT_TRANSACTION_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_OFF_BALANCE_RECEIPT_TRANSACTION_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_OFF_BALANCE_RECEIPT_TRANSACTION_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_PAYMENT_FORM_TRANSACTION_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_PAYMENT_FORM_TRANSACTION_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_TAX_TRANSACTION_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_TAX_TRANSACTION_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.DOCUMENT_TYPE_IS_VALID_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.DOCUMENT_TYPE_IS_VALID_MESSAGE;

/**
 * @author Lkhagvadorj.A
 **/

public class XacBranchBankingDocumentService implements BranchBankingDocumentService
{
  private final Environment environment;

  private static final Logger LOG = LoggerFactory.getLogger(XacBranchBankingDocumentService.class);
  private static final String TEXT_XML = "text/xml";

  public XacBranchBankingDocumentService(Environment environment)
  {
    this.environment = Objects.requireNonNull(environment, "Environment is required!");
  }

  @Override
  public String downloadDocumentByType(Map<String, String> documentParam, String documentType, String instanceId) throws BpmServiceException
  {
    String endPoint = getPropertyByKey(environment, BB_PUBLISHER_ENDPOINT);
    String soapAction = getPropertyByKey(environment, PUBLISHER_SOAP_ACTION_DOWNLOAD_CONTRACT);
    String publisherUserId = getPropertyByKey(environment, BB_PUBLISHER_USER_ID);
    String publisherPassword = getPropertyByKey(environment, BB_PUBLISHER_PASSWORD);
    String reportAbsolutePathDocType = getPropertyByKey(environment, getAbsolutePathDocType(documentType));
    String reportAbsolutePath = getPropertyByKey(environment, getAbsolutePathConstantByDocumentType(documentType));
    String requestBody = getDocumentRequestBody(documentParam, reportAbsolutePathDocType, reportAbsolutePath, publisherUserId, publisherPassword);
    String function = getPropertyByKey(environment, BB_PUBLISHER_FUNCTION);

    XacHttpClient xacHttpClient = new XacHttpClient
        .Builder(endPoint, TEXT_XML, soapAction)
        .build();

    setLogMessage(documentType, instanceId, endPoint, soapAction, reportAbsolutePathDocType, reportAbsolutePath, function);

    try (Response getDocumentResponse = xacHttpClient.postTextXml(requestBody, function))
    {
      if (getDocumentResponse.isSuccessful())
      {
        ResponseBody responseBody = getDocumentResponse.body();
        if (null == responseBody)
        {
          LOG.error(
              "########### RESPONSE BODY IS NULL WHEN DOWNLOAD [{}] document from BI PUBLISHER ... \n WITH PROCESS INSTANCE ID = [{}] \n HEADER PARAM = [{}]",
              documentType, instanceId, xacHttpClient.headerParam());
          throwException(documentType, instanceId, xacHttpClient.headerParam());
        }
        String responseString = xacHttpClient.read(Objects.requireNonNull(responseBody));

        String contractAsBase64 = StringUtils.substringBetween(responseString, "<reportBytes>", "</reportBytes>");

        if (null == contractAsBase64 || StringUtils.isBlank(contractAsBase64))
        {
          LOG.error(
              "########### RESPONSE DOCUMENT BASE64 IS NULL OR BLANK WHEN DOWNLOAD [{}] document from BI PUBLISHER ... \n WITH PROCESS INSTANCE ID = [{}] \n HEADER PARAM = [{}]",
              documentType, instanceId, xacHttpClient.headerParam());
          throwException(documentType, instanceId, xacHttpClient.headerParam());
        }
        return contractAsBase64;
      }
      throwException(documentType, instanceId, xacHttpClient.headerParam());
      return null;
    }
  }

  public static String getPropertyByKey(Environment environment, String key)
  {
    return environment.getProperty(key);
  }

  private static String getAbsolutePathDocType(String type) throws BpmServiceException
  {
    if (null == type)
    {
      throw new BpmServiceException(DOCUMENT_TYPE_IS_VALID_CODE, DOCUMENT_TYPE_IS_VALID_MESSAGE);
    }
    if (StringUtils.containsIgnoreCase(type, TRANSACTION) || type.equals(PAYMENT_FORM) || type.equals(CURRENCY_EXCHANGE_RECEIPT) || type.equals(ACCOUNT_REFERENCE))
    {
      return BB_PUBLISHER_TRANSACTION_SLIPS;
    }
    if (StringUtils.containsIgnoreCase(type, CONTRACT) || StringUtils.containsIgnoreCase(type, "child"))
    {
      return BB_PUBLISHER_TRANSACTION_CONTRACTS;
    }
    return null;
  }

  private static String getAbsolutePathConstantByDocumentType(String type) throws BpmServiceException
  {
    if (null == type)
    {
      throw new BpmServiceException(DOCUMENT_TYPE_IS_VALID_CODE, DOCUMENT_TYPE_IS_VALID_MESSAGE);
    }
    switch (type)
    {
    case TAX_PAY:
    case PAYMENT_FORM:
    case NON_CASH_TRANSACTION_RECEIPT:
      return BB_PUBLISHER_XBRPAYT;
    case CUSTOM_PAY:
    case CUSTOM_PAY_RECEIPT:
      return BB_PUBLISHER_XBRCUP;
    case INCOME_TRANSACTION_RECEIPT:
    case CASH_TRANSACTION_RECEIPT:
      return BB_PUBLISHER_XBRINTR;
    case EXPENSE_TRANSACTION_RECEIPT:
      return BB_PUBLISHER_XBROUTR;
    case MEMORIAL_ORDER_RECEIPT:
    case SALARY_PACKAGE_TRANSACTION:
      return BB_PUBLISHER_XBRMISC;
    case OFF_BALANCE_RECEIPT:
      return BB_PUBLISHER_XBROFFB;
    case CURRENCY_EXCHANGE_RECEIPT:
      return BB_PUBLISHER_XBRFXTX;
    case CASHIER_INCOME_EXPENCE_RECEIPT:
    case ADD_CASH_TRANSACTION_RECEIPT:
      return BB_PUBLISHER_XBROZTR;
    case E_TRANSACTION_RECEIPT:
      return BB_PUBLISHER_XBRNETS;
    case E_CUSTOM_PAY_RECEIPT:
      return BB_PUBLISHER_XBRGALI;
    case CHILD_FUTURE_MILLIONARE_CONTRACT:
      return BB_PUBLISHER_XBR13IS;
    case CHILD_FUTURE_MILLIONARE_THIRD_PARTY_CONDITION_CONTRACT:
      return BB_PUBLISHER_XBR14SN;
    case CHILD_FUTURE_MILLIONARE_ENDOW_CONTRACT:
      return BB_PUBLISHER_XBR12SB;
    case CHILD_ACCOUNT_CHILD_MONEY_CONTRACT:
      return BB_PUBLISHER_XBRCHM;
    case CITIZEN_MASTER_CONTRACT:
    case CITIZEN_MASTER_CONTRACT_EXTENSION:
      return BB_PUBLISHER_XBR19MC;
    case ENTITY_TIME_DEPOSIT_CONTRACT:
      return BB_PUBLISHER_XBR7HAA;
    case ENTITY_PREPAID_INTEREST_DEPOSIT_CONTRACT:
      return BB_PUBLISHER_XBR9UAA;
    case ENTITY_ACCOUNT_DEPOSIT_CONTRACT:
      return BB_PUBLISHER_XBR10HA;
    // Account reference
    case ACCOUNT_REFERENCE:
      return BB_PUBLISHER_XBRACR;
    default:
      return null;
    }
  }

  private void throwException(String documentType, String instanceId, Map<String, String> headerParam) throws BpmServiceException
  {
    String errorCode = null;
    String errorMessage = null;

    switch (documentType)
    {
    case TAX_PAY:
      errorCode = BIP_FAILED_TO_DOWNLOAD_TAX_TRANSACTION_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_TAX_TRANSACTION_MESSAGE;
      break;
    case CUSTOM_PAY:
    case CUSTOM_PAY_RECEIPT:
      errorCode = BIP_FAILED_TO_DOWNLOAD_CUSTOM_TRANSACTION_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_CUSTOM_TRANSACTION_MESSAGE;
      break;
    case INCOME_TRANSACTION_RECEIPT:
    case CASH_TRANSACTION_RECEIPT:
      errorCode = BIP_FAILED_TO_DOWNLOAD_INCOME_TRANSACTION_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_INCOME_TRANSACTION_MESSAGE;
      break;
    case EXPENSE_TRANSACTION_RECEIPT:
      errorCode = BIP_FAILED_TO_DOWNLOAD_EXPENSE_TRANSACTION_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_EXPENSE_TRANSACTION_MESSAGE;
      break;
    case MEMORIAL_ORDER_RECEIPT:
      errorCode = BIP_FAILED_TO_DOWNLOAD_MEMORIAL_ORDER_RECEIPT_TRANSACTION_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_MEMORIAL_ORDER_RECEIPT_TRANSACTION_MESSAGE;
      break;
    case OFF_BALANCE_RECEIPT:
      errorCode = BIP_FAILED_TO_DOWNLOAD_OFF_BALANCE_RECEIPT_TRANSACTION_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_OFF_BALANCE_RECEIPT_TRANSACTION_MESSAGE;
      break;
    case CASHIER_INCOME_EXPENCE_RECEIPT:
    case ADD_CASH_TRANSACTION_RECEIPT:
      errorCode = BIP_FAILED_TO_DOWNLOAD_CASHIER_INCOME_EXPENCE_TRANSACTION_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_CASHIER_INCOME_EXPENCE_TRANSACTION_MESSAGE;
      break;
    case PAYMENT_FORM:
    case NON_CASH_TRANSACTION_RECEIPT:
      errorCode = BIP_FAILED_TO_DOWNLOAD_PAYMENT_FORM_TRANSACTION_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_PAYMENT_FORM_TRANSACTION_MESSAGE;
      break;
    case CURRENCY_EXCHANGE_RECEIPT:
      errorCode = BIP_FAILED_TO_DOWNLOAD_CURRENCY_EXCHANGE_TRANSACTION_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_CURRENCY_EXCHANGE_TRANSACTION_MESSAGE;
      break;
    case E_TRANSACTION_RECEIPT:
      errorCode = BIP_FAILED_TO_DOWNLOAD_E_TRANSACTION_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_E_TRANSACTION_MESSAGE;
      break;
    case E_CUSTOM_PAY_RECEIPT:
      errorCode = BIP_FAILED_TO_DOWNLOAD_E_CUSTOM_PAY_TRANSACTION_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_E_CUSTOM_PAY_TRANSACTION_MESSAGE;
      break;
    case CHILD_FUTURE_MILLIONARE_CONTRACT:
      errorCode = BIP_FAILED_TO_DOWNLOAD_FUTURE_MILLIONARE_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_FUTURE_MILLIONARE_MESSAGE;
      break;
    case CHILD_FUTURE_MILLIONARE_THIRD_PARTY_CONDITION_CONTRACT:
      errorCode = BIP_FAILED_TO_DOWNLOAD_FUTURE_MILLIONARE_THIRD_PARTY_CONDITION_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_FUTURE_MILLIONARE_THIRD_PARTY_CONDIITON_MESSAGE;
      break;
    case CHILD_FUTURE_MILLIONARE_ENDOW_CONTRACT:
      errorCode = BIP_FAILED_TO_DOWNLOAD_FUTURE_MILLIONARE_ENDOW_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_FUTURE_MILLIONARE_ENDOW_MESSAGE;
      break;
    case CHILD_ACCOUNT_CHILD_MONEY_CONTRACT:
      errorCode = BIP_FAILED_TO_DOWNLOAD_ACCOUNT_MONEY_CHILD_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_ACCOUNT_MONEY_CHILD_MESSAGE;
      break;
    case CITIZEN_MASTER_CONTRACT:
      errorCode = BIP_FAILED_TO_DOWNLOAD_CITIZEN_MASTER_CONTRACT_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_CITIZEN_MASTER_CONTRACT_MESSAGE;
      break;
    case CITIZEN_MASTER_CONTRACT_EXTENSION:
      errorCode = BIP_FAILED_TO_DOWNLOAD_CITIZEN_MASTER_CONTRACT_EXTENSION_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_CITIZEN_MASTER_CONTRACT_EXTENSION_MESSAGE;
      break;
    case ENTITY_TIME_DEPOSIT_CONTRACT:
      errorCode = BIP_FAILED_TO_DOWNLOAD_ENTITY_TIME_DEPOSIT_SIMPLE_SPECIAL_CONTRACT_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_ENTITY_TIME_DEPOSIT_SIMPLE_SPECIAL_CONTRACT_MESSAGE;
      break;
    case ENTITY_PREPAID_INTEREST_DEPOSIT_CONTRACT:
      errorCode = BIP_FAILED_TO_DOWNLOAD_ENTITY_PREPAID_INTEREST_DEPOSIT_SIMPLE_SPECIAL_CONTRACT_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_ENTITY_PREPAID_INTEREST_DEPOSIT_SIMPLE_SPECIAL_CONTRACT_MESSAGE;
      break;
    case ENTITY_ACCOUNT_DEPOSIT_CONTRACT:
      errorCode = BIP_FAILED_TO_DOWNLOAD_ENTITY_ACCOUNT_SIMPLE_SPECIAL_CONTRACT_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_ENTITY_ACCOUNT_SIMPLE_SPECIAL_CONTRACT_MESSAGE;
      break;
    case ACCOUNT_REFERENCE:
      errorCode = BIP_FAILED_TO_DOWNLOAD_ACCOUNT_REFERENCE_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_ACCOUNT_REFERENCE_MESSAGE;
      break;

    default:
      break;
    }
    LOG.error("########### ERROR OCCURRED DURING DOWNLOAD [{}] document from BI PUBLISHER ... \n WITH PROCESS INSTANCE ID = [{}] \n HEADER PARAM = [{}]",
        documentType, instanceId, headerParam);
    throw new BpmServiceException(errorCode, errorMessage);
  }

  private void setLogMessage(String transactionType, String instanceId, String endPoint, String soapAction, String reportAbsolutePathDocType,
      String reportAbsolutePath, String function)
  {
    String type = null;
    switch (transactionType)
    {
    case TAX_PAY:
      type = TAX_PAY;
      break;
    case CUSTOM_PAY:
      type = CUSTOM_PAY;
      break;
    case CUSTOM_PAY_RECEIPT:
      type = CUSTOM_PAY_RECEIPT;
      break;
    case INCOME_TRANSACTION_RECEIPT:
    case CASH_TRANSACTION_RECEIPT:
      type = "income";
      break;
    case EXPENSE_TRANSACTION_RECEIPT:
      type = "expense";
      break;
    case MEMORIAL_ORDER_RECEIPT:
      type = "memorialOrder";
      break;
    case OFF_BALANCE_RECEIPT:
      type = "offBalance";
      break;
    case CASHIER_INCOME_EXPENCE_RECEIPT:
    case ADD_CASH_TRANSACTION_RECEIPT:
      type = "cashierIncomeExpense";
      break;
    case PAYMENT_FORM:
    case NON_CASH_TRANSACTION_RECEIPT:
      type = PAYMENT_FORM;
      break;
    case CURRENCY_EXCHANGE_RECEIPT:
      type = CURRENCY_EXCHANGE_RECEIPT;
      break;
    case E_TRANSACTION_RECEIPT:
      type = E_TRANSACTION_RECEIPT;
      break;
    case E_CUSTOM_PAY_RECEIPT:
      type = E_CUSTOM_PAY_RECEIPT;
      break;
    case CHILD_FUTURE_MILLIONARE_CONTRACT:
      type = CHILD_FUTURE_MILLIONARE_CONTRACT;
      break;
    case CHILD_FUTURE_MILLIONARE_THIRD_PARTY_CONDITION_CONTRACT:
      type = CHILD_FUTURE_MILLIONARE_THIRD_PARTY_CONDITION_CONTRACT;
      break;
    case CHILD_FUTURE_MILLIONARE_ENDOW_CONTRACT:
      type = CHILD_FUTURE_MILLIONARE_ENDOW_CONTRACT;
      break;
    case CHILD_ACCOUNT_CHILD_MONEY_CONTRACT:
      type = CHILD_ACCOUNT_CHILD_MONEY_CONTRACT;
      break;
    case CITIZEN_MASTER_CONTRACT:
      type = CITIZEN_MASTER_CONTRACT;
      break;
    case CITIZEN_MASTER_CONTRACT_EXTENSION:
      type = CITIZEN_MASTER_CONTRACT_EXTENSION;
      break;
    case ENTITY_TIME_DEPOSIT_CONTRACT:
      type = ENTITY_TIME_DEPOSIT_CONTRACT;
      break;
    case ENTITY_PREPAID_INTEREST_DEPOSIT_CONTRACT:
      type = ENTITY_PREPAID_INTEREST_DEPOSIT_CONTRACT;
      break;
    case ENTITY_ACCOUNT_DEPOSIT_CONTRACT:
      type = ENTITY_ACCOUNT_DEPOSIT_CONTRACT;
      break;
    case ACCOUNT_REFERENCE:
      type = ACCOUNT_REFERENCE;
      break;
    default:
      break;
    }
    LOG.info("########### DOWNLOAD [{}] DOCUMENT FROM BI PUBLISHER ... WITH PROCESS INSTANCE ID = [{}] \n "
            + "ENDPOINT = [{}], SOAP =[{}], REPORT_ABSOLUTE_PATH = [{}], FUNCTION=[{}]",
        type, instanceId, endPoint + function, soapAction, "/" + reportAbsolutePathDocType + "/" + reportAbsolutePath + ".xdo", function);
  }
}
