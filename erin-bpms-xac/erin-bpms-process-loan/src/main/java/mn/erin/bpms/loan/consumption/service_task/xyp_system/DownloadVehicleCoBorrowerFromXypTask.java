package mn.erin.bpms.loan.consumption.service_task.xyp_system;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import mn.erin.alfresco.connector.service.transform.TransformServiceException;
import mn.erin.alfresco.connector.service.transform.TransformerService;
import mn.erin.bpms.loan.consumption.utils.DelegationExecutionUtils;
import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.base.model.person.Person;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.model.vehicle.VehicleInfo;
import mn.erin.domain.bpm.model.vehicle.VehicleOwner;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.DocumentRepository;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CustomerService;
import mn.erin.domain.bpm.usecase.customer.GetCustomerVehicleInfo;
import mn.erin.domain.bpm.usecase.customer.GetCustomerVehicleInfoInput;
import mn.erin.domain.bpm.usecase.customer.GetCustomerVehicleOwners;
import mn.erin.domain.bpm.usecase.customer.GetCustomerVehicleOwnersInput;
import mn.erin.domain.bpm.usecase.customer.GetCustomerVehicleOwnersOutput;

import static mn.erin.bpms.loan.consumption.constant.DocumentConstants.COLLATERAL_DOCUMENT_CATEGORY;
import static mn.erin.bpms.loan.consumption.constant.DocumentConstants.VEHICLE_DOCUMENT_INFO_ID;
import static mn.erin.bpms.loan.consumption.constant.DocumentConstants.VEHICLE_DOCUMENT_SUB_CATEGORY;
import static mn.erin.bpms.loan.consumption.service_task.co_borrower.CoBorrowerUtils.getCurrentCoBorrowerIndex;
import static mn.erin.bpms.loan.consumption.service_task.xyp_system.KhurVehicleInfoUtils.getOwnerTable;
import static mn.erin.bpms.loan.consumption.service_task.xyp_system.KhurVehicleInfoUtils.getVehicleTable;
import static mn.erin.bpms.loan.consumption.service_task.xyp_system.KhurVehicleInfoUtils.setCurrentDateFields;
import static mn.erin.domain.bpm.BpmMessagesConstants.VEHICLE_INFO_NOT_FOUND_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.VEHICLE_INFO_NOT_FOUND_CODE_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.VEHICLE_PLATE_NUMBER_BLANK_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.VEHICLE_PLATE_NUMBER_CODE;
import static mn.erin.domain.bpm.BpmModuleConstants.ALFRESCO;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.EMPLOYEE_REGISTER_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.KHUR_DOWNLOAD_INQUIRIES;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.SLASH;
import static mn.erin.domain.bpm.BpmModuleConstants.TENANT_ID_XAC;
import static mn.erin.domain.bpm.BpmModuleConstants.VEHICLE_REFERENCE_CO_BORROWER_DOCUMENT_NAME;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.BRANCH_CODE_VEHICLE_REFERENCE_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.BRANCH_NAME_VEHICLE_REFERENCE_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.KHUR_VEHICLE_INFO_TEMPLATE_NAME;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.OWNER_FIRST_NAME_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.OWNER_LAST_NAME_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.OWNER_REGISTER_NUMBER_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.VEHICLE_REF_CURRENT_USER_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.VEHICLE_REF_REQUEST_ID_FIELD;

/**
 * @author Tamir
 */
public class DownloadVehicleCoBorrowerFromXypTask implements JavaDelegate
{
  private static final Logger LOG = LoggerFactory.getLogger(DownloadVehicleCoBorrowerFromXypTask.class);
  private static final String EMPTY_CASE_INSTANCE_ID = "empty";

  private final AuthenticationService authenticationService;
  private final CustomerService customerService;

  private final GroupRepository groupRepository;

  private final MembershipRepository membershipRepository;
  private final DocumentRepository documentRepository;

