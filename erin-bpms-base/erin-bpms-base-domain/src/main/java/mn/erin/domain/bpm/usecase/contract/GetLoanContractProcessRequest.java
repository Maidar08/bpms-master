package mn.erin.domain.bpm.usecase.contract;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.contract.LoanContractRequest;
import mn.erin.domain.bpm.repository.LoanContractRequestRepository;

import static mn.erin.domain.bpm.BpmMessagesConstants.GROUP_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.GROUP_ID_NULL_MESSAGE;

public class GetLoanContractProcessRequest extends AuthorizedUseCase<GetLoanContractProcessRequestInput, List<LoanContractRequest>>
{
  private static final Permission permission = new AimModulePermission("GetLoanContractProcessRequest");
  private final LoanContractRequestRepository loanContractRequestRepository;

  public GetLoanContractProcessRequest(AuthenticationService authenticationService, AuthorizationService authorizationService,
      LoanContractRequestRepository loanContractRequestRepository)
  {
    super(authenticationService, authorizationService);
    this.loanContractRequestRepository = loanContractRequestRepository;
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected List<LoanContractRequest> executeImpl(GetLoanContractProcessRequestInput input) throws UseCaseException
  {
    if (StringUtils.isBlank(input.getGroupId()))
    {
      throw new UseCaseException(GROUP_ID_NULL_CODE + ": " + GROUP_ID_NULL_MESSAGE);
    }

    try
    {
      return loanContractRequestRepository.findByGroupId(input.getGroupId(), input.getStartDate(), input.getEndDate());
    }
    catch (IllegalArgumentException | NullPointerException e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }
}
