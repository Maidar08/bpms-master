/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.model.loan;

import mn.erin.domain.base.model.EntityId;

/**
 * @author Tamir
 */
public class BorrowerId extends EntityId
{
  public BorrowerId(String id)
  {
    super(id);
  }

  public static BorrowerId valueOf(String id)
  {
    return new BorrowerId(id);
  }
}
