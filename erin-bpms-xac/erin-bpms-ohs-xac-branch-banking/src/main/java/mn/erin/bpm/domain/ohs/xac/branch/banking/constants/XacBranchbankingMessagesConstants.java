package mn.erin.bpm.domain.ohs.xac.branch.banking.constants;

/**
 * @author Bilguunbor
 */
public class XacBranchbankingMessagesConstants
{
  public static final String REGISTRATION_NUMBER_IS_NULL_ERROR_CODE = "BBS001";
  public static final String REGISTRATION_NUMBER_IS_NULL_ERROR_MESSAGE = "REGISTRATION NUMBER MUST NOT BE NULL!";

  public static final String RESPONSE_BODY_IS_NULL_ERROR_CODE = "BBS002";
  public static final String RESPONSE_BODY_IS_NULL_ERROR_MESSAGE = "RESPONSE BODY IS NULL!";

  public static final String RESPONSE_IS_NULL_ERROR_CODE = "BBS003";
  public static final String RESPONSE_IS_NULL_ERROR_MESSAGE = "SERVICE RESPONSE IS NULL!";

  public static final String SEARCH_TYPE_IS_NULL_ERROR_CODE = "BBS004";
  public static final String SEARCH_TYPE_IS_NULL_ERROR_MESSAGE = "SEARCH TYPE IS NULL!";

  public static final String INFO_FIELD_IS_NULL_ERROR_CODE = "BBS005";
  public static final String INFO_FIELD_IS_NULL_ERROR_MESSAGE = "INFO FIELD IS NULL!";

  public static final String USER_NAME_NULL_CODE = "BBS006";
  public static final String USER_NAME_NULL_MESSAGE = "User name is null!";

  public static final String PASSWORD_NULL_CODE = "BBS007";
  public static final String PASSWORD_NULL_MESSAGE = "User password is null!";

  public static final String EXT_SESSION_NULL_CODE = "BBS008";
  public static final String EXT_SESSION_NULL_MESSAGE = "External session Info is null!";

  public static final String TRANSACTION_SESSION_ID_NULL_CODE = "BBS009";
  public static final String TRANSACTION_SESSION_ID_NULL_MESSAGE = "Transaction session id is null!";

  public static final String XAC_SERVICE_PARAMETER_NULL_CODE = "BBS010";
  public static final String XAC_SERVICE_PARAMETER_NULL_MESSAGE = "Xac service parameter is null!";

  public static final String ACCOUNT_ID_NULL_CODE = "BBS011";
  public static final String ACCOUNT_ID_NULL_MESSAGE = "Account ID is null";

  public static final String INVOICE_PAYMENT_LIST_EMPTY_CODE = "BBS012";
  public static final String INVOICE_PAYMENT_LIST_INVOICE_MESSAGE = "INVOICE HAS NO PAYMENT LIST";

  public static final String INVALID_PAYMENT_LIST_FORMAT_CODE = "BBS013";
  public static final String INVALID_PAYMENT_LIST_FORMAT_MESSAGE = "Invalid Payment List format!";

  public static final String INVALID_BANK_ACC_LIST_CODE = "BBS014";
  public static final String INVALID_BANK_ACC_LIST_MESSAGE = "Invalid bank acc list format!";

  public static final String XAC_RESPONSE_NOT_JSON_ERROR_CODE = "BBS100";
  public static final String XAC_RESPONSE_NOT_JSON_ERROR_MESSAGE = "Xac service response is not valid type. Response is supposed to be type of JSON!";

  public static final String CHANNEL_ID_NULL_CODE = "BBS015";
  public static final String CHANNEL_ID_NULL_MESSAGE = "CHANNEL ID IS NULL!";

  public static final String START_DT_NULL_CODE = "BBS016";
  public static final String START_DT_NULL_MESSAGE = "START DATE IS NULL!";

  public static final String END_DT_NULL_CODE = "BBS017";
  public static final String END_DT_NULL_MESSAGE = "END DATE IS NULL";

