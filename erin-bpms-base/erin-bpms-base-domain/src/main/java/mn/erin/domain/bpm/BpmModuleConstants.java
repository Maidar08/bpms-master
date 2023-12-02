/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm;

import java.util.HashMap;
import java.util.Map;

/**
 * @author EBazarragchaa
 */
public final class BpmModuleConstants
{
  private BpmModuleConstants()
  {
    throw new IllegalStateException("Constants class");
  }

  public static final String DIRECT_ONLINE_USER_ID = "directOnline.user.id";
  public static final String DIRECT_ONLINE_TENANT_ID = "directOnline.tenant.id";
  public static final String DIRECT_ONLINE_USER_PASSWORD = "directOnline.user.password";

  public static final String COLLATERAL_ID_PREFIX = "BPMS";
  public static final String COLLATERAL_ID_NUMBER = "001";

  public static final String LOAN_CONTRACT_CAMEL_CASE = "LoanContract";
  public static final String LOAN_CLASS_RANK = "loanClassRank";

  public static final String PROCESS_CATEGORY_BRANCH_BANKING = "BRANCHBANKING";

  public static final String RETRY_ATTEMPT = "retryAttempt";

  public static final String YES_MN_VALUE = "Тийм";
  public static final String NO_MN_VALUE = "Үгүй";
  public static final String MALE_MN_VALUE = "Эрэгтэй";
  public static final String FEMALE_MN_VALUE = "Эмэгтэй";
  public static final String EMPTY_VALUE = "";
  public static final String NULL_STRING = "null";

  public static final String CONFIRM_LOAN_AMOUNT = "confirmLoanAmount";

  public static final String COMPANY_NAME = "cname";
  public static final String FCNAME = "fcname";

  public static final String CORPORATE_RANK = "corporaterank";

  public static final String CHO_BRANCH = "CHO";
  public static final String RECEIVED_USER = "receivedUser";
  public static final String RECEIVERS = "receivers";
  public static final String SENT_USER = "sentUser";

  public static final String ADMIN_1 = "admin_1";

  public static final String APPLICATION_ID = "bpms";
  public static final String MODULE_ID = "bpm";

  public static final String NEW = "ШИНЭ";
  public static final String STARTED = "СУДЛАГДАЖ БАЙНА";
  public static final String REJECTED = "ТАТГАЛЗСАН";
  public static final String CUSTOMER_APPROVED = "Зөвшөөрсөн";

  public static final String PROCESS_TYPE_ID = "processTypeId";
  public static final String PROCESS_TYPE_NAME = "processTypeName";

  public static final String PROCESS_REQUEST_ID = "processRequestId";

  public static final String CASE_INSTANCE_ID = "caseInstanceId";

  public static final String INSTANCE_ID = "instanceId";

  public static final String REGISTER_NUMBER = "registerNumber";
  public static final String CIF_NUMBER = "cifNumber";

  public static final String FULL_NAME = "fullName";
  public static final String ADDRESS = "address";
  public static final String ADDRESS_INFO = "addressInfo";

  public static final String FIRST_NAME = "firstName";
  public static final String LAST_NAME = "lastName";

  public static final String OCCUPANCY = "occupancy";
  public static final String LOAN_PURPOSE = "loanPurpose";
  public static final String BORROWER_TYPE_CATEGORY = "BorrowerCategoryCode";

  public static final String PHONE_NUMBER = "phoneNumber";
  public static final String EMAIL = "email";
  public static final String LOCALE = "locale";

  public static final String SEX = "sex";

  public static final String KHUR_DOWNLOAD_INQUIRIES = "downloadInquiries";

  public static final String LOAN_PRODUCT = "loanProduct";
  public static final String LOAN_PRODUCT_NAME = "loanProductName";
  public static final String LAST_CALCULATED_PRODUCT = "lastCalculatedProduct";

  public static final String LOAN_PRODUCT_DESCRIPTION = "loanProductDescription";
  public static final String LOAN_AMOUNT = "amount";
  public static final String HAS_COLLATERAL = "hasCollateral";
  public static final String GRANT_LOAN_AMOUNT_STRING = "grantLoanAmountString";
  public static final String GRANT_LOAN_AMOUNT = "grantLoanAmount";

  public static final String GRANTED_LOAN_AMOUNT = "loanAmount";

  public static final String PRODUCT_HAS_COLLATERAL = "productHasCollateral";

  public static final String CONFIRMED_USER_ID = "confirmedUserId";
  public static final String SCORING_SCORE = "score";
  public static final String SCORING_LEVEL_RISK = "scoring_level_risk";
  public static final String WORK_SPAN = "workspan";

  public static final String REGISTER_NUMBER_CO_BORROWER = "registerNumberCoBorrower";
  public static final String CIF_NUMBER_CO_BORROWER = "cifNumberCoBorrower";
  public static final String CO_BORROWER_CUST_ID = "coBorrowerCustId";

  public static final String FULL_NAME_CO_BORROWER = "fullNameCoBorrower";
  public static final String ADDRESS_CO_BORROWER = "addressCoBorrower";
  public static final String OCCUPANCY_CO_BORROWER = "occupancyCoBorrower";

  public static final String PHONE_NUMBER_CO_BORROWER = "phoneNumberCoBorrower";
  public static final String EMAIL_CO_BORROWER = "emailCoBorrower";

  public static final String TYPE_CO_BORROWER = "borrowerTypeCoBorrower";
  public static final String INCOME_TYPE_CO_BORROWER = "incomeTypeCoBorrower";
  public static final String CO_BORROWER_TYPE = "coBorrowerType";

  public static final String RETAIL_CUSTOMER = "Retail";
  public static final String CORPORATE_CUSTOMER = "Corporate";
  public static final String CUSTOMER = "customer";
  public static final String EMPLOYEE_REGISTER_NUMBER = "employeeRegisterNumber";

  public static final String FINGER_PRINT = "fingerPrint";
  public static final String FINGER_PRINT_LOWER_CASE = "fingerprint";

  public static final String EMPLOYEE_FINGER_PRINT = "employeeFingerPrint";

  public static final String MONTH = "month";
  public static final String SALARY_INFO = "salaryInfo";
  public static final String YEAR = "year";
  public static final String SALARY_AMOUNT = "salaryAmount";
  public static final String AVERAGE_SALARY_BEFORE_TAX = "averageSalaryBeforeTax";
  public static final String AVERAGE_SALARY_AFTER_TAX = "averageSalaryAfterTax";
  public static final String IS_EXCLUDED_NIIGMIIN_DAATGAL = "isExcludedNiigmiinDaatgal";
  public static final String IS_EXCLUDED_HEALTH_INSURANCE = "isExcludedHealthInsurance";

  public static final String XYP_SALAPY = "XYP_SALARY";

  public static final String CHANNEL = "channel";

  public static final String SALARY_INFO_CO_BORROWER = "salaryInfoCoBorrower";

  public static final String STARTED_DATE = "startedDate";
  public static final String EXPIRE_DATE = "expireDate";

  public static final String MONTH_PAYMENT_ACTIVE_LOAN = "monthPaymentActiveLoan";

  public static final String CUSTOMER_CID = "customerCID";
  public static final String BRANCH_NUMBER = "branchNumber";
  public static final String BRANCH_NO = "branchNo";
  public static final String LEGAL_STATUS = "legalStatus";

  public static final String LOAN_REPORT = "loanReport";

  public static final String LOAN_ACCOUNT_NUMBER = "loanAccountNumber";

  public static final String REPAYMENT_TYPE = "repaymentType";
  public static final String REPAYMENT_TYPE_ID = "repaymentTypeId";
  public static final String PRODUCT_CODE = "productCode";
  public static final String CURRENCY_MNT = "MNT";
  public static final String COLLATERAL_LIST_FORM_XAC_BANK = "collateralListFromXacBank";
  public static final String HAS_INSURANCE = "hasInsurance";

