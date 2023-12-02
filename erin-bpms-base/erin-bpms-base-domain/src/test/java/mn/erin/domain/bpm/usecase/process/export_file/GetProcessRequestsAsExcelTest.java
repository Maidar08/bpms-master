package mn.erin.domain.bpm.usecase.process.export_file;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.model.process.ProcessRequestId;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.model.process.ProcessTypeId;

public class GetProcessRequestsAsExcelTest
{

  private AuthenticationService authenticationService;
  private AuthorizationService authorizationService;

  private GetProcessRequestsAsExcel useCase;

  private static final String ADMIN_USER_ID = "admin";
  private static final String USER_ID = "123";
  private static final String PERMISSION_STR = "bpms.bpm.ExportProcess";

  @Before
  public void setUp()
  {
    authenticationService = Mockito.mock(AuthenticationService.class);
    authorizationService = Mockito.mock(AuthorizationService.class);

    useCase = new GetProcessRequestsAsExcel(authorizationService, authenticationService);

    Mockito.when(authenticationService.getCurrentUserId()).thenReturn(ADMIN_USER_ID);
    Mockito.when(authorizationService.hasPermission(ADMIN_USER_ID, PERMISSION_STR)).thenReturn(true);
  }

  @Test(expected = NullPointerException.class)
  public void when_authorization_service_null()
  {
    new GetProcessRequestsAsExcel(null, authenticationService);
  }

  @Test(expected = UseCaseException.class)
  public void when_search_key_null() throws UseCaseException
  {
    GetProcessRequestExcelInput getProcessRequestExcelInput = new GetProcessRequestExcelInput("topHeader", null, "groupId",  "formattedDate",
        Collections.emptyList());
    useCase.executeImpl(getProcessRequestExcelInput);
  }

  private Collection<ProcessRequest> getProcessRequests(int size)
  {
    if (size == 0 || size < 0)
    {
      return Collections.emptyList();
    }

    Collection<ProcessRequest> processRequests = new ArrayList<>();

    for (int i = 0; i < size; i++)
    {
      ProcessRequest processRequest = new ProcessRequest(ProcessRequestId.valueOf(String.valueOf(i)),
          ProcessTypeId.valueOf("pt"), GroupId.valueOf("gr"), "user", LocalDateTime.now(),
          ProcessRequestState.COMPLETED, getParams());

      processRequests.add(processRequest);
    }

    return processRequests;
  }

  private Map<String, Serializable> getParams()
  {
    Map<String, Serializable> params = new HashMap<>();

    params.put(BpmModuleConstants.FULL_NAME, "fn");
    params.put(BpmModuleConstants.REGISTER_NUMBER, "rn");
    params.put(BpmModuleConstants.CIF_NUMBER, "cn");
    params.put(BpmModuleConstants.LOAN_AMOUNT, "la");
    params.put(BpmModuleConstants.CHANNEL, "c");

    return params;
  }
}
