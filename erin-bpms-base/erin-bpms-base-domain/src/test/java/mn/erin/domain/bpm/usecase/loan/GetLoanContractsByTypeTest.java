package mn.erin.domain.bpm.usecase.loan;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.document.Document;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.service.DocumentService;

import static mn.erin.domain.bpm.BpmModuleConstants.PRODUCT_CODE;

/**
 * @author Bilguunbor
 */

public class GetLoanContractsByTypeTest
{
  private AuthenticationService authenticationService;
  private CaseService caseService;
  private DocumentService documentService;
  private GetLoanContractsByType useCase;
  private GetLoanContractsByTypeInput input;

  private final String instanceId = "instanceId";
  private final String documentType = "documentType";
  private final Map<String, Object> parameters = new HashMap<>();

  @Before
  public void setUp()
  {
    authenticationService = Mockito.mock(AuthenticationService.class);
    caseService = Mockito.mock(CaseService.class);
    documentService = Mockito.mock(DocumentService.class);
    useCase = new GetLoanContractsByType(documentService, caseService, authenticationService);
    input = new GetLoanContractsByTypeInput(instanceId, documentType, parameters);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_is_null() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test(expected = NullPointerException.class)
  public void when_authentication_service_is_null()
  {
    new GetLoanContractsByType(documentService, caseService, null);
  }

  @Test(expected = NullPointerException.class)
  public void when_case_service_is_null()
  {
    new GetLoanContractsByType(documentService, null, authenticationService);
  }

  @Test(expected = NullPointerException.class)
  public void when_document_service_is_null()
  {
    new GetLoanContractsByType(null, caseService, authenticationService);
  }

  @Test(expected = UseCaseException.class)
  public void when_parameter_is_null() throws UseCaseException
  {
    useCase.execute(new GetLoanContractsByTypeInput(instanceId, documentType, null));
  }

  @Test(expected = UseCaseException.class)
  public void when_document_type_is_null() throws UseCaseException
  {
    useCase.execute(new GetLoanContractsByTypeInput(instanceId, null, parameters));
  }

  @Test(expected = UseCaseException.class)
  public void when_instance_id_is_null() throws UseCaseException
  {
    useCase.execute(new GetLoanContractsByTypeInput(null, documentType, parameters));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void when_throws_service_exception() throws BpmServiceException, UseCaseException, ParseException
  {
    Map<String, String> exampleMap = new HashMap<>();
    Mockito.when(documentService.downloadDocumentByType(exampleMap, "documentType", "instanceId")).thenThrow(BpmServiceException.class);

    useCase.execute(input);
  }

  @Ignore
  @Test
  public void when_small_and_medium_enterprise_contract_works_correctly() throws UseCaseException
  {
    List<Document> output = useCase.execute(new GetLoanContractsByTypeInput(instanceId, "smallAndMediumEnterpriseLoanContract", parameters));
    Document document = output.get(0);

    Assert.assertEquals("Жижиг, дунд бизнесийн зээлийн гэрээ - null", document.getName());
  }

  @Ignore
  @Test
  public void when_sme_contract_works_correctly() throws UseCaseException
  {
    List<Document> output = useCase.execute(new GetLoanContractsByTypeInput(instanceId, "smeLoanContract", parameters));
    Document document = output.get(0);

    Assert.assertEquals("SME бизнесийн зээлийн гэрээ - null", document.getName());
  }
}
