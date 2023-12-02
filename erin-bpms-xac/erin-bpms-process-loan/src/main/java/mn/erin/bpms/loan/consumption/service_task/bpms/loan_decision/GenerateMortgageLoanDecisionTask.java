package mn.erin.bpms.loan.consumption.service_task.bpms.loan_decision;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
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
import mn.erin.bpms.loan.consumption.utils.CollateralUtils;
import mn.erin.common.utils.NumberUtils;
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
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.model.collateral.Collateral;
import mn.erin.domain.bpm.model.collateral.CollateralInfo;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.DocumentRepository;
import mn.erin.domain.bpm.service.BpmServiceException;

import static mn.erin.bpms.loan.consumption.utils.NumberUtils.getThousandSeparatedString;
import static mn.erin.bpms.loan.consumption.utils.NumberUtils.roundWithDecimalPlace;
import static mn.erin.domain.bpm.BpmMessagesConstants.MICRO_GRANT_AMOUNT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.MICRO_GRANT_AMOUNT_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.ACCEPTED_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.ALFRESCO;
import static mn.erin.domain.bpm.BpmModuleConstants.BLANK;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CATEGORY_LOAN_DECISION_DOCUMENT;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_LIST;
import static mn.erin.domain.bpm.BpmModuleConstants.CONFIRMED_USER_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CURRENCY_MNT;
import static mn.erin.domain.bpm.BpmModuleConstants.DOCUMENT_INFO_ID_LOAN_DECISION;
import static mn.erin.domain.bpm.BpmModuleConstants.FULL_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.FULL_NAME_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.GRANTED_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.INTEREST_RATE;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_DECISION_DOCUMENT_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PRODUCT_DESCRIPTION;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PURPOSE;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_TERM;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.SCORING_LEVEL_RISK;
import static mn.erin.domain.bpm.BpmModuleConstants.SCORING_SCORE;
import static mn.erin.domain.bpm.BpmModuleConstants.SLASH;
import static mn.erin.domain.bpm.BpmModuleConstants.SUB_CATEGORY_LOAN_DECISION_DOCUMENT;
import static mn.erin.domain.bpm.BpmModuleConstants.TEMPLATE_PATH_LOAN_DECISION_WITH_COLLATERAL;
import static mn.erin.domain.bpm.BpmModuleConstants.TENANT_ID_XAC;
import static mn.erin.domain.bpm.BpmModuleConstants.TERM;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.ADDITIONAL_SPECIAL_CONDITION_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.BRANCH_ID_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.BRANCH_NAME_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.CALCULATED_LOAN_AMOUNT_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.COLL_AMOUNT_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.COLL_ASSESSMENT_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.COLL_PRODUCT_DESCRIPTION_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.COLL_TOTAL_AMOUNT_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.COLL_TOTAL_ASSESSMENT_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.CONFIRMED_LOAN_AMOUNT_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.CONFIRMED_USER_ROLE_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.CONF_USER_NAME_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.CONF_USER_ROLE_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.COUNTRY_REGISTER_NUMBER_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.CURRENCY_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.CURRENT_USER_NAME_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.FULL_NAME_CO_BORROWER_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.FULL_NAME_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.INTEREST_RATE_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.LOAN_PURPOSE_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.OWNER_NAME_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.PROCESS_REQUEST_ID_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.PRODUCT_NAME_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.REGISTER_NUMBER_CO_BORROWER_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.REGISTER_NUMBER_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.REPAYMENT_TYPE_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.SCORE_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.SYSTEM_DATE_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.USER_ROLE;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.USER_ROLE_FIELD;

/**
 * @author Lkhagvadorj.A
 **/

public class GenerateMortgageLoanDecisionTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GenerateMortgageLoanDecisionTask.class);
  public static final String NULL_STRING = "null";

  private final AuthenticationService authenticationService;
  private final MembershipRepository membershipRepository;
  private final GroupRepository groupRepository;
  private final DocumentRepository documentRepository;
  private final RoleRepository roleRepository;
  private final UserRepository userRepository;

  public GenerateMortgageLoanDecisionTask(AuthenticationService authenticationService,
      MembershipRepository membershipRepository, GroupRepository groupRepository, DocumentRepository documentRepository,
      RoleRepository roleRepository, UserRepository userRepository)
  {
    this.authenticationService = authenticationService;
    this.membershipRepository = membershipRepository;
    this.groupRepository = groupRepository;
    this.documentRepository = documentRepository;
    this.roleRepository = roleRepository;
    this.userRepository = userRepository;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String registerNumber = (String) execution.getVariable(REGISTER_NUMBER);
    String processRequestId = (String) execution.getVariable(PROCESS_REQUEST_ID);

    String caseInstanceId = (String) execution.getVariable(CASE_INSTANCE_ID);
    String documentPath = getDocumentPath(registerNumber, processRequestId);

    List<RestTextValueField> textFields = getTextFields(execution);
    List<RestTextValueField> collTextFields = CollateralUtils.getCollateralTextFields(textFields, execution);

    LOGGER.info("########## Generates MICRO loan decision document with REQUEST ID = [{}]", processRequestId);

    List<String> documentIdList = transformDocument(documentPath, collTextFields);
    if (!documentIdList.isEmpty())
    {
      removePreviousLoanDecisionDocument(caseInstanceId, processRequestId);
    }
    for (String documentId : documentIdList)
    {
      persistLoanDecisionDocument(documentId, caseInstanceId, processRequestId);
    }
  }

  private List<RestTextValueField> addEmptyCollateralInfos(List<RestTextValueField> collTextFields)
  {
    for (int index = 1; index <= 10; index++)
    {
      collTextFields.add(new RestTextValueField("coll_prod_desc_" + index, BLANK));
      collTextFields.add(new RestTextValueField("country_reg_number_" + index, BLANK));
      collTextFields.add(new RestTextValueField("coll_assessment_" + index, BLANK));
      collTextFields.add(new RestTextValueField("coll_amount_" + index, BLANK));
      collTextFields.add(new RestTextValueField("owner_name_" + index, BLANK));
      collTextFields.add(new RestTextValueField(String.valueOf(index), BLANK));
    }

    collTextFields.add(new RestTextValueField(COLL_TOTAL_ASSESSMENT_FIELD, BLANK));
    collTextFields.add(new RestTextValueField(COLL_TOTAL_AMOUNT_FIELD, BLANK));

    return collTextFields;
  }

  private List<String> transformDocument(String documentPath, List<RestTextValueField> restTextValueFields) throws TransformServiceException
  {
    TransformerService transformerService = new AlfrescoRemoteTransformerService();

    Map<String, String> queryParams = new HashMap<>();
    queryParams.put("deleteBaseDocument", "false");
    RestTransformDocumentInput input = new RestTransformDocumentInput(TEMPLATE_PATH_LOAN_DECISION_WITH_COLLATERAL, documentPath);
    input.setTextValueField(restTextValueFields);
    input.setDynamicTables(new ArrayList<>());

    RestTransformDocumentOutput output = transformerService.transform(input, queryParams);

    return Arrays.asList(output.getBaseDocumentId(), output.getDocumentId());
  }

  private void removePreviousLoanDecisionDocument(String caseInstanceId, String processRequestId)
  {
    LOGGER.info("############ Removes previous MORTGAGE loan decision document with REQUEST ID = [{}]", processRequestId);

    documentRepository.removeBy(caseInstanceId, CATEGORY_LOAN_DECISION_DOCUMENT, SUB_CATEGORY_LOAN_DECISION_DOCUMENT, LOAN_DECISION_DOCUMENT_NAME);
  }

  private void persistLoanDecisionDocument(String documentId, String caseInstanceId, String processRequestId) throws BpmRepositoryException
  {
    if (null != documentId)
    {
      LOGGER.info("############ Persists MORTGAGE loan decision document with REQUEST ID = [{}]", processRequestId);
      documentRepository.create(documentId, DOCUMENT_INFO_ID_LOAN_DECISION, caseInstanceId, LOAN_DECISION_DOCUMENT_NAME,
          CATEGORY_LOAN_DECISION_DOCUMENT, SUB_CATEGORY_LOAN_DECISION_DOCUMENT, documentId, ALFRESCO);
    }

    LOGGER.info("########## Successful generated MORTGAGE loan decision with REQUEST ID = [{}]", processRequestId);
  }

  private String getDocumentPath(String registerNumber, String processRequestId)
  {
    long timeStamp = new Date().getTime();
    String timeStampStr = Long.toString(timeStamp);

    return BpmModuleConstants.CUSTOMERS_FOLDER + SLASH + registerNumber + SLASH + processRequestId + SLASH + LOAN_DECISION_DOCUMENT_NAME + "_" + timeStampStr;
  }

  private List<RestTextValueField> getTextFields(DelegateExecution execution) throws AimRepositoryException, BpmServiceException
  {
    List<RestTextValueField> textFields = new ArrayList<>();

    Map<String, Object> variables = execution.getVariables();

    String regNumber = (String) variables.get(REGISTER_NUMBER);
    String fullName = (String) execution.getVariable(FULL_NAME);

    String productName = (String) execution.getVariable(LOAN_PRODUCT_DESCRIPTION);
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

    if (execution.getVariable(LOAN_TERM) instanceof Long)
    {
      long term = (long) execution.getVariable(LOAN_TERM);
      textFields.add(new RestTextValueField(TERM, NumberUtils.longToString(term)));
    }
    else if (execution.getVariable(LOAN_TERM) instanceof Integer)
    {
      Integer term = (Integer) execution.getVariable(LOAN_TERM);
      textFields.add(new RestTextValueField(TERM, NumberUtils.intToString(term)));
    }
    else if (execution.getVariable(LOAN_TERM) instanceof Double)
    {
      double term = (Double) execution.getVariable(LOAN_TERM);
      textFields.add(new RestTextValueField(TERM, NumberUtils.doubleToString(term)));
    }

    String currentUserId = authenticationService.getCurrentUserId();

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

    double monthlyInterestRate = Double.parseDouble(String.valueOf(execution.getVariable(INTEREST_RATE)));
    String interestRate = String.valueOf(roundWithDecimalPlace(monthlyInterestRate, 2));

    String loanAmountStr = String.valueOf(execution.getVariable(ACCEPTED_LOAN_AMOUNT));
    textFields.add(new RestTextValueField(CONFIRMED_LOAN_AMOUNT_FIELD, getThousandSeparatedString(loanAmountStr)));

    setCalculatedLoanAmountField(execution, textFields);

    String additionalSpecialCondition = (String) execution.getVariable("additionalSpecialCondition");

    if (StringUtils.isBlank(additionalSpecialCondition) || NULL_STRING.equals(additionalSpecialCondition))
    {
      textFields.add(new RestTextValueField(ADDITIONAL_SPECIAL_CONDITION_FIELD, BLANK));
    }
    else
    {
      textFields.add(new RestTextValueField(ADDITIONAL_SPECIAL_CONDITION_FIELD, additionalSpecialCondition));
    }

    // loan decision, establishment
    String loanDecisionRequestId = (String) execution.getVariable("sanctionedBy");

    textFields.add(new RestTextValueField(SYSTEM_DATE_FIELD, systemDateStr));
    textFields.add(new RestTextValueField(REGISTER_NUMBER_FIELD, regNumber));

    textFields.add(new RestTextValueField(PROCESS_REQUEST_ID_FIELD, loanDecisionRequestId));
    textFields.add(new RestTextValueField(FULL_NAME_FIELD, fullName));

    textFields.add(new RestTextValueField(PRODUCT_NAME_FIELD, productName));

    textFields.add(new RestTextValueField(INTEREST_RATE_FIELD, interestRate));
    textFields.add(new RestTextValueField(LOAN_PURPOSE_FIELD, loanPurpose));

    // sets dynamic currency type.
    textFields.add(new RestTextValueField(CURRENCY_FIELD, CURRENCY_MNT));
    textFields.add(new RestTextValueField(CURRENT_USER_NAME_FIELD, currentUserName));
    textFields.add(new RestTextValueField(CONF_USER_NAME_FIELD, confirmedUserName));

    textFields.add(new RestTextValueField(CONF_USER_ROLE_FIELD, ""));
    textFields.add(new RestTextValueField(USER_ROLE_FIELD, ""));

    textFields.add(new RestTextValueField(SCORE_FIELD, String.valueOf(scoringScore)));
    textFields.add(new RestTextValueField(SCORING_LEVEL_RISK, scoringLevel));

    textFields.add(new RestTextValueField(REPAYMENT_TYPE_FIELD, "Үндсэн төлбөр тэнцүү"));

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

    setBranchInfo(textFields);
    return textFields;
  }

  private List<RestTextValueField> getCollateralTextFields(List<RestTextValueField> textFields, DelegateExecution execution)
  {
    @SuppressWarnings("unchecked")
    List<Map<String, Serializable>> collaterals = (List<Map<String, Serializable>>) execution.getVariable(COLLATERAL_LIST);
    if (null == collaterals || collaterals.isEmpty())
    {
      return addEmptyCollateralInfos(textFields);
    }

    Map<String, Serializable> selectedCollaterals = collaterals.get(0);
    int index = 1;
    BigDecimal totalAssessmentAmount = BigDecimal.ZERO;
    BigDecimal totalAmount = BigDecimal.ZERO;
    for (Map.Entry<String, Serializable> entry : selectedCollaterals.entrySet())
    {
      @SuppressWarnings("unchecked")
      Map<String, Boolean> checkedMap = (Map<String, Boolean>) entry.getValue();
      if (checkedMap.get("checked").equals(true))
      {
        String colId = entry.getKey();
        Collateral collateral = (Collateral) execution.getVariable(colId);
        String amountOfAssessment = String.valueOf(collateral.getAmountOfAssessment());
        CollateralInfo colSubInfo = collateral.getCollateralInfo();
        String amount = String.valueOf(colSubInfo.getAvailableAmount());
        List<String> ownerNames = collateral.getOwnerNames();
        String ownerName = null == ownerNames ? BLANK : StringUtils.join(ownerNames, ",");
        String stateRegistrationNumber = BLANK;
        if (null != colSubInfo.getStateRegistrationNumber() && !colSubInfo.getStateRegistrationNumber().equals("null"))
        {
          stateRegistrationNumber = colSubInfo.getStateRegistrationNumber();
        }

        setCollateralFieldWithIndex(textFields, COLL_PRODUCT_DESCRIPTION_FIELD, index, colId);
        setCollateralFieldWithIndex(textFields, COUNTRY_REGISTER_NUMBER_FIELD, index, stateRegistrationNumber);
        setCollateralFieldWithIndex(textFields, COLL_ASSESSMENT_FIELD, index, getThousandSeparatedString(amountOfAssessment));
        setCollateralFieldWithIndex(textFields, COLL_AMOUNT_FIELD, index, getThousandSeparatedString(amount));
        setCollateralFieldWithIndex(textFields, OWNER_NAME_FIELD, index, ownerName);

        totalAssessmentAmount = totalAssessmentAmount.add(BigDecimal.valueOf(Long.parseLong(amountOfAssessment)));
        totalAmount = totalAmount.add(BigDecimal.valueOf(Long.parseLong(amount)));
        index++;
      }
    }

    while (index <= 10)
    {
      setBlankCollateralField(textFields, index);
      index++;
    }

    textFields.add(new RestTextValueField(COLL_TOTAL_ASSESSMENT_FIELD, getThousandSeparatedString(String.valueOf(totalAssessmentAmount))));
    textFields.add(new RestTextValueField(COLL_TOTAL_AMOUNT_FIELD, getThousandSeparatedString(String.valueOf(totalAmount))));

    return textFields;
  }

  private void setBlankCollateralField(List<RestTextValueField> textFields, int index)
  {
    setCollateralFieldWithIndex(textFields, COLL_PRODUCT_DESCRIPTION_FIELD, index, BLANK);
    setCollateralFieldWithIndex(textFields, COUNTRY_REGISTER_NUMBER_FIELD, index, BLANK);
    setCollateralFieldWithIndex(textFields, COLL_ASSESSMENT_FIELD, index, BLANK);
    setCollateralFieldWithIndex(textFields, COLL_AMOUNT_FIELD, index, BLANK);
    setCollateralFieldWithIndex(textFields, OWNER_NAME_FIELD, index, BLANK);
  }

  private void setCollateralFieldWithIndex(List<RestTextValueField> textFields, String fieldName, int index, String fieldValue)
  {
    textFields.add(new RestTextValueField(fieldName + "_" + index, fieldValue));
  }

  private void setCalculatedLoanAmountField(DelegateExecution execution, List<RestTextValueField> textFields) throws BpmServiceException
  {
    Object grantAmount = execution.getVariable(GRANTED_LOAN_AMOUNT);

    if (null == grantAmount)
    {
      throw new BpmServiceException(MICRO_GRANT_AMOUNT_NULL_CODE, MICRO_GRANT_AMOUNT_NULL_MESSAGE);
    }

    if (grantAmount instanceof Long)
    {
      long grantLoanAmount = (Long) grantAmount;
      String calculatedLoanAmountStr = NumberUtils.longToString(grantLoanAmount);

      textFields.add(new RestTextValueField(CALCULATED_LOAN_AMOUNT_FIELD, calculatedLoanAmountStr));
    }
    else if (grantAmount instanceof Double)
    {
      double grantLoanAmount = (Double) grantAmount;
      String calculatedLoanAmountStr = NumberUtils.doubleToString(grantLoanAmount);

      textFields.add(new RestTextValueField(CALCULATED_LOAN_AMOUNT_FIELD, calculatedLoanAmountStr));
    }
    else if (grantAmount instanceof Integer)
    {
      int grantLoanAmount = (Integer) grantAmount;
      String calculatedLoanAmountStr = NumberUtils.intToString(grantLoanAmount);

      textFields.add(new RestTextValueField(CALCULATED_LOAN_AMOUNT_FIELD, calculatedLoanAmountStr));
    }
  }

  private void setRoleFields(String currentUserId, String confirmedUserId, List<RestTextValueField> textFields)
  {
    try
    {
      // todo : replace find by id method after jdbc impl done.
      List<Membership> currentUserMemberships = membershipRepository.listAllByUserId(TenantId.valueOf(TENANT_ID_XAC), UserId.valueOf(currentUserId));
      Membership membership = currentUserMemberships.get(0);
      Role userRole = roleRepository.findById(membership.getRoleId());

      if (null != userRole)
      {
        textFields.add(new RestTextValueField(USER_ROLE, userRole.getName()));
      }

      if (null != confirmedUserId)
      {
        List<Membership> confirmedUserMemberships = membershipRepository.listAllByUserId(TenantId.valueOf(TENANT_ID_XAC), UserId.valueOf(confirmedUserId));
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
    String currentUserId = authenticationService.getCurrentUserId();
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

        textFields.add(new RestTextValueField(BRANCH_ID_FIELD, branchCode));
        textFields.add(new RestTextValueField(BRANCH_NAME_FIELD, branchName));
      }
    }
  }
}
