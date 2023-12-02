package mn.erin.domain.bpm.constants;

/**
 * @author Lkhagvadorj.A
 **/

public class CollateralConstants
{
  private CollateralConstants()
  {

  }

  public static final String IMMOVABLE_COLLATERAL_TYPE = "I";
  public static final String MACHINERY_COLLATERAL_TYPE = "M";
  public static final String VEHICLE_COLLATERAL_TYPE = "V";
  public static final String OTHER_COLLATERAL_TYPE = "O";

  public static final String DELIMITER_COLON = ":";
  public static final String DELIMITER_PERCENT_SIGN = "%";
  public static final String XAC_SERVICE_DATE_FORMATTER_2 = "yyyy-MM-dd'T'HH:mm:ss.SSS";

  public static final String COLLATERAL_CODE = "collateralCode";
  public static final String DEDUCTION_RATE = "deductionRate";

  public static final String HOUSE_NUMBER = "houseNumber";
  public static final String PURPOSE_OF_USAGE = "purposeOfUsage";

  public static final String FORM_NUMBER = "formNumber";
  public static final String PROPERTY_ID = "propertyId";

  public static final String PROPERTY_DOC_NUMBER = "propertyDocNumber";
  public static final String BUILT_AREA = "builtArea";

  public static final String LAND_AREA = "landArea";
  public static final String LEASED_IND = "leasedInd";

  public static final String REVIEW_DATE = "reviewDate";
  public static final String DUE_DATE = "dueDate";
  public static final String RECEIVED_DATE = "receivedDate";

  public static final String CONDITION_OF_CONTRACT = "conditionOfContract";
  public static final String REMARKS = "remarks";

  public static final String YEAR_OF_CONSTRUCTION = "yearOfConstruction";
  public static final String CITY = "city";

  public static final String ADDRESS_1 = "address1";
  public static final String ADDRESS_2 = "address2";
  public static final String ADDRESS_3 = "address3";

  public static final String STREET_NAME = "streetName";
  public static final String STREET_NUMBER = "streetNumber";
  public static final String BUILDER_NAME = "builderName";
  public static final String IMMOVABLE_NUMBER = "immovableNumber";

  public static final String AMOUNT_OF_COLLATERAL = "amountOfCollateral";
  public static final String COLLATERAL_ASSESSMENT = "collateralAssessment";

  public static final String INSPECTION_TYPE = "inspectionType";
  public static final String INSPECTION_AMOUNT_VALUE = "inspectionAmountValue";

  public static final String INSPECTOR_ID = "inspectorId";
  public static final String INSPECTION_DATE = "inspectionDate";
  public static final String INSPECTION_SERIAL_NUM = "inspSerialNumber";

  public static final String OWNERSHIP_TYPE = "ownershipType";
  public static final String OWNER_CIF_NUMBER = "ownerCifNumber";
  public static final String OWNER_NAME = "ownerName";
  public static final String SERIAL_NUM = "serialNumber";

  // Machinery properties
  public static final String MANUFACTURER = "manufacturer";
  public static final String MACHINE_NUMBER = "machineNum";
  public static final String MACHINE_MODEL = "machineModel";
  public static final String MANUFACTURER_YEAR = "manufactureYear";
  public static final String CIF = "cifNumber";

  //vehicle collateral constants

  public static final String VEHICLE_REGISTER_NUMBER = "vehicleRegisterNumber";
  public static final String CHASSIS_NUMBER = "chassisNumber";
  public static final String ENGINE_NUMBER = "engineNumber";
  public static final String MANUFACTURE_YEAR = "manufactureYear";
  public static final String VEHICLE_MODEL = "vehicleModel";
  public static final String FINANCIAL_LEASING_SUPPLIER = "financialLeasingSupplier";

