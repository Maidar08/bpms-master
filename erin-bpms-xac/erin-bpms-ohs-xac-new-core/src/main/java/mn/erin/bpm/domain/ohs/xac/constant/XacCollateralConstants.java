package mn.erin.bpm.domain.ohs.xac.constant;

/**
 * @author Tamir
 */
public final class XacCollateralConstants
{
  private XacCollateralConstants()
  {

  }

  public static final String DELIMITER_COLON = ": ";
  public static final String COLL_REFERENCE_TYPE_NAME = "typename";

  public static final String COLL_REFERENCE_VAL = "val";
  public static final String COLL_REFERENCE_TEXT = "txt";

  public static final String RESPONSE_COLLATERAL_ID = "ColtrlId";
  public static final String COLLATERAL_GEN_INFO = "ColtrlGenInfo";
  public static final String INSPECTION_INFO = "InspectionInfoRec";
  public static final String OWNERSHIP_INFO = "ownershipInfoRec";

  public static final String MARGIN_PCNT_REQUEST = "MarginPcnt";
  public static final String COLTRL_CODE_REQUEST = "ColtrlCode";
  public static final String COLTRL_CRNCY_REQUEST = "ColtrlCrncy";
  public static final String CEILING_LIMIT_AMT_VAL_REQUEST = "CeilingLimitAmtVal";

  public static final String COLLATERAL_CODE_VARIABLE = "collateralCode";
  public static final String DEDUCTION_RATE_VARIABLE = "deductionRate";
  public static final String COLL_APPART_METHOD_VALUE = "V";
  public static final String DEDUCTION_RATE = "deductionRate";

  // collateral inspection info constants
  public static final String INSPECTION_TYPE_REQUEST = "InspType";
  public static final String INSPECTION_EMPLOYEE_ID_REQUEST = "InspEmpId";
  public static final String INSPECTION_AMOUNT_VALUE_REQUEST = "InspAmtVal";
  public static final String VISIT_DATE_REQUEST = "VisitDt";

  public static final String INSPECTION_TYPE = "inspectionType";
  public static final String INSPECTOR_ID = "inspectorId";
  public static final String INSPECTION_AMOUNT_VALUE = "inspectionAmountValue";
  public static final String INSPECTION_DATE = "inspectionDate";

  // collateral ownership info constants
  public static final String OWNER_TYPE_REQUEST = "OwnerType";
  public static final String OWNER_CIF_REQUEST = "CifId";
  public static final String OWNER_NAME_REQUEST = "Name";
  public static final String SERIAL_NUMBER_SERVICE = "SerialNum";
  public static final String INSP_SERIAL_NUM_SERVICE = "InspSrlNum";


  public static final String OWNERSHIP_TYPE = "ownershipType";
  public static final String OWNER_CIF_NUMBER = "ownerCifNumber";
  public static final String OWNER_NAME = "ownerName";
  public static final String SERIAL_NUMBER = "serialNumber";
  public static final String INSPECTION_SERIAL_NUMBER = "inspSerialNumber";


  // vehicle collateral constant
  public static final String VEHICLES_INFO = "VehiclesInfo";
  public static final String VEHICLE_REGISTER_NUMBER = "vehicleRegisterNumber";
  public static final String CHASSIS_NUMBER = "chassisNumber";
  public static final String ENGINE_NUMBER = "engineNumber";
  public static final String MANUFACTURE_YEAR = "manufactureYear";
  public static final String VEHICLE_MODEL = "vehicleModel";
  public static final String FINANCIAL_LEASING_SUPPLIER = "financialLeasingSupplier";

  // machinery collateral constants
  public static final String MACHINERY_FORM_NUM = "FormNum";
  public static final String MACHINERY_MANUFACTURER = "Manufacturer";
  public static final String MACHINERY_MACHINE_NUMBER = "MachineNum";
  public static final String MACHINERY_MACHINE_MODEL = "MachineModel";
  public static final String XAC_REVIEW_DATE = "ReviewDt";
  public static final String MACHINERY_MANUFACTURER_YEAR = "ManufactureYear";
  public static final String XAC_CUSTOMER_ID = "CustId";
  public static final String MACHINERY_REMARKS = "Rmks";
  public static final String MACHINERY_CUSTOMER_ID = "CustId";
  public static final String XAC_RMKS = "Rmks";
  public static final String MACHINERY_APPORTION_METHOD = "ApportionMethod";
  public static final String XAC_COL_VALUE_TYPE = "ColtrlValueType";
  public static final String MACHINERY_DERIVE_VALUE = "DeriveValueAmt";
  public static final String XAC_FORM_DERIVE_VAL = "FrmDeriveVal";
  public static final String XAC_GENERAL_COL_VAL = "GenColtrlValueRec";

  public static final String FORM_NUMBER = "formNumber";
  public static final String MANUFACTURER = "manufacturer";
  public static final String MACHINE_NUMBER = "machineNum";
  public static final String MACHINE_MODEL = "machineModel";
  public static final String MANUFACTURER_YEAR = "manufactureYear";
  public static final String CUSTOMER_ID = "custId";
  public static final String REVIEW_DATE = "reviewDate";
  public static final String REMARKS = "remarks";
  public static final String XAC_REMARKS = "Remarks";
  public static final String COLLATERAL_ASSESSMENT = "collateralAssessment";

  // immovable
  public static final String HOUSE_NUMBER = "HouseNum";
  public static final String PURPOSE_OF_USAGE = "PurposeOfUsage";
  public static final String XAC_BUILT_AREA = "BuiltArea";
  public static final String XAC_LAND_AREA = "LandArea" ;
  // other collateral constant
  public static final String OTHERS_INFO = "OthersInfo";
  public static final String OTHERS_FORM_NUMBER = "FormNumber";
  public static final String OTHERS_RECEIVED_DATE = "ReceivedDt";
  public static final String APPORTION_METHOD = "ApprtngMethod";
  public static final String OTHER_COLLATERAL_ASSESSMENT_VALUE = "CollValuebefLienAmtVal";
  public static final String OTHER_COL_VALUE_BEFORE_LIEN = "CollateralValuebeforeLien";
  public static final String OTHER_AMOUNT_VALUE = "amountValue";

  public static final String CUSTOMER_CIF = "cifNumber";
  public static final String RECEIVED_DATE = "receivedDate";

  public static final String COLTRL_ID = "ColtrlId";
  public static final String SRL_NUM_COLL_LINKAGE = "SrlNum";

  public static final String COLTRL_TYPE_COLL_LINKAGE = "ColtrlType";
  public static final String COLTRL_CODE_COLL_LINKAGE = "ColtrlCode";

  public static final String COLTRL_AMT_VAL_COLL_LINKAGE = "ColtrlAmtVal";
  public static final String COLTRL_AMT_CCY_COLL_LINKAGE = "ColtrlAmtCCY";

  public static final String CRNCY_COLL_LINKAGE = "Crncy";
  public static final String APPORT_AMT_VAL_COLL_LINKAGE = "ApportAmtVal";

  public static final String APPORT_AMT_CCY_COLL_LINKAGE = "ApportAmtCCY";
  public static final String MARGIN_PCNT_COLL_LINKAGE = "MarginPcnt";

  public static final String COLTRL_NATURE_IND_COLL_LINKAGE = "ColtrlNatureInd";
  public static final String APPARTNG_METHOD_COLL_LINKAGE = "AppartngMethod";

  public static final String SEL_FLG_COLL_LINKAGE = "SelFlg";
  public static final String LINKAGE_COLTRL_INFO_REC = "linkageColtrlInfoRec";

}

