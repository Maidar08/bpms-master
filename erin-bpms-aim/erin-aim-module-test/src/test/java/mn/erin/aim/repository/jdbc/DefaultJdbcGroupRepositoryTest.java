package mn.erin.aim.repository.jdbc;

import java.util.Collection;
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

import mn.erin.aim.repository.jdbc.interfaces.JdbcGroupParentChildRepository;
import mn.erin.aim.repository.jdbc.interfaces.JdbcGroupRepository;
import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.repository.GroupRepository;

/**
 * Tests for {@link DefaultJdbcGroupRepository} which runs on local Oracle database. Please configure your test Oracle database according to
 * jdbc-datasource-test.properties
 *
 * @author EBazarragchaa
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestAimJdbcBeanConfig.class })
@Ignore //Activate this on Teamcity
@Transactional
public class DefaultJdbcGroupRepositoryTest
{
  private static final TenantId TENANT_ID = TenantId.valueOf("erin");
  private static final String ROOT_GROUP_NUMBER = "Root";

  @Inject
  private GroupRepository groupRepository;

  @Inject
  private JdbcGroupRepository jdbcGroupRepository;

  @Inject
  private JdbcGroupParentChildRepository jdbcGroupParentChildRepository;

  @Before
  public void cleanup()
  {
    // clean up database before each test run
    // so each test runs on a blank database
    jdbcGroupParentChildRepository.deleteAll();
    jdbcGroupRepository.deleteAll();
  }

  @Test(expected = AimRepositoryException.class)
  public void createGroupThrowsExceptionWhenTenantIdIsNull() throws AimRepositoryException
  {
    groupRepository.createGroup("1", "Name", null, null);
  }

  @Test(expected = AimRepositoryException.class)
  public void createGroupThrowsExceptionWhenNameIsBlank() throws AimRepositoryException
  {
    groupRepository.createGroup("1", "", null, new TenantId("Erin"));
  }

  @Test(expected = AimRepositoryException.class)
  public void createGroupThrowsExceptionWhenNumberIsBlank() throws AimRepositoryException
  {
    groupRepository.createGroup("", "Name", null, new TenantId("Erin"));
  }

  @Test
  public void createGroupWithNullParent() throws AimRepositoryException
  {
    Group rootGroup = groupRepository.createGroup(ROOT_GROUP_NUMBER, "ERIN Systems LLC", null, TENANT_ID);
    Assert.assertNotNull(rootGroup);
    Assert.assertNotNull(rootGroup.getId());
    Assert.assertTrue(rootGroup.getChildren().isEmpty());
    Assert.assertEquals(ROOT_GROUP_NUMBER, rootGroup.getId().getId());
    Assert.assertEquals(0, rootGroup.getNthSibling());
  }

  @Test(expected = AimRepositoryException.class)
  public void createGroupThrowsExceptionDuplicate() throws AimRepositoryException
  {
    groupRepository.createGroup(ROOT_GROUP_NUMBER, "ERIN Systems LLC", null, TENANT_ID);
    groupRepository.createGroup(ROOT_GROUP_NUMBER, "ERIN Systems LLC", null, TENANT_ID);
  }

  @Test
  public void createChildGroup() throws AimRepositoryException
  {
    groupRepository.createGroup(ROOT_GROUP_NUMBER, "ERIN Systems LLC", null, TENANT_ID);
    Group childGroup1 = groupRepository.createGroup("P&T", "ERIN Systems LLC", ROOT_GROUP_NUMBER, TENANT_ID);
    Group childGroup2 = groupRepository.createGroup("A&O", "ERIN Systems LLC", ROOT_GROUP_NUMBER, TENANT_ID);

    // get updated root group
    Group rootGroup = groupRepository.findByNumberAndTenantId(ROOT_GROUP_NUMBER, TENANT_ID);

    Assert.assertEquals(2, rootGroup.getChildren().size());
    Assert.assertEquals(rootGroup.getId(), childGroup1.getParent());
    Assert.assertEquals("P&T", childGroup1.getId().getId());
    Assert.assertEquals(1, childGroup1.getNthSibling());

    Assert.assertEquals(rootGroup.getId(), childGroup2.getParent());
    Assert.assertEquals("A&O", childGroup2.getId().getId());
    Assert.assertEquals(2, childGroup2.getNthSibling());
  }

  //add validation tests

  @Test(expected = AimRepositoryException.class)
  public void doesGroupExistThrowsExceptionWhenIdIsBlank() throws AimRepositoryException
  {
    groupRepository.doesGroupExist(null);
  }

  @Test
  public void doesGroupExistNoExistence() throws AimRepositoryException
  {
    //db is cleared before every test
    //in this case, the db is empty
    Assert.assertFalse(groupRepository.doesGroupExist(ROOT_GROUP_NUMBER));
  }

  @Test
  public void doesGroupExist() throws AimRepositoryException
  {
    groupRepository.createGroup(ROOT_GROUP_NUMBER, "ERIN Systems LLC", null, TENANT_ID);
    Assert.assertTrue(groupRepository.doesGroupExist(ROOT_GROUP_NUMBER));
  }

  @Test(expected = AimRepositoryException.class)
  public void getNthSiblingThrowsExceptionWhenParentIdDoesntExist() throws AimRepositoryException
  {
    groupRepository.getNthSibling("root");
  }

  @Test
  public void getNthSiblingForNullParentId() throws AimRepositoryException
  {
    int nthSibling = groupRepository.getNthSibling(null);

    Assert.assertEquals(0, nthSibling);
  }

  @Test
  public void getNthSiblingNonNullParentId() throws AimRepositoryException
  {
    groupRepository.createGroup(ROOT_GROUP_NUMBER, "ERIN Systems LLC", null, TENANT_ID);

    int nthSibling = groupRepository.getNthSibling(ROOT_GROUP_NUMBER);

    Assert.assertEquals(1, nthSibling);
  }

  @Test(expected = AimRepositoryException.class)
  public void getAllRootGroupsThrowsExceptionWhenTenantIdIsNull() throws AimRepositoryException
  {
    groupRepository.getAllRootGroups(null);
  }

  @Test
  public void getAllRootGroupsEmptyResultForQuery() throws AimRepositoryException
  {
    List<Group> returnedGroupList = groupRepository.getAllRootGroups(TENANT_ID);
    Assert.assertTrue(returnedGroupList.isEmpty());
  }

  @Test
  public void getAllRootGroupsNonEmptyResultForQuery() throws AimRepositoryException
  {
    groupRepository.createGroup(ROOT_GROUP_NUMBER, "ERIN Systems LLC", null, TENANT_ID);
    groupRepository.createGroup("1", "ERIN Systems LLC", null, TENANT_ID);

    List<Group> returnedGroupList = groupRepository.getAllRootGroups(TENANT_ID);

    Assert.assertEquals(2, returnedGroupList.size());
    Assert.assertEquals(TENANT_ID, returnedGroupList.get(0).getTenantId());
    Assert.assertEquals(TENANT_ID, returnedGroupList.get(1).getTenantId());
    Assert.assertNull(returnedGroupList.get(0).getParent());
    Assert.assertNull(returnedGroupList.get(1).getParent());

  }

  @Test(expected = AimRepositoryException.class)
  public void findByNumberAndTenantIdThrowsExceptionWhenIdIsBlank() throws AimRepositoryException
  {
    groupRepository.findByNumberAndTenantId("", new TenantId("Erin"));
  }

  @Test(expected = AimRepositoryException.class)
  public void findByNumberAndTenantIdThrowsExceptionWhenTenantIdIsNull() throws AimRepositoryException
  {
    groupRepository.findByNumberAndTenantId("1", null);
  }

  @Test
  public void findByNumberAndTenantIdReturnsGroup() throws AimRepositoryException
  {
    Group createdGroup = groupRepository.createGroup(ROOT_GROUP_NUMBER, "ERIN Systems LLC", null, TENANT_ID);
    Assert.assertNotNull(createdGroup);

    Group foundGroup = groupRepository.findByNumberAndTenantId(ROOT_GROUP_NUMBER, TENANT_ID);
    Assert.assertNotNull(foundGroup);
    Assert.assertEquals(ROOT_GROUP_NUMBER, foundGroup.getId().getId());
    Assert.assertEquals(ROOT_GROUP_NUMBER, foundGroup.getNumber());
  }

  @Test
  public void findByNumberAndTenantIdReturnsNull() throws AimRepositoryException
  {
    Group foundGroup = groupRepository.findByNumberAndTenantId(ROOT_GROUP_NUMBER, TENANT_ID);
    Assert.assertNull(foundGroup);
  }

  @Test(expected = NullPointerException.class)
  public void findByIdThrowsExceptionWhenEntityIdIsnull()
  {
    groupRepository.findById(null);
  }

  @Test
  public void findByIdNonExistent()
  {
    Group returnedGroup = groupRepository.findById(new GroupId("1"));
    Assert.assertNull(returnedGroup);
  }

  @Test
  public void findById() throws AimRepositoryException
  {
    groupRepository.createGroup(ROOT_GROUP_NUMBER, "ERIN Systems LLC", null, TENANT_ID);
    Group returnedGroup = groupRepository.findById(new GroupId(ROOT_GROUP_NUMBER));

    Assert.assertNotNull(returnedGroup);
    Assert.assertEquals(ROOT_GROUP_NUMBER, returnedGroup.getId().getId());
    Assert.assertEquals("ERIN Systems LLC", returnedGroup.getName());
    Assert.assertEquals(null, returnedGroup.getParent());
    Assert.assertEquals(TENANT_ID, returnedGroup.getTenantId());
    Assert.assertTrue(returnedGroup.getChildren().isEmpty());
  }

  @Test
  public void findByIdWithChildren() throws AimRepositoryException
  {
    groupRepository.createGroup(ROOT_GROUP_NUMBER, "ERIN Systems LLC", null, TENANT_ID);
    groupRepository.createGroup("ANOTHER GROUP1", "ERIN Systems LLC", ROOT_GROUP_NUMBER, TENANT_ID);
    groupRepository.createGroup("ANOTHER GROUP2", "ERIN Systems LLC", ROOT_GROUP_NUMBER, TENANT_ID);
    Group returnedGroup = groupRepository.findById(new GroupId(ROOT_GROUP_NUMBER));

    Assert.assertNotNull(returnedGroup);
    Assert.assertEquals(ROOT_GROUP_NUMBER, returnedGroup.getId().getId());
    Assert.assertEquals("ERIN Systems LLC", returnedGroup.getName());
    Assert.assertEquals(null, returnedGroup.getParent());
    Assert.assertEquals(TENANT_ID, returnedGroup.getTenantId());
    Assert.assertFalse(returnedGroup.getChildren().isEmpty());
    Assert.assertEquals(2, returnedGroup.getChildren().size());

    Assert.assertEquals("ANOTHER GROUP1", returnedGroup.getChildren().get(0).getId());
    Assert.assertEquals("ANOTHER GROUP2", returnedGroup.getChildren().get(1).getId());

  }

  @Test
  public void findAllEmptyResultForQuery()
  {
    Collection<Group> returnedGroupsList = groupRepository.findAll();
    Assert.assertTrue(returnedGroupsList.isEmpty());
  }

  @Test
  public void findAllNonEmptyResultForQuery() throws AimRepositoryException
  {
    groupRepository.createGroup(ROOT_GROUP_NUMBER, "ERIN Systems LLC", null, TENANT_ID);
    groupRepository.createGroup("ANOTHER_ROOT", "ERIN Systems LLC", null, TENANT_ID);
    Collection<Group> returnedGroupsList = groupRepository.findAll();

    Assert.assertEquals(2, returnedGroupsList.size());
  }

  @Test(expected = AimRepositoryException.class)
  public void deleteGroupReturnsThrowsExceptionWhenGroupIsBlank() throws AimRepositoryException
  {
    groupRepository.deleteGroup("");
  }

  @Test
  public void deleteGroupReturnsFalseWhenNonExistent() throws AimRepositoryException
  {
    boolean result = groupRepository.deleteGroup("123");

    Assert.assertFalse(result);
  }

  @Test
  public void deleteGroupWhenGroupIsRoot() throws AimRepositoryException
  {
    groupRepository.createGroup(ROOT_GROUP_NUMBER, "ERIN Systems LLC", null, TENANT_ID);
    boolean result = groupRepository.deleteGroup(ROOT_GROUP_NUMBER);

    Assert.assertTrue(result);
    Assert.assertFalse(groupRepository.doesGroupExist(ROOT_GROUP_NUMBER));
  }

  @Test
  public void deleteGroupWhenGroupIsRootAndHasChildren() throws AimRepositoryException
  {
    groupRepository.createGroup(ROOT_GROUP_NUMBER, "ERIN Systems LLC", null, TENANT_ID);
    groupRepository.createGroup("ANOTHER_GROUP1", "ERIN Systems LLC", ROOT_GROUP_NUMBER, TENANT_ID);
    groupRepository.createGroup("ANOTHER_GROUP2", "ERIN Systems LLC", ROOT_GROUP_NUMBER, TENANT_ID);
    groupRepository.createGroup("ANOTHER_GROUP3", "ERIN Systems LLC", ROOT_GROUP_NUMBER, TENANT_ID);
    groupRepository.createGroup("ANOTHER_GROUP4", "ERIN Systems LLC", "ANOTHER_GROUP1", TENANT_ID);
    groupRepository.createGroup("ANOTHER_GROUP5", "ERIN Systems LLC", "ANOTHER_GROUP4", TENANT_ID);

    boolean result = groupRepository.deleteGroup(ROOT_GROUP_NUMBER);

    Assert.assertTrue(result);
    Assert.assertFalse(groupRepository.doesGroupExist(ROOT_GROUP_NUMBER));
    Assert.assertFalse(groupRepository.doesGroupExist("ANOTHER_GROUP1"));
    Assert.assertFalse(groupRepository.doesGroupExist("ANOTHER_GROUP2"));
    Assert.assertFalse(groupRepository.doesGroupExist("ANOTHER_GROUP3"));
    Assert.assertFalse(groupRepository.doesGroupExist("ANOTHER_GROUP4"));
    Assert.assertFalse(groupRepository.doesGroupExist("ANOTHER_GROUP5"));
  }

  @Test
  public void deleteGroupWhenGroupHasParentAndNoSiblingsAndChildren() throws AimRepositoryException
  {
    groupRepository.createGroup(ROOT_GROUP_NUMBER, "ERIN Systems LLC", null, TENANT_ID);
    groupRepository.createGroup("ANOTHER_GROUP1", "ERIN Systems LLC", ROOT_GROUP_NUMBER, TENANT_ID);
    groupRepository.createGroup("ANOTHER_GROUP2", "ERIN Systems LLC", "ANOTHER_GROUP1", TENANT_ID);
    groupRepository.createGroup("ANOTHER_GROUP3", "ERIN Systems LLC", "ANOTHER_GROUP1", TENANT_ID);
    groupRepository.createGroup("ANOTHER_GROUP4", "ERIN Systems LLC", "ANOTHER_GROUP1", TENANT_ID);
    groupRepository.createGroup("ANOTHER_GROUP5", "ERIN Systems LLC", "ANOTHER_GROUP2", TENANT_ID);

    boolean result = groupRepository.deleteGroup("ANOTHER_GROUP1");

    Assert.assertTrue(result);
    Assert.assertTrue(groupRepository.doesGroupExist(ROOT_GROUP_NUMBER));
    Assert.assertFalse(groupRepository.doesGroupExist("ANOTHER_GROUP1"));
    Assert.assertFalse(groupRepository.doesGroupExist("ANOTHER_GROUP2"));
    Assert.assertFalse(groupRepository.doesGroupExist("ANOTHER_GROUP3"));
    Assert.assertFalse(groupRepository.doesGroupExist("ANOTHER_GROUP4"));
    Assert.assertFalse(groupRepository.doesGroupExist("ANOTHER_GROUP5"));
  }

  @Test
  public void deleteGroupWhenGroupHasParentAndSiblingsAndChildren() throws AimRepositoryException
  {
    groupRepository.createGroup(ROOT_GROUP_NUMBER, "ERIN Systems LLC", null, TENANT_ID);
    groupRepository.createGroup("ANOTHER_GROUP1", "ERIN Systems LLC", ROOT_GROUP_NUMBER, TENANT_ID);
    groupRepository.createGroup("ANOTHER_GROUP6", "ERIN Systems LLC", ROOT_GROUP_NUMBER, TENANT_ID);
    groupRepository.createGroup("ANOTHER_GROUP7", "ERIN Systems LLC", ROOT_GROUP_NUMBER, TENANT_ID);
    groupRepository.createGroup("ANOTHER_GROUP8", "ERIN Systems LLC", ROOT_GROUP_NUMBER, TENANT_ID);
    groupRepository.createGroup("ANOTHER_GROUP9", "ERIN Systems LLC", ROOT_GROUP_NUMBER, TENANT_ID);
    groupRepository.createGroup("ANOTHER_GROUP10", "ERIN Systems LLC", ROOT_GROUP_NUMBER, TENANT_ID);
    groupRepository.createGroup("ANOTHER_GROUP2", "ERIN Systems LLC", "ANOTHER_GROUP7", TENANT_ID);
    groupRepository.createGroup("ANOTHER_GROUP3", "ERIN Systems LLC", "ANOTHER_GROUP7", TENANT_ID);
    groupRepository.createGroup("ANOTHER_GROUP4", "ERIN Systems LLC", "ANOTHER_GROUP7", TENANT_ID);
    groupRepository.createGroup("ANOTHER_GROUP5", "ERIN Systems LLC", "ANOTHER_GROUP2", TENANT_ID);

    boolean result = groupRepository.deleteGroup("ANOTHER_GROUP7");

    Assert.assertTrue(result);
    Assert.assertTrue(groupRepository.doesGroupExist(ROOT_GROUP_NUMBER));
    Assert.assertTrue(groupRepository.doesGroupExist("ANOTHER_GROUP1"));
    Assert.assertTrue(groupRepository.doesGroupExist("ANOTHER_GROUP6"));
    Assert.assertTrue(groupRepository.doesGroupExist("ANOTHER_GROUP8"));
    Assert.assertTrue(groupRepository.doesGroupExist("ANOTHER_GROUP9"));
    Assert.assertTrue(groupRepository.doesGroupExist("ANOTHER_GROUP10"));

    Assert.assertFalse(groupRepository.doesGroupExist("ANOTHER_GROUP7"));
    Assert.assertFalse(groupRepository.doesGroupExist("ANOTHER_GROUP2"));
    Assert.assertFalse(groupRepository.doesGroupExist("ANOTHER_GROUP3"));
    Assert.assertFalse(groupRepository.doesGroupExist("ANOTHER_GROUP4"));
    Assert.assertFalse(groupRepository.doesGroupExist("ANOTHER_GROUP5"));

    //check child has been deleted from parent
    Group returnedGroup = groupRepository.findById(new GroupId(ROOT_GROUP_NUMBER));
    Assert.assertEquals(5, returnedGroup.getChildren().size());
    Assert.assertFalse(returnedGroup.getChildren().contains(new GroupId("ANOTHER_GROUP7")));

    //check nthSibling of each child
    Assert.assertEquals(1, groupRepository.findById(new GroupId("ANOTHER_GROUP1")).getNthSibling());
    Assert.assertEquals(2, groupRepository.findById(new GroupId("ANOTHER_GROUP6")).getNthSibling());
    Assert.assertEquals(3, groupRepository.findById(new GroupId("ANOTHER_GROUP8")).getNthSibling());
    Assert.assertEquals(4, groupRepository.findById(new GroupId("ANOTHER_GROUP9")).getNthSibling());
    Assert.assertEquals(5, groupRepository.findById(new GroupId("ANOTHER_GROUP10")).getNthSibling());

  }

  @Test
  public void deleteGroupWhenGroupIsLeafNoSiblings() throws AimRepositoryException
  {
    groupRepository.createGroup(ROOT_GROUP_NUMBER, "ERIN Systems LLC", null, TENANT_ID);
    groupRepository.createGroup("ANOTHER_GROUP1", "ERIN Systems LLC", ROOT_GROUP_NUMBER, TENANT_ID);
    groupRepository.createGroup("ANOTHER_GROUP2", "ERIN Systems LLC", "ANOTHER_GROUP1", TENANT_ID);

    boolean result = groupRepository.deleteGroup("ANOTHER_GROUP2");

    Assert.assertTrue(result);
    Assert.assertTrue(groupRepository.doesGroupExist(ROOT_GROUP_NUMBER));
    Assert.assertTrue(groupRepository.doesGroupExist("ANOTHER_GROUP1"));
    Assert.assertFalse(groupRepository.doesGroupExist("ANOTHER_GROUP2"));

    Assert.assertEquals(0, groupRepository.findById(new GroupId("ANOTHER_GROUP1")).getChildren().size());
  }

  @Test
  public void deleteGroupWhenGroupIsLeafAndHasSibling() throws AimRepositoryException
  {
    groupRepository.createGroup(ROOT_GROUP_NUMBER, "ERIN Systems LLC", null, TENANT_ID);
    groupRepository.createGroup("ANOTHER_GROUP1", "ERIN Systems LLC", ROOT_GROUP_NUMBER, TENANT_ID);
    groupRepository.createGroup("ANOTHER_GROUP6", "ERIN Systems LLC", ROOT_GROUP_NUMBER, TENANT_ID);
    groupRepository.createGroup("ANOTHER_GROUP7", "ERIN Systems LLC", ROOT_GROUP_NUMBER, TENANT_ID);
    groupRepository.createGroup("ANOTHER_GROUP8", "ERIN Systems LLC", ROOT_GROUP_NUMBER, TENANT_ID);
    groupRepository.createGroup("ANOTHER_GROUP9", "ERIN Systems LLC", ROOT_GROUP_NUMBER, TENANT_ID);
    groupRepository.createGroup("ANOTHER_GROUP10", "ERIN Systems LLC", ROOT_GROUP_NUMBER, TENANT_ID);

    boolean result = groupRepository.deleteGroup("ANOTHER_GROUP7");

    Assert.assertTrue(result);
    Assert.assertTrue(groupRepository.doesGroupExist(ROOT_GROUP_NUMBER));
    Assert.assertTrue(groupRepository.doesGroupExist("ANOTHER_GROUP1"));
    Assert.assertTrue(groupRepository.doesGroupExist("ANOTHER_GROUP6"));
    Assert.assertTrue(groupRepository.doesGroupExist("ANOTHER_GROUP8"));
    Assert.assertTrue(groupRepository.doesGroupExist("ANOTHER_GROUP9"));
    Assert.assertTrue(groupRepository.doesGroupExist("ANOTHER_GROUP10"));

    Assert.assertFalse(groupRepository.doesGroupExist("ANOTHER_GROUP7"));

    //check child has been deleted from parent
    Group returnedGroup = groupRepository.findById(new GroupId(ROOT_GROUP_NUMBER));
    Assert.assertEquals(5, returnedGroup.getChildren().size());
    Assert.assertFalse(returnedGroup.getChildren().contains(new GroupId("ANOTHER_GROUP7")));

    //check nthSibling of each child
    Assert.assertEquals(1, groupRepository.findById(new GroupId("ANOTHER_GROUP1")).getNthSibling());
    Assert.assertEquals(2, groupRepository.findById(new GroupId("ANOTHER_GROUP6")).getNthSibling());
    Assert.assertEquals(3, groupRepository.findById(new GroupId("ANOTHER_GROUP8")).getNthSibling());
    Assert.assertEquals(4, groupRepository.findById(new GroupId("ANOTHER_GROUP9")).getNthSibling());
    Assert.assertEquals(5, groupRepository.findById(new GroupId("ANOTHER_GROUP10")).getNthSibling());
  }
}
