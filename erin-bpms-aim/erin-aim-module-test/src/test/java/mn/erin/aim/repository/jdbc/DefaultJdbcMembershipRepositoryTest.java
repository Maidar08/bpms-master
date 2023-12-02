package mn.erin.aim.repository.jdbc;

import java.util.List;
import java.util.Optional;
import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import mn.erin.aim.repository.jdbc.interfaces.JdbcMembershipRepository;
import mn.erin.aim.repository.jdbc.model.JdbcMembership;
import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.membership.MembershipId;
import mn.erin.domain.aim.model.role.RoleId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.MembershipRepository;

/**
 * Tests for {@link DefaultJdbcMembershipRepository} which runs on local Oracle database. Please configure your test Oracle database according to
 * jdbc-datasource-test.properties
 *
 * @author Zorig
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestAimJdbcBeanConfig.class })
@Ignore //Activate this on Teamcity
@Transactional
public class DefaultJdbcMembershipRepositoryTest
{
  @Inject
  private MembershipRepository membershipRepository;

  @Inject
  private JdbcMembershipRepository jdbcMembershipRepository;

  @Before
  public void cleanup()
  {
    // clean up database before each test run
    // so each test runs on a blank database
    jdbcMembershipRepository.deleteAll();
  }

  @Test(expected = AimRepositoryException.class)
  public void createMembershipNullUserId() throws AimRepositoryException
  {
    membershipRepository.create(null, new GroupId("1"), new RoleId("1"), new TenantId("Erin"));
  }

  @Test(expected = AimRepositoryException.class)
  public void createMembershipNullGroupId() throws AimRepositoryException
  {
    membershipRepository.create(new UserId("1"), null, new RoleId("1"), new TenantId("Erin"));
  }

  @Test(expected = AimRepositoryException.class)
  public void createMembershipNullRoleId() throws AimRepositoryException
  {
    membershipRepository.create(new UserId("1"), new GroupId("1"), null, new TenantId("Erin"));
  }

  @Test(expected = AimRepositoryException.class)
  public void createMembershipNullTenantId() throws AimRepositoryException
  {
    membershipRepository.create(new UserId("1"), new GroupId("1"), new RoleId("1"), null);
  }

  @Test
  public void createMembership() throws AimRepositoryException
  {
    Membership createdMembership = membershipRepository
        .create(new UserId("Erin User"), new GroupId("Erin Group"), new RoleId("Erin Developer"), new TenantId("Erin"));
    Optional<JdbcMembership> createdJdbcMembershipSearchResult = jdbcMembershipRepository.findById(createdMembership.getMembershipId().getId());

    Assert.assertNotNull(createdMembership);
    Assert.assertTrue(createdJdbcMembershipSearchResult.isPresent());

    JdbcMembership createdJdbcMembership = createdJdbcMembershipSearchResult.get();
    Assert.assertEquals(createdJdbcMembership.getMembershipId(), createdMembership.getMembershipId().getId());
    Assert.assertEquals(createdJdbcMembership.getGroupId(), createdMembership.getGroupId().getId(), "Erin Group");
    Assert.assertEquals(createdJdbcMembership.getUserId(), createdMembership.getUserId().getId(), "Erin User");
    Assert.assertEquals(createdJdbcMembership.getRoleId(), createdMembership.getRoleId().getId(), "Erin Developer");
    Assert.assertEquals(createdJdbcMembership.getTenantId(), createdMembership.getTenantId().getId(), "Erin");
  }

  @Test(expected = AimRepositoryException.class)
  public void createMembershipDuplicateValuesConstraint() throws AimRepositoryException
  {
    Membership createdMembership = membershipRepository
        .create(new UserId("Erin User"), new GroupId("Erin Group"), new RoleId("Erin Developer"), new TenantId("Erin"));
    Membership createdMembership2 = membershipRepository
        .create(new UserId("Erin User"), new GroupId("Erin Group"), new RoleId("Erin Developer"), new TenantId("Erin"));
  }

  @Test
  public void deleteNonExistent() throws AimRepositoryException
  {
    boolean isDeleted = membershipRepository.delete(MembershipId.valueOf("Id"));

    Assert.assertTrue(isDeleted);
  }

  @Test
  public void deleteExistent() throws AimRepositoryException
  {
    Membership createdMembership = membershipRepository
        .create(new UserId("Erin User"), new GroupId("Erin Group"), new RoleId("Erin Developer"), new TenantId("Erin"));

    boolean isDeleted = membershipRepository.delete(createdMembership.getMembershipId());

    Assert.assertTrue(isDeleted);
  }

  @Test(expected = AimRepositoryException.class)
  public void deleteThrowsExceptionNullIdentifier() throws AimRepositoryException
  {
    membershipRepository.delete(null);
  }

  @Test(expected = AimRepositoryException.class)
  public void listAllByUserIdThrowsExceptionWhenNullUserId() throws AimRepositoryException
  {
    membershipRepository.listAllByUserId(new TenantId("Erin"), null);
  }

  @Test(expected = AimRepositoryException.class)
  public void listAllByUserIdThrowsExceptionWhenNullTenantId() throws AimRepositoryException
  {
    membershipRepository.listAllByUserId(null, new UserId("User1"));
  }

  @Test
  public void listAllByUserIdEmptyResult() throws AimRepositoryException
  {
    List<Membership> returnedMembershipList = membershipRepository.listAllByUserId(new TenantId("Erin"), new UserId("User 1"));

    Assert.assertTrue(returnedMembershipList.isEmpty());
  }

  @Test
  public void listAllByUserIdNonEmptyResult() throws AimRepositoryException
  {
    Membership createdMembership = membershipRepository
        .create(new UserId("Erin User"), new GroupId("Erin Group"), new RoleId("Erin Developer"), new TenantId("Erin"));
    Membership createdMembership2 = membershipRepository
        .create(new UserId("Erin User"), new GroupId("Erin Group"), new RoleId("Erin Manager"), new TenantId("Erin"));

    List<Membership> returnedMembershipList = membershipRepository.listAllByUserId(new TenantId("Erin"), new UserId("Erin User"));

    Assert.assertEquals(2, returnedMembershipList.size());

    Membership membership1 = returnedMembershipList.get(0);
    Membership membership2 = returnedMembershipList.get(1);

    Assert.assertEquals("Erin User", membership1.getUserId().getId(), membership2.getUserId().getId());
    Assert.assertEquals("Erin", membership1.getTenantId().getId(), membership2.getTenantId().getId());

  }

  @Test(expected = AimRepositoryException.class)
  public void listAllByGroupIdThrowsExceptionWhenNullGroupId() throws AimRepositoryException
  {
    membershipRepository.listAllByGroupId(new TenantId("Erin"), null);
  }

  @Test(expected = AimRepositoryException.class)
  public void listAllByGroupIdThrowsExceptionWhenNullTenantId() throws AimRepositoryException
  {
    membershipRepository.listAllByGroupId(null, new GroupId("1"));
  }

  @Test
  public void listAllByGroupIdEmptyResult() throws AimRepositoryException
  {
    List<Membership> returnedMembershipList = membershipRepository.listAllByGroupId(new TenantId("Erin"), new GroupId("Group 1"));

    Assert.assertTrue(returnedMembershipList.isEmpty());
  }

  @Test
  public void listAllByGroupdIdNonEmptyResult() throws AimRepositoryException
  {
    Membership createdMembership = membershipRepository
        .create(new UserId("Erin User"), new GroupId("Erin Group"), new RoleId("Erin Developer"), new TenantId("Erin"));
    Membership createdMembership2 = membershipRepository
        .create(new UserId("Erin User"), new GroupId("Erin Group"), new RoleId("Erin Manager"), new TenantId("Erin"));

    List<Membership> returnedMembershipList = membershipRepository.listAllByGroupId(new TenantId("Erin"), new GroupId("Erin Group"));

    Assert.assertEquals(2, returnedMembershipList.size());

    Membership membership1 = returnedMembershipList.get(0);
    Membership membership2 = returnedMembershipList.get(1);

    Assert.assertEquals("Erin User", membership1.getUserId().getId(), membership2.getUserId().getId());
    Assert.assertEquals("Erin", membership1.getTenantId().getId(), membership2.getTenantId().getId());

  }

}
