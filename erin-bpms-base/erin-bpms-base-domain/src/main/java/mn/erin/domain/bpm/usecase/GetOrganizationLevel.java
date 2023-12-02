package mn.erin.domain.bpm.usecase;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmMessagesConstants;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.OrganizationService;

/**
 * @author Zorig
 */
public class GetOrganizationLevel extends AbstractUseCase<String, Map<String, String>>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GetOrganizationLevel.class);

  private final OrganizationService organizationService;
  private final AuthenticationService authenticationService;
  private final MembershipRepository membershipRepository;

  public GetOrganizationLevel(OrganizationService organizationService, AuthenticationService authenticationService,
      MembershipRepository membershipRepository)
  {
    this.organizationService = Objects.requireNonNull(organizationService, "Customer service is required!");
    this.authenticationService = Objects.requireNonNull(authenticationService, "Authentication is required!");
    this.membershipRepository = Objects.requireNonNull(membershipRepository, "Membership Repository is required!");
  }

  @Override
  public Map<String, String> execute(String input) throws UseCaseException
  {
    if (StringUtils.isBlank(input))
    {
      throw new UseCaseException(BpmMessagesConstants.INPUT_NULL_CODE, BpmMessagesConstants.INPUT_NULL_MESSAGE);
    }

    try
    {
      String currentUserId = authenticationService.getCurrentUserId();
      List<Membership> membershipList = membershipRepository.listAllByUserId(TenantId.valueOf("xac"), UserId.valueOf(currentUserId));
      Membership membership = membershipList.get(0);
      LOGGER.info("#########  GetOrganzationLevel Use Case: - UserId - " + currentUserId + " Membership - " + membership.getMembershipId().getId() + " Role:" + membership.getRoleId().getId());
      String branch = membership.getGroupId().getId();

      return organizationService.getOrganizationLevel(input, branch);
    }
    catch (BpmServiceException | AimRepositoryException e)
    {
      if (e instanceof BpmServiceException)
      {
        throw new UseCaseException(((BpmServiceException) e).getCode(), e.getMessage());
      }
      throw new UseCaseException(e.getMessage());
    }

  }
}
