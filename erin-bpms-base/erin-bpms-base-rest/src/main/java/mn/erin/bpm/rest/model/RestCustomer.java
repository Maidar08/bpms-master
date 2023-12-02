/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.rest.model;

import java.util.List;

/**
 * @author Tamir
 */
public class RestCustomer
{
  private String registerNumber;
  private String cifNumber;

  private String firstName;
  private String lastName;
  private String fullName;

  private List<String> emails;
  private List<String> phoneNumbers;
  private List<String> addressList;
  private boolean isRiskyCustomer = false;
  private String passportAddress;

  public RestCustomer()
  {

  }

  public RestCustomer(String registerNumber, String cifNumber, String firstName, String lastName, String fullName, List<String> emails,
      List<String> phoneNumbers, List<String> addressList)
  {
    this.registerNumber = registerNumber;
    this.cifNumber = cifNumber;
    this.firstName = firstName;
    this.lastName = lastName;
    this.fullName = fullName;
    this.emails = emails;
    this.phoneNumbers = phoneNumbers;
    this.addressList = addressList;
  }

  public String getRegisterNumber()
  {
    return registerNumber;
  }

  public void setRegisterNumber(String registerNumber)
  {
    this.registerNumber = registerNumber;
  }

  public String getCifNumber()
  {
    return cifNumber;
  }

  public void setCifNumber(String cifNumber)
  {
    this.cifNumber = cifNumber;
  }

  public String getFirstName()
  {
    return firstName;
  }

  public void setFirstName(String firstName)
  {
    this.firstName = firstName;
  }

  public String getLastName()
  {
    return lastName;
  }

  public void setLastName(String lastName)
  {
    this.lastName = lastName;
  }

  public String getFullName()
  {
    return fullName;
  }

  public void setFullName(String fullName)
  {
    this.fullName = fullName;
  }

  public List<String> getEmails()
  {
    return emails;
  }

  public void setEmails(List<String> emails)
  {
    this.emails = emails;
  }

  public List<String> getPhoneNumbers()
  {
    return phoneNumbers;
  }

  public void setPhoneNumbers(List<String> phoneNumbers)
  {
    this.phoneNumbers = phoneNumbers;
  }

  public List<String> getAddressList()
  {
    return this.addressList;
  }

  public void setAddressList(List<String> addressList)
  {
    this.addressList = addressList;
  }

  public boolean isRiskyCustomer()
  {
    return isRiskyCustomer;
  }

  public void setRiskyCustomer(boolean riskyCustomer)
  {
    isRiskyCustomer = riskyCustomer;
  }

  public String getPassportAddress()
  {
    return passportAddress;
  }

  public void setPassportAddress(String passportAddress)
  {
    this.passportAddress = passportAddress;
  }
}
