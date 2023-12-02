/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.process.get_process_types;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.ProcessDefinitionType;
import mn.erin.domain.bpm.model.process.ProcessType;
import mn.erin.domain.bpm.model.process.ProcessTypeId;
import mn.erin.domain.bpm.repository.ProcessTypeRepository;
import mn.erin.domain.bpm.usecase.process.GetProcessTypes;

import static org.mockito.Mockito.when;

/**
 * @author Tamir
 */
public class GetProcessTypesTest
{
  private static final String CURRENT_USER = "admin";
  private static final String PERMISSION_STR = "bpms.bpm.GetProcessTypes";

  private AuthenticationService authenticationService;
  private AuthorizationService authorizationService;

  private ProcessTypeRepository processTypeRepository;
  private GetProcessTypes useCase;

  @Before
  public void setUp()
  {
    authenticationService = Mockito.mock(AuthenticationService.class);
    authorizationService = Mockito.mock(AuthorizationService.class);

    processTypeRepository = Mockito.mock(ProcessTypeRepository.class);

    useCase = new GetProcessTypes(authenticationService, authorizationService, processTypeRepository);
  }

  @Test(expected = NullPointerException.class)
  public void when_null_repo()
  {
    new GetProcessTypes(authenticationService, authorizationService, null);
  }

  @Test(expected = UseCaseException.class)
  public void when_process_type_not_exist() throws UseCaseException
  {
    when(processTypeRepository.findAll()).thenReturn(Collections.emptyList());
    useCase.execute(null);
  }

  @Test
  public void when_successful_found_types() throws UseCaseException
  {
    when(authenticationService.getCurrentUserId()).thenReturn(CURRENT_USER);
    when(authorizationService.hasPermission(CURRENT_USER, PERMISSION_STR)).thenReturn(true);

    when(processTypeRepository.findAll()).thenReturn(Arrays.asList(getProcessType()));
    Collection<ProcessType> processTypes = useCase.execute(null);

    Assert.assertEquals(1, processTypes.size());
  }

  private ProcessType getProcessType()
  {
    ProcessType processType = new ProcessType(ProcessTypeId.valueOf("COMSUMPTION_LOAN"), "bpms_case", ProcessDefinitionType.CASE);
    processType.setVersion("1");
    return processType;
  }
}
