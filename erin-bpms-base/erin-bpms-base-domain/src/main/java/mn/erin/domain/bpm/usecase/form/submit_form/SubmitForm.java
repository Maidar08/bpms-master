/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.form.submit_form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmMessagesConstants;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.model.form.FieldValidation;
import mn.erin.domain.bpm.model.form.FormFieldId;
import mn.erin.domain.bpm.model.form.TaskForm;
import mn.erin.domain.bpm.model.form.TaskFormField;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.service.TaskFormService;
import mn.erin.domain.bpm.usecase.form.case_variable.SetCaseVariables;

import static mn.erin.domain.bpm.util.process.BpmUtils.toInt;
import static mn.erin.domain.bpm.util.process.SubmitFormUtils.filterFingerPrintValue;
import static mn.erin.domain.bpm.util.process.SubmitFormUtils.setDateProperties;
import static mn.erin.domain.bpm.util.process.SubmitFormUtils.toConvertLong;

/**
 * @author Tamir
 */
public class SubmitForm implements UseCase<SubmitFormInput, SubmitFormOutput>
{
  private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(SubmitForm.class);
  private static final String ERR_MSG_INPUT_IS_REQUIRED = "Submit form input is required!";

  private final TaskFormService taskFormService;
  private final CaseService caseService;

  public SubmitForm(TaskFormService taskFormService, CaseService caseService)
  {
    this.taskFormService = Objects.requireNonNull(taskFormService, "Task form service is required!");
    this.caseService = caseService;
  }

  @Override
  public SubmitFormOutput execute(SubmitFormInput input) throws UseCaseException
  {
    if (input == null)
    {
      String errorCode = "BPMS026";
      throw new UseCaseException(errorCode, ERR_MSG_INPUT_IS_REQUIRED);
    }

    String caseInstanceId = input.getCaseInstanceId();
    String taskId = input.getTaskId();
    Map<String, Object> properties = input.getProperties();

    setDateProperties(properties);
    Map<String, Object> filteredProperties = filterFingerPrintValue(properties);

    try
    {
      setCaseVariables(taskId, caseInstanceId, filteredProperties);
      Map<String, Object> readOnlyVariables = new HashMap<>();
      Map<String, Object> editableProperties = getEditableProperties(caseInstanceId, taskId, new HashMap<>(properties), readOnlyVariables);
      taskFormService.submitForm(taskId, editableProperties);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(BpmMessagesConstants.COULD_NOT_SUBMIT_CODE, e.getMessage(), e.getCause());
    }

    return new SubmitFormOutput(true);
  }

  private void setCaseVariables(String taskId, String caseInstanceId, Map<String, Object> editableProperties) throws UseCaseException
  {
    SetCaseVariables setCaseVariables = new SetCaseVariables(caseService);
    setCaseVariables.execute(new SubmitFormInput(taskId, caseInstanceId, editableProperties));
  }

  private Map<String, Object> getEditableProperties(String caseInstanceId, String taskId, Map<String, Object> properties, Map<String, Object> readOnlyVariables)
      throws BpmServiceException
  {
    TaskForm taskForm = taskFormService.getFormByTaskId(caseInstanceId, taskId);

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
      if (toConvertLong(taskFormField, properties) && null != properties.get(id))
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
