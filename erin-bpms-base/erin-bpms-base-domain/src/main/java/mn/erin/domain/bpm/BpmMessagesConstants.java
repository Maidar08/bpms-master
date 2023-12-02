package mn.erin.domain.bpm;

/**
 * @author Tamir
 */
public final class BpmMessagesConstants
{
  private BpmMessagesConstants()
  {
  }

  public static final String CAMUNDA_TASK_FORM_NOT_EXIST_CODE = "CamundaTasKFormService002";
  public static final String ONLINE_SALARY_LOG_HASH = "###### ONLINE SALARY: ";
  public static final String BNPL_LOG = "######### BNPL: ";
  public static final String INSTANT_LOAN_LOG = "######### INSTANT LOAN: ";
  public static final String ONLINE_LEASING_LOG = "######### ONLINE LEASING: ";

  public static final String INPUT_NULL_CODE = "BPMS001";
  public static final String INPUT_NULL_MESSAGE = "Input is null!";

  public static final String PARAMETER_NULL_CODE = "BPMS002";
  public static final String PARAMETER_NULL_MESSAGE = "Parameters must not be null.";

  public static final String EXECUTION_ID_NULL_CODE = "BPMS004";
  public static final String EXECUTION_ID_NULL_MESSAGE = "Execution id input is null.";

  public static final String INVALID_INPUT_CODE = "BPMS005";
  public static final String INVALID_INPUT_MESSAGE = "Invalid input.";

  public static final String PARAMETER_ENTITY_TYPE_NULL_CODE = "BPMS006";
  public static final String PARAMETER_ENTITY_TYPE_NULL_MESSAGE = "Parameter Entity Type must not be null.";

  public static final String PARAMETER_NAME_NULL_CODE = "BPMS007";
  public static final String PARAMETER_NAME_NULL_MESSAGE = "Name for parameter must not be null or blank!";

  public static final String PARAMETER_VALUE_NULL_CODE = "BPMS008";
  public static final String PARAMETER_VALUE_NULL_MESSAGE = "Value for parameter must not be blank!";

  public static final String PROCESS_REQUEST_ID_NULL_CODE = "BPMS009";
  public static final String PROCESS_REQUEST_ID_NULL_MESSAGE = "Process request id is missing in input (cannot blank)";

  public static final String PROCESS_REQUEST_NOT_EXISTS_CODE = "BPMS010";
  public static final String PROCESS_REQUEST_NOT_EXISTS_MESSAGE = "ProcessRequest doesn't exist!";

  public static final String PROCESS_INSTANCE_ID_NULL_CODE = "BPMS011";
  public static final String PROCESS_INSTANCE_ID_NULL_MESSAGE = "Process Instance Id is missing!";

  public static final String PROCESS_NOT_FOUND_CODE = "BPMS013";
  public static final String PROCESS_NOT_FOUND_MESSAGE = "Process Not Found!!";

  public static final String PARAMETERS_EMPTY_CODE = "BPMS014";
  public static final String PARAMETERS_EMPTY_MESSAGE = "Parameters are empty!";

  public static final String CIF_NULL_CODE = "BPMS015";
  public static final String CIF_NULL_MESSAGE = "CIF number is empty!";

  public static final String CASE_INSTANCE_ID_NULL_CODE = "BPMS016";
  public static final String CASE_INSTANCE_ID_NULL_MESSAGE = "Case instance id is required, cannot be blank!";

  public static final String TASK_ID_NULL_CODE = "BPMS017";
  public static final String TASK_ID_NULL_MESSAGE = "Task id is required, cannot be blank!";

  public static final String TASK_ID_NULL_ERROR_CODE = "BPMS018";
  public static final String TASK_FORM_NOT_EXIST_ERROR_CODE = "BPMS019";

  public static final String INVALID_BASIC_AUTHORIZATION_CODE = "BPMS039";
  public static final String INVALID_BASIC_AUTHORIZATION_MESSAGE = "Invalid basic authorization!";

  public static final String GROUP_ID_NULL_CODE = "BPMS043";
  public static final String GROUP_ID_NULL_MESSAGE = "Group id is missing!";

  public static final String REQUEST_BODY_NULL_CODE = "BPMS047";
  public static final String REQUEST_BODY_NULL_MESSAGE = "Request body is null!";

  public static final String REQUEST_HEADER_PARAM_NULL_CODE = "BPMS048";
  public static final String REQUEST_HEADER_PARAM_NULL_MESSAGE = "Request header parameter is null!";

  public static final String FORM_ID_NULL_CODE = "BPMS051";
  public static final String FORM_ID_NULL_MESSAGE = "Form id is required, cannot be null or empty!";

  public static final String MUST_CALCULATE_CODE = "BPMS054";
  public static final String MUST_CALCULATE_MESSAGE = "Must calculate loan amount!";

