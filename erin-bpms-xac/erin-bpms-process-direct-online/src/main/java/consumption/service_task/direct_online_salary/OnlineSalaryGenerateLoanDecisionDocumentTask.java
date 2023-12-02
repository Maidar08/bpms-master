package consumption.service_task.direct_online_salary;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.alfresco.connector.model.RestTextValueField;
import mn.erin.alfresco.connector.service.transform.AlfrescoRemoteTransformerService;
import mn.erin.alfresco.connector.service.transform.RestTransformDocumentInput;
import mn.erin.alfresco.connector.service.transform.RestTransformDocumentOutput;
import mn.erin.alfresco.connector.service.transform.TransformServiceException;
import mn.erin.alfresco.connector.service.transform.TransformerService;
import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.role.Role;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.User;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.repository.RoleRepository;
import mn.erin.domain.aim.repository.UserRepository;
import mn.erin.domain.aim.service.AimServiceRegistry;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.DocumentService;
import mn.erin.domain.bpm.usecase.document.UploadDocuments;
import mn.erin.domain.bpm.usecase.document.UploadDocumentsInput;
import mn.erin.domain.bpm.usecase.document.UploadFile;
import mn.erin.domain.bpm.util.process.BpmUtils;

import static mn.erin.domain.bpm.BpmModuleConstants.ACTION_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.ALFRESCO;
import static mn.erin.domain.bpm.BpmModuleConstants.BLANK;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CATEGORY_LOAN_DECISION_DOCUMENT;
import static mn.erin.domain.bpm.BpmModuleConstants.CONFIRMED_USER_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.CURRENCY_MNT;
import static mn.erin.domain.bpm.BpmModuleConstants.DOCUMENT_INFO_ID_LOAN_DECISION;
import static mn.erin.domain.bpm.BpmModuleConstants.ERROR_CAUSE;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ACCEPTED_LOAN_AMOUNT_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.FULL_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.FULL_NAME_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.GRANT_LOAN_AMOUNT_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.INCOME_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.INTEREST_RATE;
import static mn.erin.domain.bpm.BpmModuleConstants.ISO_DATE_FORMAT;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_DECISION_DOCUMENT_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_DECISION_NAME_PDF;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_GRANT_DATE;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_OFFICER;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PRODUCT;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PRODUCT_DESCRIPTION;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PURPOSE;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_SALARY_PROCESS_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.REPAYMENT_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.SCORING_LEVEL_RISK;
import static mn.erin.domain.bpm.BpmModuleConstants.SCORING_SCORE;
import static mn.erin.domain.bpm.BpmModuleConstants.SLASH;
import static mn.erin.domain.bpm.BpmModuleConstants.SOURCE_LDMS;
import static mn.erin.domain.bpm.BpmModuleConstants.SUB_CATEGORY_LOAN_DECISION_DOCUMENT;
import static mn.erin.domain.bpm.BpmModuleConstants.TEMPLATE_PATH_LOAN_DECISION;
import static mn.erin.domain.bpm.BpmModuleConstants.TEMPLATE_PATH_ONLINE_LOAN_DECISION;
import static mn.erin.domain.bpm.BpmModuleConstants.TENANT_ID_XAC;
import static mn.erin.domain.bpm.BpmModuleConstants.TERM;
import static mn.erin.domain.bpm.BpmModuleConstants.TRACK_NUMBER;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.ADDITIONAL_SPECIAL_CONDITION_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.BRANCH_ID_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.BRANCH_NAME_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.CALCULATED_LOAN_AMOUNT_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.COBORROWER_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.CONFIRMED_LOAN_AMOUNT_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.CONFIRMED_USER_ROLE_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.CONF_USER_NAME_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.CONF_USER_ROLE_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.CURRENCY_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.CURRENT_USER_NAME_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.FULL_NAME_CO_BORROWER_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.FULL_NAME_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.INCOME_TYPE_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.INTEREST_RATE_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.LOAN_GRANT_DATE_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.LOAN_OFFICER_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.LOAN_PURPOSE_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.LOAN_TERM_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.PROCESS_REQUEST_ID_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.PRODUCT_NAME_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.REGISTER_NUMBER_CO_BORROWER_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.REGISTER_NUMBER_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.REPAYMENT_TYPE_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.SCORE_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.SYSTEM_DATE_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.USER_ROLE;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.USER_ROLE_FIELD;
import static mn.erin.domain.bpm.util.process.BpmUtils.getPdfBase64;
import static mn.erin.domain.bpm.util.process.BpmUtils.getStringValueFromExecution;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

