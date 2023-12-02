package mn.erin.domain.bpm.exception;

import mn.erin.domain.base.usecase.ErinException;

public class BpmInvalidArgumentException extends ErinException
{
  public BpmInvalidArgumentException(String message)
  {
    super(message);
  }

  public BpmInvalidArgumentException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public BpmInvalidArgumentException(String code, String message) { super(code, message); }

  public BpmInvalidArgumentException(String code, String message, Throwable cause) { super(code, message, cause); }
}
