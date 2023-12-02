package mn.erin.bpms.loan.consumption.service_task.xyp_system;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import mn.erin.alfresco.connector.model.RestDynamicTable;
import mn.erin.alfresco.connector.model.RestTableCell;
import mn.erin.domain.base.model.person.Person;
import mn.erin.domain.base.model.person.PersonInfo;
import mn.erin.domain.bpm.model.property.PropertyProcess;

import static mn.erin.common.datetime.DateTimeUtils.SHORT_ISO_DATE_FORMAT;
import static mn.erin.domain.bpm.BpmModuleConstants.BLANK;
import static mn.erin.domain.bpm.BpmModuleConstants.STRING_AS_EMPTY;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.PROPERTY_OWNER_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.PROPERTY_SERVICE_DATE_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.PROPERTY_SERVICE_NAME_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.TABLE_NAME_PROPERTY_INFO;

/**
 * @author Tamir
 */
public final class KhurPropertyInfoUtils
{
  public static final String COMMA_SEPERATOR = ", ";

  private KhurPropertyInfoUtils()
  {

  }

  public static RestDynamicTable getPropertyTable(List<PropertyProcess> propertyProcessList)
  {
    RestDynamicTable table = new RestDynamicTable();

    List<String> columns = Arrays.asList(PROPERTY_SERVICE_NAME_FIELD, PROPERTY_SERVICE_DATE_FIELD, PROPERTY_OWNER_FIELD);

    List<List<RestTableCell>> rows = new ArrayList<>();

    if (null == propertyProcessList || propertyProcessList.isEmpty())
    {
      List<RestTableCell> tableCells = new ArrayList<>();

      tableCells.add(new RestTableCell(PROPERTY_SERVICE_NAME_FIELD, BLANK));
      tableCells.add(new RestTableCell(PROPERTY_SERVICE_DATE_FIELD, BLANK));
      tableCells.add(new RestTableCell(PROPERTY_OWNER_FIELD, BLANK));

      rows.add(tableCells);

      table.setName(TABLE_NAME_PROPERTY_INFO);
      table.setColumns(columns);
      table.setRows(rows);

      return table;
    }

    for (PropertyProcess propertyProcess : propertyProcessList)
    {
      List<RestTableCell> tableCells = new ArrayList<>();

      String serviceName = propertyProcess.getServiceName();
      Date processDate = propertyProcess.getProcessDate();
      List<Person> ownerList = propertyProcess.getOwnerList();

      tableCells.add(new RestTableCell(PROPERTY_SERVICE_NAME_FIELD, getNotNullValue(serviceName)));
      tableCells.add(new RestTableCell(PROPERTY_SERVICE_DATE_FIELD, getNotNullValue(toSimpleISODateStr(processDate))));

      if (ownerList.isEmpty())
      {
        tableCells.add(new RestTableCell(PROPERTY_OWNER_FIELD, BLANK));
      }
      else
      {
        StringBuilder ownerStrBuilder = new StringBuilder();

        for (Person person : ownerList)
        {
          String personIdStr = person.getId().getId();

          if (null != personIdStr && !personIdStr.equalsIgnoreCase(STRING_AS_EMPTY))
          {
            ownerStrBuilder.append(personIdStr).append(COMMA_SEPERATOR);
          }

          PersonInfo personInfo = person.getPersonInfo();

          if (null != personInfo)
          {
            String firstName = personInfo.getFirstName();
            String lastName = personInfo.getLastName();

            if (!StringUtils.isBlank(firstName))
            {
              ownerStrBuilder.append(firstName).append(COMMA_SEPERATOR);
            }
            if (!StringUtils.isBlank(lastName))
            {
              ownerStrBuilder.append(lastName).append(COMMA_SEPERATOR);
            }
          }
        }

        String ownerInfoStr = ownerStrBuilder.toString();
        String validOwnerString = ownerInfoStr.replaceAll(", $", "");
        tableCells.add(new RestTableCell(PROPERTY_OWNER_FIELD, getNotNullValue(validOwnerString)));
      }

      rows.add(tableCells);
    }

    table.setName(TABLE_NAME_PROPERTY_INFO);
    table.setColumns(columns);
    table.setRows(rows);

    return table;
  }

  public static String toSimpleISODateStr(Date date)
  {
    if (null == date)
    {
      return BLANK;
    }
    DateFormat sdf = new SimpleDateFormat(SHORT_ISO_DATE_FORMAT);

    return sdf.format(date);
  }

  public static String getNotNullValue(String value)
  {
    if (null == value || "null".equalsIgnoreCase(value))
    {
      return BLANK;
    }

    return value;
  }
}
