package mn.erin.bpms.loan.consumption.loan_account;

import java.util.Collections;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.impl.cmmn.entity.repository.CaseDefinitionEntity;
import org.camunda.bpm.engine.impl.cmmn.entity.runtime.CaseExecutionEntity;
import org.camunda.bpm.engine.impl.cmmn.model.CmmnActivity;
import org.camunda.bpm.engine.runtime.CaseExecution;
import org.camunda.bpm.engine.runtime.CaseExecutionQuery;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.bpms.loan.consumption.service_task.bpms.CreateLoanAccountTask;
import mn.erin.bpms.process.base.ProcessTaskException;
import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.membership.MembershipId;
import mn.erin.domain.aim.model.role.RoleId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.account.UDField;
import mn.erin.domain.bpm.model.account.UDFieldId;
import mn.erin.domain.bpm.service.CoreBankingService;
import mn.erin.domain.bpm.service.NewCoreBankingService;

import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_GENERATE_LOAN_DECISION;

/**
 * @author Zorig
 */

public class CreateLoanAccountTaskTest
{
  private NewCoreBankingService newCoreBankingService;
  private CoreBankingService coreBankingService;
  private AuthenticationService authenticationService;
  private MembershipRepository membershipRepository;
  private DelegateExecution delegateExecution;
  private CreateLoanAccountTask createLoanAccountTask;
  private CaseService caseService;
  private ProcessEngine processEngine;
  private CaseExecution caseExecution;

  @Before
  public void setUp()
  {
    delegateExecution = Mockito.mock(DelegateExecution.class);
    authenticationService = Mockito.mock(AuthenticationService.class);
    newCoreBankingService = Mockito.mock(NewCoreBankingService.class);
    coreBankingService = Mockito.mock(CoreBankingService.class);
    membershipRepository = Mockito.mock(MembershipRepository.class);
    createLoanAccountTask = new CreateLoanAccountTask(newCoreBankingService, coreBankingService, authenticationService, membershipRepository);
    caseService = Mockito.mock(CaseService.class);
    processEngine = Mockito.mock(ProcessEngine.class);
    caseExecution = Mockito.mock(CaseExecution.class);
  }

  @Test
  public void when_loan_account_create_null() throws Exception
  {
    Mockito.when(delegateExecution.getVariable("isLoanAccountCreate")).thenReturn(null);

    createLoanAccountTask.execute(delegateExecution);
    Mockito.verify(delegateExecution, Mockito.times(1)).getVariable("isLoanAccountCreate");
  }

  @Test(expected = ProcessTaskException.class)
  public void when_continue_without_account_creation() throws Exception
  {
    Mockito.when(delegateExecution.getVariable("isLoanAccountCreate")).thenReturn(false);

    createLoanAccountTask.execute(delegateExecution);
  }

  @Test
  public void when_continue_after_account_creation() throws Exception
  {
    Mockito.when(delegateExecution.getVariable("isLoanAccountCreate")).thenReturn(false);
    Mockito.when(delegateExecution.getVariable("loanAccountNumber")).thenReturn(12345678);
    Mockito.when(delegateExecution.getProcessEngine()).thenReturn(processEngine);
    Mockito.when(delegateExecution.getVariable("caseInstanceId")).thenReturn("123456");
    Mockito.when(processEngine.getCaseService()).thenReturn(caseService);
    CaseExecutionQuery caseExecutionQuery = Mockito.mock(CaseExecutionQuery.class);
    Mockito.when(caseService.createCaseExecutionQuery()).thenReturn(caseExecutionQuery);
    Mockito.when(caseExecutionQuery.caseInstanceId("123456")).thenReturn(caseExecutionQuery);
    Mockito.when(caseExecutionQuery.enabled()).thenReturn(caseExecutionQuery);
    Mockito.when(caseExecutionQuery.list()).thenReturn(Collections.singletonList(createCaseExecution(ACTIVITY_ID_GENERATE_LOAN_DECISION)));


    createLoanAccountTask.execute(delegateExecution);
    Mockito.verify(caseService, Mockito.atLeast(1)).manuallyStartCaseExecution(caseExecution.getId());
  }

  @Test(expected = ProcessTaskException.class)
  public void when_creating_account_account_number_not_null() throws Exception
  {
    Mockito.when(delegateExecution.getVariable("loanAccountNumber")).thenReturn(12345678);
    Mockito.when(delegateExecution.getVariable("isLoanAccountCreate")).thenReturn(true);

    createLoanAccountTask.execute(delegateExecution);
  }

  @Test(expected = NullPointerException.class)
  public void when_creating_account_account_number_null_product_code_null() throws Exception
  {
    Mockito.when(delegateExecution.getVariable("loanAccountNumber")).thenReturn(null);
    Mockito.when(delegateExecution.getVariable("isLoanAccountCreate")).thenReturn(true);

    createLoanAccountTask.execute(delegateExecution);
  }

