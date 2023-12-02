/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.model.salary;

import mn.erin.domain.base.model.EntityId;

/**
 * @author Tamir
 */
public class OrganizationId extends EntityId
{
  public OrganizationId(String id)
  {
    super(id);
  }

  public static OrganizationId valueOf(String id)
  {
    return new OrganizationId(id);
  }
}

