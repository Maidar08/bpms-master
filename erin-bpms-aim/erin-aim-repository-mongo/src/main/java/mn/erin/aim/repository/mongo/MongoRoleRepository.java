package mn.erin.aim.repository.mongo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.role.Role;
import mn.erin.domain.aim.model.role.RoleId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.repository.RoleRepository;
import mn.erin.domain.base.model.EntityId;

/**
 * @author Zorig
 */
public class MongoRoleRepository implements RoleRepository
{
  protected final MongoCollection<Document> roleCollection;

  public MongoRoleRepository(MongoCollection<Document> mongoCollection)
  {
    this.roleCollection = mongoCollection;
  }

  @Override
  public Role create(TenantId tenantId, String id, String name, Collection<Permission> permissions)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Role create(TenantId tenantId, String id, String name) throws AimRepositoryException
  {
    ObjectId roleId = new ObjectId(id);
    String tenantIdToStore = tenantId.getId();

    Document roleDocumentToStore = new Document();
    roleDocumentToStore.put("_id", roleId);
    roleDocumentToStore.put("name", name);
    roleDocumentToStore.put("tenantId", tenantIdToStore);

    try
    {
      roleCollection.insertOne(roleDocumentToStore);
    }
    catch (MongoException e)
    {
      throw new AimRepositoryException(e.getMessage());
    }

    Document savedRoleDocument = roleCollection.find(new Document("_id", roleId)).iterator().next();

    return convertToRole(savedRoleDocument);
  }

  @Override
  public List<Role> listAll(TenantId tenantId)
  {
    List<Role> rolesToReturn = new ArrayList<>();

    Iterator<Document> roleDocumentsIterator = roleCollection.find().iterator();
    while (roleDocumentsIterator.hasNext())
    {
      rolesToReturn.add(convertToRole(roleDocumentsIterator.next()));
    }

    return rolesToReturn;
  }

  @Override
  public Role findById(EntityId entityId)
  {
    Document roleIdFilter = new Document("_id", new ObjectId(entityId.getId()));
    Iterator<Document> returnedRoleDocumentIterator = roleCollection.find(roleIdFilter).iterator();

    if (returnedRoleDocumentIterator.hasNext())
    {
      return convertToRole(returnedRoleDocumentIterator.next());
    }
    return null;
  }

  @Override
  public Collection<Role> findAll()
  {
    List<Role> rolesListToReturn = new ArrayList<>();

    Iterator<Document> returnedRoleDocumentIterator = roleCollection.find().iterator();

    while (returnedRoleDocumentIterator.hasNext())
    {
      rolesListToReturn.add(convertToRole(returnedRoleDocumentIterator.next()));
    }
    return rolesListToReturn;
  }

  private Role convertToRole(Document roleDocument)
  {
    String roleIdString = ((ObjectId)roleDocument.get("_id")).toHexString();
    RoleId roleId = new RoleId(roleIdString);
    TenantId tenantId = new TenantId((String)roleDocument.get("tenantId"));
    String name = (String)roleDocument.get("name");
    String description = (String)roleDocument.get("description");

    Role roleToReturn = new Role(roleId, tenantId, name);
    roleToReturn.setDescription(description);

    return roleToReturn;
  }


}
