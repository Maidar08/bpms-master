package mn.erin.domain.bpm.usecase.form.runtime_variable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.model.task.Task;
import mn.erin.domain.bpm.service.RuntimeService;
import mn.erin.domain.bpm.service.TaskService;
import mn.erin.domain.bpm.usecase.form.submit_form.SubmitFormInput;

import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;

/**
 * @author Lkhagvadorj.A
 **/

public class SetVariablesOnActiveTasks extends AbstractUseCase<SubmitFormInput, Void>
{
  private final TaskService taskService;
  private final RuntimeService runtimeService;

  public SetVariablesOnActiveTasks(TaskService taskService, RuntimeService runtimeService)
  {
    this.taskService = Objects.requireNonNull(taskService, "Task Service is required!");
    this.runtimeService = Objects.requireNonNull(runtimeService, "Runtime Service is required!");
  }

  @Override
  public Void execute(SubmitFormInput input) throws UseCaseException
  {
    if (null == input)
    {
      throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
    }
    String caseInstanceId = input.getCaseInstanceId();
    Map<String, Object> variables = input.getProperties();
    List<Task> activeTasks = taskService.getActiveByCaseInstanceId(caseInstanceId);
    setDateProperties(variables);

    activeTasks.forEach(task -> setVariablesOnRuntimeService(task.getProcessInstanceId(), variables));
    return null;
  }

  private void setDateProperties(Map<String, Object> properties) throws UseCaseException
  {
    Set<Map.Entry<String, Object>> entries = properties.entrySet();

    for (Map.Entry<String, Object> property : entries)
    {
      if (property.getKey().contains(BpmModuleConstants.DATE_POSTFIX) || property.getKey().contains(BpmModuleConstants.DATE_PREFIX))
      {
        Object dateValue = property.getValue();

        if (null != dateValue)
        {
          String dateString = (String) property.getValue();
          Date formattedDate = getDateFromString(dateString);
          property.setValue(formattedDate);
        }
      }
    }
  }

  private Date getDateFromString(String dateString) throws UseCaseException
  {
    if (dateString.contains("/"))
    {
      DateFormat simpleDateFormat = new SimpleDateFormat(BpmModuleConstants.SIMPLE_DATE_FORMATTER);
      try
      {
        return simpleDateFormat.parse(dateString);
      }
      catch (ParseException e)
      {
        throw new UseCaseException(e.getMessage());
      }
    }
    DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(BpmModuleConstants.ISO_DATE_FULL_FORMATTER, Locale.getDefault());
    try
    {
      LocalDate localDate = LocalDate.parse(dateString, inputFormatter);

      return Date.from(localDate.atStartOfDay()
          .atZone(ZoneId.of(BpmModuleConstants.UTC_8_ZONE))
          .toInstant());
    }
    catch (Exception e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }

  private void setVariablesOnRuntimeService(String processInstanceId, Map<String, Object> variables)
  {
    for (Map.Entry<String, Object> entry : variables.entrySet())
    {
      runtimeService.setVariable(processInstanceId, entry.getKey(), entry.getValue());
    }
  }
}
