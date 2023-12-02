package mn.erin.domain.bpm.usecase.process;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.MessageConstants;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.model.BpmModulePermission;
import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.util.process.ProcessRequestUtils;

public class GetProcessRequestsByLoanRequestType extends AuthorizedUseCase<GetProcessRequestsByLoanRequestTypeInput, GetProcessRequestsOutput>
{
  public static final Permission permission = new BpmModulePermission("SearchByLoanRequestType");
  public final ProcessRequestRepository processRequestRepository;
  public final GroupRepository groupRepository;

  public GetProcessRequestsByLoanRequestType(AuthenticationService authenticationService,
      AuthorizationService authorizationService, ProcessRequestRepository processRequestRepository,
      GroupRepository groupRepository)
  {
    super(authenticationService, authorizationService);
    this.processRequestRepository = Objects.requireNonNull(processRequestRepository, "Process Request Repository is required!");
    this.groupRepository = groupRepository;
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected GetProcessRequestsOutput executeImpl(GetProcessRequestsByLoanRequestTypeInput input) throws UseCaseException
  {
    if (null == input)
    {
      throw new UseCaseException(MessageConstants.INPUT_REQUIRED_CODE, MessageConstants.INPUT_REQUIRED);
    }

    try
    {
      String loanSearchType = input.getLoanRequestType();

      if (StringUtils.isBlank(loanSearchType))
      {
        throw new UseCaseException(MessageConstants.NULL_PROCESS_REQUEST_TYPE_CODE, MessageConstants.NULL_PROCESS_REQUEST_TYPE);
      }

      String groupId = input.getGroupId();
      String channelType = input.getChannelType();

      String currentUserId = authenticationService.getCurrentUserId();
      Collection<ProcessRequest> processRequests = getProcessRequests(loanSearchType, currentUserId, groupId, channelType);
      if (null != processRequests)
      {
        Collection<ProcessRequest> returnProcessRequests = ProcessRequestUtils.filterProcessRequestByState(processRequests);
        return new GetProcessRequestsOutput(returnProcessRequests);
      }

      return new GetProcessRequestsOutput(null);
    }

    catch (RuntimeException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }

  private Collection<ProcessRequest> getProcessRequests(String loanSearchType, String currentUserId, String groupId, String channelType)
      throws UseCaseException
  {
    switch (loanSearchType)
    {
    case BpmModuleConstants.MY_LOAN_REQUEST:
      return getRequestsByAssignedUserId(currentUserId);
    case BpmModuleConstants.SEARCH_CUSTOMER:
      return processRequestRepository.findAll();

    case BpmModuleConstants.BRANCH_LOAN_REQUEST:
      if (StringUtils.isBlank(groupId))
      {
        throw new UseCaseException(MessageConstants.NULL_GROUP_ID_CODE, MessageConstants.NULL_GROUP_ID);
      }
      return getRequestsByGroupId(groupId);

    case BpmModuleConstants.ALL_LOAN_REQUEST:
      return processRequestRepository.findAll();

    case BpmModuleConstants.INTERNET_BANK_LOAN_REQUEST:
      if (StringUtils.isBlank(groupId))
      {
        throw new UseCaseException(MessageConstants.NULL_GROUP_ID_CODE, MessageConstants.NULL_GROUP_ID);
      }
      if (StringUtils.isBlank(channelType))
      {
        throw new UseCaseException(MessageConstants.NULL_CHANNEL_CODE, MessageConstants.NULL_CHANNEL);
      }
      return getRequestsByChannel(groupId, channelType);

    case BpmModuleConstants.SUB_GROUP_REQUEST:
      if (StringUtils.isBlank(groupId))
      {
        throw new UseCaseException(MessageConstants.NULL_GROUP_ID_CODE, MessageConstants.NULL_GROUP_ID);
      }
      return getSubGroupRequest(groupId);

    default:
      return Collections.emptyList();
    }
  }

  private Collection<ProcessRequest> getRequestsByAssignedUserId(String currentUserId) throws UseCaseException
  {
    GetProcessRequestsByAssignedUserId getProcessRequestsByAssignedUserId = new GetProcessRequestsByAssignedUserId(authenticationService, authorizationService,
        processRequestRepository);
    GetProcessRequestsByAssignedUserIdInput getProcessRequestsByAssignedUserIdInput = new GetProcessRequestsByAssignedUserIdInput(currentUserId);
    GetProcessRequestsOutput getProcessRequestsByAssignedUserIdOutput = getProcessRequestsByAssignedUserId.executeImpl(getProcessRequestsByAssignedUserIdInput);
    return getProcessRequestsByAssignedUserIdOutput.getProcessRequests();
  }

  private Collection<ProcessRequest> getRequestsByGroupId(String groupId) throws UseCaseException
  {
    GetGroupProcessRequests getGroupProcessRequests = new GetGroupProcessRequests(authenticationService, authorizationService, processRequestRepository,
        groupRepository);

    GetProcessRequestsByDateInput input = new GetProcessRequestsByDateInput(groupId, null, null);
    return getGroupProcessRequests.execute(input);
  }

  private Collection<ProcessRequest> getRequestsByChannel(String groupId, String channelType) throws UseCaseException
  {
    GetUnassignedRequestsByChannel getUnassignedRequestsByChannel = new GetUnassignedRequestsByChannel(authenticationService, authorizationService,
        processRequestRepository);
    GetUnassignedRequestsByChannelInput getUnassignedRequestsByChannelInput = new GetUnassignedRequestsByChannelInput(channelType, groupId);
    return getUnassignedRequestsByChannel.execute(getUnassignedRequestsByChannelInput);
  }

  private Collection<ProcessRequest> getSubGroupRequest(String groupId) throws UseCaseException
  {
    GetSubGroupProcessRequests subGroupProcessRequests = new GetSubGroupProcessRequests(groupRepository, authorizationService, authenticationService,
        processRequestRepository);
    GetProcessRequestsByDateInput input = new GetProcessRequestsByDateInput(groupId, null, null);
    return subGroupProcessRequests.execute(input);
  }
}
