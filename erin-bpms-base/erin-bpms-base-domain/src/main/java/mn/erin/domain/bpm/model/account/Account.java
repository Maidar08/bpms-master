/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.model.account;

import java.io.Serializable;
import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.model.Entity;

/**
 * @author EBazarragchaa
 */
public class Account implements Entity<Account>, Serializable
{
  private final AccountId id;
  private final String type;

  public Account(AccountId id, String type)
  {
    this.id = Objects.requireNonNull(id, "Account id is required!");
    this.type = Validate.notBlank(type, "Account type is required!");
  }

  public AccountId getId()
  {
    return id;
  }

  public String getType()
  {
    return type;
  }

  @Override
  public boolean sameIdentityAs(Account other)
  {
    return other != null && (this.id.equals(other.id));
  }
}
