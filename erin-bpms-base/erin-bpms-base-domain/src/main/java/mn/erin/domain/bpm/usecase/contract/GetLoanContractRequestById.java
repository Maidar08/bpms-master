package mn.erin.domain.bpm.usecase.contract;

import java.util.Objects;

import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.contract.LoanContractRequest;
import mn.erin.domain.bpm.model.process.ProcessRequestId;
import mn.erin.domain.bpm.repository.LoanContractRequestRepository;

/**
 * @author Temuulen Naranbold
 */
public class GetLoanContractRequestById extends AuthorizedUseCase<String, LoanContractRequest>
{
  private static final Permission permission = new AimModulePermission("GetLoanContractRequestById");
  private final LoanContractRequestRepository loanContractRequestRepository;

  public GetLoanContractRequestById(LoanContractRequestRepository loanContractRequestRepository,
      AuthenticationService authenticationService, AuthorizationService authorizationService)
  {
    super(authenticationService, authorizationService);
    this.loanContractRequestRepository = Objects.requireNonNull(loanContractRequestRepository, "Loan contract repository is required!");
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected LoanContractRequest executeImpl(String input) throws UseCaseException
  {
    try
    {
      return loanContractRequestRepository.findById(ProcessRequestId.valueOf(input));
    }
    catch (IllegalArgumentException | NullPointerException e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }
}