  // Date format
  public static final String DATE_FORMAT1 = "E MMM dd HH:mm:ss Z yyyy";
  public static final String DATE_FORMAT2 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
  public static final String DATE_FORMAT3 = "E MMM dd yyyy";

  // Undsen tolbor tentsuu
  public static final String REPAYMENT_EQUAL_PRINCIPLE_PAYMENT = "equalPrinciplePayment";
  // Niit tolbor tentsuu
  public static final String REPAYMENT_EQUATED_MONTHLY_INSTALLMENT = "equatedMonthlyInstallment";

  public static final String EQUAL_PRINCIPLE_PAYMENT_VALUE = "a";
  public static final String EQUATED_MONTHLY_INSTALLMENT_VALUE = "b";

  public static final String EQUAL_PRINCIPLE_PAYMENT = "Үндсэн төлбөр тэнцүү";
  public static final String EQUATED_MONTHLY_INSTALLMENT = "Нийт төлбөр тэнцүү";

  public static final String LOAN_ENQUIRE_PDF_CO_BORROWER = "coBorrowerLoanEnquirePDF";
  public static final String INDEX_CO_BORROWER = "indexCoBorrower";
  public static final String CO_BORROWER_CONTEXT = "CoBorrower";

  public static final String CUSTOMER_LOAN_ENQUIRE_PDF = "customerLoanEnquirePDF";
  public static final String TERM = "term";
  public static final String LOAN_TERM = "loanTerm";
  public static final String NUMBER_OF_PAYMENTS = "numberOfPayments";
  public static final String CHARGE = "charge";
  public static final String FEES = "fees";

  public static final String BLANK = "  ";

  // Loan contract as base 64.
  public static final String LOAN_CONTRACT_AS_BASE_64 = "loanContractAsBase64";
  public static final String ONLINE_SALARY_LOAN_CONTRACT_AS_BASE_64 = "onlineSalaryLoanContractAsBase64";

  public static final String LOAN_REPORT_AS_BASE_64 = "loanReportAsBase64";
  public static final String LOAN_PAYMENT_SCHEDULE_AS_BASE_64 = "loanPaymentScheduleAsBase64";

  // Sources
  public static final String SOURCE_CAMUNDA = "CAMUNDA";
  public static final String SOURCE_ALFRESCO = "ALFRESCO";

  public static final String SOURCE_PUBLISHER = "PUBLISHER";
  public static final String SOURCE_LDMS = "LDMS";

  // BPM Document constants
  // Loan contract
  public static final String MAIN_LOAN_CONTRACT_DOC_ID = "11.01";
  public static final String MAIN_LOAN_REPORT_DOC_ID = "11.02";
  public static final String MAIN_LOAN_PAYMENT_SCHEDULE_DOC_ID = "11.02";
  public static final String CATEGORY_LOAN_CONTRACT = "11. З.Гэрээ";

  public static final String SUB_CATEGORY_LOAN_CONTRACT = "Үндсэн зээлийн гэрээ.pdf";
  public static final String SUB_CATEGORY_LOAN_REPORT = "02. Бусад гэрээ";

  public static final String LOAN_REPORT_NAME = "Зээлийн мэдээллийн хуудас";

  public static final String NAME_DOCUMENT_LOAN_PAYMENT_SCHEDULE = "Зээлийн эргэн төлөлтийн хуваарь";

  public static final String NAME_LOAN_AMOUNT = "Зээлийн дүн";

  public static final String PRODUCT_NAME_MN = "Бүтээгдэхүүн";
  public static final String PRODUCT_NAME = "productName";
  // Loan decision
  public static final String CATEGORY_LOAN_DECISION_DOCUMENT = "10 З.Шийдвэр";
  public static final String CATEGORY_LOAN_PERMISSION_DOCUMENT = "10.3 шийдвэр";

  // Enquires (Mongol bank, Xyp etc ...)
  public static final String CATEGORY_ENQUIRE = "04. Лавлагаа";
  public static final String SUB_CATEGORY_MONGOL_BANK = "01. МБ Лавлагаа";
  public static final String DOCUMENT_NAME_MONGOL_BANK_ENQUIRE = "Монгол банкны лавлагаа";
  public static final String DOC_NAME_MONGOL_BANK_ENQUIRE_CO_BORROWER = "Монгол банкны лавлагаа Хамтран ";

  public static final String STAGE = "stage";
  public static final String INCOME_TYPE_SALARY = "Цалингийн орлого";

  // Process Request Table Header
  public static final String[] PROCESS_REQUEST_TABLE_HEADER = { "№", "Хүсэлтийн Дугаар", "Овог,Нэр", "Регистр", "CIF", "Бүтээгдэхүүн", "Хэмжээ", "Огноо",
      "Хэрэглэгч", "Салбар", "Суваг", "Төлөв" };
  public static final Map<String, String> PROCESS_TYPE_IDS = new HashMap<String, String>()
  {{
    put(CONSUMPTION_LOAN, "Хэрэглээний зээл");
    put(MICRO_LOAN, "Бичил зээл");
  }};

  public static final Map<String, String> PROCESS_REQUEST_STATE_MAP = new HashMap<String, String>()
  {{
    put("CONFIRMED", "БАТЛАГДСАН");
    put("CUST_REJECTED", "ХАРИЛЦАГЧ ТАТГАЛЗСАН");
    put("ORG_REJECTED", "БАНК ТАТГАЛЗСАН");
    put("STARTED", "СУДЛАГДАЖ БАЙНА.");
    put("NEW", "ШИНЭ");
    put("RETURNED", "БУЦААГДСАН");
    put("REJECTED", "ЗАХИРАЛ-БАНК ТАТГАЛЗСАН");
  }};

  public static final Map<String, String> PROCESS_REQUEST_TYPE = new HashMap<String, String>()
  {{
    put(MY_LOAN_REQUEST, "Миний зээлийн хүсэлт");
    put(BRANCH_LOAN_REQUEST, "Салбарын зээлийн хүсэлт");
    put(ALL_LOAN_REQUEST, "Бүх зээлийн хүсэлт");
    put(INTERNET_BANK_LOAN_REQUEST, "ИНтернет банкны хүсэлт");
    put(SEARCH_CUSTOMER, "Харилцагчийн зээлийн хүсэлт хайх");
    put(SUB_GROUP_REQUEST, "Салбарын зээлийн хүсэлт");
  }};

  public static final String MY_LOAN_REQUEST = "my-loan-request";
  public static final String BRANCH_LOAN_REQUEST = "branch-loan-request";
  public static final String ALL_LOAN_REQUEST = "all-request";
  public static final String INTERNET_BANK_LOAN_REQUEST = "ebank-request";
  public static final String SEARCH_CUSTOMER = "search-customer";
  public static final String SUB_GROUP_REQUEST = "sub-group-request";

  // Context constants (CustomerInfo, LoanInfo etc...)
  public static final String CONTEXT_CO_BORROWER = "co-borrower";
  public static final String CONTEXT_CUSTOMER = "customer";
  public static final String CONTEXT_ACCOUNT = "loan-account";
  public static final String CONTEXT_ACCOUNT_UDF = "loan-account-udf";
  public static final String CONTEXT_LOAN_CONTRACT = "loan-contract";

  public static final String ACTIVE_REQUEST = "active-request";
  public static final String LOAN_REQUEST = "loan-request";
  public static final String SCORING = "scoring";
  public static final String AREA = "area";
  public static final String ASSET_LOAN_RATIO = "asset_loan_ratio";
  public static final String INCOME_TYPE = "incomeType";

