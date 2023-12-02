package mn.erin.domain.bpm.usecase.process;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmMessagesConstants;
import mn.erin.domain.bpm.model.BpmModulePermission;
import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;

public class GetUnassignedRequestsByChannelWithFilter extends AuthorizedUseCase<GetUnassignedRequestsByChannelWithFilterInput, Collection<ProcessRequest>>
{
  private static final Permission permission = new BpmModulePermission("GetUnassignedRequestsByChannel");
  private final ProcessRequestRepository processRequestRepository;

  public GetUnassignedRequestsByChannelWithFilter(AuthenticationService authenticationService,
      AuthorizationService authorizationService, ProcessRequestRepository processRequestRepository)
  {
    super(authenticationService, authorizationService);
    this.processRequestRepository = Objects.requireNonNull(processRequestRepository, "ProcessRequestRepository is required!");
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected Collection<ProcessRequest> executeImpl(GetUnassignedRequestsByChannelWithFilterInput input) throws UseCaseException
  {
    if (input == null || StringUtils.isBlank(input.getChannelType()) || StringUtils.isBlank(input.getGroupId()))
    {
      throw new UseCaseException(BpmMessagesConstants.INVALID_INPUT_CODE, BpmMessagesConstants.INVALID_INPUT_MESSAGE);
    }

    try
    {
      List<ProcessRequest> processRequests = new ArrayList<>();
      Collection<ProcessRequest> returnedProcessRequests = processRequestRepository.findAllUnassignedProcessRequestsByChannelType(input.getChannelType(),
          input.getStartDate(), input.getEndDate());

      for (ProcessRequest processRequest: returnedProcessRequests)
      {
        if (processRequest.getGroupId() != null && processRequest.getGroupId().getId().equals(input.getGroupId()))
        {
          processRequests.add(processRequest);
        }
      }

      return processRequests;
    }
    catch (BpmRepositoryException e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }
}