  // MN
  public static final String COLLATERAL_CODE_MN = "Барьцааны код";
  public static final String FORM_NUMBER_MN = "Моторын багтаамж";
  public static final String MANUFACTURER_MN = "Үйлдвэрлэгч";
  public static final String MACHINE_MODEL_MN = "Тоног төхөөрөмжийн загвар";
  public static final String MACHINE_NUMBER_MN = "Тоног төхөөрөмжийг дугаар";
  public static final String MANUFACTURER_YEAR_MN = "Үйлдвэрлэсэн огноо";
  public static final String CIF_MN = "Хэрэглэгчийн СИФ";
  public static final String REMARKS_MN = "Тайлбар";
  public static final String REVIEW_DATE_MN = "Нөхөн барьцаалах огноо";
  public static final String INSPECTION_TYPE_MN = "Үнэлгээний төрөл";
  public static final String INSPECTION_AMOUNT_VALUE_MN = "Үнэлгээчний үнэлгээ";
  public static final String AMOUNT_OF_COLLATERAL_MN = "Барьцааны дүн";
  public static final String DEDUCTION_RATE_MN = "Хасагдуулах хувь";
  public static final String COLLATERAL_ASSESSMENT_MN = "Үнэлгээ";
  public static final String INSPECTOR_ID_MN = "Хяналт хийсэн ажилтны ID";
  public static final String INSPECTION_DATE_MN = "Үнэлгээ хийсэн огноо";
  public static final String OWNERSHIP_TYPE_MN = "Өмчлөлийн хэлбэр";
  public static final String OWNER_CIF_NUMBER_MN = "Өмчлөгчийн СИФ дугаар";
  public static final String OWNER_NAME_MN = "Өмчлөгчийн нэр";
  public static final String IMMOVABLE_NUMBER_MN = "Нэгж талбарын дугаар";

  public static final String YEAR_OF_CONSTRUCTION_MN = "Ашиглалтанд орсон он";
  public static final String CONDITION_OF_CONTRACT_MN = "Эрх олж авсан гэрээний үндсэн нөхцөл";
  public static final String PURPOSE_OF_USAGE_MN = "Хэрэглээний зорилго";
  public static final String PROPERTY_ID_MN = "Улсын бүртгэлийн дугаар";
  public static final String PROPERTY_DOC_NUMBER_MN = "Гэрчилгээний дугаар";
  public static final String BUILT_AREA_MN = "Талбайн хэмжээ";
  public static final String DUE_DATE_MN = "Гэрчилгээний дуусах он";
  public static final String LAND_AREA_MN = "Өрөөний тоо";
  public static final String CITY_MN = "Аймаг/Хот";
  public static final String ADDRESS_1_MN = "Сум/Дүүрэг";
  public static final String ADDRESS_2_MN = "Баг/Хороо";
  public static final String ADDRESS_3_MN = "Тоот";
  public static final String BUILDER_NAME_MN = "Хороолол/Хотхон";
  public static final String HOUSE_NUMBER_MN = "Байшин дугаар";
  public static final String STREET_NAME_MN = "Гудамж";
  public static final String STREET_NUMBER_MN = "Гудамж дугаар";

  public static final String VEHICLE_REGISTER_NUMBER_MN = "Тээврийн хэрэгслийн улсын дугаар";
  public static final String CHASSIS_NUMBER_MN = "Гэрчилгээний дугаар";
  public static final String ENGINE_NUMBER_MN = "Арлын дугаар";
  public static final String MANUFACTURE_YEAR_MN = " Үйлдвэрлэсэн он сар";
  public static final String VEHICLE_MODEL_MN = "Марк";
  public static final String FINANCIAL_LEASING_SUPPLIER_MN = "Санхүүгийн түрээс нийлүүлэгч 1";
  public static final String PURPOSE_MN = "Зориулалт";
  public static final String VEHICLE_ADDRESS_1_MN = "Хаяг1";
  public static final String VEHICLE_ADDRESS_2_MN = "Хаяг2";
  public static final String VEHICLE_ADDRESS_3_MN = "Хаяг3";

  public static final String OTHERS_FORM_NUMBER_MN = "Барьцаалбарын дугаар";
  public static final String CUSTOMER_CIF_MN = "Барьцаалагчийн дугаар";
  public static final String RECEIVED_DATE_MN = "Ашиглалтанд орсон огноо";
}
