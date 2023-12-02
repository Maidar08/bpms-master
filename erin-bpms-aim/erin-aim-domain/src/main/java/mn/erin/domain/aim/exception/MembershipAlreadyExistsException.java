package mn.erin.domain.aim.exception;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class MembershipAlreadyExistsException extends Exception
{
  public MembershipAlreadyExistsException(String message)
  {
    super(message);
  }

  public MembershipAlreadyExistsException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
