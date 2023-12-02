// Button action
export const CLOSE = 'ХААХ';
export const ENABLED_TASK_DIALOG_TEXT = 'үйлдлийг эхлүүлэх үү?';
export const ACTIVE_TASK_DIALOG_TEXT = 'хуудсыг нээх үү? Одоо бөглөж буй талбарын мэдээллүүд хадгалагдахгүйг анхаарна уу!';
export const CREATE_COLLATERAL_CONFIRM_DIALOG_TEXT = 'Та барьцаа хөрөнгө шинээр үүсгэхдээ итгэлтэй байна уу?';
export const CALCULATE = 'calculate';

// field type
export const BIG_DECIMAL = 'BigDecimal';

// submit form
export const SPECIAL_FORMS = 'specialForms';

// regex
export const TASK_NAME_REGEX = /(\d{1,})+(.)/;

// Task Name
export const MONGOLBANK = 'Монгол банк лавлагаа';
export const DOWNLOADKHUR = 'ХУР лавлагаа';
export const DOWNLOADKHUR_COBORROWER = '(Хамтран) ХУР лавлагаа';
export const MONGOLBANK_COBORROWER = '(Хамтран) Монгол банк лавлагаа';
export const CRITERIA = 'Анхан шатны шалгуур';
export const SCORING = 'Скоринг';
export const SALARY_CALCULATION = 'Цалингийн тооцоолол';
export const RATE = 'Хүү тооцоолох';
export const LOAN_CALCULATION = 'Зээлийн хэмжээ тооцох';
export const REMOVE_COBORROWER = 'Хамтран хасах';
export const LOAN_SEND = 'Зээл шийдвэрлүүлэхээр илгээх - ZSI';
export const LOAN_PERMIT = 'Зээл шийдвэрлэх';
export const LOAN_ACCOUNT = 'Зээлийн данс үүсгэх';
export const CREATE_COLLATERAL_LOAN_ACCOUNT = 'Барьцаатай зээлийн данс үүсгэх';
export const LOAN_CONTRACT = 'Зээлийн гэрээ бэлтгэх';
export const LOAN_ATTACHMENT_CONTRACT = 'Зээлийн хавсралт гэрээ бэлтгэх';
export const CO_OWNER_INFORMATION = 'Хамтран өмчлөгчийн мэдээлэл оруулах';
export const FIDUCIARY_ASSET = 'Фидуцийн хөрөнгийн мэдээлэл оруулах';
export const COLLATERAL_REAL_ESTATE = 'Барьцаа хөрөнгийн жагсаалт харах';
export const COLLATERAL_LIST = 'Барьцаа хөрөнгийн жагсаалт';
export const CREATE_COLLATERAL = 'Барьцаа үүсгэх';
export const CREATE_COLLATERAL_TASK_NAME = 'Барьцаа хөрөнгө үүсгэх';
export const STANDARD_HOUSING_VALUATION = 'Орон сууцны тооцоолол';

// Task Def Key
export const TASK_DEF_CONSUMPTION_MONGOL_BANK = 'Task_1tzwhk1';
export const TASK_DEF_CONSUMPTION_CO_BORROWER_MONGOL_BANK = 'UserTask_01adzfr';
export const TASK_DEF_CREATE_COLLATERAL_LOAN_ACCOUNT = 'user_task_create_account_with_collateral';
export const TASK_DEF_COLLATERAL_LIST = 'user_task_collateral_list';
export const TASK_DEF_VIEW_COLLATERAL_LIST = 'user_task_view_loan_collateral';
export const TASK_DEF_CREDIT_LINE_VIEW_COLLATERAL_LIST = 'user_task_credit_line_view_loan_collateral';
export const TASK_DEF_CREATE_LOAN_ACCOUNT  = 'Activity_1na5uix';
export const TASK_DEF_LOAN_AMOUNT_CALCULATION = 'UserTask_0r0le5y';
// Micro
export const TASK_DEF_MICRO_MONGOL_BANK = 'user_task_micro_mongol_bank';
export const TASK_DEF_MICRO_MONGOL_BANK_EXTENDED = 'user_task_micro_mongol_bank_extended';
export const TASK_DEF_MICRO_MONGOL_BANK_NEW_CORE = 'user_task_micro_mongol_bank_new_core';
export const TASK_DEF_MICRO_CO_BORROWER_MONGOL_BANK = 'user_task_micro_co_borrower_mongol_bank';
export const TASK_DEF_MICRO_LOAN_DOWNLOAD_KHUR = 'user_task_micro_loan_download_from_khur';
export const TASK_DEF_MICRO_LOAN_DOWNLOAD_KHUR_CO_BORROWER = 'user_task_micro_loan_download_from_khur_coBorrower';
export const TASK_DEF_MICRO_SIMPLE_CALCULATION = 'user_task_micro_simple_calculation';
export const TASK_DEF_MICRO_BALANCE_CALCULATION = 'user_task_micro_balance_calculation';
export const TASK_DEF_MICRO_LOAN_SCORING = 'user_task_micro_loan_scoring';
export const TASK_DEF_MICRO_LOAN_CALCULATION = 'user_task_calculate_micro_loan_amount';
export const TASK_DEF_MICRO_CREATE_COLLATERAL_LOAN_ACCOUNT = 'user_task_micro_create_account_with_collateral';
// Mortgage
export const TASK_DEF_MORTGAGE_BUSINESS_CALCULATION = 'user_task_mortgage_business_calculation';
export const TASK_DEF_MORTGAGE_SALARY_CALCULATION = 'user_task_mortgage_salary_calculation';
export const TASK_DEF_MORTGAGE_LOAN_CALCULATION = 'user_task_calculate_mortgage_loan_amount';
export const TASK_DEF_MORTGAGE_SALARY_LOAN_SCORING = 'user_task_mortgage_salary_loan_scoring';
export const TASK_DEF_MORTGAGE_BUSINESS_LOAN_SCORING = 'user_task_mortgage_business_loan_scoring';
export const TASK_DEF_MORTGAGE_GENERATE_LOAN_DECISION = 'user_task_mortgage_generate_loan_decision';

