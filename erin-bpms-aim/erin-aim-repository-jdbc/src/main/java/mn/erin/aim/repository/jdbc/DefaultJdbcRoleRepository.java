package mn.erin.aim.repository.jdbc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;

import org.apache.commons.lang3.Validate;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.stereotype.Repository;

import mn.erin.aim.repository.jdbc.interfaces.JdbcRolePermissionRepository;
import mn.erin.aim.repository.jdbc.interfaces.JdbcRoleRepository;
import mn.erin.aim.repository.jdbc.model.JdbcRole;
import mn.erin.aim.repository.jdbc.model.JdbcRolePermissionJoined;
import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.role.Role;
import mn.erin.domain.aim.model.role.RoleId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.repository.RoleRepository;
import mn.erin.domain.base.model.EntityId;

/**
 * @author Zorig
 */
@Repository
public class DefaultJdbcRoleRepository implements RoleRepository
{
  private final JdbcRoleRepository jdbcRoleRepository;
  private final JdbcRolePermissionRepository jdbcRolePermissionRepository;

  @Inject
  public DefaultJdbcRoleRepository(JdbcRoleRepository jdbcRoleRepository, JdbcRolePermissionRepository jdbcRolePermissionRepository)
  {
    this.jdbcRoleRepository = jdbcRoleRepository;
    this.jdbcRolePermissionRepository = jdbcRolePermissionRepository;
  }

  @Override
  public Role create(TenantId tenantId, String id, String name, Collection<Permission> permissions) throws AimRepositoryException
  {
    try
    {
      Validate.notNull(tenantId, "Tenant Id should not be null!");
      Validate.notBlank(id, "Id should not be null!");
      Validate.notNull(permissions, "Permissions should not be null!");

      jdbcRoleRepository.insert(id, tenantId.getId(), name);

      Role roleToReturn = new Role(new RoleId(id), tenantId, name);

      for (Permission permission: permissions)
      {
        jdbcRolePermissionRepository.insert(id, permission.getPermissionString());
        roleToReturn.addPermission(permission);
      }

      return roleToReturn;
    }
    catch (NullPointerException | IllegalArgumentException | DbActionExecutionException e)
    {
      throw new AimRepositoryException(e.getMessage(), e);
    }
  }

  @Override
  public Role create(TenantId tenantId, String id, String name) throws AimRepositoryException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Role> listAll(TenantId tenantId) throws AimRepositoryException
  {
    try
    {
      Validate.notNull(tenantId, "Tenant Id is required!");
      List<Role> rolesToReturn = new ArrayList<>();

      List<JdbcRole> returnedJdbcRoles = jdbcRoleRepository.findJdbcRolesByTenantId(tenantId.getId());

      Iterator<JdbcRole> returnedJdbcRolesIterator = returnedJdbcRoles.iterator();
      while (returnedJdbcRolesIterator.hasNext())
      {
        JdbcRole currentJdbcRole = returnedJdbcRolesIterator.next();
        String currentRoleId = currentJdbcRole.getRoleId();
        List<JdbcRolePermissionJoined> jdbcRolePermissionJoinedList = jdbcRoleRepository.getJoinedRolePermissionByRoleId(currentRoleId);
        if (jdbcRolePermissionJoinedList.size() == 0)
        {
          rolesToReturn.add(convertToRoleFromJdbcRole(currentJdbcRole));
        }
        else
        {
          rolesToReturn.add(convertToRoleFromJdbcRolePermissionJoined(jdbcRolePermissionJoinedList));
        }
      }
      return rolesToReturn;
    }
    catch (NullPointerException | DbActionExecutionException e)
    {
      throw new AimRepositoryException(e.getMessage(), e);
    }

  }

  @Override
  public Role findById(EntityId entityId)
  {
    Validate.notNull(entityId.getId(), "Entity is required!");

    Optional<JdbcRole> jdbcRole = jdbcRoleRepository.findById(entityId.getId());

    if (jdbcRole.isPresent())
    {
      List<JdbcRolePermissionJoined> jdbcRolePermissionJoinedList = jdbcRoleRepository.getJoinedRolePermissionByRoleId(entityId.getId());
      if (jdbcRolePermissionJoinedList.isEmpty())
      {
        return convertToRoleFromJdbcRole(jdbcRole.get());
      }
      else
      {
        return convertToRoleFromJdbcRolePermissionJoined(jdbcRolePermissionJoinedList);
      }
    }
    return null;
  }

  @Override
  public Collection<Role> findAll()
  {
    List<Role> rolesToReturn = new ArrayList<>();

    Iterator<JdbcRole> returnedJdbcRolesIterator = jdbcRoleRepository.findAll().iterator();
    while (returnedJdbcRolesIterator.hasNext())
    {
      JdbcRole currentJdbcRole = returnedJdbcRolesIterator.next();
      String currentRoleId = currentJdbcRole.getRoleId();
      List<JdbcRolePermissionJoined> jdbcRolePermissionJoinedList = jdbcRoleRepository.getJoinedRolePermissionByRoleId(currentRoleId);
      if (jdbcRolePermissionJoinedList.size() == 0)
      {
        rolesToReturn.add(convertToRoleFromJdbcRole(currentJdbcRole));
      }
      else
      {
        rolesToReturn.add(convertToRoleFromJdbcRolePermissionJoined(jdbcRolePermissionJoinedList));
      }
    }
    return rolesToReturn;
  }

  private Role convertToRoleFromJdbcRolePermissionJoined(List<JdbcRolePermissionJoined> jdbcRolePermissionJoinedList)
  {
    JdbcRolePermissionJoined firstJdbcRolePermissionJoined = jdbcRolePermissionJoinedList.get(0);
    RoleId roleId = new RoleId(firstJdbcRolePermissionJoined.getRoleId());
    TenantId tenantId = new TenantId(firstJdbcRolePermissionJoined.getTenantId());
    String name = firstJdbcRolePermissionJoined.getName();
    Role roleToReturn = new Role(roleId, tenantId, name);
    Iterator<JdbcRolePermissionJoined> jdbcRolePermissionJoinedListIterator = jdbcRolePermissionJoinedList.iterator();

    while (jdbcRolePermissionJoinedListIterator.hasNext())
    {
      roleToReturn.addPermission(Permission.valueOf(jdbcRolePermissionJoinedListIterator.next().getPermissionId()));
    }

    return roleToReturn;
  }

  private Role convertToRoleFromJdbcRole(JdbcRole jdbcRole)
  {
    RoleId roleId = new RoleId(jdbcRole.getRoleId());
    TenantId tenantId = new TenantId(jdbcRole.getTenantId());
    String name = jdbcRole.getName();
    Role roleToReturn = new Role(roleId, tenantId, name);

    return roleToReturn;
  }
}
