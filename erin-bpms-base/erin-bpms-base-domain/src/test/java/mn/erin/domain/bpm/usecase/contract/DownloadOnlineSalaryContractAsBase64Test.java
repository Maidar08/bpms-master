package mn.erin.domain.bpm.usecase.contract;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.variable.Variable;
import mn.erin.domain.bpm.model.variable.VariableId;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.service.DocumentService;

import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;

/**
 * @Author Sukhbat
 */

public class DownloadOnlineSalaryContractAsBase64Test
{
  private DocumentService documentService;
  private CaseService caseService;
  private DownloadOnlineSalaryContractAsBase64 useCase;

  @Before
  public void setUp()
  {
    documentService = Mockito.mock(DocumentService.class);
    caseService = Mockito.mock(CaseService.class);
    useCase = new DownloadOnlineSalaryContractAsBase64(documentService, caseService);
  }

  @Test(expected = NullPointerException.class)
  public void when_document_service_is_null()
  {
    new DownloadOnlineSalaryContractAsBase64(null, caseService);
  }

  @Test(expected = NullPointerException.class)
  public void when_case_service_is_null()
  {
    new DownloadOnlineSalaryContractAsBase64(documentService, null);
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_use_case_exception() throws UseCaseException, BpmServiceException
  {
    Mockito.when(caseService.getVariables("123")).thenReturn(generateVariableListWithNullAccountNumber());
    useCase.execute(new DownloadLoanContractAsBase64Input("123"));
  }

  @Test
  public void when_works_correctly() throws UseCaseException, BpmServiceException
  {
    Mockito.when(caseService.getVariables("123")).thenReturn(generateVariableList());
    useCase.execute(new DownloadLoanContractAsBase64Input("123"));
    Mockito.verify(documentService, Mockito.atLeast(1)).downloadOnlineSalaryContractAsBase64(Mockito.anyString(), Mockito.anyString());
  }

  private List<Variable> generateVariableListWithNullAccountNumber()
  {
    List<Variable> variableList = new ArrayList<>();
    Variable variable = new Variable(VariableId.valueOf(LOAN_ACCOUNT_NUMBER), null);
    variableList.add(variable);

    return variableList;
  }

  private List<Variable> generateVariableList()
  {
    List<Variable> variableList = new ArrayList<>();
    Variable variable = new Variable(VariableId.valueOf(LOAN_ACCOUNT_NUMBER), "123");
    variableList.add(variable);
    variableList.add(new Variable(VariableId.valueOf(PROCESS_REQUEST_ID), "123"));

    return variableList;
  }
}


