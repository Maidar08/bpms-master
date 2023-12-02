package mn.erin.bpms.loan.consumption.service_task.xyp_system;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.alfresco.connector.model.RestDynamicTable;
import mn.erin.alfresco.connector.model.RestTextValueField;
import mn.erin.alfresco.connector.service.transform.AlfrescoRemoteTransformerService;
import mn.erin.alfresco.connector.service.transform.RestTransformDocumentInput;
import mn.erin.alfresco.connector.service.transform.RestTransformDocumentOutput;
import mn.erin.alfresco.connector.service.transform.TransformerService;
import mn.erin.bpms.loan.consumption.service_task.co_borrower.CoBorrowerUtils;
import mn.erin.bpms.loan.consumption.utils.DelegationExecutionUtils;
import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.property.PropertyInfo;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.DocumentRepository;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CustomerService;
import mn.erin.domain.bpm.usecase.property.GetPropertyInfo;
import mn.erin.domain.bpm.usecase.property.GetPropertyInfoInput;

import static mn.erin.bpms.loan.consumption.constant.DocumentConstants.COLLATERAL_DOCUMENT_CATEGORY;
import static mn.erin.bpms.loan.consumption.constant.DocumentConstants.PROPERTY_DOCUMENT_INFO_ID;
import static mn.erin.bpms.loan.consumption.constant.DocumentConstants.PROPERTY_DOCUMENT_SUB_CATEGORY;
import static mn.erin.bpms.loan.consumption.service_task.xyp_system.KhurPropertyInfoUtils.getNotNullValue;
import static mn.erin.bpms.loan.consumption.service_task.xyp_system.KhurPropertyInfoUtils.getPropertyTable;
import static mn.erin.domain.bpm.BpmMessagesConstants.PROPERTY_INFO_NOT_FOUND_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PROPERTY_INFO_NOT_FOUND_CODE_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PROPERTY_NUMBER_REQUIRED_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PROPERTY_NUMBER_REQUIRED_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.ALFRESCO;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CUSTOMERS_FOLDER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROPERTY_ENQUIRE_CO_BORROWER_DOCUMENT_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.SLASH;
import static mn.erin.domain.bpm.BpmModuleConstants.TENANT_ID_XAC;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.BRANCH_CODE_VEHICLE_REFERENCE_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.BRANCH_NAME_VEHICLE_REFERENCE_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.DAY_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.KHUR_PROPERTY_INFO_TEMPLATE_PATH;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.MONTH_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.PROPERTY_ADDRESS_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.PROPERTY_INTENT_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.PROPERTY_NUMBER_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.PROPERTY_SQUARE_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.VEHICLE_REF_CURRENT_USER_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.VEHICLE_REF_REQUEST_ID_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.VEHICLE_REF_SYSTEM_DATE_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.YEAR_FIELD;

/**
 * @author Tamir
 */
public class DownloadPropertyCoBorrowerFromXypTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DownloadPropertyCoBorrowerFromXypTask.class);

  private final CustomerService customerService;
  private final AuthenticationService authenticationService;
  private final GroupRepository groupRepository;
  private final MembershipRepository membershipRepository;
  private final DocumentRepository documentRepository;

  public DownloadPropertyCoBorrowerFromXypTask(CustomerService customerService,
      AuthenticationService authenticationService, GroupRepository groupRepository, MembershipRepository membershipRepository,
      DocumentRepository documentRepository)
  {
    this.customerService = Objects.requireNonNull(customerService, "Core Banking Service is required!");
    this.authenticationService = Objects.requireNonNull(authenticationService, "Authentication service is required!");
    this.groupRepository = groupRepository;
    this.membershipRepository = membershipRepository;
    this.documentRepository = documentRepository;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String regNumberCoBorrower = DelegationExecutionUtils.getExecutionParameterStringValue(execution, REGISTER_NUMBER_CO_BORROWER);
    String requestId = (String) execution.getVariable(PROCESS_REQUEST_ID);
    String userId = authenticationService.getCurrentUserId();

    LOGGER
        .info("*********** Downloading CO-BORROWER PROPERTY information from xyp system with REG_NUMBER ={}, REQUEST_ID ={}, User ID ={}", regNumberCoBorrower,
            requestId, userId);

    Map<String, String> citizenInfo = new HashMap<>();
    Map<String, String> operatorInfo = new HashMap<>();

    String employeeRegisterNumber = (String) execution.getVariable("employeeRegisterNumber");

    String propertyId = (String) execution.getVariable("downloadInquiries");

    if (null == propertyId)
    {
      String message = String.format(PROPERTY_NUMBER_REQUIRED_MESSAGE, regNumberCoBorrower);
      throw new BpmServiceException(PROPERTY_NUMBER_REQUIRED_CODE, message);
    }

    citizenInfo.put("cert", "");
    citizenInfo.put("certFingerprint", "");
    citizenInfo.put("fingerprint", "");
    citizenInfo.put("regnum", regNumberCoBorrower);

    operatorInfo.put("cert", "");
    operatorInfo.put("certFingerprint", "");
    operatorInfo.put("fingerprint", "");
    operatorInfo.put("regnum", employeeRegisterNumber);

    PropertyInfo propertyInfo = getPropertyInfo(citizenInfo, operatorInfo, propertyId);

    if (null == propertyInfo)
    {
      String message = String.format(PROPERTY_INFO_NOT_FOUND_CODE_MESSAGE, regNumberCoBorrower);
      throw new BpmServiceException(PROPERTY_INFO_NOT_FOUND_CODE, message);
    }

    LOGGER.info("*********** Successful downloaded CO-BORROWER PROPERTY information from xyp system with REGISTER NUMBER = [{}]", regNumberCoBorrower);

    // Generates Property info documentation

    LocalDate localDate = LocalDate.now();

    List<RestTextValueField> textFields = new ArrayList<>();

    int year = localDate.getYear();
    Month localDateMonth = localDate.getMonth();
    int monthValue = localDateMonth.getValue();
    int dayOfMonth = localDate.getDayOfMonth();

    textFields.add(new RestTextValueField(YEAR_FIELD, String.valueOf(year)));
    textFields.add(new RestTextValueField(MONTH_FIELD, String.valueOf(monthValue)));
    textFields.add(new RestTextValueField(DAY_FIELD, String.valueOf(dayOfMonth)));

    String currentUserId = authenticationService.getCurrentUserId();
    List<Membership> memberships = membershipRepository.listAllByUserId(TenantId.valueOf(TENANT_ID_XAC), UserId.valueOf(currentUserId));

    Membership membership = memberships.get(0);

    if (null != membership)
    {
      GroupId groupId = membership.getGroupId();

      Group group = groupRepository.findById(groupId);

      if (null != group)
      {
        String branchCode = group.getId().getId();
        String branchName = group.getName();

        textFields.add(new RestTextValueField(BRANCH_CODE_VEHICLE_REFERENCE_FIELD, branchCode));
        textFields.add(new RestTextValueField(BRANCH_NAME_VEHICLE_REFERENCE_FIELD, branchName));
      }
    }

    textFields.add(new RestTextValueField(PROPERTY_NUMBER_FIELD, getNotNullValue(propertyInfo.getPropertyId().getId())));
    textFields.add(new RestTextValueField(PROPERTY_INTENT_FIELD, getNotNullValue(propertyInfo.getIntent())));
    textFields.add(new RestTextValueField(PROPERTY_SQUARE_FIELD, getNotNullValue(propertyInfo.getSquaredMetersArea())));
    textFields.add(new RestTextValueField(PROPERTY_ADDRESS_FIELD, getNotNullValue(propertyInfo.getAddress())));

    RestDynamicTable propertyTable = getPropertyTable(propertyInfo.getPropertyProcessList());
    List<RestDynamicTable> tables = new ArrayList<>();

    tables.add(propertyTable);

    String processRequestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    String systemDateStr = LocalDate.now().format(DateTimeFormatter.ISO_DATE);

    textFields.add(new RestTextValueField(VEHICLE_REF_REQUEST_ID_FIELD, processRequestId));
    textFields.add(new RestTextValueField(VEHICLE_REF_SYSTEM_DATE_FIELD, systemDateStr));
    textFields.add(new RestTextValueField(VEHICLE_REF_CURRENT_USER_FIELD, currentUserId));

    String documentPath = getCoBorrowerDocumentPath(regNumberCoBorrower, requestId);

    RestTransformDocumentInput input = new RestTransformDocumentInput(KHUR_PROPERTY_INFO_TEMPLATE_PATH, documentPath);

    input.setTextValueField(textFields);
    input.setDynamicTables(tables);

    TransformerService transformerService = new AlfrescoRemoteTransformerService();
    RestTransformDocumentOutput output = transformerService.transform(input);
    String documentId = output.getDocumentId();

    String caseInstanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));

    if (null == caseInstanceId)
    {
      caseInstanceId = "empty";
    }

    Integer index = CoBorrowerUtils.getCurrentCoBorrowerIndex(execution, regNumberCoBorrower);

    persistCoBorrowerPropertyDocument(caseInstanceId, documentId, index);
  }

  private void persistCoBorrowerPropertyDocument(String caseInstanceId, String documentId, Integer index) throws BpmRepositoryException
  {
    documentRepository
        .removeBy(caseInstanceId, COLLATERAL_DOCUMENT_CATEGORY, PROPERTY_DOCUMENT_SUB_CATEGORY, PROPERTY_ENQUIRE_CO_BORROWER_DOCUMENT_NAME + index);
    documentRepository
        .create(documentId, PROPERTY_DOCUMENT_INFO_ID, caseInstanceId, PROPERTY_ENQUIRE_CO_BORROWER_DOCUMENT_NAME + index, COLLATERAL_DOCUMENT_CATEGORY,
            PROPERTY_DOCUMENT_SUB_CATEGORY,
            documentId, ALFRESCO);
  }

  private PropertyInfo getPropertyInfo(Map<String, String> citizenInfo, Map<String, String> operatorInfo, String propertyId) throws UseCaseException
  {
    GetPropertyInfo getPropertyInfo = new GetPropertyInfo(customerService);

    GetPropertyInfoInput getPropertyInfoInput = new GetPropertyInfoInput(operatorInfo, citizenInfo, propertyId);

    return getPropertyInfo.execute(getPropertyInfoInput);
  }

  private String getCoBorrowerDocumentPath(String registerNumber, String requestId)
  {
    long timeStamp = new Date().getTime();
    String timeStampStr = Long.toString(timeStamp);

    return CUSTOMERS_FOLDER + SLASH + registerNumber + SLASH + requestId + SLASH + PROPERTY_ENQUIRE_CO_BORROWER_DOCUMENT_NAME + "_" + timeStampStr;
  }
}
