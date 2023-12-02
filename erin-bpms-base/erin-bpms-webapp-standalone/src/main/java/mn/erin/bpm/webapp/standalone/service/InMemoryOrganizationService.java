package mn.erin.bpm.webapp.standalone.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.OrganizationService;

@Service
public class InMemoryOrganizationService implements OrganizationService
{
  @Override
  public Map<String, String> getOrganizationLevel(String cifNumber, String branchNumber) throws BpmServiceException
  {
    return null;
  }
}
