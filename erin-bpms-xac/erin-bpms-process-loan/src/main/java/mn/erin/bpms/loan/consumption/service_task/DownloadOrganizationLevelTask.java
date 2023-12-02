package mn.erin.bpms.loan.consumption.service_task;

import java.util.Map;
import java.util.Objects;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.bpms.process.base.ProcessTaskException;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.OrganizationService;
import mn.erin.domain.bpm.usecase.GetOrganizationLevel;

import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;

/**
 * @author Zorig
 */
public class DownloadOrganizationLevelTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DownloadOrganizationLevelTask.class);
  private final OrganizationService organizationService;
  private final AuthenticationService authenticationService;
  private final MembershipRepository membershipRepository;

  public DownloadOrganizationLevelTask(OrganizationService organizationService, AuthenticationService authenticationService,
      MembershipRepository membershipRepository)
  {
    this.organizationService = Objects.requireNonNull(organizationService, "Organization service is required!");
    this.authenticationService = Objects.requireNonNull(authenticationService, "Authentication service is required!");
    this.membershipRepository = Objects.requireNonNull(membershipRepository, "Membership Repository is required!");
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String registrationNumber = (String) execution.getVariable("registerNumber");
    String requestId = (String )execution.getVariable(PROCESS_REQUEST_ID);
    String userId = authenticationService.getCurrentUserId();
    LOGGER.info("#########  Retrieving Organization Information from Xac Task.. Register Number: " + registrationNumber + ", Request ID: " + requestId + " , User ID: " + userId);

    Map<String, Object> variables = execution.getVariables();

    //validation make sure it is there/null check
    String organizationCif = (String)variables.get("organization_cif");

    try
    {
      Map<String, String> organizationInfo = getOrganizationLevel(organizationCif);

      String organizationLevel = organizationInfo.get("rank");
      String organizationName = organizationInfo.get("name");

      if (variables.containsKey("organization_level"))
      {
        execution.removeVariable("organization_level");
      }

      if (variables.containsKey("salaryOrganizationName"))
      {
        execution.removeVariable("salaryOrganizationName");
      }

      execution.setVariable("organization_level", organizationLevel);
      execution.setVariable("salaryOrganizationName", organizationName);

      LOGGER.info("*********** Successfully retrieved Organization information");
    }
    catch (UseCaseException e)
    {
      throw new ProcessTaskException(e.getCode(), e.getMessage(), e);
    }

  }

  private Map<String, String> getOrganizationLevel(String cifNumber) throws UseCaseException
  {
    GetOrganizationLevel getOrganizationLevel = new GetOrganizationLevel(organizationService, authenticationService, membershipRepository);
    return getOrganizationLevel.execute(cifNumber);
  }


}
