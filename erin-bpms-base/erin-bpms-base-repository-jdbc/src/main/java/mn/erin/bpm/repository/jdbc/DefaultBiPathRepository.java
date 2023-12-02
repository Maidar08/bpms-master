package mn.erin.bpm.repository.jdbc;

import org.springframework.stereotype.Repository;

import mn.erin.bpm.repository.jdbc.interfaces.JdbcBIPathRepository;
import mn.erin.bpm.repository.jdbc.model.JdbcBiPath;
import mn.erin.domain.bpm.model.contract.BiPath;
import mn.erin.domain.bpm.repository.BiPathRepository;
import mn.erin.domain.bpm.repository.BpmRepositoryException;

@Repository
public class DefaultBiPathRepository implements BiPathRepository {
  private final JdbcBIPathRepository jdbcBIPathRepository;
  
  public DefaultBiPathRepository(JdbcBIPathRepository jdbcBIPathRepository) {
    this.jdbcBIPathRepository = jdbcBIPathRepository;
  }

  @Override
  public BiPath getBiPath(String processTypeId, String productCode) throws BpmRepositoryException {
    try {
      JdbcBiPath biPath = jdbcBIPathRepository.getBiPath(processTypeId, productCode);
      return new BiPath(biPath.getFolder(), biPath.getPath());
    } catch (Exception e) {
      throw new BpmRepositoryException(e.getMessage());
    }
  }
}