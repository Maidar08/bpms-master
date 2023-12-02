package mn.erin.domain.bpm.usecase.contract;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.BpmModulePermission;
import mn.erin.domain.bpm.model.contract.LoanContractRequest;
import mn.erin.domain.bpm.repository.LoanContractRequestRepository;

import static mn.erin.domain.bpm.BpmMessagesConstants.COULD_NOT_GET_ALL_LOAN_CONTRACT_REQUEST_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.COULD_NOT_GET_ALL_LOAN_CONTRACT_REQUEST_MESSAGE;

public class GetAllLoanContractRequests extends AuthorizedUseCase<GetAllLoanContractRequestsInput, List<LoanContractRequest>>
{
  private static final Permission permission = new BpmModulePermission("GetAllLoanContractRequests");
  private final LoanContractRequestRepository loanContractRequestRepository;

  public GetAllLoanContractRequests(LoanContractRequestRepository loanContractRequestRepository, AuthenticationService authenticationService,
      AuthorizationService authorizationService)
  {
    super(authenticationService, authorizationService);
    this.loanContractRequestRepository = Objects.requireNonNull(loanContractRequestRepository, "Loan contract request repository is required!");
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected List<LoanContractRequest> executeImpl(GetAllLoanContractRequestsInput input) throws UseCaseException
  {
    try
    {
      return new ArrayList<>(loanContractRequestRepository.findAllByGivenDate(input.getStartDate(), input.getEndDate()));
    }
    catch (Exception e)
    {
      throw new UseCaseException(COULD_NOT_GET_ALL_LOAN_CONTRACT_REQUEST_CODE, COULD_NOT_GET_ALL_LOAN_CONTRACT_REQUEST_MESSAGE);
    }
  }
}
