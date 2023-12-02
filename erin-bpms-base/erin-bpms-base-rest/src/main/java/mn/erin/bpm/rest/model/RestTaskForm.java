/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.rest.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tamir
 */
public class RestTaskForm
{
  private String formId;
  private String taskId;
  private Collection<RestFormField> taskFormFields;
  private Map<String, Object> tableData = new HashMap<>();

  public RestTaskForm(String formId, String taskId, Collection<RestFormField> taskFormFields)
  {
    this.formId = formId;
    this.taskId = taskId;
    this.taskFormFields = taskFormFields;
  }

  public String getFormId()
  {
    return formId;
  }

  public void setFormId(String formId)
  {
    this.formId = formId;
  }

  public String getTaskId()
  {
    return taskId;
  }

  public void setTaskId(String taskId)
  {
    this.taskId = taskId;
  }

  public Collection<RestFormField> getTaskFormFields()
  {
    return taskFormFields;
  }

  public void setTaskFormFields(Collection<RestFormField> taskFormFields)
  {
    this.taskFormFields = taskFormFields;
  }

  public Map<String, Object> getTableData()
  {
    return tableData;
  }

  public void setTableData(Map<String, Object> tableData)
  {
    this.tableData = tableData;
  }
}
