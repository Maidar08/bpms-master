package mn.erin.bpms.loan.consumption.service_task.bpms.loan_decision;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.alfresco.connector.model.RestTextValueField;
import mn.erin.alfresco.connector.service.transform.AlfrescoRemoteTransformerService;
import mn.erin.alfresco.connector.service.transform.RestTransformDocumentInput;
import mn.erin.alfresco.connector.service.transform.RestTransformDocumentOutput;
import mn.erin.alfresco.connector.service.transform.TransformServiceException;
import mn.erin.alfresco.connector.service.transform.TransformerService;
import mn.erin.bpms.loan.consumption.utils.CollateralUtils;
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
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.model.collateral.Collateral;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.model.process.Process;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.DocumentRepository;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.usecase.process.collateral.GetCollateralById;
import mn.erin.domain.bpm.usecase.process.collateral.GetCollateralByIdInput;

import static mn.erin.bpms.loan.consumption.constant.CamundaVariableConstants.ORDER_VARIABLE;
import static mn.erin.bpms.loan.consumption.service_task.bpms.new_core.CreateLoanAccountConstants.BORROWER_CATEGORY_CODE;
import static mn.erin.bpms.loan.consumption.utils.NumberUtils.getRoundedNumStr;
import static mn.erin.domain.bpm.BpmModuleConstants.ALFRESCO;
import static mn.erin.domain.bpm.BpmModuleConstants.AMOUNT_OF_COLLATERAL;
import static mn.erin.domain.bpm.BpmModuleConstants.BLANK;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CATEGORY_LOAN_DECISION_DOCUMENT;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_ASSESSMENT;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_LIST;
import static mn.erin.domain.bpm.BpmModuleConstants.CONFIRMED_USER_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CURRENCY_MNT;
import static mn.erin.domain.bpm.BpmModuleConstants.DOCUMENT_INFO_ID_LOAN_DECISION;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ACCEPTED_LOAN_AMOUNT_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.FULL_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.FULL_NAME_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.HAS_COLLATERAL;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_DECISION_DOCUMENT_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PRODUCT;
import static mn.erin.domain.bpm.BpmModuleConstants.OWNER_NAMES_UDF_VARIABLE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PRODUCT;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.REPAYMENT_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.SCORING_LEVEL_RISK;
import static mn.erin.domain.bpm.BpmModuleConstants.SCORING_SCORE;
import static mn.erin.domain.bpm.BpmModuleConstants.SLASH;
import static mn.erin.domain.bpm.BpmModuleConstants.STATE_REGISTRATION_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.SUB_CATEGORY_LOAN_DECISION_DOCUMENT;
import static mn.erin.domain.bpm.BpmModuleConstants.TEMPLATE_PATH_LOAN_DECISION;
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
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.UNDER_LINE;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.USER_ROLE;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.USER_ROLE_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.OWNERSHIP_TYPE_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.LINKED_ACCOUNT_NUMBER_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.COLL_TYPE_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.DOC_NUMBER_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.CITY_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.ADDRESS1_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.ADDRESS2_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.ADDRESS3_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.BUILDER_NAME_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.HOUSE_NUMBER_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.STREET_NAME_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.STREET_NUMBER_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.INSPECTION_DATE_FIELD;
import static mn.erin.domain.bpm.util.process.BpmUtils.getFirstValueByDelimiter;
import static mn.erin.domain.bpm.util.process.BpmUtils.getSecondValueByDelimiter;

import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.NewCoreBankingService;

/**
 * @author Tamir
 */
public class GenerateLoanDecisionDocumentTask implements JavaDelegate {
  private static final Logger LOGGER = LoggerFactory.getLogger(GenerateLoanDecisionDocumentTask.class);
  public static final String NULL_STRING = "null";

  private final AuthenticationService authenticationService;
  private final MembershipRepository membershipRepository;
  private final GroupRepository groupRepository;
  private final DocumentRepository documentRepository;
  private final RoleRepository roleRepository;
  private final UserRepository userRepository;
  private final ProcessRepository processRepository;
  private final NewCoreBankingService newCoreBankingService;

  public GenerateLoanDecisionDocumentTask(AuthenticationService authenticationService,
      MembershipRepository membershipRepository,
      GroupRepository groupRepository, DocumentRepository documentRepository,
      RoleRepository roleRepository, UserRepository userRepository, ProcessRepository processRepository,
      NewCoreBankingService newCoreBankingService) {
    this.authenticationService = authenticationService;
    this.membershipRepository = membershipRepository;

    this.groupRepository = groupRepository;
    this.documentRepository = documentRepository;
    this.roleRepository = roleRepository;
    this.userRepository = userRepository;
    this.processRepository = processRepository;
    this.newCoreBankingService = newCoreBankingService;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    String registerNumber = (String) execution.getVariable(REGISTER_NUMBER);
    String processRequestId = (String) execution.getVariable(PROCESS_REQUEST_ID);

    String caseInstanceId = (String) execution.getVariable(CASE_INSTANCE_ID);
    String documentPath = getDocumentPath(registerNumber, processRequestId);

    List<RestTextValueField> restTextValueFields = getTextFields(execution);

    if (execution.hasVariable(HAS_COLLATERAL)) {
      String hasCollateralValue = String.valueOf(execution.getVariable(HAS_COLLATERAL));

      if (hasCollateralValue.equals(BpmModuleConstants.YES_MN_VALUE)) {
        LOGGER.info("########## Generates loan decision document with COLLATERAL INFOs, REQUEST ID = [{}]",
            processRequestId);

        List<RestTextValueField> textFields = getCollateralTextFields(restTextValueFields, execution);
        for (RestTextValueField field : textFields) {
          LOGGER.info("############## CHECKING TEXT FIELD VALUE ={},KEY={}", field.getValue(), field.getKey());
        }

        List<String> documentIdList = transformDocument(TEMPLATE_PATH_LOAN_DECISION_WITH_COLLATERAL, documentPath,
            textFields);
        if (!documentIdList.isEmpty()) {
          removePreviousLoanDecisionDocument(caseInstanceId, processRequestId);
        }
        for (String documentId : documentIdList) {
          persistLoanDecisionDocument(documentId, caseInstanceId, processRequestId);
        }

        return;
      }
    }

    LOGGER.info("########## Generates loan decision document with REQUEST ID = [{}]", processRequestId);

    List<String> documentIdList = transformDocument(TEMPLATE_PATH_LOAN_DECISION, documentPath, restTextValueFields);
    if (!documentIdList.isEmpty()) {
      removePreviousLoanDecisionDocument(caseInstanceId, processRequestId);
    }
    for (String documentId : documentIdList) {
      persistLoanDecisionDocument(documentId, caseInstanceId, processRequestId);
    }
  }

