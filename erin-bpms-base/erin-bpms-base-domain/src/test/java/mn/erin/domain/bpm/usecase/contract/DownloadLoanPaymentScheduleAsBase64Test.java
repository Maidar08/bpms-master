package mn.erin.domain.bpm.usecase.contract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.variable.Variable;
import mn.erin.domain.bpm.model.variable.VariableId;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.service.DocumentService;

import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.REPAYMENT_EQUAL_PRINCIPLE_PAYMENT;
import static mn.erin.domain.bpm.BpmModuleConstants.REPAYMENT_TYPE_ID;

/**
 * @Author Sukhbat
 */

public class DownloadLoanPaymentScheduleAsBase64Test
{
  private DocumentService documentService;
  private CaseService caseService;
  private AuthenticationService authenticationService;
  private MembershipRepository membershipRepository;
  private DownloadLoanPaymentScheduleAsBase64 useCase;

  @Before
  public void setUp()
  {
    documentService = Mockito.mock(DocumentService.class);
    caseService = Mockito.mock(CaseService.class);
    authenticationService = Mockito.mock(AuthenticationService.class);
    membershipRepository = Mockito.mock(MembershipRepository.class);
    useCase = new DownloadLoanPaymentScheduleAsBase64(documentService, caseService, authenticationService, membershipRepository);
  }

  @Test(expected = NullPointerException.class)
  public void when_document_service_is_null()
  {
    new DownloadLoanPaymentScheduleAsBase64(null, caseService, authenticationService, membershipRepository);
  }

  @Test(expected = NullPointerException.class)
  public void when_case_service_is_null()
  {
    new DownloadLoanPaymentScheduleAsBase64(documentService, null, authenticationService, membershipRepository);
  }

  @Test(expected = NullPointerException.class)
  public void when_authentication_service_is_null()
  {
    new DownloadLoanPaymentScheduleAsBase64(documentService, caseService, null, membershipRepository);
  }

  @Test(expected = NullPointerException.class)
  public void when_membership_repository_is_null()
  {
    new DownloadLoanPaymentScheduleAsBase64(documentService, caseService, authenticationService, null);
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_use_case_exception() throws UseCaseException, BpmServiceException
  {
    Mockito.when(caseService.getVariables("123")).thenReturn(generateVariableListWithNullAccountNumber());
    Map<String, String> input = new HashMap<>();
    input.put(CASE_INSTANCE_ID,"123");
    useCase.execute(input);
  }
  @Ignore
  @Test
  public void when_works_correctly() throws UseCaseException, BpmServiceException
  {
    Map<String, String> paymentScheduleInfo = new HashMap<>();
    paymentScheduleInfo.put("P_GRACE", "Y");
    Mockito.when(caseService.getVariables("123")).thenReturn(generateVariableList());
    Mockito.when(documentService.downloadPaymentScheduleAsBase64("123", "a", paymentScheduleInfo, "type")).thenReturn("response");
    Map<String, String> input = new HashMap<>();
    input.put(CASE_INSTANCE_ID,"123");
    useCase.execute(input);
    Mockito.verify(documentService, Mockito.atLeast(1)).downloadPaymentScheduleAsBase64("123", "a", paymentScheduleInfo, "type");
  }

  private List<Variable> generateVariableListWithNullAccountNumber()
  {
    List<Variable> variableList = new ArrayList<>();
    Variable variable = new Variable(VariableId.valueOf(LOAN_ACCOUNT_NUMBER), null);
    variableList.add(variable);
    variableList.add(new Variable(VariableId.valueOf(REPAYMENT_TYPE_ID), null));

    return variableList;
  }

  private List<Variable> generateVariableList()
  {
    List<Variable> variableList = new ArrayList<>();
    Variable variable = new Variable(VariableId.valueOf(LOAN_ACCOUNT_NUMBER), "123");
    variableList.add(variable);
    variableList.add(new Variable(VariableId.valueOf(REPAYMENT_TYPE_ID), REPAYMENT_EQUAL_PRINCIPLE_PAYMENT));
    variableList.add(new Variable(VariableId.valueOf("123")));
    return variableList;
  }
}
