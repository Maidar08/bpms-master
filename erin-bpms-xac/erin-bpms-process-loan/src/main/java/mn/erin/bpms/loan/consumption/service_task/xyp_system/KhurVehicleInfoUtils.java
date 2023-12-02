package mn.erin.bpms.loan.consumption.service_task.xyp_system;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import mn.erin.alfresco.connector.model.RestDynamicTable;
import mn.erin.alfresco.connector.model.RestTableCell;
import mn.erin.alfresco.connector.model.RestTextValueField;
import mn.erin.domain.base.model.person.AddressInfo;
import mn.erin.domain.base.model.person.ContactInfo;
import mn.erin.domain.base.model.person.PersonId;
import mn.erin.domain.base.model.person.PersonInfo;
import mn.erin.domain.bpm.model.vehicle.VehicleInfo;
import mn.erin.domain.bpm.model.vehicle.VehicleOwner;

import static mn.erin.bpms.loan.consumption.service_task.xyp_system.KhurPropertyInfoUtils.getNotNullValue;
import static mn.erin.bpms.loan.consumption.service_task.xyp_system.KhurPropertyInfoUtils.toSimpleISODateStr;
import static mn.erin.domain.bpm.BpmModuleConstants.BLANK;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.BUILD_YEAR_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.CABIN_NUMBER_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.CERTIFICATE_NUMBER_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.COLOR_NAME_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.DAY_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.IMPORT_DATE_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.MARK_NAME_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.MODEL_NAME_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.MONTH_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.OWNER_INFO_FIRST_NAME_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.OWNER_INFO_FROM_DATE_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.OWNER_INFO_FULL_ADDRESS_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.OWNER_INFO_LAST_NAME_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.OWNER_INFO_PHONE_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.OWNER_INFO_REGISTER_NUMBER_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.OWNER_INFO_TO_DATE_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.PLATE_NUMBER_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.TABLE_NAME_OWNER_INFO;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.TABLE_NAME_VEHICLE_INFO;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.TRANSMISSION_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.VEHICLE_REF_SYSTEM_DATE_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.YEAR_FIELD;

/**
 * @author Tamir
 */
public final class KhurVehicleInfoUtils
{
  private KhurVehicleInfoUtils()
  {

  }

  public static RestDynamicTable getVehicleTable(VehicleInfo vehicleInfo)
  {
    RestDynamicTable table = new RestDynamicTable();

    List<String> columns = Arrays.asList(PLATE_NUMBER_FIELD, MARK_NAME_FIELD, MODEL_NAME_FIELD, CABIN_NUMBER_FIELD,
        BUILD_YEAR_FIELD, IMPORT_DATE_FIELD, COLOR_NAME_FIELD, TRANSMISSION_FIELD, CERTIFICATE_NUMBER_FIELD);

    List<List<RestTableCell>> rows = new ArrayList<>();

    List<RestTableCell> tableCells = new ArrayList<>();

    String plateNumber = vehicleInfo.getPlateNumber();
    String mark = vehicleInfo.getMark();

    String model = vehicleInfo.getModel();
    String cabinNumber = vehicleInfo.getCabinNumber();

    String yearOfMadeStr = String.valueOf(vehicleInfo.getYearOfMade());
    String importedDateStr = toSimpleISODateStr(vehicleInfo.getImportedDate());

    String color = vehicleInfo.getColor();
    String fuelType = vehicleInfo.getFuelType();
    String certificateNumber = vehicleInfo.getCertificateNumber();

    tableCells.add(new RestTableCell(PLATE_NUMBER_FIELD, getNotNullValue(plateNumber)));
    tableCells.add(new RestTableCell(MARK_NAME_FIELD, getNotNullValue(mark)));

    tableCells.add(new RestTableCell(MODEL_NAME_FIELD, getNotNullValue(model)));
    tableCells.add(new RestTableCell(CABIN_NUMBER_FIELD, getNotNullValue(cabinNumber)));

    tableCells.add(new RestTableCell(BUILD_YEAR_FIELD, getNotNullValue(yearOfMadeStr)));
    tableCells.add(new RestTableCell(IMPORT_DATE_FIELD, getNotNullValue(importedDateStr)));

    tableCells.add(new RestTableCell(COLOR_NAME_FIELD, getNotNullValue(color)));
    tableCells.add(new RestTableCell(TRANSMISSION_FIELD, getNotNullValue(fuelType)));
    tableCells.add(new RestTableCell(CERTIFICATE_NUMBER_FIELD, getNotNullValue(certificateNumber)));

    rows.add(tableCells);

    table.setName(TABLE_NAME_VEHICLE_INFO);
    table.setColumns(columns);
    table.setRows(rows);

    return table;
  }

  public static RestDynamicTable getOwnerTable(VehicleInfo vehicleInfo)
  {
    RestDynamicTable ownerTable = new RestDynamicTable();
    List<VehicleOwner> owners = vehicleInfo.getOwners();

    if (null == owners || owners.isEmpty())
    {
      ownerTable.setName(TABLE_NAME_OWNER_INFO);
      ownerTable.setColumns(Arrays.asList(OWNER_INFO_FROM_DATE_FIELD, OWNER_INFO_TO_DATE_FIELD, OWNER_INFO_LAST_NAME_FIELD,
          OWNER_INFO_FIRST_NAME_FIELD, OWNER_INFO_REGISTER_NUMBER_FIELD, OWNER_INFO_PHONE_FIELD, OWNER_INFO_FULL_ADDRESS_FIELD));

      ownerTable.setRows(getBlankOwnersList());

      return ownerTable;
    }

    List<List<RestTableCell>> rows = getOwnerRows(owners);

    ownerTable.setName(TABLE_NAME_OWNER_INFO);
    ownerTable.setColumns(Arrays.asList(OWNER_INFO_FROM_DATE_FIELD, OWNER_INFO_TO_DATE_FIELD, OWNER_INFO_LAST_NAME_FIELD,
        OWNER_INFO_FIRST_NAME_FIELD, OWNER_INFO_REGISTER_NUMBER_FIELD, OWNER_INFO_PHONE_FIELD, OWNER_INFO_FULL_ADDRESS_FIELD));

    ownerTable.setRows(rows);

    return ownerTable;
  }

