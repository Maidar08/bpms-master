package mn.erin.domain.bpm.usecase.task;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.aim.service.AimServiceRegistry;
import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.cases.Execution;
import mn.erin.domain.bpm.model.form.TaskForm;
import mn.erin.domain.bpm.model.form.TaskFormField;
import mn.erin.domain.bpm.model.task.Task;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.usecase.execution.GetEnabledExecutions;
import mn.erin.domain.bpm.usecase.execution.GetEnabledExecutionsInput;
import mn.erin.domain.bpm.usecase.execution.GetEnabledExecutionsOutput;
import mn.erin.domain.bpm.usecase.form.get_form_by_task_id.GetFormByTaskId;
import mn.erin.domain.bpm.usecase.form.get_form_by_task_id.GetFormByTaskIdInput;
import mn.erin.domain.bpm.usecase.form.get_form_by_task_id.GetFormByTaskIdOutput;
import mn.erin.domain.bpm.usecase.form.submit_form.SubmitForm;
import mn.erin.domain.bpm.usecase.form.submit_form.SubmitFormInput;
import mn.erin.domain.bpm.usecase.process.manual_activate.ManualActivate;
import mn.erin.domain.bpm.usecase.process.manual_activate.ManualActivateInput;

import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;

/**
 * @author Lkhagvadorj.A
 **/

public class ChangeProcessTasksStateById extends AbstractUseCase<ChangeProcessTasksStateByIdInput, Void>
{
  private final AimServiceRegistry aimServiceRegistry;
  private final BpmsServiceRegistry bpmsServiceRegistry;

  public ChangeProcessTasksStateById(AimServiceRegistry aimServiceRegistry, BpmsServiceRegistry bpmsServiceRegistry)
  {
    this.aimServiceRegistry = Objects.requireNonNull(aimServiceRegistry, "Aim Service Registry is required!");
    this.bpmsServiceRegistry = Objects.requireNonNull(bpmsServiceRegistry, "Bpms Service Registry is required!");
  }

  @Override
  public Void execute(ChangeProcessTasksStateByIdInput input) throws UseCaseException
  {
    validateInput(input);

    String instanceId = input.getInstanceId();
    manuallyStartEnableProcessesById(instanceId, input.getEnableProcessIds());
    terminateEnableProcessById(instanceId, input.getTerminateProcessIds());
    try
    {
      completeActiveTaskById(instanceId, input.getActiveTaskIds());
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
    return null;
  }

  private void manuallyStartEnableProcessesById(String instanceId, List<String> enableProcessIds) throws UseCaseException
  {
    GetEnabledExecutionsOutput enableOutput = getEnabledExecutions(instanceId);
    if (null != enableOutput)
    {
      List<Execution> enableExecutions = enableOutput.getExecutions();
      ManualActivate manualActivate = new ManualActivate(aimServiceRegistry.getAuthenticationService(), aimServiceRegistry.getAuthorizationService(),
          bpmsServiceRegistry.getExecutionService());
        for (Execution execution : enableExecutions)
        {
          if (enableProcessIds.contains(execution.getActivityId().getId()))
          {
            ManualActivateInput manualActivateInput = new ManualActivateInput(execution.getId().getId(), Collections.emptyList(), Collections.emptyList());
            manualActivate.execute(manualActivateInput);
          }
        }
    }
  }

  private void terminateEnableProcessById(String instanceId, List<String> processIds) throws UseCaseException
  {
    GetEnabledExecutionsOutput enableOutput = getEnabledExecutions(instanceId);

    if (null != enableOutput)
    {
      List<Execution> enableExecutions = enableOutput.getExecutions();
      for (Execution execution : enableExecutions)
      {
        if (processIds.contains(execution.getActivityId().getId()))
        {
          CaseService caseService = bpmsServiceRegistry.getCaseService();
          caseService.disable(execution.getId().getId());
        }
      }
    }
  }

  private void completeActiveTaskById(String instanceId, List<String> activeTaskIds) throws UseCaseException, BpmServiceException
  {
    GetActiveTaskByInstanceId getActiveTaskByInstanceId = new GetActiveTaskByInstanceId(bpmsServiceRegistry.getTaskService(), bpmsServiceRegistry.getRuntimeService(), bpmsServiceRegistry.getExecutionService());
    GetActiveTaskByInstanceIdOutput output = getActiveTaskByInstanceId.execute(String.valueOf(instanceId));

    if (null != output)
    {
      List<Task> activeTasks = output.getActiveTasks();
      for (Task task : activeTasks)
      {
        if (activeTaskIds.contains(task.getDefinitionKey()))
        {

          SubmitForm submitForm = new SubmitForm(bpmsServiceRegistry.getTaskFormService(), bpmsServiceRegistry.getCaseService());
          SubmitFormInput input = new SubmitFormInput(task.getId().getId(), instanceId, getSubmitForm(instanceId, task.getId().getId()));
          submitForm.execute(input);
        }
      }
    }
  }

  private void validateInput(ChangeProcessTasksStateByIdInput input) throws UseCaseException
  {
    if (null == input)
    {
      throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
    }

    if (StringUtils.isBlank(input.getInstanceId()))
    {
      throw new UseCaseException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
    }
  }

  private GetEnabledExecutionsOutput getEnabledExecutions(String instanceId) throws UseCaseException
  {
    GetEnabledExecutions getEnabledExecutions = new GetEnabledExecutions(bpmsServiceRegistry.getExecutionService());
    return getEnabledExecutions.execute(new GetEnabledExecutionsInput(instanceId));
  }

  private Map<String, Object> getSubmitForm(String instanceId, String taskId) throws UseCaseException, BpmServiceException
  {
    Map<String, Object> returnVariables = new HashMap<>();
    TaskForm taskForm = getTaskForm(instanceId, taskId);
    if (null != taskForm)
    {
      CaseService caseService = bpmsServiceRegistry.getCaseService();
      Collection<TaskFormField> taskFormFields = taskForm.getTaskFormFields();
      for (TaskFormField formField : taskFormFields)
      {
        String id = formField.getId().getId();
        Object value = caseService.getVariableById(instanceId, id);
        returnVariables.put(id, value);
      }
      return returnVariables;
    }

    returnVariables.put("test", "test");
    return returnVariables;
  }

  private TaskForm getTaskForm(String instanceId, String taskId) throws UseCaseException
  {
    GetFormByTaskId getFormByTaskId = new GetFormByTaskId(bpmsServiceRegistry.getTaskFormService());
    GetFormByTaskIdInput input = new GetFormByTaskIdInput(instanceId, taskId);

    GetFormByTaskIdOutput output = getFormByTaskId.execute(input);
    return output.getTaskForm();
  }
}
