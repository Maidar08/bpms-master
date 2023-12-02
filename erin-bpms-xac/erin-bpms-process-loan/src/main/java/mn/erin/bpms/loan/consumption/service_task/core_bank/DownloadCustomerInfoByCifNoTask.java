/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpms.loan.consumption.service_task.core_bank;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.base.model.person.AddressInfo;
import mn.erin.domain.base.model.person.ContactInfo;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.model.customer.Customer;
import mn.erin.domain.bpm.service.NewCoreBankingService;
import mn.erin.domain.bpm.usecase.customer.GetCustomerByCustNoNewCore;

import static mn.erin.bpms.loan.consumption.constant.CamundaCoreBankConstants.OCCUPANCY;
import static mn.erin.domain.bpm.BpmModuleConstants.ADDRESS;
import static mn.erin.domain.bpm.BpmModuleConstants.BLANK;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CUSTOMER;
import static mn.erin.domain.bpm.BpmModuleConstants.EMAIL;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;

/**
 * @author Tamir
 */
public class DownloadCustomerInfoByCifNoTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DownloadCustomerInfoByCifNoTask.class);
  private final NewCoreBankingService newCoreBankingService;
  private final AuthenticationService authenticationService;

  public DownloadCustomerInfoByCifNoTask(NewCoreBankingService newCoreBankingService, AuthenticationService authenticationService)
  {
    this.newCoreBankingService = newCoreBankingService;
    this.authenticationService = authenticationService;
  }

  @Override
  public void execute(DelegateExecution delegateExecution) throws Exception
  {
    Map<String, Object> variables = delegateExecution.getVariables();
    String email = String.valueOf(variables.get(EMAIL));
    String cifNumber = (String) delegateExecution.getVariable(BpmModuleConstants.CIF_NUMBER);

    String registrationNumber = (String) delegateExecution.getVariable("registerNumber");
    String requestId = (String )delegateExecution.getVariable(PROCESS_REQUEST_ID);
    String userId = authenticationService.getCurrentUserId();
    LOGGER.info("#########  Download Customer Info By CIF Number Task.. Register Number: " + registrationNumber + ", Request ID: " + requestId + " , User ID: " + userId);


    StringBuilder phoneNumberSB = new StringBuilder();

    if (!StringUtils.isBlank(cifNumber))
    {
      Map<String, String> input = new HashMap<>();
      input.put(CIF_NUMBER, cifNumber);
      input.put(PROCESS_TYPE_ID, String.valueOf(delegateExecution.getVariable(PROCESS_TYPE_ID)));
      input.put(PHONE_NUMBER, String.valueOf(delegateExecution.getVariable(PHONE_NUMBER)));
      Customer customer = getCustomerByCif(input);

      if (null != customer)
      {
        List<ContactInfo> contactInfoList = customer.getContactInfoList();

        if (!contactInfoList.isEmpty())
        {
          if(!StringUtils.isBlank(email))
          {
            contactInfoList.get(0).setEmail(email);
          }
        }

        Object phoneNumberValue = delegateExecution.getVariable(PHONE_NUMBER);

        if (null != phoneNumberValue)
        {
          String insertedPhone = (String) delegateExecution.getVariable(PHONE_NUMBER);
          addPhoneNumberString(phoneNumberSB, insertedPhone);
        }

        for (ContactInfo contactInfo : contactInfoList)
        {
          String phone = contactInfo.getPhone();

          if (null != phone)
          {
            addPhoneNumberString(phoneNumberSB, phone);
          }
        }

        List<AddressInfo> addressInfoList = customer.getAddressInfoList();
        if (!addressInfoList.isEmpty())
        {
          AddressInfo addressInfo = addressInfoList.get(0);
          String addressString = addressInfo.getFullAddress();

          LOGGER.info("###### SETS ADDRESS = [{}]", addressString);
          delegateExecution.setVariable(ADDRESS, addressString);
        }

        String occupancy = customer.getOccupancy();
        if (!StringUtils.isBlank(occupancy))
        {
          delegateExecution.setVariable(OCCUPANCY, occupancy);
        }
          delegateExecution.setVariable(OCCUPANCY, BLANK);
      }

      if (phoneNumberSB.length() != 0)
      {
        delegateExecution.setVariable(PHONE_NUMBER, phoneNumberSB.toString());
      }
      delegateExecution.setVariable(CUSTOMER, customer);
    }

    LOGGER.info("#########  Finished Download Customer Info By CIF Number Task..");
  }

  public static StringBuilder addPhoneNumberString(StringBuilder stringBuilder, String phone)
  {
    if (stringBuilder.length() == 0)
    {
      stringBuilder.append(phone);
    }
    else
    {
      if (!contains(stringBuilder, phone))
      {
        stringBuilder.append(", ").append(phone);
      }
    }

    return stringBuilder;
  }

  private static boolean contains(StringBuilder sb, String findString)
  {

    /*
     * if the substring is found, position of the match is
     * returned which is >=0, if not -1 is returned
     */
    return sb.indexOf(findString) > -1;
  }

  private Customer getCustomerByCif(Map<String, String> input) throws UseCaseException
  {
    LOGGER.info("Downloads customer info by CIF_NUMBER =[{}] from XAC CBS.",input.get(CIF_NUMBER));

    GetCustomerByCustNoNewCore getCustomerByCustNoNewCore = new GetCustomerByCustNoNewCore(newCoreBankingService);

    return getCustomerByCustNoNewCore.execute(input);
  }
}
