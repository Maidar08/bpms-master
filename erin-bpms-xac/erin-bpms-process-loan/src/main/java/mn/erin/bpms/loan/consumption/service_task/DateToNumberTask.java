/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpms.loan.consumption.service_task;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.base.usecase.UseCaseException;

import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;

/**
 * @author Tamir
 */
public class DateToNumberTask implements JavaDelegate
{
  private final AuthenticationService authenticationService;

  public DateToNumberTask(AuthenticationService authenticationService)
  {
    this.authenticationService = authenticationService;
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(DateToNumberTask.class);
  private static final String XAC_SPAN_DAY = "xacspan";
  private static final String XAC_SPAN_DATE = "xacspanDate";
  private static final String UTC_8_ZONE = "UTC-8";

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String registrationNumber = (String) execution.getVariable("registerNumber");
    String requestId = (String )execution.getVariable(PROCESS_REQUEST_ID);
    String userId = authenticationService.getCurrentUserId();
    LOGGER.info("#########  Date To Number Task.. Register Number: " + registrationNumber + ", Request ID: " + requestId + " , User ID: " + userId);

    //add a check so that date selected isn't after current date
    Date xacSpanDate = (Date) execution.getVariable(XAC_SPAN_DATE);
    LocalDate xacLocalDate = xacSpanDate.toInstant().atZone(ZoneId.of(UTC_8_ZONE)).toLocalDate();

    LocalDate nowDate = LocalDate.now(ZoneId.systemDefault());

    long numberOfDaysBetween = Duration.between(xacLocalDate.atStartOfDay(), nowDate.atStartOfDay()).toDays();

    Double xacSpanDay = (double) numberOfDaysBetween;

    if (xacSpanDay < 0)
    {
      String errorCode = "BPMS079";
      throw new UseCaseException(errorCode, "Date cannot be in the future!");
    }

    execution.removeVariable(XAC_SPAN_DAY);
    execution.setVariable(XAC_SPAN_DAY, xacSpanDay);

    LOGGER.info("#########  Finished Date To Number... ");
  }
}
