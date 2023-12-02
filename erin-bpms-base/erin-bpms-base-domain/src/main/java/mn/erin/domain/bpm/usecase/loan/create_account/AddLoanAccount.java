package mn.erin.domain.bpm.usecase.loan.create_account;

import java.util.List;
import java.util.Map;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.NewCoreBankingService;

/**
 * @author Tamir
 */
public class AddLoanAccount extends AbstractUseCase<AddLoanAccountInput, AddLoanAccountOutput>
{
  // TODO : rename use case class name by CreateLoanAccount after core integration is done.
  private final NewCoreBankingService newCoreBankingService;

  public AddLoanAccount(NewCoreBankingService newCoreBankingService)
  {
    this.newCoreBankingService = newCoreBankingService;
  }

  @Override
  public AddLoanAccountOutput execute(AddLoanAccountInput input) throws UseCaseException
  {
    Map<String, Object> genericInfo = input.getGenericInfo();
    Map<String, Object> additionalInfos = input.getAdditionalInfos();
    List<Map<String, Object>> coBorrowers = input.getCoBorrowers();

    return createLoanAccount(genericInfo, additionalInfos, coBorrowers);
  }

  private AddLoanAccountOutput createLoanAccount(Map<String, Object> genericInfo, Map<String, Object> additionalInfos, List<Map<String, Object>> coBorrowers)
      throws UseCaseException
  {
    try
    {
      String accountNumber = newCoreBankingService.createLoanAccount(genericInfo, additionalInfos, coBorrowers);

      return new AddLoanAccountOutput(accountNumber);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
  }
}
