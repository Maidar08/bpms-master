package mn.erin.domain.bpm.service;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import mn.erin.domain.bpm.model.loan.Loan;


/**
 * Represents direct online core banking system related functions.
 *
 * @author sukhbat
 */
public interface DirectOnlineCoreBankingService
{
  /**
   * Gets all organization if by cif number and dan registration number
   *
   * @param input dan registration number, branchNumber, phoneNumber, processTypeId
   * @return min interest rate , max interest rate
   * @throws BpmServiceException when this service is not reachable or usable
   */
  Map<String, Object> findOrganizationInfo(Map<String, String> input) throws BpmServiceException;

  /**
   * @param input branchId accountId phoneNumber processTypeId
   * @return account loan information
   * @throws BpmServiceException when this service is not reachable or usable
   */
  Map<String, Object> getAccountLoanInfo(Map<String, String> input) throws BpmServiceException;

  /**
   * preserve grant loan accounts info
   * @param input accountNumber phoneNumber processRequestId
   * @return returns loan info dusbursed date, disbursed amount etc
   * @throws BpmServiceException when this service is not reachable or usable
   */
  Loan getDbsrList(Map<String, String> input) throws BpmServiceException;

  /**
   * creates loan disbursement
   * @param requestBody loan disbursement requestBody parameters
   * returns boolean. checks loan disbursement successfully created. if yes true, if no returns false.
   */
  Map<String, Object> createLoanDisbursement(Map<String, Object> requestBody);

  /**
   * Loan disbursement service. It's called when there is no previous loan payment
   * @param requestBody
   * @return
   */

  Map<String, Object> createLoanDisbursementCharge(Map<String, Object> requestBody);

  /**
   * creates loan disbursement transaction
   * @param requestBody add transaction requestBody parameters
   * @return boolean. checks loan disbursement successfully created. if yes true, if no returns false.
   */
  Map<String, Object> addTransaction(Map<String, Object> requestBody);

  /**
   *
   * @param registerNumber customer register number
   * @param branchNumber branch number
   * @return customer loan list from Mongol Bank
   */
  List<Loan> getCustomerLoanListFromMB(String registerNumber, String branchNumber) throws BpmServiceException;

  /**
   * Set Pay Off Proc used to loan closure
   *
   * @param variables Request Parameter
   * @return Transaction ID and Date in a Map
   * @throws BpmServiceException when this service is not reachable or usable
   */
  Map<String, String> setPayOffProc(JSONObject variables) throws BpmServiceException;

  /**
   * Get Repayment Schedule
   * @param input accountNumber project phoneNumber processTypeId
   * @return returns repayment schedule in a map
   * @throws BpmServiceException when this service is not reachable or usable
   */

  Map<String, Object> getRepaymentSchedule(Map<String, String> input) throws BpmServiceException;

  /**
   * Get Acc Lien
   * @param input accountNumber, moduleType, phoneNumber, processTypeId
   * @return returns Acc Lien in a map
   * @throws BpmServiceException when this service is not reachable or usable
   */
  Map<String, Object> getAccLien(Map<String, String> input) throws BpmServiceException;

  /**
   * Add Acc Lien
   * @param requestBody addAccLien requestBody parameters
   * @return returns addAccLien in a map
   * @throws BpmServiceException when this service is not reachable or usable
   */
  Map<String, Object> addAccLien(Map<String, Object> requestBody) throws BpmServiceException;

  /**
   * Modify Acc Lien
   * @param requestBody modifyAccLien requestBody parameters
   * @return returns modifyAccLien in a map
   * @throws BpmServiceException when this service is not reachable or usable
   */
  Map<String, Object> modifyAccLien(Map<String, Object> requestBody) throws BpmServiceException;

}
