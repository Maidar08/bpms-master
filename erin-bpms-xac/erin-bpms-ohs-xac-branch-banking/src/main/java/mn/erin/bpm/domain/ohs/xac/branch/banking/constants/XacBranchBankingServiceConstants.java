package mn.erin.bpm.domain.ohs.xac.branch.banking.constants;

/**
 * @author Lkhagvadorj.A
 **/

public class XacBranchBankingServiceConstants
{
  private XacBranchBankingServiceConstants()
  {

  }

  public static final String INVOICE = "invoice";
  public static final String INVOICES = "invoices";
  public static final String INVOICE_NO = "invoiceNo";
  public static final String INVOICE_TYPE = "invoiceType";
  public static final String AMOUNT = "amount";
  public static final String YEAR = "year";
  public static final String TRANSACTION_NUMBER = "transactionNumber";
  public static final String PAID_DATE = "paidDate";
  public static final String ACCOUNT_ID = "AcctId";
  public static final String ACC_ID = "AccountId";
  public static final String ACC_LIST = "accList";

  public static final String PAY_TYPE = "payType";
  public static final String CASH = "cash";
  public static final String PAYMENT_AMOUNT = "paymentAmount";
  public static final String TIN = "tin";
  public static final String TAX_PAYER_NAME = "taxpayerName";
  public static final String ACCOUNT_NUMBER = "accountNumber";
  public static final String ACCOUNT_NAME = "accountName";
  public static final String INVOICE_NUMBER = "invoiceNo";
  public static final String PIN = "pin";
  public static final String STATE_ACCOUNT = "stateAccount";
  public static final String STATE_ACCOUNT_NAME = "stateAccountName";

  //custom

  public static final String ACCOUNT_NO_CUSTOM_SERVICE = "AccountNo";
  public static final String TRANSACTION_VALUE_CUSTOM_SERVICE = "TrnValue";
  public static final String PHONE_NUMBER_CUSTOM_SERVICE = "phonenumber";

  public static final String BRANCH_NAME_SERVICE = "branch_name";
  public static final String DECLARATION_DATE_SERVICE = "declaration_date";
  public static final String INVOICE_NUM_CUSTOM_SERVICE = "invoice_num";
  public static final String INVOICE_TYPE_NAME_CUSTOM_SERVICE = "invoice_type_name";

  public static final String PAYMENT_LIST_SERVICE = "payment_list";
  public static final String BANK_ACC_LIST_SERVICE = "bank_acc_list";
  public static final String BANK_ACC_NAME_SERVICE = "bank_acc_name";
  public static final String BANK_ACC_NUMBER_SERVICE = "bank_acc_number";
  public static final String BANK_CODE_SERVICE = "bank_code";
  public static final String BANK_NAME_SERVICE = "bank_name";
  public static final String PAYMENT_ACC_NAME_SERVICE = "payment_Acc_Name";
  public static final String PAYMENT_ACC_NUMBER_SERVICE = "payment_Acc_Number";
  public static final String PAYMENT_AMOUNT_CUSTOM_SERVICE = "payment_amount";
  public static final String REGISTER_ID_SERVICE = "register_id";
  public static final String TAX_PAYER_NAME_SERVICE = "taxpayer_name";

  public static final String CUSTOM_TRANSACTION_SERVICE = "Transaction";
  public static final String CUSTOM_INVOICE_SERVICE = "Invoice";
  public static final String BATCH_NO = "BatchNo";
  public static final String DESCRIPTION = "Desc";
  public static final String REF_NO = "RefNo";
  public static final String AUTH_STATUS = "AuthStatus";
  public static final String TRANSACTION_DT = "TrnDt";

  public static final String ACTION = "action";

  /* USSD Service */
  public static final String CODE = "Code";
  public static final String USSD_STATUS = "Status";
  public static final String CIF = "cif";
  public static final String PHONE = "phone";
  public static final String CUSTOM_BRANCH = "branch";
  public static final String PHONE_USSD = "phoneUssd";
  public static final String CUSTOMER_NAME_ALT = "customerName";
  public static final String REGISTER_NUMBER = "registerNo";
  public static final String BANK_CORE_NUMBER = "phoneCore";
  public static final String USER_STATUS = "userStatus";
  public static final String REGISTERED_BRANCH = "regBrn";
  public static final String REGISTERED_DATE = "makerDtStamp";
  public static final String REGISTERED_USER = "makerId";
  public static final String ACCOUNTS = "Accounts";
  public static final String ACCOUNT = "Account";
  public static final String IS_ACCOUNT_REGISTERED = "IsRegistered";
  public static final String IS_PRIMARY_ACCOUNT = "IsPrimaryAccount";
  public static final String PRODUCT_CODE = "ProductCode";
  public static final String ACCOUNT_TYPE = "AccountType";
  public static final String ACCOUNT_NUMBER_ALT = "AccountNumber";
  public static final String CURRENCY_CODE = "CurrencyCode";
  public static final String FAILED_LOGIN_COUNT = "failureLoginCnt";
  public static final String LAST_LOGGED_DATE = "lastLoggedDate";
  public static final String LANGUAGE_ID = "LanguageId";
  public static final String CUSTOMER_DETAIL = "customerDetial";
  public static final String IS_UPDATE = "isUpdate";
  public static final String MOBILE = "mobile";
  public static final String STATUS = "status";

