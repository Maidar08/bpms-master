package mn.erin.domain.bpm.service;

import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import mn.erin.domain.bpm.model.branch_banking.AccountInfo;
import mn.erin.domain.bpm.model.branch_banking.CustomInvoice;
import mn.erin.domain.bpm.model.branch_banking.TaxInvoice;
import mn.erin.domain.bpm.model.branch_banking.transaction.CustomerTransaction;
import mn.erin.domain.bpm.model.branch_banking.transaction.ETransaction;

/**
 * @author Lkhagvadorj.A
 **/
public interface BranchBankingService
{
  /**
   * Finds transaction by user id and transaction date
   *
   * @param userId          Transaction user id
   * @param transactionDate Transaction date
   * @return found all transactions
   * @throws BpmServiceException when this service is not reachable or usable
   */
  Collection<CustomerTransaction> findByUserIdAndDate(String userId, String transactionDate, String instanceId) throws BpmServiceException;

  /**
   * Gets tin list when register option is selected
   *
   * @param registrationNumber Registration number
   * @param instanceId         Process instance id that used to log request and response to identify process request
   * @return returns Tax identification number list
   * @throws BpmServiceException when this service is not reachable or usable
   */
  Map<String, String> getTinList(String registrationNumber, String instanceId) throws BpmServiceException;

  /**
   * Get Tax Info list by type
   *
   * @param type               Tax info type
   * @param registrationNumber register number
   * @param instanceId         process instance id
   * @return Tax Info List
   * @throws BpmServiceException throws Bpm service exception
   */
  List<TaxInvoice> getTaxInfoList(String type, String registrationNumber, String instanceId) throws BpmServiceException;

  /**
   * Get Custom Info List
   *
   * @param instanceId process instance id
   * @return Custom info list
   * @throws BpmServiceException Bpm service exception
   */

  List<CustomInvoice> getCustomInfoList(String type, String searchValue, String instanceId) throws BpmServiceException;

  /**
   * Get session id that used to make transaction
   *
   * @param userName user name
   * @param password password
   * @return transaction session id
   */
  String getTransactionSessionId(String userName, String password) throws BpmServiceException;

  /**
   * make tax transaction
   *
   * @param transactionValues transaction values
   * @return transaction info
   */
  Map<String, String> makeTaxTransaction(Map<String, String> transactionValues, String instanceId) throws BpmServiceException;

  /**
   * Make custom transaction
   *
   * @param transactionValues transaction values
   * @return transaction info
   * @throws BpmServiceException when this service is not reachable or usable
   */
  Map<String, String> makeCustomTransaction(Map<String, Object> transactionValues, String instanceId) throws BpmServiceException;

  /**
   * Get account information by id
   *
   * @param instanceId instance id
   * @param accountId  account id
   * @return account information
   * @throws BpmServiceException when this service is not reachable or usable
   */
  Map<String, Object> getAccountInfo(String instanceId, String accountId, boolean hasAccountValidation) throws BpmServiceException, ParseException;

  /**
   * Get e-transactions by date interval and account id
   *
   * @param accountId account id
   * @param channelId channel id
   * @param channel   channel value
   * @param startDt   start date
   * @param endDt     end date
   * @return List of e-transactions
   * @throws BpmServiceException when this service is not reachable or usable
   */
  List<ETransaction> getTransactionEBankList(String accountId, String channelId, String channel, String startDt, String endDt, String instanceId)
      throws BpmServiceException;

  /**
   * Get group account info
   *
   * @param accountIdList - account id list
   * @param instanceId - process instance id
   * @return Account info list
   * @throws BpmServiceException when this service is not reachable or usable
   */
  Map<String, AccountInfo> getAccountNames(List<String> accountIdList, String instanceId) throws BpmServiceException;

  /**
   *
   *
   * @param accountId account id
   * @param branchId branch id
   * @param instanceId instance id
   * @return account reference value
   */
  Map<String, Object> getAccountReference(String accountId, String branchId, String instanceId) throws BpmServiceException;

  /**
   * Get user information from ussd service
   *
   * @param cif cif number
   * @param phone phone number
   * @param instanceId instance id
   * @param branch branch it's constantly empty
   * @return user information
   */
  Map<String, Object> getUserInfoFromUSSD(String cif, String phone, String branch, String instanceId) throws BpmServiceException, ParseException;



