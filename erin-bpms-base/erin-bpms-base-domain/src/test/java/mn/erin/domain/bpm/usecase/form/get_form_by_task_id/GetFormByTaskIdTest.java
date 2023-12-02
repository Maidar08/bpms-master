/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.form.get_form_by_task_id;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.form.TaskForm;
import mn.erin.domain.bpm.model.form.TaskFormId;
import mn.erin.domain.bpm.model.task.TaskId;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.TaskFormService;

import static org.mockito.Mockito.when;

/**
 * @author Tamir
 */
public class GetFormByTaskIdTest
{
  private static final String TASK_ID = "task1";
  private static final String CASE_INSTANCE_ID = "c1";

  private TaskFormService taskFormService;

  private GetFormByTaskId useCase;
  private GetFormByTaskIdInput input;

  @Before
  public void setUp()
  {
    taskFormService = Mockito.mock(TaskFormService.class);
    useCase = new GetFormByTaskId(taskFormService);
    input = new GetFormByTaskIdInput(CASE_INSTANCE_ID, TASK_ID);
  }

  @Test(expected = NullPointerException.class)
  public void verify_repository_null()
  {
    new GetFormByTaskId(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_null() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_catch_repository_exception() throws UseCaseException, BpmServiceException
  {
    when(taskFormService.getFormByTaskId(CASE_INSTANCE_ID, TASK_ID)).thenThrow(BpmServiceException.class);
    useCase.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void when_task_form_null() throws BpmServiceException, UseCaseException
  {
    when(taskFormService.getFormByTaskId(CASE_INSTANCE_ID, TASK_ID)).thenReturn(null);
    useCase.execute(input);
  }

  @Test
  public void when_found_task_form() throws BpmServiceException, UseCaseException
  {
    TaskForm taskForm = new TaskForm(TaskFormId.valueOf("1"), new TaskId(TASK_ID), Collections.emptyList());
    when(taskFormService.getFormByTaskId(CASE_INSTANCE_ID, TASK_ID)).thenReturn(taskForm);

    GetFormByTaskIdOutput output = useCase.execute(input);

    Assert.assertNotNull(output.getTaskForm());
  }
}
