package consumption.service_task.direct_online_salary;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.AimServiceRegistry;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.repository.DocumentInfoRepository;
import mn.erin.domain.bpm.repository.DocumentRepository;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.DocumentService;
import mn.erin.domain.bpm.usecase.document.UploadDocuments;
import mn.erin.domain.bpm.usecase.document.UploadDocumentsInput;
import mn.erin.domain.bpm.usecase.document.UploadFile;
import mn.erin.domain.bpm.util.process.BpmUtils;

import static consumption.constant.CamundaVariableConstants.GENDER;
import static consumption.service_task.direct_online_salary.OnlineSalaryGenerateLoanDecisionDocumentTask.NULL_STRING;
import static mn.erin.common.utils.NumberUtils.bigDecimalToString;
import static mn.erin.domain.bpm.BpmModuleConstants.ACTION_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.ADDRESS;
import static mn.erin.domain.bpm.BpmModuleConstants.ALFRESCO;
import static mn.erin.domain.bpm.BpmModuleConstants.APPLICATION_INFO_ID_LOAN;
import static mn.erin.domain.bpm.BpmModuleConstants.BNPL_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CATEGORY_LOAN_APPLICATION;
import static mn.erin.domain.bpm.BpmModuleConstants.DAY_OF_PAYMENT;
import static mn.erin.domain.bpm.BpmModuleConstants.EXTEND;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ACCEPTED_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ACCEPTED_LOAN_AMOUNT_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.FULL_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.HAS_ACTIVE_LOAN_ACCOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.INTEREST_RATE;
import static mn.erin.domain.bpm.BpmModuleConstants.INVOICE_AMOUNT_75;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_APPLICATION_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_APPLICATION_NAME_BNPL;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_APPLICATION_NAME_PDF;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PURPOSE;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.OLD_FIXED_ACCEPTED_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_SALARY_PROCESS_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.SLASH;
import static mn.erin.domain.bpm.BpmModuleConstants.SOURCE_LDMS;
import static mn.erin.domain.bpm.BpmModuleConstants.SUB_CATEGORY_LOAN_APPLICATION;
import static mn.erin.domain.bpm.BpmModuleConstants.TEMPLATE_PATH_BNPL_LOAN_APPLICATION;
import static mn.erin.domain.bpm.BpmModuleConstants.TEMPLATE_PATH_LOAN_APPLICATION;
import static mn.erin.domain.bpm.BpmModuleConstants.TERM;
import static mn.erin.domain.bpm.BpmModuleConstants.TOPUP;
import static mn.erin.domain.bpm.BpmModuleConstants.TRACK_NUMBER;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.ADDRESS_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.DAY_OF_PAYMENT_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.FIRST_NAME_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.INTEREST_RATE_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.LAST_NAME_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.LOAN_PURPOSE_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.LOAN_TERM_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.REGISTER_NUMBER_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.REQUESTED_LOAN_AMOUNT_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.SEX_FIELD;
import static mn.erin.domain.bpm.util.process.BpmUtils.getPdfBase64;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;
import static mn.erin.domain.bpm.util.process.BpmUtils.removeCommaAndGetBigDecimal;
import static mn.erin.domain.bpm.util.process.BpmUtils.toBigDecimal;
import static mn.erin.domain.bpm.util.process.DigitalLoanUtils.updateProcessParameters;
import static mn.erin.domain.bpm.util.process.DigitalLoanUtils.updateRequestParameters;

