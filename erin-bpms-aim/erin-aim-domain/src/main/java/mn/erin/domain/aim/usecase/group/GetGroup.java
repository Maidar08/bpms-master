package mn.erin.domain.aim.usecase.group;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
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
public class GetGroup extends AuthorizedUseCase<GetGroupInput, GetGroupOutput>
{
  private static final Permission permission = new AimModulePermission("GetGroup");
  private final GroupRepository groupRepository;

  public GetGroup()
  {
    super();
    this.groupRepository = null;
  }

  public GetGroup(AuthenticationService authenticationService, AuthorizationService authorizationService,
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
  protected GetGroupOutput executeImpl(GetGroupInput input) throws UseCaseException
  {
    return null;
  }

  @Override
  public GetGroupOutput execute(GetGroupInput input) throws UseCaseException
  {
    try
    {
      String parentIdString = null;

      if (!groupRepository.doesGroupExist(input.getId()))
      {
        throw new UseCaseException("Group does not exist!");
      }

      Group group = groupRepository.findById(GroupId.valueOf(input.getId()));
      String id = group.getId().getId();
      Object parent = group.getParent();
      String tenantId = group.getTenantId().getId();
      String name = group.getNumber();
      String description = group.getName();
      int nthSibling = group.getNthSibling();

      if (parent != null)
      {
        parentIdString = group.getParent().getId();
      }

      //We need to turn GroupId objects into String for each children so that it is compatible for our output object
      List<String> children = new ArrayList<>();
      Iterator<GroupId> childrenIterator = group.getChildren().iterator();

      while (childrenIterator.hasNext())
      {
        children.add(childrenIterator.next().getId());
      }

      return new GetGroupOutput(id, parentIdString, tenantId, name, description, nthSibling, children);
    }
    catch (AimRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }

  }
}
