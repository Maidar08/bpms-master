package mn.erin.bpm.repository.jdbc.interfaces;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import mn.erin.bpm.repository.jdbc.model.JdbcFormRelation;

public interface JdbcFormRelationRepository extends CrudRepository<JdbcFormRelation, String>
{
  @Query(value = "SELECT * FROM ERIN_BPMS_TASK_FORM_RELATION WHERE TASK_DEFINITION_ID = :taskDefinitionId  AND entity = 'JSON'")
  JdbcFormRelation findFormRelationByTaskDefinitionId(@Param("taskDefinitionId") String taskDefinitionId);
}
