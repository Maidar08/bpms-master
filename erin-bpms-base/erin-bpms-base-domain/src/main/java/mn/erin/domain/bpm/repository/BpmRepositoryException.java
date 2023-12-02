/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.repository;

import mn.erin.domain.base.usecase.ErinException;

/**
 * @author Tamir
 */
public class BpmRepositoryException extends ErinException
{
  public BpmRepositoryException(String message)
  {
    super(message);
  }

  public BpmRepositoryException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public BpmRepositoryException(String errorCode, String errorMessage)
  {
    super(errorCode, errorMessage);
  }
}