// Loan contract
export const TASK_DEF_C0_OWNER_CONTRACT = 'user_task_co_owner_contract';
export const TASK_DEF_EMPLOYEE_CONSUMPTION_CONTRACT = 'user_task_employee_consumption_loan';


// Default 'Тийм', 'Үгүй'
export const YES = 'Тийм';
export const NO = 'Үгүй';
export const BANK_CONDITION_HOUSE = 'Банкны нөхцөлтэй орон сууц';
export const BANK_LIST = [
  {id: 'ХасБанк', value: 'ХасБанк'}, {id: 'Ариг банк', value: 'Ариг банк'}, {id: 'Богд банк', value: 'Богд банк'},
  {id: 'Голомт банк', value: 'Голомт банк'}, {id: 'Капитрон банк', value: 'Капитрон банк'},
  {id: 'Кредит банк', value: 'Кредит банк'}, {id: 'Төрийн банк', value: 'Төрийн банк'},
  {id: 'Төрийн сан', value: 'Төрийн сан'}, {id: 'Тээвэр хөгжлийн банк', value: 'Тээвэр хөгжлийн банк'},
  {id: 'Улаанбаатар хотын банк', value: 'Улаанбаатар хотын банк'},
  {id: 'Үндэсний хөрөнгө оруулалтын банк', value: 'Үндэсний хөрөнгө оруулалтын банк'},
  {id: 'Хаан банк', value: 'Хаан банк'}, {id: 'Худалдаа хөгжлийн банк', value: 'Худалдаа хөгжлийн банк'},
  {id: 'Чингис хаан банк', value: 'Чингис хаан банк'}
];

// Salary calculation form fields
export const SALARY_FIELDS = [
  {
    id: 'ndsh', formFieldValue: {defaultValue: NO, valueInfo: ''}, label: 'НДШ чөлөөлөх эсэх', type: 'string', validations: [],
    options: [{id: 'option1', value: NO}, {id: 'option2', value: YES}], disabled: false, context: 'string', required: false
  },
  {
    id: 'emd', formFieldValue: {defaultValue: NO, valueInfo: ''}, label: 'ЭМД чөлөөлөх эсэх', type: 'string', validations: [],
    options: [{id: 'option1', value: NO}, {id: 'option2', value: YES}], disabled: false, context: 'string', required: false
  },
  {
    id: 'hasMortgage', formFieldValue: {defaultValue: NO, valueInfo: ''},
    label: 'Орон сууцны зээлтэй эсэх', type: 'string', validations: [],
    options: [
      {id: 'option1', value: NO},
      {id: 'option2', value: YES},
      {id: 'option3', value: BANK_CONDITION_HOUSE}], disabled: false, context: 'string', required: false
  }
];
export const SALARY_RESULT_FORM = [
  {
    id: 'averageSalaryAfterTax', formFieldValue: {defaultValue: 0, valueInfo: ''}, label: 'НД баталгаажсан цалин',
    type: 'BigDecimal', validations: [], options: [], disabled: true, context: 'BigDecimal', required: false
  },
  {
    id: 'averageSalaryBeforeTax', formFieldValue: {defaultValue: 0, valueInfo: ''}, label: 'Татварын өмнөх цалин',
    type: 'BigDecimal', validations: [], options: [], disabled: true, context: 'BigDecimal', required: false
  }
];
export const MONTH_NAMES = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
  'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'
];

// Task form field
export const TERM = 'term';
export const LOAN_PRODUCT = 'loanProduct';
export const INTEREST_DATE = 'interestRate';
export const FIRST_PAYMENT_DATE = 'firstPaymentDate';
export const SANCTIONED_BY = 'sanctionedBy';
export const LOAN_PURPOSE = 'loanPurpose';
export const ADDITIONAL_SPECIAL_CONDITION = 'additionalSpecialCondition';