  //used camel case
  public static final String ACCOUNT_TYPE_LOWERCASE = "accountType";
  public static final String PRODUCT_CODE_LOWERCASE = "productCode";

  public static final String CHANNEL ="channel";
  public static final String DESTINATION = "destination";
  public static final String CONTENT_MESSAGE = "contentMessage";



  /* GetAccountInfo */
  public static final String CUSTOMER_NAME = "CustomerName";
  public static final String CURRENCY = "Currency";
  public static final String AVAILABLE_BALANCE = "AvailableBalance";
  public static final String CURRENT_BALANCE = "CurrentBalance";
  public static final String OPEN_DATE = "OpenDate";
  public static final String CLOSED_DATE = "ClosedDate";
  public static final String EMPTY_STRING = "";
  public static final String JOINT_HOLDERS = "Jointholders";
  public static final String JOINT_CUSTOMER_ID = "JointCustomerId";
  public static final String JOINT_CUSTOMER_NAME = "JointCustomerName";
  public static final String ACCOUNT_STATUS = "AccountStatus";
  public static final String DORMANCY_ACCOUNT = "Dormancy";
  public static final String FROZEN_ACCOUNT = "Frozen";
  public static final String NO_DEBIT_ACCOUNT = "NoDebit";
  public static final String ACCOUNT_RECORD_STATUS = "RecordStatus";
  public static final String TD_DETAILS= "Tddetails";
  public static final String MATURITY_DATE = "MaturityDate";
  public static final String HAS_ACCESS = "HasAccess";


  public static final String ACCOUNT_CLASS = "AccountClass";
  public static final String ACCOUNT_CLASS_TYPE = "AccountClassType";
  public static final String YES_STRING = "Y";


  /*GetAccountReference*/
  public static final String ACCOUNT_NO = "accountNo";
  public static final String BRANCH = "brn";
  public static final String FEE = "fee";

  /*LoanRepayment*/
  public static final String BASIC_PAYMENT = "basicPayment"; // үндсэн төлбөр
  public static final String INTEREST_PAYMENT = "interestPayment"; // хүүний төлбөр
  public static final String PENALTY_AMOUNT = "penaltyAmount"; // алданги
  public static final String FEE_PAYMENT = "feePayment"; // шимтгэлийн төлбөр
  public static final String TOTAL_AMOUNT = "totalAmount"; // бүх төлбөрийн дүн
  public static final String SCHEDULED = "scheduled"; // хуваарийн төрөл сонгох
  /* AddTransaction */
  public static final String TRANSACTION_ID = "TrnId";

  /*Get transaction list */
  public static final String TRAN_DATE = "TranDt";
  public static final String USER_ID = "UserId";
  public static final String USER_ID_RESPONSE = "UserID";
  public static final String TRANSACTIONS = "Transactions";
  public static final String TRAN_ID = "TranID";
  public static final String TRAN_AMOUNT = "TranAmount";
  public static final String TRAN_CURRENCY = "TranCcy";
  public static final String BRANCH_ID = "BranchID";
  public static final String TRAN_ACCOUNT_ID = "AccountID";
  public static final String TRANSACTION_TYPE = "TranType";
  public static final String TRAN_SUBTYPE = "TranSubType";
  public static final String TRAN_STATUS = "TranStatus";
  public static final String TRAN_PARTICULARS = "TranParticulars";

  /* GetcAccountNames */
  public static final String ACC = "account";
  public static final String NAME = "name";
  public static final String ACC_CURRENCY = "ccy";

  // BI Publisher
  public static final String TRANSACTION = "transaction";
  public static final String CONTRACT = "contract";

  /*USSD constants*/
  public static final String ACCOUNT_TYPE_C= "C";
  public static final String ACCOUNT_TYPE_U= "U";
  public static final String ACCOUNT_TYPE_S = "S";

}