  public static final String MUST_AUTHORIZE_CODE = "BPMS052";
  public static final String MUST_AUTHORIZE_MESSAGE = "Must make decision to accept or decline loan amount!";

  public static final String CONSUMPTION_CUSTOMER_MAKE_LOAN_DECISION_CODE = "BPMS055";
  public static final String CONSUMPTION_CUSTOMER_MAKE_LOAN_DECISION_MESSAGE = "Decision must be made before finishing task!";

  public static final String TASK_FIELDS_CHANGED_CODE = "BPMS056";
  public static final String TASK_FIELDS_CHANGED_MESSAGE = "Task fields has changed. Please calculate again!";

  public static final String NULL_RESPONSE_DURING_CONFIRM_REQUEST_CODE = "BPMS058";
  public static final String NULL_RESPONSE_DURING_CONFIRM_REQUEST_MESSAGE = "Null response from MONGOL BANK during confirm request!";

  public static final String NO_SESSION_FROM_MONGOL_BANK_CODE = "BPMS059";
  public static final String NO_SESSION_FROM_MONGOL_BANK_MESSAGE = "MONGOL BANK : Service no session!";

  public static final String REQUIRED_PARAMETERS_NULL_CONFIRM_MONGOL_BANK_CODE = "BPMS060";
  public static final String REQUIRED_PARAMETERS_NULL_CONFIRM_MONGOL_BANK_MESSAGE = "Required parameters are null during confirm MONGOL BANK service!";

  public static final String CUSTOMER_CID_NULL_CODE = "BPMS061";
  public static final String CUSTOMER_CID_NULL_MESSAGE = "Customer CID cannot be null!";

  public static final String RESULT_JSON_ARRAY_IS_EMPTY_FROM_MONGOL_BANK_CODE = "BPMS062";
  public static final String RESULT_JSON_ARRAY_IS_EMPTY_FROM_MONGOL_BANK_MESSAGE = "MONGOL BANK : Service result json array is empty!";

  public static final String BORROWER_ID_IS_NULL_FROM_MONGOL_BANK_CODE = "BPMS063";
  public static final String BORROWER_ID_IS_NULL_FROM_MONGOL_BANK_MESSAGE = "MONGOL BANK : Borrower ID is null!";

  public static final String PDF_FILE_AS_BASE_64_NULL_FROM_MONGOL_BANK_CODE = "BPMS064";
  public static final String PDF_FILE_AS_BASE_64_NULL_FROM_MONGOL_BANK_MESSAGE = "MONGOL BANK: PDF file as base 64 is null!";

  public static final String CUSTOMER_RELATION_TYPE_NULL_CODE = "BPMS071";
  public static final String CUSTOMER_RELATION_TYPE_NULL_MESSAGE = "Customer relation type cannot be null!";

  public static final String REST_COMPLETED_FORM_NULL_CODE = "BPMS084";
  public static final String REST_COMPLETED_FORM_NULL_MESSAGE = "Completed form cannot be null!";

  public static final String REST_FORM_FIELDS_EMPTY_CODE = "BPMS085";
  public static final String REST_FORM_FIELDS_EMPTY_MESSAGE = "Rest form fields list cannot be empty!";

  public static final String JSON_TO_OBJECT_ERROR_CODE = "BPMS087";
  public static final String JSON_TO_OBJECT_ERROR_MESSAGE = "Error occurred when JSON to POJO object convert!";

  public static final String CALCULATION_TOO_BIG_ACCEPTED_AMOUNT_ERROR_CODE = "BPMS099";
  public static final String CALCULATION_TOO_BIG_ACCEPTED_AMOUNT_ERROR_MESSAGE = "Accepted Loan Amount must not be bigger than Granted Loan Amount!";

  public static final String CALCULATION_TOO_BIG_ACCEPTED_AMOUNT_REQUESTED_ERROR_CODE = "BPMS100";
  public static final String CALCULATION_TOO_BIG_ACCEPTED_AMOUNT_REQUESTED_ERROR_MESSAGE = "Accepted Loan Amount must not be bigger than Requested Loan Amount!";

  public static final String PROCESS_REQUEST_BLANK_GROUP_ID_ERROR_CODE = "BPMS088";
  public static final String PROCESS_REQUEST_BLANK_GROUP_ID_ERROR_MESSAGE = "Process Request Does Contain Group ID!";

  public static final String AIM_USER_BLANK_MEMBERSHIP_ERROR_CODE = "BPMS089";
  public static final String AIM_USER_BLANK_MEMBERSHIP_ERROR_MESSAGE = "User does not have membership!";

  public static final String AIM_USER_BLANK_ROLE_MEMBERSHIP_ERROR_CODE = "BPMS090";
  public static final String AIM_USER_BLANK_ROLE_MEMBERSHIP_ERROR_MESSAGE = "User does not have role in membership!";

  public static final String COMPANY_NAME_NULL_ERROR_CODE = "BPMS091";
  public static final String COMPANY_NAME_NULL_ERROR_MESSAGE = "Company name is empty!";

