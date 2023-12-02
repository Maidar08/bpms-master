/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.bpms.loan.consumption.service_task;



import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

/**
 * @author Zorig
 */
public class SetVariablesBeforeMicroAccountCreationTask  implements JavaDelegate
{
  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    execution.setVariable("isLinked", false);
  }
}
