/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.repository.jdbc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Repository;

import mn.erin.bpm.repository.jdbc.interfaces.JdbcDocumentInfoRepository;
import mn.erin.bpm.repository.jdbc.model.JdbcDocumentInfo;
import mn.erin.domain.base.model.EntityId;
import mn.erin.domain.bpm.model.document.DocumentInfo;
import mn.erin.domain.bpm.model.document.DocumentInfoId;
import mn.erin.domain.bpm.repository.DocumentInfoRepository;

/**
 * @author Tamir
 */
@Repository
public class DefaultJdbcDocumentInfoRepository implements DocumentInfoRepository
{
  private JdbcDocumentInfoRepository jdbcDocumentInfoRepository;

  @Inject
  public DefaultJdbcDocumentInfoRepository(JdbcDocumentInfoRepository jdbcDocumentInfoRepository)
  {
    this.jdbcDocumentInfoRepository = Objects.requireNonNull(jdbcDocumentInfoRepository, "Jdbc document info repository is required!");
  }

  @Override
  public DocumentInfo findById(EntityId entityId)
  {
    Validate.notNull(entityId, "Document info id is required!");

    Optional<JdbcDocumentInfo> jdbcDocumentInfo = jdbcDocumentInfoRepository.findById(entityId.getId());

    return jdbcDocumentInfo.map(this::convertToDocumentInfo).orElse(null);
  }

  @Override
  public Collection<DocumentInfo> findAll()
  {
    Collection<DocumentInfo> documentInfos = new ArrayList<>();

    Iterable<JdbcDocumentInfo> allJdbcDocumentInfo = jdbcDocumentInfoRepository.findAll();

    Iterator<JdbcDocumentInfo> documentInfoIterator = allJdbcDocumentInfo.iterator();

    if (documentInfoIterator.hasNext())
    {
      JdbcDocumentInfo jdbcDocumentInfo = documentInfoIterator.next();
      DocumentInfo documentInfo = convertToDocumentInfo(jdbcDocumentInfo);

      if (null != documentInfo)
      {
        documentInfos.add(documentInfo);
      }
    }
    return documentInfos;
  }

  @Override
  public Collection<DocumentInfo> findBasicDocuments()
  {
    Collection<DocumentInfo> documentInfos = new ArrayList<>();

    List<JdbcDocumentInfo> basicDocumentInfos = jdbcDocumentInfoRepository.getBasicDocumentInfos();

    for (JdbcDocumentInfo basicDocumentInfo : basicDocumentInfos)
    {
      DocumentInfo documentInfo = convertToDocumentInfo(basicDocumentInfo);

      if (null != documentInfo)
      {
        documentInfos.add(documentInfo);
      }
    }
    return documentInfos;
  }

  @Override
  public Collection<DocumentInfo> findByParentId(String parentId)
  {
    if (StringUtils.isBlank(parentId))
    {
      return Collections.emptyList();
    }

    Collection<DocumentInfo> documentInfos = new ArrayList<>();

    List<JdbcDocumentInfo> subDocumentInfos = jdbcDocumentInfoRepository.getByParentId(parentId);

    for (JdbcDocumentInfo subDocumentInfo : subDocumentInfos)
    {
      DocumentInfo documentInfo = convertToDocumentInfo(subDocumentInfo);
      if (null != documentInfo)
      {
        documentInfos.add(documentInfo);
      }
    }
    return documentInfos;
  }

  @Override
  public DocumentInfo findByName(String name)
  {
    JdbcDocumentInfo jdbcDocumentInfo = jdbcDocumentInfoRepository.getByName(name);

    return convertToDocumentInfo(jdbcDocumentInfo);
  }

  private DocumentInfo convertToDocumentInfo(JdbcDocumentInfo jdbcDocumentInfo)
  {
    if (null != jdbcDocumentInfo)
    {
      String id = jdbcDocumentInfo.getId();
      String parentId = jdbcDocumentInfo.getDocumentParentId();

      String name = jdbcDocumentInfo.getName();
      String type = jdbcDocumentInfo.getType();

      return new DocumentInfo(DocumentInfoId.valueOf(id), parentId, name, type);
    }

    return null;
  }
}
