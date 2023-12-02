/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.document;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.BpmModulePermission;
import mn.erin.domain.bpm.model.document.Document;
import mn.erin.domain.bpm.model.document.DocumentInfo;
import mn.erin.domain.bpm.model.document.DocumentInfoId;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.DocumentInfoRepository;
import mn.erin.domain.bpm.repository.DocumentRepository;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.DocumentService;

/**
 * @author Tamir
 */
public class UploadDocuments extends AuthorizedUseCase<UploadDocumentsInput, Boolean>
{
  private static final Logger LOG = LoggerFactory.getLogger(UploadDocuments.class);
  private static final Permission permission = new BpmModulePermission("UploadDocuments");

  private static final String EMPTY = "";
  private static final String OK_RESULT = "OK";

  private static final String BASE_64_LETTER = "base64";
  private static final String REGEX_BASE_64_PREFIX = "(^data:(\\w.*));base64,";

  private final DocumentService documentService;
  private final MembershipRepository membershipRepository;
  private final DocumentInfoRepository documentInfoRepository;
  private final DocumentRepository documentRepository;

  public UploadDocuments(AuthenticationService authenticationService, AuthorizationService authorizationService, DocumentService documentService,
      MembershipRepository membershipRepository, DocumentInfoRepository documentInfoRepository, DocumentRepository documentRepository)
  {
    super(authenticationService, authorizationService);

    this.documentService = Objects.requireNonNull(documentService, "Document service is required!");
    this.membershipRepository = Objects.requireNonNull(membershipRepository, "Membership repository is required!");

    this.documentInfoRepository = Objects.requireNonNull(documentInfoRepository, "Document info repository is required!");
    this.documentRepository = Objects.requireNonNull(documentRepository, "Document repository is required!");
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected Boolean executeImpl(UploadDocumentsInput input) throws UseCaseException
  {
    String caseInstanceId = input.getCaseInstanceId();
    String category = input.getCategory();
    String subCategory = input.getSubCategory();
    String source = input.getSource();
    Map<String, String> parameters = input.getParameters();
    List<UploadFile> uploadFiles = input.getUploadFiles();

  String currentUserId = authenticationService.getCurrentUserId();

    LOG.info("########### Current USER = [{}] uploads file to LDMS.", currentUserId);
    String groupId = getGroupId();

    if (null == groupId)
    {
      String errorCode = "DMS024";
      throw new UseCaseException(errorCode, "Group id does not exist with current user.");
    }

    DocumentInfoId documentInfoId = getDocumentInfoId(category);

    for (UploadFile uploadFile : uploadFiles)
    {
      String fileName = uploadFile.getName();

      UploadDocumentUtil.validateFileExtension(fileName);

      String documentAsBase64 = uploadFile.getContentAsBase64();

      boolean exceededMaxSize = exceedMaxFileSize(documentAsBase64);
      if (exceededMaxSize)
      {
        String errorCode = "DMS028";
        throw new UseCaseException(errorCode, "Maximum File Size Exceeded Error");
      }

      if (documentAsBase64.contains(BASE_64_LETTER))
      {
        documentAsBase64 = documentAsBase64.replaceFirst(REGEX_BASE_64_PREFIX, EMPTY);
      }

      String documentId = String.valueOf(System.currentTimeMillis());

      try
      {
        String result = documentService.uploadDocument(caseInstanceId, documentId, category, subCategory, groupId, fileName, documentAsBase64, parameters);

        if (null == result)
        {
          String errorCode = "DMS001";
          throw new UseCaseException(errorCode, "Not found result from LDMS,  with user id =" + currentUserId);
        }

        if (result.equalsIgnoreCase(OK_RESULT))
        {
          String documentReference = documentService.getDocumentReference(documentId, currentUserId);

          if (null == documentReference)
          {
            String errorCode = "DMS002";
            throw new UseCaseException(errorCode, "########### Received null document reference from DMS, document =" + fileName);
          }

          Document document = documentRepository
              .create(documentId, documentInfoId.getId(), caseInstanceId, fileName, category, subCategory, documentReference, source);

          if (null == document)
          {
            throw new UseCaseException("########## Could not persist document = " + fileName);
          }
        }
      }
      catch (BpmServiceException e)
      {
        throw new UseCaseException(e.getCode(), e.getMessage(), e);
      }
      catch (BpmRepositoryException e)
      {
        throw new UseCaseException(e.getMessage(), e);
      }

    }
    return true;
  }

  private DocumentInfoId getDocumentInfoId(String mainTypeName) throws UseCaseException
  {
    DocumentInfo documentInfo = documentInfoRepository.findByName(mainTypeName);

    if (null == documentInfo)
    {
      String errorCode = "DMS025";
      throw new UseCaseException(errorCode, "Document info not found with name:" + mainTypeName);
    }

    return documentInfo.getId();
  }

  private String getGroupId() throws UseCaseException
  {
    String currentUserId = authenticationService.getCurrentUserId();

    if (null == currentUserId)
    {
      String errorCode = "DMS026";
      throw new UseCaseException(errorCode, "Current user id is null!");
    }

    try
    {
      // TODO : get tenant id from repository.
      List<Membership> memberships = membershipRepository.listAllByUserId(TenantId.valueOf("xac"), UserId.valueOf(currentUserId));

      if (null != memberships && !memberships.isEmpty())
      {
        Membership membership = memberships.get(0);
        GroupId groupId = membership.getGroupId();

        return groupId.getId();
      }
      return null;
    }
    catch (AimRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }

  private boolean exceedMaxFileSize(String documentAsBase64)
  {
    int n = documentAsBase64.length();
    int y = 0;
    if (documentAsBase64.endsWith("=")) { y = 1; }
    if (documentAsBase64.endsWith("==")) { y = 2; }
    int fileSize = (n * 3 / 4) - y;
    return fileSize > 9200000;
  }
}
