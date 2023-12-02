/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.domain.bpm.model.property;

import mn.erin.domain.base.model.EntityId;

/**
 * @author Zorig
 */
public class PropertyServiceId extends EntityId
{
  public PropertyServiceId(String id)
  {
    super(id);
  }

  public static PropertyServiceId valueOf(String id)
  {
    return new PropertyServiceId(id);
  }
}
