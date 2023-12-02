/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.bpms.loan.consumption.service_task.bpms.scoring;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

/**
 * @author Zorig
 */
public class SetVariablesBeforeMicroScoring implements JavaDelegate
{
  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    Object dateObject = execution.getVariable("xacspanDate");

    if (dateObject != null && dateObject instanceof String)
    {
      DateFormat iso8601DateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

      String dateString = (String) dateObject;

      Date xacspanDate = iso8601DateFormat.parse(dateString);

      execution.setVariable("xacspanDate", xacspanDate);
    }
  }
}
