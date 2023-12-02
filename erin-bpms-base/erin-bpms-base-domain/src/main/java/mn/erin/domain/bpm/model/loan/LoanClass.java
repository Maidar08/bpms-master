/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.model.loan;

import java.io.Serializable;

import mn.erin.domain.base.model.ValueObject;

/**
 * @author Tamir
 */
public class LoanClass implements ValueObject<LoanClass>, Serializable
{
  private Integer rank;
  private String name;

  public LoanClass(int rank, String name)
  {
    this.rank = rank;
    this.name = name;
  }

  public Integer getRank()
  {
    return rank;
  }

  public void setRank(Integer rank)
  {
    this.rank = rank;
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
  public boolean sameValueAs(LoanClass other)
  {
    return null != other && other.getRank() == this.rank;
  }
}