public class OnlineSalaryGenerateLoanDecisionDocumentTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(OnlineSalaryGenerateLoanDecisionDocumentTask.class);
  public static final String NULL_STRING = "null";

  private final AimServiceRegistry aimServiceRegistry;
  private final DocumentService documentService;
  private final BpmsRepositoryRegistry bpmsRepositoryRegistry;
  private final MembershipRepository membershipRepository;
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final GroupRepository groupRepository;

  public OnlineSalaryGenerateLoanDecisionDocumentTask(AimServiceRegistry aimServiceRegistry, DocumentService documentService,
      BpmsRepositoryRegistry bpmsRepositoryRegistry,
      MembershipRepository membershipRepository, UserRepository userRepository, RoleRepository roleRepository,
      GroupRepository groupRepository)
  {
    this.aimServiceRegistry = aimServiceRegistry;
    this.documentService = documentService;
    this.bpmsRepositoryRegistry = bpmsRepositoryRegistry;
    this.membershipRepository = membershipRepository;
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.groupRepository = groupRepository;
  }

  @Override
  public void execute(DelegateExecution execution)
  {
    boolean isInstantLoan = false;
    boolean isOnlineSalary = false;
    String processTypeId = getValidString(execution.getVariable(PROCESS_TYPE_ID));
    if (processTypeId.equals(INSTANT_LOAN_PROCESS_TYPE_ID))
    {
      isInstantLoan = true;
    }
    if (processTypeId.equals(ONLINE_SALARY_PROCESS_TYPE))
    {
      isOnlineSalary = true;
    }
    try
    {
      String registerNumber = (String) execution.getVariable(REGISTER_NUMBER);
      String processRequestId = (String) execution.getVariable(PROCESS_REQUEST_ID);
      String instanceId = isOnlineSalary ? (String) execution.getVariable(CASE_INSTANCE_ID) : (String) execution.getVariable(PROCESS_INSTANCE_ID);
      String documentPath = getDocumentPath(registerNumber, processRequestId);
      List<RestTextValueField> restTextValueFields = getTextFields(execution, isInstantLoan, isOnlineSalary, processTypeId);
      String trackNumber = String.valueOf(execution.getVariable(TRACK_NUMBER));

      if (processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
      {
        LOGGER.info("########## Generates loan decision document with REQUEST ID = [{}] WITH TRACKNUMBER = [{}]. {}", processRequestId, trackNumber,
            (isInstantLoan ? " ActionType :" + execution.getVariable(ACTION_TYPE) + "." : ""));
      }
      else
      {
        LOGGER.info("########## Generates loan decision document with REQUEST ID = [{}]. {}", processRequestId,
            (isInstantLoan ? " ActionType :" + execution.getVariable(ACTION_TYPE) + "." : ""));
      }
      String templatePath = isOnlineSalary ? TEMPLATE_PATH_ONLINE_LOAN_DECISION : TEMPLATE_PATH_LOAN_DECISION;
      List<String> documentIdList = transformDocument(templatePath, documentPath, restTextValueFields);
      if (!documentIdList.isEmpty())
      {
        removePreviousLoanDecisionDocument(instanceId, processRequestId);
        for (String documentId : documentIdList)
        {
          persistLoanDecisionDocument(documentId, instanceId, processRequestId, processTypeId, trackNumber);
        }

        List<String> returnValue = getPdfBase64(documentIdList);

        if (null == returnValue || returnValue.size() != 2)
        {
          throw new BpmServiceException("Loan decision file not found when upload to LDMS");
        }

        if (isOnlineSalary)
        {
          LOGGER.info("######## TEST base64 [{}]", returnValue.get(1));
          sendFileToLDMS(execution, instanceId, returnValue.get(1), trackNumber, processTypeId);
        } else {
          execution.setVariable("instantLoanDecisionDocId", documentIdList);
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      String message = (StringUtils.isBlank(e.getMessage()) || e.getMessage().equals(NULL_STRING)) ? "java.lang.NullPointerException" : e.getMessage();
      execution.setVariable(ERROR_CAUSE, message);
      throw new BpmnError("Generate Loan Decision Document", message);
    }
  }

  private List<String> transformDocument(String templatePath, String documentPath, List<RestTextValueField> restTextValueFields)
      throws TransformServiceException
  {
    TransformerService transformerService = new AlfrescoRemoteTransformerService();

    Map<String, String> queryParams = new HashMap<>();
    queryParams.put("deleteBaseDocument", "false");
    RestTransformDocumentInput input = new RestTransformDocumentInput(templatePath, documentPath);
    input.setTextValueField(restTextValueFields);
    input.setDynamicTables(new ArrayList<>());

    RestTransformDocumentOutput output = transformerService.transform(input, queryParams);

    return Arrays.asList(output.getBaseDocumentId(), output.getDocumentId());
  }

  private void removePreviousLoanDecisionDocument(String caseInstanceId, String processRequestId)
  {
    LOGGER.info("############ Removes previous CONSUMPTION loan decision document with REQUEST ID = [{}]", processRequestId);

    bpmsRepositoryRegistry.getDocumentRepository()
        .removeBy(caseInstanceId, CATEGORY_LOAN_DECISION_DOCUMENT, SUB_CATEGORY_LOAN_DECISION_DOCUMENT, LOAN_DECISION_DOCUMENT_NAME);
  }

  private void persistLoanDecisionDocument(String documentId, String caseInstanceId, String processRequestId, String processTypeId, String trackNumber) throws BpmRepositoryException
  {
    if (null != documentId)
    {
      if (processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
      {
        LOGGER.info("############ Persists loan decision document with REQUEST ID = [{}], WITH TRACKNUMBER = [{}]", processRequestId, trackNumber);
      }
      else
      {
        LOGGER.info("############ Persists loan decision document with REQUEST ID = [{}]", processRequestId);
      }
      bpmsRepositoryRegistry.getDocumentRepository().create(documentId, DOCUMENT_INFO_ID_LOAN_DECISION, caseInstanceId, LOAN_DECISION_DOCUMENT_NAME,
          CATEGORY_LOAN_DECISION_DOCUMENT, SUB_CATEGORY_LOAN_DECISION_DOCUMENT, documentId, ALFRESCO);
    }
    if (processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
    {
      LOGGER.info("########## Successful generated loan decision with REQUEST ID = [{}], WITH TRACKNUMBER = [{}]", processRequestId, trackNumber);
    }
    else
    {
      LOGGER.info("########## Successful generated loan decision with REQUEST ID = [{}]", processRequestId);
    }
  }

  private void sendFileToLDMS(DelegateExecution execution, String caseInstanceId, String base64, String trackNumber, String processTypeId)
      throws UseCaseException
  {
    String processRequestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    String categoryLoanDecision = String.valueOf(execution.getVariable("categoryLoanDecision"));
    String subCategoryLoanDecision = String.valueOf(execution.getVariable("subCategoryLoanDecision"));
    List<UploadFile> uploadFiles = new ArrayList<>();

    uploadFiles.add(new UploadFile(LOAN_DECISION_NAME_PDF, base64));
    UploadDocumentsInput input = new UploadDocumentsInput(caseInstanceId, categoryLoanDecision, subCategoryLoanDecision, SOURCE_LDMS, Collections.emptyMap(),
        uploadFiles);
    UploadDocuments useCase = new UploadDocuments(aimServiceRegistry.getAuthenticationService(), aimServiceRegistry.getAuthorizationService(), documentService,
        membershipRepository, bpmsRepositoryRegistry.getDocumentInfoRepository(), bpmsRepositoryRegistry.getDocumentRepository());
    Boolean isUploadedFile = useCase.execute(input);
    if (Boolean.TRUE.equals(isUploadedFile))
    {
      if (processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
      {
        LOGGER.info("######## SENT LOAN DECISION FILE TO LDMS WITH REQUEST ID [{}], WITH TRACKNUMBER = [{}]", processRequestId, trackNumber);
      }
      else
      {
        LOGGER.info("######## SENT LOAN DECISION FILE TO LDMS WITH REQUEST ID [{}]", processRequestId);
      }
    }
  }

  private String getDocumentPath(String registerNumber, String processRequestId)
  {
    long timeStamp = new Date().getTime();
    String timeStampStr = Long.toString(timeStamp);

    return BpmModuleConstants.CUSTOMERS_FOLDER + SLASH + registerNumber + SLASH + processRequestId + SLASH + LOAN_DECISION_DOCUMENT_NAME + "_" + timeStampStr;
  }

  private List<RestTextValueField> getTextFields(DelegateExecution execution, boolean isInstantLoan, boolean isOnlineSalary, String processTypeId)
      throws AimRepositoryException
  {
    List<RestTextValueField> textFields = new ArrayList<>();

    Map<String, Object> variables = execution.getVariables();
    boolean isOnlineLeasing = processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID);
    String regNumber = (String) variables.get(REGISTER_NUMBER);
    String fullName = (String) execution.getVariable(FULL_NAME);

    String productId = String.valueOf(execution.getVariable(LOAN_PRODUCT));
    String productDescription = String.valueOf(execution.getVariable(LOAN_PRODUCT_DESCRIPTION));
    String product = isOnlineSalary || isInstantLoan ? productDescription : productId;
    String systemDateStr = LocalDate.now().format(DateTimeFormatter.ISO_DATE);

    Double scoringScore;

    if (execution.getVariable(SCORING_SCORE) instanceof Integer)
    {
      scoringScore = Double.valueOf(String.valueOf(execution.getVariable(SCORING_SCORE)));
    }
    else
    {
      scoringScore = (Double) execution.getVariable(SCORING_SCORE);
    }
    String scoringLevel = (String) execution.getVariable(SCORING_LEVEL_RISK);

    String confirmedUserId = (String) execution.getVariable(CONFIRMED_USER_ID);
    String loanPurpose = (String) execution.getVariable(LOAN_PURPOSE);
    String repaymentType = (String) execution.getVariable(REPAYMENT_TYPE);

    String term = BpmUtils.getStringValue(execution.getVariable(TERM));
    textFields.add(new RestTextValueField(TERM, isInstantLoan ? execution.getVariable("days") + " өдөр" : String.valueOf(term)));

    String currentUserId = aimServiceRegistry.getAuthenticationService().getCurrentUserId();

    String confirmedUserName;
    String currentUserName;

    if (!StringUtils.isBlank(confirmedUserId))
    {
      User confirmedUser = userRepository.findById(UserId.valueOf(confirmedUserId));
      confirmedUserName = confirmedUser.getUserInfo().getUserName();
    }
    else
    {
      confirmedUserName = " ";
    }
    if (!StringUtils.isBlank(currentUserId))
    {
      User currentUser = userRepository.findById(UserId.valueOf(currentUserId));
      currentUserName = currentUser.getUserInfo().getUserName();
    }
    else
    {
      currentUserName = " ";
    }

    String interest = String.valueOf(execution.getVariable(INTEREST_RATE));
    String interestRate = String.valueOf((new BigDecimal(interest).multiply(BigDecimal.valueOf(12))).setScale(2, RoundingMode.HALF_UP));
    String loanAmountStr = String.valueOf(execution.getVariable(FIXED_ACCEPTED_LOAN_AMOUNT_STRING));
    String calculatedLoanAmount = String.valueOf(execution.getVariable(GRANT_LOAN_AMOUNT_STRING));

    textFields.add(new RestTextValueField(CONFIRMED_LOAN_AMOUNT_FIELD, loanAmountStr));

    String additionalSpecialCondition = String.valueOf(execution.getVariable("additionalSpecialCondition"));

    if (StringUtils.isBlank(additionalSpecialCondition) || additionalSpecialCondition.equals(NULL_STRING))
    {
      textFields.add(new RestTextValueField(ADDITIONAL_SPECIAL_CONDITION_FIELD, BLANK));
    }
    else
    {
      textFields.add(new RestTextValueField(ADDITIONAL_SPECIAL_CONDITION_FIELD, additionalSpecialCondition));
    }

    // loan decision, establishment
    String onlineLoanDecisionId = "Digitalbank-" + execution.getVariable(PROCESS_REQUEST_ID);
    String loanDecisionRequestId = isOnlineSalary || isInstantLoan || isOnlineLeasing ? onlineLoanDecisionId : String.valueOf(execution.getVariable("sanctionedBy"));

    textFields.add(new RestTextValueField(SYSTEM_DATE_FIELD, systemDateStr));
    textFields.add(new RestTextValueField(REGISTER_NUMBER_FIELD, regNumber));

    textFields.add(new RestTextValueField(PROCESS_REQUEST_ID_FIELD, loanDecisionRequestId));
    textFields.add(new RestTextValueField(FULL_NAME_FIELD, fullName));

    textFields.add(new RestTextValueField(PRODUCT_NAME_FIELD, product));

    textFields.add(new RestTextValueField(INTEREST_RATE_FIELD, interestRate));
    textFields.add(new RestTextValueField(LOAN_PURPOSE_FIELD, loanPurpose));

    // sets dynamic currency type.
    textFields.add(new RestTextValueField(CURRENCY_FIELD, CURRENCY_MNT));
    textFields.add(new RestTextValueField(CALCULATED_LOAN_AMOUNT_FIELD, calculatedLoanAmount));

    textFields.add(new RestTextValueField(CURRENT_USER_NAME_FIELD, currentUserName));
    textFields.add(new RestTextValueField(CONF_USER_NAME_FIELD, confirmedUserName));

    textFields.add(new RestTextValueField(CONF_USER_ROLE_FIELD, ""));
    textFields.add(new RestTextValueField(USER_ROLE_FIELD, ""));

    textFields.add(new RestTextValueField(SCORE_FIELD, getSpaceString(String.valueOf(scoringScore))));
    textFields.add(new RestTextValueField(SCORING_LEVEL_RISK, getSpaceString(scoringLevel)));

    if (null == repaymentType)
    {
      repaymentType = BLANK;
    }

    textFields.add(new RestTextValueField(REPAYMENT_TYPE_FIELD, repaymentType));

    setRoleFields(currentUserId, confirmedUserId, textFields);

    String fullNameFirstCBIndex = FULL_NAME_CO_BORROWER + "-1";
    String regNumFirstCBIndex = REGISTER_NUMBER_CO_BORROWER + "-1";

    if (execution.hasVariable(fullNameFirstCBIndex) && execution.hasVariable(regNumFirstCBIndex))
    {
      String fullNameCB = (String) execution.getVariable(fullNameFirstCBIndex);
      String regNumCB = (String) execution.getVariable(regNumFirstCBIndex);

      textFields.add(new RestTextValueField(FULL_NAME_CO_BORROWER_FIELD, fullNameCB));
      textFields.add(new RestTextValueField(REGISTER_NUMBER_CO_BORROWER_FIELD, regNumCB));
    }
    else
    {
      textFields.add(new RestTextValueField(FULL_NAME_CO_BORROWER_FIELD, "-"));
      textFields.add(new RestTextValueField(REGISTER_NUMBER_CO_BORROWER_FIELD, "-"));
    }
    if (isInstantLoan || processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
    {
      String branchId;
      if (execution.getVariable("branchNumber").toString().isEmpty())
      {
        branchId = (String) execution.getVariable("defaultBranch");
      }
      else
      {
        branchId = (String) execution.getVariable("branchNumber");
      }
      textFields.add(new RestTextValueField(BRANCH_ID_FIELD, branchId));
      Group group = groupRepository.findByNumberAndTenantId(branchId, TenantId.valueOf(TENANT_ID_XAC));
      textFields.add(new RestTextValueField(BRANCH_NAME_FIELD, group.getName()));
    }
    if (isOnlineSalary)
    {
      setBranchInfo(textFields);
    }

    // Set some fields related to Online Salary Loan
    Date loanGrantDate = (Date) variables.get(LOAN_GRANT_DATE);
    if(processTypeId.equals(INSTANT_LOAN_PROCESS_TYPE_ID)){
      loanGrantDate = new Date();
    }
    DateFormat dateFormat = new SimpleDateFormat(ISO_DATE_FORMAT);
    String loanGrantDateString = dateFormat.format(loanGrantDate);
    String coBorrower = getStringValueFromExecution(variables, CO_BORROWER);
    String loanOfficer = getStringValueFromExecution(variables, LOAN_OFFICER);
    String incomeType = getStringValueFromExecution(variables, INCOME_TYPE);
    String loanTerm = getStringValueFromExecution(variables, TERM);

    textFields.add(new RestTextValueField(LOAN_GRANT_DATE_FIELD, loanGrantDateString));
    textFields.add(new RestTextValueField(COBORROWER_FIELD, coBorrower));
    textFields.add(new RestTextValueField(LOAN_OFFICER_FIELD, loanOfficer));
    textFields.add(new RestTextValueField(INCOME_TYPE_FIELD, incomeType));
    textFields.add(new RestTextValueField(LOAN_TERM_FIELD, isInstantLoan ? execution.getVariable("days") + " өдөр" : loanTerm + " сар"));

    return textFields;
  }

  private void setRoleFields(String currentUserId, String confirmedUserId, List<RestTextValueField> textFields)
  {
    try
    {
      // todo : replace find by id method after jdbc impl done.
      List<Membership> currentUserMemberships = membershipRepository.listAllByUserId(TenantId.valueOf(BpmModuleConstants.TENANT_ID_XAC),
          UserId.valueOf(currentUserId));
      Membership membership = currentUserMemberships.get(0);
      Role userRole = roleRepository.findById(membership.getRoleId());

      if (null != userRole)
      {
        textFields.add(new RestTextValueField(USER_ROLE, userRole.getName()));
      }

      if (null != confirmedUserId)
      {
        List<Membership> confirmedUserMemberships = membershipRepository.listAllByUserId(TenantId.valueOf(BpmModuleConstants.TENANT_ID_XAC),
            UserId.valueOf(confirmedUserId));
        Membership confirmedUserMembership = confirmedUserMemberships.get(0);
        Role confirmedUserRole = roleRepository.findById(confirmedUserMembership.getRoleId());

        if (null != confirmedUserRole)
        {
          textFields.add(new RestTextValueField(CONFIRMED_USER_ROLE_FIELD, confirmedUserRole.getName()));
        }
      }
    }
    catch (AimRepositoryException e)
    {
      LOGGER.error(e.getMessage());
    }
  }

  private void setBranchInfo(List<RestTextValueField> textFields) throws AimRepositoryException
  {
    String currentUserId = aimServiceRegistry.getAuthenticationService().getCurrentUserId();
    List<Membership> memberships = membershipRepository
        .listAllByUserId(TenantId.valueOf(TENANT_ID_XAC), UserId.valueOf(currentUserId));

    Membership membership = memberships.get(0);

    if (null != membership)
    {
      GroupId groupId = membership.getGroupId();

      Group group = groupRepository.findById(groupId);

      if (null != group)
      {
        String branchCode = group.getId().getId();
        String branchName = group.getName();

        textFields.add(new RestTextValueField(BRANCH_ID_FIELD, branchCode));
        textFields.add(new RestTextValueField(BRANCH_NAME_FIELD, branchName));
      }
    }
  }

  public static String getSpaceString(Object value)
  {
    if (null == value || NULL_STRING.equalsIgnoreCase(String.valueOf(value)))
    {
      return " ";
    }
    return String.valueOf(value);
  }
}
