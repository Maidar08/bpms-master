package mn.erin.domain.bpm.repository.directOnline;

import java.util.Map;

import mn.erin.domain.base.repository.Repository;
import mn.erin.domain.bpm.repository.BpmRepositoryException;

/**
 * @author Odgavaa
 **/

public interface DefaultParameterRepository extends Repository

{
  /**
   * @param processType process type
   * @return default parameters
   */
  Map<String, Object> getDefaultParametersByProcessType(String processType, String entity) throws BpmRepositoryException;
}