  @Test(expected = NullPointerException.class)
  public void when_creating_account_product_code_eb50() throws Exception
  {
    Mockito.when(delegateExecution.getVariable("loanAccountNumber")).thenReturn(null);
    Mockito.when(delegateExecution.getVariable("isLoanAccountCreate")).thenReturn(true);
    Mockito.when(delegateExecution.getVariable("loanProduct")).thenReturn("EB50-365-Цалингийн зээл-Иргэн");

    createLoanAccountTask.execute(delegateExecution);
  }

  @Test(expected = NullPointerException.class)
  public void when_creating_account_product_code_different() throws Exception
  {
    Mockito.when(delegateExecution.getVariable("loanAccountNumber")).thenReturn(null);
    Mockito.when(delegateExecution.getVariable("isLoanAccountCreate")).thenReturn(true);
    Mockito.when(delegateExecution.getVariable("loanProduct")).thenReturn("EG50-Something");

    createLoanAccountTask.execute(delegateExecution);
  }

  @Test(expected = NullPointerException.class)
  public void when_creating_account_product_accepted_loan_amount_null() throws Exception
  {
    Mockito.when(delegateExecution.getVariable("loanAccountNumber")).thenReturn(null);
    Mockito.when(delegateExecution.getVariable("isLoanAccountCreate")).thenReturn(true);
    Mockito.when(delegateExecution.getVariable("loanProduct")).thenReturn("EG50-Something");
    Mockito.when(delegateExecution.getVariable("fixedAcceptedLoanAmount")).thenReturn(null);

    createLoanAccountTask.execute(delegateExecution);
  }

  @Test(expected = NullPointerException.class)
  public void when_creating_account_product_accepted_loan_amount_integer() throws Exception
  {
    Mockito.when(delegateExecution.getVariable("loanAccountNumber")).thenReturn(null);
    Mockito.when(delegateExecution.getVariable("isLoanAccountCreate")).thenReturn(true);
    Mockito.when(delegateExecution.getVariable("loanProduct")).thenReturn("EG50-Something");
    Mockito.when(delegateExecution.getVariable("fixedAcceptedLoanAmount")).thenReturn(123);

    createLoanAccountTask.execute(delegateExecution);
  }

  @Test(expected = NullPointerException.class)
  public void when_creating_account_product_accepted_loan_amount_other_data_type() throws Exception
  {
    Mockito.when(delegateExecution.getVariable("loanAccountNumber")).thenReturn(null);
    Mockito.when(delegateExecution.getVariable("isLoanAccountCreate")).thenReturn(true);
    Mockito.when(delegateExecution.getVariable("loanProduct")).thenReturn("EG50-Something");
    Mockito.when(delegateExecution.getVariable("fixedAcceptedLoanAmount")).thenReturn(Long.valueOf(123));

    createLoanAccountTask.execute(delegateExecution);
  }

  @Test(expected = NullPointerException.class)
  public void when_creating_account_interest_rate_null() throws Exception
  {
    Mockito.when(delegateExecution.getVariable("loanAccountNumber")).thenReturn(null);
    Mockito.when(delegateExecution.getVariable("isLoanAccountCreate")).thenReturn(true);
    Mockito.when(delegateExecution.getVariable("loanProduct")).thenReturn("EG50-Something");
    Mockito.when(delegateExecution.getVariable("fixedAcceptedLoanAmount")).thenReturn(Long.valueOf(123));
    Mockito.when(delegateExecution.getVariable("interest_rate")).thenReturn(null);

    createLoanAccountTask.execute(delegateExecution);
  }

  @Test(expected = NullPointerException.class)
  public void when_creating_account_null_user_branch() throws Exception
  {
    Mockito.when(delegateExecution.getVariable("loanAccountNumber")).thenReturn(null);
    Mockito.when(delegateExecution.getVariable("isLoanAccountCreate")).thenReturn(true);
    Mockito.when(delegateExecution.getVariable("loanProduct")).thenReturn("EG50-Something");
    Mockito.when(delegateExecution.getVariable("fixedAcceptedLoanAmount")).thenReturn(Long.valueOf(123));
    Mockito.when(delegateExecution.getVariable("interest_rate")).thenReturn(12.3);

    createLoanAccountTask.execute(delegateExecution);
  }

  @Test(expected = NullPointerException.class)
  public void when_creating_account_user_branch_not_null() throws Exception
  {
    Mockito.when(delegateExecution.getVariable("loanAccountNumber")).thenReturn(null);
    Mockito.when(delegateExecution.getVariable("isLoanAccountCreate")).thenReturn(true);
    Mockito.when(delegateExecution.getVariable("loanProduct")).thenReturn("EG50-Something");
    Mockito.when(delegateExecution.getVariable("fixedAcceptedLoanAmount")).thenReturn(Long.valueOf(123));
    Mockito.when(delegateExecution.getVariable("interest_rate")).thenReturn(12.3);
    getBranchMock();

    createLoanAccountTask.execute(delegateExecution);
  }

