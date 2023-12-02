package mn.erin.domain.aim.model.user;

import mn.erin.domain.base.model.ValueObject;

/**
 * @author Zorig
 */
public class ContactInfo implements ValueObject<ContactInfo>
{
  private final String email;
  private final String phoneNumber;

  public ContactInfo(String email, String phoneNumber)
  {
    this.email = email;
    this.phoneNumber = phoneNumber;
  }

  public String getEmail()
  {
    return email;
  }

  public String getPhoneNumber()
  {
    return phoneNumber;
  }

  @Override
  public boolean sameValueAs(ContactInfo other)
  {
    return false;
  }
}
