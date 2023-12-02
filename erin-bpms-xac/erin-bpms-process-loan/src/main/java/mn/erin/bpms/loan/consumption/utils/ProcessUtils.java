package mn.erin.bpms.loan.consumption.utils;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateCaseExecution;
import org.camunda.bpm.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ProcessUtils
{
  private static final Logger LOG = LoggerFactory.getLogger(ProcessUtils.class);

  private ProcessUtils()
  {

  }

  public static void suspendActiveProcess(DelegateCaseExecution caseExecution, Task activeTask, String requestId)
  {
    RuntimeService runtimeService = caseExecution.getProcessEngine().getRuntimeService();

    LOG.info("########## SUSPENDS PROCESS OF ACTIVE TASK = [{}] WITH DEFINITION KEY = [{}]", activeTask.getName(), activeTask.getTaskDefinitionKey());

    String processInstanceId = activeTask.getProcessInstanceId();
    try
    {
      runtimeService.suspendProcessInstanceById(processInstanceId);
      LOG.info("########## SUCCESSFUL SUSPENDED ACTIVE PROCESS.");
    }
    catch (Exception e)
    {
      LOG.error("############# COULD NOT SUSPEND ACTIVE PROCESS OF CALCULATION STAGE WITH REQUEST ID = [{}]", requestId);
    }
  }
}
