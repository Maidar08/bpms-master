/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.form.get_forms_by_case_instance;

import java.util.List;
import java.util.Objects;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.form.TaskForm;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.TaskFormService;
import mn.erin.domain.bpm.usecase.form.common.TaskListOutput;

/**
 * @author Tamir
 */
public class GetFormsByCaseInstanceId implements UseCase<GetFormsByCaseInstanceIdInput, TaskListOutput>
{
  private final TaskFormService taskFormService;

  public GetFormsByCaseInstanceId(TaskFormService taskFormService)
  {
    this.taskFormService = Objects.requireNonNull(taskFormService, "Task form service is required!");
  }

  @Override
  public TaskListOutput execute(GetFormsByCaseInstanceIdInput input) throws UseCaseException
  {
    if (null == input)
    {
      String errorCode = "BPMS052";
      throw new UseCaseException(errorCode, "Get form by case instance id input required!");
    }

    String caseInstanceId = input.getCaseInstanceId();

    try
    {
      List<TaskForm> taskForms = taskFormService.getFormsByCaseInstanceId(caseInstanceId);
      return new TaskListOutput(taskForms);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage(), e);
    }
  }
}
