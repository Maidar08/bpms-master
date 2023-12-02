package mn.erin.domain.bpm.usecase.process;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.Process;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProcessRepository;

import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;

public class GetProcessLargeParameter extends AbstractUseCase<GetProcessParameterInput, GetProcessParameterOutput>
{
  private final ProcessRepository processRepository;

  public GetProcessLargeParameter(ProcessRepository processRepository)
  {
    this.processRepository = Objects.requireNonNull(processRepository, "Process repository is required!");
  }

  @Override
  public GetProcessParameterOutput execute(GetProcessParameterInput input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
    }

    String parameterKey = input.getParameterKey();

    try
    {
      Process returnedProcess = processRepository.filterLargeParametersByEntityType(input.getInstanceId(), input.getParameterEntityType());

      if (returnedProcess == null)
      {
        return new GetProcessParameterOutput(null);
      }

      Map<String, Serializable> parameters = returnedProcess.getProcessParameters().get(input.getParameterEntityType());

      if (parameters.containsKey(parameterKey))
      {
        Serializable serializable = parameters.get(parameterKey);

        return new GetProcessParameterOutput(serializable);
      }
    }
    catch (BpmRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
    return new GetProcessParameterOutput(null);
  }
}

