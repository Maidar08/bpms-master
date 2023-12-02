package mn.erin.bpm.base.repository.memory;

import org.springframework.stereotype.Repository;

import mn.erin.domain.bpm.model.contract.BiPath;
import mn.erin.domain.bpm.repository.BiPathRepository;
import mn.erin.domain.bpm.repository.BpmRepositoryException;

@Repository
public class InMemoryBiPathRepository implements BiPathRepository
{
  @Override
  public BiPath getBiPath(String processTypeId, String productCode) throws BpmRepositoryException {
    throw new UnsupportedOperationException("Unimplemented method 'getBiPath'");
  }
}
