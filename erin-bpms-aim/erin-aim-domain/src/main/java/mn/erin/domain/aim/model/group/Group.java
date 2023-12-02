package mn.erin.domain.aim.model.group;

import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.base.model.Entity;
import mn.erin.domain.base.model.Node;

/**
 * @author Zorig
 */
public class Group extends Node<GroupId> implements Entity<Group>
{
  private final GroupId id;
  private final TenantId tenantId;
  private final String number;
  private String name;

  public Group(GroupId id, GroupId parentId, TenantId tenantId, String number)
  {
    super(id, parentId);
    this.id = id;
    this.tenantId = tenantId;
    this.number = number;
  }

  public GroupId getId()
  {
    return id;
  }

  public TenantId getTenantId()
  {
    return tenantId;
  }

  public String getNumber()
  {
    return number;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  @Override
  public boolean sameIdentityAs(Group other)
  {
    return other != null && other.id.equals(this.id);
  }
}
