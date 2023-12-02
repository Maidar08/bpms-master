package mn.erin.bpm.repository.jdbc;

import java.time.LocalDateTime;

import org.springframework.stereotype.Repository;

import mn.erin.bpm.repository.jdbc.interfaces.JdbcUnionFieldsRepository;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.UnionFieldsRepository;

/**
 * @author Sukhbaatar
 */
@Repository
public class DefaultJdbcUnionFieldsRepository implements UnionFieldsRepository
{
  private final JdbcUnionFieldsRepository jdbcUnionFieldsRepository;

  public DefaultJdbcUnionFieldsRepository(JdbcUnionFieldsRepository jdbcUnionFieldsRepository)
  {
    this.jdbcUnionFieldsRepository = jdbcUnionFieldsRepository;
  }

  @Override
  public void create(String customerNumber, String registerId, String keyField, String trackNumber,
      String productCategory, String processRequestId, String processTypeId, String requestType, LocalDateTime dateTime)
      throws BpmRepositoryException
  {
    jdbcUnionFieldsRepository.insert(customerNumber, registerId, keyField, trackNumber, productCategory, processRequestId, processTypeId, requestType, dateTime);
  }
}
