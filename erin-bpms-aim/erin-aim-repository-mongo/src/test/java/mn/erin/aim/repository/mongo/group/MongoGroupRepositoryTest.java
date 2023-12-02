package mn.erin.aim.repository.mongo.group;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import mn.erin.aim.repository.mongo.MongoGroupRepository;
import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.tenant.TenantId;

/**
 * @author Zorig
 */
@Ignore
public class MongoGroupRepositoryTest
{
  MongoCollection<Document> groupCollection;
  MongoGroupRepository mongoGroupRepository;

  @Before
  public void setUp()
  {
    groupCollection = Mockito.mock(MongoCollection.class);
    mongoGroupRepository = new MongoGroupRepository(groupCollection);
  }

  @Test
  public void createGroupTestForRootGroup()
  {
    String tenantId = "123";
    String groupId = "00897";
    String name = "Itgel";
    Group testGroup = new Group(new GroupId(groupId), null, new TenantId(tenantId), name);

    //mongoGroupRepository.createGroup(testGroup);

    ArgumentCaptor<Document> groupArgumentCaptor = ArgumentCaptor.forClass(Document.class);
    Mockito.verify(groupCollection).insertOne(groupArgumentCaptor.capture());
    Assert.assertNull(groupArgumentCaptor.getValue().get("parentId"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void forDoesParentExist()
  {
    String parentId = "8723";
    String tenantId = "123";
    String groupId = "00897";
    String name = "Itgel";
    Group testGroup = new Group(new GroupId(groupId), new GroupId(parentId), new TenantId(tenantId), name);

    Document filter = new Document("parentId", new ObjectId(parentId));

    MongoCursor<Document> mockIterator = Mockito.mock(MongoCursor.class);

    FindIterable<Document> mockIterable = Mockito.mock(FindIterable.class);

    Mockito.when(mockIterable.iterator()).thenReturn(mockIterator);

    Mockito.when(mockIterator.hasNext()).thenReturn(false);

    Mockito.when(groupCollection.find(filter)).thenReturn(mockIterable);

    //mongoGroupRepository.createGroup(testGroup);
  }
}
