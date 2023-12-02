package mn.erin.domain.aim.model.permission;

import java.util.Objects;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.model.ValueObject;

/**
 * Presents a permission string which consists of application id, module id and action id.
 * A permission says that an action in the module of the application needs a permission to be executed
 *
 * @author EBazarragchaa
 */
public class Permission implements ValueObject<Permission>
{
  private static final String SEPARATOR = ".";
  private final String applicationId;
  private final String moduleId;
  private final String actionId;

  public Permission(String applicationId, String moduleId, String actionId)
  {
    this.applicationId = Validate.notBlank(applicationId, "Application id is required!");
    this.moduleId = Validate.notBlank(moduleId, "Module id is required!");
    this.actionId = Validate.notBlank(actionId, "Action id is required!");
  }

  public String getApplicationId()
  {
    return applicationId;
  }

  public String getModuleId()
  {
    return moduleId;
  }

  public String getActionId()
  {
    return actionId;
  }

  public final String getPermissionString()
  {
    return applicationId + SEPARATOR + moduleId + SEPARATOR + actionId;
  }

  @Override
  public String toString()
  {
    return "Permission{" +
      "applicationId='" + applicationId + '\'' +
      ", moduleId='" + moduleId + '\'' +
      ", actionId='" + actionId + '\'' +
      '}';
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Permission that = (Permission) o;
    return applicationId.equals(that.applicationId) &&
      moduleId.equals(that.moduleId) &&
      actionId.equals(that.actionId);
  }

  public static final Permission valueOf(String permissionString)
  {
    if (StringUtils.isBlank(permissionString))
    {
      return null;
    }
    String[] parts = permissionString.split(Pattern.quote(SEPARATOR));
    if (null != parts && parts.length > 2)
    {
      return new Permission(parts[0], parts[1], parts[2]);
    }

    return null;
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(applicationId, moduleId, actionId);
  }

  @Override
  public boolean sameValueAs(Permission other)
  {
    return other != null && this.equals(other);
  }
}