  public static final String COLLATERAL_DATE_ERROR_CODE = "BPMS092";
  public static final String COLLATERAL_DATE_ERROR_MESSAGE = "Collateral date format is invalid!, Parsing exception occurred with = [%s]";

  // micro balance calculation BPMS093 - BPMS094
  public static final String REPORT_COVERAGE_PERIOD_NULL_MESSAGE = "Report Coverage Period is null";
  public static final String REPORT_COVERAGE_PERIOD_NULL_CODE = "BPMS093";

  public static final String BALANCE_TOTAL_SALE_AMOUNT_ZERO_MESSAGE = "Balance total sale amount cannot be 0";
  public static final String BALANCE_TOTAL_SALE_AMOUNT_ZERO_CODE = "BPMS094";

  public static final String BALANCE_JSON_EXTRACT_EXCEPTION_MESSAGE = "Exception occurred during to extract balance calculation json data";
  public static final String BALANCE_JSON_EXTRACT_EXCEPTION_CODE = "BPMS095";

  // date parse exception
  public static final String STRING_TO_DATE_EXCEPTION_MESSAGE = "Exception occurred during convert string to date";
  public static final String STRING_TO_DATE_EXCEPTION_CODE = "BPMS096";

  public static final String TASK_DEF_KEY_NULL_ERROR_CODE = "BPMS098";
  public static final String TASK_DEF_KEY_NULL_ERROR_MESSAGE = "Task definition key is null!";

  public static final String SET_VARIABLE_INPUT_VALUE_CODE = "BPMS108";
  public static final String SET_VARIABLE_INPUT_VALUE_MESSAGE = "Set case variable input is required!";

  public static final String FORM_RELATION_ERROR_CODE = "BPMS109";

  public static final String PRODUCT_DOESNT_EXIST_ERROR_CODE = "BPMS111";
  public static final String PRODUCT_DOESNT_EXIST_ERROR_MESSAGE = "Product does not exist!";

  public static final String PRODUCT_BLANK_PRODUCT_ID_ERROR_CODE = "BPMS112";
  public static final String PRODUCT_BLANK_PRODUCT_ID_ERROR_MESSAGE = "Product Id is required!";

  public static final String PRODUCT_BLANK_APPLICATION_CATEGORY_ERROR_CODE = "BPMS113";
  public static final String PRODUCT_BLANK_APPLICATION_CATEGORY_ERROR_MESSAGE = "Product application category is required!!";

  public static final String PRODUCT_BLANK_CATEGORY_DESCRIPTION_ERROR_CODE = "BPMS114";
  public static final String PRODUCT_BLANK_CATEGORY_DESCRIPTION_ERROR_MESSAGE = "Product category description is required!";

  public static final String PRODUCT_BLANK_PRODUCT_DESCRIPTION_ERROR_CODE = "BPMS115";
  public static final String PRODUCT_BLANK_PRODUCT_DESCRIPTION_ERROR_MESSAGE = "Product description is required!";

  public static final String PRODUCT_BLANK_PRODUCT_TYPE_ERROR_CODE = "BPMS116";
  public static final String PRODUCT_BLANK_PRODUCT_TYPE_ERROR_MESSAGE = "Product type is required!";

  public static final String PRODUCT_BLANK_PRODUCT_SUB_TYPE_ERROR_CODE = "BPMS125";
  public static final String PRODUCT_BLANK_PRODUCT_SUB_TYPE_ERROR_MESSAGE = "Product sub type is required!";

  public static final String PRODUCT_BLANK_LOAN_TO_VALUE_RATIO_ERROR_CODE = "BPMS117";
  public static final String PRODUCT_BLANK_LOAN_TO_VALUE_RATIO_ERROR_MESSAGE = "Product loan to value ratio is required!";

  public static final String PRODUCT_REPOSITORY_SQL_ERROR_CODE = "BPMS118";
  public static final String PRODUCT_REPOSITORY_SQL_ERROR_MESSAGE = "SQL Exception occured during operation in product repository!";

  public static final String PRODUCT_USECASE_CREATE_UNSUCCESSFUL_ERROR_CODE = "BPMS119";
  public static final String PRODUCT_USECASE_CREATE_UNSUCCESSFUL_MESSAGE = "Unsuccessful product creation!";

  public static final String UD_FIELD_BY_FN_USECASE_EMPTY_FUNCTION_ERROR_CODE = "BPMS130";
  public static final String UD_FIELD_BY_FN_USECASE_EMPTY_FUNCTION_ERROR_MESSAGE = "Function body parameter must not be empty!!";

  public static final String NOT_FOUND_FORM_RELATION_MESSAGE = "Form relation information not found with task definition key = [%s]";
  public static final String COLLATERAL_PRODUCT_REPOSITORY_SQL_ERROR_CODE = "BPMS120";
  public static final String COLLATERAL_PRODUCT_REPOSITORY_SQL_ERROR_MESSAGE = "Unsuccessful product creation!";

