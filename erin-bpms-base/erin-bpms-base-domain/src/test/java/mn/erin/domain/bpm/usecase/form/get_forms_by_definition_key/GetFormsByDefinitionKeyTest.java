/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.form.get_forms_by_definition_key;

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

import static org.mockito.Mockito.when;

/**
 * @author Tamir
 */
public class GetFormsByDefinitionKeyTest
{
  private final static String DEFINITION_KEY = "defKey";
  private final static String TASK_ID = "taskId";

  private TaskFormService taskFormService;
  private GetFormsByDefinitionKey useCase;
  private GetFormsByDefinitionKeyInput input;

  @Before
  public void setUp()
  {
    taskFormService = Mockito.mock(TaskFormService.class);
    useCase = new GetFormsByDefinitionKey(taskFormService);
    input = new GetFormsByDefinitionKeyInput(DEFINITION_KEY);
  }

  @Test(expected = NullPointerException.class)
  public void when_repository_null()
  {
    new GetFormsByDefinitionKey(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_null() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_catch_repository_exception() throws UseCaseException, BpmServiceException
  {
    when(taskFormService.getFormsByDefinitionKey(DEFINITION_KEY)).thenThrow(BpmServiceException.class);
    useCase.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void when_task_form_null() throws UseCaseException, BpmServiceException
  {
    when(taskFormService.getFormsByDefinitionKey(DEFINITION_KEY)).thenReturn(null);
    useCase.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void when_task_form_empty_list() throws UseCaseException, BpmServiceException
  {
    when(taskFormService.getFormsByDefinitionKey(DEFINITION_KEY)).thenReturn(Collections.emptyList());
    useCase.execute(input);
  }

  @Test
  public void when_found_task_forms() throws BpmServiceException, UseCaseException
  {
    TaskForm taskForm = new TaskForm(TaskFormId.valueOf("1"), new TaskId(TASK_ID), Collections.emptyList());
    when(taskFormService.getFormsByDefinitionKey(DEFINITION_KEY)).thenReturn(Arrays.asList(taskForm));

    TaskListOutput output = useCase.execute(input);

    Assert.assertEquals(1, output.getTaskFormList().size());
  }
}
