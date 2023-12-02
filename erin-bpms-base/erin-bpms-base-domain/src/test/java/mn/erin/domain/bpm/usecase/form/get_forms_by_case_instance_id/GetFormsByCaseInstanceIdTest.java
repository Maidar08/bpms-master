/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.form.get_forms_by_case_instance_id;

import java.util.Arrays;
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
import mn.erin.domain.bpm.usecase.form.common.TaskListOutput;
import mn.erin.domain.bpm.usecase.form.get_forms_by_case_instance.GetFormsByCaseInstanceId;
import mn.erin.domain.bpm.usecase.form.get_forms_by_case_instance.GetFormsByCaseInstanceIdInput;

import static org.mockito.Mockito.when;

/**
 * @author Tamir
 */
public class GetFormsByCaseInstanceIdTest
{
  private static final String CASE_INSTANCE_ID = "instanceId";
  private static final String TASK_ID = "task1";

  private TaskFormService taskFormService;

  private GetFormsByCaseInstanceId useCase;
  private GetFormsByCaseInstanceIdInput input;

  @Before
  public void setUp()
  {
    taskFormService = Mockito.mock(TaskFormService.class);

    useCase = new GetFormsByCaseInstanceId(taskFormService);
    input = new GetFormsByCaseInstanceIdInput(CASE_INSTANCE_ID);
  }

  @Test(expected = NullPointerException.class)
  public void verify_repository_null()
  {
    new GetFormsByCaseInstanceId(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_null() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_catch_repository_exception() throws UseCaseException, BpmServiceException
  {
    when(taskFormService.getFormsByCaseInstanceId(CASE_INSTANCE_ID)).thenThrow(BpmServiceException.class);
    useCase.execute(input);
  }

  @Test
  public void when_found_task_form() throws BpmServiceException, UseCaseException
  {
    TaskForm taskForm = new TaskForm(TaskFormId.valueOf("1"),new TaskId(TASK_ID), Collections.emptyList());
    when(taskFormService.getFormsByCaseInstanceId(CASE_INSTANCE_ID)).thenReturn(Arrays.asList(taskForm));

    TaskListOutput output = useCase.execute(input);

    Assert.assertEquals(1, output.getTaskFormList().size());
  }
}
