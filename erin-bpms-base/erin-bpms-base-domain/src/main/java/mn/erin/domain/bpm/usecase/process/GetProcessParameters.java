package mn.erin.domain.bpm.usecase.process;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.Process;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProcessRepository;

public class GetProcessParameters implements UseCase<GetProcessEntityInput, Map<String, Serializable>>
{

  private final ProcessRepository processRepository;

  public GetProcessParameters(ProcessRepository processRepository)
  {
    this.processRepository = Objects.requireNonNull(processRepository, "ProcessRepository is required!");
  }

  @Override
  public Map<String, Serializable> execute(GetProcessEntityInput input) throws UseCaseException
  {
    if (input == null)
    {
      String errorCode = "BPMS011";
      throw new UseCaseException(errorCode, "Input is null!");
    }
    try
    {
      Process returnedProcess = processRepository.filterByInstanceIdAndEntityType(input.getProcessInstanceId(), input.getParameterEntityType());
      if (returnedProcess == null)
      {
        return Collections.emptyMap();
      }
      return returnedProcess.getProcessParameters().get(input.getParameterEntityType());
    }
    catch (BpmRepositoryException e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }
}
