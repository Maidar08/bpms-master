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
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.model.collateral.Collateral;
import mn.erin.domain.bpm.model.collateral.CollateralInfo;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.model.process.Process;
import mn.erin.domain.bpm.model.product.Product;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.DocumentRepository;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.repository.ProductRepository;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.usecase.process.collateral.GetCollateralById;
import mn.erin.domain.bpm.usecase.process.collateral.GetCollateralByIdInput;
import mn.erin.domain.bpm.usecase.product.GetProduct;
import mn.erin.domain.bpm.usecase.product.UniqueProductInput;

import static mn.erin.bpms.loan.consumption.constant.CamundaVariableConstants.GRANT_LOAN_AMOUNT;
import static mn.erin.bpms.loan.consumption.constant.CamundaVariableConstants.ORDER_VARIABLE;
import static mn.erin.bpms.loan.consumption.utils.NumberUtils.getRoundedNumStr;
import static mn.erin.domain.bpm.BpmMessagesConstants.MICRO_GRANT_AMOUNT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.MICRO_GRANT_AMOUNT_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.ALFRESCO;
import static mn.erin.domain.bpm.BpmModuleConstants.AMOUNT_OF_COLLATERAL;
import static mn.erin.domain.bpm.BpmModuleConstants.APPLICATION_TYPE_MICRO_LOAN;
import static mn.erin.domain.bpm.BpmModuleConstants.BLANK;
import static mn.erin.domain.bpm.BpmModuleConstants.BORROWER_TYPE_CATEGORY;
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
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_DECISION_DOCUMENT_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PRODUCT;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_TERM;
import static mn.erin.domain.bpm.BpmModuleConstants.OWNER_NAMES_UDF_VARIABLE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PRODUCT;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.SCORING_LEVEL_RISK;
import static mn.erin.domain.bpm.BpmModuleConstants.SCORING_SCORE;
import static mn.erin.domain.bpm.BpmModuleConstants.SLASH;
import static mn.erin.domain.bpm.BpmModuleConstants.STATE_REGISTRATION_NUMBER;
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
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.UNDER_LINE;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.USER_ROLE;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.USER_ROLE_FIELD;

/**
 * @author Tamir
 */
public class GenerateMicroLoanDecisionTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GenerateMicroLoanDecisionTask.class);
  public static final String NULL_STRING = "null";

  private final AuthenticationService authenticationService;
  private final MembershipRepository membershipRepository;
  private final GroupRepository groupRepository;
  private final DocumentRepository documentRepository;
  private final RoleRepository roleRepository;
  private final UserRepository userRepository;
  private final ProcessRepository processRepository;
  private final ProductRepository productRepository;

  public GenerateMicroLoanDecisionTask(AuthenticationService authenticationService, MembershipRepository membershipRepository,
      GroupRepository groupRepository, DocumentRepository documentRepository,
      RoleRepository roleRepository, UserRepository userRepository, ProcessRepository processRepository, ProductRepository productRepository)
  {
    this.authenticationService = authenticationService;
    this.membershipRepository = membershipRepository;

    this.groupRepository = groupRepository;
    this.documentRepository = documentRepository;

    this.roleRepository = roleRepository;
    this.userRepository = userRepository;

    this.processRepository = processRepository;
    this.productRepository = productRepository;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String registerNumber = (String) execution.getVariable(REGISTER_NUMBER);
    String processRequestId = (String) execution.getVariable(PROCESS_REQUEST_ID);

    String caseInstanceId = (String) execution.getVariable(CASE_INSTANCE_ID);
    String documentPath = getDocumentPath(registerNumber, processRequestId);

    List<RestTextValueField> textFields = getTextFields(execution);
    List<RestTextValueField> collTextFields = getCollateralTextFields(textFields, execution);

    LOGGER.info("########## Generates MICRO loan decision document with REQUEST ID = [{}]", processRequestId);

    List<String> documentIdList = transformDocument(TEMPLATE_PATH_LOAN_DECISION_WITH_COLLATERAL, documentPath, collTextFields);

    if (!documentIdList.isEmpty())
    {
      removePreviousLoanDecisionDocument(caseInstanceId, processRequestId);
    }
    for (String documentId : documentIdList)
    {
      persistLoanDecisionDocument(documentId, caseInstanceId, processRequestId);
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
    LOGGER.info("############ Removes previous MICRO loan decision document with REQUEST ID = [{}]", processRequestId);

    documentRepository.removeBy(caseInstanceId, CATEGORY_LOAN_DECISION_DOCUMENT, SUB_CATEGORY_LOAN_DECISION_DOCUMENT, LOAN_DECISION_DOCUMENT_NAME);
  }

  private void persistLoanDecisionDocument(String documentId, String caseInstanceId, String processRequestId) throws BpmRepositoryException
  {
    if (null != documentId)
    {
      LOGGER.info("############ Persists MICRO loan decision document with REQUEST ID = [{}]", processRequestId);
      documentRepository.create(documentId, DOCUMENT_INFO_ID_LOAN_DECISION, caseInstanceId, LOAN_DECISION_DOCUMENT_NAME,
          CATEGORY_LOAN_DECISION_DOCUMENT, SUB_CATEGORY_LOAN_DECISION_DOCUMENT, documentId, ALFRESCO);
    }

    LOGGER.info("########## Successful generated MICRO loan decision with REQUEST ID = [{}]", processRequestId);
  }

  // TODO : needs to be dynamically fill depending on collateral size in the future!

  /**
   * Adds collaterals text fields by collateral info from execution and process parameter.
   *
   * @param textFields text fields to fill.
   * @param execution  Delegation execution.
   * @return added text fields.
   * @throws UseCaseException when get process use case is not reachable or usable.
   */
  private List<RestTextValueField> getCollateralTextFields(List<RestTextValueField> textFields, DelegateExecution execution) throws UseCaseException
  {
    if (null == execution.getVariable(COLLATERAL_LIST))
    {
      return addEmptyCollateralInfos(textFields);
    }

    if (execution.getVariable(COLLATERAL_LIST) instanceof List)
    {
      return CollateralUtils.getCollateralTextFields(textFields,execution);
    }

    Map<String, Map<String, Object>> collateralsMap = (Map) execution.getVariable(COLLATERAL_LIST);

    if (null == collateralsMap || collateralsMap.isEmpty())
    {
      return addEmptyCollateralInfos(textFields);
    }

    List<BigDecimal> collAmounts = new LinkedList<>();
    List<BigDecimal> collAssessments = new LinkedList<>();

    int collSize = collateralsMap.size();
    fillEmptyCollateralRows(collSize, textFields);

    String customerFullName = String.valueOf(execution.getVariable(FULL_NAME));

    int rowIndex = 1;
    while (rowIndex <= collSize)
    {
      for (Map.Entry<String, Map<String, Object>> collMapEntity : collateralsMap.entrySet())
      {
        String collateralId = collMapEntity.getKey();
        Map<String, Object> selectedCollMap = collMapEntity.getValue();

        Integer order = Integer.valueOf(String.valueOf(selectedCollMap.get(ORDER_VARIABLE)));

        if (order.equals(rowIndex))
        {
          Map<String, Serializable> collateralMap = getCollateral(collateralId);

          if (!collateralMap.isEmpty())
          {
            // Adds collateral text fields from process parameter.
            addCollateralFromProcessParam(execution, collateralMap, collateralId, collAmounts, collAssessments, textFields, order, customerFullName);
          }
          else
          {
            // Adds collateral text fields from execution variables.
            addCollateralFromExecution(execution, collateralId, collAmounts, collAssessments, textFields, order, customerFullName);
          }
        }
      }

      rowIndex++;
    }

    BigDecimal totalCollAmount = collAmounts.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    BigDecimal totalCollAssessment = collAssessments.stream().reduce(BigDecimal.ZERO, BigDecimal::add);

    textFields.add(new RestTextValueField(COLL_TOTAL_AMOUNT_FIELD, getRoundedNumStr(String.valueOf(totalCollAmount))));
    textFields.add(new RestTextValueField(COLL_TOTAL_ASSESSMENT_FIELD, getRoundedNumStr(String.valueOf(totalCollAssessment))));

    return textFields;
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

  private void addCollateralFromProcessParam(DelegateExecution execution, Map<String, Serializable> collateralMap, String collateralId, List<BigDecimal> collAmounts,
      List<BigDecimal> collAssessments, List<RestTextValueField> textFields, int order, String customerFullName) throws UseCaseException
  {
    String collProduct = String.valueOf(collateralMap.get(PRODUCT));

    if (!StringUtils.isBlank(collProduct))
    {
      collProduct = collProduct + ", ";
    }

    LOGGER.info("############### FILLS MICRO COLLATERAL PRODUCT ROW NUMBER = [{}]", order);
    LOGGER.info("############### FILLS MICRO COLLATERAL PRODUCT = [{}]", collProduct);

    String stateRegisterNumber = String.valueOf(collateralMap.get(STATE_REGISTRATION_NUMBER));

    LOGGER.info("############### FILLS MICRO COLLATERAL STATE REGISTER NUMBER = [{}]", stateRegisterNumber);

    String collAmountStr = String.valueOf(collateralMap.get(AMOUNT_OF_COLLATERAL));

    if (null != collAmountStr && !NULL_STRING.equals(collAmountStr))
    {
      collAmounts.add(new BigDecimal(collAmountStr));
    }
    LOGGER.info("############### FILLS MICRO COLLATERAL AMOUNT = [{}]", collAmountStr);

    String collAssessmentStr = String.valueOf(collateralMap.get(COLLATERAL_ASSESSMENT));

    LOGGER.info("############### FILLS MICRO COLLATERAL ASSESSMENT = [{}]", collAssessmentStr);

    if (null != collAssessmentStr && !NULL_STRING.equals(collAssessmentStr))
    {
      collAssessments.add(new BigDecimal(collAssessmentStr));
    }

    String ownerNames;
    if (execution.getVariable(collateralId) instanceof Collateral)
    {
      Collateral collateral = (Collateral) execution.getVariable(collateralId);
      List<String> ownerNameList = collateral.getOwnerNames();
      ownerNames = null == ownerNameList ? BLANK : StringUtils.join(ownerNameList, ",");
    }
    else
    {
      ownerNames = getOwnerNamesFromProcess(collateralId);
      if (null == ownerNames)
      {
        ownerNames = BLANK;
      }
    }

    LOGGER.info("############### FILLS COLLATERAL OWNER NAMES = [{}]", ownerNames);

    addCollTextFields(textFields, order, collProduct, stateRegisterNumber, collAmountStr, collAssessmentStr, ownerNames);
  }

  private void addCollateralFromExecution(DelegateExecution execution, String collateralId, List<BigDecimal> collAmounts,
      List<BigDecimal> collAssessments, List<RestTextValueField> textFields, int order, String customerFullName)
  {
    if (execution.hasVariable(collateralId) && null != execution.getVariable(collateralId))
    {
      Collateral collateral = (Collateral) execution.getVariable(collateralId);
      CollateralInfo collateralInfo = collateral.getCollateralInfo();

      // Collateral Amount
      BigDecimal collAmount = collateralInfo.getAvailableAmount();

      if (null != collAmount)
      {
        collAmounts.add(collAmount);
      }
      String collAmountStr = String.valueOf(collAmount);

      // Collateral Assessment
      BigDecimal collAssessment = collateral.getAmountOfAssessment();

      if (null != collAssessment)
      {
        collAssessments.add(collAssessment);
      }
      String collAssessmentStr = String.valueOf(collAssessment);

      // Owner names
      List<String> ownerNames = collateral.getOwnerNames();
      String ownerNamesStr = getCommaSeparatedValues(ownerNames);

      if (StringUtils.isBlank(ownerNamesStr))
      {
        ownerNamesStr = customerFullName;
      }

      // BLANK value is State register number.
      addCollTextFields(textFields, order, collateralId, BLANK, collAmountStr, collAssessmentStr, ownerNamesStr);
    }
  }

  private void fillEmptyCollateralRows(int collSize, List<RestTextValueField> textFields)
  {
    int empRowStartIndex;

    if (collSize > 10)
    {
      empRowStartIndex = 0;
    }
    else if (collSize >= 10)
    {
      empRowStartIndex = 0;
    }
    else
    {
      empRowStartIndex = collSize + 1;
    }

    for (int emptyRowIndex = empRowStartIndex; emptyRowIndex <= 10; emptyRowIndex++)
    {
      addEmptyTextFields(textFields, emptyRowIndex);
    }
  }

  private String getCommaSeparatedValues(List<String> strList)
  {
    if (null == strList)
    {
      return BLANK;
    }

    return String.join(",", strList);
  }

  private void addCollTextFields(List<RestTextValueField> textFields, int index, String collProduct, String stateRegDesc, String collAmount,
      String collAssessment, String ownerNames)
  {
    String indexStr = String.valueOf(index);

    textFields.add(new RestTextValueField(indexStr, indexStr));
    textFields.add(new RestTextValueField(getIndexedFieldId(COLL_PRODUCT_DESCRIPTION_FIELD, indexStr), getNotNullValue(collProduct)));
    textFields.add(new RestTextValueField(getIndexedFieldId(COUNTRY_REGISTER_NUMBER_FIELD, indexStr), getNotNullValue(stateRegDesc)));

    String roundedCollAmount = getRoundedNumStr(getNotNullValue(collAmount));
    String roundedCollAssessment = getRoundedNumStr(getNotNullValue(collAssessment));

    textFields.add(new RestTextValueField(getIndexedFieldId(COLL_AMOUNT_FIELD, indexStr), roundedCollAmount));
    textFields.add(new RestTextValueField(getIndexedFieldId(COLL_ASSESSMENT_FIELD, indexStr), roundedCollAssessment));
    textFields.add(new RestTextValueField(getIndexedFieldId(OWNER_NAME_FIELD, indexStr), getNotNullValue(ownerNames)));
  }

  private void addEmptyTextFields(List<RestTextValueField> textFields, int index)
  {
    String indexStr = String.valueOf(index);

    textFields.add(new RestTextValueField(indexStr, BLANK));
    textFields.add(new RestTextValueField(getIndexedFieldId(COLL_PRODUCT_DESCRIPTION_FIELD, indexStr), BLANK));
    textFields.add(new RestTextValueField(getIndexedFieldId(COUNTRY_REGISTER_NUMBER_FIELD, indexStr), BLANK));

    textFields.add(new RestTextValueField(getIndexedFieldId(COLL_AMOUNT_FIELD, indexStr), BLANK));
    textFields.add(new RestTextValueField(getIndexedFieldId(COLL_ASSESSMENT_FIELD, indexStr), BLANK));
    textFields.add(new RestTextValueField(getIndexedFieldId(OWNER_NAME_FIELD, indexStr), BLANK));
  }

  private Map<String, Serializable> getCollateral(String collateralId) throws UseCaseException
  {
    GetCollateralByIdInput getCollateralByIdInput = new GetCollateralByIdInput(collateralId);
    GetCollateralById getCollateralById = new GetCollateralById(processRepository);

    Process process = getCollateralById.execute(getCollateralByIdInput);

    if (process == null)
    {
      return Collections.emptyMap();
    }

    Map<ParameterEntityType, Map<String, Serializable>> processParameters = process.getProcessParameters();
    Map<String, Serializable> collateralEntityParameters = processParameters.get(ParameterEntityType.COLLATERAL);

    Map<String, Serializable> collateralMap = new HashMap<>();

    for (Map.Entry<String, Serializable> collateralParameter : collateralEntityParameters.entrySet())
    {
      String collateralsJSONString = String.valueOf(collateralParameter.getValue());
      JSONObject collateralJSON = new JSONObject(collateralsJSONString);

      // Buteegdehuun
      collateralMap.put(PRODUCT,
          JSONObject.NULL.equals(collateralJSON.get(PRODUCT)) ? null : String.valueOf(collateralJSON.get(PRODUCT)));

      // Ulsiin burtgeliin dugaar
      collateralMap.put(STATE_REGISTRATION_NUMBER,
          JSONObject.NULL.equals(collateralJSON.get(STATE_REGISTRATION_NUMBER)) ? null : String.valueOf(collateralJSON.get(STATE_REGISTRATION_NUMBER)));

      //      // Baritsaanii dun,
      //      collateralMap.put(AMOUNT_OF_COLLATERAL,
      //          JSONObject.NULL.equals(collateralJSON.get(AMOUNT_OF_COLLATERAL)) ? null : String.valueOf(collateralJSON.get(AMOUNT_OF_COLLATERAL)));
      //
      //      // Unelgee
      //      collateralMap.put(COLLATERAL_ASSESSMENT,
      //          JSONObject.NULL.equals(collateralJSON.get(COLLATERAL_ASSESSMENT)) ? null : String.valueOf(collateralJSON.get(COLLATERAL_ASSESSMENT)));

      // TODO : test implementation impact on server.

      // Baritsaanii dun field = COLLATERAL_ASSESSMENT
      collateralMap.put(AMOUNT_OF_COLLATERAL,
          JSONObject.NULL.equals(collateralJSON.get(AMOUNT_OF_COLLATERAL)) ? null : String.valueOf(collateralJSON.get(COLLATERAL_ASSESSMENT)));

      // Unelgee field = AMOUNT_OF_COLLATERAL
      collateralMap.put(COLLATERAL_ASSESSMENT,
          JSONObject.NULL.equals(collateralJSON.get(COLLATERAL_ASSESSMENT)) ? null : String.valueOf(collateralJSON.get(AMOUNT_OF_COLLATERAL)));
    }
    return collateralMap;
  }

  private String getOwnerNamesFromProcess(String collateralId) throws UseCaseException
  {
    GetCollateralByIdInput getCollateralByIdInput = new GetCollateralByIdInput(collateralId);
    GetCollateralById getCollateralById = new GetCollateralById(processRepository);

    Process process = getCollateralById.execute(getCollateralByIdInput);

    if (process == null)
    {
      return null;
    }

    Map<ParameterEntityType, Map<String, Serializable>> processParameters = process.getProcessParameters();
    Map<String, Serializable> collUDFs = processParameters.get(ParameterEntityType.UD_FIELD_COLLATERAL);

    if (null == collUDFs)
    {
      return null;
    }

    for (Map.Entry<String, Serializable> collUDFMap : collUDFs.entrySet())
    {
      if (collUDFMap.getKey().equals(OWNER_NAMES_UDF_VARIABLE_ID))
      {
        if (null == collUDFMap.getValue())
        {
          return null;
        }

        return String.valueOf(collUDFMap.getValue());
      }
    }
    return null;
  }

  private String getIndexedFieldId(String id, String indexStr)
  {
    return id + UNDER_LINE + indexStr;
  }

  private String getNotNullValue(String fieldValue)
  {
    if (null != fieldValue)
    {
      return fieldValue;
    }
    return BLANK;
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

    String productName = String.valueOf(execution.getVariable(LOAN_PRODUCT));
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
    String loanPurpose = (String) execution.getVariable(BORROWER_TYPE_CATEGORY);
    String repaymentType = getRepaymentType(execution);

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

    String interestRate = String.valueOf(execution.getVariable("yearlyInterestRateString"));
    String loanAmountStr = String.valueOf(execution.getVariable(FIXED_ACCEPTED_LOAN_AMOUNT_STRING));

    textFields.add(new RestTextValueField(CONFIRMED_LOAN_AMOUNT_FIELD, loanAmountStr));

    setCalculatedLoanAmountField(execution, textFields);

    String additionalSpecialCondition = String.valueOf(execution.getVariable("additionalSpecialCondition"));

    if (StringUtils.isBlank(additionalSpecialCondition) || NULL_STRING.equals(additionalSpecialCondition))
    {
      textFields.add(new RestTextValueField(ADDITIONAL_SPECIAL_CONDITION_FIELD, BLANK));
    }
    else
    {
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
    textFields.add(new RestTextValueField(LOAN_PURPOSE_FIELD, loanPurpose));

    // sets dynamic currency type.
    textFields.add(new RestTextValueField(CURRENCY_FIELD, CURRENCY_MNT));
    textFields.add(new RestTextValueField(CURRENT_USER_NAME_FIELD, currentUserName));
    textFields.add(new RestTextValueField(CONF_USER_NAME_FIELD, confirmedUserName));

    textFields.add(new RestTextValueField(CONF_USER_ROLE_FIELD, ""));
    textFields.add(new RestTextValueField(USER_ROLE_FIELD, ""));

    textFields.add(new RestTextValueField(SCORE_FIELD, String.valueOf(scoringScore)));
    textFields.add(new RestTextValueField(SCORING_LEVEL_RISK, scoringLevel));

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

    setBranchInfo(textFields);
    return textFields;
  }

  private String getRepaymentType(DelegateExecution execution)
  {
    String loanProductId = String.valueOf(execution.getVariable(LOAN_PRODUCT));

    if (null == loanProductId)
    {
      return BLANK;
    }

    if (loanProductId.length() > 4)
    {
      loanProductId = loanProductId.substring(0, 4);
    }

    UniqueProductInput input = new UniqueProductInput(loanProductId, APPLICATION_TYPE_MICRO_LOAN);
    GetProduct getProduct = new GetProduct(productRepository);

    try
    {
      Product product = getProduct.execute(input);
      return product.getCategoryDescription();
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage());
      return BLANK;
    }
  }

  private void setCalculatedLoanAmountField(DelegateExecution execution, List<RestTextValueField> textFields) throws BpmServiceException
  {
    Object grantAmount = execution.getVariable(GRANT_LOAN_AMOUNT);

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
