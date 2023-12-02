package mn.erin.domain.bpm.usecase.process;

import java.util.Collection;
import java.util.Date;

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

import static mn.erin.domain.bpm.BpmMessagesConstants.GROUP_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.GROUP_ID_NULL_MESSAGE;

/**
 * @author EBazarragchaa
 */
public class GetGroupProcessRequests extends AuthorizedUseCase<GetProcessRequestsByDateInput, Collection<ProcessRequest>>
{
  private static final Permission permission = new BpmModulePermission("GetGroupProcessRequests");
  private final ProcessRequestRepository processRequestRepository;
  private final GroupRepository groupRepository;

  public GetGroupProcessRequests(AuthenticationService authenticationService,
    AuthorizationService authorizationService, ProcessRequestRepository processRequestRepository,
    GroupRepository groupRepository)
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
  protected Collection<ProcessRequest> executeImpl(GetProcessRequestsByDateInput input) throws UseCaseException
  {
    Collection<ProcessRequest> processRequests;
    String groupId = input.getGroupId();
    Date startDate = input.getStartDate();
    Date endDate = input.getEndDate();

    if (null == groupId)
    {
      throw new UseCaseException(GROUP_ID_NULL_CODE, GROUP_ID_NULL_MESSAGE);
    }
    if (StringUtils.isEmpty(groupId))
    {
      processRequests = processRequestRepository.findAllRequestsByDate(startDate, endDate);
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
        processRequests = processRequestRepository.findAllByUserGroupIdDate(group.getId(),startDate, endDate);
      }
      catch (BpmRepositoryException e)
      {
        throw new UseCaseException(e.getMessage());
      }
    }
    return processRequests;
  }
}
