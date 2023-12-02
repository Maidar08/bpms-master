package mn.erin.aim.repository.mongo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.group.GroupTree;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.base.model.EntityId;

import static com.mongodb.client.model.Filters.and;

/**
 * @author Zorig
 */
public class MongoGroupRepository implements GroupRepository
{
  private static final String FIELD_ID = "_id";
  private static final String FIELD_PARENT_ID = "parentId";
  private static final String FIELD_TENANT_ID = "tenantId";
  private static final String FIELD_CHILDREN = "children";
  private static final String FIELD_NTH_SIBLING = "nthSibling";
  private static final String FIELD_NAME = "name";
  private static final String FIELD_DESCRIPTION = "description";

  private static final String ERR_MSG_PARENT_NONEXISTENT = "Parent does not exist.";
  private static final String ERR_MSG_CHILD_EXISTS = "Parent already contains child. Duplicate should not be inserted.";

  private static final Logger LOGGER = LoggerFactory.getLogger(MongoGroupRepository.class);

  protected final MongoCollection<Document> groupCollection;

  public MongoGroupRepository(MongoCollection<Document> mongoCollection)
  {
    this.groupCollection = mongoCollection;
  }

  @Override
  public Group findByNumberAndTenantId(String number, TenantId tenantId) throws AimRepositoryException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Group findByName(String name) throws AimRepositoryException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Group createGroup(String number, String name, String parentId, TenantId tenantId)
  {
    ObjectId objectId = new ObjectId(new Date());
    String id = objectId.toHexString();

    GroupId parentGroupId = null;

    //if parentid is not null then that parentid needs to set/add childid
    if (!StringUtils.isBlank(parentId))
    {
      setChild(parentId, id);
      parentGroupId = GroupId.valueOf(parentId);
    }

    Group group = new Group(GroupId.valueOf(id), parentGroupId, tenantId, number);
    group.setName(name);

    //always run this after child has been set
    int nThSibling = getNthSibling(parentId);
    group.setNthSibling(nThSibling);

    //Define a constant instead of duplicating literal multiple times
    Document documentToInsert = new Document();
    documentToInsert.put(FIELD_ID, objectId);
    documentToInsert.put(FIELD_PARENT_ID, parentId);
    documentToInsert.put(FIELD_TENANT_ID, tenantId);
    documentToInsert.put(FIELD_CHILDREN, new ArrayList<String>());
    documentToInsert.put(FIELD_NTH_SIBLING, nThSibling);
    documentToInsert.put(FIELD_NAME, name);

    groupCollection.insertOne(documentToInsert);

    return group;
  }

  private boolean removeChild(String parentId, String childId)
  {
    Document parentFilter = new Document(FIELD_ID, new ObjectId(parentId));

    if (!doesGroupExist(parentId))
    {
      throw new IllegalArgumentException(ERR_MSG_PARENT_NONEXISTENT);
    }

    if (!doesChildExist(parentId, childId))
    {
      throw new IllegalArgumentException("Child does not exist. Can't delete child that does not exist.");
    }

    Document removeElementDocument = new Document(FIELD_CHILDREN, childId);
    Document updateDocument = new Document("$pull", removeElementDocument);
    Document documentThatWasUpdated = groupCollection.findOneAndUpdate(parentFilter, updateDocument);
    boolean isUpdateSuccessful = !((ArrayList<String>) documentThatWasUpdated.get(FIELD_CHILDREN)).contains(childId);
    return isUpdateSuccessful;
  }

  private boolean setChild(String parentId, String childId)
  {
    Document filter = new Document(FIELD_ID, new ObjectId(parentId));

    //check if the parent exists, implement a private method
    if (!doesGroupExist(parentId))
    {
      throw new IllegalArgumentException(ERR_MSG_CHILD_EXISTS);
    }

    Document arrayAddDocument = new Document(FIELD_CHILDREN, childId);
    Document updateDocument = new Document("$push", arrayAddDocument);

    Document documentThatWasUpdated = groupCollection.findOneAndUpdate(filter, updateDocument);

    //check the returned document if the child has been added
    boolean isSuccessfulUpdate = ((ArrayList<String>) documentThatWasUpdated.get(FIELD_CHILDREN)).contains(childId);

    return isSuccessfulUpdate;
  }

  @Override
  public boolean doesGroupExist(String id)
  {
    Validate.notNull(id, "Id being check for existence should not be null.");

    Document filter = new Document(FIELD_ID, new ObjectId(id));

    return groupCollection.find(filter).iterator().hasNext();
  }

