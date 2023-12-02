package mn.erin.domain.bpm.usecase.customer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.repository.ErrorMessageRepository;

/**
 * @author Bilguunbor
 */
public class GetErrorMessageTest
{
  private GetErrorMessage useCase;

  @Before
  public void setUp()
  {
    ErrorMessageRepository errorMessageRepository = Mockito.mock(ErrorMessageRepository.class);
    useCase = new GetErrorMessage(errorMessageRepository);
  }

  @Test(expected = UseCaseException.class)
  public void when_error_code_is_blank() throws UseCaseException
  {
    useCase.execute(" ");
  }

  @Test(expected = UseCaseException.class)
  public void when_error_code_is_null() throws UseCaseException
  {
    useCase.execute(null);
  }
}
