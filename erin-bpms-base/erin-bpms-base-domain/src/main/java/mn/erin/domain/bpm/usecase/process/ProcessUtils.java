package mn.erin.domain.bpm.usecase.process;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmMessagesConstants;
import mn.erin.domain.bpm.model.form.TaskForm;
import mn.erin.domain.bpm.model.form.TaskFormField;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.model.task.Task;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.service.ExecutionService;
import mn.erin.domain.bpm.service.RuntimeService;
import mn.erin.domain.bpm.service.TaskFormService;
import mn.erin.domain.bpm.service.TaskService;
import mn.erin.domain.bpm.usecase.form.common.TaskListOutput;
import mn.erin.domain.bpm.usecase.form.get_form_by_task_id.GetFormByTaskId;
import mn.erin.domain.bpm.usecase.form.get_form_by_task_id.GetFormByTaskIdInput;
import mn.erin.domain.bpm.usecase.form.get_forms_by_process_instance_id.GetFormsByProcessInstanceId;
import mn.erin.domain.bpm.usecase.form.get_forms_by_process_instance_id.GetFormsByProcessInstanceIdInput;
import mn.erin.domain.bpm.usecase.form.submit_form.SubmitForm;
import mn.erin.domain.bpm.usecase.form.submit_form.SubmitFormInput;
import mn.erin.domain.bpm.usecase.form.submit_form.SubmitProcessForm;
import mn.erin.domain.bpm.usecase.form.submit_form.SubmitProcessFormInput;
import mn.erin.domain.bpm.usecase.form.submit_form.SubmitProcessFormOutput;
import mn.erin.domain.bpm.usecase.task.GetActiveTaskByInstanceId;
import mn.erin.domain.bpm.usecase.task.GetActiveTaskByInstanceIdOutput;
import mn.erin.domain.bpm.usecase.task.GetActiveTaskByProcessIdAndDefinitionKey;
import mn.erin.domain.bpm.usecase.task.GetActiveTaskByProcessIdAndDefinitionKeyInput;

/**
 * @author EBazarragchaa
 */
public class ProcessUtils
{

  public static void validateProcessParameters(Map<ParameterEntityType, Map<String, Serializable>> parameters) throws UseCaseException
  {
    if (parameters == null)
    {
      throw new UseCaseException(BpmMessagesConstants.PARAMETER_NULL_CODE, BpmMessagesConstants.PARAMETER_NULL_MESSAGE);
    }

    validate(parameters);
  }

  public static void submitProcessByDefKey(String instanceId, String defKey, TaskService taskService, RuntimeService runtimeService,
      ExecutionService executionService,
      TaskFormService taskFormService, CaseService caseService) throws UseCaseException
  {
    GetActiveTaskByInstanceId getActiveTaskByInstanceId = new GetActiveTaskByInstanceId(taskService, runtimeService, executionService);
    GetActiveTaskByInstanceIdOutput activeTasksOutput = getActiveTaskByInstanceId.execute(instanceId);
    for (Task activeTask : activeTasksOutput.getActiveTasks())
    {
      String id = activeTask.getId().getId();
      String definitionKey = activeTask.getDefinitionKey();
      if (definitionKey.equals(defKey))
      {
        Map<String, Object> properties = getFormProperties(instanceId, id, taskFormService);
        SubmitFormInput submitFormInput = new SubmitFormInput(id, instanceId, properties);
        SubmitForm submitForm = new SubmitForm(taskFormService, caseService);
        submitForm.execute(submitFormInput);
      }
    }
  }

  public static void submitProcessByDefKeyAndProcessId(String instanceId, String definitionKey, TaskService taskService,
      TaskFormService taskFormService, RuntimeService runtimeService) throws UseCaseException
  {
    GetActiveTaskByProcessIdAndDefinitionKeyInput input = new GetActiveTaskByProcessIdAndDefinitionKeyInput(instanceId, definitionKey);
    GetActiveTaskByProcessIdAndDefinitionKey useCase = new GetActiveTaskByProcessIdAndDefinitionKey(taskService);

    List<Task> activeTasksOutput = useCase.execute(input);
    for (Task activeTask : activeTasksOutput)
    {
      String executionId = activeTask.getExecutionId().getId();
      String taskId = activeTask.getId().getId();

      Map<String, Object> properties = getFormPropertiesByDefKeyAndProcessId(instanceId, taskFormService, definitionKey);

      SubmitProcessFormInput submitProcessFormInput = new SubmitProcessFormInput(executionId, taskId, properties);
      SubmitProcessForm submitProcessForm = new SubmitProcessForm(runtimeService, taskFormService);

      SubmitProcessFormOutput submitProcessFormOutput = submitProcessForm.execute(submitProcessFormInput);
    }
  }


