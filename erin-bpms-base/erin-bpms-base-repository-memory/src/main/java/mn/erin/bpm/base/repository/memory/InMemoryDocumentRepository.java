package mn.erin.bpm.base.repository.memory;

import java.util.Collection;

import org.springframework.stereotype.Repository;

import mn.erin.domain.base.model.EntityId;
import mn.erin.domain.bpm.model.document.Document;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.DocumentRepository;

@Repository
public class InMemoryDocumentRepository implements DocumentRepository
{
  @Override
  public Document create(String id, String documentInfoId, String instanceId, String name, String category, String subCategory, String reference, String source)
      throws BpmRepositoryException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Collection<Document> findByProcessInstanceId(String instanceId)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void removeBy(String instanceId, String category, String subCategory)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void removeBy(String instanceId, String category, String subCategory, String documentName)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void delete(String instanceId)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Document findById(EntityId entityId)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Collection<Document> findAll()
  {
    throw new UnsupportedOperationException();
  }
}
