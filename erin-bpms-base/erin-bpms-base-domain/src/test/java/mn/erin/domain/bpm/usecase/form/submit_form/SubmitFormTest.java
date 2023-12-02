/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.form.submit_form;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.form.TaskForm;
import mn.erin.domain.bpm.model.form.TaskFormId;
import mn.erin.domain.bpm.model.task.TaskId;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.service.TaskFormService;
import mn.erin.domain.bpm.util.process.SubmitFormUtils;

import static org.mockito.Mockito.doThrow;

/**
 * @author Tamir
 */
public class SubmitFormTest
{
  private static final String CASE_INSTANCE_ID = "c1";
  private static final String TASK_ID = "task1";
  private static final String XAC_SPAN_DATE = "xacspanDate";

  private TaskFormService taskFormService;
  private CaseService caseService;

  private SubmitForm useCase;
  private SubmitFormInput input;

  private Map<String, Object> properties;

  @Before
  public void setUp()
  {
    properties = new HashMap<>();
    taskFormService = Mockito.mock(TaskFormService.class);
    caseService = Mockito.mock(CaseService.class);
    useCase = new SubmitForm(taskFormService, caseService);

    properties.put("registerNumber", "test");
    input = new SubmitFormInput(TASK_ID, CASE_INSTANCE_ID, properties);
  }

  @Test(expected = NullPointerException.class)
  public void verify_repository_null()
  {
    new SubmitForm(null, caseService);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_null() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_catch_repository_exception() throws UseCaseException, BpmServiceException
  {
    doThrow(BpmServiceException.class).when(taskFormService).submitForm(TASK_ID, properties);

    useCase.execute(input);
  }

  @Test
  public void when_submitted() throws UseCaseException, BpmServiceException
  {
    Mockito.when(taskFormService.getFormByTaskId(CASE_INSTANCE_ID, input.getTaskId()))
        .thenReturn(new TaskForm(new TaskFormId("formId"), new TaskId("taskId"), new ArrayList<>()));
    SubmitFormOutput output = useCase.execute(input);

    Assert.assertTrue(output.isSubmitted());
  }

  @Test
  public void verify_parsed_date() throws UseCaseException
  {
    Map<String, Object> properties = new HashMap<>();

    properties.put("registerNumber", "test");
    properties.put(XAC_SPAN_DATE, "2020-03-31T16:00:00.000Z");

    // not throw exception
    SubmitFormUtils.setDateProperties(properties);

    Assert.assertTrue(properties.get(XAC_SPAN_DATE) instanceof Date);
  }
}
