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

/**
 * @Author Sukhbat
 */

public class DownloadLoanReportAsBase64Test
{
  private static final String CASE_INSTANCE_ID = "caseInstanceId";
  private DocumentService documentService;
  private CaseService caseService;
  private DownloadLoanReportAsBase64 useCase;

  @Before
  public void setUp()
  {
    documentService = Mockito.mock(DocumentService.class);
    caseService = Mockito.mock(CaseService.class);
    useCase = new DownloadLoanReportAsBase64(documentService, caseService);
  }

  @Test(expected = NullPointerException.class)
  public void when_document_service_is_null()
  {
    new DownloadLoanReportAsBase64(null, caseService);
  }

  @Test(expected = NullPointerException.class)
  public void when_case_service_is_null()
  {
    new DownloadLoanReportAsBase64(documentService, null);
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_use_case_exception() throws UseCaseException, BpmServiceException
  {
    Mockito.when(caseService.getVariables("123")).thenReturn(generateVariableListWithNullAccountNumber());
    useCase.execute("123");
  }

  @Test
  public void when_works_correctly() throws UseCaseException, BpmServiceException
  {
    Mockito.when(caseService.getVariables("123")).thenReturn(generateVariableList());
    useCase.execute("123");
    Mockito.verify(documentService, Mockito.atLeast(1)).downloadLoanReportAsBase64(Mockito.anyString());
  }

  private List<Variable> generateVariableListWithNullAccountNumber()
  {
    List<Variable> variableList = new ArrayList<>();
    Variable variable = new Variable(VariableId.valueOf("loanAccountNumber"), null);
    variableList.add(variable);

    return variableList;
  }

  private List<Variable> generateVariableList()
  {
    List<Variable> variableList = new ArrayList<>();
    Variable variable = new Variable(VariableId.valueOf("loanAccountNumber"), "123");
    variableList.add(variable);

    return variableList;
  }
}
