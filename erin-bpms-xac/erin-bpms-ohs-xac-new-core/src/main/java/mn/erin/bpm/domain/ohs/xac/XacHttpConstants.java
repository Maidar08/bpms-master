/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.domain.ohs.xac;

/**
 * @author Tamir
 */
public class XacHttpConstants
{
  public static final String ERR_MSG_RESPONSE_NULL = "Response body is null!";
  public static final String APPLICATION_JSON = "application/json";
  public static final String DATE_FORMATTER = "yyyyMMddHHmmss";

  public static final String REQUEST = "Request";
  public static final String RESPONSE = "Response";

  public static final String ERROR = "Error";
  public static final String ERROR_DESC = "ErrorDesc";
  public static final String ERROR_CODE = "ErrorCode";
  public static final String ERROR_MESSAGE = "ErrorMessage";


  public static final String SUCCESS = "SUCCESS";
  public static final String FAILURE = "FAILURE";

  public static final String RESPONSE_STATUS = "Status";
  public static final String STATUS = "status";

  public static final String YES = "Y";
  public static final String NO = "N";

  public static final String CID = "cid";
  public static final String NO_SESSION = "nosession";

  public static final String SEARCH_BY_CO_BORROWER = "1";
  public static final String SEARCH_BY_PRIMARY_BORROWER = "0";

  public static final String ARG_0 = "arg0";
  public static final String ARG_1 = "arg1";

  public static final String ARG_2 = "arg2";
  public static final String ARG_3 = "arg3";

  public static final String ARG_4 = "arg4";
  public static final String ARG_5 = "arg5";

  public static final String ARG_6 = "arg6";
  public static final String ARG_7 = "arg7";

  public static final String CONTINUE = "continue";
  public static final String OK = "OK";
  public static final String RETURN = "return";

  public static final String FIELD_1 = "field1";
  public static final String FIELD_20 = "field20";
  public static final String ITEM = "item";

  public static final String LOANCODE = "loancode";
  public static final String LOAN_TYPE_CODE = "loantypecode";
  public static final String LOAN_AMOUNT = "advamount";

  public static final String ADVAMOUNT = "advamount";
  public static final String BALANCE = "balance";

  public static final String CURRENCY_CODE = "currencycode";
  public static final String LOAN_CLASS = "loanclasscode";

  public static final String STATUS_CODE = "statuscode";

  public static final String MONGOL_BANK_EXPIRE_DATE = "expdate";
  public static final String MONGOL_BANK_STARTED_DATE = "starteddate";

  public static final String NO_LOAN = "NO_LOAN";
  public static final String NO_SALARY_INFO_CODE = "1";


  public static final String UNSATISFACTORY = "Муу";
  public static final String INSECURE = "Эргэлзээтэй";

  public static final String UNCERTAIN = "Хэвийн бус";
  public static final String ATTENTIONAL = "Анхаарал хандуулах";
  public static final String NORMAL = "Хэвийн";
  public static final String LOAN_NOT_FOUND = "LONOTFND";

  public static final String RESPONSE_NULL = "Service response is null!";
  public static final String NO_SESSION_FROM_MONGOL_BANK = "MONGOL BANK : Service no session!";

  public static final String RESULT_JSON_ARRAY_IS_EMPTY_FROM_MONGOL_BANK = "MONGOL BANK : Service result json array is empty!";
  public static final String RESULT_ITEM_NULL_FROM_MONGOL_BANK = "MONGOL BANK : Service result item is empty!";

  public static final String BORROWER_ID_IS_NULL_FROM_MONGOL_BANK = "MONGOL BANK : Borrower ID is null!";
  public static final String REQUIRED_PARAMETERS_NULL_CONFIRM_MONGOL_BANK = "Required parameters are null during confirm MONGOL BANK service!";

  public static final String CUSTOMER_CID_NULL = "Customer CID cannot be null!";
  public static final String PDF_FILE_AS_BASE_64_NULL_FROM_MONGOL_BANK = "MONGOL BANK: PDF file as base 64 is null!";

  public static final String ONLINE_LOAN_CONTRACT = "onlineLoanContract";
}
