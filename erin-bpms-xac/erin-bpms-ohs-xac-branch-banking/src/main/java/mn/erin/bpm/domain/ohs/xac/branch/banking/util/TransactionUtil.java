package mn.erin.bpm.domain.ohs.xac.branch.banking.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.bpm.model.branch_banking.transaction.CustomerTransaction;
import mn.erin.domain.bpm.model.branch_banking.transaction.ETransaction;
import mn.erin.domain.bpm.model.branch_banking.transaction.TransactionId;
import mn.erin.domain.bpm.service.BpmServiceException;

import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.BRANCH_ID;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.TRANSACTIONS;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.TRANSACTION_ID;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.TRANSACTION_TYPE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.TRAN_ACCOUNT_ID;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.TRAN_AMOUNT;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.TRAN_CURRENCY;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.TRAN_DATE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.TRAN_ID;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.TRAN_PARTICULARS;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.TRAN_STATUS;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.TRAN_SUBTYPE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.USER_ID_RESPONSE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PARSE_DATE_ERROR_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PARSE_DATE_ERROR_MESSAGE;
import static mn.erin.domain.bpm.util.process.BpmNumberUtils.getThousandSepStrWithDigit;
import static mn.erin.domain.bpm.util.process.BpmUtils.getStringValueFromJson;

/**
 * @author Tamir
 */

public final class TransactionUtil
{
  private static final Logger LOGGER = LoggerFactory.getLogger(TransactionUtil.class);
  private static final String HASH_CONSTANT = "###############  ";
  private TransactionUtil()
  {

  }

  public static Collection<CustomerTransaction> toCustomerTransactions(JSONObject jsonResponse, String instanceId)
  {
    if (null == jsonResponse || jsonResponse.isEmpty())
    {
      LOGGER.error(HASH_CONSTANT + "CUSTOMER TRANSACTION LIST IS EMPTY WITH PROCESS INSTANCE ID = [{}] \n AND RESPONSE = [{}]", instanceId, jsonResponse);
      return Collections.emptyList();
    }

    List<CustomerTransaction> transactions = new ArrayList<>();

    JSONArray transactionsArray = jsonResponse.getJSONArray(TRANSACTIONS);

    for (int i = 0; i < transactionsArray.length(); i++)
    {
      JSONObject transactionJSON = transactionsArray.getJSONObject(i);

      String tranID = getString(transactionJSON.get(TRAN_ID));
      String tranDate = getString(transactionJSON.get(TRAN_DATE));

      double tranAmountValue = Double.parseDouble(getString(transactionJSON.get(TRAN_AMOUNT)));
      String tranAmount = getThousandSepStrWithDigit(tranAmountValue,2);
      String tranCcy = getString(transactionJSON.get(TRAN_CURRENCY));

      String branchId = getString(transactionJSON.get(BRANCH_ID));
      String accountId = getString(transactionJSON.get(TRAN_ACCOUNT_ID));

      String userID = getString(transactionJSON.get(USER_ID_RESPONSE));
      String type = getString(transactionJSON.get(TRANSACTION_TYPE));

      String subType = getString(transactionJSON.get(TRAN_SUBTYPE));
      String status = getString(transactionJSON.get(TRAN_STATUS));
      String tranParticulars = getString(transactionJSON.get(TRAN_PARTICULARS));

      transactions.add(new CustomerTransaction(TransactionId.valueOf(tranID), tranDate, tranAmount,
          tranCcy, branchId, accountId, userID, type, subType, status, tranParticulars));
    }
    sortList(transactions);
    return transactions;
  }

