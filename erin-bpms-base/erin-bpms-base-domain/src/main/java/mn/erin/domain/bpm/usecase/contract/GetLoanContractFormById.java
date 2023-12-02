package mn.erin.domain.bpm.usecase.contract;

import java.util.Objects;

import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.contract.LoanContractParameter;
import mn.erin.domain.bpm.model.process.ProcessInstanceId;
import mn.erin.domain.bpm.repository.LoanContractParameterRepository;

/**
 * @author Temuulen Naranbold
 */
public class GetLoanContractFormById extends AuthorizedUseCase<String, LoanContractParameter>
{
  private static final Permission permission = new AimModulePermission("GetLoanContractFromById");
  private final LoanContractParameterRepository loanContractParameterRepository;

  public GetLoanContractFormById(LoanContractParameterRepository loanContractParameterRepository,
      AuthenticationService authenticationService, AuthorizationService authorizationService)
  {
    super(authenticationService, authorizationService);
    this.loanContractParameterRepository = Objects.requireNonNull(loanContractParameterRepository, "Loan contract parameter repository is required!");
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected LoanContractParameter executeImpl(String input) throws UseCaseException
  {
    try
    {
      return loanContractParameterRepository.findById(ProcessInstanceId.valueOf(input));
    }
    catch (IllegalArgumentException | NullPointerException e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }
}
