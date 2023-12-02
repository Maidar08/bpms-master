package mn.erin.domain.bpm.usecase.organization;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.organization.OrganizationLeasing;
import mn.erin.domain.bpm.model.organization.OrganizationRequest;
import mn.erin.domain.bpm.model.organization.OrganizationRequestId;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.OrganizationLeasingRepository;

import static mn.erin.domain.bpm.BpmMessagesConstants.ORG_REG_NUM_NULL_ERROR_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.REGISTER_NUMBER_BLANK_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.REGISTER_NUMBER_BLANK_MESSAGE;

public class GetLeasingOrganizationRegNumber extends AbstractUseCase<String, Collection<OrganizationRequest>>
{
  private final OrganizationLeasingRepository organizationLeasingRepository;

  public GetLeasingOrganizationRegNumber(OrganizationLeasingRepository organizationLeasingRepository)
  {
    this.organizationLeasingRepository = organizationLeasingRepository;
  }

  @Override
  public Collection<OrganizationRequest> execute(String regNumber) throws UseCaseException
  {
    if (StringUtils.isBlank(regNumber))
    {
      throw new UseCaseException(REGISTER_NUMBER_BLANK_CODE, REGISTER_NUMBER_BLANK_MESSAGE);
    }
    try
    {
      Collection<OrganizationLeasing> orgLeasing = organizationLeasingRepository.findByRegNumber(regNumber);

      Collection<OrganizationRequest> organizationRequests = new ArrayList<>();

      organizationRequests.addAll(toOrganizationRequests(orgLeasing));

      return organizationRequests;
    }
    catch (BpmRepositoryException e)
    {
      throw new UseCaseException(ORG_REG_NUM_NULL_ERROR_CODE,
          e.getErrorMessage());
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

      organizationRequests.add(new OrganizationRequest(orgRequestId, assignee,
          registerNumber, name,
          modifiedDate, modifiedUserId,
          branchId, state, confirmedUser));
    }

    return organizationRequests;
  }
}