  private boolean doesChildExist(String parentId, String childIdSearch)
  {
    Document filter = new Document(FIELD_ID, new ObjectId(parentId));

    Bson childFilter = Filters.eq(FIELD_CHILDREN, childIdSearch);
    Bson combinationFilter = and(filter, childFilter);

    return groupCollection.find(combinationFilter).iterator().hasNext();
  }

  private boolean setParent(String parentId, String childId)
  {
    Document filter = new Document(FIELD_ID, new ObjectId(childId));
    Document parentUpdate = new Document(FIELD_PARENT_ID, parentId);
    Document updateDocument = new Document("$set", parentUpdate);
    Document documentThatWasUpdated = groupCollection.findOneAndUpdate(filter, updateDocument);

    //check the returned document if the child has been added
    boolean isSuccessfulUpdate = ((String) documentThatWasUpdated.get(FIELD_PARENT_ID)).equals(childId);
    return isSuccessfulUpdate;
  }

  private GroupId removeParent(String parentId, String childId)
  {
    return null;
  }

  @Override
  public Collection<Group> findAll()
  {
    List<Group> groupsListToReturn = new ArrayList<>();

    Iterator<Document> returnedGroupDocumentIterator = groupCollection.find().iterator();

    while (returnedGroupDocumentIterator.hasNext())
    {
      groupsListToReturn.add(convertToGroupObject(returnedGroupDocumentIterator.next()));
    }
    return groupsListToReturn;
  }

  @Override
  public boolean deleteGroup(String groupId)
  {
    Document filter = new Document(FIELD_ID, new ObjectId(groupId));

    String parentId = (String) groupCollection.find(filter).iterator().next().get(FIELD_PARENT_ID);

    if (parentId != null)
    {
      deleteSiblingShift(groupId, parentId);
      removeChild(parentId, groupId);
    }
    Queue<String> groupsToTraverse = new PriorityQueue<>();

    //root
    groupsToTraverse.add(groupId);

    Iterator<Document> documentIterator;
    Iterator<String> children;

    while (!groupsToTraverse.isEmpty())
    {
      documentIterator = groupCollection.find(filter).iterator();
      children = ((ArrayList<String>) documentIterator.next().get(FIELD_CHILDREN)).iterator();

      String currentPeak = groupsToTraverse.peek();

      while (children.hasNext())
      {
        String nextChild = children.next();
        groupsToTraverse.add(nextChild);
      }

      //delete and check if deletion is successful
      if (groupCollection.deleteOne(filter).getDeletedCount() == 0)
      {
        LOGGER.warn("Group not deleted, group id: {}", currentPeak);
      }

      groupsToTraverse.remove(currentPeak);
      String newPeak = groupsToTraverse.peek();

      //check if it has peek or empty
      //set new filter for next iteration
      if (!groupsToTraverse.isEmpty())
      {
        filter = new Document(FIELD_ID, new ObjectId(newPeak));
      }
    }

    //do a check if delete was successful and set return accordingly
    return true;
  }

  @Override
  public Set<GroupId> getAllSubGroups(String number)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Group moveGroupParent(String groupId, String parentId)
  {
    Document currentParentFilter = new Document(FIELD_ID, new ObjectId(groupId));
    String currentParentIdOfGroup = (String) groupCollection.find(currentParentFilter).iterator().next().get(FIELD_PARENT_ID);

    deleteSiblingShift(groupId, currentParentIdOfGroup);

    if (removeChild(currentParentIdOfGroup, groupId))
    {
      throw new IllegalArgumentException("Unsuccessful child removal!");
    }

    if (!setParent(parentId, groupId))
    {
      throw new IllegalArgumentException("Unsuccessful parent update!");
    }

    if (!setNthSibling(groupId, getNthSibling(parentId)))
    {
      throw new IllegalArgumentException("Unsuccessful set nthSibling");
    }

    if (!setChild(parentId, groupId))
    {
      throw new IllegalArgumentException("Unsuccessful child update!");
    }

    return mapToGroupObject(groupId);
  }

  @Override
  public Group moveGroupSibling(String groupId, int siblingNumber)
  {
    //change parent's children array field with shifts;
    //set group sibling field to index + 1/change all shifted nthSibling field

    Document groupFilter = new Document(FIELD_ID, new ObjectId(groupId));
    Document groupDocument = groupCollection.find(groupFilter).iterator().next();
    String parentId = (String) groupDocument.get(FIELD_PARENT_ID);
    Document parentFilter = new Document(FIELD_ID, new ObjectId(parentId));
    Document parentGroupDocument = groupCollection.find(parentFilter).iterator().next();

    ArrayList<String> childrenList = (ArrayList<String>) parentGroupDocument.get(FIELD_CHILDREN);

    if (siblingNumber > childrenList.size() - 1)
    {
      throw new IllegalArgumentException("Sibling number should not exceed limit the max index of sibling!");
    }

    childrenList.remove(groupId);
    childrenList.add(siblingNumber, groupId);

    Document childrenFilter = new Document(FIELD_CHILDREN, childrenList);
    Document setChildrenFilter = new Document("$set", childrenFilter);
    groupCollection.findOneAndUpdate(parentFilter, setChildrenFilter);

    Iterator<String> childrenListIterator = childrenList.iterator();
    while (childrenListIterator.hasNext())
    {
      String currentGroupFilterString = childrenListIterator.next();
      int nthSibling = childrenList.indexOf(currentGroupFilterString) + 1;
      setNthSibling(currentGroupFilterString, nthSibling);//wrap this in try catch?
    }

    return mapToGroupObject(groupId);
  }

