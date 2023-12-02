/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.domain.ohs.xac;

/**
 * @author EBazarragchaa
 */
public class XacConstants
{
  private XacConstants()
  {
  }

  public static final String WSO2_ENDPOINT = "wso2.endpoint";

  public static final String PUBLISHER_ENDPOINT = "publisher.endpoint";
  public static final String PUBLISHER_FUNCTION_DOWNLOAD_CONTRACT = "publisher.function.download.contract";

  public static final String PUBLISHER_SOAP_ACTION_DOWNLOAD_CONTRACT = "publisher.soap.action.download.contract";
  public static final String PUBLISHER_CONTRACT_FILE_FORMAT = "publisher.contract.file.format";

  public static final String PUBLISHER_REPORT_ABSOLUTE_PATH = "publisher.report.absolute.path";
  public static final String PUBLISHER_REPORT_ABSOLUTE_PATH_EMPLOYEE_LOAN = "publisher.report.absolute.path.employee-loan";

  public static final String PUBLISHER_REPORT_ONLINE_SALARY_ABSOLUTE_PATH = "publisher.report.online.salary.absolute.path";

  public static final String PUBLISHER_REPORT_ABSOLUTE_PATH_LOAN_PAYMENT_SCHEDULE = "publisher.report.absolute.path.loan-payment-schedule";
  public static final String PUBLISHER_DATA_SIZE_FOR_CHUNK_DOWNLOAD = "publisher.data.size.chunk.download";

  public static final String PUBLISHER_PATH_LOAN_REPORT_CREATION = "publisher.path.loanReport";

  public static final String PUBLISHER_USER_ID = "publisher.userId";
  public static final String PUBLISHER_USER_PASSWORD = "publisher.userPassword";

  public static final String WSO2_HEADER_SOURCE = "wso2.header.source";
  public static final String WSO2_CAMUNDA_HEADER_SOURCE = "wso2.camunda.header.source";

  // WSO2 risky customer service header.
  public static final String WSO2_HEADER_CHECK_RISKY_CUSTOMER = "wso2.header.checkRiskyCustomer";

  // WSO2 core banking service header.
  public static final String WSO2_HEADER_GET_CUSTOMER_BY_ID = "wso2.header.getCustomerById";
  public static final String WSO2_HEADER_CHECK_CUSTOMER_BY_PERSON_ID = "wso2.header.getCustomerByPersonId";
  public static final String WSO2_HEADER_CALCULATE_DEPOSIT = "wso2.header.calculateDeposit";
  public static final String WSO2_HEADER_CALCULATE_LOAN_CYCLE = "wso2.header.calculateLoanCycle";
  public static final String WSO2_HEADER_GET_UD_FIELDS = "wso2.header.getUDFields";
  public static final String WSO2_HEADER_GET_UD_FIELDS_BY_FUNCTION = "wso2.header.getUDFieldsByFn";
  public static final String WSO2_HEADER_CREATE_COLLATERAL = "wso2.header.createCRCollateral";
  public static final String WSO2_HEADER_CREATE_CL_ACCOUNT = "wso2.header.createCLAccount";
  public static final String WSO2_HEADER_GET_ACCOUNT_LIST_FC = "wso2.header.getAccountListFC";

  // WSO2 Xyp system service headers.
  public static final String WSO2_HEADER_GET_CUSTOMER_ID_CARD_INFO = "wso2.header.getCitizenIDCardInfo";
  public static final String WSO2_HEADER_GET_CUSTOMER_ADDRESS_INFO = "wso2.header.getCitizenAddressInfo";
  public static final String WSO2_HEADER_GET_CUSTOMER_SALARY_INFO = "wso2.header.getCitizenSalaryInfo";
  public static final String WSO2_HEADER_GET_CUSTOMER_VEHICLE_INFO = "wso2.header.getVehicleInfo";
  public static final String WSO2_HEADER_GET_CUSTOMER_VEHICLE_OWNER_INFO = "wso2.header.getVehicleOwnerHistoryList";

