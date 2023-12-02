package mn.erin.domain.aim.usecase.group;

import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;

/**
 * @author Zorig
 */
public class MoveGroupParent extends AuthorizedUseCase<MoveGroupParentInput, MoveGroupParentOutput>
{
  private static final Permission permission = new AimModulePermission("MoveGroupParent");
  private final GroupRepository groupRepository;

  public MoveGroupParent()
  {
    super();
    this.groupRepository = null;
  }

  public MoveGroupParent(AuthenticationService authenticationService, AuthorizationService authorizationService,
      GroupRepository groupRepository)
  {
    super(authenticationService, authorizationService);
    this.groupRepository = Objects.requireNonNull(groupRepository, "GroupRepository cannot be null!");
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected MoveGroupParentOutput executeImpl(MoveGroupParentInput input) throws UseCaseException
  {

    try
    {
      String groupId = Validate.notNull(input.getGroupId(), "Group Id can't be null!");
      String parentId = Validate.notNull(input.getParentId(), "Group Id can't be null!");

      if (!groupRepository.doesGroupExist(groupId) && !groupRepository.doesGroupExist(parentId))
      {
        throw new UseCaseException("Group does not exist!");
      }

      if(groupRepository.findById(new UserId(groupId)).getChildren().contains(new GroupId(groupId)))
      {
        throw new UseCaseException("Parent group already has a child group with same ID!");
      }

      Group movedGroup = groupRepository.moveGroupParent(groupId, parentId);
      MoveGroupParentOutput output = new MoveGroupParentOutput(movedGroup);

      return output;
    }
    catch (AimRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}
