package mn.erin.domain.bpm.usecase.cases.get_cases;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.Process;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.service.CaseService;

import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;

/**
 * @author Lkhagvadorj.A
 **/

public class CloseCasesByDate implements UseCase<CloseCasesByDateInput, Integer>
{
  private final ProcessRepository processRepository;
  private final CaseService caseService;
  private static final Logger LOGGER = LoggerFactory.getLogger(CloseCasesByDate.class);

  public CloseCasesByDate(ProcessRepository processRepository, CaseService caseService)
  {
    this.processRepository = Objects.requireNonNull(processRepository);
    this.caseService = Objects.requireNonNull(caseService);
  }

  @Override
  public Integer execute(CloseCasesByDateInput input) throws UseCaseException
  {
    if (null == input)
    {
      throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
    }
    if (null == input.getEndDate())
    {
      throw new UseCaseException("date is null!");
    }
    final LocalDate endDate = input.getEndDate();
    final int sleepPerProcess = input.getSleepPerProcess();
    final long sleepTime = input.getSleepTime();
    final int startYear = endDate.getYear() - 1;
    final LocalDate startDate = LocalDate.of(startYear, endDate.getMonth(), 1);
    final List<Process> processes = processRepository.findProcessesByDate(startDate, endDate);
    LOGGER.info("############ DATE RANGE BETWEEN {} AND {}", startDate, endDate);
    LOGGER.info("############ FOUND {} PROCESSES TO CLOSE", processes.size());
    LOGGER.info("############ Thread sleep time {} millis, sleep per proocess {}", sleepTime, sleepPerProcess);
    return terminate(processes, input, sleepPerProcess, sleepTime);
  }

  private int terminate(List<Process> processes, CloseCasesByDateInput input, int sleepPerProcess, long sleepTime)
  {
    int num = 0;
    int i = 0;
    int j = 0;
    for (Process process : processes)
    {
      if (input.isThreadStopper())
      {
        LOGGER.info("############ THREAD STOPPED");
        return num;
      }
      try
      {
        if (i == sleepPerProcess)
        {
          LOGGER.info("############ TERMINATED PROCESSES = {}", num);
          i = 0;
          Thread.sleep(sleepTime);
        }
        final String executionId = process.getId().getId();
        if (caseService.terminateCase(executionId) && caseService.closeCases(executionId))
        {
          num++;
          i++;
        }

        j++;
        LOGGER.info("############ {}", j);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }

    }
    LOGGER.info("############ TERMINATED AND CLOSED {} PROCESSES", num);
    return num;
  }
}
