package mn.erin.aim.repository.jdbc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import javax.inject.Inject;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.stereotype.Repository;

import mn.erin.aim.repository.jdbc.interfaces.JdbcGroupParentChildRepository;
import mn.erin.aim.repository.jdbc.interfaces.JdbcGroupRepository;
import mn.erin.aim.repository.jdbc.model.JdbcGroup;
import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.group.GroupTree;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.base.model.EntityId;

/**
 * @author Zorig
 */
@Repository
public class DefaultJdbcGroupRepository implements GroupRepository
{
  private static final String FIELD_ID = "_id";
  private static final String FIELD_PARENT_ID = "parentId";
  private static final String FIELD_TENANT_ID = "tenantId";
  private static final String FIELD_CHILDREN = "children";
  private static final String FIELD_NTH_SIBLING = "nthSibling";
  private static final String FIELD_NAME = "name";
  private static final String FIELD_DESCRIPTION = "description";

  private static final String ERR_MSG_PARENT_NONEXISTENT = "Parent does not exist.";
  private static final String ERR_MSG_GROUP_EXISTS = "Group already exists.";
  private static final String ERR_MSG_CHILD_EXISTS = "Parent already contains child. Duplicate should not be inserted.";

  private static final Logger LOGGER = LoggerFactory.getLogger(JdbcGroupRepository.class);

  private final JdbcGroupRepository jdbcGroupRepository;
  private final JdbcGroupParentChildRepository jdbcGroupParentChildRepository;

  @Inject
  public DefaultJdbcGroupRepository(
      JdbcGroupRepository jdbcGroupRepository,
      JdbcGroupParentChildRepository jdbcGroupParentChildRepository)
  {
    this.jdbcGroupRepository = jdbcGroupRepository;
    this.jdbcGroupParentChildRepository = jdbcGroupParentChildRepository;
  }

  @Override
  public Group findByNumberAndTenantId(String id, TenantId tenantId) throws AimRepositoryException
  {
    try
    {
      Validate.notBlank(id, "Id is required!");
      Validate.notNull(tenantId, "Tenant Id is required!");
      JdbcGroup jdbcGroup = jdbcGroupRepository.getByIdAndTenantId(id, tenantId.getId());
      if (jdbcGroup == null)
      {
        return null;
      }
      return convertToGroup(jdbcGroup);
    }
    catch (NullPointerException | IllegalArgumentException | DbActionExecutionException e)
    {
      throw new AimRepositoryException(e.getMessage(), e);
    }
  }

  @Override
  public Group findByName(String name) throws AimRepositoryException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Group createGroup(String number, String name, String parentId, TenantId tenantId) throws AimRepositoryException
  {
    try
    {
      Validate.notNull(tenantId, "Tenant Id should not be null!");
      Validate.notBlank(name, "Name should not be blank!");
      Validate.notBlank(number, "Number should not be blank!");

      Group existingGroup = findByNumberAndTenantId(number, tenantId);
      if (null != existingGroup)
      {
        throw new AimRepositoryException("Group with number [" + number + "] and tenant [" + tenantId.getId() + "] already exists!");
      }

      int nthSibling = getNthSibling(parentId);
      jdbcGroupRepository.insert(number, parentId, tenantId.getId(), name, nthSibling);

      GroupId parentIdSet = null;

      if (parentId != null)
      {
        parentIdSet = new GroupId(parentId);
        jdbcGroupParentChildRepository.insert(parentId, number);
      }

      Group groupToReturn = new Group(new GroupId(number), parentIdSet, tenantId, number);
      groupToReturn.setName(name);
      groupToReturn.setNthSibling(nthSibling);

      return groupToReturn;
    }
    catch (IllegalArgumentException | NullPointerException | DbActionExecutionException e)
    {
      throw new AimRepositoryException(e.getMessage(), e);
    }
  }

  @Override
  public boolean doesGroupExist(String id) throws AimRepositoryException
  {
    try
    {
      Validate.notBlank(id, "Id is needed for group existence check");
      return jdbcGroupRepository.existsById(id);
    }
    catch (IllegalArgumentException | NullPointerException | DbActionExecutionException e)
    {
      throw new AimRepositoryException(e.getMessage(), e);
    }
  }

  @Override
  public int getNthSibling(String parentId) throws AimRepositoryException
  {
    try
    {
      if (parentId != null)
      {
        return getChildrenCount(parentId) + 1;
      }

      //means root
      return 0;
    }
    catch (IllegalArgumentException | NullPointerException e)
    {
      throw new AimRepositoryException(e.getMessage(), e);
    }
  }

  @Override
  public List<Group> getAllRootGroups(TenantId tenantId) throws AimRepositoryException
  {
    try
    {
      Validate.notNull(tenantId, "tenantId is required!");
      List<Group> groupsToReturn = new ArrayList<>();

      Iterator<JdbcGroup> jdbcGroupList = jdbcGroupRepository.getAllRootGroupsByTenantId(tenantId.getId()).iterator();
      while (jdbcGroupList.hasNext())
      {
        groupsToReturn.add(convertToGroup(jdbcGroupList.next()));
      }

      return groupsToReturn;
    }
    catch (NullPointerException | DbActionExecutionException e)
    {
      throw new AimRepositoryException(e.getMessage(), e);
    }
  }