export const MONGOL_BANK_FORM_FIELDS = [
  ['legalStatus', 'registerNumber'],
  ['mbFirstName', 'mbOccupancy', 'mbLastName', 'mbProfession', 'mbLegalState', 'mbFamilyName', 'mgRegisterNumber', 'mbFamilyLastName',
    'mbIdNumber', 'mbFamilyRegisterNumber', 'mbBirth', 'mbNumberOfFamilyMember', 'mbAddress', 'mbJoblessNumberOfFamilyMember']
];

export const LOAN_CALCULATION_FORM_FIELDS = [
  ['incomeType', 'amount', 'monthPaymentActiveLoan', 'processTypeName', TERM, 'loanGrantDate', LOAN_PRODUCT, INTEREST_DATE,
    FIRST_PAYMENT_DATE, 'repaymentType', 'incomeAmountCoBorrower', 'debtIncomeBalanceString', 'hasCollateral', 'collateralAmount',
    'loanApprovalAmount'],
  ['salaryAmountString', 'totalIncomeAmountString', 'debtIncomeIssuanceString', 'grantLoanAmountString',
    'fixedAcceptedLoanAmount', 'confirmLoanAmount'],
];

export const STANDARD_HOUSING_VALUATION_FIELDS = [
  ['purposeOfAsset', 'totalArea', 'khoroolol', 'ownersName', 'detailedAddress', 'apartmentNumber', 'fullName', 'what3words',
    'standartPrice', 'cifNumber', 'district', 'totalRating', 'numberOfRooms', 'Khoroo'],
  ['userName', 'dateAppartmentCalculation'],
  ['branchNumber', 'commentary']
];

export const RATE_FORM_FIELDS = [
  ['total_years_worked', 'organization_cif', 'key_worker'],
  ['loan_period_string', 'resourceString', 'interest_rate_string']
];

export const SCORING_FORM_FIELDS = [
  ['xacspanDate', 'sector', 'workspan', 'gender_input', 'family_income_string', 'jobless_members'],
  ['score', 'scoring_level_risk']
];

export const LOAN_ACCOUNT_FORM_FIELDS = [
  [LOAN_PRODUCT, 'fixedAcceptedLoanAmountString', 'loanAccountNumber'],
  ['numberOfPayments', 'yearlyInterestRateString', 'accountBranchNumber', 'frequency', 'depositInterestRateString',
    'currentAccountNumber', 'dayOfPayment', 'fees', FIRST_PAYMENT_DATE],
  ['AccountFreeCode2', 'AccountFreeCode3', 'TypeOfAdvance', 'BorrowerCategoryCode', 'FREE_CODE_4', 'FREE_CODE_5', 'FREE_CODE_6',
    'FREE_CODE_7', 'FREE_CODE_8', 'FREE_CODE_9', 'FREE_CODE_10', 'NatureOfAdvance', 'CustomerIndustryType', 'PurposeOfAdvance', 'AccountFreeCode1'],
  [ADDITIONAL_SPECIAL_CONDITION]
];

export const CRITERIA_FORM_FIELDS = [
  ['fullName', 'riskyCustomerValue', 'loanClassName']
];

export const LOAN_SEND_FORM_FIELDS = [
  ['receivers'], ['loanCommentExplanation'], ['loanDecisionReason']
];

export const LOAN_PERMIT_FORM_FIELDS = [
  ['sentUser', 'receivedUser', 'loanDecision'], ['loanDecisionReason'], ['loanCommentExplanation']
];
export const LOAN_ATTACHMENT_CONTRACT_FIELDS = [
  ['loanProductDescription', 'fixedAcceptedLoanAmount', 'loanAccountNumber'],
  ['contractID', 'isTestifyByCollateral', 'rightsOfThirdPerson', 'typeOfAttachment', 'optionOfDutiesPerformance', 'noteRightsOfThirdPerson'],
  [
    'fullNameOfМortgagor0', 'registerNumberOfМortgagor0', 'ubIDOfМortgagor0', 'addressOfМortgagor0', 'phoneNumberOfМortgagor0',
    'emailAddressOfМortgagor0', 'occupationOfМortgagor0', '2008а0', '2008b0',
    'fullNameOfМortgagor1', 'registerNumberOfМortgagor1', 'ubIDOfМortgagor1', 'addressOfМortgagor1', 'phoneNumberOfМortgagor1',
    'emailAddressOfМortgagor1', 'occupationOfМortgagor1', '2008а1', '2008b1',
    'fullNameOfМortgagor2', 'registerNumberOfМortgagor2', 'ubIDOfМortgagor2', 'addressOfМortgagor2', 'phoneNumberOfМortgagor2',
    'emailAddressOfМortgagor2', 'occupationOfМortgagor2', '2008а2', '2008b2',
    'fullNameOfМortgagor3', 'registerNumberOfМortgagor3', 'ubIDOfМortgagor3', 'addressOfМortgagor3', 'phoneNumberOfМortgagor3',
    'emailAddressOfМortgagor3', 'occupationOfМortgagor3', '2008а0', '2008b3',
    'fullNameOfМortgagor4', 'registerNumberOfМortgagor4', 'ubIDOfМortgagor4', 'addressOfМortgagor4', 'phoneNumberOfМortgagor4',
    'emailAddressOfМortgagor4', 'occupationOfМortgagor4', '2008а4', '2008b4',
  ],
  [
    'fullNameOfGuarantor0', 'registerNumberOfGuarantor0', 'ubIDofGuarantor0', 'addressOfGuarantor0', 'businessAddressOfGuarantor0',
    'phoneNumberOfGuarantor0', 'representiveLegalEntityName0', 'occupationOfGuarantor0',
    'fullNameOfGuarantor1', 'registerNumberOfGuarantor1', 'ubIDofGuarantor1', 'addressOfGuarantor1', 'businessAddressOfGuarantor1',
    'phoneNumberOfGuarantor1', 'representiveLegalEntityName1', 'occupationOfGuarantor1',
    'fullNameOfGuarantor2', 'registerNumberOfGuarantor2', 'ubIDofGuarantor2', 'addressOfGuarantor2', 'businessAddressOfGuarantor2',
    'phoneNumberOfGuarantor2', 'representiveLegalEntityName2', 'occupationOfGuarantor2',
    'fullNameOfGuarantor3', 'registerNumberOfGuarantor3', 'ubIDofGuarantor3', 'addressOfGuarantor3', 'businessAddressOfGuarantor3',
    'phoneNumberOfGuarantor3', 'representiveLegalEntityName3', 'occupationOfGuarantor3',
    'fullNameOfGuarantor4', 'registerNumberOfGuarantor4', 'ubIDofGuarantor4', 'addressOfGuarantor4', 'businessAddressOfGuarantor4',
    'phoneNumberOfGuarantor4', 'representiveLegalEntityName4', 'occupationOfGuarantor4',
  ]
];

