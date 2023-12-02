package mn.erin.bpm.domain.ohs.xac.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.ADDRESS;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.ADDRESS_REGISTERED;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.BIRTH_DATE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.CUSTOMER_ID;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.CUST_ID;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.EMAIL;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.FIRSTNAME;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.LASTNAME;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.MAIL;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.NAME;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.NATIONALITY;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.OCCUPATION;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.PHONE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.REGISTRATION_NUMBER;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.TELEPHONE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CUSTOMER_NAME_IS_BLANK_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CUSTOMER_NAME_IS_BLANK_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.CUSTOMER;
import static mn.erin.domain.bpm.BpmModuleConstants.FULL_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.ISO_DATE_FORMATTER;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.XAC_CURRENCY;
import static mn.erin.domain.bpm.util.process.BpmUtils.getStringValueFromJson;

/**
 * @author @Odgavaa
 */
public final class CustomerUtilNewCore
{
  private static final String GENDER = "Gender";
  private static final String CUSTOMER_TYPE = "CustomerType";
  private static final String BRANCH_ID = "BranchId";
  private static final String CUSTOMER_NAME = "CustomerName";
  private static final String MIDDLE_NAME = "MiddleName";
  private static final String PASSPORT_ID = "PPT";
  private static final String DATE_OF_BIRTH = "DateOfBirth";
  private static final Logger LOGGER = LoggerFactory.getLogger(CustomerUtilNewCore.class);

  public CustomerUtilNewCore()
  {
     // no needed to do something
  }

  public static Map<String, Object> toCustomerInfo(JSONObject jsonResponse, String registerNumber)
  {
    Map<String, Object> customerMap = new HashMap<>();
    Customer customer = new Customer(PersonId.valueOf(registerNumber));

    String cifNumber = getStringValueFromJson(jsonResponse, CUSTOMER_ID);

    String firstname = getStringValueFromJson(jsonResponse, FIRSTNAME);
    String lastname = getStringValueFromJson(jsonResponse, LASTNAME);
    String occupancy = getStringValueFromJson(jsonResponse,OCCUPATION);
    String addressRegistered = getStringValueFromJson(jsonResponse, ADDRESS_REGISTERED);
    String phoneNumber = getStringValueFromJson(jsonResponse, TELEPHONE);
    String email = getStringValueFromJson(jsonResponse, EMAIL);
    String gender = getStringValueFromJson(jsonResponse, GENDER);
    String customerType = getStringValueFromJson(jsonResponse, CUSTOMER_TYPE);
    String branchId = getStringValueFromJson(jsonResponse, BRANCH_ID);
    String fullName = getStringValueFromJson(jsonResponse, CUSTOMER_NAME);
    String middleName = getStringValueFromJson(jsonResponse, MIDDLE_NAME);
    String passportId = getStringValueFromJson(jsonResponse, PASSPORT_ID);
    String currency = getStringValueFromJson(jsonResponse, XAC_CURRENCY);
    String birthDate = getStringValueFromJson(jsonResponse, DATE_OF_BIRTH);
    String cifCreatedDate = getStringValueFromJson(jsonResponse, "IncorpDt");



    ContactInfo contactInfo = new ContactInfo();
    contactInfo.setPhone(phoneNumber);
    contactInfo.setEmail(email);

    PersonInfo personInfo = new PersonInfo();
    personInfo.setFirstName(firstname);
    personInfo.setLastName(lastname);
    personInfo.setGender(gender);
    try
    {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ISO_DATE_FORMATTER);
      LocalDate birthDateFormatted = LocalDate.parse(birthDate, formatter);
      personInfo.setBirthDate(birthDateFormatted);
    }
    catch (Exception e)
    {
      LOGGER.info("DATE OF BIRTH PARAMETER NOT FOUND FOR CIF = {}", cifNumber);
    }

    AddressInfo addressInfo = new AddressInfo();
    addressInfo.setFullAddress(addressRegistered);

    customer.setCustomerNumber(cifNumber);
    customer.setAddressInfoList(Collections.singletonList(addressInfo));
    customer.setContactInfoList(Collections.singletonList(contactInfo));
    customer.setPersonInfo(personInfo);
    customer.setOccupancy(occupancy);