  private List<String> transformDocument(String templatePath, String documentPath,
      List<RestTextValueField> restTextValueFields) throws TransformServiceException {
    TransformerService transformerService = new AlfrescoRemoteTransformerService();

    Map<String, String> queryParams = new HashMap<>();
    queryParams.put("deleteBaseDocument", "false");
    RestTransformDocumentInput input = new RestTransformDocumentInput(templatePath, documentPath);
    input.setTextValueField(restTextValueFields);
    input.setDynamicTables(new ArrayList<>());

    RestTransformDocumentOutput output = transformerService.transform(input, queryParams);

    return Arrays.asList(output.getBaseDocumentId(), output.getDocumentId());
  }

  private void removePreviousLoanDecisionDocument(String caseInstanceId, String processRequestId) {
    LOGGER.info("############ Removes previous CONSUMPTION loan decision document with REQUEST ID = [{}]",
        processRequestId);

    documentRepository.removeBy(caseInstanceId, CATEGORY_LOAN_DECISION_DOCUMENT, SUB_CATEGORY_LOAN_DECISION_DOCUMENT,
        LOAN_DECISION_DOCUMENT_NAME);
  }

  private void persistLoanDecisionDocument(String documentId, String caseInstanceId, String processRequestId)
      throws BpmRepositoryException {
    if (null != documentId) {
      LOGGER.info("############ Persists loan decision document with REQUEST ID = [{}]", processRequestId);
      documentRepository.create(documentId, DOCUMENT_INFO_ID_LOAN_DECISION, caseInstanceId, LOAN_DECISION_DOCUMENT_NAME,
          CATEGORY_LOAN_DECISION_DOCUMENT, SUB_CATEGORY_LOAN_DECISION_DOCUMENT, documentId, ALFRESCO);
    }

    LOGGER.info("########## Successful generated loan decision with REQUEST ID = [{}]", processRequestId);
  }

  // TODO : needs to be dynamically fill depending on collateral size in the
  // future!

  /**
   * Adds collaterals text fields by collateral info from execution and process
   * parameter.
   *
   * @param textFields text fields to fill.
   * @param execution  Delegation execution.
   * @return added text fields.
   * @throws UseCaseException when get process use case is not reachable or
   *                          usable.
   */
  private List<RestTextValueField> getCollateralTextFields(List<RestTextValueField> textFields,
      DelegateExecution execution) throws UseCaseException, BpmServiceException {

    if (execution.getVariable(COLLATERAL_LIST) instanceof List) {
      return CollateralUtils.getCollateralTextFields(textFields, execution);
    }

    Map<String, Map<String, Object>> collateralsMap = (Map) execution.getVariable(BpmModuleConstants.COLLATERAL_LIST);

    List<BigDecimal> collAmountsA = new LinkedList<>();
    List<BigDecimal> collAssessmentsA = new LinkedList<>();
    List<BigDecimal> collAmountsB = new LinkedList<>();
    List<BigDecimal> collAssessmentsB = new LinkedList<>();
    List<BigDecimal> collAmountsC = new LinkedList<>();
    List<BigDecimal> collAssessmentsC = new LinkedList<>();
    List<BigDecimal> collAmountsD = new LinkedList<>();
    List<BigDecimal> collAssessmentsD = new LinkedList<>();

    int collSizeA = 0;
    int collSizeB = 0;
    int collSizeC = 0;
    int collSizeD = 0;

    for (Map.Entry<String, Map<String, Object>> collMapEntity : collateralsMap.entrySet()) {

      String collateralId = collMapEntity.getKey();
      Collateral collateral = (Collateral) execution.getVariable(collateralId);
      String colType = collateral.getType();

      if (colType.equals("I")) {
        collSizeA++;
      }

      if (colType.equals("V")) {
        collSizeB++;
      }

      if (colType.equals("M")) {
        collSizeC++;
      }

      if (colType.equals("O")) {
        collSizeD++;
      }

    }

    fillEmptyCollateralRows(collSizeA, textFields, "_a");
    fillEmptyCollateralRows(collSizeB, textFields, "_b");
    fillEmptyCollateralRows(collSizeC, textFields, "_c");
    fillEmptyCollateralRows(collSizeD, textFields, "_d");

    String customerFullName = String.valueOf(execution.getVariable(FULL_NAME));

    String loanAccountNumber = String.valueOf(execution.getVariable("loanAccountNumber"));
    int rowIndexA = 1;
    int rowIndexB = 1;
    int rowIndexC = 1;
    int rowIndexD = 1;
    String collAmountStr = "";

    for (Map.Entry<String, Map<String, Object>> collMapEntity : collateralsMap.entrySet()) {
      String collateralId = collMapEntity.getKey();

      Collateral collateral = (Collateral) execution.getVariable(collateralId);
      String colType = collateral.getType();

      if (colType.equals("I")) {
        Map<String, Serializable> collateralMap = getCollateral(collateralId);
        Map<String, Object> collInfoFromBPMS = collMapEntity.getValue();
        collAmountStr = String.valueOf(collInfoFromBPMS.get("loanAmount"));

        if (!collateralMap.isEmpty()) {
          // Adds collateral text fields from process parameter.
          addCollateralFromProcessParam(execution, collateralMap, collateralId, collAmountsA, collAssessmentsA,
              textFields, rowIndexA, customerFullName);
        } else {
          // Adds collateral text fields from execution variables.
          addCollateralFromExecution(execution, collateralId, collAmountsA, collAssessmentsA, textFields, rowIndexA,
              customerFullName, loanAccountNumber, collAmountStr);
          rowIndexA++;
        }
      }
      if (colType.equals("V")) {
        Map<String, Serializable> collateralMap = getCollateral(collateralId);
        Map<String, Object> collInfoFromBPMS = collMapEntity.getValue();
        collAmountStr = String.valueOf(collInfoFromBPMS.get("loanAmount"));

        if (!collateralMap.isEmpty()) {
          // Adds collateral text fields from process parameter.
          addCollateralFromProcessParam(execution, collateralMap, collateralId, collAmountsB, collAssessmentsB,
              textFields,
              // order,
              rowIndexB, customerFullName);
        } else {
          // Adds collateral text fields from execution variables.
          addCollateralFromExecution(execution, collateralId, collAmountsB, collAssessmentsB, textFields, // order,
              rowIndexB, customerFullName, loanAccountNumber, collAmountStr);
          rowIndexB++;
        }
      }
      if (colType.equals("M")) {
        Map<String, Serializable> collateralMap = getCollateral(collateralId);
        Map<String, Object> collInfoFromBPMS = collMapEntity.getValue();
        collAmountStr = String.valueOf(collInfoFromBPMS.get("loanAmount"));

        if (!collateralMap.isEmpty()) {
          // Adds collateral text fields from process parameter.
          addCollateralFromProcessParam(execution, collateralMap, collateralId, collAmountsC, collAssessmentsC,
              textFields, // order,
              rowIndexC, customerFullName);
        } else {
          // Adds collateral text fields from execution variables.
          addCollateralFromExecution(execution, collateralId, collAmountsC, collAssessmentsC, textFields, // order,
              rowIndexC, customerFullName, loanAccountNumber, collAmountStr);
          rowIndexC++;
        }
      }
      if (colType.equals("O")) {
        Map<String, Serializable> collateralMap = getCollateral(collateralId);
        Map<String, Object> collInfoFromBPMS = collMapEntity.getValue();
        collAmountStr = String.valueOf(collInfoFromBPMS.get("loanAmount"));

        if (!collateralMap.isEmpty()) {
          // Adds collateral text fields from process parameter.
          addCollateralFromProcessParam(execution, collateralMap, collateralId, collAmountsD, collAssessmentsD,
              textFields, // order,
              rowIndexD, customerFullName);
        } else {
          // Adds collateral text fields from execution variables.
          addCollateralFromExecution(execution, collateralId, collAmountsD, collAssessmentsD, textFields, // order,
              rowIndexD, customerFullName, loanAccountNumber, collAmountStr);
          rowIndexD++;
        }
      }
    }

    BigDecimal totalCollAmountA = collAmountsA.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    BigDecimal totalCollAssessmentA = collAssessmentsA.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    textFields.add(
        new RestTextValueField(COLL_TOTAL_AMOUNT_FIELD + "_a", getRoundedNumStr(String.valueOf(totalCollAmountA))));
    textFields.add(
        new RestTextValueField(COLL_TOTAL_ASSESSMENT_FIELD + "_a",
            getRoundedNumStr(String.valueOf(totalCollAssessmentA))));

    BigDecimal totalCollAmountB = collAmountsB.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    BigDecimal totalCollAssessmentB = collAssessmentsB.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    textFields.add(
        new RestTextValueField(COLL_TOTAL_AMOUNT_FIELD + "_b", getRoundedNumStr(String.valueOf(totalCollAmountB))));
    textFields.add(
        new RestTextValueField(COLL_TOTAL_ASSESSMENT_FIELD + "_b",
            getRoundedNumStr(String.valueOf(totalCollAssessmentB))));

    BigDecimal totalCollAmountC = collAmountsC.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    BigDecimal totalCollAssessmentC = collAssessmentsC.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    textFields.add(
        new RestTextValueField(COLL_TOTAL_AMOUNT_FIELD + "_c", getRoundedNumStr(String.valueOf(totalCollAmountC))));
    textFields.add(
        new RestTextValueField(COLL_TOTAL_ASSESSMENT_FIELD + "_c",
            getRoundedNumStr(String.valueOf(totalCollAssessmentC))));

    BigDecimal totalCollAmountD = collAmountsD.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    BigDecimal totalCollAssessmentD = collAssessmentsD.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    textFields.add(
        new RestTextValueField(COLL_TOTAL_AMOUNT_FIELD + "_d", getRoundedNumStr(String.valueOf(totalCollAmountD))));
    textFields.add(
        new RestTextValueField(COLL_TOTAL_ASSESSMENT_FIELD + "_d",
            getRoundedNumStr(String.valueOf(totalCollAssessmentD))));
    return textFields;
  }

