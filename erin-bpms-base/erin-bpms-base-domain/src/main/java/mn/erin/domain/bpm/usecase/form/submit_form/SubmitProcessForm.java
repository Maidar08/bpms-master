package mn.erin.domain.bpm.usecase.form.submit_form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.model.form.FieldValidation;
import mn.erin.domain.bpm.model.form.FormFieldId;
import mn.erin.domain.bpm.model.form.TaskForm;
import mn.erin.domain.bpm.model.form.TaskFormField;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.RuntimeService;
import mn.erin.domain.bpm.service.TaskFormService;

import static mn.erin.domain.bpm.util.process.BpmUtils.toInt;
import static mn.erin.domain.bpm.util.process.SubmitFormUtils.setDateProperties;
import static mn.erin.domain.bpm.util.process.SubmitFormUtils.toConvertLong;

public class SubmitProcessForm extends AbstractUseCase<SubmitProcessFormInput, SubmitProcessFormOutput>
{
  private final RuntimeService runtimeService;
  private final TaskFormService taskFormService;

  private static final String ERR_CODE_INPUT_IS_REQUIRED = "BPMS026";
  private static final String ERR_MSG_INPUT_IS_REQUIRED = "Submit form input is required!";

  public SubmitProcessForm(RuntimeService runtimeService, TaskFormService taskFormService)
  {
    this.runtimeService = Objects.requireNonNull(runtimeService, "Runtime service is required!");
    this.taskFormService = taskFormService;
  }

  @Override
  public SubmitProcessFormOutput execute(SubmitProcessFormInput input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException(ERR_CODE_INPUT_IS_REQUIRED, ERR_MSG_INPUT_IS_REQUIRED);
    }

    String executionId = input.getExecutionId();
    String taskId = input.getTaskId();
    Map<String, Object> properties = input.getProperties();

    setDateProperties(properties);

    try
    {
      runtimeService.setVariables(executionId, properties);
      Map<String, Object> readOnlyVariables = new HashMap<>();
      Map<String, Object> editableProperties = getProcessEditableProperties(taskId, new HashMap<>(properties), readOnlyVariables);
      taskFormService.submitForm(taskId, editableProperties);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }

    return new SubmitProcessFormOutput(true);
  }

  private Map<String, Object> getProcessEditableProperties(String taskId, Map<String, Object> properties, Map<String, Object> readOnlyVariables)
      throws BpmServiceException
  {
    TaskForm taskForm = taskFormService.getFormByTaskId(taskId);

    if (null == taskForm)
    {
      String errorCode = "CamundaTasKFormService002";
      throw new BpmServiceException(errorCode, "Task form does not exist with task id : " + taskId);
    }

    Collection<TaskFormField> taskFormFields = taskForm.getTaskFormFields();
    List<String> readOnlyFieldIds = new ArrayList<>();

    for (TaskFormField taskFormField : taskFormFields)
    {
      Collection<FieldValidation> fieldValidations = taskFormField.getFieldValidations();
      final String id = taskFormField.getId().getId();

      if (toConvertLong(taskFormField) && null != properties.get(id))
      {
        int intValue = toInt(properties.get(id));
        properties.put(id, (long) intValue);
      }

      for (FieldValidation fieldValidation : fieldValidations)
      {
        if (fieldValidation.getName().equalsIgnoreCase(BpmModuleConstants.READONLY))
        {
          FormFieldId formFieldId = taskFormField.getId();
          readOnlyFieldIds.add(formFieldId.getId());
        }
      }
    }

    for (String formFieldId : readOnlyFieldIds)
    {
      readOnlyVariables.put(formFieldId, properties.get(formFieldId));
      properties.remove(formFieldId);
    }

    return properties;
  }
}
