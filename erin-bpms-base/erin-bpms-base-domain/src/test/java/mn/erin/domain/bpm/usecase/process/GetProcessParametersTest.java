package mn.erin.domain.bpm.usecase.process;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.Process;
import mn.erin.domain.bpm.repository.ProcessRepository;
public class GetProcessParametersTest
{
  private ProcessRepository processRepository;
  private GetProcessParameters useCase;
  private Process process;

  @Before
  public void setUp()
  {
    processRepository = Mockito.mock(ProcessRepository.class);
    useCase = new GetProcessParameters(processRepository);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_null() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test(expected = NullPointerException.class)
  public void when_repo_null()
  {
    new GetProcessParameters(null);
  }
}

