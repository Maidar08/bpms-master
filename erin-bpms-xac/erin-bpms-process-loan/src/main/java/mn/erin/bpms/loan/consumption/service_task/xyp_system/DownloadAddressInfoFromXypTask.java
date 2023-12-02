/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

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

import static mn.erin.domain.bpm.BpmModuleConstants.ADDRESS;
import static mn.erin.domain.bpm.BpmModuleConstants.EMPLOYEE_REGISTER_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;

/**
 * @author Tamir
 */
public class DownloadAddressInfoFromXypTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DownloadAddressInfoFromXypTask.class);

  private final CustomerService customerService;
  private final ErrorMessageRepository errorMessageRepository;
  private final AuthenticationService authenticationService;

  public DownloadAddressInfoFromXypTask(CustomerService customerService,
      ErrorMessageRepository errorMessageRepository,
      AuthenticationService authenticationService)
  {
    this.customerService = Objects.requireNonNull(customerService, "Customer service is required!");
    this.errorMessageRepository = errorMessageRepository;
    this.authenticationService = Objects.requireNonNull(authenticationService, "Authentication service is required!");
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String regNum = (String) execution.getVariable(REGISTER_NUMBER);
    String requestId = (String) execution.getVariable(PROCESS_REQUEST_ID);
    String userId = authenticationService.getCurrentUserId();
    LOGGER.info("*********** Downloading ADDRESS information from xyp system with REG_NUMBER ={}, REQUEST_ID ={}, User ID ={}", regNum, requestId, userId);

    Map<String, Object> variables = execution.getVariables();
    AddressInfo addressInfo = getAddressInfo(execution);

    String addressString = CustomerInfoUtils.getAddressString(addressInfo);

    if (variables.containsKey(ADDRESS))
    {
      execution.removeVariable(ADDRESS);
    }

    LOGGER.info(ADDRESS + ": " + addressString);
    execution.setVariable(ADDRESS, addressString);

    LOGGER.info("*********** Successful downloaded ADDRESS information from XYP system.");
  }

  private AddressInfo getAddressInfo(DelegateExecution execution) throws UseCaseException
  {
    String regNum = DelegationExecutionUtils.getExecutionParameterStringValue(execution, REGISTER_NUMBER);
    String employeeRegNum = DelegationExecutionUtils.getExecutionParameterStringValue(execution, EMPLOYEE_REGISTER_NUMBER);

    LOGGER.info("########## ADDRESS INFO DOWNLOADING BY CUSTOMER REGISTER NUMBER = [{}]", regNum);
    LOGGER.info("########## ADDRESS INFO DOWNLOADING BY EMPLOYEE REGISTER NUMBER = [{}]", employeeRegNum);

    GetCustomerIDCardInfoInput input = new GetCustomerIDCardInfoInput(regNum, employeeRegNum);
    GetCustomerAddressInfo getCustomerAddressInfo = new GetCustomerAddressInfo(customerService);

    return getCustomerAddressInfo.execute(input);
  }
}
