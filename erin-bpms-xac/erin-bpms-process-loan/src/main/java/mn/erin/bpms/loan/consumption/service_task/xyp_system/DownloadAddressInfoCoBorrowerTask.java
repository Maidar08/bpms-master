package mn.erin.bpms.loan.consumption.service_task.xyp_system;

import java.util.Map;
import java.util.Objects;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.bpms.loan.consumption.utils.CustomerInfoUtils;
import mn.erin.bpms.loan.consumption.utils.DelegationExecutionUtils;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.base.model.person.AddressInfo;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.repository.ErrorMessageRepository;
import mn.erin.domain.bpm.service.CustomerService;
import mn.erin.domain.bpm.usecase.customer.GetCustomerAddressInfo;
import mn.erin.domain.bpm.usecase.customer.GetCustomerIDCardInfoInput;

import static mn.erin.bpms.loan.consumption.service_task.bpms.co_borrower.CoBorrowerUtils.INDEX_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.ADDRESS_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.EMPLOYEE_REGISTER_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER_CO_BORROWER;

/**
 * @author Tamir
 */
public class DownloadAddressInfoCoBorrowerTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DownloadAddressInfoCoBorrowerTask.class);

  private final CustomerService customerService;
  private final ErrorMessageRepository errorMessageRepository;
  private final AuthenticationService authenticationService;

  public DownloadAddressInfoCoBorrowerTask(CustomerService customerService, ErrorMessageRepository errorMessageRepository,
      AuthenticationService authenticationService)
  {
    this.customerService = Objects.requireNonNull(customerService, "Customer service is required!");
    this.errorMessageRepository = Objects.requireNonNull(errorMessageRepository, "ErrorMessageRepository is required!");
    this.authenticationService = authenticationService;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String regNum = (String) execution.getVariable(REGISTER_NUMBER);
    String requestId = (String )execution.getVariable(PROCESS_REQUEST_ID);
    String userId = authenticationService.getCurrentUserId();
    LOGGER.info("*********** Download Address Info Co Borrower Task with initial borrower REG_NUMBER ={}, REQUEST_ID ={}, User ID ={}", regNum, requestId, userId);

    Map<String, Object> variables = execution.getVariables();

    String currentRegNum = (String) execution.getVariable(REGISTER_NUMBER_CO_BORROWER);

    Integer indexCoBorrower = (Integer) execution.getVariable(INDEX_CO_BORROWER);

    LOGGER.info("*********** Downloading CO-BORROWER ADDRESS information from xyp system with REG_NUMBER ={}...", currentRegNum);

    AddressInfo addressInfo = getAddressInfo(execution);

    String addressString = CustomerInfoUtils.getAddressString(addressInfo);

    if (null != indexCoBorrower)
    {
      for (Integer index = indexCoBorrower; index > 0; index--)
      {
        String regNumberCoBorrower = (String) execution.getVariable(REGISTER_NUMBER_CO_BORROWER + "-" + index);

        if (regNumberCoBorrower.equalsIgnoreCase(currentRegNum))
        {
          String indexedAddress = ADDRESS_CO_BORROWER + "-" + index;
          if (variables.containsKey(indexedAddress))
          {
            execution.removeVariable(indexedAddress);
          }
          execution.setVariable(indexedAddress, addressString);
        }
      }
    }

    LOGGER.info("*********** Successful downloaded CO-BORROWER ADDRESS information from XYP system.");
  }

  private AddressInfo getAddressInfo(DelegateExecution execution) throws UseCaseException
  {
    String regNum = DelegationExecutionUtils.getExecutionParameterStringValue(execution, REGISTER_NUMBER_CO_BORROWER);
    String employeeRegNum = DelegationExecutionUtils.getExecutionParameterStringValue(execution, EMPLOYEE_REGISTER_NUMBER);

    GetCustomerIDCardInfoInput input = new GetCustomerIDCardInfoInput(regNum, employeeRegNum);
    GetCustomerAddressInfo getCustomerAddressInfo = new GetCustomerAddressInfo(customerService);

    return getCustomerAddressInfo.execute(input);
  }
}
