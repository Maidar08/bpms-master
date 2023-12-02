package mn.erin.aim.repository.mongo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.ContactInfo;
import mn.erin.domain.aim.model.user.User;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.model.user.UserInfo;
import mn.erin.domain.aim.repository.UserRepository;
import mn.erin.domain.base.model.EntityId;

import static com.mongodb.client.model.Filters.and;

/**
 * @author Zorig
 */
public class MongoUserRepository implements UserRepository
{
  protected final MongoCollection<Document> userCollection;
  private static final String TENANT_ID = "tenantId";
  private static final String DOCUMENT_KEY_FIRST_NAME = "firstName";
  private static final String DOCUMENT_KEY_LAST_NAME = "lastName";
  private static final String DOCUMENT_KEY_PHONE_NUMBER = "phoneNumber";
  private static final String DOCUMENT_KEY_EMAIL = "email";
  
  public MongoUserRepository(MongoCollection<Document> mongoCollection)
  {
    this.userCollection = mongoCollection;
  }

  @Override
  public List<User> getAllUsers(TenantId tenantId)
  {
    Document tenantIdFilter = new Document(TENANT_ID, tenantId.getId());

    Iterator<Document> returnedUserDocumentIterator = userCollection.find(tenantIdFilter).iterator();
    List<User> usersToReturn = new ArrayList<>();

    while (returnedUserDocumentIterator.hasNext())
    {
      Document currentUserDocument = returnedUserDocumentIterator.next();
      usersToReturn.add(convertToUser(currentUserDocument));
    }

    return usersToReturn;
  }

  @Override
  public User createUser(TenantId tenantId, UserInfo userInfo, ContactInfo contactInfo) throws AimRepositoryException
  {
    ObjectId userId = new ObjectId(new Date());
    String tenantIdToStore = tenantId.getId();
    String firstName = userInfo.getFirstName();
    String lastName = userInfo.getLastName();
    String displayName = userInfo.getDisplayName();
    String email = contactInfo.getEmail();
    String phoneNumber = contactInfo.getPhoneNumber();

    Document userDocumentToStore = new Document();
    userDocumentToStore.put("_id", userId);
    userDocumentToStore.put(TENANT_ID, tenantIdToStore);
    userDocumentToStore.put(DOCUMENT_KEY_FIRST_NAME, firstName);
    userDocumentToStore.put(DOCUMENT_KEY_LAST_NAME, lastName);
    userDocumentToStore.put(DOCUMENT_KEY_EMAIL, email);
    userDocumentToStore.put(DOCUMENT_KEY_PHONE_NUMBER, phoneNumber);
    userDocumentToStore.put("displayName", displayName);

    try
    {
      userCollection.insertOne(userDocumentToStore);
    }
    catch (MongoException e)
    {
      throw new AimRepositoryException(e.getMessage());
    }

    Document storedDocument = userCollection.find(new Document("_id", userId)).iterator().next();
    return convertToUser(storedDocument);
  }
/*
<<<<<<< Updated upstream:modules/aim/erin-aim-repository-mongo/src/main/java/mn/erin/aim/repository/mongo/MongoUserRepository.java
=======


>>>>>>> Stashed changes:modules/aim/erin-aim-repository-mongo/src/main/java/mn/erin/aim/repository/mongo/MongoUserRepository.java
 */
  @Override
  public User getUserByTenantId(TenantId tenantId, UserId userId) throws AimRepositoryException
  {
    Document userIdFilter = new Document("_id", new ObjectId(userId.getId()));
    Document tenantIdFilter = new Document(TENANT_ID, tenantId.getId());
    Bson andFilter = and(userIdFilter, tenantIdFilter);

    Iterator<Document> returnedUserDocumentIterator = userCollection.find(andFilter).iterator();

    if (returnedUserDocumentIterator.hasNext())
    {
      return convertToUser(returnedUserDocumentIterator.next());
    }
    //or return null?
    throw new AimRepositoryException("User not found!");
  }

  @Override
  public boolean deleteUser(UserId userId)
  {
    Document userIdFilter = new Document("_id", new ObjectId(userId.getId()));
    Document result = userCollection.findOneAndDelete(userIdFilter);

    return !userCollection.find(userIdFilter).iterator().hasNext();
  }

