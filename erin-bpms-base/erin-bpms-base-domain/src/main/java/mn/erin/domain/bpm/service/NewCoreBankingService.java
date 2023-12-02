package mn.erin.domain.bpm.service;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import mn.erin.domain.bpm.model.account.UDField;
import mn.erin.domain.bpm.model.account.XacAccount;
import mn.erin.domain.bpm.model.collateral.Collateral;
import mn.erin.domain.bpm.model.customer.Customer;
import mn.erin.domain.bpm.model.loan_contract.LinkageCollateralInfo;

/**
 * @author Tamir
 */
public interface NewCoreBankingService
{
  /**
   * Returns the customer to a given customer number.
   *
   * @param input cifNumber, phoneNumber, processRequestId
   * @return the customer or null
   * @throws BpmServiceException when this service is not reachable or usable
   */
  Map<String, Object> findCustomerByCifNumber(Map<String, String> input) throws BpmServiceException;

  /**
   * Returns the customer to a given person id.
   *
   * @param personId the person id, not blank (register number etc.)
   * @return the customer or null
   * @throws BpmServiceException when this service is not reachable or usable
   */
  Customer findCustomerByPersonIdAndType(String personId, String customerType) throws BpmServiceException;

  /**
   * Checks if the customer to a given person id is risky.
   *
   * @param personId the person id, not blank
   * @return true if the customer risky, otherwise false
   * @throws BpmServiceException when this service is not reachable or usable
   */
  boolean checkRiskyCustomer(String personId) throws BpmServiceException;

  /**
   * Gets all collateral by customer cif number.
   *
   * @param cifNumber the unique customer number.
   * @return all collateral related cif.
   * @throws BpmServiceException when this service is not reachable or usable
   */
  List<Collateral> getCollateralsByCifNumber(String cifNumber) throws BpmServiceException;

  /**
   * Gets collateral codes by collateral type
   *
   * @param colType the collateral type
   * @return list of collateral codes
   * @throws BpmServiceException when this service is not reachable or usable
   */
  List<String> getCollateralCode(String colType) throws BpmServiceException;

  /**
   * Created immovable property collateral.
   *
   * @param genericInfo    genericInfo map
   * @param collateralInfo collateralInfo map
   * @param inspectionInfo inspection info map
   * @param ownershipInfo  ownershipInfo map
   * @return created collateral ID.
   * @throws BpmServiceException when this service is not reachable or usable
   */
  String createImmovableCollateral(Map<String, Object> genericInfo, Map<String, Object> collateralInfo,
      Map<String, Object> inspectionInfo, Map<String, Object> ownershipInfo) throws BpmServiceException;

  /**
   * Created machinery collateral.
   *
   * @param genericInfo    genericInfo map
   * @param collateralInfo collateralInfo map
   * @param inspectionInfo inspection info map
   * @param ownershipInfo  ownershipInfo map
   * @return created collateral ID.
   * @throws BpmServiceException when this service is not reachable or usable
   */
  String createMachineryCollateral(Map<String, Object> genericInfo, Map<String, Object> collateralInfo,
      Map<String, Object> inspectionInfo, Map<String, Object> ownershipInfo) throws BpmServiceException;

  /**
   * Gets collateral reference codes depending on types.
   *
   * @param types collateral reference types.
   * @return found reference codes related types.
   */
  Map<String, List<String>> getCollReferenceCodes(List<String> types) throws BpmServiceException;

  /**
   * Retrieves a list of account based on registeration number and customer ID
   *
   * @param input  customer register number, customer cif, phoneNumber, processTypeId
   * @return Map of account creation information
   */

  List<XacAccount> getAccountsList(Map<String, String> input) throws BpmServiceException;

  /**
   * Returns UD Fields information related to account creation
   *
   * @param input loan product code, phoneNumber, processTypeId
   * @return Map of account creation information
   */
  Map<String, UDField> getUDFields(Map<String, String> input) throws BpmServiceException;

  /**
   * Creates loan account with collateral, coBorrowers info.
   *
   * @param genericInfos    Account creation information
   * @param additionalInfos Account additional infos
   * @param coBorrowers     Linkage co-borrower infos
   * @return created account number
   * @throws BpmServiceException when this service is not reachable or usable
   */
  String createLoanAccount(Map<String, Object> genericInfos, Map<String, Object> additionalInfos, List<Map<String, Object>> coBorrowers)
      throws BpmServiceException;

  /**
   * Create vehicle collateral
   *
   * @param collateralGenericInfo generic information of collateral
   * @param vehicleInfo           vehicle collateral information
   * @param inspectionInfo        information of collateral inspection
   * @param ownershipInfo         information of collateral information
   * @return return created collateral id
   * @throws BpmServiceException throws service exceptions
   */
  String createVehicleCollateral(Map<String, Object> collateralGenericInfo, Map<String, Object> vehicleInfo, Map<String, Object> inspectionInfo,
      Map<String, Object> ownershipInfo) throws BpmServiceException;

