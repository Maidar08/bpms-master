/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpms.loan.consumption.exception;

/**
 * @author Tamir
 */
public class CamundaServiceException extends Exception
{
  public CamundaServiceException(String message)
  {
    super(message);
  }

  public CamundaServiceException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