  @Override
  public User updateUserInfo(UserId userId, UserInfo newUserInfo) throws AimRepositoryException
  {
    Document userIdFilter = new Document("_id", new ObjectId(userId.getId()));
    String firstName = newUserInfo.getFirstName();
    String lastName = newUserInfo.getLastName();
    String displayName = newUserInfo.getDisplayName();

    Document updateFirstNameDocument = new Document(DOCUMENT_KEY_FIRST_NAME, firstName);
    Document updateLastNameDocument = new Document(DOCUMENT_KEY_LAST_NAME, lastName);
    Document updateDisplayNameDocument = new Document("displayName", firstName);
    Bson andFilter = and(updateFirstNameDocument, updateLastNameDocument, updateDisplayNameDocument);
    Document setAllUserInfoDocument = new Document("$set", andFilter);

    Document result = userCollection.findOneAndUpdate(userIdFilter, setAllUserInfoDocument);

    return convertToUser(userCollection.find(userIdFilter).iterator().next());
  }

  @Override
  public User updateContactInfo(UserId userId, ContactInfo contactInfo) throws AimRepositoryException
  {
    Document userIdFilter = new Document("_id", new ObjectId(userId.getId()));
    String email = contactInfo.getEmail();
    String phoneNumber = contactInfo.getPhoneNumber();

    Document updateEmailDocument = new Document(DOCUMENT_KEY_EMAIL, email);
    Document updatePhoneNumberDocument = new Document(DOCUMENT_KEY_PHONE_NUMBER, phoneNumber);
    Bson andFilter = and(updateEmailDocument, updatePhoneNumberDocument);
    Document setAllUserInfoDocument = new Document("$set", andFilter);

    Document result = userCollection.findOneAndUpdate(userIdFilter, setAllUserInfoDocument);

    return convertToUser(userCollection.find(userIdFilter).iterator().next());
  }

  @Override
  public User changeTenant(UserId userId, TenantId newTenantId)
  {
    Document userIdFilter = new Document("_id", new ObjectId(userId.getId()));
    String tenantId = newTenantId.getId();

    Document updateTenantIdDocument = new Document(TENANT_ID, tenantId);
    Document setAllUserInfoDocument = new Document("$set", updateTenantIdDocument);

    Document result = userCollection.findOneAndUpdate(userIdFilter, setAllUserInfoDocument);

    return convertToUser(userCollection.find(userIdFilter).iterator().next());
  }

  @Override
  public User findById(EntityId entityId)
  {
    Document userIdFilter = new Document("_id", new ObjectId(entityId.getId()));

    Iterator<Document> returnedUserDocumentIterator = userCollection.find(userIdFilter).iterator();

    if (returnedUserDocumentIterator.hasNext())
    {
      return convertToUser(returnedUserDocumentIterator.next());
    }
    return null;
  }

  @Override
  public Collection<User> findAll()
  {
    List<User> usersListToReturn = new ArrayList<>();

    Iterator<Document> returnedUserDocumentIterator = userCollection.find().iterator();

    while (returnedUserDocumentIterator.hasNext())
    {
      usersListToReturn.add(convertToUser(returnedUserDocumentIterator.next()));
    }
    return usersListToReturn;
  }

  private User convertToUser(Document userDocument)
  {
    String userIdString = ((ObjectId) userDocument.get("_id")).toHexString();
    UserId userId = new UserId(userIdString);
    TenantId tenantId = new TenantId((String) userDocument.get(TENANT_ID));
    User userToReturn = new User(userId, tenantId);

    String email = (String)userDocument.get(DOCUMENT_KEY_EMAIL);
    String phoneNumber = (String)userDocument.get(DOCUMENT_KEY_PHONE_NUMBER);
    ContactInfo contactInfo = new ContactInfo(email, phoneNumber);

    String firstName = (String)userDocument.get(DOCUMENT_KEY_FIRST_NAME);
    String lastName = (String)userDocument.get(DOCUMENT_KEY_LAST_NAME);
    UserInfo userInfo = new UserInfo(firstName, lastName);

    userToReturn.setContactInfo(contactInfo);
    userToReturn.setUserInfo(userInfo);

    return userToReturn;
  }

}
