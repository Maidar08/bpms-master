package mn.erin.bpms.loan.consumption.service_task.mongol_bank;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

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
import mn.erin.domain.bpm.service.LoanService;
import mn.erin.domain.bpm.usecase.loan.GetMongolBankInfoNew;

import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.LOAN_CLASS_NAME;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.LOAN_CLASS_RANK;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.LOAN_ENQUIRE_PDF_CO_BORROWER;
import static mn.erin.bpms.loan.consumption.service_task.bpms.co_borrower.CoBorrowerUtils.INDEX_CO_BORROWER;
import static mn.erin.bpms.loan.consumption.service_task.mongol_bank.DownloadInfoFromMongolBankNewTask.getLegalStatus;
import static mn.erin.bpms.loan.consumption.service_task.mongol_bank.DownloadInfoFromMongolBankNewTask.getLowestLoanClass;
import static mn.erin.domain.bpm.BpmModuleConstants.BRANCH_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

/**
 * @author Tamir
 */
public class DownloadCoBorrowerInfoFromMongolBankTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DownloadCoBorrowerInfoFromMongolBankTask.class);

  private final LoanService loanService;
  private final AuthenticationService authenticationService;

  private final AuthorizationService authorizationService;
  private final MembershipRepository membershipRepository;
  private final Environment environment;

  public DownloadCoBorrowerInfoFromMongolBankTask(LoanService loanService, AuthenticationService authenticationService,
      AuthorizationService authorizationService, MembershipRepository membershipRepository, Environment environment)
  {
    this.loanService = Objects.requireNonNull(loanService, "Loan service is required!");
    this.authenticationService = Objects.requireNonNull(authenticationService, "Authentication service is required!");

    this.authorizationService = Objects.requireNonNull(authorizationService, "Authorization service is required!");
    this.membershipRepository = Objects.requireNonNull(membershipRepository, "Membership repository is required!");
    this.environment = Objects.requireNonNull(environment, "Spring Environment is required!");
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    try
    {
      Map<String, String> requestParameters = new HashMap<>();
      String requestId = (String) execution.getVariable(PROCESS_REQUEST_ID);
      String registerNumber = getValidString(execution.getVariable("registerNumberCoBorrower"));
      String currentUserId = authenticationService.getCurrentUserId();
      String branchNumber = getValidString(execution.getVariable(BRANCH_NUMBER));
      if (null == branchNumber)
      {
        branchNumber = getGroupId(currentUserId);
      }
      requestParameters.put(PROCESS_REQUEST_ID, requestId);
      requestParameters.put(REGISTER_NUMBER, registerNumber);
      requestParameters.put("type","2");// it will be constant
      requestParameters.put("customerTypeId", getLegalStatus(execution));
      requestParameters.put(BRANCH_NUMBER, branchNumber);
      requestParameters.put("userId", currentUserId);
      requestParameters.put("customerName", currentUserId);
      requestParameters.put(PROCESS_TYPE_ID, String.valueOf(execution.getVariable(PROCESS_TYPE_ID)));
      requestParameters.put(PHONE_NUMBER, String.valueOf(execution.getVariable(PHONE_NUMBER)));

      LOGGER.info("############ Starting download CO-BORROWER MONGOL BANK info with REG_NUMBER ={}, REQUEST_ID ={}, User ID ={}", registerNumber, requestId,
          currentUserId);

      GetMongolBankInfoNew getMongolBankInfoNew = new GetMongolBankInfoNew(loanService);
      Map<String, Object> mbLoanInfo = getMongolBankInfoNew.execute(requestParameters);

      if (mbLoanInfo != null && !mbLoanInfo.isEmpty())
      {
        setLoanClass(execution, mbLoanInfo);
        getLoanPdfFile(execution, mbLoanInfo);
      }
      LOGGER.info("############ Downloaded CO-BORROWER MONGOL BANK info with REG_NUMBER ={}, REQUEST_ID ={}, User ID ={}", registerNumber, requestId,
          currentUserId);
    }
    catch (UseCaseException e)
    {
      throw new ProcessTaskException(e.getCode(), e.getMessage());
    }
  }

  private void setLoanClass(DelegateExecution execution, Map<String, Object> mbInfo)
  {
    Integer coBorrowerIndex = (Integer) execution.getVariable(INDEX_CO_BORROWER);

    List<Loan> loanList = (List<Loan>) mbInfo.get("mbCustomerLoanList");
    List<Loan> coBorrowerLoans = (List<Loan>) mbInfo.get("mbCoBorrowerLoans");
    loanList.addAll(coBorrowerLoans);
    LoanClass lowestLoanClass = getLowestLoanClass(loanList);
    Integer rank = lowestLoanClass.getRank();
    String loanClassName = lowestLoanClass.getName();

    String currentRegNum = (String) execution.getVariable(REGISTER_NUMBER_CO_BORROWER);

    for (Integer index = coBorrowerIndex; index > 0; index--)
    {
      String regNumIndexedCoBorrower = (String) execution.getVariable(REGISTER_NUMBER_CO_BORROWER + "-" + index);

      if (regNumIndexedCoBorrower.equalsIgnoreCase(currentRegNum))
      {
        execution.setVariable(LOAN_CLASS_RANK + "-" + index, rank);
        execution.setVariable(LOAN_CLASS_NAME + "-" + index, loanClassName);
      }
    }

    LOGGER.info("############# Set CO-BORROWER LOAN CLASS as {} with  rank = {}", loanClassName, rank);
  }

  private void getLoanPdfFile(DelegateExecution execution, Map<String, Object> mbLoanInfo)
  {

    Integer currentCBIndex = getCurrentCoBorrowerIndex(execution);
    String pdfFileAsBase64 = String.valueOf(mbLoanInfo.get("mbBase64Pdf"));
    byte[] encodedByteFile = Base64.getEncoder().encode(pdfFileAsBase64.getBytes());
    if (null == currentCBIndex)
    {
      execution.setVariable(LOAN_ENQUIRE_PDF_CO_BORROWER, encodedByteFile);
    }
    execution.setVariable(LOAN_ENQUIRE_PDF_CO_BORROWER + "-" + currentCBIndex, encodedByteFile);
  }

  private Integer getCurrentCoBorrowerIndex(DelegateExecution execution)
  {
    String currentRegNum = (String) execution.getVariable(REGISTER_NUMBER_CO_BORROWER);
    Integer coBorrowerIndex = (Integer) execution.getVariable(INDEX_CO_BORROWER);

    for (Integer index = coBorrowerIndex; index > 0; index--)
    {
      String regNumIndexedCB = (String) execution.getVariable(REGISTER_NUMBER_CO_BORROWER + "-" + index);

      if (regNumIndexedCB.equalsIgnoreCase(currentRegNum))
      {
        return index;
      }
    }
    return null;
  }

  private String getGroupId(String userId) throws UseCaseException
  {
    GetUserMembershipsInput input = new GetUserMembershipsInput(userId);

    GetUserMembership getUserMembership = new GetUserMembership(authenticationService, authorizationService, membershipRepository);

    GetMembershipOutput output = getUserMembership.execute(input);

    return output.getGroupId();
  }
}
