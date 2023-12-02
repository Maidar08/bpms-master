package mn.erin.domain.bpm;

/**
 * @author Tamir
 */
public final class BpmTemplateFieldConstants {
  private BpmTemplateFieldConstants() {
  }

  public static final String KHUR_VEHICLE_INFO_TEMPLATE_NAME = "401_Teevriin_Heregsliin_Lavlagaa.docx";
  public static final String KHUR_PROPERTY_INFO_TEMPLATE_PATH = "Templates/201_Ul_Hodloh_Horongiin_Lavlagaa.docx";

  public static final String YEAR_FIELD = "year";
  public static final String MONTH_FIELD = "month";
  public static final String DAY_FIELD = "day";

  public static final String INTEREST_RATE_FIELD = "interest_rate";
  public static final String CONFIRMED_LOAN_AMOUNT_FIELD = "confirmed_loan_amount";
  public static final String ADDITIONAL_SPECIAL_CONDITION_FIELD = "additional_special_condition";
  public static final String SYSTEM_DATE_FIELD = "system_date";
  public static final String REGISTER_NUMBER_FIELD = "register_number";
  public static final String PROCESS_REQUEST_ID_FIELD = "process_request_id";
  public static final String FULL_NAME_FIELD = "full_name";
  public static final String PRODUCT_NAME_FIELD = "product_name";
  public static final String LOAN_PURPOSE_FIELD = "loan_purpose";
  public static final String CURRENCY_FIELD = "currency";
  public static final String CALCULATED_LOAN_AMOUNT_FIELD = "calculated_loan_amount";
  public static final String CURRENT_USER_NAME_FIELD = "current_user_name";
  public static final String CONF_USER_NAME_FIELD = "conf_user_name";
  public static final String CONF_USER_ROLE_FIELD = "conf_user_role";
  public static final String USER_ROLE = "user_role";
  public static final String USER_ROLE_FIELD = "user_role";
  public static final String SCORE_FIELD = "score";
  public static final String REPAYMENT_TYPE_FIELD = "repayment_type";
  public static final String FULL_NAME_CO_BORROWER_FIELD = "full_name_co_borrower";
  public static final String REGISTER_NUMBER_CO_BORROWER_FIELD = "register_number_co_borrower";
  public static final String CONFIRMED_USER_ROLE_FIELD = "conf_user_role";
  public static final String BRANCH_ID_FIELD = "branch_id";
  public static final String BRANCH_NAME_FIELD = "branch_name";

  public static final String COLL_PRODUCT_DESCRIPTION_FIELD = "coll_prod_desc";
  public static final String COUNTRY_REGISTER_NUMBER_FIELD = "country_reg_number";

  public static final String COLL_AMOUNT_FIELD = "coll_amount";
  public static final String COLL_ASSESSMENT_FIELD = "coll_assessment";

  public static final String COLL_TOTAL_AMOUNT_FIELD = "total_amount";
  public static final String COLL_TOTAL_ASSESSMENT_FIELD = "total_assessment";
  public static final String OWNER_NAME_FIELD = "owner_name";

  // added constants for task
  public static final String OWNERSHIP_TYPE_FIELD = "ownership_type";
  public static final String LINKED_ACCOUNT_NUMBER_FIELD = "linked_acc_num";
  public static final String COLL_TYPE_FIELD = "coll_type";
  public static final String DOC_NUMBER_FIELD = "doc_number";
  public static final String CITY_FIELD = "city";
  public static final String ADDRESS1_FIELD = "address1";
  public static final String ADDRESS2_FIELD = "address2";
  public static final String ADDRESS3_FIELD = "address3";
  public static final String BUILDER_NAME_FIELD = "builder_name";
  public static final String HOUSE_NUMBER_FIELD = "house_number";
  public static final String STREET_NAME_FIELD = "street_name";
  public static final String STREET_NUMBER_FIELD = "street_number";
  public static final String INSPECTION_DATE_FIELD = "inspection_date";

