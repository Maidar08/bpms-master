/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package consumption.service_task.direct_online_salary;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.bpm.domain.ohs.xac.XacNewCoreBankingService;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.base.model.person.AddressInfo;
import mn.erin.domain.base.model.person.ContactInfo;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.model.customer.Customer;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.usecase.process.UpdateRequestParameters;
import mn.erin.domain.bpm.usecase.process.UpdateRequestParametersInput;

import static consumption.constant.CamundaVariableConstants.CIF_CREATION_DATE;
import static consumption.constant.CamundaVariableConstants.GENDER;
import static consumption.constant.CamundaVariableConstants.XAC_SPAN_DATE;
import static consumption.util.CustomerInfoUtils.getAddressString;
import static mn.erin.domain.bpm.BpmModuleConstants.ADDRESS_INFO;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CUSTOMER;
import static mn.erin.domain.bpm.BpmModuleConstants.DIRECT_ONLINE_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.EMAIL;
import static mn.erin.domain.bpm.BpmModuleConstants.FULL_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.OCCUPANCY;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;
import static mn.erin.domain.bpm.util.process.BpmUtils.convertStringToDate;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;
import static mn.erin.domain.bpm.util.process.DigitalLoanUtils.getLogPrefix;

/**
 * @author Sukhbat
 */
public class DirectOnlineDownloadCustomerInfoByCifNoTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DirectOnlineDownloadCustomerInfoByCifNoTask.class);
  private final XacNewCoreBankingService xacNewCoreBankingService;
  private final AuthenticationService authenticationService;
  private final AuthorizationService authorizationService;
  private final ProcessRequestRepository processRequestRepository;

  public DirectOnlineDownloadCustomerInfoByCifNoTask(XacNewCoreBankingService xacNewCoreBankingService, AuthenticationService authenticationService,
      AuthorizationService authorizationService, ProcessRequestRepository processRequestRepository)
  {
    this.xacNewCoreBankingService = xacNewCoreBankingService;
    this.authenticationService = authenticationService;
    this.authorizationService = authorizationService;
    this.processRequestRepository = processRequestRepository;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String processTypeId = getValidString(execution.getVariable(PROCESS_TYPE_ID));
    Map<String, Serializable> parameters = new HashMap<>();
    Map<String, Object> variables = execution.getVariables();
    String email = (String) variables.get(EMAIL);
    String cifNumber = (String) execution.getVariable(BpmModuleConstants.CIF_NUMBER);

    StringBuilder phoneNumberSB = new StringBuilder();
    Map<String, String> input = new HashMap<>();
    input.put(CIF_NUMBER, cifNumber);
    input.put(PROCESS_TYPE_ID, String.valueOf(execution.getVariable(PROCESS_TYPE_ID)));
    input.put(PHONE_NUMBER, String.valueOf(execution.getVariable(PHONE_NUMBER)));
    Map<String, Object> customerMap = xacNewCoreBankingService.findCustomerByCifNumber(input);
    Customer customer = (Customer) customerMap.get(CUSTOMER);

    if (null != customer)
    {
      List<ContactInfo> contactInfoList = customer.getContactInfoList();

      if (!contactInfoList.isEmpty() && email != null)
      {
        contactInfoList.get(0).setEmail(email);
      }

      Object phoneNumberValue = execution.getVariable(PHONE_NUMBER);

      if (null != phoneNumberValue)
      {
        String insertedPhone = String.valueOf(execution.getVariable(PHONE_NUMBER));
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
        String addressString = getAddressString(addressInfo);

        LOGGER.info("###### SETS ADDRESS INFO = [{}]", addressString);
        execution.setVariable(ADDRESS_INFO, addressString);
      }

      String fullName = String.valueOf(customerMap.get(FULL_NAME));
      if (!StringUtils.isBlank(fullName))
      {
        execution.setVariable(FULL_NAME, fullName);
      }

      String occupancy = customer.getOccupancy();
      if (!StringUtils.isBlank(occupancy))
      {
        execution.setVariable(OCCUPANCY, occupancy);
      }
      execution.setVariable(GENDER, customer.getPersonInfo().getGender());
      execution.setVariable(REGISTER_NUMBER, customerMap.get(REGISTER_NUMBER));
      parameters.put(REGISTER_NUMBER, (Serializable) customerMap.get(REGISTER_NUMBER));
      String cifCreationDate = String.valueOf(customerMap.get("cifCreatedDate"));
      if (!StringUtils.isBlank(cifCreationDate))
      {
        execution.setVariable(CIF_CREATION_DATE, cifCreationDate);
        Date formattedDate = convertStringToDate("yyyy-MM-dd", cifCreationDate);
        execution.setVariable(XAC_SPAN_DATE, formattedDate);
      }
    }

    execution.setVariable(CUSTOMER, customer);

    updateRequestParameters(execution, parameters);

    LOGGER.info("{} Finished Download Customer Info By CIF Number Task... with cif = [{}], process instance id = [{}]",
        getLogPrefix(processTypeId), cifNumber,
        processTypeId.equals(DIRECT_ONLINE_PROCESS_TYPE_ID) ? execution.getVariable(CASE_INSTANCE_ID) : execution.getVariable(PROCESS_INSTANCE_ID));
  }

  public static void addPhoneNumberString(StringBuilder stringBuilder, String phone)
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
  }

  private void updateRequestParameters(DelegateExecution execution, Map<String, Serializable> parameters) throws UseCaseException
  {
    String requestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    UpdateRequestParameters updateRequestParameters = new UpdateRequestParameters(authenticationService, authorizationService, processRequestRepository);
    UpdateRequestParametersInput input = new UpdateRequestParametersInput(requestId, parameters);
    updateRequestParameters.execute(input);
  }

  private static boolean contains(StringBuilder sb, String findString)
  {

    /*
     * if the substring is found, position of the match is
     * returned which is >=0, if not -1 is returned
     */
    return sb.indexOf(findString) > -1;
  }
}
