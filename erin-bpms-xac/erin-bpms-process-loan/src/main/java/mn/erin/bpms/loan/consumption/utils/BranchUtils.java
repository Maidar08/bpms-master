package mn.erin.bpms.loan.consumption.utils;

import java.util.List;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.base.usecase.UseCaseException;

import static mn.erin.domain.bpm.BpmMessagesConstants.GET_BRANCH_ERROR_CODE;

/**
 * @author Tamir
 */
public final class BranchUtils
{
  private BranchUtils()
  {

  }

  public static String getBranchId(String tenantId, AuthenticationService authenticationService, MembershipRepository membershipRepository)
      throws UseCaseException
  {
    try
    {
      String currentUserId = authenticationService.getCurrentUserId();
      List<Membership> membershipList = membershipRepository.listAllByUserId(TenantId.valueOf(tenantId), UserId.valueOf(currentUserId));

      Membership membership = membershipList.get(0);
      return membership.getGroupId().getId();
    }
    catch (AimRepositoryException e)
    {
      throw new UseCaseException(GET_BRANCH_ERROR_CODE, e.getMessage());
    }
  }
}
