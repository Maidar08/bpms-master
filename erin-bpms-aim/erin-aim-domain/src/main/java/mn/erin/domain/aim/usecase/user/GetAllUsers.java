package mn.erin.domain.aim.usecase.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.ContactInfo;
import mn.erin.domain.aim.model.user.User;
import mn.erin.domain.aim.model.user.UserInfo;
import mn.erin.domain.aim.repository.UserRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetAllUsers extends AuthorizedUseCase<Void, List<GetUserOutput>>
{
  private static final Permission permission = new AimModulePermission("GetAllUsers");
  private final UserRepository userRepository;
  private final TenantIdProvider tenantIdProvider;

  public GetAllUsers()
  {
    super();
    this.userRepository = null;
    this.tenantIdProvider = null;
  }

  public GetAllUsers(AuthenticationService authenticationService, AuthorizationService authorizationService,
      UserRepository userRepository, TenantIdProvider tenantIdProvider)
  {
    super(authenticationService, authorizationService);
    this.userRepository = Objects.requireNonNull(userRepository, "UserRepository cannot be null!");
    this.tenantIdProvider = Objects.requireNonNull(tenantIdProvider, "TenantID provider cannot be null!");
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected List<GetUserOutput> executeImpl(Void input)
  {
    List<GetUserOutput> result = new ArrayList<>();

    List<User> allUsers = userRepository.getAllUsers(TenantId.valueOf(tenantIdProvider.getCurrentUserTenantId()));

    for (User user : allUsers)
    {
      result.add(convert(user));
    }

    return result;
  }

  public GetUserOutput convert(User user)
  {
    UserInfo userInfo = user.getUserInfo();
    ContactInfo contactInfo = user.getContactInfo();

    return new GetUserOutput.Builder(user.getUserId().getId())
        .withFirstName(userInfo != null ? userInfo.getFirstName() : null)
        .withLastName(userInfo != null ? userInfo.getLastName() : null)
        .withDisplayName(userInfo != null ? userInfo.getDisplayName() : null)
        .withTenant(user.getTenantId().getId())
        .withEmail(contactInfo != null ? contactInfo.getEmail() : null)
        .withPhoneNumber(contactInfo != null ? contactInfo.getPhoneNumber() : null)
        .build();
  }
}
