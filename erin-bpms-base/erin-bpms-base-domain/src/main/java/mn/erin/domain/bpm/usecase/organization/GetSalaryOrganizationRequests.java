package mn.erin.domain.bpm.usecase.organization;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.BpmModulePermission;
import mn.erin.domain.bpm.model.organization.OrganizationRequest;
import mn.erin.domain.bpm.model.organization.OrganizationRequestId;
import mn.erin.domain.bpm.model.organization.OrganizationSalary;
import mn.erin.domain.bpm.model.process.ProcessInstanceId;
import mn.erin.domain.bpm.repository.OrganizationSalaryRepository;

/**
 * @author Tamir
 */
public class GetSalaryOrganizationRequests extends AuthorizedUseCase<GetAllOrganizationRequestInput, GetAllOrganizationRequestsOutput>
{
  private static final Permission permission = new BpmModulePermission("GetSalaryOrganizationRequests");

  private final OrganizationSalaryRepository organizationSalaryRepository;

  public GetSalaryOrganizationRequests(AuthenticationService authenticationService, AuthorizationService authorizationService,
      OrganizationSalaryRepository organizationSalaryRepository)
  {
    super(authenticationService, authorizationService);
    this.organizationSalaryRepository = Validate.notNull(organizationSalaryRepository);
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected GetAllOrganizationRequestsOutput executeImpl(GetAllOrganizationRequestInput input) throws UseCaseException
  {
    Collection<OrganizationSalary> allOrgSalary = organizationSalaryRepository.findAllByGivenDate(input.getStartDate(), input.getEndDate());

    Collection<OrganizationRequest> organizationRequests = new ArrayList<>();

    organizationRequests.addAll(toOrganizationRequests(allOrgSalary));

    return new GetAllOrganizationRequestsOutput(organizationRequests);
  }

  private Collection<? extends OrganizationRequest> toOrganizationRequests(Collection<? extends OrganizationRequest> extendedRequests)
  {
    Collection<OrganizationRequest> organizationRequests = new ArrayList<>();

    for (OrganizationRequest extendedRequest : extendedRequests)
    {
      OrganizationRequestId orgRequestId = extendedRequest.getId();
      ProcessInstanceId instanceId = extendedRequest.getInstanceId();

      String assignee = extendedRequest.getAssignee();

      String registerNumber = extendedRequest.getOrganizationName();
      String name = extendedRequest.getOrganizationNumber();

      LocalDateTime modifiedDate = extendedRequest.getContractDate();
      String modifiedUserId = extendedRequest.getCifNumber();

      String branchId = extendedRequest.getBranchId();
      String state = extendedRequest.getState();
      String confirmedUser = extendedRequest.getConfirmedUser();

      organizationRequests.add(new OrganizationRequest(orgRequestId, instanceId, assignee,
          registerNumber, name,
          modifiedDate, modifiedUserId,
          branchId, state,
          confirmedUser ));
    }

    return organizationRequests;
  }
}