export const LOAN_ATTACHMENT_CONTRACT_SUBTITLE = ['', '', 'Үүрэг хүлээгч/Барьцаалуулагчийн мэдээлэл', 'Барьцаалуулагч/Батлан даагчийн мэдээлэл'];

export const CO_OWNER_INFORMATION_FIELDS = [
  ['loanProductDescription', 'fixedAcceptedLoanAmount', 'loanAccountNumber'],
  ['contractNumber', 'loanAttachmentNumber', 'dateOfLoanAgreement', 'typeOfLoanAgreement', 'collateralListNumber', 'attachment'],
  [
    'fullNameCoOwner0', 'birthCertificateCoOwner0', 'ageGroupCoOwner0', 'registerCoOwner0', 'addressCoOwner0', 'attachmentCoOwner0',
    'fullNameCoOwner1', 'birthCertificateCoOwner1', 'ageGroupCoOwner1', 'registerCoOwner1', 'addressCoOwner1', 'attachmentCoOwner1',
    'fullNameCoOwner2', 'birthCertificateCoOwner2', 'ageGroupCoOwner2', 'registerCoOwner2', 'addressCoOwner2', 'attachmentCoOwner2',
    'fullNameCoOwner3', 'birthCertificateCoOwner3', 'ageGroupCoOwner3', 'registerCoOwner3', 'addressCoOwner3', 'attachmentCoOwner3',
    'fullNameCoOwner4', 'birthCertificateCoOwner4', 'ageGroupCoOwner4', 'registerCoOwner4', 'addressCoOwner4', 'attachmentCoOwner4',
  ],
  ['dateOfLoanAgreementForCoOwner', 'collateralAttachmentNumber', 'attachmentNumber']
];

export const CO_OWNER_INFORMATION_SUBTITLE = ['', '', 'Хамтран өмчлөгчийн мэдээлэл', 'Дараачийн барьцааны \nгэрээний нөхцөл /2011/'];

export const COLLATERAL_REAL_ESTATE_FIELDS = [
  ['loanProductDescription', 'fixedAcceptedLoanAmount', 'loanAccountNumber'],
  ['typeOfCollateral', 'customerName', 'attachmentType', 'choosingCollateral0', 'position', 'dateOfContract',
    'loanAttachmentNumber', 'interpretationOfTheSignature', 'attachmentNumber'],
  [
    'choosingCollateral0',
    'choosingCollateral1',
    'choosingCollateral2',
    'choosingCollateral3',
    'choosingCollateral4'
  ]
];

export const COLLATERAL_REAL_ESTATE_SUBTITLE = ['', '', ''];

export const COLLATERAL_FIELDS = [
  {
    id: 'collateralAmount', formFieldValue: {defaultValue: 0, valueInfo: ''}, label: 'Нийт барьцааны дүн', type: 'BigDecimal', validations: [],
    options: [], disabled: false, context: 'BigDecimal', required: false
  },
  {
    id: 'LTVField', formFieldValue: {defaultValue: 0, valueInfo: ''}, label: 'LTV', type: 'String', validations: [],
    options: [], disabled: false, context: 'String', required: false
  },
  {
    id: 'loanApprovalAmount', formFieldValue: {defaultValue: 0, valueInfo: ''}, label: 'Зээл зөвшөөрөх хэмжээ', type: 'BigDecimal', validations: [],
    options: [], disabled: false, context: 'BigDecimal', required: false
  },
  {
    id: 'lackedAmountField', formFieldValue: {defaultValue: 0, valueInfo: ''}, label: 'Дутаж байгаа барьцаа', type: 'BigDecimal', validations: [],
    options: [], disabled: false, context: 'BigDecimal', required: false
  },
];