  public DownloadVehicleCoBorrowerFromXypTask(AuthenticationService authenticationService, CustomerService customerService, GroupRepository groupRepository,
      MembershipRepository membershipRepository, DocumentRepository documentRepository)
  {
    this.authenticationService = Objects.requireNonNull(authenticationService, "Authentication service is required!");
    this.customerService = Objects.requireNonNull(customerService, "Customer service is required!");


    this.groupRepository = Objects.requireNonNull(groupRepository, "Group repository is required!");
    this.membershipRepository = Objects.requireNonNull(membershipRepository, "Membership repository is required!");
    this.documentRepository = Objects.requireNonNull(documentRepository, "Document repository is required!");
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String regNumCoBorrower = DelegationExecutionUtils.getExecutionParameterStringValue(execution, REGISTER_NUMBER_CO_BORROWER);
    String employeeRegNum = DelegationExecutionUtils.getExecutionParameterStringValue(execution, EMPLOYEE_REGISTER_NUMBER);


    String requestId = (String) execution.getVariable(PROCESS_REQUEST_ID);
    String userId = authenticationService.getCurrentUserId();

    LOG.info("############ Downloading CO-BORROWER VEHICLE information from xyp system with REG_NUMBER ={}, REQUEST_ID ={}, User ID ={}", regNumCoBorrower,
        requestId, userId);

    String plateNumber = (String) execution.getVariable(KHUR_DOWNLOAD_INQUIRIES);

    if (null == plateNumber)
    {
      String message = String.format(VEHICLE_PLATE_NUMBER_BLANK_MESSAGE, regNumCoBorrower);
      throw new BpmServiceException(VEHICLE_PLATE_NUMBER_CODE, message);
    }

    VehicleInfo vehicleInfo = getVehicleInfo(regNumCoBorrower, employeeRegNum, plateNumber);

    if (null == vehicleInfo)
    {
      String message = String.format(VEHICLE_INFO_NOT_FOUND_CODE_MESSAGE, regNumCoBorrower, plateNumber);
      throw new BpmServiceException(VEHICLE_INFO_NOT_FOUND_CODE, message);
    }

    if (null != vehicleInfo.getPlateNumber())
    {
      String plateNumberFromVehicle = vehicleInfo.getPlateNumber();
      LOG.info("############ Downloading CO-BORROWER VEHICLE OWNERS information from xyp system with"
              + " REG_NUMBER ={},"
              + " REQUEST_ID ={},"
              + " User ID ={},"
              + " Plate Number = {}.",
          regNumCoBorrower, requestId, userId, plateNumberFromVehicle);

      try
      {
        List<VehicleOwner> vehicleOwners = getVehicleOwners(regNumCoBorrower, employeeRegNum, plateNumberFromVehicle);

        LOG.info("############ Successful downloaded CO-BORROWER VEHICLE OWNERS information from xyp system with"
                + " REG_NUMBER ={},"
                + " REQUEST_ID ={},"
                + " User ID ={},"
                + " Plate Number = {}.",
            regNumCoBorrower, requestId, userId, plateNumberFromVehicle);

        vehicleInfo.setOwners(vehicleOwners);
      }
      catch (UseCaseException e)
      {
        LOG.error("############ OWNER HISTORY LIST SERVICE GETS ERROR = = [{}], WHEN DOWNLOAD CO-BORROWER ENQUIRE = [{}]", e.getMessage(), regNumCoBorrower);
      }
    }

    generateVehicleDocument(regNumCoBorrower, execution, vehicleInfo);
  }

  private void generateVehicleDocument(String regNumCoBorrower, DelegateExecution execution, VehicleInfo vehicleInfo)
      throws AimRepositoryException, TransformServiceException, BpmRepositoryException
  {
    String requestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    LOG.info("############ Generating CO-BORROWER VEHICLE INFORMATION document with REQUEST ID = [{}]", requestId);

    String registerNumber = vehicleInfo.getCurrentOwner().getId().getId();
    String caseInstanceId = (String) execution.getVariable(CASE_INSTANCE_ID);
    String documentPath = getCoBorrowerDocumentPath(registerNumber, requestId);

    List<RestTextValueField> textFields = getTextFields(requestId, vehicleInfo);
    List<RestDynamicTable> tableFields = getTableFields(vehicleInfo);

    String documentId = transformDocument(textFields, tableFields, documentPath);

    // Persists ID card document.
    if (null == caseInstanceId)
    {
      caseInstanceId = EMPTY_CASE_INSTANCE_ID;
    }

    Integer index = getCurrentCoBorrowerIndex(execution, regNumCoBorrower);

    persistDocument(caseInstanceId, documentId, index);
    LOG.info("############ Successful generated CO-BORROWER VEHICLE INFORMATION document with REQUEST ID = [{}]", requestId);
  }

