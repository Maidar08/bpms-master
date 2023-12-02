package mn.erin.bpm.repository.jdbc.interfaces;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import mn.erin.bpm.repository.jdbc.model.JdbcDefaultParameter;

/**
 * @author Odgavaa
 **/

public interface JdbcDefaultParameterRepository extends CrudRepository<JdbcDefaultParameter, String>{
  @Query(value = "SELECT * FROM ERIN_BPMS_DEFAULT_PARAMETER WHERE PROCESS_TYPE = (:processType) AND ENTITY = (:entity)")
  List<JdbcDefaultParameter> getDefaultParametersByProcessType(@Param(value = "processType") String processType, @Param(value = "entity") String entity);
}

