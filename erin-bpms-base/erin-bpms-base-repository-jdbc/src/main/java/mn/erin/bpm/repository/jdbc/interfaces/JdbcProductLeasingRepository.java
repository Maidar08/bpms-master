package mn.erin.bpm.repository.jdbc.interfaces;

import org.springframework.data.repository.CrudRepository;

import mn.erin.bpm.repository.jdbc.model.JdbcProductLeasing;

/**
 * @author Tamir
 */
public interface JdbcProductLeasingRepository extends CrudRepository<JdbcProductLeasing, String>
{
}
