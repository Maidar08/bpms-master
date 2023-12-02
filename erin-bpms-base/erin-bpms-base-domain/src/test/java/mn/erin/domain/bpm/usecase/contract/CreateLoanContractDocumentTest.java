package mn.erin.domain.bpm.usecase.contract;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.variable.Variable;
import mn.erin.domain.bpm.model.variable.VariableId;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.DocumentRepository;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CaseService;

public class CreateLoanContractDocumentTest
{
  private static final String CASE_INSTANCE_ID = "caseInstanceId";

  private AuthenticationService authenticationService;
  private AuthorizationService authorizationService;
  private CaseService caseService;
  private DocumentRepository documentRepository;
  private CreateLoanContractDocument useCase;

  @Before
  public void setUp()
  {
    authenticationService = Mockito.mock(AuthenticationService.class);
    authorizationService = Mockito.mock(AuthorizationService.class);
    caseService = Mockito.mock(CaseService.class);
    documentRepository = Mockito.mock(DocumentRepository.class);
    useCase = new CreateLoanContractDocument(authenticationService, authorizationService, caseService, documentRepository);
  }

  @Test(expected = NullPointerException.class)
  public void when_authentication_service_is_null()
  {
    new CreateLoanContractDocument(null, authorizationService, caseService, documentRepository);
  }

  @Test(expected = NullPointerException.class)
  public void when_authorization_service_is_null()
  {
    new CreateLoanContractDocument(authenticationService, null, caseService, documentRepository);
  }

  @Test(expected = NullPointerException.class)
  public void when_case_service_is_null()
  {
    new CreateLoanContractDocument(authenticationService, authorizationService, null, documentRepository);
  }

  @Test(expected = NullPointerException.class)
  public void when_document_repository_is_null()
  {
    new CreateLoanContractDocument(authenticationService, authorizationService, caseService, null);
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_use_case_exception() throws UseCaseException, BpmServiceException, BpmRepositoryException
  {
    Mockito.when(authenticationService.getCurrentUserId()).thenReturn("branchSpecialist");
    Mockito.when(caseService.getVariables(CASE_INSTANCE_ID)).thenReturn(generateVariableList());
    Mockito.when(documentRepository
            .create(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
        .thenThrow(BpmRepositoryException.class);
    useCase.execute(CASE_INSTANCE_ID);
  }

  @Test
  public void when_case_instance_id_is_null() throws UseCaseException
  {
    Assert.assertTrue(useCase.execute(null));
  }

  private List<Variable> generateVariableList()
  {
    List<Variable> variableList = new ArrayList<>();
    Variable variable = new Variable(VariableId.valueOf("processRequestId"), "123");
    variableList.add(variable);

    return variableList;
  }
}