export const FIDUCIARY_ASSET_FIELDS = [
  ['loanProductDescription', 'fixedAcceptedLoanAmount', 'loanAccountNumber'], ['contractID'],
  [
    'fullName0', 'registerNumber0', 'ubNumber0', 'address0', 'businessAddress0', 'phoneNumber0', 'representativeName0', 'occupancy0',
    'fullName1', 'registerNumber1', 'ubNumber1', 'address1', 'businessAddress1', 'phoneNumber1', 'representativeName1', 'occupancy1',
    'fullName2', 'registerNumber2', 'ubNumber2', 'address2', 'businessAddress2', 'phoneNumber2', 'representativeName2', 'occupancy2',
    'fullName3', 'registerNumber3', 'ubNumber3', 'address3', 'businessAddress3', 'phoneNumber3', 'representativeName3', 'occupancy3',
    'fullName4', 'registerNumber4', 'ubNumber4', 'address4', 'businessAddress4', 'phoneNumber4', 'representativeName4', 'occupancy4'
  ],
  [
    'fiduciaryType0', 'certificateNumber0', 'arliinDugaar0', 'quantity0', 'dateOfCommission0', 'areaSize0', 'materialFeature0',
    'transactionPrice0', 'realPrice0',
    'fiduciaryType1', 'certificateNumber1', 'arliinDugaar1', 'quantity1', 'dateOfCommission1', 'areaSize1', 'materialFeature1',
    'transactionPrice1', 'realPrice1',
    'fiduciaryType2', 'certificateNumber2', 'arliinDugaar2', 'quantity2', 'dateOfCommission2', 'areaSize2', 'materialFeature2',
    'transactionPrice2', 'realPrice2',
    'fiduciaryType3', 'certificateNumber3', 'arliinDugaar3', 'quantity3', 'dateOfCommission3', 'areaSize3', 'materialFeature3',
    'transactionPrice3', 'realPrice3',
    'fiduciaryType4', 'certificateNumber4', 'arliinDugaar4', 'quantity4', 'dateOfCommission4', 'areaSize4', 'materialFeature4',
    'transactionPrice4', 'realPrice4'
  ]
];

export const FIDUCIARY_ASSET_SUBTITLE = ['', '', 'Үүрэг хүлээгч/Барьцаалуулагчийн мэдээлэл', 'Фидуцийн хөрөнгийн мэдээлэл'];

export const CREATE_COLLATERAL_LOAN_ACCOUNT_FIELDS = [
  [LOAN_PRODUCT, 'fixedAcceptedLoanAmountString', 'loanAccountNumber'],
  [
    'numberOfPayments', 'yearlyInterestRateString', 'accountBranchNumber', 'frequency', 'depositInterestRateString',
    'currentAccountNumber', 'dayOfPayment', 'fees', FIRST_PAYMENT_DATE,
  ],
  ['insuranceCUDF', 'totalCollateralAmountUDF'],
  ['AccountFreeCode2', 'AccountFreeCode3', 'TypeOfAdvance', 'BorrowerCategoryCode', 'FREE_CODE_4', 'FREE_CODE_5', 'FREE_CODE_6',
    'FREE_CODE_7', 'FREE_CODE_8', 'FREE_CODE_9', 'FREE_CODE_10', 'NatureOfAdvance', 'CustomerIndustryType', 'PurposeOfAdvance', 'AccountFreeCode1'
  ],
  [ADDITIONAL_SPECIAL_CONDITION]
];

export const CREATE_COLLATERAL_LOAN_ACCOUNT_SUBTITLE = ['', '', 'UDF талбар', 'Барьцаа хөрөнгө'];
// Collateral list
export const COLLATERAL_LIST_MAP_KEY = 'collateralList';

// Loan Account with Collateral
export const COLLATERAL_CONNECTION_RATE = 'collateralConnectionRate';
export const COLLATERAL_ACCOUNT = 'collateralAccount';

export const WRONG_COLLATERAL_CONNECTION_RATE_VALUE_MESSAGE = 'Барьцаанд холбох хувийн нийлбэр 100%-с их байж болохгүйг анхаарна уу!';
export const WRONG_COLLATERAL_ORDER_MESSAGE = 'Барьцааны дугаарлалт 0 байна!';
export const WRONG_COLLATERAL_LOAN_AMOUNT_MESSAGE = 'Барьцааны дүн 0 байна!';
export const DUPLICATED_COLLATERAL_ORDER_MESSAGE = 'Барьцааны дугаарлалт давхацсан байна!';
export const COLLATERAL_ID = 'collateralId';
export const AMOUNT_OF_ASSESSMENT = 'amountOfAssessment';
export const LOAN_AMOUNT = 'loanAmount';
export const ORDER = 'order';
export const FIXED_ACCEPTED_LOAN_AMOUNT = 'fixedAcceptedLoanAmountString';
export const LOAN_TYPES = ['CONSUMER', 'SMALL_MICRO'];

