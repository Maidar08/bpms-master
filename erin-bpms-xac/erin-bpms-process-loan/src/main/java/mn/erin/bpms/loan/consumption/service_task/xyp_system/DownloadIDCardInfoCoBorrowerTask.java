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

import static mn.erin.bpms.loan.consumption.service_task.bpms.co_borrower.CoBorrowerUtils.INDEX_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.ADDRESS_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.ALFRESCO;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CUSTOMERS_FOLDER;
import static mn.erin.domain.bpm.BpmModuleConstants.EMPLOYEE_REGISTER_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.FULL_NAME_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.ID_CARD_ENQUIRE_CO_BORROWER_DOCUMENT_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.ID_CARD_TEMPLATE;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.SLASH;

/**
 * @author Tamir
 */
public class DownloadIDCardInfoCoBorrowerTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DownloadIDCardInfoCoBorrowerTask.class);

  private final CustomerService customerService;
  private final AuthenticationService authenticationService;
  private final GroupRepository groupRepository;
  private final MembershipRepository membershipRepository;
  private final DocumentRepository documentRepository;

  public DownloadIDCardInfoCoBorrowerTask(CustomerService customerService, AuthenticationService authenticationService
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
    String regNum = (String) execution.getVariable(REGISTER_NUMBER);
    String requestId = (String) execution.getVariable(PROCESS_REQUEST_ID);
    String userId = authenticationService.getCurrentUserId();
    LOGGER.info("*********** Download ID Card Info Co Borrower Task with initial borrower REG_NUMBER ={}, REQUEST_ID ={}, User ID ={}", regNum, requestId,
        userId);

    Integer indexCoBorrower = (Integer) execution.getVariable(INDEX_CO_BORROWER);

    String currentRegNumber = DelegationExecutionUtils.getExecutionParameterStringValue(execution, REGISTER_NUMBER_CO_BORROWER);
    String employeeRegNum = DelegationExecutionUtils.getExecutionParameterStringValue(execution, EMPLOYEE_REGISTER_NUMBER);

    LOGGER.info("*********** Downloading CO-BORROWER information from xyp system with REG_NUMBER={}", currentRegNumber);

    Customer customer = downloadCustomerInfo(currentRegNumber, employeeRegNum);

    Integer currentCoBorrowerIndex = 0;

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
          LOGGER.info("********** Sets customer full name from Xyp enquire with REG_NUMBER = {}", currentRegNumber);

          Map<String, Object> variables = execution.getVariables();

          for (Integer index = indexCoBorrower; index > 0; index--)
          {
            String regNumCoBorrower = (String) execution.getVariable(REGISTER_NUMBER_CO_BORROWER + "-" + index);

            if (regNumCoBorrower.equalsIgnoreCase(currentRegNumber))
            {
              currentCoBorrowerIndex = index;
              String indexedFullName = FULL_NAME_CO_BORROWER + "-" + index;
              if (variables.containsKey(indexedFullName))
              {
                execution.removeVariable(indexedFullName);
              }
              execution.setVariable(indexedFullName, fullName);
            }
          }
        }
      }
    }

    // Generates ID card documentation.
    LOGGER.info("################ Generates CO-BORROWER ID Card document with REG_NUMBER ={}", currentRegNumber);

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

    String fullAddress = (String) execution.getVariable(ADDRESS_CO_BORROWER + "-" + currentCoBorrowerIndex);

    LocalDate birthDate = customer.getPersonInfo().getBirthDate();
    String birthDateAsText = birthDate.toString();

    textFields.add(new RestTextValueField("year", String.valueOf(year)));
    textFields.add(new RestTextValueField("month", String.valueOf(monthValue)));
    textFields.add(new RestTextValueField("day", String.valueOf(dayOfMonth)));

    textFields.add(new RestTextValueField("sur_name", familyName));
    textFields.add(new RestTextValueField("last_name", lastName));
    textFields.add(new RestTextValueField("first_name", firstName));

    textFields.add(new RestTextValueField("reg_num", currentRegNumber));
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

    String documentPath = getDocumentPath(currentRegNumber, requestId, currentCoBorrowerIndex);
    RestTransformDocumentInput input = new RestTransformDocumentInput(ID_CARD_TEMPLATE, documentPath);

    input.setTextValueField(textFields);
    List<RestImageValueField> imageValueFields = new ArrayList<>();

    imageValueFields.add(new RestImageValueField("image", image));

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

    // Removes previous document.
    documentRepository
        .removeBy(caseInstanceId, "04. Лавлагаа", "01. Лавлагаа", ID_CARD_ENQUIRE_CO_BORROWER_DOCUMENT_NAME + currentCoBorrowerIndex);

    documentRepository
        .create(documentId, "04.01", caseInstanceId, ID_CARD_ENQUIRE_CO_BORROWER_DOCUMENT_NAME + currentCoBorrowerIndex, "04. Лавлагаа", "01. Лавлагаа", documentId,
            ALFRESCO);

    LOGGER.info("*********** Successful downloaded CO-BORROWER information from XYP system.");
  }

  private String getDocumentPath(String registerNumber, String requestId, Integer currentCoBorrowerIndex)
  {
    long timeStamp = new Date().getTime();
    String timeStampStr = Long.toString(timeStamp);

    return CUSTOMERS_FOLDER + SLASH + registerNumber + SLASH + requestId + SLASH + ID_CARD_ENQUIRE_CO_BORROWER_DOCUMENT_NAME + "_" + currentCoBorrowerIndex + "_"
        + timeStampStr;
  }

  private Customer downloadCustomerInfo(String regNumber, String employeeRegNumber)
      throws UseCaseException
  {
    GetCustomerIDCardInfoInput input = new GetCustomerIDCardInfoInput(regNumber, employeeRegNumber);

    GetCustomerIDCardInfo getCustomerFromXyp = new GetCustomerIDCardInfo(customerService);

    return getCustomerFromXyp.execute(input);
  }
}
