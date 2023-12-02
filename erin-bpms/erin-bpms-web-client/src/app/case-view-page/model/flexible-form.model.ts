import {FormsModel} from '../../models/app.model';

// Зээлийн хавсралт гэрээ
export const LOAN_ATTACHMENT_COLLATERAL_OWNER: FormsModel[] = [
  {id: 'fullNameOfМortgagor', label: 'Овог, нэр', formFieldValue: {defaultValue: '', valueInfo: ''},
    options: [], validations: [], context: undefined, required: false, disabled: false, type: 'string'},
  {id: 'registerNumberOfМortgagor', label: 'Регистр/УБ дугаар',  formFieldValue: {defaultValue: '', valueInfo: ''},
    options: [], validations: [], context: undefined,  required: false, disabled: false, type: 'string'},
  {id: 'ubIDOfМortgagor', label: 'УБ дугаар',  formFieldValue: {defaultValue: '', valueInfo: ''},
    options: [], validations: [], context: undefined, required: false, disabled: false, type: 'string'},
  {id: 'addressOfМortgagor', label: 'Хаяг',  formFieldValue: {defaultValue: '', valueInfo: ''},
    options: [], validations: [], context: undefined, required: false, disabled: false, type: 'string'},
  {id: 'phoneNumberOfМortgagor', label: 'Утас',  formFieldValue: {defaultValue: '', valueInfo: ''},
    options: [], validations: [], context: undefined, required: false, disabled: false, type: 'string'},
  {id: 'emailAddressOfМortgagor', label: 'Цахим хаяг',  formFieldValue: {defaultValue: '', valueInfo: ''},
    options: [], validations: [], context: undefined, required: false, disabled: false, type: 'string'},
  {id: 'occupationOfМortgagor', label: 'Ажил мэргэжил',  formFieldValue: {defaultValue: '', valueInfo: ''},
    options: [], validations: [], context: undefined, required: false, disabled: false, type: 'string'},
  {id: '2008а', label: '2008а', required: false,  formFieldValue: {defaultValue: '', valueInfo: ''},
    options: [], validations: [], context: undefined, disabled: false, type: 'string'},
  {id: '20086', label: '20086', required: false,  formFieldValue: {defaultValue: '', valueInfo: ''},
    options: [], validations: [], context: undefined, disabled: false, type: 'string'},
];

export const LOAN_ATTACHMENT_GUARANTOR: FormsModel[] = [
  {id: 'fullNameOfGuarantor', label: 'Овог, нэр',  formFieldValue: {defaultValue: '', valueInfo: ''},
    options: [], validations: [], context: undefined, required: false, disabled: false, type: 'string'},
  {id: 'registerNumberOfGuarantor', label: 'Регистр',  formFieldValue: {defaultValue: '', valueInfo: ''},
    options: [], validations: [], context: undefined, required: false, disabled: false, type: 'string'},
  {id: 'ubIDofGuarantor', label: 'УБ дугаар',  formFieldValue: {defaultValue: '', valueInfo: ''},
    options: [], validations: [], context: undefined, required: false, disabled: false, type: 'string'},
  {id: 'addressOfGuarantor', label: 'Хаяг',  formFieldValue: {defaultValue: '', valueInfo: ''},
    options: [], validations: [], context: undefined, required: false, disabled: false, type: 'string'},
  {id: 'businessAddressOfGuarantor', label: 'Бизнесийн хаяг',  formFieldValue: {defaultValue: '', valueInfo: ''},
    options: [], validations: [], context: undefined, required: false, disabled: false, type: 'string'},
  {id: 'phoneNumberOfGuarantor', label: 'Утас',  formFieldValue: {defaultValue: '', valueInfo: ''},
    options: [], validations: [], context: undefined, required: false, disabled: false, type: 'string'},
  {id: 'representiveLegalEntityName', label: 'Төлөөлөх хуулийн этгээдийн нэр',  formFieldValue: {defaultValue: '', valueInfo: ''},
    options: [], validations: [], context: undefined, required: false, disabled: false, type: 'string'},
  {id: 'occupationOfGuarantor', label: 'Ажил мэргэжил',  formFieldValue: {defaultValue: '', valueInfo: ''},
    options: [], validations: [], context: undefined, required: false, disabled: false, type: 'string'},
];
export const LOAN_ATTACHMENT_FLEXIBLE_FORM: FormsModel[][] = [ null, null, LOAN_ATTACHMENT_COLLATERAL_OWNER, LOAN_ATTACHMENT_GUARANTOR ];

