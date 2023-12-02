package mn.erin.domain.bpm;

public class BpmBranchBankingConstants
{
  // Branch Banking
  // Billing
  // Tax
  public static final String INVOICE_NO = "invoiceNo";
  public static final String INVOICE_NUMBER = "invoiceNumber";
  public static final String INVOICE_AMOUNT = "invoiceAmount";
  public static final String PREVIOUS_INVOICE_AMOUNT = "previousInvoiceAmount";
  public static final String INVOICE_TYPE = "invoiceType";
  public static final String PAY_FULL = "payFull";
  public static final String TRANSACTION_TYPE = "transactionType";
  public static final String TIN = "tin";
  public static final String TAX_PAYER_NAME = "taxPayerName";
  public static final String TRANSACTION_DESC = "transactionDesc";
  public static final String TAX_ACCOUNT = "taxAccount";
  public static final String TAX_ACCOUNT_NAME = "taxAccountName";
  public static final String PIN = "pin";
  public static final String STATE_ACCOUNT = "stateAccount";
  public static final String STATE_ACCOUNT_NAME = "stateAccountName";
  public static final String TRANSACTION_NUMBER = "transactionNumber";
  public static final String PAID_DATE = "paidDate";
  public static final String PAY_CUT_AMOUNT = "payCutAmount";
  public static final String TAX_CURRENCY = "currency";
  public static final String TAX_NUMBER = "taxNumber";
  public static final String TAX_TYPE_CODE = "taxTypeCode";
  public static final String ASSET_DETAIL = "assetDetail";
  public static final String PERIOD = "period";
  public static final String PAY_MORE = "payMore";
  public static final String AMOUNT = "amount";
  public static final String BRANCH_CODE = "branchCode";
  public static final String PERIOD_TYPE = "periodType";
  public static final String SUB_BRANCH_NAME = "subBranchName";
  public static final String TAX_TYPE_NAME = "taxTypeName";
  public static final String YEAR = "year";
  public static final String SUB_BRANCH_CODE = "subBranchCode";
  public static final String ACCOUNT_BRANCH = "accountBranch";
  public static final String CCY = "ccy";
  public static final String GROUP_ID = "groupId";
  public static final String ACCOUNT_HAS_ACCESS = "accountHasAccess";

  // Custom constants
  public static final String ACCOUNT_NUMBER = "accountNumber";
  public static final String TRANSACTION_DESCRIPTION = "transactionDescription";
  public static final String SEARCH_TYPE = "searchType";
  public static final String SEARCH_VALUE_CUSTOM = "searchValueCustom";
  public static final String SEARCH_VALUE_TAX = "searchValueTax";
  public static final String CURRENCY_TYPE = "currencyType";
  public static final String TRANSACTION_CURRENCY = "transactionCurrency";
  public static final String PAY_AMOUNT = "payAmount";
  public static final String DEDUCTION_AMOUNT = "deductionAmount";
  public static final String ACCOUNT_CURRENCY = "accountCurrency";
  public static final String ACCOUNT_NAME = "accountName";
  public static final String ACCOUNT_BALANCE = "accountBalance";
  public static final String CUSTOMER_NAME = "customerName";
  public static final String USER_NAME = "userName";
  public static final String ATTACHMENT_NUMBER = "attachmentNumber";
  public static final String NAME = "name";
  public static final String PARENTS_NAME = "parentsName";
  public static final String CUSTOM_TAX_PAYER_NAME = "taxPayerName";

  public static final String BRANCH_NAME = "branchName";

  public static final String REGISTERED_BRANCH = "registeredBranch";

  public static final String CHARGE = "charge";
  public static final String DECLARATION_DATE = "declarationDate";
  public static final String PAYMENT_LIST = "paymentList";
  public static final String BANK_ACCOUNT_NAME = "bankAccountName";
  public static final String BANK_ACCOUNT_NUMBER = "bankAccountNumber";
  public static final String BANK_CODE = "bankCode";
  public static final String BANK_NAME = "bankName";
  public static final String PAYMENT_ACCOUNT_NAME = "paymentAccountName";
  public static final String PAYMENT_ACCOUNT_NUMBER = "paymentAccountNumber";
  public static final String PAYMENT_AMOUNT = "paymentAmount";
  public static final String REGISTER_ID = "registerId";
  public static final String VALUE = "value";

  //Account info
  public static final String CUSTOMER_FULL_NAME = "customerFullName";
  public static final String ACCOUNT_BALANCE_REF = "accountBalanceRef";
  public static final String ACCOUNT_CREATED_DATE = "accountCreatedDt";
  public static final String ACCOUNT_END_DATE = "accountCloseDt";
  public static final String ACCOUNT_JOINT_HOLDERS = "accountJointHolders";
  public static final String ACCOUNT_CCY = "accountCcy";
  public static final String BLNC = "blnc";

  /****** BI Piblisher  ******/

  // Billing
  public static final String TAX_PAY = "taxPayTransaction";
  public static final String CUSTOM_PAY = "customPayTransaction";

