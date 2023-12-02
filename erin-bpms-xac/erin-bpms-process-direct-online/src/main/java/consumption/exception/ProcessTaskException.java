package consumption.exception;

import mn.erin.domain.base.usecase.ErinException;

/**
 * Process task specific exception
 *
 * @author EBazarragchaa
 */
public class ProcessTaskException extends ErinException
{
  public ProcessTaskException(Throwable cause)
  {
    super(cause);
  }

  public ProcessTaskException(String message)
  {
    super(message);
  }

  public ProcessTaskException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public ProcessTaskException(String code, String message) { super(code, message); }

  public ProcessTaskException(String code, String message, Throwable cause) { super(code, message, cause); }
}
