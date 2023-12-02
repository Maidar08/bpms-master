package mn.erin.domain.aim.usecase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.aim.BaseUseCaseTest;
import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.usecase.group.DeleteGroup;
import mn.erin.domain.aim.usecase.group.DeleteGroupInput;
import mn.erin.domain.base.usecase.UseCaseException;

/**
 * @author Zorig
 */
public class DeleteGroupTest extends BaseUseCaseTest
{
  DeleteGroup deleteGroup;
  GroupRepository groupRepository;

  @Before
  public void setUpGroup()
  {
    groupRepository = Mockito.mock(GroupRepository.class);
    deleteGroup= new DeleteGroup(authenticationService, authorizationService, groupRepository);
    mockRequiredAuthServices();
  }

  @Test(expected = UseCaseException.class)
  public void throwsExceptionWhenGroupDoesNotExist() throws AimRepositoryException, UseCaseException
  {
    String id = "123456";
    DeleteGroupInput input = new DeleteGroupInput(id);

    Mockito.when(groupRepository.doesGroupExist(id)).thenReturn(false);

    deleteGroup.execute(input);


  }

  @Test
  public void deletesGroupWhenGroupExists() throws AimRepositoryException, UseCaseException
  {
    String id = "123456";
    DeleteGroupInput input = new DeleteGroupInput(id);

    Mockito.when(groupRepository.doesGroupExist(id)).thenReturn(true);
    deleteGroup.execute(input);
    Mockito.verify(groupRepository).deleteGroup(id);
  }
}
