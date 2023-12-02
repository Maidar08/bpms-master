package mn.erin.domain.bpm.usecase.customer;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CoreBankingService;

import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;

/**
 * @author Zorig
 */
public class GetCustomerLoanPeriod extends AbstractUseCase<GetCustomerLoanPeriodInput, Integer>
{
  private final CoreBankingService coreBankingService;

  public GetCustomerLoanPeriod(CoreBankingService coreBankingService)
  {
    this.coreBankingService = Objects.requireNonNull(coreBankingService, "Core banking service is required!");
  }

  @Override
  public Integer execute(GetCustomerLoanPeriodInput input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
    }

    if (StringUtils.isBlank(input.getCustomerCif()))
    {
      String errorCode = "CBS013";
      throw new UseCaseException(errorCode, "Invalid Customer CIF or Organization CIF input!");
    }

    try
    {
      return coreBankingService.getCustomerLoanPeriodInformation(input.getCustomerCif());
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage(), e);
    }
  }
}
