package mn.erin.domain.bpm.repository;

import mn.erin.domain.bpm.model.contract.BiPath;

public interface BiPathRepository {
  /**
   * @param processTypeId Unique process or case instance id.
   * @param productCode      Unique process or case instance id.
   * @return Found bi path and folder.
   * @throws BpmRepositoryException
   */
  BiPath getBiPath(String processTypeId, String productCode) throws BpmRepositoryException;
}