  public static List<List<RestTableCell>> getBlankOwnersList()
  {
    List<List<RestTableCell>> blankOwners = new ArrayList<>();

    List<RestTableCell> tableCells = new ArrayList<>();

    tableCells.add(new RestTableCell(OWNER_INFO_REGISTER_NUMBER_FIELD, BLANK));
    tableCells.add(new RestTableCell(OWNER_INFO_FROM_DATE_FIELD, BLANK));
    tableCells.add(new RestTableCell(OWNER_INFO_TO_DATE_FIELD, BLANK));
    tableCells.add(new RestTableCell(OWNER_INFO_LAST_NAME_FIELD, BLANK));
    tableCells.add(new RestTableCell(OWNER_INFO_FIRST_NAME_FIELD, BLANK));
    tableCells.add(new RestTableCell(OWNER_INFO_PHONE_FIELD, BLANK));
    tableCells.add(new RestTableCell(OWNER_INFO_FULL_ADDRESS_FIELD, BLANK));

    blankOwners.add(tableCells);

    return blankOwners;
  }

  public static List<List<RestTableCell>> getOwnerRows(List<VehicleOwner> owners)
  {
    List<List<RestTableCell>> rows = new ArrayList<>();

    for (VehicleOwner owner : owners)
    {
      List<RestTableCell> tableCells = new ArrayList<>();

      PersonId personId = owner.getId();

      if (null == personId)
      {
        tableCells.add(new RestTableCell(OWNER_INFO_REGISTER_NUMBER_FIELD, BLANK));
      }
      else
      {
        tableCells.add(new RestTableCell(OWNER_INFO_REGISTER_NUMBER_FIELD, getNotNullValue(personId.getId())));
      }

      Date startOwnerDate = owner.getStartOwnerDate();
      Date endOwnerDate = owner.getEndOwnerDate();

      tableCells.add(new RestTableCell(OWNER_INFO_FROM_DATE_FIELD, toSimpleISODateStr(startOwnerDate)));
      tableCells.add(new RestTableCell(OWNER_INFO_TO_DATE_FIELD, toSimpleISODateStr(endOwnerDate)));

      PersonInfo personInfo = owner.getPersonInfo();

      if (null == personInfo)
      {
        tableCells.add(new RestTableCell(OWNER_INFO_LAST_NAME_FIELD, BLANK));
        tableCells.add(new RestTableCell(OWNER_INFO_FIRST_NAME_FIELD, BLANK));
      }
      else
      {
        String lastName = personInfo.getLastName();
        String firstName = personInfo.getFirstName();

        tableCells.add(new RestTableCell(OWNER_INFO_LAST_NAME_FIELD, getNotNullValue(lastName)));
        tableCells.add(new RestTableCell(OWNER_INFO_FIRST_NAME_FIELD, getNotNullValue(firstName)));
      }

      List<ContactInfo> contactInfoList = owner.getContactInfoList();

      if (contactInfoList.isEmpty())
      {
        tableCells.add(new RestTableCell(OWNER_INFO_PHONE_FIELD, BLANK));
      }
      else
      {
        ContactInfo contactInfo = contactInfoList.get(0);

        if (null == contactInfo)
        {
          tableCells.add(new RestTableCell(OWNER_INFO_PHONE_FIELD, BLANK));
        }
        else
        {
          String phone = contactInfo.getPhone();
          tableCells.add(new RestTableCell(OWNER_INFO_PHONE_FIELD, getNotNullValue(phone)));
        }
      }

      List<AddressInfo> addressInfoList = owner.getAddressInfoList();

      if (addressInfoList.isEmpty())
      {
        tableCells.add(new RestTableCell(OWNER_INFO_FULL_ADDRESS_FIELD, BLANK));
      }
      else
      {
        AddressInfo addressInfo = addressInfoList.get(0);

        if (null == addressInfo)
        {
          tableCells.add(new RestTableCell(OWNER_INFO_FULL_ADDRESS_FIELD, BLANK));
        }
        else
        {
          String fullAddress = addressInfo.getFullAddress();
          tableCells.add(new RestTableCell(OWNER_INFO_FULL_ADDRESS_FIELD, getNotNullValue(fullAddress)));
        }
      }

      rows.add(tableCells);
    }

    return rows;
  }

  public static void setCurrentDateFields(List<RestTextValueField> textFields)
  {
    LocalDate localDate = LocalDate.now();
    String currentSystemDate = localDate.toString();

    int year = localDate.getYear();
    Month localDateMonth = localDate.getMonth();
    int monthValue = localDateMonth.getValue();
    int dayOfMonth = localDate.getDayOfMonth();

    textFields.add(new RestTextValueField(YEAR_FIELD, String.valueOf(year)));
    textFields.add(new RestTextValueField(MONTH_FIELD, String.valueOf(monthValue)));

    textFields.add(new RestTextValueField(DAY_FIELD, String.valueOf(dayOfMonth)));
    textFields.add(new RestTextValueField(VEHICLE_REF_SYSTEM_DATE_FIELD, currentSystemDate));
  }
}