  public static final String CONSUMPTION_LOAN = "consumptionLoan";
  public static final String CONSUMPTION_LOAN_2 = "ConsumptionLoan";
  public static final String MICRO_LOAN = "microLoan";

  public static final String CREDIT_LINE_LOAN = "creditLineLoan";
  public static final String MORTGAGE_LOAN = "mortgageLoan";

  //Salary calculation
  public static final String SALARY_CALCULATION = "SalaryCalculation";
  public static final String SALARY_CALCULATION_HHOAT_DISCOUNT = "HhoatDiscount";
  public static final String SALARY_CALCULATION_HHOAT_TAX = "HhoatTax";
  public static final String SALARY_CALCULATION_HHOAT_TAX_NEW = "HhoatTaxNew";

  public static final String APPLICATION_TYPE_CONSUMPTION_LOAN = "CONSUMER";
  public static final String APPLICATION_TYPE_MICRO_LOAN = "SMALL_MICRO";

  public static final String APPLICATION_TYPE_CREDIT_LINE = "CREDIT_LINE";

  public static final Map<String, String> PROCESS_TYPES = new HashMap<String, String>()
  {{
    put(CONSUMPTION_LOAN, "Хэрэглээний зээл");
  }};

  public static final String USER_TASK = "userTask";
  public static final String ISO_DATE_FORMAT = "yyyy-MM-dd";
  public static final String SIMPLE_DATE_FORMAT = "yyyy/MM/dd";
  public static final String ISO_DATE_FORMAT_WITH_HOUR = "yyyy-MM-dd HH:mm";

  public static final String TASK_ID = "taskId";
  public static final String FORM_ID = "formId";
  public static final String TASK_DEFINITION_KEY = "taskDefinitionKey";
  public static final String FORM_FIELDS = "formFields";
  public static final String SPECIAL_FORMS = "specialForms";

  // Xyp constants
  public static final String ID_CARD_TEMPLATE = "Templates/101_103_Irgenii_Unemlehnii_Lavlagaa.docx";

  public static final String ID_CARD_ENQUIRE_DOCUMENT_NAME = "Иргэний үнэмлэхний лавлагаа";
  public static final String ID_CARD_ENQUIRE_CO_BORROWER_DOCUMENT_NAME = "Иргэний үнэмлэхний лавлагаа Хамтран ";

  public static final String SALARY_ENQUIRE_DOCUMENT_NAME = "Нийгмийн даатгалын лавлагаа";
  public static final String SALARY_ENQUIRE_CO_BORROWER_DOCUMENT_NAME = "Нийгмийн даатгалын лавлагаа Xaмтран ";

  public static final String PROPERTY_ENQUIRE_DOCUMENT_NAME = "Үл хөдлөх хөрөнгий лавлагаа";
  public static final String PROPERTY_ENQUIRE_CO_BORROWER_DOCUMENT_NAME = "Үл хөдлөх хөрөнгий лавлагаа Xaмтран ";

  public static final String SLASH = "/";
  public static final String ALFRESCO = "ALFRESCO";
  public static final String CUSTOMERS_FOLDER = "Customers";

  // User Task Definition Key
  public static final String TASK_DEF_CREATE_COLLATERAL = "user_task_create_collateral";
  public static final String TASK_DEF_MICRO_LOAN_AMOUNT = "user_task_calculate_micro_loan_amount";

  // Activity names
  public static final String MONGOL_BANK_ACTIVITY_NAME = "02. Монгол банк лавлагаа";
  public static final String ELEMENTARY_CRITERIA_ACTIVITY_NAME = "07. Анхан шатны шалгуур";

  public static final String SCORING_ACTIVITY_NAME = "09. Скоринг";
  public static final String MORTGAGE_SCORING_NAME = "13. Скоринг хийх";
  public static final String SCORING_MICRO_ACTIVITY_NAME = "13. Скоринг хийх";

  public static final String SALARY_CALCULATION_ACTIVITY_NAME = "08. Тооцоолол хийх";

  public static final String CALCULATE_INTEREST_RATE_ACTIVITY_NAME = "10. Хүү тооцоолох";
  public static final String CALCULATE_LOAN_AMOUNT_ACTIVITY_NAME = "11. Зээлийн хэмжээ тооцох";
  public static final String CALCULATE_LOAN_AMOUNT_MICRO_ACTIVITY_NAME = "09. Зээлийн хэмжээ тооцох";

  public static final String REMOVE_CO_BORROWER_ACTIVITY_NAME = "12. Хамтран хасах";
  public static final String LOAN_DECISION_ACTIVITY_NAME = "16. Зээл шийдвэрлэх";
  public static final String MICRO_LOAN_DECISION_ACTIVITY_NAME = "15. Зээл шийдвэрлэх";
  public static final String CREATE_LOAN_ACCOUNT_ACTIVITY_NAME = "17. Зээлийн данс үүсгэх";
  public static final String CREATE_LOAN_ACCOUNT_ACTIVITY_NAME_PREVIOUS = "14. Зээлийн данс үүсгэх";

  public static final String CREATE_LOAN_ACCOUNT_ACTIVITY_NAME_NO_NUMBER = "Зээлийн данс үүсгэх";
  public static final String CREATE_LOAN_ACCOUNT_WITH_COLLATERAL_ACTIVITY_NAME = "17. Барьцаатай зээлийн данс үүсгэх";
  public static final String CREATE_LOAN_ACCOUNT_MICRO_WITH_COLLATERAL_ACTIVITY_NAME = "16. Зээлийн данс үүсгэх";
  public static final String INTEREST_RATE = "interestRate";
  public static final String YEARLY_INTEREST_RATE = "yearlyInterestRate";
  public static final String YEARLY_INTEREST_RATE_STRING = "yearlyInterestRateString";
  public static final String YEARLY_INTEREST_RATE_STRING_PERCENTAGE = "yearlyInterestRateStringPercentage";
  public static final String DEPOSIT_INTEREST_RATE_STRING = "depositInterestRateString";
  public static final String DEPOSIT_INTEREST_RATE = "depositInterestRate";
  public static final String CURRENT_ACCOUNT_NUMBER = "currentAccountNumber";
  public static final String ACCOUNT_BRANCH_NUMBER = "accountBranchNumber";
  public static final String FIRST_PAYMENT_DATE = "firstPaymentDate";
  public static final String REPAYMENT_DATE = "repaymentDate";
  public static final String DAY_OF_PAYMENT = "dayOfPayment";
  public static final String TASK_NUMBER_CREATE_LOAN_ACCOUNT = "17";
  public static final String ZDC = "ZDC";

  public static final String ACTIVITY_ID_STAGE_COLLATERAL_LIST = "stage_collateral_list";

  public static final String LOAN_GRANT_DATE = "loanGrantDate";

  // Roles
  public static final String ROLE_RANALYST = "ranalyst";
  public static final String ROLE_HUB_DIRECTOR = "hub_director";
  public static final String ROLE_BRANCH_DIRECTOR = "branch_director";
  public static final String ROLE_RC_SPECIALIST = "rc_specialist";
  public static final String ROLE_HR_SPECIALIST = "hr_specialist";
  public static final String ROLE_ADMIN_1 = "admin_1";

  // loan amount field
  public static final String FIXED_ACCEPTED_LOAN_AMOUNT = "fixedAcceptedLoanAmount";
  public static final String OLD_FIXED_ACCEPTED_LOAN_AMOUNT = "oldFixedAcceptedLoanAmount";

