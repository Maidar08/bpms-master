/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.form.get_forms_by_process_instance_id;

import java.util.ArrayList;
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
public class GetFormsByProcessInstanceId implements UseCase<GetFormsByProcessInstanceIdInput, TaskListOutput>
{
  private final TaskFormService taskFormService;

  public GetFormsByProcessInstanceId(TaskFormService taskFormService)
  {
    this.taskFormService = Objects.requireNonNull(taskFormService, "Task form service is required!");
  }

  @Override
  public TaskListOutput execute(GetFormsByProcessInstanceIdInput input) throws UseCaseException
  {

    if (null == input)
    {
      throw new UseCaseException(BpmMessagesConstants.INPUT_NULL_CODE, BpmMessagesConstants.INPUT_NULL_MESSAGE);
    }

    String processInstanceId = input.getProcessInstanceId();

    List<TaskForm> taskForms = new ArrayList<>();

    try
    {
      taskForms = taskFormService.getFormsByProcessInstanceId(processInstanceId);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage(), e);
    }

    return new TaskListOutput(taskForms);
  }
}