  // WSO2 Mongol bank service headers.
  public static final String WSO2_HEADER_GET_CUSTOMER_CID = "wso2.header.getCustomerCID";
  public static final String WSO2_HEADER_GET_CUSTOMER_CID_BY_CO_BORROWER = "wso2.header.getXBTCustInfoSpl";
  public static final String WSO2_HEADER_GET_CUSTOMER_LOAN_ENQUIRE_INFO = "wso2.header.getCustomerLoanEnquire";
  public static final String WSO2_HEADER_CONFIRM_ENQUIRE_INFO = "wso2.header.getXBTInfoDetailed";
  public static final String WSO2_HEADER_GET_CUSTOMER_LOAN_INFO = "wso2.header.getXBTLoanInfo";
  public static final String WSO2_HEADER_GET_CUSTOMER_LOAN_INFO_PDF = "wso2.header.getXBTInfoPdf";
  public static final String WSO2_HEADER_GET_CUSTOMER_DETAIL = "wso2.header.getXBTCustDetails";
  public static final String WSO2_HEADER_GET_CUSTOMER_RELATED_INFO = "wso2.header.getXBTRelatedInfo";

  // WSO2 organization service headers
  public static final String WSO2_HEADER_GET_ORGANIZATION_INFO = "wso2.header.getOrganizationInfo";

  // WSO2 Collateral services
  public static final String WSO2_HEADER_GET_COLLATERAL_LIST = "wso2.header.GetCollateralList";

  //WSO2 Get Property Info Services
  public static final String WSO2_HEADER_GET_PROPERTY_INFO = "wso2.header.getPropertyInfo";

  /* NEW CORE BANKING SERVICE CONSTANTS */

  public static final String NEW_CORE_ENDPOINT = "new.core.endpoint";
  public static final String NEW_CORE_CHECK_RISKY_ENDPOINT = "new.core.check.risky.endpoint";

  public static final String NEW_CORE_HEADER_SOURCE = "new.core.header.source";
  public static final String NEW_CORE_ONLINE_LEASING_HEADER_SOURCE = "new.core.online.leasing.header.source";
  public static final String NEW_CORE_HEADER_GET_CUSTOMER_INFO = "new.core.header.getCustomerInfo";
  public static final String NEW_CORE_HEADER_CHECK_RISKY_CUSTOMER = "new.core.header.checkRiskyCustomer";
  public static final String NEW_CORE_HEADER_GET_CUSTOMER_BY_ID = "new.core.header.getNewCoreCustomerByPersonId";
  public static final String NEW_CORE_HEADER_REQUEST_TYPE = "new.core.header.requestType";
  public static final String NEW_CORE_HEADER_GET_COLLATERAL_CODE = "new.core.header.getCollateralCode";
  public static final String LANGUAGE_ID = "new.core.header.languageId";

  // WSO2 New Collateral Service
  public static final String NEW_CORE_WSO2_HEADER_CREATE_IMMOVABLE_COLL = "new.core.wso2.header.createImmovableCollateral";
  public static final String NEW_CORE_WSO2_HEADER_ADD_VEHICLE_COLLATERAL = "new.core.wso2.header.createVehicleCollateral";
  public static final String NEW_CORE_WSO2_HEADER_ADD_OTHER_COLLATERAL = "new.core.wso2.header.createOtherCollateral";
  public static final String NEW_CORE_WSO2_HEADER_CREATE_MACHINERY_COLL = "new.core.wso2.header.createMachineryCollateral";
  public static final String NEW_CORE_WSO2_HEADER_LINK_COLLATERALS = "new.core.wso2.header.linkCollaterals";

  public static final String NEW_CORE_WSO2_HEADER_GET_REFERENCE_CODES = "new.core.wso2.header.getReferenceCodes";

