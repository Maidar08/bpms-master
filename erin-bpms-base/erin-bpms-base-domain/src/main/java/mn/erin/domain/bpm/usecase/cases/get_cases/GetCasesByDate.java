package mn.erin.domain.bpm.usecase.cases.get_cases;

import java.util.List;
import java.util.Objects;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.Process;
import mn.erin.domain.bpm.repository.ProcessRepository;

import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;

/**
 * @author Lkhagvadorj.A
 **/

public class GetCasesByDate implements UseCase<GetCasesByDateInput, List<Process>>
{
  private final ProcessRepository processRepository;

  public GetCasesByDate(ProcessRepository processRepository)
  {
    this.processRepository = Objects.requireNonNull(processRepository);
  }

  @Override
  public List<Process> execute(GetCasesByDateInput input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
    }

    return processRepository.findProcessesByDate(input.getStartDate(), input.getEndDate());
  }
}
