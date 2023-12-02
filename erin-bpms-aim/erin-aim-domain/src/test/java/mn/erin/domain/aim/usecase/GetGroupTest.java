package mn.erin.domain.aim.usecase;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.aim.BaseUseCaseTest;
import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.usecase.group.GetGroup;
import mn.erin.domain.aim.usecase.group.GetGroupInput;
import mn.erin.domain.aim.usecase.group.GetGroupOutput;
import mn.erin.domain.base.usecase.UseCaseException;

/**
 * @author Zorig
 */
public class GetGroupTest extends BaseUseCaseTest
{
  GetGroup getGroup;
  GroupRepository groupRepository;

  @Before
  public void setUpGroup()
  {
    groupRepository = Mockito.mock(GroupRepository.class);
    getGroup = new GetGroup(authenticationService, authorizationService, groupRepository);
    mockRequiredAuthServices();
  }

  @Test(expected = UseCaseException.class)
  public void throwsExceptionWhenGroupDoesNotExist() throws AimRepositoryException, UseCaseException
  {
    String id = "123456";
    GetGroupInput input = new GetGroupInput(id);

    Mockito.when(groupRepository.doesGroupExist(id)).thenReturn(false);

    getGroup.execute(input);
  }

  @Test
  public void getsGroupWhenGroupExists() throws AimRepositoryException, UseCaseException
  {
    String groupId = "000897";
    Group groupToReturn = new Group(new GroupId(groupId), new GroupId("123456"), new TenantId("123"), "NAME");
    groupToReturn.addChild(new GroupId("1"));
    groupToReturn.addChild(new GroupId("2"));
    groupToReturn.addChild(new GroupId("3"));

    Mockito.when(groupRepository.findById(GroupId.valueOf(groupId))).thenReturn(groupToReturn);

    Mockito.when(groupRepository.doesGroupExist(groupId)).thenReturn(true);

    GetGroupInput input = new GetGroupInput(groupId);

    GetGroupOutput output = getGroup.execute(input);

    Assert.assertEquals(groupId, output.getId());
    Assert.assertEquals("123456", output.getParentId());
    Assert.assertEquals("123", output.getTenantId());
    Assert.assertEquals("NAME", output.getName());
    Assert.assertEquals(Arrays.asList("1", "2", "3"), output.getChildren());
  }

  @Test
  public void getsGroupWhenGroupParentisNull() throws AimRepositoryException, UseCaseException
  {
    String groupId = "000897";
    Group groupToReturn = new Group(new GroupId(groupId), null, new TenantId("123"), "NAME");
    groupToReturn.addChild(new GroupId("1"));
    groupToReturn.addChild(new GroupId("2"));
    groupToReturn.addChild(new GroupId("3"));

    Mockito.when(groupRepository.findById(GroupId.valueOf(groupId))).thenReturn(groupToReturn);

    Mockito.when(groupRepository.doesGroupExist(groupId)).thenReturn(true);

    GetGroupInput input = new GetGroupInput(groupId);

    GetGroupOutput output = getGroup.execute(input);

    Assert.assertEquals(groupId, output.getId());
    Assert.assertNull(output.getParentId());
    Assert.assertEquals("123", output.getTenantId());
    Assert.assertEquals("NAME", output.getName());
    Assert.assertEquals(Arrays.asList("1", "2", "3"), output.getChildren());
  }



}