export const FIDUCIARY_ASSET_COLLATERAL_OWNER: FormsModel[] = [
    {id: 'fullName', label: 'Овог нэр', required: false, options: [], formFieldValue: {defaultValue: '', valueInfo: ''},
      validations: [], disabled: false, type: 'string', context: undefined},
    {id: 'registerNumber', label: 'Регистр', required: false, options: [], formFieldValue: {defaultValue: '', valueInfo: ''},
      validations: [], disabled: false, type: 'string', context: undefined},
    {id: 'ubNumber', label: 'УБ дугаар', required: false, options: [], formFieldValue: {defaultValue: '', valueInfo: ''},
      validations: [], disabled: false, type: 'string', context: undefined},
    {id: 'address', label: 'Хаяг', required: false, options: [], formFieldValue: {defaultValue: '', valueInfo: ''},
      validations: [], disabled: false, type: 'string', context: undefined},
    {id: 'businessAddress', label: 'Бизнессийн хаяг', required: false, options: [], formFieldValue: {defaultValue: '', valueInfo: ''},
      validations: [], disabled: false, type: 'string', context: undefined},
    {id: 'phoneNumber', label: 'Утас', required: false, options: [], formFieldValue: {defaultValue: '', valueInfo: ''},
      validations: [], disabled: false, type: 'string', context: undefined},
    {id: 'representativeName', label: 'Төлөөлөх хуулийн этгээдийн нэр', required: false, options: [],
      formFieldValue: {defaultValue: '', valueInfo: ''}, validations: [], disabled: false, type: 'string', context: undefined},
    {id: 'occupancy', label: 'Ажил мэргэжил', required: false, options: [], formFieldValue: {defaultValue: '', valueInfo: ''},
      validations: [], disabled: false, type: 'string', context: undefined},
];

export const FIDUCIARY_ASSET_INFO: FormsModel[] = [
  {id: 'fiduciaryType', label: 'Нэр төрөл', required: false, options: [], formFieldValue: {defaultValue: '', valueInfo: ''},
    validations: [], disabled: false, type: 'string', context: undefined},
  {id: 'certificateNumber', label: 'Гэрчилгээний дугаар', required: false, options: [], formFieldValue: {defaultValue: '', valueInfo: ''},
    validations: [], disabled: false, type: 'string', context: undefined},
  {id: 'arliinDugaar', label: 'Арлын дугаар', required: false, options: [], formFieldValue: {defaultValue: '', valueInfo: ''},
    validations: [], disabled: false, type: 'string', context: undefined},
  {id: 'quantity', label: 'Тоо ширхэг', required: false, options: [], formFieldValue: {defaultValue: '', valueInfo: ''},
    validations: [], disabled: false, type: 'string', context: undefined},
  {id: 'dateOfCommission', label: 'Ашиглалтанд орсон огноо', required: false, options: [], formFieldValue: {defaultValue: '', valueInfo: ''},
    validations: [], disabled: false, type: 'date', context: undefined},
  {id: 'areaSize', label: 'Талбайн хэмжээ', required: false, options: [], formFieldValue: {defaultValue: '', valueInfo: ''},
    validations: [], disabled: false, type: 'BigDecimal', context: undefined},
  {id: 'materialFeature', label: 'Хийц, загвар/Материалын шинж', required: false, options: [], formFieldValue: {defaultValue: '', valueInfo: ''},
    validations: [], disabled: false, type: 'string', context: undefined},
  {id: 'transactionPrice', label: 'Үнэ/Шилжүүлэх үеийн байдлаар', required: false, options: [], formFieldValue: {defaultValue: '', valueInfo: ''},
    validations: [], disabled: false, type: 'BigDecimal', context: undefined},
  {id: 'realPrice', label: 'Үнэ/Бодитоор гаргуулах үеийн байдал', required: false, options: [], formFieldValue: {defaultValue: '', valueInfo: ''},
    validations: [], disabled: false, type: 'BigDecimal', context: undefined},
];

