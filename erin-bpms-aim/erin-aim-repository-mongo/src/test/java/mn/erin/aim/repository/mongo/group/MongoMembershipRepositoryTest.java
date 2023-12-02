package mn.erin.aim.repository.mongo.group;

import java.util.List;
import java.util.NoSuchElementException;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import mn.erin.aim.repository.mongo.MongoMembershipRepository;
import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.role.RoleId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.UserId;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class MongoMembershipRepositoryTest
{
  private static final TenantId TENANT_ID = TenantId.valueOf("-tenant-");

  @Mock(name = "findIterable")
  protected FindIterable<Document> findIterable;

  @Mock(name = "mongoCursor")
  protected MongoCursor<Document> mongoCursor;

  @Mock(name = "collection")
  protected MongoCollection<Document> collection;

  private MongoMembershipRepository membershipRepository;

  @Before
  public void before()
  {
    MockitoAnnotations.initMocks(this);
    Mockito.when(findIterable.iterator()).thenReturn(mongoCursor);
    membershipRepository = new MongoMembershipRepository(collection);
  }

  @Test
  public void should_return_empty_list_on_list_all()
  {
    Mockito.when(collection.find(Mockito.any(Bson.class)))
      .thenReturn(null);
    List<Membership> memberships = membershipRepository.listAllByUserId(TENANT_ID, UserId.valueOf("-user-"));
    assertTrue(memberships.isEmpty());
  }

  @Test
  public void should_return_memberships()
  {
    mockCollectionFind(getDummyMembership());
    List<Membership> memberships = membershipRepository.listAllByUserId(TENANT_ID, UserId.valueOf("-user-"));
    assertEquals(1, memberships.size());
  }

  @Test(expected = AimRepositoryException.class)
  public void should_throw_exception_on_creating() throws AimRepositoryException
  {
    mockCollectionFind(getDummyMembership());
    membershipRepository.create(UserId.valueOf("user"), GroupId.valueOf("group"), RoleId.valueOf("role"), TENANT_ID);
  }

  @Test
  public void should_create_membership() throws AimRepositoryException
  {
    Mockito.when(collection.find(Mockito.any(Bson.class)))
      .thenReturn(findIterable);
    Mockito.when(mongoCursor.next()).thenThrow(NoSuchElementException.class);
    Membership membership = membershipRepository.create(UserId.valueOf("user"), GroupId.valueOf("group"), RoleId.valueOf("role"), TENANT_ID);
    assertNotNull(membership);
  }

  private void mockCollectionFind(Document documentToReturn)
  {
    Mockito.when(collection.find(Mockito.any(Bson.class)))
      .thenReturn(findIterable);
    Mockito.when(mongoCursor.hasNext()).thenReturn(true, false);
    Mockito.when(mongoCursor.next()).thenReturn(documentToReturn);
  }

  private Document getDummyMembership()
  {
    Document membershipAsDocument = new Document();

    String _id = "_id";

    membershipAsDocument.put(_id, new ObjectId());
    membershipAsDocument.put("userId", new Document(_id, "user"));
    membershipAsDocument.put("groupId", new Document(_id, "group"));
    membershipAsDocument.put("roleId", new Document(_id, "role"));
    membershipAsDocument.put("tenantId", new Document(_id, "-tenant-"));

    return membershipAsDocument;
  }
}