  /**
   * Create other collateral
   *
   * @param collateralGenInfo   generic info of collateral
   * @param otherCollateralInfo other collateral's info
   * @param inspectionInfo      inspection info
   * @param ownershipInfo       ownership info
   * @return created collateral code
   */
  String createOtherCollateral(Map<String, Object> collateralGenInfo, Map<String, Object> otherCollateralInfo, Map<String, Object> inspectionInfo,
      Map<String, Object> ownershipInfo) throws BpmServiceException;

  /**
   * Get machinery collateral info by collateral id
   *
   * @param collateralId collateral id
   * @return Map of collateral info
   */
  Map<String, Object> getMachineryCollateral(String collateralId) throws BpmServiceException;

  /**
   * Get other collateral info by collateral id
   *
   * @param collateralId collateral id
   * @return Map of collateral info
   */

  Map<String, Object> getOtherCollateral(String collateralId) throws BpmServiceException;

  /**
   * Get machinery collateral info by collateral id
   *
   * @param collateralId collateral id
   * @return Map of collateral info
   */
  Map<String, Object> getImmovablePropertyCollateral(String collateralId) throws BpmServiceException;

  /**
   * Modify created machinery collateral
   *
   * @param collateralId   collateral id
   * @param genericInfo    generic collateral info
   * @param collateralInfo machinery collateral info
   * @param inspectionInfo inspection info
   * @param ownershipInfo  ownership info
   * @return modified collateral id
   */
  String modifyMachineryCollateral(String collateralId, Map<String, Object> genericInfo, Map<String, Object> collateralInfo,
      Map<String, Object> inspectionInfo, Map<String, Object> ownershipInfo) throws BpmServiceException;

  /**
   * Modify created immovable property collateral
   *
   * @param collateralId   collateral id
   * @param genericInfo    generic collateral info
   * @param collateralInfo immovable property collateral info
   * @param inspectionInfo inspection info
   * @param ownershipInfo  ownership info
   * @return modified collateral id
   */
  String modifyImmovablePropCollateral(String collateralId, Map<String, Object> genericInfo, Map<String, Object> collateralInfo,
      Map<String, Object> inspectionInfo, Map<String, Object> ownershipInfo) throws BpmServiceException;

  /**
   * Get vehicle collateral info by collateral id
   *
   * @param input collateral id
   * @return Map of collateral info
   */
  Map<String, Object> getVehicleCollInfo(String input) throws BpmServiceException;

  /**
   * Modify created vehicle collateral id
   *
   * @param collateralId collateral id
   * @param genericInfo collateral generic information
   * @param collateralInfo collateral information
   * @param inspectionInfo inspection information
   * @param ownershipInfo ownership information
   * @return id
   * @throws BpmServiceException when this service is not reachable or usable
   */
  String modifyVehicleCollateral(String collateralId, Map<String, Object> genericInfo, Map<String, Object> collateralInfo,
      Map<String, Object> inspectionInfo, Map<String, Object> ownershipInfo) throws BpmServiceException;

  /**
   * Modify created other collateral
   *
   * @param collateralId   collateral id
   * @param genericInfo    generic collateral info
   * @param collateralInfo other collateral info
   * @param inspectionInfo inspection info
   * @param ownershipInfo  ownership info
   * @return modified collateral id
   */
  String modifyOtherCollateral(String collateralId, Map<String, Object> genericInfo, Map<String, Object> collateralInfo, Map<String, Object> inspectionInfo,
      Map<String, Object> ownershipInfo) throws BpmServiceException;

  /**
   * Links collaterals to loan account.
   *
   * @param accountNumber     account number
   * @param linkageType       collateral linkage type
   * @param collateralDetails detail infos.
   * @return linked response.
   */
  boolean linkCollaterals(String accountNumber, String linkageType, Map<String, Object> collateralDetails) throws BpmServiceException;

  /**
   * Gets account info with account number
   *
   * @param input account number, phoneNumber, processTypeId
   * @return Map of account info
   */
  Map<String, Object> getLoanAccountInfo(Map<String, String> input) throws BpmServiceException;

  /**
   *  Gets inquire collateral details.
   *
   * @param entityId entity id
   * @param entityType entity type
   * @return List of linkage collateral information
   * @throws BpmServiceException when this service is not reachable or usable
   */
  List<LinkageCollateralInfo> getInquireCollateralDetails(String entityId, String entityType) throws BpmServiceException;

  /**
   *  Gets account information
   *
   * @param input account number, phoneNumber,
   * @return Map of account information
   * @throws BpmServiceException when this service is not reachable or usable
   */
  Map<String, Object> getAccountInfo(Map<String, String> input) throws BpmServiceException;

  /**
   * add loan with unscheduled repayment transaction
   *
   * @param instanceId instance id
   * @param variables  request parameters
   * @return getting values from response Transaction id with scheduled, Transaction id with unscheduled, Status, Appnum
   * @throws BpmServiceException when this service is not reachable or usable
   */
  Map<String, Object> addUnschLoanRepayment(String instanceId, JSONObject variables) throws BpmServiceException;

  /**
   * add loan with scheduled repayment transaction
   *
   * @param variables Request parameters
   * @return Transaction ID and date in map
   * @throws BpmServiceException when this service is not reachable or usable
   */
  Map<String, Object> makeScheduledLoanPayment(String instanceId, JSONObject variables) throws BpmServiceException;
}
