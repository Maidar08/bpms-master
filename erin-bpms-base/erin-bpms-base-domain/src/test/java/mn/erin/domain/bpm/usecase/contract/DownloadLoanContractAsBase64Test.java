package mn.erin.domain.bpm.usecase.contract;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.variable.Variable;
import mn.erin.domain.bpm.model.variable.VariableId;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.service.DocumentService;

import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.REPAYMENT_EQUAL_PRINCIPLE_PAYMENT;
import static mn.erin.domain.bpm.BpmModuleConstants.REPAYMENT_TYPE_ID;

/**
 * @Author Sukhbat
 */

public class DownloadLoanContractAsBase64Test
{
  private static final String CASE_INSTANCE_ID = "caseInstanceId";
  private DocumentService documentService;
  private CaseService caseService;
  private Environment environment;
  private DownloadLoanContractAsBase64 useCase;

  @Before
  public void setUp()
  {
    documentService = Mockito.mock(DocumentService.class);
    caseService = Mockito.mock(CaseService.class);
    environment = Mockito.mock(Environment.class);
    useCase = new DownloadLoanContractAsBase64(documentService, caseService, environment);
  }

  @Test(expected = NullPointerException.class)
  public void when_document_service_is_null()
  {
    new DownloadLoanContractAsBase64(null, caseService, environment);
  }

  @Test(expected = NullPointerException.class)
  public void when_case_service_is_null()
  {
    new DownloadLoanContractAsBase64(documentService, null, environment);
  }

  // TODO: improve test
  @Ignore
  @Test(expected = UseCaseException.class)
  public void when_throws_use_Case_exception() throws UseCaseException, BpmServiceException
  {
    Mockito.when(caseService.getVariables("123")).thenReturn(generateVariableListWithNullAccountNumber());
    useCase.execute(new DownloadLoanContractAsBase64Input("123123"));
  }

  // TODO: improve test
  @Ignore
  @Test
  public void when_works_correctly() throws UseCaseException, BpmServiceException
  {
    Mockito.when(caseService.getVariables("123")).thenReturn(generateVariableList());
    useCase.execute(new DownloadLoanContractAsBase64Input("123"));
    Mockito.verify(documentService, Mockito.atLeast(1)).downloadContractAsBase64(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
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

    return variableList;
  }
}
