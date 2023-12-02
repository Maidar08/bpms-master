/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.form.get_forms_by_definition_key;

import java.util.List;
import java.util.Objects;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmMessagesConstants;
import mn.erin.domain.bpm.model.form.TaskForm;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.TaskFormService;
import mn.erin.domain.bpm.usecase.form.common.TaskListOutput;

/**
 * @author Tamir
 */
public class GetFormsByDefinitionKey implements UseCase<GetFormsByDefinitionKeyInput, TaskListOutput>
{
  private final TaskFormService taskFormService;

  public GetFormsByDefinitionKey(TaskFormService taskFormService)
  {
    this.taskFormService = Objects.requireNonNull(taskFormService, "Task form service is required!");
  }

  @Override
  public TaskListOutput execute(GetFormsByDefinitionKeyInput input) throws UseCaseException
  {
    if (null == input)
    {
      throw new UseCaseException(BpmMessagesConstants.INPUT_NULL_CODE, BpmMessagesConstants.INPUT_NULL_MESSAGE);
    }

    String definitionKey = input.getDefinitionKey();
    List<TaskForm> taskForms = null;
    try
    {
      taskForms = taskFormService.getFormsByDefinitionKey(definitionKey);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage(), e);
    }

    if (null == taskForms || taskForms.isEmpty())
    {
      String errorCode = "CamundaCaseService002";
      throw new UseCaseException(errorCode, "Form does not exit with definition key: " + definitionKey);
    }

    return new TaskListOutput(taskForms);
  }
}
