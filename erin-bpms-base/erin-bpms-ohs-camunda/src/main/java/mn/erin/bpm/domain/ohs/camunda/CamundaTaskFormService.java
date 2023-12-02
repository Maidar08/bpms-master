/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.domain.ohs.camunda;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.form.FormField;
import org.camunda.bpm.engine.form.TaskFormData;
import org.camunda.bpm.engine.task.Task;
import org.springframework.util.StringUtils;

import mn.erin.bpm.domain.ohs.camunda.util.CamundaEntityUtil;
import mn.erin.domain.bpm.BpmMessagesConstants;
import mn.erin.domain.bpm.model.form.FormFieldId;
import mn.erin.domain.bpm.model.form.FormFieldValue;
import mn.erin.domain.bpm.model.form.TaskForm;
import mn.erin.domain.bpm.model.form.TaskFormField;
import mn.erin.domain.bpm.model.variable.Variable;
import mn.erin.domain.bpm.model.variable.VariableId;
import mn.erin.domain.bpm.provider.FingerPrintProvider;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.TaskFormService;

import static mn.erin.domain.bpm.BpmMessagesConstants.SUBMIT_NULL_VALUE_ERROR_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.SUBMIT_NULL_VALUE_ERROR_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.EMPLOYEE_FINGER_PRINT;
import static mn.erin.domain.bpm.BpmModuleConstants.FINGER_PRINT;

/**
 * @author Tamir
 */
public class CamundaTaskFormService implements TaskFormService
{
  private static final String ERR_MSG_TASK_ID_EMPTY = "Task id is required!";
  private static final String ERR_MSG_FORM_NOT_EXIST = "Task form does not exist!";

  private static final String ERR_MSG_PROPERTY_EMPTY = "Task id or form properties empty!";
  private static final String ERR_MSG_FORM_NOT_EXIST_WITH_ID = "Form does not exist with process definition id :";
  private static final String FINGER_PRINT_PROVIDER_IS_REQUIRED = "Finger print provider is required!";
  private static final String PREFIX_DEFAULT_BASE_64 = "data:image/png;base64,";

  private final ProcessEngineProvider processEngineProvider;
  private final FingerPrintProvider fingerPrintProvider;

  public CamundaTaskFormService(ProcessEngineProvider processEngineProvider, FingerPrintProvider fingerPrintProvider)
  {
    this.processEngineProvider = Objects.requireNonNull(processEngineProvider);
    this.fingerPrintProvider = Objects.requireNonNull(fingerPrintProvider, FINGER_PRINT_PROVIDER_IS_REQUIRED);
  }

  @Override
  public void submitForm(String taskId, Map<String, Object> properties) throws BpmServiceException
  {
    if (taskId.isEmpty())
    {
      throw new BpmServiceException(ERR_MSG_PROPERTY_EMPTY);
    }

    setFingerPrintProviderValues(properties);

    try
    {
      formService().submitTaskForm(taskId, properties);
    }
    catch (NullPointerException ex)
    {
      throw new BpmServiceException(SUBMIT_NULL_VALUE_ERROR_CODE, SUBMIT_NULL_VALUE_ERROR_MESSAGE, ex.getCause());
    }
    catch (Exception e)
    {
      throw new BpmServiceException(BpmMessagesConstants.COULD_NOT_SUBMIT_CODE, e.getMessage(), e.getCause());
    }
  }

  @Override
  public TaskForm getFormByTaskId(String caseInstanceId, String taskId) throws BpmServiceException
  {
    if (StringUtils.isEmpty(taskId))
    {
      String errorCode = "CamundaTasKFormService001";
      throw new BpmServiceException(errorCode, ERR_MSG_TASK_ID_EMPTY);
    }

    TaskFormData taskFormData = formService().getTaskFormData(taskId);

    if (null == taskFormData)
    {
      String errorCode = "CamundaTasKFormService002";
      throw new BpmServiceException(errorCode, ERR_MSG_FORM_NOT_EXIST);
    }

    List<Variable> variableList = getVariablesByInstanceId(caseInstanceId);

    TaskForm taskForm = CamundaEntityUtil.toTaskForm(taskFormData);
    return fillFormFieldValue(taskForm, variableList);
  }

  @Override
  public TaskForm getFormByTaskIdBeforeCreationOfTask(String taskId) throws BpmServiceException
  {
    if (StringUtils.isEmpty(taskId))
    {
      throw new BpmServiceException(ERR_MSG_TASK_ID_EMPTY);
    }

    TaskFormData taskFormData = formService().getTaskFormData(taskId);

    if (null == taskFormData)
    {
      throw new BpmServiceException(ERR_MSG_FORM_NOT_EXIST);
    }

    List<Variable> variableList = getVariables(taskFormData);

    TaskForm taskForm = CamundaEntityUtil.toTaskForm(taskFormData);
    return fillFormFieldValue(taskForm, variableList);
  }

  @Override
  public List<TaskForm> getFormsByDefinitionId(String definitionId) throws BpmServiceException
  {
    List<Task> taskList = taskService().createTaskQuery()
        .processDefinitionId(definitionId)
        .list();

    if (taskList.isEmpty())
    {
      throw new BpmServiceException(ERR_MSG_FORM_NOT_EXIST_WITH_ID + definitionId);
    }

    return getTaskForms(taskList);
  }

