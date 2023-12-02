package mn.erin.domain.bpm.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import mn.erin.domain.bpm.model.account.Account;
import mn.erin.domain.bpm.model.account.UDField;
import mn.erin.domain.bpm.model.account.XacAccount;
import mn.erin.domain.bpm.model.collateral.Collateral;
import mn.erin.domain.bpm.model.customer.Customer;

/**
 * Represents core banking system related functions.
 *
 * @author EBazarragchaa
 */
public interface CoreBankingService
{
  /**
   * Returns the customer to a given person id.
   *
   * @param personId the person id, not blank (register number etc.)
   * @return the customer or null
   * @throws BpmServiceException when this service is not reachable or usable
   */
  Customer findCustomerByPersonId(String personId) throws BpmServiceException;

  /**
   * Returns the customer to a given customer number.
   *
   * @param cifNumber the customer number, not blank (CIF number etc.)
   * @return the customer or null
   * @throws BpmServiceException when this service is not reachable or usable
   */
  Customer findCustomerByCifNumber(String cifNumber) throws BpmServiceException;

  /**
   * Checks if the customer to a given person id is risky.
   *
   * @param personId the person id, not blank
   * @return true if the customer risky, otherwise false
   * @throws BpmServiceException when this service is not reachable or usable
   */
  boolean checkRiskyCustomer(String personId) throws BpmServiceException;

  Account createCustomerAccount(String cifNumber, String accountType);

  /**
   * Returns Xac customer resource information
   *
   * @param customerCif
   * @return String resource (account balance total)
   */
  String getCustomerResource(String customerCif) throws BpmServiceException;

  /**
   * Returns customer loan period information
   *
   * @param customerCif
   * @return String loan history and loan period
   */
  int getCustomerLoanPeriodInformation(String customerCif) throws BpmServiceException;

  /**
   * Returns UD Fields information related to account creation
   *
   * @param productCode
   * @return Map of account creation information
   */
  Map<String, UDField> getUDFields(String productCode) throws BpmServiceException;

  /**
   * Returns UD Fields By Function
   *
   * @param bodyFunction function in body request parameter
   * @return Map of account creation information
   */
  Map<String, UDField> getUDFieldsByFunction(String bodyFunction) throws BpmServiceException;

  /**
   * Creates loan account
   *
   * @param productCode
   * @param accountInformation Map of account creation information
   * @return Map of account creation information
   */
  int createLoanAccount(String productCode, Map<String, String> accountInformation, List<Map<String, String>> coBorrowers) throws BpmServiceException;

  /**
   * Creates collateral loan account
   *
   * @param productCode
   * @param accountInformation Map of account creation information
   * @return Map of account creation information
   */
  int createCollateralLoanAccount(String productCode, Map<String, String> accountInformation, List<Map<String, String>> coBorrowers, Map<String, Map<String, Object>> collateralMap) throws BpmServiceException;

  /**
   * Retrieves a list of account based on registeration number and customer ID
   *
   * @param regNumber
   * @param customerID
   * @return Map of account creation information
   */

  List<XacAccount> getAccountsListFC(String regNumber, String customerID) throws BpmServiceException;

  /**
   * Gets all collateral by customer cif number.
   *
   * @param cifNumber the unique customer number.
   * @return all collateral related cif.
   * @throws BpmServiceException when this service is not reachable or usable
   */
  List<Collateral> getCollateralsByCifNumber(String cifNumber) throws BpmServiceException;

  /**
   * Gets all collateral by customer cif number.
   *
   * @param branch the unique customer number.
   * @param cif customer id
   * @param collateralCreationInfo map of required information to create a collateral.
   * @param udFields map of ud fields used in creation of colalteral.
   * @return all collateral related cif.
   * @throws BpmServiceException when this service is not reachable or usable
   */
  boolean createCollateral(String branch, String cif, Map<String, String> collateralCreationInfo, Map<String, Serializable> udFields) throws BpmServiceException;
}
