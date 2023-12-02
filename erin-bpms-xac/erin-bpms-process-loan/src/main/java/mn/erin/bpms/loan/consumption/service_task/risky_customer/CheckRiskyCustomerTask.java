/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpms.loan.consumption.service_task.risky_customer;

import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.service.NewCoreBankingService;
import mn.erin.domain.bpm.usecase.customer.CheckRiskyCustomer;

import static mn.erin.bpms.loan.consumption.constant.CamundaRiskyCustomerConstants.IS_RISKY_CUSTOMER;
import static mn.erin.bpms.loan.consumption.constant.CamundaRiskyCustomerConstants.RISKY;
import static mn.erin.bpms.loan.consumption.constant.CamundaRiskyCustomerConstants.RISKY_CUSTOMER_VALUE;
import static mn.erin.bpms.loan.consumption.constant.CamundaRiskyCustomerConstants.SAFELY;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;

/**
 * @author EBazarragchaa
 */
public class CheckRiskyCustomerTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(CheckRiskyCustomerTask.class);
  private final NewCoreBankingService coreBankingService;
  private final AuthenticationService authenticationService;

  public CheckRiskyCustomerTask(NewCoreBankingService coreBankingService, AuthenticationService authenticationService)
  {
    this.coreBankingService = coreBankingService;
    this.authenticationService = authenticationService;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    Map<String, Object> variables = execution.getVariables();

    String registerNumber = (String) variables.get(BpmModuleConstants.REGISTER_NUMBER);
    String requestId = (String) execution.getVariable(PROCESS_REQUEST_ID);
    String userId = authenticationService.getCurrentUserId();

    LOGGER.info("##### Download RISKY CUSTOMER INFO by REG_NUMBER ={}, REQUEST_ID ={}, User ID ={}", registerNumber, requestId, userId);
    boolean isRisky = new CheckRiskyCustomer(coreBankingService).execute(registerNumber);

    execution.setVariable(IS_RISKY_CUSTOMER, isRisky);

    if (isRisky)
    {
      LOGGER.info("##### Customer is risky customer REG_NUMBER ={} ", registerNumber);
      execution.setVariable(RISKY_CUSTOMER_VALUE, RISKY);
    }
    else
    {
      LOGGER.info("##### Customer is safe customer REG_NUMBER ={} ", registerNumber);
      execution.setVariable(RISKY_CUSTOMER_VALUE, SAFELY);
    }

    LOGGER.info("*********** Successful downloaded risky customer info.");
  }
}
