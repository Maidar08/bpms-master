package mn.erin.domain.bpm.usecase.contract;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.aim.usecase.group.GetSubGroupIds;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.contract.LoanContractRequest;
import mn.erin.domain.bpm.repository.LoanContractRequestRepository;

import static mn.erin.domain.bpm.BpmMessagesConstants.END_DATE_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.END_DATE_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.GROUP_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.GROUP_ID_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.START_DATE_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.START_DATE_NULL_MESSAGE;

/**
 * @author Bilguunbor
 */

public class GetSubGroupLoanContractRequest extends AuthorizedUseCase<GetLoanContractProcessRequestInput, List<LoanContractRequest>>
{
  private static final Permission permission = new AimModulePermission("GetSubGroupLoanContractProcessRequest");

  private final GroupRepository groupRepository;
  private final LoanContractRequestRepository loanContractRequestRepository;

  public GetSubGroupLoanContractRequest(GroupRepository groupRepository, AuthenticationService authenticationService,
      AuthorizationService authorizationService, LoanContractRequestRepository loanContractRequestRepository)
  {
    super(authenticationService, authorizationService);
    this.groupRepository = Objects.requireNonNull(groupRepository, "Group repository is required!");
    this.loanContractRequestRepository = Objects.requireNonNull(loanContractRequestRepository, "Loan contract request repository is required!");
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected List<LoanContractRequest> executeImpl(GetLoanContractProcessRequestInput input) throws UseCaseException
  {
    validateNotBlank(input.getGroupId(), GROUP_ID_NULL_CODE + ": " + GROUP_ID_NULL_MESSAGE);
    validateNotNull(input.getStartDate(), START_DATE_NULL_CODE + ": " + START_DATE_NULL_MESSAGE);
    validateNotNull(input.getEndDate(), END_DATE_NULL_CODE + ": " + END_DATE_NULL_MESSAGE);

    GetSubGroupIds getSubGroupIds = new GetSubGroupIds(authenticationService, authorizationService, groupRepository);
    List<String> subGroupIds = getSubGroupIds.execute(input.getGroupId());

    GetLoanContractProcessRequest getLoanContractProcessRequest = new GetLoanContractProcessRequest(authenticationService, authorizationService,
        loanContractRequestRepository);
    List<LoanContractRequest> subGroupLoanContractRequestList = new ArrayList<>();

    for (String groupId : subGroupIds)
    {
      GetLoanContractProcessRequestInput requestInput = new GetLoanContractProcessRequestInput(groupId, input.getStartDate(), input.getEndDate());
      List<LoanContractRequest> loanContractRequestList = getLoanContractProcessRequest.execute(requestInput);

      if (!loanContractRequestList.isEmpty())
      {
        subGroupLoanContractRequestList.addAll(loanContractRequestList);
      }
    }

    return subGroupLoanContractRequestList;
  }
}
