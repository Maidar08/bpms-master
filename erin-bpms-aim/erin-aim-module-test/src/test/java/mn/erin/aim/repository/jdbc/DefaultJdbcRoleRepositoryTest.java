package mn.erin.aim.repository.jdbc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import mn.erin.aim.repository.jdbc.interfaces.JdbcRolePermissionRepository;
import mn.erin.aim.repository.jdbc.interfaces.JdbcRoleRepository;
import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.role.Role;
import mn.erin.domain.aim.model.role.RoleId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.repository.RoleRepository;

/**
 * Tests for {@link DefaultJdbcRoleRepository} which runs on local Oracle database. Please configure your test Oracle database according to
 * jdbc-datasource-test.properties
 * @author Zorig
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestAimJdbcBeanConfig.class })
@Ignore //Activate this on Teamcity
@Transactional
public class DefaultJdbcRoleRepositoryTest
{
  @Inject
  private RoleRepository roleRepository;

  @Inject
  private JdbcRoleRepository jdbcRoleRepository;

  @Inject
  private JdbcRolePermissionRepository jdbcRolePermissionRepository;

  @Before
  public void cleanup()
  {
    // clean up database before each test run
    // so each test runs on a blank database
    jdbcRoleRepository.deleteAll();
    jdbcRolePermissionRepository.deleteAll();
  }

  @Test(expected = AimRepositoryException.class)
  public void createRoleThrowsExceptionWhenTenantIdIsNull() throws AimRepositoryException
  {
    roleRepository.create(null, "name", "description", new ArrayList<>());
  }

  @Test(expected = AimRepositoryException.class)
  public void createRoleThrowsExceptionWhenNameIsBlank() throws AimRepositoryException
  {
    roleRepository.create(new TenantId("Erin"), "", "description", new ArrayList<>());
  }

  @Test(expected = AimRepositoryException.class)
  public void createRoleThrowsExceptionWhenPermissionsListIsNull() throws AimRepositoryException
  {
    roleRepository.create(new TenantId("Erin"), "name", "description", null);
  }

  @Test(expected = AimRepositoryException.class)
  public void listAllThrowsExceptionWhenTenantIdIsNull() throws AimRepositoryException
  {
    roleRepository.listAll(null);
  }

  @Test(expected = NullPointerException.class)
  public void findByIdThrowsExceptionWhenEntityIdIsNull()
  {
    roleRepository.findById(null);
  }

  @Test
  public void createRoleWithNoPermission() throws AimRepositoryException
  {
    Role createdRole = roleRepository.create(new TenantId("Erin"), "Developer", null, new ArrayList<>());
    Role createdRoleSearchResult = roleRepository.findById(createdRole.getRoleId());

    Assert.assertNotNull(createdRole);
    Assert.assertNotNull(createdRoleSearchResult);
    Assert.assertTrue(createdRole.getPermissions().isEmpty() && createdRoleSearchResult.getPermissions().isEmpty());
    Assert.assertEquals(createdRole.getRoleId(), createdRoleSearchResult.getRoleId());
    Assert.assertEquals(createdRole.getDescription(), createdRoleSearchResult.getDescription(), null);
    Assert.assertEquals(createdRole.getName(), createdRoleSearchResult.getName(), "Developer");
    Assert.assertEquals(createdRole.getTenantId().getId(), createdRoleSearchResult.getTenantId().getId(), "Erin");
  }

  @Test
  public void createRoleWithMultiplePermissions() throws AimRepositoryException
  {
    List<Permission> permissionList = new ArrayList<>();
    permissionList.add(new Permission("bpms", "bpm", "GetGroupProcessRequests"));
    permissionList.add(new Permission("bpms", "bpm", "CreateProcessRequest"));
    Role createdRole = roleRepository.create(new TenantId("Erin"), "Developer", null, permissionList);
    Role createdRoleSearchResult = roleRepository.findById(createdRole.getRoleId());

    Collection<Permission> createdRolePermissionList = createdRole.getPermissions();
    Collection<Permission> createdRoleSearchResultPermissionList = createdRoleSearchResult.getPermissions();

    Assert.assertNotNull(createdRole);
    Assert.assertNotNull(createdRoleSearchResult);
    Assert.assertEquals(createdRole.getRoleId(), createdRoleSearchResult.getRoleId());
    Assert.assertEquals(createdRole.getDescription(), createdRoleSearchResult.getDescription());
    Assert.assertEquals(createdRole.getName(), createdRoleSearchResult.getName());
    Assert.assertEquals(createdRole.getTenantId(), createdRoleSearchResult.getTenantId());

    Assert.assertEquals(createdRolePermissionList.size(), createdRoleSearchResultPermissionList.size(), permissionList.size());

    Iterator<Permission> permissionListIterator = permissionList.iterator();
    Iterator<Permission> createdRolePermissionListIterator = createdRolePermissionList.iterator();
    Iterator<Permission> createdRoleSearchResultPermissionListIterator = createdRoleSearchResultPermissionList.iterator();
    while (permissionListIterator.hasNext())
    {
      Assert.assertEquals(permissionListIterator.next().getPermissionString(), createdRolePermissionListIterator.next().getPermissionString(), createdRoleSearchResultPermissionListIterator.next().getPermissionString());
    }
  }

  @Test
  public void listAllWhenEmptyResultForFindByTenantIdQuery() throws AimRepositoryException
  {
    List<Role> returnedRoleList = roleRepository.listAll(new TenantId("Erin"));

    Assert.assertTrue(returnedRoleList.isEmpty());
  }

  @Test
  public void listAllWhenMultipleRolesAreReturned() throws AimRepositoryException
  {
    List<Permission> permissionList = new ArrayList<>();
    permissionList.add(new Permission("bpms", "bpm", "GetGroupProcessRequests"));
    permissionList.add(new Permission("bpms", "bpm", "CreateProcessRequest"));
    Role createdRole = roleRepository.create(new TenantId("Erin"), "Developer", null, permissionList);
    Role createdRole2 = roleRepository.create(new TenantId("Erin"), "Manager", null, new ArrayList<>());

    List<Role> allRoles = roleRepository.listAll(new TenantId("Erin"));
    Assert.assertEquals(2, allRoles.size());
    Role role1 = allRoles.get(0);
    Role role2 = allRoles.get(1);

    Assert.assertTrue(role2.getPermissions().isEmpty());
    Assert.assertEquals(createdRole2.getRoleId(), role2.getRoleId());
    Assert.assertEquals(createdRole2.getDescription(), role2.getDescription());
    Assert.assertEquals(createdRole2.getName(), role2.getName());
    Assert.assertEquals(createdRole2.getTenantId(), role2.getTenantId());

    Collection<Permission> role1PermissionList = role1.getPermissions();

    Assert.assertEquals(role1.getRoleId(), createdRole.getRoleId());
    Assert.assertEquals(role1.getDescription(), createdRole.getDescription());
    Assert.assertEquals(role1.getName(), createdRole.getName());
    Assert.assertEquals(role1.getTenantId(), createdRole.getTenantId());

    Assert.assertEquals(2, role1PermissionList.size());

    Iterator<Permission> permissionListIterator = role1PermissionList.iterator();
    Iterator<Permission> createdRolePermissionListIterator = createdRole.getPermissions().iterator();
    while (permissionListIterator.hasNext())
    {
      Assert.assertEquals(permissionListIterator.next().getPermissionString(), createdRolePermissionListIterator.next().getPermissionString());
    }
  }

  @Test
  public void findByIdReturnNull()
  {
    Role returnedRole = roleRepository.findById(new RoleId("RoleId"));

    Assert.assertNull(returnedRole);
  }

  @Test public void findByIdReturnsRoleWithPermissions() throws AimRepositoryException
  {
    List<Permission> permissionList = new ArrayList<>();
    permissionList.add(new Permission("bpms", "bpm", "GetGroupProcessRequests"));
    permissionList.add(new Permission("bpms", "bpm", "CreateProcessRequest"));
    Role createdRole = roleRepository.create(new TenantId("Erin"), "Developer", null, permissionList);

    Role returnedRole = roleRepository.findById(new RoleId(createdRole.getRoleId().getId()));
    Collection<Permission> returnedRolePermissions = returnedRole.getPermissions();

    Assert.assertEquals(createdRole.getRoleId(), returnedRole.getRoleId());
    Assert.assertEquals(createdRole.getDescription(), returnedRole.getDescription());
    Assert.assertEquals(createdRole.getName(), returnedRole.getName());
    Assert.assertEquals(createdRole.getTenantId(), returnedRole.getTenantId());

    Assert.assertEquals(2, returnedRolePermissions.size());

    Iterator<Permission> returnedRolePermissionsListIterator = returnedRolePermissions.iterator();
    Iterator<Permission> createdRolePermissionsListIterator = createdRole.getPermissions().iterator();
    while (returnedRolePermissionsListIterator.hasNext())
    {
      Assert.assertEquals(returnedRolePermissionsListIterator.next().getPermissionString(), createdRolePermissionsListIterator.next().getPermissionString());
    }
  }

  @Test
  public void findByIdReturnsRoleWithNoPermission() throws AimRepositoryException
  {
    Role createdRole = roleRepository.create(new TenantId("Erin"), "Developer", null, new ArrayList<>());
    Role returnedRole = roleRepository.findById(new RoleId(createdRole.getRoleId().getId()));

    Assert.assertEquals(createdRole.getRoleId(), returnedRole.getRoleId());
    Assert.assertEquals(createdRole.getDescription(), returnedRole.getDescription());
    Assert.assertEquals(createdRole.getName(), returnedRole.getName());
    Assert.assertEquals(createdRole.getTenantId(), returnedRole.getTenantId());

    Assert.assertTrue(returnedRole.getPermissions().isEmpty());
  }

  @Test
  public void findAllWhenFindAllQueryReturnsEmpty()
  {
    Collection<Role> returnedRolesList = roleRepository.findAll();
    Assert.assertTrue(returnedRolesList.isEmpty());
  }

  @Test
  public void findAllWithMultipleRoles() throws AimRepositoryException
  {
    List<Permission> permissionList = new ArrayList<>();
    permissionList.add(new Permission("bpms", "bpm", "GetGroupProcessRequests"));
    permissionList.add(new Permission("bpms", "bpm", "CreateProcessRequest"));
    Role createdRole = roleRepository.create(new TenantId("Erin"), "Developer", null, permissionList);
    Role createdRole2 = roleRepository.create(new TenantId("Erin"), "Manager", null, new ArrayList<>());

    Collection<Role> allRoles = roleRepository.findAll();
    Assert.assertEquals(2, allRoles.size());
    Iterator<Role> allRolesIterator = allRoles.iterator();
    Role role1 = allRolesIterator.next();
    Role role2 = allRolesIterator.next();

    Assert.assertTrue(role2.getPermissions().isEmpty());
    Assert.assertEquals(createdRole2.getRoleId(), role2.getRoleId());
    Assert.assertEquals(createdRole2.getDescription(), role2.getDescription());
    Assert.assertEquals(createdRole2.getName(), role2.getName());
    Assert.assertEquals(createdRole2.getTenantId(), role2.getTenantId());

    Collection<Permission> role1PermissionList = role1.getPermissions();

    Assert.assertEquals(role1.getRoleId(), createdRole.getRoleId());
    Assert.assertEquals(role1.getDescription(), createdRole.getDescription());
    Assert.assertEquals(role1.getName(), createdRole.getName());
    Assert.assertEquals(role1.getTenantId(), createdRole.getTenantId());

    Assert.assertEquals(2, role1PermissionList.size());

    Iterator<Permission> permissionListIterator = role1PermissionList.iterator();
    Iterator<Permission> createdRolePermissionListIterator = createdRole.getPermissions().iterator();
    while (permissionListIterator.hasNext())
    {
      Assert.assertEquals(permissionListIterator.next().getPermissionString(), createdRolePermissionListIterator.next().getPermissionString());
    }
  }
}
