package mn.erin.bpms.loan.consumption.service_task.mongol_bank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.camunda.bpm.engine.CaseService;
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
import mn.erin.bpms.loan.consumption.utils.MongolBankServiceUtils;
import mn.erin.bpms.process.base.ProcessTaskException;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.membership.GetMembershipOutput;
import mn.erin.domain.aim.usecase.membership.GetUserMembership;
import mn.erin.domain.aim.usecase.membership.GetUserMembershipsInput;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.model.loan.Loan;
import mn.erin.domain.bpm.model.loan.LoanClass;
import mn.erin.domain.bpm.model.loan.LoanEnquire;
import mn.erin.domain.bpm.model.loan.LoanEnquireId;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.repository.DocumentRepository;
import mn.erin.domain.bpm.service.LoanService;
import mn.erin.domain.bpm.usecase.loan.ConfirmLoanEnquire;
import mn.erin.domain.bpm.usecase.loan.ConfirmLoanEnquireInput;
import mn.erin.domain.bpm.usecase.loan.GetCustomerDetail;
import mn.erin.domain.bpm.usecase.loan.GetCustomerLoanCid;
import mn.erin.domain.bpm.usecase.loan.GetCustomerLoanCidInput;
import mn.erin.domain.bpm.usecase.loan.GetCustomerRelatedInfo;
import mn.erin.domain.bpm.usecase.loan.GetCustomerRelatedInfoInput;
import mn.erin.domain.bpm.usecase.loan.GetLoanEnquire;
import mn.erin.domain.bpm.usecase.loan.GetLoanEnquireInput;
import mn.erin.domain.bpm.usecase.loan.GetLoanEnquireWithFile;
import mn.erin.domain.bpm.usecase.loan.GetLoanEnquireWithFileInput;
import mn.erin.domain.bpm.usecase.loan.GetLoanInfo;
import mn.erin.domain.bpm.usecase.loan.GetLoanInfoInput;
import mn.erin.domain.bpm.usecase.loan.GetLoanInfoOutput;
import mn.erin.domain.bpm.usecase.process.UpdateRequestState;
import mn.erin.domain.bpm.usecase.process.UpdateRequestStateInput;
import mn.erin.domain.bpm.usecase.process.UpdateRequestStateOutput;

