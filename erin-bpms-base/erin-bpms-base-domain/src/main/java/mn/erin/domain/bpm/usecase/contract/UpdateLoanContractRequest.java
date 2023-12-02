package mn.erin.domain.bpm.usecase.contract;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.repository.LoanContractRequestRepository;

import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;

public class UpdateLoanContractRequest extends AbstractUseCase<UpdateLoanContractRequestInput, Boolean>{

  private final LoanContractRequestRepository loanContractRequestRepository;

  public UpdateLoanContractRequest(LoanContractRequestRepository loanContractRequestRepository)
  {
    this.loanContractRequestRepository = loanContractRequestRepository;
  }

  @Override
  public Boolean execute(UpdateLoanContractRequestInput input) throws UseCaseException
  {
    if (input == null){
      throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_CODE);
    }

    if (StringUtils.isBlank(input.getProcessRequestId()))
    {
      return loanContractRequestRepository.update(input.getProcessInstanceId(), input.getParameterValue());
    }
    return loanContractRequestRepository.update(input.getProcessInstanceId(), input.getProcessRequestId(), input.getParameterValue());
  }
}




