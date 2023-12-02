package mn.erin.bpms.loan.consumption.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.impl.cmmn.entity.runtime.CaseExecutionEntity;
import org.camunda.bpm.engine.impl.cmmn.model.CmmnActivity;
import org.camunda.bpm.engine.impl.cmmn.model.CmmnCaseDefinition;
import org.camunda.bpm.engine.runtime.CaseExecution;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.bpms.loan.consumption.service_task.bpms.co_borrower.CoBorrowerUtils;

import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_REMOVE_CO_BORROWER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

public class CoBorrowerUtilsTest
{
  private static final String REQUEST_ID = "1";

  private List<CaseExecution> enabledExecutions;
  private CaseService caseService;

  @Before
  public void setUp()
  {
    enabledExecutions = getEnabledExecutions();
    caseService = Mockito.mock(CaseService.class);
  }

  @Test
  public void verify_disable_remove_co_borrower_processes()
  {
    CoBorrowerUtils.disableCoBorrowerProcessByActivityIds(enabledExecutions, caseService, REQUEST_ID,
        Arrays.asList(ACTIVITY_ID_REMOVE_CO_BORROWER));

    Mockito.verify(caseService, times(1)).disableCaseExecution(any());
  }

  @Test
  public void verify_when_null_enabled_executions()
  {
    CoBorrowerUtils.disableCoBorrowerProcessByActivityIds(null, caseService, REQUEST_ID,
        Arrays.asList(ACTIVITY_ID_REMOVE_CO_BORROWER));

    Mockito.verify(caseService, times(0)).disableCaseExecution(any());
  }

  private List<CaseExecution> getEnabledExecutions()
  {
    List<CaseExecution> enabledExecutions = new ArrayList<>();

    CaseExecutionEntity execution0 = new CaseExecutionEntity();
    CaseExecutionEntity execution1 = new CaseExecutionEntity();
    CaseExecutionEntity execution2 = new CaseExecutionEntity();
    CaseExecutionEntity execution3 = new CaseExecutionEntity();

    execution0.setActivity(new CmmnActivity(ACTIVITY_ID_REMOVE_CO_BORROWER, new CmmnCaseDefinition("1")));
    execution1.setActivity(new CmmnActivity("test", new CmmnCaseDefinition("2")));
    execution2.setActivity(new CmmnActivity("test2", new CmmnCaseDefinition("3")));
    execution3.setActivity(new CmmnActivity(ACTIVITY_ID_REMOVE_CO_BORROWER + "-1", new CmmnCaseDefinition("4")));

    enabledExecutions.add(execution0);
    enabledExecutions.add(execution1);
    enabledExecutions.add(execution2);
    enabledExecutions.add(execution3);

    return enabledExecutions;
  }
}