  public static List<ETransaction> toETransactionList(JSONObject response, String channel, String instanceId) throws BpmServiceException
  {
    if (response.isEmpty())
    {
      LOGGER.error(HASH_CONSTANT + "E-BANK TRANSACTION LIST IS EMPTY WITH PROCESS INSTANCE ID = [{}] \n AND RESPONSE = [{}]", instanceId, response);
      return Collections.emptyList();
    }

    if (!response.has(TRANSACTIONS))
    {
      LOGGER.error(HASH_CONSTANT + "E-BANK TRANSACTION LIST RESPONSE HAS NOT 'Transactions' PARAMETER  WITH PROCESS INSTANCE ID = [{}] \n AND RESPONSE = [{}]", instanceId, response);
      return Collections.emptyList();
    }

    List<ETransaction> transactionsList = new ArrayList<>();
    if (response.get(TRANSACTIONS) instanceof JSONArray)
    {
      JSONArray transactions = response.getJSONArray(TRANSACTIONS);

      for (int index = 0; index < transactions.length(); index++)
      {
        JSONObject transaction = (JSONObject) transactions.get(index);
        getTransactionsInfo(transaction, transactionsList, channel, instanceId);
      }
    }
    else
    {
      JSONObject transaction = (JSONObject) response.get(TRANSACTIONS);
      getTransactionsInfo(transaction, transactionsList, channel, instanceId);
    }

    return transactionsList;
  }

  private static void getTransactionsInfo(JSONObject transaction, List<ETransaction> transactionsList, String channel, String instanceId) throws BpmServiceException
  {
    String tranId = getStringValueFromJson(transaction, "TranID");
    String tranDt = getStringValueFromJson(transaction, "TranDt");
    String tranCcy = getStringValueFromJson(transaction, "TranCcy");
    double tranAmountValue= Double.parseDouble(getStringValueFromJson(transaction, "TranAmount"));
    String tranAmount = getThousandSepStrWithDigit(tranAmountValue, 2);
    String rate = getStringValueFromJson(transaction, "Rate");
    String fee = getStringValueFromJson(transaction, "Fee");
    String particulars = getStringValueFromJson(transaction, "TranParticulars");
    String accountId = getStringValueFromJson(transaction, "AccountID");
    String accountName = getStringValueFromJson(transaction, "AccountName");
    String toBranchId = getStringValueFromJson(transaction, "ToBranchID");
    String toAccountId = getStringValueFromJson(transaction, "ToAccountID");
    String toAccountName = getStringValueFromJson(transaction, "ToAccountName");
    String toBankName = getStringValueFromJson(transaction, "ToBankName");
    String toCcy = getStringValueFromJson(transaction, "ToCcy");
    double toAmountValue = Double.parseDouble(getStringValueFromJson(transaction, "ToAmount"));
    String toAmount = getThousandSepStrWithDigit(toAmountValue, 2);
    String userId = getStringValueFromJson(transaction, "UserID");
    try
    {
      tranDt = formatDate(tranDt);
    }
    catch (ParseException e)
    {
      LOGGER.error(HASH_CONSTANT + "ERROR OCCURRED DURING DOWNLOAD E-BANK TRANSACTION LIST. \n FAILED TO PARSE TRANSACTION DATE = [{}], PROCESS INSTANCE ID = [{}]", tranDt, instanceId);
      throw new BpmServiceException(PARSE_DATE_ERROR_CODE, PARSE_DATE_ERROR_MESSAGE);
    }

    ETransaction eTransaction = new ETransaction(userId, tranId, tranDt, channel, accountName, " ", accountId, tranAmount, tranCcy, particulars, toBankName,
        toBranchId, toAccountName, toAccountId, toAmount, toCcy, rate, fee);
    transactionsList.add(eTransaction);
  }

  private static String getString(Object value)
  {
    if (null == value)
    {
      return "";
    }
    return String.valueOf(value);
  }

  private static int sortList(List<CustomerTransaction> customerTransactions)
  {
    customerTransactions.sort((o1, o2) -> {
      if (o1.getTransactionDate() == null || o2.getTransactionDate() == null)
        return 0;
      return o1.getTransactionDate().compareTo(o2.getTransactionDate());
    });
    return 0;
  }

  private static String formatDate(String dateString) throws ParseException
  {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    return format.format(format.parse(dateString));
  }

  public static Map<String, String> toAddTransaction(JSONObject jsonResponse)
  {
    Map<String, String> transactionInfo = new HashMap<>();
    transactionInfo.put(TRANSACTION_ID, String.valueOf(jsonResponse.get("TrnId")));
    transactionInfo.put(TRAN_DATE, String.valueOf(jsonResponse.get("TrnDt")));

    return transactionInfo;
  }
}
