package mn.erin.bpm.repository.jdbc.interfaces;

import org.springframework.data.repository.CrudRepository;

import mn.erin.bpm.repository.jdbc.model.JdbcContractInfoLeasing;

/**
 * @author Tamir
 */
public interface JdbcContractInfoLeasingRepository extends CrudRepository<JdbcContractInfoLeasing, String>
{

}