export const FIDUCIARY_ASSET_FLEXIBLE_FORM: FormsModel[][] = [ null, null, FIDUCIARY_ASSET_COLLATERAL_OWNER, FIDUCIARY_ASSET_INFO ];

export const CO_OWNER_INFORMATION_FORM: FormsModel[] = [
  {id: 'fullNameCoOwner', label: 'Овог, нэр', required: false, options: [],
    formFieldValue: {defaultValue: '', valueInfo: ''}, validations: [], disabled: false, type: 'string', context: undefined},
  {id: 'birthCertificateCoOwner', label: 'Төрсний Гэрчилгээ', required: false, options: [],
    formFieldValue: {defaultValue: '', valueInfo: ''}, validations: [], disabled: false, type: 'string', context: undefined},
  {id: 'ageGroupCoOwner', label: 'Насны ангилал', required: false, options: [{id: 'ageGroupCoOwner01', value: '0-13'},
    {id: 'ageGroupCoOwner02', value: '14-17'}, {id: 'ageGroupCoOwner03', value: '18 нас хүрсэн'}],
    formFieldValue: {defaultValue: '', valueInfo: ''}, validations: [], disabled: false, type: 'string', context: undefined},
  {id: 'registerCoOwner', label: 'Регистр', required: false, options: [],
    formFieldValue: {defaultValue: '', valueInfo: ''}, validations: [], disabled: false, type: 'string', context: undefined},
  {id: 'addressCoOwner', label: 'Аймаг/Хот', required: false, options: [],
    formFieldValue: {defaultValue: '', valueInfo: ''}, validations: [], disabled: false, type: 'string', context: undefined},
  {id: 'attachmentCoOwner', label: 'Хавсралт', required: false, options: [],
    formFieldValue: {defaultValue: '', valueInfo: ''}, validations: [], disabled: false, type: 'string', context: undefined},
];

export const CO_OWNER_INFORMATION_FLEXIBLE_FORM: FormsModel[][] = [ null, null, CO_OWNER_INFORMATION_FORM, null ];

export const COLLATERAL_REAL_ESTATE_FORM: FormsModel[] = [
  {id: 'choosingCollateral', label: 'Барьцаа сонгох', required: false, options: [],
    formFieldValue: {defaultValue: '', valueInfo: ''}, validations: [], disabled: false, type: 'string', context: undefined},
];

export const COLLATERAL_REAL_ESTATE_FLEXIBLE_FORM: FormsModel[][] = [ null, null, COLLATERAL_REAL_ESTATE_FORM ];

// Micro loan calculation collateral
export const MICRO_LOAN_CALCULATION_COLLATERAL_INFO: FormsModel[] = [
  {id: 'collateralAsset', label: 'Барьцаа хөрөнгө', required: false, options: [],
    formFieldValue: {defaultValue: '', valueInfo: ''}, validations: [], disabled: false, type: 'string', context: undefined},
  {id: 'collateralAmount', label: 'Барьцааны дүн', required: false, options: [],
    formFieldValue: {defaultValue: 0, valueInfo: ''}, validations: [], disabled: false, type: 'BigDecimal', context: undefined},
  {id: 'loanApprovalAmount', label: 'Зээл олгох дүн', required: false, options: [],
    formFieldValue: {defaultValue: 0, valueInfo: ''}, validations: [{name: 'readonly', configuration: null}], disabled: false, type: 'BigDecimal', context: undefined},
];

