package mn.erin.domain.bpm.usecase.branch_banking.billing;

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
import mn.erin.domain.bpm.service.BranchBankingDocumentService;
import mn.erin.domain.bpm.service.CaseService;

import static mn.erin.domain.bpm.BpmPublisherParamConstants.P_DATE;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.P_TRAN;

/**
 * @author Lkhagvadorj.A
 **/

public class DownloadPublisherDocumentByTypeTest
{
  private CaseService caseService;
  private BranchBankingDocumentService branchBankingDocumentService;
  private DownloadPublisherDocumentsByType useCase;
  private AuthenticationService authenticationService;

  @Before
  public void setUp()
  {
    caseService = Mockito.mock(CaseService.class);
    branchBankingDocumentService = Mockito.mock(BranchBankingDocumentService.class);
    authenticationService = Mockito.mock(AuthenticationService.class);

    useCase = new DownloadPublisherDocumentsByType(caseService, branchBankingDocumentService, authenticationService);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_null() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_instance_id_is_blank() throws UseCaseException
  {
    useCase.execute(new DownloadPublisherDocumentsByTypeInput(null, ""));
  }

  @Test(expected = UseCaseException.class)
  public void when_input_document_type_is_blank() throws UseCaseException
  {
    useCase.execute(new DownloadPublisherDocumentsByTypeInput("test", ""));
  }

  @Ignore
  @Test
  public void when_download_document_from_bip_with_type_tax_pay() throws BpmServiceException, UseCaseException
  {
    Map<String, String> documentParameter = getDocumentParameter();
    Mockito.when(branchBankingDocumentService.downloadDocumentByType(documentParameter, "taxPay", "123")).thenReturn("testSource");
    List<Document> document = useCase.execute(new DownloadPublisherDocumentsByTypeInput("test", "taxPay"));
    Assert.assertEquals("Татварын гүйлгээний баримт - XB0003848", document.get(0).getName());
  }

  private Map<String, String> getDocumentParameter()
  {
    Map<String, String> documentParameter = new HashMap<>();
    documentParameter.put(P_TRAN, "testTransactionNumber");
    documentParameter.put(P_DATE, "testDate");

    return documentParameter;
  }
}