  public static final String COLLATERAL_PRODUCT_BLANK_MORE_INFORMATION_ERROR_CODE = "BPMS121";
  public static final String COLLATERAL_PRODUCT_BLANK_MORE_INFORMATION_ERROR_MESSAGE = "Collateal Product more information field is required!";

  public static final String COLLATERAL_PRODUCT_BLANK_SUB_TYPE_ERROR_CODE = "BPMS122";
  public static final String COLLATERAL_PRODUCT_BLANK_SUB_TYPE_ERROR_MESSAGE = "Collateral Product sub type is required!";

  public static final String COMPLETED_FORM_NOT_FOUND_MESSAGE = "Completed form not found with given task id = [%s] and case instance id = [%s]!";

  // Mortgage Loan Amount Calculation
  public static final String INVALID_LOAN_PRODUCT_CODE = "BPMS150";
  public static final String INVALID_LOAN_PRODUCT_MESSAGE = "Invalid loan product";
  public static final String INVALID_MAX_LOAN_AMOUNT_MORTGAGE_CODE = "BPMS151";
  public static final String INVALID_MAX_LOAN_AMOUNT_MORTGAGE_MESSAGE = "Accepted loan amount can not be greater than max loan amount!";
  public static final String INVALID_MAX_LOAN_TERM_MORTGAGE_CODE = "BPMS152";
  public static final String INVALID_MAX_LOAN_TERM_MORTGAGE_MESSAGE = "Loan term can not be greater than max loan term!";

  // SQL Exception
  public static final String CLOB_CONVERSION_TO_STRING_FAIL_CODE = "BPMS123";
  public static final String CLOB_CONVERSION_TO_STRING_FAIL_MESSAGE = "Exception occured during convert clob into string";

  // Collateral error message
  public static final String WRONG_COLLATERAL_LOAN_AMOUNT_VALUE_CODE = "BPMS124";
  public static final String WRONG_COLLATERAL_LOAN_AMOUNT_VALUE_MESSAGE = "Collateral loan amount is too much!";

  public static final String LOAN_AMOUNT_IS_ZERO_EXCEPTION_CODE = "BPMS129";
  public static final String LOAN_AMOUNT_IS_ZERO_EXCEPTION_MESSAGE = "Loan amount value cannot be zero or less than zero!";

  public static final String FIXED_ACCEPTED_LOAN_AMOUNT_NULL_MESSAGE = "Fixed Accepted Loan Amount is null!";
  public static final String FIXED_ACCEPTED_LOAN_AMOUNT_NULL_CODE = "BPMS110";

  public static final String COULD_NOT_SET_VARIABLE_VALUE_CODE = "BPMS126";
  public static final String COULD_NOT_SET_VARIABLE_VALUE_MESSAGE = "Could not set case variable, reason for that : [%s]";

  public static final String COULD_NOT_SUBMIT_CODE = "BPM129";

  //Property Info Error messages
  public static final String PROPERTY_INFO_PROPERTY_ID_BLANK_CODE = "BPMS200";
  public static final String PROPERTY_INFO_PROPERTY_ID_BLANK_MESSAGE = "Property Id is required!";

  public static final String PROPERTY_INFO_CERT_FIELD_NULL_CODE = "BPMS201";
  public static final String PROPERTY_INFO_CERT_FIELD_NULL_MESSAGE = "Cert field is required!";

  public static final String PROPERTY_INFO_CERT_FINGERPRINT_FIELD_NULL_CODE = "BPMS202";
  public static final String PROPERTY_INFO_CERT_FINGERPRINT_FIELD_NULL_MESSAGE = "Cert fingerprint field is required!";

  public static final String PROPERTY_INFO_FINGERPRINT_BLANK_CODE = "BPMS203";
  public static final String PROPERTY_INFO_FINGERPRINT_BLANK_MESSAGE = "Fingerprint is required!";

  public static final String REGISTER_NUMBER_BLANK_CODE = "BPMS204";
  public static final String REGISTER_NUMBER_BLANK_MESSAGE = "Register number is required!";

  public static final String VEHICLE_PLATE_NUMBER_CODE = "BPMS205";
  public static final String VEHICLE_PLATE_NUMBER_BLANK_MESSAGE = "Vehicle plate number is blank! with customer REGISTER NUMBER = [%s]!";

  public static final String DOWNLOAD_VEHICLE_REFENCE_INFO_CODE = "BPMS206";

  public static final String VEHICLE_CABIN_NUMBER_CODE = "BPMS207";
  public static final String VEHICLE_CABIN_NUMBER_BLANK_MESSAGE = "Vehicle cabin number is required! with customer REGISTER NUMBER = [%s]!";