import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.BORROWER_ID;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.CUSTOMER_CID_BY_CO_BORROWER;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.ENQUIRE_ID;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.FOREIGN_CITIZEN_SEARCH_TYPE;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.FOUNDATION_SEARCH_TYPE;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.GOVERNMENT_ORGANIZATION_SEARCH_TYPE;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.IS_CONFIRMED_LOAN_ENQUIRE;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.LEGAL_STATUS_FOUNDATION;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.LEGAL_STATUS_GOVERNMENT_AGENCY;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.LEGAL_STATUS_MONASTERY;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.LEGAL_STATUS_ORGANIZATION;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.LOAN_CLASS_NAME;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.LOAN_CLASS_RANK;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.LOAN_ENQUIRE_PDF;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.LOAN_NOT_FOUND;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.MONASTERY_SEARCH_TYPE;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.NORMAL;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.ORGANIZATION_SEARCH_TYPE;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.REGISTER_SEARCH_VALUE_TYPE;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.SEARCH_TYPE_MONGOLIAN_CITIZEN;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.VALUE_FOREIGN_CITIZEN;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.VALUE_MONGOLIAN_CITIZEN;
import static mn.erin.bpms.loan.consumption.utils.MongolBankServiceUtils.getLowestLoanClass;
import static mn.erin.bpms.loan.consumption.utils.StringUtils.getStringValue;
import static mn.erin.bpms.loan.consumption.utils.StringUtils.toWholeNumber;
import static mn.erin.domain.bpm.BpmMessagesConstants.ONLINE_SALARY_LOG_HASH;
import static mn.erin.domain.bpm.BpmModuleConstants.ALFRESCO;
import static mn.erin.domain.bpm.BpmModuleConstants.BRANCH_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CATEGORY_ENQUIRE;
import static mn.erin.domain.bpm.BpmModuleConstants.CUSTOMER_CID;
import static mn.erin.domain.bpm.BpmModuleConstants.DOCUMENT_NAME_MONGOL_BANK_ENQUIRE;
import static mn.erin.domain.bpm.BpmModuleConstants.DOWNLOAD_MONGOL_BANK;
import static mn.erin.domain.bpm.BpmModuleConstants.LEGAL_STATUS;
import static mn.erin.domain.bpm.BpmModuleConstants.MB_HAS_SESSION;
import static mn.erin.domain.bpm.BpmModuleConstants.MICRO_MONGOL_BANK_LEGAL_STATE;
import static mn.erin.domain.bpm.BpmModuleConstants.MONGOL_BANK_CUSTOMER_RELATED_INFO_DOCUMENT_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.MONGOL_BANK_EMPLOYEES_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.MONGOL_BANK_ESTABLISHED_DATE;
import static mn.erin.domain.bpm.BpmModuleConstants.MONGOL_BANK_EXECUTIVE_FIRST_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.MONGOL_BANK_EXECUTIVE_LAST_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.MONGOL_BANK_EXECUTIVE_REG_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.MONGOL_BANK_LAW_STATUS;
import static mn.erin.domain.bpm.BpmModuleConstants.MONGOL_BANK_NUMBER_OF_SHAREHOLDERS;
import static mn.erin.domain.bpm.BpmModuleConstants.MONGOL_BANK_OFFICIAL_ADDRESS;
import static mn.erin.domain.bpm.BpmModuleConstants.MONGOL_BANK_ORGANIZATION_FITCH;
import static mn.erin.domain.bpm.BpmModuleConstants.MONGOL_BANK_ORGANIZATION_MOODY;
import static mn.erin.domain.bpm.BpmModuleConstants.MONGOL_BANK_ORGANIZATION_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.MONGOL_BANK_ORGANIZATION_SP;
import static mn.erin.domain.bpm.BpmModuleConstants.MONGOL_BANK_ORGANIZATION_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.MONGOL_BANK_REG_IN_MONGOLIA;
import static mn.erin.domain.bpm.BpmModuleConstants.MONGOL_BANK_REG_NUM_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.MONGOL_BANK_SHAREHOLDER_RELATED_INFO_DOCUMENT_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.MONGOL_BANK_STATE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.NO_MN_VALUE;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.SLASH;
import static mn.erin.domain.bpm.BpmModuleConstants.SUB_CATEGORY_MONGOL_BANK;
import static mn.erin.domain.bpm.BpmModuleConstants.TEMPLATE_PATH_MONGOL_BANK_CUSTOMER_RELATED_INFO;
import static mn.erin.domain.bpm.BpmModuleConstants.TEMPLATE_PATH_MONGOL_BANK_SHAREHOLDER_RELATED_INFO;
import static mn.erin.domain.bpm.BpmModuleConstants.YES_MN_VALUE;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.CONNECTING_FORM_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.FIELD6;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.FIRST_NAME_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.LAST_NAME_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.LEGAL_STATE_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.REGISTER_NUMBER_FIELD;

/**
 * @author Lkhagvadorj.A
 **/

