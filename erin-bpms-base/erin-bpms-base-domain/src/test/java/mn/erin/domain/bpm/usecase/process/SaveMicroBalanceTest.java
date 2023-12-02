package mn.erin.domain.bpm.usecase.process;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.usecase.calculations.CalculateMicroBalanceInput;

import static mn.erin.domain.bpm.BpmModuleConstants.BALANCE_TOTAL_INCOME_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.BALANCE_TOTAL_INCOME_PERCENT;
import static mn.erin.domain.bpm.BpmModuleConstants.REPORT_PERIOD;

/**
 * @author Lkhagvadorj.A
 **/

public class SaveMicroBalanceTest
{
  private ProcessRepository processRepository;
  private SaveMicroBalance useCase;

  @Before
  public void setUp()
  {
    processRepository = Mockito.mock(ProcessRepository.class);
    useCase = new SaveMicroBalance(processRepository);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_null() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_instance_id_null() throws UseCaseException
  {
    useCase.execute(new CalculateMicroBalanceInput("", 0, null, null, null, null));
  }

  @Test
  public void when_use_case_executes() throws BpmRepositoryException, UseCaseException
  {
    EnumMap<ParameterEntityType, Map<String, Serializable>> parameters = new EnumMap<>(ParameterEntityType.class);
    Map<String, Serializable> param = new HashMap<>();
    param.put(BALANCE_TOTAL_INCOME_AMOUNT, 0.0);
    param.put(BALANCE_TOTAL_INCOME_PERCENT, 0.0);
    param.put(REPORT_PERIOD, 0);
    parameters.put(ParameterEntityType.BALANCE, param);
    Mockito.when(processRepository.deleteEntity("123", ParameterEntityType.BALANCE)).thenReturn(true);
    Mockito.when(processRepository.updateParameters("123", parameters)).thenReturn(3);

    Boolean output = useCase.execute(new CalculateMicroBalanceInput("123", 0, null, null, null, null));

    Mockito.verify(processRepository, Mockito.atLeastOnce()).deleteEntity("123", ParameterEntityType.BALANCE);
    Mockito.verify(processRepository, Mockito.atLeastOnce()).updateParameters("123", parameters);
    Assert.assertTrue(output);
  }
}