  public static final String VEHICLE_INFO_NOT_FOUND_CODE = "BPMS208";
  public static final String VEHICLE_INFO_NOT_FOUND_CODE_MESSAGE = "Vehicle information not found! with REGISTER NUMBER = [%s], PLATE NUMBER = [%s]!";

  public static final String COLLATERAL_PRODUCT_NOT_FOUND_CODE = "BPMS127";
  public static final String COLLATERAL_PRODUCT_NOT_FOUND_MESSAGE = "Collateral Product not found with COLLATERAL ID = [%s], type = [%s], sub type = [%s], description = [%s].";

  public static final String COLLATERAL_PRODUCT_NOT_FOUND_WO_COLLID_CODE = "BPMS131";
  public static final String COLLATERAL_PRODUCT_NOT_FOUND_WO_COLLID_MESSAGE = "Collateral Product not found with type = [%s], sub type = [%s], description = [%s].";

  public static final String PROPERTY_INFO_NOT_FOUND_CODE = "BPMS209";
  public static final String PROPERTY_INFO_NOT_FOUND_CODE_MESSAGE = "Property information not found! with REGISTER NUMBER = [%s]!";

  public static final String PROPERTY_NUMBER_REQUIRED_CODE = "BPMS210";
  public static final String PROPERTY_NUMBER_REQUIRED_MESSAGE = "Property ID is required! with customer REGISTER NUMBER = [%s]!";

  public static final String MICRO_GRANT_AMOUNT_NULL_CODE = "BPMS128";
  public static final String MICRO_GRANT_AMOUNT_NULL_MESSAGE = "Micro loan grant amount is empty! during generate loan decision document.";

  public static final String SCORING_PROCESS_TASK_NOT_COMPLETED_CODE = "BPMS211";
  public static final String SCORING_PROCESS_TASK_NOT_COMPLETED_MESSAGE = "Scoring task must be completed!";

  public static final String CUSTOMER_NAME_IS_BLANK_CODE = "BPMS212";
  public static final String CUSTOMER_NAME_IS_BLANK_MESSAGE = "Customer name does not exist for customer = [%s]!";

  public static final String COLLATERAL_INFO_NOT_FOUND_CODE = "BPMS213";
  public static final String COLLATERAL_INFO_NOT_FOUND_MESSAGE = "Collateral information not found with collateral ID = [%s]!";

  public static final String MANUAL_ACTIVATE_ERROR_CODE = "BPMS214";

  public static final String INVALID_DATE_FORMAT_CODE = "BPM127";
  public static final String INVALID_DATE_FORMAT_MESSAGE = "Invalid created date format";

  public static final String GRANT_LOAN_AMOUNT_ZERO_ERR_CODE = "BPMS215";
  public static final String GRANT_LOAN_AMOUNT_ZERO_ERR_MESSAGE = "Олгож болох зээлийн хэмжээ 0 утгатай байна, хэмжээ тооцох таскыг тооцоолоод дуусгана уу!";

  public static final String ACCEPTED_LOAN_AMOUNT_ZERO_ERR_CODE = "BPMS216";
  public static final String ACCEPTED_LOAN_AMOUNT_ZERO_ERR_MESSAGE = "Батлах зээлийн хэмжээ 0 утгатай байна, хэмжээ тооцох таскыг тооцоолоод дуусгана уу!";


  // Camunda error message
  public static final String CAMUNDA_TASK_FORM_DOES_NOT_EXIST_CODE = "CamundaTasKFormService002";
  public static final String CAMUNDA_TASK_FORM_DOES_NOT_EXIST_MESSAGE = "Task forms does not exist ";

  // Process type message
  public static final String PROCESS_TYPE_CATEGORY_IS_NULL_CODE = "BPMS217";
  public static final String PROCESS_TYPE_CATEGORY_IS_NULL_MESSAGE = "Process type category is null";
  public static final String PROCESS_TYPE_ID_NULL_CODE = "BPMS222";
  public static final String PROCESS_TYPE_ID_NULL_MESSAGE = "Process type is missing";

  public static final String ORGANIZATION_SALARY_ALREADY_EXIST_MESSAGE = "Salary organization already exists with register number %s";
  public static final String ORGANIZATION_LEASING_ALREADY_EXIST_MESSAGE = "Leasing organization already exists with register number %s";

  public static final String COULD_NOT_CREATE_ORGANIZATION_CODE = "BPMS218";

  public static final String BRANCH_ID_NULL_ERROR_CODE = "BPMS219";
  public static final String ORG_TYPE_NULL_ERROR_CODE = "BPMS220";
  public static final String ORG_REG_NUM_NULL_ERROR_CODE = "BPMS221";


  public static final String START_CASE_ERROR_CODE = "BPMS222";
  public static final String START_CASE_INPUT_ERROR_CODE = "BPMS223";

  public static final String INVALID_DESCRIPTION_ERROR_CODE = "BPMS224";

