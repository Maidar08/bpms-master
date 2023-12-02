/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpms.loan.consumption.service_task.mongol_bank;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.bpms.loan.consumption.exception.CamundaServiceException;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.membership.MembershipId;
import mn.erin.domain.aim.model.role.RoleId;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.LoanService;

import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.CUSTOMER_CID_BY_CO_BORROWER;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.FOREIGN_CITIZEN_SEARCH_TYPE;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.REGISTER_SEARCH_VALUE_TYPE;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.SEARCH_TYPE_MONGOLIAN_CITIZEN;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.VALUE_FOREIGN_CITIZEN;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.VALUE_MONGOLIAN_CITIZEN;
import static mn.erin.bpms.loan.consumption.service_task.mongol_bank.ServiceTaskTestConstants.USER_ID;
import static mn.erin.bpms.loan.consumption.service_task.mongol_bank.ServiceTaskTestConstants.VALUE_BRANCH_NUMBER;
import static mn.erin.bpms.loan.consumption.service_task.mongol_bank.ServiceTaskTestConstants.VALUE_CUSTOMER_CID;
import static mn.erin.bpms.loan.consumption.service_task.mongol_bank.ServiceTaskTestConstants.VALUE_CUSTOMER_CID_BY_CO_BORROWER;
import static mn.erin.bpms.loan.consumption.service_task.mongol_bank.ServiceTaskTestConstants.VALUE_REG_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.BRANCH_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CUSTOMER_CID;
import static mn.erin.domain.bpm.BpmModuleConstants.LEGAL_STATUS;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Tamir
 */
public class GetCustomerCIDFromMongolBankTaskTest
{
  public static final String GET_MEMBERSHIP_PERMISSION_STRING = "admin.aim.GetUserMembership";

  private LoanService loanService;
  private AuthenticationService authenticationService;

  private AuthorizationService authorizationService;
  private MembershipRepository membershipRepository;
  private DelegateExecution delegateExecution;

  private GetCustomerCIDFromMongolBankTask getCustomerCIDFromMongolBankTask;

  @Before
  public void setUp()
  {
    loanService = Mockito.mock(LoanService.class);
    membershipRepository = Mockito.mock(MembershipRepository.class);

    authenticationService = Mockito.mock(AuthenticationService.class);
    authorizationService = Mockito.mock(AuthorizationService.class);

    delegateExecution = Mockito.mock(DelegateExecution.class);
    getCustomerCIDFromMongolBankTask = new GetCustomerCIDFromMongolBankTask(loanService, authenticationService, authorizationService, membershipRepository);
  }

  @Test(expected = CamundaServiceException.class)
  public void when_customer_cid_null() throws Exception
  {
    Map<String, Object> properties = getProperties(VALUE_REG_NUMBER, null);
    properties.put(LEGAL_STATUS, VALUE_FOREIGN_CITIZEN);

    when(delegateExecution.getVariables()).thenReturn(properties);
    when(delegateExecution.getVariable(REGISTER_NUMBER)).thenReturn(VALUE_REG_NUMBER);
    when(delegateExecution.getVariable(BRANCH_NUMBER)).thenReturn(null);
    when(delegateExecution.getVariable(LEGAL_STATUS)).thenReturn(VALUE_FOREIGN_CITIZEN);

    mockAuthorizedUseCase(USER_ID, GET_MEMBERSHIP_PERMISSION_STRING);

    Membership membership = getMembership();

    when(membershipRepository.findByUserId(UserId.valueOf(USER_ID))).thenReturn(membership);

    when(loanService.getCustomerCID(REGISTER_SEARCH_VALUE_TYPE, VALUE_REG_NUMBER, FOREIGN_CITIZEN_SEARCH_TYPE,
        false, VALUE_BRANCH_NUMBER, USER_ID, USER_ID)).thenReturn(null);

    getCustomerCIDFromMongolBankTask.execute(delegateExecution);
  }

  @Test
  public void when_successful_download_cid() throws Exception
  {
    Map<String, Object> properties = getProperties(VALUE_REG_NUMBER, VALUE_BRANCH_NUMBER);
    properties.put(LEGAL_STATUS, VALUE_MONGOLIAN_CITIZEN);

    when(delegateExecution.getVariables()).thenReturn(properties);
    when(delegateExecution.getVariable(REGISTER_NUMBER)).thenReturn(VALUE_REG_NUMBER);
    when(delegateExecution.getVariable(BRANCH_NUMBER)).thenReturn(VALUE_BRANCH_NUMBER);
    when(delegateExecution.getVariable(LEGAL_STATUS)).thenReturn(VALUE_MONGOLIAN_CITIZEN);

    when(authenticationService.getCurrentUserId()).thenReturn(USER_ID);

    mockTwoTimesGetCustomerCID();

    getCustomerCIDFromMongolBankTask.execute(delegateExecution);

    verify(delegateExecution, times(1)).setVariable(CUSTOMER_CID, VALUE_CUSTOMER_CID);
    verify(delegateExecution, times(1)).setVariable(CUSTOMER_CID_BY_CO_BORROWER, VALUE_CUSTOMER_CID_BY_CO_BORROWER);
  }

  private void mockTwoTimesGetCustomerCID() throws BpmServiceException
  {
    // downloads customer cid
    when(loanService.getCustomerCID(REGISTER_SEARCH_VALUE_TYPE, VALUE_REG_NUMBER, SEARCH_TYPE_MONGOLIAN_CITIZEN,
        false, VALUE_BRANCH_NUMBER, USER_ID, USER_ID)).thenReturn(VALUE_CUSTOMER_CID);

    // downloads customer cid by co-borrower.
    when(loanService.getCustomerCID(REGISTER_SEARCH_VALUE_TYPE, VALUE_REG_NUMBER, SEARCH_TYPE_MONGOLIAN_CITIZEN,
        true, VALUE_BRANCH_NUMBER, USER_ID, USER_ID)).thenReturn(VALUE_CUSTOMER_CID_BY_CO_BORROWER);
  }

  private Map<String, Object> getProperties(String regNumValue, String branchValue)
  {
    Map<String, Object> properties = new HashMap<>();

    properties.put(REGISTER_NUMBER, regNumValue);
    properties.put(BRANCH_NUMBER, branchValue);

    return properties;
  }

  private void mockAuthorizedUseCase(String userId, String permissionStr)
  {
    when(authenticationService.getCurrentUserId()).thenReturn(userId);
    when(authorizationService.hasPermission(USER_ID, permissionStr)).thenReturn(Boolean.TRUE);
  }

  private Membership getMembership()
  {
    return new Membership(MembershipId.valueOf("membership1"), UserId.valueOf(USER_ID), GroupId.valueOf("101"), RoleId.valueOf("branch_specialist"));
  }
}