  public static final String ACCEPTED_LOAN_AMOUNT = "acceptedLoanAmount";
  public static final String LOAN_APPROVAL_AMOUNT = "loanApprovalAmount";
  public static final String FIXED_ACCEPTED_LOAN_AMOUNT_STRING = "fixedAcceptedLoanAmountString";
  public static final String DEBT_INCOME_INSURANCE_STRING = "debtIncomeIssuanceString";
  public static final String DEBT_INCOME_BALANCE = "debtIncomeBalance";
  public static final String DEBT_INCOME_INSURANCE = "debtIncomeIssuance";
  public static final String DEBT_INCOME_INSURANCE_PERCENT = "debtIncomeIssuancePercent";
  public static final String AFTER_AVERAGE_SALARY_TAX = "averageSalaryAfterTax";
  public static final String IS_CUSTOMER_CONFIRMED = "confirmLoanAmount";
  public static final String MAX_AMOUNT = "MaxAmount";

  // Mongol Bank
  public static final String DOWNLOAD_MONGOL_BANK = "downloadMongolBank";
  public static final String MONGOL_BANK_FIRST_NAME = "mbFirstName";
  public static final String MONGOL_BANK_LAST_NAME = "mbLastName";
  public static final String MONGOL_BANK_LEGAL_STATE = "mbLegalState";
  public static final String MONGOL_BANK_REGISTER_NUMBER = "mgRegisterNumber";
  public static final String MONGOL_BANK_ID_NUMBER = "mbIdNumber";
  public static final String MONGOL_BANK_DATE_OF_BIRTH = "mbBirth";
  public static final String MONGOL_BANK_ADDRESS = "mbAddress";
  public static final String MONGOL_BANK_OCCUPANCY = "mbOccupancy";
  public static final String MONGOL_BANK_PROFESSION = "mbProfession";
  public static final String MONGOL_BANK_FAMILY_NAME = "mbFamilyName";
  public static final String MONGOL_BANK_FAMILY_LAST_NAME = "mbFamilyLastName";
  public static final String MONGOL_BANK_FAMILY_REGISTER_NUMBER = "mbFamilyRegisterNumber";
  public static final String MONGOL_BANK_NUMBER_OF_FAMILY_MEMBER = "mbNumberOfFamilyMember";
  public static final String MONGOL_BANK_JOBLESS_NUMBER_OF_FAMILY_MEMBER = "mbJoblessNumberOfFamilyMember";

  // Micro Mongol Bank
  public static final String MONGOL_BANK_LAW_STATUS = "lawStatus";
  public static final String MONGOL_BANK_ORGANIZATION_NAME = "organizationName";
  public static final String MONGOL_BANK_ORGANIZATION_FITCH = "organizationFitch";
  public static final String MONGOL_BANK_ORGANIZATION_SP = "organizationSP";
  public static final String MONGOL_BANK_ORGANIZATION_MOODY = "organizationMoody";
  public static final String MONGOL_BANK_REG_IN_MONGOLIA = "registrationInMongolia";
  public static final String MONGOL_BANK_ORGANIZATION_TYPE = "organizationType";
  public static final String MONGOL_BANK_REG_NUM_ID = "registerNumberId";
  public static final String MONGOL_BANK_STATE_NUMBER = "stateNumber";
  public static final String MONGOL_BANK_ESTABLISHED_DATE = "established";
  public static final String MONGOL_BANK_OFFICIAL_ADDRESS = "officialAddress";
  public static final String MONGOL_BANK_EMPLOYEES_NUMBER = "numberOfEmployees";
  public static final String MONGOL_BANK_EXECUTIVE_FIRST_NAME = "executiveFirstName";
  public static final String MONGOL_BANK_EXECUTIVE_LAST_NAME = "executiveLastName";
  public static final String MICRO_MONGOL_BANK_LEGAL_STATE = "legalState";
  public static final String MONGOL_BANK_EXECUTIVE_REG_NUMBER = "executiveRegisterNumber";
  public static final String MONGOL_BANK_NUMBER_OF_SHAREHOLDERS = "numberOfShareholders";
  // micro loan amount field
  public static final String DEBT_TO_SOLVENCY_RATIO = "debtToSolvencyRatio";
  public static final String DEBT_TO_SOLVENCY_RATIO_STRING = "debtToSolvencyRatioString";
  public static final String DEBT_TO_INCOME_RATIO = "debtToIncomeRatio";
  public static final String DEBT_TO_INCOME_RATIO_STRING = "debtToIncomeRatioString";
  public static final String DEBT_TO_INCOME_ASSET = "debtToAssetsRatio";
  public static final String DEBT_TO_INCOME_ASSET_STRING = "debtToAssetsRatioString";
  public static final String CURRENT_ASSETS_RATIO = "currentAssetsRatio";
  public static final String CURRENT_ASSETS_RATIO_STRING = "currentAssetsRatioString";
  public static final String COLLATERAL_PROVIDED_AMOUNT = "collateralProvidedAmount";

  // Collateral fields
  public static final String COLLATERAL_LIST = "collateralList";
  public static final String COLLATERAL_ID = "collateralId";
  public static final String COLLATERAL_DESCRIPTION = "collateralDescription";
  public static final String COLLATERAL_VALUE = "collateralValue";
  public static final String COLLATERAL_BASIC_TYPE = "collateralBasicType";
  public static final String FORM_OF_OWNERSHIP = "formOfOwnership";
  public static final String AMOUNT_OF_COLLATERAL = "amountOfCollateral";
  public static final String OWNER_NAMES = "ownerNames";
  public static final String OWNER_NAME = "ownerName";
  public static final String COLLATERAL_SUB_TYPE = "collateralSubType";
  public static final String STATE_REGISTRATION_NUMBER = "stateRegistrationNumber";
  public static final String DEDUCTION_RATE = "deductionRate";
  public static final String PRODUCT = "product";
  public static final String START_DATE = "startDate";
  public static final String END_DATE = "endDate";
  public static final String COLLATERAL_ASSESSMENT = "collateralAssessment";
  public static final String LOCATION_OF_COLLATERAL = "locationOfCollateral";
  public static final String TYPE_OF_ASSESSMENT = "typeOfAssessment";

  public static final String COLLATERAL_CODE_FORM_FIELD_ID = "collateralCode";
  public static final String INSPECTION_TYPE_CODE_FORM_FIELD_ID = "inspectionType";

  public static final String COLL_TYPE_IMMOVABLE = "i";
  public static final String COLL_TYPE_VEHICLE = "v";
  public static final String COLL_TYPE_MACHINERY = "m";
  public static final String COLL_TYPE_OTHERS = "o";

  public static final String INSPECTION_DATE_FORM_FIELD_ID = "inspectionDate";
  public static final String REFERENCE_CODE_COLL_OWNERTYPE = "ownertype";
  public static final String REFERENCE_CODE_COLL_INSPTYPE = "insptype";

  public static final String REFERENCE_CODE_COLL_CITY = "city";
  public static final String REFERENCE_CODE_COLL_PURPOSE = "purpose";
  public static final String REFERENCE_CODE_COLL_LEASING_SUPPLIER = "nocharge";
  public static final String REFERENCE_CODE_COLL_VEHICLE_TYPE = "vehtype";

  public static final String OWNERSHIP_TYPE_CODE_FORM_FIELD_ID = "ownershipType";
  public static final String CITY_FORM_FIELD_ID = "city";
  public static final String PURPOSE_OF_USAGE_FIELD_ID = "purposeOfUsage";
  public static final String FINANCIAL_LEASING_SUPPLIER = "financialLeasingSupplier";

  // machinery collateral
  public static final String FORM_NUMBER = "formNumber";
  public static final String MANUFACTURER = "manufacturer";
  public static final String MACHINE_NUMBER = "machineNum";
  public static final String MACHINE_MODEL = "machineModel";
  public static final String MANUFACTURER_YEAR = "manufactureYear";
  public static final String CUSTOMER_ID = "custId";
  public static final String REVIEW_DATE = "reviewDate";
  public static final String REMARKS = "remarks";
  public static final String INSPECTION_AMOUNT_VALUE = "inspectionAmountValue";
  public static final String INSPECTOR_ID = "inspectorId";
  public static final String INSPECTION_DATE = "inspectionDate";
  public static final String OWNER_CIF_NUMBER = "ownerCifNumber";
  public static final String LOAN_BORROWER_TYPE = "borrowerType";

