package mn.erin.domain.aim.repository;

import java.util.Collection;
import java.util.List;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.role.Role;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.base.repository.Repository;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface RoleRepository extends Repository<Role>
{
  /**
   * Creates a new role
   *
   * @param tenantId    The ID of the tenant
   * @param id        The id of the role
   * @param name The name of the role
   * @param permissions The permission of the role
   * @return A new role object
   */
  Role create(TenantId tenantId, String id, String name, Collection<Permission> permissions) throws AimRepositoryException;

  /**
   * Creates a new role
   *
   * @param tenantId    The ID of the tenant
   * @param id        The id of the role
   * @param name The name of the role
   * @return A new role object
   */
  Role create(TenantId tenantId, String id, String name) throws AimRepositoryException;

  /**
   * Lists all role of the specified tenant
   *
   * @param tenantId The ID of the tenant whose roles will be returned
   * @return A list of all roles
   */
  List<Role> listAll(TenantId tenantId) throws AimRepositoryException;
}
