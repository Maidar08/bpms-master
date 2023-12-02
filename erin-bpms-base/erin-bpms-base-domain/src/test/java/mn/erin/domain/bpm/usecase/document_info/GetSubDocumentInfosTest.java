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

public class GetSubDocumentInfosTest
{
  private static final Permission permission = new BpmModulePermission("GetSubDocumentInfos");
  private static final String PARENT_ID = "parentId";

  private GetSubDocumentInfos useCase;
  private DocumentInfoRepository documentInfoRepository;
  private AuthenticationService authenticationService;
  private AuthorizationService authorizationService;

  @Before
  public void setUp()
  {
    documentInfoRepository = Mockito.mock(DocumentInfoRepository.class);
    authenticationService = Mockito.mock(AuthenticationService.class);
    authorizationService = Mockito.mock(AuthorizationService.class);
    useCase = new GetSubDocumentInfos(authenticationService, authorizationService, documentInfoRepository);
  }

  @Test(expected = NullPointerException.class)
  public void when_document_info_repository_is_null()
  {
    new GetSubDocumentInfos(authenticationService, authorizationService, null);
  }

  @Test
  public void when_permission_granted()
  {
    Assert.assertEquals(permission, useCase.getPermission());
  }

  @Test(expected = UseCaseException.class)
  public void when_parent_id_is_null() throws UseCaseException
  {
    useCase.executeImpl(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_parent_id_is_blank() throws UseCaseException
  {
    useCase.executeImpl(" ");
  }

  @Test
  public void when_sub_document_info_is_found() throws UseCaseException
  {
    Collection<DocumentInfo> documentInfos = generateDocumentInfoCollection();
    Mockito.when(documentInfoRepository.findByParentId(PARENT_ID)).thenReturn(documentInfos);

    GetSubDocumentInfosOutput output = useCase.executeImpl(PARENT_ID);
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
