package mn.erin.domain.bpm.usecase.document;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.membership.MembershipId;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.role.RoleId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.BpmModulePermission;
import mn.erin.domain.bpm.model.document.DocumentInfo;
import mn.erin.domain.bpm.model.document.DocumentInfoId;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.DocumentInfoRepository;
import mn.erin.domain.bpm.repository.DocumentRepository;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.DocumentService;

/**
 * @author Bilguunbor
 */
public class UploadDocumentsTest
{
  private static final String CASE_INSTANCE_ID = "caseInstanceId";
  private static final String CATEGORY = "category";
  private static final String SUB_CATEGORY = "subCategory";
  private static final String SOURCE = "source";
  private static final Permission permission = new BpmModulePermission("UploadDocuments");

  private UploadDocuments useCase;
  private UploadDocumentsInput input;
  private AuthorizationService authorizationService;
  private AuthenticationService authenticationService;
  private DocumentService documentService;
  private DocumentInfoRepository documentInfoRepository;
  private DocumentRepository documentRepository;
  private MembershipRepository membershipRepository;

  List<UploadFile> uploadFileList = generateUploadFileList();
  List<Membership> memberships = generateMemberShipList();

  @Before
  public void setUp() throws BpmServiceException
  {
    authenticationService = Mockito.mock(AuthenticationService.class);
    authorizationService = Mockito.mock(AuthorizationService.class);
    documentService = Mockito.mock(DocumentService.class);
    documentRepository = Mockito.mock(DocumentRepository.class);
    documentInfoRepository = Mockito.mock(DocumentInfoRepository.class);
    membershipRepository = Mockito.mock(MembershipRepository.class);
    useCase = new UploadDocuments(authenticationService, authorizationService, documentService, membershipRepository, documentInfoRepository,
        documentRepository);
    input = new UploadDocumentsInput(CASE_INSTANCE_ID, CATEGORY, SUB_CATEGORY, SOURCE, Collections.emptyMap(), uploadFileList);
  }

  @Test(expected = NullPointerException.class)
  public void when_document_service_is_null()
  {
    new UploadDocuments(authenticationService, authorizationService, null, membershipRepository, documentInfoRepository,
        documentRepository);
  }

  @Test(expected = NullPointerException.class)
  public void when_membership_repository_is_null()
  {
    new UploadDocuments(authenticationService, authorizationService, documentService, null, documentInfoRepository,
        documentRepository);
  }

  @Test(expected = NullPointerException.class)
  public void when_document_info_repository_is_null()
  {
    new UploadDocuments(authenticationService, authorizationService, documentService, membershipRepository, null,
        documentRepository);
  }

  @Test(expected = NullPointerException.class)
  public void when_document_repository_is_null()
  {
    new UploadDocuments(authenticationService, authorizationService, documentService, membershipRepository, documentInfoRepository,
        null);
  }

  @Test
  public void when_permission_granted()
  {
    Assert.assertEquals(permission, useCase.getPermission());
  }

  @Test(expected = UseCaseException.class)
  public void when_current_user_id_is_null() throws UseCaseException
  {
    Mockito.when(authenticationService.getCurrentUserId()).thenReturn("123");

    Mockito.when(authenticationService.getCurrentUserId()).thenReturn(null);

    useCase.executeImpl(input);
  }

  @Test(expected = UseCaseException.class)
  public void when_base_64_over_sized() throws AimRepositoryException, UseCaseException
  {
    setupMocks();
    useCase.executeImpl(new UploadDocumentsInput(CASE_INSTANCE_ID, CATEGORY, SUB_CATEGORY, SOURCE, Collections.emptyMap(), generateUploadFileListWithLargeBase64Document()));
  }

  @Ignore
  @Test(expected = UseCaseException.class)
  public void when_document_service_upload_document_result_is_null() throws AimRepositoryException, UseCaseException, BpmServiceException
  {
    setupMocks();
    Mockito.when(documentService
        .uploadDocument(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
            Collections.emptyMap())).thenReturn(null);

    useCase.executeImpl(input);
  }
  @Ignore
  @Test(expected = UseCaseException.class)
  public void when_document_service_get_document_reference_is_null() throws AimRepositoryException, BpmServiceException, UseCaseException
  {
    setupMocks();
    Mockito.when(documentService
        .uploadDocument(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
          dummyMap())).thenReturn("OK");
    Mockito.when(documentService.getDocumentReference(Mockito.anyString(), Mockito.anyString())).thenReturn(null);

    useCase.executeImpl(input);
  }
 @Ignore
  @Test(expected = UseCaseException.class)
  public void when_document_repository_document_is_null() throws BpmServiceException, AimRepositoryException, UseCaseException, BpmRepositoryException
  {
    setupMocks();
    Mockito.when(documentService
        .uploadDocument(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
            dummyMap())).thenReturn("OK");
    Mockito.when(documentService.getDocumentReference(Mockito.anyString(), Mockito.anyString())).thenReturn("reference");
    Mockito.when(documentRepository
        .create(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
            Mockito.anyString(), Mockito.anyString())).thenReturn(null);

    useCase.executeImpl(input);
  }

  private List<UploadFile> generateUploadFileList()
  {
    List<UploadFile> uploadFiles = new ArrayList<>();

    UploadFile uploadFile = new UploadFile("name", "base64");
    uploadFiles.add(uploadFile);

    return uploadFiles;
  }

  private List<UploadFile> generateUploadFileListWithLargeBase64Document()
  {
    List<UploadFile> uploadFiles = new ArrayList<>();

    UploadFile uploadFile = new UploadFile("name", generateLargeSizeBase64());
    uploadFiles.add(uploadFile);

    return uploadFiles;
  }

  private List<Membership> generateMemberShipList()
  {
    List<Membership> memberships = new ArrayList<>();
    Membership membership = new Membership(MembershipId.valueOf("123"), UserId.valueOf("123"), GroupId.valueOf("123"), RoleId.valueOf("123"));
    memberships.add(membership);

    return memberships;
  }

  private String generateLargeSizeBase64()
  {
    Random random = ThreadLocalRandom.current();
    byte[] randomBytes = new byte[9200001];
    random.nextBytes(randomBytes);

    return Base64.getUrlEncoder().encodeToString(randomBytes);
  }

  private void setupMocks() throws AimRepositoryException
  {
    Mockito.when(authenticationService.getCurrentUserId()).thenReturn("123");
    Mockito.when(membershipRepository.listAllByUserId(TenantId.valueOf("xac"), UserId.valueOf("123"))).thenReturn(memberships);
    Mockito.when(documentInfoRepository.findByName(input.getCategory())).thenReturn(new DocumentInfo(DocumentInfoId.valueOf("123"), "123", "category", "type"));
  }
  private Map<String, String> dummyMap()
  {

    Map<String, String> map = new HashMap<String, String>()
    {{
      put("key1", "value1");
      put("key2", "value2");
    }};
    return map;
  }
}
