package mn.erin.bpm.repository.jdbc.interfaces;

import org.springframework.data.repository.CrudRepository;

import mn.erin.bpm.repository.jdbc.model.JdbcContractExtension;

/**
 * @author Tamir
 */
public interface JdbcContractExtensionRepository extends CrudRepository<JdbcContractExtension, String>
{

}
