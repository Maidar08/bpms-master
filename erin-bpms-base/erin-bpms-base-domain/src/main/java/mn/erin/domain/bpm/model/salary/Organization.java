/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.model.salary;

import java.io.Serializable;

import mn.erin.domain.base.model.Entity;

/**
 * @author Tamir
 */
public class Organization implements Entity<Organization>, Serializable
{
  private final OrganizationId organizationId;
  private String name;
  private String regionName;

  public Organization(OrganizationId organizationId, String name)
  {
    this.organizationId = organizationId;
    this.name = name;
  }

  public Organization(OrganizationId organizationId, String name, String regionName)
  {
    this.organizationId = organizationId;
    this.name = name;
    this.regionName = regionName;
  }

  public OrganizationId getOrganizationId()
  {
    return organizationId;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getRegionName()
  {
    return regionName;
  }

  public void setRegionName(String regionName)
  {
    this.regionName = regionName;
  }

  @Override
  public boolean sameIdentityAs(Organization other)
  {
    return null != other && other.organizationId.sameValueAs(this.organizationId);
  }
}