  @Test(expected = NullPointerException.class)
  public void when_creating_account_score_null() throws Exception
  {
    Mockito.when(delegateExecution.getVariable("loanAccountNumber")).thenReturn(null);
    Mockito.when(delegateExecution.getVariable("isLoanAccountCreate")).thenReturn(true);
    Mockito.when(delegateExecution.getVariable("loanProduct")).thenReturn("EG50-Something");
    Mockito.when(delegateExecution.getVariable("fixedAcceptedLoanAmount")).thenReturn(Long.valueOf(123));
    Mockito.when(delegateExecution.getVariable("interest_rate")).thenReturn(12.3);
    getBranchMock();

    createLoanAccountTask.execute(delegateExecution);
  }

  @Test(expected = UseCaseException.class)
  public void when_creating_account_score_integer_type() throws Exception
  {
    Mockito.when(delegateExecution.getVariable("loanAccountNumber")).thenReturn(null);
    Mockito.when(delegateExecution.getVariable("isLoanAccountCreate")).thenReturn(true);
    Mockito.when(delegateExecution.getVariable("loanProduct")).thenReturn("EG50-Something");
    Mockito.when(delegateExecution.getVariable("fixedAcceptedLoanAmount")).thenReturn(Long.valueOf(123));
    Mockito.when(delegateExecution.getVariable("interest_rate")).thenReturn(12.3);
    getBranchMock();
    Mockito.when(delegateExecution.getVariable("score")).thenReturn(300);

    createLoanAccountTask.execute(delegateExecution);
  }

  @Test(expected = UseCaseException.class)
  public void when_creating_account_score_string_type() throws Exception
  {
    Mockito.when(delegateExecution.getVariable("loanAccountNumber")).thenReturn(null);
    Mockito.when(delegateExecution.getVariable("isLoanAccountCreate")).thenReturn(true);
    Mockito.when(delegateExecution.getVariable("loanProduct")).thenReturn("EG50-Something");
    Mockito.when(delegateExecution.getVariable("fixedAcceptedLoanAmount")).thenReturn(Long.valueOf(123));
    Mockito.when(delegateExecution.getVariable("interest_rate")).thenReturn(12.3);
    getBranchMock();
    Mockito.when(delegateExecution.getVariable("score")).thenReturn("300");

    createLoanAccountTask.execute(delegateExecution);
  }

  @Test(expected = UseCaseException.class)
  public void when_creating_account_score_different_type() throws Exception
  {
    Mockito.when(delegateExecution.getVariable("loanAccountNumber")).thenReturn(null);
    Mockito.when(delegateExecution.getVariable("isLoanAccountCreate")).thenReturn(true);
    Mockito.when(delegateExecution.getVariable("loanProduct")).thenReturn("EG50-Something");
    Mockito.when(delegateExecution.getVariable("fixedAcceptedLoanAmount")).thenReturn(Long.valueOf(123));
    Mockito.when(delegateExecution.getVariable("interest_rate")).thenReturn(12.3);
    getBranchMock();
    Mockito.when(delegateExecution.getVariable("score")).thenReturn(12.3);

    createLoanAccountTask.execute(delegateExecution);
  }

  @Test(expected = NullPointerException.class)
  public void when_creating_account_score_not_null_subtype() throws Exception
  {
    Mockito.when(delegateExecution.getVariable("loanAccountNumber")).thenReturn(null);
    Mockito.when(delegateExecution.getVariable("isLoanAccountCreate")).thenReturn(true);
    Mockito.when(delegateExecution.getVariable("loanProduct")).thenReturn("EG50-Something");
    Mockito.when(delegateExecution.getVariable("fixedAcceptedLoanAmount")).thenReturn(Long.valueOf(123));
    Mockito.when(delegateExecution.getVariable("interest_rate")).thenReturn(12.3);
    getBranchMock();
    Mockito.when(delegateExecution.getVariable("score")).thenReturn(12.3);
    Mockito.when(coreBankingService.getUDFields("EG50")).thenReturn(Collections.singletonMap("SUBTYPE", createMockUDField()));

    createLoanAccountTask.execute(delegateExecution);
  }

  private CaseExecution createCaseExecution(String activityID)
  {
    CaseExecutionEntity caseExecutionEntity = new CaseExecutionEntity();
    caseExecutionEntity.setActivity(new CmmnActivity(activityID, new CaseDefinitionEntity()));

    return caseExecutionEntity;
  }

  private void getBranchMock() throws AimRepositoryException
  {
    Mockito.when(authenticationService.getCurrentUserId()).thenReturn("Erin");
    Mockito.when(membershipRepository.listAllByUserId(TenantId.valueOf("xac"), UserId.valueOf("Erin"))).thenReturn(Collections.singletonList(new Membership(
        MembershipId.valueOf("1"), UserId.valueOf("Erin"), GroupId.valueOf("108"), RoleId.valueOf("Role"))));
  }

  private UDField createMockUDField()
  {
    UDField udFieldToReturn = new UDField(UDFieldId.valueOf("SUBTYPE"), "desc", "T", "5",
        false, "N", "ST00", true, false);

    return udFieldToReturn;
  }

}
