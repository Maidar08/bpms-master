/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.form.get_form_by_task_id;

import java.util.Objects;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.form.TaskForm;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.TaskFormService;

import static mn.erin.domain.bpm.BpmMessagesConstants.TASK_FORM_NOT_EXIST_ERROR_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.TASK_ID_NULL_ERROR_CODE;

/**
 * @author Tamir
 */
public class GetFormByTaskId implements UseCase<GetFormByTaskIdInput, GetFormByTaskIdOutput>
{
  private static final String ERR_MSG_INPUT_NULL = "Get form by id input cannot be null!";
  private static final String ERR_MSG_NOT_EXIST = "Form does not exist for current user!";

  private final TaskFormService taskFormService;

  public GetFormByTaskId(TaskFormService taskFormService)
  {
    this.taskFormService = Objects.requireNonNull(taskFormService, "Task form service is required!");
  }

  @Override
  public GetFormByTaskIdOutput execute(GetFormByTaskIdInput input) throws UseCaseException
  {
    if (null == input)
    {
      throw new UseCaseException(TASK_ID_NULL_ERROR_CODE, ERR_MSG_INPUT_NULL);
    }

    String caseInstanceId = input.getCaseInstanceId();
    String taskId = input.getTaskId();

    try
    {
      TaskForm taskForm = taskFormService.getFormByTaskId(caseInstanceId, taskId);

      if (null == taskForm)
      {
        throw new UseCaseException(TASK_FORM_NOT_EXIST_ERROR_CODE, ERR_MSG_NOT_EXIST);
      }

      return new GetFormByTaskIdOutput(taskForm);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage(), e);
    }
  }
}
