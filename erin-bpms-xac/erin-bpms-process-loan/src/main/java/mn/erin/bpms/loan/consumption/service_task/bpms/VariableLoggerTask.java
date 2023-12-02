/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpms.loan.consumption.service_task.bpms;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.common.utils.LoggerUtils;
import mn.erin.domain.aim.service.AuthenticationService;

import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;

/**
 * @author Tamir
 */
public class VariableLoggerTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(VariableLoggerTask.class);

  private final AuthenticationService authenticationService;

  public VariableLoggerTask(AuthenticationService authenticationService)
  {
    this.authenticationService = authenticationService;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String registrationNumber = (String) execution.getVariable("registerNumber");
    String requestId = (String )execution.getVariable(PROCESS_REQUEST_ID);
    String userId = authenticationService.getCurrentUserId();
    LOGGER.info("#########  Logging execution variables... REG_NUMBER ={}, REQUEST_ID ={}, User ID ={}", registrationNumber, requestId, userId);

    LoggerUtils.logVariables(LOGGER, execution.getVariables());

    LOGGER.info("########## Successful logged execution variables.");
  }
}
