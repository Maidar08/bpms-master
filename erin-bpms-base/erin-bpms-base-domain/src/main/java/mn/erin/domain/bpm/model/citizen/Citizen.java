/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.model.citizen;

import mn.erin.domain.base.model.person.Person;
import mn.erin.domain.base.model.person.PersonId;

/**
 * @author Tamir
 */
public class Citizen extends Person
{
  private String nationality;
  private PassportInfo passportInfo;

  public Citizen(PersonId personId)
  {
    super(personId);
  }

  public Citizen(PersonId personId, String nationality, PassportInfo passportInfo)
  {
    super(personId);
    this.nationality = nationality;
    this.passportInfo = passportInfo;
  }

  public String getNationality()
  {
    return nationality;
  }

  public void setNationality(String nationality)
  {
    this.nationality = nationality;
  }

  public PassportInfo getPassportInfo()
  {
    return passportInfo;
  }

  public void setPassportInfo(PassportInfo passportInfo)
  {
    this.passportInfo = passportInfo;
  }
}
