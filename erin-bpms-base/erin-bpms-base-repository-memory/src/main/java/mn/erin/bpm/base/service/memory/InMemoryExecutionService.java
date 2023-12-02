package mn.erin.bpm.base.service.memory;

import java.util.List;

import org.springframework.stereotype.Service;

import mn.erin.domain.bpm.model.cases.Execution;
import mn.erin.domain.bpm.model.variable.Variable;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.ExecutionService;

@Service
public class InMemoryExecutionService implements ExecutionService
{
  @Override
  public List<Execution> getCompletedByInstanceId(String instanceId) throws BpmServiceException
  {
    return null;
  }

  @Override
  public List<Execution> getEnabledByInstanceId(String instanceId) throws BpmServiceException
  {
    return null;
  }

  @Override
  public List<Execution> getActiveByInstanceId(String instanceId) throws BpmServiceException
  {
    return null;
  }

  @Override
  public void manualActivate(String executionId) throws BpmServiceException
  {

  }

  @Override
  public void manualActivate(String executionId, List<Variable> variables) throws BpmServiceException
  {

  }

  @Override
  public void manualActivate(String executionId, List<Variable> variables, List<Variable> deletions) throws BpmServiceException
  {

  }
}
