/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.domain.ohs.xac.repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONObject;

import mn.erin.common.file.FileUtil;
import mn.erin.domain.base.model.EntityId;
import mn.erin.domain.bpm.model.form.TaskForm;
import mn.erin.domain.bpm.model.form.TaskFormField;
import mn.erin.domain.bpm.model.form.TaskFormId;
import mn.erin.domain.bpm.model.task.TaskId;
import mn.erin.domain.bpm.repository.TaskFormRepository;

import static mn.erin.bpm.domain.ohs.xac.repository.XacTaskFormUtils.toTaskFormFields;

/**
 * @author Tamir
 */
public class XacTaskFormRepository implements TaskFormRepository
{
  private static final String FORM_JSON = "/xac-task-forms.json";
  private static final String FORM_FIELDS = "formFields";


  private static final String TASK_ID = "taskId";
  private static final String FORM_ID = "formId";

  @Override
  public TaskForm findById(EntityId entityId)
  {
    try (InputStream inputStream = XacTaskFormRepository.class.getResourceAsStream(FORM_JSON))
    {
      JSONArray taskForms = FileUtil.readInputStream(inputStream);

      for (int index = 0; index < taskForms.length(); index++)
      {
        JSONObject taskFormJson = taskForms.getJSONObject(index);

        String taskId = taskFormJson.getString(TASK_ID);
        String formId = taskFormJson.getString(FORM_ID);

        if (formId.equalsIgnoreCase(entityId.getId()))
        {
          JSONArray formFields = taskFormJson.getJSONArray(FORM_FIELDS);
          Collection<TaskFormField> taskFormFields = toTaskFormFields(formFields);

          return new TaskForm(TaskFormId.valueOf(formId), TaskId.valueOf(taskId), taskFormFields);
        }
      }
    }
    catch (IOException e)
    {
      throw new IllegalArgumentException(e.getMessage(), e);
    }
    return null;
  }

  @Override
  public Collection<TaskForm> findAll()
  {
    throw new UnsupportedOperationException();
  }
}
