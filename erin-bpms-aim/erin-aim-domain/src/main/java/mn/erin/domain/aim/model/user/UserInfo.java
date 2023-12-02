package mn.erin.domain.aim.model.user;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.model.ValueObject;

/**
 * @author Zorig
 */
public class UserInfo implements ValueObject<UserInfo>
{
  private final String firstName;
  private final String lastName;
  private final String displayName;

  public UserInfo(String firstName, String lastName)
  {
    this.firstName = firstName;
    this.lastName = lastName;

    this.displayName = toDisplayName(firstName, lastName);
  }

  public String getFirstName()
  {
    return firstName;
  }

  public String getLastName()
  {
    return lastName;
  }

  public String getDisplayName()
  {
    return displayName;
  }

  public String getUserName() {
    if (null != lastName && lastName.length() > 1 && !StringUtils.isBlank(firstName))
     {
       return lastName.charAt(0) + "." + firstName;
     }
    if (StringUtils.isBlank(firstName))
    {
     return "(empty name)";
    }
    else
    {
      return firstName;
    }

  }

  @Override
  public boolean sameValueAs(UserInfo other)
  {
    return false;
  }

  private String toDisplayName(String firstName, String lastName)
  {
    if(firstName == null){
      return (lastName == null ? "" : lastName );
    }

    return (lastName == null ? firstName : (firstName + " " + lastName) );
  }



}
