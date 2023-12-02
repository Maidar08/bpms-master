package mn.erin.domain.aim.model.user;

import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.base.model.Entity;

import static mn.erin.domain.aim.constant.AimErrorMessageConstant.TENANT_ID_CANNOT_BE_NULL;
import static mn.erin.domain.aim.constant.AimErrorMessageConstant.USER_ID_CANNOT_BE_NULL;

/**
 * @author Zorig
 */
public class User implements Entity<User>
{
  private final UserId userId;
  private final TenantId tenantId;
  private String password;
  private UserInfo userInfo;
  private ContactInfo contactInfo;

  public User(UserId userId, TenantId tenantId)
  {
    this.userId = Objects.requireNonNull(userId, USER_ID_CANNOT_BE_NULL);
    this.tenantId = Objects.requireNonNull(tenantId, TENANT_ID_CANNOT_BE_NULL);
  }

  public User(UserId userId, TenantId tenantId, String password)
  {
    this.userId = Objects.requireNonNull(userId, USER_ID_CANNOT_BE_NULL);
    this.tenantId = Objects.requireNonNull(tenantId, "TenantID cannot be null!");
    this.password = Validate.notBlank(password);
  }

  public User(UserId userId, TenantId tenantId, UserInfo userInfo, ContactInfo contactInfo)
  {
    this.userId = Objects.requireNonNull(userId, USER_ID_CANNOT_BE_NULL);
    this.tenantId = Objects.requireNonNull(tenantId, TENANT_ID_CANNOT_BE_NULL);
    this.userInfo = userInfo;
    this.contactInfo = contactInfo;
  }

  public String getPassword()
  {
    return password;
  }

  public void setPassword(String password)
  {
    this.password = password;
  }

  public void setUserInfo(UserInfo userInfo)
  {
    this.userInfo = userInfo;
  }

  public void setContactInfo(ContactInfo contactInfo)
  {
    this.contactInfo = contactInfo;
  }

  public UserId getUserId()
  {
    return userId;
  }

  public TenantId getTenantId()
  {
    return tenantId;
  }

  public UserInfo getUserInfo()
  {
    return userInfo;
  }

  public ContactInfo getContactInfo()
  {
    return contactInfo;
  }

  @Override
  public boolean sameIdentityAs(User other)
  {
    return other.userId.equals(this.userId);
  }
}
