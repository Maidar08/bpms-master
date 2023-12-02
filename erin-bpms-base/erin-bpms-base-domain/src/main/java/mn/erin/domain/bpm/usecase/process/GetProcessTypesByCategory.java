package mn.erin.domain.bpm.usecase.process;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.ProcessType;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProcessTypeRepository;

public class GetProcessTypesByCategory extends AbstractUseCase<String, GetProcessTypesByCategoryOutput>
{
  private final ProcessTypeRepository processTypeRepository;

  public GetProcessTypesByCategory(ProcessTypeRepository processTypeRepository)
  {
    this.processTypeRepository = processTypeRepository;
  }

  @Override
  public GetProcessTypesByCategoryOutput execute(String processTypeCategory) throws UseCaseException
  {
    if (!StringUtils.isBlank(processTypeCategory))
    {
      try
      {
        List<ProcessType> processTypes = processTypeRepository.findByProcessTypeCategory(processTypeCategory);
        return new GetProcessTypesByCategoryOutput(processTypes);
      }
      catch (BpmRepositoryException e)
      {
        throw new UseCaseException("BPMS050", "Process type does not exist!");
      }
    }
    return null;
  }
}