  // Customer Transaction
  public static final String INCOME_TRANSACTION_RECEIPT = "incomeTransactionReceipt";
  public static final String EXPENSE_TRANSACTION_RECEIPT = "expenseTransactionReceipt";
  public static final String MEMORIAL_ORDER_RECEIPT = "memorialOrderTransaction";
  public static final String OFF_BALANCE_RECEIPT = "offBalanceTransaction";
  public static final String PAYMENT_FORM = "paymentForm";
  public static final String CURRENCY_EXCHANGE_RECEIPT = "currencyExchangeReceipt";
  public static final String CASHIER_INCOME_EXPENCE_RECEIPT = "cashierIncomeExpenceTransaction";
  public static final String CUSTOM_PAY_RECEIPT = "customPayTransactionReceipt";
  public static final String E_CUSTOM_PAY_RECEIPT = "eCustomPayTransaction";
  public static final String E_TRANSACTION_RECEIPT = "eTransaction";

  //e transaction document constants.
  public static final String TRANSACTION_ID = "transactionId";
  public static final String TRANSACTION_NO = "transactionNo";
  public static final String TRANSACTION_DATE = "transactionDate";

  public static final String TRANSACTION_AMOUNT = "transactionAmount";
  public static final String RATE = "rate";
  public static final String ACCOUNT_ID = "accountId";
  public static final String ACCOUNT_ID_ENTER = "accountIdEnter";
  public static final String PAYER_USER_ID = "payerUserId";
  public static final String TRANSACTION_END_DATE = "transactionEndDate";
  public static final String TRANSACTION_START_DATE = "transactionStartDate";
  public static final String TRANSACTION_FORM_TYPE = "transactionFormType";

  public static final String TAX_TRANSACTION_DOC_NAME = "Татварын гүйлгээний баримт - ";
  public static final String CUSTOM_TRANSACTION_DOC_NAME = "Гаалийн гүйлгээний баримт - ";
  public static final String CUSTOM_TRANSACTION_RECEIPT_DOC_NAME = "Гаалийн гүйлгээний маягт - ";
  public static final String INCOME_TRANSACTION_DOC_NAME = "Орлогийн  баримт - ";
  public static final String EXPENSE_TRANSACTION_DOC_NAME = "Зарлагын баримт - ";
  public static final String MEMORIAL_ORDER_DOC_NAME = "Мемориалын ордер маягт - ";
  public static final String OFF_BALANCE_DOC_NAME = "Тэнцлийн гадуурх гүйлгээний маягт - ";
  public static final String CASHIER_INCOME_EXPENSE_DOC_NAME = "Кассын орлого зарлагын маягт - ";
  public static final String PAYMENT_FORM_DOC_NAME = "Төлбөрийн маягт - ";
  public static final String CURRENCY_EXCHANGE_RECEIPT_NAME = "Валют арилжааны маягт - ";
  public static final String E_CUSTOM_PAY_DOC_NAME = " Гаалийн цахим гүйлгээний маягт - ";
  public static final String E_TRANSACTION_DOC_NAME = "Цахим гүйлгээ - ";

  //Deposit Contract
  public static final String CHILD_FUTURE_MILLIONARE_CONTRACT_NAME = "Хүүхэд-Ирээдүйн саятан - ";
  public static final String CHILD_FUTURE_MILLIONARE_THIRD_PARTY_CONTRACT_NAME = "Хүүхэд-Ирээдүйн саятан гуравдагч нөхцөл - ";
  public static final String CHILD_FUTURE_MILLIONARE_ENDOW_NAME = "Хүүхэд-Ирээдүйн саятан бэлэглэл - ";
  public static final String CHILD_ACCOUNT_CHILD_MONEY_NAME = "Хүүхэд-Харилцах - Хүүхдийн мөнгө - ";
  public static final String CITIZEN_MASTER_CONTRACT_NAME = "Иргэн-Мастер гэрээ - ";
  public static final String CITIZEN_MASTER_CONTRACT_EXTENSION_NAME = "Иргэн-Мастер гэрээний хавсралт - ";
  public static final String ENTITY_TIME_DEPOSIT_SIMPLE_SPECIAL_CONTRACT_NAME = "ААН-Хугацаатай хадгаламж энгийн-тусгай  - ";
  public static final String ENTITY_PREPAID_INTEREST_DEPOSIT_SIMPLE_SPECIAL_CONTRACT_NAME = "ААН-Хүүгээ зарлагаддаг хадгаламж энгийн-тусгай  - ";
  public static final String ENTITY_ACCOUNT_SIMPLE_SPECIAL_CONTRACT_NAME = "ААН-Харилцах данс энгийн-тусгай  - ";

  public static final String CHILD_FUTURE_MILLIONARE_CONTRACT = "childFutureMillionare";
  public static final String CHILD_FUTURE_MILLIONARE_THIRD_PARTY_CONDITION_CONTRACT = "childFutureMillionareThirdPartyCondition";
  public static final String CHILD_FUTURE_MILLIONARE_ENDOW_CONTRACT = "childFutureMillionareEndow";
  public static final String CHILD_ACCOUNT_CHILD_MONEY_CONTRACT = "childAccountChildMoney";
  public static final String CITIZEN_MASTER_CONTRACT = "citizenMasterContract";
  public static final String CITIZEN_MASTER_CONTRACT_EXTENSION = "citizenMasterContractExtension";
  public static final String ENTITY_TIME_DEPOSIT_CONTRACT = "entityTimeDepositContract";
  public static final String ENTITY_PREPAID_INTEREST_DEPOSIT_CONTRACT = "entityPrepaidInterestDepositContract";
  public static final String ENTITY_ACCOUNT_DEPOSIT_CONTRACT = "entityAccountDepositContract";

