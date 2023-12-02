package mn.erin.domain.bpm.usecase.organization;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.BpmModulePermission;
import mn.erin.domain.bpm.model.organization.OrganizationLeasing;
import mn.erin.domain.bpm.model.organization.OrganizationRequest;
import mn.erin.domain.bpm.model.organization.OrganizationRequestId;
import mn.erin.domain.bpm.model.organization.OrganizationSalary;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.OrganizationLeasingRepository;
import mn.erin.domain.bpm.repository.OrganizationSalaryRepository;

import static mn.erin.domain.bpm.BpmMessagesConstants.GROUP_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.GROUP_ID_NULL_MESSAGE;

public class GetGroupOrganizationRequests extends AuthorizedUseCase<String, Collection<OrganizationRequest>>
{
  private static final Permission permission = new BpmModulePermission("GetBranchOrganizationRequests");
  private final GroupRepository groupRepository;
  private final OrganizationSalaryRepository organizationSalaryRepository;
  private final OrganizationLeasingRepository organizationLeasingRepository;

  public GetGroupOrganizationRequests(AuthenticationService authenticationService, AuthorizationService authorizationService, GroupRepository groupRepository,
      OrganizationSalaryRepository organizationSalaryRepository, OrganizationLeasingRepository organizationLeasingRepository)
  {
    super(authenticationService, authorizationService);
    this.groupRepository = Objects.requireNonNull(groupRepository, "Group repository is required!");
    this.organizationSalaryRepository = Objects.requireNonNull(organizationSalaryRepository, "Organization salary repository is required!");
    this.organizationLeasingRepository = Objects.requireNonNull(organizationLeasingRepository, "Organization leasing repository is required!");
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected Collection executeImpl(String groupId) throws UseCaseException
  {

    Collection<OrganizationSalary> salaryOrganizations;
    Collection<OrganizationLeasing> leasingOrganizations;

    Collection<OrganizationRequest> groupOrganizationRequests = new ArrayList<>();

    if (StringUtils.isBlank(groupId))
    {
      throw new UseCaseException(GROUP_ID_NULL_CODE, GROUP_ID_NULL_MESSAGE);
    }
    else
    {
      Group group = groupRepository.findById(GroupId.valueOf(groupId));
      if (null == group)
      {
        String errorCode = "BPMS044";
        throw new UseCaseException(errorCode, "Group with id [" + groupId + "] doesn't exist!");
      }
      try
      {
        salaryOrganizations = organizationSalaryRepository.findByGroupId(group.getId());
        leasingOrganizations = organizationLeasingRepository.findByGroupId(group.getId());

        groupOrganizationRequests.addAll(toOrganizationRequests(salaryOrganizations));
        groupOrganizationRequests.addAll(toOrganizationRequests(leasingOrganizations));

        return groupOrganizationRequests;
      }
      catch (BpmRepositoryException e)
      {
        throw new UseCaseException(e.getMessage());
      }
    }
  }

  private Collection<? extends OrganizationRequest> toOrganizationRequests(Collection<? extends OrganizationRequest> extendedRequests)
  {
    Collection<OrganizationRequest> organizationRequests = new ArrayList<>();

    for (OrganizationRequest extendedRequest : extendedRequests)
    {
      OrganizationRequestId orgRequestId = extendedRequest.getId();
      String assignee = extendedRequest.getAssignee();

      String registerNumber = extendedRequest.getOrganizationName();
      String name = extendedRequest.getOrganizationNumber();

      LocalDateTime modifiedDate = extendedRequest.getContractDate();
      String modifiedUserId = extendedRequest.getCifNumber();

      String branchId = extendedRequest.getBranchId();
      String state = extendedRequest.getState();

      String confirmedUser = extendedRequest.getConfirmedUser();

      organizationRequests.add(new OrganizationRequest(orgRequestId, assignee, registerNumber, name, modifiedDate, modifiedUserId, branchId, state, confirmedUser));
    }
    return organizationRequests;
  }
}
