/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.service;

import mn.erin.domain.base.usecase.ErinException;

/**
 * @author Tamir
 */
public class BpmServiceException extends ErinException
{
  public BpmServiceException(String message) { super(message); }

  public BpmServiceException(String message, Throwable cause) { super(message, cause); }

  public BpmServiceException(String code, String message) { super(code, message); }

  public BpmServiceException(String code, String message, Throwable cause) { super(code, message, cause); }
}
