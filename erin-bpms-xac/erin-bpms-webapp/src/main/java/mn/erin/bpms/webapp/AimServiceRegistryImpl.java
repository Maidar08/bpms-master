package mn.erin.bpms.webapp;

import javax.inject.Inject;

import mn.erin.domain.aim.provider.ExtSessionInfoCache;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.repository.RoleRepository;
import mn.erin.domain.aim.service.AimServiceRegistry;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.service.EncryptionService;
import mn.erin.domain.aim.service.PermissionService;
import mn.erin.domain.aim.service.TenantIdProvider;

/**
 * @author Lkhagvadorj.A
 **/

public class AimServiceRegistryImpl implements AimServiceRegistry
{
  private PermissionService permissionService;
  private AuthenticationService authenticationService;
  private AuthorizationService authorizationService;
  private MembershipRepository membershipRepository;
  private RoleRepository roleRepository;
  private GroupRepository groupRepository;
  private TenantIdProvider tenantIdProvider;
  private EncryptionService encryptionService;



  private ExtSessionInfoCache extSessionInfoCache;


  @Inject
  public void setPermissionService(PermissionService permissionService)
  {
    this.permissionService = permissionService;
  }

  @Inject
  public void setAuthenticationService(AuthenticationService authenticationService)
  {
    this.authenticationService = authenticationService;
  }

  @Inject
  public void setAuthorizationService(AuthorizationService authorizationService)
  {
    this.authorizationService = authorizationService;
  }

  @Inject
  public void setMembershipRepository(MembershipRepository membershipRepository)
  {
    this.membershipRepository = membershipRepository;
  }

  @Inject
  public void setRoleRepository(RoleRepository roleRepository)
  {
    this.roleRepository = roleRepository;
  }

  @Inject
  public void setGroupRepository(GroupRepository groupRepository)
  {
    this.groupRepository = groupRepository;
  }

  @Inject
  public void setTenantIdProvider(TenantIdProvider tenantIdProvider)
  {
    this.tenantIdProvider = tenantIdProvider;
  }

  @Inject
  public void setEncryptionService(EncryptionService encryptionService) { this.encryptionService = encryptionService;}
  @Inject
  public void setExtSessionInfoCache(ExtSessionInfoCache extSessionInfoCache) { this.extSessionInfoCache = extSessionInfoCache;}
  @Override
  public PermissionService getPermissionService()
  {
    return permissionService;
  }

  @Override
  public AuthenticationService getAuthenticationService()
  {
    return authenticationService;
  }

  @Override
  public AuthorizationService getAuthorizationService()
  {
    return authorizationService;
  }

  @Override
  public MembershipRepository getMembershipRepository()
  {
    return membershipRepository;
  }

  @Override
  public RoleRepository getRoleRepository()
  {
    return roleRepository;
  }

  @Override
  public TenantIdProvider getTenantIdProvider() { return tenantIdProvider; }

  @Override
  public EncryptionService getEncryptionService(){ return  encryptionService; }

  @Override
   public ExtSessionInfoCache getExtSessionInfoCache(){ return  extSessionInfoCache; }
  @Override
  public GroupRepository getGroupRepository() { return groupRepository; }
}