  private void addCollateralFromProcessParam(DelegateExecution execution, Map<String, Serializable> collateralMap,
      String collateralId, List<BigDecimal> collAmounts,
      List<BigDecimal> collAssessments, List<RestTextValueField> textFields, int order, String customerFullName)
      throws UseCaseException {
    String collProduct = String.valueOf(collateralMap.get(PRODUCT));

    if (!StringUtils.isBlank(collProduct)) {
      collProduct = collProduct + ", ";
    }

    LOGGER.info("############### FILLS COLLATERAL PRODUCT ROW NUMBER = [{}]", order);
    LOGGER.info("############### FILLS COLLATERAL PRODUCT = [{}]", collProduct);

    String stateRegisterNumber = String.valueOf(collateralMap.get(STATE_REGISTRATION_NUMBER));

    LOGGER.info("############### FILLS COLLATERAL STATE REGISTER NUMBER = [{}]", stateRegisterNumber);

    String collAmountStr = String.valueOf(collateralMap.get(AMOUNT_OF_COLLATERAL));

    if (null != collAmountStr && !NULL_STRING.equals(collAmountStr)) {
      collAmounts.add(new BigDecimal(collAmountStr));
    }
    LOGGER.info("############### FILLS COLLATERAL AMOUNT = [{}]", collAmountStr);

    String collAssessmentStr = String.valueOf(collateralMap.get(COLLATERAL_ASSESSMENT));

    LOGGER.info("############### FILLS COLLATERAL ASSESSMENT = [{}]", collAssessmentStr);

    if (null != collAssessmentStr && !NULL_STRING.equals(collAssessmentStr)) {
      collAssessments.add(new BigDecimal(collAssessmentStr));
    }

    String ownerNames;
    if (execution.getVariable(collateralId) instanceof Collateral) {
      Collateral collateral = (Collateral) execution.getVariable(collateralId);
      List<String> ownerNameList = collateral.getOwnerNames();
      ownerNames = null == ownerNameList ? BLANK : StringUtils.join(ownerNameList, ",");
    } else {
      ownerNames = getOwnerNamesFromProcess(collateralId);
      if (null == ownerNames) {
        ownerNames = customerFullName;
      }
    }
    LOGGER.info("############### FILLS COLLATERAL OWNER NAMES = [{}]", ownerNames);

    // addCollTextFields(textFields, order, collProduct, stateRegisterNumber,
    // collAmountStr, collAssessmentStr, ownerNames,
    // ownershipType, loanAccountNumber);
  }

