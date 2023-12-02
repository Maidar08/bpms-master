package mn.erin.bpms.loan.consumption.service_task.core_bank;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.bpms.process.base.ProcessTaskException;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.base.model.person.AddressInfo;
import mn.erin.domain.base.model.person.ContactInfo;
import mn.erin.domain.base.model.person.PersonInfo;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.model.customer.Customer;
import mn.erin.domain.bpm.service.CoreBankingService;
import mn.erin.domain.bpm.service.NewCoreBankingService;
import mn.erin.domain.bpm.usecase.customer.GetCustomerByCustNoNewCore;
import mn.erin.domain.bpm.usecase.customer.GetCustomerByPersonIdAndType;
import mn.erin.domain.bpm.usecase.customer.GetCustomerByPersonIdAndTypeInput;

import static mn.erin.bpms.loan.consumption.service_task.bpms.co_borrower.CoBorrowerUtils.INDEX_CO_BORROWER;
import static mn.erin.bpms.loan.consumption.service_task.bpms.co_borrower.CoBorrowerUtils.setIndexedVariable;
import static mn.erin.bpms.loan.consumption.utils.CustomerInfoUtils.setCoBorrowerFullName;
import static mn.erin.bpms.loan.consumption.utils.CustomerInfoUtils.setCustomerType;
import static mn.erin.domain.bpm.BpmModuleConstants.ADDRESS_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.EMAIL_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.EMPTY_VALUE;
import static mn.erin.domain.bpm.BpmModuleConstants.FULL_NAME_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.NULL_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.OCCUPANCY_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER_CO_BORROWER;

/**
 * @author Tamir
 */
public class DownloadCoBorrowerInfoTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DownloadCoBorrowerInfoTask.class);

  private final CoreBankingService coreBankingService;
  private final NewCoreBankingService newCoreBankingService;
  private final AuthenticationService authenticationService;

  public DownloadCoBorrowerInfoTask(CoreBankingService coreBankingService, NewCoreBankingService newCoreBankingService,
      AuthenticationService authenticationService)
  {
    this.coreBankingService = coreBankingService;
    this.newCoreBankingService = newCoreBankingService;
    this.authenticationService = authenticationService;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String registerNumber = (String) execution.getVariable(BpmModuleConstants.REGISTER_NUMBER);
    String requestId = (String) execution.getVariable(PROCESS_REQUEST_ID);
    String userId = authenticationService.getCurrentUserId();

    if (validateCoBorrowerRegisterNumber(execution, registerNumber))
    {
      if (validatePreviousCoBorrowersRegisterNumber(execution))
      {
        LOGGER.info("************* Update co-borrower task started... REG_NUMBER ={}, REQUEST_ID ={}, User ID ={}", registerNumber, requestId, userId);

        // downloads info by reg number from CBS.
        downloadInfoByRegNo(execution);

        // downloads info by cif number from CBS.
        downloadInfoByCifNo(execution);
      }
      else
      {
        throw new ProcessTaskException("Co Borrower is added with this register!");
      }
    }
    else
    {
      throw new ProcessTaskException("Could not register Co Borrower with the same register id!");
    }
  }

  private boolean validateCoBorrowerRegisterNumber(DelegateExecution execution, String registerNumber)
  {
    Integer index = (Integer) execution.getVariable(INDEX_CO_BORROWER);
    String coBorrowerRegisterNumber = (String) execution.getVariable(REGISTER_NUMBER_CO_BORROWER + "-" + index);

    return !StringUtils.equals(coBorrowerRegisterNumber, registerNumber);
  }

  private boolean validatePreviousCoBorrowersRegisterNumber(DelegateExecution execution)
  {
    /* Get currently adding co borrower register number. */
    Integer index = (Integer) execution.getVariable(INDEX_CO_BORROWER);
    String coBorrowerRegisterNumber = (String) execution.getVariable(REGISTER_NUMBER_CO_BORROWER + "-" + index);

    for (int i = index - 1; i >= 1; i--)
    {
      /* Get previously added co borrower register number. */
      String previousCoBorrowerRegisterNumber = (String) execution.getVariable(REGISTER_NUMBER_CO_BORROWER + "-" + i);
      if (StringUtils.equals(coBorrowerRegisterNumber, previousCoBorrowerRegisterNumber))
      {
        return false;
      }
    }

    return true;
  }

  private void downloadInfoByRegNo(DelegateExecution execution) throws UseCaseException
  {
    Integer index = (Integer) execution.getVariable(INDEX_CO_BORROWER);
    String regNumberCoBorrower = (String) execution.getVariable(REGISTER_NUMBER_CO_BORROWER + "-" + index);
    try
    {
      Map<String, Object> variables = execution.getVariables();
      Customer customer = getCustomerByRegNo(regNumberCoBorrower, variables);

      if (null != customer)
      {
        PersonInfo personInfo = customer.getPersonInfo();

        String firstName = personInfo.getFirstName();

        setCoBorrowerFullName(firstName, execution);
        execution.setVariable(FULL_NAME_CO_BORROWER, firstName);

        String cifNumber = customer.getCustomerNumber();

        if (null == cifNumber)
        {
          execution.setVariable(CIF_NUMBER_CO_BORROWER, EMPTY_VALUE);
        }
        else
        {
          execution.setVariable(CIF_NUMBER_CO_BORROWER, cifNumber);
          setIndexedVariable(execution, CIF_NUMBER_CO_BORROWER, cifNumber);
        }
      }
    }
    catch (RuntimeException e)
    {
      LOGGER.error(e.getMessage(), e);
    }
  }

  private Customer getCustomerByRegNo(String registerNumber, Map<String, Object> variables) throws UseCaseException
  {
    LOGGER.info("Downloads co-borrower info by REG_NUMBER = {} from XAC CBS.", registerNumber);

    String customerType = setCustomerType(variables.get("borrowerTypeCoBorrower"));
    GetCustomerByPersonIdAndTypeInput input = new GetCustomerByPersonIdAndTypeInput("", customerType, registerNumber);
    GetCustomerByPersonIdAndType useCase = new GetCustomerByPersonIdAndType(newCoreBankingService);

    LOGGER.info("**************** Successful downloaded co-borrower info by REG_NUMBER = {}.", registerNumber);

    return useCase.execute(input);
  }

  private void downloadInfoByCifNo(DelegateExecution execution) throws UseCaseException
  {
    Integer index = (Integer) execution.getVariable(INDEX_CO_BORROWER);
    String cifNumber = (String) execution.getVariable(CIF_NUMBER_CO_BORROWER + "-" + index);

    if (!StringUtils.isBlank(cifNumber))
    {
      Map<String, String> input = new HashMap<>();
      input.put(CIF_NUMBER, cifNumber);
      input.put(PROCESS_TYPE_ID, String.valueOf(execution.getVariable(PROCESS_TYPE_ID)));
      input.put(PHONE_NUMBER, String.valueOf(execution.getVariable(PHONE_NUMBER)));
      Customer customer = getCustomerByCif(input);

      if (null == customer)
      {
        return;
      }

      LOGGER.info("########## Successful downloaded customer info by CIF number = [{}]", cifNumber);

      List<ContactInfo> contactInfoList = customer.getContactInfoList();
      List<AddressInfo> addressInfoList = customer.getAddressInfoList();

      setContactInfo(execution, contactInfoList);

      setAddressInfo(execution, addressInfoList);

      setOccupancy(execution, customer.getOccupancy());
    }
  }

  private void setOccupancy(DelegateExecution execution, String occupancy)
  {
    if (NULL_STRING.equals(occupancy) || StringUtils.isBlank(occupancy))
    {
      execution.setVariable(OCCUPANCY_CO_BORROWER, EMPTY_VALUE);
    }
    else
    {
      execution.setVariable(OCCUPANCY_CO_BORROWER, occupancy);
      setIndexedVariable(execution, OCCUPANCY_CO_BORROWER, occupancy);
    }
  }

  private void setAddressInfo(DelegateExecution execution, List<AddressInfo> addressInfoList)
  {
    if (addressInfoList.isEmpty())
    {
      // removes previous filled value.
      execution.setVariable(ADDRESS_CO_BORROWER, EMPTY_VALUE);
    }
    else
    {
      AddressInfo addressInfo = addressInfoList.get(0);
      if (NULL_STRING.equals(addressInfo.getFullAddress()) || StringUtils.isBlank(addressInfo.getFullAddress()))
      {
        execution.setVariable(ADDRESS_CO_BORROWER, EMPTY_VALUE);
      }
      String addressString = addressInfo.getFullAddress();
      execution.setVariable(ADDRESS_CO_BORROWER, addressString);
      setIndexedVariable(execution, ADDRESS_CO_BORROWER, addressString);
    }
  }

  private void setContactInfo(DelegateExecution execution, List<ContactInfo> contactInfoList)
  {
    if (contactInfoList.isEmpty())
    {
      execution.setVariable(PHONE_NUMBER_CO_BORROWER, EMPTY_VALUE);
      execution.setVariable(EMAIL_CO_BORROWER, EMPTY_VALUE);
    }
    else
    {
      ContactInfo contactInfo = contactInfoList.get(0);

      if (null != contactInfo)
      {
        String phone = contactInfo.getPhone();
        String email = contactInfo.getEmail();

        if (StringUtils.isBlank(phone))
        {
          execution.removeVariable(PHONE_NUMBER_CO_BORROWER);
        }
        else
        {
          execution.setVariable(PHONE_NUMBER_CO_BORROWER, phone);
          setIndexedVariable(execution, PHONE_NUMBER_CO_BORROWER, phone);
        }

        if (StringUtils.isBlank(email))
        {
          execution.removeVariable(EMAIL_CO_BORROWER);
        }
        else
        {
          execution.setVariable(EMAIL_CO_BORROWER, email);
          setIndexedVariable(execution, EMAIL_CO_BORROWER, email);
        }
      }
    }
  }

  private Customer getCustomerByCif(Map<String, String> input) throws UseCaseException
  {
    LOGGER.info("Downloads co-borrower info by cifNumber = [{}] from XAC CBS.", input.get(CIF_NUMBER));

    GetCustomerByCustNoNewCore getCustomerByCustNoNewCore = new GetCustomerByCustNoNewCore(newCoreBankingService);

    return getCustomerByCustNoNewCore.execute(input);
  }
}
