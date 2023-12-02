package mn.erin.domain.bpm.repository;

import java.util.Collection;

import mn.erin.domain.base.repository.Repository;
import mn.erin.domain.bpm.model.document.Document;

/**
 * @author Tamir
 */
public interface DocumentRepository extends Repository<Document>
{

  /**
   * @param id             Unique id of document.
   * @param documentInfoId Unique id of document info.
   * @param instanceId     Unique id of process or case instance.
   * @param name           Document name.
   * @param category       Document category.
   * @param subCategory    Document sub category.
   * @param reference      Document reference otherwise link or related id.
   * @param source         Document source.
   * @return Created document entity.
   * @throws BpmRepositoryException when this repository is not reachable or usable
   */
  Document create(String id, String documentInfoId, String instanceId, String name, String category, String subCategory, String reference, String source)
      throws
      BpmRepositoryException;

  /**
   * @param instanceId Unique process or case instance id.
   * @return Found documents with given instance id.
   */
  Collection<Document> findByProcessInstanceId(String instanceId);

  /**
   * Removes documents by following parameters.
   *
   * @param instanceId  Unique process or case instance id.
   * @param category    Document category.
   * @param subCategory Document sub category.
   */
  void removeBy(String instanceId, String category, String subCategory);

  /**
   * Removes documents by instanceId, category, subCategory, String documentName.
   *
   * @param instanceId  Unique process or case instance id.
   * @param category    Document category.
   * @param subCategory Document sub category.
   * @param documentName Document name.
   */
  void removeBy(String instanceId, String category, String subCategory, String documentName);

  /**
   * Removes documents by following parameters.
   *
   * @param instanceId  Unique process or case instance id.
   */
  void delete(String instanceId);
}