  public static final String COLLATERAL_TYPE_INVALID_ERROR_CODE = "BPMS225";
  public static final String COLLATERAL_TYPE_INVALID_ERROR_MESSAGE = "Collateral type is empty!";

  public static final String COLL_REF_TYPES_EMPTY_ERROR_CODE = "BPMS226";
  public static final String COLL_REF_TYPES_EMPTY_ERROR_MESSAGE = "Collateral reference types is empty!";

  public static final String SUBMIT_NULL_VALUE_ERROR_CODE = "BPMS225";
  public static final String SUBMIT_NULL_VALUE_ERROR_MESSAGE = "Form field value might be null!";

  public static final String PARSE_DATE_ERROR_CODE = "BPMS400";
  public static final String PARSE_DATE_ERROR_MESSAGE = "Error occurred during parse date formatted data";

  public static final String GET_BRANCH_ERROR_CODE = "BPMS401";

  public static final String ALREADY_CREATED_ACCOUNT_ERROR_CODE = "BPMS402";
  public static final String ALREADY_CREATED_ACCOUNT_ERROR_MESSAGE = "Loan Account has already been created!";

  public static final String UNABLE_COMPLETE_ACCOUNT_TASK_ERROR_CODE = "BPMS403";
  public static final String UNABLE_COMPLETE_ACCOUNT_TASK_ERROR_MESSAGE = "Unable to continue without creating account! Account number is null!";

  public static final String COLL_LINKAGE_RESPONSE_ERROR_CODE = "BPMS404";
  public static final String COLL_LINKAGE_RESPONSE_ERROR_MESSAGE = "Collateral Linkage response is invalid from XAC service, request id = [%s] account number = [%s]";

  public static final String COLLATERAL_LIST_TASK_NOT_COMPLETED_CODE = "BPMS406";
  public static final String COLLATERAL_LIST_TASK_NOT_COMPLETED_MESSAGE = "Collateral list task must be completed!";

  // Branch banking error code
  public static final String TAX_SEARCH_TYPE_NULL_CODE = "BB001";
  public static final String TAX_SEARCH_TYPE_NULL_MESSAGE = "Tax search type is null!";

  public static final String TAX_SEARCH_VALUE_NULL_CODE = "BB002";
  public static final String TAX_SEARCH_VALUE_NULL_MESSAGE = "Tax search type is null!";

  public static final String CUSTOM_SEARCH_TYPE_NULL_CODE = "BB003";
  public static final String CUSTOM_SEARCH_TYPE_NULL_MESSAGE = "Custom search type is null";

  public static final String CUSTOM_SEARCH_VALUE_IS_NULL_CODE = "BB004";
  public static final String CUSTOM_SEARCH_VALUE_IS_NULL_MESSAGE = "Custom search value is null";

  public static final String BB_INVALID_INVOICE_AMOUNT_CODE = "BB005";
  public static final String BB_INVALID_INVOICE_AMOUNT_MESSAGE = "Invalid Invoice Amount!";

  public static final String BB_DOCUMENT_TYPE_NULL_CODE = "BB006";
  public static final String BB_DOCUMENT_TYPE_NULL_MESSAGE = "Document type is null!";

  public static final String BB_REGISTER_NUMBER_NULL_CODE = "BB007";
  public static final String BB_REGISTER_NUMBER_NULL_MESSAGE = "Register number is null!";

  public static final String BB_PHONE_NUMBER_NULL_CODE = "BB008";
  public static final String BB_PHONE_NUMBER_NULL_MESSAGE = "Phone number is null!";

  public static final String BB_TRANSACTION_DESCRIPTION_NULL_CODE = "BB009";
  public static final String BB_TRANSACTION_DESCRIPTION_NULL_MESSAGE = "Transaction description is null!";

  public static final String BB_TRANSACTION_ACCOUNT_ID_NULL_CODE = "BB010";
  public static final String BB_TRANSACTION_ACCOUNT_ID_NULL_MESSAGE = "Transaction EBank list account id is null!";

  public static final String BB_TRANSACTION_CHANNEL_ID_NULL_CODE = "BB011";
  public static final String BB_TRANSACTION_CHANNEL_ID_NULL_MESSAGE = "Transaction EBank list channel id is null!";

  public static final String BB_TRANSACTION_START_DT_NULL_CODE = "BB012";
  public static final String BB_TRANSACTION_START_DT_NULL_MESSAGE = "Transaction EBank list start date is null!";

  public static final String BB_TRANSACTION_END_DT_NULL_CODE = "BB013";
  public static final String BB_TRANSACTION_END_DT_NULL_MESSAGE = "Transaction EBank list end date is null!";

  public static final String BB_BRANCH_ID_IS_NULL_CODE = "BB014";
  public static final String BB_BRANCH_ID_IS_NULL_MESSAGE = "Branch id is null";

  public static final String BB_ACCOUNT_NUMBER_NULL_CODE = "BB020";
  public static final String BB_ACCOUNT_NUMBER_NULL_MESSAGE = "Account number is null";