  // Collateral calculate
  public static final String AMOUNT_OF_ASSESSMENT = "amountOfAssessment";
  public static final String COLLATERAL_INFO = "collateralInfo";
  public static final String COLLATERAL_CONNECTION_RATE = "collateralConnectionRate";
  public static final String COLLATERAL_LOAN_AMOUNT = "loanAmount";

  // Form field properties
  public static final String ID = "id";
  public static final String FORM_FIELD_VALUE = "formFieldValue";
  public static final String LABEL = "label";
  public static final String TYPE = "type";
  public static final String VALIDATIONS = "validations";
  public static final String OPTIONS = "options";
  public static final String DISABLED = "disabled";

  public static final String USER_TASK_NAME_CREATE_COLLATERAL = "14. Барьцаа хөрөнгө үүсгэх";
  public static final String CREATE_COLLATERAL_TASK_NAME = "Барьцаа үүсгэх";

  public static final String COLLATERAL_LIST_USER_TASK_DEF_KEY = "user_task_collateral_list";
  public static final String COLLATERAL_TABLE_SPECIAL_FORM_KEY = "COLLATERAL_TABLE";
  public static final String COLLATERAL_CHECKED = "checked";
  public static final String COLLATERAL_AMOUNT_OF_ASSESSMENT = "amountOfAssessment";
  public static final String COLLATERAL_ACCOUNT = "collateralAccount";

  public static final String RELATED_USER_TASK_ID = "relatedUserTaskId";

  public static final String LOAN_DECISION_DOCUMENT_NAME = "Зээлийн шийдвэр";
  public static final String VEHICLE_REFERENCE_DOCUMENT_NAME = "Тээврийн хэрэгслийн лавлагааа";
  public static final String VEHICLE_REFERENCE_CO_BORROWER_DOCUMENT_NAME = "Тээврийн хэрэгслийн лавлагааа Хамтран";

  public static final String MONGOL_BANK_CUSTOMER_RELATED_INFO_DOCUMENT_NAME = "Холбогдох иргэний мэдээлэл";
  public static final String MONGOL_BANK_SHAREHOLDER_RELATED_INFO_DOCUMENT_NAME = "Хувь нийлүүлэгч иргэн";

  public static final String TENANT_ID_XAC = "xac";

  public static final String TEMPLATE_PATH_LOAN_DECISION = "Templates/Зээлийн шийдвэр.docx";
  public static final String TEMPLATE_PATH_LOAN_DECISION_WITH_COLLATERAL = "Templates/Зээлийн шийдвэр (барьцаатай).docx";
  public static final String TEMPLATE_PATH_MONGOL_BANK_CUSTOMER_RELATED_INFO = "Templates/Холбогдох иргэний мэдээлэл.docx";
  public static final String TEMPLATE_PATH_MONGOL_BANK_SHAREHOLDER_RELATED_INFO = "Templates/Хувь нийлүүлэгч иргэн.docx";

  public static final String DOCUMENT_INFO_ID_LOAN_DECISION = "10 З";
  public static final String SUB_CATEGORY_LOAN_DECISION_DOCUMENT = "01. Зээлийн шийдвэр";

  public static final String ACTIVITY_ID_CREATE_LOAN_ACCOUNT_WITH_COLLATERAL = "process_task_create_loan_account_with_collateral";
  public static final String ACTIVITY_ID_CALCULATE_LOAN_AMOUNT_AFTER_ACCOUNT_CREATION = "process_task_calculate_loan_amount_after_account_creation";
  public static final String ACTIVITY_ID_CREATE_COLLATERAL = "process_task_create_collateral";
  public static final String ACTIVITY_ID_COLLATERAL_LIST = "process_task_collateral_list";

  public static final String DEF_ID_ROOT_LOAN_AMOUNT = "ProcessTask_1rl8r0u";
  public static final String DEF_ID_LOAN_AMOUNT_AFTER_ROOT = "ProcessTask_0jq5jx6";
  public static final String DEF_ID_LOAN_AMOUNT_AFTER_COLL_LIST = "process_task_calculate_loan_amount_after_loan_account_creation";

  // Micro
  public static final String BALANCE_TOTALS_JSON = "balanceTotalsJson";
  public static final String SALE = "sale";
  public static final String OPERATION = "operation";
  public static final String ASSET = "asset";
  public static final String DEBT = "debt";

  public static final String BALANCE_TOTAL_INCOME_AMOUNT_STRING = "balanceTotalIncomeAmountString";
  public static final String BALANCE_TOTAL_INCOME_AMOUNT = "balanceTotalIncomeAmount";
  public static final String BALANCE_TOTAL_INCOME_PERCENT_STRING = "balanceTotalIncomePercentString";
  public static final String BALANCE_TOTAL_INCOME_PERCENT = "balanceTotalIncomePercent";

  public static final String REPORT_COVERAGE_PERIOD = "reportCoveragePeriod";

  public static final String ISO_DATE_FULL_FORMATTER = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
  public static final String ISO_DATE_FORMATTER = "yyyy-MM-dd'T'HH:mm:ss.SSS";
  public static final String ISO_DATE_FORMATTER_2 = "yyyy-MM-dd'T'00:00:00.000";
  public static final String SIMPLE_DATE_FORMATTER = "dd/MM/yyyy";
  public static final String ISO_SIMPLE_DATE_FORMATTER = "yyyy-MM-dd";

  public static final String DATE_POSTFIX = "Date";
  public static final String DATE_PREFIX = "date";
  public static final String UTC_8_ZONE = "UTC-8";
  public static final String READONLY = "readonly";

  public static final String EB_50_PRODUCT_CODE = "EB50";
  public static final String EF_50_PRODUCT_CODE = "EF50";
  public static final String EB_51_PRODUCT_CODE = "EB51";
  public static final String EB_50_PRODUCT_DESCRIPTION = "EB50-365-Цалингийн зээл-Иргэн";
  public static final String EF_50_PRODUCT_DESCRIPTION = "EF50-365-Ажиллагсадын хэрэглээний зээл";
  public static final String EB_51_PRODUCT_DESCRIPTION = "EB51-365-Цалингийн зээл-Иргэн-EMI";

  public static final String OWNER_NAMES_UDF_VARIABLE_ID = "BARITSAA HURUNGU UMCHLUGSDIIN NERS";

  public static final String TOTAL_COLLATERAL_AMOUNT_UDF_VAR_ID = "totalCollateralAmountUDF";
  public static final String INSURANCE_C_UDF_EXECUTION_VAR_ID = "insuranceCUDF";
  public static final String COLLATERAL_AMOUNT = "collateralAmount";
  public static final String PRODUCT_APPLICATION_CATEGORY_SMALL_MICRO = "SMALL_MICRO";

  public static final String EXCEL_MEDIA_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

  public static final String ORGANIZATION_REGISTER_NUMBER_REGEX = "^[0-9]+$";

  public static final String AUTHORIZATION = "Authorization";

