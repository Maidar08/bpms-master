package mn.erin.domain.bpm.util.process;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.model.form.TaskFormField;

import static mn.erin.domain.bpm.BpmModuleConstants.FINGER_PRINT_LOWER_CASE;
import static mn.erin.domain.bpm.util.process.BpmUtils.isInt;

public class SubmitFormUtils
{
  public static void setDateProperties(Map<String, Object> properties) throws UseCaseException
  {
    Set<Map.Entry<String, Object>> entries = properties.entrySet();

    for (Map.Entry<String, Object> property : entries)
    {
      if (property.getKey().contains(BpmModuleConstants.DATE_POSTFIX) || property.getKey().contains(BpmModuleConstants.DATE_PREFIX))
      {
        Object dateValue = property.getValue();

        if (null != dateValue && !(property.getValue() instanceof Date))
        {
          String dateString = (String) property.getValue();
          Date formattedDate = getDateFromString(dateString);

          if (null != formattedDate)
          {
            property.setValue(formattedDate);
          }
        }
      }
    }
  }

  public static Date getDateFromString(String dateString) throws UseCaseException
  {
    if (StringUtils.isBlank(dateString))
    {
      return null;
    }
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
      try
      {
        DateTimeFormatter inputFormatter2 = DateTimeFormatter.ofPattern(BpmModuleConstants.ISO_SIMPLE_DATE_FORMATTER, Locale.getDefault());
        LocalDate localDate = LocalDate.parse(dateString, inputFormatter2);

        return Date.from(localDate.atStartOfDay()
            .atZone(ZoneId.of(BpmModuleConstants.UTC_8_ZONE))
            .toInstant());
      }
      catch (Exception exception)
      {
        throw new UseCaseException(e.getMessage());
      }
    }
  }

  public static Map<String, Object> filterFingerPrintValue(Map<String, Object> properties)
  {
    Map<String, Object> filteredMap = new HashMap<>();

    Set<Map.Entry<String, Object>> entries = properties.entrySet();

    for (Map.Entry<String, Object> entry : entries)
    {
      String lowerCaseKey = entry.getKey().toLowerCase();

      if (!lowerCaseKey.contains(FINGER_PRINT_LOWER_CASE))
      {
        filteredMap.put(entry.getKey(), entry.getValue());
      }
    }
    return filteredMap;
  }

  public static boolean toConvertLong(TaskFormField taskFormField)
  {
    return taskFormField.getType().equals("long") && isInt(taskFormField.getFormFieldValue().getDefaultValue());
  }

  public static boolean toConvertLong(TaskFormField taskFormField, Map<String, Object> properties)
  {
    return taskFormField.getType().equals("long") && isInt(taskFormField.getFormFieldValue().getDefaultValue());
  }
}