  public static final String BB_ONE_OF_CIF_PHONE_IS_REQUIRED_CODE = "BB0021";
  public static final String BB_ONE_OF_CIF_PHONE_IS_REQUIRED_MESSAGE = "CIF number or phone number is required";

  public static final String BB_CIF_NUMBER_IS_NULL_CODE = "BB0022";
  public static final String BB_CIF_NUMBER_IS_NULL_MESSAGE = "CIF Number is null!";

  public static final String BB_REGISTERED_BRANCH_IS_NULL_CODE = "BB0023";
  public static final String BB_REGISTERED_BRANCH_IS_NULL_MESSAGE = "Registered branch id is null!";

  public static final String BB_LOGIN_USERNAME_IS_NULL_CODE = "BB0030";
  public static final String BB_LOGIN_USERNAME_IS_NULL_MESSAGE = "Login username is null!";

  public static final String BB_CUSTOMER_INFO_IS_NULL_CODE = "BB0031";
  public static final String BB_CUSTOMER_INFO_IS_NULL_MESSAGE = "Customer information is null";

  public static final String BB_MAIN_ACCOUNT_IS_NULL_CODE = "BB0032";
  public static final String BB_MAIN_ACCOUNT_IS_NULL_MESSAGE = "USSD main account is null";

  public static final String BB_USSD_ACCOUNTS_PARSE_ERROR_CODE = "BB0033";
  public static final String BB_USSD_ACCOUNTS_PARSE_ERROR_MESSAGE = "Error parsing USSD accounts information";

  public static final String BB_MOBILE_NUMBER_IS_NULL_CODE = "BB0034";
  public static final String BB_MOBILE_NUMBER_IS_NULL_MESSAGE = "USSD phone number is required to update password!";

  public static final String BB_STATUS_IS_NULL_CODE = "BB0035";
  public static final String BB_STATUS_IS_NULL_MESSAGE = "Status is required!";

  public static final String BB_CHANNEL_NULL_CODE = "BB0024";
  public static final String BB_CHANNEL_NULL_MESSAGE = "Channel is null";

  public static final String BB_DESTINATION_NULL_CODE = "BB0025";
  public static final String BB_DESTINATION_NULL_MESSAGE = "USSD phone number is null";

  public static final String BB_CONTENT_MESSAGE_NULL_CODE = "BB0026";
  public static final String BB_CONTENT_MESSAGE_NULL_MESSAGE = "OTP code is null";

  public static final String BB_ID_IS_NULL_CODE = "BB0036";
  public static final String BB_ID_IS_NULL_MESSAGE = "id cannot be null";

  // Salary package transaction
  public static final String BB_ACCOUNT_INFO_LIST_EMPTY_CODE = "BB021";
  public static final String BB_ACCOUNT_INFO_LIST_EMPTY_MESSAGE = "Account info list is empty";
  public static final String BB_MISSING_TABLE_HEADER_CODE = "BB022";
  public static final String BB_MISSING_TABLE_HEADER_MESSAGE = "Table headers are missing!";

  public static final String BB_BASE64_IS_NULL_CODE = "BB023";
  public static final String BB_BASE64_IS_NULL_MESSAGE = "Encoded base64 string is null!";

  public static final String BB_RESPONSE_NULL_CODE = "BB024";
  public static final String BB_RESPONSE_NULL_MESSAGE = "Excel reading service returned null response!";

  public static final String BB_SALARY_ACCOUNT_LIST_NULL_CODE = "BB025";
  public static final String BB_SALARY_ACCOUNT_LIST_NULL_MESSAGE = "Salary account list is empty!";

  public static final String BB_OTP_EXPIRED_CODE = "BB035";
  public static final String BB_OTP_EXPIRED_MESSAGE = "Generated one time password is expired!";

  public static final String BB_OTP_FAILED_CODE = "BB036";
  public static final String BB_OTP_FAILED_MESSAGE = "Failed to validate given code to the OTP!";

  // Loan account info
  public static final String BB_LOAN_ACCOUNT_ID_IS_NULL_CODE = "BB037";
  public static final String BB_LOAN_ACCOUNT_ID_IS_NULL_MESSAGE = "Loan repayment account id is null!";

  public static final String BB_USSD_PHONE_NUMBER_NULL_CODE = "BB038";
  public static final String BB_USSD_PHONE_NUMBER_NULL_MESSAGE = "Enter your phone number to register for the USSD service";

  // transaction limit
  public static final String BB_TRANSACTION_LIMIT_EXCEPTION_CODE = "BB100";
  public static final String BB_TRANSACTION_LIMIT_EXCEPTION_MESSAGE = "Transaction limit exceed! max limit is : ";

  public static final String BPMS_PARAMETER_NOT_FOUND_CODE = "BPMS405";

  //Loan Contract