  // Micro
  // Simple calculation field ids
  public static final String CALCULATE_SIMPLE_CALCULATION = "calculateSimpleCalculation";
  public static final String REPORTING_PERIOD_CASH = "reportingPeriodCash";
  public static final String CURRENT_ASSETS = "currentAssets";
  public static final String FIXED_ASSETS = "fixedAssets";
  public static final String SUPPLIER_PAY = "supplierPay";
  public static final String SHORT_TERM_PAYMENT = "shortTermPayment";
  public static final String LONG_TERM_PAYMENT = "longTermPayment";
  public static final String TOTAL_ASSET = "totalAsset";
  public static final String TOTAL_ASSET_STRING = "totalAssetString";
  public static final String REPORT_PERIOD = "reportPeriod";
  public static final String SALES_INCOME = "salesIncome";
  public static final String OTHER_INCOME = "otherIncome";
  public static final String COST_OF_GOODS = "costOfGoods";
  public static final String OPERATING_EXPENSES = "operatingExpenses";
  public static final String TAX_COSTS = "taxCosts";
  public static final String RENTAL_EXPENSES = "rentalExpenses";
  public static final String OTHER_EXPENSES = "otherExpense";
  public static final String NET_PROFIT = "netProfit";
  public static final String NET_PROFIT_STRING = "netProfitString";
  public static final String NET_BENEFIT = "netBenefit";

  // Balance calculation field ids
  public static final String CALCULATE_BALANCE_CALCULATION = "calculateBalanceCalculation";
  public static final String IS_COMPLETED_CUSTOMER_MAIN_INFO = "isCompletedCustomerMainInfo";
  public static final String LAST_CALCULATION_TYPE = "lastCalculationType";
  public static final String BALANCE = "Balance";
  public static final String SIMPLE = "Simple";

  //Mortgage

  //Business Calculation variable names
  public static final String BUSINESS_CALCULATION = "businessCalculation";
  public static final String OPERATION_COST = "operationCost";
  public static final String SOLD_GOODS = "soldGoods";
  public static final String ACTIVE_LOAN_PAYMENT = "activeLoanPayment";

  public static final String DOUBLE_FORM_FIELD_TYPE = "double";
  public static final String LONG_FORM_FIELD_TYPE = "long";
  public static final String BIG_DECIMAL_FORM_FIELD_TYPE = "BigDecimal";

  public static final String STRING_AS_EMPTY = "empty";
  public static final String INTERNET_BANK_USER_PROPERTY = "internetBankUser";

  public static final String ONLINE_SALARY_PRODUCT_CODE = "EB71";
  public static final String PREVIUOS_EBANK_USER_NAME = "ebank";

  // Mortgage
  // Loan Amount fields
  public static final String MAX_LOAN_AMOUNT_VALIDATION = "maxLoanAmountValidation";
  public static final String MAX_LOAN_AMOUNT = "maxLoanAmount";
  public static final String MAX_LOAN_TERM = "maxLoanTerm";
  public static final String CONDITION_MET = "conditionsMet";
  public static final String CONTRACT_LOAN_AMOUNT = "loanAmount";

  public static final String PROFIT_TYPE_REMMITANCE = "Гуйвуулгын орлого, бусад орлого";
  public static final String PROFIT_TYPE_COBORROWER = "Хамтран зээлдэгчийн орлого";

  //Organization Registration
  public static final String ORGANIZATION_TYPE_SALARY = "salaryOrganization";
  public static final String ORGANIZATION_TYPE_LEASING = "leasingOrganization";

  public static final String BRANCH_ORGANIZATION_REQUEST = "branch-organization-request";

  // Organization Request Table Header
  public static final String[] ORGANIZATION_REQUEST_TABLE_HEADER = { "№", "Хүсэлтийн Дугаар", "Овог,Нэр", "Регистр", "Байгууллагын төрөл", "Огноо",
      "Хэрэглэгч", "Салбар", "Төлөв" };
  public static final String[] LEASING_ORGANIZATION_REQUEST_TABLE_HEADER = { "№", "Регистрийн дугаар", "Салбарын дугаар", "Бүртгэлийн нэр", "Гэрээний дугаар",
      "Гэрээ байгуулсан огноо", "Гэрээний хугацаа", "Гэрээний дуусах огноо", "Хураамж", "Өмнөх гэрээний дугаар", "Хамтран ажиллагчийн төрөл",
      "үйл ажиллагааны чиглэлийн код", "үйл ажиллагааны чиглэл", "СИФ дугаар", "Улсын бүртгэлийн дугаар", "Байгуулагдсан огноо", "Хаяг", "Утас", "И-мэйл",
      "Ангилал", "Тайлбар /Нөхцөл/ шалтгаан", "Овог нэр", "Утас", "И-мэйл", "Тайлбар", "Банкинд төлөх хэлбэр", "Хувь / Хэмжээ", "Зээлийн хэмжээ",
      "Нийлүүлэгчид төлөх хугацаа", "Нийлүүлэгчид төлөх хувь", "Дансны дугаар", "Хүүгийн нөхцөл", "Хүүний хэмжээ", "Зээл олгосны шимтгэл",
      "Санхүү түрээс нийлүүлэгч эсэх", "Онлайнаар зээл олгох эсэх BNPL", "Терминал дугаар", "Сунгалтын хугацаа", "Сунгасан эсэх", "Сунгасан огноо", "Төлөв",
      "Анх хүсэлт үүсгэсэн хэрэглэгч", "Анх хүсэлт үүсгэсэн хадгалсан огноо", "Батлуулах товч дарсан хэрэглэгчийн ID", "Батлуулах товч дарсан огноо",
      "баталсан хэрэглэгчийн нэр", "баталсан огноо, цаг минут", "Сүүлд өөрчилсөн хэрэглэгчийн ID", "Сүүлд өөрчилсөн огноо", "Зассан тоо" };
  public static final String[] SALARY_ORGANIZATION_REQUEST_TABLE_HEADER = { "№", "Регистрийн дугаар", "Гэрээний дугаар", "CONTRACTNUMBER", "СИФ дугаар", "Салбарын дугаар",
      "Байгууллагын нэр", "FCNAME", "LOVNUMBER", "Дансны дугаар", "EXPOSURECATEGORY_CODE", "Үйл ажиллагааны чиглэл", "Байгуулагдсан огноо", "Нийт ажилтны тоо", "Холбогдох ажилтны нэр",
      "Утасны дугаар", "FORM4001", "Гэрээ байгуулсан огноо", "Гэрээний дуусах огноо", "MSTARTSALARY", "MENDSALARY", "Түлхүүр ажилтны хүү", "Хүүний хэмжээ", "Улсын бүртгэлийн дугаар",
      "Сунгасан огноо", "Банкинд төлөх алданги", "Байгууллагын төрөл", "Өмнөх гэрээний дугаар", "Цалингийн гүйлгээний шимтгэл", "CHARGEGLACCOUNT", "Зээлд хамрагдах эсэх",
      "RELEASEEMPNAME", "ADDITION_INFO", "Байгууллагын зэрэглэл", "RECORD_STAT", "AUTH_STAT", "ONCE_AUTH", "Хүүгийн нөхцөл", "ERATE_MAX", "Цалин тавих өдрүүд", "Цалин тавих өдрүүд", "Сард хэдэн удаа цалин тавих",
      "Гэрээний хугацаа", "Сунгасан эсэх", "Сунгасан огноо", "Сунгалтын хугацаа", "CCREATED_DATE", "Тохирсон тусгай", "ДАН регистр", "Дүүрэг", "Онлайнаар зээл", "CREATED_USERID", "CREATED_AT",
      "MAKER_ID", "MAKER_DT_STAMP", "CHECKER_ID", "CHECKER_DT_STAMP", "LAST_UPDATED_BY", "UPDATED_AT", "MOD_NO" };
  public static final Map<String, String> ORGANIZATION_REQUEST_TYPE = new HashMap<String, String>()
  {{
    put(BRANCH_ORGANIZATION_REQUEST, "Салбарын хүсэлт");
    put(ORGANIZATION_TYPE_SALARY, "Цалингийн байгууллага");
    put(ORGANIZATION_TYPE_LEASING, "Лизингийн байгууллага");
  }};