public class GenerateLoanApplicationTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GenerateLoanApplicationTask.class);
  private final DocumentRepository documentRepsitory;
  private final AuthenticationService authenticationService;
  private final AuthorizationService authorizationService;
  private final DocumentService documentService;
  private final MembershipRepository membershipRepository;
  private final DocumentInfoRepository documentInfoRepository;
  private final AimServiceRegistry aimServiceRegistry;
  private final BpmsRepositoryRegistry bpmsRepositoryRegistry;

  public GenerateLoanApplicationTask(DocumentRepository documentRepsitory, AuthenticationService authenticationService,
      AuthorizationService authorizationService, DocumentService documentService, MembershipRepository membershipRepository,
      DocumentInfoRepository documentInfoRepository, AimServiceRegistry aimServiceRegistry, BpmsRepositoryRegistry bpmsRepositoryRegistry)
  {
    this.documentRepsitory = documentRepsitory;
    this.authenticationService = authenticationService;
    this.authorizationService = authorizationService;
    this.documentService = documentService;
    this.membershipRepository = membershipRepository;
    this.documentInfoRepository = documentInfoRepository;
    this.aimServiceRegistry = aimServiceRegistry;
    this.bpmsRepositoryRegistry = bpmsRepositoryRegistry;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    boolean isInstantLoan = false;
    if (getValidString(execution.getVariable(PROCESS_TYPE_ID)).equals(INSTANT_LOAN_PROCESS_TYPE_ID))
    {
      isInstantLoan = true;
    }
    String registerNumber = (String) execution.getVariable(REGISTER_NUMBER);
    String processRequestId = (String) execution.getVariable(PROCESS_REQUEST_ID);
    String processTypeId = String.valueOf(execution.getVariable(PROCESS_TYPE_ID));
    String trackNumber = String.valueOf(execution.getVariable(TRACK_NUMBER));

    String instanceId;
    String documentPath;
    String templatePath;
    if (processTypeId.equals(BNPL_PROCESS_TYPE_ID))
    {
      instanceId = String.valueOf(execution.getVariable(PROCESS_INSTANCE_ID));
      documentPath = getDocumentPath(registerNumber, processRequestId, LOAN_APPLICATION_NAME_BNPL);
      templatePath = TEMPLATE_PATH_BNPL_LOAN_APPLICATION;
    }
    else
    {
      instanceId = !isInstantLoan ?  String.valueOf(execution.getVariable(CASE_INSTANCE_ID)) : String.valueOf(execution.getVariable(PROCESS_INSTANCE_ID));
      documentPath = getDocumentPath(registerNumber, processRequestId, LOAN_APPLICATION_NAME);
      templatePath = TEMPLATE_PATH_LOAN_APPLICATION;
    }

    List<RestTextValueField> restTextValueFields = getTextFields(execution, processTypeId, isInstantLoan);

    List<String> documentIdList = transformDocument(templatePath, documentPath, restTextValueFields);
    execution.setVariable("loanApplicationDocList", documentIdList);
    if (!documentIdList.isEmpty())
    {
      removePreviousLoanApplication(instanceId, processRequestId);
    }

    List<String> returnValue = getPdfBase64(documentIdList);
    if (null == returnValue || returnValue.isEmpty() || returnValue.size() != 2)
    {
      throw new BpmServiceException("File not found when upload loan application file to LDMS");
    }

    persistLoanApplication(returnValue.get(0), instanceId, processRequestId, trackNumber, processTypeId);

    if (processTypeId.equals(ONLINE_SALARY_PROCESS_TYPE))
    {
      sendFileToLDMS(execution, instanceId, returnValue.get(1), processTypeId, trackNumber);
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

  private void removePreviousLoanApplication(String instanceId, String processRequestId)
  {
    LOGGER.info("############ Removes previous loan application with REQUEST ID = [{}]", processRequestId);
    documentRepsitory.removeBy(instanceId, CATEGORY_LOAN_APPLICATION, SUB_CATEGORY_LOAN_APPLICATION, LOAN_APPLICATION_NAME);
  }

  private void persistLoanApplication(String documentId, String instanceId, String processRequestId, String trackNumber, String processTypeId) throws BpmRepositoryException
  {
    if (null != documentId)
    {
      if (processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
      {
        LOGGER.info("########## Persist loan application with REQUEST ID = [{}], WITH TRACKNUMBER = [{}]", processRequestId, trackNumber);
        documentRepsitory.create(documentId, APPLICATION_INFO_ID_LOAN, instanceId, LOAN_APPLICATION_NAME, CATEGORY_LOAN_APPLICATION,
            SUB_CATEGORY_LOAN_APPLICATION, documentId, ALFRESCO);
      }
      else
      {
        LOGGER.info("########## Persist loan application with REQUEST ID = [{}]", processRequestId);
        documentRepsitory.create(documentId, APPLICATION_INFO_ID_LOAN, instanceId, LOAN_APPLICATION_NAME, CATEGORY_LOAN_APPLICATION,
            SUB_CATEGORY_LOAN_APPLICATION, documentId, ALFRESCO);
      }
    }

    if (processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
    {
      LOGGER.info("########## Successful generated loan application with REQUEST ID = [{}], WITH TRACKNUMBER = [{}]", processRequestId, trackNumber);
    }
    else
    {
      LOGGER.info("########## Successful generated loan application with REQUEST ID = [{}]", processRequestId);
    }
  }

  private String getDocumentPath(String registerNumber, String processRequestId, String docName)
  {
    long timeStamp = new Date().getTime();
    String timeStampStr = Long.toString(timeStamp);

    return BpmModuleConstants.CUSTOMERS_FOLDER + SLASH + registerNumber + SLASH + processRequestId + SLASH + docName + "_" + timeStampStr;
  }

  private List<RestTextValueField> getTextFields(DelegateExecution execution, String processTypeId, boolean isInstantLoan)
      throws BpmServiceException, UseCaseException
  {
    List<RestTextValueField> textFields = new ArrayList<>();

    Map<String, Object> variables = execution.getVariables();

    String registerNumber = (String) variables.get(REGISTER_NUMBER);
    String fullName = (String) execution.getVariable(FULL_NAME);
    String[] s = fullName.split(" ");
    String firstName = s[0];
    String lastName = s.length > 1 ? s[1] : "";

    String sex = (String) execution.getVariable(GENDER);
    sex = sex.equals("M") ? "Эрэгтэй" : "Эмэгтэй";
    String address = BpmUtils.getValidString(execution.getVariable(ADDRESS));

    Integer dayOfPayment = null;
    BigDecimal interestRate;
    String loanAmountStr;
    String loanPurpose = (String) execution.getVariable(LOAN_PURPOSE);
    String loanTerm = String.valueOf(execution.getVariable(TERM));
    if (processTypeId.equals(ONLINE_SALARY_PROCESS_TYPE))
    {
      dayOfPayment = Integer.parseInt(String.valueOf(execution.getVariable(DAY_OF_PAYMENT)));
    }
    if (processTypeId.equals(ONLINE_SALARY_PROCESS_TYPE)  || isInstantLoan || processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
    {
      interestRate = toBigDecimal(String.valueOf(execution.getVariable(INTEREST_RATE)));
      loanAmountStr = String.valueOf(execution.getVariable(FIXED_ACCEPTED_LOAN_AMOUNT_STRING));

      String dayOfPaymentString = " ";
      if (isInstantLoan)
      {
        dayOfPaymentString = String.valueOf(LocalDate.now().plusDays((int) execution.getVariable("days")));
      }
      else if (processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID)) {
        dayOfPaymentString = " ";
      }
      else {
        dayOfPaymentString = String.valueOf(dayOfPayment);
      }

      if (isInstantLoan)
      {
        String actionType = getValidString(execution.getVariable(ACTION_TYPE));
        boolean hasActiveLoanAccount = (boolean) execution.getVariable(HAS_ACTIVE_LOAN_ACCOUNT);
        BigDecimal fixedAcceptedLoanAmount = new BigDecimal(getValidString(execution.getVariable(FIXED_ACCEPTED_LOAN_AMOUNT)));
        if(!StringUtils.isBlank(actionType) && hasActiveLoanAccount){
          BigDecimal clearBalance = new BigDecimal(getValidString(execution.getVariable("clearBalance")));
          if ((actionType.equals(TOPUP)))
          {
            loanAmountStr = bigDecimalToString(clearBalance.add(fixedAcceptedLoanAmount));
          }
          if ((actionType.equals(EXTEND)))
          {
            loanAmountStr = bigDecimalToString(clearBalance);
          }
          execution.setVariable(FIXED_ACCEPTED_LOAN_AMOUNT_STRING, loanAmountStr);
          execution.setVariable(FIXED_ACCEPTED_LOAN_AMOUNT, removeCommaAndGetBigDecimal(loanAmountStr));
          String processRequestId = (String) execution.getVariable(PROCESS_REQUEST_ID);
          String instanceId = String.valueOf(execution.getVariable(PROCESS_INSTANCE_ID));
          updateParameter(processRequestId, instanceId, FIXED_ACCEPTED_LOAN_AMOUNT_STRING, loanAmountStr);
        }
        execution.setVariable(OLD_FIXED_ACCEPTED_LOAN_AMOUNT, fixedAcceptedLoanAmount.toString());
      }
      textFields.add(new RestTextValueField(LOAN_TERM_FIELD, isInstantLoan ? execution.getVariable("days") + " өдөр" : loanTerm + " сар"));
      textFields.add(new RestTextValueField(DAY_OF_PAYMENT_FIELD, dayOfPaymentString));
    }
    else
    {
      interestRate = toBigDecimal("0");
      loanAmountStr = String.valueOf(execution.getVariable(INVOICE_AMOUNT_75));
      textFields.add(new RestTextValueField(LOAN_TERM_FIELD, loanTerm));
    }
    textFields.add(new RestTextValueField(REGISTER_NUMBER_FIELD, registerNumber));
    textFields.add(new RestTextValueField(FIRST_NAME_FIELD, firstName));
    textFields.add(new RestTextValueField(LAST_NAME_FIELD, lastName));
    textFields.add(new RestTextValueField(SEX_FIELD, sex));
    textFields.add(new RestTextValueField(ADDRESS_FIELD, getSpaceString(address)));

    textFields.add(new RestTextValueField(INTEREST_RATE_FIELD, String.valueOf(interestRate)));
    textFields.add(new RestTextValueField(REQUESTED_LOAN_AMOUNT_FIELD, loanAmountStr));
    textFields.add(new RestTextValueField(LOAN_PURPOSE_FIELD, loanPurpose));

    return textFields;
  }

  private void sendFileToLDMS(DelegateExecution execution, String caseInstanceId, String base64, String processTypeId, String trackNumber) throws UseCaseException
  {
    String processRequestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    String categoryLoanApplication = String.valueOf(execution.getVariable("categoryLoanApplication"));
    String subCategoryLoanApplication = String.valueOf(execution.getVariable("subCategoryLoanApplication"));
    List<UploadFile> uploadFiles = new ArrayList<>();

    uploadFiles.add(new UploadFile(LOAN_APPLICATION_NAME_PDF, base64));
    UploadDocumentsInput input = new UploadDocumentsInput(caseInstanceId, categoryLoanApplication, subCategoryLoanApplication, SOURCE_LDMS,
        Collections.emptyMap(), uploadFiles);
    UploadDocuments useCase = new UploadDocuments(authenticationService, authorizationService, documentService, membershipRepository, documentInfoRepository,
        documentRepsitory);
    Boolean isUploadedFile = useCase.execute(input);
    if (Boolean.TRUE.equals(isUploadedFile))
    {
      if (processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
      {
        LOGGER.info("######## SENT LOAN APPLICATION FILE TO LDMS WITH REQUEST ID [{}], WITH TRACKNUMBER =[{}]", processRequestId, trackNumber);
      }
      else
      {
        LOGGER.info("######## SENT LOAN APPLICATION FILE TO LDMS WITH REQUEST ID [{}]", processRequestId);
      }
    }
  }
  private static String getSpaceString(Object value)
  {
    if (null == value || NULL_STRING.equalsIgnoreCase(String.valueOf(value)) || value.toString().isEmpty())
    {
      return " ";
    }
    return String.valueOf(value);
  }

  private void updateParameter(String requestId, String processInstanceId, String valueName, String value) throws UseCaseException
  {
    Map<String, Serializable> parameters = new HashMap<>();
    parameters.put(valueName, value);

    updateRequestParameters(aimServiceRegistry, bpmsRepositoryRegistry.getProcessRequestRepository(), requestId, parameters);

    Map<ParameterEntityType, Map<String, Serializable>> processParams = new HashMap<>();
    processParams.put(ParameterEntityType.INSTANT_LOAN, parameters);

    updateProcessParameters(aimServiceRegistry, bpmsRepositoryRegistry.getProcessRepository(), processInstanceId, processParams);
  }
}