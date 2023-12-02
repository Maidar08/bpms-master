package mn.erin.domain.aim.service;

import mn.erin.domain.aim.provider.ExtSessionInfoCache;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.repository.RoleRepository;

/**
 * @author Lkhagvadorj.A
 **/

public interface AimServiceRegistry
{
  PermissionService getPermissionService();

  AuthenticationService getAuthenticationService();

  AuthorizationService getAuthorizationService();

  MembershipRepository getMembershipRepository();

  RoleRepository getRoleRepository();

  GroupRepository getGroupRepository();

  TenantIdProvider getTenantIdProvider();

  EncryptionService getEncryptionService();

  ExtSessionInfoCache getExtSessionInfoCache();
}