  public static final Map<String, String> ORGANIZATION_REQUEST_STATE = new HashMap<String, String>()
  {{
    put("NEW", "ШИНЭ");
    put("STARTED", "СУДЛАГДАЖ БУЙ");
    put("ORG_REJECTED", "БАНК ТАТГАЛЗСАН");
    put("CUST_REJECTED", "ХАРИЛЦАГЧ ТАТГАЛЗСАН");
    put("REJECTED", "ЗАХИРАЛ-БАНК ТАТГАЛЗСАН");
    put("RETURNED", "БУЦААСАН");
    put("CONFIRMED", "БАТАЛСАН");
  }};

  public static final String ONLINE_SALARY_PROCESS_TYPE = "onlineSalary";
  public static final String LINE_LOAN_TYPE = "Зээлийн шугам";
  public static final String PAID_LOAN_STATUS = "Төлөгдөж дууссан";
  public static final String CO_BORROWER = "coborrower";
  public static final String LOAN_OFFICER = "loanOfficer";
  public static final String STATE = "State";
  public static final String MB_HAS_SESSION = "mbHasSession";
  public static final String ACTIVE_LOAN_ACCOUNT_LIST = "activeLoanAccountList";

  //PUBLISHER_DOCUMENT
  public static final String ONLINE_SALARY_LOAN_REPAYMENT_BASE64 = "loanRepaymentFile";
  public static final String ONLINE_SALARY_LOAN_CONTRACT_BASE64 = "loanContractFile";
  // LOAN ACCOUNT
  public static final String FREQUENCY = "frequency";
  public static final String IS_SALARY_ORGANIZATION = "isSalaryOrganization";
  public static final String ERATE_MAX = "erate_max";
  public static final String ERATE = "erate";
  public static final String FAMILY_INCOME = "family_income";
  public static final String FAMILY_INCOME_STRING = "family_income_string";

  /* Direct online loan constants*/
  public static final String PRODUCT_APPLICATION_CATEGORY_ONLINE_SALARY = "ONLINE_SALARY";
  public static final String LOAN_INFO = "loanInfo";
  public static final String DAN_INFO = "danInfo";

  public static final String XAC_BRANCH_ID = "BranchID";
  public static final String XAC_ACCOUNT_ID = "AccountID";
  public static final String XAC_CURRENCY = "Currency";
  public static final String XAC_AMOUNT = "Amount";
  public static final String XAC_FROM_BRANCH_ID = "FromBranchID";
  public static final String XAC_FROM_ACCOUNT_ID = "FromAccountID";
  public static final String XAC_DESCRIPTION = "Description";
  public static final String XAC_CLOSING_LOAN_AMOUNT = "ClosingLoanAmount";
  public static final String XAC_BALANCE = "Balance";

  public static final String HAS_ACTIVE_LOAN_ACCOUNT = "hasActiveLoanAccount";
  public static final String TOTAL_CLOSING_AMOUNT = "totalClosingAmount";
  public static final String TOTAL_BALANCE = "totalBalance";
  public static final String FAILED_ACCOUNT_LIST = "failedAccountList";
  public static final String ERROR_CAUSE = "errorCause";
  public static final String DAN_REGISTER = "danRegister";

  public static final String DEFAULT_ACCOUNT = "defaultAccount";
  public static final String DEFAULT_BRANCH = "settleBranch";
  public static final String DIRECT_ONLINE_PROCESS_TYPE_CATEGORY = "DIRECT_ONLINE";
  public static final String DIRECT_ONLINE_PROCESS_TYPE_ID = "onlineSalary";
  public static final String DIRECT_ONLINE_SALARY_CONFIRM_TIME_RANGE = "directOnline.salary.confirm.time.range";
  public static final String DIRECT_ONLINE_SALARY_DELETE_TIME_RANGE = "directOnline.salary.delete.time.range";

  public static final String INTEREST_RATE_UC = "InterestRate";
  public static final String TERM_UC = "Term";
  public static final String CHARGE_UC = "Charge";
  public static final String DIRECT_ONLINE_CHANNEL = "directOnline.channel";

  /* Loan calculation */
  public static final String MORTGAGE_LOAN_MONTH = "mortgage.loan.month";

  // LDMS
  // Document name
  public static final String CATEGORY_LOAN_APPLICATION = "01. Өргөдөл";
  public static final String APPLICATION_INFO_ID_LOAN = "01";
  public static final String LOAN_APPLICATION_NAME = "Зээлийн өргөдөл";
  public static final String DIRECT_ONLINE_LOAN_CONTRACT_NAME = "Онлайн зээлийн гэрээ";
  public static final String SUB_CATEGORY_LOAN_APPLICATION = "01. Өргөдөл, хүсэлт";
  public static final String LOAN_APPLICATION_NAME_PDF = "Зээлийн өргөдөл.pdf";
  public static final String LOAN_DECISION_NAME_PDF = "Зээлийн шийдвэр.pdf";
  public static final String LOAN_PAYMENT_SCHEDULE_NAME_PDF = "Зээлийн эргэн төлөлтийн хуваарь.pdf";
  public static final String LOAN_CONTRACT_NAME_PDF = "Онлайн зээлийн гэрээ.pdf";
  public static final String SALARY_ENQUIRE_NAME_PDF = "Нийгмийн даатгалын лавлагаа.pdf";
  public static final String DOCUMENT_NAME_MONGOL_BANK_ENQUIRE_PDF = "Монгол банкны лавлагаа.pdf";

  public static final String TEMPLATE_PATH_LOAN_APPLICATION = "Templates/Зээлийн өргөдөл.docx";
  public static final String TEMPLATE_PATH_ONLINE_LOAN_DECISION = "Templates/Онлайн зээлийн шийдвэр.docx";
  public static final String DIRECT_ONLINE_LOAN_DECISION_DOC_NAME = "Онлайн зээлийн шийдвэр";
  public static final String TEMPLATE_PATH_NDSH_ENQUIRE = "Templates/social_insurance_payment_info_12.docx";
  public static final String TEMPLATE_PATH_BNPL = "Templates/BNPL Зээлийн шийдвэр.docx";

  // employee loan product
  public static final String EMPLOYEE_LOAN_PRODUCT_ID = "employee.loan.product-id";

  //bnpl

  public static final String TEMPLATE_PATH_BNPL_LOAN_APPLICATION = "Templates/BNPL Зээлийн өргөдөл.docx";
  public static final String LOAN_APPLICATION_NAME_BNPL = "BNPL Зээлийн өргөдөл";

  public static final String BNPL = "BNPL";
  public static final String PROCESS_INSTANCE_ID = "processInstanceId";
  public static final String BNPL_PROCESS_TYPE_ID = "bnplLoan";
  public static final String BNPL_LOAN_AMOUNT = "bnplLoanAmount";
  public static final String DEBT_INCOME_ISSUANCE_PERCENT = "debtIncomeIssuancePercent";
  public static final String INVOICE_NUM = "invoiceNum";
  public static final String INVOICE_STATE = "invoiceState";
  public static final String INVOICE_AMOUNT = "invoiceAmount";
  public static final String INVOICE_AMOUNT_75 = "invoiceAmount75";
  public static final String LDMS_CATEGORY = "category";
  public static final String LDMS_SUB_CATEGORY = "subCategory";