export const MICRO_LOAN_CALCULATION_FLEXIBLE_FORM: FormsModel[][] = [null, null, MICRO_LOAN_CALCULATION_COLLATERAL_INFO];
export const MICRO_LOAN_CALC_FLEX_FORM_MAX_LENGTH: any[] = [null, null, 20];

export const MICRO_CALCULATION_DYNAMIC_COLUMN_FIELDS = [
  null,
  [
    ['hasMortgage', 'consumerLoanRepayment', 'businessLoanRepayment', 'householdExpenses', 'purposeOfLoan'],
    ['debtToSolvencyRatioString', 'debtToIncomeRatioString', 'debtToAssetsRatioString', 'currentAssetsRatioString', 'collateralProvidedAmountString',
      'grantLoanAmount', 'acceptedLoanAmount']
  ],
  null
];

// Mortgage loan calculation collateral
export const MORTGAGE_LOAN_CALCULATION_COLLATERAL_INFO: FormsModel[] = [
  {id: 'typeOfIncome', label: 'Орлогын төрөл', required: false, options: [{id: 'incomeCoBorrower', value: 'Хамтран зээлдэгчийн орлого'},
      {id: 'incomeCashFlow', value: 'Гуйвуулгын орлого, бусад орлого'}],
    formFieldValue: {defaultValue: '', valueInfo: ''}, validations: [], disabled: false, type: 'string', context: undefined},
  {id: 'seasonOne', label: 'Улирал 1', required: false, options: [],
    formFieldValue: {defaultValue: 0, valueInfo: ''}, validations: [], disabled: false, type: 'BigDecimal', context: undefined},
  {id: 'seasonTwo', label: 'Улирал 2', required: false, options: [],
    formFieldValue: {defaultValue: 0, valueInfo: ''}, validations: [], disabled: false, type: 'BigDecimal', context: undefined},
  {id: 'seasonThree', label: 'Улирал 3', required: false, options: [],
    formFieldValue: {defaultValue: 0, valueInfo: ''}, validations: [], disabled: false, type: 'BigDecimal', context: undefined},
  {id: 'seasonFour', label: 'Улирал 4', required: false, options: [],
    formFieldValue: {defaultValue: 0, valueInfo: ''}, validations: [], disabled: false, type: 'BigDecimal', context: undefined},
  {id: 'annualResults', label: 'Жилийн дүн', required: false, options: [],
    formFieldValue: {defaultValue: 0, valueInfo: ''}, validations: [{name: 'readonly', configuration: null}], disabled: false, type: 'BigDecimal', context: undefined},
  {id: 'monthlyAverage', label: 'Сарын дундаж', required: false, options: [],
    formFieldValue: {defaultValue: 0, valueInfo: ''}, validations: [{name: 'readonly', configuration: null}], disabled: false, type: 'BigDecimal', context: undefined}
];

export const MORTGAGE_LOAN_CALCULATION_FLEXIBLE_FORM: FormsModel[][] = [null, MORTGAGE_LOAN_CALCULATION_COLLATERAL_INFO, null, null];
export const MORTGAGE_LOAN_CALC_FLEX_FORM_MAX_LENGTH: any[] = [null, 3, null, null];

export const MORTGAGE_CALCULATION_DYNAMIC_COLUMN_FIELDS = [
  null,
  null,
  null,
  [
    ['totalFunding', 'housingFinancing', 'borrowerFinances', 'borrowerFinancesPercent', 'monthlyPayment', 'autoGarage', 'borrowerFinanceGarage', 'borrowerFinanceGaragePercent'],
    ['conditionsMet', 'propertyLocation', 'maxLoanAmount', 'maxLoanTerm', 'loanMonthlyPayment', 'loanAmount', 'acceptedLoanAmount', 'authorize']
  ]
];
