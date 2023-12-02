package mn.erin.bpm.base.repository.memory;

import org.springframework.stereotype.Repository;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.repository.ErrorMessageRepository;

@Repository
public class InMemoryErrorMessageRepository implements ErrorMessageRepository
{
  @Override
  public String getMessage(String code)
  {
    return null;
  }

  @Override
  public void checkRegisterNumber(String registerNumber) throws UseCaseException
  {

  }
}
