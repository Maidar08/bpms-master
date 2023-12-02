package mn.erin.bpm.rest.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tamir
 */
public class RestCompletedForm implements Serializable
{
  private static final long serialVersionUID = -8552574541715810634L;

  private String caseInstanceId;
  private String taskId;
  private String formId;
  private String taskDefinitionKey;

  private Collection<RestCompletedFormField> formFields = new ArrayList<>();
  private Map<String, Object> specialForms = new HashMap<>();

  public RestCompletedForm()
  {
  }

  public RestCompletedForm(String caseInstanceId, String taskId, String formId, Collection<RestCompletedFormField> formFields,
      Map<String, Object> specialForms)
  {
    this.caseInstanceId = caseInstanceId;
    this.taskId = taskId;
    this.formId = formId;

    this.formFields = formFields;
    this.specialForms = specialForms;
  }

  public RestCompletedForm(String caseInstanceId, String taskId, String formId, String taskDefKey, Collection<RestCompletedFormField> formFields,
                           Map<String, Object> specialForms)
  {
    this.caseInstanceId = caseInstanceId;
    this.taskId = taskId;
    this.formId = formId;

    this.formFields = formFields;
    this.specialForms = specialForms;
    this.taskDefinitionKey = taskDefKey;
  }

  public String getCaseInstanceId()
  {
    return caseInstanceId;
  }

  public void setCaseInstanceId(String caseInstanceId)
  {
    this.caseInstanceId = caseInstanceId;
  }

  public String getTaskId()
  {
    return taskId;
  }

  public void setTaskId(String taskId)
  {
    this.taskId = taskId;
  }

  public String getFormId()
  {
    return formId;
  }

  public void setFormId(String formId)
  {
    this.formId = formId;
  }

  public String getTaskDefinitionKey() {
    return taskDefinitionKey;
  }

  public void setTaskDefinitionKey(String taskDefinitionKey) {
    this.taskDefinitionKey = taskDefinitionKey;
  }

  public Collection<RestCompletedFormField> getFormFields()
  {
    return formFields;
  }

  public void setFormFields(Collection<RestCompletedFormField> formFields)
  {
    this.formFields = formFields;
  }

  public Map<String, Object> getSpecialForms()
  {
    return specialForms;
  }

  public void setSpecialForms(Map<String, Object> specialForms)
  {
    this.specialForms = specialForms;
  }
}
