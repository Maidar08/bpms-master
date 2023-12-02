package mn.erin.domain.bpm.usecase.document_info;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.BpmModulePermission;
import mn.erin.domain.bpm.model.document.DocumentInfo;
import mn.erin.domain.bpm.model.document.DocumentInfoId;
import mn.erin.domain.bpm.repository.DocumentInfoRepository;

public class GetBasicDocumentInfosTest
{
  private static final Permission permission = new BpmModulePermission("GetBasicDocumentInfos");

  private DocumentInfoRepository documentInfoRepository;
  private AuthenticationService authenticationService;
  private AuthorizationService authorizationService;
  private GetBasicDocumentInfos useCase;

  @Before
  public void setUp()
  {
    documentInfoRepository = Mockito.mock(DocumentInfoRepository.class);
    authenticationService = Mockito.mock(AuthenticationService.class);
    authorizationService = Mockito.mock(AuthorizationService.class);
    useCase = new GetBasicDocumentInfos(authenticationService, authorizationService, documentInfoRepository);
  }

  @Test(expected = NullPointerException.class)
  public void when_document_info_repository_is_null()
  {
    useCase = new GetBasicDocumentInfos(authenticationService, authorizationService, null);
  }

  @Test
  public void when_permission_granted()
  {
    Assert.assertEquals(permission, useCase.getPermission());
  }

  @Test
  public void when_document_infos_found() throws UseCaseException
  {
    Void input = null;

    Collection<DocumentInfo> documentInfos = generateDocumentInfoCollection();
    Mockito.when(documentInfoRepository.findBasicDocuments()).thenReturn(documentInfos);

    GetBasicDocumentInfosOutput output = useCase.executeImpl(input);
    Assert.assertNotNull(output);

    Collection<DocumentInfo> outputCollection = output.getDocumentInfos();
    Assert.assertEquals(documentInfos, outputCollection);
  }

  private Collection<DocumentInfo> generateDocumentInfoCollection()
  {
    DocumentInfo documentInfo = new DocumentInfo(DocumentInfoId.valueOf("123"), "123", "name", "type");
    Collection<DocumentInfo> documentInfos = new ArrayList<>();
    documentInfos.add(documentInfo);

    return documentInfos;
  }
}