  public static final String DEFAULT_PARAM_ENTITY_NAME = "Default";
  public static final String BNPL_LOAN_DEFAULT_PARAM_PROCESS_TYPE = "BnplLoan";
  public static final String BNPL_CONTRACT_BASE64 = "bnplContractFile";
  public static final String BNPL_CONTRACT_NAME_PDF = "BNPL Зээлийн гэрээ.pdf";
  public static final String BNPL_REPAYMENT_BASE64 = "bnplRepaymentFile";
  public static final String BNPL_REPORT_BASE64 = "bnplReportFile";
  public static final String BNPL_REPORT_NAME_PDF = "BNPL Зээлийн мэдээллийн хуудас.pdf";
  public static final String TERMINAL_ID = "terminalId";
  public static final String BNPL_CONTRACT_AS_BASE64 = "BNPLContractAsBase64";
  public static final String INSTANT_LOAN_CONTRACT_AS_BASE64 = "InstantLoanContractAsBase64";

  // Organization registration
  public static final String CONTRACT_NUMBER = "contractNumber";
  public static final String SALARY_REQUEST = "salaryOrganization";

  public static final String TEMPORARY_STATE = "temporaryState";
  public static final String CANCEL_ORGANIZATION_STATE = "Гэрээ цуцалсан";
  public static final String TO_CONFIRM_ORGANIZATION_STATE = "Батлагдсан";

  public static final String REPAYMENT_SCHEDULE_PROJECT = "repaymentSchedule.project";
  //  Salary calculation update
  public static final String SALARY_CALCULATION_MAX_INCOME = "maxIncome";
  public static final String SALARY_CALCULATION_TAX_PERCENT = "taxPercent";
  public static final String SALARY_CALCULATION_TAX_ADDED = "taxPercentAdded";
  public static final String SALARY_CALCULATION_CONSTANT_TAX = "constantTax";

  //Instant Loan
  public static final String INSTANT_LOAN_PROCESS_TYPE_CATEGORY = "INSTANT_LOAN";
  public static final String INSTANT_LOAN_PROCESS_TYPE_ID = "instantLoan";
  public static final String INSTANT_LOAN_CONFIRM_TIME_RANGE = "instant.loan.confirm.time.range";
  public static final String INSTANT_LOAN_DELETE_TIME_RANGE = "instant.loan.delete.time.range";
  public static final String INSTANT_LOAN_REPAYMENT_BASE64 = "instantLoanRepaymentFile";
  public static final String INSTANT_LOAN_CONTRACT_BASE64 = "instantLoanContractFile";
  public static final String INSTANT_LOAN_REPORT_BASE64 = "instantLoanReportFile";
  public static final String INSTANT_LOAN_CONTRACT_NAME_PDF = "INSTANT LOAN Зээлийн гэрээ.pdf";
  public static final String INSTANT_LOAN_REPORT_NAME_PDF = "INSTANT LOAN Зээлийн мэдээллийн хуудас.pdf";
  public static final String INTEREST_AMOUNT = "InterestAmount";
  public static final String ACC_CURRENCY_CC = "accCurrency";
  public static final String ISDmd = "ISDmd";
  public static final String ACTION_TYPE = "actionType";
  public static final String EXTEND = "extend";
  public static final String TOPUP = "topup";
  public static final String START = "start";

  public static final String REQUEST_ID = "requestId";
  public static final String INSTANT_LOAN_CHO_DEF_BRANCH  = "instant_loan_cho_def_branch";

  public static final String INTEREST_DUE = "InterestDue";
  public static final String PENALTY_AMOUNT = "PenaltyAmount";

  // Online leasing parameters
  public static final String ONLINE_LEASING_DEFAULT_PARAM_PROCESS_TYPE = "OnlineLeasing";
  public static final String ONLINE_LEASING_PROCESS_TYPE_CATEGORY = "ONLINE_LEASING";
  public static final String ONLINE_LEASING_PROCESS_TYPE_ID = "onlineLeasing";
  public static final String ONLINE_LEASING_1_APPLICATION_CATEGORY = "OnlineLeasing1";
  public static final String ONLINE_LEASING_2_APPLICATION_CATEGORY = "OnlineLeasing2";
  public static final String TRACK_NUMBER = "trackNumber";
  public static final String KEY_FIELD_1 = "keyField1";
  public static final String ONLINE_LEASING_CONFIRM_TIME_RANGE = "online.leasing.confirm.time.range";
  public static final String ONLINE_LEASING_DELETE_TIME_RANGE = "online.leasing.delete.time.range";
  public static final String ONLINE_LEASING_ORGANIZATION_DEFAULT_ACCOUNT = "online.leasing.organization.default.account";
  public static final String PRODUCT_CATEGORY = "productCategory";
  public static final String COLLECTED_AMOUNT = "CollectedAmount";
  public static final String CONFMISC10 = "ConfMisc10";
  public static final String MIN_AMOUNT = "MinAmount";
  public static final String PROCESS_TYPE = "ProcessTypeId";
  public static final String EXPIRY = "Expiry";
  public static final String INTEREST = "Interest";
  public static final String ONLINE_LEASING_REPAYMENT_BASE64 = "onlineLeasingRepaymentFile";
  public static final String ONLINE_LEASING_CONTRACT_BASE64 = "onlineLeasingContractFile";
  public static final String ONLINE_LEASING_REPORT_BASE64 = "onlineLeasingReportFile";
  public static final String ONLINE_LEASING_CONTRACT_NAME_PDF = "Онлайн лизингийн гэрээ.pdf";
  public static final String ONLINE_LEASING_REPORT_NAME_PDF = "Зээлийн мэдээллийн хуудас.pdf";

  public static final String NO_CHANGE_BRANCH_PROCESS_TYPES = "no.change.branch.process.types";
  public static final String TEMPLATE_PATH_CALCULATION_INFO = "Templates/Бизнесийн зээлийн тооцоолол.docx";
  public static final String CALCULATION_INFO_NAME = "Бизнесийн зээлийн тооцоолол";

  public static final String CATEGORY_CALCULATION_INFO_NAME = "09. Судалгаа";
  public static final String SUB_CATEGORY_CALCULATION_INFO_NAME = "03. Санал тооцоолол";
  public static final String DOCUMENT_INFO_ID_CALCULATION_INFO_NAME = "09.03";
  public static final String[] CONSUMPTION_LOAN_CALCULATION_FIELDS = new String[] { INTEREST_RATE, TERM, LOAN_GRANT_DATE, FIRST_PAYMENT_DATE, GRANT_LOAN_AMOUNT_STRING, "debtIncomeBalanceString",
      DEBT_INCOME_INSURANCE_STRING, LOAN_APPROVAL_AMOUNT, FIXED_ACCEPTED_LOAN_AMOUNT, CONFIRM_LOAN_AMOUNT, MONTH_PAYMENT_ACTIVE_LOAN };
  public static final String[] BUSINESS_LOAN_CALCULATION_FIELDS =  new String[] {
     LOAN_TERM, INTEREST_RATE, "loanProductType", "areasOfActivity", "hasMortgage", "purposeOfLoan", DEBT_TO_SOLVENCY_RATIO_STRING,
          DEBT_TO_INCOME_RATIO_STRING, DEBT_TO_INCOME_ASSET_STRING, CURRENT_ASSETS_RATIO_STRING, "collateralProvidedAmountString", GRANT_LOAN_AMOUNT, ACCEPTED_LOAN_AMOUNT };
  public static final String[] MORTGAGE_LOAN_CALCULATION_FIELDS = new String[] { LOAN_PRODUCT,  LOAN_TERM, INTEREST_RATE, "borrowersIncome", "borrowersIncomePercent", "coBorrowersIncome",
      "coBorrowersIncomePercent", "transferIncome", "transferIncomePercent", "netIncome", "netIncomePercent", "totalFunding", "housingFinancing", "borrowerFinances",
    "borrowerFinancesPercent", "monthlyPayment", "borrowerFinanceGaragePercent", ACCEPTED_LOAN_AMOUNT, CONDITION_MET, "propertyLocation", "loanMonthlyPayment",
    LOAN_AMOUNT, "authorize" };
}

