/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.form.get_forms_by_definition_id;

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
public class GetFormsByDefinitionId implements UseCase<GetFormsByDefinitionIdInput, TaskListOutput>
{
  private final TaskFormService taskFormService;

  public GetFormsByDefinitionId(TaskFormService taskFormService)
  {
    this.taskFormService = Objects.requireNonNull(taskFormService, "Task form service is required!");
  }

  @Override
  public TaskListOutput execute(GetFormsByDefinitionIdInput input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException(BpmMessagesConstants.INPUT_NULL_CODE, BpmMessagesConstants.INPUT_NULL_MESSAGE);
    }

    String definitionId = input.getDefinitionId();
    List<TaskForm> taskForms = null;
    try
    {
      taskForms = taskFormService.getFormsByDefinitionId(definitionId);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }

    if (null == taskForms || taskForms.isEmpty())
    {
      String errorCode = "CamundaTasKFormService002";
      throw new UseCaseException(errorCode, "Form does not exit with definition id: " + definitionId);
    }

    return new TaskListOutput(taskForms);
  }
}
