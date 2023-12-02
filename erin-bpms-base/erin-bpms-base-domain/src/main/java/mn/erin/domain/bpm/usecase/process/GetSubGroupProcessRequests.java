package mn.erin.domain.bpm.usecase.process;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.aim.usecase.group.GetSubGroupIds;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.BpmModulePermission;
import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.util.process.ProcessRequestUtils;

public class GetSubGroupProcessRequests extends AuthorizedUseCase<GetProcessRequestsByDateInput, Collection<ProcessRequest >>
{
  private static final Permission permission = new BpmModulePermission("GetSubGroupProcessRequests");

  private final GroupRepository groupRepository;
  private final ProcessRequestRepository processRequestRepository;

  public GetSubGroupProcessRequests()
  {
    super();
    this.groupRepository = null;
    this.processRequestRepository = null;
  }

  public GetSubGroupProcessRequests(GroupRepository groupRepository, AuthorizationService authorizationService,
      AuthenticationService authenticationService, ProcessRequestRepository processRequestRepository)
  {
    super(authenticationService, authorizationService);
    this.groupRepository = groupRepository;
    this.processRequestRepository = processRequestRepository;
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected Collection<ProcessRequest> executeImpl(GetProcessRequestsByDateInput input) throws UseCaseException
  {
    GetSubGroupIds getSubGroupIds = new GetSubGroupIds(authenticationService, authorizationService, groupRepository);
    List <String> subGroupIds = getSubGroupIds.execute(input.getGroupId());

    GetGroupProcessRequests groupProcessRequests = new GetGroupProcessRequests(authenticationService, authorizationService, processRequestRepository, groupRepository);
    Collection<ProcessRequest> processRequests = new ArrayList<>();

//    assert processRequestRepository != null;
//    Collection<ProcessRequest> allProcessRequests =  processRequestRepository.findAll();

    for (String groupId: subGroupIds)
    {
      GetProcessRequestsByDateInput input1 = new GetProcessRequestsByDateInput(groupId, input.getStartDate(), input.getEndDate());
      Collection<ProcessRequest> tmpProcessRequest = groupProcessRequests.execute(input1);
      if (!tmpProcessRequest.isEmpty())
      {
        processRequests.addAll(tmpProcessRequest);
      }
//      processRequests.addAll(getGroupProcessRequests(groupId, allProcessRequests));
    }
    return ProcessRequestUtils.filterProcessRequestByState(processRequests);
  }

  private List<ProcessRequest> getGroupProcessRequests(String groupId, Collection<ProcessRequest> allProcessRequests)
  {
    return allProcessRequests.stream().filter(processRequest -> String.valueOf(processRequest.getGroupId()).equals(groupId)).collect(Collectors.toList());
  }
}