  public static final String UNDER_LINE = "_";

  // Vehicle Reference constants.

  public static final String VEHICLE_REF_REQUEST_ID_FIELD = "requestId";
  public static final String VEHICLE_REF_CURRENT_USER_FIELD = "user";
  public static final String VEHICLE_REF_SYSTEM_DATE_FIELD = "systemDate";

  public static final String TABLE_NAME_VEHICLE_INFO = "vehicleInfoTable";
  public static final String TABLE_NAME_OWNER_INFO = "ownerInfoTable";
  public static final String TABLE_NAME_PROPERTY_INFO = "propertyInfoTable";

  public static final String OWNER_LAST_NAME_FIELD = "ownerLastname";
  public static final String OWNER_FIRST_NAME_FIELD = "ownerFirstname";
  public static final String OWNER_REGISTER_NUMBER_FIELD = "ownerRegnum";

  public static final String PLATE_NUMBER_FIELD = "pn";
  public static final String CABIN_NUMBER_FIELD = "can";
  public static final String CERTIFICATE_NUMBER_FIELD = "cen";

  public static final String MARK_NAME_FIELD = "mk";
  public static final String MODEL_NAME_FIELD = "mo";

  public static final String BUILD_YEAR_FIELD = "by";
  public static final String IMPORT_DATE_FIELD = "im";

  public static final String COLOR_NAME_FIELD = "co";
  public static final String TRANSMISSION_FIELD = "tr";

  public static final String OWNER_INFO_FROM_DATE_FIELD = "sd";
  public static final String OWNER_INFO_TO_DATE_FIELD = "ed";

  public static final String OWNER_INFO_LAST_NAME_FIELD = "ln";
  public static final String OWNER_INFO_FIRST_NAME_FIELD = "fn";
  public static final String OWNER_INFO_REGISTER_NUMBER_FIELD = "rn";

  public static final String OWNER_INFO_PHONE_FIELD = "ph";
  public static final String OWNER_INFO_FULL_ADDRESS_FIELD = "add";

  public static final String BRANCH_CODE_VEHICLE_REFERENCE_FIELD = "branchcode";
  public static final String BRANCH_NAME_VEHICLE_REFERENCE_FIELD = "branchname";

  // Customer Property Reference constants

  public static final String PROPERTY_NUMBER_FIELD = "propertyNumber";
  public static final String PROPERTY_INTENT_FIELD = "intent";

  public static final String PROPERTY_SQUARE_FIELD = "square";
  public static final String PROPERTY_ADDRESS_FIELD = "address";

  public static final String PROPERTY_SERVICE_NAME_FIELD = "serviceName";
  public static final String PROPERTY_SERVICE_DATE_FIELD = "serviceDate";
  public static final String PROPERTY_OWNER_FIELD = "owner";

  // Mongol bank
  // customer related info
  public static final String FIRST_NAME_FIELD = "first_name";
  public static final String LAST_NAME_FIELD = "last_name";
  public static final String LEGAL_STATE_FIELD = "legal_state";
  public static final String CONNECTING_FORM_FIELD = "connecting_form";
  public static final String FIELD6 = "field6";

  // Direct online salary loan
  public static final String SEX_FIELD = "sex";
  public static final String ADDRESS_FIELD = "address";
  public static final String REQUESTED_LOAN_AMOUNT_FIELD = "request_loan_amount";
  public static final String DAY_OF_PAYMENT_FIELD = "day_of_payment";
  public static final String LOAN_TERM_FIELD = "loan_term";
  public static final String LOAN_GRANT_DATE_FIELD = "loan_grant_date";
  public static final String COBORROWER_FIELD = "coborrower";
  public static final String LOAN_OFFICER_FIELD = "loan_officer";
  public static final String INCOME_TYPE_FIELD = "income_type";
}