    customerMap.put(CUSTOMER, customer);
    customerMap.put(CUSTOMER_TYPE, customerType);
    customerMap.put(BRANCH_ID, branchId);
    customerMap.put(FULL_NAME, fullName);
    customerMap.put(MIDDLE_NAME, middleName);
    customerMap.put("passportId", passportId);
    customerMap.put(XAC_CURRENCY, currency);
    customerMap.put(REGISTER_NUMBER, registerNumber);
    customerMap.put("cifCreatedDate", cifCreatedDate);
    return customerMap;
  }

  public static Customer toRetailCustomer(JSONObject jsonResponse)
  {
    try
    {
      String cifNumber = String.valueOf(jsonResponse.get(CUST_ID));
      String regNumber = getStringValueFromJson(jsonResponse, REGISTRATION_NUMBER);

      String address = getStringValueFromJson(jsonResponse, ADDRESS);
      String nationality = getStringValueFromJson(jsonResponse, NATIONALITY);

      String phoneNumber = String.valueOf(jsonResponse.get(PHONE));
      String mail = getStringValueFromJson(jsonResponse, MAIL);

      String firstName = getStringValueFromJson(jsonResponse, FIRSTNAME);
      String lastName = getStringValueFromJson(jsonResponse, "LastName");
      String displayName = getStringValueFromJson(jsonResponse, NAME);

      String birthDate = getStringValueFromJson(jsonResponse, BIRTH_DATE);

      DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ISO_DATE_FORMATTER);
      LocalDate birthDateFormatted = LocalDate.parse(birthDate, formatter);

      if (StringUtils.isBlank(displayName))
      {
        String errorMessage = String.format(CUSTOMER_NAME_IS_BLANK_MESSAGE, regNumber);
        throw new BpmServiceException(CUSTOMER_NAME_IS_BLANK_CODE, errorMessage);
      }

      Customer customer = new Customer(PersonId.valueOf(regNumber), cifNumber);

      customer.setNationality(nationality);

      PersonInfo personInfo = new PersonInfo();

      personInfo.setFirstName(firstName);
      personInfo.setLastName(lastName);
      personInfo.setBirthDate(birthDateFormatted);

      customer.setPersonInfo(personInfo);

      AddressInfo addressInfo = new AddressInfo();
      addressInfo.setFullAddress(address);

      customer.setAddressInfoList(Collections.singletonList(addressInfo));

      ContactInfo contactInfo = new ContactInfo();
      contactInfo.setEmail(mail);
      contactInfo.setPhone(phoneNumber);

      customer.setContactInfoList(Collections.singletonList(contactInfo));

      return customer;
    }
    catch (BpmServiceException e)
    {
      LOGGER.error("####### JSON EXCEPTION DURING DOWNLOAD CUSTOMER INFO = [{}]", e.getMessage());
      return null;
    }
  }

  public static Customer toCorporateCustomer(JSONObject jsonResponse)
  {
    try
    {
      String cifNumber = String.valueOf(jsonResponse.get(CUST_ID));
      Object regNumber = jsonResponse.get(REGISTRATION_NUMBER);

      String displayName = getStringValueFromJson(jsonResponse, "LastName");
      String lastname = getStringValueFromJson(jsonResponse, "LastName");
      String address = getStringValueFromJson(jsonResponse, ADDRESS);

      String date = getStringValueFromJson(jsonResponse, BIRTH_DATE);
      Object phoneNumber = jsonResponse.get(PHONE);
      String mail = getStringValueFromJson(jsonResponse, MAIL);

      DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ISO_DATE_FORMATTER);
      LocalDate dateFormatted = LocalDate.parse(date, formatter);

      if (StringUtils.isBlank(displayName))
      {
        displayName = lastname;
        // TODO: temporarily commented because of test data
        // String errorMessage = String.format(CUSTOMER_NAME_IS_BLANK_MESSAGE, regNumber);
        // throw new BpmServiceException(CUSTOMER_NAME_IS_BLANK_CODE, errorMessage);
      }

      Customer customer = new Customer(PersonId.valueOf(regNumber.toString()), cifNumber);

      PersonInfo personInfo = new PersonInfo();

      personInfo.setFirstName(displayName);
      personInfo.setBirthDate(dateFormatted);

      customer.setPersonInfo(personInfo);

      AddressInfo addressInfo = new AddressInfo();
      addressInfo.setFullAddress(address);

      customer.setAddressInfoList(Collections.singletonList(addressInfo));

      ContactInfo contactInfo = new ContactInfo();
      contactInfo.setEmail(mail);
      contactInfo.setPhone(phoneNumber.toString());

      customer.setContactInfoList(Collections.singletonList(contactInfo));

      return customer;
    }
    catch (DateTimeParseException | JSONException e)
    {
      LOGGER.error("####### JSON EXCEPTION DURING DOWNLOAD CUSTOMER INFO = [{}]", e.getMessage());
      return null;
    }
  }
}
