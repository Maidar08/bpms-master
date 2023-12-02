package mn.erin.domain.bpm.usecase.process;

import java.util.Collection;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.MessageConstants;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.BpmModulePermission;
import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.util.process.ProcessRequestUtils;

/**
 * @author Lkhagvadorj
 */

public class GetProcessRequestBySearchKey extends AuthorizedUseCase<GetProcessRequestBySearchKeyInput, GetProcessRequestsOutput>
{
  public static final Permission permission = new BpmModulePermission("SearchByRegisterNumber");
  public final ProcessRequestRepository processRequestRepository;
  public final GroupRepository groupRepository;

  public GetProcessRequestBySearchKey(AuthenticationService authenticationService,
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
  protected GetProcessRequestsOutput executeImpl(GetProcessRequestBySearchKeyInput input) throws UseCaseException
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

      GetProcessRequestsByLoanRequestType getProcessRequestsByLoanRequestType = new GetProcessRequestsByLoanRequestType(authenticationService, authorizationService, processRequestRepository, groupRepository);
      Collection<ProcessRequest> processRequests = getProcessRequestsByLoanRequestType.executeImpl(input.getProcessRequestsByLoanRequestTypeInput()).getProcessRequests();

      if (null == processRequests)
      {
        return new GetProcessRequestsOutput(null);
      }

      Collection<ProcessRequest> returnedProcessRequestList = ProcessRequestUtils.findBySearchKey(processRequests, input.getSearchKey());

      return new GetProcessRequestsOutput(returnedProcessRequestList);
    }

    catch (RuntimeException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}