  @Override
  public Group renameGroup(String groupId, String newName)
  {
    Document filter = new Document(FIELD_ID, new ObjectId(groupId));
    Document nameDocument = new Document(FIELD_NAME, newName);
    Document setFilter = new Document("$set", nameDocument);

    Document updatedDocument = groupCollection.findOneAndUpdate(filter, setFilter);

    String name = (String) groupCollection.find(filter).iterator().next().get(FIELD_NAME);

    if (!newName.equals(name))
    {
      throw new IllegalArgumentException("Unsuccessful name change!");
    }

    return mapToGroupObject(groupId);
  }

  private boolean deleteSiblingShift(String groupId, String parentId)
  {
    Document filter = new Document(FIELD_ID, new ObjectId(groupId));
    Document parentFilter = new Document(FIELD_ID, new ObjectId(parentId));

    int index = ((int) groupCollection.find(filter).iterator().next().get(FIELD_NTH_SIBLING)) - 1;

    List<String> siblings = (ArrayList<String>) groupCollection.find(parentFilter).iterator().next().get(FIELD_CHILDREN);
    List<String> siblingsToShiftList = new ArrayList<>();

    for (int i = index; i <= siblings.size() - 1; i++)
    {
      siblingsToShiftList.add(siblings.get(i));
    }

    Iterator<String> siblingsToShiftIterator = siblingsToShiftList.iterator();

    int nthSibling;

    while (siblingsToShiftIterator.hasNext())
    {
      Document siblingFilter = new Document(FIELD_ID, new ObjectId(siblingsToShiftIterator.next()));
      nthSibling = (int) groupCollection.find(siblingFilter).iterator().next().get(FIELD_NTH_SIBLING);
      Document siblingNumberDocument = new Document(FIELD_NTH_SIBLING, nthSibling - 1);
      Document setFilter = new Document("$set", siblingNumberDocument);
      groupCollection.findOneAndUpdate(siblingFilter, setFilter);
    }

    //add checker for successful change, set return accordingly
    return true;
  }

  private int getChildrenCount(String parentId)
  {
    if (!doesGroupExist(parentId))
    {
      throw new IllegalArgumentException(ERR_MSG_PARENT_NONEXISTENT);
    }

    Document filter = new Document(FIELD_ID, new ObjectId(parentId));

    return ((ArrayList<String>) groupCollection.find(filter).iterator().next().get(FIELD_CHILDREN)).size();
  }

  @Override
  public int getNthSibling(String parentId)
  {
    //if we decide to start counting sibling from 0, then we can set root as a -negative number, and start counting from 0
    if (parentId != null)
    {
      return getChildrenCount(parentId) + 1;
    }

    //means root
    return 0;
  }

  @Override
  public List<Group> getAllRootGroups(TenantId tenantId)
  {
    List<Group> groupsToReturn = new ArrayList<>();

    Document tenantIdFilter = new Document(FIELD_TENANT_ID, tenantId.getId() );
    Document rootFilter = new Document(FIELD_PARENT_ID, null);
    Bson andFilter = and(tenantIdFilter, rootFilter);
    Iterator<Document> groupDocumentIterator = groupCollection.find(andFilter).iterator();
    while (groupDocumentIterator.hasNext())
    {
      groupsToReturn.add(convertToGroupObject(groupDocumentIterator.next()));
    }
    return groupsToReturn;
  }

  /*
  @Override
  public List<GroupTree> getAllGroups()
  {
    String rootFilterString = null;
    Document filter = new Document(FIELD_PARENT_ID, rootFilterString);

    List<GroupTree> allGroups = new ArrayList<>();

    Iterator<Document> rootGroupDocumentIterator = groupCollection.find(filter).iterator();
    List<String> allRootGroupNodes = new ArrayList<>();
    while (rootGroupDocumentIterator.hasNext())
    {
      String nextRootGroupNodeId = ((ObjectId) rootGroupDocumentIterator.next().get(FIELD_ID)).toHexString();
      allRootGroupNodes.add(nextRootGroupNodeId);
    }

    Iterator<String> allRootGroupNodesIterator = allRootGroupNodes.iterator();
    while (allRootGroupNodesIterator.hasNext())
    {
      allGroups.add(getGroupSubTree(allRootGroupNodesIterator.next()));
    }

    return allGroups;
  }
   */

