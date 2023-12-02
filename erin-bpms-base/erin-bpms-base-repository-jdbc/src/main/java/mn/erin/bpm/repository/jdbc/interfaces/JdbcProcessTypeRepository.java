package mn.erin.bpm.repository.jdbc.interfaces;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import mn.erin.bpm.repository.jdbc.model.JdbcProcessType;

/**
 * @author EBazarragchaa
 */
public interface JdbcProcessTypeRepository extends CrudRepository<JdbcProcessType, String>
{
  @Modifying
  @Query(value = "INSERT INTO PROCESS_TYPE(PROCESS_TYPE_ID, DEFINITION_KEY, PROCESS_DEFINITION_TYPE, NAME) VALUES(:processTypeId, :definitionKey, :processDefinitionType, :name)")
  int insert(@Param("processTypeId") String processTypeId, @Param("definitionKey") String definitionKey, @Param("processDefinitionType")String processDefinitionType, @Param("name") String name);


  @Query(value = "SELECT * FROM PROCESS_TYPE WHERE PROCESS_TYPE_CATEGORY = (:processTypeCategory)")
  List<JdbcProcessType> findByProcessTypeCategory(@Param(value = "processTypeCategory") String processTypeCategory);


}
