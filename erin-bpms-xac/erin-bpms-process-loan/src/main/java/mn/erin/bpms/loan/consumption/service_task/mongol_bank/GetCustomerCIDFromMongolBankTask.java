/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpms.loan.consumption.service_task.mongol_bank;

import java.util.Map;
import java.util.Objects;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.bpms.loan.consumption.exception.CamundaServiceException;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.membership.GetMembershipOutput;
import mn.erin.domain.aim.usecase.membership.GetUserMembership;
import mn.erin.domain.aim.usecase.membership.GetUserMembershipsInput;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.LoanService;
import mn.erin.domain.bpm.usecase.loan.GetCustomerLoanCid;
import mn.erin.domain.bpm.usecase.loan.GetCustomerLoanCidInput;

import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.CUSTOMER_CID_BY_CO_BORROWER;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.FOREIGN_CITIZEN_SEARCH_TYPE;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.REGISTER_SEARCH_VALUE_TYPE;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.SEARCH_TYPE_MONGOLIAN_CITIZEN;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.VALUE_FOREIGN_CITIZEN;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.VALUE_MONGOLIAN_CITIZEN;
import static mn.erin.domain.bpm.BpmModuleConstants.BRANCH_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CUSTOMER_CID;
import static mn.erin.domain.bpm.BpmModuleConstants.LEGAL_STATUS;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;

/**
 * @author Tamir
 */
public class GetCustomerCIDFromMongolBankTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GetCustomerCIDFromMongolBankTask.class);

  private final LoanService loanService;
  private final AuthenticationService authenticationService;
  private final AuthorizationService authorizationService;
  private final MembershipRepository membershipRepository;

  public GetCustomerCIDFromMongolBankTask(LoanService loanService, AuthenticationService authenticationService,
      AuthorizationService authorizationService, MembershipRepository membershipRepository)
  {
    this.loanService = Objects.requireNonNull(loanService, "Loan service is required!");
    this.authenticationService = Objects.requireNonNull(authenticationService, "Authentication service is required!");

    this.authorizationService = Objects.requireNonNull(authorizationService, "Authorization service is required!");
    this.membershipRepository = Objects.requireNonNull(membershipRepository, "Membership repository is required!");
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    LOGGER.info("############# Gets customer CID from MONGOL BANK 1st service..............");

    Map<String, Object> executionVariables = execution.getVariables();

    String registerNumber = (String) executionVariables.get(REGISTER_NUMBER);
    String branchNumber = (String) executionVariables.get(BRANCH_NUMBER);

    String searchType = null;
    String legalStatus = (String) executionVariables.get(LEGAL_STATUS);

    if (legalStatus.equalsIgnoreCase(VALUE_MONGOLIAN_CITIZEN))
    {
      searchType = SEARCH_TYPE_MONGOLIAN_CITIZEN;
    }
    else if (legalStatus.equalsIgnoreCase(VALUE_FOREIGN_CITIZEN))
    {
      searchType = FOREIGN_CITIZEN_SEARCH_TYPE;
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
      throw new CamundaServiceException("MONGOL BANK : Customer CID is null!");
    }

    execution.setVariable(CUSTOMER_CID, customerCID);
    execution.setVariable(CUSTOMER_CID_BY_CO_BORROWER, customerCIDByCoBorrower);

    LOGGER.info("############# Successful received customer CID from MONGOL BANK 1st service.");
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
}
