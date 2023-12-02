/* task constants */
export const TASK_DEF_BILLING_TAX_PAY_CHILD = 'user_task_branch_banking_tax_pay_child';
export const TASK_DEF_BILLING_TAX_PAY = 'user_task_branch_banking_tax_pay';
export const TASK_DEF_BILLING_CUSTOM_PAY_CHILD = 'user_task_branch_banking_custom_pay_child';
export const TASK_DEF_BILLING_CUSTOM_PAY = 'user_task_branch_banking_custom_pay';
export const TASK_DEF_TRANSACTION_DOCUMENT = 'user_task_branch_banking_customer_transaction_form';
export const TASK_DEF_E_TRANSACTION_DOCUMENT = 'user_task_branch_banking_e_transaction';
export const TASK_DEF_DEPOSIT_CONTRACT = 'user_task_branch_banking_deposit_contract';
export const TASK_DEF_FUTURE_MILLIONARE = 'user_task_branch_banking_future_millionare';
export const TASK_DEF_ACCOUNT_REFERENCE = 'user_task_branch_banking_account_reference';
export const TASK_DEF_SALARY_PACKAGE_TRANSACTION = 'user_task_branch_banking_salary_package';
export const TASK_DEF_LOAN_REPAYMENT = 'user_task_branch_banking_loan_repayment';
export const TASKS_WITH_NOT_TRANSACTION = [TASK_DEF_FUTURE_MILLIONARE, TASK_DEF_DEPOSIT_CONTRACT, TASK_DEF_E_TRANSACTION_DOCUMENT,
  TASK_DEF_TRANSACTION_DOCUMENT, TASK_DEF_BILLING_CUSTOM_PAY, TASK_DEF_BILLING_TAX_PAY];
export const TASK_DEF_PREPARE_MORTGAGE_LOAN_CONTRACT_INFO = 'user_task_prepare_mortgage_loan_contract_info';

/* form field id constants */
export const TRANSACTION_FORM_TYPE = 'transactionFormType';
export const CONTRACT_TYPE = 'contractType';
export const BRANCH_BANKING =  'branch-banking';
export const TAX_PAY = 'taxPayTransaction';
export const CUSTOM_PAY = 'customPayTransaction';
export const ACCOUNT_REFERENCE = 'accountReference';
export const MEMORIAL_ORDER_RECEIPT = 'memorialOrderTransaction';
export const SALARY_PACKAGE_TRANSACTION = 'salaryPackageTransaction';
export const ACCOUNT_REFERENCE_FIELDS = ['accountCcy', 'customerFullName', 'accountBalanceRef', 'accountCreatedDt', 'accountEndDt'];
export const ACCOUNT_INFO_FIELDS = ['accountName', 'accountBalance', 'accountReference', 'accountCurrency'];
export const CHECK_REQUIRED_FIELDS_ACTION = ['checkNameAndAccount', 'uploadFile' , 'removeFile', 'complete',
  'selectTableRow', 'getCustomerTransactions', 'getEtransactionList', 'calculateLoanRepayment'];

/*USSD action constants*/
export const FORGOT_PASSWORD = {actionId: 'forgetPassword', actionName: 'Нууц үг илгээх'};
export const RECOVER_RIGHTS = {actionId: 'recoveryRights', actionName: 'Харилцагчийн эрх сэргээх'};
export const REMOVE_RIGHTS = {actionId: 'removeRights', actionName: 'Харилцагчийн эрх цуцлах'};
export const UNBLOCK = {actionId: 'unblock', actionName: 'Харилцагчийн блок гаргах'};
export const SAVE_USSD_INFO = {actionId: 'save', actionName: 'Харилцагчийн мэдээлэл хадгалах'};

/* dialog constants */
export const UPDATE_PASSWORD_CONFIRM_MESSAGE = 'Та харилцагчийн нууц үг сэргээхдээ итгэлтэй байна уу?';
export const RECOVER_USER_RIGHT_CONFIRM_MESSAGE = 'Та харилцагчийн эрх сэргээхдээ итгэлтэй байна уу?';
export const REMOVE_USER_RIGHT_CONFIRM_MESSAGE = 'Та харилцагчийн эрх цуцлахдаа итгэлтэй байна уу?';
export const UNBLOCK_USER_CONFIRM_MESSAGE = 'Та харилцагчийн блок гаргахдаа итгэлтэй байна уу?';
export const CONFIRM_DIALOG_BUTTONS  = [
  {actionId: 'confirm', actionName: 'Тийм'},
  {actionId: 'cancel', actionName: 'Үгүй'}
];
export const  USSD_PHONE_NUMBER_EMPTY_MESSAGE = 'USSD үйлчилгээнд бүртгэх утасны дугаараа оруулна уу!';
export const  OTP_VERIFICATION_DIALOG_FIELD = {
    id: 'otpCode', formFieldValue: {defaultValue: null, valueInfo: ''}, label: 'Баталгаажуулах код', type: 'BigDecimal',
    validations: [{name: 'required', configuration: null}], context: 'BigDecimal', required: true
};

/*Loan repayment action constants*/
export const LOAN_REPAYMENT_WARNING = 'Урьдчилсан төлөлт эсвэл илүү төлөлт хийх үед үндсэн зээлээс хасагдана. Хуваарьт өдөр дахин төлбөр төлөхийг анхаарна уу!!!';
export const LOAN_REPAYMENT_WARNING_ON_TIME = 'Та заавал хуваарийн дагуу төлөлт хийсний дараа хуваарийн бус төлөлт хийх боломжтой!';
export const LOAN_REPAYMENT_WARNING_OVER_DUE = 'Зээл төлөх хуваарьт өдөр биш байна!';

export const LIST_EMPTY_MESSAGE = 'Жагсаалт хоосон байна';
export const ACCOUNT_HAS_NOT_ACCESS_MESSAGE = 'Нууцлалтай данс тул үлдэгдэл харуулах боломжгүй!!!\n' +
  'Нууцлалтай дансны мэдээлэл харах эрхтэй хэрэглэгчид харагдана.';

/*USSD constants*/
export const USSD_EMPTY_MAIN_ACCOUNT_WARNING = 'Харилцагчид тохирох үндсэн данс байхгүй байна!';

