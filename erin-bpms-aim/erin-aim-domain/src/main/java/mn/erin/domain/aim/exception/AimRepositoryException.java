package mn.erin.domain.aim.exception;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class AimRepositoryException extends Exception
{
  public AimRepositoryException(String message)
  {
    super(message);
  }

  public AimRepositoryException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