  /**
   * change user status. For example: block, unblock, change password etc.
   * @param mobile mobile number
   * @param status user status O - re-activate, B - unblock, P - change password, C - cancel.
   * @param instanceId instance id
   * @return response message
   * @throws BpmServiceException service exception when this service is not reachable or usable
   */
  Map<String, Object> userStatusChange(String mobile, String status, String instanceId) throws BpmServiceException;

  /**
   * no cash make account fee transaction
   *
   * @param transactionsParameters transaction values
   * @param amount amount
   * @param transactionSubType transaction sub type
   * @param instanceId instance id
   * @return transaction info
   * @throws BpmServiceException when this service is not reachable or usable
   */
  Map<String, String> makeAccountFeeTransactionTask(List<Map<String, Object>> transactionsParameters, String amount, String currency, String transactionSubType, String instanceId) throws BpmServiceException;

  /**
   * update or create ussd user
   *
   * @param updatedUser user information to update or create
   * @param languageId it's constantly MN now
   * @return result
   * @throws BpmServiceException service exception
   * @throws ParseException json or xml exception
   */
  Map<String, Object> updateUserUSSD(Map<String, Object> updatedUser, String languageId, String instanceId) throws BpmServiceException, ParseException;
  /**
   * no cash make account fee transaction
   *
   * @param transactionParameters transaction values
   * @param transactionType transaction type
   * @param transactionSubType transaction sub type
   * @param userTransactionCode user transaction code
   * @param remarks description
   * @param transactionParticulars transaction particulars
   * @param valueDate value date
   * @param dtlsOnResponse details on response
   * @param transactionRefNumber transaction reference number
   * @param instanceId instance id
   * @return transaction info
   * @throws BpmServiceException when this service is not reachable or usable
   */
  Map<String, String> makeNoCashAccountFeeTransactionTask(List<Map<String, Object>> transactionParameters, String transactionType, String transactionSubType, String userTransactionCode, String remarks, String transactionParticulars, String valueDate, String dtlsOnResponse, String transactionRefNumber,  String instanceId) throws BpmServiceException;

  /**
   * Send Otp through destination
   * @param channel email or phoneNumber type
   * @param destination phoneNumber
   * @param contentMessage OTP
   * @param instanceId instance id
   * @return description SUCCESS
   * @throws BpmServiceException when this service is not reachable or usable
   */
  boolean userOtpSend(String channel, String destination, String contentMessage, String instanceId) throws BpmServiceException, ParseException;

  /**
   * Get loan account information
   * @param accountNumber loan paying account number
   * @param instanceId instance id
   * @return loan account information
   * @throws BpmServiceException when this service is not reachable or usable
   */
  Map<String, Object> getLoanAccountInfo(String accountNumber, String instanceId) throws BpmServiceException;

  /**
   * add loan with unscheduled repayment transaction
   * @param instanceId instance id
   * @param variables request parameters
   * @return getting values from response Transaction id with scheduled, Transaction id with unscheduled, Status, Appnum
   * @throws BpmServiceException when this service is not reachable or usable
   */
  Map<String, Object> addUnschLoanRepayment(String instanceId, JSONObject variables) throws BpmServiceException;

  /** add loan with scheduled repayment transaction
   * @param variables Request parameters
   * @return Transaction ID and date in map
   * @throws BpmServiceException when this service is not reachable or usable
   */
  Map<String, Object> makeScheduledLoanPayment(String instanceId, JSONObject variables) throws BpmServiceException;

  /**
   * Set Pay Off Proc
   *
   * @param variables Request Parameter
   * @return Transaction ID and Date in a Map
   * @throws BpmServiceException when this service is not reachable or usable
   */
  Map<String, String> setPayOffProc(JSONObject variables) throws BpmServiceException;

  /**
   * Make user endorse on ussd service
   *
   * @param id id number
   * @param instanceId instance id
   * @param branch branch it's constantly empty
   * @return status of request
   */
  boolean makeUserEndorse(String id, String branch, String instanceId) throws BpmServiceException, ParseException;

  /**
   * Make user cancel on ussd service
   *
   * @param id id number
   * @param instanceId instance id
   * @param branch branch it's constantly empty
   * @return status of request
   */
  boolean makeUserCancel(String id, String branch, String instanceId) throws BpmServiceException, ParseException;
}