  private GroupTree getGroupSubTree(String startingFilterString)
  {
    //add a check for starting group existence
    Document filter = new Document(FIELD_PARENT_ID, startingFilterString);

    if (startingFilterString != null)
    {
      filter = new Document(FIELD_ID, new ObjectId(startingFilterString));
    }

    //queue of id strings
    Queue<String> groupsToTraverse = new PriorityQueue<>();
    //hashmap used to keep track/link parent to children
    Map<String, GroupTree> map = new HashMap<>();

    String startingGroupId = ((ObjectId) groupCollection.find(filter).iterator().next().get(FIELD_ID)).toHexString();

    groupsToTraverse.add(startingGroupId);

    String groupIdToDo;

    while (!groupsToTraverse.isEmpty())
    {
      groupIdToDo = groupsToTraverse.peek();

      Group mappedGroup = mapToGroupObject(groupIdToDo);

      String parentId = null;

      if (mappedGroup.getParent() != null)
      {
        parentId = mappedGroup.getParent().getId();
      }

      GroupTree currentGroup = new GroupTree(mappedGroup.getId().getId(), parentId, mappedGroup.getTenantId().getId(), mappedGroup.getNumber());
      currentGroup.setName(mappedGroup.getName());
      currentGroup.setNthSibling(mappedGroup.getNthSibling());

      //load queue for other iterations
      Iterator<GroupId> childrenIterator = mappedGroup.getChildren().iterator();
      while (childrenIterator.hasNext())
      {
        String nextChild = childrenIterator.next().getId();
        groupsToTraverse.add(nextChild);
      }

      // set/connect child(s) with parent
      if (currentGroup.getParent() != null)
      {
        GroupTree parentObject = map.get(currentGroup.getParent());
        parentObject.addChild(currentGroup);
      }

      map.put(groupIdToDo, currentGroup);

      //remove peek
      groupsToTraverse.remove();
    }

    return map.get(startingGroupId);
  }

  private Group mapToGroupObject(String filterId)
  {
    Document filter = new Document(FIELD_ID, new ObjectId(filterId));
    Document returnedGroupDocument = groupCollection.find(filter).iterator().next();

    return convertToGroupObject(returnedGroupDocument);
  }

  private Group convertToGroupObject(Document returnedGroupDocument)
  {
    GroupId parentIdObject = null;

    String id = ((ObjectId) returnedGroupDocument.get(FIELD_ID)).toHexString();
    String parentId = (String) returnedGroupDocument.get(FIELD_PARENT_ID);
    String tenantId = (String) returnedGroupDocument.get(FIELD_TENANT_ID);
    String name = (String) returnedGroupDocument.get(FIELD_NAME);
    int nthSibling = (int) returnedGroupDocument.get(FIELD_NTH_SIBLING);
    String description = (String) returnedGroupDocument.get(FIELD_DESCRIPTION);
    List<String> children = (ArrayList<String>) returnedGroupDocument.get(FIELD_CHILDREN);

    //needed for constructor of Group
    if (parentId != null)
    {
      parentIdObject = new GroupId(parentId);
    }

    Group groupToReturn = new Group(new GroupId(id), parentIdObject, new TenantId(tenantId), name);
    groupToReturn.setName(description);
    groupToReturn.setNthSibling(nthSibling);

    //add all children into group object's list one at a time
    Iterator<String> childrenIterator = children.iterator();
    while (childrenIterator.hasNext())
    {
      groupToReturn.addChild(new GroupId(childrenIterator.next()));
    }

    return groupToReturn;
  }

  @Override
  public Group findById(EntityId entityId)
  {
    String filterId = entityId.getId();

    Group groupToReturn = mapToGroupObject(filterId);
    return groupToReturn;
  }

  private boolean setNthSibling(String groupId, int nthSibling)
  {
    Document groupFilter = new Document(FIELD_ID, new ObjectId(groupId));
    Document nthSiblingFilter = new Document(FIELD_NTH_SIBLING, nthSibling);
    Document setFilter = new Document("$set", nthSiblingFilter);
    groupCollection.findOneAndUpdate(groupFilter, setFilter);

    int updatedNthSibling = (int) groupCollection.find(groupFilter).iterator().next().get(FIELD_NTH_SIBLING);
    boolean isSuccessfulUpdate = nthSibling == updatedNthSibling;

    return isSuccessfulUpdate;
  }
}