  private void addCollateralFromExecution(DelegateExecution execution, String collateralId,
      List<BigDecimal> collAmounts,
      List<BigDecimal> collAssessments, List<RestTextValueField> textFields, int order, String customerFullName,
      String loanAccountNumber, String collAmountStr)
      throws UseCaseException, BpmServiceException {
    if (execution.hasVariable(collateralId) && null != execution.getVariable(collateralId)) {

      Collateral collateral = (Collateral) execution.getVariable(collateralId);

      // Collateral Amount
      BigDecimal collAmount = new BigDecimal(collAmountStr);

      if (null != collAmount) {
        collAmounts.add(collAmount);
      }

      // Collateral Assessment
      BigDecimal collAssessment = collateral.getAmountOfAssessment();
      if (null != collAssessment) {
        collAssessments.add(collAssessment);
      }
      String collAssessmentStr = String.valueOf(collAssessment);

      // Owner names
      List<String> ownerNames = collateral.getOwnerNames();
      String ownerNamesStr = getCommaSeparatedValues(ownerNames);

      if (StringUtils.isBlank(ownerNamesStr)) {
        ownerNamesStr = customerFullName;
      }

      Map<String, String> ownershipTest = new HashMap<>();

      String collateralType = collateral.getType();
      List<String> types = new LinkedList<>();
      types.add("ownertype");
      types.add("city");

      Map<String, List<String>> someSth = newCoreBankingService.getCollReferenceCodes(types);
      List<String> ownertype = someSth.get("ownertype");
      List<String> cityCode = someSth.get("city");

      if (collateralType.equals("M")) {
        Map<String, Object> collateralInfo1 = newCoreBankingService.getMachineryCollateral(collateralId);
        List<String> type = newCoreBankingService.getCollateralCode(collateralType);

        for (String item : type) {
          if (collateralInfo1.get("collateralCode").equals(item)) {
            ownershipTest.put("type", item);
          }
        }

        for (String item : ownertype) {
          String a = getFirstValueByDelimiter(item, ":", "error");
          if (collateralInfo1.get("ownershipType").equals(a)) {
            ownershipTest.put("ownershipType", getSecondValueByDelimiter(item, ":", "error"));
          }
        }

        ownershipTest.put("colType", "Machinery");
        ownershipTest.put("inspectionDate", String.valueOf(collateralInfo1.get("inspectionDate")));
        ownershipTest.put("propertyDocNumber", String.valueOf(collateralInfo1.get("machineNum")));

      }
      if (collateralType.equals("V")) {
        Map<String, Object> collateralInfo1 = newCoreBankingService.getVehicleCollInfo(collateralId);
        List<String> type = newCoreBankingService.getCollateralCode(collateralType);

        for (String item : type) {
          if (collateralInfo1.get("collateralCode").equals(getFirstValueByDelimiter(item, ":", "error"))) {
            ownershipTest.put("type", getSecondValueByDelimiter(item, ":", "error"));
          }
        }
        for (String item : ownertype) {
          String a = getFirstValueByDelimiter(item, ":", "error");
          if (collateralInfo1.get("ownershipType").equals(a)) {
            ownershipTest.put("ownershipType", getSecondValueByDelimiter(item, ":", "error"));
          }
        }

        ownershipTest.put("colType", "Vehicle");
        ownershipTest.put("propertyDocNumber", String.valueOf(collateralInfo1.get("chassisNumber")));
        ownershipTest.put("address1", String.valueOf(collateralInfo1.get("address1")));
        ownershipTest.put("address2", String.valueOf(collateralInfo1.get("address2")));
        ownershipTest.put("address3", String.valueOf(collateralInfo1.get("address3")));
        ownershipTest.put("inspectionDate", String.valueOf(collateralInfo1.get("inspectionDate")));
      }

      if (collateralType.equals("I")) {
        Map<String, Object> collateralInfo1 = newCoreBankingService.getImmovablePropertyCollateral(collateralId);
        List<String> type = newCoreBankingService.getCollateralCode(collateralType);

        for (String item : type) {
          if (collateralInfo1.get("collateralCode").equals(getFirstValueByDelimiter(item, ":", "error"))) {
            ownershipTest.put("type", getSecondValueByDelimiter(item, ":", "error"));
          }
        }

        for (String item : ownertype) {
          String a = getFirstValueByDelimiter(item, ":", "error");

          if (collateralInfo1.get("ownershipType").equals(a)) {
            ownershipTest.put("ownershipType", getSecondValueByDelimiter(item, ":", "error"));
          }
        }

        ownershipTest.put("colType", "Immovable");
        ownershipTest.put("propertyDocNumber", String.valueOf(collateralInfo1.get("propertyDocNumber")));

        for (String item : cityCode) {
          String b = getFirstValueByDelimiter(item, ":", "error");
          if (collateralInfo1.get("city").equals(b)) {
            ownershipTest.put("city", getSecondValueByDelimiter(item, ":", "error"));
          }
        }
        ownershipTest.put("address1", String.valueOf(collateralInfo1.get("address1")));
        ownershipTest.put("address2", String.valueOf(collateralInfo1.get("address2")));
        ownershipTest.put("address3", String.valueOf(collateralInfo1.get("address3")));
        ownershipTest.put("builderName", String.valueOf(collateralInfo1.get("builderName")));
        ownershipTest.put("houseNumber", String.valueOf(collateralInfo1.get("houseNumber")));
        ownershipTest.put("streetName", String.valueOf(collateralInfo1.get("streetName")));
        ownershipTest.put("streetNumber", String.valueOf(collateralInfo1.get("streetNumber")));
        ownershipTest.put("inspectionDate", String.valueOf(collateralInfo1.get("inspectionDate")));
      }

      if (collateralType.equals("O")) {
        Map<String, Object> collateralInfo1 = newCoreBankingService.getOtherCollateral(collateralId);
        List<String> type = newCoreBankingService.getCollateralCode(collateralType);

        for (String item : type) {
          if (collateralInfo1.get("collateralCode").equals(getFirstValueByDelimiter(item, ":", "error"))) {
            ownershipTest.put("type", getSecondValueByDelimiter(item, ":", "error"));
          }
        }

        for (String item : ownertype) {
          String a = getFirstValueByDelimiter(item, ":", "error");

          if (collateralInfo1.get("ownershipType").equals(a)) {
            ownershipTest.put("ownershipType", getSecondValueByDelimiter(item, ":", "error"));
          }
        }
        ownershipTest.put("colType", "Others");
        ownershipTest.put("inspectionDate", String.valueOf(collateralInfo1.get("inspectionDate")));
        ownershipTest.put("remarks", String.valueOf(collateralInfo1.get("remarks")));
      }

      String ownershipTypeStr = String.valueOf(ownershipTest.get("ownershipType"));
      String colTypeStr = ownershipTest.get("colType");
      String cityStr = ownershipTest.get("city");
      String address1Str = ownershipTest.get("address1");
      String address2Str = ownershipTest.get("address2");
      String address3Str = ownershipTest.get("address3");
      String builderName = ownershipTest.get("builderName");
      String houseNumber = ownershipTest.get("houseNumber");
      String streetNameStr = ownershipTest.get("streetName");
      String streetNumberStr = ownershipTest.get("streetNumber");
      String inspectionDateStr = ownershipTest.get("inspectionDate");
      String propertyDocNumberStr = ownershipTest.get("propertyDocNumber");
      String remarksStr = ownershipTest.get("remarks");
      String typeStr = ownershipTest.get("type");

      String loanAccountNumberStr = String.valueOf(execution.getVariable(BpmModuleConstants.LOAN_ACCOUNT_NUMBER));

      addCollTextFields(textFields, order, collateralId, BLANK, collAmountStr, collAssessmentStr, ownerNamesStr,
          ownershipTypeStr, loanAccountNumberStr, colTypeStr, cityStr, address1Str, address2Str, address3Str,
          builderName, houseNumber, streetNameStr, streetNumberStr, inspectionDateStr, propertyDocNumberStr,
          remarksStr, typeStr);
    }
  }