  //Account Reference
  public static final String ACCOUNT_REFERENCE = "accountReference";
  public static final String ACCOUNT_REFERENCE_NAME = "Дансны тодорхойлолт - ";
  public static final String REDUCE_FEES = "reduceFees";
  public static final String PRINT_BALANCE_AMOUNT = "printBalanceAmount";
  public static final String PRINT_AVERAGE_AMOUNT = "printAverageAmount";
  public static final String SHOW_ACCOUNT_CREATED_DATE = "showAccountCreatedDt";
  public static final String ADDITIONAL_INFO = "additionalInfo";
  public static final String LANGUAGE = "language";
  public static final String EXEMPT_FROM_FEES = "exemptFromFees";
  public static final String FEES_AMOUNT = "feesAmount";
  public static final String JOINT_HOLDERS_LIST = "jointHolders";
  public static final String DESC_RECIPIENT = "descriptionRecipient";
  public static final String ENGLISH = "Англи";
  public static final String ADD_CASH_TRANSACTION_RECEIPT = "addCashTransactionReceipt";
  public static final String NON_CASH_TRANSACTION_RECEIPT = "nonCashTransactionReceipt";
  public static final String CASH_TRANSACTION_RECEIPT = "cashTransactionReceipt";

  //USSD
  public static final String MAIN_ACCOUNTS = "mainAccounts";
  public static final String IS_PRIMARY = "isPrimary";

  //Salary Package Transaction
  public static final String SALARY_PACKAGE_TRANSACTION = "salaryPackageTransaction";
  public static final String SALARY_PACKAGE_TRANSACTION_NAME_MN = "Цалингийн багц гүйлгээ - ";
  public static final String KEY = "key";
  public static final String HEADER_NAME = "headerName";
  public static final String HEADERS_ENVIRONMENT_PATH = "branch.banking.SalaryPackageTransaction.headers";
  public static final String AMOUNT_CAPITAL_A = "Amount";
  public static final String CURRENCY = "Currency";
  public static final String TRANSACTION_COUNT = "transactionCount";
  public static final String TRANSACTION_TOTAL_AMOUNT = "transactionTotalAmount";
  public static final String HAS_FEE = "hasFee";
  public static final String INVALID_ACCOUNTS = "invalidAccounts";
  public static final String TRANSACTION_DT = "transactionDt";
  public static final String FILE_NAME = "fileNameChips";

  //OTP
  public static final String TIME_OUT = "Time out";
  public static final String FAILED = "Failed";

  // scheduled and unscheduled loan repayment
  public static final String CASH = "Бэлэн";
  public static final String NON_CASH = "Бэлэн бус";
  public static final String PAY_LOAN_AMOUNT = "payLoanAmount";
  public static final String LOAN_BALANCE = "loanBalance";
  public static final String TOTAL_AMOUNT = "totalAmount";
  public static final String CURRENCY_VALUE = "currencyValue";
  public static final String TRAN_TYPE = "TranType";
  public static final String FROM_ACCOUNT_ID = "FromAccountID";
  public static final String FROM_ACC_CURRENCY = "FromAccCurrency";
  public static final String DESCRIPTION = "Description";
  public static final String ACC_ID = "AccountID";
  public static final String TO_ACC_CURRENCY = "ToAccCurrency";
  public static final String TRAN_CURRENCY = "TranCurrency";
  public static final String TRANSACTION_EXCG_RATE_CODE = "tranExcgRateCode";
  public static final String LOAN_REPAYMENT_TYPE = "loanRepaymentType";
  public static final String VALUE_DATE = "ValueDate";
  public static final String TRAN_ID = "TrnId";
  public static final String TRAN_DATE = "TrnDt";

  public static final String[] LOAN_REPAYMENT_VARIABLES_NAME = { ACCOUNT_ID, LOAN_REPAYMENT_TYPE, LOAN_BALANCE, CURRENCY_VALUE, CUSTOMER_FULL_NAME,
      "basicPayment", "interestPayment", "penaltyAmount", "feePayment", TOTAL_AMOUNT, TRANSACTION_TYPE, ACCOUNT_NUMBER, PAY_LOAN_AMOUNT,
      TRANSACTION_DESCRIPTION, ACCOUNT_CURRENCY, TRANSACTION_EXCG_RATE_CODE, ACCOUNT_BALANCE, ACCOUNT_NAME,  BLNC};
  public static final String LOAN_REPAYMENT_PROCESS_ID = "bpms_branch_banking_loan_repayment";

  //CHO
  public static final String CHO_BRANCH_NUMBER = "cho.branch.number";
}
