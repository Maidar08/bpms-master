/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.domain.ohs.xac.exception;

import mn.erin.domain.base.usecase.ErinException;

/**
 * @author Tamir
 */
public class XacHttpException extends ErinException
{
  public XacHttpException(String message)
  {
    super(message);
  }

  public XacHttpException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public XacHttpException(String code, String message) { super(code, message); }

  public XacHttpException(String code, String message, Throwable cause) { super(code, message, cause); }
}
