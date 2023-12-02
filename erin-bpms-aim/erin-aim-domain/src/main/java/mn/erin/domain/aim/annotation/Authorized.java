package mn.erin.domain.aim.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker for authorized use case classes which shall be scanned for detecting static permission string.
 *
 * @author EBazarragchaa
 */
// Make the annotation available at runtime:
@Retention(RetentionPolicy.RUNTIME)
// Allow to use only on types:
@Target(ElementType.TYPE)
public @interface Authorized
{
  /**
   * User friendly name of annotated class.
   */
  String name() default "";
}
