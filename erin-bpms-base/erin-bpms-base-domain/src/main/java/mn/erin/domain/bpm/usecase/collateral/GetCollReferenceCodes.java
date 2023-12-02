package mn.erin.domain.bpm.usecase.collateral;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.NewCoreBankingService;

import static mn.erin.domain.bpm.BpmMessagesConstants.COLL_REF_TYPES_EMPTY_ERROR_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.SUBMIT_NULL_VALUE_ERROR_CODE;

/**
 * @author Tamir
 */
public class GetCollReferenceCodes extends AbstractUseCase<GetCollReferenceCodesInput, GetCollReferenceCodesOutput>
{
  private final NewCoreBankingService newCoreBankingService;

  public GetCollReferenceCodes(NewCoreBankingService newCoreBankingService)
  {
    this.newCoreBankingService = Objects.requireNonNull(newCoreBankingService, "Core banking service is required!");
  }

  @Override
  public GetCollReferenceCodesOutput execute(GetCollReferenceCodesInput input) throws UseCaseException
  {
    List<String> types = input.getTypes();

    if (types.isEmpty())
    {
      throw new UseCaseException(SUBMIT_NULL_VALUE_ERROR_CODE, COLL_REF_TYPES_EMPTY_ERROR_MESSAGE);
    }

    try
    {
      Map<String, List<String>> referenceCodes = newCoreBankingService.getCollReferenceCodes(types);

      return new GetCollReferenceCodesOutput(referenceCodes);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
  }
}
