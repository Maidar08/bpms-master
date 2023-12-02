package mn.erin.bpm.webapp.standalone.repository;

import java.util.Collection;

import org.springframework.stereotype.Repository;

import mn.erin.domain.base.model.EntityId;
import mn.erin.domain.bpm.model.document.DocumentInfo;
import mn.erin.domain.bpm.repository.DocumentInfoRepository;

@Repository
public class InMemoryDocumentInfoRepository implements DocumentInfoRepository
{
  @Override
  public Collection<DocumentInfo> findBasicDocuments()
  {
    return null;
  }

  @Override
  public Collection<DocumentInfo> findByParentId(String parentId)
  {
    return null;
  }

  @Override
  public DocumentInfo findByName(String name)
  {
    return null;
  }

  @Override
  public DocumentInfo findById(EntityId entityId)
  {
    return null;
  }

  @Override
  public Collection<DocumentInfo> findAll()
  {
    return null;
  }
}
