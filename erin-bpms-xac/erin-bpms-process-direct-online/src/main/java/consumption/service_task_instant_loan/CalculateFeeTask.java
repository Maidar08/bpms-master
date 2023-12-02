package consumption.service_task_instant_loan;

import java.util.Collection;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;

import static mn.erin.domain.bpm.BpmMessagesConstants.INSTANT_LOAN_LOG;

public class CalculateFeeTask implements JavaDelegate
{
  private final BpmsRepositoryRegistry bpmsRepositoryRegistry;
  private static final Logger LOGGER = LoggerFactory.getLogger(CalculateFeeTask.class);

  public CalculateFeeTask(BpmsRepositoryRegistry bpmsRepositoryRegistry)
  {
    this.bpmsRepositoryRegistry = bpmsRepositoryRegistry;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String cifNumber = String.valueOf(execution.getVariable("cifNumber"));
    ProcessRequestRepository processRequestRepository = bpmsRepositoryRegistry.getProcessRequestRepository();
    int numberOfRequests = getNumberOfRequests(processRequestRepository, cifNumber);
    LOGGER.info(INSTANT_LOAN_LOG + "{} number of COMPLETED or DISBURSED loan request found on CIF number = {}", numberOfRequests, cifNumber);
    execution.setVariable("number_of_requests", numberOfRequests);

    if (numberOfRequests > 5)
    {
      LOGGER.info(INSTANT_LOAN_LOG + "Interest rate is updated.");
    }
    else
    {
      LOGGER.info(INSTANT_LOAN_LOG + "Interest rate is not updated.");
    }
  }

  private int getNumberOfRequests(ProcessRequestRepository processRequestRepository, String cifNumber)
  {
    Collection<ProcessRequest> instantLoanProcessRequests = processRequestRepository.getInstantLoanProcessRequestsByCifNumber(cifNumber);
    return instantLoanProcessRequests.size();
  }
}
