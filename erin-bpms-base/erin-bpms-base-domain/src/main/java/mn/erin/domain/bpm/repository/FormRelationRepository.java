package mn.erin.domain.bpm.repository;

import mn.erin.domain.base.repository.Repository;
import mn.erin.domain.bpm.model.form.TaskFormRelation;

/**
 * @author Tamir
 */
public interface FormRelationRepository extends Repository<TaskFormRelation>
{
  /**
   * Finds form relation by task definition key.
   *
   * @param taskDefinitionId given task definition id otherwise definition key.
   * @return @return found {@link TaskFormRelation}.
   * @throws BpmRepositoryException when there is a SQL exception or runtime exception.
   */
  TaskFormRelation findByTaskDefinitionId(String taskDefinitionId) throws BpmRepositoryException;
}
