/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpms.loan.consumption.service_task.core_bank;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.base.model.person.ContactInfo;
import mn.erin.domain.base.model.person.PersonId;
import mn.erin.domain.base.model.person.PersonInfo;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.customer.Customer;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.service.NewCoreBankingService;
import mn.erin.domain.bpm.usecase.customer.GetCustomerByPersonIdAndType;
import mn.erin.domain.bpm.usecase.customer.GetCustomerByPersonIdAndTypeInput;
import mn.erin.domain.bpm.usecase.process.UpdateRequestParameters;
import mn.erin.domain.bpm.usecase.process.UpdateRequestParametersInput;
import mn.erin.domain.bpm.usecase.process.UpdateRequestParametersOutput;

import static mn.erin.bpms.loan.consumption.utils.CustomerInfoUtils.createContactInfo;
import static mn.erin.bpms.loan.consumption.utils.CustomerInfoUtils.setCustomerType;
import static mn.erin.bpms.loan.consumption.utils.CustomerInfoUtils.setFullNameVariable;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CUSTOMER;
import static mn.erin.domain.bpm.BpmModuleConstants.EMAIL;
import static mn.erin.domain.bpm.BpmModuleConstants.EMPTY_VALUE;
import static mn.erin.domain.bpm.BpmModuleConstants.FULL_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;

public class DownloadCustomerInfoByRegNoTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DownloadCustomerInfoByRegNoTask.class);

  private final NewCoreBankingService newCoreBankingService;
  private final ProcessRequestRepository processRequestRepository;

  private final AuthenticationService authenticationService;
  private final AuthorizationService authorizationService;

  public DownloadCustomerInfoByRegNoTask(NewCoreBankingService newCoreBankingService,
      ProcessRequestRepository processRequestRepository,
      AuthenticationService authenticationService, AuthorizationService authorizationService)
  {
    this.newCoreBankingService = Objects.requireNonNull(newCoreBankingService, "New Core banking service is required");
    this.processRequestRepository = Objects.requireNonNull(processRequestRepository, "Process request repository is required!");
    this.authenticationService = authenticationService;
    this.authorizationService = authorizationService;
  }

  @Override
  public void execute(DelegateExecution delegateExecution) throws Exception
  {
    String userId = authenticationService.getCurrentUserId();

    Map<String, Object> variables = delegateExecution.getVariables();

    String registerNumber = (String) variables.get(REGISTER_NUMBER);
    String phoneNumber = (String) variables.get(PHONE_NUMBER);

    String processRequestId = (String) variables.get(PROCESS_REQUEST_ID);
    String email = (String) variables.get(EMAIL);

    LOGGER.info("#########  Download Customer Info By Reg Number Task.. Request ID: " + processRequestId + " , User ID: " + userId);

    Object borrowerType = variables.get("borrowerType");

    String customerType = setCustomerType(borrowerType);

    try
    {
      Customer customer = getCustomerByRegNoAndType(registerNumber, customerType);

      if (null != customer)
      {
        PersonInfo personInfo = customer.getPersonInfo();

        String lastName = personInfo.getLastName();
        String firstName = personInfo.getFirstName();

        LOGGER.info("PersonInfo from CBS firstname=" + firstName + ", lastname=" + lastName);

        setFullNameVariable(firstName, lastName, customerType, delegateExecution);

        Map<String, Serializable> processRequestParams = new HashMap<>();

        if (customerType.equalsIgnoreCase("Retail"))
        {
          LOGGER.info("FULL_NAME : " + firstName + " " + lastName);
          processRequestParams.put(FULL_NAME, firstName + " " + lastName);
        }
        else
        {
          processRequestParams.put(FULL_NAME, customer.getPersonInfo().getFirstName());
        }
        String cifNumber = customer.getCustomerNumber();

        if (null == cifNumber)
        {
          delegateExecution.setVariable(CIF_NUMBER, EMPTY_VALUE);
          LOGGER.info("CIF_NUMBER : " + EMPTY_VALUE);
          LOGGER.info("PHONE_NUMBER : " + phoneNumber);
          LOGGER.info("EMAIL : " + email);
          customer.setContactInfoList(Arrays.asList(createContactInfo(phoneNumber, email)));
          delegateExecution.setVariable(CUSTOMER, customer);
        }
        else
        {
          LOGGER.info("CIF_NUMBER : " + cifNumber);
          processRequestParams.put(CIF_NUMBER, cifNumber);
          delegateExecution.setVariable(CIF_NUMBER, cifNumber);
        }

        updateParameters(processRequestId, processRequestParams);
      }
      else
      {
        Customer customerDefault = new Customer(PersonId.valueOf(registerNumber));
        customerDefault.setContactInfoList(Arrays.asList(new ContactInfo(phoneNumber, email)));

        delegateExecution.setVariable(CUSTOMER, customerDefault);
      }

      LOGGER.info("#########  Finished Download Customer Info By Reg Number Task.... ");
    }
    catch (RuntimeException e)
    {
      LOGGER.error(e.getMessage(), e);
    }
  }

  private Customer getCustomerByRegNoAndType(String registerNumber, String customerType) throws UseCaseException
  {
    LOGGER.info("Downloads customer info by REG_NUMBER = {} from XAC CBS.", registerNumber);
    GetCustomerByPersonIdAndTypeInput input = new GetCustomerByPersonIdAndTypeInput("", customerType, registerNumber);
    GetCustomerByPersonIdAndType useCase = new GetCustomerByPersonIdAndType(newCoreBankingService);

    Customer customer = useCase.execute(input);
    LOGGER.info("**************** Successful downloaded customer info by REG_NUMBER and CUSTOMER_TYPE = {}", registerNumber);

    return customer;
  }

  private boolean updateParameters(String processRequestId, Map<String, Serializable> parameters) throws UseCaseException
  {
    UpdateRequestParametersInput input = new UpdateRequestParametersInput(processRequestId, parameters);
    UpdateRequestParameters useCase = new UpdateRequestParameters(authenticationService, authorizationService, processRequestRepository);

    UpdateRequestParametersOutput output = useCase.execute(input);

    return output.isUpdated();
  }
}
