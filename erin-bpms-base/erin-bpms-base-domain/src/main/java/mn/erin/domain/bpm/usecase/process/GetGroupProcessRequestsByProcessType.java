package mn.erin.domain.bpm.usecase.process;

import java.util.Collection;

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
import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.util.process.ProcessRequestUtils;

import static mn.erin.domain.bpm.BpmMessagesConstants.GROUP_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.GROUP_ID_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PROCESS_TYPE_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PROCESS_TYPE_ID_NULL_MESSAGE;

public class GetGroupProcessRequestsByProcessType extends AuthorizedUseCase<GetGroupProcessRequestsByProcessTypeInput, Collection<ProcessRequest>>
{

  private static final Permission permission = new BpmModulePermission("GetGroupProcessRequestsByProcessType");

  private final ProcessRequestRepository processRequestRepository;
  private final GroupRepository groupRepository;

  public GetGroupProcessRequestsByProcessType(AuthenticationService authenticationService,
      AuthorizationService authorizationService, ProcessRequestRepository processRequestRepository, GroupRepository groupRepository)
  {
    super(authenticationService, authorizationService);
    this.processRequestRepository = processRequestRepository;
    this.groupRepository = groupRepository;
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected Collection<ProcessRequest> executeImpl(GetGroupProcessRequestsByProcessTypeInput input) throws UseCaseException
  {
    Collection<ProcessRequest> processRequests;
    if (null == input.groupId)
    {
      throw new UseCaseException(GROUP_ID_NULL_CODE, GROUP_ID_NULL_MESSAGE);
    }
    if (null == input.processTypeId)
    {
      throw new UseCaseException(PROCESS_TYPE_ID_NULL_CODE, PROCESS_TYPE_ID_NULL_MESSAGE);
    }

    if (StringUtils.isEmpty(input.groupId))
    {
      processRequests = ProcessRequestUtils.filterProcessRequestByState(processRequestRepository.findAll());
    }
    else
    {
      Group group = groupRepository.findById(GroupId.valueOf(input.groupId));
      if (null == group)
      {
        String errorCode = "BPMS044";
        throw new UseCaseException(errorCode, "Group with id [" + input.groupId + "] doesn't exist!");
      }

      try
      {
        processRequests = processRequestRepository.findAllByGroupIdAndProcessType(group.getId(), input.processTypeId);
      }
      catch (BpmRepositoryException e)
      {
        throw new UseCaseException(e.getMessage());
      }
    }
    return processRequests;
  }
}
