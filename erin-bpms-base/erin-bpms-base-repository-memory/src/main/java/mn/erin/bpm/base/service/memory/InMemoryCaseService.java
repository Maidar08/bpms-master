package mn.erin.bpm.base.service.memory;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import mn.erin.domain.bpm.model.cases.Case;
import mn.erin.domain.bpm.model.variable.Variable;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CaseService;

@Service
public class InMemoryCaseService implements CaseService
{
  @Override
  public Case startCase(String caseDefinitionKey) throws BpmServiceException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void completeByActivityId(String instanceId, String activityId)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Variable> getVariables(String instanceId) throws BpmServiceException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object getVariableById(String instanceId, String name) throws BpmServiceException
  {
    return null;
  }

  @Override
  public List<Case> getUserCases()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Case findByTaskId(String taskId) throws BpmServiceException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void deleteVariablesByInstanceIdAndVariableName(String instanceId, List<String> documentNames) throws BpmServiceException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setCaseVariables(String caseInstanceId, Map<String, Object> variables)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setCaseVariableById(String caseInstanceId, String variableId, Object variableValue)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean terminateCase(String caseExecutionId)
  {
    return false;
  }

  @Override
  public boolean closeCases(String caseExecutionId)
  {
    return false;
  }

  @Override
  public void disable(String id)
  {

  }
}
