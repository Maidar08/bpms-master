/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.model.account;

import mn.erin.domain.base.model.EntityId;

/**
 * @author EBazarragchaa
 */
public class AccountId extends EntityId
{
  public AccountId(String id)
  {
    super(id);
  }

  public static AccountId valueOf(String id)
  {
    return new AccountId(id);
  }
}
