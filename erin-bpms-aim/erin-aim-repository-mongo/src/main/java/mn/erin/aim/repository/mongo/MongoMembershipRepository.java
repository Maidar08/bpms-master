package mn.erin.aim.repository.mongo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.apache.commons.lang3.Validate;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.membership.MembershipId;
import mn.erin.domain.aim.model.role.RoleId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.MembershipRepository;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class MongoMembershipRepository implements MembershipRepository
{
  private static final String ERR_MSG_MEMBERSHIP_ID = "MembershipID cannot be null!";
  private static final String ERR_MSG_USER_ID = "UserID cannot be null!";
  private static final String ERR_MSG_GROUP_ID = "GroupID cannot be null!";
  private static final String ERR_MSG_ROLE_ID = "RoleID cannot be null!";
  private static final String ERR_MSG_TENANT_ID = "TenantID cannot be null!";

  private static final String FIELD_ID = "_id";
  private static final String FIELD_USER_ID = "userId";
  private static final String FIELD_GROUP_ID = "groupId";
  private static final String FIELD_ROLE_ID = "roleId";
  private static final String FIELD_TENANT_ID = "tenantId";

  private final MongoCollection<Document> collection;

  public MongoMembershipRepository(MongoCollection<Document> collection)
  {
    this.collection = Objects.requireNonNull(collection, "Collection cannot be null!");
  }

  @Override
  public Membership create(UserId userId, GroupId groupId, RoleId roleId, TenantId tenantId) throws AimRepositoryException
  {
    Validate.notNull(userId, ERR_MSG_USER_ID);
    Validate.notNull(groupId, ERR_MSG_GROUP_ID);
    Validate.notNull(roleId, ERR_MSG_ROLE_ID);
    Validate.notNull(tenantId, ERR_MSG_TENANT_ID);

    Bson filter = and(eq(FIELD_USER_ID, userId), eq(FIELD_GROUP_ID, groupId));

    try
    {
      FindIterable<Document> result = collection.find(filter);
      Iterator<Document> iterator = result.iterator();
      iterator.next();
    }
    catch (NoSuchElementException e)
    {
      Document membershipAsDocument = new Document();

      ObjectId membershipId = new ObjectId(new Date());

      membershipAsDocument.put(FIELD_ID, membershipId);
      membershipAsDocument.put(FIELD_USER_ID, userId);
      membershipAsDocument.put(FIELD_GROUP_ID, groupId);
      membershipAsDocument.put(FIELD_ROLE_ID, roleId);
      membershipAsDocument.put(FIELD_TENANT_ID, tenantId);

      collection.insertOne(membershipAsDocument);

      return new Membership(MembershipId.valueOf(membershipId.toHexString()), userId, groupId, roleId);
    }

    throw new AimRepositoryException("User with the ID: [" + userId.getId() +
        "] already exists in the group with the ID: [" + groupId.getId() + "]");
  }

  @Override
  public Membership findByUserId(UserId userId)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Membership> listAllByUserId(TenantId tenantId, UserId userId)
  {
    Validate.notNull(userId, ERR_MSG_USER_ID);

    Bson tenantIdFilter = eq(FIELD_TENANT_ID, tenantId);
    Bson userIdFilter = eq(FIELD_USER_ID, userId);
    Bson filter = and(tenantIdFilter, userIdFilter);

    FindIterable<Document> documents = collection.find(filter);

    if (documents == null)
    {
      return Collections.emptyList();
    }

    Iterator<Document> iterator = documents.iterator();

    List<Membership> memberships = new ArrayList<>();

    while (iterator.hasNext())
    {
      Document document = iterator.next();
      memberships.add(mapToMembership(document));
    }

    return memberships;
  }

  @Override
  public List<Membership> listAllByGroupId(TenantId tenantId, GroupId groupId)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Membership> listAllBy(TenantId tenantId, GroupId groupId, RoleId roleId) throws AimRepositoryException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean delete(MembershipId membershipId)
  {
    Validate.notNull(membershipId, ERR_MSG_MEMBERSHIP_ID);

    Bson filter = eq(FIELD_ID, new ObjectId(membershipId.getId()));
    Document deletedMembershipAsDocument = collection.findOneAndDelete(filter);
    return deletedMembershipAsDocument != null;
  }

  @Override
  public Collection<Membership> listAllByRole(RoleId roleId) throws AimRepositoryException
  {
    return null;
  }

  private Membership mapToMembership(Document document)
  {
    Document userIdAsDocument = (Document) document.get(FIELD_USER_ID);
    Document groupIdAsDocument = (Document) document.get(FIELD_GROUP_ID);
    Document roleIdAsDocument = (Document) document.get(FIELD_ROLE_ID);
    Document tenantIdAsDocument = (Document) document.get(FIELD_TENANT_ID);

    MembershipId membershipId = MembershipId.valueOf(document.getObjectId(FIELD_ID).toHexString());
    UserId userId = UserId.valueOf((String) userIdAsDocument.get(FIELD_ID));
    GroupId groupId = GroupId.valueOf((String) groupIdAsDocument.get(FIELD_ID));
    RoleId roleId = RoleId.valueOf((String) roleIdAsDocument.get(FIELD_ID));
    TenantId tenantId = TenantId.valueOf((String) tenantIdAsDocument.get(FIELD_ID));

    Membership membership = new Membership(membershipId, userId, groupId, roleId);
    membership.setTenantId(tenantId);
    return membership;
  }
}