  private void persistDocument(String caseInstanceId, String documentId, Integer index) throws BpmRepositoryException
  {

    // Persists ID card document.
    if (null == caseInstanceId)
    {
      caseInstanceId = EMPTY_CASE_INSTANCE_ID;
    }

    documentRepository.removeBy(caseInstanceId, COLLATERAL_DOCUMENT_CATEGORY, VEHICLE_DOCUMENT_SUB_CATEGORY,
        VEHICLE_REFERENCE_CO_BORROWER_DOCUMENT_NAME + index);

    documentRepository.create(documentId, VEHICLE_DOCUMENT_INFO_ID, caseInstanceId,
        VEHICLE_REFERENCE_CO_BORROWER_DOCUMENT_NAME + index, COLLATERAL_DOCUMENT_CATEGORY, VEHICLE_DOCUMENT_SUB_CATEGORY, documentId, ALFRESCO);

    LOG.info("########## SUCCESSFUL CREATED CO-BORROWER DOCUMENT BY CASE INSTANCE ID = [{}], CATEGORY = [{}], SUB CATEGORY = [{}], DOC NAME = [{}]",
        caseInstanceId, COLLATERAL_DOCUMENT_CATEGORY, VEHICLE_DOCUMENT_SUB_CATEGORY, VEHICLE_REFERENCE_CO_BORROWER_DOCUMENT_NAME + index);
  }

  private List<RestTextValueField> getTextFields(String requestId, VehicleInfo vehicleInfo) throws AimRepositoryException
  {
    List<RestTextValueField> textFields = new ArrayList<>();

    textFields.add(new RestTextValueField(VEHICLE_REF_REQUEST_ID_FIELD, requestId));

    setBranchInfo(textFields);
    setCurrentDateFields(textFields);

    Person currentOwner = vehicleInfo.getCurrentOwner();

    String ownerRegNumber = currentOwner.getId().getId();
    String firstName = currentOwner.getPersonInfo().getFirstName();
    String lastName = currentOwner.getPersonInfo().getLastName();

    textFields.add(new RestTextValueField(OWNER_REGISTER_NUMBER_FIELD, ownerRegNumber));
    textFields.add(new RestTextValueField(OWNER_FIRST_NAME_FIELD, firstName));
    textFields.add(new RestTextValueField(OWNER_LAST_NAME_FIELD, lastName));

    return textFields;
  }

  private List<RestDynamicTable> getTableFields(VehicleInfo vehicleInfo)
  {
    List<RestDynamicTable> tableFields = new ArrayList<>();

    tableFields.add(getVehicleTable(vehicleInfo));
    tableFields.add(getOwnerTable(vehicleInfo));

    return tableFields;
  }

  private String transformDocument(List<RestTextValueField> textFields, List<RestDynamicTable> tableFields, String documentPath)
      throws TransformServiceException
  {
    RestTransformDocumentInput input = new RestTransformDocumentInput("Templates/" +
        KHUR_VEHICLE_INFO_TEMPLATE_NAME, documentPath);

    input.setTextValueField(textFields);
    input.setDynamicTables(tableFields);

    TransformerService transformerService = new AlfrescoRemoteTransformerService();
    RestTransformDocumentOutput output = transformerService.transform(input);

    return output.getDocumentId();
  }

  private String getCoBorrowerDocumentPath(String registerNumber, String processRequestId)
  {
    long timeStamp = new Date().getTime();
    String timeStampStr = Long.toString(timeStamp);

    return BpmModuleConstants.CUSTOMERS_FOLDER + SLASH + registerNumber + SLASH + processRequestId + SLASH + VEHICLE_REFERENCE_CO_BORROWER_DOCUMENT_NAME + "_"
        + timeStampStr;
  }

  private void setBranchInfo(List<RestTextValueField> textFields) throws AimRepositoryException
  {
    String currentUserId = authenticationService.getCurrentUserId();

    textFields.add(new RestTextValueField(VEHICLE_REF_CURRENT_USER_FIELD, currentUserId));

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
  }

  private VehicleInfo getVehicleInfo(String regNum, String employeeRegNum, String plateNumber)
      throws UseCaseException
  {
    GetCustomerVehicleInfoInput input = new GetCustomerVehicleInfoInput(regNum, employeeRegNum,plateNumber);
    GetCustomerVehicleInfo getCustomerVehicleInfo = new GetCustomerVehicleInfo(customerService);

    return getCustomerVehicleInfo.execute(input);
  }

  private List<VehicleOwner> getVehicleOwners(String regNum, String employeeRegNum, String plateNumber)
      throws UseCaseException
  {
    GetCustomerVehicleOwnersInput ownersInput = new GetCustomerVehicleOwnersInput(regNum, employeeRegNum, plateNumber);
    GetCustomerVehicleOwners getCustomerVehicleOwners = new GetCustomerVehicleOwners(customerService);

    GetCustomerVehicleOwnersOutput ownersOutput = getCustomerVehicleOwners.execute(ownersInput);

    return ownersOutput.getOwners();
  }
}