  @Override
  public boolean deleteGroup(String groupId) throws AimRepositoryException
  {
    try
    {
      Validate.notBlank(groupId, "Group Id is needed for deletion");

      if (!doesGroupExist(groupId))
      {
        return false;
      }

      String parentId = jdbcGroupParentChildRepository.getParentIdByChildGroupId(groupId);

      if (parentId != null)
      {
        deleteSiblingShift(groupId, parentId);
        removeChild(parentId, groupId);
      }

      Queue<String> groupsToTraverse = new PriorityQueue<>();
      //root
      groupsToTraverse.add(groupId);

      String currentPeak;
      Iterator<String> children;

      while (!groupsToTraverse.isEmpty())
      {
        currentPeak = groupsToTraverse.peek();

        children = jdbcGroupParentChildRepository.getAllChildrenByParentId(currentPeak).iterator();

        while (children.hasNext())
        {
          String nextChild = children.next();
          groupsToTraverse.add(nextChild);
        }

        //get rid of actual itself and its children connection
        //add try catch
        jdbcGroupRepository.deleteById(currentPeak);
        jdbcGroupParentChildRepository.deleteByParentId(currentPeak);

        groupsToTraverse.remove(currentPeak);
      }

      //do a check if delete was successful and set return accordingly
      return true;
    }
    catch (NullPointerException | IllegalArgumentException | DbActionExecutionException e)
    {
      throw new AimRepositoryException(e.getMessage(), e);
    }
  }

  @Override
  public Set<GroupId> getAllSubGroups(String number)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Group moveGroupParent(String groupId, String parentId)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Group moveGroupSibling(String groupId, int siblingNumber)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Group renameGroup(String groupId, String name)
  {
    jdbcGroupRepository.setByName(groupId, name);

    if(jdbcGroupRepository.getById(groupId).getName().equals(name)){
      JdbcGroup findJdbcGroup = jdbcGroupRepository.getById(groupId);
      if (findJdbcGroup != null)
      {
        return convertToGroup(findJdbcGroup);
      }
    }
    return null;
  }

  @Override
  public Group findById(EntityId entityId)
  {
    Validate.notNull(entityId, "Entity Id is required!");
    Optional<JdbcGroup> findJdbcGroup = jdbcGroupRepository.findById(entityId.getId());
    if (findJdbcGroup.isPresent())
    {
      return convertToGroup(findJdbcGroup.get());
    }
    return null;
  }

  @Override
  public Collection<Group> findAll()
  {
    List<Group> groupsToReturn = new ArrayList<>();

    Iterator<JdbcGroup> returnedJdbcGroups = jdbcGroupRepository.findAll().iterator();
    while (returnedJdbcGroups.hasNext())
    {
      groupsToReturn.add(convertToGroup(returnedJdbcGroups.next()));
    }

    return groupsToReturn;
  }

  private Group convertToGroup(JdbcGroup jdbcGroup)
  {
    GroupId groupId = new GroupId(jdbcGroup.getId());
    TenantId tenantId = new TenantId(jdbcGroup.getTenantId());
    GroupId parentId = null;
    if (jdbcGroup.getParentId() != null)
    {
      parentId = new GroupId(jdbcGroup.getParentId());
    }
    String name = jdbcGroup.getName();
    int nthSibling = jdbcGroup.getNthSibling();

    Group groupToReturn = new Group(groupId, parentId, tenantId, groupId.getId());
    groupToReturn.setName(name);
    groupToReturn.setNthSibling(nthSibling);

    Iterator<String> childrenStringsIterator = jdbcGroupParentChildRepository.getAllChildrenByParentId(jdbcGroup.getId()).iterator();
    while (childrenStringsIterator.hasNext())
    {
      GroupId childGroupIdToAdd = new GroupId(childrenStringsIterator.next());
      groupToReturn.addChild(childGroupIdToAdd);
    }

    return groupToReturn;
  }

  private boolean removeChild(String parentId, String childId)
  {
    //TODO: check if parent exists
    //TODO: check if child exists
    jdbcGroupParentChildRepository.deleteByParentIdAndChildId(parentId, childId);
    return true;
  }

  /*
  private boolean setChild(String parentId, String childId) throws AimRepositoryException
  {
    if (!doesGroupExist(parentId))
    {
      throw new IllegalArgumentException("Parent does not exist.");
    }

    JdbcGroupParentChild jdbcGroupParentChild = new JdbcGroupParentChild();
    jdbcGroupParentChild.setParentId(parentId);
    jdbcGroupParentChild.setChildId(childId);

    try
    {
      jdbcGroupParentChild = jdbcGroupParentChildRepository.save(jdbcGroupParentChild);
    }
    catch (DbActionExecutionException e)
    {
      //throw some kind of exception
      throw new AimRepositoryException(e.getMessage(), e);
    }

    return 0 < jdbcGroupParentChildRepository.checkParentChild(parentId, childId);
  }
   */

  private boolean doesChildExist(String parentId, String childIdSearch)
  {
    return false;
  }

  private boolean deleteSiblingShift(String groupId, String parentId)
  {
    int currentNthSibling = jdbcGroupRepository.findById(groupId).get().getNthSibling();

    boolean flag = true;
    while (flag)
    {
      currentNthSibling++;
      int rowsAffected = jdbcGroupRepository.updateNthSiblingByParentIdAndNthSibling(parentId, currentNthSibling);
      if (rowsAffected != 1)
      {
        flag = false;
      }
    }

    //add checker for successful change, set return accordingly
    return true;
  }

  private int getChildrenCount(String parentId) throws AimRepositoryException
  {
    if (!doesGroupExist(parentId))
    {
      throw new IllegalArgumentException(ERR_MSG_PARENT_NONEXISTENT);
    }

    return jdbcGroupParentChildRepository.countChildren(parentId);
  }

  private GroupTree getGroupSubTree(String startingFilterString)
  {
    return null;
  }

  private Group mapToGroupObject(String filterId)
  {
    //TODO:Check the OPTIONAL if it is blank
    //TODO:add try catch
    JdbcGroup jdbcGroup = jdbcGroupRepository.findById(filterId).get();
    return convertToGroup(jdbcGroup);
  }
}