public class DownloadLoanInfoFromMongolBankTaskExtended implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DownloadLoanInfoFromMongolBankTaskExtended.class);

  private final LoanService loanService;
  private final AuthenticationService authenticationService;

  private final AuthorizationService authorizationService;
  private final MembershipRepository membershipRepository;
  private final DocumentRepository documentRepository;
  private final BpmsRepositoryRegistry bpmsRepositoryRegistry;

  public DownloadLoanInfoFromMongolBankTaskExtended(LoanService loanService, AuthenticationService authenticationService,
      AuthorizationService authorizationService, MembershipRepository membershipRepository, DocumentRepository documentRepository,
      BpmsRepositoryRegistry bpmsRepositoryRegistry)
  {
    this.loanService = Objects.requireNonNull(loanService, "Loan service is required!");
    this.authenticationService = Objects.requireNonNull(authenticationService, "Authentication service is required!");

    this.authorizationService = Objects.requireNonNull(authorizationService, "Authorization service is required!");
    this.membershipRepository = Objects.requireNonNull(membershipRepository, "Membership repository is required!");
    this.documentRepository = documentRepository;
    this.bpmsRepositoryRegistry = bpmsRepositoryRegistry;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    if (null != execution.getVariable(DOWNLOAD_MONGOL_BANK) && (boolean) execution.getVariable(DOWNLOAD_MONGOL_BANK))
    {
      String caseInstanceId = (String) execution.getVariable(CASE_INSTANCE_ID);
      LOGGER.info("############ Starting MONGOL BANK services ... ");

      try
      {
        // step 1 - Downloads CID number.
        downloadCustomerCID(execution);

        // step 2 - Downloads loan enquire.
        LoanEnquire loanEnquire = downloadLoanEnquire(execution);

        LoanEnquireId id = loanEnquire.getId();

        String loanEnquireId = id.getId();

        if (loanEnquireId.equalsIgnoreCase(LOAN_NOT_FOUND))
        {
          // checks customer has loan by co-borrower.
          downloadLoanEnquireByCoBorrower(execution);

          downloadLoanEnquirePDF(execution);
        }
        else
        {
          // step 3 - confirms loan enquire.
          confirmLoanEnquire(execution);

          // step 4 - Downloads customer loan infos.
          downloadCustomerLoanInfo(execution);

          // step 5 - Downloads customer loan enquire by co-borrower.
          downloadLoanEnquireByCoBorrower(execution);

          // step 6 - downloads loan enquire PDF file.
          downloadLoanEnquirePDF(execution);
        }

        if(null == execution.getVariable("borrowerType") || execution.getVariable("borrowerType").equals("Иргэн"))
        {
          downloadCustomerDetailInfo(execution);
        }

        if(null != execution.getVariable("borrowerType") && execution.getVariable("borrowerType").equals("Аж ахуй нэгж"))
        {
          downloadOrganizationDetailInfo(execution);
        }

      }
      catch (UseCaseException e)
      {
        if (e.getCode().equals("BPMS059"))
        {
          LOGGER.error(ONLINE_SALARY_LOG_HASH + "{}", e.getMessage());
          updateRequestState(execution);
          execution.setVariable(MB_HAS_SESSION, false);
          return;
        }
        throw new ProcessTaskException(e.getCode(), e.getMessage());
      }

      if (!execution.hasVariable(LOAN_CLASS_NAME))
      {
        execution.setVariable(LOAN_CLASS_NAME, NORMAL);
      }

      LOGGER.info("############ Successful completed MONGOL Bank services.");

      LOGGER.info("############ Removes LEGAL_STATUS variable from execution and case variables.");
      execution.removeVariable(LEGAL_STATUS);
      CaseService caseService = execution.getProcessEngine().getCaseService();
      caseService.removeVariable(caseInstanceId, LEGAL_STATUS);
    }
  }

  private void downloadCustomerCID(DelegateExecution execution) throws ProcessTaskException, UseCaseException
  {
    Map<String, Object> executionVariables = execution.getVariables();

    String registerNumber = (String) executionVariables.get(REGISTER_NUMBER);
    String branchNumber = (String) executionVariables.get(BRANCH_NUMBER);

    LOGGER.info("########### Downloads customer CID from MONGOL BANK 1st service with REG_NUMBER = {}...", registerNumber);

    String searchType = null;
    String legalStatus = (String) executionVariables.get(LEGAL_STATUS);

    // GETS SEARCH TYPE DEPENDING ON LEGAL STATUS.
    if (legalStatus.equalsIgnoreCase(VALUE_MONGOLIAN_CITIZEN))
    {
      searchType = SEARCH_TYPE_MONGOLIAN_CITIZEN;
    }
    else if (legalStatus.equalsIgnoreCase(VALUE_FOREIGN_CITIZEN))
    {
      searchType = FOREIGN_CITIZEN_SEARCH_TYPE;
    }
    else if (legalStatus.equalsIgnoreCase(LEGAL_STATUS_ORGANIZATION))
    {
      searchType = ORGANIZATION_SEARCH_TYPE;
    }
    else if (legalStatus.equalsIgnoreCase(LEGAL_STATUS_GOVERNMENT_AGENCY))
    {
      searchType = GOVERNMENT_ORGANIZATION_SEARCH_TYPE;
    }
    else if (legalStatus.equalsIgnoreCase(LEGAL_STATUS_MONASTERY))
    {
      searchType = MONASTERY_SEARCH_TYPE;
    }
    else if (legalStatus.equalsIgnoreCase(LEGAL_STATUS_FOUNDATION))
    {
      searchType = FOUNDATION_SEARCH_TYPE;
    }

    String currentUserId = authenticationService.getCurrentUserId();

    if (null == branchNumber)
    {
      branchNumber = getGroupId(currentUserId);
    }

    String customerCID = getCustomerCID(registerNumber, searchType,
        false, branchNumber, currentUserId, currentUserId);

    String customerCIDByCoBorrower = getCustomerCID(registerNumber, searchType,
        true, branchNumber, currentUserId, currentUserId);

    if (null == customerCID)
    {
      String errorCode = "BPMS061";
      throw new ProcessTaskException(errorCode, "MONGOL BANK : Customer CID is null!");
    }

    execution.setVariable(CUSTOMER_CID, customerCID);
    execution.setVariable(CUSTOMER_CID_BY_CO_BORROWER, customerCIDByCoBorrower);
    String processTypeId = String.valueOf(execution.getVariable("processTypeId"));
    if (processTypeId.equals("onlineSalary"))
    {
      execution.setVariable(MB_HAS_SESSION, true);
    }

    LOGGER.info("############# Successful downloaded customer CID from MONGOL BANK 1st service with REG_NUMBER ={}", registerNumber);
  }

  private void downloadCustomerDetailInfo(DelegateExecution execution) throws UseCaseException, TransformServiceException, BpmRepositoryException
  {
    String customerCid = String.valueOf(execution.getVariable(CUSTOMER_CID));
    GetCustomerRelatedInfoInput input = new GetCustomerRelatedInfoInput(customerCid, null);
    GetCustomerDetail getCustomerDetail = new GetCustomerDetail(loanService);
    Map<String, String> customerDetailMap = getCustomerDetail.execute(input);

    //TODO: uncomment this later, commented as requested from XAC depending on MB service works wrong sometimes

//    if (null != customerDetailMap && !customerDetailMap.isEmpty())
//    {
//      execution.setVariable(MONGOL_BANK_FIRST_NAME, getStringValue(customerDetailMap.get("field1")));
//      execution.setVariable(MONGOL_BANK_LAST_NAME, getStringValue(customerDetailMap.get("field2")));
//      execution.setVariable(MONGOL_BANK_LEGAL_STATE, getStringValue(customerDetailMap.get("field3")));
//      execution.setVariable(MONGOL_BANK_REGISTER_NUMBER, getStringValue(customerDetailMap.get("field4")));
//      execution.setVariable(MONGOL_BANK_ID_NUMBER, getStringValue(customerDetailMap.get("field5")));
//      execution.setVariable(MONGOL_BANK_DATE_OF_BIRTH, getStringValue(customerDetailMap.get("field6")));
//      execution.setVariable(MONGOL_BANK_ADDRESS, getStringValue(customerDetailMap.get("field7")));
//      execution.setVariable(MONGOL_BANK_OCCUPANCY, getStringValue(customerDetailMap.get("field8")));
//      execution.setVariable(MONGOL_BANK_PROFESSION, getStringValue(customerDetailMap.get("field9")));
//      execution.setVariable(MONGOL_BANK_FAMILY_NAME, getStringValue(customerDetailMap.get("field10")));
//      execution.setVariable(MONGOL_BANK_FAMILY_LAST_NAME, getStringValue(customerDetailMap.get("field11")));
//      execution.setVariable(MONGOL_BANK_FAMILY_REGISTER_NUMBER, getStringValue(customerDetailMap.get("field12")));
//      execution.setVariable(MONGOL_BANK_NUMBER_OF_FAMILY_MEMBER, getStringValue(customerDetailMap.get("field13")));
//      execution.setVariable(MONGOL_BANK_JOBLESS_NUMBER_OF_FAMILY_MEMBER, getStringValue(customerDetailMap.get("field14")));
//    }
    downloadCustomerRelatedInfo(execution);
  }

    private void downloadOrganizationDetailInfo(DelegateExecution execution) throws UseCaseException, TransformServiceException, BpmRepositoryException
    {
        String customerCid = String.valueOf(execution.getVariable(CUSTOMER_CID));
        GetCustomerRelatedInfoInput input = new GetCustomerRelatedInfoInput(customerCid, null);
        GetCustomerDetail getCustomerDetail = new GetCustomerDetail(loanService);
        Map<String, String> customerDetailMap = getCustomerDetail.execute(input);

        if (null != customerDetailMap && !customerDetailMap.isEmpty())
        {
            execution.setVariable(MONGOL_BANK_LAW_STATUS, getStringValue(customerDetailMap.get("field1")));
            execution.setVariable(MONGOL_BANK_ORGANIZATION_NAME, getStringValue(customerDetailMap.get("field2")));
            execution.setVariable(MONGOL_BANK_ORGANIZATION_FITCH, getStringValue(customerDetailMap.get("field3")));
            execution.setVariable(MONGOL_BANK_ORGANIZATION_SP, getStringValue(customerDetailMap.get("field4")));
            execution.setVariable(MONGOL_BANK_ORGANIZATION_MOODY, getStringValue(customerDetailMap.get("field5")));
            execution.setVariable(MONGOL_BANK_REG_IN_MONGOLIA, getStringValue(customerDetailMap.get("field6")));
            execution.setVariable(MONGOL_BANK_ORGANIZATION_TYPE, getStringValue(customerDetailMap.get("field7")));
            execution.setVariable(MONGOL_BANK_REG_NUM_ID, getStringValue(customerDetailMap.get("field8")));
            execution.setVariable(MONGOL_BANK_STATE_NUMBER, getStringValue(customerDetailMap.get("field9")));
            execution.setVariable(MONGOL_BANK_ESTABLISHED_DATE, getStringValue(customerDetailMap.get("field10")));
            execution.setVariable(MONGOL_BANK_OFFICIAL_ADDRESS, getStringValue(customerDetailMap.get("field11")));
            execution.setVariable(MONGOL_BANK_EMPLOYEES_NUMBER, getStringValue(customerDetailMap.get("field12")));
            execution.setVariable(MONGOL_BANK_EXECUTIVE_FIRST_NAME, getStringValue(customerDetailMap.get("field13")));
            execution.setVariable(MONGOL_BANK_EXECUTIVE_LAST_NAME, getStringValue(customerDetailMap.get("field14")));
            execution.setVariable(MICRO_MONGOL_BANK_LEGAL_STATE, getStringValue(customerDetailMap.get("field15")));
            execution.setVariable(MONGOL_BANK_EXECUTIVE_REG_NUMBER, getStringValue(customerDetailMap.get("field16")));
            execution.setVariable(MONGOL_BANK_NUMBER_OF_SHAREHOLDERS, getStringValue(customerDetailMap.get("field17")));
        }
      downloadShareholderRelatedInfo(execution);
    }

  private void downloadCustomerRelatedInfo(DelegateExecution execution) throws UseCaseException, TransformServiceException, BpmRepositoryException
  {
    String customerCid = String.valueOf(execution.getVariable(CUSTOMER_CID));
    GetCustomerRelatedInfoInput input = new GetCustomerRelatedInfoInput(customerCid, "citizenrelationinfo");
    GetCustomerRelatedInfo getCustomerRelatedInfo = new GetCustomerRelatedInfo(loanService);
    Map<String, String> customerInfoMap = getCustomerRelatedInfo.execute(input);

    if (null != customerInfoMap && !customerInfoMap.isEmpty())
    {
      List<RestTextValueField> textFields = new ArrayList<>();
      String field3 = getStringValue(customerInfoMap.get("field3"));
      field3 = field3.equals("1.0") ? YES_MN_VALUE : NO_MN_VALUE;
      String field6 = getStringValue(customerInfoMap.get("field6"));
      field6 = field6.equals("1.0") ? YES_MN_VALUE : NO_MN_VALUE;

      textFields.add(new RestTextValueField(FIRST_NAME_FIELD, getStringValue(customerInfoMap.get("field1"))));
      textFields.add(new RestTextValueField(LAST_NAME_FIELD, getStringValue(customerInfoMap.get("field2"))));
      textFields.add(new RestTextValueField(LEGAL_STATE_FIELD, field3));
      textFields.add(new RestTextValueField(REGISTER_NUMBER_FIELD, getStringValue(customerInfoMap.get("field4"))));
      textFields.add(new RestTextValueField(CONNECTING_FORM_FIELD, getStringValue(customerInfoMap.get("field5"))));
      textFields.add(new RestTextValueField(FIELD6, field6));

      String registerNumber = (String) execution.getVariable(REGISTER_NUMBER);
      String processRequestId = (String) execution.getVariable(PROCESS_REQUEST_ID);
      String documentPath = getDocumentPath(registerNumber, processRequestId, MONGOL_BANK_CUSTOMER_RELATED_INFO_DOCUMENT_NAME);
      String caseInstanceId = (String) execution.getVariable(CASE_INSTANCE_ID);
      List<String> documentIdList = transformDocument(documentPath, textFields, TEMPLATE_PATH_MONGOL_BANK_CUSTOMER_RELATED_INFO);

      if (!documentIdList.isEmpty())
      {
        removePreviousDocument(caseInstanceId, processRequestId, MONGOL_BANK_CUSTOMER_RELATED_INFO_DOCUMENT_NAME);
      }
      for (String documentId : documentIdList)
      {
        persistDocument(documentId, caseInstanceId, processRequestId, MONGOL_BANK_CUSTOMER_RELATED_INFO_DOCUMENT_NAME);
      }
    }
  }

  private void downloadShareholderRelatedInfo(DelegateExecution execution) throws UseCaseException, TransformServiceException, BpmRepositoryException
  {
    String customerCid = String.valueOf(execution.getVariable(CUSTOMER_CID));
    GetCustomerRelatedInfoInput input = new GetCustomerRelatedInfoInput(customerCid, "orgrelationinfo");
    GetCustomerRelatedInfo getCustomerRelatedInfo = new GetCustomerRelatedInfo(loanService);
    Map<String, String> customerInfoMap = getCustomerRelatedInfo.execute(input);

    if (null != customerInfoMap && !customerInfoMap.isEmpty())
    {
      List<RestTextValueField> textFields = new ArrayList<>();
      String field3 = getStringValue(customerInfoMap.get("field3"));
      field3 = field3.equals("1.0") ? YES_MN_VALUE : NO_MN_VALUE;

      textFields.add(new RestTextValueField(FIRST_NAME_FIELD, getStringValue(customerInfoMap.get("field1"))));
      textFields.add(new RestTextValueField(LAST_NAME_FIELD, getStringValue(customerInfoMap.get("field2"))));
      textFields.add(new RestTextValueField(LEGAL_STATE_FIELD, field3));
      textFields.add(new RestTextValueField(REGISTER_NUMBER_FIELD, toWholeNumber(getStringValue(customerInfoMap.get("field4")))));

      String registerNumber = (String) execution.getVariable(REGISTER_NUMBER);
      String processRequestId = (String) execution.getVariable(PROCESS_REQUEST_ID);
      String documentPath = getDocumentPath(registerNumber, processRequestId, MONGOL_BANK_SHAREHOLDER_RELATED_INFO_DOCUMENT_NAME);
      String caseInstanceId = (String) execution.getVariable(CASE_INSTANCE_ID);
      List<String> documentIdList = transformDocument(documentPath, textFields, TEMPLATE_PATH_MONGOL_BANK_SHAREHOLDER_RELATED_INFO);

      if (!documentIdList.isEmpty())
      {
        removePreviousDocument(caseInstanceId, processRequestId, MONGOL_BANK_SHAREHOLDER_RELATED_INFO_DOCUMENT_NAME);
      }
      for (String documentId : documentIdList)
      {
        persistDocument(documentId, caseInstanceId, processRequestId, MONGOL_BANK_SHAREHOLDER_RELATED_INFO_DOCUMENT_NAME);
      }
    }
  }

  private String getDocumentPath(String registerNumber, String processRequestId, String documentName)
  {
    long timeStamp = new Date().getTime();
    String timeStampStr = Long.toString(timeStamp);

    return BpmModuleConstants.CUSTOMERS_FOLDER + SLASH + registerNumber + SLASH + processRequestId + SLASH + documentName + "_" + timeStampStr;
  }

  private List<String> transformDocument(String documentPath, List<RestTextValueField> restTextValueFields, String templatePathName) throws TransformServiceException
  {
    TransformerService transformerService = new AlfrescoRemoteTransformerService();

    Map<String, String> queryParams = new HashMap<>();
    queryParams.put("deleteBaseDocument", "false");
    RestTransformDocumentInput input = new RestTransformDocumentInput(templatePathName, documentPath);
    input.setTextValueField(restTextValueFields);
    input.setDynamicTables(new ArrayList<>());

    RestTransformDocumentOutput output = transformerService.transform(input, queryParams);

    return Arrays.asList(output.getBaseDocumentId(), output.getDocumentId());
  }

  private void removePreviousDocument(String caseInstanceId, String processRequestId, String documentName)
  {
    LOGGER.info("############ Removes previous Customer related info document with REQUEST ID = [{}]", processRequestId);

    documentRepository.removeBy(caseInstanceId, CATEGORY_ENQUIRE, SUB_CATEGORY_MONGOL_BANK, documentName);
  }

  private void persistDocument(String documentId, String caseInstanceId, String processRequestId, String documentName) throws BpmRepositoryException
  {
    if (null != documentId)
    {
      LOGGER.info("############ Persists Customer related info document with REQUEST ID = [{}]", processRequestId);
      documentRepository.create(documentId, DOCUMENT_NAME_MONGOL_BANK_ENQUIRE, caseInstanceId, documentName,
          CATEGORY_ENQUIRE, SUB_CATEGORY_MONGOL_BANK, documentId, ALFRESCO);
    }

    LOGGER.info("########## Successful generated MORTGAGE loan decision with REQUEST ID = [{}]", processRequestId);
  }

  private LoanEnquire downloadLoanEnquire(DelegateExecution execution) throws ProcessTaskException, UseCaseException
  {
    String registerNumber = (String) execution.getVariable(REGISTER_NUMBER);

    LOGGER.info("############# Downloading loan enquire from MONGOL BANK 2nd service with REG_NUMBER ={} ...", registerNumber);

    String customerCID = (String) execution.getVariable(CUSTOMER_CID);

    GetLoanEnquireInput input = new GetLoanEnquireInput(customerCID, false);
    GetLoanEnquire getLoanEnquire = new GetLoanEnquire(loanService);

    LoanEnquire loanEnquire = getLoanEnquire.execute(input);

    if (null == loanEnquire)
    {
      String errorCode = "BPMS066";
      throw new ProcessTaskException(errorCode, "Customer loan enquire is null! with REG_NUMBER =" + registerNumber);
    }

    String borrowerId = loanEnquire.getBorrowerId().getId();
    String enquireId = loanEnquire.getId().getId();

    execution.setVariable(ENQUIRE_ID, enquireId);
    execution.setVariable(BORROWER_ID, borrowerId);

    LOGGER.info("############# Successful downloaded loan enquire from MONGOL BANK 2nd service.");

    return loanEnquire;
  }

  private void confirmLoanEnquire(DelegateExecution execution) throws UseCaseException, ProcessTaskException
  {
    String registerNumber = (String) execution.getVariable(REGISTER_NUMBER);

    LOGGER.info("############ Confirms MONGOL BANK 3nd service with REG_NUMBER ={} ...", registerNumber);

    String enquireId = (String) execution.getVariable(ENQUIRE_ID);

    if (enquireId.equalsIgnoreCase(LOAN_NOT_FOUND))
    {
      return;
    }

    String borrowerId = (String) execution.getVariable(BORROWER_ID);
    String customerCID = (String) execution.getVariable(CUSTOMER_CID);

    ConfirmLoanEnquireInput input = new ConfirmLoanEnquireInput(enquireId, borrowerId, customerCID);
    ConfirmLoanEnquire confirmLoanEnquire = new ConfirmLoanEnquire(loanService);

    Boolean isConfirmed = confirmLoanEnquire.execute(input);

    if (null != isConfirmed && !isConfirmed)
    {
      String errorCode = "BPMS069";
      throw new ProcessTaskException(errorCode, "MONGOL BANK : Could not confirm mongol bank services with RES_NUMBER=" + registerNumber);
    }

    execution.setVariable(IS_CONFIRMED_LOAN_ENQUIRE, true);

    LOGGER.info("############# Successful confirmed MONGOL BANK 3nd service.");
  }

  private void downloadCustomerLoanInfo(DelegateExecution execution) throws UseCaseException
  {
    String registerNumber = (String) execution.getVariable(REGISTER_NUMBER);

    LOGGER.info("############ Downloading loan infos from MONGOL BANK 4th service with REG_NUMBER ={}...", registerNumber);

    String enquireId = (String) execution.getVariable(ENQUIRE_ID);

    if (enquireId.equalsIgnoreCase(LOAN_NOT_FOUND))
    {
      return;
    }

    String customerCID = (String) execution.getVariable(CUSTOMER_CID);
    String borrowerId = (String) execution.getVariable(BORROWER_ID);

    GetLoanInfoInput input = new GetLoanInfoInput(borrowerId, customerCID);
    GetLoanInfo getLoanInfo = new GetLoanInfo(loanService);

    GetLoanInfoOutput output = getLoanInfo.execute(input);

    List<Loan> loanList = output.getLoanList();
    LoanClass lowestLoanClass = getLowestLoanClass(loanList);

    assert lowestLoanClass != null;
    Integer rank = lowestLoanClass.getRank();
    String loanClassName = lowestLoanClass.getName();

    execution.setVariable(LOAN_CLASS_RANK, rank);
    execution.setVariable(LOAN_CLASS_NAME, loanClassName);

    LOGGER.info("############# Successful downloaded loan infos from MONGOL BANK  4th service.");
  }

  private void downloadLoanEnquireByCoBorrower(DelegateExecution execution) throws UseCaseException
  {
    LOGGER.info("############ Downloads customer CID by Co-borrower from MONGOL BANK 5th service with REG_NUMBER.............");

    String customerCidByCoBorrower = (String) execution.getVariable(CUSTOMER_CID_BY_CO_BORROWER);

    if (null == customerCidByCoBorrower)
    {
      return;
    }

    GetLoanEnquireInput getLoanEnquireInput = new GetLoanEnquireInput(customerCidByCoBorrower, true);
    GetLoanEnquire getLoanEnquire = new GetLoanEnquire(loanService);

    LoanEnquire loanEnquire = getLoanEnquire.execute(getLoanEnquireInput);

    String enquireId = loanEnquire.getId().getId();
    String borrowerId = loanEnquire.getBorrowerId().getId();

    if (LOAN_NOT_FOUND.equalsIgnoreCase(enquireId))
    {
      return;
    }

    setLowestClassRank(execution, borrowerId, customerCidByCoBorrower);

    LOGGER.info("############# Successful downloaded customer CID by Co-borrower from MONGOL BANK 5th service.");
  }

  private void downloadLoanEnquirePDF(DelegateExecution execution) throws UseCaseException
  {
    LOGGER.error("############ Downloading loan enquire PDF from Mongol bank 6th service..............");

    String loanEnquireId = (String) execution.getVariable(ENQUIRE_ID);

    String borrowerId = (String) execution.getVariable(BORROWER_ID);
    String customerCID = (String) execution.getVariable(CUSTOMER_CID);

    GetLoanEnquireWithFileInput input = new GetLoanEnquireWithFileInput(loanEnquireId, borrowerId, customerCID);
    GetLoanEnquireWithFile getLoanEnquireWithFile = new GetLoanEnquireWithFile(loanService);

    LoanEnquire loanEnquire = getLoanEnquireWithFile.execute(input);
    byte[] enquireAsFile = loanEnquire.getEnquireAsFile();

    if (null != enquireAsFile)
    {
      execution.setVariable(LOAN_ENQUIRE_PDF, enquireAsFile);
    }

    LOGGER.error("############# Successful downloaded loan enquire PDF from Mongol bank 6th service");
  }

  private String getGroupId(String userId) throws UseCaseException
  {
    GetUserMembershipsInput input = new GetUserMembershipsInput(userId);

    GetUserMembership getUserMembership = new GetUserMembership(authenticationService, authorizationService, membershipRepository);

    GetMembershipOutput output = getUserMembership.execute(input);

    return output.getGroupId();
  }

  private String getCustomerCID(String searchValue, String searchType, boolean isCoborrower, String branchNumber, String userId,
      String userName) throws UseCaseException
  {

    GetCustomerLoanCidInput input = new GetCustomerLoanCidInput(
        REGISTER_SEARCH_VALUE_TYPE, searchValue,
        searchType, isCoborrower, branchNumber,
        userId, userName);

    GetCustomerLoanCid getCustomerLoanCid = new GetCustomerLoanCid(loanService);

    return getCustomerLoanCid.execute(input);
  }

  private void setLowestClassRank(DelegateExecution execution, String borrowerId, String customerCid) throws UseCaseException
  {
    List<Loan> loanList = getLoanList(borrowerId, customerCid);
    LoanClass loanClassByCoBorrower = MongolBankServiceUtils.getLowestLoanClass(loanList);
    Map<String, Object> variables = execution.getVariables();

    if (variables.containsKey(LOAN_CLASS_RANK))
    {
      Integer rank = (Integer) variables.get(LOAN_CLASS_RANK);
      assert loanClassByCoBorrower != null;
      Integer rankByCoBorrower = loanClassByCoBorrower.getRank();

      if (rank > rankByCoBorrower)
      {
        execution.removeVariable(LOAN_CLASS_RANK);
        execution.removeVariable(LOAN_CLASS_NAME);

        execution.setVariable(LOAN_CLASS_RANK, rankByCoBorrower);
        execution.setVariable(LOAN_CLASS_NAME, loanClassByCoBorrower.getName());
      }
    }
    else
    {
      assert loanClassByCoBorrower != null;
      execution.setVariable(LOAN_CLASS_RANK, loanClassByCoBorrower.getRank());
      execution.setVariable(LOAN_CLASS_RANK, loanClassByCoBorrower.getName());
    }
  }

  private List<Loan> getLoanList(String borrowerId, String customerCID) throws UseCaseException
  {
    GetLoanInfoInput getLoanInfoInput = new GetLoanInfoInput(borrowerId, customerCID);
    GetLoanInfo getLoanInfo = new GetLoanInfo(loanService);

    GetLoanInfoOutput output = getLoanInfo.execute(getLoanInfoInput);
    return output.getLoanList();
  }

  private  void updateRequestState(DelegateExecution execution) throws UseCaseException
  {
    String processTypeId = String.valueOf(execution.getVariable("processTypeId"));
    if (processTypeId.equals("onlineSalary"))
    {
      String processRequestId = (String) execution.getVariable(PROCESS_REQUEST_ID);
      UpdateRequestState updateRequestState = new UpdateRequestState(bpmsRepositoryRegistry.getProcessRequestRepository());
      UpdateRequestStateInput input = new UpdateRequestStateInput(processRequestId, ProcessRequestState.MB_SESSION_EXPIRED);
      UpdateRequestStateOutput output = updateRequestState.execute(input);
      boolean isStateUpdated = output.isUpdated();
      if (isStateUpdated)
      {
        LOGGER.info(ONLINE_SALARY_LOG_HASH + "Updated process request state to MB_SESSION_EXPIRED  with request id [{}].", processRequestId);
      }
    }
  }
}
