/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.process.get_request_by_created_date;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.model.process.ProcessRequestId;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.model.process.ProcessTypeId;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.usecase.process.GetRequestsByCreatedDate;
import mn.erin.domain.bpm.usecase.process.GetRequestsByCreatedDateInput;

/**
 * @author Tamir
 */
public class GetRequestsByCreatedDateTest
{
  private static final String DATE_STRING = "1231231233";
  private static final String PARAMETER_VALUE = "cif-12345678";

  public static final String USER_ID = "chris";
  public static final String INVALID_DATE = "2020-04-07--";

  private AuthenticationService authenticationService;
  private AuthorizationService authorizationService;
  private ProcessRequestRepository processRequestRepository;

  private GetRequestsByCreatedDate useCase;

  @Before
  public void setUp()
  {
    authenticationService = Mockito.mock(AuthenticationService.class);
    authorizationService = Mockito.mock(AuthorizationService.class);
    processRequestRepository = Mockito.mock(ProcessRequestRepository.class);
    useCase = new GetRequestsByCreatedDate(authenticationService, authorizationService, processRequestRepository);

    Mockito.when(authenticationService.getCurrentUserId()).thenReturn(USER_ID);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_null() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_start_created_date_blank() throws UseCaseException
  {
    GetRequestsByCreatedDateInput input = new GetRequestsByCreatedDateInput(PARAMETER_VALUE, " ", DATE_STRING);
    useCase.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void when_end_created_date_blank() throws UseCaseException
  {
    GetRequestsByCreatedDateInput input = new GetRequestsByCreatedDateInput(PARAMETER_VALUE, DATE_STRING, " ");
    useCase.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void when_parameter_value_blank() throws UseCaseException
  {
    GetRequestsByCreatedDateInput input = new GetRequestsByCreatedDateInput(" ", DATE_STRING, DATE_STRING);
    useCase.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void when_throw_number_format_exception() throws UseCaseException
  {
    GetRequestsByCreatedDateInput input = new GetRequestsByCreatedDateInput(PARAMETER_VALUE, INVALID_DATE, DATE_STRING);

    useCase.execute(input);
  }

  // TODO fix the test
  @Ignore
  @Test(expected = UseCaseException.class)
  public void when_throw_bpm_repo_exception() throws BpmRepositoryException, UseCaseException
  {
    GetRequestsByCreatedDateInput input = new GetRequestsByCreatedDateInput(PARAMETER_VALUE, DATE_STRING, DATE_STRING);

    Date date = new Date(Long.parseLong(DATE_STRING));
    Date validDate = new Date(date.getYear(), date.getMonth(), date.getDate());

    Mockito.when(processRequestRepository
            .findAllByCreatedDateInterval(input.getParameterValue(), validDate, validDate)).thenThrow(
            BpmRepositoryException.class);

    useCase.execute(input);
  }

  // TODO fix the test
  @Ignore
  @Test
  public void when_successful_found_requests() throws BpmRepositoryException, UseCaseException
  {
    GetRequestsByCreatedDateInput input = new GetRequestsByCreatedDateInput(PARAMETER_VALUE, DATE_STRING, DATE_STRING);

    Date date = new Date(Long.parseLong(DATE_STRING));
    Date validDate = new Date(date.getYear(), date.getMonth(), date.getDate());
    Mockito.when(processRequestRepository
            .findAllByCreatedDateInterval(input.getParameterValue(), validDate, validDate)).thenReturn(
            Collections.singletonList(new ProcessRequest(ProcessRequestId.valueOf("r1"), ProcessTypeId.valueOf("t1"), GroupId.valueOf("g1"), "u1", LocalDateTime.now(),
                    ProcessRequestState.NEW, new HashMap<>())));

    Collection<ProcessRequest> processRequests = useCase.execute(input);

    Iterator<ProcessRequest> iterator = processRequests.iterator();

    ProcessRequest processRequest = iterator.next();

    Assert.assertEquals("r1", processRequest.getId().getId());
  }
}
