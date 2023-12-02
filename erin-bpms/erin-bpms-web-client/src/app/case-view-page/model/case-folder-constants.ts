export const CONSUMPTION_LOAN = 'consumptionLoan';
export const MICRO_LOAN = 'microLoan';
export const MORTGAGE_LOAN = 'mortgageLoan';
export const CREDIT_LINE_LOAN = 'creditLineLoan';
export const CONSUMPTION_LOAN_CONTRACT = 'consumptionLoanContract';
export const MICRO_LOAN_CONTRACT = 'microLoanContract';
export const CREDIT_LINE_LOAN_CONTRACT = 'creditLineLoanContract';
export const MORTGAGE_LOAN_CONTRACT = 'mortgageLoanContract';
export const EMPLOYEE_LOAN_CONTRACT = 'employeeLoanContract';
export const EMPLOYEE_MORTGAGE_LOAN_CONTRACT = 'employeeMortgageLoanContract';
export const DIRECT_PRINTING_CONTRACT = 'directPrintingContract';
export const ONLINE_SALARY = 'onlineSalary';
export const BNPL = 'bnplLoan';
export const INSTANT_LOAN = 'instantLoan';
export const ONLINE_LEASING = 'onlineLeasing';
export const LOAN_CONTRACT = 'loan-contract';
export const LOAN_CONTRACT_CAMEL_CASE = 'LoanContract';

export const DOCUMENT_TITLE = 'Баримт бичиг';
export const NOTE_TITLE = 'Тэмдэглэл';

export const ORGANIZATION_REGISTRATION = 'organizationRegistration';

export const CASE_FOLDER_TITLES_MAP = new Map<string, string[]>([
  [CONSUMPTION_LOAN, ['Үндсэн мэдээлэл', 'Судалгааны шат', DOCUMENT_TITLE, 'Зээлийн данс', 'Зээлийн гэрээ', NOTE_TITLE]],
  [MICRO_LOAN, ['Үндсэн мэдээлэл', DOCUMENT_TITLE, NOTE_TITLE]],
  [CREDIT_LINE_LOAN, ['Үндсэн мэдээлэл', DOCUMENT_TITLE, NOTE_TITLE]],
  [MORTGAGE_LOAN, ['Үндсэн мэдээлэл', DOCUMENT_TITLE, NOTE_TITLE]],
  [CONSUMPTION_LOAN_CONTRACT, [DOCUMENT_TITLE]],
  [MICRO_LOAN_CONTRACT, [DOCUMENT_TITLE]],
  [CREDIT_LINE_LOAN_CONTRACT, [DOCUMENT_TITLE]],
  [MORTGAGE_LOAN_CONTRACT, [DOCUMENT_TITLE]],
  [EMPLOYEE_LOAN_CONTRACT, [DOCUMENT_TITLE]],
  [EMPLOYEE_MORTGAGE_LOAN_CONTRACT, [DOCUMENT_TITLE]],
  [DIRECT_PRINTING_CONTRACT, [DOCUMENT_TITLE]],
  [ONLINE_SALARY, [DOCUMENT_TITLE]],
  [BNPL, [DOCUMENT_TITLE]],
  [ORGANIZATION_REGISTRATION, [DOCUMENT_TITLE]],
  [INSTANT_LOAN, [DOCUMENT_TITLE]],
  [ONLINE_LEASING, [DOCUMENT_TITLE]]
]);

export const CASE_FOLDER_TITLE_ENTITY_MAP = new Map<string, string>([
  ['Үндсэн мэдээлэл', 'main-info'],
  ['Судалгааны шат', 'loan-info'],
  ['Зээлийн данс', 'account-info'],
  ['Зээлийн гэрээ', 'loan-contract'],
  [NOTE_TITLE, 'NOTE']
]);

