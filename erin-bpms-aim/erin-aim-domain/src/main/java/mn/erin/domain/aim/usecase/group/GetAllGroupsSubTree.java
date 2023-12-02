package mn.erin.domain.aim.usecase.group;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupTree;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;

/**
 * @author Zorig
 */
public class GetAllGroupsSubTree implements UseCase<Void, GetAllGroupsSubTreeOuput>
{
  private final GroupRepository groupRepository;
  private final TenantIdProvider tenantIdProvider;
  private final GetGroupSubTree getGroupSubTree;

  public GetAllGroupsSubTree(GroupRepository groupRepository, GetGroupSubTree getGroupSubTree, TenantIdProvider tenantIdProvider)
  {
    this.groupRepository = Objects.requireNonNull(groupRepository, "GroupRepository cannot be null!");
    this.getGroupSubTree = Objects.requireNonNull(getGroupSubTree, "GetGroupSubTree cannot be null!");
    this.tenantIdProvider = Objects.requireNonNull(tenantIdProvider, "TenantIdProvider cannot be null!");
  }


  @Override
  public GetAllGroupsSubTreeOuput execute(Void input) throws UseCaseException
  {
    try
    {
      TenantId tenantId = TenantId.valueOf(tenantIdProvider.getCurrentUserTenantId());
      List<String> rootGroupIds = new ArrayList<>();
      Iterator<Group> rootGroupsIterator =  groupRepository.getAllRootGroups(tenantId).iterator();
      while (rootGroupsIterator.hasNext())
      {
        rootGroupIds.add(rootGroupsIterator.next().getId().getId());
      }

      List<GroupTree> rootGroupTreesToReturn = new ArrayList<>();

      Iterator<String> rootGroupIdsIterator = rootGroupIds.iterator();
      while (rootGroupIdsIterator.hasNext())
      {
        GetGroupSubTreeOutput getGroupSubTreeOutput = getGroupSubTree.execute(new GetGroupSubTreeInput(rootGroupIdsIterator.next()));
        GroupTree groupTreeToAdd = getGroupSubTreeOutput.getGroupTree();
        rootGroupTreesToReturn.add(groupTreeToAdd);
      }

      return new GetAllGroupsSubTreeOuput(rootGroupTreesToReturn);
    }
    catch (AimRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }

  }
}