  public static final String BPMS_LOAN_CONTRACT_REQUEST_ID_ERROR_CODE = "BPMS407";
  public static final String BPMS_LOAN_CONTRACT_REQUEST_ID_ERROR_MESSAGE = "Not able to create request with this account! Max request limit with this account is 9";

  public static final String COLLATERAL_ID_NULL_CODE = "BPMS408";
  public static final String COLLATERAL_ID_NULL_MESSAGE = "Collateral Id is missing!";

  public static final String COULD_NOT_START_PROCESS_ERROR_CODE = "BPMS409";
  public static final String COULD_NOT_START_PROCESS_ERROR_MESSAGE = "Could not start new process!";

  //File
  public static final String CONTENT_AS_BASE64_NULL_CODE = "BPMS220";
  public static final String CONTENT_AS_BASE64_NULL_MESSAGE = "Content as base64 is null!";
  public static final String ACCOUNT_NUMBER_NULL_CODE = "BPMS410";
  public static final String ACCOUNT_NUMBER_NULL_MESSAGE = "Account number is null!";

  public static final String START_DATE_NULL_CODE = "BPMS412";
  public static final String START_DATE_NULL_MESSAGE = "Start date is null!";

  public static final String END_DATE_NULL_CODE = "BPMS413";
  public static final String END_DATE_NULL_MESSAGE ="End date is null!";

  public static final String COULD_NOT_GET_ALL_LOAN_CONTRACT_REQUEST_CODE = "BPMS414";
  public static final String COULD_NOT_GET_ALL_LOAN_CONTRACT_REQUEST_MESSAGE = "Failed to acquire all loan contract requests!";

  public static final String CUSTOMER_REGISTER_NUMBER_NULL_CODE = "BPMS523";
  public static final String CUSTOMER_REGISTER_NUMBER_NULL_MESSAGE = "Customer register number is cannot be null!";

  //Direct Online Loan
  public static final String DAN_REGISTER_NUMBER_NULL_CODE = "BPMS521";
  public static final String DAN_REGISTER_NUMBER_NULL_MESSAGE = "DAN Register Number is blank!";

  public static final String DISTRICT_NULL_CODE = "BPMS526";
  public static final String DISTRICT_NULL_MESSAGE = "DAN district is blank!";

  public static final String BRANCH_NUMBER_IS_NULL_CODE = "BPMS524";
  public static final String BRANCH_NUMBER_IS_NULL_MESSAGE = "Customer branch number cannot be null!";

  public static final String CUSTOMER_NAME_IS_NULL_CODE = "BPMS525";
  public static final String CUSTOMER_NAME_IS_NULL_MESSAGE = "Customer name  cannot be null!";

  // BNPL
  public static final String MODULE_TYPE_IS_NULL_CODE = "BPMS528";
  public static final String MODULE_TYPE_IS_NULL_MESSAGE = "module type id is blank!";

  public static final String REPAYMENT_SCHEDULE_TYPE_IS_NULL_CODE = "BPMS529";
  public static final String REPAYMENT_SCHEDULE_TYPE_NULL_MESSAGE = "Repayment schedule type id is blank!";

  public static final String COULD_NOT_FIND_COMPLETED_PROCESS_BY_CIF_IN_LAST_24_MESSAGE = "Could not find confirmed process in last 24 hours by cif = ";

  public static final String COULD_NOT_FIND_COMPLETED_PROCESS_BY_CIF_IN_LAST_48_MESSAGE = "Could not find confirmed process in last 48 hours by cif = ";

  public static final String COULD_NOT_FIND_CONFIRMED_PROCESS_BY_CIF_IN_LAST_7_DAYS_MESSAGE = "Could not find CONFIRMED process in last 7 days by cif = ";
  public static final String COULD_NOT_FIND_COMPLETED_PROCESS_BY_CIF_IN_LAST_7_DAYS_MESSAGE = "Could not find COMPLETED process in last 7 days by cif = ";

  public static final String INVOICE_NOT_FOUND_ERROR_CODE = "BPMS530";

  public static final String INVOICE_AMOUNT_IS_GREATER_THAN_LOAN_AMOUNT_ERROR_CODE = "BPMS531";
  public static final String INVOICE_AMOUNT_IS_GREATER_THAN_LOAN_AMOUNT_ERROR_MESSAGE = "Invoice amount is greater than loan calculated amount!";

  public static final String CONTRACT_NUMBER_BLANK_CODE = "BPMS532";
  public static final String CONTRACT_NUMBER_BLANK_MESSAGE = "Contract number is required!";

  public static final String CONTRACT_NUMBER_IS_NULL_CODE = "BPM533";
  public static final String CONTRACT_NUMBER_IS_NULL_MESSAGE = "Contract number cannot be null!";

  public static final String CONTRACT_REQUEST_ID_IS_NULL_CODE = "BPM534";
  public static final String CONTRACT_REQUEST_ID_IS_NULL_MESSAGE = "Contract request id cannot be null!";
}
