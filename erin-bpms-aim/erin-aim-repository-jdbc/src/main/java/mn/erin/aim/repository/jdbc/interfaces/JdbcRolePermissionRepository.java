package mn.erin.aim.repository.jdbc.interfaces;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import mn.erin.aim.repository.jdbc.model.JdbcRolePermission;

/**
 * @author Zorig
 */
public interface JdbcRolePermissionRepository extends CrudRepository<JdbcRolePermission, String>
{
  @Modifying
  @Query(value = "INSERT INTO AIM_ROLE_PERMISSION(ROLE_ID, PERMISSION_ID) VALUES(:roleId, :permission)")
  int insert(@Param("roleId") String roleId, @Param("permission") String permission);
}
