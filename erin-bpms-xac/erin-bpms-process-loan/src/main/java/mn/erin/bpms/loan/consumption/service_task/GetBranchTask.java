package mn.erin.bpms.loan.consumption.service_task;

import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.bpms.process.base.ProcessTaskException;
import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.AuthenticationService;

import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;

/**
 * @author Zorig
 */
public class GetBranchTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GetBranchTask.class);
  private final AuthenticationService authenticationService;
  private final MembershipRepository membershipRepository;

  public GetBranchTask(AuthenticationService authenticationService, MembershipRepository membershipRepository)
  {
    this.authenticationService = authenticationService;
    this.membershipRepository = membershipRepository;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String registrationNumber = (String) execution.getVariable("registerNumber");
    String requestId = (String )execution.getVariable(PROCESS_REQUEST_ID);
    String userId = authenticationService.getCurrentUserId();
    LOGGER.info("#########  Get Branch Task.. Register Number: " + registrationNumber + ", Request ID: " + requestId + " , User ID: " + userId);

    try
    {
      String branchNumber = getGroupId();

      String familyIncomeString = (String) execution.getVariable("family_income_string");
      if (familyIncomeString.equals("0 - 488,940"))
      {
        execution.setVariable("family_income", Long.valueOf(300000));
      }
      else if (familyIncomeString.equals("488,941 - 627,812"))
      {
        execution.setVariable("family_income", Long.valueOf(500000));
      }
      else if (familyIncomeString.equals("627,813 - 948,608"))
      {
        execution.setVariable("family_income", Long.valueOf(700000));
      }
      else if (familyIncomeString.equals("948,608+"))
      {
        execution.setVariable("family_income", Long.valueOf(1000000));
      }

      //this is done to always add 76 points in scoring dmn
      execution.removeVariable("pro_btw_coll_ln");
      execution.setVariable("pro_btw_coll_ln", Double.valueOf(2));

      execution.removeVariable("area");
      execution.setVariable("area", branchNumber);

      LOGGER.info("#########  Finished Get Branch Task...");
    }
    catch (AimRepositoryException | NullPointerException e)
    {
      throw new ProcessTaskException(e.getMessage());
    }
  }

  private String getGroupId() throws AimRepositoryException
  {
    String currentUserId = authenticationService.getCurrentUserId();
    List<Membership> memberships = membershipRepository.listAllByUserId(TenantId.valueOf("xac"), UserId.valueOf(currentUserId));
    return memberships.get(0).getGroupId().getId();
  }
}
