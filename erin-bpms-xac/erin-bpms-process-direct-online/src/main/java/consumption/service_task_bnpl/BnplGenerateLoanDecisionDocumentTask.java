package consumption.service_task_bnpl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.AimServiceRegistry;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.service.BpmServiceException;

import static mn.erin.domain.bpm.BpmModuleConstants.ALFRESCO;
import static mn.erin.domain.bpm.BpmModuleConstants.BLANK;
import static mn.erin.domain.bpm.BpmModuleConstants.BNPL_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.CATEGORY_LOAN_DECISION_DOCUMENT;
import static mn.erin.domain.bpm.BpmModuleConstants.CURRENCY_MNT;
import static mn.erin.domain.bpm.BpmModuleConstants.DOCUMENT_INFO_ID_LOAN_DECISION;
import static mn.erin.domain.bpm.BpmModuleConstants.ERROR_CAUSE;
import static mn.erin.domain.bpm.BpmModuleConstants.FULL_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.INVOICE_AMOUNT_75;
import static mn.erin.domain.bpm.BpmModuleConstants.ISO_DATE_FORMAT;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_DECISION_DOCUMENT_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PRODUCT_DESCRIPTION;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PURPOSE;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.REPAYMENT_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.SLASH;
import static mn.erin.domain.bpm.BpmModuleConstants.SUB_CATEGORY_LOAN_DECISION_DOCUMENT;
import static mn.erin.domain.bpm.BpmModuleConstants.TEMPLATE_PATH_BNPL;
import static mn.erin.domain.bpm.BpmModuleConstants.TENANT_ID_XAC;
import static mn.erin.domain.bpm.BpmModuleConstants.TERM;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.BRANCH_ID_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.CALCULATED_LOAN_AMOUNT_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.CONFIRMED_LOAN_AMOUNT_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.CURRENCY_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.FULL_NAME_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.INTEREST_RATE_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.LOAN_GRANT_DATE_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.LOAN_PURPOSE_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.LOAN_TERM_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.PROCESS_REQUEST_ID_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.PRODUCT_NAME_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.REGISTER_NUMBER_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.REPAYMENT_TYPE_FIELD;
import static mn.erin.domain.bpm.util.process.BpmUtils.getPdfBase64;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

public class BnplGenerateLoanDecisionDocumentTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(BnplGenerateLoanDecisionDocumentTask.class);

  private final AimServiceRegistry aimServiceRegistry;
  private final BpmsRepositoryRegistry bpmsRepositoryRegistry;
  private final MembershipRepository membershipRepository;
  private final GroupRepository groupRepository;

  public BnplGenerateLoanDecisionDocumentTask(AimServiceRegistry aimServiceRegistry,
      BpmsRepositoryRegistry bpmsRepositoryRegistry, MembershipRepository membershipRepository,
      GroupRepository groupRepository)
  {
    this.aimServiceRegistry = aimServiceRegistry;
    this.bpmsRepositoryRegistry = bpmsRepositoryRegistry;
    this.membershipRepository = membershipRepository;
    this.groupRepository = groupRepository;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    try
    {
      /* Get variables from execution. */
      String registerNumber = (String) execution.getVariable(REGISTER_NUMBER);
      String processRequestId = (String) execution.getVariable(PROCESS_REQUEST_ID);
      String processType = (String) execution.getVariable(PROCESS_TYPE_ID);
      String processInstanceId = (String) execution.getVariable(PROCESS_INSTANCE_ID);
      String documentPath = getDocumentPath(registerNumber, processRequestId);

      List<RestTextValueField> restTextValueFields = getTextFields(execution, processType, processRequestId);
      LOGGER.info("########## Generates loan decision document with REQUEST ID = [{}]", processRequestId);

      List<String> documentIdList = transformDocument(TEMPLATE_PATH_BNPL, documentPath, restTextValueFields);
      execution.setVariable("bnplLoanDecisionDocList", documentIdList);

      if (!documentIdList.isEmpty())
      {
        removePreviousLoanDecisionDocument(processInstanceId, processRequestId);

        for (String documentId : documentIdList)
        {
          persistLoanDecisionDocument(documentId, processInstanceId, processRequestId);
        }

        List<String> returnValue = getPdfBase64(documentIdList);

        if (null == returnValue || returnValue.size() != 2)
        {
          throw new BpmServiceException("BNPL Loan decision file was not found when uploading to LDMS");
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      execution.setVariable(ERROR_CAUSE, e.getMessage());
      throw new BpmnError("Generate BNPL Loan Decision Document", e.getMessage());
    }
  }

  private String getDocumentPath(String registerNumber, String processRequestId)
  {
    long timeStamp = new Date().getTime();
    String timeStampStr = Long.toString(timeStamp);

    return BpmModuleConstants.CUSTOMERS_FOLDER + SLASH + registerNumber + SLASH + processRequestId + SLASH + LOAN_DECISION_DOCUMENT_NAME + "_" + timeStampStr;
  }

  private List<RestTextValueField> getTextFields(DelegateExecution execution, String processType, String processRequestId)
      throws AimRepositoryException, ParseException
  {
    List<RestTextValueField> textFields = new ArrayList<>();
    textFields.add(new RestTextValueField(PROCESS_REQUEST_ID_FIELD, processRequestId));

    Map<String, Object> variables = execution.getVariables();

    String regNumber = (String) variables.get(REGISTER_NUMBER);
    String fullName = (String) execution.getVariable(FULL_NAME);

    String productDescription = String.valueOf(execution.getVariable(LOAN_PRODUCT_DESCRIPTION));

    String loanPurpose = (String) execution.getVariable(LOAN_PURPOSE);
    String repaymentType = (String) execution.getVariable(REPAYMENT_TYPE);
    String loanTerm = getValidString(execution.getVariable(TERM));
    String loanAmountStr = String.valueOf(execution.getVariable(INVOICE_AMOUNT_75));

    textFields.add(new RestTextValueField(CONFIRMED_LOAN_AMOUNT_FIELD, loanAmountStr));

    String calculatedLoanAmount = String.valueOf(execution.getVariable(BNPL_LOAN_AMOUNT));

    textFields.add(new RestTextValueField(REGISTER_NUMBER_FIELD, regNumber));
    textFields.add(new RestTextValueField(FULL_NAME_FIELD, fullName));

    textFields.add(new RestTextValueField(PRODUCT_NAME_FIELD, productDescription));
    textFields.add(new RestTextValueField(INTEREST_RATE_FIELD, "0"));

    textFields.add(new RestTextValueField(LOAN_PURPOSE_FIELD, loanPurpose));
    textFields.add(new RestTextValueField(CURRENCY_FIELD, CURRENCY_MNT));

    textFields.add(new RestTextValueField(CALCULATED_LOAN_AMOUNT_FIELD, calculatedLoanAmount));

    if (null == repaymentType)
    {
      repaymentType = BLANK;
    }

    textFields.add(new RestTextValueField(REPAYMENT_TYPE_FIELD, repaymentType));
    setBranchInfo(textFields);

    LocalDateTime date = LocalDateTime.now();
    DateFormat dateFormat = new SimpleDateFormat(ISO_DATE_FORMAT);

    String loanGrantDateString = dateFormat.format(dateFormat.parse(date.toString()));

    textFields.add(new RestTextValueField(LOAN_GRANT_DATE_FIELD, loanGrantDateString));
    textFields.add(new RestTextValueField(LOAN_TERM_FIELD, loanTerm));

    return textFields;
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
        textFields.add(new RestTextValueField(BRANCH_ID_FIELD, branchCode));
      }
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

    RestTransformDocumentOutput output = transformerService.transform(input, queryParams);

    return Arrays.asList(output.getBaseDocumentId(), output.getDocumentId());
  }

  private void removePreviousLoanDecisionDocument(String caseInstanceId, String processRequestId)
  {
    LOGGER.info("############ Removes previous BNPL loan decision document with REQUEST ID = [{}]", processRequestId);

    bpmsRepositoryRegistry.getDocumentRepository()
        .removeBy(caseInstanceId, CATEGORY_LOAN_DECISION_DOCUMENT, SUB_CATEGORY_LOAN_DECISION_DOCUMENT, LOAN_DECISION_DOCUMENT_NAME);
  }

  private void persistLoanDecisionDocument(String documentId, String caseInstanceId, String processRequestId) throws BpmRepositoryException
  {
    if (null != documentId)
    {
      LOGGER.info("############ Persists BNPL loan decision document with REQUEST ID = [{}]", processRequestId);
      bpmsRepositoryRegistry.getDocumentRepository().create(documentId, DOCUMENT_INFO_ID_LOAN_DECISION, caseInstanceId, LOAN_DECISION_DOCUMENT_NAME,
          CATEGORY_LOAN_DECISION_DOCUMENT, SUB_CATEGORY_LOAN_DECISION_DOCUMENT, documentId, ALFRESCO);
    }
    LOGGER.info("########## Successful generated BNPL loan decision with REQUEST ID = [{}]", processRequestId);
  }
}