  public static final String NEW_CORE_WSO2_HEADER_GET_MACHINERY_COLLATERAL_INFO = "new.core.wso2.header.GetMachineryColtrlInfo";
  public static final String NEW_CORE_WSO2_HEADER_GET_IMMOVABLE_PROPERTY_COLLATERAL_INFO = "new.core.wso2.header.GetImmovableColtrlInfo";
  public static final String NEW_CORE_WSO2_HEADER_MODIFY_MACHINERY_COLLATERAL = "new.core.wso2.header.ModifyMachineryColtrl";
  public static final String NEW_CORE_HEADER_CREATE_ACCOUNT = "new.core.wso2.header.AddLoanAccount";
  public static final String NEW_CORE_WSO2_HEADER_MODIFY_IMMOVABLE_PROP_COLLATERAL = "new.core.wso2.header.ModifyImmovPropColtrl";
  public static final String NEW_CORE_WSO2_HEADER_GET_VEHICLE_COLLATERAL_INFO = "new.core.wso2.header.GetVehiclesColtrlInfo";
  public static final String NEW_CORE_WSO2_HEADER_MODIFY_VEHICLE_PROP_COLLATERAL = "new.core.wso2.header.ModifyVehiclesColtrl";

  public static final String NEW_CORE_WSO2_HEADER_GET_OTHER_COLLATERAL_INFO = "new.core.wso2.header.GetOthersColtrlInfo";
  public static final String NEW_CORE_WSO2_HEADER_MODIFY_OTHER_COLLATERAL_INFO = "new.core.wso.header.ModifyOthersColtrl";

  public static final String PROPERTY_KEY_COLLATERAL_CURRENCY_TYPE = "collateral.currency.type";
  public static final String PROPERTY_KEY_COLLATERAL_CEILING_LIMIT_AMOUNT = "collateral.ceiling.limit.amount";

  public static final String PROPERTY_KEY_COLLATERAL_VALUE_TYPE = "collateral.value.type";
  public static final String PROPERTY_KEY_COLLATERAL_IS_CONFIRMED = "collateral.frmDeriveVal";
  public static final String PROPERTY_KEY_COLLATERAL_APPORTION_METHOD = "collateral.apportion.method";

  // WSO2 New Collateral Service
  public static final String NEW_CORE_HEADER_GETC_COLLATERAL_LIST = "new.core.header.GetcCollateralList";
  public static final String NEW_CORE_HEADER_GET_ACCOUNT_LIST = "new.core.header.getAccountList";
  public static final String NEW_CORE_HEADER_GETC_UD_FIELDS = "new.core.header.getcUDFields";

  // Loan Account Property Keys
  public static final String PROPERTY_KEY_ACCOUNT_FREQUENCY_CALL = "new.core.account.frequency.call";
  public static final String PROPERTY_KEY_ACCOUNT_FREQUENCY_TYPE = "new.core.account.frequency.type";
  public static final String PROPERTY_KEY_ACCOUNT_FREQUENCY_HOLIDAY_STATUS = "new.core.account.frequency.holiday.status";

  public static final String PROPERTY_KEY_ACCOUNT_REPAYMENT_METHOD = "new.core.account.repayment.method";
  public static final String PROPERTY_KEY_ACCOUNT_DEFAULT_CURRENCY = "new.core.account.default.currency";

  public static final String PROPERTY_KEY_ACCOUNT_REPRICING_PLAN = "new.core.account.repricing.plan";
  public static final String PROPERTY_KEY_TEST_ACCOUNT_DATE = "test.config.account.install.start.date";

  public static final String PROPERTY_KEY_COLLATERAL_LINKAGE_TYPE = "new.core.collateral.linkage.type";
  public static final String PROPERTY_KEY_COLLATERAL_CURRENCY = "new.core.collateral.currency";

  public static final String PROPERTY_KEY_COLLATERAL_NATURE_IND = "new.core.collateral.nature.ind";
  public static final String PROPERTY_KEY_COLLATERAL_APPORTIONING_FLAG = "new.core.collateral.apportioning.flag";
  public static final String PROPERTY_KEY_COLLATERAL_SELF_FLAG = "new.core.collateral.self.flag";