  private void fillEmptyCollateralRows(int collSize, List<RestTextValueField> textFields, String type) {
    int empRowStartIndex;

    if (collSize > 5) {
      empRowStartIndex = 0;
    } else if (collSize >= 5) {
      empRowStartIndex = 0;
    } else {
      empRowStartIndex = collSize + 1;
    }

    for (int emptyRowIndex = empRowStartIndex; emptyRowIndex <= 5; emptyRowIndex++) {
      addEmptyTextFields(textFields, emptyRowIndex, type);
    }
  }

  private String getCommaSeparatedValues(List<String> strList) {
    if (null == strList) {
      return BLANK;
    }

    return String.join(",", strList);
  }

  private void addCollTextFields(List<RestTextValueField> textFields, int index, String collProduct,
      String stateRegDesc, String collAmount,
      String collAssessment, String ownerNames, String ownershipType, String loanAccountNumber,
      String colType, String city, String address1, String address2, String address3, String builderName,
      String houseNumber, String streetName, String streetNumber, String inspectionDate, String chassisNumber,
      String remarks, String type) throws BpmServiceException {
    String indexStr = String.valueOf(index);
    ;

    if (colType.equals("Immovable")) {

      textFields.add(new RestTextValueField(getIndexedFieldId(COLL_PRODUCT_DESCRIPTION_FIELD + "_a", indexStr),
          "\nБарьцааны код:\n" + getNotNullValue(collProduct)));
      textFields.add(new RestTextValueField(getIndexedFieldId(COUNTRY_REGISTER_NUMBER_FIELD + "_a", indexStr),
          "\nУлсын бүртгэлийн дугаар :\n" + getNotNullValue(stateRegDesc)));
      textFields.add(new RestTextValueField(getIndexedFieldId(COLL_TYPE_FIELD + "_a", indexStr),
          "Барьцааны үндсэн төрөл: \n" + getNotNullValue(type)));
      textFields.add(
          new RestTextValueField(getIndexedFieldId(CITY_FIELD + "_a", indexStr), "\nХаяг:" + getNotNullValue(city)));
      textFields
          .add(new RestTextValueField(getIndexedFieldId(ADDRESS1_FIELD + "_a", indexStr),
              "," + getNotNullValue(address1)));
      textFields
          .add(new RestTextValueField(getIndexedFieldId(ADDRESS2_FIELD + "_a", indexStr),
              "," + getNotNullValue(address2)));
      textFields
          .add(new RestTextValueField(getIndexedFieldId(ADDRESS3_FIELD + "_a", indexStr),
              "," + getNotNullValue(address3)));
      textFields
          .add(new RestTextValueField(getIndexedFieldId(BUILDER_NAME_FIELD + "_a", indexStr),
              "," + getNotNullValue(builderName)));
      textFields
          .add(new RestTextValueField(getIndexedFieldId(HOUSE_NUMBER_FIELD + "_a", indexStr),
              "," + getNotNullValue(houseNumber)));
      textFields
          .add(new RestTextValueField(getIndexedFieldId(STREET_NUMBER_FIELD + "_a", indexStr),
              "," + getNotNullValue(streetName)));
      textFields
          .add(new RestTextValueField(getIndexedFieldId(STREET_NUMBER_FIELD + "_a", indexStr),
              getNotNullValue(streetNumber)));
      textFields
          .add(new RestTextValueField(getIndexedFieldId(INSPECTION_DATE_FIELD + "_a", indexStr),
              "\nҮнэлгээ хийсэн огноо:\n" + getNotNullValue(getFirstValueByDelimiter(inspectionDate, "T", "error"))));
      textFields
          .add(
              new RestTextValueField(getIndexedFieldId(DOC_NUMBER_FIELD + "_a", indexStr),
                  "\nГэрчилгээний дугаар:\n" + getNotNullValue(chassisNumber)));

      String roundedCollAmount = getRoundedNumStr(getNotNullValue(collAmount));
      String roundedCollAssessment = getRoundedNumStr(getNotNullValue(collAssessment));

      textFields.add(new RestTextValueField(getIndexedFieldId(COLL_AMOUNT_FIELD + "_a", indexStr), roundedCollAmount));
      textFields
          .add(
              new RestTextValueField(getIndexedFieldId(COLL_ASSESSMENT_FIELD + "_a", indexStr), roundedCollAssessment));
      textFields
          .add(
              new RestTextValueField(getIndexedFieldId(OWNER_NAME_FIELD + "_a", indexStr),
                  getNotNullValue(ownerNames)));
      textFields
          .add(new RestTextValueField(getIndexedFieldId(OWNERSHIP_TYPE_FIELD + "_a", indexStr), ownershipType));
      textFields
          .add(new RestTextValueField(getIndexedFieldId(LINKED_ACCOUNT_NUMBER_FIELD + "_a", indexStr),
              loanAccountNumber));
    }

    if (colType.equals("Vehicle")) {

      textFields.add(new RestTextValueField(getIndexedFieldId(COLL_PRODUCT_DESCRIPTION_FIELD + "_b", indexStr),
          "\nБарьцааны код :\n" + getNotNullValue(collProduct)));
      textFields.add(new RestTextValueField(getIndexedFieldId(COLL_TYPE_FIELD + "_b", indexStr),
          "Барьцааны үндсэн төрөл: \n" + getNotNullValue(type)));
      textFields
          .add(new RestTextValueField(getIndexedFieldId(ADDRESS1_FIELD + "_b", indexStr),
              "\nХаяг 1\n" + getNotNullValue(address1)));
      textFields
          .add(new RestTextValueField(getIndexedFieldId(ADDRESS2_FIELD + "_b", indexStr),
              "\nХаяг 2\n" + getNotNullValue(address2)));
      textFields
          .add(new RestTextValueField(getIndexedFieldId(ADDRESS3_FIELD + "_b", indexStr),
              "\nХаяг 3\n" + getNotNullValue(address3)));

      textFields
          .add(new RestTextValueField(getIndexedFieldId(INSPECTION_DATE_FIELD + "_b", indexStr),
              "\nҮнэлгээ хийсэн огноо:\n" + getNotNullValue(getFirstValueByDelimiter(inspectionDate, "T", "err"))));
      textFields
          .add(
              new RestTextValueField(getIndexedFieldId(DOC_NUMBER_FIELD + "_b", indexStr),
                  "\nТоног төхөөрөмжийн дугаар :\n" + getNotNullValue(chassisNumber)));

      String roundedCollAmount = getRoundedNumStr(getNotNullValue(collAmount));
      String roundedCollAssessment = getRoundedNumStr(getNotNullValue(collAssessment));

      textFields.add(new RestTextValueField(getIndexedFieldId(COLL_AMOUNT_FIELD + "_b", indexStr), roundedCollAmount));
      textFields
          .add(
              new RestTextValueField(getIndexedFieldId(COLL_ASSESSMENT_FIELD + "_b", indexStr), roundedCollAssessment));
      textFields
          .add(
              new RestTextValueField(getIndexedFieldId(OWNER_NAME_FIELD + "_b", indexStr),
                  getNotNullValue(ownerNames)));
      textFields
          .add(new RestTextValueField(getIndexedFieldId(OWNERSHIP_TYPE_FIELD + "_b", indexStr), ownershipType));
      textFields
          .add(new RestTextValueField(getIndexedFieldId(LINKED_ACCOUNT_NUMBER_FIELD + "_b", indexStr),
              loanAccountNumber));
    }

    if (colType.equals("Machinery")) {

      textFields.add(new RestTextValueField(getIndexedFieldId(COLL_PRODUCT_DESCRIPTION_FIELD + "_c", indexStr),
          "\nБарьцааны код :\n" + getNotNullValue(collProduct)));
      textFields.add(new RestTextValueField(getIndexedFieldId(COLL_TYPE_FIELD + "_c", indexStr),
          "Барьцааны үндсэн төрөл: \n" + getNotNullValue(type)));
      textFields
          .add(new RestTextValueField(getIndexedFieldId(INSPECTION_DATE_FIELD + "_c", indexStr),
              "\nҮнэлгээ хийсэн огноо:\n" + getNotNullValue(getFirstValueByDelimiter(inspectionDate, "T", "error"))));
      textFields
          .add(
              new RestTextValueField(getIndexedFieldId(DOC_NUMBER_FIELD + "_c", indexStr),
                  getNotNullValue(chassisNumber)));

      String roundedCollAmount = getRoundedNumStr(getNotNullValue(collAmount));
      String roundedCollAssessment = getRoundedNumStr(getNotNullValue(collAssessment));

      textFields.add(new RestTextValueField(getIndexedFieldId(COLL_AMOUNT_FIELD + "_c", indexStr), roundedCollAmount));
      textFields
          .add(
              new RestTextValueField(getIndexedFieldId(COLL_ASSESSMENT_FIELD + "_c", indexStr), roundedCollAssessment));
      textFields
          .add(
              new RestTextValueField(getIndexedFieldId(OWNER_NAME_FIELD + "_c", indexStr),
                  getNotNullValue(ownerNames)));
      textFields
          .add(new RestTextValueField(getIndexedFieldId(OWNERSHIP_TYPE_FIELD + "_c", indexStr), ownershipType));
      textFields
          .add(new RestTextValueField(getIndexedFieldId(LINKED_ACCOUNT_NUMBER_FIELD + "_c", indexStr),
              loanAccountNumber));
    }
    if (colType.equals("Others")) {

      textFields.add(new RestTextValueField(getIndexedFieldId(COLL_PRODUCT_DESCRIPTION_FIELD + "_d", indexStr),
          "\nБарьцааны код :\n" + getNotNullValue(collProduct)));
      textFields.add(new RestTextValueField(getIndexedFieldId(COLL_TYPE_FIELD + "_d", indexStr),
          "Барьцааны үндсэн төрөл: \n" + getNotNullValue(type)));
      textFields
          .add(new RestTextValueField(getIndexedFieldId(INSPECTION_DATE_FIELD + "_d", indexStr),
              "\nҮнэлгээ хийсэн огноо:\n" + getNotNullValue(getFirstValueByDelimiter(inspectionDate, "T", "error"))));
      textFields
          .add(
              new RestTextValueField(getIndexedFieldId(DOC_NUMBER_FIELD + "_d", indexStr),
                  "\n Тайлбар хэсэгт оруулсан мэдээлэл:\n" + getNotNullValue(remarks)));

      String roundedCollAmount = getRoundedNumStr(getNotNullValue(collAmount));
      String roundedCollAssessment = getRoundedNumStr(getNotNullValue(collAssessment));

      textFields.add(new RestTextValueField(getIndexedFieldId(COLL_AMOUNT_FIELD + "_d", indexStr), roundedCollAmount));
      textFields
          .add(
              new RestTextValueField(getIndexedFieldId(COLL_ASSESSMENT_FIELD + "_d", indexStr), roundedCollAssessment));
      textFields
          .add(
              new RestTextValueField(getIndexedFieldId(OWNER_NAME_FIELD + "_d", indexStr),
                  getNotNullValue(ownerNames)));
      textFields
          .add(new RestTextValueField(getIndexedFieldId(OWNERSHIP_TYPE_FIELD + "_d", indexStr), ownershipType));
      textFields
          .add(new RestTextValueField(getIndexedFieldId(LINKED_ACCOUNT_NUMBER_FIELD + "_d", indexStr),
              loanAccountNumber));
    }
  }

