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
public class PropertyId extends EntityId
{
  public PropertyId(String id)
  {
    super(id);
  }

  public static PropertyId valueOf(String id)
  {
    return new PropertyId(id);
  }
}