  // Loan Account Info
  public static final String NEW_CORE_HEADER_GET_LOAN_ACCOUNT_INFO = "new.core.loan.account.info";

  //WSO2 Send Sms Services
  public static final String WSO2_HEADER_SEND_SMS = "wso2.header.sendSMS";

  //Loan contract
  public static final String PROPERTY_KEY_INQUIRE_COLLATERAL_INFO = "inquire.collateral.info";

  //Account info
  public static final String NEW_CORE_HEADER_GET_ACCOUNT_INFO = "new.core.account.info";
  public static final String WSO2_HEADER_SOURCE_ACCOUNT_INFO = "new.core.header.source.account.info";

  //Online Bank services
  public static final String WSO2_INTERNET_BANK_HEADER_REQUEST_TYPE = "wso2.internetBank.header.requestType";
  public static final String WSO2_INTERNET_BANK_HEADER_SOURCE = "wso2.internetBank.header.source";
  public static final String WSO2_HEADER_FUNCTION_GET_LOAN_INFO_LIST = "wso2.header.getcibBotLoan";

  public static final String WSO2_HEADER_FIND_ORGANIZATION_INFO = "wso2.header.findOrganizationInfo";
  public static final String WSO2_HEADER_GET_LOAN_INFO = "wso2.header.GetLoanOnlineCalc";
  public static final String WSO2_HEADER_LOAN_SAVE_PAYMENT = "wso2.header.LoanSavePayment";
  public static final String WSO2_HEADER_SET_PAY_OFF_PROCESS = "wso2.header.SetPayOffProc";

  //  NEW CORE BANKING SERVICE CONSTANTS

  //Loan information
  public static final String WSO_HEADER_GET_DBSR_LIST = "wso2.header.getDbsrListLists";

  public static final String WSO2_HEADER_FUNCTION_CREATE_DISBURSEMENT = "wso2.header.CreateLoanDisb";
  public static final String WSO2_HEADER_FUNCTION_CREATE_DISBURSEMENT_CHARGE = "wso2.header.CreateLoanDisbCharge";

  public static final String WSO2_HEADER_FUNCTION_ADD_TRANSACTION = "wso2.header.AddTransaction";

  //CHO
  public static final String CHO_BRANCH_NUMBER = "cho.branch.number";

  //Mongol bank new service
  public static final String WSO2_HEADER_GET_CIB_INFO = "wso2.header.getCIBInfo";

  //BNPL
  public static final String WSO2_HEADER_GET_REPAYMENT_SCHEDULE = "wso2.header.getRepaymentSchedule";
  public static final String WSO2_BNPL_HEADER_REQUEST_TYPE = "new.core.header.requestType";
  public static final String WSO2_SET_BNPL_STATE = "wso2.header.setBnplState";
  public static final String HEADER_SOURCE_EGW = "header.source.egw";
  public static final String WSO2_HEADER_ADD_ACC_LIEN = "wso2.header.addAccLien";
  public static final String WSO2_HEADER_MODIFY_ACC_LIEN = "wso2.header.modifyAccLien";
  public static final String HEADER_SOURCE_GET_BNPL_INVOICE_INFO = "header.source.getBnplInvoiceInfo";

  //BNPL
  public static final String WSO2_HEADER_GET_ACC_LIEN = "wso2.header.getAccLien";
  public static final String WSO2_ACC_LIEN_HEADER_REQUEST_TYPE = "wso2.internetBank.header.requestType";
  public static final String GET_ACC_LIEN_ERROR_CODE = "get.accLien.errorCode";

  public static final String HEADER_ADD_LOAN_REPAYMENT = "wso2.header.AddLoanRepayment";
  public static final String HEADER_ADD_LOAN_SCH_PAYMENT = "wso2.header.addLoanSchPayment";
}