// Loan account with collateral error messages
export const VALIDATE_NEGATIVE_VALUE_MESSAGE = 'Даатгалын хувь талбарт 0-ээс бага утга оруулж болохгүйг анхаарна уу!';

export const COLLATERAL_FORMS = [
  {
    id: COLLATERAL_ID, formFieldValue: {defaultValue: '', valueInfo: ''},
    label: 'Барьцааны код', type: 'string', validations: [{name: 'readonly', configuration: null}],
    options: [], disabled: false, context: 'string', required: false
  },
  {
    id: AMOUNT_OF_ASSESSMENT, formFieldValue: {defaultValue: 0, valueInfo: ''},
    label: 'Барьцааны үнэлгээ', type: 'BigDecimal', validations: [{name: 'readonly', configuration: null}],
    options: [], disabled: false, context: 'BigDecimal', required: false
  },
  {
    id: COLLATERAL_CONNECTION_RATE, formFieldValue: {defaultValue: 0, valueInfo: ''}, label: 'Холбох хувь',
    type: 'BigDecimal', validations: [{name: 'readonly', configuration: null}], options: [],
    disabled: false, context: 'BigDecimal', required: false
  },
  {
    id: LOAN_AMOUNT, formFieldValue: {defaultValue: 0, valueInfo: ''}, label: 'Зээлд холбох дүн',
    type: 'BigDecimal', validations: [], options: [], disabled: false, context: 'BigDecimal', required: false
  },
  {
    id: ORDER, formFieldValue: {defaultValue: 0, valueInfo: ''}, label: 'Дугаарлалт',
    type: 'BigDecimal', validations: [], options: [], disabled: false, context: 'BigDecimal', required: false
  }
];
// fields need set validation manually
export const COLLATERAL_UPDATE_FIELDS_ID = ['collateralBasicType', 'formOfOwnership', 'amountOfCollateral',
  'collateralSubType', 'product', 'startDate'];
export const COLLATERAL_UDF_FIELDS_ID = ['OWNED_TYPE', 'ULSIIN BURTGELIIN DUGAAR2'];
export const LOAN_CALCULATION_FIELD_ID = [TERM];
export const NOT_CHECK_MANDATORY_FIELDS_NAME = ['Батлах зээлийн хэмжээ'];
// collateral UDF
export const CHANGE_COLLATERAL_LABELS = {'GEREENII DUGAAR': 'Гэрээний дугаар', OWNED_TYPE: 'Өмчлөлийн хэлбэр'};

// Micro loan regular calculation
export const MICRO_LOAN_SIMPLE_CALCULATION_FIELDS = [
  ['reportingPeriodCash', 'currentAssets', 'fixedAssets', 'supplierPay', 'shortTermPayment', 'longTermPayment', 'totalAssetString'],
  ['reportPeriod', 'salesIncome', 'otherIncome', 'costOfGoods', 'operatingExpenses', 'taxCosts', 'rentalExpenses', 'otherExpense', 'netProfitString']
];
export const MICRO_LOAN_SIMPLE_CALCULATION_SUBTITLE = ['Баланс', 'Орлого үр дүнгийн тайлан'];

// Micro loan regular calculation
export const MICRO_LOAN_DOWNLOAD_KHUR_FIELDS = [
  ['registerNumber', 'referenceType'],
  ['employeeRegisterNumber', 'downloadInquiries'],
  ['month']
];

export const MICRO_LOAN_DOWNLOAD_KHUR_CO_BORROWER_FIELDS = [
  ['registerNumberCoBorrower', 'referenceType'],
  ['employeeRegisterNumber', 'downloadInquiries'],
  ['month']
];

export const MICRO_LOAN_DOWNLOAD_KHUR_SUBTITLE = ['', '', ''];

