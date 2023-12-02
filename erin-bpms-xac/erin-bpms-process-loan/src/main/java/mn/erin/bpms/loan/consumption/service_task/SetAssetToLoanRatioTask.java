/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.bpms.loan.consumption.service_task;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static mn.erin.domain.bpm.BpmModuleConstants.ASSET_LOAN_RATIO;
import static mn.erin.domain.bpm.BpmModuleConstants.INCOME_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;

/**
 * @author Zorig
 */
public class SetAssetToLoanRatioTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(SetAssetToLoanRatioTask.class);

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String registrationNumber = (String) execution.getVariable("registerNumber");
    String requestId = (String )execution.getVariable(PROCESS_REQUEST_ID);
    LOGGER.info("#########  Set Asset To Loan Ratio Task.. Register Number: " + registrationNumber + ", Request ID: " + requestId);

    //this is done to always add 194 points in scoring dmn
    execution.removeVariable(ASSET_LOAN_RATIO);
    execution.setVariable(ASSET_LOAN_RATIO, Double.valueOf(2));

    execution.removeVariable(INCOME_TYPE);
    execution.setVariable(INCOME_TYPE, "B");

    LOGGER.info("######## Finished setting asset to loan ratio...");
  }
}