  private void addEmptyTextFields(List<RestTextValueField> textFields, int index, String type) {
    String indexStr = String.valueOf(index);

    if (type.equals("_a")) {

      textFields.add(new RestTextValueField(getIndexedFieldId(COLL_PRODUCT_DESCRIPTION_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(COUNTRY_REGISTER_NUMBER_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(COLL_AMOUNT_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(COLL_ASSESSMENT_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(OWNER_NAME_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(OWNERSHIP_TYPE_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(LINKED_ACCOUNT_NUMBER_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(COLL_TYPE_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(CITY_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(ADDRESS1_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(ADDRESS2_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(ADDRESS3_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(BUILDER_NAME_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(HOUSE_NUMBER_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(STREET_NAME_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(STREET_NUMBER_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(INSPECTION_DATE_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(DOC_NUMBER_FIELD + type, indexStr), BLANK));
    }

    if (type.equals("_b")) {
      textFields.add(new RestTextValueField(getIndexedFieldId(COLL_PRODUCT_DESCRIPTION_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(COUNTRY_REGISTER_NUMBER_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(COLL_AMOUNT_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(COLL_ASSESSMENT_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(OWNER_NAME_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(OWNERSHIP_TYPE_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(LINKED_ACCOUNT_NUMBER_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(COLL_TYPE_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(ADDRESS1_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(ADDRESS2_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(ADDRESS3_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(INSPECTION_DATE_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(DOC_NUMBER_FIELD + type, indexStr), BLANK));
    }

    if (type.equals("_c")) {
      textFields.add(new RestTextValueField(getIndexedFieldId(COLL_PRODUCT_DESCRIPTION_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(COUNTRY_REGISTER_NUMBER_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(COLL_AMOUNT_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(COLL_ASSESSMENT_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(OWNER_NAME_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(OWNERSHIP_TYPE_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(LINKED_ACCOUNT_NUMBER_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(COLL_TYPE_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(INSPECTION_DATE_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(DOC_NUMBER_FIELD + type, indexStr), BLANK));
    }

    if (type.equals("_d")) {
      textFields.add(new RestTextValueField(getIndexedFieldId(COLL_PRODUCT_DESCRIPTION_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(COUNTRY_REGISTER_NUMBER_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(COLL_AMOUNT_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(COLL_ASSESSMENT_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(OWNER_NAME_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(OWNERSHIP_TYPE_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(LINKED_ACCOUNT_NUMBER_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(COLL_TYPE_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(INSPECTION_DATE_FIELD + type, indexStr), BLANK));
      textFields.add(new RestTextValueField(getIndexedFieldId(DOC_NUMBER_FIELD + type, indexStr), BLANK));
    }
  }

  private Map<String, Serializable> getCollateral(String collateralId) throws UseCaseException {
    GetCollateralByIdInput getCollateralByIdInput = new GetCollateralByIdInput(collateralId);
    GetCollateralById getCollateralById = new GetCollateralById(processRepository);

    Process process = getCollateralById.execute(getCollateralByIdInput);

    if (process == null) {
      return Collections.emptyMap();
    }

    Map<ParameterEntityType, Map<String, Serializable>> processParameters = process.getProcessParameters();

    Map<String, Serializable> collateralEntityParameters = processParameters.get(ParameterEntityType.COLLATERAL);

    Map<String, Serializable> collateralMap = new HashMap<>();

    for (Map.Entry<String, Serializable> collateralParameter : collateralEntityParameters.entrySet()) {
      String collateralsJSONString = String.valueOf(collateralParameter.getValue());
      JSONObject collateralJSON = new JSONObject(collateralsJSONString);

      // Buteegdehuun
      collateralMap.put(PRODUCT,
          JSONObject.NULL.equals(collateralJSON.get(PRODUCT)) ? null : String.valueOf(collateralJSON.get(PRODUCT)));

      // Ulsiin burtgeliin dugaar
      collateralMap.put(STATE_REGISTRATION_NUMBER,
          JSONObject.NULL.equals(collateralJSON.get(STATE_REGISTRATION_NUMBER)) ? null
              : String.valueOf(collateralJSON.get(STATE_REGISTRATION_NUMBER)));

      // // Baritsaanii dun,
      // collateralMap.put(AMOUNT_OF_COLLATERAL,
      // JSONObject.NULL.equals(collateralJSON.get(AMOUNT_OF_COLLATERAL)) ? null :
      // String.valueOf(collateralJSON.get(AMOUNT_OF_COLLATERAL)));
      //
      // // Unelgee
      // collateralMap.put(COLLATERAL_ASSESSMENT,
      // JSONObject.NULL.equals(collateralJSON.get(COLLATERAL_ASSESSMENT)) ? null :
      // String.valueOf(collateralJSON.get(COLLATERAL_ASSESSMENT)));

      // Baritsaanii dun field = COLLATERAL_ASSESSMENT
      collateralMap.put(AMOUNT_OF_COLLATERAL,
          JSONObject.NULL.equals(collateralJSON.get(AMOUNT_OF_COLLATERAL)) ? null
              : String.valueOf(collateralJSON.get(COLLATERAL_ASSESSMENT)));

      // Unelgee field = AMOUNT_OF_COLLATERAL
      collateralMap.put(COLLATERAL_ASSESSMENT,
          JSONObject.NULL.equals(collateralJSON.get(COLLATERAL_ASSESSMENT)) ? null
              : String.valueOf(collateralJSON.get(AMOUNT_OF_COLLATERAL)));

    }
    return collateralMap;
  }

  private String getOwnerNamesFromProcess(String collateralId) throws UseCaseException {
    GetCollateralByIdInput getCollateralByIdInput = new GetCollateralByIdInput(collateralId);
    GetCollateralById getCollateralById = new GetCollateralById(processRepository);

    Process process = getCollateralById.execute(getCollateralByIdInput);

    if (process == null) {
      return null;
    }

    Map<ParameterEntityType, Map<String, Serializable>> processParameters = process.getProcessParameters();
    Map<String, Serializable> collUDFs = processParameters.get(ParameterEntityType.UD_FIELD_COLLATERAL);

    if (null == collUDFs) {
      return null;
    }

    for (Map.Entry<String, Serializable> collUDFMap : collUDFs.entrySet()) {
      if (collUDFMap.getKey().equals(OWNER_NAMES_UDF_VARIABLE_ID)) {
        if (null == collUDFMap.getValue()) {
          return null;
        }

        return String.valueOf(collUDFMap.getValue());
      }
    }
    return null;
  }

  private String getIndexedFieldId(String id, String indexStr) {
    return id + UNDER_LINE + indexStr;
  }

  private String getNotNullValue(String fieldValue) {
    if (null != fieldValue) {
      return fieldValue;
    }
    return BLANK;
  }

  private String getDocumentPath(String registerNumber, String processRequestId) {
    long timeStamp = new Date().getTime();
    String timeStampStr = Long.toString(timeStamp);

    return BpmModuleConstants.CUSTOMERS_FOLDER + SLASH + registerNumber + SLASH + processRequestId + SLASH
        + LOAN_DECISION_DOCUMENT_NAME + "_" + timeStampStr;
  }

  private List<RestTextValueField> getTextFields(DelegateExecution execution) throws AimRepositoryException {
    List<RestTextValueField> textFields = new ArrayList<>();

    Map<String, Object> variables = execution.getVariables();

    String regNumber = (String) variables.get(REGISTER_NUMBER);
    String fullName = (String) execution.getVariable(FULL_NAME);

    String productName = String.valueOf(execution.getVariable(LOAN_PRODUCT));
    String systemDateStr = LocalDate.now().format(DateTimeFormatter.ISO_DATE);

    Double scoringScore;

    if (execution.getVariable(SCORING_SCORE) instanceof Integer) {
      scoringScore = Double.valueOf(String.valueOf(execution.getVariable(SCORING_SCORE)));
    } else {
      scoringScore = (Double) execution.getVariable(SCORING_SCORE);
    }
    String scoringLevel = (String) execution.getVariable(SCORING_LEVEL_RISK);

    String confirmedUserId = (String) execution.getVariable(CONFIRMED_USER_ID);
    String loanPurpose = (String) execution.getVariable(BORROWER_CATEGORY_CODE);
    String repaymentType = (String) execution.getVariable(REPAYMENT_TYPE);

    if (execution.getVariable(TERM) instanceof Long) {
      long term = (long) execution.getVariable(TERM);
      textFields.add(new RestTextValueField(TERM, String.valueOf(term)));
    }

    if (execution.getVariable(TERM) instanceof Integer) {
      Integer term = (Integer) execution.getVariable(TERM);
      textFields.add(new RestTextValueField(TERM, String.valueOf(term)));
    }

    String currentUserId = authenticationService.getCurrentUserId();

    String confirmedUserName;
    String currentUserName;

    if (!StringUtils.isBlank(confirmedUserId)) {
      User confirmedUser = userRepository.findById(UserId.valueOf(confirmedUserId));
      confirmedUserName = confirmedUser.getUserInfo().getUserName();
    } else {
      confirmedUserName = " ";
    }
    if (!StringUtils.isBlank(currentUserId)) {
      User currentUser = userRepository.findById(UserId.valueOf(currentUserId));
      currentUserName = currentUser.getUserInfo().getUserName();
    } else {
      currentUserName = " ";
    }

    String interestRate = String.valueOf(execution.getVariable("yearlyInterestRateString"));
    String loanAmountStr = String.valueOf(execution.getVariable(FIXED_ACCEPTED_LOAN_AMOUNT_STRING));

    textFields.add(new RestTextValueField(CONFIRMED_LOAN_AMOUNT_FIELD, loanAmountStr));

    String calculatedLoanAmount = String.valueOf(execution.getVariable("grantLoanAmountString"));
    String additionalSpecialCondition = String.valueOf(execution.getVariable("additionalSpecialCondition"));

    if (StringUtils.isBlank(additionalSpecialCondition) || additionalSpecialCondition.equals("null")) {
      textFields.add(new RestTextValueField(ADDITIONAL_SPECIAL_CONDITION_FIELD, BLANK));
    } else {
      textFields.add(new RestTextValueField(ADDITIONAL_SPECIAL_CONDITION_FIELD, additionalSpecialCondition));
    }

    // loan decision, establishment
    String loanDecisionRequestId = String.valueOf(execution.getVariable("sanctionedBy"));

    textFields.add(new RestTextValueField(SYSTEM_DATE_FIELD, systemDateStr));
    textFields.add(new RestTextValueField(REGISTER_NUMBER_FIELD, regNumber));

    textFields.add(new RestTextValueField(PROCESS_REQUEST_ID_FIELD, loanDecisionRequestId));
    textFields.add(new RestTextValueField(FULL_NAME_FIELD, fullName));

    textFields.add(new RestTextValueField(PRODUCT_NAME_FIELD, productName));

    textFields.add(new RestTextValueField(INTEREST_RATE_FIELD, interestRate));

    textFields.add(new RestTextValueField(LOAN_PURPOSE_FIELD, getNotNullValue(loanPurpose)));

    // sets dynamic currency type.
    textFields.add(new RestTextValueField(CURRENCY_FIELD, CURRENCY_MNT));
    textFields.add(new RestTextValueField(CALCULATED_LOAN_AMOUNT_FIELD, calculatedLoanAmount));

    textFields.add(new RestTextValueField(CURRENT_USER_NAME_FIELD, currentUserName));
    textFields.add(new RestTextValueField(CONF_USER_NAME_FIELD, confirmedUserName));

    textFields.add(new RestTextValueField(CONF_USER_ROLE_FIELD, ""));
    textFields.add(new RestTextValueField(USER_ROLE_FIELD, ""));

    textFields.add(new RestTextValueField(SCORE_FIELD, String.valueOf(scoringScore)));
    textFields.add(new RestTextValueField(SCORING_LEVEL_RISK, scoringLevel));

    if (null == repaymentType) {
      repaymentType = BLANK;
    }

    textFields.add(new RestTextValueField(REPAYMENT_TYPE_FIELD, repaymentType));

    setRoleFields(currentUserId, confirmedUserId, textFields);

    String fullNameFirstCBIndex = FULL_NAME_CO_BORROWER + "-1";
    String regNumFirstCBIndex = REGISTER_NUMBER_CO_BORROWER + "-1";

    if (execution.hasVariable(fullNameFirstCBIndex) && execution.hasVariable(regNumFirstCBIndex)) {
      String fullNameCB = (String) execution.getVariable(fullNameFirstCBIndex);
      String regNumCB = (String) execution.getVariable(regNumFirstCBIndex);

      textFields.add(new RestTextValueField(FULL_NAME_CO_BORROWER_FIELD, fullNameCB));
      textFields.add(new RestTextValueField(REGISTER_NUMBER_CO_BORROWER_FIELD, regNumCB));
    } else {
      textFields.add(new RestTextValueField(FULL_NAME_CO_BORROWER_FIELD, "-"));
      textFields.add(new RestTextValueField(REGISTER_NUMBER_CO_BORROWER_FIELD, "-"));
    }

    setBranchInfo(textFields);
    return textFields;
  }

  private void setRoleFields(String currentUserId, String confirmedUserId, List<RestTextValueField> textFields) {
    try {
      // todo : replace find by id method after jdbc impl done.
      List<Membership> currentUserMemberships = membershipRepository
          .listAllByUserId(TenantId.valueOf(BpmModuleConstants.TENANT_ID_XAC), UserId.valueOf(currentUserId));
      Membership membership = currentUserMemberships.get(0);
      Role userRole = roleRepository.findById(membership.getRoleId());

      if (null != userRole) {
        textFields.add(new RestTextValueField(USER_ROLE, userRole.getName()));
      }

      if (null != confirmedUserId) {
        List<Membership> confirmedUserMemberships = membershipRepository
            .listAllByUserId(TenantId.valueOf(BpmModuleConstants.TENANT_ID_XAC), UserId.valueOf(confirmedUserId));
        Membership confirmedUserMembership = confirmedUserMemberships.get(0);
        Role confirmedUserRole = roleRepository.findById(confirmedUserMembership.getRoleId());

        if (null != confirmedUserRole) {
          textFields.add(new RestTextValueField(CONFIRMED_USER_ROLE_FIELD, confirmedUserRole.getName()));
        }
      }
    } catch (AimRepositoryException e) {
      LOGGER.error(e.getMessage());
    }
  }

  private void setBranchInfo(List<RestTextValueField> textFields) throws AimRepositoryException {
    String currentUserId = authenticationService.getCurrentUserId();
    List<Membership> memberships = membershipRepository
        .listAllByUserId(TenantId.valueOf(TENANT_ID_XAC), UserId.valueOf(currentUserId));

    Membership membership = memberships.get(0);

    if (null != membership) {
      GroupId groupId = membership.getGroupId();

      Group group = groupRepository.findById(groupId);

      if (null != group) {
        String branchCode = group.getId().getId();
        String branchName = group.getName();

        textFields.add(new RestTextValueField(BRANCH_ID_FIELD, branchCode));
        textFields.add(new RestTextValueField(BRANCH_NAME_FIELD, branchName));
      }
    }
  }
}
