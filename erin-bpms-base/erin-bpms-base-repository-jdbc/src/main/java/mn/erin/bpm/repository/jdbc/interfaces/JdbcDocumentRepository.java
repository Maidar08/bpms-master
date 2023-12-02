package mn.erin.bpm.repository.jdbc.interfaces;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import mn.erin.bpm.repository.jdbc.model.JdbcDocument;

/**
 * @author Tamir
 */
public interface JdbcDocumentRepository extends CrudRepository<JdbcDocument, String>
{
  @Modifying
  @Query(value = "INSERT INTO DOCUMENT(ID, DOCUMENT_INFO_ID, PROCESS_INSTANCE_ID, NAME, CATEGORY, SUB_CATEGORY, REFERENCE, SOURCE) VALUES(:id, :documentInfoId, :processInstanceId, :name, :category, :subCategory, :reference, :source)")
  int insert(@Param("id") String id, @Param("documentInfoId") String documentInfoId, @Param("processInstanceId") String processInstanceId,
      @Param("name") String name, @Param("category") String category,
      @Param("subCategory") String subCategory, @Param("reference") String reference, @Param("source") String source);

  @Query(value = "Select * FROM DOCUMENT WHERE PROCESS_INSTANCE_ID = :processInstanceId")
  List<JdbcDocument> findByProcessInstanceId(@Param("processInstanceId") String processInstanceId);

  @Modifying
  @Query(value = "DELETE FROM DOCUMENT WHERE PROCESS_INSTANCE_ID = :processInstanceId AND CATEGORY = :category AND SUB_CATEGORY = :subCategory")
  void delete(@Param("processInstanceId") String processInstanceId, @Param("category") String category, @Param("subCategory") String subCategory);

  @Modifying
  @Query(value = "DELETE FROM DOCUMENT WHERE PROCESS_INSTANCE_ID = :processInstanceId AND CATEGORY = :category AND SUB_CATEGORY = :subCategory AND NAME  = :name")
  void deleteByDocumentName(@Param("processInstanceId") String processInstanceId, @Param("category") String category, @Param("subCategory") String subCategory, @Param("name") String name);

  @Modifying
  @Query(value = "DELETE FROM DOCUMENT WHERE PROCESS_INSTANCE_ID = :processInstanceId")
  void deleteByProcessInstanceId(@Param("processInstanceId") String processInstanceId);
}
