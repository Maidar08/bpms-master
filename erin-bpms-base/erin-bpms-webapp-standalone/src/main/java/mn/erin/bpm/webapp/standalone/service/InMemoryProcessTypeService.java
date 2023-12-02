package mn.erin.bpm.webapp.standalone.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import mn.erin.domain.bpm.service.ProcessTypeService;

@Service
public class InMemoryProcessTypeService implements ProcessTypeService
{
  @Override
  public String startProcess(String processTypeId, String processRequestId)
  {
    return null;
  }

  @Override
  public String startProcess(String processTypeId)
  {
    return null;
  }

  @Override
  public String startProcessWithVariables(String processTypeId, String processRequestId, Map<String, Object> parameters)
  {
    return null;
  }

  @Override
  public String startProcess(String processTypeId, Map<String, Object> variables)
  {
    return null;
  }

  @Override
  public void terminateProcess(String caseInstanceId)
  {
    throw new UnsupportedOperationException();
  }
}
