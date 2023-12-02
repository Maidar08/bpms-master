/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.model.citizen;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author Tamir
 */
public class PassportInfo implements Serializable
{
  private String passportAddress;
  private LocalDate issueDate;
  private LocalDate expireDate;
  private String image;

  public PassportInfo(String passportAddress, LocalDate issueDate, LocalDate expireDate)
  {
    this.passportAddress = passportAddress;
    this.issueDate = issueDate;
    this.expireDate = expireDate;
  }

  public PassportInfo(String passportAddress, String image, LocalDate issueDate, LocalDate expireDate)
  {
    this.passportAddress = passportAddress;
    this.image = image;
    this.issueDate = issueDate;
    this.expireDate = expireDate;
  }

  public String getPassportAddress()
  {
    return passportAddress;
  }

  public void setPassportAddress(String passportAddress)
  {
    this.passportAddress = passportAddress;
  }

  public String getImage()
  {
    return image;
  }

  public void setImage(String image)
  {
    this.image = image;
  }

  public LocalDate getIssueDate()
  {
    return issueDate;
  }

  public void setIssueDate(LocalDate issueDate)
  {
    this.issueDate = issueDate;
  }

  public LocalDate getExpireDate()
  {
    return expireDate;
  }

  public void setExpireDate(LocalDate expireDate)
  {
    this.expireDate = expireDate;
  }
}
