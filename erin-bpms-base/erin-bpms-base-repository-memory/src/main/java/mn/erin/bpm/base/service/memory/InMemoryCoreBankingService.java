package mn.erin.bpm.base.service.memory;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import mn.erin.domain.bpm.model.account.Account;
import mn.erin.domain.bpm.model.account.UDField;
import mn.erin.domain.bpm.model.account.XacAccount;
import mn.erin.domain.bpm.model.collateral.Collateral;
import mn.erin.domain.bpm.model.customer.Customer;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CoreBankingService;

@Service
public class InMemoryCoreBankingService implements CoreBankingService
{
  @Override
  public Customer findCustomerByPersonId(String personId) throws BpmServiceException
  {
    return null;
  }

  @Override
  public Customer findCustomerByCifNumber(String cifNumber) throws BpmServiceException
  {
    return null;
  }

  @Override
  public boolean checkRiskyCustomer(String personId) throws BpmServiceException
  {
    return false;
  }

  @Override
  public Account createCustomerAccount(String cifNumber, String accountType)
  {
    return null;
  }

  @Override
  public String getCustomerResource(String customerCif) throws BpmServiceException
  {
    return null;
  }

  @Override
  public int getCustomerLoanPeriodInformation(String customerCif) throws BpmServiceException
  {
    return 0;
  }

  @Override
  public Map<String, UDField> getUDFields(String productCode) throws BpmServiceException
  {
    return null;
  }

  @Override
  public Map<String, UDField> getUDFieldsByFunction(String bodyFunction) throws BpmServiceException
  {
    return null;
  }

  @Override
  public int createLoanAccount(String productCode, Map<String, String> accountInformation, List<Map<String, String>> coBorrowers) throws BpmServiceException
  {
    return 0;
  }

  @Override
  public int createCollateralLoanAccount(String productCode, Map<String, String> accountInformation, List<Map<String, String>> coBorrowers,
      Map<String, Map<String, Object>> collateralMap) throws BpmServiceException
  {
    return 0;
  }

  @Override
  public List<XacAccount> getAccountsListFC(String regNumber, String customerID) throws BpmServiceException
  {
    return null;
  }

  @Override
  public List<Collateral> getCollateralsByCifNumber(String cifNumber) throws BpmServiceException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean createCollateral(String branch, String cif, Map<String, String> collateralCreationInfo, Map<String, Serializable> udFields)
      throws BpmServiceException
  {
    return false;
  }
}
