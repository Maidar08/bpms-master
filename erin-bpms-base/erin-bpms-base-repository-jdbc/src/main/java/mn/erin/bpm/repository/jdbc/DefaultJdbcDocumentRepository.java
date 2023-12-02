package mn.erin.bpm.repository.jdbc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import mn.erin.bpm.repository.jdbc.interfaces.JdbcDocumentRepository;
import mn.erin.bpm.repository.jdbc.model.JdbcDocument;
import mn.erin.domain.base.model.EntityId;
import mn.erin.domain.bpm.model.document.Document;
import mn.erin.domain.bpm.model.document.DocumentId;
import mn.erin.domain.bpm.model.document.DocumentInfoId;
import mn.erin.domain.bpm.model.process.ProcessInstanceId;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.DocumentRepository;

/**
 * @author Tamir
 */
@Repository
public class DefaultJdbcDocumentRepository implements DocumentRepository
{
  private final JdbcDocumentRepository jdbcDocumentRepository;

  public DefaultJdbcDocumentRepository(JdbcDocumentRepository jdbcDocumentRepository)
  {
    this.jdbcDocumentRepository = jdbcDocumentRepository;
  }

  @Override
  public Document create(String id, String documentInfoId, String instanceId, String name, String category, String subCategory, String reference,
      String source)
      throws BpmRepositoryException
  {

    try
    {
      jdbcDocumentRepository.insert(id, documentInfoId, instanceId, name, category, subCategory, reference, source);
    }
    catch (Exception e)
    {
      throw new BpmRepositoryException(e.getMessage());
    }

    return new Document(DocumentId.valueOf(id), DocumentInfoId.valueOf(documentInfoId), ProcessInstanceId.valueOf(instanceId), name, category,
        subCategory,
        reference, source);
  }

  @Override
  public Collection<Document> findByProcessInstanceId(String instanceId)
  {
    if (StringUtils.isBlank(instanceId))
    {
      return Collections.emptyList();
    }

    List<JdbcDocument> jdbcDocuments = jdbcDocumentRepository.findByProcessInstanceId(instanceId);

    Collection<Document> documents = new ArrayList<>();

    for (JdbcDocument jdbcDocument : jdbcDocuments)
    {
      Document document = toDocument(jdbcDocument);

      if (null != document)
      {
        documents.add(document);
      }
    }
    return documents;
  }

  @Override
  public void removeBy(String instanceId, String category, String subCategory)
  {
    if (!StringUtils.isBlank(instanceId) && !StringUtils.isBlank(category) && !StringUtils.isBlank(subCategory))
    {
      jdbcDocumentRepository.delete(instanceId, category, subCategory);
    }
  }

  @Override
  public void removeBy(String instanceId, String category, String subCategory, String documentName)
  {
    if (!StringUtils.isBlank(instanceId) && !StringUtils.isBlank(category) && !StringUtils.isBlank(subCategory) && !StringUtils.isBlank(documentName))
    {
      jdbcDocumentRepository.deleteByDocumentName(instanceId, category, subCategory, documentName);
    }
  }

  @Override
  public void delete(String instanceId)
  {
    jdbcDocumentRepository.deleteByProcessInstanceId(instanceId);
  }

  @Override
  public Document findById(EntityId entityId)
  {
    Optional<JdbcDocument> jdbcDocument = jdbcDocumentRepository.findById(entityId.getId());

    return jdbcDocument.map(this::toDocument).orElse(null);
  }

  @Override
  public Collection<Document> findAll()
  {

    Iterable<JdbcDocument> allDocument = jdbcDocumentRepository.findAll();
    Iterator<JdbcDocument> documentsIterator = allDocument.iterator();

    Collection<Document> documents = new ArrayList<>();

    if (documentsIterator.hasNext())
    {
      JdbcDocument jdbcDocumentInfo = documentsIterator.next();
      Document document = toDocument(jdbcDocumentInfo);

      if (null != document)
      {
        documents.add(document);
      }
    }
    return documents;
  }

  private Document toDocument(JdbcDocument jdbcDocument)
  {
    if (null != jdbcDocument)
    {
      String id = jdbcDocument.getId();
      String documentInfoId = jdbcDocument.getDocumentInfoId();
      String processInstanceId = jdbcDocument.getProcessInstanceId();

      String name = jdbcDocument.getName();
      String reference = jdbcDocument.getReference();

      String category = jdbcDocument.getCategory();
      String subCategory = jdbcDocument.getSubCategory();
      String source = jdbcDocument.getSource();

      return new Document(DocumentId.valueOf(id), DocumentInfoId.valueOf(documentInfoId), ProcessInstanceId.valueOf(processInstanceId), name, category,
          subCategory,
          reference, source);
    }
    return null;
  }
}
