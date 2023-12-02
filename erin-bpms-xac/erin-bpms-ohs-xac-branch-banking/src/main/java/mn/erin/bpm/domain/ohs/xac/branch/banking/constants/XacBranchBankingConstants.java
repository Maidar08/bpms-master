package mn.erin.bpm.domain.ohs.xac.branch.banking.constants;

/**
 * @author Bilguunbor
 */
public class XacBranchBankingConstants
{
  private XacBranchBankingConstants()
  {

  }
  /* BRANCH BANKING SERVICE CONSTANTS */

  public static final String BB_ENDPOINT = "branch.banking.endpoint";
  public static final String BB_HEADER_SOURCE = "branch.banking.header.source";

  public static final String BB_PUBLISHER_FUNCTION = "publisher.function";
  public static final String BB_PUBLISHER_USER_ID = "publisher.userId";
  public static final String BB_PUBLISHER_PASSWORD = "publisher.userPassword";

  public static final String BB_HEADER_REQUEST_TYPE = "branch.banking.header.requestType";
  public static final String BB_HEADER_GET_TIN_LIST = "branch.banking.header.getBillingTins";
  public static final String BB_HEADER_GET_INVOICE_LIST = "branch.banking.getInvoice.GETTAXINVOICEINFO";
  public static final String BB_HEADER_GET_ASSET_INVOICE = "branch.banking.getInvoice.GETINVOICENOBYASSET";
  public static final String BB_HEADER_GET_CUSTOM_INFO = "branch.banking.header.getCustomInfo";

  public static final String BB_URL_FUNCTION_GET_TAX_INFO = "branch.banking.getTaxInfo";
  public static final String BB_URL_FUNCTION_GET_CUSTOM_INFO = "branch.banking.getCustomInfo";

  public static final String BB_URL_FUNCTION_SET_CUSTOM_INFO = "branch.banking.setCustomInfo";
  public static final String BB_SET_CUSTOM_PAYMENT = "branch.banking.SETINFO";

  public static final String BB_FISSO_LOGIN = "branch.banking.Fisso.login";
  public static final String BB_SET_TAX_PAYMENT = "branch.banking.SETTAXPAYMENT";
  public static final String BB_GET_ACCOUNT_INFO = "branch.banking.getAccountInfo";
  public static final String BB_GET_ACCOUNT_REFERENCE = "branch.banking.getAccountReference";

  public static final String BB_URL_FUNCTION_GET_TRANSACTION_LIST = "branch.banking.GetcTransactionList";
  public static final String BB_GET_TRANSACTION_LIST = "branch.banking.GetTransactionList";


  public static final String BB_ADD_CASH_CREDIT = "branch.banking.AddCashCredit";
  public static final String BB_ADD_TRANSACTION = "branch.banking.AddTransaction";

  public static final String BB_TRANSACTION_E_BANK_LIST = "branch.banking.GetTransactionEBankList";

  //Salary Package
  public static final String BB_GET_C_ACCOUNT_NAMES = "branch.banking.getcAccountNames";

  //USSD
  public static final String BB_USSD_SEARCH_USER = "branch.banking.ussdSearchUser";
  public static final String BB_USSD_SERVICE = "branch.banking.ussdService";
  public static final String BB_USER_STATUS_CHANGE = "branch.banking.ussdStatChange";
  public static final String BB_USER_OTP_SEND = "branch.banking.userOtpSend";
  public static final String BB_USSD_USER_ENDORSE = "branch.banking.ussdUserEndorse";
  public static final String BB_USSD_USER_CANCEL = "branch.banking.ussdUserCancel";

  //loan repay
  public static final String BB_LOAN_POS_INFO = "branch.banking.LoanOvduPosInfo";
  public static final String BB_ADD_LOAN_REPAYMENT = "branch.banking.AddLoanRepayment";
  public static final String BB_LOAN_SCHEDULED_PAYMENT = "branch.banking.addLoanSchPayment";
  public static final String BB_SET_PAY_OFF_PROC = "branch.banking.SetPayOffProc";
  public static final String BB_SET_PAY_OFF_PROC_URL = "branch.banking.SetPayOffProc";


  // BIPublisher constants are here
  public static final String BB_PUBLISHER_ENDPOINT = "publisher.endpoint";
  public static final String PUBLISHER_SOAP_ACTION_DOWNLOAD_CONTRACT = "publisher.soap.action.download.contract";
  public static final String BB_PUBLISHER_XBRPAYT = "branch.banking.publisher.xbrpayt";
  public static final String BB_PUBLISHER_XBRCUP = "branch.banking.publisher.xbrcup";
  public static final String BB_PUBLISHER_XBRINTR = "branch.banking.publisher.xbrintr";
  public static final String BB_PUBLISHER_XBROUTR = "branch.banking.publisher.xbroutr";
  public static final String BB_PUBLISHER_XBRMISC = "branch.banking.publisher.xbrmisc";
  public static final String BB_PUBLISHER_XBROFFB = "branch.banking.publisher.xbroffb";
  public static final String BB_PUBLISHER_XBRFXTX = "branch.banking.publisher.xbrfxtx";
  public static final String BB_PUBLISHER_XBROZTR = "branch.banking.publisher.xbroztr";
  public static final String BB_PUBLISHER_XBRNETS = "branch.banking.publisher.xbrnets";
  public static final String BB_PUBLISHER_XBRGALI = "branch.banking.publisher.xbrgali";
  public static final String BB_PUBLISHER_TRANSACTION_SLIPS = "branch.banking.publisher.finacle.slips";
  public static final String BB_PUBLISHER_TRANSACTION_CONTRACTS = "branch.banking.publisher.finacle.contracts";
  public static final String BB_PUBLISHER_XBR13IS = "branch.banking.publisher.xbr13is";
  public static final String BB_PUBLISHER_XBR12SB = "branch.banking.publisher.xbr12sb";
  public static final String BB_PUBLISHER_XBR14SN = "branch.banking.publisher.xbr14sn";
  public static final String BB_PUBLISHER_XBRCHM = "branch.banking.publisher.xbrchm";
  public static final String BB_PUBLISHER_XBR19MC = "branch.banking.publisher.xbr19mc";
  public static final String BB_PUBLISHER_XBR7HAA = "branch.banking.publisher.xbr7haa";
  public static final String BB_PUBLISHER_XBR9UAA = "branch.banking.publisher.xbr9uaa";
  public static final String BB_PUBLISHER_XBR10HA = "branch.banking.publisher.xbr10ha";
  public static final String BB_PUBLISHER_XBRACR = "branch.banking.publisher.xbracr";
}
