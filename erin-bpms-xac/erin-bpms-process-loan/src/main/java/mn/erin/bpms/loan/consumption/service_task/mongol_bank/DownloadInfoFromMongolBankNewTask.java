package mn.erin.bpms.loan.consumption.service_task.mongol_bank;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import mn.erin.bpms.process.base.ProcessTaskException;
import mn.erin.domain.aim.service.AimServiceRegistry;
import mn.erin.domain.aim.usecase.membership.GetMembershipOutput;
import mn.erin.domain.aim.usecase.membership.GetUserMembership;
import mn.erin.domain.aim.usecase.membership.GetUserMembershipsInput;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.loan.Loan;
import mn.erin.domain.bpm.model.loan.LoanClass;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;
import mn.erin.domain.bpm.usecase.document.UploadDocuments;
import mn.erin.domain.bpm.usecase.document.UploadDocumentsInput;
import mn.erin.domain.bpm.usecase.document.UploadFile;
import mn.erin.domain.bpm.usecase.loan.GetMongolBankInfoNew;

import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.NORMAL;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.FOREIGN_CITIZEN_SEARCH_TYPE;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.FOUNDATION_SEARCH_TYPE;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.GOVERNMENT_ORGANIZATION_SEARCH_TYPE;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.LEGAL_STATUS_FOUNDATION;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.LEGAL_STATUS_GOVERNMENT_AGENCY;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.LEGAL_STATUS_MONASTERY;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.LEGAL_STATUS_ORGANIZATION;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.LOAN_CLASS_NAME;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.LOAN_CLASS_RANK;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.LOAN_ENQUIRE_PDF;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.MONASTERY_SEARCH_TYPE;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.ORGANIZATION_SEARCH_TYPE;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.SEARCH_TYPE_MONGOLIAN_CITIZEN;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.VALUE_FOREIGN_CITIZEN;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.VALUE_MONGOLIAN_CITIZEN;
import static mn.erin.bpms.loan.consumption.utils.CustomerInfoUtils.getParametersOfCustomerToLdms;
import static mn.erin.domain.bpm.BpmMessagesConstants.BNPL_LOG;
import static mn.erin.domain.bpm.BpmMessagesConstants.ONLINE_SALARY_LOG_HASH;
import static mn.erin.domain.bpm.BpmModuleConstants.BNPL_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.BRANCH_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.DOCUMENT_NAME_MONGOL_BANK_ENQUIRE_PDF;
import static mn.erin.domain.bpm.BpmModuleConstants.EMPTY_VALUE;
import static mn.erin.domain.bpm.BpmModuleConstants.LEGAL_STATUS;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_BORROWER_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.MICRO_MONGOL_BANK_LEGAL_STATE;
import static mn.erin.domain.bpm.BpmModuleConstants.MONGOL_BANK_ADDRESS;
import static mn.erin.domain.bpm.BpmModuleConstants.MONGOL_BANK_EXECUTIVE_FIRST_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.MONGOL_BANK_EXECUTIVE_LAST_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.MONGOL_BANK_FIRST_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.MONGOL_BANK_ID_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.MONGOL_BANK_LAST_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.MONGOL_BANK_LEGAL_STATE;
import static mn.erin.domain.bpm.BpmModuleConstants.MONGOL_BANK_OCCUPANCY;
import static mn.erin.domain.bpm.BpmModuleConstants.MONGOL_BANK_OFFICIAL_ADDRESS;
import static mn.erin.domain.bpm.BpmModuleConstants.MONGOL_BANK_ORGANIZATION_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.MONGOL_BANK_REGISTER_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.MONGOL_BANK_REG_NUM_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.MONGOL_BANK_STATE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.NO_MN_VALUE;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_SALARY_PROCESS_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.SOURCE_LDMS;
import static mn.erin.domain.bpm.BpmModuleConstants.TRACK_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.YES_MN_VALUE;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.util.process.BpmUtils.convertByteArrayToBase64;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

public class DownloadInfoFromMongolBankNewTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DownloadInfoFromMongolBankNewTask.class);
  private static final String DOWNLOAD_MONGOL_BANK_VARIABLE_NAME = "downloadMongolBank";

  private final AimServiceRegistry aimServiceRegistry;
  private final BpmsServiceRegistry bpmsServiceRegistry;
  private final BpmsRepositoryRegistry bpmsRepositoryRegistry;

  public DownloadInfoFromMongolBankNewTask(AimServiceRegistry aimServiceRegistry, BpmsServiceRegistry bpmsServiceRegistry,
      BpmsRepositoryRegistry bpmsRepositoryRegistry)
  {
    this.aimServiceRegistry = Objects.requireNonNull(aimServiceRegistry, "Aim service registry is required!");
    this.bpmsServiceRegistry = Objects.requireNonNull(bpmsServiceRegistry, "Bpms service registry  required!");
    this.bpmsRepositoryRegistry = Objects.requireNonNull(bpmsRepositoryRegistry, "Bpms repository registry required!");
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    if (null != execution.getVariable(DOWNLOAD_MONGOL_BANK_VARIABLE_NAME) && (boolean) execution.getVariable(DOWNLOAD_MONGOL_BANK_VARIABLE_NAME))
    {
      try
      {
        Map<String, String> requestParameters = new HashMap<>();

        String caseInstanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));
        String processInstanceId = String.valueOf(execution.getVariable(PROCESS_INSTANCE_ID));
        String instanceId = execution.getVariable(PROCESS_TYPE_ID).equals(ONLINE_SALARY_PROCESS_TYPE) ? caseInstanceId : processInstanceId;

        String trackNumber = String.valueOf(execution.getVariable(TRACK_NUMBER));
        String processTypeId = String.valueOf(execution.getVariable(PROCESS_TYPE_ID));
        String requestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
        String registerNumber = getValidString(execution.getVariable(REGISTER_NUMBER));
        String currentUserId = aimServiceRegistry.getAuthenticationService().getCurrentUserId();
        String branchNumber = getValidString(execution.getVariable(BRANCH_NUMBER));
        if (StringUtils.isEmpty(branchNumber))
        {
          branchNumber = getGroupId(currentUserId);
        }
        requestParameters.put(PROCESS_REQUEST_ID, requestId);
        requestParameters.put(REGISTER_NUMBER, registerNumber);
        requestParameters.put("customerTypeId", getLegalStatus(execution));
        requestParameters.put(BRANCH_NUMBER, branchNumber);
        requestParameters.put("userId", currentUserId);
        requestParameters.put("customerName", currentUserId);
        requestParameters.put(PROCESS_TYPE_ID, processTypeId);
        requestParameters.put(PHONE_NUMBER, String.valueOf(execution.getVariable(PHONE_NUMBER)));

        execution.setVariable("mbRequestParameter", requestParameters);

        if (processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
        {
          LOGGER.info("############ Starting download customer MONGOL BANK info with REG_NUMBER ={}, REQUEST_ID ={}, User ID ={}, TRACKNUMBER ={}", registerNumber, requestId,
              currentUserId, trackNumber);
        }
        else
        {
          LOGGER.info("############ Starting download customer MONGOL BANK info with REG_NUMBER ={}, REQUEST_ID ={}, User ID ={}", registerNumber, requestId,
              currentUserId);
        }

        GetMongolBankInfoNew getMongolBankInfoNew = new GetMongolBankInfoNew(bpmsServiceRegistry.getLoanService());
        Map<String, Object> mbLoanInfo = getMongolBankInfoNew.execute(requestParameters);

        if (mbLoanInfo != null && !mbLoanInfo.isEmpty())
        {
          setLoanClass(execution, mbLoanInfo);
          getDetailInfo(execution, mbLoanInfo);
          getLoanFile(execution, mbLoanInfo);

          LOGGER.info("############ Successfully downloaded customer MONGOL BANK info with REG_NUMBER ={}, REQUEST_ID ={}, User ID ={}", registerNumber, requestId,
              currentUserId);

          if (processTypeId.equals(ONLINE_SALARY_PROCESS_TYPE))
          {
            sendFileToLDMS(execution, instanceId);
          }
        }
      }
      catch (UseCaseException e)
      {
        throw new ProcessTaskException(e.getCode(), "CIB_FAILED " + e.getMessage());
      }
    }
  }

  private void getDetailInfo(DelegateExecution execution, Map<String, Object> mbInfo)
  {
    Map<String, Object> customerDetailInfo = (Map<String, Object>) mbInfo.get("mbCustomerInfo");
    String mongolBankLegalState = customerDetailInfo.get(MONGOL_BANK_LEGAL_STATE).equals("1") ? YES_MN_VALUE : NO_MN_VALUE;
    String borrowerType = getValidString(execution.getVariable(LOAN_BORROWER_TYPE));

    if (borrowerType.equals(EMPTY_VALUE) || borrowerType.equals("Иргэн"))
    {
      setCustomerDetailInfo(customerDetailInfo, execution, mongolBankLegalState);
    }
    if (borrowerType.equals("Аж ахуй нэгж"))
    {
      setOrganizationDetailInfo(customerDetailInfo, execution, mongolBankLegalState);
    }
  }

  private void setCustomerDetailInfo(Map<String, Object> customerDetailInfo, DelegateExecution execution, String mongolBankLegalState)
  {
    execution.setVariable(MONGOL_BANK_FIRST_NAME, customerDetailInfo.get(MONGOL_BANK_FIRST_NAME));
    execution.setVariable(MONGOL_BANK_LAST_NAME, customerDetailInfo.get(MONGOL_BANK_LAST_NAME));
    execution.setVariable(MONGOL_BANK_LEGAL_STATE, mongolBankLegalState);
    execution.setVariable(MONGOL_BANK_REGISTER_NUMBER, customerDetailInfo.get(MONGOL_BANK_REGISTER_NUMBER));
    execution.setVariable(MONGOL_BANK_ID_NUMBER, customerDetailInfo.get(MONGOL_BANK_ID_NUMBER));
    execution.setVariable(MONGOL_BANK_ADDRESS, customerDetailInfo.get(MONGOL_BANK_ADDRESS));
    execution.setVariable(MONGOL_BANK_OCCUPANCY, customerDetailInfo.get(MONGOL_BANK_OCCUPANCY));
  }

  private void setOrganizationDetailInfo(Map<String, Object> customerDetailInfo, DelegateExecution execution, String mongolBankLegalState)
  {
    execution.setVariable(MONGOL_BANK_ORGANIZATION_NAME, customerDetailInfo.get(MONGOL_BANK_FIRST_NAME));
    execution.setVariable(MONGOL_BANK_REG_NUM_ID, customerDetailInfo.get(MONGOL_BANK_REGISTER_NUMBER));
    execution.setVariable(MONGOL_BANK_STATE_NUMBER, customerDetailInfo.get(MONGOL_BANK_ID_NUMBER));
    execution.setVariable(MONGOL_BANK_OFFICIAL_ADDRESS, customerDetailInfo.get(MONGOL_BANK_ADDRESS));
    execution.setVariable(MONGOL_BANK_EXECUTIVE_FIRST_NAME, customerDetailInfo.get(MONGOL_BANK_EXECUTIVE_FIRST_NAME));
    execution.setVariable(MONGOL_BANK_EXECUTIVE_LAST_NAME, customerDetailInfo.get(MONGOL_BANK_EXECUTIVE_LAST_NAME));
    execution.setVariable(MICRO_MONGOL_BANK_LEGAL_STATE, mongolBankLegalState);
  }

  private String getGroupId(String userId) throws UseCaseException
  {
    GetUserMembershipsInput input = new GetUserMembershipsInput(userId);

    GetUserMembership getUserMembership = new GetUserMembership(aimServiceRegistry.getAuthenticationService(), aimServiceRegistry.getAuthorizationService(),
        aimServiceRegistry.getMembershipRepository());

    GetMembershipOutput output = getUserMembership.execute(input);

    return output.getGroupId();
  }

  private void setLoanClass(DelegateExecution execution, Map<String, Object> mbInfo)
  {
    List<Loan> loanList = (List<Loan>) mbInfo.get("mbCustomerLoanList");
    List<Loan> coBorrowerLoans = (List<Loan>) mbInfo.get("mbCoBorrowerLoans");
    loanList.addAll(coBorrowerLoans);
    LoanClass lowestLoanClass = getLowestLoanClass(loanList);
    Integer rank = lowestLoanClass.getRank();
    String loanClassName = lowestLoanClass.getName();

    execution.setVariable(LOAN_CLASS_RANK, rank);
    execution.setVariable(LOAN_CLASS_NAME, loanClassName);

    LOGGER.info("############# Set LOAN CLASS as {} with  rank = {}", loanClassName, rank);
  }

  private void getLoanFile(DelegateExecution execution, Map<String, Object> mbLoanInfo)
  {
    String pdfFileAsBase64 = String.valueOf(mbLoanInfo.get("mbBase64Pdf"));
    byte[] encodedByteFile = Base64.getEncoder().encode(pdfFileAsBase64.getBytes());
    execution.setVariable(LOAN_ENQUIRE_PDF, encodedByteFile);
  }

  private void sendFileToLDMS(DelegateExecution execution, String instanceId) throws ProcessTaskException
  {
    String processRequestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));

    String categoryEnquire = String.valueOf(execution.getVariable("categoryEnquire"));
    String subCategoryMongolBank = String.valueOf(execution.getVariable("subCategoryMongolBank"));
    List<UploadFile> uploadFiles = new ArrayList<>();

    byte[] mbEnquireFile = (byte[]) execution.getVariable(LOAN_ENQUIRE_PDF);

    final String base64 = convertByteArrayToBase64(mbEnquireFile);
    uploadFiles.add(new UploadFile(DOCUMENT_NAME_MONGOL_BANK_ENQUIRE_PDF, base64));

    Map<String, String> parameterValues = getParametersOfCustomerToLdms(execution);
    UploadDocumentsInput input = new UploadDocumentsInput(instanceId, categoryEnquire, subCategoryMongolBank, SOURCE_LDMS, parameterValues, uploadFiles);
    UploadDocuments useCase = new UploadDocuments(aimServiceRegistry.getAuthenticationService(), aimServiceRegistry.getAuthorizationService(),
        bpmsServiceRegistry.getDocumentService(), aimServiceRegistry.getMembershipRepository(), bpmsRepositoryRegistry.getDocumentInfoRepository(),
        bpmsRepositoryRegistry.getDocumentRepository());
    Boolean isUploadedFile = null;
    try
    {
      isUploadedFile = useCase.execute(input);
    }
    catch (UseCaseException e)
    {
      LOGGER.error("LDMS UPLOAD ERROR \n {}", base64);
      e.printStackTrace();
      throw new ProcessTaskException(e.getCode(), e.getMessage());
    }
    if (Boolean.TRUE.equals(isUploadedFile))
    {
      String log = getValidString(execution.getVariable(PROCESS_TYPE_ID)).equals(BNPL_PROCESS_TYPE_ID) ? BNPL_LOG: ONLINE_SALARY_LOG_HASH;
      LOGGER.info("{} SENT MONGOL BANK ENQUIRE FILE TO LDMS WITH REQUEST ID [{}]",log, processRequestId);
    }
  }

  public static LoanClass getLowestLoanClass(List<Loan> loanList)
  {
    if (!loanList.isEmpty())
    {
      loanList.sort((loan1, loan2) -> loan2.getLoanClass().getRank().compareTo((loan1.getLoanClass().getRank())));
      return loanList.get(0).getLoanClass();
    }
    else
      return new LoanClass(1, NORMAL);
  }

  public static String getLegalStatus(DelegateExecution execution)
  {
    // GETS SEARCH TYPE DEPENDING ON LEGAL STATUS.
    String legalStatus = getValidString(execution.getVariable(LEGAL_STATUS));

    switch (legalStatus)
    {
    case VALUE_MONGOLIAN_CITIZEN:
      return SEARCH_TYPE_MONGOLIAN_CITIZEN;
    case VALUE_FOREIGN_CITIZEN:
      return FOREIGN_CITIZEN_SEARCH_TYPE;
    case LEGAL_STATUS_ORGANIZATION:
      return ORGANIZATION_SEARCH_TYPE;
    case LEGAL_STATUS_GOVERNMENT_AGENCY:
      return GOVERNMENT_ORGANIZATION_SEARCH_TYPE;
    case LEGAL_STATUS_MONASTERY:
      return MONASTERY_SEARCH_TYPE;
    case LEGAL_STATUS_FOUNDATION:
      return FOUNDATION_SEARCH_TYPE;
    default:
      return null;
    }
  }
}