  @Override
  public List<TaskForm> getFormsByDefinitionKey(String definitionKey) throws BpmServiceException
  {
    List<Task> taskList = taskService().createTaskQuery()
        .processDefinitionKey(definitionKey)
        .list();

    if (taskList.isEmpty())
    {
      String errorCode = "CamundaCaseService002";
      throw new BpmServiceException(errorCode, ERR_MSG_FORM_NOT_EXIST_WITH_ID + definitionKey);
    }

    return getTaskForms(taskList);
  }

  @Override
  public List<TaskForm> getFormsByProcessInstanceId(String processInstanceId) throws BpmServiceException
  {
    if (processInstanceId.isEmpty())
    {
      String errorCode = "BPMS013";
      throw new BpmServiceException(errorCode, "Process instance id  is required!");
    }

    List<Task> taskList = taskService().createTaskQuery()
        .processInstanceId(processInstanceId)
        .orderByTaskId()
        .asc()
        .list();

    return getTaskForms(taskList);
  }

  @Override
  public List<TaskForm> getFormsByCaseInstanceId(String caseInstanceId) throws BpmServiceException
  {
    if (caseInstanceId.isEmpty())
    {
      String errorCode = "BPMS015";
      throw new BpmServiceException(errorCode, "Case instance id  is required!");
    }

    List<Task> taskList = taskService().createTaskQuery()
        .caseInstanceId(caseInstanceId)
        .orderByTaskId()
        .asc()
        .list();

    return getTaskForms(taskList);
  }

  @Override
  public TaskForm getFormByTaskId(String taskId)
  {
    Task task = taskService().createTaskQuery().taskId(taskId).initializeFormKeys().singleResult();
    return getTaskForm(task);
  }

  private TaskService taskService()
  {
    return processEngine().getTaskService();
  }

  private FormService formService()
  {
    return processEngine().getFormService();
  }

  private ProcessEngine processEngine()
  {
    return processEngineProvider.getProcessEngine();
  }

  private List<Variable> getVariables(TaskFormData taskFormData)
  {
    List<Variable> variableList = new ArrayList<>();

    List<FormField> formFields = taskFormData.getFormFields();

    for (FormField formField : formFields)
    {
      variableList.add(new Variable(VariableId.valueOf(formField.getId()), (Serializable) formField.getValue()));
    }

    return variableList;
  }

  private List<Variable> getVariablesByInstanceId(String caseInstanceId)
  {
    List<Variable> variableList = new ArrayList<>();

    if (null != caseInstanceId)
    {
      Map<String, Object> variables = processEngine().getCaseService().getVariables(caseInstanceId);
      Set<Map.Entry<String, Object>> entries = variables.entrySet();

      for (Map.Entry<String, Object> entry : entries)
      {
        String key = entry.getKey();
        Object value = entry.getValue();

        variableList.add(new Variable(VariableId.valueOf(key), (Serializable) value));
      }
    }
    return variableList;
  }

  private TaskForm fillFormFieldValue(TaskForm taskForm, List<Variable> variables)
  {
    if (variables.isEmpty())
    {
      return taskForm;
    }

    Collection<TaskFormField> taskFormFields = taskForm.getTaskFormFields();

    for (TaskFormField taskFormField : taskFormFields)
    {
      FormFieldId formFieldId = taskFormField.getId();

      if (null != formFieldId)
      {
        String formFieldIdString = formFieldId.getId();
        for (Variable variable : variables)
        {
          VariableId variableId = variable.getId();
          String id = variableId.getId();

          if (id.equalsIgnoreCase(formFieldIdString))
          {
            Serializable value = variable.getValue();
            if (null != value)
            {
              taskFormField.setFormFieldValue(new FormFieldValue(value));
            }
          }
        }
      }
    }

    taskForm.setTaskFormFields(taskFormFields);

    return taskForm;
  }

  private List<TaskForm> getTaskForms(List<Task> taskList)
  {
    List<TaskForm> taskForms = new ArrayList<>();

    for (Task task : taskList)
    {
      String taskId = task.getId();
      TaskFormData taskFormData = formService().getTaskFormData(taskId);
      taskForms.add(CamundaEntityUtil.toTaskForm(taskFormData));
    }
    return taskForms;
  }

  private TaskForm getTaskForm(Task task)
  {
    String taskId = task.getId();
    TaskFormData taskFormData = formService().getTaskFormData(taskId);

    return CamundaEntityUtil.toTaskForm(taskFormData);
  }

  private void setFingerPrintProviderValues(Map<String, Object> properties)
  {
    String fingerPrint = getStringValue(properties, FINGER_PRINT);
    String empFingerPrint = getStringValue(properties, EMPLOYEE_FINGER_PRINT);

    if (null != fingerPrint)
    {
      fingerPrint = replaceBase64Prefix(fingerPrint);

      fingerPrintProvider.setCustomerFingerPrint(fingerPrint);
      properties.remove(FINGER_PRINT);
    }

    if (null != empFingerPrint)
    {
      empFingerPrint = replaceBase64Prefix(empFingerPrint);

      fingerPrintProvider.setEmployeeFingerPrint(empFingerPrint);
      properties.remove(EMPLOYEE_FINGER_PRINT);
    }
  }

  private String replaceBase64Prefix(String fingerPrint)
  {
    if (fingerPrint.contains(PREFIX_DEFAULT_BASE_64))
    {
      return fingerPrint.replace(PREFIX_DEFAULT_BASE_64, "");
    }
    return fingerPrint;
  }

  private String getStringValue(Map<String, Object> properties, String key)
  {
    if (properties.containsKey(key))
    {
      Object value = properties.get(key);

      if (value instanceof String)
      {
        return (String) value;
      }
    }
    return null;
  }
}
