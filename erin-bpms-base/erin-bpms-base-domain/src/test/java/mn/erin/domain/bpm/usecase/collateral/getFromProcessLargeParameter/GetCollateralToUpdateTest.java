package mn.erin.domain.bpm.usecase.collateral.getFromProcessLargeParameter;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.model.process.Process;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.usecase.process.GetProcessParameterOutput;
import mn.erin.domain.bpm.usecase.process.collateral.getFromProcessLargeParameters.GetCollateralToUpdate;
import mn.erin.domain.bpm.usecase.process.collateral.getFromProcessLargeParameters.GetCollateralToUpdateInput;

import static mn.erin.domain.bpm.BpmModuleConstants.CREATE_COLLATERAL_TASK_NAME;

/**
 * @author Lkhagvadorj
 */
public class GetCollateralToUpdateTest
{
  private ProcessRepository processRepository;
  private GetCollateralToUpdate useCase;

  @Before
  public void setup()
  {
    this.processRepository = Mockito.mock(ProcessRepository.class);
    this.useCase = new GetCollateralToUpdate(processRepository);
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_use_case_exception() throws UseCaseException
  {
    useCase.execute(null);
  }


  @Test(expected = UseCaseException.class)
  public void when_throws_repository_exception() throws BpmRepositoryException, UseCaseException, SQLException
  {
    Mockito.when(processRepository.filterLargeParamBySearchKeyFromValue(null, CREATE_COLLATERAL_TASK_NAME, ParameterEntityType.COMPLETED_FORM)).thenThrow(BpmRepositoryException.class);
    useCase.execute( new GetCollateralToUpdateInput("123", null, ParameterEntityType.COMPLETED_FORM) );
  }

  @Test
  public void when_returnValue() throws BpmRepositoryException, UseCaseException, SQLException
  {
    Mockito.when(processRepository.filterLargeParamBySearchKeyFromValue("test", CREATE_COLLATERAL_TASK_NAME, ParameterEntityType.COMPLETED_FORM)).thenReturn( getReturnedProcess() );
    GetCollateralToUpdateInput input = new GetCollateralToUpdateInput("testId", "test", ParameterEntityType.COMPLETED_FORM);
    final String returnedValue = useCase.execute(input).getParameterValue().toString();
    Mockito.verify( processRepository, Mockito.times(1) ).filterLargeParamBySearchKeyFromValue("test", CREATE_COLLATERAL_TASK_NAME, ParameterEntityType.COMPLETED_FORM);
    Assert.assertEquals("12test123123", returnedValue );
  }

  @Test
  public void when_return_null() throws BpmRepositoryException, UseCaseException, SQLException
  {
    Mockito.when(processRepository.filterLargeParamBySearchKeyFromValue("test", CREATE_COLLATERAL_TASK_NAME, ParameterEntityType.COMPLETED_FORM)).thenReturn( null );
    GetCollateralToUpdateInput input = new GetCollateralToUpdateInput("testId", "test", ParameterEntityType.COMPLETED_FORM);
    final GetProcessParameterOutput output = useCase.execute(input);
    Assert.assertEquals(null, output );
  }

  private Process getReturnedProcess()
  {
    Map<String, Serializable> map = new HashMap<>();
    final Map<ParameterEntityType, Map<String, Serializable>> processParameters = new HashMap<>();
    map.put("3", "12test123123");
    processParameters.put(ParameterEntityType.COMPLETED_FORM, map);
    return new Process(null, null,null, null,  null,processParameters);
  }
}
