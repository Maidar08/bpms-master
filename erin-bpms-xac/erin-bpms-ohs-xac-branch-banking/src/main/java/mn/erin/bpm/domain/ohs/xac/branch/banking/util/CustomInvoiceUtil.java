package mn.erin.bpm.domain.ohs.xac.branch.banking.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.bpm.model.branch_banking.CustomInvoice;
import mn.erin.domain.bpm.model.branch_banking.PaymentInfo;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.util.process.BpmUtils;

import static ch.qos.logback.core.CoreConstants.EMPTY_STRING;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.AUTH_STATUS;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.BANK_ACC_LIST_SERVICE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.BANK_ACC_NAME_SERVICE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.BANK_ACC_NUMBER_SERVICE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.BANK_CODE_SERVICE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.BANK_NAME_SERVICE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.BATCH_NO;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.BRANCH_NAME_SERVICE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.DECLARATION_DATE_SERVICE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.DESCRIPTION;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.INVOICE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.INVOICE_NUM_CUSTOM_SERVICE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.INVOICE_TYPE_NAME_CUSTOM_SERVICE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.PAYMENT_ACC_NAME_SERVICE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.PAYMENT_ACC_NUMBER_SERVICE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.PAYMENT_AMOUNT_CUSTOM_SERVICE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.PAYMENT_LIST_SERVICE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.REF_NO;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.REGISTER_ID_SERVICE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.TAX_PAYER_NAME_SERVICE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.TRANSACTION_DT;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.INVALID_BANK_ACC_LIST_CODE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.INVALID_BANK_ACC_LIST_MESSAGE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.INVALID_PAYMENT_LIST_FORMAT_CODE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.INVALID_PAYMENT_LIST_FORMAT_MESSAGE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.INVOICE_PAYMENT_LIST_EMPTY_CODE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.INVOICE_PAYMENT_LIST_INVOICE_MESSAGE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CHARGE;
import static mn.erin.domain.bpm.util.process.BpmUtils.getStringValueFromJson;

public class CustomInvoiceUtil
{
  private static String branchName;
  private static String invoiceNumber;
  private static String invoiceTypeName;
  private static String registerNumber;
  private static String taxPayerName;
  private static String declarationDate;
  private static String charge;

  private static final Logger LOGGER = LoggerFactory.getLogger(CustomInvoiceUtil.class);
  private static final String HASH_CONSTANT = "###############  ";

  public static List<CustomInvoice> toCustomList(JSONObject response, String instanceId)
  {
    List<CustomInvoice> customInfoList = new ArrayList<>();

    if (!response.has(INVOICE))
    {
      return Collections.emptyList();
    }

    if (response.get(INVOICE) instanceof JSONArray)
    {
      JSONArray invoiceArray = (JSONArray) response.get(INVOICE);

      for (int index = 0; index < invoiceArray.length(); index++)
      {
        JSONObject invoice = (JSONObject) invoiceArray.get(index);
        List<PaymentInfo> paymentInfoList = new ArrayList<>();

        if (toCustomInvoice(invoice, paymentInfoList, customInfoList, response, instanceId))
        {
          return Collections.emptyList();
        }
      }
    }
    else
    {
      JSONObject invoice = response.getJSONObject(INVOICE);
      List<PaymentInfo> paymentListArray = new ArrayList<>();

      if (toCustomInvoice(invoice, paymentListArray, customInfoList, response, instanceId))
      {
        return Collections.emptyList();
      }
    }

    return customInfoList;
  }

  private static boolean toCustomInvoice(JSONObject invoice, List<PaymentInfo> paymentInfoList, List<CustomInvoice> customInfoList, JSONObject response, String instanceId)
  {
    setCustomInfos(invoice);

    try
    {
      getPaymentInfo(invoice, paymentInfoList);

      customInfoList.add(
          new CustomInvoice(branchName, charge, declarationDate, invoiceNumber, invoiceTypeName, paymentInfoList, registerNumber, taxPayerName));

      return false;
    }
    catch (BpmServiceException | RuntimeException e)
    {
      LOGGER.error(HASH_CONSTANT + "ERROR OCCURRED WHEN GET CUSTOM INFO LIST WITH PROCESS INSTANCE ID = [{}] \n AND RESPONSE = [{}]", instanceId, response);
      return true;
    }
  }

