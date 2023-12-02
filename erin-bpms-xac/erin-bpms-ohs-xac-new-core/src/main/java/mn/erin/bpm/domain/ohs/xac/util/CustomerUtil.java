package mn.erin.bpm.domain.ohs.xac.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.model.person.AddressInfo;
import mn.erin.domain.base.model.person.ContactInfo;
import mn.erin.domain.base.model.person.PersonId;
import mn.erin.domain.base.model.person.PersonInfo;
import mn.erin.domain.bpm.model.customer.Customer;
import mn.erin.domain.bpm.service.BpmServiceException;

import static mn.erin.domain.bpm.BpmMessagesConstants.CUSTOMER_NAME_IS_BLANK_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CUSTOMER_NAME_IS_BLANK_MESSAGE;

/**
 * @author Tamir
 */
public final class CustomerUtil
{
  private static final Logger LOGGER = LoggerFactory.getLogger(CustomerUtil.class);

  private static final String MSG_ERR_OCCUPANCY_NOT_FOUND = "Customer occupancy field not found!";
  private static final String EMPTY_VALUE = "";

  private static final String CUSTNO = "CUSTNO";
  private static final String CUSTPERSONAL = "Custpersonal";

  private static final String TELEPHNO = "TELEPHNO";
  private static final String FSTNAME = "FSTNAME";
  private static final String LSTNAME = "LSTNAME";

  private static final String CUSTPROF = "Custprof";

  private static final String ADDRLN_CITY = "ADDRLN1";
  public static final String ADDRLN_DISTRICT = "ADDRLN2";

  public static final String ADDRLN_QUARTER = "ADDRLN3";
  public static final String ADDRLN_GATEWAY_NUMBER = "ADDRLN4";
  public static final String CURRDESIG = "CURRDESIG";

  private CustomerUtil()
  {

  }

  public static Customer toCustomerFullInfo(JSONObject jsonResponse, String registerNumber, AddressInfo addressInfo)
  {
    String cifNumber = getStringValueOrEmpty(jsonResponse, CUSTNO);
    JSONObject customerPersonalInfo = (JSONObject) jsonResponse.get(CUSTPERSONAL);

    List<ContactInfo> contactInfoList = new ArrayList<>();

    if (customerPersonalInfo.has(TELEPHNO))
    {
      Object telephone = customerPersonalInfo.get(TELEPHNO);

      if (telephone instanceof String)
      {
        String telephoneString = (String) telephone;
        String[] phoneNumbers = telephoneString.split(" ");

        for (int index = 0; index < phoneNumbers.length; index++)
        {
          String numberString = phoneNumbers[index];

          ContactInfo contactInfo = new ContactInfo();
          contactInfo.setPhone(numberString);
          contactInfoList.add(contactInfo);
        }
      }
      else if (telephone instanceof Integer)
      {
        Integer phoneNumber = customerPersonalInfo.getInt(TELEPHNO);

        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setPhone(String.valueOf(phoneNumber));

        contactInfoList.add(contactInfo);
      }
    }

    String firstName = getStringValueOrEmpty(customerPersonalInfo, FSTNAME);
    String lastName = getStringValueOrEmpty(customerPersonalInfo, LSTNAME);

    Customer customer = new Customer(PersonId.valueOf(registerNumber));

    try
    {
      // customer occupancy info
      if (customerPersonalInfo.has(CUSTPROF))
      {
        JSONObject custcorp = customerPersonalInfo.getJSONObject(CUSTPROF);
        if (null != custcorp && custcorp.has(CURRDESIG))
        {
          String occupancy = getStringValueOrEmpty(custcorp, CURRDESIG);
          customer.setOccupancy(occupancy);
        }
      }
    }
    catch (JSONException e)
    {
      LOGGER.warn(MSG_ERR_OCCUPANCY_NOT_FOUND);
    }

    customer.setCustomerNumber(cifNumber);

    PersonInfo personInfo = new PersonInfo();

    personInfo.setFirstName(firstName);
    personInfo.setLastName(lastName);

    customer.setAddressInfoList(Arrays.asList(addressInfo));
    customer.setPersonInfo(personInfo);

    customer.setContactInfoList(contactInfoList);

    return customer;
  }

  public static Customer toCustomer(JSONObject jsonResponse)
  {
    try
    {
      String registerNumber = getStringValueOrEmpty(jsonResponse, "regno");
      String cifNumber = getStringValueOrEmpty(jsonResponse, "custno");

      String firstName = "";
      String lastName = "";
      String displayName = getStringValueOrEmpty(jsonResponse, "custname");

      if (StringUtils.isBlank(displayName))
      {
        String errorMessage = String.format(CUSTOMER_NAME_IS_BLANK_MESSAGE, registerNumber);
        throw new BpmServiceException(CUSTOMER_NAME_IS_BLANK_CODE, errorMessage);
      }

      String[] splitNames = displayName.trim().split("\\s+");
      if (splitNames.length == 1)
      {
        firstName = splitNames[0];
        lastName = "";
      }
      else if (splitNames.length == 2)
      {
        firstName = splitNames[1];
        lastName = splitNames[0];
      }
      else
      {
        firstName = displayName;
      }

      Customer customer = new Customer(PersonId.valueOf(registerNumber), cifNumber);
      PersonInfo personInfo = new PersonInfo();

      personInfo.setFirstName(firstName);
      personInfo.setLastName(lastName);

      customer.setPersonInfo(personInfo);

      return customer;
    }
    catch (JSONException | BpmServiceException e)
    {
      LOGGER.error("####### JSON EXCEPTION DURING DOWNLOAD CUSTOMER INFO.", e);
      return null;
    }
  }

  public static AddressInfo getAddress(JSONObject jsonResponse)
  {
    String city = getStringValueOrEmpty(jsonResponse, ADDRLN_CITY);
    String district = getStringValueOrEmpty(jsonResponse, ADDRLN_DISTRICT);

    String quarter = getStringValueOrEmpty(jsonResponse, ADDRLN_QUARTER);
    String street = getStringValueOrEmpty(jsonResponse, ADDRLN_GATEWAY_NUMBER);

    AddressInfo addressInfo = new AddressInfo();

    addressInfo.setCity(city);
    addressInfo.setDistrict(district);

    addressInfo.setQuarter(quarter);
    addressInfo.setStreet(street);

    return addressInfo;
  }

  private static String getStringValueOrEmpty(JSONObject jsonObject, String key)
  {
    if (jsonObject.has(key))
    {
      if (null != jsonObject.get(key) && jsonObject.get(key) instanceof String)
      {
        return (String) jsonObject.get(key);
      }
    }
    return EMPTY_VALUE;
  }
}
