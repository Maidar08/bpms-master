package mn.erin.domain.bpm.usecase.process.process_parameter;

import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.Process;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.usecase.process.GetProcessParameterInput;

import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;

/**
 * @author Lkhagvadorj
 */
public class GetProcessParameterByName extends AbstractUseCase<GetProcessParameterInput, Process>
{
  private final ProcessRepository processRepository;

  public GetProcessParameterByName(ProcessRepository processRepository)
  {
    this.processRepository = Objects.requireNonNull(processRepository, "Process repository is required!");
  }

  @Override
  public Process execute(GetProcessParameterInput input) throws UseCaseException
  {
    if (null == input)
    {
      throw new UseCaseException( INPUT_NULL_CODE, INPUT_NULL_MESSAGE );
    }

    Validate.notBlank(input.getInstanceId());
    Validate.notBlank(input.getParameterKey());

    return processRepository.filterByParameterName(input.getInstanceId(), input.getParameterKey());
  }
}
