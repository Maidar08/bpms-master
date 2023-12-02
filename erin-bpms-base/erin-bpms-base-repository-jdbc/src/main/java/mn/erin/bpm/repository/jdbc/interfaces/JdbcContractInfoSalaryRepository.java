package mn.erin.bpm.repository.jdbc.interfaces;

import org.springframework.data.repository.CrudRepository;

import mn.erin.bpm.repository.jdbc.model.JdbcContractInfoSalary;

/**
 * @author Tamir
 */
public interface JdbcContractInfoSalaryRepository extends CrudRepository<JdbcContractInfoSalary, String>
{
}
