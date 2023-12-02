package mn.erin.domain.bpm.usecase.collateral.updateCollateral;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.model.process.Process;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.CollateralProductRepository;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.usecase.process.collateral.updateCollateral.UpdateCollateral;
import mn.erin.domain.bpm.usecase.process.collateral.updateCollateral.UpdateCollateralInput;

import static mn.erin.domain.bpm.BpmModuleConstants.CREATE_COLLATERAL_TASK_NAME;

/**
 * @author Lkhagvadorj
 */
public class UpdateCollateralTest
{
  private static final String CURRENT_USER = "admin";
  private static final String PERMISSION_STR = "bpms.bpm.GetProcessRequestByProcessInstanceId";

  private AuthenticationService authenticationService;
  private AuthorizationService authorizationService;
  private CollateralProductRepository collateralProductRepository;

  private ProcessRepository processRepository;
  private UpdateCollateral useCase;

  @Before
  public void setup()
  {
    authenticationService = Mockito.mock(AuthenticationService.class);
    authorizationService = Mockito.mock(AuthorizationService.class);

    processRepository = Mockito.mock(ProcessRepository.class);
    collateralProductRepository = Mockito.mock(CollateralProductRepository.class);
    useCase = new UpdateCollateral(authenticationService, authorizationService, processRepository, collateralProductRepository);

    Mockito.when(authenticationService.getCurrentUserId()).thenReturn(CURRENT_USER);
    Mockito.when(authorizationService.hasPermission(CURRENT_USER, PERMISSION_STR)).thenReturn(true);
  }

  @Test(expected = UseCaseException.class)
  public void when_throw_exception() throws BpmRepositoryException, UseCaseException, SQLException
  {
    Mockito.when(processRepository.filterLargeParamBySearchKeyFromValue("c1", CREATE_COLLATERAL_TASK_NAME, ParameterEntityType.COMPLETED_FORM)).thenThrow(BpmRepositoryException.class);
    useCase.execute(new UpdateCollateralInput("123", "c1", ParameterEntityType.COMPLETED_FORM, null));
  }

  @Test
  public void when_return_empty_list_from_process_large_parameter() throws BpmRepositoryException, UseCaseException, SQLException
  {
    Mockito.when(processRepository.filterLargeParamBySearchKeyFromValue(null, CREATE_COLLATERAL_TASK_NAME, ParameterEntityType.COMPLETED_FORM))
        .thenReturn(null);
    final Boolean execute = useCase.execute(new UpdateCollateralInput("123", null, ParameterEntityType.COMPLETED_FORM, null));
    Assert.assertFalse("when return null from process large parameter", execute);
  }

  @Test
  @Ignore
  public void when_final_parameter_to_update_is_null() throws BpmRepositoryException, UseCaseException, SQLException
  {
    Mockito.when(processRepository.filterLargeParamBySearchKeyFromValue("12345testId", CREATE_COLLATERAL_TASK_NAME, ParameterEntityType.COMPLETED_FORM)).thenReturn(getReturnedProcess());

    Mockito.when(processRepository.updateLargeParameters("testId", getReturnedProcess().getProcessParameters())).thenReturn(1);
    Mockito.when(processRepository.updateParameters("testId", getReturnedProcess().getProcessParameters())).thenReturn(1);

    final Boolean execute = useCase.execute(new UpdateCollateralInput("testId", "12345testId", ParameterEntityType.COMPLETED_FORM, Collections.emptyList()));
    Assert.assertTrue("when update successfully", execute);
  }

  private Process getReturnedProcess()
  {
    Map<String, Serializable> map = new HashMap<>();
    final Map<ParameterEntityType, Map<String, Serializable>> processParameters = new HashMap<>();
    map.put("1", "{'formId':'12345testId', 'caseInstanceId':'test'}");
    map.put("2", "asdhauiwcn");
    map.put("3", "12test123123");
    processParameters.put(ParameterEntityType.COMPLETED_FORM, map);
    return new Process(null, null, null, null,null, processParameters);
  }
}
