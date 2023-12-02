package mn.erin.domain.aim.usecase.group;

import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;

/**
 * @author Zorig
 */
public class MoveGroupSibling extends AuthorizedUseCase<MoveGroupSiblingInput, MoveGroupSiblingOutput>
{
  private static final Permission permission = new AimModulePermission("MoveGroupSibling");
  private final GroupRepository groupRepository;

  public MoveGroupSibling()
  {
    super();
    this.groupRepository = null;
  }

  public MoveGroupSibling(AuthenticationService authenticationService, AuthorizationService authorizationService,
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
  protected MoveGroupSiblingOutput executeImpl(MoveGroupSiblingInput input) throws UseCaseException
  {
    String groupId = Validate.notNull(input.getGroupId(), "Group Id can't be null!");
    int nthSibling = Validate.notNull(input.getNthSibling(), "Nth Sibling can't be null!");

    try
    {
      if (!groupRepository.doesGroupExist(groupId))
      {
        throw new UseCaseException("Group does not exist!");
      }

      if (nthSibling < 0)
      {
        throw new UseCaseException("NthSibling must be a positive number!");
      }

      Group updatedGroup = groupRepository.moveGroupSibling(groupId, nthSibling);
      MoveGroupSiblingOutput output = new MoveGroupSiblingOutput(updatedGroup);

      return output;
    }
    catch (AimRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }

  }
}
