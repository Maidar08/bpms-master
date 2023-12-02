package mn.erin.domain.bpm.usecase.form.relation;

import java.util.Objects;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmMessagesConstants;
import mn.erin.domain.bpm.model.form.TaskFormRelation;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.FormRelationRepository;

/**
 * @author Tamir
 */
public class GetFormRelation extends AbstractUseCase<String, GetFormRelationOutput>
{
  private final FormRelationRepository formRelationRepository;

  public GetFormRelation(FormRelationRepository formRelationRepository)
  {
    this.formRelationRepository = Objects.requireNonNull(formRelationRepository, "Form relation repository is required!");
  }

  @Override
  public GetFormRelationOutput execute(String taskDefinitionKey) throws UseCaseException
  {
    if (null == taskDefinitionKey)
    {
      return null;
    }

    try
    {
      TaskFormRelation taskFormRelation = formRelationRepository.findByTaskDefinitionId(taskDefinitionKey);

      if (null == taskFormRelation)
      {
        return null;
      }

      return new GetFormRelationOutput(taskFormRelation);
    }
    catch (BpmRepositoryException e)
    {
      throw new UseCaseException(BpmMessagesConstants.FORM_RELATION_ERROR_CODE, e.getMessage());
    }
  }
}
