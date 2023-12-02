/*
 * Copyright (C) ERIN SYSTEMS LLC, 2020. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.bpms.loan.consumption.service_task.bpms.contract;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

/**
 * @author Zorig
 */
public class SetLoanPreparationBoolean implements JavaDelegate
{
  private static final String IS_COMPLETED_LOAN_PREPARATION = "IS_COMPLETED_LOAN_PREPARATION";

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    execution.setVariable(IS_COMPLETED_LOAN_PREPARATION, true);
  }
}