// micro balance variable name
export const BALANCE_SALE_FORM = 'sale';
export const BALANCE_OPERATION_FORM = 'operation';
export const BALANCE_ASSET_FORM = 'asset';
export const BALANCE_DEBT_FORM = 'debt';
export const REPORT_COVERAGE_PERIOD = 'reportPeriod';
export const BALANCE_TOTAL_INCOME_AMOUNT = 'balanceTotalIncomeAmountString';
export const BALANCE_TOTAL_INCOME_PERCENT = 'balanceTotalIncomePercentString';
export const TOTAL_SALE_AMOUNT = 'totalSaleAmount';
export const TOTAL_SALE_COST = 'totalSaleCost';
export const TOTAL_SALE_COST_PERCENT = 'totalSaleCostPercent';
export const TOTAL_OPERATION_COST_AMOUNT = 'totalOperationCostAmount';
export const TOTAL_OPERATION_COST_PERCENT = 'totalOperationCostPercent';
export const TOTAL_OPERATION_INCOME_AMOUNT = 'totalOperationIncomeAmount';
export const TOTAL_OPERATION_INCOME_PERCENT = 'totalOperationIncomePercent';
export const CURRENT_ASSET_AMOUNT = 'currentAssetAmount';
export const CURRENT_ASSET_PERCENT = 'currentAssetPercent';
export const MAIN_ASSET_AMOUNT = 'mainAssetAmount';
export const MAIN_ASSET_PERCENT = 'mainAssetPercent';
export const SHORT_TERM_DEBT_AMOUNT = 'shortTermDebtAmount';
export const SHORT_TERM_DEBT_PERCENT = 'shortTermDebtPercent';
export const TOTAL_DEBT_AMOUNT = 'totalDebtAmount';
export const TOTAL_DEBT_PERCENT = 'totalDebtPercent';
export const AMOUNT1 = 'amount1';
export const AMOUNT2 = 'amount2';
export const PERCENT1 = 'percent1';
export const PERCENT2 = 'percent2';

export const MICRO_LOAN_SCORING_FORM_FIELDS = [
  ['xacspanDate', 'deposit', 'businessSpan', 'businessOwnerAge', 'netWorth', 'rentalCosts'],
  ['score', 'scoring_level_risk']
];


export const MICRO_LOAN_CALCULATION_FIELDS = [
  ['amount', 'loanTerm', INTEREST_DATE, LOAN_PRODUCT, 'loanProductType', 'areasOfActivity'],
  ['hasMortgage', 'consumerLoanRepayment', 'businessLoanRepayment', 'householdExpenses', 'purposeOfLoan',
    'debtToSolvencyRatioString', 'debtToIncomeRatioString', 'debtToAssetsRatioString', 'currentAssetsRatioString', 'collateralProvidedAmountString',
    'grantLoanAmount', 'acceptedLoanAmount'],
  [
    'collateralAsset0', 'collateralAmount0', 'loanApprovalAmount0',
    'collateralAsset1', 'collateralAmount1', 'loanApprovalAmount1',
    'collateralAsset2', 'collateralAmount2', 'loanApprovalAmount2',
    'collateralAsset3', 'collateralAmount3', 'loanApprovalAmount3',
    'collateralAsset4', 'collateralAmount4', 'loanApprovalAmount4',
    'collateralAsset5', 'collateralAmount5', 'loanApprovalAmount5',
    'collateralAsset6', 'collateralAmount6', 'loanApprovalAmount6',
    'collateralAsset7', 'collateralAmount7', 'loanApprovalAmount7',
    'collateralAsset8', 'collateralAmount8', 'loanApprovalAmount8',
    'collateralAsset9', 'collateralAmount9', 'loanApprovalAmount9',
    'collateralAsset10', 'collateralAmount10', 'loanApprovalAmount10',
    'collateralAsset11', 'collateralAmount11', 'loanApprovalAmount11',
    'collateralAsset12', 'collateralAmount12', 'loanApprovalAmount12',
    'collateralAsset13', 'collateralAmount13', 'loanApprovalAmount13',
    'collateralAsset14', 'collateralAmount14', 'loanApprovalAmount14',
    'collateralAsset15', 'collateralAmount15', 'loanApprovalAmount15',
    'collateralAsset16', 'collateralAmount16', 'loanApprovalAmount16',
    'collateralAsset17', 'collateralAmount17', 'loanApprovalAmount17',
    'collateralAsset18', 'collateralAmount18', 'loanApprovalAmount18',
    'collateralAsset19', 'collateralAmount19', 'loanApprovalAmount19',
  ]
];
export const MICRO_LOAN_CALCULATION_TITLES = ['Зээлийн түүх', 'Журмын нөхцөлүүд'];

export const MICRO_CREATE_COLLATERAL_LOAN_ACCOUNT_FIELDS = [
  [LOAN_PRODUCT, 'fixedAcceptedLoanAmountString', 'loanAccountNumber'],
  [
    'numberOfPayments', 'yearlyInterestRateString', 'accountBranchNumber', 'frequency', 'depositInterestRateString',
    'currentAccountNumber', 'dayOfPayment', 'fees', FIRST_PAYMENT_DATE,
  ],
  ['insuranceCUDF', 'totalCollateralAmountUDF'],
  [
    'AccountFreeCode2', 'AccountFreeCode3', 'TypeOfAdvance', 'BorrowerCategoryCode', 'FREE_CODE_4', 'FREE_CODE_5', 'FREE_CODE_6',
    'FREE_CODE_7', 'FREE_CODE_8', 'FREE_CODE_9', 'FREE_CODE_10', 'NatureOfAdvance', 'CustomerIndustryType', 'PurposeOfAdvance', 'AccountFreeCode1'
  ],
  [ADDITIONAL_SPECIAL_CONDITION]
];

export const MICRO_CREATE_COLLATERAL_LOAN_ACCOUNT_SUBTITLE = ['', '', 'UDF талбар', 'Барьцаа хөрөнгө'];

