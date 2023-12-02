package mn.erin.bpm.repository.jdbc;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.inject.Inject;

import org.json.JSONObject;
import org.springframework.stereotype.Repository;

import mn.erin.bpm.repository.jdbc.interfaces.JdbcFormRelationRepository;
import mn.erin.bpm.repository.jdbc.model.JdbcFormRelation;
import mn.erin.domain.base.model.EntityId;
import mn.erin.domain.bpm.model.form.FormFieldRelation;
import mn.erin.domain.bpm.model.form.TaskDefinitionId;
import mn.erin.domain.bpm.model.form.TaskFormRelation;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.FormRelationRepository;

import static mn.erin.domain.bpm.util.process.BpmUtils.convertClobToString;

/**
 * @author Tamir
 */
@Repository
public class DefaultJdbcFormRelationRepository implements FormRelationRepository
{
  private final JdbcFormRelationRepository jdbcFormRelationRepository;

  @Inject
  public DefaultJdbcFormRelationRepository(JdbcFormRelationRepository jdbcFormRelationRepository)
  {
    this.jdbcFormRelationRepository = Objects.requireNonNull(jdbcFormRelationRepository, "JDBC form relation repository is required!");
  }

  @Override
  public TaskFormRelation findByTaskDefinitionId(String taskDefinitionId) throws BpmRepositoryException
  {
    JdbcFormRelation formValue = jdbcFormRelationRepository.findFormRelationByTaskDefinitionId(taskDefinitionId);
    if (formValue == null)
    {
      return null;
    }

    String formValueString = null;
    try
    {
      formValueString = convertClobToString(formValue.getFormValue());
      return toTaskFormRelation(taskDefinitionId, new JSONObject(formValueString).toMap());
    }
    catch (Exception e)
    {
      throw new BpmRepositoryException(e.getMessage());
    }
  }

  @Override
  public TaskFormRelation findById(EntityId entityId)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Collection<TaskFormRelation> findAll()
  {
    throw new UnsupportedOperationException();
  }

  private TaskFormRelation toTaskFormRelation(String taskDefinitionId, Map<String, Object> formRelationJson)
  {
    if (formRelationJson.isEmpty())
    {
      return null;
    }
    Map<String, Collection<FormFieldRelation>> fieldRelations = new HashMap<>();
    formRelationJson.forEach((key, value) ->
    {
      Collection<FormFieldRelation> fieldRelationCollection = (Collection<FormFieldRelation>) value;
      fieldRelations.put(key, fieldRelationCollection);
    });

    return new TaskFormRelation(TaskDefinitionId.valueOf(taskDefinitionId), fieldRelations);
  }
}
