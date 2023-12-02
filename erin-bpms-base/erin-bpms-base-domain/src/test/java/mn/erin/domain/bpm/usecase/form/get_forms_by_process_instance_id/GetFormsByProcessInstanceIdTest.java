/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.form.get_forms_by_process_instance_id;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

import static org.mockito.Mockito.when;

/**
 * @author Tamir
 */
public class GetFormsByProcessInstanceIdTest
{
  private static final String PROCESS_INSTANCE_ID = "123456";
  private static final String TASK_ID = "submitForm";

  private TaskFormService taskFormService;
  private GetFormsByProcessInstanceId useCase;
  private GetFormsByProcessInstanceIdInput input;

  @Before
  public void setUp()
  {
    taskFormService = Mockito.mock(TaskFormService.class);
    useCase = new GetFormsByProcessInstanceId(taskFormService);
    input = new GetFormsByProcessInstanceIdInput(PROCESS_INSTANCE_ID);
  }

  @Test(expected = NullPointerException.class)
  public void when_repository_null()
  {
    new GetFormsByProcessInstanceId(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_null() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_catch_repository_exception() throws UseCaseException, BpmServiceException
  {
    when(taskFormService.getFormsByProcessInstanceId(PROCESS_INSTANCE_ID)).thenThrow(BpmServiceException.class);
    useCase.execute(input);
  }

  @Test
  public void when_found_task_form() throws BpmServiceException, UseCaseException
  {
    TaskForm taskForm = new TaskForm(TaskFormId.valueOf("1"),new TaskId(TASK_ID), Collections.emptyList());

    when(taskFormService.getFormsByProcessInstanceId(PROCESS_INSTANCE_ID)).thenReturn(Arrays.asList(taskForm));

    TaskListOutput output = useCase.execute(input);

    List<TaskForm> taskForms = output.getTaskFormList();

    Assert.assertEquals(taskForms.size(), 1);
    Assert.assertEquals(TASK_ID, taskForms.get(0).getTaskId().getId());
    Assert.assertEquals(Collections.emptyList(), taskForms.get(0).getTaskFormFields());
  }
}
