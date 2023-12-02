/*
 * Copyright (C) ERIN SYSTEMS LLC, 2020. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.domain.bpm.model.product;

import mn.erin.domain.base.model.EntityId;

/**
 * @author Zorig
 */
public class ProductId extends EntityId
{
  public ProductId(String id)
  {
    super(id);
  }

  public static ProductId valueOf(String id)
  {
    return new ProductId(id);
  }
}