  private static void setCustomInfos(JSONObject object)
  {
    branchName = getStringValueFromJson(object, BRANCH_NAME_SERVICE);
    invoiceTypeName = getStringValueFromJson(object, INVOICE_TYPE_NAME_CUSTOM_SERVICE);
    taxPayerName = getStringValueFromJson(object, TAX_PAYER_NAME_SERVICE);

    invoiceNumber = toStringValueFromJson(object, INVOICE_NUM_CUSTOM_SERVICE);
    registerNumber = toStringValueFromJson(object, REGISTER_ID_SERVICE);
    charge = toStringValueFromJson(object, CHARGE);
    declarationDate = toStringValueFromJson(object, DECLARATION_DATE_SERVICE);
  }

  private static void setPaymentInfo(JSONObject paymentInfo, List<PaymentInfo> paymentInfoList) throws BpmServiceException
  {
    String paymentAmountString = toStringValueFromJson(paymentInfo, PAYMENT_AMOUNT_CUSTOM_SERVICE);
    BigDecimal paymentAmount = new BigDecimal(paymentAmountString);
    String paymentAccountNumber = toStringValueFromJson(paymentInfo, PAYMENT_ACC_NUMBER_SERVICE);
    String paymentAccountName = getStringValueFromJson(paymentInfo, PAYMENT_ACC_NAME_SERVICE);

    if (!paymentInfo.has(BANK_ACC_LIST_SERVICE) || !(paymentInfo.get(BANK_ACC_LIST_SERVICE) instanceof JSONObject))
    {
      throw new BpmServiceException(INVALID_BANK_ACC_LIST_CODE, INVALID_BANK_ACC_LIST_MESSAGE);
    }
    JSONObject bankInfo = (JSONObject) paymentInfo.get(BANK_ACC_LIST_SERVICE);

    String bankAccountName = getStringValueFromJson(bankInfo, BANK_ACC_NAME_SERVICE);
    String bankAccountNumber = toStringValueFromJson(bankInfo, BANK_ACC_NUMBER_SERVICE);
    String bankCode = toStringValueFromJson(bankInfo, BANK_CODE_SERVICE);
    String bankName = getStringValueFromJson(bankInfo, BANK_NAME_SERVICE);

    paymentInfoList
        .add(new PaymentInfo(bankAccountName, bankAccountNumber, bankCode, bankName, paymentAccountName, paymentAccountNumber, paymentAmount, invoiceTypeName));
  }

  private static void getPaymentInfo(JSONObject object, List<PaymentInfo> paymentInfoList) throws BpmServiceException
  {
    if (!object.has(PAYMENT_LIST_SERVICE))
    {
      throw new BpmServiceException(INVOICE_PAYMENT_LIST_EMPTY_CODE, INVOICE_PAYMENT_LIST_INVOICE_MESSAGE);
    }

    if (object.get(PAYMENT_LIST_SERVICE) instanceof JSONArray)
    {
      JSONArray paymentJsonArray = object.getJSONArray(PAYMENT_LIST_SERVICE);

      for (int index = 0; index < paymentJsonArray.length(); index++)
      {
        JSONObject paymentInfo = paymentJsonArray.getJSONObject(index);
        setPaymentInfo(paymentInfo, paymentInfoList);
      }
    }
    else if (object.get(PAYMENT_LIST_SERVICE) instanceof JSONObject)
    {
      JSONObject paymentJsonObject = object.getJSONObject(PAYMENT_LIST_SERVICE);
      setPaymentInfo(paymentJsonObject, paymentInfoList);
    }
    else
    {
      throw new BpmServiceException(INVALID_PAYMENT_LIST_FORMAT_CODE, INVALID_PAYMENT_LIST_FORMAT_MESSAGE);
    }
  }

  private static String toStringValueFromJson(JSONObject object, String name)
  {
    return object.has(name) ? BpmUtils.toString(object.get(name)) : EMPTY_STRING;
  }

  public static Map<String, String> toCustomTransactionResponse(JSONObject jsonResponse)
  {
    Map<String, String> transactionInfo = new HashMap<>();
    transactionInfo.put(BATCH_NO, String.valueOf(jsonResponse.get("BatchNo")));
    transactionInfo.put(DESCRIPTION, String.valueOf(jsonResponse.get("Desc")));
    transactionInfo.put(REF_NO, String.valueOf(jsonResponse.get("RefNo")));
    transactionInfo.put(AUTH_STATUS, String.valueOf(jsonResponse.get("AuthStatus")));
    transactionInfo.put(TRANSACTION_DT, String.valueOf(jsonResponse.get("TrnDt")));
    return transactionInfo;
  }
}
