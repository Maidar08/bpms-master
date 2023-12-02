package mn.erin.domain.bpm.usecase.customer;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.repository.ErrorMessageRepository;

public class GetErrorMessage extends AbstractUseCase<String, String>
{
  private final ErrorMessageRepository errorMessageRepository;

  public GetErrorMessage(ErrorMessageRepository errorMessageRepository)
  {
    this.errorMessageRepository = errorMessageRepository;
  }

  @Override
  public String execute(String errorCode) throws UseCaseException
  {
    if (StringUtils.isBlank(errorCode))
    {
      throw new UseCaseException("Error code cannot be blank!");
    }
    return errorMessageRepository.getMessage(errorCode);
  }
}
