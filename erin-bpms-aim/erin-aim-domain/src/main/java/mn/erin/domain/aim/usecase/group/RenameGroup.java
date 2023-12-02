package mn.erin.domain.aim.usecase.group;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
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
public class RenameGroup extends AuthorizedUseCase<RenameGroupInput, RenameGroupOutput>
{
  private static final Permission permission = new AimModulePermission("RenameGroup");
  private final GroupRepository groupRepository;

  public RenameGroup()
  {
    super();
    this.groupRepository = null;
  }

  public RenameGroup(AuthenticationService authenticationService, AuthorizationService authorizationService,
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
  protected RenameGroupOutput executeImpl(RenameGroupInput input) throws UseCaseException
  {
    String groupId = Validate.notNull(input.getGroupId(), "Group Id can't be null!");
    String name = Validate.notNull(input.getName(), "Name can't be null!");

    if (StringUtils.isBlank(input.getName()))
    {
      throw new UseCaseException("Name should not be blank!");
    }

    try
    {
      if (!groupRepository.doesGroupExist(groupId))
      {
        throw new UseCaseException("Group does not exist!");
      }

      Group renamedGroup = groupRepository.renameGroup(groupId, name);

      RenameGroupOutput output = new RenameGroupOutput(renamedGroup);
      return output;
    }
    catch (AimRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }


  }
}
