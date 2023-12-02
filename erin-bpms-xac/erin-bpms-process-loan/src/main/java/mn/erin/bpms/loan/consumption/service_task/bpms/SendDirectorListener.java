package mn.erin.bpms.loan.consumption.service_task.bpms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.spin.plugin.variable.SpinValues;
import org.camunda.spin.plugin.variable.value.JsonValue;
import org.json.JSONObject;

import mn.erin.domain.bpm.model.form.FieldProperty;
import mn.erin.domain.bpm.model.form.FormFieldId;
import mn.erin.domain.bpm.model.form.TaskForm;
import mn.erin.domain.bpm.model.form.TaskFormField;
import mn.erin.domain.bpm.service.TaskFormService;

/**
 * @author Zorig
 */
public class SendDirectorListener implements TaskListener
{
  private final TaskFormService taskFormService;

  public SendDirectorListener(TaskFormService taskFormService)
  {
    this.taskFormService = taskFormService;
  }

  @Override
  public void notify(DelegateTask delegateTask)
  {
    String taskId = delegateTask.getId();


    try
    {
      TaskForm formByTaskId = taskFormService.getFormByTaskIdBeforeCreationOfTask(taskId);

      Collection<TaskFormField> taskFormFields = formByTaskId.getTaskFormFields();

      /*FormService formService = delegateTask.getProcessEngineServices().getFormService();

      TaskFormData taskFormData = formService.getTaskFormData(taskId);

      List<FormField> formFields = taskFormData.getFormFields();

      for (FormField formField : formFields)
      {
        String formFieldId = formField.getId();

        if (formFieldId.equals("receivers"))
        {

        }
      }

      TaskFormField formField = new TaskFormField();
      formField.setLabel("Label!");
      formField.setId(FormFieldId.valueOf("receivers"));
      formField.setType("String");

      List<FieldProperty> fieldProperties = new ArrayList<>();

      List<String> userIds = new ArrayList<>();
      userIds.add("bat");
      userIds.add("bold");

      for (String userId : userIds)
      {
        FieldProperty fieldProperty = new FieldProperty(userId, userId);
        fieldProperties.add(fieldProperty);
      }

      formField.setFieldProperties(fieldProperties);

      delegateTask.setVariable(taskId, formField);*/

      for (TaskFormField taskFormField : taskFormFields)
      {
        FormFieldId id = taskFormField.getId();
        String formFieldId = id.getId();

        if (formFieldId.equals("receivers"))
        {
          // 1. get all director user id.

          // 2. set form field property

          List<String> userIds = new ArrayList<>();

          userIds.add("bat");
          userIds.add("bold");

          List<FieldProperty> fieldProperties = new ArrayList<>();

          for (String userId : userIds)
          {
            FieldProperty fieldProperty = new FieldProperty(userId, userId);
            fieldProperties.add(fieldProperty);
          }

          taskFormField.setFieldProperties(fieldProperties);

          JSONObject json = new JSONObject();
          JSONObject innerJson = new JSONObject();
          innerJson.put("type", "String");
          innerJson.put("value", "Stri");
          json.put("receivers", innerJson);

          JsonValue jsonValue = SpinValues.jsonValue(json.toString()).create();

          List<String> list1 = new ArrayList<>();
          list1.add("1");
          list1.add("2");

          delegateTask.setVariable(taskId, jsonValue);
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