  public static final String TRANSACTION_PARAMETERS_NULL_CODE = "BBS018";
  public static final String TRANSACTION_PARAMETERS_NULL_MESSAGE = "Transaction PARAMETERS IS NULL";

  public static final String ACCOUNT_ID_LIST_NULL_CODE = "BBS019";
  public static final String ACCOUNT_ID_LIST_NULL_MESSAGE = "Account id list is null";

  public static final String BOTH_OF_PHONE_AND_CIF_NULL_CODE = "BBS020";
  public static final String BOTH_OF_PHONE_AND_CIF_NULL_MESSAGE = "BOTH OF PHONE AND CIF ARE NULL";

  public static final String DESTINATION_IS_NULL_CODE = "BBS021";
  public static final String DESTINATION_IS_NULL_MESSAGE = "destination is null!";

  public static final String CONTENT_AS_BASE64_NULL_CODE = "BBS022";
  public static final String CONTENT_AS_BASE64_NULL_MESSAGE = "EXCEL AS BASE64 CONTENT IS NULL!";

  public static final String CANNOT_CREATE_FILE_INPUT_STREAM_CODE = "BBS023";
  public static final String CANNOT_CREATE_FILE_INPUT_STREAM_MESSAGE = "CANNOT CREATE FILE INPUT STREAM FROM GIVEN BYTE ARRAY!";

  public static final String STATUS_IS_NULL_ERROR_CODE = "BBS024";
  public static final String STATUS_IS_NULL_ERROR_MESSAGE = "status is null!";

  public static final String MOBILE_NUMBER_IS_NULL_CODE = "BBS025";
  public static final String MOBILE_NUMBER_IS_NULL_MESSAGE = "Mobile number is required!";

  public static final String USER_STATUS_IS_NULL_CODE = "BBS026";
  public static final String USER_STATUS_IS_NULL_MESSAGE = "User status is required!";

  public static final String ACCOUNT_STATUS_IS_CLOSED_ERROR_CODE = "BBS027";
  public static final String ACCOUNT_STATUS_IS_CLOSED_ERROR_MESSAGE = "Account status is closed!";

  public static final String USER_INFO_NOT_FOUND_CODE = "BBS028";
  public static final String USER_INFO_NOT_FOUND_MESSAGE = "User Information not found!";

  public static final String USER_NOT_REGISTERED_CODE = "BBS029";
  public static final String USER_NOT_REGISTERED_MESSAGE = "Unregistered user!";

  public static final String ACCOUNT_STATUS_IS_FROZEN_ERROR_CODE = "BBS030";
  public static final String ACCOUNT_STATUS_IS_FROZEN_ERROR_MESSAGE = "This account is frozen!";

  public static final String ACCOUNT_STATUS_IS_DORMANCY_ERROR_CODE = "BBS031";
  public static final String ACCOUNT_STATUS_IS_DORMANCY_ERROR_MESSAGE = "This account is non active!";

  public static final String ACCOUNT_STATUS_IS_NO_DEBIT_ERROR_CODE = "BBS032";
  public static final String ACCOUNT_STATUS_IS_NO_DEBIT_ERROR_MESSAGE = "This account is no debit!";

  public static final String ACCOUNT_IS_UNAUTHORIZED_FOR_EXPENSE_ERROR_CODE = "BBS033";
  public static final String ACCOUNT_IS_UNAUTHORIZED_FOR_EXPENSE_ERROR_MESSAGE = "This account is unauthorized for expense.";

  public static final String ACCOUNT_BALANCE_ERROR_CODE = "BBS034";
  public static final String ACCOUNT_BALANCE_ERROR_MESSAGE = "The account balance is not enough to repay the loan. Check your account balance and loan repayment amount!";

  public static final String PAY_LOAN_AMOUNT_ERROR_CODE = "BBS035";
  public static final String PAY_LOAN_AMOUNT_ERROR_MESSAGE = "The amount is wrong! Possible to pay with the amount in the loan payment section!";
}
