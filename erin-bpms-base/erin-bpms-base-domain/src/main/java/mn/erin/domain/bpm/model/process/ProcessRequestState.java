/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.model.process;

/**
 * @author EBazarragchaa
 */
public enum ProcessRequestState
{
  NEW, STARTED, CONFIRMED, PROCESSING, REJECTED, ORG_REJECTED, CUST_REJECTED, SCORING_REJECTED, AMOUNT_REJECTED,
  COMPLETED, DISBURSED, RETURNED,  MB_SESSION_EXPIRED, DELETED, SYSTEM_FAILED, SERVER_FAILED, DISBURSE_FAILED, OVERDUE,
  TRANSACTION_FAILED, LOAN_ACCOUNT_FAILED, FILE_UPLOAD_FAILED, AMOUNT_BLOCKED, AMOUNT_BLOCKED_FAILED, CIB_FAILED;

  public static String fromEnumToString(ProcessRequestState processRequestState)
  {
    switch (processRequestState)
    {
    case NEW:
      return "NEW";
    case STARTED:
      return "STARTED";
    case REJECTED:
      return "REJECTED";
    case CUST_REJECTED:
      return "CUST_REJECTED";
    case ORG_REJECTED:
      return "ORG_REJECTED";
    case SCORING_REJECTED:
      return "SCORING_REJECTED";
    case AMOUNT_REJECTED:
      return "AMOUNT_REJECTED";
    case COMPLETED:
      return "COMPLETED";
    case CONFIRMED:
      return "CONFIRMED";
    case PROCESSING:
      return "PROCESSING";
    case DISBURSED:
      return "DISBURSED";
    case RETURNED:
      return "RETURNED";
    case DELETED:
      return "DELETED";
    case MB_SESSION_EXPIRED:
      return "MB_SESSION_EXPIRED";
    case SYSTEM_FAILED:
      return "SYSTEM_FAILED";
    case SERVER_FAILED:
      return "SERVER_FAILED";
    case DISBURSE_FAILED:
      return "DISBURSE_FAILED";
    case OVERDUE:
      return "OVERDUE";
    case TRANSACTION_FAILED:
      return "TRANSACTION_FAILED";
    case LOAN_ACCOUNT_FAILED:
      return "LOAN_ACCOUNT_FAILED";
    case AMOUNT_BLOCKED:
      return "AMOUNT_BLOCKED";
    case AMOUNT_BLOCKED_FAILED:
      return "AMOUNT_BLOCKED_FAILED";
    case FILE_UPLOAD_FAILED:
      return "FILE_UPLOAD_FAILED";
    case CIB_FAILED:
      return "CIB_FAILED";
    default:
      throw new IllegalArgumentException("Incompatible State!");
    }
  }

  public static ProcessRequestState fromStringToEnum(String processRequestStateStr)
  {
    switch (processRequestStateStr)
    {
    case "NEW":
      return ProcessRequestState.NEW;
    case "STARTED":
      return ProcessRequestState.STARTED;
    case "REJECTED":
      return ProcessRequestState.REJECTED;
    case "CUST_REJECTED":
      return ProcessRequestState.CUST_REJECTED;
    case "ORG_REJECTED":
      return ProcessRequestState.ORG_REJECTED;
    case "COMPLETED":
      return ProcessRequestState.COMPLETED;
    case "SCORING_REJECTED":
      return SCORING_REJECTED;
    case "AMOUNT_REJECTED":
      return AMOUNT_REJECTED;
    case "CONFIRMED":
      return ProcessRequestState.CONFIRMED;
    case "PROCESSING":
      return PROCESSING;
    case "DISBURSED":
      return ProcessRequestState.DISBURSED;
    case "RETURNED":
      return ProcessRequestState.RETURNED;
    case "MB_SESSION_EXPIRED":
      return ProcessRequestState.MB_SESSION_EXPIRED;
    case "DELETED":
      return ProcessRequestState.DELETED;
    case "SERVER_FAILED":
      return ProcessRequestState.SERVER_FAILED;
    case "SYSTEM_FAILED":
      return ProcessRequestState.SYSTEM_FAILED;
    case "DISBURSE_FAILED":
      return ProcessRequestState.DISBURSE_FAILED;
    case "OVERDUE":
      return ProcessRequestState.OVERDUE;
    case "TRANSACTION_FAILED":
      return ProcessRequestState.TRANSACTION_FAILED;
    case "LOAN_ACCOUNT_FAILED":
      return ProcessRequestState.LOAN_ACCOUNT_FAILED;
    case "FILE_UPLOAD_FAILED":
      return ProcessRequestState.FILE_UPLOAD_FAILED;
    case "AMOUNT_BLOCKED":
      return  ProcessRequestState.AMOUNT_BLOCKED;
    case "AMOUNT_BLOCKED_FAILED":
      return ProcessRequestState.AMOUNT_BLOCKED_FAILED;
    case "CIB_FAILED":
      return ProcessRequestState.CIB_FAILED;
    default:
      throw new IllegalArgumentException("Incompatible State!");
    }
  }
}
