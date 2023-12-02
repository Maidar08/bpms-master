/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.document_info;

import java.util.Collection;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.BpmModulePermission;
import mn.erin.domain.bpm.model.document.DocumentInfo;
import mn.erin.domain.bpm.repository.DocumentInfoRepository;

/**
 * @author Tamir
 */
public class GetSubDocumentInfos extends AuthorizedUseCase<String, GetSubDocumentInfosOutput>
{
  private static final Permission permission = new BpmModulePermission("GetSubDocumentInfos");
  private final DocumentInfoRepository documentInfoRepository;

  public GetSubDocumentInfos(AuthenticationService authenticationService, AuthorizationService authorizationService, DocumentInfoRepository documentInfoRepository)
  {
    super(authenticationService, authorizationService);
    this.documentInfoRepository = Objects.requireNonNull(documentInfoRepository, "Document info repository is required!");
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected GetSubDocumentInfosOutput executeImpl(String parentId) throws UseCaseException
  {
    if (StringUtils.isBlank(parentId))
    {
      throw new UseCaseException("Parent document info id is required!");
    }

    Collection<DocumentInfo> documentInfos = documentInfoRepository.findByParentId(parentId);

    return new GetSubDocumentInfosOutput(documentInfos);
  }
}
