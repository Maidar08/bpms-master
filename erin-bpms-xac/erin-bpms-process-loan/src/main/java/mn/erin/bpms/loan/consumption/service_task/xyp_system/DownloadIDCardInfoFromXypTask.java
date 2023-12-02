/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpms.loan.consumption.service_task.xyp_system;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.alfresco.connector.model.RestImageValueField;
import mn.erin.alfresco.connector.model.RestTextValueField;
import mn.erin.alfresco.connector.service.transform.AlfrescoRemoteTransformerService;
import mn.erin.alfresco.connector.service.transform.RestTransformDocumentInput;
import mn.erin.alfresco.connector.service.transform.RestTransformDocumentOutput;
import mn.erin.alfresco.connector.service.transform.TransformerService;
import mn.erin.bpms.loan.consumption.utils.DelegationExecutionUtils;
import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.base.model.person.PersonInfo;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.citizen.PassportInfo;
import mn.erin.domain.bpm.model.customer.Customer;
import mn.erin.domain.bpm.repository.DocumentRepository;
import mn.erin.domain.bpm.service.CustomerService;
import mn.erin.domain.bpm.usecase.customer.GetCustomerIDCardInfo;
import mn.erin.domain.bpm.usecase.customer.GetCustomerIDCardInfoInput;

import static mn.erin.domain.bpm.BpmModuleConstants.ADDRESS;
import static mn.erin.domain.bpm.BpmModuleConstants.ALFRESCO;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CUSTOMER;
import static mn.erin.domain.bpm.BpmModuleConstants.CUSTOMERS_FOLDER;
import static mn.erin.domain.bpm.BpmModuleConstants.EMPLOYEE_REGISTER_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.FULL_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.ID_CARD_ENQUIRE_DOCUMENT_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.ID_CARD_TEMPLATE;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.SLASH;

/**
 * @author Tamir
 */
public class DownloadIDCardInfoFromXypTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DownloadIDCardInfoFromXypTask.class);

  private final CustomerService customerService;
  private final AuthenticationService authenticationService;
  private final GroupRepository groupRepository;
  private final MembershipRepository membershipRepository;
  private final DocumentRepository documentRepository;

  public DownloadIDCardInfoFromXypTask(CustomerService customerService, AuthenticationService authenticationService
      , GroupRepository groupRepository, MembershipRepository membershipRepository, DocumentRepository documentRepository)
  {
    this.customerService = Objects.requireNonNull(customerService, "Customer service is required!");
    this.authenticationService = Objects.requireNonNull(authenticationService, "Authentication service is required!");
    this.groupRepository = groupRepository;
    this.membershipRepository = membershipRepository;
    this.documentRepository = documentRepository;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String regNum = DelegationExecutionUtils.getExecutionParameterStringValue(execution, REGISTER_NUMBER);
    String employeeRegNum = DelegationExecutionUtils.getExecutionParameterStringValue(execution, EMPLOYEE_REGISTER_NUMBER);
    String requestId = (String) execution.getVariable(PROCESS_REQUEST_ID);
    String userId = authenticationService.getCurrentUserId();

    LOGGER.info("*********** Downloading CUSTOMER information from xyp system with REG_NUMBER ={}, REQUEST_ID ={}, User ID ={}", regNum, requestId, userId);

    String occupancy = (String) execution.getVariable("occupancy");

    String cifNumber = (String) execution.getVariable(CIF_NUMBER);

    Customer customer = downloadCustomerInfo(regNum, employeeRegNum);

    if (null != cifNumber && null != customer)
    {
      customer.setCustomerNumber(cifNumber);
    }

    if (null != occupancy && null != customer)
    {
      customer.setOccupancy(occupancy);
    }

    Map<String, Object> variables = execution.getVariables();

    if (variables.containsKey(CUSTOMER))
    {
      execution.removeVariable(CUSTOMER);
    }

    execution.setVariable(CUSTOMER, customer);

    if (null != customer)
    {
      PersonInfo personInfo = customer.getPersonInfo();

      if (null != personInfo)
      {
        String lastName = personInfo.getLastName();
        String firstName = personInfo.getFirstName();

        if (!StringUtils.isBlank(lastName) && !StringUtils.isBlank(firstName))
        {
          String fullName = lastName + " " + firstName;
          LOGGER.info("********** Sets customer full name from Xyp enquire with REG_NUMBER ={}", regNum);
          execution.setVariable(FULL_NAME, fullName);
          execution.setVariable("lastName", lastName);
          execution.setVariable("firstName", firstName);
        }
      }
    }

    // Generates ID card documentation.
    LOGGER.info("################ Generates CUSTOMER ID Card document with with REG_NUMBER ={}, REQUEST_ID ={}, User ID ={}", regNum, requestId, userId);

    List<RestTextValueField> textFields = new ArrayList<>();

    PersonInfo personInfo = customer.getPersonInfo();

    String firstName = personInfo.getFirstName();
    String lastName = personInfo.getLastName();
    String familyName = personInfo.getFamilyName();

    String gender = personInfo.getGender();
    String nationality = customer.getNationality();

    String currentUserId = authenticationService.getCurrentUserId();
    String systemDateStr = LocalDate.now().format(DateTimeFormatter.ISO_DATE);

    LocalDate localDate = LocalDate.now();

    int year = localDate.getYear();
    Month month = localDate.getMonth();
    int monthValue = month.getValue();
    int dayOfMonth = localDate.getDayOfMonth();

    PassportInfo passportInfo = customer.getPassportInfo();

    String passportAddress = passportInfo.getPassportAddress();

    LocalDate passportIssueDate = passportInfo.getIssueDate();
    LocalDate passportExpireDate = passportInfo.getExpireDate();

    String image = passportInfo.getImage();
    String fullAddress = (String) execution.getVariable(ADDRESS);

    LocalDate birthDate = customer.getPersonInfo().getBirthDate();
    String birthDateAsText = birthDate.toString();

    textFields.add(new RestTextValueField("year", String.valueOf(year)));
    textFields.add(new RestTextValueField("month", String.valueOf(monthValue)));
    textFields.add(new RestTextValueField("day", String.valueOf(dayOfMonth)));

    textFields.add(new RestTextValueField("sur_name", familyName));
    textFields.add(new RestTextValueField("last_name", lastName));
    textFields.add(new RestTextValueField("first_name", firstName));

    textFields.add(new RestTextValueField("reg_num", regNum));
    textFields.add(new RestTextValueField("nationality", nationality));
    textFields.add(new RestTextValueField("gender", gender));

    textFields.add(new RestTextValueField("birth_date_as_text", birthDateAsText));
    textFields.add(new RestTextValueField("passport_address", passportAddress));

    textFields.add(new RestTextValueField("full_address", fullAddress));

    textFields.add(new RestTextValueField("passport_issue_date", passportIssueDate.toString()));
    textFields.add(new RestTextValueField("passport_expire_date", passportExpireDate.toString()));

    textFields.add(new RestTextValueField("request_id", requestId));

    textFields.add(new RestTextValueField("user_id", currentUserId));
    textFields.add(new RestTextValueField("system_date", systemDateStr));

    List<Membership> memberships = membershipRepository.listAllByUserId(TenantId.valueOf("xac"), UserId.valueOf(currentUserId));

    Membership membership = memberships.get(0);

    if (null != membership)
    {
      GroupId groupId = membership.getGroupId();

      Group group = groupRepository.findById(groupId);

      if (null != group)
      {
        String branchCode = group.getId().getId();
        String branchName = group.getName();

        textFields.add(new RestTextValueField("branch_code", branchCode));
        textFields.add(new RestTextValueField("branch_name", branchName));
      }
    }

    String documentPath = getDocumentPath(regNum, requestId);
    RestTransformDocumentInput input = new RestTransformDocumentInput(ID_CARD_TEMPLATE, documentPath);

    List<RestImageValueField> imageValueFields = new ArrayList<>();

    imageValueFields.add(new RestImageValueField("image", image));

    input.setTextValueField(textFields);
    input.setImageValueField(imageValueFields);
    input.setDynamicTables(new ArrayList<>());

    TransformerService transformerService = new AlfrescoRemoteTransformerService();

    RestTransformDocumentOutput output = transformerService.transform(input);
    String documentId = output.getDocumentId();

    // Persists ID card document.
    String caseInstanceId = (String) execution.getVariable(CASE_INSTANCE_ID);

    if (null == caseInstanceId)
    {
      caseInstanceId = "empty";
    }

    documentRepository.removeBy(caseInstanceId, "04. Лавлагаа", "01. Лавлагаа", ID_CARD_ENQUIRE_DOCUMENT_NAME);

    documentRepository.create(documentId, "04.01", caseInstanceId, ID_CARD_ENQUIRE_DOCUMENT_NAME, "04. Лавлагаа", "01. Лавлагаа", documentId, ALFRESCO);

    LOGGER.info("*********** Successful generated CUSTOMER ID Card document");
    LOGGER.info("*********** Successful downloaded CUSTOMER information from XYP system.");
  }

  private Customer downloadCustomerInfo(String regNumber, String employeeRegNumber)
      throws UseCaseException
  {
    GetCustomerIDCardInfoInput input = new GetCustomerIDCardInfoInput(regNumber, employeeRegNumber);

    GetCustomerIDCardInfo getCustomerFromXyp = new GetCustomerIDCardInfo(customerService);

    return getCustomerFromXyp.execute(input);
  }

  private String getDocumentPath(String registerNumber, String requestId)
  {
    long timeStamp = new Date().getTime();
    String timeStampStr = Long.toString(timeStamp);

    return CUSTOMERS_FOLDER + SLASH + registerNumber + SLASH + requestId + SLASH + ID_CARD_ENQUIRE_DOCUMENT_NAME + "_" + timeStampStr;
  }
}