  private static Map<String, Object> getFormProperties(String instanceId, String taskId, TaskFormService taskFormService) throws UseCaseException
  {
    GetFormByTaskId getFormByTaskId = new GetFormByTaskId(taskFormService);
    TaskForm taskForm = getFormByTaskId.execute(new GetFormByTaskIdInput(instanceId, taskId)).getTaskForm();
    Map<String, Object> properties = new HashMap<>();
    for (TaskFormField field : taskForm.getTaskFormFields())
    {
      properties.put(field.getId().getId(), field.getFormFieldValue().getDefaultValue());
    }
    return properties;
  }

  public static Map<String, Object> getFormPropertiesByDefKeyAndProcessId(String processInstanceId, TaskFormService taskFormService, String definitionKey) throws UseCaseException
  {
    GetFormsByProcessInstanceIdInput input = new GetFormsByProcessInstanceIdInput(processInstanceId);
    GetFormsByProcessInstanceId getFormsByProcessInstanceId = new GetFormsByProcessInstanceId(taskFormService);

    TaskListOutput taskFormOutput = getFormsByProcessInstanceId.execute(input);

    Map<String, Object> properties = new HashMap<>();

    for (TaskForm taskForm : taskFormOutput.getTaskFormList())
    {
      if (taskForm.getTaskDefinitionKey().equals(definitionKey))
      {
        for (TaskFormField formField : taskForm.getTaskFormFields())
        {
          properties.put(formField.getId().getId(), formField.getFormFieldValue().getDefaultValue());
        }
      }
    }
    return properties;
  }
  public static TaskForm getTaskFormByDefKeyAndProcessId(String processInstanceId, TaskFormService taskFormService, String definitionKey) throws UseCaseException
  {
    GetFormsByProcessInstanceIdInput input = new GetFormsByProcessInstanceIdInput(processInstanceId);
    GetFormsByProcessInstanceId getFormsByProcessInstanceId = new GetFormsByProcessInstanceId(taskFormService);

    TaskListOutput taskFormOutput = getFormsByProcessInstanceId.execute(input);
    for (TaskForm taskForm : taskFormOutput.getTaskFormList())
    {
      if (taskForm.getTaskDefinitionKey().equals(definitionKey))
      {
        return  taskForm;
      }
    }
    return null;
  }
  public static void disableServiceTasksById(String instanceId, List<String> taskIds){

  }

  private static void validate(Map<ParameterEntityType, Map<String, Serializable>> parameters) throws UseCaseException
  {
    for (Map.Entry<ParameterEntityType, Map<String, Serializable>> parameter : parameters.entrySet())
    {
      if (parameter.getKey() == null)
      {
        throw new UseCaseException(BpmMessagesConstants.PARAMETER_ENTITY_TYPE_NULL_CODE, BpmMessagesConstants.PARAMETER_ENTITY_TYPE_NULL_MESSAGE);
      }

      for (Map.Entry<String, Serializable> nameAndValue : parameter.getValue().entrySet())
      {
        if (nameAndValue.getKey() == null || StringUtils.isBlank(nameAndValue.getKey()))
        {
          throw new UseCaseException(BpmMessagesConstants.PARAMETER_NAME_NULL_CODE, BpmMessagesConstants.PARAMETER_NAME_NULL_MESSAGE);
        }
        if (nameAndValue.getValue() instanceof String)
        {
          String parameterValueString = nameAndValue.getValue().toString();
          if (StringUtils.isBlank(parameterValueString))
          {
            nameAndValue.setValue("empty");
          }
        }
      }
    }
  }
}