// Mortgage business calculation
export const MORTGAGE_BUSINESS_CALCULATION_FIELDS = [
  ['businessActivities', 'areasActivity', 'numberEmployees', 'businessHousingInfo', 'salesChannels', 'durationOfBusiness', 'newBusiness'],
  ['reportPeriod', 'salesIncome', 'otherIncome', 'soldGoods', 'operationCost', 'activeLoanPayment', 'netProfit']
];
export const MORTGAGE_BUSINESS_CALCULATION_SUBTITLE = ['Бизнесийн мэдээлэл', 'Орлого үр дүнгийн тайлан'];

export const MORTGAGE_LOAN_CALCULATION_FIELDS = [
  ['loanProduct', 'amount', 'loanTerm', 'interestRate'],
  [
    'typeOfIncome0', 'seasonOne0', 'seasonTwo0', 'seasonThree0', 'seasonFour0', 'annualResults0', 'monthlyAverage0',
    'typeOfIncome1', 'seasonOne1', 'seasonTwo1', 'seasonThree1', 'seasonFour1', 'annualResults1', 'monthlyAverage1',
    'typeOfIncome2', 'seasonOne2', 'seasonTwo2', 'seasonThree2', 'seasonFour2', 'annualResults2', 'monthlyAverage2',
  ],
  ['borrowersIncome', 'borrowersIncomePercent', 'coBorrowersIncome', 'coBorrowersIncomePercent',
    'transferIncome', 'transferIncomePercent', 'netIncome', 'netIncomePercent'],
  ['totalFunding', 'housingFinancing', 'borrowerFinances', 'borrowerFinancesPercent', 'monthlyPayment', 'autoGarage',
    'borrowerFinanceGarage', 'borrowerFinanceGaragePercent', 'loanAmount', 'conditionsMet', 'propertyLocation', 'maxLoanAmount',
    'maxLoanTerm', 'acceptedLoanAmount', 'loanMonthlyPayment', 'authorize']
];
export const MORTGAGE_LOAN_CALCULATION_COLUMN_NUMBERS = [4, 5, 4, null];
export const MORTGAGE_LOAN_CALCULATION_TITLES = ['', 'Нэмэлт орлого', 'Орлогын бүтэц', 'Журмын нөхцөлүүд'];

export const MORTGAGE_GENERATE_LOAN_DECISION_DYNAMIC_COLUMN_FIELDS = [
  [SANCTIONED_BY, LOAN_PURPOSE, 'workspaceName', 'numberOfCoBorrowers', 'borrowerMonthlyIncome', 'coBorrowerMonthlyIncome', 'address', 'requestDate'],
  [ADDITIONAL_SPECIAL_CONDITION]
];


// Messages
export const COL_REMOVE_DIALOG_MESSAGE = 'Та сонгосон барьцааг дансанд холбох барьцаанаас хасахдаа итгэлтэй байна уу?';

//  form field hint text
export const FORM_FIELD_HINT = [
  {id: 'yearOfConstruction', fieldHint: 'MMYYYY форматаар бөглөнө үү.'},
  {id: 'manufactureYear', fieldHint: 'MMYYYY форматаар бөглөнө үү.'},
  // {id: 'accountIdEnter', fieldHint: 'Дансны дугаар оруулан ENTER дарна уу!'},
  // {id: 'accountNumber', fieldHint: 'Дансны дугаар оруулан ENTER дарна уу!'}
];

// Organization Registration Consts
export const TASK_DEF_SALARY_ORGANIZATION_REGISTRATION = 'user_task_salary_organization_registration';
export const TASK_DEF_LEASING_ORGANIZATION_REGISTRATION = 'user_task_leasing_organization_registration';
export const ORGANIZATION_ALREADY_EXIST = 'Анхаар: Энэ регистер дээр өмнө нь гэрээ байгуулагдсан байна. Шинээр бүртгэх шаардлагатай бол цааш үргэлжлүүлнэ үү!';
export const SALARY_ORGANIZATION_CONTRACT = 'Цалингийн байгууллагын гэрээ';
export const LEASING_ORGANIZATION_CONTRACT = 'Лизингийн байгууллагын гэрээ';

export const ORGANIZATION_BRANCH_IDS = [
  {
    id: 'allBranchLists', formFieldValue: {}, label: 'Салбар', type: 'string', validations: [],
    options: [{id: '001', value: '001'}, {id: '102', value: '102'}, {id: 'option1', value: '103'}, {id: 'option2', value: '104'},
      {id: 'option1', value: '105'}, {id: 'option2', value: '106'}, {id: 'option1', value: '108'}, {id: 'option2', value: '108'}], disabled: false, context: 'string', required: false
  }
];
export const VALUE_ONE_MONTH_DIALOG_MESSAGE = 'Тайланд оруулж буй утгууд нь нэг сарын дундаж дүн байна';
export const DIGITAL_LOANS_PROCESS_TYPE_ID = ["bnplLoan", "instantLoan", "onlineLeasing"];
