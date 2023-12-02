package mn.erin.bpms.loan.consumption.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cmmn.entity.runtime.CaseExecutionEntity;
import org.camunda.bpm.engine.impl.cmmn.model.CmmnActivity;
import org.camunda.bpm.engine.impl.cmmn.model.CmmnCaseDefinition;
import org.camunda.bpm.engine.runtime.CaseExecution;
import org.camunda.bpm.engine.runtime.CaseExecutionQuery;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_REMOVE_CO_BORROWER;
import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_STAGE_COLLATERAL_LIST;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class CaseExecutionUtilsTest
{
  private static final String CASE_INSTANCE_ID_VALUE = "c1";

  private ProcessEngine processEngine;
  private CaseService caseService;
  private CaseExecutionQuery caseExecutionQuery;

  @Before
  public void setUp()
  {
    processEngine = Mockito.mock(ProcessEngine.class);
    caseService = Mockito.mock(CaseService.class);
    caseExecutionQuery = Mockito.mock(CaseExecutionQuery.class);
  }

  @Ignore
  @Test
  public void verifyNeverInvoked()
  {
    mockExecutions(getExecutions());

    CaseExecutionUtils.manuallyStartExecution(CASE_INSTANCE_ID_VALUE, ACTIVITY_ID_STAGE_COLLATERAL_LIST, processEngine, new HashMap<>());

    verify(caseService, never()).manuallyStartCaseExecution("executionId0", new HashMap<>());
  }

  private void mockExecutions(List<CaseExecution> executions)
  {
    Mockito.when(processEngine.getCaseService()).thenReturn(caseService);
    Mockito.when(caseService.createCaseExecutionQuery()).thenReturn(caseExecutionQuery);
    Mockito.when(caseExecutionQuery.caseInstanceId(CASE_INSTANCE_ID_VALUE)).thenReturn(caseExecutionQuery);

    Mockito.when(caseExecutionQuery.active()).thenReturn(caseExecutionQuery);
    Mockito.when(caseExecutionQuery.list()).thenReturn(executions);
  }

  private List<CaseExecution> getExecutions()
  {
    List<CaseExecution> caseExecutions = new ArrayList<>();

    CaseExecutionEntity execution0 = new CaseExecutionEntity();
    CaseExecutionEntity execution1 = new CaseExecutionEntity();
    CaseExecutionEntity execution2 = new CaseExecutionEntity();
    CaseExecutionEntity execution3 = new CaseExecutionEntity();


    execution0.setActivity(new CmmnActivity(ACTIVITY_ID_STAGE_COLLATERAL_LIST, new CmmnCaseDefinition("1")));
    execution3.setActivity(new CmmnActivity(ACTIVITY_ID_REMOVE_CO_BORROWER + "-1", new CmmnCaseDefinition("4")));

    execution1.setActivity(new CmmnActivity("test", new CmmnCaseDefinition("2")));
    execution2.setActivity(new CmmnActivity("test2", new CmmnCaseDefinition("3")));

    execution0.setId("executionId0");
    execution1.setId("executionId1");
    execution2.setId("executionId2");
    execution3.setId("executionId3");

    caseExecutions.add(execution0);
    caseExecutions.add(execution1);
    caseExecutions.add(execution2);
    caseExecutions.add(execution3);

    return caseExecutions;
  }
}
