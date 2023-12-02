package mn.erin.domain.bpm.usecase.contract;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.ProcessInstanceId;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.LoanContractParameterRepository;

/**
 * @author Temuulen Naranbold
 */
public class UpdateLoanContractParameter extends AuthorizedUseCase<LoanContractParameterInput, Void>
{
  private static final Permission permission = new AimModulePermission("CreateLoanContractParameter");
  private final LoanContractParameterRepository loanContractParameterRepository;

  public UpdateLoanContractParameter(LoanContractParameterRepository loanContractParameterRepository,
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
  protected Void executeImpl(LoanContractParameterInput input) throws UseCaseException
  {
    try
    {
      Validate.notNull(input);
      loanContractParameterRepository.update(ProcessInstanceId.valueOf(input.getProcessInstanceId()), input.getDefKey(),
          input.getParameterValue(), input.getParameterEntityType());
      final Map<String, Object> tableMap = input.getTableMap();
      if (null != tableMap && !tableMap.isEmpty())
      {
        loanContractParameterRepository.update(ProcessInstanceId.valueOf(input.getProcessInstanceId()), input.getDefKey(),
            input.getTableMap(), "JSON");
      }
      return null;
    }
    catch (NullPointerException | IllegalArgumentException | BpmRepositoryException e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }
}
