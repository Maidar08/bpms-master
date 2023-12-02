/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpms.loan.consumption.service_task.mongol_bank;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import mn.erin.bpms.loan.consumption.utils.MongolBankServiceUtils;
import mn.erin.bpms.process.base.ProcessTaskException;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.membership.GetMembershipOutput;
import mn.erin.domain.aim.usecase.membership.GetUserMembership;
import mn.erin.domain.aim.usecase.membership.GetUserMembershipsInput;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.loan.Loan;
import mn.erin.domain.bpm.model.loan.LoanClass;
import mn.erin.domain.bpm.model.loan.LoanEnquire;
import mn.erin.domain.bpm.model.loan.LoanEnquireId;
import mn.erin.domain.bpm.service.LoanService;
import mn.erin.domain.bpm.usecase.loan.ConfirmLoanEnquire;
import mn.erin.domain.bpm.usecase.loan.ConfirmLoanEnquireInput;
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
import static mn.erin.domain.bpm.BpmModuleConstants.BRANCH_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CUSTOMER_CID;
import static mn.erin.domain.bpm.BpmModuleConstants.LEGAL_STATUS;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;

/**
 * @author Tamir
 */
public class DownloadLoanInfoFromMongolBankTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DownloadLoanInfoFromMongolBankTask.class);

  private final LoanService loanService;
  private final AuthenticationService authenticationService;

  private final AuthorizationService authorizationService;
  private final MembershipRepository membershipRepository;
  private final Environment environment;

  public DownloadLoanInfoFromMongolBankTask(LoanService loanService, AuthenticationService authenticationService,
      AuthorizationService authorizationService, MembershipRepository membershipRepository, Environment environment)
  {
    this.loanService = Objects.requireNonNull(loanService, "Loan service is required!");
    this.authenticationService = Objects.requireNonNull(authenticationService, "Authentication service is required!");

    this.authorizationService = Objects.requireNonNull(authorizationService, "Authorization service is required!");
    this.membershipRepository = Objects.requireNonNull(membershipRepository, "Membership repository is required!");
    this.environment = Objects.requireNonNull(environment, "Spring environment is required!");
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String caseInstanceId = (String) execution.getVariable(CASE_INSTANCE_ID);
    LOGGER.info("############ Starting MONGOL BANK services ... ");

    try
    {
      // step 1 - Downloads CID number.
      downloadCustomerCID(execution);

      downloadCustomerRelatedInfo(execution);

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
    }
    catch (UseCaseException e)
    {
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

    String customerCID = getCustomerCID(REGISTER_SEARCH_VALUE_TYPE, registerNumber, searchType,
        false, branchNumber, currentUserId, currentUserId);

    String customerCIDByCoBorrower = getCustomerCID(REGISTER_SEARCH_VALUE_TYPE, registerNumber, searchType,
        true, branchNumber, currentUserId, currentUserId);

    if (null == customerCID)
    {
      String errorCode = "BPMS061";
      throw new ProcessTaskException(errorCode, "MONGOL BANK : Customer CID is null!");
    }

    execution.setVariable(CUSTOMER_CID, customerCID);
    execution.setVariable(CUSTOMER_CID_BY_CO_BORROWER, customerCIDByCoBorrower);

    LOGGER.info("############# Successful downloaded customer CID from MONGOL BANK 1st service with REG_NUMBER ={}", registerNumber);
  }

  private void downloadCustomerRelatedInfo(DelegateExecution execution) throws UseCaseException
  {
    String CID = String.valueOf(execution.getVariable(CUSTOMER_CID));
    GetCustomerRelatedInfoInput input = new GetCustomerRelatedInfoInput(CID, "citizenrelationinfo");
    GetCustomerRelatedInfo getCustomerRelatedInfo = new GetCustomerRelatedInfo(loanService);
//    Map<String, String> execute = getCustomerRelatedInfo.execute(input);
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

  private String getCustomerCID(String searchValueType, String searchValue, String searchType, boolean isCoborrower, String branchNumber, String userId,
      String userName) throws UseCaseException
  {

    GetCustomerLoanCidInput input = new GetCustomerLoanCidInput(searchValueType, searchValue,
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
}
