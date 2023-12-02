package mn.erin.bpm.domain.ohs.xac.branch.banking.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import mn.erin.domain.bpm.model.account.UDField;
import mn.erin.domain.bpm.model.account.XacAccount;
import mn.erin.domain.bpm.model.collateral.Collateral;
import mn.erin.domain.bpm.model.customer.Customer;
import mn.erin.domain.bpm.model.loan_contract.LinkageCollateralInfo;
import mn.erin.domain.bpm.service.NewCoreBankingService;

/**
 * @author Tamir
 */
public class XacBranchBankingNewCoreService implements NewCoreBankingService
{
  @Override
  public Map<String, Object> findCustomerByCifNumber(Map<String, String> input)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Customer findCustomerByPersonIdAndType(String personId, String customerType)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean checkRiskyCustomer(String personId)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Collateral> getCollateralsByCifNumber(String cifNumber)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<String> getCollateralCode(String colType)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public String createImmovableCollateral(Map<String, Object> genericInfo, Map<String, Object> collateralInfo, Map<String, Object> inspectionInfo,
      Map<String, Object> ownershipInfo)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public String createMachineryCollateral(Map<String, Object> genericInfo, Map<String, Object> collateralInfo, Map<String, Object> inspectionInfo,
      Map<String, Object> ownershipInfo)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Map<String, List<String>> getCollReferenceCodes(List<String> types)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<XacAccount> getAccountsList(Map<String, String> input)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Map<String, UDField> getUDFields(Map<String, String> input)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public String createLoanAccount(Map<String, Object> genericInfos, Map<String, Object> additionalInfos, List<Map<String, Object>> coBorrowers)

  {
    return null;
  }

  @Override
  public String createVehicleCollateral(Map<String, Object> collateralCreationInfo, Map<String, Object> vehicleInfo, Map<String, Object> infectionInfo,
      Map<String, Object> ownershipInfo)
  {
    return null;
  }

  @Override
  public String createOtherCollateral(Map<String, Object> collateralGenInfo, Map<String, Object> otherCollateralInfo, Map<String, Object> inspectionInfo,
      Map<String, Object> ownershipInfo)
  {
    return null;
  }

  @Override
  public Map<String, Object> getMachineryCollateral(String collateralId)
  {
    return Collections.emptyMap();
  }

  @Override
  public Map<String, Object> getOtherCollateral(String collateralId)
  {
    return Collections.emptyMap();
  }

  @Override
  public Map<String, Object> getImmovablePropertyCollateral(String collateralId)
  {
    return Collections.emptyMap();
  }

  @Override
  public String modifyMachineryCollateral(String collateralId, Map<String, Object> genericInfo, Map<String, Object> collateralInfo,
      Map<String, Object> inspectionInfo, Map<String, Object> ownershipInfo)
  {
    return null;
  }

  @Override
  public String modifyImmovablePropCollateral(String collateralId, Map<String, Object> genericInfo, Map<String, Object> collateralInfo,
      Map<String, Object> inspectionInfo, Map<String, Object> ownershipInfo)
  {
    return null;
  }

  @Override
  public Map<String, Object> getVehicleCollInfo(String input)
  {
    return Collections.emptyMap();
  }

  @Override
  public String modifyVehicleCollateral(String collateralId, Map<String, Object> genericInfo, Map<String, Object> collateralInfo,
      Map<String, Object> inspectionInfo, Map<String, Object> ownershipInfo)
  {
    return null;
  }

  @Override
  public String modifyOtherCollateral(String collateralId, Map<String, Object> genericInfo, Map<String, Object> collateralInfo,
      Map<String, Object> inspectionInfo, Map<String, Object> ownershipInfo)
  {
    return null;
  }

  @Override
  public boolean linkCollaterals(String accountNumber, String linkageType, Map<String, Object> collateralDetails)
  {
    return false;
  }

  @Override
  public Map<String, Object> getLoanAccountInfo(Map<String, String> input)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<LinkageCollateralInfo> getInquireCollateralDetails(String entityId, String entityType)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Map<String, Object> getAccountInfo(Map<String, String> input)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Map<String, Object> makeScheduledLoanPayment(String instanceId, JSONObject variables)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Map<String, Object> addUnschLoanRepayment(String instanceId, JSONObject variables)
  {
    throw new UnsupportedOperationException();
  }
}
