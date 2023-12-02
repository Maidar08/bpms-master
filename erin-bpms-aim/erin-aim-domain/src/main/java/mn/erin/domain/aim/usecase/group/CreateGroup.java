package mn.erin.domain.aim.usecase.group;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;

/**
 * @author Zorig
 */
public class CreateGroup extends AuthorizedUseCase<CreateGroupInput, CreateGroupOutput>
{
  private static final Permission permission = new AimModulePermission("CreateGroup");
  private final GroupRepository groupRepository;
  private final TenantIdProvider tenantIdProvider;

  public CreateGroup()
  {
    super();
    this.groupRepository = null;
    this.tenantIdProvider = null;
  }

  public CreateGroup(AuthenticationService authenticationService, AuthorizationService authorizationService,
      GroupRepository groupRepository, TenantIdProvider tenantIdProvider)
  {
    super(authenticationService, authorizationService);
    this.groupRepository = Objects.requireNonNull(groupRepository, "GroupRepository cannot be null!");
    this.tenantIdProvider = Objects.requireNonNull(tenantIdProvider, "TenantIdProvider cannot be null!");
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected CreateGroupOutput executeImpl(CreateGroupInput input) throws UseCaseException
  {
    if ((input.getParentId() != null && StringUtils.isBlank(input.getParentId())))
    {
      throw new UseCaseException("Field(s) can't be blank space.");
    }

    TenantId tenantId = TenantId.valueOf(tenantIdProvider.getCurrentUserTenantId());

    try
    {
      Group createdGroup = groupRepository.createGroup(input.getId(), input.getName(), input.getParentId(), tenantId);
      return new CreateGroupOutput(createdGroup.getId().getId(), createdGroup.getNthSibling());
    }
    catch (AimRepositoryException e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }
}