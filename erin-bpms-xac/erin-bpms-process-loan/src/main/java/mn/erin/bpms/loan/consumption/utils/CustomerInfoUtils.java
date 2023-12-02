/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpms.loan.consumption.utils;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import mn.erin.domain.base.model.person.AddressInfo;
import mn.erin.domain.base.model.person.ContactInfo;

import static mn.erin.bpms.loan.consumption.service_task.bpms.co_borrower.CoBorrowerUtils.INDEX_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CORPORATE_CUSTOMER;
import static mn.erin.domain.bpm.BpmModuleConstants.FULL_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.FULL_NAME_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.RETAIL_CUSTOMER;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

/**
 * @author Tamir
 */
public final class CustomerInfoUtils
{
  public static final String SPACE = " ";
  private static final String EMPTY = "";

  private CustomerInfoUtils()
  {

  }

  public static ContactInfo createContactInfo(String phoneNumber, String email)
  {
    ContactInfo contactInfo = new ContactInfo();

    contactInfo.setPhone(phoneNumber);
    contactInfo.setEmail(email);

    return contactInfo;
  }

  public static String getAddressString(AddressInfo addressInfo)
  {
    String city = addressInfo.getCity();
    String district = addressInfo.getDistrict();

    String quarter = addressInfo.getQuarter();
    String street = addressInfo.getStreet();

    if (null == street || street.isEmpty())
    {
      street = EMPTY;
    }

    String residence = addressInfo.getResidence();
    if (null == residence || residence.isEmpty())
    {
      residence = EMPTY;
    }

    return city + SPACE + district + SPACE + quarter + SPACE + street + SPACE + residence;
  }

  public static void setFullNameVariable(String firstName, String lastName, String customerType, DelegateExecution delegateExecution)
  {
    if (customerType.equalsIgnoreCase(RETAIL_CUSTOMER))
    {
      String fullName = firstName + " " + lastName;
      delegateExecution.setVariable(FULL_NAME, fullName);
    }
    else
    {
      delegateExecution.setVariable(FULL_NAME, firstName);
    }
  }

  public static void setCoBorrowerFullName(String firstName, DelegateExecution delegateExecution)
  {
    delegateExecution.setVariable(FULL_NAME_CO_BORROWER, firstName);

    Integer index = (Integer) delegateExecution.getVariable(INDEX_CO_BORROWER);
    delegateExecution.setVariable(FULL_NAME_CO_BORROWER + "-" + index, firstName);
  }

  public static String setCustomerType(Object borrowerType)
  {

    String customerType;
    if ("Аж ахуй нэгж".equals(borrowerType))
    {
      customerType = CORPORATE_CUSTOMER;
    }
    else
    {
      customerType = RETAIL_CUSTOMER;
    }
    return customerType;
  }

  public static Map<String, String> getParametersOfCustomerToLdms(DelegateExecution execution)
  {
    Map<String, String> parameterValues = new HashMap<>();

    parameterValues.put(PROCESS_TYPE_ID, getValidString(execution.getVariable(PROCESS_TYPE_ID)));
    parameterValues.put(PROCESS_REQUEST_ID, getValidString(execution.getVariable(PROCESS_REQUEST_ID)));
    parameterValues.put(CIF_NUMBER, getValidString(execution.getVariable(CIF_NUMBER)));
    parameterValues.put(REGISTER_NUMBER, getValidString(execution.getVariable(REGISTER_NUMBER)));
    parameterValues.put(FULL_NAME, getValidString(execution.getVariable(FULL_NAME)));
    return parameterValues;
  }
}
