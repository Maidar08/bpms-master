package mn.erin.domain.bpm.usecase.process;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmMessagesConstants;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProcessRepository;

import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_MESSAGE;

/**
 * @author Lkhagvadorj.A
 **/

public class DeleteProcessParameterByInstanceIdAndEntity extends AbstractUseCase<DeleteProcessParameterByInstanceIdAndEntityInput, Integer>
{
  private final ProcessRepository processRepository;

  public DeleteProcessParameterByInstanceIdAndEntity(ProcessRepository processRepository)
  {
    this.processRepository = Objects.requireNonNull(processRepository, "ProcessRepository is required!");
  }

  @Override
  public Integer execute(DeleteProcessParameterByInstanceIdAndEntityInput input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException(BpmMessagesConstants.INPUT_NULL_CODE, BpmMessagesConstants.INPUT_NULL_MESSAGE);
    }

    if (StringUtils.isBlank(input.getProcessInstanceId()))
    {
      throw new UseCaseException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
    }

    try
    {
      processRepository.deleteEntity(input.getProcessInstanceId(), ParameterEntityType.BALANCE);
    }
    catch (BpmRepositoryException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
    return null;
  }
}
