/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.aim.rest.shiro.config;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;

import mn.erin.domain.aim.constant.AimConstants;
import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.role.Role;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.User;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.repository.RoleRepository;
import mn.erin.domain.aim.repository.UserRepository;

/**
 * @author EBazarragchaa
 */
public class AimRealm extends AuthorizingRealm
{
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final MembershipRepository membershipRepository;

  public AimRealm(MembershipRepository membershipRepository, UserRepository userRepository, RoleRepository roleRepository)
  {
    this.membershipRepository = Objects.requireNonNull(membershipRepository);
    this.userRepository = Objects.requireNonNull(userRepository);
    this.roleRepository = Objects.requireNonNull(roleRepository);
  }

  @Override
  public String getName()
  {
    return "AimRealm";
  }

  @Override
  protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals)
  {
    String userName = (String) getAvailablePrincipal(principals);

    Subject currentUser = SecurityUtils.getSubject();
    String tenantId = (String) currentUser.getSession().getAttribute(AimConstants.SESSION_ATTR_TENANT_ID);

    SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();

    try
    {
      if (AimConstants.ADMIN_USER_ID.equals(userName))
      {
        authorizationInfo.addStringPermission("*");
      }
      else
      {
        List<Membership> memberships = membershipRepository.listAllByUserId(TenantId.valueOf(tenantId), UserId.valueOf(userName));
        Set<String> stringPermissions = new TreeSet<>();

        for (Membership membership : memberships)
        {
          Role role = roleRepository.findById(membership.getRoleId());
          authorizationInfo.addRole(role.getRoleId().getId());
          stringPermissions.addAll(getShiroRolePermissions(role));
        }

        authorizationInfo.addStringPermissions(stringPermissions);
      }

      return authorizationInfo;
    }
    catch (AimRepositoryException e)
    {
      throw new AuthorizationException(e.getMessage(), e);
    }


  }

  @Override
  protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException
  {
    AimAuthenticationToken uToken = (AimAuthenticationToken) token;

    if (StringUtils.isBlank(uToken.getTenantId()) || StringUtils.isBlank(uToken.getUsername())
      || StringUtils.isBlank(new String(uToken.getPassword())))
    {
      throw new UnknownAccountException("User authentication data [userId, tenantId, password] is invalid!");
    }

    try
    {
      User user = userRepository.getUserByTenantId(TenantId.valueOf(uToken.getTenantId()), UserId.valueOf(uToken.getUsername()));

      if (new String(uToken.getPassword()).equals(user.getPassword()))
      {
        return new SimpleAuthenticationInfo(uToken.getUsername(), uToken.getPassword(), getName());
      }
      else
      {
        throw new UnknownAccountException("Wrong credentials!");
      }
    }
    catch (AimRepositoryException e)
    {
      // TODO log please
      throw new UnknownAccountException("Wrong credentials!");
    }
  }

  private Collection<String> getShiroRolePermissions(Role role)
  {
    Set<String> shiroPermissions = new TreeSet<>();
    for (Permission aimPermission : role.getPermissions())
    {
      String shiroPermission = ShiroUtils.toShiroPermission(aimPermission);
      shiroPermissions.add(shiroPermission);
    }

    return shiroPermissions;
  }
}
